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
name|RelDistributionTraitDef
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
name|Project
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes  * a {@link org.apache.calcite.rel.core.Filter}  * past a {@link org.apache.calcite.rel.core.Project}.  *  * @see CoreRules#FILTER_PROJECT_TRANSPOSE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|FilterProjectTransposeRule
extends|extends
name|RelRule
argument_list|<
name|FilterProjectTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a FilterProjectTransposeRule. */
specifier|protected
name|FilterProjectTransposeRule
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
comment|/**    * Creates a FilterProjectTransposeRule.    *    *<p>Equivalent to the rule created by    * {@link #FilterProjectTransposeRule(Class, Predicate, Class, Predicate, boolean, boolean, RelBuilderFactory)}    * with some default predicates that do not allow a filter to be pushed    * past the project if there is a correlation condition anywhere in the    * filter (since in some cases it can prevent a    * {@link org.apache.calcite.rel.core.Correlate} from being de-correlated).    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterProjectTransposeRule
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
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|boolean
name|copyFilter
parameter_list|,
name|boolean
name|copyProject
parameter_list|,
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
operator|.
name|withOperandFor
argument_list|(
name|filterClass
argument_list|,
name|f
lambda|->
operator|!
name|RexUtil
operator|.
name|containsCorrelation
argument_list|(
name|f
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|,
name|projectClass
argument_list|,
name|project
lambda|->
literal|true
argument_list|)
operator|.
name|withCopyFilter
argument_list|(
name|copyFilter
argument_list|)
operator|.
name|withCopyProject
argument_list|(
name|copyProject
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a FilterProjectTransposeRule.    *    *<p>If {@code copyFilter} is true, creates the same kind of Filter as    * matched in the rule, otherwise it creates a Filter using the RelBuilder    * obtained by the {@code relBuilderFactory}.    * Similarly for {@code copyProject}.    *    *<p>Defining predicates for the Filter (using {@code filterPredicate})    * and/or the Project (using {@code projectPredicate} allows making the rule    * more restrictive.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
parameter_list|<
name|F
extends|extends
name|Filter
parameter_list|,
name|P
extends|extends
name|Project
parameter_list|>
name|FilterProjectTransposeRule
parameter_list|(
name|Class
argument_list|<
name|F
argument_list|>
name|filterClass
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|F
argument_list|>
name|filterPredicate
parameter_list|,
name|Class
argument_list|<
name|P
argument_list|>
name|projectClass
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|P
argument_list|>
name|projectPredicate
parameter_list|,
name|boolean
name|copyFilter
parameter_list|,
name|boolean
name|copyProject
parameter_list|,
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
name|predicate
argument_list|(
name|filterPredicate
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
name|projectClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|projectPredicate
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
operator|.
name|withCopyFilter
argument_list|(
name|copyFilter
argument_list|)
operator|.
name|withCopyProject
argument_list|(
name|copyProject
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterProjectTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
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
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|)
operator|.
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
name|predicate
argument_list|(
name|filter
lambda|->
operator|!
name|RexUtil
operator|.
name|containsCorrelation
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|projectClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|project
lambda|->
literal|true
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
operator|.
name|withCopyFilter
argument_list|(
name|filterFactory
operator|==
literal|null
argument_list|)
operator|.
name|withCopyProject
argument_list|(
name|projectFactory
operator|==
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|FilterProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|boolean
name|copyFilter
parameter_list|,
name|boolean
name|copyProject
parameter_list|,
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
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|exactly
argument_list|(
name|operand
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withCopyFilter
argument_list|(
name|copyFilter
argument_list|)
operator|.
name|withCopyProject
argument_list|(
name|copyProject
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
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|project
operator|.
name|containsOver
argument_list|()
condition|)
block|{
comment|// In general a filter cannot be pushed below a windowing calculation.
comment|// Applying the filter before the aggregation function changes
comment|// the results of the windowing invocation.
comment|//
comment|// When the filter is on the PARTITION BY expression of the OVER clause
comment|// it can be pushed down. For now we don't support this.
return|return;
block|}
comment|// convert the filter to one that references the child of the project
name|RexNode
name|newCondition
init|=
name|RelOptUtil
operator|.
name|pushPastProject
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|,
name|project
argument_list|)
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
name|RelNode
name|newFilterRel
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|isCopyFilter
argument_list|()
condition|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|project
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonList
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonList
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|newCondition
operator|=
name|RexUtil
operator|.
name|removeNullabilityCast
argument_list|(
name|relBuilder
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|newCondition
argument_list|)
expr_stmt|;
name|newFilterRel
operator|=
name|filter
operator|.
name|copy
argument_list|(
name|traitSet
argument_list|,
name|input
argument_list|,
name|newCondition
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newFilterRel
operator|=
name|relBuilder
operator|.
name|push
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|newCondition
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|RelNode
name|newProject
init|=
name|config
operator|.
name|isCopyProject
argument_list|()
condition|?
name|project
operator|.
name|copy
argument_list|(
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newFilterRel
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
else|:
name|relBuilder
operator|.
name|push
argument_list|(
name|newFilterRel
argument_list|)
operator|.
name|project
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newProject
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration.    *    *<p>If {@code copyFilter} is true, creates the same kind of Filter as    * matched in the rule, otherwise it creates a Filter using the RelBuilder    * obtained by the {@code relBuilderFactory}.    * Similarly for {@code copyProject}.    *    *<p>Defining predicates for the Filter (using {@code filterPredicate})    * and/or the Project (using {@code projectPredicate} allows making the rule    * more restrictive. */
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
name|ImmutableFilterProjectTransposeRule
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
name|f
lambda|->
operator|!
name|RexUtil
operator|.
name|containsCorrelation
argument_list|(
name|f
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|,
name|Project
operator|.
name|class
argument_list|,
name|p
lambda|->
literal|true
argument_list|)
operator|.
name|withCopyFilter
argument_list|(
literal|true
argument_list|)
operator|.
name|withCopyProject
argument_list|(
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|FilterProjectTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|FilterProjectTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Whether to create a {@link Filter} of the same convention as the      * matched Filter. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isCopyFilter
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Sets {@link #isCopyFilter()}. */
name|Config
name|withCopyFilter
parameter_list|(
name|boolean
name|copyFilter
parameter_list|)
function_decl|;
comment|/** Whether to create a {@link Project} of the same convention as the      * matched Project. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isCopyProject
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Sets {@link #isCopyProject()}. */
name|Config
name|withCopyProject
parameter_list|(
name|boolean
name|copyProject
parameter_list|)
function_decl|;
comment|/** Defines an operand tree for the given 2 classes. */
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
name|Predicate
argument_list|<
name|Filter
argument_list|>
name|filterPredicate
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Predicate
argument_list|<
name|Project
argument_list|>
name|projectPredicate
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
name|predicate
argument_list|(
name|filterPredicate
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
name|projectClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|projectPredicate
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
comment|/** Defines an operand tree for the given 3 classes. */
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
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|relClass
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
name|projectClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|relClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
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

