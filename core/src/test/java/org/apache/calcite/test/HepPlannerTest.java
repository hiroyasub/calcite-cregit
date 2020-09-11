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
name|RelOptListener
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
name|RelOptMaterialization
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
name|HepMatchOrder
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
name|logical
operator|.
name|LogicalIntersect
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
name|LogicalUnion
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
name|CoerceInputsRule
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
name|sql
operator|.
name|SqlExplainLevel
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|isLinux
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * HepPlannerTest is a unit test for {@link HepPlanner}. See  * {@link RelOptRulesTest} for an explanation of how to add tests; the tests in  * this class are targeted at exercising the planner, and use specific rules for  * convenience only, whereas the tests in that class are targeted at exercising  * specific rules, and use the planner for convenience only. Hence the split.  */
end_comment

begin_class
class|class
name|HepPlannerTest
extends|extends
name|RelOptTestBase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|String
name|UNION_TREE
init|=
literal|"(select name from dept union select ename from emp)"
operator|+
literal|" union (select ename from bonus)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMPLEX_UNION_TREE
init|=
literal|"select * from (\n"
operator|+
literal|"  select ENAME, 50011895 as cat_id, '1' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50011895 union all\n"
operator|+
literal|"  select ENAME, 50013023 as cat_id, '2' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013023 union all\n"
operator|+
literal|"  select ENAME, 50013032 as cat_id, '3' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013032 union all\n"
operator|+
literal|"  select ENAME, 50013024 as cat_id, '4' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013024 union all\n"
operator|+
literal|"  select ENAME, 50004204 as cat_id, '5' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50004204 union all\n"
operator|+
literal|"  select ENAME, 50013043 as cat_id, '6' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013043 union all\n"
operator|+
literal|"  select ENAME, 290903 as cat_id, '7' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 290903 union all\n"
operator|+
literal|"  select ENAME, 50008261 as cat_id, '8' as cat_name, 1 as require_free_postage, 0 as require_15return, 0 as require_48hour,1 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50008261 union all\n"
operator|+
literal|"  select ENAME, 124478013 as cat_id, '9' as cat_name, 0 as require_free_postage, 0 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 124478013 union all\n"
operator|+
literal|"  select ENAME, 124472005 as cat_id, '10' as cat_name, 0 as require_free_postage, 0 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 124472005 union all\n"
operator|+
literal|"  select ENAME, 50013475 as cat_id, '11' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013475 union all\n"
operator|+
literal|"  select ENAME, 50018263 as cat_id, '12' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50018263 union all\n"
operator|+
literal|"  select ENAME, 50013498 as cat_id, '13' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50013498 union all\n"
operator|+
literal|"  select ENAME, 350511 as cat_id, '14' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350511 union all\n"
operator|+
literal|"  select ENAME, 50019790 as cat_id, '15' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50019790 union all\n"
operator|+
literal|"  select ENAME, 50015382 as cat_id, '16' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50015382 union all\n"
operator|+
literal|"  select ENAME, 350503 as cat_id, '17' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350503 union all\n"
operator|+
literal|"  select ENAME, 350401 as cat_id, '18' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 350401 union all\n"
operator|+
literal|"  select ENAME, 50015560 as cat_id, '19' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50015560 union all\n"
operator|+
literal|"  select ENAME, 122658003 as cat_id, '20' as cat_name, 0 as require_free_postage, 1 as require_15return, 1 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 122658003 union all\n"
operator|+
literal|"  select ENAME, 50022371 as cat_id, '100' as cat_name, 0 as require_free_postage, 0 as require_15return, 0 as require_48hour,0 as require_insurance from emp where EMPNO = 20171216 and MGR = 0 and ENAME = 'Y' and SAL = 50022371\n"
operator|+
literal|") a"
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
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
name|HepPlannerTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
name|void
name|testRuleClass
parameter_list|()
block|{
comment|// Verify that an entire class of rules can be applied.
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
name|addRuleClass
argument_list|(
name|CoerceInputsRule
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|addRule
argument_list|(
name|CoerceInputsRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|withCoerceNames
argument_list|(
literal|false
argument_list|)
operator|.
name|withConsumerRelClass
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|)
operator|.
name|toRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|CoerceInputsRule
operator|.
name|Config
operator|.
name|DEFAULT
operator|.
name|withCoerceNames
argument_list|(
literal|false
argument_list|)
operator|.
name|withConsumerRelClass
argument_list|(
name|LogicalIntersect
operator|.
name|class
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"CoerceInputsRule:Intersection"
argument_list|)
comment|// TODO
operator|.
name|toRule
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"(select name from dept union select ename from emp)\n"
operator|+
literal|"intersect (select fname from customer.contact)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|planner
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRuleDescription
parameter_list|()
block|{
comment|// Verify that a rule can be applied via its description.
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
name|addRuleByDescription
argument_list|(
literal|"FilterToCalcRule"
argument_list|)
expr_stmt|;
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
name|addRule
argument_list|(
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select name from sales.dept where deptno=12"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|planner
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/**    * Ensures {@link org.apache.calcite.rel.AbstractRelNode} digest does not include    * full digest tree.    */
annotation|@
name|Test
name|void
name|relDigestLength
parameter_list|()
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
literal|10
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"select * from ("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"select name from sales.dept"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" union all select name from sales.dept"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|RelRoot
name|root
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|root
operator|.
name|rel
argument_list|)
expr_stmt|;
name|RelNode
name|best
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
comment|// Good digest should look like rel#66:LogicalProject(input=rel#64:LogicalUnion)
comment|// Bad digest includes full tree like rel#66:LogicalProject(input=rel#64:LogicalUnion(...))
comment|// So the assertion is to ensure digest includes LogicalUnion exactly once
name|assertIncludesExactlyOnce
argument_list|(
literal|"best.getDescription()"
argument_list|,
name|best
operator|.
name|toString
argument_list|()
argument_list|,
literal|"LogicalUnion"
argument_list|)
expr_stmt|;
name|assertIncludesExactlyOnce
argument_list|(
literal|"best.getDigest()"
argument_list|,
name|best
operator|.
name|getDigest
argument_list|()
argument_list|,
literal|"LogicalUnion"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPlanToDot
parameter_list|()
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|RelRoot
name|root
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select name from sales.dept"
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|root
operator|.
name|rel
argument_list|)
expr_stmt|;
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
name|planner
operator|.
name|getRoot
argument_list|()
operator|.
name|explain
argument_list|(
name|planWriter
argument_list|)
expr_stmt|;
name|String
name|planStr
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|planStr
argument_list|,
name|isLinux
argument_list|(
literal|"digraph {\n"
operator|+
literal|"\"LogicalTableScan\\ntable = [CATALOG, SA\\nLES, DEPT]\\n\" -> "
operator|+
literal|"\"LogicalProject\\nNAME = $1\\n\" [label=\"0\"]\n"
operator|+
literal|"}\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertIncludesExactlyOnce
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|digest
parameter_list|,
name|String
name|substring
parameter_list|)
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|>=
literal|0
condition|)
block|{
name|pos
operator|=
name|digest
operator|.
name|indexOf
argument_list|(
name|substring
argument_list|,
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|cnt
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cnt
argument_list|,
parameter_list|()
lambda|->
name|message
operator|+
literal|" should include<<"
operator|+
name|substring
operator|+
literal|">> exactly once"
operator|+
literal|", actual value is "
operator|+
name|digest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchLimitOneTopDown
parameter_list|()
block|{
comment|// Verify that only the top union gets rewritten.
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
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|UNION_TREE
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchLimitOneBottomUp
parameter_list|()
block|{
comment|// Verify that only the bottom union gets rewritten.
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
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|BOTTOM_UP
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|UNION_TREE
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchUntilFixpoint
parameter_list|()
block|{
comment|// Verify that both unions get rewritten.
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
name|addMatchLimit
argument_list|(
name|HepProgram
operator|.
name|MATCH_UNTIL_FIXPOINT
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|UNION_TREE
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testReplaceCommonSubexpression
parameter_list|()
block|{
comment|// Note that here it may look like the rule is firing
comment|// twice, but actually it's only firing once on the
comment|// common sub-expression.  The purpose of this test
comment|// is to make sure the planner can deal with
comment|// rewriting something used as a common sub-expression
comment|// twice by the same parent (the join in this case).
specifier|final
name|String
name|sql
init|=
literal|"select d1.deptno from (select * from dept) d1,\n"
operator|+
literal|"(select * from dept) d2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withRule
argument_list|(
name|CoreRules
operator|.
name|PROJECT_REMOVE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that if two relational expressions are equivalent, the planner    * notices, and only applies the rule once. */
annotation|@
name|Test
name|void
name|testCommonSubExpression
parameter_list|()
block|{
comment|// In the following,
comment|//   (select 1 from dept where abs(-1)=20)
comment|// occurs twice, but it's a common sub-expression, so the rule should only
comment|// apply once.
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
name|FILTER_TO_CALC
argument_list|)
expr_stmt|;
specifier|final
name|HepTestListener
name|listener
init|=
operator|new
name|HepTestListener
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|addListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"(select 1 from dept where abs(-1)=20)\n"
operator|+
literal|"union all\n"
operator|+
literal|"(select 1 from dept where abs(-1)=20)"
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sql
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|RelNode
name|bestRel
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|bestRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|bestRel
operator|.
name|getInput
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|listener
operator|.
name|getApplyTimes
argument_list|()
operator|==
literal|1
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubprogram
parameter_list|()
block|{
comment|// Verify that subprogram gets re-executed until fixpoint.
comment|// In this case, the first time through we limit it to generate
comment|// only one calc; the second time through it will generate
comment|// a second calc, and then merge them.
name|HepProgramBuilder
name|subprogramBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|subprogramBuilder
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addMatchLimit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|CALC_MERGE
argument_list|)
expr_stmt|;
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
name|addSubprogram
argument_list|(
name|subprogramBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select upper(ename) from\n"
operator|+
literal|"(select lower(ename) as ename from emp where empno = 100)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroup
parameter_list|()
block|{
comment|// Verify simultaneous application of a group of rules.
comment|// Intentionally add them in the wrong order to make sure
comment|// that order doesn't matter within the group.
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
name|addGroupBegin
argument_list|()
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|CALC_MERGE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addGroupEnd
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select upper(name) from dept where deptno=20"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGC
parameter_list|()
block|{
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
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|TOP_DOWN
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|CALC_MERGE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|)
expr_stmt|;
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
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select upper(name) from dept where deptno=20"
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
comment|// Reuse of HepPlanner (should trigger GC).
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select upper(name) from dept where deptno=20"
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRelNodeCacheWithDigest
parameter_list|()
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|String
name|query
init|=
literal|"(select n_nationkey from SALES.CUSTOMER) union all\n"
operator|+
literal|"(select n_name from CUSTOMER_MODIFIABLEVIEW)"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withTester
argument_list|(
name|t
lambda|->
name|createDynamicTester
argument_list|()
argument_list|)
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|with
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|planner
argument_list|)
operator|.
name|checkUnchanged
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRuleApplyCount
parameter_list|()
block|{
specifier|final
name|long
name|applyTimes1
init|=
name|checkRuleApplyCount
argument_list|(
name|HepMatchOrder
operator|.
name|ARBITRARY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|applyTimes1
argument_list|,
name|is
argument_list|(
literal|316L
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|applyTimes2
init|=
name|checkRuleApplyCount
argument_list|(
name|HepMatchOrder
operator|.
name|DEPTH_FIRST
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|applyTimes2
argument_list|,
name|is
argument_list|(
literal|87L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaterialization
parameter_list|()
block|{
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|HepProgram
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|RelNode
name|tableRel
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
literal|"select * from dept"
argument_list|)
operator|.
name|rel
decl_stmt|;
name|RelNode
name|queryRel
init|=
name|tableRel
decl_stmt|;
name|RelOptMaterialization
name|mat1
init|=
operator|new
name|RelOptMaterialization
argument_list|(
name|tableRel
argument_list|,
name|queryRel
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"default"
argument_list|,
literal|"mv"
argument_list|)
argument_list|)
decl_stmt|;
name|planner
operator|.
name|addMaterialization
argument_list|(
name|mat1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|planner
operator|.
name|getMaterializations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|planner
operator|.
name|getMaterializations
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|mat1
argument_list|)
expr_stmt|;
name|planner
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|planner
operator|.
name|getMaterializations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|checkRuleApplyCount
parameter_list|(
name|HepMatchOrder
name|matchOrder
parameter_list|)
block|{
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
name|addMatchOrder
argument_list|(
name|matchOrder
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_REDUCE_EXPRESSIONS
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_REDUCE_EXPRESSIONS
argument_list|)
expr_stmt|;
specifier|final
name|HepTestListener
name|listener
init|=
operator|new
name|HepTestListener
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|addListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|COMPLEX_UNION_TREE
argument_list|)
operator|.
name|rel
argument_list|)
expr_stmt|;
name|planner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
return|return
name|listener
operator|.
name|getApplyTimes
argument_list|()
return|;
block|}
comment|/** Listener for HepPlannerTest; counts how many times rules fire. */
specifier|private
specifier|static
class|class
name|HepTestListener
implements|implements
name|RelOptListener
block|{
specifier|private
name|long
name|applyTimes
decl_stmt|;
name|HepTestListener
parameter_list|(
name|long
name|applyTimes
parameter_list|)
block|{
name|this
operator|.
name|applyTimes
operator|=
name|applyTimes
expr_stmt|;
block|}
name|long
name|getApplyTimes
parameter_list|()
block|{
return|return
name|applyTimes
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|relEquivalenceFound
parameter_list|(
name|RelEquivalenceEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|ruleAttempted
parameter_list|(
name|RuleAttemptedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|isBefore
argument_list|()
condition|)
block|{
operator|++
name|applyTimes
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ruleProductionSucceeded
parameter_list|(
name|RuleProductionEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|relDiscarded
parameter_list|(
name|RelDiscardedEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|relChosen
parameter_list|(
name|RelChosenEvent
name|event
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

