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
name|plan
operator|.
name|RelOptUtil
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
name|prepare
operator|.
name|Prepare
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
name|RelNode
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
name|RelRoot
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
name|RelVisitor
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
name|core
operator|.
name|CorrelationId
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
name|externalize
operator|.
name|RelXmlWriter
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
name|sql
operator|.
name|SqlExplainLevel
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
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
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
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
name|sql
operator|.
name|validate
operator|.
name|SqlConformanceEnum
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Bug
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
name|Litmus
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.apache.calcite.sql2rel.SqlToRelConverter}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlToRelConverterTest
extends|extends
name|SqlToRelTestBase
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlToRelConverterTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|SqlToRelConverterTest
operator|.
name|class
argument_list|)
return|;
block|}
comment|/** Sets the SQL statement for a test. */
specifier|public
specifier|final
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|tester
argument_list|,
literal|false
argument_list|,
name|SqlToRelConverter
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
specifier|protected
specifier|final
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|plan
parameter_list|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|convertsTo
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntegerLiteral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntervalLiteralYearToMonth
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  cast(empno as Integer) * (INTERVAL '1-1' YEAR TO MONTH)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntervalLiteralHourToMinute
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" cast(empno as Integer) * (INTERVAL '1:1' HOUR TO MINUTE)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAliasList
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select a + b from (\n"
operator|+
literal|"  select deptno, 1 as uno, name from dept\n"
operator|+
literal|") as d(a, b, c)\n"
operator|+
literal|"where c like 'X%'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAliasList2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|"  select a, b, c from (values (1, 2, 3)) as t (c, b, a)\n"
operator|+
literal|") join dept on dept.deptno = c\n"
operator|+
literal|"order by c + a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that AND(x, AND(y, z)) gets flattened to AND(x, y, z).    */
annotation|@
name|Test
specifier|public
name|void
name|testMultiAnd
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where deptno< 10\n"
operator|+
literal|"and deptno> 5\n"
operator|+
literal|"and (deptno = 8 or empno< 100)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"JOIN dept on emp.deptno = dept.deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-245">[CALCITE-245]    * Off-by-one translation of ON clause of JOIN</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testConditionOffByOne
parameter_list|()
block|{
comment|// Bug causes the plan to contain
comment|//   LogicalJoin(condition=[=($9, $9)], joinType=[inner])
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"JOIN dept on emp.deptno + 0 = dept.deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConditionOffByOneReversed
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"JOIN dept on dept.deptno = emp.deptno + 0"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"JOIN dept on emp.deptno + 1 = dept.deptno - 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp join dept\n"
operator|+
literal|" on emp.deptno = dept.deptno and emp.empno in (1, 3)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnInSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp left join dept\n"
operator|+
literal|"on emp.empno = 1\n"
operator|+
literal|"or dept.deptno in (select deptno from emp where empno> 5)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp left join dept\n"
operator|+
literal|"on emp.empno = 1\n"
operator|+
literal|"or exists (select deptno from emp where empno> dept.deptno + 5)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM emp JOIN dept USING (deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-74">[CALCITE-74]    * JOIN ... USING fails in 3-way join with    * UnsupportedOperationException</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsingThreeWay
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"join emp as e2 using (empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsingCompound
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp LEFT JOIN ("
operator|+
literal|"SELECT *, deptno * 5 as empno FROM dept) "
operator|+
literal|"USING (deptno,empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-801">[CALCITE-801]    * NullPointerException using USING on table alias with column    * aliases</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testValuesUsing
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, min(e.empid) as empid\n"
operator|+
literal|"from (values (100, 'Bill', 1)) as e(empid, name, deptno)\n"
operator|+
literal|"join (values (1, 'LeaderShip')) as d(deptno, name)\n"
operator|+
literal|"  using (deptno)\n"
operator|+
literal|"group by d.deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNatural
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM emp NATURAL JOIN dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNaturalNoCommonColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM emp NATURAL JOIN (SELECT deptno AS foo, name FROM dept) AS d"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNaturalMultipleCommonColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"NATURAL JOIN (SELECT deptno, name AS ename FROM dept) AS d"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinWithUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select grade\n"
operator|+
literal|"from (select empno from emp union select deptno from dept),\n"
operator|+
literal|"  salgrade"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno as d from emp group by d"
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAliasOfSubExpressionsInProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno+empno as d, deptno+empno+mgr\n"
operator|+
literal|"from emp group by d,mgr"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAliasEqualToColumnName
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno, ename as deptno from emp group by empno, deptno"
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOrdinal
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno from emp group by 1"
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByContainsLiterals
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) from (\n"
operator|+
literal|"  select 1 from emp group by substring(ename from 2 for 3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAliasInHaving
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(empno) as e from emp having e> 1"
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupJustOneAgg
parameter_list|()
block|{
comment|// just one agg
specifier|final
name|String
name|sql
init|=
literal|"select deptno, sum(sal) as sum_sal from emp group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupExpressionsInsideAndOut
parameter_list|()
block|{
comment|// Expressions inside and outside aggs. Common sub-expressions should be
comment|// eliminated: 'sal' always translates to expression #2.
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  deptno + 4, sum(sal), sum(3 + sal), 2 * count(sal)\n"
operator|+
literal|"from emp group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateNoGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupEmpty
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(deptno) from emp group by ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|// Same effect as writing "GROUP BY deptno"
annotation|@
name|Test
specifier|public
name|void
name|testSingletonGroupingSet
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(sal) from emp group by grouping sets (deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSets
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ((deptno), (ename, deptno))\n"
operator|+
literal|"order by 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSetsProduct
parameter_list|()
block|{
comment|// Example in SQL:2011:
comment|//   GROUP BY GROUPING SETS ((A, B), (C)), GROUPING SETS ((X, Y), ())
comment|// is transformed to
comment|//   GROUP BY GROUPING SETS ((A, B, X, Y), (A, B), (C, X, Y), (C))
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (0, 1, 2, 3, 4)) as t(a, b, c, x, y)\n"
operator|+
literal|"group by grouping sets ((a, b), c), grouping sets ((x, y), ())"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** When the GROUPING function occurs with GROUP BY (effectively just one    * grouping set), we can translate it directly to 1. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupingFunctionWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  deptno, grouping(deptno), count(*), grouping(empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by empno, deptno\n"
operator|+
literal|"order by 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingFunction
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  deptno, grouping(deptno), count(*), grouping(empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(empno, deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * GROUP BY with duplicates.    *    *<p>From SQL spec:    *<blockquote>NOTE 190&mdash; That is, a simple<em>group by clause</em>    * that is not primitive may be transformed into a primitive<em>group by    * clause</em> by deleting all parentheses, and deleting extra commas as    * necessary for correct syntax. If there are no grouping columns at all (for    * example, GROUP BY (), ()), this is transformed to the canonical form GROUP    * BY ().    *</blockquote> */
comment|// Same effect as writing "GROUP BY ()"
annotation|@
name|Test
specifier|public
name|void
name|testGroupByWithDuplicates
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(sal) from emp group by (), ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** GROUP BY with duplicate (and heavily nested) GROUPING SETS. */
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateGroupingSets
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) from emp\n"
operator|+
literal|"group by sal,\n"
operator|+
literal|"  grouping sets (deptno,\n"
operator|+
literal|"    grouping sets ((deptno, ename), ename),\n"
operator|+
literal|"      (ename)),\n"
operator|+
literal|"  ()"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSetsCartesianProduct
parameter_list|()
block|{
comment|// Equivalent to (a, c), (a, d), (b, c), (b, d)
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by grouping sets (a, b), grouping sets (c, d)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSetsCartesianProduct2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by grouping sets (a, (a, b)), grouping sets (c), d"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollupSimple
parameter_list|()
block|{
comment|// a is nullable so is translated as just "a"
comment|// b is not null, so is represented as 0 inside Aggregate, then
comment|// using "CASE WHEN i$b THEN NULL ELSE b END"
specifier|final
name|String
name|sql
init|=
literal|"select a, b, count(*) as c\n"
operator|+
literal|"from (values (cast(null as integer), 2)) as t(a, b)\n"
operator|+
literal|"group by rollup(a, b)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollup
parameter_list|()
block|{
comment|// Equivalent to {(a, b), (a), ()}  * {(c, d), (c), ()}
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by rollup(a, b), rollup(c, d)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollupTuples
parameter_list|()
block|{
comment|// rollup(b, (a, d)) is (b, a, d), (b), ()
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by rollup(b, (a, d))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCube
parameter_list|()
block|{
comment|// cube(a, b) is {(a, b), (a), (b), ()}
specifier|final
name|String
name|sql
init|=
literal|"select 1\n"
operator|+
literal|"from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by cube(a, b)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSetsWith
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with t(a, b, c, d) as (values (1, 2, 3, 4))\n"
operator|+
literal|"select 1 from t\n"
operator|+
literal|"group by rollup(a, b), rollup(c, d)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHaving
parameter_list|()
block|{
comment|// empty group-by clause, having
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal + sal) from emp having sum(sal)> 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupBug281
parameter_list|()
block|{
comment|// Dtbug 281 gives:
comment|//   Internal error:
comment|//   Type 'RecordType(VARCHAR(128) $f0)' has no field 'NAME'
specifier|final
name|String
name|sql
init|=
literal|"select name from (select name from dept group by name)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupBug281b
parameter_list|()
block|{
comment|// Try to confuse it with spurious columns.
specifier|final
name|String
name|sql
init|=
literal|"select name, foo from (\n"
operator|+
literal|"select deptno, name, count(deptno) as foo\n"
operator|+
literal|"from dept\n"
operator|+
literal|"group by name, deptno, name)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByExpression
parameter_list|()
block|{
comment|// This used to cause an infinite loop,
comment|// SqlValidatorImpl.getValidatedNodeType
comment|// calling getValidatedNodeTypeIfKnown
comment|// calling getValidatedNodeType.
specifier|final
name|String
name|sql
init|=
literal|"select count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by substring(ename FROM 1 FOR 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, sum(sal), sum(distinct sal), count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  deptno, sum(sal * 2) filter (where empno< 10), count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFakeStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM (VALUES (0, 0)) AS T(A, \"*\")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select distinct sal + 5 from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-476">[CALCITE-476]    * DISTINCT flag in windowed aggregates</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectOverDistinct
parameter_list|()
block|{
comment|// Checks to see if<aggregate>(DISTINCT x) is set and preserved
comment|// as a flag for the aggregate call.
specifier|final
name|String
name|sql
init|=
literal|"select SUM(DISTINCT deptno)\n"
operator|+
literal|"over (ROWS BETWEEN 10 PRECEDING AND CURRENT ROW)\n"
operator|+
literal|"from emp\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testSelectOverDistinct()} but for streaming queries. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectStreamPartitionDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream\n"
operator|+
literal|"  count(distinct orderId) over (partition by productId\n"
operator|+
literal|"    order by rowtime\n"
operator|+
literal|"    range interval '1' second preceding) as c,\n"
operator|+
literal|"  count(distinct orderId) over w as c2,\n"
operator|+
literal|"  count(orderId) over w as c3\n"
operator|+
literal|"from orders\n"
operator|+
literal|"window w as (partition by productId)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinctGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select distinct sum(sal) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that if the clause of SELECT DISTINCT contains duplicate    * expressions, they are only aggregated once.    */
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinctDup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct sal + 5, deptno, sal + 5 from emp where deptno< 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithoutFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 2+2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Tests referencing columns from a sub-query that has duplicate column    * names. I think the standard says that this is illegal. We roll with it,    * and rename the second column to "e0". */
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateColumnsInSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"e\" from (\n"
operator|+
literal|"select empno as \"e\", deptno as d, 1 as \"e\" from EMP)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrder
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp order by empno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderDescNullsLast
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp order by empno desc nulls last"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOrdinalDesc
parameter_list|()
block|{
comment|// FRG-98
if|if
condition|(
operator|!
name|tester
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByOrdinal
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1, deptno, empno from emp order by 2 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ordinals rounded down, so 2.5 should have same effect as 2, and
comment|// generate identical plan
specifier|final
name|String
name|sql2
init|=
literal|"select empno + 1, deptno, empno from emp order by 2.5 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderDistinct
parameter_list|()
block|{
comment|// The relexp aggregates by 3 expressions - the 2 select expressions
comment|// plus the one to sort on. A little inefficient, but acceptable.
specifier|final
name|String
name|sql
init|=
literal|"select distinct empno, deptno + 1\n"
operator|+
literal|"from emp order by deptno + 1 + empno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByNegativeOrdinal
parameter_list|()
block|{
comment|// Regardless of whether sort-by-ordinals is enabled, negative ordinals
comment|// are treated like ordinary numbers.
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1, deptno, empno from emp order by -1 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOrdinalInExpr
parameter_list|()
block|{
comment|// Regardless of whether sort-by-ordinals is enabled, ordinals
comment|// inside expressions are treated like integers.
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1, deptno, empno from emp order by 1 + 2 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByIdenticalExpr
parameter_list|()
block|{
comment|// Expression in ORDER BY clause is identical to expression in SELECT
comment|// clause, so plan should not need an extra project.
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 from emp order by deptno asc, empno + 1 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as x, empno - 2 as y from emp order by y"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAliasInExpr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as x, empno - 2 as y\n"
operator|+
literal|"from emp order by y + 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAliasOverrides
parameter_list|()
block|{
if|if
condition|(
operator|!
name|tester
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByAlias
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// plan should contain '(empno + 1) + 3'
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as empno, empno - 2 as y\n"
operator|+
literal|"from emp order by empno + 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAliasDoesNotOverride
parameter_list|()
block|{
if|if
condition|(
name|tester
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByAlias
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// plan should contain 'empno + 3', not '(empno + 1) + 3'
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as empno, empno - 2 as y\n"
operator|+
literal|"from emp order by empno + 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderBySameExpr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp, dept\n"
operator|+
literal|"order by sal + empno desc, sal * empno, sal + empno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, sal from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"select deptno, deptno from dept\n"
operator|+
literal|"order by sal desc, empno asc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnionOrdinal
parameter_list|()
block|{
if|if
condition|(
operator|!
name|tester
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByOrdinal
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|sql
init|=
literal|"select empno, sal from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"select deptno, deptno from dept\n"
operator|+
literal|"order by 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnionExprs
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, sal from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"select deptno, deptno from dept\n"
operator|+
literal|"order by empno * sal + 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderOffsetFetch
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp\n"
operator|+
literal|"order by empno offset 10 rows fetch next 5 rows only"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffsetFetch
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp\n"
operator|+
literal|"offset 10 rows fetch next 5 rows only"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp offset 10 rows"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFetch
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp fetch next 5 rows only"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-439">[CALCITE-439]    * SqlValidatorUtil.uniquify() may not terminate under some    * conditions</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"$f2\", max(x), max(x + 1)\n"
operator|+
literal|"from (values (1, 2)) as t(\"$f2\", x)\n"
operator|+
literal|"group by \"$f2\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderGroup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"order by deptno * sum(sal) desc, min(empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountNoGroup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*), sum(sal)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where empno> 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWith
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-309">[CALCITE-309]    * WITH ... ORDER BY query gives AssertionError</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testWithOrder
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2 order by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithUnionOrder
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select empno, deptno as x from emp)\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"order by empno + x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp where deptno> 10)\n"
operator|+
literal|"select empno from emp2 where deptno< 30\n"
operator|+
literal|"union all\n"
operator|+
literal|"select deptno from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with w(x, y) as\n"
operator|+
literal|"  (select * from dept where deptno> 10)\n"
operator|+
literal|"select x from w where x< 30 union all select deptno from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExistsRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExistsDecorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExistsDecorrelateRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideScalarSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select (\n"
operator|+
literal|" with dept2 as (select * from dept where deptno> 10)"
operator|+
literal|" select count(*) from dept2) as c\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideScalarSubQueryRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select (\n"
operator|+
literal|" with dept2 as (select * from dept where deptno> 10)"
operator|+
literal|" select count(*) from dept2) as c\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-365">[CALCITE-365]    * AssertionError while translating query with WITH and correlated    * sub-query</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testWithExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with t (a, b) as (select * from (values (1, 2)))\n"
operator|+
literal|"select * from t where exists (\n"
operator|+
literal|"  select 1 from emp where deptno = t.a)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, name from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno + deptno from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableExtend
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept extend (x varchar(5) not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableExtendSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, x from dept extend (x int)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableExtendExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno + x from dept extend (x int not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiableViewExtend
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from EMP_MODIFIABLEVIEW extend (x varchar(5) not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiableViewExtendSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, empno from EMP_MODIFIABLEVIEW extend (x varchar(5) not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiableViewExtendExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno + x from EMP_MODIFIABLEVIEW extend (x int not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (SAL int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnCaseSensitiveCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, \"sal\", HIREDATE, MGR\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (\"sal\" boolean)\n"
operator|+
literal|" where \"sal\" = true"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnExtendedCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, EXTRA\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW2\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, EXTRA\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW2 extend (EXTRA boolean)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnCaseSensitiveExtendedCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, \"extra\"\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW2 extend (\"extra\" boolean)\n"
operator|+
literal|" where \"extra\" = false"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnUnderlyingCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (COMM int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectViewExtendedColumnCaseSensitiveUnderlyingCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, \"comm\"\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (\"comm\" int)\n"
operator|+
literal|" where \"comm\" = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults(empno INTEGER NOT NULL, deptno INTEGER)"
operator|+
literal|" set deptno = 1, empno = 20, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnCaseSensitiveCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults(\"slacker\" INTEGER, deptno INTEGER)"
operator|+
literal|" set deptno = 1, \"slacker\" = 100"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableViewCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW3(empno INTEGER NOT NULL, deptno INTEGER)"
operator|+
literal|" set deptno = 20, empno = 20, ename = 'Bob'"
operator|+
literal|" where empno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableViewCaseSensitiveCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(\"slacker\" INTEGER, deptno INTEGER)"
operator|+
literal|" set deptno = 20, \"slacker\" = 100"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableViewExtendedCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(\"slacker\" INTEGER, extra BOOLEAN)"
operator|+
literal|" set deptno = 20, \"slacker\" = 100, extra = true"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableViewExtendedCaseSensitiveCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(\"extra\" INTEGER, extra BOOLEAN)"
operator|+
literal|" set deptno = 20, \"extra\" = 100, extra = true"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableViewUnderlyingCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW3(extra BOOLEAN, comm INTEGER)"
operator|+
literal|" set empno = 20, comm = true, extra = true"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectModifiableViewConstraint
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from EMP_MODIFIABLEVIEW2 where deptno = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiableViewDDLExtend
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select extra from EMP_MODIFIABLEVIEW2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplicitTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"table emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(ramp(3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTableWithLateral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept, lateral table(ramp(dept.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTableWithLateral2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept, lateral table(ramp(deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1732">[CALCITE-1732]    * IndexOutOfBoundsException when using LATERAL TABLE with more than one    * field</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTableWithLateral3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept, lateral table(DEDUP(dept.deptno, dept.name))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSample
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp tablesample substitute('DATASET1') where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample substitute('DATASET1')\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample substitute('DATASET2')\n"
operator|+
literal|"where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleBernoulli
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp tablesample bernoulli(50) where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleBernoulliQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample bernoulli(10) repeatable(1)\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample bernoulli(50) repeatable(99)\n"
operator|+
literal|"where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleSystem
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp tablesample system(50) where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleSystemQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample system(10) repeatable(1)\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample system(50) repeatable(99)\n"
operator|+
literal|"where empno> 5"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTableWithCursorParam
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from table(dedup("
operator|+
literal|"cursor(select ename from emp),"
operator|+
literal|" cursor(select name from dept), 'NAME'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnest
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from unnest(multiset[1,2])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from unnest(multiset(select*from dept))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArray
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select*from unnest(array(select*from dept))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestWithOrdinality
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from unnest(array(select*from dept)) with ordinality"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select multiset(select deptno from dept) from (values(true))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 'a',multiset[10] from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetOfColumns
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 'abc',multiset[deptno,sal] from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetOfColumnsRex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 'abc',multiset[deptno,sal] from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *,\n"
operator|+
literal|"  multiset(select * from emp where deptno=dept.deptno) as empset\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationJoinRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *,\n"
operator|+
literal|"  multiset(select * from emp where deptno=dept.deptno) as empset\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-864">[CALCITE-864]    * Correlation variable has incorrect row type if it is populated by right    * side of a Join</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelatedSubQueryInJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"where d.name = (\n"
operator|+
literal|"  select max(name)\n"
operator|+
literal|"  from dept as d2\n"
operator|+
literal|"  where d2.deptno = d.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp\n"
operator|+
literal|"where exists (select 1 from dept where deptno=55)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotExistsCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp where not exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedDecorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedDecorrelateRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno limit 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedLimitDecorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno limit 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedLimitDecorrelateRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno limit 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInValueListShort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno in (10, 20)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInValueListLong
parameter_list|()
block|{
comment|// Go over the default threshold of 20 to force a sub-query.
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno in"
operator|+
literal|" (10, 20, 30, 40, 50, 60, 70, 80, 90, 100"
operator|+
literal|", 110, 120, 130, 140, 150, 160, 170, 180, 190"
operator|+
literal|", 200, 210, 220, 230)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno in"
operator|+
literal|" (select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubQueryRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno in"
operator|+
literal|" (select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeInUncorrelatedSubQueryRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where (empno, deptno) in"
operator|+
literal|" (select deptno - 10, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno not in"
operator|+
literal|" (select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno not in"
operator|+
literal|" (select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereInCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"where e.sal in (\n"
operator|+
literal|"  select e2.sal from emp as e2 where e2.deptno> e.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubQueryInSelect
parameter_list|()
block|{
comment|// In the SELECT clause, the value of IN remains in 3-valued logic
comment|// -- it's not forced into 2-valued by the "... IS TRUE" wrapper as in the
comment|// WHERE clause -- so the translation is more complicated.
specifier|final
name|String
name|sql
init|=
literal|"select name, deptno in (\n"
operator|+
literal|"  select case when true then deptno else null end from emp)\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubQueryInSelectRex
parameter_list|()
block|{
comment|// In the SELECT clause, the value of IN remains in 3-valued logic
comment|// -- it's not forced into 2-valued by the "... IS TRUE" wrapper as in the
comment|// WHERE clause -- so the translation is more complicated.
specifier|final
name|String
name|sql
init|=
literal|"select name, deptno in (\n"
operator|+
literal|"  select case when true then deptno else null end from emp)\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubQueryInHavingRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) as s\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"having count(*)> 2\n"
operator|+
literal|"and deptno in (\n"
operator|+
literal|"  select case when true then deptno else null end from emp)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUncorrelatedScalarSubQueryInOrderRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename\n"
operator|+
literal|"from emp\n"
operator|+
literal|"order by (select case when true then deptno else null end from emp) desc,\n"
operator|+
literal|"  ename"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUncorrelatedScalarSubQueryInGroupOrderRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) as s\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"order by (select case when true then deptno else null end from emp) desc,\n"
operator|+
literal|"  count(*)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUncorrelatedScalarSubQueryInAggregateRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum((select min(deptno) from emp)) as s\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Plan should be as {@link #testInUncorrelatedSubQueryInSelect}, but with    * an extra NOT. Both queries require 3-valued logic. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select case when true then deptno else null end from dept)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select case when true then deptno else null end from dept)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Since 'deptno NOT IN (SELECT deptno FROM dept)' can not be null, we    * generate a simpler plan. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectNotNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select deptno from dept)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Since 'deptno NOT IN (SELECT mgr FROM emp)' can be null, we need a more    * complex plan, including counts of null and not-null keys. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectMayBeNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select mgr from emp)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Even though "mgr" allows nulls, we can deduce from the WHERE clause that    * it will never be null. Therefore we can generate a simpler plan. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectDeduceNotNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select mgr from emp where mgr> 5)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Similar to {@link #testNotInUncorrelatedSubQueryInSelectDeduceNotNull()},    * using {@code IS NOT NULL}. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectDeduceNotNull2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select mgr from emp where mgr is not null)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Similar to {@link #testNotInUncorrelatedSubQueryInSelectDeduceNotNull()},    * using {@code IN}. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectDeduceNotNull3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select mgr from emp where mgr in (\n"
operator|+
literal|"    select mgr from emp where deptno = 10))\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubQueryInSelectNotNullRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select deptno from dept)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from unnest(select multiset[deptno] from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestSelectRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from unnest(select multiset[deptno] from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUnnest
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from dept as d, unnest(multiset[d.deptno * 2])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUnnestRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from dept as d, unnest(multiset[d.deptno * 2])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp,\n"
operator|+
literal|"  LATERAL (select * from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateralDecorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp,\n"
operator|+
literal|" LATERAL (select * from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateralDecorrelateRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp,\n"
operator|+
literal|" LATERAL (select * from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateralDecorrelateThetaRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp,\n"
operator|+
literal|" LATERAL (select * from dept where emp.deptno< dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCorrelations
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select 2+deptno d2, 3+deptno d3 from emp) e\n"
operator|+
literal|" where exists (select 1 from (select deptno+1 d1 from dept) d\n"
operator|+
literal|" where d1=e.d2 and exists (select 2 from (select deptno+4 d4, deptno+5 d5, deptno+6 d6 from dept)\n"
operator|+
literal|" where d4=d.d1 and d5=d.d1 and d6=e.d3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCorrelationsDecorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select 2+deptno d2, 3+deptno d3 from emp) e\n"
operator|+
literal|" where exists (select 1 from (select deptno+1 d1 from dept) d\n"
operator|+
literal|" where d1=e.d2 and exists (select 2 from (select deptno+4 d4, deptno+5 d5, deptno+6 d6 from dept)\n"
operator|+
literal|" where d4=d.d1 and d5=d.d1 and d6=e.d3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCorrelationsDecorrelatedRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select 2+deptno d2, 3+deptno d3 from emp) e\n"
operator|+
literal|" where exists (select 1 from (select deptno+1 d1 from dept) d\n"
operator|+
literal|" where d1=e.d2 and exists (select 2 from (select deptno+4 d4, deptno+5 d5, deptno+6 d6 from dept)\n"
operator|+
literal|" where d4=d.d1 and d5=d.d1 and d6=e.d3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testElement
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select element(multiset[5]) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testElementInValues
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values element(multiset[5])"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp union all select deptno from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp union select deptno from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionValues
parameter_list|()
block|{
comment|// union with values
specifier|final
name|String
name|sql
init|=
literal|"values (10), (20)\n"
operator|+
literal|"union all\n"
operator|+
literal|"select 34 from emp\n"
operator|+
literal|"union all values (30), (45 + 10)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionSubQuery
parameter_list|()
block|{
comment|// union of sub-query, inside from list, also values
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp as emp0 cross join\n"
operator|+
literal|" (select empno from emp union all\n"
operator|+
literal|"  select deptno from dept where deptno> 20 union all\n"
operator|+
literal|"  values (45), (67))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDistinctFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 is distinct from 2 from (values(true))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNotDistinctFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 is not distinct from 2 from (values(true))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotLike
parameter_list|()
block|{
comment|// note that 'x not like y' becomes 'not(x like y)'
specifier|final
name|String
name|sql
init|=
literal|"values ('a' not like 'b' escape 'c')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTumble
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select STREAM\n"
operator|+
literal|"  TUMBLE_START(rowtime, INTERVAL '1' MINUTE) AS s,\n"
operator|+
literal|"  TUMBLE_END(rowtime, INTERVAL '1' MINUTE) AS e\n"
operator|+
literal|"from Shipments\n"
operator|+
literal|"GROUP BY TUMBLE(rowtime, INTERVAL '1' MINUTE)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotNotIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from EMP where not (ename not in ('Fred') )"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverMultiple
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) over w1,\n"
operator|+
literal|"  sum(deptno) over w1,\n"
operator|+
literal|"  sum(deptno) over w2\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where deptno - sal> 999\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding),\n"
operator|+
literal|"  w2 as (partition by job order by hiredate rows 3 preceding disallow partial),\n"
operator|+
literal|"  w3 as (partition by job order by hiredate range interval '1' second preceding)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-750">[CALCITE-750]    * Allow windowed aggregate on top of regular aggregate</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testNestedAggregates
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT\n"
operator|+
literal|"  avg(sum(sal) + 2 * min(empno) + 3 * avg(empno))\n"
operator|+
literal|"  over (partition by deptno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test one of the custom conversions which is recognized by the class of the    * operator (in this case,    * {@link org.apache.calcite.sql.fun.SqlCaseOperator}).    */
annotation|@
name|Test
specifier|public
name|void
name|testCase
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values (case 'a' when 'a' then 1 end)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests one of the custom conversions which is recognized by the identity    * of the operator (in this case,    * {@link org.apache.calcite.sql.fun.SqlStdOperatorTable#CHARACTER_LENGTH}).    */
annotation|@
name|Test
specifier|public
name|void
name|testCharLength
parameter_list|()
block|{
comment|// Note that CHARACTER_LENGTH becomes CHAR_LENGTH.
name|sql
argument_list|(
literal|"values (character_length('foo'))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverAvg
parameter_list|()
block|{
comment|// AVG(x) gets translated to SUM(x)/COUNT(x).  Because COUNT controls
comment|// the return type there usually needs to be a final CAST to get the
comment|// result back to match the type of x.
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) over w1,\n"
operator|+
literal|"  avg(sal) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverAvg2
parameter_list|()
block|{
comment|// Check to see if extra CAST is present.  Because CAST is nested
comment|// inside AVG it passed to both SUM and COUNT so the outer final CAST
comment|// isn't needed.
specifier|final
name|String
name|sql
init|=
literal|"select sum(sal) over w1,\n"
operator|+
literal|"  avg(CAST(sal as real)) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverCountStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(sal) over w1,\n"
operator|+
literal|"  count(*) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that a window containing only ORDER BY is implicitly CURRENT ROW.    */
annotation|@
name|Test
specifier|public
name|void
name|testOverOrderWindow
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select last_value(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as (order by empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Same query using inline window
specifier|final
name|String
name|sql2
init|=
literal|"select last_value(deptno) over (order by empno)\n"
operator|+
literal|"from emp\n"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that a window with a FOLLOWING bound becomes BETWEEN CURRENT ROW    * AND FOLLOWING.    */
annotation|@
name|Test
specifier|public
name|void
name|testOverOrderFollowingWindow
parameter_list|()
block|{
comment|// Window contains only ORDER BY (implicitly CURRENT ROW).
specifier|final
name|String
name|sql
init|=
literal|"select last_value(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as (order by empno rows 2 following)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Same query using inline window
specifier|final
name|String
name|sql2
init|=
literal|"select\n"
operator|+
literal|"  last_value(deptno) over (order by empno rows 2 following)\n"
operator|+
literal|"from emp\n"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTumbleTable
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream"
operator|+
literal|" tumble_end(rowtime, interval '2' hour) as rowtime, productId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour), productId"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testTumbleTable()} but on a table where "rowtime" is at    * position 1 not 0. */
annotation|@
name|Test
specifier|public
name|void
name|testTumbleTableRowtimeNotFirstColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream\n"
operator|+
literal|"   tumble_end(rowtime, interval '2' hour) as rowtime, orderId\n"
operator|+
literal|"from shipments\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour), orderId"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHopTable
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream hop_start(rowtime, interval '1' hour,"
operator|+
literal|" interval '3' hour) as rowtime,\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by hop(rowtime, interval '1' hour, interval '3' hour)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionTable
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream session_start(rowtime, interval '1' hour)"
operator|+
literal|" as rowtime,\n"
operator|+
literal|"  session_end(rowtime, interval '1' hour),\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by session(rowtime, interval '1' hour)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInterval
parameter_list|()
block|{
comment|// temporarily disabled per DTbug 1212
if|if
condition|(
operator|!
name|Bug
operator|.
name|DT785_FIXED
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|sql
init|=
literal|"values(cast(interval '1' hour as interval hour to second))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStream
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream productId from orders where productId = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream\n"
operator|+
literal|" floor(rowtime to second) as rowtime, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by floor(rowtime to second)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamWindowedAggregation
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *,\n"
operator|+
literal|"  count(*) over (partition by productId\n"
operator|+
literal|"    order by rowtime\n"
operator|+
literal|"    range interval '1' second preceding) as c\n"
operator|+
literal|"from orders"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplainAsXml
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select 1 + 2, 3 from (values (true))"
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sql
argument_list|)
operator|.
name|rel
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|RelXmlWriter
name|planWriter
init|=
operator|new
name|RelXmlWriter
argument_list|(
name|pw
argument_list|,
name|SqlExplainLevel
operator|.
name|EXPPLAN_ATTRIBUTES
argument_list|)
decl_stmt|;
name|rel
operator|.
name|explain
argument_list|(
name|planWriter
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"<RelNode type=\"LogicalProject\">\n"
operator|+
literal|"\t<Property name=\"EXPR$0\">\n"
operator|+
literal|"\t\t+(1, 2)\t</Property>\n"
operator|+
literal|"\t<Property name=\"EXPR$1\">\n"
operator|+
literal|"\t\t3\t</Property>\n"
operator|+
literal|"\t<Inputs>\n"
operator|+
literal|"\t\t<RelNode type=\"LogicalValues\">\n"
operator|+
literal|"\t\t\t<Property name=\"tuples\">\n"
operator|+
literal|"\t\t\t\t[{ true }]\t\t\t</Property>\n"
operator|+
literal|"\t\t\t<Inputs/>\n"
operator|+
literal|"\t\t</RelNode>\n"
operator|+
literal|"\t</Inputs>\n"
operator|+
literal|"</RelNode>\n"
argument_list|,
name|Util
operator|.
name|toLinux
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-412">[CALCITE-412]    * RelFieldTrimmer: when trimming Sort, the collation and trait set don't    * match</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSortWithTrim
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename from (select * from emp order by sal) a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|trim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffset0
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp offset 0"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test group-by CASE expression involving a non-query IN    */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByCaseSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT CASE WHEN emp.empno IN (3) THEN 0 ELSE 1 END\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY (CASE WHEN emp.empno IN (3) THEN 0 ELSE 1 END)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test aggregate function on a CASE expression involving a non-query IN    */
annotation|@
name|Test
specifier|public
name|void
name|testAggCaseSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(CASE WHEN empno IN (3) THEN 0 ELSE 1 END) FROM emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-753">[CALCITE-753]    * Test aggregate operators do not derive row types with duplicate column    * names</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testAggNoDuplicateColumnNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT  empno, EXPR$2, COUNT(empno) FROM (\n"
operator|+
literal|"    SELECT empno, deptno AS EXPR$2\n"
operator|+
literal|"    FROM emp)\n"
operator|+
literal|"GROUP BY empno, EXPR$2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggScalarSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(SELECT min(deptno) FROM dept) FROM emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test aggregate function on a CASE expression involving IN with a    * sub-query.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-551">[CALCITE-551]    * Sub-query inside aggregate function</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testAggCaseInSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(\n"
operator|+
literal|"  CASE WHEN deptno IN (SELECT deptno FROM dept) THEN 1 ELSE 0 END)\n"
operator|+
literal|"FROM emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelatedSubQueryInAggregate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(\n"
operator|+
literal|"  (select char_length(name) from dept\n"
operator|+
literal|"   where dept.deptno = emp.empno))\n"
operator|+
literal|"FROM emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-614">[CALCITE-614]    * IN within CASE within GROUP BY gives AssertionError</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByCaseIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" (CASE WHEN (deptno IN (10, 20)) THEN 0 ELSE deptno END),\n"
operator|+
literal|" min(empno) from EMP\n"
operator|+
literal|"group by (CASE WHEN (deptno IN (10, 20)) THEN 0 ELSE deptno END)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
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
name|String
name|sql
init|=
literal|"insert into empnullables (deptno, empno, ename)\n"
operator|+
literal|"values (10, 150, 'Fred')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (50, 'Fred')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults (deptno) values (300)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertSubsetWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults values (100)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBind
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables (deptno, empno, ename)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults (deptno) values (?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindSubsetWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults values (?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertSubsetView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables_20\n"
operator|+
literal|"values (10, 'Fred')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults(updated TIMESTAMP)"
operator|+
literal|" (ename, deptno, empno, updated, sal)"
operator|+
literal|" values ('Fred', 456, 44, timestamp '2017-03-12 13:03:05', 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults(updated TIMESTAMP)"
operator|+
literal|" (ename, deptno, empno, updated, sal)"
operator|+
literal|" values ('Fred', 456, 44, ?, 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(updated TIMESTAMP)"
operator|+
literal|" (ename, deptno, empno, updated, sal)"
operator|+
literal|" values ('Fred', 20, 44, timestamp '2017-03-12 13:03:05', 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(updated TIMESTAMP)"
operator|+
literal|" (ename, deptno, empno, updated, sal)"
operator|+
literal|" values ('Fred', 20, 44, ?, 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
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
name|String
name|sql
init|=
literal|"delete from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteWhere
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emp where deptno = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteBind
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emp where deptno = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteBindExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emp(enddate TIMESTAMP) where enddate< ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteBindModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from EMP_MODIFIABLEVIEW2 where empno = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(note VARCHAR) where note = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp set empno = empno + 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1527"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testUpdateSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set empno = (\n"
operator|+
literal|"  select min(empno) from emp as e where e.deptno = emp.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateWhere
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp set empno = empno + 1 where deptno = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2 set sal = sal + 5000 where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update empdefaults(updated TIMESTAMP)"
operator|+
literal|" set deptno = 1, updated = timestamp '2017-03-12 13:03:05', empno = 20, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2(updated TIMESTAMP)"
operator|+
literal|" set updated = timestamp '2017-03-12 13:03:05', sal = sal + 5000 where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateBind
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp"
operator|+
literal|" set sal = sal + ? where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1708"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testUpdateBindExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp(test INT)"
operator|+
literal|" set test = ?, sal = sal + 5000 where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1708"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testUpdateBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2(test INT)"
operator|+
literal|" set test = ?, sal = sal + 5000 where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-985"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testMerge
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emp as target\n"
operator|+
literal|"using (select * from emp where deptno = 30) as source\n"
operator|+
literal|"on target.empno = source.empno\n"
operator|+
literal|"when matched then\n"
operator|+
literal|"  update set sal = sal + source.sal\n"
operator|+
literal|"when not matched then\n"
operator|+
literal|"  insert (empno, deptno, sal)\n"
operator|+
literal|"  values (source.empno, source.deptno, source.sal)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectView
parameter_list|()
block|{
comment|// translated condition: deptno = 20 and sal> 1000 and empno> 100
specifier|final
name|String
name|sql
init|=
literal|"select * from emp_20 where empno> 100"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables_20 (empno, ename)\n"
operator|+
literal|"values (150, 'Fred')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW (EMPNO, ENAME, JOB)"
operator|+
literal|" values (34625, 'nom', 'accountant')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertSubsetModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW "
operator|+
literal|"values (10, 'Fred')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW (empno, job)"
operator|+
literal|" values (?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertBindSubsetModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW"
operator|+
literal|" values (?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|conformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertWithCustomColumnResolving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into struct.t values (?, ?, ?, ?, ?, ?, ?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertWithCustomColumnResolving2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into struct.t_nullables (f0.c0, f1.c2, c1)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertViewWithCustomColumnResolving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into struct.t_10 (f0.c0, f1.c2, c1, k0,\n"
operator|+
literal|"  f1.a0, f2.a0, f0.c1, f2.c3)\n"
operator|+
literal|"values (?, ?, ?, ?, ?, ?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateWithCustomColumnResolving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update struct.t set c0 = c0 + 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryAggregateFunctionFollowedBySimpleOperation
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from EMP\n"
operator|+
literal|"where deptno> (select min(deptno) * 2 + 10 from EMP)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from EMP\n"
operator|+
literal|"where deptno> (values 10)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryLimitOne
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from EMP\n"
operator|+
literal|"where deptno> (select deptno\n"
operator|+
literal|"from EMP order by deptno limit 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-710">[CALCITE-710]    * When look up sub-queries, perform the same logic as the way when ones were    * registered</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testIdenticalExpressionInSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from EMP\n"
operator|+
literal|"where deptno in (1, 2) or deptno in (1, 2)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-694">[CALCITE-694]    * Scan HAVING clause for sub-queries and IN-lists</a> relating to IN.    */
annotation|@
name|Test
specifier|public
name|void
name|testHavingAggrFunctionIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"having sum(case when deptno in (1, 2) then 0 else 1 end) +\n"
operator|+
literal|"sum(case when deptno in (3, 4) then 0 else 1 end)> 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-694">[CALCITE-694]    * Scan HAVING clause for sub-queries and IN-lists</a>, with a sub-query in    * the HAVING clause.    */
annotation|@
name|Test
specifier|public
name|void
name|testHavingInSubQueryWithAggrFunction
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sal\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by sal\n"
operator|+
literal|"having sal in (\n"
operator|+
literal|"  select deptno\n"
operator|+
literal|"  from dept\n"
operator|+
literal|"  group by deptno\n"
operator|+
literal|"  having sum(deptno)> 0)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-716">[CALCITE-716]    * Scalar sub-query and aggregate function in SELECT or HAVING clause gives    * AssertionError</a>; variant involving HAVING clause.    */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateAndScalarSubQueryInHaving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"having max(emp.empno)> (SELECT min(emp.empno) FROM emp)\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-716">[CALCITE-716]    * Scalar sub-query and aggregate function in SELECT or HAVING clause gives    * AssertionError</a>; variant involving SELECT clause.    */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateAndScalarSubQueryInSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|"  max(emp.empno)> (SELECT min(emp.empno) FROM emp) as b\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * window aggregate and ranking functions with grouped aggregates</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testWindowAggWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select min(deptno), rank() over (order by empno),\n"
operator|+
literal|"max(empno) over (partition by deptno)\n"
operator|+
literal|"from emp group by deptno, empno\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-847">[CALCITE-847]    * AVG window function in GROUP BY gives AssertionError</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testWindowAverageWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select avg(deptno) over ()\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving joins</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testWindowAggWithGroupByAndJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select min(d.deptno), rank() over (order by e.empno),\n"
operator|+
literal|" max(e.empno) over (partition by e.deptno)\n"
operator|+
literal|"from emp e, dept d\n"
operator|+
literal|"where e.deptno = d.deptno\n"
operator|+
literal|"group by d.deptno, e.empno, e.deptno\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving HAVING clause</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testWindowAggWithGroupByAndHaving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select min(deptno), rank() over (order by empno),\n"
operator|+
literal|"max(empno) over (partition by deptno)\n"
operator|+
literal|"from emp group by deptno, empno\n"
operator|+
literal|"having empno< 10 and min(deptno)< 20\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving join with sub-query that contains window function and    * GROUP BY</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testWindowAggInSubQueryJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select T.x, T.y, T.z, emp.empno\n"
operator|+
literal|"from (select min(deptno) as x,\n"
operator|+
literal|"   rank() over (order by empno) as y,\n"
operator|+
literal|"   max(empno) over (partition by deptno) as z\n"
operator|+
literal|"   from emp group by deptno, empno) as T\n"
operator|+
literal|" inner join emp on T.x = emp.deptno\n"
operator|+
literal|" and T.y = emp.empno\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1313">[CALCITE-1313]    * Validator should derive type of expression in ORDER BY</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOver
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select deptno, rank() over(partition by empno order by deptno)\n"
operator|+
literal|"from emp order by row_number() over(partition by empno order by deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case (correlated scalar aggregate sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationScalarAggAndFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and e1.sal> (select avg(sal) from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1543">[CALCITE-1543]    * Correlated scalar sub-query with multiple aggregates gives    * AssertionError</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationMultiScalarAggregate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(e1.empno)\n"
operator|+
literal|"from emp e1, dept d1\n"
operator|+
literal|"where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.sal> (select avg(e2.sal) from emp e2\n"
operator|+
literal|"  where e2.deptno = d1.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationScalarAggAndFilterRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and e1.sal> (select avg(sal) from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case (correlated EXISTS sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationExistsAndFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and exists (select * from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|expand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationExistsAndFilterRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and exists (select * from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** A theta join condition, unlike the equi-join condition in    * {@link #testCorrelationExistsAndFilterRex()}, requires a value    * generator. */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationExistsAndFilterThetaRex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and exists (select * from emp e2 where e1.empno< e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case (correlated NOT EXISTS sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationNotExistsAndFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1, dept d1 where e1.deptno = d1.deptno\n"
operator|+
literal|"and e1.deptno< 10 and d1.deptno< 15\n"
operator|+
literal|"and not exists (select * from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|decorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select k0 from struct.t"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolving2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select c2 from struct.t"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolving3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select f1.c2 from struct.t"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolving4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select c1 from struct.t order by f0.c1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolving5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(c1) from struct.t group by f0.c1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolvingWithSelectStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from struct.t"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomColumnResolvingWithSelectFieldNameDotStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select f1.* from struct.t"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]    * Dynamic Table / Dynamic Star support</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testSelectFromDynamicTable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select n_nationkey, n_name from SALES.NATION"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testSelectStarFromDynamicTable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from SALES.NATION"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testReferDynamicStarInSelectOB
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select n_nationkey, n_name\n"
operator|+
literal|"from (select * from SALES.NATION)\n"
operator|+
literal|"order by n_regionkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testDynamicStarInTableJoin
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from "
operator|+
literal|" (select * from SALES.NATION) T1, "
operator|+
literal|" (SELECT * from SALES.CUSTOMER) T2 "
operator|+
literal|" where T1.n_nationkey = T2.c_nationkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testReferDynamicStarInSelectWhereGB
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select n_regionkey, count(*) as cnt from "
operator|+
literal|"(select * from SALES.NATION) where n_nationkey> 5 "
operator|+
literal|"group by n_regionkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testDynamicStarInJoinAndSubQ
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from "
operator|+
literal|" (select * from SALES.NATION T1, "
operator|+
literal|" SALES.CUSTOMER T2 where T1.n_nationkey = T2.c_nationkey)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testStarJoinStaticDynTable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from SALES.NATION N, SALES.REGION as R "
operator|+
literal|"where N.n_regionkey = R.r_regionkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testGrpByColFromStarInSubQuery
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT n.n_nationkey AS col "
operator|+
literal|" from (SELECT * FROM SALES.NATION) as n "
operator|+
literal|" group by n.n_nationkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testDynStarInExistSubQ
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from SALES.REGION where exists (select * from SALES.NATION)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for Dynamic Table / Dynamic Star support    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testSelStarOrderBy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * from SALES.NATION order by n_nationkey"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|getTesterWithDynamicTable
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1321">[CALCITE-1321]    * Configurable IN list size when converting IN clause to join</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testInToSemiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT empno"
operator|+
literal|" FROM emp AS e"
operator|+
literal|" WHERE cast(e.empno as bigint) in (130, 131, 132, 133, 134)"
decl_stmt|;
comment|// No conversion to join since less than IN-list size threshold 10
name|SqlToRelConverter
operator|.
name|Config
name|noConvertConfig
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withInSubQueryThreshold
argument_list|(
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|noConvertConfig
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotConverted}"
argument_list|)
expr_stmt|;
comment|// Conversion to join since greater than IN-list size threshold 2
name|SqlToRelConverter
operator|.
name|Config
name|convertConfig
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withInSubQueryThreshold
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|convertConfig
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planConverted}"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Tester
name|getTesterWithDynamicTable
parameter_list|()
block|{
return|return
name|tester
operator|.
name|withCatalogReaderFactory
argument_list|(
operator|new
name|Function
argument_list|<
name|RelDataTypeFactory
argument_list|,
name|Prepare
operator|.
name|CatalogReader
argument_list|>
argument_list|()
block|{
specifier|public
name|Prepare
operator|.
name|CatalogReader
name|apply
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|MockCatalogReader
argument_list|(
name|typeFactory
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|MockCatalogReader
name|init
parameter_list|()
block|{
comment|// CREATE SCHEMA "SALES;
comment|// CREATE DYNAMIC TABLE "NATION"
comment|// CREATE DYNAMIC TABLE "CUSTOMER"
name|MockSchema
name|schema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"SALES"
argument_list|)
decl_stmt|;
name|registerSchema
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|MockTable
name|nationTable
init|=
operator|new
name|MockDynamicTable
argument_list|(
name|this
argument_list|,
name|schema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|schema
operator|.
name|getName
argument_list|()
argument_list|,
literal|"NATION"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|registerTable
argument_list|(
name|nationTable
argument_list|)
expr_stmt|;
name|MockTable
name|customerTable
init|=
operator|new
name|MockDynamicTable
argument_list|(
name|this
argument_list|,
name|schema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|schema
operator|.
name|getName
argument_list|()
argument_list|,
literal|"CUSTOMER"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|registerTable
argument_list|(
name|customerTable
argument_list|)
expr_stmt|;
comment|// CREATE TABLE "REGION" - static table with known schema.
specifier|final
name|RelDataType
name|intType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varcharType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
name|MockTable
name|regionTable
init|=
name|MockTable
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|schema
argument_list|,
literal|"REGION"
argument_list|,
literal|false
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|regionTable
operator|.
name|addColumn
argument_list|(
literal|"R_REGIONKEY"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|regionTable
operator|.
name|addColumn
argument_list|(
literal|"R_NAME"
argument_list|,
name|varcharType
argument_list|)
expr_stmt|;
name|regionTable
operator|.
name|addColumn
argument_list|(
literal|"R_COMMENT"
argument_list|,
name|varcharType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|regionTable
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// CHECKSTYLE: IGNORE 1
block|}
operator|.
name|init
argument_list|()
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
name|testLarge
parameter_list|()
block|{
name|SqlValidatorTest
operator|.
name|checkLarge
argument_list|(
literal|400
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
specifier|final
name|RelRoot
name|root
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|root
operator|.
name|project
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionInFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x0, x1 from (\n"
operator|+
literal|"  select 'a' as x0, 'a' as x1, 'a' as x2 from emp\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select 'bb' as x0, 'bb' as x1, 'bb' as x2 from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Visitor that checks that every {@link RelNode} in a tree is valid.    *    * @see RelNode#isValid(Litmus, RelNode.Context)    */
specifier|public
specifier|static
class|class
name|RelValidityChecker
extends|extends
name|RelVisitor
implements|implements
name|RelNode
operator|.
name|Context
block|{
name|int
name|invalidCount
decl_stmt|;
specifier|final
name|Deque
argument_list|<
name|RelNode
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|correlationIds
parameter_list|()
block|{
specifier|final
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|CorrelationId
argument_list|>
name|builder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|r
range|:
name|stack
control|)
block|{
name|builder
operator|.
name|addAll
argument_list|(
name|r
operator|.
name|getVariablesSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|node
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
try|try
block|{
name|stack
operator|.
name|push
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
name|this
argument_list|)
condition|)
block|{
operator|++
name|invalidCount
expr_stmt|;
block|}
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|,
name|ordinal
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Allows fluent testing. */
specifier|public
class|class
name|Sql
block|{
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|expand
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|decorrelate
decl_stmt|;
specifier|private
specifier|final
name|Tester
name|tester
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|trim
decl_stmt|;
specifier|private
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
decl_stmt|;
specifier|private
specifier|final
name|SqlConformance
name|conformance
decl_stmt|;
name|Sql
parameter_list|(
name|String
name|sql
parameter_list|,
name|boolean
name|expand
parameter_list|,
name|boolean
name|decorrelate
parameter_list|,
name|Tester
name|tester
parameter_list|,
name|boolean
name|trim
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|config
parameter_list|,
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|expand
operator|=
name|expand
expr_stmt|;
name|this
operator|.
name|decorrelate
operator|=
name|decorrelate
expr_stmt|;
name|this
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
name|this
operator|.
name|trim
operator|=
name|trim
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|conformance
operator|=
name|conformance
expr_stmt|;
block|}
specifier|public
name|void
name|ok
parameter_list|()
block|{
name|convertsTo
argument_list|(
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|convertsTo
parameter_list|(
name|String
name|plan
parameter_list|)
block|{
name|tester
operator|.
name|withExpand
argument_list|(
name|expand
argument_list|)
operator|.
name|withDecorrelation
argument_list|(
name|decorrelate
argument_list|)
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
operator|.
name|withConfig
argument_list|(
name|config
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
name|sql
argument_list|,
name|plan
argument_list|,
name|trim
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sql
name|withConfig
parameter_list|(
name|SqlToRelConverter
operator|.
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|expand
parameter_list|(
name|boolean
name|expand
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|decorrelate
parameter_list|(
name|boolean
name|decorrelate
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|with
parameter_list|(
name|Tester
name|tester
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|trim
parameter_list|(
name|boolean
name|trim
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|conformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expand
argument_list|,
name|decorrelate
argument_list|,
name|tester
argument_list|,
name|trim
argument_list|,
name|config
argument_list|,
name|conformance
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlToRelConverterTest.java
end_comment

end_unit

