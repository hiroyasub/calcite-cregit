begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|hep
operator|.
name|*
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

begin_comment
comment|/**  * HepPlannerTest is a unit test for {@link HepPlanner}. See {@link  * RelOptRulesTest} for an explanation of how to add tests; the tests in this  * class are targeted at exercising the planner, and use specific rules for  * convenience only, whereas the tests in that class are targeted at exercising  * specific rules, and use the planner for convenience only. Hence the split.  */
end_comment

begin_class
specifier|public
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
specifier|public
name|void
name|testRuleClass
parameter_list|()
throws|throws
name|Exception
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
operator|new
name|CoerceInputsRule
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|CoerceInputsRule
argument_list|(
name|IntersectRel
operator|.
name|class
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|planner
argument_list|,
literal|"(select name from dept union select ename from emp)"
operator|+
literal|" intersect (select fname from customer.contact)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRuleDescription
parameter_list|()
throws|throws
name|Exception
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
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|planner
argument_list|,
literal|"select name from sales.dept where deptno=12"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchLimitOneTopDown
parameter_list|()
throws|throws
name|Exception
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
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchLimitOneBottomUp
parameter_list|()
throws|throws
name|Exception
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
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchUntilFixpoint
parameter_list|()
throws|throws
name|Exception
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
name|UnionToDistinctRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|UNION_TREE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceCommonSubexpression
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note that here it may look like the rule is firing
comment|// twice, but actually it's only firing once on the
comment|// common subexpression.  The purpose of this test
comment|// is to make sure the planner can deal with
comment|// rewriting something used as a common subexpression
comment|// twice by the same parent (the join in this case).
name|checkPlanning
argument_list|(
name|RemoveTrivialProjectRule
operator|.
name|INSTANCE
argument_list|,
literal|"select d1.deptno from (select * from dept) d1,"
operator|+
literal|" (select * from dept) d2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubprogram
parameter_list|()
throws|throws
name|Exception
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
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|subprogramBuilder
operator|.
name|addRuleInstance
argument_list|(
name|MergeCalcRule
operator|.
name|INSTANCE
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
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|"select upper(ename) from (select lower(ename) as ename from emp)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
throws|throws
name|Exception
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
name|MergeCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|programBuilder
operator|.
name|addGroupEnd
argument_list|()
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|"select upper(name) from dept where deptno=20"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End HepPlannerTest.java
end_comment

end_unit

