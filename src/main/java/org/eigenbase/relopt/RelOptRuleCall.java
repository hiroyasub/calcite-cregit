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
name|java
operator|.
name|util
operator|.
name|logging
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
name|trace
operator|.
name|*
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

begin_comment
comment|/**  * A<code>RelOptRuleCall</code> is an invocation of a {@link RelOptRule} with a  * set of {@link RelNode relational expression}s as arguments.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelOptRuleCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|protected
specifier|static
specifier|final
name|Logger
name|tracer
init|=
name|EigenbaseTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelOptRuleOperand
name|operand0
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|nodeChildren
decl_stmt|;
specifier|private
specifier|final
name|RelOptRule
name|rule
decl_stmt|;
specifier|public
specifier|final
name|RelNode
index|[]
name|rels
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|parents
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a RelOptRuleCall.      *      * @param planner Planner      * @param operand Root operand      * @param rels Array of relational expressions which matched each operand      * @param nodeChildren For each node which matched with<code>      * matchAnyChildren</code>=true, a list of the node's children      * @param parents list of parent RelNodes corresponding to the first      * relational expression in the array argument, if known; otherwise, null      */
specifier|protected
name|RelOptRuleCall
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
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|nodeChildren
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|parents
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|this
operator|.
name|operand0
operator|=
name|operand
expr_stmt|;
name|this
operator|.
name|nodeChildren
operator|=
name|nodeChildren
expr_stmt|;
name|this
operator|.
name|rule
operator|=
name|operand
operator|.
name|getRule
argument_list|()
expr_stmt|;
name|this
operator|.
name|rels
operator|=
name|rels
expr_stmt|;
name|this
operator|.
name|parents
operator|=
name|parents
expr_stmt|;
assert|assert
name|rels
operator|.
name|length
operator|==
name|rule
operator|.
name|operands
operator|.
name|size
argument_list|()
assert|;
block|}
specifier|protected
name|RelOptRuleCall
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
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|nodeChildren
parameter_list|)
block|{
name|this
argument_list|(
name|planner
argument_list|,
name|operand
argument_list|,
name|rels
argument_list|,
name|nodeChildren
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the root operand matched by this rule.      *      * @return root operand      */
specifier|public
name|RelOptRuleOperand
name|getOperand0
parameter_list|()
block|{
return|return
name|operand0
return|;
block|}
comment|/**      * Returns the invoked planner rule.      *      * @return planner rule      */
specifier|public
name|RelOptRule
name|getRule
parameter_list|()
block|{
return|return
name|rule
return|;
block|}
comment|/**      * Returns a list of matched relational expressions.      *      * @return matched relational expressions      *      * @deprecated Use {@link #getRelList()} or {@link #rel(int)}      */
specifier|public
name|RelNode
index|[]
name|getRels
parameter_list|()
block|{
return|return
name|rels
return|;
block|}
comment|/**      * Returns a list of matched relational expressions.      *      * @return matched relational expressions      * @see #rel(int)      */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getRelList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rels
argument_list|)
return|;
block|}
comment|/**      * Retrieves the {@code ordinal}th matched relational expression. This      * corresponds to the {@code ordinal}th operand of the rule.      *      * @param ordinal Ordinal      * @param<T> Type      * @return Relational expression      */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelNode
parameter_list|>
name|T
name|rel
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|rels
index|[
name|ordinal
index|]
return|;
block|}
comment|/**      * Returns the children of a given relational expression node matched in a      * rule.      *      *<p>If the operand which caused the match has {@link      * RelOptRuleOperand#matchAnyChildren}=false, the children will have their      * own operands and therefore be easily available in the array returned by      * the {@link #getRels} method, so this method returns null.      *      *<p>This method is for {@link RelOptRuleOperand#matchAnyChildren}=true,      * which is generally used when a node can have a variable number of      * children, and hence where the matched children are not retrievable by any      * other means.      *      * @param rel Relational expression      *      * @return Children of relational expression      */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getChildRels
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|nodeChildren
operator|.
name|get
argument_list|(
name|rel
argument_list|)
return|;
block|}
comment|/**      * Returns the planner.      *      * @return planner      */
specifier|public
name|RelOptPlanner
name|getPlanner
parameter_list|()
block|{
return|return
name|planner
return|;
block|}
comment|/**      * @return list of parents of the first relational expression      */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getParents
parameter_list|()
block|{
return|return
name|parents
return|;
block|}
comment|/**      * Called by the rule whenever it finds a match. The implementation of this      * method will guarantee that the original relational expression (e.g.,      *<code>this.rels[0]</code>) has its traits propagated to the new      * relational expression (<code>rel</code>) and its unregistered children.      * Any trait not specifically set in the RelTraitSet returned by<code>      * rel.getTraits()</code> will be copied from<code>      * this.rels[0].getTraitSet()</code>.      */
specifier|public
specifier|abstract
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End RelOptRuleCall.java
end_comment

end_unit

