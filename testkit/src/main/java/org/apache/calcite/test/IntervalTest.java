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

begin_comment
comment|/** Test cases for intervals.  *  *<p>Called, with varying implementations of {@link Fixture},  * from both parser and validator test.  */
end_comment

begin_class
specifier|public
class|class
name|IntervalTest
block|{
specifier|private
specifier|final
name|Fixture
name|f
decl_stmt|;
specifier|public
name|IntervalTest
parameter_list|(
name|Fixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|f
operator|=
name|fixture
expr_stmt|;
block|}
comment|/** Runs all tests. */
specifier|public
name|void
name|testAll
parameter_list|()
block|{
comment|// Tests that should pass both parser and validator
name|subTestIntervalYearPositive
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalSecondPositive
argument_list|()
expr_stmt|;
comment|// Tests that should pass parser but fail validator
name|subTestIntervalYearNegative
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthNegative
argument_list|()
expr_stmt|;
name|subTestIntervalMonthNegative
argument_list|()
expr_stmt|;
name|subTestIntervalDayNegative
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourNegative
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinuteNegative
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondNegative
argument_list|()
expr_stmt|;
name|subTestIntervalHourNegative
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinuteNegative
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondNegative
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteNegative
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondNegative
argument_list|()
expr_stmt|;
name|subTestIntervalSecondNegative
argument_list|()
expr_stmt|;
name|subTestMisc
argument_list|()
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalYearPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' YEAR(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' YEAR(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' YEAR(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' YEAR(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' YEAR(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' YEAR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' YEAR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' YEAR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' YEAR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR TO MONTH that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalYearToMonthPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99-11' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99-0' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1-2' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99-11' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99-0' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647-11' YEAR(10) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(10) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0-0' YEAR(1) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(1) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2006-2' YEAR(4) TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(4) TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MONTH that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMonthPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' MONTH(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' MONTH(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' MONTH(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' MONTH(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' MONTH(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' MONTH"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' DAY(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' DAY(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' DAY(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' DAY(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' DAY(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' DAY"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' DAY"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' DAY"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|subTestIntervalDayToHourPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2' DAY(2) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23' DAY(2) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0' DAY(2) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647 23' DAY(10) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(10) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0' DAY(1) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(1) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345 2' DAY(4) TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1 2' DAY TO HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1 2' DAY TO HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1 2' DAY TO HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1 2' DAY TO HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO MINUTE that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayToMinutePositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2:3' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647 23:59' DAY(10) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(10) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0:0' DAY(1) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(1) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345 6:7' DAY(4) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1 2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO SECOND that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59:59' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0:0' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59:59' DAY(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0:0' DAY(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647 23:59:59' DAY(10) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(10) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647 23:59:59.999999999' DAY(10) TO SECOND(9)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(10) TO SECOND(9) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(1) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(1) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0.0' DAY(1) TO SECOND(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(1) TO SECOND(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345 6:7:8' DAY(4) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345 6:7:8.9012' DAY(4) TO SECOND(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) TO SECOND(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1 2:3:4' DAY TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' HOUR(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' HOUR(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' HOUR(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' HOUR(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' HOUR"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO MINUTE that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourToMinutePositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '23:59' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:3' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '23:59' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647:59' HOUR(10) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(10) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0' HOUR(1) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(1) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345:7' HOUR(4) TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(4) TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO SECOND that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '23:59:59' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0:0' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '23:59:59.999999' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:3:4' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:59:59' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0:0' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:59:59.999999' HOUR TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647:59:59' HOUR(10) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(10) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647:59:59.999999999' HOUR(10) TO SECOND(9)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(10) TO SECOND(9) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(1) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(1) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0:0.0' HOUR(1) TO SECOND(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(1) TO SECOND(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345:7:8' HOUR(4) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(4) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345:7:8.9012' HOUR(4) TO SECOND(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR(4) TO SECOND(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+2:3:4' HOUR TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMinutePositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' MINUTE(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' MINUTE(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' MINUTE(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' MINUTE(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' MINUTE(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' MINUTE"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE TO SECOND that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '59:59' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '59:59.999999' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0.0' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2:4' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:59' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(2) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:59.999999' MINUTE TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99:0.0' MINUTE TO SECOND(6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND(6) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647:59' MINUTE(10) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(10) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647:59.999999999' MINUTE(10) TO SECOND(9)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(10) TO SECOND(9) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0' MINUTE(1) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(1) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0.0' MINUTE(1) TO SECOND(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(1) TO SECOND(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345:8' MINUTE(4) TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(4) TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2345:7.8901' MINUTE(4) TO SECOND(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE(4) TO SECOND(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+3:4' MINUTE TO SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... SECOND that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalSecondPositive
parameter_list|()
block|{
comment|// default precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' SECOND(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' SECOND(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' SECOND(2, 6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(2, 6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '99' SECOND(2, 6)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(2, 6) NOT NULL"
argument_list|)
expr_stmt|;
comment|// max precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647' SECOND(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '2147483647.999999999' SECOND(10, 9)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(10, 9) NOT NULL"
argument_list|)
expr_stmt|;
comment|// min precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' SECOND(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0.0' SECOND(1, 1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(1, 1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234' SECOND(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1234.56789' SECOND(4, 5)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND(4, 5) NOT NULL"
argument_list|)
expr_stmt|;
comment|// sign
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '+1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '-1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'1' SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'+1' SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '+1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL +'-1' SECOND"
argument_list|)
operator|.
name|assertParse
argument_list|(
literal|"INTERVAL '-1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'+1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL -'-1' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalYearNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' YEAR(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL YEAR\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL YEAR.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' YEAR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of YEAR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' YEAR(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of YEAR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' YEAR(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of YEAR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' YEAR(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of YEAR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' YEAR(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"YEAR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' YEAR(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"YEAR\\(10\\) field"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^YEAR(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL YEAR\\(11\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^YEAR(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL YEAR\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR TO MONTH that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalYearToMonthNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for "
operator|+
literal|"INTERVAL YEAR\\(2\\) TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1-2' for "
operator|+
literal|"INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1--2' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1--2' for "
operator|+
literal|"INTERVAL YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100-0' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of YEAR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100-0' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of YEAR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000-0' YEAR(3) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of YEAR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000-0' YEAR(3) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of YEAR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648-0' YEAR(10) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of YEAR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648-0' YEAR(10) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of YEAR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-12' YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-12' for INTERVAL YEAR TO MONTH.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1-1' ^YEAR(11) TO MONTH^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL YEAR\\(11\\) TO MONTH"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0-0' ^YEAR(0) TO MONTH^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL YEAR\\(0\\) TO MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MONTH that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalMonthNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' MONTH(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL MONTH\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL MONTH.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MONTH\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' MONTH(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MONTH\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' MONTH(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of MONTH\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' MONTH(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of MONTH\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' MONTH(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of MONTH\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' MONTH(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of MONTH\\(10\\) field.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^MONTH(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL MONTH\\(11\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^MONTH(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL MONTH\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalDayNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' DAY(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL DAY\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL DAY.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' DAY"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' DAY(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' DAY(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' DAY(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' DAY(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' DAY(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^DAY(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL DAY\\(11\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^DAY(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL DAY\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO HOUR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalDayToHourNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 x' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 x' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ' ' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ' ' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' DAY(2) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1 1' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1 1' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 -1' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 -1' for INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 24' DAY TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 24' for INTERVAL DAY TO HOUR.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 1' ^DAY(11) TO HOUR^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL DAY\\(11\\) TO HOUR"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0' ^DAY(0) TO HOUR^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for INTERVAL DAY\\(0\\) TO HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO MINUTE that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalDayToMinuteNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ' :' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ' :' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'x 1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'x 1:1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 x:1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 x:1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:x' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:x' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2:3' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:1.2' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:1.2' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2:3' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1 1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1 1:1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 -1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 -1:1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:-1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:-1' for INTERVAL DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0:0' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0:0' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of DAY\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000 0:0' DAY(3) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000 0:0' DAY(3) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of DAY\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648 0:0' DAY(10) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648 0:0' DAY(10) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 24:1' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 24:1' for "
operator|+
literal|"INTERVAL DAY TO MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:60' DAY TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:60' for INTERVAL DAY TO MINUTE.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 1:1' ^DAY(11) TO MINUTE^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL DAY\\(11\\) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0' ^DAY(0) TO MINUTE^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL DAY\\(0\\) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO SECOND that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalDayToSecondNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ' ::' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ' ::' for INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ' ::.' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ' ::\\.' for INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.2' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2:x' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2:x' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2:3' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2:3' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.2' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.2' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' DAY(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2345 6:7:8901' DAY TO SECOND(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '2345 6:7:8901' for "
operator|+
literal|"INTERVAL DAY TO SECOND\\(4\\)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1 1:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1 1:1:1' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 -1:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 -1:1:1' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:-1:1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:-1:1' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:-1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:-1' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:1.-1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:1.-1' for "
operator|+
literal|"INTERVAL DAY TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '100 0' for "
operator|+
literal|"INTERVAL DAY TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '100 0' for "
operator|+
literal|"INTERVAL DAY\\(2\\) TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1000 0' for "
operator|+
literal|"INTERVAL DAY\\(3\\) TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-1000 0' for "
operator|+
literal|"INTERVAL DAY\\(3\\) TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648 1:1:0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648 1:1:0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"DAY\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '2147483648 0' for "
operator|+
literal|"INTERVAL DAY\\(10\\) TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-2147483648 0' for "
operator|+
literal|"INTERVAL DAY\\(10\\) TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 24:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 24:1:1' for "
operator|+
literal|"INTERVAL DAY TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:60:1' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:60:1' for "
operator|+
literal|"INTERVAL DAY TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:60' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:60' for "
operator|+
literal|"INTERVAL DAY TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:1.0000001' DAY TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:1\\.0000001' for "
operator|+
literal|"INTERVAL DAY TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:1:1.0001' DAY TO SECOND(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:1:1\\.0001' for "
operator|+
literal|"INTERVAL DAY TO SECOND\\(3\\).*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 1' ^DAY(11) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL DAY\\(11\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1 1' ^DAY TO SECOND(10)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '10' out of range for "
operator|+
literal|"INTERVAL DAY TO SECOND\\(10\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' ^DAY(0) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL DAY\\(0\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' ^DAY TO SECOND(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '0' out of range for "
operator|+
literal|"INTERVAL DAY TO SECOND\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalHourNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' HOUR(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL HOUR\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL HOUR.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of "
operator|+
literal|"HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' HOUR(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of "
operator|+
literal|"HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' HOUR(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of "
operator|+
literal|"HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' HOUR(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of "
operator|+
literal|"HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' HOUR(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' HOUR(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^HOUR(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL HOUR\\(11\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^HOUR(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL HOUR\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO MINUTE that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalHourToMinuteNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ':' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ':' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:x' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2:3' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for "
operator|+
literal|"INTERVAL HOUR\\(2\\) TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for "
operator|+
literal|"INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1:1' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1:1' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:-1' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:-1' for INTERVAL HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000:0' HOUR(3) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000:0' HOUR(3) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:60' HOUR TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:60' for INTERVAL HOUR TO MINUTE.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1:1' ^HOUR(11) TO MINUTE^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL HOUR\\(11\\) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0' ^HOUR(0) TO MINUTE^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL HOUR\\(0\\) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO SECOND that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalHourToSecondNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '::' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '::' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '::.' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '::\\.' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.2' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2:x' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2:x' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:x:3' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:x:3' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.x' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.x' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for INTERVAL HOUR\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1' for INTERVAL HOUR\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '6:7:8901' HOUR TO SECOND(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '6:7:8901' for INTERVAL HOUR TO SECOND\\(4\\)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1:1:1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1:1:1' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:-1:1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:-1:1' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:-1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:-1' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.-1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.-1' for INTERVAL HOUR TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0:0' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of "
operator|+
literal|"HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0:0' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of "
operator|+
literal|"HOUR\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000:0:0' HOUR(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of "
operator|+
literal|"HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000:0:0' HOUR(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of "
operator|+
literal|"HOUR\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of "
operator|+
literal|"HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of "
operator|+
literal|"HOUR\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:60:1' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:60:1' for "
operator|+
literal|"INTERVAL HOUR TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:60' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:60' for "
operator|+
literal|"INTERVAL HOUR TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.0000001' HOUR TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.0000001' for "
operator|+
literal|"INTERVAL HOUR TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.0001' HOUR TO SECOND(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.0001' for "
operator|+
literal|"INTERVAL HOUR TO SECOND\\(3\\).*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1:1:1' ^HOUR(11) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL HOUR\\(11\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1:1:1' ^HOUR TO SECOND(10)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '10' out of range for "
operator|+
literal|"INTERVAL HOUR TO SECOND\\(10\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' ^HOUR(0) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL HOUR\\(0\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' ^HOUR TO SECOND(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '0' out of range for "
operator|+
literal|"INTERVAL HOUR TO SECOND\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '-' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.2' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' MINUTE(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL MINUTE\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL MINUTE.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MINUTE\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' MINUTE(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MINUTE\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' MINUTE(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of MINUTE\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' MINUTE(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of MINUTE\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' MINUTE(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of MINUTE\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' MINUTE(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of MINUTE\\(10\\) field.*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^MINUTE(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for "
operator|+
literal|"INTERVAL MINUTE\\(11\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^MINUTE(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for "
operator|+
literal|"INTERVAL MINUTE\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE TO SECOND that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteToSecondNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ':' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ':' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ':.' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ':\\.' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.2' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.2' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:x' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:x' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'x:3' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'x:3' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1.x' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1\\.x' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1:2' for INTERVAL MINUTE\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 1' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 1' for INTERVAL MINUTE\\(2\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '7:8901' MINUTE TO SECOND(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '7:8901' for INTERVAL MINUTE TO SECOND\\(4\\)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1:1' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1:1' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:-1' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:-1' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1.-1' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1.-1' for INTERVAL MINUTE TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MINUTE\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100:0' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of MINUTE\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000:0' MINUTE(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of MINUTE\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000:0' MINUTE(3) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of MINUTE\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of MINUTE\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of MINUTE\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:60' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:60' for"
operator|+
literal|" INTERVAL MINUTE TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1.0000001' MINUTE TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1\\.0000001' for"
operator|+
literal|" INTERVAL MINUTE TO SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:1:1.0001' MINUTE TO SECOND(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:1:1\\.0001' for"
operator|+
literal|" INTERVAL MINUTE TO SECOND\\(3\\).*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1:1' ^MINUTE(11) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for"
operator|+
literal|" INTERVAL MINUTE\\(11\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1:1' ^MINUTE TO SECOND(10)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '10' out of range for"
operator|+
literal|" INTERVAL MINUTE TO SECOND\\(10\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0' ^MINUTE(0) TO SECOND^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for"
operator|+
literal|" INTERVAL MINUTE\\(0\\) TO SECOND"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0:0' ^MINUTE TO SECOND(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '0' out of range for"
operator|+
literal|" INTERVAL MINUTE TO SECOND\\(0\\)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... SECOND that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlParserTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXNegative() tests.    */
specifier|public
name|void
name|subTestIntervalSecondNegative
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL ':' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format ':' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '.' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '\\.' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.x' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.x' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'x.1' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'x\\.1' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1 2' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1 2' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1:2' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1:2' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1-2' SECOND(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1-2' for INTERVAL SECOND\\(2\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL 'bogus text' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format 'bogus text' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
comment|// negative field values
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '--1' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '--1' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.-1' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.-1' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of SECOND\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '100' SECOND(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 100 exceeds precision of SECOND\\(2\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1000' SECOND(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 1,000 exceeds precision of SECOND\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-1000' SECOND(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -1,000 exceeds precision of SECOND\\(3\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '2147483648' SECOND(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value 2,147,483,648 exceeds precision of SECOND\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '-2147483648' SECOND(10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval field value -2,147,483,648 exceeds precision of SECOND\\(10\\) field.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.0000001' SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.0000001' for INTERVAL SECOND.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.0000001' SECOND(2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.0000001' for INTERVAL SECOND\\(2\\).*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.0001' SECOND(2, 3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.0001' for INTERVAL SECOND\\(2, 3\\).*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.0000000001' SECOND(2, 9)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1\\.0000000001' for"
operator|+
literal|" INTERVAL SECOND\\(2, 9\\).*"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1' ^SECOND(11)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '11' out of range for"
operator|+
literal|" INTERVAL SECOND\\(11\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1.1' ^SECOND(1, 10)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '10' out of range for"
operator|+
literal|" INTERVAL SECOND\\(1, 10\\)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^SECOND(0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval leading field precision '0' out of range for"
operator|+
literal|" INTERVAL SECOND\\(0\\)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0' ^SECOND(1, 0)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Interval fractional second precision '0' out of range for"
operator|+
literal|" INTERVAL SECOND\\(1, 0\\)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|subTestMisc
parameter_list|()
block|{
comment|// Miscellaneous
comment|// fractional value is not OK, even if it is 0
name|f
operator|.
name|wholeExpr
argument_list|(
literal|"INTERVAL '1.0' HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal interval literal format '1.0' for INTERVAL HOUR"
argument_list|)
expr_stmt|;
comment|// only seconds are allowed to have a fractional part
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '1.0' SECOND"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// leading zeros do not cause precision to be exceeded
name|f
operator|.
name|expr
argument_list|(
literal|"INTERVAL '0999' MONTH(3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Fluent interface for binding an expression to create a fixture that can    * be used to validate, check AST, or check type. */
specifier|public
interface|interface
name|Fixture
block|{
name|Fixture2
name|expr
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
name|Fixture2
name|wholeExpr
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
block|}
comment|/** Fluent interface to validate an expression. */
specifier|public
interface|interface
name|Fixture2
block|{
comment|/** Checks that the expression is valid in the parser      * but invalid (with the given error message) in the validator. */
name|void
name|fails
parameter_list|(
name|String
name|expected
parameter_list|)
function_decl|;
comment|/** Checks that the expression is valid in the parser and validator      * and has the given column type. */
name|void
name|columnType
parameter_list|(
name|String
name|expectedType
parameter_list|)
function_decl|;
comment|/** Checks that the expression parses successfully and produces the given      * SQL when unparsed. */
name|Fixture2
name|assertParse
parameter_list|(
name|String
name|expectedAst
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

