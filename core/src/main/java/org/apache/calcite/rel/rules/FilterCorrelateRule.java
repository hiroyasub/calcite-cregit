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
name|rel
operator|.
name|rules
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
name|Correlate
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
name|Filter
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexUtil
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
name|tools
operator|.
name|RelBuilderFactory
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
name|immutables
operator|.
name|value
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Planner rule that pushes a {@link Filter} above a {@link Correlate} into the  * inputs of the Correlate.  *  * @see CoreRules#FILTER_CORRELATE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|FilterCorrelateRule
extends|extends
name|RelRule
argument_list|<
name|FilterCorrelateRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a FilterCorrelateRule. */
specifier|protected
name|FilterCorrelateRule
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
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterCorrelateRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterCorrelateRule
parameter_list|(
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
specifier|final
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Correlate
name|corr
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|aboveFilters
init|=
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|leftFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|rightFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Try to push down above filters. These are typically where clause
comment|// filters. They can be pushed down if they are not on the NULL
comment|// generating side.
name|RelOptUtil
operator|.
name|classifyFilters
argument_list|(
name|corr
argument_list|,
name|aboveFilters
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|corr
operator|.
name|getJoinType
argument_list|()
operator|.
name|canPushRightFromAbove
argument_list|()
argument_list|,
name|aboveFilters
argument_list|,
name|leftFilters
argument_list|,
name|rightFilters
argument_list|)
expr_stmt|;
if|if
condition|(
name|leftFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rightFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no filters got pushed
return|return;
block|}
comment|// Create Filters on top of the children if any filters were
comment|// pushed to them.
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|corr
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|leftRel
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|corr
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|leftFilters
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rightRel
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|corr
operator|.
name|getRight
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|rightFilters
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Create the new Correlate
name|RelNode
name|newCorrRel
init|=
name|corr
operator|.
name|copy
argument_list|(
name|corr
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|leftRel
argument_list|,
name|rightRel
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|corr
argument_list|,
name|newCorrRel
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|leftFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|filter
argument_list|,
name|leftRel
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|rightFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|filter
argument_list|,
name|rightRel
argument_list|)
expr_stmt|;
block|}
comment|// Create a Filter on top of the join if needed
name|relBuilder
operator|.
name|push
argument_list|(
name|newCorrRel
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|filter
argument_list|(
name|RexUtil
operator|.
name|fixUp
argument_list|(
name|rexBuilder
argument_list|,
name|aboveFilters
argument_list|,
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableFilterCorrelateRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|Correlate
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|FilterCorrelateRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|FilterCorrelateRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Correlate
argument_list|>
name|correlateClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|filterClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|correlateClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

