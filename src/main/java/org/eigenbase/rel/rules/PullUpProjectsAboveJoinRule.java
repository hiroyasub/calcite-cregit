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
name|rel
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * PullUpProjectsAboveJoinRule implements the rule for pulling {@link  * ProjectRel}s beneath a {@link JoinRel} above the {@link JoinRel}. Projections  * are pulled up if the {@link ProjectRel} doesn't originate from a null  * generating input in an outer join.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|PullUpProjectsAboveJoinRule
extends|extends
name|RelOptRule
block|{
comment|// ~ Static fields/initializers --------------------------------------------
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|PullUpProjectsAboveJoinRule
name|instanceTwoProjectChildren
init|=
operator|new
name|PullUpProjectsAboveJoinRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsAboveJoinRule: with two ProjectRel children"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PullUpProjectsAboveJoinRule
name|instanceLeftProjectChild
init|=
operator|new
name|PullUpProjectsAboveJoinRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsAboveJoinRule: with ProjectRel on left"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PullUpProjectsAboveJoinRule
name|instanceRightProjectChild
init|=
operator|new
name|PullUpProjectsAboveJoinRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsAboveJoinRule: with ProjectRel on right"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|PullUpProjectsAboveJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|JoinRel
name|joinRel
init|=
operator|(
name|JoinRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|JoinRelType
name|joinType
init|=
name|joinRel
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
name|ProjectRel
name|leftProj
decl_stmt|;
name|ProjectRel
name|rightProj
decl_stmt|;
name|RelNode
name|leftJoinChild
decl_stmt|;
name|RelNode
name|rightJoinChild
decl_stmt|;
comment|// see if at least one input's projection doesn't generate nulls
if|if
condition|(
name|hasLeftChild
argument_list|(
name|call
argument_list|)
operator|&&
operator|!
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
condition|)
block|{
name|leftProj
operator|=
operator|(
name|ProjectRel
operator|)
name|call
operator|.
name|rels
index|[
literal|1
index|]
expr_stmt|;
name|leftJoinChild
operator|=
name|getProjectChild
argument_list|(
name|call
argument_list|,
name|leftProj
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|leftProj
operator|=
literal|null
expr_stmt|;
name|leftJoinChild
operator|=
name|call
operator|.
name|rels
index|[
literal|1
index|]
expr_stmt|;
block|}
if|if
condition|(
name|hasRightChild
argument_list|(
name|call
argument_list|)
operator|&&
operator|!
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
condition|)
block|{
name|rightProj
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
name|rightProj
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rightProj
operator|=
literal|null
expr_stmt|;
name|rightJoinChild
operator|=
name|joinRel
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|leftProj
operator|==
literal|null
operator|)
operator|&&
operator|(
name|rightProj
operator|==
literal|null
operator|)
condition|)
block|{
return|return;
block|}
comment|// Construct two RexPrograms and combine them.  The bottom program
comment|// is a join of the projection expressions from the left and/or
comment|// right projects that feed into the join.  The top program contains
comment|// the join condition.
comment|// Create a row type representing a concatentation of the inputs
comment|// underneath the projects that feed into the join.  This is the input
comment|// into the bottom RexProgram.  Note that the join type is an inner
comment|// join because the inputs haven't actually been joined yet.
name|RelDataType
name|joinChildrenRowType
init|=
name|JoinRel
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
name|joinRel
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
expr|<
name|RelDataTypeField
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create projection expressions, combining the projection expressions
comment|// from the projects that feed into the join.  For the RHS projection
comment|// expressions, shift them to the right by the number of fields on
comment|// the LHS.  If the join input was not a projection, simply create
comment|// references to the inputs.
name|int
name|nProjExprs
init|=
name|joinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|RexNode
index|[]
name|projExprs
init|=
operator|new
name|RexNode
index|[
name|nProjExprs
index|]
decl_stmt|;
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[
name|nProjExprs
index|]
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|createProjectExprs
argument_list|(
name|leftProj
argument_list|,
name|leftJoinChild
argument_list|,
literal|0
argument_list|,
name|rexBuilder
argument_list|,
name|joinChildrenRowType
operator|.
name|getFields
argument_list|()
argument_list|,
name|projExprs
argument_list|,
name|fieldNames
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|RelDataTypeField
index|[]
name|leftFields
init|=
name|leftJoinChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|int
name|nFieldsLeft
init|=
name|leftFields
operator|.
name|length
decl_stmt|;
name|createProjectExprs
argument_list|(
name|rightProj
argument_list|,
name|rightJoinChild
argument_list|,
name|nFieldsLeft
argument_list|,
name|rexBuilder
argument_list|,
name|joinChildrenRowType
operator|.
name|getFields
argument_list|()
argument_list|,
name|projExprs
argument_list|,
name|fieldNames
argument_list|,
operator|(
operator|(
name|leftProj
operator|==
literal|null
operator|)
condition|?
name|nFieldsLeft
else|:
name|leftProj
operator|.
name|getProjectExps
argument_list|()
operator|.
name|length
operator|)
argument_list|)
expr_stmt|;
name|RelDataType
index|[]
name|projTypes
init|=
operator|new
name|RelDataType
index|[
name|nProjExprs
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
name|projTypes
index|[
name|i
index|]
operator|=
name|projExprs
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
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
name|fieldNames
argument_list|)
decl_stmt|;
comment|// create the RexPrograms and merge them
name|RexProgram
name|bottomProgram
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|joinChildrenRowType
argument_list|,
name|projExprs
argument_list|,
literal|null
argument_list|,
name|projRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
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
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
name|RexProgram
name|topProgram
init|=
name|topProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
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
comment|// expand out the join condition and construct a new JoinRel that
comment|// directly references the join children without the intervening
comment|// ProjectRels
name|RexNode
name|newCondition
init|=
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|mergedProgram
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
name|JoinRel
name|newJoinRel
init|=
operator|new
name|JoinRel
argument_list|(
name|joinRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|leftJoinChild
argument_list|,
name|rightJoinChild
argument_list|,
name|newCondition
argument_list|,
name|joinRel
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getVariablesStopped
argument_list|()
argument_list|)
decl_stmt|;
comment|// expand out the new projection expressions; if the join is an
comment|// outer join, modify the expressions to reference the join output
name|RexNode
index|[]
name|newProjExprs
init|=
operator|new
name|RexNode
index|[
name|nProjExprs
index|]
decl_stmt|;
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
name|RelDataTypeField
index|[]
name|newJoinFields
init|=
name|newJoinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|int
name|nJoinFields
init|=
name|newJoinFields
operator|.
name|length
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
operator|==
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
name|newProjExprs
index|[
name|i
index|]
operator|=
name|newExpr
expr_stmt|;
block|}
else|else
block|{
name|newProjExprs
index|[
name|i
index|]
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
name|getFields
argument_list|()
argument_list|,
name|newJoinFields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// finally, create the projection on top of the join
name|RelNode
name|newProjRel
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newJoinRel
argument_list|,
name|newProjExprs
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newProjRel
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param call RelOptRuleCall      *      * @return true if the rule was invoked with a left project child      */
specifier|protected
name|boolean
name|hasLeftChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
operator|(
name|call
operator|.
name|rels
index|[
literal|1
index|]
operator|instanceof
name|ProjectRel
operator|)
return|;
block|}
comment|/**      * @param call RelOptRuleCall      *      * @return true if the rule was invoked with 2 children      */
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
comment|/**      * @param call RelOptRuleCall      *      * @return ProjectRel corresponding to the right child      */
specifier|protected
name|ProjectRel
name|getRightChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
operator|(
name|ProjectRel
operator|)
name|call
operator|.
name|rels
index|[
literal|2
index|]
return|;
block|}
comment|/**      * Returns the child of the project that will be used as input into the new      * JoinRel once the projects are pulled above the JoinRel.      *      * @param call RelOptRuleCall      * @param project project RelNode      * @param leftChild true if the project corresponds to the left projection      * @return child of the project that will be used as input into the new      * JoinRel once the projects are pulled above the JoinRel      */
specifier|protected
name|RelNode
name|getProjectChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|ProjectRel
name|project
parameter_list|,
name|boolean
name|leftChild
parameter_list|)
block|{
return|return
name|project
operator|.
name|getChild
argument_list|()
return|;
block|}
comment|/**      * Creates projection expressions corresponding to one of the inputs into      * the join      *      * @param projRel the projection input into the join (if it exists)      * @param joinChild the child of the projection input (if there is a      * projection); otherwise, this is the join input      * @param adjustmentAmount the amount the expressions need to be shifted by      * @param rexBuilder rex builder      * @param joinChildrenFields concatentation of the fields from the left and      * right join inputs (once the projections have been removed)      * @param projExprs array of projection expressions to be created      * @param fieldNames array of the names of the projection fields      * @param offset starting index in the arrays to be filled in      */
specifier|private
name|void
name|createProjectExprs
parameter_list|(
name|ProjectRel
name|projRel
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
name|RelDataTypeField
index|[]
name|joinChildrenFields
parameter_list|,
name|RexNode
index|[]
name|projExprs
parameter_list|,
name|String
index|[]
name|fieldNames
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|RelDataTypeField
index|[]
name|childFields
init|=
name|joinChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
if|if
condition|(
name|projRel
operator|!=
literal|null
condition|)
block|{
name|RexNode
index|[]
name|origProjExprs
init|=
name|projRel
operator|.
name|getProjectExps
argument_list|()
decl_stmt|;
name|RelDataTypeField
index|[]
name|projFields
init|=
name|projRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|int
name|nChildFields
init|=
name|childFields
operator|.
name|length
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|origProjExprs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|adjustmentAmount
operator|==
literal|0
condition|)
block|{
name|projExprs
index|[
name|i
operator|+
name|offset
index|]
operator|=
name|origProjExprs
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// shift the references by the adjustment amount
name|RexNode
name|newProjExpr
init|=
name|origProjExprs
index|[
name|i
index|]
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
decl_stmt|;
name|projExprs
index|[
name|i
operator|+
name|offset
index|]
operator|=
name|newProjExpr
expr_stmt|;
block|}
name|fieldNames
index|[
name|i
operator|+
name|offset
index|]
operator|=
name|projFields
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|projExprs
index|[
name|i
operator|+
name|offset
index|]
operator|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|childFields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|i
operator|+
name|adjustmentAmount
argument_list|)
expr_stmt|;
name|fieldNames
index|[
name|i
operator|+
name|offset
index|]
operator|=
name|childFields
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End PullUpProjectsAboveJoinRule.java
end_comment

end_unit

