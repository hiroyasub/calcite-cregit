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
name|core
operator|.
name|RelFactories
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
name|mutable
operator|.
name|MutableRel
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
name|mutable
operator|.
name|MutableRels
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
name|FilterJoinRule
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
name|FilterProjectTransposeRule
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
name|FilterToCalcRule
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
name|ProjectMergeRule
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
name|ProjectToWindowRule
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
name|SemiJoinRule
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
name|sql2rel
operator|.
name|RelDecorrelator
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
name|tools
operator|.
name|RelBuilder
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
name|Assert
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Tests for {@link MutableRel} sub-classes.  */
end_comment

begin_class
specifier|public
class|class
name|MutableRelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConvertAggregate
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Aggregate"
argument_list|,
literal|"select empno, sum(sal) from emp group by empno"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertFilter
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Filter"
argument_list|,
literal|"select * from emp where ename = 'DUMMY'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertProject
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Project"
argument_list|,
literal|"select ename from emp"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertSort
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Sort"
argument_list|,
literal|"select * from emp order by ename"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertCalc
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Calc"
argument_list|,
literal|"select * from emp where ename = 'DUMMY'"
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertWindow
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Window"
argument_list|,
literal|"select sal, avg(sal) over (partition by deptno) from emp"
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ProjectToWindowRule
operator|.
name|PROJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertCollect
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Collect"
argument_list|,
literal|"select multiset(select deptno from dept) from (values(true))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertUncollect
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Uncollect"
argument_list|,
literal|"select * from unnest(multiset[1,2])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertTableModify
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"TableModify"
argument_list|,
literal|"insert into dept select empno, ename from emp"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertSample
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Sample"
argument_list|,
literal|"select * from emp tablesample system(50) where empno> 5"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertTableFunctionScan
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"TableFunctionScan"
argument_list|,
literal|"select * from table(ramp(3))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertValues
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Values"
argument_list|,
literal|"select * from (values (1, 2))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertJoin
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Join"
argument_list|,
literal|"select * from emp join dept using (deptno)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertSemiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept where exists (\n"
operator|+
literal|"  select * from emp\n"
operator|+
literal|"  where emp.deptno = dept.deptno\n"
operator|+
literal|"  and emp.sal> 100)"
decl_stmt|;
name|checkConvertMutableRel
argument_list|(
literal|"Join"
argument_list|,
comment|// with join type as semi
name|sql
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|FilterProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|FilterJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|,
name|ProjectMergeRule
operator|.
name|INSTANCE
argument_list|,
name|SemiJoinRule
operator|.
name|PROJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertCorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept where exists (\n"
operator|+
literal|"  select * from emp\n"
operator|+
literal|"  where emp.deptno = dept.deptno\n"
operator|+
literal|"  and emp.sal> 100)"
decl_stmt|;
name|checkConvertMutableRel
argument_list|(
literal|"Correlate"
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertUnion
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Union"
argument_list|,
literal|"select * from emp where deptno = 10"
operator|+
literal|"union select * from emp where ename like 'John%'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMinus
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Minus"
argument_list|,
literal|"select * from emp where deptno = 10"
operator|+
literal|"except select * from emp where ename like 'John%'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertIntersect
parameter_list|()
block|{
name|checkConvertMutableRel
argument_list|(
literal|"Intersect"
argument_list|,
literal|"select * from emp where deptno = 10"
operator|+
literal|"intersect select * from emp where ename like 'John%'"
argument_list|)
expr_stmt|;
block|}
comment|/** Verifies that after conversion to and from a MutableRel, the new    * RelNode remains identical to the original RelNode. */
specifier|private
specifier|static
name|void
name|checkConvertMutableRel
parameter_list|(
name|String
name|rel
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|checkConvertMutableRel
argument_list|(
name|rel
argument_list|,
name|sql
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Verifies that after conversion to and from a MutableRel, the new    * RelNode remains identical to the original RelNode. */
specifier|private
specifier|static
name|void
name|checkConvertMutableRel
parameter_list|(
name|String
name|rel
parameter_list|,
name|String
name|sql
parameter_list|,
name|boolean
name|decorrelate
parameter_list|,
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
specifier|final
name|SqlToRelTestBase
name|test
init|=
operator|new
name|SqlToRelTestBase
argument_list|()
block|{     }
decl_stmt|;
name|RelNode
name|origRel
init|=
name|test
operator|.
name|createTester
argument_list|()
operator|.
name|convertSqlToRel
argument_list|(
name|sql
argument_list|)
operator|.
name|rel
decl_stmt|;
if|if
condition|(
name|decorrelate
condition|)
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|origRel
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|origRel
operator|=
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|origRel
argument_list|,
name|relBuilder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rules
operator|!=
literal|null
condition|)
block|{
specifier|final
name|HepProgram
name|hepProgram
init|=
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleCollection
argument_list|(
name|rules
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|hepProgram
argument_list|)
decl_stmt|;
name|hepPlanner
operator|.
name|setRoot
argument_list|(
name|origRel
argument_list|)
expr_stmt|;
name|origRel
operator|=
name|hepPlanner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
block|}
comment|// Convert to and from a mutable rel.
specifier|final
name|MutableRel
name|mutableRel
init|=
name|MutableRels
operator|.
name|toMutable
argument_list|(
name|origRel
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|newRel
init|=
name|MutableRels
operator|.
name|fromMutable
argument_list|(
name|mutableRel
argument_list|)
decl_stmt|;
comment|// Check if the mutable rel digest contains the target rel.
specifier|final
name|String
name|mutableRelStr
init|=
name|mutableRel
operator|.
name|deep
argument_list|()
decl_stmt|;
specifier|final
name|String
name|msg1
init|=
literal|"Mutable rel: "
operator|+
name|mutableRelStr
operator|+
literal|" does not contain target rel: "
operator|+
name|rel
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|msg1
argument_list|,
name|mutableRelStr
operator|.
name|contains
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check if the mutable rel's row-type is identical to the original
comment|// rel's row-type.
specifier|final
name|RelDataType
name|origRelType
init|=
name|origRel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|mutableRelType
init|=
name|mutableRel
operator|.
name|rowType
decl_stmt|;
specifier|final
name|String
name|msg2
init|=
literal|"Mutable rel's row type does not match with the original rel.\n"
operator|+
literal|"Original rel type: "
operator|+
name|origRelType
operator|+
literal|";\nMutable rel type: "
operator|+
name|mutableRelType
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|msg2
argument_list|,
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"origRelType"
argument_list|,
name|origRelType
argument_list|,
literal|"mutableRelType"
argument_list|,
name|mutableRelType
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check if the new rel converted from the mutable rel is identical
comment|// to the original rel.
specifier|final
name|String
name|origRelStr
init|=
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|origRel
argument_list|)
decl_stmt|;
specifier|final
name|String
name|newRelStr
init|=
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|newRel
argument_list|)
decl_stmt|;
specifier|final
name|String
name|msg3
init|=
literal|"The converted new rel is different from the original rel.\n"
operator|+
literal|"Original rel: "
operator|+
name|origRelStr
operator|+
literal|";\nNew rel: "
operator|+
name|newRelStr
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|msg3
argument_list|,
name|origRelStr
argument_list|,
name|newRelStr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MutableRelTest.java
end_comment

end_unit

