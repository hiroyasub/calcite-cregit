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
name|rex
operator|.
name|RexNode
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
name|runtime
operator|.
name|FlatLists
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
name|BuiltInMethod
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
name|ImmutableNullableList
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
name|Pair
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
name|ReflectiveVisitor
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
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMultimap
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
name|Modifier
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
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
name|Set
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
name|ConcurrentHashMap
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
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@link RelMetadataProvider} interface that dispatches  * metadata methods to methods on a given object via reflection.  *  *<p>The methods on the target object must be public and non-static, and have  * the same signature as the implemented metadata method except for an  * additional first parameter of type {@link RelNode} or a sub-class. That  * parameter gives this provider an indication of that relational expressions it  * can handle.</p>  *  *<p>For an example, see {@link RelMdColumnOrigins#SOURCE}.  */
end_comment

begin_class
specifier|public
class|class
name|ReflectiveRelMetadataProvider
implements|implements
name|RelMetadataProvider
implements|,
name|ReflectiveVisitor
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|UnboundMetadata
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Metadata
argument_list|>
name|metadataClass0
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMultimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|handlerMap
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ReflectiveRelMetadataProvider.    *    * @param map Map    * @param metadataClass0 Metadata class    * @param handlerMap Methods handled and the objects to call them on    */
specifier|protected
name|ReflectiveRelMetadataProvider
parameter_list|(
name|ConcurrentMap
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|UnboundMetadata
argument_list|>
name|map
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Metadata
argument_list|>
name|metadataClass0
parameter_list|,
name|Multimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|handlerMap
parameter_list|)
block|{
assert|assert
operator|!
name|map
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"are your methods named wrong?"
assert|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
name|this
operator|.
name|metadataClass0
operator|=
name|metadataClass0
expr_stmt|;
name|this
operator|.
name|handlerMap
operator|=
name|ImmutableMultimap
operator|.
name|copyOf
argument_list|(
name|handlerMap
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an implementation of {@link RelMetadataProvider} that scans for    * methods with a preceding argument.    *    *<p>For example, {@link BuiltInMetadata.Selectivity} has a method    * {@link BuiltInMetadata.Selectivity#getSelectivity(RexNode)}.    * A class</p>    *    *<blockquote><pre><code>    * class RelMdSelectivity {    *   public Double getSelectivity(Union rel, RexNode predicate) { }    *   public Double getSelectivity(Filter rel, RexNode predicate) { }    *</code></pre></blockquote>    *    *<p>provides implementations of selectivity for relational expressions    * that extend {@link org.apache.calcite.rel.core.Union}    * or {@link org.apache.calcite.rel.core.Filter}.</p>    */
specifier|public
specifier|static
name|RelMetadataProvider
name|reflectiveSource
parameter_list|(
name|Method
name|method
parameter_list|,
name|MetadataHandler
name|target
parameter_list|)
block|{
return|return
name|reflectiveSource
argument_list|(
name|target
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|method
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a reflective metadata provider that implements several    * methods. */
specifier|public
specifier|static
name|RelMetadataProvider
name|reflectiveSource
parameter_list|(
name|MetadataHandler
name|target
parameter_list|,
name|Method
modifier|...
name|methods
parameter_list|)
block|{
return|return
name|reflectiveSource
argument_list|(
name|target
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|methods
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelMetadataProvider
name|reflectiveSource
parameter_list|(
specifier|final
name|MetadataHandler
name|target
parameter_list|,
specifier|final
name|ImmutableList
argument_list|<
name|Method
argument_list|>
name|methods
parameter_list|)
block|{
specifier|final
name|Space2
name|space
init|=
name|Space2
operator|.
name|create
argument_list|(
name|target
argument_list|,
name|methods
argument_list|)
decl_stmt|;
comment|// This needs to be a concurrent map since RelMetadataProvider are cached in static
comment|// fields, thus the map is subject to concurrent modifications later.
comment|// See map.put in org.apache.calcite.rel.metadata.ReflectiveRelMetadataProvider.apply(
comment|// java.lang.Class<? extends org.apache.calcite.rel.RelNode>)
specifier|final
name|ConcurrentMap
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|UnboundMetadata
argument_list|>
name|methodsMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|RelNode
argument_list|>
name|key
range|:
name|space
operator|.
name|classes
control|)
block|{
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
name|Method
argument_list|>
name|builder
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Method
name|method
range|:
name|methods
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|space
operator|.
name|find
argument_list|(
name|key
argument_list|,
name|method
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Method
argument_list|>
name|handlerMethods
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|UnboundMetadata
name|function
init|=
parameter_list|(
name|rel
parameter_list|,
name|mq
parameter_list|)
lambda|->
operator|(
name|Metadata
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|space
operator|.
name|metadataClass0
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|space
operator|.
name|metadataClass0
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
comment|// Suppose we are an implementation of Selectivity
comment|// that wraps "filter", a LogicalFilter. Then we
comment|// implement
comment|//   Selectivity.selectivity(rex)
comment|// by calling method
comment|//   new SelectivityImpl().selectivity(filter, rex)
block_content|if (method.equals(BuiltInMethod.METADATA_REL.method
init|)
decl_stmt|)
block|{
return|return
name|rel
return|;
block|}
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
name|BuiltInMethod
operator|.
name|OBJECT_TO_STRING
operator|.
name|method
argument_list|)
condition|)
block|{
return|return
name|space
operator|.
name|metadataClass0
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|rel
operator|+
literal|")"
return|;
block|}
name|int
name|i
init|=
name|methods
operator|.
name|indexOf
argument_list|(
name|method
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not handled: "
operator|+
name|method
operator|+
literal|" for "
operator|+
name|rel
argument_list|)
throw|;
block|}
specifier|final
name|Method
name|handlerMethod
init|=
name|handlerMethods
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlerMethod
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not handled: "
operator|+
name|method
operator|+
literal|" for "
operator|+
name|rel
argument_list|)
throw|;
block|}
specifier|final
name|Object
index|[]
name|args1
decl_stmt|;
specifier|final
name|List
name|key1
decl_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
name|args1
operator|=
operator|new
name|Object
index|[]
block|{
name|rel
block|,
name|mq
block|}
expr_stmt|;
name|key1
operator|=
name|FlatLists
operator|.
name|of
argument_list|(
name|rel
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|args1
operator|=
operator|new
name|Object
index|[
name|args
operator|.
name|length
operator|+
literal|2
index|]
expr_stmt|;
name|args1
index|[
literal|0
index|]
operator|=
name|rel
expr_stmt|;
name|args1
index|[
literal|1
index|]
operator|=
name|mq
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|args
argument_list|,
literal|0
argument_list|,
name|args1
argument_list|,
literal|2
argument_list|,
name|args
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|args2
init|=
name|args1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|args2
index|[
literal|1
index|]
operator|=
name|method
expr_stmt|;
comment|// replace RelMetadataQuery with method
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|args2
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|args2
index|[
name|j
index|]
operator|==
literal|null
condition|)
block|{
name|args2
index|[
name|j
index|]
operator|=
name|NullSentinel
operator|.
name|INSTANCE
expr_stmt|;
block|}
if|else if
condition|(
name|args2
index|[
name|j
index|]
operator|instanceof
name|RexNode
condition|)
block|{
comment|// Can't use RexNode.equals - it is not deep
name|args2
index|[
name|j
index|]
operator|=
name|args2
index|[
name|j
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
name|key1
operator|=
name|FlatLists
operator|.
name|copyOf
argument_list|(
name|args2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mq
operator|.
name|map
operator|.
name|put
argument_list|(
name|rel
argument_list|,
name|key1
argument_list|,
name|NullSentinel
operator|.
name|INSTANCE
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|CyclicMetadataException
argument_list|()
throw|;
block|}
try|try
block|{
return|return
name|handlerMethod
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
name|args1
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
decl||
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|throwIfUnchecked
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|mq
operator|.
name|map
operator|.
name|remove
argument_list|(
name|rel
argument_list|,
name|key1
argument_list|)
expr_stmt|;
block|}
block|}
block_content|)
function|;
name|methodsMap
operator|.
name|put
parameter_list|(
name|key
parameter_list|,
name|function
parameter_list|)
constructor_decl|;
block|}
end_class

begin_return
return|return
operator|new
name|ReflectiveRelMetadataProvider
argument_list|(
name|methodsMap
argument_list|,
name|space
operator|.
name|metadataClass0
argument_list|,
name|space
operator|.
name|providerMap
argument_list|)
return|;
end_return

begin_function
unit|}    public
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
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
parameter_list|(
name|MetadataDef
argument_list|<
name|M
argument_list|>
name|def
parameter_list|)
block|{
specifier|final
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|entry
range|:
name|handlerMap
operator|.
name|entries
argument_list|()
control|)
block|{
if|if
condition|(
name|def
operator|.
name|methods
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|//noinspection unchecked
name|builder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|boolean
name|couldImplement
parameter_list|(
name|Method
name|handlerMethod
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
if|if
condition|(
operator|!
name|handlerMethod
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
operator|(
name|handlerMethod
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|!=
literal|0
operator|||
operator|(
name|handlerMethod
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|PUBLIC
operator|)
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes1
init|=
name|handlerMethod
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
return|return
name|parameterTypes1
operator|.
name|length
operator|==
name|parameterTypes
operator|.
name|length
operator|+
literal|2
operator|&&
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|parameterTypes1
index|[
literal|0
index|]
argument_list|)
operator|&&
name|RelMetadataQuery
operator|.
name|class
operator|==
name|parameterTypes1
index|[
literal|1
index|]
operator|&&
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterTypes
argument_list|)
operator|.
name|equals
argument_list|(
name|Util
operator|.
name|skip
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|parameterTypes1
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_comment
comment|//~ Methods ----------------------------------------------------------------
end_comment

begin_function
specifier|public
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|apply
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|M
argument_list|>
name|metadataClass
parameter_list|)
block|{
if|if
condition|(
name|metadataClass
operator|==
name|metadataClass0
condition|)
block|{
return|return
name|apply
argument_list|(
name|relClass
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
end_function

begin_function
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"SuspiciousMethodCalls"
block|}
argument_list|)
specifier|public
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|apply
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
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|>
name|newSources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|function
init|=
name|map
operator|.
name|get
argument_list|(
name|relClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
for|for
control|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Class
name|clazz
range|:
name|newSources
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|function
argument_list|)
expr_stmt|;
block|}
return|return
name|function
return|;
block|}
else|else
block|{
name|newSources
operator|.
name|add
argument_list|(
name|relClass
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|interfaceClass
range|:
name|relClass
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|interfaceClass
argument_list|)
condition|)
block|{
specifier|final
name|UnboundMetadata
argument_list|<
name|M
argument_list|>
name|function2
init|=
name|map
operator|.
name|get
argument_list|(
name|interfaceClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|function2
operator|!=
literal|null
condition|)
block|{
for|for
control|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Class
name|clazz
range|:
name|newSources
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|function2
argument_list|)
expr_stmt|;
block|}
return|return
name|function2
return|;
block|}
block|}
block|}
if|if
condition|(
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|relClass
operator|.
name|getSuperclass
argument_list|()
argument_list|)
condition|)
block|{
name|relClass
operator|=
operator|(
name|Class
argument_list|<
name|RelNode
argument_list|>
operator|)
name|relClass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_function

begin_comment
comment|/** Workspace for computing which methods can act as handlers for    * given metadata methods. */
end_comment

begin_class
specifier|static
class|class
name|Space
block|{
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|classes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Pair
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|,
name|Method
argument_list|>
argument_list|,
name|Method
argument_list|>
name|handlerMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableMultimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|providerMap
decl_stmt|;
name|Space
parameter_list|(
name|Multimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|providerMap
parameter_list|)
block|{
name|this
operator|.
name|providerMap
operator|=
name|ImmutableMultimap
operator|.
name|copyOf
argument_list|(
name|providerMap
argument_list|)
expr_stmt|;
comment|// Find the distinct set of RelNode classes handled by this provider,
comment|// ordered base-class first.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|entry
range|:
name|providerMap
operator|.
name|entries
argument_list|()
control|)
block|{
specifier|final
name|Method
name|method
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|MetadataHandler
name|provider
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Method
name|handlerMethod
range|:
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|couldImplement
argument_list|(
name|handlerMethod
argument_list|,
name|method
argument_list|)
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Class
argument_list|<
name|RelNode
argument_list|>
name|relNodeClass
init|=
operator|(
name|Class
argument_list|<
name|RelNode
argument_list|>
operator|)
name|handlerMethod
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|classes
operator|.
name|add
argument_list|(
name|relNodeClass
argument_list|)
expr_stmt|;
name|handlerMap
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|relNodeClass
argument_list|,
name|method
argument_list|)
argument_list|,
name|handlerMethod
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Finds an implementation of a method for {@code relNodeClass} or its      * nearest base class. Assumes that base classes have already been added to      * {@code map}. */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"SuspiciousMethodCalls"
block|}
argument_list|)
name|Method
name|find
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relNodeClass
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|relNodeClass
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|r
init|=
name|relNodeClass
init|;
condition|;
control|)
block|{
name|Method
name|implementingMethod
init|=
name|handlerMap
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|r
argument_list|,
name|method
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|implementingMethod
operator|!=
literal|null
condition|)
block|{
return|return
name|implementingMethod
return|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
range|:
name|r
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|implementingMethod
operator|=
name|handlerMap
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|clazz
argument_list|,
name|method
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|implementingMethod
operator|!=
literal|null
condition|)
block|{
return|return
name|implementingMethod
return|;
block|}
block|}
block|}
name|r
operator|=
name|r
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
operator|!
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|r
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No handler for method ["
operator|+
name|method
operator|+
literal|"] applied to argument of type ["
operator|+
name|relNodeClass
operator|+
literal|"]; we recommend you create a catch-all (RelNode) handler"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|/** Extended work space. */
end_comment

begin_class
specifier|static
class|class
name|Space2
extends|extends
name|Space
block|{
specifier|private
name|Class
argument_list|<
name|Metadata
argument_list|>
name|metadataClass0
decl_stmt|;
name|Space2
parameter_list|(
name|Class
argument_list|<
name|Metadata
argument_list|>
name|metadataClass0
parameter_list|,
name|ImmutableMultimap
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|providerMap
parameter_list|)
block|{
name|super
argument_list|(
name|providerMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadataClass0
operator|=
name|metadataClass0
expr_stmt|;
block|}
specifier|public
specifier|static
name|Space2
name|create
parameter_list|(
name|MetadataHandler
name|target
parameter_list|,
name|ImmutableList
argument_list|<
name|Method
argument_list|>
name|methods
parameter_list|)
block|{
assert|assert
name|methods
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
specifier|final
name|Method
name|method0
init|=
name|methods
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
name|Class
argument_list|<
name|Metadata
argument_list|>
name|metadataClass0
init|=
operator|(
name|Class
operator|)
name|method0
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
assert|assert
name|Metadata
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|metadataClass0
argument_list|)
assert|;
for|for
control|(
name|Method
name|method
range|:
name|methods
control|)
block|{
assert|assert
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|metadataClass0
assert|;
block|}
specifier|final
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|Method
argument_list|,
name|MetadataHandler
argument_list|>
name|providerBuilder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Method
name|method
range|:
name|methods
control|)
block|{
name|providerBuilder
operator|.
name|put
argument_list|(
name|method
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Space2
argument_list|(
name|metadataClass0
argument_list|,
name|providerBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

unit|}
end_unit

