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
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Collections
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Pair of objects.  *  *<p>Because a pair implements {@link #equals(Object)}, {@link #hashCode()} and  * {@link #compareTo(Pair)}, it can be used in any kind of  * {@link java.util.Collection}.  *  * @param<T1> Left-hand type  * @param<T2> Right-hand type  */
end_comment

begin_class
specifier|public
class|class
name|Pair
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|Pair
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
argument_list|>
implements|,
name|Map
operator|.
name|Entry
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
implements|,
name|Serializable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|T1
name|left
decl_stmt|;
specifier|public
specifier|final
name|T2
name|right
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Pair.    *    * @param left  left value    * @param right right value    */
specifier|public
name|Pair
parameter_list|(
name|T1
name|left
parameter_list|,
name|T2
name|right
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
comment|/**    * Creates a Pair of appropriate type.    *    *<p>This is a shorthand that allows you to omit implicit types. For    * example, you can write:    *<blockquote>return Pair.of(s, n);</blockquote>    * instead of    *<blockquote>return new Pair&lt;String, Integer&gt;(s, n);</blockquote>    *    * @param left  left value    * @param right right value    * @return A Pair    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
name|Pair
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|of
parameter_list|(
name|T1
name|left
parameter_list|,
name|T2
name|right
parameter_list|)
block|{
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
comment|/** Creates a {@code Pair} from a {@link java.util.Map.Entry}. */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|of
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
parameter_list|)
block|{
return|return
name|of
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
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
operator|(
name|obj
operator|instanceof
name|Pair
operator|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|left
argument_list|,
operator|(
operator|(
name|Pair
operator|)
name|obj
operator|)
operator|.
name|left
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|right
argument_list|,
operator|(
operator|(
name|Pair
operator|)
name|obj
operator|)
operator|.
name|right
argument_list|)
return|;
block|}
comment|/** {@inheritDoc}    *    *<p>Computes hash code consistent with    * {@link java.util.Map.Entry#hashCode()}. */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|keyHash
init|=
name|left
operator|==
literal|null
condition|?
literal|0
else|:
name|left
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|int
name|valueHash
init|=
name|right
operator|==
literal|null
condition|?
literal|0
else|:
name|right
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
name|keyHash
operator|^
name|valueHash
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|Nonnull
name|Pair
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|that
parameter_list|)
block|{
comment|//noinspection unchecked
name|int
name|c
init|=
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|this
operator|.
name|left
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
comment|//noinspection unchecked
name|c
operator|=
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|this
operator|.
name|right
argument_list|,
operator|(
name|Comparable
operator|)
name|that
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<"
operator|+
name|left
operator|+
literal|", "
operator|+
name|right
operator|+
literal|">"
return|;
block|}
specifier|public
name|T1
name|getKey
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|T2
name|getValue
parameter_list|()
block|{
return|return
name|right
return|;
block|}
specifier|public
name|T2
name|setValue
parameter_list|(
name|T2
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Compares a pair of comparable values of the same type. Null collates    * less than everything else, but equal to itself.    *    * @param c1 First value    * @param c2 Second value    * @return a negative integer, zero, or a positive integer if c1    * is less than, equal to, or greater than c2.    */
specifier|private
specifier|static
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|C
argument_list|>
parameter_list|>
name|int
name|compare
parameter_list|(
name|C
name|c1
parameter_list|,
name|C
name|c2
parameter_list|)
block|{
if|if
condition|(
name|c1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|c2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
if|else if
condition|(
name|c2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
return|;
block|}
block|}
comment|/**    * Converts a collection of Pairs into a Map.    *    *<p>This is an obvious thing to do because Pair is similar in structure to    * {@link java.util.Map.Entry}.    *    *<p>The map contains a copy of the collection of Pairs; if you change the    * collection, the map does not change.    *    * @param pairs Collection of Pair objects    * @return map with the same contents as the collection    */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|toMap
parameter_list|(
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|pairs
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|pair
range|:
name|pairs
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|pair
operator|.
name|left
argument_list|,
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/**    * Converts two lists into a list of {@link Pair}s,    * whose length is the lesser of the lengths of the    * source lists.    *    * @param ks Left list    * @param vs Right list    * @return List of pairs    * @see org.apache.calcite.linq4j.Ord#zip(java.util.List)    */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|zip
parameter_list|(
name|List
argument_list|<
name|K
argument_list|>
name|ks
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|)
block|{
return|return
name|zip
argument_list|(
name|ks
argument_list|,
name|vs
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Converts two lists into a list of {@link Pair}s.    *    *<p>The length of the combined list is the lesser of the lengths of the    * source lists. But typically the source lists will be the same length.</p>    *    * @param ks     Left list    * @param vs     Right list    * @param strict Whether to fail if lists have different size    * @return List of pairs    * @see org.apache.calcite.linq4j.Ord#zip(java.util.List)    */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|List
argument_list|<
name|K
argument_list|>
name|ks
parameter_list|,
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
specifier|final
name|int
name|size
decl_stmt|;
if|if
condition|(
name|strict
condition|)
block|{
if|if
condition|(
name|ks
operator|.
name|size
argument_list|()
operator|!=
name|vs
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|size
operator|=
name|ks
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
name|Math
operator|.
name|min
argument_list|(
name|ks
operator|.
name|size
argument_list|()
argument_list|,
name|vs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ZipList
argument_list|<>
argument_list|(
name|ks
argument_list|,
name|vs
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**    * Converts two iterables into an iterable of {@link Pair}s.    *    *<p>The resulting iterator ends whenever the first of the input iterators    * ends. But typically the source iterators will be the same length.</p>    *    * @param ks Left iterable    * @param vs Right iterable    * @return Iterable over pairs    */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|K
argument_list|>
name|ks
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|vs
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|K
argument_list|>
name|kIterator
init|=
name|ks
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|vIterator
init|=
name|vs
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ZipIterator
argument_list|<>
argument_list|(
name|kIterator
argument_list|,
name|vIterator
argument_list|)
return|;
block|}
return|;
block|}
comment|/**    * Converts two arrays into a list of {@link Pair}s.    *    *<p>The length of the combined list is the lesser of the lengths of the    * source arrays. But typically the source arrays will be the same    * length.</p>    *    * @param ks Left array    * @param vs Right array    * @return List of pairs    */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|K
index|[]
name|ks
parameter_list|,
specifier|final
name|V
index|[]
name|vs
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ks
index|[
name|index
index|]
argument_list|,
name|vs
index|[
name|index
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|ks
operator|.
name|length
argument_list|,
name|vs
operator|.
name|length
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Returns a mutable list of pairs backed by a pair of mutable lists.    *    *<p>Modifications to this list are reflected in the backing lists, and vice    * versa.    *    * @param<K> Key (left) value type    * @param<V> Value (right) value type */
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|zipMutable
parameter_list|(
specifier|final
name|List
argument_list|<
name|K
argument_list|>
name|ks
parameter_list|,
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|)
block|{
return|return
operator|new
name|MutableZipList
argument_list|<>
argument_list|(
name|ks
argument_list|,
name|vs
argument_list|)
return|;
block|}
comment|/**    * Returns an iterable over the left slice of an iterable.    *    * @param iterable Iterable over pairs    * @param<L>      Left type    * @param<R>      Right type    * @return Iterable over the left elements    */
specifier|public
specifier|static
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
name|Iterable
argument_list|<
name|L
argument_list|>
name|left
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
operator|new
name|LeftIterator
argument_list|<>
argument_list|(
name|iterable
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns an iterable over the right slice of an iterable.    *    * @param iterable Iterable over pairs    * @param<L>      right type    * @param<R>      Right type    * @return Iterable over the right elements    */
specifier|public
specifier|static
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
name|Iterable
argument_list|<
name|R
argument_list|>
name|right
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
operator|new
name|RightIterator
argument_list|<>
argument_list|(
name|iterable
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|K
argument_list|>
name|left
parameter_list|(
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|pairs
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|K
argument_list|>
argument_list|()
block|{
specifier|public
name|K
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|pairs
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getKey
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|pairs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|List
argument_list|<
name|V
argument_list|>
name|right
parameter_list|(
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|pairs
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
specifier|public
name|V
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|pairs
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|pairs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an iterator that iterates over (i, i + 1) pairs in an iterable.    *    *<p>For example, {@code adjacents([3, 5, 7])} returns [(3, 5), (5, 7)].</p>    *    * @param iterable Source collection    * @param<T> Element type    * @return Iterable over adjacent element pairs    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|T
argument_list|,
name|T
argument_list|>
argument_list|>
name|adjacents
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
return|return
operator|new
name|AdjacentIterator
argument_list|<>
argument_list|(
name|iterator
argument_list|)
return|;
block|}
return|;
block|}
comment|/**    * Returns an iterator that iterates over (0, i) pairs in an iterable for    * i&gt; 0.    *    *<p>For example, {@code firstAnd([3, 5, 7])} returns [(3, 5), (3, 7)].</p>    *    * @param iterable Source collection    * @param<T> Element type    * @return Iterable over pairs of the first element and all other elements    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|T
argument_list|,
name|T
argument_list|>
argument_list|>
name|firstAnd
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
specifier|final
name|T
name|first
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|FirstAndIterator
argument_list|<>
argument_list|(
name|iterator
argument_list|,
name|first
argument_list|)
return|;
block|}
return|;
block|}
comment|/** Iterator that returns the left field of each pair.    *    * @param<L> Left-hand type    * @param<R> Right-hand type */
specifier|private
specifier|static
class|class
name|LeftIterator
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|L
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterator
decl_stmt|;
name|LeftIterator
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|L
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Iterator that returns the right field of each pair.    *    * @param<L> Left-hand type    * @param<R> Right-hand type */
specifier|private
specifier|static
class|class
name|RightIterator
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|R
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterator
decl_stmt|;
name|RightIterator
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|R
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Iterator that returns the first element of a collection paired with every    * other element.    *    * @param<E> Element type */
specifier|private
specifier|static
class|class
name|FirstAndIterator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|E
argument_list|,
name|E
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
decl_stmt|;
specifier|private
specifier|final
name|E
name|first
decl_stmt|;
name|FirstAndIterator
parameter_list|(
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|,
name|E
name|first
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|E
argument_list|,
name|E
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|of
argument_list|(
name|first
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
comment|/** Iterator that pairs elements from two iterators.    *    * @param<L> Left-hand type    * @param<R> Right-hand type */
specifier|private
specifier|static
class|class
name|ZipIterator
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|L
argument_list|>
name|leftIterator
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|rightIterator
decl_stmt|;
name|ZipIterator
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|L
argument_list|>
name|leftIterator
parameter_list|,
name|Iterator
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|rightIterator
parameter_list|)
block|{
name|this
operator|.
name|leftIterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|leftIterator
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightIterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rightIterator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|leftIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|rightIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|leftIterator
operator|.
name|next
argument_list|()
argument_list|,
name|rightIterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|leftIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|rightIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Iterator that returns consecutive pairs of elements from an underlying    * iterator.    *    * @param<E> Element type */
specifier|private
specifier|static
class|class
name|AdjacentIterator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|E
argument_list|,
name|E
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|E
name|first
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
decl_stmt|;
name|E
name|previous
decl_stmt|;
name|AdjacentIterator
parameter_list|(
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
name|this
operator|.
name|first
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|previous
operator|=
name|first
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|E
argument_list|,
name|E
argument_list|>
name|next
parameter_list|()
block|{
specifier|final
name|E
name|current
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|E
argument_list|,
name|E
argument_list|>
name|pair
init|=
name|of
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
decl_stmt|;
name|previous
operator|=
name|current
expr_stmt|;
return|return
name|pair
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
comment|/** Unmodifiable list of pairs, backed by a pair of lists.    *    *<p>Though it is unmodifiable, it is mutable: if the contents of one    * of the backing lists changes, the contents of this list will appear to    * change. The length, however, is fixed on creation.    *    * @param<K> Left-hand type    * @param<V> Right-hand type    *    * @see MutableZipList */
specifier|private
specifier|static
class|class
name|ZipList
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|K
argument_list|>
name|ks
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|vs
decl_stmt|;
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
name|ZipList
parameter_list|(
name|List
argument_list|<
name|K
argument_list|>
name|ks
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|ks
operator|=
name|ks
expr_stmt|;
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ks
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
name|vs
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
comment|/** A mutable list of pairs backed by a pair of mutable lists.    *    *<p>Modifications to this list are reflected in the backing lists, and vice    * versa.    *    * @param<K> Key (left) value type    * @param<V> Value (right) value type */
specifier|private
specifier|static
class|class
name|MutableZipList
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|K
argument_list|>
name|ks
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|vs
decl_stmt|;
name|MutableZipList
parameter_list|(
name|List
argument_list|<
name|K
argument_list|>
name|ks
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|)
block|{
name|this
operator|.
name|ks
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|ks
argument_list|)
expr_stmt|;
name|this
operator|.
name|vs
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|vs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ks
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
name|vs
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
name|Math
operator|.
name|min
argument_list|(
name|ks
operator|.
name|size
argument_list|()
argument_list|,
name|vs
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|pair
parameter_list|)
block|{
name|ks
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|pair
operator|.
name|left
argument_list|)
expr_stmt|;
name|vs
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|K
name|bufferedRow
init|=
name|ks
operator|.
name|remove
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|V
name|stateSet
init|=
name|vs
operator|.
name|remove
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|bufferedRow
argument_list|,
name|stateSet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|previous
init|=
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|ks
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|pair
operator|.
name|left
argument_list|)
expr_stmt|;
name|vs
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
return|return
name|previous
return|;
block|}
block|}
block|}
end_class

end_unit

