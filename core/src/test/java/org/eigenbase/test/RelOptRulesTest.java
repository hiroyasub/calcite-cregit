begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|rules
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
comment|/**  * Unit test for rules in {@link org.eigenbase.rel} and subpackages.  *  *<p>As input, the test supplies a SQL statement and a single rule; the SQL is  * translated into relational algebra and then fed into a {@link  * org.eigenbase.relopt.hep.HepPlanner}. The planner fires the rule on every  * pattern match in a depth-first left-to-right preorder traversal of the tree  * for as long as the rule continues to succeed in applying its transform. (For  * rules which call transformTo more than once, only the last result is used.)  * The plan before and after "optimization" is diffed against a .ref file using  * {@link DiffRepository}.  *  *<p>Procedure for adding a new test case:  *  *<ol>  *<li>Add a new public test method for your rule, following the existing  * examples. You'll have to come up with an SQL statement to which your rule  * will apply in a meaningful way. See {@link SqlToRelTestBase} class comments  * for details on the schema.  *<li>Run the test. It should fail. Inspect the output in  * RelOptRulesTest.log.xml; verify that the "planBefore" is the correct  * translation of your SQL, and that it contains the pattern on which your rule  * is supposed to fire. If all is well, check out RelOptRulesTest.ref.xml and  * replace it with the new .log.xml.  *<li>Run the test again. It should fail again, but this time it should contain  * a "planAfter" entry for your rule. Verify that your rule applied its  * transformation correctly, and then update the .ref.xml file again.  *<li>Run the test one last time; this time it should pass.  *</ol>  */
end_comment

begin_class
specifier|public
class|class
name|RelOptRulesTest
extends|extends
name|RelOptTestBase
block|{
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
name|RelOptRulesTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionToDistinctRule
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|UnionToDistinctRule
operator|.
name|instance
argument_list|,
literal|"select * from dept union select * from dept"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractJoinFilterRule
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|ExtractJoinFilterRule
operator|.
name|instance
argument_list|,
literal|"select 1 from emp inner join dept on emp.deptno=dept.deptno"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRedundantSemiJoinRule
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|AddRedundantSemiJoinRule
operator|.
name|instance
argument_list|,
literal|"select 1 from emp inner join dept on emp.deptno = dept.deptno"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushFilterThroughOuterJoin
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushFilterPastJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|,
literal|"select 1 from sales.dept d left outer join sales.emp e"
operator|+
literal|" on d.deptno = e.deptno"
operator|+
literal|" where d.name = 'Charlie'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReduceAverage
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|ReduceAggregatesRule
operator|.
name|instance
argument_list|,
literal|"select name, max(name), avg(deptno), min(name)"
operator|+
literal|" from sales.dept group by name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushProjectPastFilter
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushProjectPastFilterRule
operator|.
name|instance
argument_list|,
literal|"select empno + deptno from emp where sal = 10 * comm "
operator|+
literal|"and upper(ename) = 'FOO'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushProjectPastJoin
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushProjectPastJoinRule
operator|.
name|instance
argument_list|,
literal|"select e.sal + b.comm from emp e inner join bonus b "
operator|+
literal|"on e.ename = b.ename and e.deptno = 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushProjectPastSetOp
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushProjectPastSetOpRule
operator|.
name|instance
argument_list|,
literal|"select sal from "
operator|+
literal|"(select * from emp e1 union all select * from emp e2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushJoinThroughUnionOnLeft
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushJoinThroughUnionRule
operator|.
name|instanceUnionOnLeft
argument_list|,
literal|"select r1.sal from "
operator|+
literal|"(select * from emp e1 union all select * from emp e2) r1, "
operator|+
literal|"emp r2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushJoinThroughUnionOnRight
parameter_list|()
block|{
name|checkPlanning
argument_list|(
name|PushJoinThroughUnionRule
operator|.
name|instanceUnionOnRight
argument_list|,
literal|"select r1.sal from "
operator|+
literal|"emp r1, "
operator|+
literal|"(select * from emp e1 union all select * from emp e2) r2"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptRulesTest.java
end_comment

end_unit

