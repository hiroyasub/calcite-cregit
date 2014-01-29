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
comment|/**  * Unit test of the Optiq adapter for Splunk.  */
end_comment

begin_class
specifier|public
class|class
name|SplunkAdapterTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SPLUNK_URL
init|=
literal|"https://localhost:8089"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SPLUNK_USER
init|=
literal|"admin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SPLUNK_PASSWORD
init|=
literal|"changeme"
decl_stmt|;
comment|/** Whether this test is enabled. Tests are disabled unless we know that    * Splunk is present and loaded with the requisite data. */
specifier|private
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|loadDriverClass
parameter_list|()
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.impl.splunk.SplunkDriver"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"driver not found"
argument_list|,
name|e
argument_list|)
throw|;
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
comment|/**    * Tests the vanity driver.    */
annotation|@
name|Test
specifier|public
name|void
name|testVanityDriver
parameter_list|()
throws|throws
name|SQLException
block|{
name|loadDriverClass
argument_list|()
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|setProperty
argument_list|(
literal|"url"
argument_list|,
name|SPLUNK_URL
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
name|SPLUNK_USER
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
name|SPLUNK_PASSWORD
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:splunk:"
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
annotation|@
name|Test
specifier|public
name|void
name|testVanityDriverArgsInUrl
parameter_list|()
throws|throws
name|SQLException
block|{
name|loadDriverClass
argument_list|()
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:splunk:"
operator|+
literal|"url='"
operator|+
name|SPLUNK_URL
operator|+
literal|"'"
operator|+
literal|";user='"
operator|+
name|SPLUNK_USER
operator|+
literal|"'"
operator|+
literal|";password='"
operator|+
name|SPLUNK_PASSWORD
operator|+
literal|"'"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|static
specifier|final
name|String
index|[]
name|SQL_STRINGS
init|=
block|{
literal|"select \"source\", \"sourcetype\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
block|,
literal|"select \"sourcetype\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
block|,
literal|"select distinct \"sourcetype\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
block|,
literal|"select count(\"sourcetype\")\n"
operator|+
literal|"from \"splunk\".\"splunk\""
block|,
comment|// gives wrong answer, not error. currently returns same as count.
literal|"select count(distinct \"sourcetype\")\n"
operator|+
literal|"from \"splunk\".\"splunk\""
block|,
literal|"select \"sourcetype\", count(\"source\")\n"
operator|+
literal|"from \"splunk\".\"splunk\"\n"
operator|+
literal|"group by \"sourcetype\""
block|,
literal|"select \"sourcetype\", count(\"source\") as c\n"
operator|+
literal|"from \"splunk\".\"splunk\"\n"
operator|+
literal|"group by \"sourcetype\"\n"
operator|+
literal|"order by c desc\n"
block|,
comment|// group + order
literal|"select s.\"product_id\", count(\"source\") as c\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"where s.\"sourcetype\" = 'access_combined_wcookie'\n"
operator|+
literal|"group by s.\"product_id\"\n"
operator|+
literal|"order by c desc\n"
block|,
comment|// non-advertised field
literal|"select s.\"sourcetype\", s.\"action\" from \"splunk\".\"splunk\" as s"
block|,
literal|"select s.\"source\", s.\"product_id\", s.\"product_name\", s.\"method\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"where s.\"sourcetype\" = 'access_combined_wcookie'\n"
block|,
literal|"select p.\"product_name\", s.\"action\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"  join \"mysql\".\"products\" as p\n"
operator|+
literal|"on s.\"product_id\" = p.\"product_id\""
block|,
literal|"select s.\"source\", s.\"product_id\", p.\"product_name\", p.\"price\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"    join \"mysql\".\"products\" as p\n"
operator|+
literal|"    on s.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"where s.\"sourcetype\" = 'access_combined_wcookie'\n"
block|,   }
decl_stmt|;
specifier|static
specifier|final
name|String
index|[]
name|ERROR_SQL_STRINGS
init|=
block|{
comment|// gives error in SplunkPushDownRule
literal|"select count(*) from \"splunk\".\"splunk\""
block|,
comment|// gives no rows; suspect off-by-one because no base fields are
comment|// referenced
literal|"select s.\"product_id\", s.\"product_name\", s.\"method\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"where s.\"sourcetype\" = 'access_combined_wcookie'\n"
block|,
comment|// horrible error if you access a field that doesn't exist
literal|"select s.\"sourcetype\", s.\"access\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
block|,   }
decl_stmt|;
comment|// Fields:
comment|// sourcetype=access_*
comment|// action (purchase | update)
comment|// method (GET | POST)
comment|/**    * Reads from a table.    */
annotation|@
name|Test
specifier|public
name|void
name|testSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select \"source\", \"sourcetype\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select distinct \"sourcetype\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSql
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select p.\"product_name\", /*s.\"product_id\",*/ s.\"action\"\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"join \"mysql\".\"products\" as p\n"
operator|+
literal|"on s.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"where s.\"action\" = 'PURCHASE'"
argument_list|)
expr_stmt|;
comment|/*             "select s.\"eventtype\", count(\"source\") as c\n"             + "from \"splunk\".\"splunk\" as s\n"             + "group by s.\"eventtype\"\n"             + "order by c desc\n"; */
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
name|loadDriverClass
argument_list|()
expr_stmt|;
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
literal|"url"
argument_list|,
name|SPLUNK_URL
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
name|SPLUNK_USER
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
name|SPLUNK_PASSWORD
argument_list|)
expr_stmt|;
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:splunk:"
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
if|if
condition|(
operator|!
name|enabled
argument_list|()
condition|)
block|{
return|return;
block|}
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
comment|// End SplunkAdapterTest.java
end_comment

end_unit

