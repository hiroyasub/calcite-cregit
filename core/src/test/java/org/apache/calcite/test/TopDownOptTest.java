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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRules
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
name|ConventionTraitDef
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
name|RelOptCluster
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
name|RelOptRule
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
name|volcano
operator|.
name|VolcanoPlanner
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
name|rules
operator|.
name|JoinCommuteRule
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
name|JoinPushThroughJoinRule
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
name|ProjectSortTransposeRule
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
name|SortProjectTransposeRule
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Unit test for top-down optimization.  *  *<p>As input, the test supplies a SQL statement and rules; the SQL is  * translated into relational algebra and then fed into a  * {@link VolcanoPlanner}. The plan before and after "optimization" is  * diffed against a .ref file using {@link DiffRepository}.  *  *<p>Procedure for adding a new test case:  *  *<ol>  *<li>Add a new public test method for your rule, following the existing  * examples. You'll have to come up with an SQL statement to which your rule  * will apply in a meaningful way. See  * {@link org.apache.calcite.test.catalog.MockCatalogReaderSimple} class  * for details on the schema.  *  *<li>Run the test. It should fail. Inspect the output in  * {@code target/surefire/.../TopDownOptTest.xml}.  *  *<li>Verify that the "planBefore" is the correct  * translation of your SQL, and that it contains the pattern on which your rule  * is supposed to fire. If all is well, replace  * {@code src/test/resources/.../TopDownOptTest.xml} and  * with the new {@code target/surefire/.../TopDownOptTest.xml}.  *  *<li>Run the test again. It should fail again, but this time it should contain  * a "planAfter" entry for your rule. Verify that your rule applied its  * transformation correctly, and then update the  * {@code src/test/resources/.../TopDownOptTest.xml} file again.  *  *<li>Run the test one last time; this time it should pass.  *</ol>  */
end_comment

begin_class
class|class
name|TopDownOptTest
extends|extends
name|RelOptTestBase
block|{
annotation|@
name|Test
name|void
name|testSortAgg
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select mgr, count(*) from sales.emp\n"
operator|+
literal|"group by mgr order by mgr desc nulls last limit 5"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortAggPartialKey
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select mgr,deptno,comm,count(*) from sales.emp\n"
operator|+
literal|"group by mgr,deptno,comm\n"
operator|+
literal|"order by comm desc nulls last, deptno nulls first"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortMergeJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"sales.emp r join sales.bonus s on r.ename=s.ename and r.job=s.job\n"
operator|+
literal|"order by r.job desc nulls last, r.ename nulls first"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortMergeJoinRight
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"sales.emp r join sales.bonus s on r.ename=s.ename and r.job=s.job\n"
operator|+
literal|"order by s.job desc nulls last, s.ename nulls first"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeJoinDeriveLeft1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, job, max(sal) from sales.emp group by ename, job) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeJoinDeriveLeft2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, job, mgr, max(sal) from sales.emp group by ename, job, mgr) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeJoinDeriveRight1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.bonus s join\n"
operator|+
literal|"(select ename, job, max(sal) from sales.emp group by ename, job) r\n"
operator|+
literal|"on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeJoinDeriveRight2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from sales.bonus s join\n"
operator|+
literal|"(select ename, job, mgr, max(sal) from sales.emp group by ename, job, mgr) r\n"
operator|+
literal|"on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test if "order by mgr desc nulls last" can be pushed through the projection ("select mgr").
annotation|@
name|Test
name|void
name|testSortProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select mgr from sales.emp order by mgr desc nulls last"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test that Sort cannot push through projection because of non-trival call
comment|// (e.g. RexCall(sal * -1)). In this example, the reason is that "sal * -1"
comment|// creates opposite ordering if Sort is pushed down.
annotation|@
name|Test
name|void
name|testSortProjectOnRexCall
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename, sal * -1 as sal, mgr from\n"
operator|+
literal|"sales.emp order by ename desc, sal desc, mgr desc nulls last"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test that Sort can push through projection when cast is monotonic.
annotation|@
name|Test
name|void
name|testSortProjectWhenCastLeadingToMonotonic
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from sales.emp order by cast(deptno as float) desc"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test that Sort cannot push through projection when cast is not monotonic.
annotation|@
name|Test
name|void
name|testSortProjectWhenCastLeadingToNonMonotonic
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from sales.emp order by cast(deptno as varchar) desc"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// No sort on left join input.
annotation|@
name|Test
name|void
name|testSortProjectDeriveWhenCastLeadingToMonotonic
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, cast(job as varchar) as job, max_sal + 1 from\n"
operator|+
literal|"(select ename, job, max(sal) as max_sal from sales.emp group by ename, job) t) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// need sort on left join input.
annotation|@
name|Test
name|void
name|testSortProjectDeriveOnRexCall
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, sal * -1 as sal, max_job from\n"
operator|+
literal|"(select ename, sal, max(job) as max_job from sales.emp group by ename, sal) t) r\n"
operator|+
literal|"join sales.bonus s on r.sal=s.sal and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// need sort on left join input.
annotation|@
name|Test
name|void
name|testSortProjectDeriveWhenCastLeadingToNonMonotonic
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, cast(job as numeric) as job, max_sal + 1 from\n"
operator|+
literal|"(select ename, job, max(sal) as max_sal from sales.emp group by ename, job) t) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// no Sort need for left join input.
annotation|@
name|Test
name|void
name|testSortProjectDerive3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, cast(job as varchar) as job, sal + 1 from\n"
operator|+
literal|"(select ename, job, sal from sales.emp limit 100) t) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// need Sort on left join input.
annotation|@
name|Test
name|void
name|testSortProjectDerive4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, cast(job as bigint) as job, sal + 1 from\n"
operator|+
literal|"(select ename, job, sal from sales.emp limit 100) t) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test if top projection can enforce sort when inner sort cannot produce satisfying ordering.
annotation|@
name|Test
name|void
name|testSortProjectDerive5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename, empno*-1, job from\n"
operator|+
literal|"(select * from sales.emp order by ename, empno, job limit 10) order by ename, job"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortProjectDerive
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, job, max_sal + 1 from\n"
operator|+
literal|"(select ename, job, max(sal) as max_sal from sales.emp group by ename, job) t) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// need Sort on projection.
annotation|@
name|Test
name|void
name|testSortProjectDerive2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct ename, sal*-2, mgr\n"
operator|+
literal|"from (select ename, mgr, sal from sales.emp order by ename, mgr, sal limit 100) t"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortProjectDerive6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select comm, deptno, slacker from\n"
operator|+
literal|"(select * from sales.emp order by comm, deptno, slacker limit 10) t\n"
operator|+
literal|"order by comm, slacker"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test traits push through filter.
annotation|@
name|Test
name|void
name|testSortFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ename, job, mgr, max_sal from\n"
operator|+
literal|"(select ename, job, mgr, max(sal) as max_sal from sales.emp group by ename, job, mgr) as t\n"
operator|+
literal|"where max_sal> 1000\n"
operator|+
literal|"order by mgr desc, ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|// test traits derivation in filter.
annotation|@
name|Test
name|void
name|testSortFilterDerive
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select ename, job, max_sal from\n"
operator|+
literal|"(select ename, job, max(sal) as max_sal from sales.emp group by ename, job) t where job> 1000) r\n"
operator|+
literal|"join sales.bonus s on r.job=s.job and r.ename=s.ename"
decl_stmt|;
name|Query
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|)
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**  * A helper class that creates Volcano planner with top-down optimization enabled. This class  * allows easy-to-add and easy-to-remove rules from the planner.  */
end_comment

