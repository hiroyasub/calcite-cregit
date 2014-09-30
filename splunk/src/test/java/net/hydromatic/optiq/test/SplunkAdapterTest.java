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
name|test
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
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|*
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
name|hamcrest
operator|.
name|CoreMatchers
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
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test of the Calcite adapter for Splunk.  */
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
comment|/** Whether to run Splunk tests. Disabled by default, because we do not expect    * Splunk to be installed and populated data set. To enable,    * specify {@code -Dcalcite.test.splunk=true} on the Java command line. */
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"calcite.test.splunk"
argument_list|)
decl_stmt|;
comment|/** Whether this test is enabled. Tests are disabled unless we know that    * Splunk is present and loaded with the requisite data. */
specifier|private
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|ENABLED
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
if|if
condition|(
operator|!
name|enabled
argument_list|()
condition|)
block|{
return|return;
block|}
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
if|if
condition|(
operator|!
name|enabled
argument_list|()
condition|)
block|{
return|return;
block|}
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
name|a0
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
operator|(
name|a0
operator|.
name|next
argument_list|()
operator|&&
name|a0
operator|.
name|next
argument_list|()
operator|&&
name|a0
operator|.
name|next
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected at least 3 rows"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
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
block|}
block|}
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
argument_list|,
name|expect
argument_list|(
literal|"sourcetype=access_combined_wcookie"
argument_list|,
literal|"sourcetype=vendor_sales"
argument_list|,
literal|"sourcetype=secure"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expect
parameter_list|(
specifier|final
name|String
modifier|...
name|lines
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|lines
argument_list|)
decl_stmt|;
return|return
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
name|a0
parameter_list|)
block|{
try|try
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|actual
init|=
name|OptiqAssert
operator|.
name|toStringList
argument_list|(
name|a0
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actual
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
block|}
block|}
return|;
block|}
comment|/** "status" is not a built-in column but we know it has some values in the    * test data. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectNonBuiltInColumn
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select \"status\"\n"
operator|+
literal|"from \"splunk\".\"splunk\""
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
name|a0
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|actual
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|a0
operator|.
name|next
argument_list|()
condition|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|a0
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|actual
operator|.
name|contains
argument_list|(
literal|"404"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"cannot plan due to CAST in ON clause"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testJoinToJdbc
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
literal|"join \"foodmart\".\"product\" as p\n"
operator|+
literal|"on cast(s.\"product_id\" as integer) = p.\"product_id\"\n"
operator|+
literal|"where s.\"action\" = 'PURCHASE'"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select s.\"host\", count(\"source\") as c\n"
operator|+
literal|"from \"splunk\".\"splunk\" as s\n"
operator|+
literal|"group by s.\"host\"\n"
operator|+
literal|"order by c desc\n"
argument_list|,
name|expect
argument_list|(
literal|"host=vendor_sales; C=30244"
argument_list|,
literal|"host=www1; C=24221"
argument_list|,
literal|"host=www3; C=22975"
argument_list|,
literal|"host=www2; C=22595"
argument_list|,
literal|"host=mailsv; C=9829"
argument_list|)
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
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|f
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
operator|!
name|enabled
argument_list|()
condition|)
block|{
return|return;
block|}
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
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
literal|"inline:"
operator|+
name|JdbcTest
operator|.
name|FOODMART_MODEL
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
name|f
operator|.
name|apply
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
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
block|}
end_class

begin_comment
comment|// End SplunkAdapterTest.java
end_comment

end_unit

