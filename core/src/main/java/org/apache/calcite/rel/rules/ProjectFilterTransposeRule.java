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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalProject
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
name|RelDataTypeField
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
name|RexInputRef
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
name|RexShuttle
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|ImmutableBitSet
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
name|LinkedHashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Project}  * past a {@link org.apache.calcite.rel.core.Filter}.  *  * @see CoreRules#PROJECT_FILTER_TRANSPOSE  * @see CoreRules#PROJECT_FILTER_TRANSPOSE_WHOLE_EXPRESSIONS  * @see CoreRules#PROJECT_FILTER_TRANSPOSE_WHOLE_PROJECT_EXPRESSIONS  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|ProjectFilterTransposeRule
extends|extends
name|RelRule
argument_list|<
name|ProjectFilterTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a ProjectFilterTransposeRule. */
specifier|protected
name|ProjectFilterTransposeRule
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
name|ProjectFilterTransposeRule
parameter_list|(
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
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
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
name|projectClass
argument_list|,
name|filterClass
argument_list|)
operator|.
name|withPreserveExprCondition
argument_list|(
name|preserveExprCondition
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|ProjectFilterTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|,
name|boolean
name|wholeProject
parameter_list|,
name|boolean
name|wholeFilter
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
name|withPreserveExprCondition
argument_list|(
name|preserveExprCondition
argument_list|)
operator|.
name|withWholeProject
argument_list|(
name|wholeProject
argument_list|)
operator|.
name|withWholeFilter
argument_list|(
name|wholeFilter
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
name|Project
name|origProject
decl_stmt|;
specifier|final
name|Filter
name|filter
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
name|origProject
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|filter
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|origProject
operator|=
literal|null
expr_stmt|;
name|filter
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|input
init|=
name|filter
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|origFilter
init|=
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|origProject
operator|!=
literal|null
operator|&&
name|origProject
operator|.
name|containsOver
argument_list|()
condition|)
block|{
comment|// Cannot push project through filter if project contains a windowed
comment|// aggregate -- it will affect row counts. Abort this rule
comment|// invocation; pushdown will be considered after the windowed
comment|// aggregate has been implemented. It's OK if the filter contains a
comment|// windowed aggregate.
return|return;
block|}
if|if
condition|(
operator|(
name|origProject
operator|!=
literal|null
operator|)
operator|&&
name|origProject
operator|.
name|getRowType
argument_list|()
operator|.
name|isStruct
argument_list|()
operator|&&
name|origProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|RelDataTypeField
operator|::
name|isDynamicStar
argument_list|)
condition|)
block|{
comment|// The PushProjector would change the plan:
comment|//
comment|//    prj(**=[$0])
comment|//    : - filter
comment|//        : - scan
comment|//
comment|// to form like:
comment|//
comment|//    prj(**=[$0])                    (1)
comment|//    : - filter                      (2)
comment|//        : - prj(**=[$0], ITEM= ...) (3)
comment|//            :  - scan
comment|// This new plan has more cost that the old one, because of the new
comment|// redundant project (3), if we also have FilterProjectTransposeRule in
comment|// the rule set, it will also trigger infinite match of the ProjectMergeRule
comment|// for project (1) and (3).
return|return;
block|}
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|topProject
decl_stmt|;
if|if
condition|(
name|origProject
operator|!=
literal|null
operator|&&
operator|(
name|config
operator|.
name|isWholeProject
argument_list|()
operator|||
name|config
operator|.
name|isWholeFilter
argument_list|()
operator|)
condition|)
block|{
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|set
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelOptUtil
operator|.
name|InputFinder
name|refCollector
init|=
operator|new
name|RelOptUtil
operator|.
name|InputFinder
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|isWholeFilter
argument_list|()
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
name|refCollector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|isWholeProject
argument_list|()
condition|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|origProject
operator|.
name|getProjects
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refCollector
operator|.
name|visitEach
argument_list|(
name|origProject
operator|.
name|getProjects
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Build a list with inputRefs, in order, first, then other expressions.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|refs
init|=
name|refCollector
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|field
range|:
name|builder
operator|.
name|fields
argument_list|()
control|)
block|{
if|if
condition|(
name|refs
operator|.
name|get
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|field
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|||
name|set
operator|.
name|contains
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|set
operator|.
name|removeAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|list
argument_list|)
expr_stmt|;
specifier|final
name|Replacer
name|replacer
init|=
operator|new
name|Replacer
argument_list|(
name|list
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|builder
operator|.
name|filter
argument_list|(
name|replacer
operator|.
name|visit
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|replacer
operator|.
name|visitList
argument_list|(
name|origProject
operator|.
name|getProjects
argument_list|()
argument_list|)
argument_list|,
name|origProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|topProject
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// The traditional mode of operation of this rule: push down field
comment|// references. The effect is similar to RelFieldTrimmer.
specifier|final
name|PushProjector
name|pushProjector
init|=
operator|new
name|PushProjector
argument_list|(
name|origProject
argument_list|,
name|origFilter
argument_list|,
name|input
argument_list|,
name|config
operator|.
name|preserveExprCondition
argument_list|()
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|topProject
operator|=
name|pushProjector
operator|.
name|convertProject
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|topProject
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|topProject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Replaces whole expressions, or parts of an expression, with references to    * expressions computed by an underlying Project. */
specifier|private
specifier|static
class|class
name|Replacer
extends|extends
name|RexShuttle
block|{
specifier|final
name|ImmutableMap
argument_list|<
name|RexNode
argument_list|,
name|Integer
argument_list|>
name|map
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
decl_stmt|;
name|Replacer
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exprs
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|)
block|{
name|this
operator|.
name|relBuilder
operator|=
name|relBuilder
expr_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|,
name|Integer
argument_list|>
name|b
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RexNode
name|expr
range|:
name|exprs
control|)
block|{
name|b
operator|.
name|put
argument_list|(
name|expr
argument_list|,
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
name|map
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|RexNode
name|visit
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
specifier|final
name|Integer
name|i
init|=
name|map
operator|.
name|get
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|null
condition|)
block|{
return|return
name|relBuilder
operator|.
name|field
argument_list|(
name|i
argument_list|)
return|;
block|}
return|return
name|e
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitList
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exprs
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|out
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|expr
range|:
name|exprs
control|)
block|{
name|out
operator|.
name|add
argument_list|(
name|visit
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|RexNode
argument_list|>
name|visitList
argument_list|(
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exprs
argument_list|,
name|boolean
expr|@
name|Nullable
index|[]
name|update
argument_list|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|>
name|clonedOperands
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|exprs
control|)
block|{
name|RexNode
name|clonedOperand
init|=
name|visit
argument_list|(
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|clonedOperand
operator|!=
name|operand
operator|)
operator|&&
operator|(
name|update
operator|!=
literal|null
operator|)
condition|)
block|{
name|update
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
name|clonedOperands
operator|.
name|add
argument_list|(
name|clonedOperand
argument_list|)
expr_stmt|;
block|}
return|return
name|clonedOperands
operator|.
name|build
argument_list|()
return|;
block|}
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
name|ImmutableProjectFilterTransposeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
decl_stmt|;
name|Config
name|PROJECT
init|=
name|DEFAULT
operator|.
name|withWholeProject
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Config
name|PROJECT_FILTER
init|=
name|PROJECT
operator|.
name|withWholeFilter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectFilterTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectFilterTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Expressions that should be preserved in the projection. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|()
block|{
return|return
name|expr
lambda|->
literal|false
return|;
block|}
comment|/** Sets {@link #preserveExprCondition()}. */
name|Config
name|withPreserveExprCondition
parameter_list|(
name|PushProjector
operator|.
name|ExprCondition
name|condition
parameter_list|)
function_decl|;
comment|/** Whether to push whole expressions from the project;      * if false (the default), only pushes references. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isWholeProject
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Sets {@link #isWholeProject()}. */
name|Config
name|withWholeProject
parameter_list|(
name|boolean
name|wholeProject
parameter_list|)
function_decl|;
comment|/** Whether to push whole expressions from the filter;      * if false (the default), only pushes references. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isWholeFilter
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Sets {@link #isWholeFilter()}. */
name|Config
name|withWholeFilter
parameter_list|(
name|boolean
name|wholeFilter
parameter_list|)
function_decl|;
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
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
name|Filter
argument_list|>
name|filterClass
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
name|projectClass
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
name|filterClass
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
annotation|@
name|Override
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|OperandTransform
name|operandSupplier
parameter_list|()
block|{
return|return
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
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
name|LogicalFilter
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
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
name|Project
argument_list|>
name|projectClass
parameter_list|,
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
name|RelNode
argument_list|>
name|inputClass
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
name|projectClass
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
name|filterClass
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
name|inputClass
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

