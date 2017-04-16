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
name|Ordering
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
name|sql
operator|.
name|Types
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
name|Collections
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
name|assertEquals
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
comment|/**  * System test of the Calcite file adapter, which can also read and parse  * HTML tables over HTTP.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTest
block|{
comment|// helper functions
specifier|private
name|Fluent
name|sql
parameter_list|(
name|String
name|model
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Fluent
argument_list|(
name|model
argument_list|,
name|sql
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
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expect
parameter_list|(
name|String
modifier|...
name|expectedLines
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|expectedLines
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|expected
init|=
name|b
operator|.
name|toString
argument_list|()
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
return|;
block|}
comment|/** Returns a function that checks the contents of a result set against an    * expected string. */
specifier|private
specifier|static
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expectUnordered
parameter_list|(
name|String
modifier|...
name|expected
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedLines
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
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
name|SqlTest
operator|.
name|collect
argument_list|(
name|lines
argument_list|,
name|resultSet
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|lines
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedLines
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
name|Util
operator|.
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
name|FileReaderTest
operator|.
name|file
argument_list|(
literal|"target/test-classes/"
operator|+
name|model
operator|+
literal|".json"
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
comment|/** Reads from a local file and checks the result. */
annotation|@
name|Test
specifier|public
name|void
name|testFileSelect
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select H1 from T1 where H0 = 'R1C0'"
decl_stmt|;
name|sql
argument_list|(
literal|"testModel"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"H1=R1C1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads from a local file without table headers&lt;TH&gt; and checks the    * result. */
annotation|@
name|Test
specifier|public
name|void
name|testNoThSelect
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
literal|"select \"col1\" from T1_NO_TH where \"col0\" like 'R0%'"
decl_stmt|;
name|sql
argument_list|(
literal|"testModel"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"col1=R0C1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads from a local file - finds larger table even without&lt;TH&gt;    * elements. */
annotation|@
name|Test
specifier|public
name|void
name|testFindBiggerNoTh
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"col4\" from TABLEX2 where \"col0\" like 'R1%'"
decl_stmt|;
name|sql
argument_list|(
literal|"testModel"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"col4=R1C4"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads from a URL and checks the result. */
annotation|@
name|Test
specifier|public
name|void
name|testUrlSelect
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
literal|"select \"State\", \"Statehood\" from \"States_as_of\"\n"
operator|+
literal|"where \"State\" = 'California'"
decl_stmt|;
name|sql
argument_list|(
literal|"wiki"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"State=California; Statehood=1850-09-09"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the EMPS table. */
annotation|@
name|Test
specifier|public
name|void
name|testSalesEmps
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.emps"
decl_stmt|;
name|sql
argument_list|(
literal|"sales"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=100; NAME=Fred; DEPTNO=30"
argument_list|,
literal|"EMPNO=110; NAME=Eric; DEPTNO=20"
argument_list|,
literal|"EMPNO=110; NAME=John; DEPTNO=40"
argument_list|,
literal|"EMPNO=120; NAME=Wilma; DEPTNO=20"
argument_list|,
literal|"EMPNO=130; NAME=Alice; DEPTNO=40"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the DEPTS table. */
annotation|@
name|Test
specifier|public
name|void
name|testSalesDepts
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.depts"
decl_stmt|;
name|sql
argument_list|(
literal|"sales"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=10; NAME=Sales"
argument_list|,
literal|"DEPTNO=20; NAME=Marketing"
argument_list|,
literal|"DEPTNO=30; NAME=Accounts"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the DEPTS table from the CSV schema. */
annotation|@
name|Test
specifier|public
name|void
name|testCsvSalesDepts
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.depts"
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=10; NAME=Sales"
argument_list|,
literal|"DEPTNO=20; NAME=Marketing"
argument_list|,
literal|"DEPTNO=30; NAME=Accounts"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the EMPS table from the CSV schema. */
annotation|@
name|Test
specifier|public
name|void
name|testCsvSalesEmps
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.emps"
decl_stmt|;
specifier|final
name|String
index|[]
name|lines
init|=
block|{
literal|"EMPNO=100; NAME=Fred; DEPTNO=10; GENDER=; CITY=; EMPID=30; AGE=25; SLACKER=true; MANAGER=false; JOINEDAT=1996-08-03"
block|,
literal|"EMPNO=110; NAME=Eric; DEPTNO=20; GENDER=M; CITY=San Francisco; EMPID=3; AGE=80; SLACKER=null; MANAGER=false; JOINEDAT=2001-01-01"
block|,
literal|"EMPNO=110; NAME=John; DEPTNO=40; GENDER=M; CITY=Vancouver; EMPID=2; AGE=null; SLACKER=false; MANAGER=true; JOINEDAT=2002-05-03"
block|,
literal|"EMPNO=120; NAME=Wilma; DEPTNO=20; GENDER=F; CITY=; EMPID=1; AGE=5; SLACKER=null; MANAGER=true; JOINEDAT=2005-09-07"
block|,
literal|"EMPNO=130; NAME=Alice; DEPTNO=40; GENDER=F; CITY=Vancouver; EMPID=2; AGE=null; SLACKER=false; MANAGER=true; JOINEDAT=2007-01-01"
block|,     }
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|lines
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the HEADER_ONLY table from the CSV schema. The CSV file has one    * line - the column headers - but no rows of data. */
annotation|@
name|Test
specifier|public
name|void
name|testCsvSalesHeaderOnly
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.header_only"
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql
argument_list|)
operator|.
name|returns
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Reads the EMPTY table from the CSV schema. The CSV file has no lines,    * therefore the table has a system-generated column called    * "EmptyFileHasNoColumns". */
annotation|@
name|Test
specifier|public
name|void
name|testCsvSalesEmpty
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.\"EMPTY\""
decl_stmt|;
name|checkSql
argument_list|(
name|sql
argument_list|,
literal|"sales-csv"
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
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnName
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"EmptyFileHasNoColumns"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertThat
argument_list|(
name|actual
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1754">[CALCITE-1754]    * In Csv adapter, convert DATE and TIME values to int, and TIMESTAMP values    * to long</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByTimestampAdd
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c,\n"
operator|+
literal|"  {fn timestampadd(SQL_TSI_DAY, 1, JOINEDAT) } as t\n"
operator|+
literal|"from EMPS group by {fn timestampadd(SQL_TSI_DAY, 1, JOINEDAT ) } "
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=1; T=1996-08-04"
argument_list|,
literal|"C=1; T=2002-05-04"
argument_list|,
literal|"C=1; T=2005-09-08"
argument_list|,
literal|"C=1; T=2007-01-02"
argument_list|,
literal|"C=1; T=2001-01-02"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select count(*) as c,\n"
operator|+
literal|"  {fn timestampadd(SQL_TSI_MONTH, 1, JOINEDAT) } as t\n"
operator|+
literal|"from EMPS group by {fn timestampadd(SQL_TSI_MONTH, 1, JOINEDAT ) } "
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=1; T=2002-06-03"
argument_list|,
literal|"C=1; T=2005-10-07"
argument_list|,
literal|"C=1; T=2007-02-01"
argument_list|,
literal|"C=1; T=2001-02-01"
argument_list|,
literal|"C=1; T=1996-09-03"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select\n"
operator|+
literal|" distinct {fn timestampadd(SQL_TSI_MONTH, 1, JOINEDAT) } as t\n"
operator|+
literal|"from EMPS"
decl_stmt|;
name|sql
argument_list|(
literal|"sales-csv"
argument_list|,
name|sql3
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"T=2002-06-03"
argument_list|,
literal|"T=2005-10-07"
argument_list|,
literal|"T=2007-02-01"
argument_list|,
literal|"T=2001-02-01"
argument_list|,
literal|"T=1996-09-03"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Fluent API to perform test actions. */
specifier|private
class|class
name|Fluent
block|{
specifier|private
specifier|final
name|String
name|model
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expect
decl_stmt|;
name|Fluent
parameter_list|(
name|String
name|model
parameter_list|,
name|String
name|sql
parameter_list|,
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expect
parameter_list|)
block|{
name|this
operator|.
name|model
operator|=
name|model
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|expect
operator|=
name|expect
expr_stmt|;
block|}
comment|/** Runs the test. */
name|Fluent
name|ok
parameter_list|()
block|{
try|try
block|{
name|checkSql
argument_list|(
name|sql
argument_list|,
name|model
argument_list|,
name|expect
argument_list|)
expr_stmt|;
return|return
name|this
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
comment|/** Assigns a function to call to test whether output is correct. */
name|Fluent
name|checking
parameter_list|(
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|expect
parameter_list|)
block|{
return|return
operator|new
name|Fluent
argument_list|(
name|model
argument_list|,
name|sql
argument_list|,
name|expect
argument_list|)
return|;
block|}
comment|/** Sets the rows that are expected to be returned from the SQL query. */
name|Fluent
name|returns
parameter_list|(
name|String
modifier|...
name|expectedLines
parameter_list|)
block|{
return|return
name|checking
argument_list|(
name|expect
argument_list|(
name|expectedLines
argument_list|)
argument_list|)
return|;
block|}
comment|/** Sets the rows that are expected to be returned from the SQL query,      * in no particular order. */
name|Fluent
name|returnsUnordered
parameter_list|(
name|String
modifier|...
name|expectedLines
parameter_list|)
block|{
return|return
name|checking
argument_list|(
name|expectUnordered
argument_list|(
name|expectedLines
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlTest.java
end_comment

end_unit

