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
name|optiq
operator|.
name|MutableSchema
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
name|MapSchema
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for a JDBC front-end (with some quite complex SQL) and Linq4j back-end  * (based on in-memory collections).  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontLinqBackTest
extends|extends
name|TestCase
block|{
comment|/**      * Runs a simple query that reads from a table in an in-memory schema.      */
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
name|assertThat
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
comment|/**      * Runs a simple query that joins between two in-memory schemas.      */
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
name|assertThat
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
literal|"cust_id=100; prod_id=10; empid=100; deptno=10; name=Bill\n"
operator|+
literal|"cust_id=150; prod_id=20; empid=150; deptno=10; name=Sebastian\n"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple GROUP BY.      */
specifier|public
name|void
name|testGroupBy
parameter_list|()
block|{
name|assertThat
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
literal|"deptno=10; S=250; C=2\n"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple ORDER BY.      */
specifier|public
name|void
name|testOrderBy
parameter_list|()
block|{
name|assertThat
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
literal|"UN=SEBASTIAN; deptno=10\n"
operator|+
literal|"UN=BILL; deptno=10\n"
operator|+
literal|"UN=ERIC; deptno=20\n"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple UNION, plus ORDER BY.      *      *<p>Also tests a query that returns a single column. We optimize this case      * internally, using non-array representations for rows.</p>      */
specifier|public
name|void
name|testUnionAllOrderBy
parameter_list|()
block|{
name|assertThat
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
comment|/**      * Tests UNION.      */
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
name|assertThat
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
name|returns
argument_list|(
literal|"X=E\n"
operator|+
literal|"X=S\n"
operator|+
literal|"X=B\n"
operator|+
literal|"X=M\n"
operator|+
literal|"X=H\n"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests INTERSECT.      */
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
name|assertThat
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
comment|/**      * Tests EXCEPT.      */
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
name|assertThat
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
name|returns
argument_list|(
literal|"X=E\n"
operator|+
literal|"X=B\n"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWhereBad
parameter_list|()
block|{
name|assertThat
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
specifier|public
name|void
name|testWhereOr
parameter_list|()
block|{
name|assertThat
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
literal|"empid=100; deptno=10; name=Bill\n"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWhereLike
parameter_list|()
block|{
if|if
condition|(
literal|false
condition|)
comment|// TODO: fix current error "Operands E.name, 'B%' not comparable to
comment|// each other"
name|assertThat
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"where e.\"empid\"> 120 and e.\"name\" like 'B%'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"cust_id=100; prod_id=10; empid=100; name=Bill\n"
operator|+
literal|"cust_id=150; prod_id=20; empid=150; name=Sebastian\n"
argument_list|)
expr_stmt|;
block|}
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
literal|"empid=0; deptno=0; name=first\n"
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
literal|"C=4\n"
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
literal|"ROWCOUNT=2\n"
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
name|returns
argument_list|(
literal|"name=Bill; C=2\n"
operator|+
literal|"name=Eric; C=1\n"
operator|+
literal|"name=first; C=1\n"
operator|+
literal|"name=Sebastian; C=2\n"
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
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|assertThat
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
name|JdbcTest
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
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|MapSchema
name|mapSchema
init|=
name|MapSchema
operator|.
name|create
argument_list|(
name|optiqConnection
argument_list|,
name|rootSchema
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|mapSchema
operator|.
name|addTable
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|JdbcTest
operator|.
name|AbstractModifiableTable
argument_list|(
name|mapSchema
argument_list|,
name|JdbcTest
operator|.
name|Employee
operator|.
name|class
argument_list|,
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createType
argument_list|(
name|JdbcTest
operator|.
name|Employee
operator|.
name|class
argument_list|)
argument_list|,
literal|"bar"
argument_list|)
block|{
specifier|public
name|Enumerator
name|enumerator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|employees
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
literal|"insert into \"foo\".\"bar\" values (1, 1, 'second')"
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
literal|"values (1, 3, 'third'), (1, 4, 'fourth'), (1, 5, 'fifth ')"
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
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontLinqBackTest.java
end_comment

end_unit

