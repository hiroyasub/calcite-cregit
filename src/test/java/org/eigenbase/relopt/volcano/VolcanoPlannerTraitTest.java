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
name|relopt
operator|.
name|volcano
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
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
name|oj
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
name|convert
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * VolcanoPlannerTraitTest  *  * @author Stephan Zuercher  */
end_comment

begin_class
specifier|public
class|class
name|VolcanoPlannerTraitTest
extends|extends
name|TestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * Private calling convention representing a generic "physical" calling      * convention.      */
specifier|private
specifier|static
specifier|final
name|Convention
name|PHYS_CALLING_CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"PHYS"
argument_list|,
name|RelNode
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Private trait definition for an alternate type of traits.      */
specifier|private
specifier|static
specifier|final
name|AltTraitDef
name|ALT_TRAIT_DEF
init|=
operator|new
name|AltTraitDef
argument_list|()
decl_stmt|;
comment|/**      * Private alternate trait.      */
specifier|private
specifier|static
specifier|final
name|AltTrait
name|ALT_TRAIT
init|=
operator|new
name|AltTrait
argument_list|(
name|ALT_TRAIT_DEF
argument_list|,
literal|"ALT"
argument_list|)
decl_stmt|;
comment|/**      * Private alternate trait.      */
specifier|private
specifier|static
specifier|final
name|AltTrait
name|ALT_TRAIT2
init|=
operator|new
name|AltTrait
argument_list|(
name|ALT_TRAIT_DEF
argument_list|,
literal|"ALT2"
argument_list|)
decl_stmt|;
comment|/**      * Ordinal count for alternate traits (so they can implement equals() and      * avoid being canonized into the same trait).      */
specifier|private
specifier|static
name|int
name|altTraitOrdinal
init|=
literal|0
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|VolcanoPlannerTraitTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|testDoubleConversion
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
name|instance
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ALT_TRAIT_DEF
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|PhysToIteratorConverterRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|AltTraitConverterRule
argument_list|(
name|ALT_TRAIT
argument_list|,
name|ALT_TRAIT2
argument_list|,
literal|"AltToAlt2ConverterRule"
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|PhysLeafRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|IterSingleRule
argument_list|()
argument_list|)
expr_stmt|;
name|RelOptCluster
name|cluster
init|=
name|VolcanoPlannerTest
operator|.
name|newCluster
argument_list|(
name|planner
argument_list|)
decl_stmt|;
name|NoneLeafRel
name|noneLeafRel
init|=
name|RelOptUtil
operator|.
name|addTrait
argument_list|(
operator|new
name|NoneLeafRel
argument_list|(
name|cluster
argument_list|,
literal|"noneLeafRel"
argument_list|)
argument_list|,
name|ALT_TRAIT
argument_list|)
decl_stmt|;
name|NoneSingleRel
name|noneRel
init|=
name|RelOptUtil
operator|.
name|addTrait
argument_list|(
operator|new
name|NoneSingleRel
argument_list|(
name|cluster
argument_list|,
name|noneLeafRel
argument_list|)
argument_list|,
name|ALT_TRAIT2
argument_list|)
decl_stmt|;
name|RelNode
name|convertedRel
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|noneRel
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|ALT_TRAIT2
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
name|IterSingleRel
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ALT_TRAIT2
argument_list|,
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ALT_TRAIT_DEF
argument_list|)
argument_list|)
expr_stmt|;
name|RelNode
name|child
init|=
name|result
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
name|child
operator|instanceof
name|AltTraitConverter
operator|)
operator|||
operator|(
name|child
operator|instanceof
name|PhysToIteratorConverter
operator|)
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|child
operator|instanceof
name|AltTraitConverter
operator|)
operator|||
operator|(
name|child
operator|instanceof
name|PhysToIteratorConverter
operator|)
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|PhysLeafRel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTraitPropagation
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
name|instance
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ALT_TRAIT_DEF
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|PhysToIteratorConverterRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|AltTraitConverterRule
argument_list|(
name|ALT_TRAIT
argument_list|,
name|ALT_TRAIT2
argument_list|,
literal|"AltToAlt2ConverterRule"
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|PhysLeafRule
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|IterSingleRule2
argument_list|()
argument_list|)
expr_stmt|;
name|RelOptCluster
name|cluster
init|=
name|VolcanoPlannerTest
operator|.
name|newCluster
argument_list|(
name|planner
argument_list|)
decl_stmt|;
name|NoneLeafRel
name|noneLeafRel
init|=
name|RelOptUtil
operator|.
name|addTrait
argument_list|(
operator|new
name|NoneLeafRel
argument_list|(
name|cluster
argument_list|,
literal|"noneLeafRel"
argument_list|)
argument_list|,
name|ALT_TRAIT
argument_list|)
decl_stmt|;
name|NoneSingleRel
name|noneRel
init|=
name|RelOptUtil
operator|.
name|addTrait
argument_list|(
operator|new
name|NoneSingleRel
argument_list|(
name|cluster
argument_list|,
name|noneLeafRel
argument_list|)
argument_list|,
name|ALT_TRAIT2
argument_list|)
decl_stmt|;
name|RelNode
name|convertedRel
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|noneRel
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|ALT_TRAIT2
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
name|IterSingleRel
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ALT_TRAIT2
argument_list|,
name|result
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ALT_TRAIT_DEF
argument_list|)
argument_list|)
expr_stmt|;
name|RelNode
name|child
init|=
name|result
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|IterSingleRel
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ALT_TRAIT2
argument_list|,
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|ALT_TRAIT_DEF
argument_list|)
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|child
operator|instanceof
name|AltTraitConverter
operator|)
operator|||
operator|(
name|child
operator|instanceof
name|PhysToIteratorConverter
operator|)
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|child
operator|instanceof
name|AltTraitConverter
operator|)
operator|||
operator|(
name|child
operator|instanceof
name|PhysToIteratorConverter
operator|)
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|PhysLeafRel
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|static
class|class
name|AltTrait
implements|implements
name|RelTrait
block|{
specifier|private
specifier|final
name|AltTraitDef
name|traitDef
decl_stmt|;
specifier|private
specifier|final
name|int
name|ordinal
decl_stmt|;
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
specifier|private
name|AltTrait
parameter_list|(
name|AltTraitDef
name|traitDef
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|traitDef
operator|=
name|traitDef
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|ordinal
operator|=
name|altTraitOrdinal
operator|++
expr_stmt|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|traitDef
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AltTrait
name|that
init|=
operator|(
name|AltTrait
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|ordinal
operator|==
name|that
operator|.
name|ordinal
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ordinal
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AltTraitDef
extends|extends
name|RelTraitDef
argument_list|<
name|AltTrait
argument_list|>
block|{
specifier|private
name|MultiMap
argument_list|<
name|RelTrait
argument_list|,
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|ConverterRule
argument_list|>
argument_list|>
name|conversionMap
init|=
operator|new
name|MultiMap
argument_list|<
name|RelTrait
argument_list|,
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|ConverterRule
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Class
argument_list|<
name|AltTrait
argument_list|>
name|getTraitClass
parameter_list|()
block|{
return|return
name|AltTrait
operator|.
name|class
return|;
block|}
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
literal|"alt_phys"
return|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTrait
name|toTrait
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
name|RelTrait
name|fromTrait
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|conversionMap
operator|.
name|containsKey
argument_list|(
name|fromTrait
argument_list|)
condition|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|ConverterRule
argument_list|>
name|traitAndRule
range|:
name|conversionMap
operator|.
name|getMulti
argument_list|(
name|fromTrait
argument_list|)
control|)
block|{
name|RelTrait
name|trait
init|=
name|traitAndRule
operator|.
name|left
decl_stmt|;
name|ConverterRule
name|rule
init|=
name|traitAndRule
operator|.
name|right
decl_stmt|;
if|if
condition|(
name|trait
operator|==
name|toTrait
condition|)
block|{
name|RelNode
name|converted
init|=
name|rule
operator|.
name|convert
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|converted
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|planner
operator|.
name|getCost
argument_list|(
name|converted
argument_list|)
operator|.
name|isInfinite
argument_list|()
operator|||
name|allowInfiniteCostConverters
operator|)
condition|)
block|{
return|return
name|converted
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelTrait
name|fromTrait
parameter_list|,
name|RelTrait
name|toTrait
parameter_list|)
block|{
if|if
condition|(
name|conversionMap
operator|.
name|containsKey
argument_list|(
name|fromTrait
argument_list|)
condition|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|ConverterRule
argument_list|>
name|traitAndRule
range|:
name|conversionMap
operator|.
name|getMulti
argument_list|(
name|fromTrait
argument_list|)
control|)
block|{
if|if
condition|(
name|traitAndRule
operator|.
name|left
operator|==
name|toTrait
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|registerConverterRule
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|ConverterRule
name|converterRule
parameter_list|)
block|{
if|if
condition|(
operator|!
name|converterRule
operator|.
name|isGuaranteed
argument_list|()
condition|)
block|{
return|return;
block|}
name|RelTrait
name|fromTrait
init|=
name|converterRule
operator|.
name|getInTrait
argument_list|()
decl_stmt|;
name|RelTrait
name|toTrait
init|=
name|converterRule
operator|.
name|getOutTrait
argument_list|()
decl_stmt|;
name|conversionMap
operator|.
name|putMulti
argument_list|(
name|fromTrait
argument_list|,
operator|new
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|ConverterRule
argument_list|>
argument_list|(
name|toTrait
argument_list|,
name|converterRule
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|TestLeafRel
extends|extends
name|AbstractRelNode
block|{
specifier|private
name|String
name|label
decl_stmt|;
specifier|protected
name|TestLeafRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|)
expr_stmt|;
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|label
return|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|planner
operator|.
name|makeInfiniteCost
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createJavaType
argument_list|(
name|Void
operator|.
name|TYPE
argument_list|)
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|}
argument_list|)
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"label"
argument_list|,
name|label
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|NoneLeafRel
extends|extends
name|TestLeafRel
block|{
specifier|protected
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
block|}
specifier|private
specifier|static
class|class
name|PhysLeafRel
extends|extends
name|TestLeafRel
block|{
name|PhysLeafRel
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
argument_list|,
name|label
argument_list|)
expr_stmt|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|planner
operator|.
name|makeTinyCost
argument_list|()
return|;
block|}
comment|// TODO: SWZ Implement clone?
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|TestSingleRel
extends|extends
name|SingleRel
block|{
specifier|protected
name|TestSingleRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|planner
operator|.
name|makeInfiniteCost
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
return|;
block|}
comment|// TODO: SWZ Implement clone?
block|}
specifier|private
specifier|static
class|class
name|NoneSingleRel
extends|extends
name|TestSingleRel
block|{
specifier|protected
name|NoneSingleRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
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
name|child
argument_list|)
expr_stmt|;
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
name|Convention
operator|.
name|NONE
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
specifier|private
specifier|static
class|class
name|IterSingleRel
extends|extends
name|TestSingleRel
implements|implements
name|JavaRel
block|{
specifier|public
name|IterSingleRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
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
name|CallingConvention
operator|.
name|ITERATOR
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|planner
operator|.
name|makeTinyCost
argument_list|()
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
name|CallingConvention
operator|.
name|ITERATOR
argument_list|)
assert|;
return|return
operator|new
name|IterSingleRel
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
specifier|public
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PhysLeafRule
extends|extends
name|RelOptRule
block|{
name|PhysLeafRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|NoneLeafRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// implement RelOptRule
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|PHYS_CALLING_CONVENTION
return|;
block|}
comment|// implement RelOptRule
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
operator|(
name|NoneLeafRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|PhysLeafRel
argument_list|(
name|leafRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|leafRel
operator|.
name|getLabel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|IterSingleRule
extends|extends
name|RelOptRule
block|{
name|IterSingleRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|NoneSingleRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// implement RelOptRule
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|CallingConvention
operator|.
name|ITERATOR
return|;
block|}
specifier|public
name|RelTrait
name|getOutTrait
parameter_list|()
block|{
return|return
name|getOutConvention
argument_list|()
return|;
block|}
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|NoneSingleRel
name|rel
init|=
operator|(
name|NoneSingleRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutTrait
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|IterSingleRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|converted
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|IterSingleRule2
extends|extends
name|RelOptRule
block|{
name|IterSingleRule2
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|NoneSingleRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// implement RelOptRule
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|CallingConvention
operator|.
name|ITERATOR
return|;
block|}
specifier|public
name|RelTrait
name|getOutTrait
parameter_list|()
block|{
return|return
name|getOutConvention
argument_list|()
return|;
block|}
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|NoneSingleRel
name|rel
init|=
operator|(
name|NoneSingleRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutTrait
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IterSingleRel
name|child
init|=
operator|new
name|IterSingleRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|converted
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|IterSingleRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AltTraitConverterRule
extends|extends
name|ConverterRule
block|{
specifier|private
specifier|final
name|RelTrait
name|toTrait
decl_stmt|;
specifier|private
name|AltTraitConverterRule
parameter_list|(
name|AltTrait
name|fromTrait
parameter_list|,
name|AltTrait
name|toTrait
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|fromTrait
argument_list|,
name|toTrait
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|toTrait
operator|=
name|toTrait
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|AltTraitConverter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
argument_list|,
name|toTrait
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isGuaranteed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AltTraitConverter
extends|extends
name|ConverterRelImpl
block|{
specifier|private
specifier|final
name|RelTrait
name|toTrait
decl_stmt|;
specifier|private
name|AltTraitConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelTrait
name|toTrait
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|toTrait
operator|.
name|getTraitDef
argument_list|()
argument_list|,
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|toTrait
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|toTrait
operator|=
name|toTrait
expr_stmt|;
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
return|return
operator|new
name|AltTraitConverter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|toTrait
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PhysToIteratorConverterRule
extends|extends
name|ConverterRule
block|{
specifier|public
name|PhysToIteratorConverterRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|PHYS_CALLING_CONVENTION
argument_list|,
name|CallingConvention
operator|.
name|ITERATOR
argument_list|,
literal|"PhysToIteratorRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|PhysToIteratorConverter
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
block|}
specifier|private
specifier|static
class|class
name|PhysToIteratorConverter
extends|extends
name|ConverterRelImpl
block|{
specifier|public
name|PhysToIteratorConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|ConventionTraitDef
operator|.
name|instance
argument_list|,
name|child
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|CallingConvention
operator|.
name|ITERATOR
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
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
return|return
operator|new
name|PhysToIteratorConverter
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

begin_comment
comment|// End VolcanoPlannerTraitTest.java
end_comment

end_unit

