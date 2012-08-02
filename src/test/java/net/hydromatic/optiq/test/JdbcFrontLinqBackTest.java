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
block|}
end_class

begin_comment
comment|// End JdbcFrontLinqBackTest.java
end_comment

end_unit

