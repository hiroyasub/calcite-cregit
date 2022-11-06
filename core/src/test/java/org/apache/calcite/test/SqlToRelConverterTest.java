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
name|CalciteConnectionConfigImpl
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
name|NullCollation
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
name|plan
operator|.
name|Contexts
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
name|plan
operator|.
name|RelTrait
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
name|plan
operator|.
name|RelTraitDef
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
name|plan
operator|.
name|RelTraitSet
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
name|plan
operator|.
name|hep
operator|.
name|HepPlanner
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
name|plan
operator|.
name|hep
operator|.
name|HepProgram
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
name|plan
operator|.
name|hep
operator|.
name|HepProgramBuilder
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
name|RelCollationTraitDef
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
name|RelShuttleImpl
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
name|RelDotWriter
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
name|logical
operator|.
name|LogicalCalc
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalSort
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
name|logical
operator|.
name|LogicalTableModify
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
name|rules
operator|.
name|CoreRules
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
name|rex
operator|.
name|RexNode
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
name|sql
operator|.
name|validate
operator|.
name|SqlDelegatingConformance
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|AfterAll
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|hamcrest
operator|.
name|core
operator|.
name|Is
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
name|core
operator|.
name|Is
operator|.
name|isA
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.apache.calcite.sql2rel.SqlToRelConverter}.  * See {@link RelOptRulesTest} for an explanation of how to add tests;  */
end_comment

