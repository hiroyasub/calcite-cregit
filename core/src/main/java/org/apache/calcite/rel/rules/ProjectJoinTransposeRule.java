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
name|Join
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
name|LogicalJoin
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
name|RexCall
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
name|RexOver
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
name|ImmutableBeans
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Project}  * past a {@link org.apache.calcite.rel.core.Join}  * by splitting the projection into a projection on top of each child of  * the join.  *  * @see CoreRules#PROJECT_JOIN_TRANSPOSE  */
end_comment

begin_class
specifier|public
class|class
name|ProjectJoinTransposeRule
extends|extends
name|RelRule
argument_list|<
name|ProjectJoinTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a ProjectJoinTransposeRule. */
specifier|protected
name|ProjectJoinTransposeRule
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
name|ProjectJoinTransposeRule
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
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
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
name|projectClass
argument_list|,
name|joinClass
argument_list|)
operator|.
name|withPreserveExprCondition
argument_list|(
name|preserveExprCondition
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
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Normalize the join condition so we don't end up misidentified expanded
comment|// form of IS NOT DISTINCT FROM as PushProject also visit the filter condition
comment|// and push down expressions.
name|RexNode
name|joinFilter
init|=
name|join
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitCall
parameter_list|(
name|RexCall
name|rexCall
parameter_list|)
block|{
specifier|final
name|RexNode
name|node
init|=
name|super
operator|.
name|visitCall
argument_list|(
name|rexCall
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|RexCall
operator|)
condition|)
block|{
return|return
name|node
return|;
block|}
return|return
name|RelOptUtil
operator|.
name|collapseExpandedIsNotDistinctFromExpr
argument_list|(
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|call
operator|.
name|builder
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// locate all fields referenced in the projection and join condition;
comment|// determine which inputs are referenced in the projection and
comment|// join condition; if all fields are being referenced and there are no
comment|// special expressions, no point in proceeding any further
specifier|final
name|PushProjector
name|pushProjector
init|=
operator|new
name|PushProjector
argument_list|(
name|origProject
argument_list|,
name|joinFilter
argument_list|,
name|join
argument_list|,
name|config
operator|.
name|preserveExprCondition
argument_list|()
argument_list|,
name|call
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pushProjector
operator|.
name|locateAllRefs
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// create left and right projections, projecting only those
comment|// fields referenced on each side
specifier|final
name|RelNode
name|leftProject
init|=
name|pushProjector
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rightProject
init|=
name|pushProjector
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// convert the join condition to reference the projected columns
name|RexNode
name|newJoinFilter
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
name|pushProjector
operator|.
name|getAdjustments
argument_list|()
decl_stmt|;
if|if
condition|(
name|joinFilter
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|projectJoinFieldList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|projectJoinFieldList
operator|.
name|addAll
argument_list|(
name|join
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projectJoinFieldList
operator|.
name|addAll
argument_list|(
name|leftProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projectJoinFieldList
operator|.
name|addAll
argument_list|(
name|rightProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|newJoinFilter
operator|=
name|pushProjector
operator|.
name|convertRefsAndExprs
argument_list|(
name|joinFilter
argument_list|,
name|projectJoinFieldList
argument_list|,
name|adjustments
argument_list|)
expr_stmt|;
block|}
comment|// create a new join with the projected children
specifier|final
name|Join
name|newJoin
init|=
name|join
operator|.
name|copy
argument_list|(
name|join
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|requireNonNull
argument_list|(
name|newJoinFilter
argument_list|,
literal|"newJoinFilter must not be null"
argument_list|)
argument_list|,
name|leftProject
argument_list|,
name|rightProject
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
comment|// put the original project on top of the join, converting it to
comment|// reference the modified projection list
specifier|final
name|RelNode
name|topProject
init|=
name|pushProjector
operator|.
name|createNewProject
argument_list|(
name|newJoin
argument_list|,
name|adjustments
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|topProject
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
name|Config
name|DEFAULT
init|=
name|EMPTY
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
name|LogicalProject
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|withPreserveExprCondition
argument_list|(
name|expr
lambda|->
operator|!
operator|(
name|expr
operator|instanceof
name|RexOver
operator|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectJoinTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectJoinTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines when an expression should not be pushed. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|()
function_decl|;
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
name|Join
argument_list|>
name|joinClass
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
name|joinClass
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

