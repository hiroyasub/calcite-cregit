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
name|test
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

begin_comment
comment|/**  * MockRelOptPlanner is a mock implementation of the {@link RelOptPlanner}  * interface.  */
end_comment

begin_class
specifier|public
class|class
name|MockRelOptPlanner
extends|extends
name|AbstractRelOptPlanner
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|RelNode
name|root
decl_stmt|;
specifier|private
name|RelOptRule
name|rule
decl_stmt|;
specifier|private
name|RelNode
name|transformationResult
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptPlanner
specifier|public
name|void
name|setRoot
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|rel
expr_stmt|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|RelNode
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|public
name|void
name|addMaterialization
parameter_list|(
name|RelNode
name|tableRel
parameter_list|,
name|RelNode
name|queryRel
parameter_list|)
block|{
comment|// ignore - this planner does not support materializations
block|}
comment|// implement RelOptPlanner
specifier|public
name|boolean
name|addRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|rule
operator|==
literal|null
operator|:
literal|"MockRelOptPlanner only supports a single rule"
assert|;
name|this
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|boolean
name|removeRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|RelNode
name|changeTraits
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|toTraits
parameter_list|)
block|{
return|return
name|rel
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|RelNode
name|findBestExp
parameter_list|()
block|{
if|if
condition|(
name|rule
operator|!=
literal|null
condition|)
block|{
name|matchRecursive
argument_list|(
name|root
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
comment|/**      * Recursively matches a rule.      *      *      *      * @param rel Relational expression      * @param parent Parent relational expression      * @param ordinalInParent Ordinal of relational expression among its      * siblings      *      * @return whether match occurred      */
specifier|private
name|boolean
name|matchRecursive
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|parent
parameter_list|,
name|int
name|ordinalInParent
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|bindings
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|rule
operator|.
name|getOperand
argument_list|()
argument_list|,
name|rel
argument_list|,
name|bindings
argument_list|)
condition|)
block|{
name|MockRuleCall
name|call
init|=
operator|new
name|MockRuleCall
argument_list|(
name|this
argument_list|,
name|rule
operator|.
name|getOperand
argument_list|()
argument_list|,
name|bindings
operator|.
name|toArray
argument_list|(
operator|new
name|RelNode
index|[
name|bindings
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|rule
operator|.
name|matches
argument_list|(
name|call
argument_list|)
condition|)
block|{
name|rule
operator|.
name|onMatch
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|transformationResult
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|transformationResult
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|replaceInput
argument_list|(
name|ordinalInParent
argument_list|,
name|transformationResult
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|List
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|children
init|=
name|rel
operator|.
name|getInputs
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
name|children
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|matchRecursive
argument_list|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|rel
argument_list|,
name|i
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Matches a relational expression to a rule.      *      * @param operand Root operand of rule      * @param rel Relational expression      * @param bindings Bindings, populated on successful match      *      * @return whether relational expression matched rule      */
specifier|private
name|boolean
name|match
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|bindings
parameter_list|)
block|{
if|if
condition|(
operator|!
name|operand
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|bindings
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|RelOptRuleOperand
index|[]
name|childOperands
init|=
name|operand
operator|.
name|getChildOperands
argument_list|()
decl_stmt|;
if|if
condition|(
name|childOperands
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|n
init|=
name|childOperands
operator|.
name|length
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|childRels
init|=
name|rel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|!=
name|childRels
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
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
name|n
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
operator|!
name|match
argument_list|(
name|childOperands
index|[
name|i
index|]
argument_list|,
name|childRels
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|bindings
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|RelNode
name|register
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|equivRel
parameter_list|)
block|{
return|return
name|rel
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|RelNode
name|ensureRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|equivRel
parameter_list|)
block|{
return|return
name|rel
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|boolean
name|isRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
class|class
name|MockRuleCall
extends|extends
name|RelOptRuleCall
block|{
comment|/**          * Creates a MockRuleCall.          *          * @param planner Planner          * @param operand Operand          * @param rels List of matched relational expressions          */
name|MockRuleCall
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelNode
index|[]
name|rels
parameter_list|)
block|{
name|super
argument_list|(
name|planner
argument_list|,
name|operand
argument_list|,
name|rels
argument_list|,
name|Collections
operator|.
expr|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// implement RelOptRuleCall
specifier|public
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|transformationResult
operator|=
name|rel
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MockRelOptPlanner.java
end_comment

end_unit