begin_class
class|class
name|SqlToRelConverterTest
extends|extends
name|SqlToRelTestBase
block|{
specifier|private
specifier|static
specifier|final
name|SqlToRelFixture
name|LOCAL_FIXTURE
init|=
name|SqlToRelFixture
operator|.
name|DEFAULT
operator|.
name|withDiffRepos
argument_list|(
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|SqlToRelConverterTest
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Nullable
specifier|private
specifier|static
name|DiffRepository
name|diffRepos
init|=
literal|null
decl_stmt|;
annotation|@
name|AfterAll
specifier|public
specifier|static
name|void
name|checkActualAndReferenceFiles
parameter_list|()
block|{
if|if
condition|(
name|diffRepos
operator|!=
literal|null
condition|)
block|{
name|diffRepos
operator|.
name|checkActualAndReferenceFiles
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|SqlToRelFixture
name|fixture
parameter_list|()
block|{
name|diffRepos
operator|=
name|LOCAL_FIXTURE
operator|.
name|diffRepos
argument_list|()
expr_stmt|;
return|return
name|LOCAL_FIXTURE
return|;
block|}
annotation|@
name|Test
name|void
name|testDotLiteralAfterNestedRow
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ((1,2),(3,4,5)).\"EXPR$1\".\"EXPR$2\" from emp"
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
name|void
name|testDotLiteralAfterRow
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select row(1,2).\"EXPR$1\" from emp"
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
name|void
name|testDotAfterParenthesizedIdentifier
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select (home_address).city from emp_address"
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
name|void
name|testRowValueConstructorWithSubquery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ROW("
operator|+
literal|"(select deptno\n"
operator|+
literal|"from dept\n"
operator|+
literal|"where dept.deptno = emp.deptno), emp.ename)\n"
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
name|void
name|testIntervalExpression
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select interval mgr hour as h from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2468">[CALCITE-2468]    * struct type alias should not cause IndexOutOfBoundsException</a>.    */
annotation|@
name|Test
name|void
name|testStructTypeAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select t.r AS myRow\n"
operator|+
literal|"from (select row(row(1)) r from dept) t"
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
name|void
name|testJoinUsingDynamicTable
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from SALES.NATION t1\n"
operator|+
literal|"join SALES.NATION t2\n"
operator|+
literal|"using (n_nationkey)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that AND(x, AND(y, z)) gets flattened to AND(x, y, z).    */
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-245">[CALCITE-245]    * Off-by-one translation of ON clause of JOIN</a>. */
annotation|@
name|Test
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
name|withExpand
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
name|withExpand
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-801">[CALCITE-801]    * NullPointerException using USING on table alias with column aliases</a>. */
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3387">[CALCITE-3387]    * Query with GROUP BY and JOIN ... USING wrongly fails with    * "Column 'DEPTNO' is ambiguous"</a>. */
annotation|@
name|Test
name|void
name|testJoinUsingWithUnqualifiedCommonColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, name\n"
operator|+
literal|"FROM emp JOIN dept using (deptno)"
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
comment|/** Similar to {@link #testJoinUsingWithUnqualifiedCommonColumn()},    * but with nested common column. */
annotation|@
name|Test
name|void
name|testJoinUsingWithUnqualifiedNestedCommonColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select (coord).x from\n"
operator|+
literal|"customer.contact_peek t1\n"
operator|+
literal|"join customer.contact_peek t2\n"
operator|+
literal|"using (coord)"
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
comment|/** Similar to {@link #testJoinUsingWithUnqualifiedCommonColumn()},    * but with aggregate. */
annotation|@
name|Test
name|void
name|testJoinUsingWithAggregate
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
literal|"full join dept using (deptno)\n"
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
comment|/** Similar to {@link #testJoinUsingWithUnqualifiedCommonColumn()},    * but with grouping sets. */
annotation|@
name|Test
name|void
name|testJoinUsingWithGroupingSets
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, grouping(deptno),\n"
operator|+
literal|"grouping(deptno, job), count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"join dept using (deptno)\n"
operator|+
literal|"group by grouping sets ((deptno), (deptno, job))"
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
comment|/** Similar to {@link #testJoinUsingWithUnqualifiedCommonColumn()},    * but with multiple join. */
annotation|@
name|Test
name|void
name|testJoinUsingWithMultipleJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, ename\n"
operator|+
literal|"FROM emp "
operator|+
literal|"JOIN dept using (deptno)\n"
operator|+
literal|"JOIN (values ('Calcite', 200)) as s(ename, salary) using (ename)"
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
name|void
name|testGroupByAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno as d from emp group by d"
argument_list|)
operator|.
name|withConformance
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
name|withConformance
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
name|void
name|testGroupByAliasEqualToColumnName
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno, ename as deptno from emp group by empno, deptno"
argument_list|)
operator|.
name|withConformance
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
name|void
name|testGroupByOrdinal
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno from emp group by 1"
argument_list|)
operator|.
name|withConformance
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
name|withConformance
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
name|void
name|testAliasInHaving
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(empno) as e from emp having e> 1"
argument_list|)
operator|.
name|withConformance
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
name|void
name|testGroupingSets
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ((deptno), (ename, deptno))\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2147">[CALCITE-2147]    * Incorrect plan in with with ROLLUP inside GROUPING SETS</a>.    *    *<p>Equivalence example:    *<blockquote>GROUP BY GROUPING SETS (ROLLUP(A, B), CUBE(C,D))</blockquote>    *<p>is equal to    *<blockquote>GROUP BY GROUPING SETS ((A,B), (A), (),    * (C,D), (C), (D) )</blockquote>    */
annotation|@
name|Test
name|void
name|testGroupingSetsWithRollup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ( rollup(deptno), (ename, deptno))\n"
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
name|void
name|testGroupingSetsWithCube
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ( (deptno), CUBE(ename, deptno))\n"
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
name|void
name|testGroupingSetsWithRollupCube
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ( CUBE(deptno), ROLLUP(ename, deptno))\n"
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
name|void
name|testGroupingSetsRepeated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, group_id()\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by grouping sets (deptno, (), job, (deptno, job), deptno,\n"
operator|+
literal|"  job, deptno)"
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
comment|/** As {@link #testGroupingSetsRepeated()} but with no {@code GROUP_ID}    * function. (We still need the plan to contain a Union.) */
annotation|@
name|Test
name|void
name|testGroupingSetsRepeatedNoGroupId
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, job\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by grouping sets (deptno, (), job, (deptno, job), deptno,\n"
operator|+
literal|"  job, deptno)"
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
comment|/** As {@link #testGroupingSetsRepeated()} but grouping sets are distinct.    * The {@code GROUP_ID} is replaced by 0.*/
annotation|@
name|Test
name|void
name|testGroupingSetsWithGroupId
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, group_id()\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by grouping sets (deptno, (), job)"
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
name|void
name|testAggFilterWithIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  deptno, sum(sal * 2) filter (where empno not in (1, 2)), count(*)\n"
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
name|void
name|testSelectNull
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select null from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select null as dummy from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithCast
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(null as timestamp) dummy from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"over (ORDER BY empno ROWS BETWEEN 10 PRECEDING AND CURRENT ROW)\n"
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
name|void
name|testDuplicateColumnsInSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"e\" from (\n"
operator|+
literal|"select empno as \"e\", deptno as d, 1 as \"e0\" from EMP)"
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
comment|// duplicate field is dropped, so plan is same
specifier|final
name|String
name|sql2
init|=
literal|"select empno from emp order by empno, empno asc"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ditto
specifier|final
name|String
name|sql3
init|=
literal|"select empno from emp order by empno, empno desc"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that if a column occurs twice in ORDER BY, only the first key is    * kept. */
annotation|@
name|Test
name|void
name|testOrderBasedRepeatFields
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp order by empno DESC, empno ASC"
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
name|void
name|testOrderByOrdinalDesc
parameter_list|()
block|{
comment|// This test requires a conformance that sorts by ordinal
specifier|final
name|SqlToRelFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
name|f2
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByOrdinal
argument_list|()
argument_list|,
name|f2
lambda|->
name|f2
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1, deptno, empno from emp order by 2 desc"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ordinals rounded down, so 2.5 should have the same effect as 2, and
comment|// generate identical plan
specifier|final
name|String
name|sql2
init|=
literal|"select empno + 1, deptno, empno from emp order by 2.5 desc"
decl_stmt|;
name|f
operator|.
name|withSql
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
name|void
name|testOrderByAliasOverrides
parameter_list|()
block|{
comment|// This test requires a conformance that sorts by alias
specifier|final
name|SqlToRelFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
name|f2
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
name|f2
lambda|->
name|f2
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
argument_list|)
decl_stmt|;
comment|// plan should contain '(empno + 1) + 3'
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as empno, empno - 2 as y\n"
operator|+
literal|"from emp order by empno + 3"
decl_stmt|;
name|f
operator|.
name|withSql
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
name|void
name|testOrderByAliasDoesNotOverride
parameter_list|()
block|{
comment|// This test requires a conformance that does not sort by alias
specifier|final
name|SqlToRelFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
operator|!
name|f2
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
name|f2
lambda|->
name|f2
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
argument_list|)
decl_stmt|;
comment|// plan should contain 'empno + 3', not '(empno + 1) + 3'
specifier|final
name|String
name|sql
init|=
literal|"select empno + 1 as empno, empno - 2 as y\n"
operator|+
literal|"from emp order by empno + 3"
decl_stmt|;
name|f
operator|.
name|withSql
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
literal|"order by sal + empno desc, sal * empno, sal + empno desc"
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
name|void
name|testOrderUnionOrdinal
parameter_list|()
block|{
comment|// This test requires a conformance that sorts by ordinal
specifier|final
name|SqlToRelFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
name|f2
operator|.
name|getConformance
argument_list|()
operator|.
name|isSortByOrdinal
argument_list|()
argument_list|,
name|f2
lambda|->
name|f2
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
argument_list|)
decl_stmt|;
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
name|f
operator|.
name|withSql
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
name|void
name|testOrderOffsetFetchWithDynamicParameter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp\n"
operator|+
literal|"order by empno offset ? rows fetch next ? rows only"
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
name|void
name|testOffsetFetchWithDynamicParameter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp\n"
operator|+
literal|"offset ? rows fetch next ? rows only"
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
name|void
name|testOffsetWithDynamicParameter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp offset ? rows"
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
annotation|@
name|Test
name|void
name|testFetchWithDynamicParameter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp fetch next ? rows only"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-439">[CALCITE-439]    * SqlValidatorUtil.uniquify() may not terminate under some conditions</a>. */
annotation|@
name|Test
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|withExpand
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|withExpand
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
name|void
name|testModifiableViewExtend
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW extend (x varchar(5) not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModifiableViewExtendSubset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, empno\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW extend (x varchar(5) not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModifiableViewExtendExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno + x\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW extend (x int not null)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewUnderlyingCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW3(extra BOOLEAN, comm INTEGER)"
operator|+
literal|" set empno = 20, comm = 123, extra = true"
operator|+
literal|" where ename = 'Bob'"
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectModifiableViewConstraint
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from EMP_MODIFIABLEVIEW2\n"
operator|+
literal|"where deptno = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModifiableViewDdlExtend
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
annotation|@
name|Test
name|void
name|testSnapshotOnTemporalTable1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from products_temporal "
operator|+
literal|"for system_time as of TIMESTAMP '2011-01-02 00:00:00'"
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
name|void
name|testSnapshotOnTemporalTable2
parameter_list|()
block|{
comment|// Test temporal table with virtual columns.
specifier|final
name|String
name|sql
init|=
literal|"select * from VIRTUALCOLUMNS.VC_T1 "
operator|+
literal|"for system_time as of TIMESTAMP '2011-01-02 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinTemporalTableOnSpecificTime1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders,\n"
operator|+
literal|"  products_temporal for system_time as of\n"
operator|+
literal|"    TIMESTAMP '2011-01-02 00:00:00'"
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
name|void
name|testJoinTemporalTableOnSpecificTime2
parameter_list|()
block|{
comment|// Test temporal table with virtual columns.
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders,\n"
operator|+
literal|"  VIRTUALCOLUMNS.VC_T1 for system_time as of\n"
operator|+
literal|"    TIMESTAMP '2011-01-02 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinTemporalTableOnColumnReference1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"join products_temporal for system_time as of orders.rowtime\n"
operator|+
literal|"on orders.productid = products_temporal.productid"
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
name|void
name|testJoinTemporalTableOnColumnReference2
parameter_list|()
block|{
comment|// Test temporal table with virtual columns.
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"join VIRTUALCOLUMNS.VC_T1 for system_time as of orders.rowtime\n"
operator|+
literal|"on orders.productid = VIRTUALCOLUMNS.VC_T1.a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Lateral join with temporal table, both snapshot's input scan    * and snapshot's period reference outer columns. Should not    * decorrelate join.    */
annotation|@
name|Test
name|void
name|testCrossJoinTemporalTable1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"cross join lateral (\n"
operator|+
literal|"  select * from products_temporal for system_time\n"
operator|+
literal|"  as of orders.rowtime\n"
operator|+
literal|"  where orders.productid = products_temporal.productid)\n"
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
comment|/**    * Lateral join with temporal table, snapshot's input scan    * reference outer columns, but snapshot's period is static.    * Should be able to decorrelate join.    */
annotation|@
name|Test
name|void
name|testCrossJoinTemporalTable2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"cross join lateral (\n"
operator|+
literal|"  select * from products_temporal for system_time\n"
operator|+
literal|"  as of TIMESTAMP '2011-01-02 00:00:00'\n"
operator|+
literal|"  where orders.productid = products_temporal.productid)\n"
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
comment|/**    * Lateral join with temporal table, snapshot's period reference    * outer columns. Should not decorrelate join.    */
annotation|@
name|Test
name|void
name|testCrossJoinTemporalTable3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"cross join lateral (\n"
operator|+
literal|"  select * from products_temporal for system_time\n"
operator|+
literal|"  as of orders.rowtime\n"
operator|+
literal|"  where products_temporal.productid> 1)\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1732">[CALCITE-1732]    * IndexOutOfBoundsException when using LATERAL TABLE with more than one    * field</a>. */
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4673">[CALCITE-4673]    * If arguments to a table function use correlation variables,    * SqlToRelConverter should eliminate duplicate variables</a>.    *    *<p>The {@code LogicalTableFunctionScan} should have two identical    * correlation variables like "{@code $cor0.DEPTNO}", but before this bug was    * fixed, we have different ones: "{@code $cor0.DEPTNO}" and    * "{@code $cor1.DEPTNO}". */
annotation|@
name|Test
name|void
name|testCorrelationCollectionTableInSubQuery
parameter_list|()
block|{
name|Consumer
argument_list|<
name|String
argument_list|>
name|fn
init|=
name|sql
lambda|->
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planExpanded}"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotExpanded}"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|fn
operator|.
name|accept
argument_list|(
literal|"select e.deptno,\n"
operator|+
literal|"  (select * from lateral table(DEDUP(e.deptno, e.deptno)))\n"
operator|+
literal|"from emp e"
argument_list|)
expr_stmt|;
comment|// same effect without LATERAL
name|fn
operator|.
name|accept
argument_list|(
literal|"select e.deptno,\n"
operator|+
literal|"  (select * from table(DEDUP(e.deptno, e.deptno)))\n"
operator|+
literal|"from emp e"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelatedScalarSubQueryInSelectList
parameter_list|()
block|{
name|Consumer
argument_list|<
name|String
argument_list|>
name|fn
init|=
name|sql
lambda|->
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planExpanded}"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotExpanded}"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|fn
operator|.
name|accept
argument_list|(
literal|"select deptno,\n"
operator|+
literal|"  (select min(1) from emp where empno> d.deptno) as i0,\n"
operator|+
literal|"  (select min(0) from emp where deptno = d.deptno "
operator|+
literal|"                            and ename = 'SMITH'"
operator|+
literal|"                            and d.deptno> 0) as i1\n"
operator|+
literal|"from dept as d"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelationLateralSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT deptno, ename\n"
operator|+
literal|"FROM\n"
operator|+
literal|"  (SELECT DISTINCT deptno FROM emp) t1,\n"
operator|+
literal|"  LATERAL (\n"
operator|+
literal|"    SELECT ename, sal\n"
operator|+
literal|"    FROM emp\n"
operator|+
literal|"    WHERE deptno IN (t1.deptno, t1.deptno)\n"
operator|+
literal|"    AND   deptno = t1.deptno\n"
operator|+
literal|"    ORDER BY sal\n"
operator|+
literal|"    DESC LIMIT 3)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
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
name|void
name|testCorrelationExistsWithSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select emp.deptno, dept.deptno\n"
operator|+
literal|"from emp, dept\n"
operator|+
literal|"where exists (select * from emp\n"
operator|+
literal|"  where emp.deptno = dept.deptno\n"
operator|+
literal|"  and emp.deptno = dept.deptno\n"
operator|+
literal|"  and emp.deptno in (dept.deptno, dept.deptno))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
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
name|void
name|testCorrelationInWithSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select deptno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where deptno in (select deptno\n"
operator|+
literal|"    from dept\n"
operator|+
literal|"    where emp.deptno = dept.deptno\n"
operator|+
literal|"    and emp.deptno = dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3847">[CALCITE-3847]    * Decorrelation for join with lateral table outputs wrong plan if the join    * condition contains correlation variables</a>. */
annotation|@
name|Test
name|void
name|testJoinLateralTableWithConditionCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, r.num from dept join\n"
operator|+
literal|" lateral table(ramp(dept.deptno)) as r(num)\n"
operator|+
literal|" on deptno=num"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4206">[CALCITE-4206]    * RelDecorrelator outputs wrong plan for correlate sort with fetch    * limit</a>. */
annotation|@
name|Test
name|void
name|testCorrelateSortWithLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, ename\n"
operator|+
literal|"FROM\n"
operator|+
literal|"  (SELECT DISTINCT deptno FROM emp) t1,\n"
operator|+
literal|"  LATERAL (\n"
operator|+
literal|"    SELECT ename, sal\n"
operator|+
literal|"    FROM emp\n"
operator|+
literal|"    WHERE deptno = t1.deptno\n"
operator|+
literal|"    ORDER BY sal\n"
operator|+
literal|"    DESC LIMIT 3\n"
operator|+
literal|"  )"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4333">[CALCITE-4333]    * The Sort rel should be decorrelated even though it has fetch or limit    * when its parent is not a Correlate</a>. */
annotation|@
name|Test
name|void
name|testSortLimitWithCorrelateInput
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT deptno, ename\n"
operator|+
literal|"    FROM\n"
operator|+
literal|"        (SELECT DISTINCT deptno FROM emp) t1,\n"
operator|+
literal|"          LATERAL (\n"
operator|+
literal|"            SELECT ename, sal\n"
operator|+
literal|"            FROM emp\n"
operator|+
literal|"            WHERE deptno = t1.deptno)\n"
operator|+
literal|"    ORDER BY ename DESC\n"
operator|+
literal|"    LIMIT 3"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4437">[CALCITE-4437]    * The Sort rel should be decorrelated even though it has fetch or limit    * when it is not inside a Correlate</a>.    */
annotation|@
name|Test
name|void
name|testProjectSortLimitWithCorrelateInput
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT ename||deptno FROM\n"
operator|+
literal|"    (SELECT deptno, ename\n"
operator|+
literal|"    FROM\n"
operator|+
literal|"        (SELECT DISTINCT deptno FROM emp) t1,\n"
operator|+
literal|"          LATERAL (\n"
operator|+
literal|"            SELECT ename, sal\n"
operator|+
literal|"            FROM emp\n"
operator|+
literal|"            WHERE deptno = t1.deptno)\n"
operator|+
literal|"    ORDER BY ename DESC\n"
operator|+
literal|"    LIMIT 3)"
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
name|withDecorrelate
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
name|void
name|testUnnestArrayAggPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, e2.empno_avg\n"
operator|+
literal|"from dept_nested as d outer apply\n"
operator|+
literal|" (select avg(e.empno) as empno_avg from UNNEST(d.employees) as e) e2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
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
name|void
name|testUnnestArrayPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, e2.empno\n"
operator|+
literal|"from dept_nested as d,\n"
operator|+
literal|" UNNEST(d.employees) e2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnnestArrayPlanAs
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, e2.empno\n"
operator|+
literal|"from dept_nested as d,\n"
operator|+
literal|" UNNEST(d.employees) as e2(empno, y, z)"
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
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3789">[CALCITE-3789]    * Support validation of UNNEST multiple array columns like Presto</a>.    */
annotation|@
name|Test
name|void
name|testAliasUnnestArrayPlanWithSingleColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, employee.empno\n"
operator|+
literal|"from dept_nested_expanded as d,\n"
operator|+
literal|" UNNEST(d.employees) as t(employee)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3789">[CALCITE-3789]    * Support validation of UNNEST multiple array columns like Presto</a>.    */
annotation|@
name|Test
name|void
name|testAliasUnnestArrayPlanWithDoubleColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.deptno, e, k.empno\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.admins, d.employees) as t(e, k)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayOfRecord
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select employees[1].detail.skills[2+3].desc from dept_nested"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFlattenRecords
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select employees[1] from dept_nested"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testUnnestArrayNoExpand
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select name,\n"
operator|+
literal|"    array (select *\n"
operator|+
literal|"        from emp\n"
operator|+
literal|"        where deptno = dept.deptno) as emp_array,\n"
operator|+
literal|"    multiset (select *\n"
operator|+
literal|"        from emp\n"
operator|+
literal|"        where deptno = dept.deptno) as emp_multiset,\n"
operator|+
literal|"    map (select empno, job\n"
operator|+
literal|"        from emp\n"
operator|+
literal|"        where deptno = dept.deptno) as job_map\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|withExpand
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
name|void
name|testCorrelationJoin
parameter_list|()
block|{
name|checkCorrelationJoin
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelationJoinRex
parameter_list|()
block|{
name|checkCorrelationJoin
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|void
name|checkCorrelationJoin
parameter_list|(
name|boolean
name|expand
parameter_list|)
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
name|withExpand
argument_list|(
name|expand
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelatedArraySubQuery
parameter_list|()
block|{
name|checkCorrelatedArraySubQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelatedArraySubQueryRex
parameter_list|()
block|{
name|checkCorrelatedArraySubQuery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|void
name|checkCorrelatedArraySubQuery
parameter_list|(
name|boolean
name|expand
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *,\n"
operator|+
literal|"    array (select * from emp\n"
operator|+
literal|"        where deptno = dept.deptno) as empset\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
name|expand
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelatedMapSubQuery
parameter_list|()
block|{
name|checkCorrelatedMapSubQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCorrelatedMapSubQueryRex
parameter_list|()
block|{
name|checkCorrelatedMapSubQuery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|void
name|checkCorrelatedMapSubQuery
parameter_list|(
name|boolean
name|expand
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *,\n"
operator|+
literal|"  map (select empno, job\n"
operator|+
literal|"       from emp where deptno = dept.deptno) as jobMap\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
name|expand
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-864">[CALCITE-864]    * Correlation variable has incorrect row type if it is populated by right    * side of a Join</a>. */
annotation|@
name|Test
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
name|withExpand
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
name|withDecorrelate
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for<a href="https://issues.apache.org/jira/browse/CALCITE-4560">[CALCITE-4560]    * Wrong plan when decorrelating EXISTS subquery with COALESCE in the predicate</a>. */
annotation|@
name|Test
name|void
name|testExistsDecorrelateComplexCorrelationPredicate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select e1.empno from empnullables e1 where exists (\n"
operator|+
literal|"  select 1 from empnullables e2 where COALESCE(e1.ename,'M')=COALESCE(e2.ename,'M'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|void
name|testUniqueWithExpand
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where unique (select 1 from dept where deptno=55)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"UNIQUE is only supported if expand = false"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUniqueWithProjectLateral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where unique (select 1 from dept where deptno=55)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testUniqueWithOneProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where unique (select name from dept where deptno=55)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testUniqueWithManyProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where unique (select * from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testNotUnique
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where not unique (select 1 from dept where deptno=55)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testNotUniqueCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp where not unique (\n"
operator|+
literal|"  select 1 from dept where emp.deptno=dept.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|void
name|testAllValueList
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno> all (10, 20)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testSomeValueList
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno> some (10, 20)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testSome
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno> some (\n"
operator|+
literal|"  select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testSomeWithEquality
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno = some (\n"
operator|+
literal|"  select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testSomeWithNotEquality
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where deptno<> some (\n"
operator|+
literal|"  select deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|withExpand
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
name|void
name|testNotCaseInThreeClause
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where not case when "
operator|+
literal|"true then deptno in (10,20) else true end"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testNotCaseInMoreClause
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where not case when "
operator|+
literal|"true then deptno in (10,20) when false then false else deptno in (30,40) end"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|void
name|testNotCaseInWithoutElse
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno from emp where not case when "
operator|+
literal|"true then deptno in (10,20)  end"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withExpand
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|withDecorrelate
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
name|withDecorrelate
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
name|withDecorrelate
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
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
name|withDecorrelate
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
name|void
name|testIsDistinctFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno is distinct from deptno\n"
operator|+
literal|"from (values (cast(null as int), 1),\n"
operator|+
literal|"             (2, cast(null as int))) as emp(empno, deptno)"
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
name|void
name|testIsNotDistinctFrom
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empno is not distinct from deptno\n"
operator|+
literal|"from (values (cast(null as int), 1),\n"
operator|+
literal|"             (2, cast(null as int))) as emp(empno, deptno)"
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
name|void
name|testTableFunctionTumble
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(tumble(table Shipments, descriptor(rowtime), INTERVAL '1' MINUTE))"
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
name|void
name|testTableFunctionTumbleWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"tumble(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"  SIZE => INTERVAL '1' MINUTE))"
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
name|void
name|testTableFunctionTumbleWithParamReordered
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"tumble(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  SIZE => INTERVAL '1' MINUTE,\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime)))"
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
name|void
name|testTableFunctionTumbleWithInnerJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(tumble(table Shipments, descriptor(rowtime), INTERVAL '1' MINUTE)) a\n"
operator|+
literal|"join table(tumble(table Shipments, descriptor(rowtime), INTERVAL '1' MINUTE)) b\n"
operator|+
literal|"on a.orderid = b.orderid"
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
name|void
name|testTableFunctionTumbleWithOffset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(tumble(table Shipments, descriptor(rowtime),\n"
operator|+
literal|"  INTERVAL '10' MINUTE, INTERVAL '1' MINUTE))"
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
name|void
name|testTableFunctionHop
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(hop(table Shipments, descriptor(rowtime), "
operator|+
literal|"INTERVAL '1' MINUTE, INTERVAL '2' MINUTE))"
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
name|void
name|testTableFunctionHopWithOffset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(hop(table Shipments, descriptor(rowtime), "
operator|+
literal|"INTERVAL '1' MINUTE, INTERVAL '5' MINUTE, INTERVAL '3' MINUTE))"
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
name|void
name|testTableFunctionHopWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"hop(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"  SLIDE => INTERVAL '1' MINUTE,\n"
operator|+
literal|"  SIZE => INTERVAL '2' MINUTE))"
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
name|void
name|testTableFunctionHopWithParamReordered
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"hop(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  SLIDE => INTERVAL '1' MINUTE,\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"  SIZE => INTERVAL '2' MINUTE))"
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
name|void
name|testTableFunctionSession
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(session(table Shipments, descriptor(rowtime), "
operator|+
literal|"descriptor(orderId), INTERVAL '10' MINUTE))"
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
name|void
name|testTableFunctionSessionWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"session(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"  KEY => descriptor(orderId),\n"
operator|+
literal|"  SIZE => INTERVAL '10' MINUTE))"
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
name|void
name|testTableFunctionSessionWithParamReordered
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"session(\n"
operator|+
literal|"  DATA => table Shipments,\n"
operator|+
literal|"  KEY => descriptor(orderId),\n"
operator|+
literal|"  TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"  SIZE => INTERVAL '10' MINUTE))"
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
name|void
name|testTableFunctionTumbleWithSubQueryParam
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(tumble((select * from Shipments), descriptor(rowtime), INTERVAL '1' MINUTE))"
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
name|void
name|testTableFunctionHopWithSubQueryParam
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(hop((select * from Shipments), descriptor(rowtime), "
operator|+
literal|"INTERVAL '1' MINUTE, INTERVAL '2' MINUTE))"
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
name|void
name|testTableFunctionSessionWithSubQueryParam
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(session((select * from Shipments), descriptor(rowtime), "
operator|+
literal|"descriptor(orderId), INTERVAL '10' MINUTE))"
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
name|void
name|testTableFunctionSessionCompoundSessionKey
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(session(table Orders, descriptor(rowtime), "
operator|+
literal|"descriptor(orderId, productId), INTERVAL '10' MINUTE))"
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
name|void
name|testTableFunctionWithPartitionKey
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders partition by productid, 3))"
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
name|void
name|testTableFunctionWithMultiplePartitionKeys
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders partition by (orderId, productid), 3))"
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
name|void
name|testTableFunctionWithOrderKey
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders order by orderId, 3))"
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
name|void
name|testTableFunctionWithMultipleOrderKeys
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders order by (orderId, productid), 3))"
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
name|void
name|testTableFunctionWithComplexOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders order by (orderId desc, productid desc nulls last), 3))"
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
name|void
name|testTableFunctionWithOrderByWithNullLast
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders order by orderId desc nulls last, 3))"
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
name|void
name|testTableFunctionWithPartitionKeyAndOrderKey
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn(table orders partition by productid order by orderId, 3))"
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
name|void
name|testTableFunctionWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"topn(\n"
operator|+
literal|"  DATA => table orders partition by productid order by orderId,\n"
operator|+
literal|"  COL => 3))"
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
name|void
name|testTableFunctionWithSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(topn("
operator|+
literal|"select * from orders partition by productid order by orderId desc nulls last, 3))"
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
name|void
name|testTableFunctionWithSubQueryWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"topn(\n"
operator|+
literal|"  DATA => select * from orders partition by productid order by orderId nulls first,\n"
operator|+
literal|"  COL => 3))"
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
name|void
name|testTableFunctionWithMultipleInputTables
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"similarlity(\n"
operator|+
literal|"  table emp partition by deptno order by empno nulls first,\n"
operator|+
literal|"  table emp_b partition by deptno order by empno nulls first))"
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
name|void
name|testTableFunctionWithMultipleInputTablesWithParamNames
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\n"
operator|+
literal|"similarlity(\n"
operator|+
literal|"  LTABLE => table emp partition by deptno order by empno nulls first,\n"
operator|+
literal|"  RTABLE => table emp_b partition by deptno order by empno nulls first))"
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
annotation|@
name|Test
name|void
name|testOverDefaultBracket
parameter_list|()
block|{
comment|// c2 and c3 are equivalent to c1;
comment|// c5 is equivalent to c4;
comment|// c7 is equivalent to c6.
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  count(*) over (order by deptno) c1,\n"
operator|+
literal|"  count(*) over (order by deptno\n"
operator|+
literal|"    range unbounded preceding) c2,\n"
operator|+
literal|"  count(*) over (order by deptno\n"
operator|+
literal|"    range between unbounded preceding and current row) c3,\n"
operator|+
literal|"  count(*) over (order by deptno\n"
operator|+
literal|"    rows unbounded preceding) c4,\n"
operator|+
literal|"  count(*) over (order by deptno\n"
operator|+
literal|"    rows between unbounded preceding and current row) c5,\n"
operator|+
literal|"  count(*) over (order by deptno\n"
operator|+
literal|"    range between unbounded preceding and unbounded following) c6,\n"
operator|+
literal|" count(*) over (order by deptno\n"
operator|+
literal|"    rows between unbounded preceding and unbounded following) c7\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-750">[CALCITE-750]    * Allow windowed aggregate on top of regular aggregate</a>. */
annotation|@
name|Test
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
comment|/**    * Tests that a window with specifying null treatment.    */
annotation|@
name|Test
name|void
name|testOverNullTreatmentWindow
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"lead(deptno, 1) over w,\n "
operator|+
literal|"lead(deptno, 2) ignore nulls over w,\n"
operator|+
literal|"lead(deptno, 3) respect nulls over w,\n"
operator|+
literal|"lead(deptno, 1) over w,\n"
operator|+
literal|"lag(deptno, 2) ignore nulls over w,\n"
operator|+
literal|"lag(deptno, 2) respect nulls over w,\n"
operator|+
literal|"first_value(deptno) over w,\n"
operator|+
literal|"first_value(deptno) ignore nulls over w,\n"
operator|+
literal|"first_value(deptno) respect nulls over w,\n"
operator|+
literal|"last_value(deptno) over w,\n"
operator|+
literal|"last_value(deptno) ignore nulls over w,\n"
operator|+
literal|"last_value(deptno) respect nulls over w\n"
operator|+
literal|" from emp\n"
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
block|}
comment|/**    * Tests that a window with a FOLLOWING bound becomes BETWEEN CURRENT ROW    * AND FOLLOWING.    */
annotation|@
name|Test
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
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|toRel
argument_list|()
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
literal|"\t\t+(1, 2)\n"
operator|+
literal|"\t</Property>\n"
operator|+
literal|"\t<Property name=\"EXPR$1\">\n"
operator|+
literal|"\t\t3\n"
operator|+
literal|"\t</Property>\n"
operator|+
literal|"\t<Inputs>\n"
operator|+
literal|"\t\t<RelNode type=\"LogicalValues\">\n"
operator|+
literal|"\t\t\t<Property name=\"tuples\">\n"
operator|+
literal|"\t\t\t\t[{ true }]\n"
operator|+
literal|"\t\t\t</Property>\n"
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
annotation|@
name|Test
name|void
name|testExplainAsDot
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
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|toRel
argument_list|()
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
name|RelDotWriter
name|planWriter
init|=
operator|new
name|RelDotWriter
argument_list|(
name|pw
argument_list|,
name|SqlExplainLevel
operator|.
name|EXPPLAN_ATTRIBUTES
argument_list|,
literal|false
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
literal|"digraph {\n"
operator|+
literal|"\"LogicalValues\\ntuples = [{ true }]\\n\" -> \"LogicalProject\\nEXPR$0 = +(1, 2)"
operator|+
literal|"\\nEXPR$1 = 3\\n\" [label=\"0\"]\n"
operator|+
literal|"}\n"
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
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3183">[CALCITE-3183]    * Trimming method for Filter rel uses wrong traitSet</a>. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Test
name|void
name|testFilterAndSortWithTrim
parameter_list|()
block|{
comment|// Run query and save plan after trimming
specifier|final
name|String
name|sql
init|=
literal|"select count(a.EMPNO)\n"
operator|+
literal|"from (select * from emp order by sal limit 3) a\n"
operator|+
literal|"where a.EMPNO> 10 group by 2"
decl_stmt|;
name|RelNode
name|afterTrim
init|=
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|withFactory
argument_list|(
name|t
lambda|->
comment|// Create a customized test with RelCollation trait in the test
comment|// cluster.
name|t
operator|.
name|withPlannerFactory
argument_list|(
name|context
lambda|->
operator|new
name|MockRelOptPlanner
argument_list|(
name|Contexts
operator|.
name|empty
argument_list|()
argument_list|)
block|{
block_content|@Override public List<RelTraitDef> getRelTraitDefs(
argument_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelTraitSet
name|emptyTraitSet
parameter_list|()
block|{
return|return
name|RelTraitSet
operator|.
name|createEmpty
argument_list|()
operator|.
name|plus
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
block|}
block|)
end_class

begin_expr_stmt
unit|)
operator|.
name|toRel
argument_list|()
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Get Sort and Filter operators
end_comment

begin_decl_stmt
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|final
name|RelShuttleImpl
name|visitor
init|=
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalSort
name|sort
parameter_list|)
block|{
name|rels
operator|.
name|add
argument_list|(
name|sort
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|)
block|{
name|rels
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|filter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|visitor
operator|.
name|visit
argument_list|(
name|afterTrim
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Ensure sort and filter operators have consistent traitSet after trimming
end_comment

begin_expr_stmt
name|assertThat
argument_list|(
name|rels
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|RelTrait
name|filterCollation
init|=
name|rels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|RelTrait
name|sortCollation
init|=
name|rels
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertThat
argument_list|(
name|filterCollation
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertThat
argument_list|(
name|sortCollation
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertThat
argument_list|(
name|filterCollation
operator|.
name|satisfies
argument_list|(
name|sortCollation
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    @
name|Test
name|void
name|testRelShuttleForLogicalCalc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename from emp"
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|toRel
argument_list|()
decl_stmt|;
specifier|final
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|)
expr_stmt|;
specifier|final
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|calc
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelShuttleImpl
name|visitor
init|=
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalCalc
name|calc
parameter_list|)
block|{
name|RelNode
name|visitedRel
init|=
name|super
operator|.
name|visit
argument_list|(
name|calc
argument_list|)
decl_stmt|;
name|rels
operator|.
name|add
argument_list|(
name|visitedRel
argument_list|)
expr_stmt|;
return|return
name|visitedRel
return|;
block|}
block|}
decl_stmt|;
name|calc
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rels
operator|.
name|size
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
name|rels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|isA
argument_list|(
name|LogicalCalc
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRelShuttleForLogicalTableModify
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into emp select * from emp"
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|toRel
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelShuttleImpl
name|visitor
init|=
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalTableModify
name|modify
parameter_list|)
block|{
name|RelNode
name|visitedRel
init|=
name|super
operator|.
name|visit
argument_list|(
name|modify
argument_list|)
decl_stmt|;
name|rels
operator|.
name|add
argument_list|(
name|visitedRel
argument_list|)
expr_stmt|;
return|return
name|visitedRel
return|;
block|}
block|}
decl_stmt|;
name|rel
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rels
operator|.
name|size
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
name|rels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|isA
argument_list|(
name|LogicalTableModify
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Tests group-by CASE expression involving a non-query IN. */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Tests an aggregate function on a CASE expression involving a non-query    * IN. */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-753">[CALCITE-753]    * Test aggregate operators do not derive row types with duplicate column    * names</a>. */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Test aggregate function on a CASE expression involving IN with a    * sub-query.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-551">[CALCITE-551]    * Sub-query inside aggregate function</a>. */
end_comment

begin_function
annotation|@
name|Test
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
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelatedForOuterFields
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT ARRAY(SELECT dept.deptno)\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"LEFT OUTER JOIN dept\n"
operator|+
literal|"ON emp.empno = dept.deptno"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-614">[CALCITE-614]    * IN within CASE within GROUP BY gives AssertionError</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults(updated TIMESTAMP)\n"
operator|+
literal|" (ename, deptno, empno, updated, sal)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindExtendedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empdefaults(updated TIMESTAMP)\n"
operator|+
literal|" (ename, deptno, empno, updated, sal)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(updated TIMESTAMP)\n"
operator|+
literal|" (ename, deptno, empno, updated, sal)\n"
operator|+
literal|" values ('Fred', 20, 44, timestamp '2017-03-12 13:03:05', 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(updated TIMESTAMP)\n"
operator|+
literal|" (ename, deptno, empno, updated, sal)\n"
operator|+
literal|" values ('Fred', 20, 44, ?, 999999)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertWithSort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"select deptno, ename from emp order by ename"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertWithLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"select deptno, ename from emp order by ename limit 10"
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(note VARCHAR)\n"
operator|+
literal|"where note = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3229">[CALCITE-3229]    * UnsupportedOperationException for UPDATE with IN query</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUpdateSubQueryWithIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set empno = 1 where empno in (\n"
operator|+
literal|"  select empno from emp where empno=2)"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3292">[CALCITE-3292]    * NPE for UPDATE with IN query</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUpdateSubQueryWithIn1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set empno = 1 where emp.empno in (\n"
operator|+
literal|"  select emp.empno from emp where emp.empno=2)"
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
end_function

begin_comment
comment|/** Similar to {@link #testUpdateSubQueryWithIn()} but with not in instead of in. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUpdateSubQueryWithNotIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set empno = 1 where empno not in (\n"
operator|+
literal|"  select empno from emp where empno=2)"
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2\n"
operator|+
literal|"set sal = sal + 5000 where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2(updated TIMESTAMP)\n"
operator|+
literal|"set updated = timestamp '2017-03-12 13:03:05', sal = sal + 5000\n"
operator|+
literal|"where slacker = false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateBind2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp"
operator|+
literal|" set sal = ? where slacker = false"
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
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-1708"
argument_list|)
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-1708"
argument_list|)
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-985"
argument_list|)
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|withConformance
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
end_function

begin_function
annotation|@
name|Test
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
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|withExtendedTester
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2936">[CALCITE-2936]    * Existential sub-query that has aggregate without grouping key    * should be simplified to constant boolean expression</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSimplifyExistsAggregateSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1 where exists\n"
operator|+
literal|"(select avg(sal) from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSimplifyNotExistsAggregateSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT e1.empno\n"
operator|+
literal|"FROM emp e1 where not exists\n"
operator|+
literal|"(select avg(sal) from emp e2 where e1.empno = e2.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2936">[CALCITE-2936]    * Existential sub-query that has Values with at least 1 tuple    * should be simplified to constant boolean expression</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSimplifyExistsValuesSubQuery
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
literal|"where exists (values 10)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSimplifyNotExistsValuesSubQuery
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
literal|"where not exists (values 10)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testReduceConstExpr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(case when 'y' = 'n' then ename else 0.1 end) from emp"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testSubQueryNoExpand
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select (select empno from EMP where 1 = 0)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1799">[CALCITE-1799]    * "OR .. IN" sub-query conversion wrong</a>.    *    *<p>The problem is only fixed if you have {@code expand = false}.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSubQueryOr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp where deptno = 10 or deptno in (\n"
operator|+
literal|"    select dept.deptno from dept where deptno< 5)\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-695">[CALCITE-695]    * SqlSingleValueAggFunction is created when it may not be needed</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-710">[CALCITE-710]    * When look up sub-queries, perform the same logic as the way when ones were    * registered</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-694">[CALCITE-694]    * Scan HAVING clause for sub-queries and IN-lists</a> relating to IN.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-694">[CALCITE-694]    * Scan HAVING clause for sub-queries and IN-lists</a>, with a sub-query in    * the HAVING clause.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-716">[CALCITE-716]    * Scalar sub-query and aggregate function in SELECT or HAVING clause gives    * AssertionError</a>; variant involving HAVING clause.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-716">[CALCITE-716]    * Scalar sub-query and aggregate function in SELECT or HAVING clause gives    * AssertionError</a>; variant involving SELECT clause.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * window aggregate and ranking functions with grouped aggregates</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-847">[CALCITE-847]    * AVG window function in GROUP BY gives AssertionError</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving joins</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving HAVING clause</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-770">[CALCITE-770]    * variant involving join with sub-query that contains window function and    * GROUP BY</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1313">[CALCITE-1313]    * Validator should derive type of expression in ORDER BY</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/**    * Test case (correlated scalar aggregate sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1543">[CALCITE-1543]    * Correlated scalar sub-query with multiple aggregates gives    * AssertionError</a>. */
end_comment

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case (correlated EXISTS sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** A theta join condition, unlike the equi-join condition in    * {@link #testCorrelationExistsAndFilterRex()}, requires a value    * generator. */
end_comment

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case (correlated NOT EXISTS sub-query) for    *<a href="https://issues.apache.org/jira/browse/CALCITE-714">[CALCITE-714]    * When de-correlating, push join condition into sub-query</a>.    */
end_comment

begin_function
annotation|@
name|Test
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
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for decorrelating sub-query that has aggregate with    * grouping sets.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCorrelationAggregateGroupSets
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
literal|"  where e2.deptno = d1.deptno group by cube(comm, mgr))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelationInProjectionWithScan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select array(select e.deptno) from emp e"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelationInProjectionWithProjection
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select array(select e.deptno)\n"
operator|+
literal|"from (select deptno, ename from emp) e"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMultiCorrelationInProjectionWithProjection
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select cardinality(array(select e.deptno)), array(select e.ename)[0]\n"
operator|+
literal|"from (select deptno, ename from emp) e"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelationInProjectionWithCorrelatedProjection
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select cardinality(arr) from (\n"
operator|+
literal|"  select array(select e.deptno) arr from (\n"
operator|+
literal|"    select deptno, ename from emp) e)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
name|withConfig
argument_list|(
name|c
lambda|->
comment|// Don't prune the Project. We want to see columns "FO"."C1"& "C1".
name|c
operator|.
name|addRelBuilderConfigTransform
argument_list|(
name|c2
lambda|->
name|c2
operator|.
name|withPruneInputOfAggregate
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]    * Dynamic Table / Dynamic Star support</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSelectFromDynamicTable
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** As {@link #testSelectFromDynamicTable} but "SELECT *". */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSelectStarFromDynamicTable
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2080">[CALCITE-2080]    * Query with NOT IN operator and literal fails throws AssertionError: 'Cast    * for just nullability not allowed'</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testNotInWithLiteral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM SALES.NATION\n"
operator|+
literal|"WHERE n_name NOT IN\n"
operator|+
literal|"    (SELECT ''\n"
operator|+
literal|"     FROM SALES.NATION)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** As {@link #testSelectFromDynamicTable} but with ORDER BY. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testReferDynamicStarInSelectOB
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** As {@link #testSelectFromDynamicTable} but with join. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDynamicStarInTableJoin
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDynamicNestedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select t3.fake_q1['fake_col2'] as fake2\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select t2.fake_col as fake_q1\n"
operator|+
literal|"  from SALES.CUSTOMER as t2) as t3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2900">[CALCITE-2900]    * RelStructuredTypeFlattener generates wrong types on nested columns</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testNestedColumnType
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select empa.home_address.zip\n"
operator|+
literal|"from sales.emp_address empa\n"
operator|+
literal|"where empa.home_address.city = 'abc'"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2962">[CALCITE-2962]    * RelStructuredTypeFlattener generates wrong types for nested column when    * flattenProjection</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSelectNestedColumnType
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  char_length(coord.\"unit\") as unit_length\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      fname,\n"
operator|+
literal|"      coord\n"
operator|+
literal|"    from\n"
operator|+
literal|"      customer.contact_peek\n"
operator|+
literal|"    where\n"
operator|+
literal|"      coord.x> 1\n"
operator|+
literal|"      and coord.y> 1\n"
operator|+
literal|"  ) as view\n"
operator|+
literal|"where\n"
operator|+
literal|"  fname = 'john'"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedStructFieldAccess
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.skill['others']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedStructPrimitiveFieldAccess
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.skill['others']['a']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testFunctionWithStructInput
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_type(skill)\n"
operator|+
literal|"from sales.dept_nested"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFunctionForStructInput
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select collect(skill) as collect_skill,\n"
operator|+
literal|"  count(skill) as count_skill, count(*) as count_star,\n"
operator|+
literal|"  approx_count_distinct(skill) as approx_count_distinct_skill,\n"
operator|+
literal|"  max(skill) as max_skill, min(skill) as min_skill,\n"
operator|+
literal|"  any_value(skill) as any_value_skill\n"
operator|+
literal|"from sales.dept_nested"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFunctionForStructInputByName
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select collect(skill) as collect_skill,\n"
operator|+
literal|"  count(skill) as count_skill, count(*) as count_star,\n"
operator|+
literal|"  approx_count_distinct(skill) as approx_count_distinct_skill,\n"
operator|+
literal|"  max(skill) as max_skill, min(skill) as min_skill,\n"
operator|+
literal|"  any_value(skill) as any_value_skill\n"
operator|+
literal|"from sales.dept_nested group by name"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedPrimitiveFieldAccess
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.skill['desc']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayElementNestedPrimitive
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.employees[0]['empno']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayElementDoublyNestedPrimitive
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.employees[0]['detail']['skills'][0]['type']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayElementDoublyNestedStruct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dn.employees[0]['detail']['skills'][0]\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayElementThreeTimesNestedStruct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select dn.employees[0]['detail']['skills'][0]['others']\n"
operator|+
literal|"from sales.dept_nested dn"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3003">[CALCITE-3003]    * AssertionError when GROUP BY nested field</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupByNestedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  coord.x,\n"
operator|+
literal|"  coord_ne.sub.a,\n"
operator|+
literal|"  avg(coord.y)\n"
operator|+
literal|"from\n"
operator|+
literal|"  customer.contact_peek\n"
operator|+
literal|"group by\n"
operator|+
literal|"  coord_ne.sub.a,\n"
operator|+
literal|"  coord.x"
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
end_function

begin_comment
comment|/**    * Similar to {@link #testGroupByNestedColumn()},    * but with grouping sets.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupingSetsWithNestedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  coord.x,\n"
operator|+
literal|"  coord.\"unit\",\n"
operator|+
literal|"  coord_ne.sub.a,\n"
operator|+
literal|"  avg(coord.y)\n"
operator|+
literal|"from\n"
operator|+
literal|"  customer.contact_peek\n"
operator|+
literal|"group by\n"
operator|+
literal|"  grouping sets (\n"
operator|+
literal|"    (coord_ne.sub.a, coord.x, coord.\"unit\"),\n"
operator|+
literal|"    (coord.x, coord.\"unit\")\n"
operator|+
literal|"  )"
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
end_function

begin_comment
comment|/**    * Similar to {@link #testGroupByNestedColumn()},    * but with cube.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupByCubeWithNestedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  coord.x,\n"
operator|+
literal|"  coord.\"unit\",\n"
operator|+
literal|"  coord_ne.sub.a,\n"
operator|+
literal|"  avg(coord.y)\n"
operator|+
literal|"from\n"
operator|+
literal|"  customer.contact_peek\n"
operator|+
literal|"group by\n"
operator|+
literal|"  cube (coord_ne.sub.a, coord.x, coord.\"unit\")"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testDynamicSchemaUnnest
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select t1.c_nationkey, t3.fake_col3\n"
operator|+
literal|"from SALES.CUSTOMER as t1,\n"
operator|+
literal|"lateral (select t2 as fake_col3\n"
operator|+
literal|"         from unnest(t1.fake_col) as t2) as t3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarDynamicSchemaUnnest
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from SALES.CUSTOMER as t1,\n"
operator|+
literal|"lateral (select t2 as fake_col3\n"
operator|+
literal|"         from unnest(t1.fake_col) as t2) as t3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarDynamicSchemaUnnest2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from SALES.CUSTOMER as t1,\n"
operator|+
literal|"unnest(t1.fake_col) as t2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarDynamicSchemaUnnestNestedSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select t2.c1\n"
operator|+
literal|"from (select * from SALES.CUSTOMER) as t1,\n"
operator|+
literal|"unnest(t1.fake_col) as t2(c1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testReferDynamicStarInSelectWhereGB
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDynamicStarInJoinAndSubQ
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarJoinStaticDynTable
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGrpByColFromStarInSubQuery
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDynStarInExistSubQ
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]    * Create the a new DynamicRecordType, avoiding star expansion when working    * with this type</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSelectDynamicStarOrderBy
parameter_list|()
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
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1321">[CALCITE-1321]    * Configurable IN list size when converting IN clause to join</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testInToSemiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT empno\n"
operator|+
literal|"FROM emp AS e\n"
operator|+
literal|"WHERE cast(e.empno as bigint) in (130, 131, 132, 133, 134)"
decl_stmt|;
comment|// No conversion to join since less than IN-list size threshold 10
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|b
lambda|->
name|b
operator|.
name|withInSubQueryThreshold
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotConverted}"
argument_list|)
expr_stmt|;
comment|// Conversion to join since greater than IN-list size threshold 2
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|b
lambda|->
name|b
operator|.
name|withInSubQueryThreshold
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planConverted}"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4683">[CALCITE-4683]    * IN-list converted to JOIN throws type mismatch exception</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testInToSemiJoinWithNewProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (\n"
operator|+
literal|"SELECT '20210101' AS dt, deptno\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY deptno\n"
operator|+
literal|") t\n"
operator|+
literal|"WHERE cast(deptno as varchar) in ('1')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withInSubQueryThreshold
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1944">[CALCITE-1944]    * Window function applied to sub-query with dynamic star gets wrong    * plan</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWindowOnDynamicStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(n_nationkey) OVER w\n"
operator|+
literal|"FROM (SELECT * FROM SALES.NATION) subQry\n"
operator|+
literal|"WINDOW w AS (PARTITION BY REGION ORDER BY n_nationkey)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowAndGroupByWithDynamicStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT\n"
operator|+
literal|"n_regionkey,\n"
operator|+
literal|"MAX(MIN(n_nationkey)) OVER (PARTITION BY n_regionkey)\n"
operator|+
literal|"FROM (SELECT * FROM SALES.NATION)\n"
operator|+
literal|"GROUP BY n_regionkey"
decl_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
operator|new
name|SqlDelegatingConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGroupByAlias
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
operator|.
name|withDynamicTable
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2366">[CALCITE-2366]    * Add support for ANY_VALUE aggregate function</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAnyValueAggregateFunctionNoGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT any_value(empno) as anyempno FROM emp AS e"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testAnyValueAggregateFunctionGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT any_value(empno) as anyempno FROM emp AS e group by e.sal"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testSomeAndEveryAggregateFunctions
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT some(empno = 130) as someempnoexists,\n"
operator|+
literal|" every(empno> 0) as everyempnogtzero\n"
operator|+
literal|" FROM emp AS e group by e.sal"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testLarge
parameter_list|()
block|{
comment|// Size factor used to be 400, but lambdas use a lot of stack
specifier|final
name|int
name|x
init|=
literal|300
decl_stmt|;
specifier|final
name|SqlToRelFixture
name|fixture
init|=
name|fixture
argument_list|()
decl_stmt|;
name|SqlValidatorTest
operator|.
name|checkLarge
argument_list|(
name|x
argument_list|,
name|input
lambda|->
block|{
specifier|final
name|RelRoot
name|root
init|=
name|fixture
operator|.
name|withSql
argument_list|(
name|input
argument_list|)
operator|.
name|toRoot
argument_list|()
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
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPivot
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT mgr, deptno, job, sal FROM emp)\n"
operator|+
literal|"PIVOT (SUM(sal) AS ss, COUNT(*)\n"
operator|+
literal|"    FOR (job, deptno)\n"
operator|+
literal|"    IN (('CLERK', 10) AS c10, ('MANAGER', 20) AS m20))"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPivot2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM   (SELECT deptno, job, sal\n"
operator|+
literal|"        FROM   emp)\n"
operator|+
literal|"PIVOT  (SUM(sal) AS sum_sal, COUNT(*) AS \"COUNT\"\n"
operator|+
literal|"        FOR (job) IN ('CLERK', 'MANAGER' mgr, 'ANALYST' AS \"a\"))\n"
operator|+
literal|"ORDER BY deptno"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnpivot
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT INCLUDE NULLS (remuneration\n"
operator|+
literal|"  FOR remuneration_type IN (comm AS 'commission',\n"
operator|+
literal|"                            sal as 'salary'))"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognize1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    partition by job, sal\n"
operator|+
literal|"    order by job asc, sal desc, empno\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"      up as up.mgr> prev(up.mgr)) as mr"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from emp match_recognize (\n"
operator|+
literal|"  partition by job, sal\n"
operator|+
literal|"  order by job asc, sal desc\n"
operator|+
literal|"  measures MATCH_NUMBER() as match_num,\n"
operator|+
literal|"    CLASSIFIER() as var_match,\n"
operator|+
literal|"    STRT.mgr as start_nw,\n"
operator|+
literal|"    LAST(DOWN.mgr) as bottom_nw,\n"
operator|+
literal|"    LAST(up.mgr) as end_nw\n"
operator|+
literal|"  pattern (strt down+ up+)\n"
operator|+
literal|"  define\n"
operator|+
literal|"    down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"    up as up.mgr> prev(up.mgr)) as mr"
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1909">[CALCITE-1909]    * Output rowType of Match should include PARTITION BY and ORDER BY    * columns</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from emp match_recognize (\n"
operator|+
literal|"  partition by job\n"
operator|+
literal|"  order by sal\n"
operator|+
literal|"  measures MATCH_NUMBER() as match_num,\n"
operator|+
literal|"    CLASSIFIER() as var_match,\n"
operator|+
literal|"    STRT.mgr as start_nw,\n"
operator|+
literal|"    LAST(DOWN.mgr) as bottom_nw,\n"
operator|+
literal|"    LAST(up.mgr) as end_nw\n"
operator|+
literal|"  pattern (strt down+ up+)\n"
operator|+
literal|"  define\n"
operator|+
literal|"    down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"    up as up.mgr> prev(up.mgr)) as mr"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from emp match_recognize (\n"
operator|+
literal|"  partition by job\n"
operator|+
literal|"  order by sal\n"
operator|+
literal|"  measures MATCH_NUMBER() as match_num,\n"
operator|+
literal|"    CLASSIFIER() as var_match,\n"
operator|+
literal|"    STRT.mgr as start_nw,\n"
operator|+
literal|"    LAST(DOWN.mgr) as bottom_nw,\n"
operator|+
literal|"    LAST(up.mgr) as end_nw\n"
operator|+
literal|"  ALL ROWS PER MATCH\n"
operator|+
literal|"  pattern (strt down+ up+)\n"
operator|+
literal|"  define\n"
operator|+
literal|"    down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"    up as up.mgr> prev(up.mgr)) as mr"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to next row\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"      up as up.mgr> NEXT(up.mgr)\n"
operator|+
literal|"  ) mr"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizeSubset1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.mgr< PREV(down.mgr),\n"
operator|+
literal|"      up as up.mgr> NEXT(up.mgr)\n"
operator|+
literal|"  ) mr"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizePrevLast
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
literal|"MATCH_RECOGNIZE (\n"
operator|+
literal|"  MEASURES\n"
operator|+
literal|"    STRT.mgr AS start_mgr,\n"
operator|+
literal|"    LAST(DOWN.mgr) AS bottom_mgr,\n"
operator|+
literal|"    LAST(UP.mgr) AS end_mgr\n"
operator|+
literal|"  ONE ROW PER MATCH\n"
operator|+
literal|"  PATTERN (STRT DOWN+ UP+)\n"
operator|+
literal|"  DEFINE\n"
operator|+
literal|"    DOWN AS DOWN.mgr< PREV(DOWN.mgr),\n"
operator|+
literal|"    UP AS UP.mgr> PREV(LAST(DOWN.mgr, 1), 1)\n"
operator|+
literal|") AS T"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizePrevDown
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
literal|"MATCH_RECOGNIZE (\n"
operator|+
literal|"  MEASURES\n"
operator|+
literal|"    STRT.mgr AS start_mgr,\n"
operator|+
literal|"    LAST(DOWN.mgr) AS up_days,\n"
operator|+
literal|"    LAST(UP.mgr) AS total_days\n"
operator|+
literal|"  PATTERN (STRT DOWN+ UP+)\n"
operator|+
literal|"  DEFINE\n"
operator|+
literal|"    DOWN AS DOWN.mgr< PREV(DOWN.mgr),\n"
operator|+
literal|"    UP AS UP.mgr> PREV(DOWN.mgr)\n"
operator|+
literal|") AS T"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPrevClassifier
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
literal|"MATCH_RECOGNIZE (\n"
operator|+
literal|"  MEASURES\n"
operator|+
literal|"    STRT.mgr AS start_mgr,\n"
operator|+
literal|"    LAST(DOWN.mgr) AS up_days,\n"
operator|+
literal|"    LAST(UP.mgr) AS total_days\n"
operator|+
literal|"  PATTERN (STRT DOWN? UP+)\n"
operator|+
literal|"  DEFINE\n"
operator|+
literal|"    DOWN AS DOWN.mgr< PREV(DOWN.mgr),\n"
operator|+
literal|"    UP AS CASE\n"
operator|+
literal|"            WHEN PREV(CLASSIFIER()) = 'STRT'\n"
operator|+
literal|"              THEN UP.mgr> 15\n"
operator|+
literal|"            ELSE\n"
operator|+
literal|"              UP.mgr> 20\n"
operator|+
literal|"            END\n"
operator|+
literal|") AS T"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testMatchRecognizeIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from emp match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    partition by job, sal\n"
operator|+
literal|"    order by job asc, sal desc, empno\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.mgr in (0, 1),\n"
operator|+
literal|"      up as up.mgr> prev(up.mgr)) as mr"
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2323">[CALCITE-2323]    * Validator should allow alternative nullCollations for ORDER BY in    * OVER</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUserDefinedOrderByOverLow
parameter_list|()
block|{
name|checkUserDefinedOrderByOver
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUserDefinedOrderByOverHigh
parameter_list|()
block|{
name|checkUserDefinedOrderByOver
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUserDefinedOrderByOverFirst
parameter_list|()
block|{
name|checkUserDefinedOrderByOver
argument_list|(
name|NullCollation
operator|.
name|FIRST
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUserDefinedOrderByOverLast
parameter_list|()
block|{
name|checkUserDefinedOrderByOver
argument_list|(
name|NullCollation
operator|.
name|LAST
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
name|void
name|checkUserDefinedOrderByOver
parameter_list|(
name|NullCollation
name|nullCollation
parameter_list|)
block|{
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|"  rank() over (partition by empno order by comm desc)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"order by row_number() over (partition by empno order by comm)"
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|CalciteConnectionProperty
operator|.
name|DEFAULT_NULL_COLLATION
operator|.
name|camelName
argument_list|()
argument_list|,
name|nullCollation
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CalciteConnectionConfigImpl
name|connectionConfig
init|=
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|false
argument_list|)
operator|.
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withDefaultNullCollation
argument_list|(
name|connectionConfig
operator|.
name|defaultNullCollation
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonValueExpressionOperator
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename format json,\n"
operator|+
literal|"ename format json encoding utf8,\n"
operator|+
literal|"ename format json encoding utf16,\n"
operator|+
literal|"ename format json encoding utf32\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_exists(ename, 'lax $')\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_value(ename, 'lax $')\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_query(ename, 'lax $')\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonType
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_type(ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonPretty
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_pretty(ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonDepth
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_depth(ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonLength
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_length(ename, 'strict $')\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonKeys
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_keys(ename, 'strict $')\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArray
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_array(ename, ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArrayAgg1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_arrayagg(ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArrayAgg2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_arrayagg(ename order by ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArrayAgg3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_arrayagg(ename order by ename null on null)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArrayAgg4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_arrayagg(ename null on null) within group (order by ename)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonObject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_object(ename: deptno, ename: deptno)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonObjectAgg
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select json_objectagg(ename: deptno)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonPredicate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"ename is json,\n"
operator|+
literal|"ename is json value,\n"
operator|+
literal|"ename is json object,\n"
operator|+
literal|"ename is json array,\n"
operator|+
literal|"ename is json scalar,\n"
operator|+
literal|"ename is not json,\n"
operator|+
literal|"ename is not json value,\n"
operator|+
literal|"ename is not json object,\n"
operator|+
literal|"ename is not json array,\n"
operator|+
literal|"ename is not json scalar\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonNestedJsonObjectConstructor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"json_object(\n"
operator|+
literal|"  'key1' :\n"
operator|+
literal|"  json_object(\n"
operator|+
literal|"    'key2' :\n"
operator|+
literal|"    ename)),\n"
operator|+
literal|"  json_object(\n"
operator|+
literal|"    'key3' :\n"
operator|+
literal|"    json_array(12, 'hello', deptno))\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonNestedJsonArrayConstructor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"json_array(\n"
operator|+
literal|"  json_object(\n"
operator|+
literal|"    'key1' :\n"
operator|+
literal|"    json_object(\n"
operator|+
literal|"      'key2' :\n"
operator|+
literal|"       ename)),\n"
operator|+
literal|"  json_array(12, 'hello', deptno))\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonNestedJsonObjectAggConstructor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"json_object(\n"
operator|+
literal|"  'k2' :\n"
operator|+
literal|"  json_objectagg(\n"
operator|+
literal|"    ename :\n"
operator|+
literal|"    json_object(\n"
operator|+
literal|"      'k1' :\n"
operator|+
literal|"      deptno)))\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonNestedJsonArrayAggConstructor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"json_object(\n"
operator|+
literal|"  'k2' :\n"
operator|+
literal|"  json_arrayagg(\n"
operator|+
literal|"    json_object(\n"
operator|+
literal|"      ename :\n"
operator|+
literal|"      deptno)))\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithinGroup1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group (order by deptno, hiredate desc)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithinGroup2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select dept.deptno,\n"
operator|+
literal|" collect(sal) within group (order by sal desc) as s,\n"
operator|+
literal|" collect(sal) within group (order by 1)as s1,\n"
operator|+
literal|" collect(sal) within group (order by sal)\n"
operator|+
literal|"  filter (where sal> 2000) as s2\n"
operator|+
literal|"from emp\n"
operator|+
literal|"join dept using (deptno)\n"
operator|+
literal|"group by dept.deptno"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithinGroup3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group (order by empno not in (1, 2)), count(*)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testModeFunction
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select mode(deptno)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testModeFunctionWithWinAgg
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, ename,\n"
operator|+
literal|"  mode(job) over (partition by deptno order by ename)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4644">[CALCITE-4644]    * Add PERCENTILE_CONT and PERCENTILE_DISC aggregate functions</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentileCont
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_cont(0.25) within group (order by deptno)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileContWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|" percentile_cont(0.25) within group (order by empno desc)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileDisc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_disc(0.25) within group (order by deptno)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileDiscWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno,\n"
operator|+
literal|" percentile_disc(0.25) within group (order by empno)\n"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrderByRemoval1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|"  select empno from emp order by deptno offset 0) t\n"
operator|+
literal|"order by empno desc"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrderByRemoval2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|"  select empno from emp order by deptno offset 1) t\n"
operator|+
literal|"order by empno desc"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrderByRemoval3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|"  select empno from emp order by deptno limit 10) t\n"
operator|+
literal|"order by empno"
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
end_function

begin_comment
comment|/** Tests LEFT JOIN LATERAL with USING. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftJoinLateral1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values 4) as t(c)\n"
operator|+
literal|" left join lateral\n"
operator|+
literal|" (select c,a*c from (values 2) as s(a)) as r(d,c)\n"
operator|+
literal|" using(c)"
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
end_function

begin_comment
comment|/** Tests LEFT JOIN LATERAL with NATURAL JOIN. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftJoinLateral2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values 4) as t(c)\n"
operator|+
literal|" natural left join lateral\n"
operator|+
literal|" (select c,a*c from (values 2) as s(a)) as r(d,c)"
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
end_function

begin_comment
comment|/** Tests LEFT JOIN LATERAL with ON condition. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftJoinLateral3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values 4) as t(c)\n"
operator|+
literal|" left join lateral\n"
operator|+
literal|" (select c,a*c from (values 2) as s(a)) as r(d,c)\n"
operator|+
literal|" on t.c=r.c"
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
end_function

begin_comment
comment|/** Tests LEFT JOIN LATERAL with multiple columns from outer. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftJoinLateral4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (4,5)) as t(c,d)\n"
operator|+
literal|" left join lateral\n"
operator|+
literal|" (select c,a*c from (values 2) as s(a)) as r(d,c)\n"
operator|+
literal|" on t.c+t.d=r.c"
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
end_function

begin_comment
comment|/** Tests LEFT JOIN LATERAL with correlating variable coming    * from one level up join scope. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftJoinLateral5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values 4) as t (c)\n"
operator|+
literal|"left join lateral\n"
operator|+
literal|"  (select f1+b1 from (values 2) as foo(f1)\n"
operator|+
literal|"    join\n"
operator|+
literal|"  (select c+1 from (values 3)) as bar(b1)\n"
operator|+
literal|"  on f1=b1)\n"
operator|+
literal|"as r(n) on c=n"
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
end_function

begin_comment
comment|/** Tests CROSS JOIN LATERAL with multiple columns from outer. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCrossJoinLateral1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values (4,5)) as t(c,d)\n"
operator|+
literal|" cross join lateral\n"
operator|+
literal|" (select c,a*c as f from (values 2) as s(a)\n"
operator|+
literal|" where c+d=a*c)"
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
end_function

begin_comment
comment|/** Tests CROSS JOIN LATERAL with correlating variable coming    * from one level up join scope. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCrossJoinLateral2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (values 4) as t (c)\n"
operator|+
literal|"cross join lateral\n"
operator|+
literal|"(select * from (\n"
operator|+
literal|"  select f1+b1 from (values 2) as foo(f1)\n"
operator|+
literal|"    join\n"
operator|+
literal|"  (select c+1 from (values 3)) as bar(b1)\n"
operator|+
literal|"  on f1=b1\n"
operator|+
literal|") as r(n) where c=n)"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithinDistinct1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select avg(empno) within distinct (deptno)\n"
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
end_function

begin_comment
comment|/** Test case for:    *<a href="https://issues.apache.org/jira/browse/CALCITE-3310">[CALCITE-3310]    * Approximate and exact aggregate calls are recognized as the same    * during sql-to-rel conversion</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testProjectApproximateAndExactAggregates
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT empno, count(distinct ename),\n"
operator|+
literal|"approx_count_distinct(ename)\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY empno"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testProjectAggregatesIgnoreNullsAndNot
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select lead(sal, 4) IGNORE NULLS, lead(sal, 4) over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3456">[CALCITE-3456]    * AssertionError throws when aggregation same digest in sub-query in same    * scope</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAggregateWithSameDigestInSubQueries
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  CASE WHEN job IN ('810000', '820000') THEN job\n"
operator|+
literal|"  ELSE 'error'\n"
operator|+
literal|"  END AS job_name,\n"
operator|+
literal|"  count(empno)\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"where job<> '' or job IN ('810000', '820000')\n"
operator|+
literal|"GROUP by deptno, job"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|addRelBuilderConfigTransform
argument_list|(
name|c2
lambda|->
name|c2
operator|.
name|withPruneInputOfAggregate
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3575">[CALCITE-3575]    * IndexOutOfBoundsException when converting SQL to rel</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPushDownJoinConditionWithProjectMerge
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|" (select empno, deptno from emp) a\n"
operator|+
literal|" join dept b\n"
operator|+
literal|"on a.deptno + 20 = b.deptno"
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2997">[CALCITE-2997]    * Avoid pushing down join condition in SqlToRelConverter</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDoNotPushDownJoinCondition
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
literal|"join dept as d on e.deptno + 20 = d.deptno / 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|addRelBuilderConfigTransform
argument_list|(
name|b
lambda|->
name|b
operator|.
name|withPushJoinCondition
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** As {@link #testDoNotPushDownJoinCondition()}. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPushDownJoinCondition
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
literal|"join dept as d on e.deptno + 20 = d.deptno / 2"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testCoalesceOnNullableField
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select coalesce(mgr, 0) from emp"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4145">[CALCITE-4145]    * Exception when query from UDF field with structured type</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUdfWithStructuredReturnType
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, tmp.r.f0, tmp.r.f1 FROM\n"
operator|+
literal|"(SELECT deptno, STRUCTURED_FUNC() AS r from dept)tmp"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3826">[CALCITE-3826]    * UPDATE assigns wrong type to bind variables</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDynamicParamTypesInUpdate
parameter_list|()
block|{
name|RelNode
name|rel
init|=
name|sql
argument_list|(
literal|"update emp set sal = ?, ename = ? where empno = ?"
argument_list|)
operator|.
name|toRel
argument_list|()
decl_stmt|;
name|LogicalTableModify
name|modify
init|=
operator|(
name|LogicalTableModify
operator|)
name|rel
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|parameters
init|=
name|modify
operator|.
name|getSourceExpressionList
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|parameters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parameters
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parameters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parameters
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4167">[CALCITE-4167]    * Group by COALESCE IN throws NullPointerException</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupByCoalesceIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select case when coalesce(ename, 'a') in ('1', '2')\n"
operator|+
literal|"then 'CKA' else 'QT' END, count(distinct deptno) from emp\n"
operator|+
literal|"group by case when coalesce(ename, 'a') in ('1', '2') then 'CKA' else 'QT' END"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testSortInSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (select empno from emp order by empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planRemoveSort}"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withRemoveSortInSubQuery
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planKeepSort}"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimUnionAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimUnionDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"union\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimIntersectAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimIntersectDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"intersect\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimExceptAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"except all\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTrimExceptDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select deptno from\n"
operator|+
literal|"(select ename, deptno from emp\n"
operator|+
literal|"except\n"
operator|+
literal|"select name, deptno from dept)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinWithOnConditionQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT emp.deptno, emp.sal\n"
operator|+
literal|"FROM dept\n"
operator|+
literal|"JOIN emp\n"
operator|+
literal|"ON (SELECT AVG(emp.sal)> 0 FROM emp)"
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinExpandAndDecorrelation
parameter_list|()
block|{
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT emp.deptno, emp.sal\n"
operator|+
literal|"FROM dept\n"
operator|+
literal|"JOIN emp ON emp.deptno = dept.deptno AND emp.sal< (\n"
operator|+
literal|"  SELECT AVG(emp.sal)\n"
operator|+
literal|"  FROM emp\n"
operator|+
literal|"  WHERE  emp.deptno = dept.deptno\n"
operator|+
literal|")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|configBuilder
lambda|->
name|configBuilder
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|withDecorrelationEnabled
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planExpanded}"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConfig
argument_list|(
name|configBuilder
lambda|->
name|configBuilder
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelationEnabled
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotExpanded}"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testImplicitJoinExpandAndDecorrelation
parameter_list|()
block|{
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT emp.deptno, emp.sal\n"
operator|+
literal|"FROM dept, emp "
operator|+
literal|"WHERE emp.deptno = dept.deptno AND emp.sal< (\n"
operator|+
literal|"  SELECT AVG(emp.sal)\n"
operator|+
literal|"  FROM emp\n"
operator|+
literal|"  WHERE  emp.deptno = dept.deptno\n"
operator|+
literal|")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planExpanded}"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|false
argument_list|)
operator|.
name|convertsTo
argument_list|(
literal|"${planNotExpanded}"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4295">[CALCITE-4295]    * Composite of two checker with SqlOperandCountRange throws IllegalArgumentException</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCompositeOfCountRange
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select COMPOSITE(deptno)\n"
operator|+
literal|"from dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|true
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInWithConstantList
parameter_list|()
block|{
name|String
name|expr
init|=
literal|"1 in (1,2,3)"
decl_stmt|;
name|expr
argument_list|(
name|expr
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFunctionExprInOver
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select ename, row_number() over(partition by char_length(ename)\n"
operator|+
literal|" order by deptno desc) as rn\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withFactory
argument_list|(
name|t
lambda|->
name|t
operator|.
name|withValidatorConfig
argument_list|(
name|config
lambda|->
name|config
operator|.
name|withIdentifierExpansion
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withTrim
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5089">[CALCITE-5089]    * Allow GROUP BY ALL or DISTINCT set quantifier on GROUPING SETS</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupByDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, job, count(*)\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY DISTINCT\n"
operator|+
literal|"CUBE (deptno, job),\n"
operator|+
literal|"ROLLUP (deptno, job)"
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
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5089">[CALCITE-5089]    * Allow GROUP BY ALL or DISTINCT set quantifier on GROUPING SETS</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupByAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT deptno, job, count(*)\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY ALL\n"
operator|+
literal|"CUBE (deptno, job),\n"
operator|+
literal|"ROLLUP (deptno, job)"
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5045">[CALCITE-5045]    * Alias within GroupingSets throws type mis-match exception</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAliasWithinGroupingSets
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT empno / 2 AS x\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY ROLLUP(x)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5145">[CALCITE-5145]    * CASE statement within GROUPING SETS throws type mis-match exception</a>.    */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testCaseAliasWithinGroupingSets
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT empno,\n"
operator|+
literal|"CASE\n"
operator|+
literal|"WHEN ename in ('Fred','Eric') THEN 'CEO'\n"
operator|+
literal|"ELSE 'Other'\n"
operator|+
literal|"END AS derived_col\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY GROUPING SETS ((empno, derived_col),(empno))"
argument_list|)
operator|.
name|withConformance
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5145">[CALCITE-5145]    * CASE statement within GROUPING SETS throws type mis-match exception</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseWithinGroupingSets
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT empno,\n"
operator|+
literal|"CASE WHEN ename IN ('Fred','Eric') THEN 'Manager' ELSE 'Other' END\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"GROUP BY GROUPING SETS (\n"
operator|+
literal|"(empno, CASE WHEN ename IN ('Fred','Eric') THEN 'Manager' ELSE 'Other' END),\n"
operator|+
literal|"(empno)\n"
operator|+
literal|")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withConformance
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
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5297">[CALCITE-5297]    * Casting dynamic variable twice throws exception</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDynamicParameterDoubleCast
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT CAST(CAST(? AS INTEGER) AS CHAR)"
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
end_function

unit|}
end_unit

