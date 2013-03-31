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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * PushProjectPastSetOpRule implements the rule for pushing a {@link ProjectRel}  * past a {@link SetOpRel}. The children of the {@link SetOpRel} will project  * only the {@link RexInputRef}s referenced in the original {@link ProjectRel}.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|PushProjectPastSetOpRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushProjectPastSetOpRule
name|instance
init|=
operator|new
name|PushProjectPastSetOpRule
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Expressions that should be preserved in the projection      */
specifier|private
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a PushProjectPastSetOpRule.      */
specifier|private
name|PushProjectPastSetOpRule
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
comment|/**      * Creates a PushProjectPastSetOpRule with an explicit condition whether      * to preserve expressions.      *      * @param preserveExprCondition Condition whether to preserve expressions      */
specifier|public
name|PushProjectPastSetOpRule
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
name|SetOpRel
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
name|SetOpRel
name|setOpRel
init|=
operator|(
name|SetOpRel
operator|)
name|call
operator|.
name|rels
index|[
literal|1
index|]
decl_stmt|;
comment|// cannot push project past a distinct
if|if
condition|(
operator|!
name|setOpRel
operator|.
name|all
condition|)
block|{
return|return;
block|}
comment|// locate all fields referenced in the projection
name|PushProjector
name|pushProject
init|=
operator|new
name|PushProjector
argument_list|(
name|origProj
argument_list|,
literal|null
argument_list|,
name|setOpRel
argument_list|,
name|preserveExprCondition
argument_list|)
decl_stmt|;
name|pushProject
operator|.
name|locateAllRefs
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newSetOpInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
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
comment|// push the projects completely below the setop; this
comment|// is different from pushing below a join, where we decompose
comment|// to try to keep expensive expressions above the join,
comment|// because UNION ALL does not have any filtering effect,
comment|// and it is the only operator this rule currently acts on
for|for
control|(
name|RelNode
name|input
range|:
name|setOpRel
operator|.
name|getInputs
argument_list|()
control|)
block|{
comment|// be lazy:  produce two ProjectRels, and let another rule
comment|// merge them (could probably just clone origProj instead?)
name|ProjectRel
name|p
init|=
name|pushProject
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|input
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|newSetOpInputs
operator|.
name|add
argument_list|(
name|pushProject
operator|.
name|createNewProject
argument_list|(
name|p
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create a new setop whose children are the ProjectRels created above
name|SetOpRel
name|newSetOpRel
init|=
name|setOpRel
operator|.
name|copy
argument_list|(
name|setOpRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newSetOpInputs
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newSetOpRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PushProjectPastSetOpRule.java
end_comment

end_unit

