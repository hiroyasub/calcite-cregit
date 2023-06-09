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
name|RelCollations
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
name|SingleRel
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|ImmutableIntList
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
name|assertEquals
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
comment|/**  * Tests that ensures that we do not add enforcers for the already satisfied traits.  * See https://issues.apache.org/jira/browse/CALCITE-4466 for more information.  */
end_comment

begin_class
specifier|public
class|class
name|MultipleTraitConversionTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ConstantConditions"
argument_list|)
annotation|@
name|Test
name|void
name|testMultipleTraitConversion
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
name|addRelTraitDef
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|CustomTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|setNoneConventionHasInfiniteCost
argument_list|(
literal|false
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
name|RelTraitSet
name|fromTraits
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|ImmutableIntList
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RelTraitSet
name|toTraits
init|=
name|fromTraits
operator|.
name|plus
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|plus
argument_list|(
name|CustomTrait
operator|.
name|TO
argument_list|)
decl_stmt|;
name|CustomLeafRel
name|rel
init|=
operator|new
name|CustomLeafRel
argument_list|(
name|cluster
argument_list|,
name|fromTraits
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|RelNode
name|convertedRel
init|=
name|planner
operator|.
name|changeTraitsUsingConverters
argument_list|(
name|rel
argument_list|,
name|toTraits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CustomTraitEnforcer
operator|.
name|class
argument_list|,
name|convertedRel
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|convertedRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|satisfies
argument_list|(
name|toTraits
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that the equivalence set contains only the original and converted rels.
comment|// It should not contain the collation enforcer, because the "from" collation already
comment|// satisfies the "to" collation.
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
init|=
name|planner
operator|.
name|getSubset
argument_list|(
name|rel
argument_list|)
operator|.
name|set
operator|.
name|rels
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rels
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rels
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|r
lambda|->
name|r
operator|instanceof
name|CustomLeafRel
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rels
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|r
lambda|->
name|r
operator|instanceof
name|CustomTraitEnforcer
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Leaf rel.    */
specifier|private
specifier|static
class|class
name|CustomLeafRel
extends|extends
name|PlannerTests
operator|.
name|TestLeafRel
block|{
name|CustomLeafRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|CustomLeafRel
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
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
return|return
operator|new
name|CustomLeafRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|)
return|;
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
block|}
comment|/**    * An enforcer used by the custom trait def.    */
specifier|private
specifier|static
class|class
name|CustomTraitEnforcer
extends|extends
name|SingleRel
block|{
specifier|private
name|CustomTraitEnforcer
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
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
return|return
operator|new
name|CustomTraitEnforcer
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Custom trait.    */
specifier|private
specifier|static
class|class
name|CustomTrait
implements|implements
name|RelTrait
block|{
specifier|private
specifier|static
specifier|final
name|CustomTrait
name|FROM
init|=
operator|new
name|CustomTrait
argument_list|(
literal|"FROM"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CustomTrait
name|TO
init|=
operator|new
name|CustomTrait
argument_list|(
literal|"TO"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|label
decl_stmt|;
specifier|private
name|CustomTrait
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|CustomTraitDef
operator|.
name|INSTANCE
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
name|equals
argument_list|(
name|trait
argument_list|)
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
comment|// No-op
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|label
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|CustomTrait
operator|)
operator|&&
name|label
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|CustomTrait
operator|)
name|o
operator|)
operator|.
name|label
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|label
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/**    * Custom trait definition.    */
specifier|private
specifier|static
class|class
name|CustomTraitDef
extends|extends
name|RelTraitDef
argument_list|<
name|CustomTrait
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|CustomTraitDef
name|INSTANCE
init|=
operator|new
name|CustomTraitDef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|CustomTrait
argument_list|>
name|getTraitClass
parameter_list|()
block|{
return|return
name|CustomTrait
operator|.
name|class
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
literal|"custom"
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
name|CustomTrait
name|toTrait
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
return|return
operator|new
name|CustomTraitEnforcer
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|toTrait
argument_list|)
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
name|CustomTrait
name|fromTrait
parameter_list|,
name|CustomTrait
name|toTrait
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|CustomTrait
name|getDefault
parameter_list|()
block|{
return|return
name|CustomTrait
operator|.
name|FROM
return|;
block|}
block|}
block|}
end_class

end_unit

