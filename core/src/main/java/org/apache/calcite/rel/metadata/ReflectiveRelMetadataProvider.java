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
name|base
operator|.
name|Throwables
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
name|Lists
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
name|Maps
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
name|Sets
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
name|InvocationHandler
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
name|Set
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
name|Map
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
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ReflectiveRelMetadataProvider.    *    * @param map Map    * @param metadataClass0 Metadata class    */
specifier|protected
name|ReflectiveRelMetadataProvider
parameter_list|(
name|Map
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
name|Object
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
name|Object
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
name|Object
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
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
comment|// Find the distinct set of RelNode classes handled by this provider,
comment|// ordered base-class first.
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
name|Sets
operator|.
name|newHashSet
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
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Method
name|handlerMethod
range|:
name|target
operator|.
name|getClass
argument_list|()
operator|.
name|getMethods
argument_list|()
control|)
block|{
for|for
control|(
name|Method
name|method
range|:
name|methods
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
specifier|final
name|Map
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
name|HashMap
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
name|find
argument_list|(
name|handlerMap
argument_list|,
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
operator|new
name|UnboundMetadata
argument_list|()
block|{
specifier|public
name|Metadata
name|bind
parameter_list|(
specifier|final
name|RelNode
name|rel
parameter_list|,
specifier|final
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
operator|(
name|Metadata
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|metadataClass0
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|metadataClass0
block|}
argument_list|,
operator|new
name|InvocationHandler
argument_list|()
block|{
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Suppose we are an implementation of Selectivity
comment|// that wraps "filter", a LogicalFilter. Then we
comment|// implement
comment|//   Selectivity.selectivity(rex)
comment|// by calling method
comment|//   new SelectivityImpl().selectivity(filter, rex)
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
name|BuiltInMethod
operator|.
name|METADATA_REL
operator|.
name|method
argument_list|)
condition|)
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
name|key
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
name|key
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
name|key
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
operator|!
name|mq
operator|.
name|set
operator|.
name|add
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
name|CyclicMetadataException
operator|.
name|INSTANCE
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
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|mq
operator|.
name|set
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|methodsMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|function
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ReflectiveRelMetadataProvider
argument_list|(
name|methodsMap
argument_list|,
name|metadataClass0
argument_list|)
return|;
block|}
comment|/** Finds an implementation of a method for {@code relNodeClass} or its    * nearest base class. Assumes that base classes have already been added to    * {@code map}. */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"SuspiciousMethodCalls"
block|}
argument_list|)
specifier|private
specifier|static
name|Method
name|find
parameter_list|(
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
parameter_list|,
name|Class
argument_list|<
name|RelNode
argument_list|>
name|relNodeClass
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|newSources
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Method
name|implementingMethod
decl_stmt|;
while|while
condition|(
name|relNodeClass
operator|!=
literal|null
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
name|relNodeClass
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
else|else
block|{
name|newSources
operator|.
name|add
argument_list|(
name|relNodeClass
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
range|:
name|relNodeClass
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
if|if
condition|(
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|relNodeClass
operator|.
name|getSuperclass
argument_list|()
argument_list|)
condition|)
block|{
name|relNodeClass
operator|=
operator|(
name|Class
argument_list|<
name|RelNode
argument_list|>
operator|)
name|relNodeClass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|relNodeClass
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
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
comment|//~ Methods ----------------------------------------------------------------
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
block|}
end_class

begin_comment
comment|// End ReflectiveRelMetadataProvider.java
end_comment

end_unit

