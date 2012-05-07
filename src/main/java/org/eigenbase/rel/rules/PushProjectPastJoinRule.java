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
comment|/**  * PushProjectPastJoinRule implements the rule for pushing a projection past a  * join by splitting the projection into a projection on top of each child of  * the join.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|PushProjectPastJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushProjectPastJoinRule
name|instance
init|=
operator|new
name|PushProjectPastJoinRule
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Condition for expressions that should be preserved in the projection.      */
specifier|private
specifier|final
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a PushProjectPastJoinRule.      */
specifier|private
name|PushProjectPastJoinRule
parameter_list|()
block|{
name|this
argument_list|(
name|PushProjector
operator|.
name|ExprCondition
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a PushProjectPastJoinRule with an explicit condition.      *      * @param preserveExprCondition Condition for expressions that should be      * preserved in the projection      */
specifier|public
name|PushProjectPastJoinRule
parameter_list|(
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|preserveExprCondition
operator|=
name|preserveExprCondition
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
name|ProjectRel
name|origProj
init|=
operator|(
name|ProjectRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
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
literal|1
index|]
decl_stmt|;
comment|// locate all fields referenced in the projection and join condition;
comment|// determine which inputs are referenced in the projection and
comment|// join condition; if all fields are being referenced and there are no
comment|// special expressions, no point in proceeding any further
name|PushProjector
name|pushProject
init|=
operator|new
name|PushProjector
argument_list|(
name|origProj
argument_list|,
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|joinRel
argument_list|,
name|preserveExprCondition
argument_list|)
decl_stmt|;
if|if
condition|(
name|pushProject
operator|.
name|locateAllRefs
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// create left and right projections, projecting only those
comment|// fields referenced on each side
name|RelNode
name|leftProjRel
init|=
name|pushProject
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|joinRel
operator|.
name|getLeft
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RelNode
name|rightProjRel
init|=
name|pushProject
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|joinRel
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
name|pushProject
operator|.
name|getAdjustments
argument_list|()
decl_stmt|;
if|if
condition|(
name|joinRel
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|projJoinFieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
decl_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|joinRel
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|leftProjRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|rightProjRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|RelDataTypeField
index|[]
name|projJoinFields
init|=
name|projJoinFieldList
operator|.
name|toArray
argument_list|(
operator|new
name|RelDataTypeField
index|[
name|projJoinFieldList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|newJoinFilter
operator|=
name|pushProject
operator|.
name|convertRefsAndExprs
argument_list|(
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|projJoinFields
argument_list|,
name|adjustments
argument_list|)
expr_stmt|;
block|}
comment|// create a new joinrel with the projected children
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
name|leftProjRel
argument_list|,
name|rightProjRel
argument_list|,
name|newJoinFilter
argument_list|,
name|joinRel
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
name|joinRel
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
decl_stmt|;
comment|// put the original project on top of the join, converting it to
comment|// reference the modified projection list
name|ProjectRel
name|topProject
init|=
name|pushProject
operator|.
name|createNewProject
argument_list|(
name|newJoinRel
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
block|}
end_class

begin_comment
comment|// End PushProjectPastJoinRule.java
end_comment

end_unit

