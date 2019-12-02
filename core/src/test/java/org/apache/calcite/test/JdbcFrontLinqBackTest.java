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
name|java
operator|.
name|JavaTypeFactory
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
name|jdbc
operator|.
name|CalciteConnection
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|schema
operator|.
name|SchemaPlus
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
name|schema
operator|.
name|Schemas
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
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
name|schema
operator|.
name|impl
operator|.
name|AbstractTableQueryable
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Disabled
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|PreparedStatement
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
name|Collection
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
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|CalciteAssert
operator|.
name|hr
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|CalciteAssert
operator|.
name|that
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
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for a JDBC front-end (with some quite complex SQL) and Linq4j back-end  * (based on in-memory collections).  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontLinqBackTest
block|{
comment|/**    * Runs a simple query that reads from a table in an in-memory schema.    */
annotation|@
name|Test
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"where s.\"cust_id\" = 100"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"cust_id=100; prod_id=10\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs a simple query that joins between two in-memory schemas.    */
annotation|@
name|Test
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"hr\".\"emps\" as e\n"
operator|+
literal|"on e.\"empid\" = s.\"cust_id\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cust_id=100; prod_id=10; empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000"
argument_list|,
literal|"cust_id=150; prod_id=20; empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple GROUP BY.    */
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", sum(\"empid\") as s, count(*) as c\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"group by \"deptno\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"deptno=20; S=200; C=1\n"
operator|+
literal|"deptno=10; S=360; C=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple ORDER BY.    */
annotation|@
name|Test
specifier|public
name|void
name|testOrderBy
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select upper(\"name\") as un, \"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"order by \"deptno\", \"name\" desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableSort(sort0=[$1], sort1=[$2], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[UPPER($t2)], UN=[$t5], deptno=[$t1], name=[$t2])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"UN=THEODORE; deptno=10\n"
operator|+
literal|"UN=SEBASTIAN; deptno=10\n"
operator|+
literal|"UN=BILL; deptno=10\n"
operator|+
literal|"UN=ERIC; deptno=20\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple UNION, plus ORDER BY.    *    *<p>Also tests a query that returns a single column. We optimize this case    * internally, using non-array representations for rows.</p>    */
annotation|@
name|Test
specifier|public
name|void
name|testUnionAllOrderBy
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"name\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"union all\n"
operator|+
literal|"select \"name\"\n"
operator|+
literal|"from \"hr\".\"depts\"\n"
operator|+
literal|"order by 1 desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"name=Theodore\n"
operator|+
literal|"name=Sebastian\n"
operator|+
literal|"name=Sales\n"
operator|+
literal|"name=Marketing\n"
operator|+
literal|"name=HR\n"
operator|+
literal|"name=Eric\n"
operator|+
literal|"name=Bill\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests UNION.    */
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(\"name\" from 1 for 1) as x\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"union\n"
operator|+
literal|"select substring(\"name\" from 1 for 1) as y\n"
operator|+
literal|"from \"hr\".\"depts\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=T"
argument_list|,
literal|"X=E"
argument_list|,
literal|"X=S"
argument_list|,
literal|"X=B"
argument_list|,
literal|"X=M"
argument_list|,
literal|"X=H"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests INTERSECT.    */
annotation|@
name|Test
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(\"name\" from 1 for 1) as x\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"intersect\n"
operator|+
literal|"select substring(\"name\" from 1 for 1) as y\n"
operator|+
literal|"from \"hr\".\"depts\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"X=S\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests EXCEPT.    */
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(\"name\" from 1 for 1) as x\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"except\n"
operator|+
literal|"select substring(\"name\" from 1 for 1) as y\n"
operator|+
literal|"from \"hr\".\"depts\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=T"
argument_list|,
literal|"X=E"
argument_list|,
literal|"X=B"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereBad
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"where empid> 120"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Column 'EMPID' not found in any table"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-9">[CALCITE-9]    * RexToLixTranslator not incrementing local variable name counter</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereOr
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where (\"empid\" = 100 or \"empid\" = 200)\n"
operator|+
literal|"and \"deptno\" = 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereLike
parameter_list|()
block|{
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"where e.\"empid\"< 120 or e.\"name\" like 'S%'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsert
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|mutable
argument_list|(
name|employees
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=0; deptno=0; name=first; salary=0.0; commission=null\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" select * from \"hr\".\"emps\""
argument_list|)
operator|.
name|updates
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=5\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" "
operator|+
literal|"select * from \"hr\".\"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|updates
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\", count(*) as c from \"foo\".\"bar\" "
operator|+
literal|"group by \"name\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; C=2"
argument_list|,
literal|"name=Eric; C=1"
argument_list|,
literal|"name=Theodore; C=2"
argument_list|,
literal|"name=first; C=1"
argument_list|,
literal|"name=Sebastian; C=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBind
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|mutable
argument_list|(
name|employees
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=1\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|doWithConnection
argument_list|(
name|c
lambda|->
block|{
try|try
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into \"foo\".\"bar\"\n"
operator|+
literal|"values (?, 0, ?, 10.0, null)"
decl_stmt|;
try|try
init|(
name|PreparedStatement
name|p
init|=
name|c
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|p
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|p
operator|.
name|executeUpdate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=2\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"foo\".\"bar\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=0; deptno=0; name=first; salary=0.0; commission=null"
argument_list|,
literal|"empid=1; deptno=0; name=foo; salary=10.0; commission=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDelete
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|mutable
argument_list|(
name|employees
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"foo\".\"bar\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=0; deptno=0; name=first; salary=0.0; commission=null"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" select * from \"hr\".\"emps\""
argument_list|)
operator|.
name|updates
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=5"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|deleteSql
init|=
literal|"delete from \"foo\".\"bar\" "
operator|+
literal|"where \"deptno\" = 10"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
name|deleteSql
argument_list|)
operator|.
name|updates
argument_list|(
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"name\", count(*) as c\n"
operator|+
literal|"from \"foo\".\"bar\"\n"
operator|+
literal|"group by \"name\""
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Eric; C=1"
argument_list|,
literal|"name=first; C=1"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates the post processor routine to be applied against a Connection.    *    *<p>Table schema is based on JdbcTest#Employee    * (refer to {@link JdbcFrontLinqBackTest#mutable}).    *    * @param initialData records to be presented in table    * @return a connection post-processor    */
specifier|private
specifier|static
name|CalciteAssert
operator|.
name|ConnectionPostProcessor
name|makePostProcessor
parameter_list|(
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|initialData
parameter_list|)
block|{
return|return
name|connection
lambda|->
block|{
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|SchemaPlus
name|mapSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tableName
init|=
literal|"bar"
decl_stmt|;
specifier|final
name|JdbcTest
operator|.
name|AbstractModifiableTable
name|table
init|=
name|mutable
argument_list|(
name|tableName
argument_list|,
name|initialData
argument_list|)
decl_stmt|;
name|mapSchema
operator|.
name|add
argument_list|(
name|tableName
argument_list|,
name|table
argument_list|)
expr_stmt|;
return|return
name|calciteConnection
return|;
block|}
return|;
block|}
comment|/**    * Method to be shared with {@code RemoteDriverTest}.    *    * @param initialData record to be presented in table    */
specifier|public
specifier|static
name|Connection
name|makeConnection
parameter_list|(
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|initialData
parameter_list|)
throws|throws
name|Exception
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
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|connection
operator|=
name|makePostProcessor
argument_list|(
name|initialData
argument_list|)
operator|.
name|apply
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**    * Creates a connection with an empty modifiable table with    * {@link JdbcTest.Employee} schema.    */
specifier|public
specifier|static
name|Connection
name|makeConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|makeConnection
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|mutable
parameter_list|(
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
parameter_list|)
block|{
name|employees
operator|.
name|add
argument_list|(
operator|new
name|JdbcTest
operator|.
name|Employee
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|"first"
argument_list|,
literal|0f
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|with
argument_list|(
name|makePostProcessor
argument_list|(
name|employees
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|JdbcTest
operator|.
name|AbstractModifiableTable
name|mutable
parameter_list|(
name|String
name|tableName
parameter_list|,
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
parameter_list|)
block|{
return|return
operator|new
name|JdbcTest
operator|.
name|AbstractModifiableTable
argument_list|(
name|tableName
argument_list|)
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|JdbcTest
operator|.
name|Employee
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|employees
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|JdbcTest
operator|.
name|Employee
operator|.
name|class
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Schemas
operator|.
name|tableExpression
argument_list|(
name|schema
argument_list|,
name|getElementType
argument_list|()
argument_list|,
name|tableName
argument_list|,
name|clazz
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|getModifiableCollection
parameter_list|()
block|{
return|return
name|employees
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsert2
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|mutable
argument_list|(
name|employees
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" values (1, 1, 'second', 2, 2)"
argument_list|)
operator|.
name|updates
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\"\n"
operator|+
literal|"values (1, 3, 'third', 0, 3), (1, 4, 'fourth', 0, 4), (1, 5, 'fifth ', 0, 3)"
argument_list|)
operator|.
name|updates
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=5\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" values (1, 6, null, 0, null)"
argument_list|)
operator|.
name|updates
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foo\".\"bar\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=6\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Local Statement insert    */
annotation|@
name|Test
specifier|public
name|void
name|testInsert3
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|makeConnection
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
literal|"insert into \"foo\".\"bar\" values (1, 1, 'second', 2, 2)"
decl_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|boolean
name|status
init|=
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|==
literal|null
argument_list|)
expr_stmt|;
name|int
name|updateCount
init|=
name|statement
operator|.
name|getUpdateCount
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updateCount
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Local PreparedStatement insert WITHOUT bind variables    */
annotation|@
name|Test
specifier|public
name|void
name|testPreparedStatementInsert
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|makeConnection
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
literal|"insert into \"foo\".\"bar\" values (1, 1, 'second', 2, 2)"
decl_stmt|;
name|PreparedStatement
name|preparedStatement
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|preparedStatement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|status
init|=
name|preparedStatement
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|preparedStatement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|==
literal|null
argument_list|)
expr_stmt|;
name|int
name|updateCount
init|=
name|preparedStatement
operator|.
name|getUpdateCount
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updateCount
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Local PreparedStatement insert WITH bind variables    */
annotation|@
name|Test
specifier|public
name|void
name|testPreparedStatementInsert2
parameter_list|()
throws|throws
name|Exception
block|{
block|}
comment|/** Some of the rows have the wrong number of columns. */
annotation|@
name|Test
specifier|public
name|void
name|testInsertMultipleRowMismatch
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|mutable
argument_list|(
name|employees
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"insert into \"foo\".\"bar\" values\n"
operator|+
literal|" (1, 3, 'third'),\n"
operator|+
literal|" (1, 4, 'fourth'),\n"
operator|+
literal|" (1, 5, 'fifth ', 3)"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Incompatible types"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontLinqBackTest.java
end_comment

end_unit

