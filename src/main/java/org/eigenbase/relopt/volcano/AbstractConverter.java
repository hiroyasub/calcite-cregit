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

begin_comment
comment|/**  * Converts a relational expression to any given output convention.  *  *<p>Unlike most {@link ConverterRel}s, an abstract converter is always  * abstract. You would typically create an<code>AbstractConverter</code> when  * it is necessary to transform a relational expression immediately; later,  * rules will transform it into relational expressions which can be implemented.  *</p>  *  *<p>If an abstract converter cannot be satisfied immediately (because the  * source subset is abstract), the set is flagged, so this converter will be  * expanded as soon as a non-abstract relexp is added to the set.</p>  */
end_comment

begin_class
specifier|public
class|class
name|AbstractConverter
extends|extends
name|ConverterRelImpl
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|AbstractConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|Convention
name|outConvention
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|rel
argument_list|,
name|outConvention
operator|.
name|getTraitDef
argument_list|()
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|outConvention
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitDef
name|traitDef
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitDef
argument_list|,
name|traits
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|AbstractConverter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|traitDef
argument_list|,
name|traitSet
argument_list|)
return|;
block|}
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
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[
name|traitSet
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|traitSet
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|terms
index|[
literal|0
index|]
operator|=
literal|"child"
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
name|traitSet
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|traitSet
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|traitSet
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|explain
argument_list|(
name|this
argument_list|,
name|terms
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Rule which converts an {@link AbstractConverter} into a chain of      * converters from the source relation to the target traits.      *      *<p>The chain produced is mimimal: we have previously built the transitive      * closure of the graph of conversions, so we choose the shortest chain.</p>      *      *<p>Unlike the {@link AbstractConverter} they are replacing, these      * converters are guaranteed to be able to convert any relation of their      * calling convention. Furthermore, because they introduce subsets of other      * calling conventions along the way, these subsets may spawn more efficient      * conversions which are not generally applicable.</p>      *      *<p>AbstractConverters can be messy, so they restrain themselves: they      * don't fire if the target subset already has an implementation (with less      * than infinite cost).</p>      */
specifier|public
specifier|static
class|class
name|ExpandConversionRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|ExpandConversionRule
name|instance
init|=
operator|new
name|ExpandConversionRule
argument_list|()
decl_stmt|;
comment|/**          * Creates an ExpandConversionRule.          */
specifier|private
name|ExpandConversionRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|AbstractConverter
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|VolcanoPlanner
name|planner
init|=
operator|(
name|VolcanoPlanner
operator|)
name|call
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|AbstractConverter
name|converter
init|=
operator|(
name|AbstractConverter
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|RelSubset
name|converterSubset
init|=
name|planner
operator|.
name|getSubset
argument_list|(
name|converter
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|child
init|=
name|converter
operator|.
name|getChild
argument_list|()
decl_stmt|;
name|RelNode
name|converted
init|=
name|planner
operator|.
name|changeTraitsUsingConverters
argument_list|(
name|child
argument_list|,
name|converter
operator|.
name|traitSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|converted
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
literal|false
condition|)
block|{
return|return;
block|}
comment|// Since we couldn't convert directly, create abstract converters to
comment|// all sibling subsets. This will cause them to be important, and
comment|// hence rules will fire which may generate the conversion we need.
specifier|final
name|RelSet
name|set
init|=
name|planner
operator|.
name|getSet
argument_list|(
name|child
argument_list|)
decl_stmt|;
for|for
control|(
name|RelSubset
name|subset
range|:
name|set
operator|.
name|subsets
control|)
block|{
if|if
condition|(
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getTraitSet
argument_list|()
argument_list|)
operator|||
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|converter
operator|.
name|traitSet
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|AbstractConverter
name|newConverter
init|=
operator|new
name|AbstractConverter
argument_list|(
name|child
operator|.
name|getCluster
argument_list|()
argument_list|,
name|subset
argument_list|,
name|converter
operator|.
name|traitDef
argument_list|,
name|converter
operator|.
name|traitSet
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newConverter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End AbstractConverter.java
end_comment

end_unit

