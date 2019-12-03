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
name|plan
operator|.
name|volcano
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
name|Convention
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelOptRuleCall
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
name|RelOptRuleOperand
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|util
operator|.
name|List
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|GoodSingleRule
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|NoneLeafRel
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|NoneSingleRel
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|PHYS_CALLING_CONVENTION
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|PhysLeafRel
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|PhysSingleRel
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|TestSingleRel
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
name|plan
operator|.
name|volcano
operator|.
name|PlannerTests
operator|.
name|newCluster
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link VolcanoPlanner}  */
end_comment

begin_class
specifier|public
class|class
name|ComboRuleTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testCombo
parameter_list|()
block|{
name|VolcanoPlanner
name|planner
init|=
operator|new
name|VolcanoPlanner
argument_list|()
decl_stmt|;
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
name|addRule
argument_list|(
operator|new
name|ComboRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|AddIntermediateNodeRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|GoodSingleRule
argument_list|()
argument_list|)
expr_stmt|;
name|RelOptCluster
name|cluster
init|=
name|newCluster
argument_list|(
name|planner
argument_list|)
decl_stmt|;
name|NoneLeafRel
name|leafRel
init|=
operator|new
name|NoneLeafRel
argument_list|(
name|cluster
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|NoneSingleRel
name|singleRel
init|=
operator|new
name|NoneSingleRel
argument_list|(
name|cluster
argument_list|,
name|leafRel
argument_list|)
decl_stmt|;
name|NoneSingleRel
name|singleRel2
init|=
operator|new
name|NoneSingleRel
argument_list|(
name|cluster
argument_list|,
name|singleRel
argument_list|)
decl_stmt|;
name|RelNode
name|convertedRel
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|singleRel2
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|convertedRel
argument_list|)
expr_stmt|;
name|RelNode
name|result
init|=
name|planner
operator|.
name|chooseDelegate
argument_list|()
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|IntermediateNode
argument_list|)
expr_stmt|;
block|}
comment|/** Intermediate node, the cost decreases as it is pushed up the tree    * (more inputs it has, cheaper it gets). */
specifier|private
specifier|static
class|class
name|IntermediateNode
extends|extends
name|TestSingleRel
block|{
specifier|final
name|int
name|nodesBelowCount
decl_stmt|;
name|IntermediateNode
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|int
name|nodesBelowCount
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodesBelowCount
operator|=
name|nodesBelowCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|1.0
operator|/
name|nodesBelowCount
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|comprises
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
assert|;
return|return
operator|new
name|IntermediateNode
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|nodesBelowCount
argument_list|)
return|;
block|}
block|}
comment|/** Rule that adds an intermediate node above the {@link PhysLeafRel}. */
specifier|private
specifier|static
class|class
name|AddIntermediateNodeRule
extends|extends
name|RelOptRule
block|{
name|AddIntermediateNodeRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|NoneLeafRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|PHYS_CALLING_CONVENTION
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|NoneLeafRel
name|leaf
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|physLeaf
init|=
operator|new
name|PhysLeafRel
argument_list|(
name|leaf
operator|.
name|getCluster
argument_list|()
argument_list|,
name|leaf
operator|.
name|label
argument_list|)
decl_stmt|;
name|RelNode
name|intermediateNode
init|=
operator|new
name|IntermediateNode
argument_list|(
name|physLeaf
operator|.
name|getCluster
argument_list|()
argument_list|,
name|physLeaf
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|intermediateNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Matches {@link PhysSingleRel}-{@link IntermediateNode}-Any    * and converts to {@link IntermediateNode}-{@link PhysSingleRel}-Any. */
specifier|private
specifier|static
class|class
name|ComboRule
extends|extends
name|RelOptRule
block|{
name|ComboRule
parameter_list|()
block|{
name|super
argument_list|(
name|createOperand
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|RelOptRuleOperand
name|createOperand
parameter_list|()
block|{
name|RelOptRuleOperand
name|input
init|=
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
decl_stmt|;
name|input
operator|=
name|operand
argument_list|(
name|IntermediateNode
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|=
name|operand
argument_list|(
name|PhysSingleRel
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|input
return|;
block|}
annotation|@
name|Override
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|PHYS_CALLING_CONVENTION
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|<
literal|3
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|PhysSingleRel
operator|&&
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|IntermediateNode
operator|&&
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
operator|instanceof
name|RelNode
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|IntermediateNode
name|oldInter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelNode
name|physRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
operator|.
name|copy
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newInputs
argument_list|)
decl_stmt|;
name|RelNode
name|converted
init|=
operator|new
name|IntermediateNode
argument_list|(
name|physRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|physRel
argument_list|,
name|oldInter
operator|.
name|nodesBelowCount
operator|+
literal|1
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

