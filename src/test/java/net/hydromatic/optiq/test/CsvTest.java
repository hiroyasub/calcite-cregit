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
name|*
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

begin_comment
comment|/**  * Unit test of the Optiq adapter for CSV.  */
end_comment

begin_class
specifier|public
class|class
name|CsvTest
extends|extends
name|TestCase
block|{
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
comment|/**    * Tests the vanity driver.    */
specifier|public
name|void
name|_testVanityDriver
parameter_list|()
throws|throws
name|SQLException
block|{
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:csv:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests the vanity driver with properties in the URL.    */
specifier|public
name|void
name|_testVanityDriverArgsInUrl
parameter_list|()
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:csv:"
operator|+
literal|"directory='foo'"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Reads from a table.    */
specifier|public
name|void
name|testSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select * from EMPS"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSql
parameter_list|(
name|String
name|sql
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
literal|"target/test-classes/model.json"
argument_list|)
expr_stmt|;
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
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
block|}
end_class

begin_comment
comment|// End CsvTest.java
end_comment

end_unit

