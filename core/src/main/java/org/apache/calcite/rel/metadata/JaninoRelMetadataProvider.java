begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteSystemProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|interpreter
operator|.
name|JaninoRexCompiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|janino
operator|.
name|RelMetadataHandlerGeneratorUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheLoader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|LoadingCache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|UncheckedExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|CompileException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|CompilerFactoryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|ICompilerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|ISimpleCompiler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@link RelMetadataProvider} interface that generates  * a class that dispatches to the underlying providers.  */
end_comment

begin_class
specifier|public
class|class
name|JaninoRelMetadataProvider
implements|implements
name|RelMetadataProvider
implements|,
name|MetadataHandlerProvider
block|{
specifier|private
specifier|final
name|RelMetadataProvider
name|provider
decl_stmt|;
comment|// Constants and static fields
specifier|public
specifier|static
specifier|final
name|JaninoRelMetadataProvider
name|DEFAULT
init|=
name|JaninoRelMetadataProvider
operator|.
name|of
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|/** Cache of pre-generated handlers by provider and kind of metadata.    * For the cache to be effective, providers should implement identity    * correctly. */
specifier|private
specifier|static
specifier|final
name|LoadingCache
argument_list|<
name|Key
argument_list|,
name|MetadataHandler
argument_list|<
name|?
argument_list|>
argument_list|>
name|HANDLERS
init|=
name|maxSize
argument_list|(
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
argument_list|,
name|CalciteSystemProperty
operator|.
name|METADATA_HANDLER_CACHE_MAXIMUM_SIZE
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|CacheLoader
operator|.
name|from
argument_list|(
name|key
lambda|->
name|generateCompileAndInstantiate
argument_list|(
name|key
operator|.
name|handlerClass
argument_list|,
name|key
operator|.
name|provider
operator|.
name|handlers
argument_list|(
name|key
operator|.
name|handlerClass
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Private constructor; use {@link #of}. */
specifier|private
name|JaninoRelMetadataProvider
parameter_list|(
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
block|}
comment|/** Creates a JaninoRelMetadataProvider.    *    * @param provider Underlying provider    */
specifier|public
specifier|static
name|JaninoRelMetadataProvider
name|of
parameter_list|(
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
if|if
condition|(
name|provider
operator|instanceof
name|JaninoRelMetadataProvider
condition|)
block|{
return|return
operator|(
name|JaninoRelMetadataProvider
operator|)
name|provider
return|;
block|}
return|return
operator|new
name|JaninoRelMetadataProvider
argument_list|(
name|provider
argument_list|)
return|;
block|}
comment|// helper for initialization
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|maxSize
parameter_list|(
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
parameter_list|,
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|maximumSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|JaninoRelMetadataProvider
operator|&&
operator|(
operator|(
name|JaninoRelMetadataProvider
operator|)
name|obj
operator|)
operator|.
name|provider
operator|.
name|equals
argument_list|(
name|provider
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|109
operator|+
name|provider
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
annotation|@
name|Override
specifier|public
operator|<
expr|@
name|Nullable
name|M
expr|extends @
name|Nullable
name|Metadata
operator|>
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|apply
argument_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|M
argument_list|>
name|metadataClass
argument_list|)
block|{
throw|throw
argument_list|new
name|UnsupportedOperationException
argument_list|()
block|;   }
expr|@
name|Deprecated
comment|// to be removed before 2.0
expr|@
name|Override
specifier|public
operator|<
name|M
expr|extends
name|Metadata
operator|>
name|Multimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlers
argument_list|(
name|MetadataDef
argument_list|<
name|M
argument_list|>
name|def
argument_list|)
block|{
return|return
name|provider
operator|.
name|handlers
argument_list|(
name|def
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|MetadataHandler
argument_list|<
name|?
argument_list|>
argument_list|>
name|handlers
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
argument_list|>
name|handlerClass
parameter_list|)
block|{
return|return
name|provider
operator|.
name|handlers
argument_list|(
name|handlerClass
argument_list|)
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|MH
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|MH
name|generateCompileAndInstantiate
parameter_list|(
name|Class
argument_list|<
name|MH
argument_list|>
name|handlerClass
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
extends|extends
name|Metadata
argument_list|>
argument_list|>
name|handlers
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
extends|extends
name|Metadata
argument_list|>
argument_list|>
name|uniqueHandlers
init|=
name|handlers
operator|.
name|stream
argument_list|()
operator|.
name|distinct
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|RelMetadataHandlerGeneratorUtil
operator|.
name|HandlerNameAndGeneratedCode
name|handlerNameAndGeneratedCode
init|=
name|RelMetadataHandlerGeneratorUtil
operator|.
name|generateHandler
argument_list|(
name|handlerClass
argument_list|,
name|uniqueHandlers
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|compile
argument_list|(
name|handlerNameAndGeneratedCode
operator|.
name|getHandlerName
argument_list|()
argument_list|,
name|handlerNameAndGeneratedCode
operator|.
name|getGeneratedCode
argument_list|()
argument_list|,
name|handlerClass
argument_list|,
name|uniqueHandlers
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CompileException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error compiling:\n"
operator|+
name|handlerNameAndGeneratedCode
operator|.
name|getGeneratedCode
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|static
parameter_list|<
name|MH
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|MH
name|compile
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|generatedCode
parameter_list|,
name|Class
argument_list|<
name|MH
argument_list|>
name|handlerClass
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|argList
parameter_list|)
throws|throws
name|CompileException
throws|,
name|IOException
block|{
specifier|final
name|ICompilerFactory
name|compilerFactory
decl_stmt|;
name|ClassLoader
name|classLoader
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|JaninoRelMetadataProvider
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
literal|"classLoader"
argument_list|)
decl_stmt|;
try|try
block|{
name|compilerFactory
operator|=
name|CompilerFactoryFactory
operator|.
name|getDefaultCompilerFactory
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to instantiate java compiler"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|ISimpleCompiler
name|compiler
init|=
name|compilerFactory
operator|.
name|newSimpleCompiler
argument_list|()
decl_stmt|;
name|compiler
operator|.
name|setParentClassLoader
argument_list|(
name|JaninoRexCompiler
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
comment|// Add line numbers to the generated janino class
name|compiler
operator|.
name|setDebuggingInformation
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|generatedCode
argument_list|)
expr_stmt|;
block|}
name|compiler
operator|.
name|cook
argument_list|(
name|generatedCode
argument_list|)
expr_stmt|;
specifier|final
name|Constructor
name|constructor
decl_stmt|;
specifier|final
name|Object
name|o
decl_stmt|;
try|try
block|{
name|constructor
operator|=
name|compiler
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
operator|.
name|getDeclaredConstructors
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
name|o
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|argList
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
decl||
name|IllegalAccessException
decl||
name|InvocationTargetException
decl||
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|handlerClass
operator|.
name|cast
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
parameter_list|<
name|H
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|H
name|revise
parameter_list|(
name|Class
argument_list|<
name|H
argument_list|>
name|handlerClass
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|handlerClass
argument_list|,
name|provider
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
return|return
name|handlerClass
operator|.
name|cast
argument_list|(
name|HANDLERS
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|Util
operator|.
name|causeOrSelf
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/** Registers some classes. Does not flush the providers, but next time we    * need to generate a provider, it will handle all of these classes. So,    * calling this method reduces the number of times we need to re-generate. */
annotation|@
name|Deprecated
specifier|public
name|void
name|register
parameter_list|(
name|Iterable
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|>
name|classes
parameter_list|)
block|{
block|}
comment|/** Exception that indicates there there should be a handler for    * this class but there is not. The action is probably to    * re-generate the handler class. Use {@link MetadataHandlerProvider.NoHandler} instead.    * */
annotation|@
name|Deprecated
specifier|public
specifier|static
class|class
name|NoHandler
extends|extends
name|MetadataHandlerProvider
operator|.
name|NoHandler
block|{
specifier|public
name|NoHandler
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
parameter_list|)
block|{
name|super
argument_list|(
name|relClass
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Key for the cache. */
specifier|private
specifier|static
class|class
name|Key
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
extends|extends
name|Metadata
argument_list|>
argument_list|>
name|handlerClass
decl_stmt|;
specifier|final
name|RelMetadataProvider
name|provider
decl_stmt|;
specifier|private
name|Key
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
argument_list|>
name|handlerClass
parameter_list|,
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|handlerClass
operator|=
name|handlerClass
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|handlerClass
operator|.
name|hashCode
argument_list|()
operator|*
literal|37
operator|+
name|provider
operator|.
name|hashCode
argument_list|()
operator|)
operator|*
literal|37
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|Key
operator|&&
operator|(
operator|(
name|Key
operator|)
name|obj
operator|)
operator|.
name|handlerClass
operator|.
name|equals
argument_list|(
name|handlerClass
argument_list|)
operator|&&
operator|(
operator|(
name|Key
operator|)
name|obj
operator|)
operator|.
name|provider
operator|.
name|equals
argument_list|(
name|provider
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|MH
extends|extends
name|MetadataHandler
argument_list|<
name|?
argument_list|>
parameter_list|>
name|MH
name|handler
parameter_list|(
specifier|final
name|Class
argument_list|<
name|MH
argument_list|>
name|handlerClass
parameter_list|)
block|{
return|return
name|handlerClass
operator|.
name|cast
argument_list|(
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|RelMetadataQuery
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|handlerClass
block|}
argument_list|,
parameter_list|(
name|proxy
parameter_list|,
name|method
parameter_list|,
name|args
parameter_list|)
lambda|->
block|{
specifier|final
name|RelNode
name|r
init|=
name|requireNonNull
argument_list|(
operator|(
name|RelNode
operator|)
name|args
index|[
literal|0
index|]
argument_list|,
literal|"(RelNode) args[0]"
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|NoHandler
argument_list|(
name|r
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

