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
name|java
operator|.
name|util
operator|.
name|AbstractList
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

begin_comment
comment|/**  * An immutable list that may contain null values.  *  *<p>If the list cannot contain null values, use  * {@link com.google.common.collect.ImmutableList}.</p>  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableNullableList
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|List
name|SINGLETON_NULL
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|E
index|[]
name|elements
decl_stmt|;
comment|// private - does not copy array
specifier|private
name|ImmutableNullableList
parameter_list|(
name|E
index|[]
name|elements
parameter_list|)
block|{
name|this
operator|.
name|elements
operator|=
name|elements
expr_stmt|;
block|}
comment|/**    * Returns an immutable list containing the given elements, in order.    *    *<p>Behavior as    * {@link com.google.common.collect.ImmutableList#copyOf(java.util.Collection)}    * except that this list allows nulls.</p>    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|copyOf
parameter_list|(
name|Collection
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
name|ImmutableNullableList
operator|||
name|elements
operator|instanceof
name|ImmutableList
operator|||
name|elements
operator|==
name|SINGLETON_NULL
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|E
argument_list|>
operator|)
name|elements
return|;
block|}
comment|// If there are no nulls, ImmutableList is better.
for|for
control|(
name|E
name|object
range|:
name|elements
control|)
block|{
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
specifier|final
name|Object
index|[]
name|objects
init|=
name|elements
operator|.
name|toArray
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
return|return
operator|new
name|ImmutableNullableList
argument_list|<
name|E
argument_list|>
argument_list|(
operator|(
name|E
index|[]
operator|)
name|objects
argument_list|)
return|;
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|elements
argument_list|)
return|;
block|}
comment|/**    * Returns an immutable list containing the given elements, in order.    *    *<p>Behavior as    * {@link com.google.common.collect.ImmutableList#copyOf(Object[])}    * except that this list allows nulls.</p>    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
comment|// Check for nulls.
for|for
control|(
name|E
name|object
range|:
name|elements
control|)
block|{
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|new
name|ImmutableNullableList
argument_list|<
name|E
argument_list|>
argument_list|(
name|elements
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// There are no nulls. ImmutableList is better.
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|elements
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 1 element. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
name|List
argument_list|<
name|E
argument_list|>
operator|)
name|SINGLETON_NULL
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|e1
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 2 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 3 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 4 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|,
name|e4
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 5 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
parameter_list|)
block|{
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|,
name|e4
argument_list|,
name|e5
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 6 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
name|e6
parameter_list|)
block|{
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|,
name|e4
argument_list|,
name|e5
argument_list|,
name|e6
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 7 elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
name|e6
parameter_list|,
name|E
name|e7
parameter_list|)
block|{
comment|// Only we can see the varargs array. Therefore the list is immutable.
comment|//noinspection unchecked
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|,
name|e4
argument_list|,
name|e5
argument_list|,
name|e6
argument_list|,
name|e7
argument_list|)
return|;
block|}
comment|/** Creates an immutable list of 8 or more elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
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
name|e6
parameter_list|,
name|E
name|e7
parameter_list|,
name|E
name|e8
parameter_list|,
name|E
modifier|...
name|others
parameter_list|)
block|{
name|Object
index|[]
name|array
init|=
operator|new
name|Object
index|[
literal|8
operator|+
name|others
operator|.
name|length
index|]
decl_stmt|;
name|array
index|[
literal|0
index|]
operator|=
name|e1
expr_stmt|;
name|array
index|[
literal|1
index|]
operator|=
name|e2
expr_stmt|;
name|array
index|[
literal|2
index|]
operator|=
name|e3
expr_stmt|;
name|array
index|[
literal|3
index|]
operator|=
name|e4
expr_stmt|;
name|array
index|[
literal|4
index|]
operator|=
name|e5
expr_stmt|;
name|array
index|[
literal|5
index|]
operator|=
name|e6
expr_stmt|;
name|array
index|[
literal|6
index|]
operator|=
name|e7
expr_stmt|;
name|array
index|[
literal|7
index|]
operator|=
name|e8
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|others
argument_list|,
literal|0
argument_list|,
name|array
argument_list|,
literal|8
argument_list|,
name|others
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
return|return
operator|new
name|ImmutableNullableList
argument_list|<
name|E
argument_list|>
argument_list|(
operator|(
name|E
index|[]
operator|)
name|array
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|E
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|elements
index|[
name|index
index|]
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
name|length
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
argument_list|<
name|E
argument_list|>
argument_list|()
return|;
block|}
comment|/**    * A builder for creating immutable nullable list instances.    */
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
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/**      * Creates a new builder. The returned builder is equivalent to the builder      * generated by      * {@link org.apache.calcite.util.ImmutableNullableList#builder}.      */
specifier|public
name|Builder
parameter_list|()
block|{
block|}
comment|/**      * Adds {@code element} to the {@code ImmutableNullableList}.      *      * @param element the element to add      * @return this {@code Builder} object      */
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
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableList}.      *      * @param elements the {@code Iterable} to add to the      *     {@code ImmutableNullableList}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
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
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableList}.      *      * @param elements the elements to add to the {@code ImmutableNullableList}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
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
comment|/**      * Adds each element of {@code elements} to the      * {@code ImmutableNullableList}.      *      * @param elements the elements to add to the {@code ImmutableNullableList}      * @return this {@code Builder} object      * @throws NullPointerException if {@code elements} is null      */
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
comment|/**      * Returns a newly-created {@code ImmutableNullableList} based on the      * contents of the {@code Builder}.      */
specifier|public
name|List
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

begin_comment
comment|// End ImmutableNullableList.java
end_comment

end_unit