begin_class
class|class
name|Query
extends|extends
name|RelOptTestBase
block|{
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
name|TopDownOptTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|private
name|String
name|sql
decl_stmt|;
specifier|private
name|VolcanoPlanner
name|planner
decl_stmt|;
specifier|private
name|Query
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
name|planner
operator|=
operator|new
name|VolcanoPlanner
argument_list|()
expr_stmt|;
comment|// Always use top-down optimization
name|planner
operator|.
name|setTopDownOpt
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|RelOptUtil
operator|.
name|registerDefaultRules
argument_list|(
name|planner
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Remove to Keep deterministic join order.
name|planner
operator|.
name|removeRule
argument_list|(
name|JoinCommuteRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|JoinPushThroughJoinRule
operator|.
name|LEFT
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|JoinPushThroughJoinRule
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
comment|// Always use sorted agg.
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
comment|// pushing down sort should be handled by top-down optimization.
name|planner
operator|.
name|removeRule
argument_list|(
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|ProjectSortTransposeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Query
name|create
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Query
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
name|Query
name|addRule
parameter_list|(
name|RelOptRule
name|ruleToAdd
parameter_list|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|ruleToAdd
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Query
name|addRules
parameter_list|(
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rulesToAdd
parameter_list|)
block|{
for|for
control|(
name|RelOptRule
name|ruleToAdd
range|:
name|rulesToAdd
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|ruleToAdd
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|Query
name|removeRule
parameter_list|(
name|RelOptRule
name|ruleToRemove
parameter_list|)
block|{
name|planner
operator|.
name|removeRule
argument_list|(
name|ruleToRemove
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Query
name|removeRules
parameter_list|(
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rulesToRemove
parameter_list|)
block|{
for|for
control|(
name|RelOptRule
name|ruleToRemove
range|:
name|rulesToRemove
control|)
block|{
name|planner
operator|.
name|removeRule
argument_list|(
name|ruleToRemove
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|void
name|check
parameter_list|()
block|{
name|SqlToRelTestBase
operator|.
name|Tester
name|tester
init|=
name|createTester
argument_list|()
operator|.
name|withDecorrelation
argument_list|(
literal|true
argument_list|)
operator|.
name|withClusterFactory
argument_list|(
name|cluster
lambda|->
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
name|planner
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

