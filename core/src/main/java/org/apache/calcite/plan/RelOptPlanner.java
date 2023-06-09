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
name|metadata
operator|.
name|CachingRelMetadataProvider
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
name|RelMetadataProvider
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
name|rex
operator|.
name|RexExecutor
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
name|CancelFlag
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
name|List
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
name|Pattern
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
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Sets the root node of this query.    *    * @param rel Relational expression    */
name|void
name|setRoot
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Returns the root node of this query.    *    * @return Root node    */
annotation|@
name|Nullable
name|RelNode
name|getRoot
parameter_list|()
function_decl|;
comment|/**    * Registers a rel trait definition. If the {@link RelTraitDef} has already    * been registered, does nothing.    *    * @return whether the RelTraitDef was added, as per    * {@link java.util.Collection#add}    */
name|boolean
name|addRelTraitDef
parameter_list|(
name|RelTraitDef
name|relTraitDef
parameter_list|)
function_decl|;
comment|/**    * Clear all the registered RelTraitDef.    */
name|void
name|clearRelTraitDefs
parameter_list|()
function_decl|;
comment|/**    * Returns the list of active trait types.    */
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|getRelTraitDefs
parameter_list|()
function_decl|;
comment|/**    * Removes all internal state, including all registered rules,    * materialized views, and lattices.    */
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Returns the list of all registered rules.    */
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|getRules
parameter_list|()
function_decl|;
comment|/**    * Registers a rule.    *    *<p>If the rule has already been registered, does nothing.    * This method determines if the given rule is a    * {@link org.apache.calcite.rel.convert.ConverterRule} and pass the    * ConverterRule to all    * {@link #addRelTraitDef(RelTraitDef) registered} RelTraitDef    * instances.    *    * @return whether the rule was added, as per    * {@link java.util.Collection#add}    */
name|boolean
name|addRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
comment|/**    * Removes a rule.    *    * @return true if the rule was present, as per    * {@link java.util.Collection#remove(Object)}    */
name|boolean
name|removeRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
function_decl|;
comment|/**    * Provides the Context created when this planner was constructed.    *    * @return Never null; either an externally defined context, or a dummy    * context that returns null for each requested interface    */
name|Context
name|getContext
parameter_list|()
function_decl|;
comment|/**    * Sets the exclusion filter to use for this planner. Rules which match the    * given pattern will not be fired regardless of whether or when they are    * added to the planner.    *    * @param exclusionFilter pattern to match for exclusion; null to disable    *                        filtering    */
name|void
name|setRuleDescExclusionFilter
parameter_list|(
annotation|@
name|Nullable
name|Pattern
name|exclusionFilter
parameter_list|)
function_decl|;
comment|/**    * Does nothing.    *    * @deprecated Previously, this method installed the cancellation-checking    * flag for this planner, but is now deprecated. Now, you should add a    * {@link CancelFlag} to the {@link Context} passed to the constructor.    *    * @param cancelFlag flag which the planner should periodically check    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|void
name|setCancelFlag
parameter_list|(
name|CancelFlag
name|cancelFlag
parameter_list|)
function_decl|;
comment|/**    * Changes a relational expression to an equivalent one with a different set    * of traits.    *    * @param rel Relational expression (may or may not have been registered; must    *   not have the desired traits)    * @param toTraits Trait set to convert the relational expression to    * @return Relational expression with desired traits. Never null, but may be    *   abstract    */
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
comment|/**    * Negotiates an appropriate planner to deal with distributed queries. The    * idea is that the schemas decide among themselves which has the most    * knowledge. Right now, the local planner retains control.    */
name|RelOptPlanner
name|chooseDelegate
parameter_list|()
function_decl|;
comment|/**    * Defines a pair of relational expressions that are equivalent.    *    *<p>Typically {@code tableRel} is a    * {@link org.apache.calcite.rel.logical.LogicalTableScan} representing a    * table that is a materialized view and {@code queryRel} is the SQL    * expression that populates that view. The intention is that    * {@code tableRel} is cheaper to evaluate and therefore if the query being    * optimized uses (or can be rewritten to use) {@code queryRel} as a    * sub-expression then it can be optimized by using {@code tableRel}    * instead.</p>    */
name|void
name|addMaterialization
parameter_list|(
name|RelOptMaterialization
name|materialization
parameter_list|)
function_decl|;
comment|/**    * Returns the materializations that have been registered with the planner.    */
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|getMaterializations
parameter_list|()
function_decl|;
comment|/**    * Defines a lattice.    *    *<p>The lattice may have materializations; it is not necessary to call    * {@link #addMaterialization} for these; they are registered implicitly.    */
name|void
name|addLattice
parameter_list|(
name|RelOptLattice
name|lattice
parameter_list|)
function_decl|;
comment|/**    * Retrieves a lattice, given its star table.    */
annotation|@
name|Nullable
name|RelOptLattice
name|getLattice
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
function_decl|;
comment|/**    * Finds the most efficient expression to implement this query.    *    * @throws CannotPlanException if cannot find a plan    */
name|RelNode
name|findBestExp
parameter_list|()
function_decl|;
comment|/**    * Returns the factory that creates    * {@link org.apache.calcite.plan.RelOptCost}s.    */
name|RelOptCostFactory
name|getCostFactory
parameter_list|()
function_decl|;
comment|/**    * Computes the cost of a RelNode. In most cases, this just dispatches to    * {@link RelMetadataQuery#getCumulativeCost}.    *    * @param rel Relational expression of interest    * @param mq Metadata query    * @return estimated cost    */
annotation|@
name|Nullable
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
function_decl|;
comment|// CHECKSTYLE: IGNORE 2
comment|/** @deprecated Use {@link #getCost(RelNode, RelMetadataQuery)}    * or, better, call {@link RelMetadataQuery#getCumulativeCost(RelNode)}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
annotation|@
name|Nullable
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Registers a relational expression in the expression bank.    *    *<p>After it has been registered, you may not modify it.    *    *<p>The expression must not already have been registered. If you are not    * sure whether it has been registered, call    * {@link #ensureRegistered(RelNode, RelNode)}.    *    * @param rel      Relational expression to register (must not already be    *                 registered)    * @param equivRel Relational expression it is equivalent to (may be null)    * @return the same expression, or an equivalent existing expression    */
name|RelNode
name|register
parameter_list|(
name|RelNode
name|rel
parameter_list|,
annotation|@
name|Nullable
name|RelNode
name|equivRel
parameter_list|)
function_decl|;
comment|/**    * Registers a relational expression if it is not already registered.    *    *<p>If {@code equivRel} is specified, {@code rel} is placed in the same    * equivalence set. It is OK if {@code equivRel} has different traits;    * {@code rel} will end up in a different subset of the same set.    *    *<p>It is OK if {@code rel} is a subset.    *    * @param rel      Relational expression to register    * @param equivRel Relational expression it is equivalent to (may be null)    * @return Registered relational expression    */
name|RelNode
name|ensureRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|,
annotation|@
name|Nullable
name|RelNode
name|equivRel
parameter_list|)
function_decl|;
comment|/**    * Determines whether a relational expression has been registered.    *    * @param rel expression to test    * @return whether rel has been registered    */
name|boolean
name|isRegistered
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Tells this planner that a schema exists. This is the schema's chance to    * tell the planner about all of the special transformation rules.    */
name|void
name|registerSchema
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|)
function_decl|;
comment|/**    * Adds a listener to this planner.    *    * @param newListener new listener to be notified of events    */
name|void
name|addListener
parameter_list|(
name|RelOptListener
name|newListener
parameter_list|)
function_decl|;
comment|/**    * Gives this planner a chance to register one or more    * {@link RelMetadataProvider}s in the chain which will be used to answer    * metadata queries.    *    *<p>Planners which use their own relational expressions internally    * to represent concepts such as equivalence classes will generally need to    * supply corresponding metadata providers.</p>    *    * @param list receives planner's custom providers, if any    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|void
name|registerMetadataProviders
parameter_list|(
name|List
argument_list|<
name|RelMetadataProvider
argument_list|>
name|list
parameter_list|)
function_decl|;
comment|/**    * Gets a timestamp for a given rel's metadata. This timestamp is used by    * {@link CachingRelMetadataProvider} to decide whether cached metadata has    * gone stale.    *    * @param rel rel of interest    * @return timestamp of last change which might affect metadata derivation    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|long
name|getRelMetadataTimestamp
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Prunes a node from the planner.    *    *<p>When a node is pruned, the related pending rule    * calls are cancelled, and future rules will not fire.    * This can be used to reduce the search space.</p>    * @param rel the node to prune.    */
name|void
name|prune
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/**    * Registers a class of RelNode. If this class of RelNode has been seen    * before, does nothing.    *    * @param node Relational expression    */
name|void
name|registerClass
parameter_list|(
name|RelNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Creates an empty trait set. It contains all registered traits, and the    * default values of any traits that have them.    *    *<p>The empty trait set acts as the prototype (a kind of factory) for all    * subsequently created trait sets.</p>    *    * @return Empty trait set    */
name|RelTraitSet
name|emptyTraitSet
parameter_list|()
function_decl|;
comment|/** Sets the object that can execute scalar expressions. */
name|void
name|setExecutor
parameter_list|(
annotation|@
name|Nullable
name|RexExecutor
name|executor
parameter_list|)
function_decl|;
comment|/** Returns the executor used to evaluate constant expressions. */
annotation|@
name|Nullable
name|RexExecutor
name|getExecutor
parameter_list|()
function_decl|;
comment|/** Called when a relational expression is copied to a similar expression. */
name|void
name|onCopy
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|newRel
parameter_list|)
function_decl|;
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link RexExecutor} */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
interface|interface
name|Executor
extends|extends
name|RexExecutor
block|{   }
comment|/**    * Thrown by {@link org.apache.calcite.plan.RelOptPlanner#findBestExp()}.    */
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

end_unit

