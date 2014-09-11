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
name|tools
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
name|Util
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
name|Assert
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
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
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
name|containsString
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link SqlRun}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlRunTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"select count(*) as c1 from \"foodmart\".\"days\";\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!set outputformat mysql\n"
operator|+
literal|"select count(*) as c1 from \"foodmart\".\"days\";\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!plan\n"
operator|+
literal|"\n"
argument_list|,
literal|"!use foodmart\n"
operator|+
literal|"select count(*) as c1 from \"foodmart\".\"days\";\n"
operator|+
literal|"C1\n"
operator|+
literal|"7\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!set outputformat mysql\n"
operator|+
literal|"select count(*) as c1 from \"foodmart\".\"days\";\n"
operator|+
literal|"+----+\n"
operator|+
literal|"| C1 |\n"
operator|+
literal|"+----+\n"
operator|+
literal|"| 7  |\n"
operator|+
literal|"+----+\n"
operator|+
literal|"(1 row)\n"
operator|+
literal|"\n"
operator|+
literal|"!ok\n"
operator|+
literal|"JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcAggregateRel(group=[{}], C1=[COUNT()])\n"
operator|+
literal|"    JdbcProjectRel(DUMMY=[0])\n"
operator|+
literal|"      JdbcTableScan(table=[[foodmart, days]])\n"
operator|+
literal|"!plan\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testError
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"select blah from blah;\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"select blah from blah;\n"
operator|+
literal|"java.sql.SQLException: error while executing SQL \"select blah from blah\n"
operator|+
literal|"\": From line 1, column 18 to line 1, column 21: Table 'BLAH' not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPlan
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"!plan\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EnumerableValuesRel(tuples=[[{ 1 }, { 2 }]])\n"
operator|+
literal|"!plan\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPlanAfterOk
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!plan\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EXPR$0\n"
operator|+
literal|"1\n"
operator|+
literal|"2\n"
operator|+
literal|"!ok\n"
operator|+
literal|"EnumerableValuesRel(tuples=[[{ 1 }, { 2 }]])\n"
operator|+
literal|"!plan\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** It is OK to have consecutive '!plan' calls and no '!ok'.    * (Previously there was a "result already open" error.) */
annotation|@
name|Test
specifier|public
name|void
name|testPlanPlan
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"!plan\n"
operator|+
literal|"values (3), (4);\n"
operator|+
literal|"!plan\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EnumerableValuesRel(tuples=[[{ 1 }, { 2 }]])\n"
operator|+
literal|"!plan\n"
operator|+
literal|"values (3), (4);\n"
operator|+
literal|"EnumerableValuesRel(tuples=[[{ 3 }, { 4 }]])\n"
operator|+
literal|"!plan\n"
operator|+
literal|"EXPR$0\n"
operator|+
literal|"3\n"
operator|+
literal|"4\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Content inside a '!ok' command, that needs to be matched. */
annotation|@
name|Test
specifier|public
name|void
name|testOkContent
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"baz\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EXPR$0\n"
operator|+
literal|"1\n"
operator|+
literal|"2\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Content inside a '!plan' command, that needs to be matched. */
annotation|@
name|Test
specifier|public
name|void
name|testPlanContent
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"foo\n"
operator|+
literal|"!plan\n"
operator|+
literal|"baz\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EnumerableValuesRel(tuples=[[{ 1 }, { 2 }]])\n"
operator|+
literal|"!plan\n"
operator|+
literal|"EXPR$0\n"
operator|+
literal|"1\n"
operator|+
literal|"2\n"
operator|+
literal|"!ok\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIfFalse
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"!if (false) {\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"anything\n"
operator|+
literal|"you like\n"
operator|+
literal|"!plan\n"
operator|+
literal|"!}\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"!if (false) {\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"anything\n"
operator|+
literal|"you like\n"
operator|+
literal|"!plan\n"
operator|+
literal|"!}\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIfTrue
parameter_list|()
block|{
name|check
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"!if (true) {\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"anything\n"
operator|+
literal|"you like\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!}\n"
operator|+
literal|"\n"
argument_list|,
name|containsString
argument_list|(
literal|"!use foodmart\n"
operator|+
literal|"!if (true) {\n"
operator|+
literal|"values (1), (2);\n"
operator|+
literal|"EXPR$0\n"
operator|+
literal|"1\n"
operator|+
literal|"2\n"
operator|+
literal|"!ok\n"
operator|+
literal|"!}\n"
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|check
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|check
argument_list|(
name|input
argument_list|,
name|CoreMatchers
operator|.
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|check
parameter_list|(
name|String
name|input
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|SqlRun
name|run
init|=
operator|new
name|SqlRun
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|run
operator|.
name|execute
argument_list|(
operator|new
name|SqlRun
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"foodmart"
argument_list|)
condition|)
block|{
return|return
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|connect
argument_list|()
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown connection '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|Util
operator|.
name|toLinux
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
name|out
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlRunTest.java
end_comment

end_unit

