begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Space-efficient, comparable, immutable lists.  */
end_comment

begin_class
specifier|public
class|class
name|FlatLists
block|{
specifier|public
specifier|static
specifier|final
name|ComparableEmptyList
name|COMPARABLE_EMPTY_LIST
init|=
operator|new
name|ComparableEmptyList
argument_list|()
decl_stmt|;
comment|/** Creates a flat list with 2 elements. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|)
block|{
return|return
operator|new
name|Flat2List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t0
argument_list|,
name|t1
argument_list|)
return|;
block|}
comment|/** Creates a flat list with 3 elements. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|,
name|T
name|t2
parameter_list|)
block|{
return|return
operator|new
name|Flat3List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t0
argument_list|,
name|t1
argument_list|,
name|t2
argument_list|)
return|;
block|}
comment|/**    * Creates a memory-, CPU- and cache-efficient immutable list.    *    * @param t Array of members of list    * @param<T> Element type    * @return List containing the given members    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|T
modifier|...
name|t
parameter_list|)
block|{
return|return
name|_flatList
argument_list|(
name|t
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Creates a memory-, CPU- and cache-efficient immutable list,    * always copying the contents.    *    * @param t Array of members of list    * @param<T> Element type    * @return List containing the given members    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|copy
parameter_list|(
name|T
modifier|...
name|t
parameter_list|)
block|{
return|return
name|_flatList
argument_list|(
name|t
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Creates a memory-, CPU- and cache-efficient immutable list, optionally    * copying the list.    *    * @param copy Whether to always copy the list    * @param t Array of members of list    * @return List containing the given members    */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|_flatList
parameter_list|(
name|T
index|[]
name|t
parameter_list|,
name|boolean
name|copy
parameter_list|)
block|{
switch|switch
condition|(
name|t
operator|.
name|length
condition|)
block|{
case|case
literal|0
case|:
return|return
name|COMPARABLE_EMPTY_LIST
return|;
case|case
literal|1
case|:
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|t
index|[
literal|0
index|]
argument_list|)
return|;
case|case
literal|2
case|:
return|return
operator|new
name|Flat2List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t
index|[
literal|0
index|]
argument_list|,
name|t
index|[
literal|1
index|]
argument_list|)
return|;
case|case
literal|3
case|:
return|return
operator|new
name|Flat3List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t
index|[
literal|0
index|]
argument_list|,
name|t
index|[
literal|1
index|]
argument_list|,
name|t
index|[
literal|2
index|]
argument_list|)
return|;
default|default:
comment|// REVIEW: AbstractList contains a modCount field; we could
comment|//   write our own implementation and reduce creation overhead a
comment|//   bit.
if|if
condition|(
name|copy
condition|)
block|{
return|return
operator|new
name|ComparableListImpl
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|t
operator|.
name|clone
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ComparableListImpl
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|t
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Creates a memory-, CPU- and cache-efficient immutable list from an    * existing list. The list is always copied.    *    * @param t Array of members of list    * @param<T> Element type    * @return List containing the given members    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|t
parameter_list|)
block|{
switch|switch
condition|(
name|t
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|COMPARABLE_EMPTY_LIST
return|;
case|case
literal|1
case|:
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|t
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
case|case
literal|2
case|:
return|return
operator|new
name|Flat2List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|t
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
case|case
literal|3
case|:
return|return
operator|new
name|Flat3List
argument_list|<
name|T
argument_list|>
argument_list|(
name|t
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|t
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|t
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
return|;
default|default:
comment|// REVIEW: AbstractList contains a modCount field; we could
comment|//   write our own implementation and reduce creation overhead a
comment|//   bit.
comment|//noinspection unchecked
return|return
operator|new
name|ComparableListImpl
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|t
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
specifier|abstract
class|class
name|AbstractFlatList
parameter_list|<
name|T
parameter_list|>
implements|implements
name|List
argument_list|<
name|T
argument_list|>
implements|,
name|RandomAccess
block|{
specifier|protected
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|asArrayList
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|T
index|[]
operator|)
name|toArray
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|asArrayList
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|ListIterator
argument_list|<
name|T
argument_list|>
name|listIterator
parameter_list|()
block|{
return|return
name|asArrayList
argument_list|()
operator|.
name|listIterator
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|add
parameter_list|(
name|T
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|T
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
specifier|public
name|boolean
name|addAll
parameter_list|(
name|int
name|index
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|T
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
specifier|public
name|boolean
name|retainAll
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
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|T
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|T
name|element
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|T
name|element
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|T
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|ListIterator
argument_list|<
name|T
argument_list|>
name|listIterator
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|asArrayList
argument_list|()
operator|.
name|listIterator
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|subList
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|)
block|{
return|return
name|asArrayList
argument_list|()
operator|.
name|subList
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|indexOf
argument_list|(
name|o
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|public
name|boolean
name|containsAll
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|c
control|)
block|{
if|if
condition|(
operator|!
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|remove
parameter_list|(
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
block|}
comment|/**    * List that stores its two elements in the two members of the class.    * Unlike {@link java.util.ArrayList} or    * {@link java.util.Arrays#asList(Object[])} there is    * no array, only one piece of memory allocated, therefore is very compact    * and cache and CPU efficient.    *    *<p>The list is read-only, cannot be modified or resized, and neither    * of the elements can be null.    *    *<p>The list is created via {@link FlatLists#of}.    *    * @param<T>    */
specifier|protected
specifier|static
class|class
name|Flat2List
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractFlatList
argument_list|<
name|T
argument_list|>
implements|implements
name|ComparableList
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|T
name|t0
decl_stmt|;
specifier|private
specifier|final
name|T
name|t1
decl_stmt|;
name|Flat2List
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|)
block|{
name|this
operator|.
name|t0
operator|=
name|t0
expr_stmt|;
name|this
operator|.
name|t1
operator|=
name|t1
expr_stmt|;
assert|assert
name|t0
operator|!=
literal|null
assert|;
assert|assert
name|t1
operator|!=
literal|null
assert|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|t0
operator|+
literal|", "
operator|+
name|t1
operator|+
literal|"]"
return|;
block|}
specifier|public
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
switch|switch
condition|(
name|index
condition|)
block|{
case|case
literal|0
case|:
return|return
name|t0
return|;
case|case
literal|1
case|:
return|return
name|t1
return|;
default|default:
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"index "
operator|+
name|index
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|t0
argument_list|,
name|t1
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Flat2List
condition|)
block|{
name|Flat2List
name|that
init|=
operator|(
name|Flat2List
operator|)
name|o
decl_stmt|;
return|return
name|Utilities
operator|.
name|equal
argument_list|(
name|this
operator|.
name|t0
argument_list|,
name|that
operator|.
name|t0
argument_list|)
operator|&&
name|Utilities
operator|.
name|equal
argument_list|(
name|this
operator|.
name|t1
argument_list|,
name|that
operator|.
name|t1
argument_list|)
return|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|t0
argument_list|,
name|t1
argument_list|)
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|1
decl_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|Utilities
operator|.
name|hash
argument_list|(
name|t0
argument_list|)
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|Utilities
operator|.
name|hash
argument_list|(
name|t1
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
specifier|public
name|int
name|indexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|t0
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|t1
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|1
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
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|t1
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|t0
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
specifier|public
parameter_list|<
name|T2
parameter_list|>
name|T2
index|[]
name|toArray
parameter_list|(
name|T2
index|[]
name|a
parameter_list|)
block|{
name|a
index|[
literal|0
index|]
operator|=
operator|(
name|T2
operator|)
name|t0
expr_stmt|;
name|a
index|[
literal|1
index|]
operator|=
operator|(
name|T2
operator|)
name|t1
expr_stmt|;
return|return
name|a
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
name|t0
block|,
name|t1
block|}
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|List
name|o
parameter_list|)
block|{
comment|//noinspection unchecked
name|Flat2List
argument_list|<
name|T
argument_list|>
name|that
init|=
operator|(
name|Flat2List
argument_list|<
name|T
argument_list|>
operator|)
name|o
decl_stmt|;
name|int
name|c
init|=
name|Utilities
operator|.
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|t0
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|t0
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|Utilities
operator|.
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|t1
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|t1
argument_list|)
return|;
block|}
block|}
comment|/**    * List that stores its three elements in the three members of the class.    * Unlike {@link java.util.ArrayList} or    * {@link java.util.Arrays#asList(Object[])} there is    * no array, only one piece of memory allocated, therefore is very compact    * and cache and CPU efficient.    *    *<p>The list is read-only, cannot be modified or resized, and none    * of the elements can be null.    *    *<p>The list is created via {@link FlatLists#of(java.util.List)}.    *    * @param<T>    */
specifier|protected
specifier|static
class|class
name|Flat3List
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractFlatList
argument_list|<
name|T
argument_list|>
implements|implements
name|ComparableList
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|T
name|t0
decl_stmt|;
specifier|private
specifier|final
name|T
name|t1
decl_stmt|;
specifier|private
specifier|final
name|T
name|t2
decl_stmt|;
name|Flat3List
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|,
name|T
name|t2
parameter_list|)
block|{
name|this
operator|.
name|t0
operator|=
name|t0
expr_stmt|;
name|this
operator|.
name|t1
operator|=
name|t1
expr_stmt|;
name|this
operator|.
name|t2
operator|=
name|t2
expr_stmt|;
assert|assert
name|t0
operator|!=
literal|null
assert|;
assert|assert
name|t1
operator|!=
literal|null
assert|;
assert|assert
name|t2
operator|!=
literal|null
assert|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|t0
operator|+
literal|", "
operator|+
name|t1
operator|+
literal|", "
operator|+
name|t2
operator|+
literal|"]"
return|;
block|}
specifier|public
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
switch|switch
condition|(
name|index
condition|)
block|{
case|case
literal|0
case|:
return|return
name|t0
return|;
case|case
literal|1
case|:
return|return
name|t1
return|;
case|case
literal|2
case|:
return|return
name|t2
return|;
default|default:
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"index "
operator|+
name|index
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|3
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|t0
argument_list|,
name|t1
argument_list|,
name|t2
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Flat3List
condition|)
block|{
name|Flat3List
name|that
init|=
operator|(
name|Flat3List
operator|)
name|o
decl_stmt|;
return|return
name|Utilities
operator|.
name|equal
argument_list|(
name|this
operator|.
name|t0
argument_list|,
name|that
operator|.
name|t0
argument_list|)
operator|&&
name|Utilities
operator|.
name|equal
argument_list|(
name|this
operator|.
name|t1
argument_list|,
name|that
operator|.
name|t1
argument_list|)
operator|&&
name|Utilities
operator|.
name|equal
argument_list|(
name|this
operator|.
name|t2
argument_list|,
name|that
operator|.
name|t2
argument_list|)
return|;
block|}
return|return
name|o
operator|.
name|equals
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|1
decl_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|Utilities
operator|.
name|hash
argument_list|(
name|t0
argument_list|)
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|Utilities
operator|.
name|hash
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|31
operator|+
name|Utilities
operator|.
name|hash
argument_list|(
name|t2
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
specifier|public
name|int
name|indexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|t0
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|t1
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|t2
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|2
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
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|t2
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|2
return|;
block|}
if|if
condition|(
name|t1
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|t0
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
specifier|public
parameter_list|<
name|T2
parameter_list|>
name|T2
index|[]
name|toArray
parameter_list|(
name|T2
index|[]
name|a
parameter_list|)
block|{
name|a
index|[
literal|0
index|]
operator|=
operator|(
name|T2
operator|)
name|t0
expr_stmt|;
name|a
index|[
literal|1
index|]
operator|=
operator|(
name|T2
operator|)
name|t1
expr_stmt|;
name|a
index|[
literal|2
index|]
operator|=
operator|(
name|T2
operator|)
name|t2
expr_stmt|;
return|return
name|a
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
name|t0
block|,
name|t1
block|,
name|t2
block|}
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|List
name|o
parameter_list|)
block|{
comment|//noinspection unchecked
name|Flat3List
argument_list|<
name|T
argument_list|>
name|that
init|=
operator|(
name|Flat3List
argument_list|<
name|T
argument_list|>
operator|)
name|o
decl_stmt|;
name|int
name|c
init|=
name|Utilities
operator|.
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|t0
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|t0
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
name|c
operator|=
name|Utilities
operator|.
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|t1
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|t1
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|Utilities
operator|.
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|t2
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|t2
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ComparableEmptyList
extends|extends
name|AbstractList
implements|implements
name|Comparable
argument_list|<
name|List
argument_list|>
block|{
specifier|private
name|ComparableEmptyList
parameter_list|()
block|{
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|1
return|;
comment|// same as Collections.emptyList()
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|List
operator|&&
operator|(
operator|(
name|List
operator|)
name|o
operator|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|List
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|o
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
block|}
comment|/** List that is also comparable.    *    *<p>You can create an instance whose type    * parameter {@code T} does not extend {@link Comparable}, but you will get a    * {@link ClassCastException} at runtime when you call    * {@link #compareTo(Object)} if the elements of the list do not implement    * {@code Comparable}.    */
specifier|public
interface|interface
name|ComparableList
parameter_list|<
name|T
parameter_list|>
extends|extends
name|List
argument_list|<
name|T
argument_list|>
extends|,
name|Comparable
argument_list|<
name|List
argument_list|>
block|{   }
specifier|static
class|class
name|ComparableListImpl
parameter_list|<
name|T
extends|extends
name|Comparable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|T
argument_list|>
implements|implements
name|ComparableList
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
decl_stmt|;
specifier|protected
name|ComparableListImpl
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
specifier|public
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|List
name|o
parameter_list|)
block|{
return|return
name|compare
argument_list|(
name|list
argument_list|,
name|o
argument_list|)
return|;
block|}
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|int
name|compare
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list0
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|list1
parameter_list|)
block|{
specifier|final
name|int
name|size0
init|=
name|list0
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size1
init|=
name|list1
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size1
operator|==
name|size0
condition|)
block|{
return|return
name|compare
argument_list|(
name|list0
argument_list|,
name|list1
argument_list|,
name|size0
argument_list|)
return|;
block|}
specifier|final
name|int
name|c
init|=
name|compare
argument_list|(
name|list0
argument_list|,
name|list1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|size0
argument_list|,
name|size1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|size0
operator|-
name|size1
return|;
block|}
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
parameter_list|>
name|int
name|compare
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list0
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|list1
parameter_list|,
name|int
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
name|o0
init|=
name|list0
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Comparable
name|o1
init|=
name|list1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|o0
operator|.
name|compareTo
argument_list|(
name|o1
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FlatLists.java
end_comment

end_unit

