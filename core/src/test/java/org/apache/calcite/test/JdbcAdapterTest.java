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
name|hsqldb
operator|.
name|jdbcDriver
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.jdbc} package.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcAdapterTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testUnionPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"sales_fact_1997\"\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"sales_fact_1998\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcUnion(all=[true])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, sales_fact_1997]])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, sales_fact_1998]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1998\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from (\n"
operator|+
literal|"  select * from \"sales_fact_1997\"\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select * from \"sales_fact_1998\")\n"
operator|+
literal|"where \"product_id\" = 1"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = 1\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1998\"\n"
operator|+
literal|"WHERE \"product_id\" = 1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"store_name\" from \"store\"\n"
operator|+
literal|"where \"store_name\" in ('Store 1', 'Store 10', 'Store 11', 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"store_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"store\"\n"
operator|+
literal|"WHERE \"store_name\" = 'Store 1' OR \"store_name\" = 'Store 10' OR \"store_name\" = 'Store 11' OR \"store_name\" = 'Store 15' OR \"store_name\" = 'Store 16' OR \"store_name\" = 'Store 24' OR \"store_name\" = 'Store 3' OR \"store_name\" = 'Store 7'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=1; store_name=Store 1\n"
operator|+
literal|"store_id=3; store_name=Store 3\n"
operator|+
literal|"store_id=7; store_name=Store 7\n"
operator|+
literal|"store_id=10; store_name=Store 10\n"
operator|+
literal|"store_id=11; store_name=Store 11\n"
operator|+
literal|"store_id=15; store_name=Store 15\n"
operator|+
literal|"store_id=16; store_name=Store 16\n"
operator|+
literal|"store_id=24; store_name=Store 24\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquiJoinPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, e.deptno, dname \n"
operator|+
literal|"from scott.emp e inner join scott.dept d \n"
operator|+
literal|"on e.deptno = d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$2], DNAME=[$4])\n"
operator|+
literal|"    JdbcJoin(condition=[=($2, $3)], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$7])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"      JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, DEPT]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", "
operator|+
literal|"\"t\".\"DEPTNO\", \"t0\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t0\" "
operator|+
literal|"ON \"t\".\"DEPTNO\" = \"t0\".\"DEPTNO\""
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-631">[CALCITE-631]    * Push theta joins down to JDBC adapter</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testNonEquiJoinPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, grade \n"
operator|+
literal|"from scott.emp e inner join scott.salgrade s \n"
operator|+
literal|"on e.sal> s.losal and e.sal< s.hisal"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], GRADE=[$3])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(>($2, $4),<($2, $5))], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, SALGRADE]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", "
operator|+
literal|"\"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"SAL\"\nFROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN \"SCOTT\".\"SALGRADE\" ON \"t\".\"SAL\"> \"SALGRADE\".\"LOSAL\" AND \"t\".\"SAL\"< \"SALGRADE\".\"HISAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonEquiJoinReverseConditionPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, grade \n"
operator|+
literal|"from scott.emp e inner join scott.salgrade s \n"
operator|+
literal|"on s.losal<= e.sal and s.hisal>= e.sal"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], GRADE=[$3])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(<=($4, $2),>=($5, $2))], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, SALGRADE]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", "
operator|+
literal|"\"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN \"SCOTT\".\"SALGRADE\" ON \"t\".\"SAL\">= \"SALGRADE\".\"LOSAL\" AND \"t\".\"SAL\"<= \"SALGRADE\".\"HISAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMixedJoinPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select e.empno, e.ename, e.empno, e.ename  \n"
operator|+
literal|"from scott.emp e inner join scott.emp m on  \n"
operator|+
literal|"e.mgr = m.empno and e.sal> m.sal"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], EMPNO0=[$0], ENAME0=[$1])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(=($2, $4),>($3, $5))], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], MGR=[$3], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", "
operator|+
literal|"\"t\".\"EMPNO\" AS \"EMPNO0\", \"t\".\"ENAME\" AS \"ENAME0\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"MGR\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"EMPNO\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" ON \"t\".\"MGR\" = \"t0\".\"EMPNO\" AND \"t\".\"SAL\"> \"t0\".\"SAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMixedJoinWithOrPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select e.empno, e.ename, e.empno, e.ename  \n"
operator|+
literal|"from scott.emp e inner join scott.emp m on  \n"
operator|+
literal|"e.mgr = m.empno and (e.sal> m.sal or m.hiredate> e.hiredate)"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], EMPNO0=[$0], ENAME0=[$1])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(=($2, $5), OR(>($4, $7),>($6, $3)))], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], MGR=[$3], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], HIREDATE=[$4], SAL=[$5])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", "
operator|+
literal|"\"t\".\"EMPNO\" AS \"EMPNO0\", \"t\".\"ENAME\" AS \"ENAME0\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"MGR\", \"HIREDATE\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"EMPNO\", \"HIREDATE\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" ON \"t\".\"MGR\" = \"t0\".\"EMPNO\" AND (\"t\".\"SAL\"> \"t0\".\"SAL\" OR \"t\".\"HIREDATE\"< \"t0\".\"HIREDATE\")"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tesJoin3TablesPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select  empno, ename, dname, grade \n"
operator|+
literal|"from scott.emp e inner join scott.dept d \n"
operator|+
literal|"on e.deptno = d.deptno \n"
operator|+
literal|"inner join scott.salgrade s \n"
operator|+
literal|"on e.sal> s.losal and e.sal< s.hisal"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$3], ENAME=[$4], DNAME=[$12], GRADE=[$0])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(>($8, $1),<($8, $2))], joinType=[inner])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, SALGRADE]])\n"
operator|+
literal|"      JdbcJoin(condition=[=($7, $8)], joinType=[inner])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, DEPT]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"EMP\".\"EMPNO\", \"EMP\".\"ENAME\", "
operator|+
literal|"\"DEPT\".\"DNAME\", \"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM \"SCOTT\".\"SALGRADE\"\n"
operator|+
literal|"INNER JOIN (\"SCOTT\".\"EMP\" INNER JOIN \"SCOTT\".\"DEPT\" ON \"EMP\".\"DEPTNO\" = \"DEPT\".\"DEPTNO\") "
operator|+
literal|"ON \"SALGRADE\".\"LOSAL\"< \"EMP\".\"SAL\" AND \"SALGRADE\".\"HISAL\"> \"EMP\".\"SAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCrossJoinWithJoinKeyPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, d.deptno, dname \n"
operator|+
literal|"from scott.emp e,scott.dept d \n"
operator|+
literal|"where e.deptno = d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$2], ENAME=[$3], DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"    JdbcJoin(condition=[=($4, $0)], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, DEPT]])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$7])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t0\".\"EMPNO\", \"t0\".\"ENAME\", "
operator|+
literal|"\"t\".\"DEPTNO\", \"t\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"EMPNO\", \"ENAME\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" ON \"t\".\"DEPTNO\" = \"t0\".\"DEPTNO\""
argument_list|)
expr_stmt|;
block|}
comment|// JdbcJoin not used for this
annotation|@
name|Test
specifier|public
name|void
name|testCartesianJoinWithoutKeyPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, d.deptno, dname \n"
operator|+
literal|"from scott.emp e,scott.dept d"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableJoin(condition=[true], "
operator|+
literal|"joinType=[inner])\n"
operator|+
literal|"  JdbcToEnumerableConverter\n"
operator|+
literal|"    JdbcProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"  JdbcToEnumerableConverter\n"
operator|+
literal|"    JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, DEPT]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCrossJoinWithJoinKeyAndFilterPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, d.deptno, dname \n"
operator|+
literal|"from scott.emp e,scott.dept d \n"
operator|+
literal|"where e.deptno = d.deptno \n"
operator|+
literal|"and e.deptno=20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$2], ENAME=[$3], DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"    JdbcJoin(condition=[=($4, $0)], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, DEPT]])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$7])\n"
operator|+
literal|"        JdbcFilter(condition=[=(CAST($7):INTEGER, 20)])\n"
operator|+
literal|"          JdbcTableScan(table=[[SCOTT, EMP]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"t1\".\"EMPNO\", \"t1\".\"ENAME\", "
operator|+
literal|"\"t\".\"DEPTNO\", \"t\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"EMPNO\", \"ENAME\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\"\n"
operator|+
literal|"WHERE CAST(\"DEPTNO\" AS INTEGER) = 20) AS \"t1\" ON \"t\".\"DEPTNO\" = \"t1\".\"DEPTNO\""
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-657">[CALCITE-657]    * NullPointerException when executing JdbcAggregate implement method</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJdbcAggregate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|url
init|=
name|MultiJdbcSchemaJoinTest
operator|.
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|baseConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Statement
name|baseStmt
init|=
name|baseConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE T2 (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS INTEGER)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T2 VALUES (1, 1)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T2 VALUES (2, null)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|baseConnection
operator|.
name|commit
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
literal|"  defaultSchema: 'BASEJDBC',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'BASEJDBC',\n"
operator|+
literal|"       jdbcDriver: '"
operator|+
name|jdbcDriver
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       jdbcUrl: '"
operator|+
name|url
operator|+
literal|"',\n"
operator|+
literal|"       jdbcCatalog: null,\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
specifier|final
name|Connection
name|calciteConnection
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
name|ResultSet
name|rs
init|=
name|calciteConnection
operator|.
name|prepareStatement
argument_list|(
literal|"select 10 * count(ID) from t2"
argument_list|)
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|rs
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
name|assertThat
argument_list|(
operator|(
name|Long
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|20L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
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
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|calciteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-259">[CALCITE-259]    * Using sub-queries in CASE statement against JDBC tables generates invalid    * Oracle SQL</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryWithSingleValue
parameter_list|()
block|{
specifier|final
name|String
name|expected
decl_stmt|;
switch|switch
condition|(
name|CalciteAssert
operator|.
name|DB
condition|)
block|{
case|case
name|MYSQL
case|:
name|expected
operator|=
literal|"Subquery returns more than 1 row"
expr_stmt|;
break|break;
default|default:
name|expected
operator|=
literal|"more than one value in agg SINGLE_VALUE"
expr_stmt|;
block|}
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"full_name\" FROM \"employee\" WHERE "
operator|+
literal|"\"employee_id\" = (SELECT \"employee_id\" FROM \"salary\")"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"SINGLE_VALUE"
argument_list|)
operator|.
name|throws_
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcAdapterTest.java
end_comment

end_unit

