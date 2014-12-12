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
name|check
argument_list|(
literal|"select 1 from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAliasList
parameter_list|()
block|{
name|check
argument_list|(
literal|"select a + b from (\n"
operator|+
literal|"  select deptno, 1 as one, name from dept\n"
operator|+
literal|") as d(a, b, c)\n"
operator|+
literal|"where c like 'X%'"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAliasList2
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (\n"
operator|+
literal|"  select a, b, c from (values (1, 2, 3)) as t (c, b, a)\n"
operator|+
literal|") join dept on dept.deptno = c\n"
operator|+
literal|"order by c + a"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where deptno< 10\n"
operator|+
literal|"and deptno> 5\n"
operator|+
literal|"and (deptno = 8 or empno< 100)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOn
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp JOIN dept on emp.deptno = dept.deptno"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-245">CALCITE-245</a>,    * "Off-by-one translation of ON clause of JOIN".    */
annotation|@
name|Test
specifier|public
name|void
name|testConditionOffByOne
parameter_list|()
block|{
comment|// Bug causes the plan to contain
comment|//   LogicalJoin(condition=[=($9, $9)], joinType=[inner])
name|check
argument_list|(
literal|"SELECT * FROM emp JOIN dept on emp.deptno + 0 = dept.deptno"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConditionOffByOneReversed
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp JOIN dept on dept.deptno = emp.deptno + 0"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnExpression
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp JOIN dept on emp.deptno + 1 = dept.deptno - 2"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnIn
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp join dept\n"
operator|+
literal|" on emp.deptno = dept.deptno and emp.empno in (1, 3)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsing
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp JOIN dept USING (deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-74">CALCITE-74</a>,    * "JOIN ... USING fails in 3-way join with UnsupportedOperationException". */
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsingThreeWay
parameter_list|()
block|{
name|check
argument_list|(
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"join emp as e2 using (empno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUsingCompound
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp LEFT JOIN ("
operator|+
literal|"SELECT *, deptno * 5 as empno FROM dept) "
operator|+
literal|"USING (deptno,empno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNatural
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp NATURAL JOIN dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNaturalNoCommonColumn
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp NATURAL JOIN (SELECT deptno AS foo, name FROM dept) AS d"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinNaturalMultipleCommonColumn
parameter_list|()
block|{
name|check
argument_list|(
literal|"SELECT * FROM emp NATURAL JOIN (SELECT deptno, name AS ename FROM dept) AS d"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinWithUnion
parameter_list|()
block|{
name|check
argument_list|(
literal|"select grade from "
operator|+
literal|"(select empno from emp union select deptno from dept), "
operator|+
literal|"salgrade"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select deptno from emp group by deptno"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select deptno, sum(sal) as sum_sal from emp group by deptno"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select deptno + 4, sum(sal), sum(3 + sal), 2 * count(sal) from emp group by deptno"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|sql
argument_list|(
literal|"select 1\n"
operator|+
literal|"from (values (0, 1, 2, 3, 4)) as t(a, b, c, x, y)\n"
operator|+
literal|"group by grouping sets ((a, b), c), grouping sets ((x, y), ())"
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
name|sql
argument_list|(
literal|"select deptno, grouping(deptno), count(*), grouping(empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by empno, deptno\n"
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
name|testGroupingFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, grouping(deptno), count(*), grouping(empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(empno, deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * GROUP BY with duplicates    *    *<p>From SQL spec:    *<blockquote>NOTE 190 â That is, a simple<em>group by clause</em> that is    * not primitive may be transformed into a primitive<em>group by clause</em>    * by deleting all parentheses, and deleting extra commas as necessary for    * correct syntax. If there are no grouping columns at all (for example,    * GROUP BY (), ()), this is transformed to the canonical form GROUP BY ().    *</blockquote> */
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
name|sql
argument_list|(
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
name|sql
argument_list|(
literal|"select 1 from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by grouping sets (a, b), grouping sets (c, d)"
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
name|sql
argument_list|(
literal|"select 1 from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by grouping sets (a, (a, b)), grouping sets (c), d"
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
name|sql
argument_list|(
literal|"select a, b, count(*) as c\n"
operator|+
literal|"from (values (cast(null as integer), 2)) as t(a, b)\n"
operator|+
literal|"group by rollup(a, b)"
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
name|sql
argument_list|(
literal|"select 1 from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by rollup(a, b), rollup(c, d)"
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
name|sql
argument_list|(
literal|"select 1 from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by rollup(b, (a, d))"
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
name|sql
argument_list|(
literal|"select 1 from (values (1, 2, 3, 4)) as t(a, b, c, d)\n"
operator|+
literal|"group by cube(a, b)"
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
name|sql
argument_list|(
literal|"with t(a, b, c, d) as (values (1, 2, 3, 4))\n"
operator|+
literal|"select 1 from t\n"
operator|+
literal|"group by rollup(a, b), rollup(c, d)"
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
name|check
argument_list|(
literal|"select sum(sal + sal) from emp having sum(sal)> 10"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select name from (select name from dept group by name)"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|sql
argument_list|(
literal|"select name, foo from ("
operator|+
literal|"select deptno, name, count(deptno) as foo "
operator|+
literal|"from dept "
operator|+
literal|"group by name, deptno, name)"
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
name|sql
argument_list|(
literal|"select count(*) from emp group by substring(ename FROM 1 FOR 1)"
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
name|sql
argument_list|(
literal|"select deptno, sum(sal), sum(distinct sal), count(*) "
operator|+
literal|"from emp "
operator|+
literal|"group by deptno"
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
name|check
argument_list|(
literal|"select distinct sal + 5, deptno, sal + 5 from emp where deptno< 10"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|tester
operator|.
name|assertConvertsTo
argument_list|(
name|sql
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrder
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp order by empno"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderDescNullsLast
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp order by empno desc nulls last"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1, deptno, empno from emp order by 2 desc"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
comment|// ordinals rounded down, so 2.5 should have same effect as 2, and
comment|// generate identical plan
name|check
argument_list|(
literal|"select empno + 1, deptno, empno from emp order by 2.5 desc"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select distinct empno, deptno + 1 from emp order by deptno + 1 + empno"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1, deptno, empno from emp order by -1 desc"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1, deptno, empno from emp order by 1 + 2 desc"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1 from emp order by deptno asc, empno + 1 desc"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAlias
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno + 1 as x, empno - 2 as y from emp order by y"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByAliasInExpr
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno + 1 as x, empno - 2 as y from emp order by y + 3"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1 as empno, empno - 2 as y from emp order by empno + 3"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno + 1 as empno, empno - 2 as y from emp order by empno + 3"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderBySameExpr
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp, dept order by sal + empno desc, sal * empno, sal + empno"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnion
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by sal desc, empno asc"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by 2"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnionExprs
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by empno * sal + 2"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderOffsetFetch
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp order by empno offset 10 rows fetch next 5 rows only"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffsetFetch
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp offset 10 rows fetch next 5 rows only"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffset
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp offset 10 rows"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFetch
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp fetch next 5 rows only"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select \"$f2\", max(x), max(x + 1)\n"
operator|+
literal|"from (values (1, 2)) as t(\"$f2\", x)\n"
operator|+
literal|"group by \"$f2\""
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select deptno, count(*) "
operator|+
literal|"from emp "
operator|+
literal|"group by deptno "
operator|+
literal|"order by deptno * sum(sal) desc, min(empno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountNoGroup
parameter_list|()
block|{
name|check
argument_list|(
literal|"select count(*), sum(sal)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where empno> 10"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWith
parameter_list|()
block|{
name|check
argument_list|(
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2 order by deptno"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithUnionOrder
parameter_list|()
block|{
name|check
argument_list|(
literal|"with emp2 as (select empno, deptno as x from emp)\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"order by empno + x"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithUnion
parameter_list|()
block|{
name|check
argument_list|(
literal|"with emp2 as (select * from emp where deptno> 10)\n"
operator|+
literal|"select empno from emp2 where deptno< 30 union all select deptno from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithAlias
parameter_list|()
block|{
name|check
argument_list|(
literal|"with w(x, y) as (select * from dept where deptno> 10)\n"
operator|+
literal|"select x from w where x< 30 union all select deptno from dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExists
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExistsDecorrelate
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideScalarSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select (\n"
operator|+
literal|" with dept2 as (select * from dept where deptno> 10)"
operator|+
literal|" select count(*) from dept2) as c\n"
operator|+
literal|"from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableExtend
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept extend (x varchar(5) not null)"
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplicitTable
parameter_list|()
block|{
name|check
argument_list|(
literal|"table emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTable
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from table(ramp(3))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSample
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp tablesample substitute('DATASET1') where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleQuery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample substitute('DATASET1')\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample substitute('DATASET2')\n"
operator|+
literal|"where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleBernoulli
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp tablesample bernoulli(50) where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleBernoulliQuery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample bernoulli(10) repeatable(1)\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample bernoulli(50) repeatable(99)\n"
operator|+
literal|"where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleSystem
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from emp tablesample system(50) where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSampleSystemQuery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select * from (\n"
operator|+
literal|" select * from emp as e tablesample system(10) repeatable(1)\n"
operator|+
literal|" join dept on e.deptno = dept.deptno\n"
operator|+
literal|") tablesample system(50) repeatable(99)\n"
operator|+
literal|"where empno> 5"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollectionTableWithCursorParam
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from table(dedup("
operator|+
literal|"cursor(select ename from emp),"
operator|+
literal|" cursor(select name from dept), 'NAME'))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnest
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from unnest(multiset[1,2])"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from unnest(multiset(select*from dept))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select multiset(select deptno from dept) from (values(true))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 'a',multiset[10] from dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetOfColumns
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 'abc',multiset[deptno,sal] from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelationJoin
parameter_list|()
block|{
name|check
argument_list|(
literal|"select *,"
operator|+
literal|"         multiset(select * from emp where deptno=dept.deptno) "
operator|+
literal|"               as empset"
operator|+
literal|"      from dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExists
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from emp where exists (select 1 from dept where deptno=55)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelated
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select*from emp where exists (select 1 from dept where emp.deptno=dept.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedDecorrelate
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select*from emp where exists (select 1 from dept where emp.deptno=dept.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedLimit
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno limit 1)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelatedLimitDecorrelate
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select*from emp where exists (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno limit 1)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInValueListShort
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp where deptno in (10, 20)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInValueListLong
parameter_list|()
block|{
comment|// Go over the default threshold of 20 to force a subquery.
name|check
argument_list|(
literal|"select empno from emp where deptno in"
operator|+
literal|" (10, 20, 30, 40, 50, 60, 70, 80, 90, 100"
operator|+
literal|", 110, 120, 130, 140, 150, 160, 170, 180, 190"
operator|+
literal|", 200, 210, 220, 230)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp where deptno in"
operator|+
literal|" (select deptno from dept)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubquery
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno from emp where deptno not in"
operator|+
literal|" (select deptno from dept)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInUncorrelatedSubqueryInSelect
parameter_list|()
block|{
comment|// In the SELECT clause, the value of IN remains in 3-valued logic
comment|// -- it's not forced into 2-valued by the "... IS TRUE" wrapper as in the
comment|// WHERE clause -- so the translation is more complicated.
name|check
argument_list|(
literal|"select name, deptno in (\n"
operator|+
literal|"  select case when true then deptno else null end from emp)\n"
operator|+
literal|"from dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
comment|/** Plan should be as {@link #testInUncorrelatedSubqueryInSelect}, but with    * an extra NOT. Both queries require 3-valued logic. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubqueryInSelect
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select case when true then deptno else null end from dept)\n"
operator|+
literal|"from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
comment|/** Since 'deptno NOT IN (SELECT deptno FROM dept)' can not be null, we    * generate a simpler plan. */
annotation|@
name|Test
specifier|public
name|void
name|testNotInUncorrelatedSubqueryInSelectNotNull
parameter_list|()
block|{
name|check
argument_list|(
literal|"select empno, deptno not in (\n"
operator|+
literal|"  select deptno from dept)\n"
operator|+
literal|"from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestSelect
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from unnest(select multiset[deptno] from dept)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinUnnest
parameter_list|()
block|{
name|check
argument_list|(
literal|"select*from dept as d, unnest(multiset[d.deptno * 2])"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateral
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from emp, LATERAL (select * from dept where emp.deptno=dept.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateralDecorrelate
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from emp, LATERAL (select * from dept where emp.deptno=dept.deptno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCorrelations
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from (select 2+deptno d2, 3+deptno d3 from emp) e\n"
operator|+
literal|" where exists (select 1 from (select deptno+1 d1 from dept) d\n"
operator|+
literal|" where d1=e.d2 and exists (select 2 from (select deptno+4 d4, deptno+5 d5, deptno+6 d6 from dept)\n"
operator|+
literal|" where d4=d.d1 and d5=d.d1 and d6=e.d3))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCorrelationsDecorrelated
parameter_list|()
block|{
name|tester
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|assertConvertsTo
argument_list|(
literal|"select * from (select 2+deptno d2, 3+deptno d3 from emp) e\n"
operator|+
literal|" where exists (select 1 from (select deptno+1 d1 from dept) d\n"
operator|+
literal|" where d1=e.d2 and exists (select 2 from (select deptno+4 d4, deptno+5 d5, deptno+6 d6 from dept)\n"
operator|+
literal|" where d4=d.d1 and d5=d.d1 and d6=e.d3))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testElement
parameter_list|()
block|{
name|check
argument_list|(
literal|"select element(multiset[5]) from emp"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testElementInValues
parameter_list|()
block|{
name|check
argument_list|(
literal|"values element(multiset[5])"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionAll
parameter_list|()
block|{
comment|// union all
name|check
argument_list|(
literal|"select empno from emp union all select deptno from dept"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
comment|// union without all
name|check
argument_list|(
literal|"select empno from emp union select deptno from dept"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"values (10), (20)\n"
operator|+
literal|"union all\n"
operator|+
literal|"select 34 from emp\n"
operator|+
literal|"union all values (30), (45 + 10)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionSubquery
parameter_list|()
block|{
comment|// union of subquery, inside from list, also values
name|check
argument_list|(
literal|"select deptno from emp as emp0 cross join\n"
operator|+
literal|" (select empno from emp union all\n"
operator|+
literal|"  select deptno from dept where deptno> 20 union all\n"
operator|+
literal|"  values (45), (67))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDistinctFrom
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 is distinct from 2 from (values(true))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNotDistinctFrom
parameter_list|()
block|{
name|check
argument_list|(
literal|"select 1 is not distinct from 2 from (values(true))"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"values ('a' not like 'b' escape 'c')"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverMultiple
parameter_list|()
block|{
name|check
argument_list|(
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
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"values (case 'a' when 'a' then 1 end)"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"values (character_length('foo'))"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select sum(sal) over w1,\n"
operator|+
literal|"  avg(sal) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select sum(sal) over w1,\n"
operator|+
literal|"  avg(CAST(sal as real)) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverCountStar
parameter_list|()
block|{
name|check
argument_list|(
literal|"select count(sal) over w1,\n"
operator|+
literal|"  count(*) over w1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w1 as (partition by job order by hiredate rows 2 preceding)"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select last_value(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as (order by empno)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
comment|// Same query using inline window
name|check
argument_list|(
literal|"select last_value(deptno) over (order by empno)\n"
operator|+
literal|"from emp\n"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|check
argument_list|(
literal|"select last_value(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as (order by empno rows 2 following)"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
comment|// Same query using inline window
name|check
argument_list|(
literal|"select last_value(deptno) over (order by empno rows 2 following)\n"
operator|+
literal|"from emp\n"
argument_list|,
literal|"${plan}"
argument_list|)
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
name|Bug
operator|.
name|DT785_FIXED
condition|)
block|{
name|check
argument_list|(
literal|"values(cast(interval '1' hour as interval hour to second))"
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-412">CALCITE-412</a>,    * "RelFieldTrimmer: when trimming Sort, the collation and trait set don't    * match". */
annotation|@
name|Test
specifier|public
name|void
name|testSortWithTrim
parameter_list|()
block|{
name|tester
operator|.
name|assertConvertsTo
argument_list|(
literal|"select ename from (select * from emp order by sal) a"
argument_list|,
literal|"${plan}"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Visitor that checks that every {@link RelNode} in a tree is valid.    *    * @see RelNode#isValid(boolean)    */
specifier|public
specifier|static
class|class
name|RelValidityChecker
extends|extends
name|RelVisitor
block|{
name|int
name|invalidCount
decl_stmt|;
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
if|if
condition|(
operator|!
name|node
operator|.
name|isValid
argument_list|(
literal|true
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
name|Sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
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
name|assertConvertsTo
argument_list|(
name|sql
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlToRelConverterTest.java
end_comment

end_unit

