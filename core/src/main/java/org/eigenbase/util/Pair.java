begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
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
name|*
import|;
end_import

begin_comment
comment|/**  * Pair of objects.  *  *<p>Because a pair implements {@link #equals(Object)}, {@link #hashCode()} and  * {@link #compareTo(Pair)}, it can be used in any kind of  * {@link java.util.Collection}.  */
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
comment|/**      * Creates a Pair.      *      * @param left left value      * @param right right value      */
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
comment|/**      * Creates a Pair of appropriate type.      *      *<p>This is a shorthand that allows you to omit implicit types. For      * example, you can write:      *<blockquote>return Pair.of(s, n);</blockquote>      * instead of      *<blockquote>return new Pair&lt;String, Integer&gt;(s, n);</blockquote>      *      * @param left left value      * @param right right value      * @return A Pair      */
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
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
argument_list|(
name|left
argument_list|,
name|right
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
name|Util
operator|.
name|equal
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
name|Util
operator|.
name|equal
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
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h1
init|=
name|Util
operator|.
name|hash
argument_list|(
literal|0
argument_list|,
name|left
argument_list|)
decl_stmt|;
return|return
name|Util
operator|.
name|hash
argument_list|(
name|h1
argument_list|,
name|right
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Pair
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|that
parameter_list|)
block|{
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
comment|/**      * Compares a pair of comparable values of the same type. Null collates      * less than everything else, but equal to itself.      *      * @param c1 First value      * @param c2 Second value      * @return  a negative integer, zero, or a positive integer if c1      *          is less than, equal to, or greater than c2.      */
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
comment|/**      * Converts a collection of Pairs into a Map.      *      *<p>This is an obvious thing to do because Pair is similar in structure to      * {@link java.util.Map.Entry}.      *      *<p>The map contains a copy of the collection of Pairs; if you change the      * collection, the map does not change.      *      * @param pairs Collection of Pair objects      *      * @return map with the same contents as the collection      */
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
name|Collection
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
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
comment|/**      * Converts two lists into a list of {@link org.eigenbase.util.Pair}s,      * whose length is the lesser of the lengths of the      * source lists.</p>      *      * @param ks Left list      * @param vs Right list      * @return List of pairs      *      * @see net.hydromatic.linq4j.Ord#zip(java.util.List)      */
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
comment|/**      * Converts two lists into a list of {@link Pair}s.      *      *<p>The length of the combined list is the lesser of the lengths of the      * source lists. But typically the source lists will be the same length.</p>      *      * @param ks Left list      * @param vs Right list      * @param strict Whether to fail if lists have different size      * @return List of pairs      *      * @see net.hydromatic.linq4j.Ord#zip(java.util.List)      */
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
return|;
block|}
comment|/**      * Converts two iterables into an iterable of {@link Pair}s.      *      *<p>The resulting iterator ends whenever the first of the input iterators      * ends. But typically the source iterators will be the same length.</p>      *      * @param ks Left iterable      * @param vs Right iterable      * @return Iterable over pairs      */
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
name|K
argument_list|>
name|ks
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|V
argument_list|>
name|vs
parameter_list|)
block|{
return|return
operator|new
name|Iterable
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
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
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
name|Iterator
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
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|kIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|vIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|kIterator
operator|.
name|next
argument_list|()
argument_list|,
name|vIterator
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
name|kIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|vIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**      * Converts two arrays into a list of {@link Pair}s.      *      *<p>The length of the combined list is the lesser of the lengths of the      * source arrays. But typically the source arrays will be the same      * length.</p>      *      * @param ks Left array      * @param vs Right array      * @return List of pairs      */
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
comment|/**      * Returns an iterable over the left slice of an iterable.      *      * @param iterable Iterable over pairs      * @param<L> Left type      * @param<R> Right type      * @return Iterable over the left elements      */
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
operator|new
name|Iterable
argument_list|<
name|L
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|L
argument_list|>
name|iterator
parameter_list|()
block|{
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
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|L
argument_list|>
argument_list|()
block|{
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
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns an iterable over the right slice of an iterable.      *      * @param iterable Iterable over pairs      * @param<L> right type      * @param<R> Right type      * @return Iterable over the right elements      */
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
operator|new
name|Iterable
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|R
argument_list|>
name|iterator
parameter_list|()
block|{
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
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
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
block|}
end_class

begin_comment
comment|// End Pair.java
end_comment

end_unit

