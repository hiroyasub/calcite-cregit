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
name|rel
operator|.
name|rules
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
name|avatica
operator|.
name|util
operator|.
name|TimeUnitRange
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
name|rex
operator|.
name|RexNode
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|test
operator|.
name|RexImplicationCheckerTest
operator|.
name|Fixture
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
name|Ordering
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
name|RangeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|Calendar
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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

begin_comment
comment|/** Unit tests for {@link DateRangeRules} algorithms. */
end_comment

begin_class
specifier|public
class|class
name|DateRangeRulesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearFromDateColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|e
init|=
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|,
name|f
operator|.
name|exYear
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|DateRangeRules
operator|.
name|extractTimeUnits
argument_list|(
name|e
argument_list|)
argument_list|,
name|is
argument_list|(
name|set
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateRangeRules
operator|.
name|extractTimeUnits
argument_list|(
name|f
operator|.
name|dec
argument_list|)
argument_list|,
name|is
argument_list|(
name|set
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateRangeRules
operator|.
name|extractTimeUnits
argument_list|(
name|f
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
name|set
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// extract YEAR from a DATE column
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|e
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2014-01-01),<($9, 2015-01-01))"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2014-01-01),<($9, 2015-01-01))"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|ge
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|">=($9, 2014-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|gt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|">=($9, 2015-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|lt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<($9, 2014-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|le
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<($9, 2015-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|ne
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<>(EXTRACT(FLAG(YEAR), $9), 2014)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearFromTimestampColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2014-01-01),<($9, 2015-01-01))"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|ge
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|">=($9, 2014-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|gt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|">=($9, 2015-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|lt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<($9, 2014-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|le
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<($9, 2015-01-01)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|ne
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"<>(EXTRACT(FLAG(YEAR), $9), 2014)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearAndMonthFromDateColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2014
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(AND(>=($9, 2014-01-01),<($9, 2015-01-01)),"
operator|+
literal|" AND(>=($9, 2014-06-01),<($9, 2014-07-01)))"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2014-01-01),<($9, 2015-01-01),"
operator|+
literal|">=($9, 2014-06-01),<($9, 2014-07-01))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1601">[CALCITE-1601]    * DateRangeRules loses OR filters</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearAndMonthFromDateColumn2
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
specifier|final
name|String
name|s1
init|=
literal|"AND("
operator|+
literal|"AND(>=($9, 2000-01-01),<($9, 2001-01-01)),"
operator|+
literal|" OR("
operator|+
literal|"AND(>=($9, 2000-02-01),<($9, 2000-03-01)), "
operator|+
literal|"AND(>=($9, 2000-03-01),<($9, 2000-04-01)), "
operator|+
literal|"AND(>=($9, 2000-05-01),<($9, 2000-06-01))))"
decl_stmt|;
specifier|final
name|String
name|s2
init|=
literal|"AND(>=($9, 2000-01-01),<($9, 2001-01-01),"
operator|+
literal|" OR("
operator|+
literal|"AND(>=($9, 2000-02-01),<($9, 2000-03-01)), "
operator|+
literal|"AND(>=($9, 2000-03-01),<($9, 2000-04-01)), "
operator|+
literal|"AND(>=($9, 2000-05-01),<($9, 2000-06-01))))"
decl_stmt|;
specifier|final
name|RexNode
name|e
init|=
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2000
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|or
argument_list|(
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|e
argument_list|,
name|is
argument_list|(
name|s1
argument_list|)
argument_list|,
name|is
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearAndDayFromDateColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2010
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exDay
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|31
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(AND(>=($9, 2010-01-01),<($9, 2011-01-01)),"
operator|+
literal|" OR(AND(>=($9, 2010-01-31),<($9, 2010-02-01)),"
operator|+
literal|" AND(>=($9, 2010-03-31),<($9, 2010-04-01)),"
operator|+
literal|" AND(>=($9, 2010-05-31),<($9, 2010-06-01)),"
operator|+
literal|" AND(>=($9, 2010-07-31),<($9, 2010-08-01)),"
operator|+
literal|" AND(>=($9, 2010-08-31),<($9, 2010-09-01)),"
operator|+
literal|" AND(>=($9, 2010-10-31),<($9, 2010-11-01)),"
operator|+
literal|" AND(>=($9, 2010-12-31),<($9, 2011-01-01))))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearMonthDayFromDateColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
comment|// The following condition finds the 2 leap days between 2010 and 2020,
comment|// namely 29th February 2012 and 2016.
comment|//
comment|// Currently there are redundant conditions, e.g.
comment|// "AND(>=($9, 2011-01-01),<($9, 2020-01-01))". We should remove them by
comment|// folding intervals.
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|gt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2010
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|lt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2020
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exDay
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|29
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2011-01-01),"
operator|+
literal|" AND(>=($9, 2011-01-01),<($9, 2020-01-01)),"
operator|+
literal|" OR(AND(>=($9, 2011-02-01),<($9, 2011-03-01)),"
operator|+
literal|" AND(>=($9, 2012-02-01),<($9, 2012-03-01)),"
operator|+
literal|" AND(>=($9, 2013-02-01),<($9, 2013-03-01)),"
operator|+
literal|" AND(>=($9, 2014-02-01),<($9, 2014-03-01)),"
operator|+
literal|" AND(>=($9, 2015-02-01),<($9, 2015-03-01)),"
operator|+
literal|" AND(>=($9, 2016-02-01),<($9, 2016-03-01)),"
operator|+
literal|" AND(>=($9, 2017-02-01),<($9, 2017-03-01)),"
operator|+
literal|" AND(>=($9, 2018-02-01),<($9, 2018-03-01)),"
operator|+
literal|" AND(>=($9, 2019-02-01),<($9, 2019-03-01))),"
operator|+
literal|" OR(AND(>=($9, 2012-02-29),<($9, 2012-03-01)),"
operator|+
literal|" AND(>=($9, 2016-02-29),<($9, 2016-03-01))))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearMonthDayFromTimestampColumn
parameter_list|()
block|{
specifier|final
name|Fixture2
name|f
init|=
operator|new
name|Fixture2
argument_list|()
decl_stmt|;
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|gt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2010
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|lt
argument_list|(
name|f
operator|.
name|exYear
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2020
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exMonth
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|eq
argument_list|(
name|f
operator|.
name|exDay
argument_list|,
name|f
operator|.
name|literal
argument_list|(
literal|29
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"AND(>=($9, 2011-01-01),"
operator|+
literal|" AND(>=($9, 2011-01-01),<($9, 2020-01-01)),"
operator|+
literal|" OR(AND(>=($9, 2011-02-01),<($9, 2011-03-01)),"
operator|+
literal|" AND(>=($9, 2012-02-01),<($9, 2012-03-01)),"
operator|+
literal|" AND(>=($9, 2013-02-01),<($9, 2013-03-01)),"
operator|+
literal|" AND(>=($9, 2014-02-01),<($9, 2014-03-01)),"
operator|+
literal|" AND(>=($9, 2015-02-01),<($9, 2015-03-01)),"
operator|+
literal|" AND(>=($9, 2016-02-01),<($9, 2016-03-01)),"
operator|+
literal|" AND(>=($9, 2017-02-01),<($9, 2017-03-01)),"
operator|+
literal|" AND(>=($9, 2018-02-01),<($9, 2018-03-01)),"
operator|+
literal|" AND(>=($9, 2019-02-01),<($9, 2019-03-01))),"
operator|+
literal|" OR(AND(>=($9, 2012-02-29),<($9, 2012-03-01)),"
operator|+
literal|" AND(>=($9, 2016-02-29),<($9, 2016-03-01))))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|TimeUnitRange
argument_list|>
name|set
parameter_list|(
name|TimeUnitRange
modifier|...
name|es
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|es
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkDateRange
parameter_list|(
name|Fixture
name|f
parameter_list|,
name|RexNode
name|e
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
name|checkDateRange
argument_list|(
name|f
argument_list|,
name|e
argument_list|,
name|matcher
argument_list|,
name|CoreMatchers
operator|.
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkDateRange
parameter_list|(
name|Fixture
name|f
parameter_list|,
name|RexNode
name|e
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|simplifyMatcher
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
argument_list|>
name|operandRanges
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// We rely on the collection being sorted (so YEAR comes before MONTH
comment|// before HOUR) and unique. A predicate on MONTH is not useful if there is
comment|// no predicate on YEAR. Then when we apply the predicate on DAY it doesn't
comment|// generate hundreds of ranges we'll later throw away.
specifier|final
name|List
argument_list|<
name|TimeUnitRange
argument_list|>
name|timeUnits
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|sortedCopy
argument_list|(
name|DateRangeRules
operator|.
name|extractTimeUnits
argument_list|(
name|e
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|TimeUnitRange
name|timeUnit
range|:
name|timeUnits
control|)
block|{
name|e
operator|=
name|e
operator|.
name|accept
argument_list|(
operator|new
name|DateRangeRules
operator|.
name|ExtractShuttle
argument_list|(
name|f
operator|.
name|rexBuilder
argument_list|,
name|timeUnit
argument_list|,
name|operandRanges
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|e2
init|=
name|f
operator|.
name|simplify
operator|.
name|simplify
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e2
operator|.
name|toString
argument_list|()
argument_list|,
name|simplifyMatcher
argument_list|)
expr_stmt|;
block|}
comment|/** Common expressions across tests. */
specifier|private
specifier|static
class|class
name|Fixture2
extends|extends
name|Fixture
block|{
specifier|private
specifier|final
name|RexNode
name|exYear
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|exMonth
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|exDay
decl_stmt|;
name|Fixture2
parameter_list|()
block|{
name|exYear
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rexBuilder
operator|.
name|makeFlag
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|)
argument_list|,
name|ts
argument_list|)
argument_list|)
expr_stmt|;
name|exMonth
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|intRelDataType
argument_list|,
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rexBuilder
operator|.
name|makeFlag
argument_list|(
name|TimeUnitRange
operator|.
name|MONTH
argument_list|)
argument_list|,
name|ts
argument_list|)
argument_list|)
expr_stmt|;
name|exDay
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|intRelDataType
argument_list|,
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rexBuilder
operator|.
name|makeFlag
argument_list|(
name|TimeUnitRange
operator|.
name|DAY
argument_list|)
argument_list|,
name|ts
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DateRangeRulesTest.java
end_comment

end_unit

