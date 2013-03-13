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
name|test
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
name|runtime
operator|.
name|SqlFunctions
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Utilities
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|SqlFunctions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for the methods in {@link SqlFunctions} that implement SQL  * functions.  */
end_comment

begin_class
specifier|public
class|class
name|SqlFunctionsTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testCharLength
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|(
name|Integer
operator|)
literal|3
argument_list|,
name|charLength
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|charLength
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConcat
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a bcd"
argument_list|,
name|concat
argument_list|(
literal|"a b"
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|concat
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|concat
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|concat
argument_list|(
literal|null
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLower
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a bcd"
argument_list|,
name|lower
argument_list|(
literal|"A bCd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lower
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUpper
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"A BCD"
argument_list|,
name|upper
argument_list|(
literal|"A bCd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lower
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInitCap
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Aa"
argument_list|,
name|initCap
argument_list|(
literal|"aA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Zz"
argument_list|,
name|initCap
argument_list|(
literal|"zz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Az"
argument_list|,
name|initCap
argument_list|(
literal|"AZ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Try A Little  "
argument_list|,
name|initCap
argument_list|(
literal|"tRy a littlE  "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1a"
argument_list|,
name|initCap
argument_list|(
literal|"1A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" B0123b"
argument_list|,
name|initCap
argument_list|(
literal|" b0123B"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLesser
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|lesser
argument_list|(
literal|"a"
argument_list|,
literal|"bc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ac"
argument_list|,
name|lesser
argument_list|(
literal|"bc"
argument_list|,
literal|"ac"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|lesser
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected NPE, got "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|lesser
argument_list|(
literal|null
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lesser
argument_list|(
operator|(
name|Comparable
operator|)
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGreater
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"bc"
argument_list|,
name|greater
argument_list|(
literal|"a"
argument_list|,
literal|"bc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bc"
argument_list|,
name|greater
argument_list|(
literal|"bc"
argument_list|,
literal|"ac"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|greater
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected NPE, got "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|greater
argument_list|(
literal|null
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|greater
argument_list|(
operator|(
name|Comparable
operator|)
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link SqlFunctions#rtrim}. */
specifier|public
name|void
name|testRtrim
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|rtrim
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|rtrim
argument_list|(
literal|"    "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"   x"
argument_list|,
name|rtrim
argument_list|(
literal|"   x  "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"   x"
argument_list|,
name|rtrim
argument_list|(
literal|"   x "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"   x y"
argument_list|,
name|rtrim
argument_list|(
literal|"   x y "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"   x"
argument_list|,
name|rtrim
argument_list|(
literal|"   x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|rtrim
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link SqlFunctions#ltrim}. */
specifier|public
name|void
name|testLtrim
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|ltrim
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|ltrim
argument_list|(
literal|"    "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x  "
argument_list|,
name|ltrim
argument_list|(
literal|"   x  "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x "
argument_list|,
name|ltrim
argument_list|(
literal|"   x "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x y "
argument_list|,
name|ltrim
argument_list|(
literal|"x y "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|ltrim
argument_list|(
literal|"   x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|ltrim
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link SqlFunctions#trim}. */
specifier|public
name|void
name|testTrim
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|trim
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|trim
argument_list|(
literal|"    "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|trim
argument_list|(
literal|"   x  "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|trim
argument_list|(
literal|"   x "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x y"
argument_list|,
name|trim
argument_list|(
literal|"   x y "
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|trim
argument_list|(
literal|"   x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|trim
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnixDateToString
parameter_list|()
block|{
comment|// Verify these using the "date" command. E.g.
comment|// $ date -u --date="@$(expr 10957 \* 86400)"
comment|// Sat Jan  1 00:00:00 UTC 2000
name|assertEquals
argument_list|(
literal|"2000-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|10957
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-02"
argument_list|,
name|unixDateToString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1971-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|365
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1972-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|730
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1972-02-28"
argument_list|,
name|unixDateToString
argument_list|(
literal|788
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1972-02-29"
argument_list|,
name|unixDateToString
argument_list|(
literal|789
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1972-03-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|790
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1969-01-01"
argument_list|,
name|unixDateToString
argument_list|(
operator|-
literal|365
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2000-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|10957
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2000-02-28"
argument_list|,
name|unixDateToString
argument_list|(
literal|11015
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2000-02-29"
argument_list|,
name|unixDateToString
argument_list|(
literal|11016
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2000-03-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|11017
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1945-02-24"
argument_list|,
name|unixDateToString
argument_list|(
operator|-
literal|9077
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testYmdToUnixDate
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ymdToUnixDate
argument_list|(
literal|1970
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|365
argument_list|,
name|ymdToUnixDate
argument_list|(
literal|1971
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|365
argument_list|,
name|ymdToUnixDate
argument_list|(
literal|1969
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11017
argument_list|,
name|ymdToUnixDate
argument_list|(
literal|2000
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|9077
argument_list|,
name|ymdToUnixDate
argument_list|(
literal|1945
argument_list|,
literal|2
argument_list|,
literal|24
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDateToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"1970-01-01"
argument_list|,
name|unixDateToString
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1971-02-03"
argument_list|,
name|unixDateToString
argument_list|(
literal|0
operator|+
literal|365
operator|+
literal|31
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTimeToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"00:00:00"
argument_list|,
name|unixTimeToString
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"23:59:59"
argument_list|,
name|unixTimeToString
argument_list|(
literal|86400000
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTimestampToString
parameter_list|()
block|{
comment|// ISO format would be "1970-01-01T00:00:00" but SQL format is different
name|assertEquals
argument_list|(
literal|"1970-01-01 00:00:00"
argument_list|,
name|unixTimestampToString
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-02-01 23:59:59"
argument_list|,
name|unixTimestampToString
argument_list|(
literal|86400000L
operator|*
literal|32L
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for      * {@link Utilities#compare(java.util.List, java.util.List)}. */
specifier|public
name|void
name|testCompare
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ac
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|abc
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|a
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|ac
argument_list|,
name|ac
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|ac
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|ac
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|ac
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|empty
argument_list|,
name|ac
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|ac
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|ac
argument_list|,
name|abc
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|ac
argument_list|,
name|empty
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Utilities
operator|.
name|compare
argument_list|(
name|empty
argument_list|,
name|empty
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTruncateLong
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|12000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12345L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12000L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12001L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|11999L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|13000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12345L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|12000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12000L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|13000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12001L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|12000L
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|11999L
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTruncateInt
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12345
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12000
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|12001
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
literal|11999
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|13000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12345
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12000
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|13000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|12001
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|truncate
argument_list|(
operator|-
literal|11999
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|round
argument_list|(
literal|12345
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13000
argument_list|,
name|SqlFunctions
operator|.
name|round
argument_list|(
literal|12845
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|12000
argument_list|,
name|SqlFunctions
operator|.
name|round
argument_list|(
operator|-
literal|12345
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|13000
argument_list|,
name|SqlFunctions
operator|.
name|round
argument_list|(
operator|-
literal|12845
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlFunctionsTest.java
end_comment

end_unit

