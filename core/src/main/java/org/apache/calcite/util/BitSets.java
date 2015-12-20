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
name|ImmutableSortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * Utility functions for {@link BitSet}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|BitSets
block|{
specifier|private
name|BitSets
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"no instances!"
argument_list|)
throw|;
block|}
comment|/**    * Returns true if all bits set in the second parameter are also set in the    * first. In other words, whether x is a super-set of y.    *    * @param set0 Containing bitmap    * @param set1 Bitmap to be checked    *    * @return Whether all bits in set1 are set in set0    */
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|BitSet
name|set0
parameter_list|,
name|BitSet
name|set1
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|set1
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|set1
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|set0
operator|.
name|get
argument_list|(
name|i
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
comment|/**    * Returns true if all bits set in the second parameter are also set in the    * first. In other words, whether x is a super-set of y.    *    * @param set0 Containing bitmap    * @param set1 Bitmap to be checked    *    * @return Whether all bits in set1 are set in set0    */
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|BitSet
name|set0
parameter_list|,
name|ImmutableBitSet
name|set1
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|set1
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|set1
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|set0
operator|.
name|get
argument_list|(
name|i
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
comment|/**    * Returns an iterable over the bits in a bitmap that are set to '1'.    *    *<p>This allows you to iterate over a bit set using a 'foreach' construct.    * For instance:    *    *<blockquote><code>    * BitSet bitSet;<br>    * for (int i : Util.toIter(bitSet)) {<br>    *&nbsp;&nbsp;print(i);<br>    * }</code></blockquote>    *    * @param bitSet Bit set    * @return Iterable    */
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|toIter
parameter_list|(
specifier|final
name|BitSet
name|bitSet
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|>=
literal|0
return|;
block|}
specifier|public
name|Integer
name|next
parameter_list|()
block|{
name|int
name|prev
init|=
name|i
decl_stmt|;
name|i
operator|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|prev
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
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|toIter
parameter_list|(
specifier|final
name|ImmutableBitSet
name|bitSet
parameter_list|)
block|{
return|return
name|bitSet
return|;
block|}
comment|/**    * Converts a bitset to a list.    *    *<p>The list is mutable, and future changes to the list do not affect the    * contents of the bit set.    *    * @param bitSet Bit set    * @return List of set bits    */
specifier|public
specifier|static
name|IntList
name|toList
parameter_list|(
specifier|final
name|BitSet
name|bitSet
parameter_list|)
block|{
specifier|final
name|IntList
name|list
init|=
operator|new
name|IntList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Converts a BitSet to an array.    *    * @param bitSet Bit set    * @return Array of set bits    */
specifier|public
specifier|static
name|int
index|[]
name|toArray
parameter_list|(
specifier|final
name|BitSet
name|bitSet
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
name|bitSet
operator|.
name|cardinality
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|integers
index|[
name|j
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|integers
return|;
block|}
comment|/**    * Creates a bitset with given bits set.    *    *<p>For example, {@code of(0, 3)} returns a bit set with bits {0, 3}    * set.    *    * @param bits Array of bits to set    * @return Bit set    */
specifier|public
specifier|static
name|BitSet
name|of
parameter_list|(
name|int
modifier|...
name|bits
parameter_list|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bit
range|:
name|bits
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/**    * Creates a BitSet with given bits set.    *    *<p>For example, {@code of(new Integer[] {0, 3})} returns a bit set    * with bits {0, 3} set.    *    * @param bits Array of bits to set    * @return Bit set    */
specifier|public
specifier|static
name|BitSet
name|of
parameter_list|(
name|Integer
index|[]
name|bits
parameter_list|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bit
range|:
name|bits
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/**    * Creates a BitSet with given bits set.    *    *<p>For example, {@code of(Arrays.asList(0, 3)) } returns a bit set    * with bits {0, 3} set.    *    * @param bits Collection of bits to set    * @return Bit set    */
specifier|public
specifier|static
name|BitSet
name|of
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|bits
parameter_list|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Number
name|bit
range|:
name|bits
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|bit
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/**    * Creates a BitSet with given bits set.    *    *<p>For example, {@code of(ImmutableIntList.of(0, 3))} returns a bit set    * with bits {0, 3} set.    *    * @param bits Collection of bits to set    * @return Bit set    */
specifier|public
specifier|static
name|BitSet
name|of
parameter_list|(
name|ImmutableIntList
name|bits
parameter_list|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|bits
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|bits
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/**    * Creates a bitset with bits from {@code fromIndex} (inclusive) to    * specified {@code toIndex} (exclusive) set to {@code true}.    *    *<p>For example, {@code range(0, 3)} returns a bit set with bits    * {0, 1, 2} set.    *    * @param fromIndex Index of the first bit to be set.    * @param toIndex   Index after the last bit to be set.    * @return Bit set    */
specifier|public
specifier|static
name|BitSet
name|range
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|toIndex
operator|>
name|fromIndex
condition|)
block|{
comment|// Avoid http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6222207
comment|// "BitSet internal invariants may be violated"
name|bitSet
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
block|}
return|return
name|bitSet
return|;
block|}
comment|/** Creates a BitSet with bits between 0 and {@code toIndex} set. */
specifier|public
specifier|static
name|BitSet
name|range
parameter_list|(
name|int
name|toIndex
parameter_list|)
block|{
return|return
name|range
argument_list|(
literal|0
argument_list|,
name|toIndex
argument_list|)
return|;
block|}
comment|/** Sets all bits in a given BitSet corresponding to integers from a list. */
specifier|public
specifier|static
name|void
name|setAll
parameter_list|(
name|BitSet
name|bitSet
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
name|Number
name|number
range|:
name|list
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|number
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns a BitSet that is the union of the given BitSets. Does not modify    * any of the inputs. */
specifier|public
specifier|static
name|BitSet
name|union
parameter_list|(
name|BitSet
name|set0
parameter_list|,
name|BitSet
modifier|...
name|sets
parameter_list|)
block|{
specifier|final
name|BitSet
name|s
init|=
operator|(
name|BitSet
operator|)
name|set0
operator|.
name|clone
argument_list|()
decl_stmt|;
for|for
control|(
name|BitSet
name|set
range|:
name|sets
control|)
block|{
name|s
operator|.
name|or
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/** Returns the previous clear bit.    *    *<p>Has same behavior as {@link BitSet#previousClearBit}, but that method    * does not exist before 1.7. */
specifier|public
specifier|static
name|int
name|previousClearBit
parameter_list|(
name|BitSet
name|bitSet
parameter_list|,
name|int
name|fromIndex
parameter_list|)
block|{
if|if
condition|(
name|fromIndex
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
while|while
condition|(
name|fromIndex
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|bitSet
operator|.
name|get
argument_list|(
name|fromIndex
argument_list|)
condition|)
block|{
return|return
name|fromIndex
return|;
block|}
operator|--
name|fromIndex
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** Computes the closure of a map from integers to bits.    *    *<p>The input must have an entry for each position.    *    *<p>Does not modify the input map or its bit sets. */
specifier|public
specifier|static
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|closure
parameter_list|(
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
parameter_list|)
block|{
if|if
condition|(
name|equivalence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableSortedMap
operator|.
name|of
argument_list|()
return|;
block|}
name|int
name|length
init|=
name|equivalence
operator|.
name|lastKey
argument_list|()
decl_stmt|;
for|for
control|(
name|BitSet
name|bitSet
range|:
name|equivalence
operator|.
name|values
argument_list|()
control|)
block|{
name|length
operator|=
name|Math
operator|.
name|max
argument_list|(
name|length
argument_list|,
name|bitSet
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|equivalence
operator|.
name|size
argument_list|()
operator|<
name|length
operator|||
name|equivalence
operator|.
name|firstKey
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|old
init|=
name|equivalence
decl_stmt|;
name|equivalence
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
name|old
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|equivalence
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|bitSet
operator|==
literal|null
condition|?
operator|new
name|BitSet
argument_list|()
else|:
name|bitSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Closure
name|closure
init|=
operator|new
name|Closure
argument_list|(
name|equivalence
argument_list|)
decl_stmt|;
return|return
name|closure
operator|.
name|closure
return|;
block|}
comment|/** Populates a {@link BitSet} from an iterable, such as a list of integer. */
specifier|public
specifier|static
name|void
name|populate
parameter_list|(
name|BitSet
name|bitSet
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
name|Number
name|number
range|:
name|list
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|number
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Populates a {@link BitSet} from an    *  {@link ImmutableIntList}. */
specifier|public
specifier|static
name|void
name|populate
parameter_list|(
name|BitSet
name|bitSet
parameter_list|,
name|ImmutableIntList
name|list
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|list
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Setup equivalence Sets for each position. If i and j are equivalent then    * they will have the same equivalence Set. The algorithm computes the    * closure relation at each position for the position wrt to positions    * greater than it. Once a closure is computed for a position, the closure    * Set is set on all its descendants. So the closure computation bubbles up    * from lower positions and the final equivalence Set is propagated down    * from the lowest element in the Set.    */
specifier|private
specifier|static
class|class
name|Closure
block|{
specifier|private
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
decl_stmt|;
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|closure
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Closure
parameter_list|(
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
parameter_list|)
block|{
name|this
operator|.
name|equivalence
operator|=
name|equivalence
expr_stmt|;
specifier|final
name|ImmutableIntList
name|keys
init|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|equivalence
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|pos
range|:
name|keys
control|)
block|{
name|computeClosure
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|BitSet
name|computeClosure
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|BitSet
name|o
init|=
name|closure
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
return|return
name|o
return|;
block|}
name|BitSet
name|b
init|=
name|equivalence
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|o
operator|=
operator|(
name|BitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|b
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|b
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|o
operator|.
name|or
argument_list|(
name|computeClosure
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|closure
operator|.
name|put
argument_list|(
name|pos
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|i
operator|=
name|o
operator|.
name|nextSetBit
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|=
name|b
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|closure
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End BitSets.java
end_comment

end_unit

