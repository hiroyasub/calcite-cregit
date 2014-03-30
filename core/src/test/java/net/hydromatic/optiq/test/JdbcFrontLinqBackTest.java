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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|QueryProvider
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Queryable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
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
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
operator|.
name|that
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
name|that
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
name|that
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
name|returns
argument_list|(
literal|"cust_id=100; prod_id=10; empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"cust_id=150; prod_id=20; empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
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
name|that
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
name|that
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
name|that
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
name|that
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
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
name|that
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
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
name|that
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
name|that
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
comment|/** Test case for https://github.com/julianhyde/optiq/issues/9. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereOr
parameter_list|()
block|{
name|that
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
name|that
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
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
decl_stmt|;
name|OptiqAssert
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
name|returns
argument_list|(
literal|"ROWCOUNT=4\n"
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
name|returns
argument_list|(
literal|"ROWCOUNT=3\n"
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
specifier|private
name|OptiqAssert
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
operator|new
name|OptiqAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|connection
init|=
name|OptiqAssert
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|,
literal|"foodmart"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|optiqConnection
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
name|optiqConnection
return|;
block|}
block|}
argument_list|)
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
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
decl_stmt|;
name|OptiqAssert
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
name|returns
argument_list|(
literal|"ROWCOUNT=1\n"
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
name|returns
argument_list|(
literal|"ROWCOUNT=3\n"
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
name|returns
argument_list|(
literal|"ROWCOUNT=1\n"
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
argument_list|<
name|JdbcTest
operator|.
name|Employee
argument_list|>
argument_list|()
decl_stmt|;
name|OptiqAssert
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

