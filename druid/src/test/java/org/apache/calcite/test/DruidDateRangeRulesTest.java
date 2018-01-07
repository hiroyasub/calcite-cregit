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
name|test
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
name|adapter
operator|.
name|druid
operator|.
name|DruidDateTimeUtils
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
name|rel
operator|.
name|rules
operator|.
name|DateRangeRules
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|TimestampString
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
name|Util
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
name|joda
operator|.
name|time
operator|.
name|Interval
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
name|List
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
name|hamcrest
operator|.
name|core
operator|.
name|IsNull
operator|.
name|notNullValue
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
name|DruidDateRangeRulesTest
block|{
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
comment|// AND(>=($8, 2014-01-01),<($8, 2015-01-01),>=($8, 2014-06-01),<($8, 2014-07-01))
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
literal|"[2014-06-01T00:00:00.000Z/2014-07-01T00:00:00.000Z]"
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
comment|// AND(AND(>=($8, 2010-01-01),<($8, 2011-01-01)),
comment|//     OR(AND(>=($8, 2010-01-31),<($8, 2010-02-01)),
comment|//        AND(>=($8, 2010-03-31),<($8, 2010-04-01)),
comment|//        AND(>=($8, 2010-05-31),<($8, 2010-06-01)),
comment|//        AND(>=($8, 2010-07-31),<($8, 2010-08-01)),
comment|//        AND(>=($8, 2010-08-31),<($8, 2010-09-01)),
comment|//        AND(>=($8, 2010-10-31),<($8, 2010-11-01)),
comment|//        AND(>=($8, 2010-12-31),<($8, 2011-01-01))))
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
literal|"[2010-01-31T00:00:00.000Z/2010-02-01T00:00:00.000Z, "
operator|+
literal|"2010-03-31T00:00:00.000Z/2010-04-01T00:00:00.000Z, "
operator|+
literal|"2010-05-31T00:00:00.000Z/2010-06-01T00:00:00.000Z, "
operator|+
literal|"2010-07-31T00:00:00.000Z/2010-08-01T00:00:00.000Z, "
operator|+
literal|"2010-08-31T00:00:00.000Z/2010-09-01T00:00:00.000Z, "
operator|+
literal|"2010-10-31T00:00:00.000Z/2010-11-01T00:00:00.000Z, "
operator|+
literal|"2010-12-31T00:00:00.000Z/2011-01-01T00:00:00.000Z]"
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
comment|// AND(>=($8, 2011-01-01),"
comment|//     AND(>=($8, 2011-01-01),<($8, 2020-01-01)),
comment|//     OR(AND(>=($8, 2011-02-01),<($8, 2011-03-01)),
comment|//        AND(>=($8, 2012-02-01),<($8, 2012-03-01)),
comment|//        AND(>=($8, 2013-02-01),<($8, 2013-03-01)),
comment|//        AND(>=($8, 2014-02-01),<($8, 2014-03-01)),
comment|//        AND(>=($8, 2015-02-01),<($8, 2015-03-01)),
comment|//        AND(>=($8, 2016-02-01),<($8, 2016-03-01)),
comment|//        AND(>=($8, 2017-02-01),<($8, 2017-03-01)),
comment|//        AND(>=($8, 2018-02-01),<($8, 2018-03-01)),
comment|//        AND(>=($8, 2019-02-01),<($8, 2019-03-01))),
comment|//     OR(AND(>=($8, 2012-02-29),<($8, 2012-03-01)),
comment|//        AND(>=($8, 2016-02-29),<($8, 2016-03-01))))
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
literal|"[2012-02-29T00:00:00.000Z/2012-03-01T00:00:00.000Z, "
operator|+
literal|"2016-02-29T00:00:00.000Z/2016-03-01T00:00:00.000Z]"
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
comment|// AND(>=($9, 2011-01-01),
comment|//     AND(>=($9, 2011-01-01),<($9, 2020-01-01)),
comment|//     OR(AND(>=($9, 2011-02-01),<($9, 2011-03-01)),
comment|//        AND(>=($9, 2012-02-01),<($9, 2012-03-01)),
comment|//        AND(>=($9, 2013-02-01),<($9, 2013-03-01)),
comment|//        AND(>=($9, 2014-02-01),<($9, 2014-03-01)),
comment|//        AND(>=($9, 2015-02-01),<($9, 2015-03-01)),
comment|//        AND(>=($9, 2016-02-01),<($9, 2016-03-01)),
comment|//        AND(>=($9, 2017-02-01),<($9, 2017-03-01)),
comment|//        AND(>=($9, 2018-02-01),<($9, 2018-03-01)),
comment|//        AND(>=($9, 2019-02-01),<($9, 2019-03-01))),
comment|//     OR(AND(>=($9, 2012-02-29),<($9, 2012-03-01)),"
comment|//        AND(>=($9, 2016-02-29),<($9, 2016-03-01))))
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
literal|"[2012-02-29T00:00:00.000Z/2012-03-01T00:00:00.000Z, "
operator|+
literal|"2016-02-29T00:00:00.000Z/2016-03-01T00:00:00.000Z]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1738">[CALCITE-1738]    * Push CAST of literals to Druid</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterWithCast
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
name|Calendar
name|c
init|=
name|Util
operator|.
name|calendar
argument_list|()
decl_stmt|;
name|c
operator|.
name|clear
argument_list|()
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
literal|2010
argument_list|,
name|Calendar
operator|.
name|JANUARY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|TimestampString
name|from
init|=
name|TimestampString
operator|.
name|fromCalendarFields
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|c
operator|.
name|clear
argument_list|()
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
literal|2011
argument_list|,
name|Calendar
operator|.
name|JANUARY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|TimestampString
name|to
init|=
name|TimestampString
operator|.
name|fromCalendarFields
argument_list|(
name|c
argument_list|)
decl_stmt|;
comment|// d>= 2010-01-01 AND d< 2011-01-01
name|checkDateRangeNoSimplify
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|and
argument_list|(
name|f
operator|.
name|ge
argument_list|(
name|f
operator|.
name|d
argument_list|,
name|f
operator|.
name|cast
argument_list|(
name|f
operator|.
name|timestampDataType
argument_list|,
name|f
operator|.
name|timestampLiteral
argument_list|(
name|from
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|f
operator|.
name|lt
argument_list|(
name|f
operator|.
name|d
argument_list|,
name|f
operator|.
name|cast
argument_list|(
name|f
operator|.
name|timestampDataType
argument_list|,
name|f
operator|.
name|timestampLiteral
argument_list|(
name|to
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"[2010-01-01T00:00:00.000Z/2011-01-01T00:00:00.000Z]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// For testFilterWithCast we need to no simplify the expression, which would
comment|// remove the CAST, in order to match the way expressions are presented when
comment|// HiveRexExecutorImpl is used in Hive
specifier|private
name|void
name|checkDateRangeNoSimplify
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
name|intervalMatcher
parameter_list|)
block|{
name|e
operator|=
name|DateRangeRules
operator|.
name|replaceTimeUnits
argument_list|(
name|f
operator|.
name|rexBuilder
argument_list|,
name|e
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Interval
argument_list|>
name|intervals
init|=
name|DruidDateTimeUtils
operator|.
name|createInterval
argument_list|(
name|e
argument_list|,
literal|"UTC"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|intervals
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|intervals
operator|.
name|toString
argument_list|()
argument_list|,
name|intervalMatcher
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
name|intervalMatcher
parameter_list|)
block|{
name|e
operator|=
name|DateRangeRules
operator|.
name|replaceTimeUnits
argument_list|(
name|f
operator|.
name|rexBuilder
argument_list|,
name|e
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
name|List
argument_list|<
name|Interval
argument_list|>
name|intervals
init|=
name|DruidDateTimeUtils
operator|.
name|createInterval
argument_list|(
name|e2
argument_list|,
literal|"UTC"
argument_list|)
decl_stmt|;
if|if
condition|(
name|intervals
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"null interval"
argument_list|)
throw|;
block|}
name|assertThat
argument_list|(
name|intervals
operator|.
name|toString
argument_list|()
argument_list|,
name|intervalMatcher
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
comment|// End DruidDateRangeRulesTest.java
end_comment

end_unit

