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
name|plan
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
name|hint
operator|.
name|Hintable
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|BiFunction
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
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|/**    * Generator for {@link #id} values.    */
specifier|private
specifier|static
name|int
name|nextId
init|=
literal|0
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
specifier|protected
specifier|final
name|RelOptRuleOperand
name|operand0
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|RelNode
argument_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
argument_list|>
name|nodeInputs
decl_stmt|;
specifier|public
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
comment|/**    * Creates a RelOptRuleCall.    *    * @param planner      Planner    * @param operand      Root operand    * @param rels         Array of relational expressions which matched each    *                     operand    * @param nodeInputs   For each node which matched with    *                     {@code matchAnyChildren}=true, a list of the node's    *                     inputs    * @param parents      list of parent RelNodes corresponding to the first    *                     relational expression in the array argument, if known;    *                     otherwise, null    */
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
name|nodeInputs
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
name|id
operator|=
name|nextId
operator|++
expr_stmt|;
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
name|nodeInputs
operator|=
name|nodeInputs
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
name|nodeInputs
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
name|nodeInputs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the root operand matched by this rule.    *    * @return root operand    */
specifier|public
name|RelOptRuleOperand
name|getOperand0
parameter_list|()
block|{
return|return
name|operand0
return|;
block|}
comment|/**    * Returns the invoked planner rule.    *    * @return planner rule    */
specifier|public
name|RelOptRule
name|getRule
parameter_list|()
block|{
return|return
name|rule
return|;
block|}
comment|/**    * Returns a list of matched relational expressions.    *    * @return matched relational expressions    * @deprecated Use {@link #getRelList()} or {@link #rel(int)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
comment|/**    * Returns a list of matched relational expressions.    *    * @return matched relational expressions    * @see #rel(int)    */
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
comment|/**    * Retrieves the {@code ordinal}th matched relational expression. This    * corresponds to the {@code ordinal}th operand of the rule.    *    * @param ordinal Ordinal    * @param<T>     Type    * @return Relational expression    */
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
comment|/**    * Returns the children of a given relational expression node matched in a    * rule.    *    *<p>If the policy of the operand which caused the match is not    * {@link org.apache.calcite.plan.RelOptRuleOperandChildPolicy#ANY},    * the children will have their    * own operands and therefore be easily available in the array returned by    * the {@link #getRelList()} method, so this method returns null.    *    *<p>This method is for    * {@link org.apache.calcite.plan.RelOptRuleOperandChildPolicy#ANY},    * which is generally used when a node can have a variable number of    * children, and hence where the matched children are not retrievable by any    * other means.    *    *<p>Warning: it produces wrong result for {@code unordered(...)} case.    *    * @param rel Relational expression    * @return Children of relational expression    */
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
name|nodeInputs
operator|.
name|get
argument_list|(
name|rel
argument_list|)
return|;
block|}
comment|/** Assigns the input relational expressions of a given relational expression,    * as seen by this particular call. Is only called when the operand is    * {@link RelOptRule#any()}. */
specifier|protected
name|void
name|setChildRels
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
if|if
condition|(
name|nodeInputs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nodeInputs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|nodeInputs
operator|.
name|put
argument_list|(
name|rel
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the planner.    *    * @return planner    */
specifier|public
name|RelOptPlanner
name|getPlanner
parameter_list|()
block|{
return|return
name|planner
return|;
block|}
comment|/**    * Returns the current RelMetadataQuery or its sub-class,    * to be used for instance by    * {@link RelOptRule#onMatch(RelOptRuleCall)}.    */
specifier|public
parameter_list|<
name|M
extends|extends
name|RelMetadataQuery
parameter_list|>
name|M
name|getMetadataQuery
parameter_list|()
block|{
return|return
name|rel
argument_list|(
literal|0
argument_list|)
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataQuery
argument_list|()
return|;
block|}
comment|/**    * @return list of parents of the first relational expression    */
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
comment|/**    * Registers that a rule has produced an equivalent relational expression.    *    *<p>Called by the rule whenever it finds a match. The implementation of    * this method guarantees that the original relational expression (that is,    *<code>this.rels[0]</code>) has its traits propagated to the new    * relational expression (<code>rel</code>) and its unregistered children.    * Any trait not specifically set in the RelTraitSet returned by<code>    * rel.getTraits()</code> will be copied from<code>    * this.rels[0].getTraitSet()</code>.    *    *<p>The implementation of this method also guarantees that the original    * relational expression (that is,<code>this.rels[0]</code>) has its hints propagated to    * the new relational expression (<code>rel</code>). The hints propagation strategy can be    * customized through specified {@code handler}.    *    * @param rel     Relational expression equivalent to the root relational    *                expression of the rule call, {@code call.rels(0)}    * @param equiv   Map of other equivalences    * @param handler Handler to customize the relational expression that would    *                be registered into the planner, the 1th argument is the    *                original expression and the 2th argument is the new relational    *                expression    */
specifier|public
specifier|abstract
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|equiv
parameter_list|,
name|BiFunction
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|handler
parameter_list|)
function_decl|;
comment|/**    * Registers that a rule has produced an equivalent relational expression,    * with specified equivalences.    *    *<p>The hints are copied fully from the original expression    * (that is,<code>this.rels[0]</code>) to the new relational expression    * (<code>rel</code>) if both of them are all instances of    * {@link Hintable}.    *    * @param rel   Relational expression equivalent to the root relational    *              expression of the rule call, {@code call.rels(0)}    * @param equiv Map of other equivalences    */
specifier|public
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Map
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|equiv
parameter_list|)
block|{
name|transformTo
argument_list|(
name|rel
argument_list|,
name|equiv
argument_list|,
name|RelOptUtil
operator|::
name|copyRelHints
argument_list|)
expr_stmt|;
block|}
comment|/**    * Registers that a rule has produced an equivalent relational expression,    * but no other equivalences.    *    *<p>The hints are copied fully from the original expression    * (that is,<code>this.rels[0]</code>) to the new relational expression    * (<code>rel</code>) if both of them are all instances of    * {@link Hintable}.    *    * @param rel Relational expression equivalent to the root relational    *            expression of the rule call, {@code call.rels(0)}    */
specifier|public
specifier|final
name|void
name|transformTo
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|transformTo
argument_list|(
name|rel
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link org.apache.calcite.tools.RelBuilder} to be used by    * code within the call. The {@link RelOptRule#relBuilderFactory} argument contains policies    * such as what implementation of {@link Filter} to create. */
specifier|public
name|RelBuilder
name|builder
parameter_list|()
block|{
return|return
name|rule
operator|.
name|relBuilderFactory
operator|.
name|create
argument_list|(
name|rel
argument_list|(
literal|0
argument_list|)
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptRuleCall.java
end_comment

end_unit

