begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|TestUtil
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link PartiallyOrderedSet}.  */
end_comment

begin_class
specifier|public
class|class
name|PartiallyOrderedSetTest
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
comment|// 100, 250, 1000, 3000 are reasonable
specifier|private
specifier|static
specifier|final
name|int
name|SCALE
init|=
name|OptiqAssert
operator|.
name|ENABLE_SLOW
condition|?
literal|250
else|:
literal|50
decl_stmt|;
specifier|final
name|long
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|String
argument_list|>
name|STRING_SUBSET_ORDERING
init|=
operator|new
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|String
name|e1
parameter_list|,
name|String
name|e2
parameter_list|)
block|{
comment|// e1< e2 if every char in e1 is also in e2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|e1
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|e2
operator|.
name|indexOf
argument_list|(
name|e1
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|<
literal|0
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
block|}
decl_stmt|;
comment|// Integers, ordered by division. Top is 1, its children are primes,
comment|// etc.
specifier|static
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
name|IS_DIVISOR
init|=
operator|new
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|Integer
name|e1
parameter_list|,
name|Integer
name|e2
parameter_list|)
block|{
return|return
name|e2
operator|%
name|e1
operator|==
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|// Bottom is 1, parents are primes, etc.
specifier|static
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
name|IS_DIVISOR_INVERSE
init|=
operator|new
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|Integer
name|e1
parameter_list|,
name|Integer
name|e2
parameter_list|)
block|{
return|return
name|e1
operator|%
name|e2
operator|==
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|// Ordered by bit inclusion. E.g. the children of 14 (1110) are
comment|// 12 (1100), 10 (1010) and 6 (0110).
specifier|static
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
name|IS_BIT_SUBSET
init|=
operator|new
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|Integer
name|e1
parameter_list|,
name|Integer
name|e2
parameter_list|)
block|{
return|return
operator|(
name|e2
operator|&
name|e1
operator|)
operator|==
name|e2
return|;
block|}
block|}
decl_stmt|;
comment|// Ordered by bit inclusion. E.g. the children of 14 (1110) are
comment|// 12 (1100), 10 (1010) and 6 (0110).
specifier|static
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
name|IS_BIT_SUPERSET
init|=
operator|new
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|lessThan
parameter_list|(
name|Integer
name|e1
parameter_list|,
name|Integer
name|e2
parameter_list|)
block|{
return|return
operator|(
name|e2
operator|&
name|e1
operator|)
operator|==
name|e1
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testPoset
parameter_list|()
block|{
name|String
name|empty
init|=
literal|"''"
decl_stmt|;
name|String
name|abcd
init|=
literal|"'abcd'"
decl_stmt|;
name|PartiallyOrderedSet
argument_list|<
name|String
argument_list|>
name|poset
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|STRING_SUBSET_ORDERING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|poset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|poset
operator|.
name|out
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"PartiallyOrderedSet size: 0 elements: {\n"
operator|+
literal|"}"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|poset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
name|empty
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
name|abcd
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|poset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['abcd']"
argument_list|,
name|poset
operator|.
name|getNonChildren
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getNonParents
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|ab
init|=
literal|"'ab'"
decl_stmt|;
name|poset
operator|.
name|add
argument_list|(
name|ab
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|poset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|empty
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['ab']"
argument_list|,
name|poset
operator|.
name|getParents
argument_list|(
name|empty
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['ab']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|abcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|poset
operator|.
name|getParents
argument_list|(
name|abcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|ab
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['abcd']"
argument_list|,
name|poset
operator|.
name|getParents
argument_list|(
name|ab
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// "bcd" is child of "abcd" and parent of ""
specifier|final
name|String
name|bcd
init|=
literal|"'bcd'"
decl_stmt|;
name|poset
operator|.
name|add
argument_list|(
name|bcd
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|poset
operator|.
name|isValid
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|bcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['abcd']"
argument_list|,
name|poset
operator|.
name|getParents
argument_list|(
name|bcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['ab', 'bcd']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|abcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|poset
operator|.
name|out
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"PartiallyOrderedSet size: 4 elements: {\n"
operator|+
literal|"  'abcd' parents: [] children: ['ab', 'bcd']\n"
operator|+
literal|"  'ab' parents: ['abcd'] children: ['']\n"
operator|+
literal|"  'bcd' parents: ['abcd'] children: ['']\n"
operator|+
literal|"  '' parents: ['ab', 'bcd'] children: []\n"
operator|+
literal|"}"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|b
init|=
literal|"'b'"
decl_stmt|;
comment|// ancestors of an element not in the set
name|assertEqualsList
argument_list|(
literal|"['ab', 'abcd', 'bcd']"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['abcd']"
argument_list|,
name|poset
operator|.
name|getNonChildren
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getNonParents
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|b
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEqualsList
argument_list|(
literal|"['ab', 'bcd']"
argument_list|,
name|poset
operator|.
name|getParents
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|b
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['ab', 'bcd']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|abcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['b']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|bcd
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"['b']"
argument_list|,
name|poset
operator|.
name|getChildren
argument_list|(
name|ab
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEqualsList
argument_list|(
literal|"['ab', 'abcd', 'bcd']"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
comment|// descendants and ancestors of an element with no descendants
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|poset
operator|.
name|getDescendants
argument_list|(
name|empty
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEqualsList
argument_list|(
literal|"['ab', 'abcd', 'b', 'bcd']"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
name|empty
argument_list|)
argument_list|)
expr_stmt|;
comment|// some more ancestors of missing elements
name|assertEqualsList
argument_list|(
literal|"['abcd']"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
literal|"'ac'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEqualsList
argument_list|(
literal|"[]"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
literal|"'z'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEqualsList
argument_list|(
literal|"['ab', 'abcd']"
argument_list|,
name|poset
operator|.
name|getAncestors
argument_list|(
literal|"'a'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPosetTricky
parameter_list|()
block|{
name|PartiallyOrderedSet
argument_list|<
name|String
argument_list|>
name|poset
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|STRING_SUBSET_ORDERING
argument_list|)
decl_stmt|;
comment|// A tricky little poset with 4 elements:
comment|// {a<= ab and ac, b< ab, ab, ac}
name|poset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"'a'"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"'b'"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"'ac'"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|"'ab'"
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPosetBits
parameter_list|()
block|{
specifier|final
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
name|poset
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|IS_BIT_SUPERSET
argument_list|)
decl_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|2112
argument_list|)
expr_stmt|;
comment|// {6, 11} i.e. 64 + 2048
name|poset
operator|.
name|add
argument_list|(
literal|2240
argument_list|)
expr_stmt|;
comment|// {6, 7, 11} i.e. 64 + 128 + 2048
name|poset
operator|.
name|add
argument_list|(
literal|2496
argument_list|)
expr_stmt|;
comment|// {6, 7, 8, 11} i.e. 64 + 128 + 256 + 2048
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|remove
argument_list|(
literal|2240
argument_list|)
expr_stmt|;
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|2240
argument_list|)
expr_stmt|;
comment|// {6, 7, 11} i.e. 64 + 128 + 2048
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPosetBitsRemoveParent
parameter_list|()
block|{
specifier|final
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
name|poset
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|IS_BIT_SUPERSET
argument_list|)
decl_stmt|;
name|poset
operator|.
name|add
argument_list|(
literal|66
argument_list|)
expr_stmt|;
comment|// {bit 2, bit 6}
name|poset
operator|.
name|add
argument_list|(
literal|68
argument_list|)
expr_stmt|;
comment|// {bit 3, bit 6}
name|poset
operator|.
name|add
argument_list|(
literal|72
argument_list|)
expr_stmt|;
comment|// {bit 4, bit 6}
name|poset
operator|.
name|add
argument_list|(
literal|64
argument_list|)
expr_stmt|;
comment|// {bit 6}
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
name|poset
operator|.
name|remove
argument_list|(
literal|64
argument_list|)
expr_stmt|;
comment|// {bit 6}
name|printValidate
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorPoset
parameter_list|()
block|{
if|if
condition|(
operator|!
name|OptiqAssert
operator|.
name|ENABLE_SLOW
condition|)
block|{
return|return;
block|}
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
name|integers
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|IS_DIVISOR
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20, 24, 30, 40, 60]"
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|integers
operator|.
name|getDescendants
argument_list|(
literal|120
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[240, 360, 480, 600, 720, 840, 960]"
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|integers
operator|.
name|getAncestors
argument_list|(
literal|120
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|integers
operator|.
name|getDescendants
argument_list|(
literal|1
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|998
argument_list|,
name|integers
operator|.
name|getAncestors
argument_list|(
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|integers
operator|.
name|isValid
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorSeries
parameter_list|()
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR
argument_list|,
name|DEBUG
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorRandom
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR
argument_list|,
name|DEBUG
argument_list|,
name|random
argument_list|(
name|random
argument_list|,
name|SCALE
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorRandomWithRemoval
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR
argument_list|,
name|DEBUG
argument_list|,
name|random
argument_list|(
name|random
argument_list|,
name|SCALE
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorInverseSeries
parameter_list|()
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR_INVERSE
argument_list|,
name|DEBUG
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorInverseRandom
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR_INVERSE
argument_list|,
name|DEBUG
argument_list|,
name|random
argument_list|(
name|random
argument_list|,
name|SCALE
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivisorInverseRandomWithRemoval
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|checkPoset
argument_list|(
name|IS_DIVISOR_INVERSE
argument_list|,
name|DEBUG
argument_list|,
name|random
argument_list|(
name|random
argument_list|,
name|SCALE
argument_list|,
name|SCALE
operator|*
literal|3
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubsetSeries
parameter_list|()
block|{
name|checkPoset
argument_list|(
name|IS_BIT_SUBSET
argument_list|,
name|DEBUG
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
name|SCALE
operator|/
literal|2
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubsetRandom
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|checkPoset
argument_list|(
name|IS_BIT_SUBSET
argument_list|,
name|DEBUG
argument_list|,
name|random
argument_list|(
name|random
argument_list|,
name|SCALE
operator|/
literal|4
argument_list|,
name|SCALE
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Random seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
parameter_list|<
name|E
parameter_list|>
name|void
name|printValidate
parameter_list|(
name|PartiallyOrderedSet
argument_list|<
name|E
argument_list|>
name|poset
parameter_list|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|dump
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|poset
operator|.
name|isValid
argument_list|(
name|DEBUG
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkPoset
parameter_list|(
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Integer
argument_list|>
name|ordering
parameter_list|,
name|boolean
name|debug
parameter_list|,
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|generator
parameter_list|,
name|boolean
name|remove
parameter_list|)
block|{
specifier|final
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
name|poset
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|ordering
argument_list|)
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|z
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|dump
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
range|:
name|generator
control|)
block|{
if|if
condition|(
name|remove
operator|&&
name|z
operator|++
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|debug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"remove "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|poset
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|dump
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|debug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"add "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|poset
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|dump
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|++
name|n
argument_list|,
name|poset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|100
condition|)
block|{
if|if
condition|(
operator|!
name|poset
operator|.
name|isValid
argument_list|(
literal|false
argument_list|)
condition|)
block|{
name|dump
argument_list|(
name|poset
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|poset
operator|.
name|isValid
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|poset
operator|.
name|isValid
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|poset
operator|.
name|out
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
parameter_list|<
name|E
parameter_list|>
name|void
name|dump
parameter_list|(
name|PartiallyOrderedSet
argument_list|<
name|E
argument_list|>
name|poset
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|poset
operator|.
name|out
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Collection
argument_list|<
name|Integer
argument_list|>
name|range
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
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
name|start
operator|+
name|index
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
name|end
operator|-
name|start
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|random
parameter_list|(
name|Random
name|random
parameter_list|,
specifier|final
name|int
name|size
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|set
operator|.
name|size
argument_list|()
operator|<
name|size
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|max
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
specifier|private
specifier|static
name|void
name|assertEqualsList
parameter_list|(
name|String
name|expected
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ss
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|ss
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PartiallyOrderedSetTest.java
end_comment

end_unit

