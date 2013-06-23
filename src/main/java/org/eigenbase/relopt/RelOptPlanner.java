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
name|logging
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
name|regex
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
name|rel
operator|.
name|metadata
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
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>RelOptPlanner</code> is a query optimizer: it transforms a relational  * expression into a semantically equivalent relational expression, according to  * a given set of rules and a cost model.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptPlanner
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
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
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Sets the root node of this query.      *      * @param rel Relational expression      */
specifier|public
name|void
name|setRoot
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**      * Returns the root node of this query.      *      * @return Root node      */
specifier|public
name|RelNode
name|getRoot
parameter_list|()
function_decl|;
comment|/**      * Registers a rel trait definition. If the {@link RelTraitDef} has already      * been registered, does nothing.      *      * @return whether the RelTraitDef was added, as per {@link      * java.util.Collection#add}      */
specifier|public
name|boolean
name|addRelTraitDef
parameter_list|(
name|RelTraitDef
name|relTraitDef
parameter_list|)
function_decl|;
comment|/**      * Registers a rule. If the rule has already been registered, does nothing.      * This method should determine if the given rule is a {@link      * org.eigenbase.rel.convert.ConverterRule} and pass the ConverterRule to      * all {@link #addRelTraitDef(RelTraitDef) registered} RelTraitDef      * instances.      *      * @return whether the rule was added, as per {@link      * java.util.Collection#add}      */
specifier|public
name|boolean
name|addRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
comment|/**      * Removes a rule.      *      * @return true if the rule was present, as per {@link      * java.util.Collection#remove(Object)}      */
name|boolean
name|removeRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
comment|/**      * Sets the exclusion filter to use for this planner. Rules which match the      * given pattern will not be fired regardless of whether or when they are      * added to the planner.      *      * @param exclusionFilter pattern to match for exclusion; null to disable      * filtering      */
specifier|public
name|void
name|setRuleDescExclusionFilter
parameter_list|(
name|Pattern
name|exclusionFilter
parameter_list|)
function_decl|;
comment|/**      * Installs the cancellation-checking flag for this planner. The planner      * should periodically check this flag and terminate the planning process if      * it sees a cancellation request.      *      * @param cancelFlag flag which the planner should periodically check      */
specifier|public
name|void
name|setCancelFlag
parameter_list|(
name|CancelFlag
name|cancelFlag
parameter_list|)
function_decl|;
comment|/**      * Changes a relational expression to an equivalent one with a different set      * of traits.      *      *      * @param rel Relational expression, may or may not have been registered      * @param toTraits Trait set to convert relational expression to      *      * @return Relational expression with desired traits. Never null, but may be      * abstract      *      * @pre !rel.getTraits().equals(toTraits)      * @post return != null      */
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
function_decl|;
comment|/**      * Negotiates an appropriate planner to deal with distributed queries. The      * idea is that the schemas decide among themselves which has the most      * knowledge. Right now, the local planner retains control.      */
specifier|public
name|RelOptPlanner
name|chooseDelegate
parameter_list|()
function_decl|;
comment|/**      * Finds the most efficient expression to implement this query.      *      * @throws CannotPlanException if cannot find a plan      */
specifier|public
name|RelNode
name|findBestExp
parameter_list|()
function_decl|;
comment|/**      * Creates a cost object.      */
specifier|public
name|RelOptCost
name|makeCost
parameter_list|(
name|double
name|dRows
parameter_list|,
name|double
name|dCpu
parameter_list|,
name|double
name|dIo
parameter_list|)
function_decl|;
comment|/**      * Creates a cost object representing an enormous non-infinite cost.      */
specifier|public
name|RelOptCost
name|makeHugeCost
parameter_list|()
function_decl|;
comment|/**      * Creates a cost object representing infinite cost.      */
specifier|public
name|RelOptCost
name|makeInfiniteCost
parameter_list|()
function_decl|;
comment|/**      * Creates a cost object representing a small positive cost.      */
specifier|public
name|RelOptCost
name|makeTinyCost
parameter_list|()
function_decl|;
comment|/**      * Creates a cost object representing zero cost.      */
specifier|public
name|RelOptCost
name|makeZeroCost
parameter_list|()
function_decl|;
comment|/**      * Computes the cost of a RelNode. In most cases, this just dispatches to      * {@link RelMetadataQuery#getCumulativeCost}.      *      * @param rel expression of interest      *      * @return estimated cost      */
specifier|public
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**      * Registers a relational expression in the expression bank.      *      *<p>After it has been registered, you may not modify it.      *      *<p>The expression must not already have been registered. If you are not      * sure whether it has been registered, call {@link      * #ensureRegistered(RelNode,RelNode)}.      *      * @param rel Relational expression to register (must not already be      * registered)      * @param equivRel Relational expression it is equivalent to (may be null)      *      * @return the same expression, or an equivalent existing expression      *      * @pre !isRegistered(rel)      */
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
function_decl|;
comment|/**      * Registers a relational expression if it is not already registered.      *      * @param rel Relational expression to register      * @param equivRel Relational expression it is equivalent to (may be null)      *      * @return Registered relational expression      */
name|RelNode
name|ensureRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|equivRel
parameter_list|)
function_decl|;
comment|/**      * Determines whether a relational expression has been registered.      *      *      * @param rel expression to test      *      * @return whether rel has been registered      */
specifier|public
name|boolean
name|isRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**      * Tells this planner that a schema exists. This is the schema's chance to      * tell the planner about all of the special transformation rules.      */
specifier|public
name|void
name|registerSchema
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|)
function_decl|;
comment|/**      * Adds a listener to this planner.      *      * @param newListener new listener to be notified of events      */
specifier|public
name|void
name|addListener
parameter_list|(
name|RelOptListener
name|newListener
parameter_list|)
function_decl|;
comment|/**      * Gives this planner a chance to register one or more {@link      * RelMetadataProvider}s in the chain which will be used to answer metadata      * queries. Planners which use their own relational expressions internally      * to represent concepts such as equivalence classes will generally need to      * supply corresponding metadata providers.      *      * @param chain receives planner's custom providers, if any      */
specifier|public
name|void
name|registerMetadataProviders
parameter_list|(
name|ChainedRelMetadataProvider
name|chain
parameter_list|)
function_decl|;
comment|/**      * Gets a timestamp for a given rel's metadata. This timestamp is used by      * {@link CachingRelMetadataProvider} to decide whether cached metadata has      * gone stale.      *      * @param rel rel of interest      *      * @return timestamp of last change which might affect metadata derivation      */
specifier|public
name|long
name|getRelMetadataTimestamp
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**      * Sets the importance of a relational expression.      *      *<p>An important use of this method is when a {@link RelOptRule} has      * created a relational expression which is indisputably better than the      * original relational expression. The rule set the original relational      * expression's importance to zero, to reduce the search space. Pending rule      * calls are cancelled, and future rules will not fire.      *      * @param rel Relational expression      * @param importance Importance      */
name|void
name|setImportance
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|double
name|importance
parameter_list|)
function_decl|;
comment|/**      * Registers a class of RelNode. If this class of RelNode has been seen      * before, does nothing.      *      * @param node Relational expression      */
name|void
name|registerClass
parameter_list|(
name|RelNode
name|node
parameter_list|)
function_decl|;
comment|/**      * Creates an empty trait set. It contains all registered traits, and the      * default values of any traits that have them.      *      *<p>The empty trait set acts as the prototype (a kind of factory) for all      * subsequently created trait sets.</p>      *      * @return Empty trait set      */
name|RelTraitSet
name|emptyTraitSet
parameter_list|()
function_decl|;
comment|/** Thrown by {@link org.eigenbase.relopt.RelOptPlanner#findBestExp()}. */
class|class
name|CannotPlanException
extends|extends
name|RuntimeException
block|{
specifier|public
name|CannotPlanException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End RelOptPlanner.java
end_comment

end_unit

