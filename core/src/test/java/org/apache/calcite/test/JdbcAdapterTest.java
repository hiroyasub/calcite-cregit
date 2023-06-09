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
name|config
operator|.
name|CalciteConnectionProperty
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
name|config
operator|.
name|Lex
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
name|test
operator|.
name|CalciteAssert
operator|.
name|AssertThat
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
name|test
operator|.
name|CalciteAssert
operator|.
name|DatabaseInstance
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
name|test
operator|.
name|schemata
operator|.
name|foodmart
operator|.
name|FoodmartSchema
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
name|Smalls
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.jdbc} package.  */
end_comment

begin_class
class|class
name|JdbcAdapterTest
block|{
comment|/** Ensures that tests that are modifying data (doing DML) do not run at the    * same time. */
specifier|private
specifier|static
specifier|final
name|ReentrantLock
name|LOCK
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/** VALUES is pushed down. */
annotation|@
name|Test
name|void
name|testValuesPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from \"days\", (values 1, 2) as t(c)"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, days]])\n"
operator|+
literal|"    JdbcValues(tuples=[[{ 1 }, { 2 }]])"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"days\",\n"
operator|+
literal|"(VALUES (1),\n"
operator|+
literal|"(2)) AS \"t\" (\"C\")"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
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
operator|||
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|planHasSql
argument_list|(
name|jdbcSql
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3115">[CALCITE-3115]    * Cannot add JdbcRules which have different JdbcConvention    * to same VolcanoPlanner's RuleSet</a>. */
annotation|@
name|Test
name|void
name|testUnionPlan2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_SCOTT_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_name\" from \"foodmart\".\"store\" where \"store_id\"< 10\n"
operator|+
literal|"union all\n"
operator|+
literal|"select ename from SCOTT.emp where empno> 10"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableUnion(all=[true])\n"
operator|+
literal|"  JdbcToEnumerableConverter\n"
operator|+
literal|"    JdbcProject(store_name=[$3])\n"
operator|+
literal|"      JdbcFilter(condition=[<($0, 10)])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, store]])\n"
operator|+
literal|"  JdbcToEnumerableConverter\n"
operator|+
literal|"    JdbcProject(ENAME=[$1])\n"
operator|+
literal|"      JdbcFilter(condition=[>($0, 10)])\n"
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
literal|"SELECT \"store_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"store\"\n"
operator|+
literal|"WHERE \"store_id\"< 10"
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"ENAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\"\n"
operator|+
literal|"WHERE \"EMPNO\"> 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
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
name|void
name|testInPlan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
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
literal|"WHERE \"store_name\" IN ('Store 1', 'Store 10', 'Store 11',"
operator|+
literal|" 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')"
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
literal|"select empno, ename, e.deptno, dname\n"
operator|+
literal|"from scott.emp e inner join scott.dept d\n"
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
annotation|@
name|Test
name|void
name|testPushDownSort
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
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TOPDOWN_OPT
operator|.
name|camelName
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|query
argument_list|(
literal|"select ename\n"
operator|+
literal|"from scott.emp\n"
operator|+
literal|"order by empno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    JdbcProject(ENAME=[$1], EMPNO=[$0])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, EMP]])"
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
literal|"SELECT \"ENAME\", \"EMPNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\"\n"
operator|+
literal|"ORDER BY \"EMPNO\" NULLS LAST"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3751">[CALCITE-3751]    * JDBC adapter wrongly pushes ORDER BY into sub-query</a>. */
annotation|@
name|Test
name|void
name|testOrderByPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, job, sum(sal)\n"
operator|+
literal|"from \"EMP\"\n"
operator|+
literal|"group by deptno, job\n"
operator|+
literal|"order by 1, 2"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    JdbcProject(DEPTNO=[$1], JOB=[$0], EXPR$2=[$2])\n"
operator|+
literal|"      JdbcAggregate(group=[{2, 7}], EXPR$2=[SUM($5)])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])"
decl_stmt|;
specifier|final
name|String
name|sqlHsqldb
init|=
literal|"SELECT \"DEPTNO\", \"JOB\", SUM(\"SAL\")\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\"\n"
operator|+
literal|"GROUP BY \"JOB\", \"DEPTNO\"\n"
operator|+
literal|"ORDER BY \"DEPTNO\" NULLS LAST, \"JOB\" NULLS LAST"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TOPDOWN_OPT
operator|.
name|camelName
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
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
name|sqlHsqldb
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-631">[CALCITE-631]    * Push theta joins down to JDBC adapter</a>. */
annotation|@
name|Test
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
literal|"select empno, ename, grade\n"
operator|+
literal|"from scott.emp e inner join scott.salgrade s\n"
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
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", \"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN \"SCOTT\".\"SALGRADE\" "
operator|+
literal|"ON \"t\".\"SAL\"> \"SALGRADE\".\"LOSAL\" "
operator|+
literal|"AND \"t\".\"SAL\"< \"SALGRADE\".\"HISAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"select empno, ename, grade\n"
operator|+
literal|"from scott.emp e inner join scott.salgrade s\n"
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
literal|"SELECT \"t\".\"EMPNO\", \"t\".\"ENAME\", \"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"SAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN \"SCOTT\".\"SALGRADE\" ON \"t\".\"SAL\">= \"SALGRADE\".\"LOSAL\" "
operator|+
literal|"AND \"t\".\"SAL\"<= \"SALGRADE\".\"HISAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"select e.empno, e.ename, e.empno, e.ename\n"
operator|+
literal|"from scott.emp e inner join scott.emp m on\n"
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
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" "
operator|+
literal|"ON \"t\".\"MGR\" = \"t0\".\"EMPNO\" AND \"t\".\"SAL\"> \"t0\".\"SAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"select e.empno, e.ename, e.empno, e.ename\n"
operator|+
literal|"from scott.emp e inner join scott.emp m on\n"
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
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" "
operator|+
literal|"ON \"t\".\"MGR\" = \"t0\".\"EMPNO\" "
operator|+
literal|"AND (\"t\".\"SAL\"> \"t0\".\"SAL\" OR \"t\".\"HIREDATE\"< \"t0\".\"HIREDATE\")"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoin3TablesPlan
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
literal|"select  empno, ename, dname, grade\n"
operator|+
literal|"from scott.emp e inner join scott.dept d\n"
operator|+
literal|"on e.deptno = d.deptno\n"
operator|+
literal|"inner join scott.salgrade s\n"
operator|+
literal|"on e.sal> s.losal and e.sal< s.hisal"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], DNAME=[$5], GRADE=[$6])\n"
operator|+
literal|"    JdbcJoin(condition=[AND(>($2, $7),<($2, $8))], joinType=[inner])\n"
operator|+
literal|"      JdbcJoin(condition=[=($3, $4)], joinType=[inner])\n"
operator|+
literal|"        JdbcProject(EMPNO=[$0], ENAME=[$1], SAL=[$5], DEPTNO=[$7])\n"
operator|+
literal|"          JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"        JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"          JdbcTableScan(table=[[SCOTT, DEPT]])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, SALGRADE]])\n"
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
literal|"\"t0\".\"DNAME\", \"SALGRADE\".\"GRADE\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"SAL\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t0\" ON \"t\".\"DEPTNO\" = \"t0\".\"DEPTNO\"\n"
operator|+
literal|"INNER JOIN \"SCOTT\".\"SALGRADE\" "
operator|+
literal|"ON \"t\".\"SAL\"> \"SALGRADE\".\"LOSAL\" "
operator|+
literal|"AND \"t\".\"SAL\"< \"SALGRADE\".\"HISAL\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"select empno, ename, d.deptno, dname\n"
operator|+
literal|"from scott.emp e,scott.dept d\n"
operator|+
literal|"where e.deptno = d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$3], DNAME=[$4])\n"
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
literal|"\"t0\".\"DEPTNO\", \"t0\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"DEPTNO\"\nFROM \"SCOTT\".\"EMP\") AS \"t\"\n"
operator|+
literal|"INNER JOIN (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t0\" ON \"t\".\"DEPTNO\" = \"t0\".\"DEPTNO\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"select empno, ename, d.deptno, dname\n"
operator|+
literal|"from scott.emp e,scott.dept d"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"    JdbcProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, EMP]])\n"
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
literal|"select empno, ename, d.deptno, dname\n"
operator|+
literal|"from scott.emp e,scott.dept d\n"
operator|+
literal|"where e.deptno = d.deptno\n"
operator|+
literal|"and e.deptno=20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$3], DNAME=[$4])\n"
operator|+
literal|"    JdbcJoin(condition=[=($2, $3)], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$7])\n"
operator|+
literal|"        JdbcFilter(condition=[=(CAST($7):INTEGER, 20)])\n"
operator|+
literal|"          JdbcTableScan(table=[[SCOTT, EMP]])\n"
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
literal|"SELECT \"t0\".\"EMPNO\", \"t0\".\"ENAME\", "
operator|+
literal|"\"t1\".\"DEPTNO\", \"t1\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\"\n"
operator|+
literal|"WHERE CAST(\"DEPTNO\" AS INTEGER) = 20) AS \"t0\"\n"
operator|+
literal|"INNER JOIN (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t1\" ON \"t0\".\"DEPTNO\" = \"t1\".\"DEPTNO\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinConditionAlwaysTruePushDown
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
literal|"select empno, ename, d.deptno, dname\n"
operator|+
literal|"from scott.emp e,scott.dept d\n"
operator|+
literal|"where true"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"    JdbcProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"      JdbcTableScan(table=[[SCOTT, EMP]])\n"
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
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"ENAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t\",\n"
operator|+
literal|"(SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\") AS \"t0\""
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-893">[CALCITE-893]    * Theta join in JdbcAdapter</a>. */
annotation|@
name|Test
name|void
name|testJoinPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT T1.\"brand_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\" AS T1\n"
operator|+
literal|" INNER JOIN \"foodmart\".\"product_class\" AS T2\n"
operator|+
literal|" ON T1.\"product_class_id\" = T2.\"product_class_id\"\n"
operator|+
literal|"WHERE T2.\"product_department\" = 'Frozen Foods'\n"
operator|+
literal|" OR T2.\"product_department\" = 'Baking Goods'\n"
operator|+
literal|" AND T1.\"brand_name\"<> 'King'"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|275
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1372">[CALCITE-1372]    * JDBC adapter generates SQL with wrong field names</a>. */
annotation|@
name|Test
name|void
name|testJoinPlan2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT v1.deptno, v2.deptno\n"
operator|+
literal|"FROM Scott.dept v1 LEFT JOIN Scott.emp v2 ON v1.deptno = v2.deptno\n"
operator|+
literal|"WHERE v2.job LIKE 'PRESIDENT'"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinCartesian
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM Scott.dept, Scott.emp"
decl_stmt|;
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
name|sql
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|56
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinCartesianCount
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) as c\n"
operator|+
literal|"FROM Scott.dept, Scott.emp"
decl_stmt|;
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
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=56\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1382">[CALCITE-1382]    * ClassCastException in JDBC adapter</a>. */
annotation|@
name|Test
name|void
name|testJoinPlan3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) AS c FROM (\n"
operator|+
literal|"  SELECT count(emp.empno) `Count Emp`,\n"
operator|+
literal|"      dept.dname `Department Name`\n"
operator|+
literal|"  FROM emp emp\n"
operator|+
literal|"  JOIN dept dept ON emp.deptno = dept.deptno\n"
operator|+
literal|"  JOIN salgrade salgrade ON emp.comm = salgrade.hisal\n"
operator|+
literal|"  WHERE dept.dname LIKE '%A%'\n"
operator|+
literal|"  GROUP BY emp.deptno, dept.dname)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"c=1\n"
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT COUNT(*) AS \"c\"\n"
operator|+
literal|"FROM (SELECT \"t0\".\"DEPTNO\", \"t2\".\"DNAME\"\n"
operator|+
literal|"FROM (SELECT \"HISAL\"\n"
operator|+
literal|"FROM \"SCOTT\".\"SALGRADE\") AS \"t\"\n"
operator|+
literal|"INNER JOIN ((SELECT \"COMM\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"SCOTT\".\"EMP\") AS \"t0\" "
operator|+
literal|"INNER JOIN (SELECT \"DEPTNO\", \"DNAME\"\n"
operator|+
literal|"FROM \"SCOTT\".\"DEPT\"\n"
operator|+
literal|"WHERE \"DNAME\" LIKE '%A%') AS \"t2\" "
operator|+
literal|"ON \"t0\".\"DEPTNO\" = \"t2\".\"DEPTNO\") "
operator|+
literal|"ON \"t\".\"HISAL\" = \"t0\".\"COMM\"\n"
operator|+
literal|"GROUP BY \"t0\".\"DEPTNO\", \"t2\".\"DNAME\") AS \"t3\""
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|JdbcTest
operator|.
name|SCOTT_MODEL
argument_list|)
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
operator|.
name|planHasSql
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-657">[CALCITE-657]    * NullPointerException when executing JdbcAggregate implement method</a>. */
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2206">[CALCITE-2206]    * JDBC adapter incorrectly pushes windowed aggregates down to HSQLDB</a>. */
annotation|@
name|Test
name|void
name|testOverNonSupportedDialect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"store_id\", \"account_id\", \"exp_date\",\n"
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\",\n"
operator|+
literal|" last_value(\"time_id\") over () as \"last_version\"\n"
operator|+
literal|"from \"expense_fact\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableWindow(window#0=[window(aggs [LAST_VALUE($3)])])\n"
operator|+
literal|"  JdbcToEnumerableConverter\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, expense_fact]])\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTablesNoCatalogSchema
parameter_list|()
block|{
comment|// Switch from "FOODMART" user, whose default schema is 'foodmart',
comment|// to "sa", whose default schema is the root, and therefore cannot
comment|// see the table unless directed to look in a particular schema.
specifier|final
name|String
name|model
init|=
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
operator|.
name|replace
argument_list|(
literal|"jdbcUser: 'FOODMART'"
argument_list|,
literal|"jdbcUser: 'sa'"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"jdbcPassword: 'FOODMART'"
argument_list|,
literal|"jdbcPassword: ''"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"jdbcCatalog: 'foodmart'"
argument_list|,
literal|"jdbcCatalog: null"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"jdbcSchema: 'foodmart'"
argument_list|,
literal|"jdbcSchema: null"
argument_list|)
decl_stmt|;
comment|// Since Calcite uses PostgreSQL JDBC driver version>= 4.1,
comment|// catalog/schema can be retrieved from JDBC connection and
comment|// this test succeeds
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
comment|// Calcite uses PostgreSQL JDBC driver version>= 4.1
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over ()"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
comment|// Since Calcite uses HSQLDB JDBC driver version< 4.1,
comment|// catalog/schema cannot be retrieved from JDBC connection and
comment|// this test fails
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over ()"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"'expense_fact' not found"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1506">[CALCITE-1506]    * Push OVER Clause to underlying SQL via JDBC adapter</a>.    *    *<p>Test runs only on Postgres; the default database, Hsqldb, does not    * support OVER. */
annotation|@
name|Test
name|void
name|testOverDefault
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over ()"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2], "
operator|+
literal|"time_id=[$3], category_id=[$4], currency_id=[$5], amount=[$6],"
operator|+
literal|" last_version=[LAST_VALUE($3) OVER (RANGE BETWEEN UNBOUNDED"
operator|+
literal|" PRECEDING AND UNBOUNDED FOLLOWING)])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, expense_fact]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" LAST_VALUE(\"time_id\") OVER (RANGE BETWEEN UNBOUNDED"
operator|+
literal|" PRECEDING AND UNBOUNDED FOLLOWING) AS \"last_version\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2305">[CALCITE-2305]    * JDBC adapter generates invalid casts on PostgreSQL, because PostgreSQL does    * not have TINYINT and DOUBLE types</a>. */
annotation|@
name|Test
name|void
name|testCast
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cast(\"store_id\" as TINYINT),"
operator|+
literal|"cast(\"store_id\" as DOUBLE)"
operator|+
literal|" from \"expense_fact\""
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT CAST(\"store_id\" AS SMALLINT),"
operator|+
literal|" CAST(\"store_id\" AS DOUBLE PRECISION)\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverRowsBetweenBoundFollowingAndFollowing
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over (partition by \"account_id\""
operator|+
literal|" order by \"time_id\" rows between 1 following and 10 following)"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2], "
operator|+
literal|"time_id=[$3], category_id=[$4], currency_id=[$5], amount=[$6],"
operator|+
literal|" last_version=[LAST_VALUE($3) OVER (PARTITION BY $1"
operator|+
literal|" ORDER BY $3 ROWS BETWEEN 1 FOLLOWING AND 10 FOLLOWING)])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, expense_fact]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" LAST_VALUE(\"time_id\") OVER (PARTITION BY \"account_id\""
operator|+
literal|" ORDER BY \"time_id\" ROWS BETWEEN 1 FOLLOWING"
operator|+
literal|" AND 10 FOLLOWING) AS \"last_version\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverRowsBetweenBoundPrecedingAndCurrent
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over (partition by \"account_id\""
operator|+
literal|" order by \"time_id\" rows between 3 preceding and current row)"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2], "
operator|+
literal|"time_id=[$3], category_id=[$4], currency_id=[$5], amount=[$6],"
operator|+
literal|" last_version=[LAST_VALUE($3) OVER (PARTITION BY $1"
operator|+
literal|" ORDER BY $3 ROWS BETWEEN 3 PRECEDING AND CURRENT ROW)])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, expense_fact]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" LAST_VALUE(\"time_id\") OVER (PARTITION BY \"account_id\""
operator|+
literal|" ORDER BY \"time_id\" ROWS BETWEEN 3 PRECEDING"
operator|+
literal|" AND CURRENT ROW) AS \"last_version\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverDisallowPartial
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over (partition by \"account_id\""
operator|+
literal|" order by \"time_id\" rows 3 preceding disallow partial)"
operator|+
literal|" as \"last_version\" from \"expense_fact\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2],"
operator|+
literal|" time_id=[$3], category_id=[$4], currency_id=[$5],"
operator|+
literal|" amount=[$6], last_version=[CASE(>=(COUNT() OVER"
operator|+
literal|" (PARTITION BY $1 ORDER BY $3 ROWS BETWEEN 3 PRECEDING AND"
operator|+
literal|" CURRENT ROW), 2), LAST_VALUE($3) OVER (PARTITION BY $1"
operator|+
literal|" ORDER BY $3 ROWS BETWEEN 3 PRECEDING AND CURRENT ROW),"
operator|+
literal|" null)])\n    JdbcTableScan(table=[[foodmart,"
operator|+
literal|" expense_fact]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" CASE WHEN (COUNT(*) OVER (PARTITION BY \"account_id\""
operator|+
literal|" ORDER BY \"time_id\" ROWS BETWEEN 3 PRECEDING"
operator|+
literal|" AND CURRENT ROW))>= 2 THEN LAST_VALUE(\"time_id\")"
operator|+
literal|" OVER (PARTITION BY \"account_id\" ORDER BY \"time_id\""
operator|+
literal|" ROWS BETWEEN 3 PRECEDING AND CURRENT ROW)"
operator|+
literal|" ELSE NULL END AS \"last_version\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLastValueOver
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
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
name|POSTGRESQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" last_value(\"time_id\") over (partition by \"account_id\""
operator|+
literal|" order by \"time_id\") as \"last_version\""
operator|+
literal|" from \"expense_fact\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2],"
operator|+
literal|" time_id=[$3], category_id=[$4], currency_id=[$5], amount=[$6],"
operator|+
literal|" last_version=[LAST_VALUE($3) OVER (PARTITION BY $1 ORDER BY $3"
operator|+
literal|" RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, expense_fact]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\", \"category_id\", \"currency_id\", \"amount\","
operator|+
literal|" LAST_VALUE(\"time_id\") OVER (PARTITION BY \"account_id\""
operator|+
literal|" ORDER BY \"time_id\" RANGE BETWEEN UNBOUNDED PRECEDING AND"
operator|+
literal|" CURRENT ROW) AS \"last_version\""
operator|+
literal|"\nFROM \"foodmart\".\"expense_fact\""
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-259">[CALCITE-259]    * Using sub-queries in CASE statement against JDBC tables generates invalid    * Oracle SQL</a>. */
annotation|@
name|Test
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
literal|"Sub"
operator|+
literal|"query returns more than 1 row"
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
name|FoodmartSchema
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-865">[CALCITE-865]    * Unknown table type causes NullPointerException in JdbcSchema</a>. The issue    * occurred because of the "SYSTEM_INDEX" table type when run against    * PostgreSQL. */
annotation|@
name|Test
name|void
name|testMetadataTables
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The troublesome tables occur in PostgreSQL's system schema.
specifier|final
name|String
name|model
init|=
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
operator|.
name|replace
argument_list|(
literal|"jdbcSchema: 'foodmart'"
argument_list|,
literal|"jdbcSchema: null"
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|ResultSet
name|resultSet
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
literal|"%"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
operator|.
name|isEmpty
argument_list|()
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
block|}
annotation|@
name|Test
name|void
name|testMetadataFunctions
parameter_list|()
block|{
specifier|final
name|String
name|model
init|=
literal|""
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_STR',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyToStringFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'FIBONACCI_TABLE',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           methodName: 'fibonacciTable'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       materializations: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           table: 'TEST_VIEW',\n"
operator|+
literal|"           sql: 'SELECT 1'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
operator|.
name|metaData
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
return|return
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getFunctions
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|"%"
argument_list|)
return|;
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
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"FUNCTION_CAT=null; FUNCTION_SCHEM=adhoc; FUNCTION_NAME=FIBONACCI_TABLE; REMARKS=null; FUNCTION_TYPE=0; SPECIFIC_NAME=FIBONACCI_TABLE\n"
operator|+
literal|"FUNCTION_CAT=null; FUNCTION_SCHEM=adhoc; FUNCTION_NAME=MY_STR; REMARKS=null; FUNCTION_TYPE=0; SPECIFIC_NAME=MY_STR\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-666">[CALCITE-666]    * Anti-semi-joins against JDBC adapter give wrong results</a>. */
annotation|@
name|Test
name|void
name|testScalarSubQuery
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
literal|"SELECT COUNT(empno) AS cEmpNo FROM \"SCOTT\".\"EMP\" "
operator|+
literal|"WHERE DEPTNO<> (SELECT * FROM (VALUES 1))"
argument_list|)
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
name|returns
argument_list|(
literal|"CEMPNO=14\n"
argument_list|)
expr_stmt|;
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
literal|"SELECT ename FROM \"SCOTT\".\"EMP\" "
operator|+
literal|"WHERE DEPTNO = (SELECT deptno FROM \"SCOTT\".\"DEPT\" "
operator|+
literal|"WHERE dname = 'ACCOUNTING')"
argument_list|)
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
name|returns
argument_list|(
literal|"ENAME=CLARK\nENAME=KING\nENAME=MILLER\n"
argument_list|)
expr_stmt|;
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
literal|"SELECT COUNT(ename) AS cEname FROM \"SCOTT\".\"EMP\" "
operator|+
literal|"WHERE DEPTNO> (SELECT deptno FROM \"SCOTT\".\"DEPT\" "
operator|+
literal|"WHERE dname = 'ACCOUNTING')"
argument_list|)
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
name|returns
argument_list|(
literal|"CENAME=11\n"
argument_list|)
expr_stmt|;
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
literal|"SELECT COUNT(ename) AS cEname FROM \"SCOTT\".\"EMP\" "
operator|+
literal|"WHERE DEPTNO< (SELECT deptno FROM \"SCOTT\".\"DEPT\" "
operator|+
literal|"WHERE dname = 'ACCOUNTING')"
argument_list|)
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
name|returns
argument_list|(
literal|"CENAME=0\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Acquires an exclusive connection to a test database, and cleans it.    *    *<p>Cleans any previous TableModify states and creates    * one expense_fact instance with store_id = 666.    *    *<p>Caller must close the returned wrapper, so that the next test can    * acquire the lock and use the database.    *    * @param c JDBC connection    */
specifier|private
name|LockWrapper
name|exclusiveCleanDb
parameter_list|(
name|Connection
name|c
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|LockWrapper
name|wrapper
init|=
name|LockWrapper
operator|.
name|lock
argument_list|(
name|LOCK
argument_list|)
decl_stmt|;
try|try
init|(
name|Statement
name|statement
init|=
name|c
operator|.
name|createStatement
argument_list|()
init|)
block|{
specifier|final
name|String
name|dSql
init|=
literal|"DELETE FROM \"foodmart\".\"expense_fact\""
operator|+
literal|" WHERE \"store_id\"=666\n"
decl_stmt|;
specifier|final
name|String
name|iSql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\"(\n"
operator|+
literal|" \"store_id\", \"account_id\", \"exp_date\", \"time_id\","
operator|+
literal|" \"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|" VALUES (666, 666, TIMESTAMP '1997-01-01 00:00:00',"
operator|+
literal|" 666, '666', 666, 666)"
decl_stmt|;
name|statement
operator|.
name|executeUpdate
argument_list|(
name|dSql
argument_list|)
expr_stmt|;
name|int
name|rowCount
init|=
name|statement
operator|.
name|executeUpdate
argument_list|(
name|iSql
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rowCount
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
decl||
name|RuntimeException
decl||
name|Error
name|e
parameter_list|)
block|{
name|wrapper
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1527">[CALCITE-1527]    * Support DML in the JDBC adapter</a>. */
annotation|@
name|Test
name|void
name|testTableModifyInsert
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\"(\n"
operator|+
literal|" \"store_id\", \"account_id\", \"exp_date\", \"time_id\","
operator|+
literal|" \"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|"VALUES (666, 666, TIMESTAMP '1997-01-01 00:00:00',"
operator|+
literal|" 666, '666', 666, 666)"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcTableModify(table=[[foodmart, expense_fact]], "
operator|+
literal|"operation=[INSERT], flattened=[false])\n"
operator|+
literal|"    JdbcValues(tuples=[[{ 666, 666, 1997-01-01 00:00:00, 666, "
operator|+
literal|"'666', 666, 666 }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\" (\"store_id\", "
operator|+
literal|"\"account_id\", \"exp_date\", \"time_id\", \"category_id\", \"currency_id\", "
operator|+
literal|"\"amount\")\n"
operator|+
literal|"VALUES (666, 666, TIMESTAMP '1997-01-01 00:00:00', 666, '666', "
operator|+
literal|"666, 666)"
decl_stmt|;
specifier|final
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
operator|||
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
init|(
name|LockWrapper
name|ignore
init|=
name|exclusiveCleanDb
argument_list|(
name|connection
argument_list|)
init|)
block|{
name|that
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|planUpdateHasSql
argument_list|(
name|jdbcSql
argument_list|,
literal|1
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
block|}
annotation|@
name|Test
name|void
name|testTableModifyInsertMultiValues
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\"(\n"
operator|+
literal|" \"store_id\", \"account_id\", \"exp_date\", \"time_id\","
operator|+
literal|" \"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|"VALUES (666, 666, TIMESTAMP '1997-01-01 00:00:00',"
operator|+
literal|"   666, '666', 666, 666),\n"
operator|+
literal|" (666, 777, TIMESTAMP '1997-01-01 00:00:00',"
operator|+
literal|"   666, '666', 666, 666)"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcTableModify(table=[[foodmart, expense_fact]], "
operator|+
literal|"operation=[INSERT], flattened=[false])\n"
operator|+
literal|"    JdbcValues(tuples=[["
operator|+
literal|"{ 666, 666, 1997-01-01 00:00:00, 666, '666', 666, 666 }, "
operator|+
literal|"{ 666, 777, 1997-01-01 00:00:00, 666, '666', 666, 666 }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\""
operator|+
literal|" (\"store_id\", \"account_id\", \"exp_date\", \"time_id\", "
operator|+
literal|"\"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|"VALUES "
operator|+
literal|"(666, 666, TIMESTAMP '1997-01-01 00:00:00', 666, '666', 666, 666),\n"
operator|+
literal|"(666, 777, TIMESTAMP '1997-01-01 00:00:00', 666, '666', 666, 666)"
decl_stmt|;
specifier|final
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
operator|||
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
init|(
name|LockWrapper
name|ignore
init|=
name|exclusiveCleanDb
argument_list|(
name|connection
argument_list|)
init|)
block|{
name|that
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|planUpdateHasSql
argument_list|(
name|jdbcSql
argument_list|,
literal|2
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
block|}
annotation|@
name|Test
name|void
name|testTableModifyInsertWithSubQuery
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
decl_stmt|;
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
init|(
name|LockWrapper
name|ignore
init|=
name|exclusiveCleanDb
argument_list|(
name|connection
argument_list|)
init|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\"(\n"
operator|+
literal|" \"store_id\", \"account_id\", \"exp_date\", \"time_id\","
operator|+
literal|" \"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|"SELECT  \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\" + 1, \"category_id\", \"currency_id\","
operator|+
literal|" \"amount\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\"\n"
operator|+
literal|"WHERE \"store_id\" = 666"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcTableModify(table=[[foodmart, expense_fact]], operation=[INSERT], flattened=[false])\n"
operator|+
literal|"    JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2], time_id=[+($3, 1)], category_id=[$4], currency_id=[$5], amount=[$6])\n"
operator|+
literal|"      JdbcFilter(condition=[=($0, 666)])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, expense_fact]])\n"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"INSERT INTO \"foodmart\".\"expense_fact\""
operator|+
literal|" (\"store_id\", \"account_id\", \"exp_date\", \"time_id\","
operator|+
literal|" \"category_id\", \"currency_id\", \"amount\")\n"
operator|+
literal|"SELECT \"store_id\", \"account_id\", \"exp_date\","
operator|+
literal|" \"time_id\" + 1 AS \"time_id\", \"category_id\","
operator|+
literal|" \"currency_id\", \"amount\"\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\"\n"
operator|+
literal|"WHERE \"store_id\" = 666"
decl_stmt|;
name|that
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|planUpdateHasSql
argument_list|(
name|jdbcSql
argument_list|,
literal|1
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
block|}
annotation|@
name|Test
name|void
name|testTableModifyUpdate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
decl_stmt|;
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
init|(
name|LockWrapper
name|ignore
init|=
name|exclusiveCleanDb
argument_list|(
name|connection
argument_list|)
init|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"UPDATE \"foodmart\".\"expense_fact\"\n"
operator|+
literal|" SET \"account_id\"=888\n"
operator|+
literal|" WHERE \"store_id\"=666\n"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcTableModify(table=[[foodmart, expense_fact]], operation=[UPDATE], updateColumnList=[[account_id]], sourceExpressionList=[[888]], flattened=[false])\n"
operator|+
literal|"    JdbcProject(store_id=[$0], account_id=[$1], exp_date=[$2], time_id=[$3], category_id=[$4], currency_id=[$5], amount=[$6], EXPR$0=[888])\n"
operator|+
literal|"      JdbcFilter(condition=[=($0, 666)])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, expense_fact]])"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"UPDATE \"foodmart\".\"expense_fact\""
operator|+
literal|" SET \"account_id\" = 888\n"
operator|+
literal|"WHERE \"store_id\" = 666"
decl_stmt|;
name|that
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|planUpdateHasSql
argument_list|(
name|jdbcSql
argument_list|,
literal|1
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
block|}
annotation|@
name|Test
name|void
name|testTableModifyDelete
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|DatabaseInstance
operator|.
name|HSQLDB
argument_list|)
decl_stmt|;
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
init|(
name|LockWrapper
name|ignore
init|=
name|exclusiveCleanDb
argument_list|(
name|connection
argument_list|)
init|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"DELETE FROM \"foodmart\".\"expense_fact\"\n"
operator|+
literal|"WHERE \"store_id\"=666\n"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcTableModify(table=[[foodmart, expense_fact]], operation=[DELETE], flattened=[false])\n"
operator|+
literal|"    JdbcFilter(condition=[=($0, 666)])\n"
operator|+
literal|"      JdbcTableScan(table=[[foodmart, expense_fact]]"
decl_stmt|;
specifier|final
name|String
name|jdbcSql
init|=
literal|"DELETE FROM \"foodmart\".\"expense_fact\"\n"
operator|+
literal|"WHERE \"store_id\" = 666"
decl_stmt|;
name|that
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|planUpdateHasSql
argument_list|(
name|jdbcSql
argument_list|,
literal|1
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
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1572">[CALCITE-1572]    * JdbcSchema throws exception when detecting nullable columns</a>. */
annotation|@
name|Test
name|void
name|testColumnNullability
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"employee_id\", \"position_id\"\n"
operator|+
literal|"from \"foodmart\".\"employee\" limit 10"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|10
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[employee_id INTEGER NOT NULL, position_id INTEGER]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|pushBindParameters
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, ename from emp where empno = ?"
decl_stmt|;
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
name|sql
argument_list|)
operator|.
name|consumesPreparedStatement
argument_list|(
name|p
lambda|->
name|p
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|7566
argument_list|)
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"EMPNO\", \"ENAME\"\nFROM \"SCOTT\".\"EMP\"\nWHERE \"EMPNO\" = ?"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4619">[CALCITE-4619]    * "Full join" generates an incorrect execution plan under mysql</a>. */
annotation|@
name|Test
name|void
name|testFullJoinNonSupportedDialect
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
name|H2
operator|||
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, ename, e.deptno, dname\n"
operator|+
literal|"from scott.emp e full join scott.dept d\n"
operator|+
literal|"on e.deptno = d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}],"
operator|+
literal|" DNAME=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($2, $3)], joinType=[full])\n"
operator|+
literal|"    JdbcToEnumerableConverter\n"
operator|+
literal|"      JdbcProject(EMPNO=[$0], ENAME=[$1], DEPTNO=[$7])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, EMP]])\n"
operator|+
literal|"    JdbcToEnumerableConverter\n"
operator|+
literal|"      JdbcProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        JdbcTableScan(table=[[SCOTT, DEPT]])"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5243">[CALCITE-5243]    * "SELECT NULL AS C causes NoSuchMethodException: java.sql.ResultSet.getVoid(int)</a>. */
annotation|@
name|Test
name|void
name|testNullSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select NULL AS C from \"days\""
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FoodmartSchema
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|7
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=null\nC=null\nC=null\nC=null\nC=null\nC=null\nC=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Acquires a lock, and releases it when closed. */
specifier|static
class|class
name|LockWrapper
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
name|LockWrapper
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
comment|/** Acquires a lock and returns a closeable wrapper. */
specifier|static
name|LockWrapper
name|lock
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
operator|new
name|LockWrapper
argument_list|(
name|lock
argument_list|)
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

