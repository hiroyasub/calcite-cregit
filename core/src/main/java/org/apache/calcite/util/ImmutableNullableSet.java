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
name|util
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
name|metadata
operator|.
name|NullSentinel
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
name|Collections2
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
name|ImmutableSet
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
name|Iterables
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
name|Iterators
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
name|NonNull
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
name|java
operator|.
name|util
operator|.
name|AbstractSet
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
import|;
end_import

begin_comment
comment|/**  * An immutable set that may contain null values.  *  *<p>If the set cannot contain null values, use {@link ImmutableSet}.  *  *<p>We do not yet support sorted sets.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableNullableSet
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractSet
argument_list|<
name|E
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Set
name|SINGLETON_NULL
init|=
operator|new
name|ImmutableNullableSet
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NullSentinel
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|SINGLETON
init|=
name|Collections
operator|.
name|singleton
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|Object
argument_list|>
name|elements
decl_stmt|;
specifier|private
name|ImmutableNullableSet
parameter_list|(
name|ImmutableSet
argument_list|<
name|Object
argument_list|>
name|elements
parameter_list|)
block|{
name|this
operator|.
name|elements
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|elements
argument_list|,
literal|"elements"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|elements
operator|.
name|iterator
argument_list|()
argument_list|,
name|e
lambda|->
name|e
operator|==
name|NullSentinel
operator|.
name|INSTANCE
condition|?
name|castNonNull
argument_list|(
literal|null
argument_list|)
else|:
operator|(
name|E
operator|)
name|e
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|elements
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
return|return
name|elements
operator|.
name|contains
argument_list|(
name|o
operator|==
literal|null
condition|?
name|NullSentinel
operator|.
name|INSTANCE
else|:
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeAll
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Returns an immutable set containing the given elements.    *    *<p>Behavior is as {@link ImmutableSet#copyOf(Iterable)}    * except that this set allows nulls.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"StaticPseudoFunctionalStyleMethod"
block|}
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|copyOf
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|elements
parameter_list|)
block|{
if|if
condition|(
name|elements
operator|instanceof
name|ImmutableNullableSet
operator|||
name|elements
operator|instanceof
name|ImmutableSet
operator|||
name|elements
operator|==
name|Collections
operator|.
name|emptySet
argument_list|()
operator|||
name|elements
operator|==
name|Collections
operator|.
name|emptySortedSet
argument_list|()
operator|||
name|elements
operator|==
name|SINGLETON_NULL
operator|||
name|elements
operator|.
name|getClass
argument_list|()
operator|==
name|SINGLETON
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
operator|(
name|Set
argument_list|<
name|E
argument_list|>
operator|)
name|elements
return|;
block|}
specifier|final
name|ImmutableSet
argument_list|<
name|Object
argument_list|>
name|set
decl_stmt|;
if|if
condition|(
name|elements
operator|instanceof
name|Collection
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|E
argument_list|>
name|collection
init|=
operator|(
name|Collection
argument_list|<
name|E
argument_list|>
operator|)
name|elements
decl_stmt|;
switch|switch
condition|(
name|collection
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
case|case
literal|1
case|:
name|E
name|element
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|collection
argument_list|)
decl_stmt|;
return|return
name|element
operator|==
literal|null
condition|?
name|SINGLETON_NULL
else|:
name|ImmutableSet
operator|.
name|of
argument_list|(
name|element
argument_list|)
return|;
default|default:
name|set
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Collections2
operator|.
name|transform
argument_list|(
name|collection
argument_list|,
name|e
lambda|->
name|e
operator|==
literal|null
condition|?
name|NullSentinel
operator|.
name|INSTANCE
else|:
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|set
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|elements
argument_list|,
name|e
lambda|->
name|e
operator|==
literal|null
condition|?
name|NullSentinel
operator|.
name|INSTANCE
else|:
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|set
operator|.
name|contains
argument_list|(
name|NullSentinel
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
return|return
operator|new
name|ImmutableNullableSet
argument_list|<>
argument_list|(
name|set
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|Set
argument_list|<
name|E
argument_list|>
operator|)
name|set
return|;
block|}
block|}
comment|/**    * Returns an immutable set containing the given elements.    *    *<p>Behavior as    * {@link ImmutableSet#copyOf(Object[])}    * except that this set allows nulls.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|copyOf
parameter_list|(
name|E
index|[]
name|elements
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
name|elements
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|copyOf
parameter_list|(
name|E
index|[]
name|elements
parameter_list|,
name|boolean
name|needCopy
parameter_list|)
block|{
comment|// If there are no nulls, ImmutableSet is better.
if|if
condition|(
operator|!
name|containsNull
argument_list|(
name|elements
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|elements
argument_list|)
return|;
block|}
specifier|final
annotation|@
name|Nullable
name|Object
index|[]
name|objects
init|=
name|needCopy
condition|?
name|Arrays
operator|.
name|copyOf
argument_list|(
name|elements
argument_list|,
name|elements
operator|.
name|length
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
else|:
name|elements
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|objects
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|objects
index|[
name|i
index|]
operator|=
name|NullSentinel
operator|.
name|INSTANCE
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"nullness"
block|,
literal|"NullableProblems"
block|}
argument_list|)
annotation|@
name|NonNull
name|Object
index|[]
name|nonNullObjects
init|=
name|objects
decl_stmt|;
return|return
operator|new
name|ImmutableNullableSet
argument_list|<
name|E
argument_list|>
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|nonNullObjects
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|boolean
name|containsNull
parameter_list|(
name|E
index|[]
name|elements
parameter_list|)
block|{
for|for
control|(
name|E
name|element
range|:
name|elements
control|)
block|{
if|if
condition|(
name|element
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Creates an immutable set of 1 element. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|e1
operator|==
literal|null
condition|?
operator|(
name|Set
argument_list|<
name|E
argument_list|>
operator|)
name|SINGLETON_NULL
else|:
name|ImmutableSet
operator|.
name|of
argument_list|(
name|e1
argument_list|)
return|;
block|}
comment|/** Creates an immutable set of 2 elements. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e1
parameter_list|,
name|E
name|e2
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
operator|(
name|E
index|[]
operator|)
operator|new
name|Object
index|[]
block|{
name|e1
block|,
name|e2
block|}
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Creates an immutable set of 3 elements. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e1
parameter_list|,
name|E
name|e2
parameter_list|,
name|E
name|e3
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
operator|(
name|E
index|[]
operator|)
operator|new
name|Object
index|[]
block|{
name|e1
block|,
name|e2
block|,
name|e3
block|}
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Creates an immutable set of 4 elements. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e1
parameter_list|,
name|E
name|e2
parameter_list|,
name|E
name|e3
parameter_list|,
name|E
name|e4
parameter_list|)
block|{
return|return
name|copyOf
argument_list|(
operator|(
name|E
index|[]
operator|)
operator|new
name|Object
index|[]
block|{
name|e1
block|,
name|e2
block|,
name|e3
block|,
name|e4
block|}
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Creates an immutable set of 5 or more elements. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e1
parameter_list|,
name|E
name|e2
parameter_list|,
name|E
name|e3
parameter_list|,
name|E
name|e4
parameter_list|,
name|E
name|e5
parameter_list|,
name|E
modifier|...
name|others
parameter_list|)
block|{
name|E
index|[]
name|elements
init|=
operator|(
name|E
index|[]
operator|)
operator|new
name|Object
index|[
literal|5
operator|+
name|others
operator|.
name|length
index|]
decl_stmt|;
name|elements
index|[
literal|0
index|]
operator|=
name|e1
expr_stmt|;
name|elements
index|[
literal|1
index|]
operator|=
name|e2
expr_stmt|;
name|elements
index|[
literal|2
index|]
operator|=
name|e3
expr_stmt|;
name|elements
index|[
literal|3
index|]
operator|=
name|e4
expr_stmt|;
name|elements
index|[
literal|4
index|]
operator|=
name|e5
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|others
argument_list|,
literal|0
argument_list|,
name|elements
argument_list|,
literal|5
argument_list|,
name|others
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|copyOf
argument_list|(
name|elements
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns a new builder. The generated builder is equivalent to the builder    * created by the {@link Builder} constructor.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Builder
argument_list|<
name|E
argument_list|>
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * A builder for creating immutable nullable set instances.    *    * @param<E> element type    */
specifier|public
specifier|static
specifier|final
class|class
name|Builder
parameter_list|<
name|E
parameter_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|contents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Creates a new builder. The returned builder is equivalent to the builder      * generated by      * {@link ImmutableNullableSet#builder}.      */
specifier|public
name|Builder
parameter_list|()
block|{
block|}
comment|/**      * Adds {@code element} to the {@code ImmutableNullableSet}.      *      * @param element the element to add      * @return this {@code Builder} object      */
specifier|public
name|Builder
argument_list|<
name|E
argument_list|>
name|add
parameter_list|(
name|E
name|element
parameter_list|)
block|{
name|contents
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableSet}.      *      * @param elements the {@code Iterable} to add to the      *     {@code ImmutableNullableSet}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
specifier|public
name|Builder
argument_list|<
name|E
argument_list|>
name|addAll
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|elements
parameter_list|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|contents
argument_list|,
name|elements
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableSet}.      *      * @param elements the elements to add to the {@code ImmutableNullableSet}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
specifier|public
name|Builder
argument_list|<
name|E
argument_list|>
name|add
parameter_list|(
name|E
modifier|...
name|elements
parameter_list|)
block|{
for|for
control|(
name|E
name|element
range|:
name|elements
control|)
block|{
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableSet}.      *      * @param elements the elements to add to the {@code ImmutableNullableSet}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
specifier|public
name|Builder
argument_list|<
name|E
argument_list|>
name|addAll
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|elements
parameter_list|)
block|{
name|Iterators
operator|.
name|addAll
argument_list|(
name|contents
argument_list|,
name|elements
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns a newly-created {@code ImmutableNullableSet} based on the      * contents of the {@code Builder}.      */
specifier|public
name|Set
argument_list|<
name|E
argument_list|>
name|build
parameter_list|()
block|{
return|return
name|copyOf
argument_list|(
name|contents
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

