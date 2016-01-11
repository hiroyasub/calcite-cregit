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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|base
operator|.
name|Throwables
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Properties
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit test of the Calcite adapter for CSV.  */
end_comment

begin_class
specifier|public
class|class
name|CsvTest
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
specifier|public
specifier|static
name|String
name|toLinux
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\r\n"
argument_list|,
literal|"\n"
argument_list|)
return|;
block|}
comment|/**    * Tests the vanity driver.    */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testVanityDriver
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
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testVanityDriverArgsInUrl
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
comment|/** Tests an inline schema with a non-existent directory. */
annotation|@
name|Test
specifier|public
name|void
name|testBadDirectory
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
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'bad',\n"
operator|+
literal|"       factory: 'org.apache.calcite.adapter.csv.CsvSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         directory: '/does/not/exist'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
comment|// must print "directory ... not found" to stdout, but not fail
name|ResultSet
name|tables
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|tables
operator|.
name|next
argument_list|()
expr_stmt|;
name|tables
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
literal|"model"
argument_list|,
literal|"select * from EMPS"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectSingleProjectGz
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"select name from EMPS"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectSingleProject
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"select name from DEPTS"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-898">[CALCITE-898]    * Type inference multiplying Java long by SQL INTEGER</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectLongMultiplyInteger
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno * 3 as e3\n"
operator|+
literal|"from long_emps where empno = 100"
decl_stmt|;
name|checkSql
argument_list|(
name|sql
argument_list|,
literal|"bug"
argument_list|,
operator|new
name|Function1
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
name|assertThat
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|o
init|=
operator|(
name|Long
operator|)
name|resultSet
operator|.
name|getObject
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|,
name|is
argument_list|(
literal|300L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
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
name|Throwables
operator|.
name|propagate
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
name|testCustomTable
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-with-custom-table"
argument_list|,
literal|"select * from CUSTOM_TABLE.EMPS"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushDownProjectDumb
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// rule does not fire, because we're using 'dumb' tables in simple model
name|checkSql
argument_list|(
literal|"model"
argument_list|,
literal|"explain plan for select * from EMPS"
argument_list|,
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[SALES, EMPS]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushDownProject
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"explain plan for select * from EMPS"
argument_list|,
literal|"PLAN=CsvTableScan(table=[[SALES, EMPS]], fields=[[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushDownProject2
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"explain plan for select name, empno from EMPS"
argument_list|,
literal|"PLAN=CsvTableScan(table=[[SALES, EMPS]], fields=[[1, 0]])\n"
argument_list|)
expr_stmt|;
comment|// make sure that it works...
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"select name, empno from EMPS"
argument_list|,
literal|"NAME=Fred; EMPNO=100"
argument_list|,
literal|"NAME=Eric; EMPNO=110"
argument_list|,
literal|"NAME=John; EMPNO=110"
argument_list|,
literal|"NAME=Wilma; EMPNO=120"
argument_list|,
literal|"NAME=Alice; EMPNO=130"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterableSelect
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"filterable-model"
argument_list|,
literal|"select name from EMPS"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterableSelectStar
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"filterable-model"
argument_list|,
literal|"select * from EMPS"
argument_list|)
expr_stmt|;
block|}
comment|/** Filter that can be fully handled by CsvFilterableTable. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterableWhere
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"filterable-model"
argument_list|,
literal|"select empno, gender, name from EMPS where name = 'John'"
argument_list|,
literal|"EMPNO=110; GENDER=M; NAME=John"
argument_list|)
expr_stmt|;
block|}
comment|/** Filter that can be partly handled by CsvFilterableTable. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterableWhere2
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"filterable-model"
argument_list|,
literal|"select empno, gender, name from EMPS where gender = 'F' and empno> 125"
argument_list|,
literal|"EMPNO=130; GENDER=F; NAME=Alice"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJson
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"bug"
argument_list|,
literal|"select _MAP['id'] as id,\n"
operator|+
literal|" _MAP['title'] as title,\n"
operator|+
literal|" CHAR_LENGTH(CAST(_MAP['title'] AS VARCHAR(30))) as len\n"
operator|+
literal|" from \"archers\""
argument_list|,
literal|"ID=19990101; TITLE=Tractor trouble.; LEN=16"
argument_list|,
literal|"ID=19990103; TITLE=Charlie's surprise.; LEN=19"
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
name|output
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Function1
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|output
parameter_list|()
block|{
return|return
operator|new
name|Function1
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
return|;
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
modifier|...
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
name|expect
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a function that checks the contents of a result set against an    * expected string. */
specifier|private
specifier|static
name|Function1
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
name|expected
parameter_list|)
block|{
return|return
operator|new
name|Function1
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
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CsvTest
operator|.
name|collect
argument_list|(
name|lines
argument_list|,
name|resultSet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
argument_list|,
name|lines
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
return|;
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
name|Function1
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
name|jsonPath
argument_list|(
name|model
argument_list|)
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
name|String
name|jsonPath
parameter_list|(
name|String
name|model
parameter_list|)
block|{
specifier|final
name|URL
name|url
init|=
name|CsvTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/"
operator|+
name|model
operator|+
literal|".json"
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|url
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"file:"
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|"file:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
specifier|private
specifier|static
name|void
name|collect
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|result
parameter_list|,
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
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
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
name|getString
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
name|result
operator|.
name|add
argument_list|(
name|toLinux
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnString
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"select * from emps join depts on emps.name = depts.name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWackyColumns
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"select * from wacky_column_names where false"
argument_list|,
literal|"bug"
argument_list|,
name|expect
argument_list|()
argument_list|)
expr_stmt|;
name|checkSql
argument_list|(
literal|"select \"joined at\", \"naME\" from wacky_column_names where \"2gender\" = 'F'"
argument_list|,
literal|"bug"
argument_list|,
name|expect
argument_list|(
literal|"joined at=2005-09-07; naME=Wilma"
argument_list|,
literal|"joined at=2007-01-01; naME=Alice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBoolean
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"smart"
argument_list|,
literal|"select empno, slacker from emps where slacker"
argument_list|,
literal|"EMPNO=100; SLACKER=true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadme
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"SELECT d.name, COUNT(*) cnt"
operator|+
literal|" FROM emps AS e"
operator|+
literal|" JOIN depts AS d ON e.deptno = d.deptno"
operator|+
literal|" GROUP BY d.name"
argument_list|,
literal|"smart"
argument_list|,
name|expect
argument_list|(
literal|"NAME=Sales; CNT=1"
argument_list|,
literal|"NAME=Marketing; CNT=2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-824">[CALCITE-824]    * Type inference when converting IN clause to semijoin</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testInToSemiJoinWithCast
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Note that the IN list needs at least 20 values to trigger the rewrite
comment|// to a semijoin. Try it both ways.
specifier|final
name|String
name|sql
init|=
literal|"SELECT e.name\n"
operator|+
literal|"FROM emps AS e\n"
operator|+
literal|"WHERE cast(e.empno as bigint) in "
decl_stmt|;
name|checkSql
argument_list|(
name|sql
operator|+
name|range
argument_list|(
literal|130
argument_list|,
name|SqlToRelConverter
operator|.
name|IN_SUBQUERY_THRESHOLD
operator|-
literal|5
argument_list|)
argument_list|,
literal|"smart"
argument_list|,
name|expect
argument_list|(
literal|"NAME=Alice"
argument_list|)
argument_list|)
expr_stmt|;
name|checkSql
argument_list|(
name|sql
operator|+
name|range
argument_list|(
literal|130
argument_list|,
name|SqlToRelConverter
operator|.
name|IN_SUBQUERY_THRESHOLD
argument_list|)
argument_list|,
literal|"smart"
argument_list|,
name|expect
argument_list|(
literal|"NAME=Alice"
argument_list|)
argument_list|)
expr_stmt|;
name|checkSql
argument_list|(
name|sql
operator|+
name|range
argument_list|(
literal|130
argument_list|,
name|SqlToRelConverter
operator|.
name|IN_SUBQUERY_THRESHOLD
operator|+
literal|1000
argument_list|)
argument_list|,
literal|"smart"
argument_list|,
name|expect
argument_list|(
literal|"NAME=Alice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1051">[CALCITE-1051]    * Underflow exception due to scaling IN clause literals</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testInToSemiJoinWithoutCast
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e.name\n"
operator|+
literal|"FROM emps AS e\n"
operator|+
literal|"WHERE e.empno in "
operator|+
name|range
argument_list|(
literal|130
argument_list|,
name|SqlToRelConverter
operator|.
name|IN_SUBQUERY_THRESHOLD
argument_list|)
decl_stmt|;
name|checkSql
argument_list|(
name|sql
argument_list|,
literal|"smart"
argument_list|,
name|expect
argument_list|(
literal|"NAME=Alice"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|range
parameter_list|(
name|int
name|first
parameter_list|,
name|int
name|count
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
operator|==
literal|0
condition|?
literal|"("
else|:
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|first
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDateType
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
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
name|jsonPath
argument_list|(
literal|"bug"
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
init|)
block|{
name|ResultSet
name|res
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"DATE"
argument_list|,
literal|"JOINEDAT"
argument_list|)
decl_stmt|;
name|res
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
operator|.
name|getInt
argument_list|(
literal|"DATA_TYPE"
argument_list|)
argument_list|,
name|java
operator|.
name|sql
operator|.
name|Types
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|res
operator|=
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"DATE"
argument_list|,
literal|"JOINTIME"
argument_list|)
expr_stmt|;
name|res
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
operator|.
name|getInt
argument_list|(
literal|"DATA_TYPE"
argument_list|)
argument_list|,
name|java
operator|.
name|sql
operator|.
name|Types
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|res
operator|=
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"DATE"
argument_list|,
literal|"JOINTIMES"
argument_list|)
expr_stmt|;
name|res
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|res
operator|.
name|getInt
argument_list|(
literal|"DATA_TYPE"
argument_list|)
argument_list|,
name|java
operator|.
name|sql
operator|.
name|Types
operator|.
name|TIMESTAMP
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select \"JOINEDAT\", \"JOINTIME\", \"JOINTIMES\" from \"DATE\" where EMPNO = 100"
argument_list|)
decl_stmt|;
name|resultSet
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// date
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
argument_list|,
name|resultSet
operator|.
name|getDate
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|valueOf
argument_list|(
literal|"1996-08-03"
argument_list|)
argument_list|,
name|resultSet
operator|.
name|getDate
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// time
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|class
argument_list|,
name|resultSet
operator|.
name|getTime
argument_list|(
literal|2
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Time
operator|.
name|valueOf
argument_list|(
literal|"00:01:02"
argument_list|)
argument_list|,
name|resultSet
operator|.
name|getTime
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// timestamp
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|class
argument_list|,
name|resultSet
operator|.
name|getTimestamp
argument_list|(
literal|3
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Timestamp
operator|.
name|valueOf
argument_list|(
literal|"1996-08-03 00:01:02"
argument_list|)
argument_list|,
name|resultSet
operator|.
name|getTimestamp
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CsvTest.java
end_comment

end_unit

