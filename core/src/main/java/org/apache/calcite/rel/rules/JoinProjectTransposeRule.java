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
name|Contexts
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
name|Strong
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
name|JoinRelType
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
operator|.
name|ProjectFactory
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
name|RexLocalRef
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
name|RexProgram
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
name|RexProgramBuilder
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|Pair
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
name|Collections
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
comment|/**  * Planner rule that matches a  * {@link org.apache.calcite.rel.core.Join} one of whose inputs is a  * {@link org.apache.calcite.rel.logical.LogicalProject}, and  * pulls the project up.  *  *<p>Projections are pulled up if the  * {@link org.apache.calcite.rel.logical.LogicalProject} doesn't originate from  * a null generating input in an outer join.  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|JoinProjectTransposeRule
extends|extends
name|RelRule
argument_list|<
name|JoinProjectTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a JoinProjectTransposeRule. */
specifier|protected
name|JoinProjectTransposeRule
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
name|JoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|,
name|boolean
name|includeOuter
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
name|withDescription
argument_list|(
name|description
argument_list|)
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
name|withIncludeOuter
argument_list|(
name|includeOuter
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withDescription
argument_list|(
name|description
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|,
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
name|withDescription
argument_list|(
name|description
argument_list|)
operator|.
name|withRelBuilderFactory
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|projectFactory
argument_list|)
argument_list|)
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|,
name|boolean
name|includeOuter
parameter_list|,
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
name|withDescription
argument_list|(
name|description
argument_list|)
operator|.
name|withRelBuilderFactory
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|projectFactory
argument_list|)
argument_list|)
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
name|withIncludeOuter
argument_list|(
name|includeOuter
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
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|JoinRelType
name|joinType
init|=
name|join
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
name|Project
name|leftProject
decl_stmt|;
name|Project
name|rightProject
decl_stmt|;
name|RelNode
name|leftJoinChild
decl_stmt|;
name|RelNode
name|rightJoinChild
decl_stmt|;
comment|// If 1) the rule works on outer joins, or
comment|//    2) input's projection doesn't generate nulls
specifier|final
name|boolean
name|includeOuter
init|=
name|config
operator|.
name|isIncludeOuter
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasLeftChild
argument_list|(
name|call
argument_list|)
operator|&&
operator|(
name|includeOuter
operator|||
operator|!
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
operator|)
condition|)
block|{
name|leftProject
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|leftJoinChild
operator|=
name|getProjectChild
argument_list|(
name|call
argument_list|,
name|leftProject
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|leftProject
operator|=
literal|null
expr_stmt|;
name|leftJoinChild
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasRightChild
argument_list|(
name|call
argument_list|)
operator|&&
operator|(
name|includeOuter
operator|||
operator|!
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
operator|)
condition|)
block|{
name|rightProject
operator|=
name|getRightChild
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|rightJoinChild
operator|=
name|getProjectChild
argument_list|(
name|call
argument_list|,
name|rightProject
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rightProject
operator|=
literal|null
expr_stmt|;
name|rightJoinChild
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
comment|// Skip projects containing over clause
if|if
condition|(
name|leftProject
operator|!=
literal|null
operator|&&
name|leftProject
operator|.
name|containsOver
argument_list|()
condition|)
block|{
name|leftProject
operator|=
literal|null
expr_stmt|;
name|leftJoinChild
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rightProject
operator|!=
literal|null
operator|&&
name|rightProject
operator|.
name|containsOver
argument_list|()
condition|)
block|{
name|rightProject
operator|=
literal|null
expr_stmt|;
name|rightJoinChild
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|leftProject
operator|==
literal|null
operator|)
operator|&&
operator|(
name|rightProject
operator|==
literal|null
operator|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|includeOuter
condition|)
block|{
if|if
condition|(
name|leftProject
operator|!=
literal|null
operator|&&
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
operator|&&
operator|!
name|Strong
operator|.
name|allStrong
argument_list|(
name|leftProject
operator|.
name|getProjects
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|rightProject
operator|!=
literal|null
operator|&&
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
operator|&&
operator|!
name|Strong
operator|.
name|allStrong
argument_list|(
name|rightProject
operator|.
name|getProjects
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
comment|// Construct two RexPrograms and combine them.  The bottom program
comment|// is a join of the projection expressions from the left and/or
comment|// right projects that feed into the join.  The top program contains
comment|// the join condition.
comment|// Create a row type representing a concatenation of the inputs
comment|// underneath the projects that feed into the join.  This is the input
comment|// into the bottom RexProgram.  Note that the join type is an inner
comment|// join because the inputs haven't actually been joined yet.
specifier|final
name|RelDataType
name|joinChildrenRowType
init|=
name|SqlValidatorUtil
operator|.
name|deriveJoinRowType
argument_list|(
name|leftJoinChild
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rightJoinChild
operator|.
name|getRowType
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create projection expressions, combining the projection expressions
comment|// from the projects that feed into the join.  For the RHS projection
comment|// expressions, shift them to the right by the number of fields on
comment|// the LHS.  If the join input was not a projection, simply create
comment|// references to the inputs.
specifier|final
name|int
name|nProjExprs
init|=
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|createProjectExprs
argument_list|(
name|leftProject
argument_list|,
name|leftJoinChild
argument_list|,
literal|0
argument_list|,
name|rexBuilder
argument_list|,
name|joinChildrenRowType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|projects
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|leftFields
init|=
name|leftJoinChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nFieldsLeft
init|=
name|leftFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|createProjectExprs
argument_list|(
name|rightProject
argument_list|,
name|rightJoinChild
argument_list|,
name|nFieldsLeft
argument_list|,
name|rexBuilder
argument_list|,
name|joinChildrenRowType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|projects
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|projTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nProjExprs
condition|;
name|i
operator|++
control|)
block|{
name|projTypes
operator|.
name|add
argument_list|(
name|projects
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|left
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RelDataType
name|projRowType
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|projTypes
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|projects
argument_list|)
argument_list|)
decl_stmt|;
comment|// create the RexPrograms and merge them
specifier|final
name|RexProgram
name|bottomProgram
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|joinChildrenRowType
argument_list|,
name|Pair
operator|.
name|left
argument_list|(
name|projects
argument_list|)
argument_list|,
literal|null
argument_list|,
name|projRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
specifier|final
name|RexProgramBuilder
name|topProgramBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|projRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|topProgramBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
name|topProgramBuilder
operator|.
name|addCondition
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RexProgram
name|topProgram
init|=
name|topProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
specifier|final
name|RexProgram
name|mergedProgram
init|=
name|RexProgramBuilder
operator|.
name|mergePrograms
argument_list|(
name|topProgram
argument_list|,
name|bottomProgram
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
comment|// expand out the join condition and construct a new LogicalJoin that
comment|// directly references the join children without the intervening
comment|// ProjectRels
specifier|final
name|RexNode
name|newCondition
init|=
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|requireNonNull
argument_list|(
name|mergedProgram
operator|.
name|getCondition
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"mergedProgram.getCondition() for "
operator|+
name|mergedProgram
argument_list|)
argument_list|)
decl_stmt|;
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
name|newCondition
argument_list|,
name|leftJoinChild
argument_list|,
name|rightJoinChild
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
comment|// expand out the new projection expressions; if the join is an
comment|// outer join, modify the expressions to reference the join output
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newProjExprs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexLocalRef
argument_list|>
name|projList
init|=
name|mergedProgram
operator|.
name|getProjectList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newJoinFields
init|=
name|newJoin
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nJoinFields
init|=
name|newJoinFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|nJoinFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nProjExprs
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|newExpr
init|=
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|projList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|joinType
operator|.
name|isOuterJoin
argument_list|()
condition|)
block|{
name|newExpr
operator|=
name|newExpr
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
name|joinChildrenRowType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|newJoinFields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newProjExprs
operator|.
name|add
argument_list|(
name|newExpr
argument_list|)
expr_stmt|;
block|}
comment|// finally, create the projection on top of the join
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|newJoin
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|newProjExprs
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
comment|// if the join was outer, we might need a cast after the
comment|// projection to fix differences wrt nullability of fields
if|if
condition|(
name|joinType
operator|.
name|isOuterJoin
argument_list|()
condition|)
block|{
name|relBuilder
operator|.
name|convert
argument_list|(
name|join
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
comment|/** Returns whether the rule was invoked with a left project child. */
specifier|protected
name|boolean
name|hasLeftChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|Project
return|;
block|}
comment|/** Returns whether the rule was invoked with 2 children. */
specifier|protected
name|boolean
name|hasRightChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|rels
operator|.
name|length
operator|==
literal|3
return|;
block|}
comment|/** Returns the Project corresponding to the right child. */
specifier|protected
name|Project
name|getRightChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
return|;
block|}
comment|/**    * Returns the child of the project that will be used as input into the new    * LogicalJoin once the projects are pulled above the LogicalJoin.    *    * @param call      RelOptRuleCall    * @param project   project RelNode    * @param leftChild true if the project corresponds to the left projection    * @return child of the project that will be used as input into the new    * LogicalJoin once the projects are pulled above the LogicalJoin    */
specifier|protected
name|RelNode
name|getProjectChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Project
name|project
parameter_list|,
name|boolean
name|leftChild
parameter_list|)
block|{
return|return
name|project
operator|.
name|getInput
argument_list|()
return|;
block|}
comment|/**    * Creates projection expressions corresponding to one of the inputs into    * the join.    *    * @param project            the projection input into the join (if it exists)    * @param joinChild          the child of the projection input (if there is a    *                           projection); otherwise, this is the join input    * @param adjustmentAmount   the amount the expressions need to be shifted by    * @param rexBuilder         rex builder    * @param joinChildrenFields concatenation of the fields from the left and    *                           right join inputs (once the projections have been    *                           removed)    * @param projects           Projection expressions&amp; names to be created    */
specifier|protected
name|void
name|createProjectExprs
parameter_list|(
annotation|@
name|Nullable
name|Project
name|project
parameter_list|,
name|RelNode
name|joinChild
parameter_list|,
name|int
name|adjustmentAmount
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|joinChildrenFields
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
parameter_list|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|childFields
init|=
name|joinChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|project
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|namedProjects
init|=
name|project
operator|.
name|getNamedProjects
argument_list|()
decl_stmt|;
name|int
name|nChildFields
init|=
name|childFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|nChildFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nChildFields
condition|;
name|i
operator|++
control|)
block|{
name|adjustments
index|[
name|i
index|]
operator|=
name|adjustmentAmount
expr_stmt|;
block|}
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|namedProjects
control|)
block|{
name|RexNode
name|e
init|=
name|pair
operator|.
name|left
decl_stmt|;
if|if
condition|(
name|adjustmentAmount
operator|!=
literal|0
condition|)
block|{
comment|// shift the references by the adjustment amount
name|e
operator|=
name|e
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
name|childFields
argument_list|,
name|joinChildrenFields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|projects
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|e
argument_list|,
name|pair
operator|.
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// no projection; just create references to the inputs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|childFields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|childFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|projects
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|i
operator|+
name|adjustmentAmount
argument_list|)
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|ImmutableJoinProjectTransposeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|inputs
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"JoinProjectTransposeRule(Project-Project)"
argument_list|)
decl_stmt|;
name|Config
name|LEFT
init|=
name|DEFAULT
operator|.
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|inputs
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"JoinProjectTransposeRule(Project-Other)"
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
name|Config
name|RIGHT
init|=
name|DEFAULT
operator|.
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|inputs
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"JoinProjectTransposeRule(Other-Project)"
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
name|Config
name|OUTER
init|=
name|DEFAULT
operator|.
name|withDescription
argument_list|(
literal|"Join(IncludingOuter)ProjectTransposeRule(Project-Project)"
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withIncludeOuter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Config
name|LEFT_OUTER
init|=
name|LEFT
operator|.
name|withDescription
argument_list|(
literal|"Join(IncludingOuter)ProjectTransposeRule(Project-Other)"
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withIncludeOuter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Config
name|RIGHT_OUTER
init|=
name|RIGHT
operator|.
name|withDescription
argument_list|(
literal|"Join(IncludingOuter)ProjectTransposeRule(Other-Project)"
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withIncludeOuter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|JoinProjectTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|JoinProjectTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Whether to include outer joins, default false. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isIncludeOuter
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Sets {@link #isIncludeOuter()}. */
name|Config
name|withIncludeOuter
parameter_list|(
name|boolean
name|includeOuter
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

