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
name|linq4j
operator|.
name|function
operator|.
name|Functions
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
name|mapping
operator|.
name|Mappings
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
name|Preconditions
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
name|UnmodifiableListIterator
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
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|ListIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
comment|/**  * An immutable list of {@link Integer} values backed by an array of  * {@code int}s.  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableIntList
extends|extends
name|FlatLists
operator|.
name|AbstractFlatList
argument_list|<
name|Integer
argument_list|>
block|{
specifier|private
specifier|final
name|int
index|[]
name|ints
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Object
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ImmutableIntList
name|EMPTY
init|=
operator|new
name|EmptyImmutableIntList
argument_list|()
decl_stmt|;
comment|// Does not copy array. Must remain private.
specifier|private
name|ImmutableIntList
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
name|this
operator|.
name|ints
operator|=
name|ints
expr_stmt|;
block|}
comment|/**    * Returns an empty ImmutableIntList.    */
specifier|public
specifier|static
name|ImmutableIntList
name|of
parameter_list|()
block|{
return|return
name|EMPTY
return|;
block|}
comment|/**    * Creates an ImmutableIntList from an array of {@code int}.    */
specifier|public
specifier|static
name|ImmutableIntList
name|of
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|ints
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates an ImmutableIntList from an array of {@code Number}.    */
specifier|public
specifier|static
name|ImmutableIntList
name|copyOf
parameter_list|(
name|Number
modifier|...
name|numbers
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
name|numbers
operator|.
name|length
index|]
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ints
index|[
name|i
index|]
operator|=
name|numbers
index|[
name|i
index|]
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|ints
argument_list|)
return|;
block|}
comment|/**    * Creates an ImmutableIntList from an iterable of {@link Number}.    */
specifier|public
specifier|static
name|ImmutableIntList
name|copyOf
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|instanceof
name|ImmutableIntList
condition|)
block|{
return|return
operator|(
name|ImmutableIntList
operator|)
name|list
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|collection
init|=
name|list
operator|instanceof
name|Collection
condition|?
operator|(
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
operator|)
name|list
else|:
name|Lists
operator|.
name|newArrayList
argument_list|(
name|list
argument_list|)
decl_stmt|;
return|return
name|copyFromCollection
argument_list|(
name|collection
argument_list|)
return|;
block|}
comment|/**    * Creates an ImmutableIntList from an iterator of {@link Number}.    */
specifier|public
specifier|static
name|ImmutableIntList
name|copyOf
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|copyFromCollection
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ImmutableIntList
name|copyFromCollection
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|number
range|:
name|list
control|)
block|{
name|ints
index|[
name|i
operator|++
index|]
operator|=
name|number
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|ints
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|ints
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"contracts.conditional.postcondition.not.satisfied"
argument_list|)
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
operator|(
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
operator|(
name|obj
operator|instanceof
name|ImmutableIntList
operator|)
operator|)
condition|?
name|Arrays
operator|.
name|equals
argument_list|(
name|ints
argument_list|,
operator|(
operator|(
name|ImmutableIntList
operator|)
name|obj
operator|)
operator|.
name|ints
argument_list|)
else|:
operator|(
operator|(
name|obj
operator|instanceof
name|List
operator|)
operator|&&
name|obj
operator|.
name|equals
argument_list|(
name|this
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|ints
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|ints
operator|.
name|length
operator|==
literal|0
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
name|ints
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|ints
operator|.
name|length
index|]
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
name|objects
index|[
name|i
index|]
operator|=
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
annotation|@
name|Override
specifier|public
argument_list|<
name|T
argument_list|>
annotation|@
name|Nullable
name|T
index|[]
name|toArray
argument_list|(
name|T
expr|@
name|Nullable
index|[]
name|a
argument_list|)
block|{
specifier|final
name|int
name|size
init|=
name|ints
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|castNonNull
argument_list|(
name|a
argument_list|)
operator|.
name|length
operator|<
name|size
condition|)
block|{
comment|// Make a new array of a's runtime type, but my contents:
name|a
operator|=
name|a
operator|.
name|getClass
argument_list|()
operator|==
name|Object
index|[]
operator|.
name|class
condition|?
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|size
index|]
else|:
operator|(
name|T
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|requireNonNull
argument_list|(
name|a
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
argument_list|)
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|Class
operator|)
name|a
operator|.
name|getClass
argument_list|()
operator|==
name|Integer
index|[]
operator|.
name|class
condition|)
block|{
specifier|final
name|Integer
index|[]
name|integers
init|=
operator|(
name|Integer
index|[]
operator|)
name|a
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|integers
index|[
name|i
index|]
operator|=
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|toArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|a
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|length
operator|>
name|size
condition|)
block|{
name|a
index|[
name|size
index|]
operator|=
name|castNonNull
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
comment|/** Returns an array of {@code int}s with the same contents as this list. */
specifier|public
name|int
index|[]
name|toIntArray
parameter_list|()
block|{
return|return
name|ints
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns an List of {@code Integer}. */
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|toIntegerList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|arrayList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|ints
control|)
block|{
name|arrayList
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|arrayList
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ints
index|[
name|index
index|]
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ints
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|listIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListIterator
argument_list|<
name|Integer
argument_list|>
name|listIterator
parameter_list|()
block|{
return|return
name|listIterator
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListIterator
argument_list|<
name|Integer
argument_list|>
name|listIterator
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|AbstractIndexedListIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|size
argument_list|()
argument_list|,
name|index
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ImmutableIntList
operator|.
name|this
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|indexOf
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|indexOf
argument_list|(
operator|(
name|int
operator|)
operator|(
name|Integer
operator|)
name|o
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|int
name|indexOf
parameter_list|(
name|int
name|seek
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ints
index|[
name|i
index|]
operator|==
name|seek
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|lastIndexOf
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|lastIndexOf
argument_list|(
operator|(
name|int
operator|)
operator|(
name|Integer
operator|)
name|o
argument_list|)
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|int
name|lastIndexOf
parameter_list|(
name|int
name|seek
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|ints
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
if|if
condition|(
name|ints
index|[
name|i
index|]
operator|==
name|seek
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableIntList
name|append
parameter_list|(
name|Integer
name|e
parameter_list|)
block|{
return|return
name|append
argument_list|(
operator|(
name|int
operator|)
name|e
argument_list|)
return|;
block|}
comment|/** Returns a copy of this list with one element added. */
specifier|public
name|ImmutableIntList
name|append
parameter_list|(
name|int
name|element
parameter_list|)
block|{
if|if
condition|(
name|ints
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|of
argument_list|(
name|element
argument_list|)
return|;
block|}
specifier|final
name|int
index|[]
name|newInts
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|ints
argument_list|,
name|ints
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newInts
index|[
name|ints
operator|.
name|length
index|]
operator|=
name|element
expr_stmt|;
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|newInts
argument_list|)
return|;
block|}
comment|/** Returns a list that contains the values lower to upper - 1.    *    *<p>For example, {@code range(1, 3)} contains [1, 2]. */
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|range
parameter_list|(
specifier|final
name|int
name|lower
parameter_list|,
specifier|final
name|int
name|upper
parameter_list|)
block|{
return|return
name|Functions
operator|.
name|generate
argument_list|(
name|upper
operator|-
name|lower
argument_list|,
name|index
lambda|->
name|lower
operator|+
name|index
argument_list|)
return|;
block|}
comment|/** Returns the identity list [0, ..., count - 1].    *    * @see Mappings#isIdentity(List, int)    */
specifier|public
specifier|static
name|ImmutableIntList
name|identity
parameter_list|(
name|int
name|count
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|integers
init|=
operator|new
name|int
index|[
name|count
index|]
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
name|integers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|integers
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|integers
argument_list|)
return|;
block|}
comment|/** Returns a copy of this list with all of the given integers added. */
specifier|public
name|ImmutableIntList
name|appendAll
parameter_list|(
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|instanceof
name|Collection
operator|&&
operator|(
operator|(
name|Collection
operator|)
name|list
operator|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|this
argument_list|,
name|list
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Increments {@code offset} to each element of the list and    * returns a new int list.    */
specifier|public
name|ImmutableIntList
name|incr
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|integers
init|=
operator|new
name|int
index|[
name|ints
operator|.
name|length
index|]
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|integers
index|[
name|i
index|]
operator|=
name|ints
index|[
name|i
index|]
operator|+
name|offset
expr_stmt|;
block|}
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|integers
argument_list|)
return|;
block|}
comment|/** Special sub-class of {@link ImmutableIntList} that is always    * empty and has only one instance. */
specifier|private
specifier|static
class|class
name|EmptyImmutableIntList
extends|extends
name|ImmutableIntList
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|EMPTY_ARRAY
return|;
block|}
annotation|@
name|Override
specifier|public
argument_list|<
name|T
argument_list|>
annotation|@
name|Nullable
name|T
index|[]
name|toArray
argument_list|(
name|T
expr|@
name|Nullable
index|[]
name|a
argument_list|)
block|{
if|if
condition|(
name|castNonNull
argument_list|(
name|a
argument_list|)
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|a
index|[
literal|0
index|]
operator|=
name|castNonNull
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
expr|<
name|Integer
operator|>
name|emptyList
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListIterator
argument_list|<
name|Integer
argument_list|>
name|listIterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
expr|<
name|Integer
operator|>
name|emptyList
argument_list|()
operator|.
name|listIterator
argument_list|()
return|;
block|}
block|}
comment|/** Extension to {@link com.google.common.collect.UnmodifiableListIterator}    * that operates by index.    *    * @param<E> element type */
specifier|private
specifier|abstract
specifier|static
class|class
name|AbstractIndexedListIterator
parameter_list|<
name|E
parameter_list|>
extends|extends
name|UnmodifiableListIterator
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|protected
specifier|abstract
name|E
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
specifier|protected
name|AbstractIndexedListIterator
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkPositionIndex
argument_list|(
name|position
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|position
operator|<
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|E
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|get
argument_list|(
name|position
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|nextIndex
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|hasPrevious
parameter_list|()
block|{
return|return
name|position
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|E
name|previous
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasPrevious
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|get
argument_list|(
operator|--
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|previousIndex
parameter_list|()
block|{
return|return
name|position
operator|-
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

