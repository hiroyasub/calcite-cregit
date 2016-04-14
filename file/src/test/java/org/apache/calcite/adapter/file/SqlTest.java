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
name|adapter
operator|.
name|file
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
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|PrintStream
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
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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

begin_comment
comment|/**  * System test of the Calcite file adapter, which can also read and parse  * HTML tables over HTTP.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTest
block|{
comment|// helper functions
specifier|private
name|void
name|checkSql
parameter_list|(
name|String
name|model
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
name|sql
argument_list|,
name|model
argument_list|,
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
name|output
argument_list|(
name|resultSet
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSql
parameter_list|(
name|String
name|model
parameter_list|,
name|String
name|sql
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
name|sql
argument_list|,
name|model
argument_list|,
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
name|String
name|actual
init|=
name|SqlTest
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Assertion failure:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\tExpected: '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\tActual: '"
operator|+
name|actual
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSql
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|model
parameter_list|,
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|fn
parameter_list|)
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Statement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
literal|"target/test-classes/"
operator|+
name|model
operator|+
literal|".json"
argument_list|)
expr_stmt|;
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|fn
operator|.
name|apply
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|connection
argument_list|,
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|int
name|n
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnLabel
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|"; "
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|output
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|PrintStream
name|out
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ResultSetMetaData
name|metaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|columnCount
init|=
name|metaData
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|columnCount
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|close
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Statement
name|statement
parameter_list|)
block|{
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|// tests
comment|/**    * Reads from a local file and checks the result    */
annotation|@
name|Test
specifier|public
name|void
name|testFileSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"testModel"
argument_list|,
literal|"select H1 from T1 where H0 = 'R1C0'"
argument_list|,
literal|"H1=R1C1\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads from a local file without table headers<TH> and checks the result    */
annotation|@
name|Test
specifier|public
name|void
name|testNoTHSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FileSuite
operator|.
name|hazNetwork
argument_list|()
argument_list|)
expr_stmt|;
name|checkSql
argument_list|(
literal|"testModel"
argument_list|,
literal|"select \"col1\" from T1_NO_TH where \"col0\" like 'R0%'"
argument_list|,
literal|"col1=R0C1\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads from a local file - finds larger table even without<TH> elements    */
annotation|@
name|Test
specifier|public
name|void
name|testFindBiggerNoTH
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"testModel"
argument_list|,
literal|"select \"col4\" from TABLEX2 where \"col0\" like 'R1%'"
argument_list|,
literal|"col4=R1C4\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads from a URL and checks the result    */
annotation|@
name|Test
specifier|public
name|void
name|testURLSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FileSuite
operator|.
name|hazNetwork
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"State\", \"Statehood\" from \"States\"\n"
operator|+
literal|"where \"State\" = 'California'"
decl_stmt|;
name|checkSql
argument_list|(
literal|"wiki"
argument_list|,
name|sql
argument_list|,
literal|"State=California; Statehood=1850-09-09\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTest.java
end_comment

end_unit

