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
name|RelRule
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
name|volcano
operator|.
name|AbstractConverter
operator|.
name|ExpandConversionRule
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
name|TestLeafRel
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
comment|/**  * Unit test for {@link org.apache.calcite.rel.RelDistributionTraitDef}.  */
end_comment

begin_class
class|class
name|TraitConversionTest
block|{
specifier|private
specifier|static
specifier|final
name|ConvertRelDistributionTraitDef
name|NEW_TRAIT_DEF_INSTANCE
init|=
operator|new
name|ConvertRelDistributionTraitDef
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SimpleDistribution
name|SIMPLE_DISTRIBUTION_ANY
init|=
operator|new
name|SimpleDistribution
argument_list|(
literal|"ANY"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SimpleDistribution
name|SIMPLE_DISTRIBUTION_RANDOM
init|=
operator|new
name|SimpleDistribution
argument_list|(
literal|"RANDOM"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SimpleDistribution
name|SIMPLE_DISTRIBUTION_SINGLETON
init|=
operator|new
name|SimpleDistribution
argument_list|(
literal|"SINGLETON"
argument_list|)
decl_stmt|;
annotation|@
name|Test
name|void
name|testTraitConversion
parameter_list|()
block|{
specifier|final
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
name|addRelTraitDef
argument_list|(
name|NEW_TRAIT_DEF_INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|RandomSingleTraitRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|SingleLeafTraitRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|ExpandConversionRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|setTopDownOpt
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|newCluster
argument_list|(
name|planner
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|RelNode
name|convertedRel
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|singleRel
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
specifier|final
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
name|RandomSingleRel
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|SIMPLE_DISTRIBUTION_RANDOM
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|result
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|input
operator|instanceof
name|BridgeRel
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|SIMPLE_DISTRIBUTION_RANDOM
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|input2
init|=
name|input
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|input2
operator|instanceof
name|SingletonLeafRel
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|input2
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|input2
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|SIMPLE_DISTRIBUTION_SINGLETON
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Converts a {@link NoneSingleRel} (none convention, distribution any)    * to {@link RandomSingleRel} (physical convention, distribution random). */
specifier|public
specifier|static
class|class
name|RandomSingleTraitRule
extends|extends
name|RelRule
argument_list|<
name|RandomSingleTraitRule
operator|.
name|Config
argument_list|>
block|{
specifier|static
specifier|final
name|RandomSingleTraitRule
name|INSTANCE
init|=
name|Config
operator|.
name|EMPTY
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|NoneSingleRel
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|toRule
argument_list|()
decl_stmt|;
name|RandomSingleTraitRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
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
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|NoneSingleRel
name|single
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|input
init|=
name|single
operator|.
name|getInput
argument_list|()
decl_stmt|;
name|RelNode
name|physInput
init|=
name|convert
argument_list|(
name|input
argument_list|,
name|single
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|PHYS_CALLING_CONVENTION
argument_list|)
operator|.
name|plus
argument_list|(
name|SIMPLE_DISTRIBUTION_RANDOM
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|RandomSingleRel
argument_list|(
name|single
operator|.
name|getCluster
argument_list|()
argument_list|,
name|physInput
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
annotation|@
name|Override
specifier|default
name|RandomSingleTraitRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|RandomSingleTraitRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Rel with physical convention and random distribution. */
specifier|private
specifier|static
class|class
name|RandomSingleRel
extends|extends
name|TestSingleRel
block|{
name|RandomSingleRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
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
operator|.
name|plus
argument_list|(
name|SIMPLE_DISTRIBUTION_RANDOM
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|makeTinyCost
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|RandomSingleRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Converts {@link NoneLeafRel} (none convention, any distribution) to    * {@link SingletonLeafRel} (physical convention, singleton distribution). */
specifier|public
specifier|static
class|class
name|SingleLeafTraitRule
extends|extends
name|RelRule
argument_list|<
name|SingleLeafTraitRule
operator|.
name|Config
argument_list|>
block|{
specifier|static
specifier|final
name|SingleLeafTraitRule
name|INSTANCE
init|=
name|Config
operator|.
name|EMPTY
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|NoneLeafRel
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|toRule
argument_list|()
decl_stmt|;
name|SingleLeafTraitRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
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
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|NoneLeafRel
name|leafRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|SingletonLeafRel
argument_list|(
name|leafRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|leafRel
operator|.
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
annotation|@
name|Override
specifier|default
name|SingleLeafTraitRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|SingleLeafTraitRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Rel with singleton distribution, physical convention. */
specifier|private
specifier|static
class|class
name|SingletonLeafRel
extends|extends
name|TestLeafRel
block|{
name|SingletonLeafRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|String
name|label
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
operator|.
name|plus
argument_list|(
name|SIMPLE_DISTRIBUTION_SINGLETON
argument_list|)
argument_list|,
name|label
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|makeTinyCost
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|SingletonLeafRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|label
argument_list|)
return|;
block|}
block|}
comment|/** Bridges the {@link SimpleDistribution}, difference between    * {@link SingletonLeafRel} and {@link RandomSingleRel}. */
specifier|private
specifier|static
class|class
name|BridgeRel
extends|extends
name|TestSingleRel
block|{
name|BridgeRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
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
operator|.
name|plus
argument_list|(
name|SIMPLE_DISTRIBUTION_RANDOM
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|makeTinyCost
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
operator|new
name|BridgeRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Dummy distribution for test (simplified version of RelDistribution). */
specifier|private
specifier|static
class|class
name|SimpleDistribution
implements|implements
name|RelTrait
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|SimpleDistribution
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|NEW_TRAIT_DEF_INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|trait
operator|==
name|this
operator|||
name|trait
operator|==
name|SIMPLE_DISTRIBUTION_ANY
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
block|}
comment|/** Dummy distribution trait def for test (handles conversion of    * SimpleDistribution). */
specifier|private
specifier|static
class|class
name|ConvertRelDistributionTraitDef
extends|extends
name|RelTraitDef
argument_list|<
name|SimpleDistribution
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|SimpleDistribution
argument_list|>
name|getTraitClass
parameter_list|()
block|{
return|return
name|SimpleDistribution
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getSimpleName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
literal|"ConvertRelDistributionTraitDef"
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|SimpleDistribution
name|toTrait
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
if|if
condition|(
name|toTrait
operator|==
name|SIMPLE_DISTRIBUTION_ANY
condition|)
block|{
return|return
name|rel
return|;
block|}
return|return
operator|new
name|BridgeRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|SimpleDistribution
name|fromTrait
parameter_list|,
name|SimpleDistribution
name|toTrait
parameter_list|)
block|{
return|return
operator|(
name|fromTrait
operator|==
name|toTrait
operator|)
operator|||
operator|(
name|toTrait
operator|==
name|SIMPLE_DISTRIBUTION_ANY
operator|)
operator|||
operator|(
name|fromTrait
operator|==
name|SIMPLE_DISTRIBUTION_SINGLETON
operator|&&
name|toTrait
operator|==
name|SIMPLE_DISTRIBUTION_RANDOM
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SimpleDistribution
name|getDefault
parameter_list|()
block|{
return|return
name|SIMPLE_DISTRIBUTION_ANY
return|;
block|}
block|}
comment|/** Any distribution and none convention. */
specifier|private
specifier|static
class|class
name|NoneLeafRel
extends|extends
name|TestLeafRel
block|{
name|NoneLeafRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|String
name|label
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|label
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Convention
operator|.
name|NONE
argument_list|,
name|SIMPLE_DISTRIBUTION_ANY
argument_list|)
assert|;
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
block|}
comment|/** Rel with any distribution and none convention. */
specifier|private
specifier|static
class|class
name|NoneSingleRel
extends|extends
name|TestSingleRel
block|{
name|NoneSingleRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Convention
operator|.
name|NONE
argument_list|,
name|SIMPLE_DISTRIBUTION_ANY
argument_list|)
assert|;
return|return
operator|new
name|NoneSingleRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

