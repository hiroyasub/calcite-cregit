begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|ImmutableIntList
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|BitSet
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
name|SortedMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link net.hydromatic.optiq.util.BitSets}.  */
end_comment

begin_class
specifier|public
class|class
name|BitSetsTest
block|{
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#toIter(java.util.BitSet)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testToIterBitSet
parameter_list|()
block|{
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|assertToIterBitSet
argument_list|(
literal|""
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertToIterBitSet
argument_list|(
literal|"0"
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertToIterBitSet
argument_list|(
literal|"0, 1"
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertToIterBitSet
argument_list|(
literal|"10"
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that iterating over a BitSet yields the expected string.    *    * @param expected Expected string    * @param bitSet   Bit set    */
specifier|private
name|void
name|assertToIterBitSet
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
name|BitSet
name|bitSet
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|bitSet
argument_list|)
control|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#toList(java.util.BitSet)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testToListBitSet
parameter_list|()
block|{
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|bitSet
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Integer
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|bitSet
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|bitSet
operator|.
name|set
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|bitSet
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method {@link net.hydromatic.optiq.util.BitSets#of(int...)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetOf
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Integer
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method {@link net.hydromatic.optiq.util.BitSets#range(int, int)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetsRange
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Integer
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#toArray(java.util.BitSet)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetsToArray
parameter_list|()
block|{
name|int
index|[]
index|[]
name|arrays
init|=
block|{
block|{}
block|,
block|{
literal|0
block|}
block|,
block|{
literal|0
block|,
literal|2
block|}
block|,
block|{
literal|1
block|,
literal|65
block|}
block|,
block|{
literal|100
block|}
block|}
decl_stmt|;
for|for
control|(
name|int
index|[]
name|array
range|:
name|arrays
control|)
block|{
name|assertThat
argument_list|(
name|BitSets
operator|.
name|toArray
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
name|array
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#union(java.util.BitSet, java.util.BitSet...)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetsUnion
parameter_list|()
block|{
name|assertThat
argument_list|(
name|BitSets
operator|.
name|union
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{1, 3}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|union
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|100
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{1, 3, 100}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|union
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|2
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{1, 2, 3}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#contains(java.util.BitSet, java.util.BitSet)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetsContains
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|BitSets
operator|.
name|range
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|14
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|20
argument_list|,
literal|25
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|14
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BitSet
name|empty
init|=
name|BitSets
operator|.
name|of
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|range
argument_list|(
literal|20
argument_list|,
literal|25
argument_list|)
argument_list|,
name|empty
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|empty
argument_list|,
name|empty
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|empty
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|empty
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|empty
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|BitSets
operator|.
name|contains
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#of(org.eigenbase.util.ImmutableIntList)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testBitSetOfImmutableIntList
parameter_list|()
block|{
name|ImmutableIntList
name|list
init|=
name|ImmutableIntList
operator|.
name|of
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
name|list
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BitSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
name|ImmutableIntList
operator|.
name|of
argument_list|(
literal|2
argument_list|,
literal|70
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
name|list
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|70
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method    * {@link net.hydromatic.optiq.util.BitSets#previousClearBit(java.util.BitSet, int)}.    */
annotation|@
name|Test
specifier|public
name|void
name|testPreviousClearBit
parameter_list|()
block|{
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|()
argument_list|,
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|int
name|actual
init|=
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|()
argument_list|,
operator|-
literal|2
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|actual
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|4
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|previousClearBit
argument_list|(
name|BitSets
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the method {@link BitSets#closure(java.util.SortedMap)}    */
annotation|@
name|Test
specifier|public
name|void
name|testClosure
parameter_list|()
block|{
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|empty
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|closure
argument_list|(
name|empty
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|empty
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currently you need an entry for each position, otherwise you get an NPE.
comment|// We should fix that.
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|0
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|1
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|2
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|3
argument_list|,
name|BitSets
operator|.
name|of
argument_list|(
literal|4
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|4
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|5
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|6
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|7
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|8
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|9
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|10
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|11
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|12
argument_list|,
name|BitSets
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|original
init|=
name|map
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|BitSets
operator|.
name|closure
argument_list|(
name|map
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{0={3, 4, 12}, 1={}, 2={7}, 3={3, 4, 12}, 4={4, 12}, 5={}, 6={}, 7={7}, 8={}, 9={}, 10={}, 11={}, 12={4, 12}}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"argument modified"
argument_list|,
name|map
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|original
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End BitSetsTest.java
end_comment

end_unit

