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
operator|.
name|volcano
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Predicate1
import|;
end_import

begin_comment
comment|/**  * A<code>RelSubset</code> is set of expressions in a set which have the same  * calling convention. An expression may be in more than one sub-set of a set;  * the same expression is used.  *  * @author jhyde  * @version $Id$  * @since 16 December, 2001  */
end_comment

begin_class
specifier|public
class|class
name|RelSubset
extends|extends
name|AbstractRelNode
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
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
comment|/**      * cost of best known plan (it may have improved since)      */
name|RelOptCost
name|bestCost
decl_stmt|;
comment|/**      * The set this subset belongs to.      */
specifier|final
name|RelSet
name|set
decl_stmt|;
comment|/**      * best known plan      */
name|RelNode
name|best
decl_stmt|;
comment|/**      * Timestamp for metadata validity      */
name|long
name|timestamp
decl_stmt|;
comment|/**      * Flag indicating whether this RelSubset's importance was artificially      * boosted.      */
name|boolean
name|boosted
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|RelSubset
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelSet
name|set
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
name|this
operator|.
name|bestCost
operator|=
name|computeBestCost
argument_list|(
name|cluster
operator|.
name|getPlanner
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|boosted
operator|=
literal|false
expr_stmt|;
name|recomputeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
name|RelOptCost
name|computeBestCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|RelOptCost
name|bestCost
init|=
name|VolcanoCost
operator|.
name|INFINITY
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|getRels
argument_list|()
control|)
block|{
specifier|final
name|RelOptCost
name|cost
init|=
name|planner
operator|.
name|getCost
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|cost
operator|.
name|isLt
argument_list|(
name|bestCost
argument_list|)
condition|)
block|{
name|bestCost
operator|=
name|cost
expr_stmt|;
block|}
block|}
return|return
name|bestCost
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesSet
parameter_list|()
block|{
return|return
name|set
operator|.
name|variablesPropagated
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesUsed
parameter_list|()
block|{
return|return
name|set
operator|.
name|variablesUsed
return|;
block|}
specifier|public
name|RelNode
name|getBest
parameter_list|()
block|{
return|return
name|best
return|;
block|}
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|planner
operator|.
name|makeZeroCost
argument_list|()
return|;
block|}
specifier|public
name|double
name|getRows
parameter_list|()
block|{
if|if
condition|(
name|best
operator|==
literal|null
condition|)
block|{
return|return
name|VolcanoCost
operator|.
name|INFINITY
operator|.
name|getRows
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|best
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
comment|// Not a typical implementation of "explain". We don't gather terms&
comment|// values to be printed later. We actually do the work.
name|String
name|s
init|=
name|getDescription
argument_list|()
decl_stmt|;
name|pw
operator|.
name|item
argument_list|(
literal|"subset"
argument_list|,
name|s
argument_list|)
expr_stmt|;
specifier|final
name|AbstractRelNode
name|input
init|=
operator|(
name|AbstractRelNode
operator|)
name|getRels
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|input
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|done
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|computeDigest
parameter_list|()
block|{
name|StringBuilder
name|digest
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Subset#"
argument_list|)
decl_stmt|;
name|digest
operator|.
name|append
argument_list|(
name|set
operator|.
name|id
argument_list|)
expr_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|traitSet
control|)
block|{
name|digest
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|trait
argument_list|)
expr_stmt|;
block|}
return|return
name|digest
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|set
operator|.
name|rel
operator|.
name|getRowType
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
for|for
control|(
name|RelNode
name|rel
range|:
name|set
operator|.
name|rels
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|isDistinct
argument_list|()
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
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
for|for
control|(
name|RelNode
name|rel
range|:
name|set
operator|.
name|rels
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|isKey
argument_list|(
name|columns
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
comment|/** Returns the collection of RelNodes one of whose inputs is in this      * subset. */
name|Set
argument_list|<
name|RelNode
argument_list|>
name|getParents
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|RelNode
argument_list|>
name|list
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|parent
range|:
name|set
operator|.
name|getParentRels
argument_list|()
control|)
block|{
for|for
control|(
name|RelSubset
name|rel
range|:
name|inputSubsets
argument_list|(
name|parent
argument_list|)
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|set
operator|==
name|set
operator|&&
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|traitSet
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
comment|/** Returns the collection of distinct subsets that contain a RelNode one      * of whose inputs is in this subset. */
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|getParentSubsets
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|list
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|RelSubset
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|parent
range|:
name|set
operator|.
name|getParentRels
argument_list|()
control|)
block|{
for|for
control|(
name|RelSubset
name|rel
range|:
name|inputSubsets
argument_list|(
name|parent
argument_list|)
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|set
operator|==
name|set
operator|&&
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|traitSet
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|planner
operator|.
name|getSubset
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|RelSubset
argument_list|>
name|inputSubsets
parameter_list|(
name|RelNode
name|parent
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|RelSubset
argument_list|>
operator|)
operator|(
name|List
operator|)
name|parent
operator|.
name|getInputs
argument_list|()
return|;
block|}
comment|/** Returns a list of relational expressions one of whose children is this      * subset. The elements of the list are distinct. */
specifier|public
name|Collection
argument_list|<
name|RelNode
argument_list|>
name|getParentRels
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|RelNode
argument_list|>
name|list
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
name|parentLoop
label|:
for|for
control|(
name|RelNode
name|parent
range|:
name|set
operator|.
name|getParentRels
argument_list|()
control|)
block|{
for|for
control|(
name|RelSubset
name|rel
range|:
name|inputSubsets
argument_list|(
name|parent
argument_list|)
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|set
operator|==
name|set
operator|&&
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|traitSet
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
continue|continue
name|parentLoop
continue|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
name|RelSet
name|getSet
parameter_list|()
block|{
return|return
name|set
return|;
block|}
comment|/**      * Adds expression<code>rel</code> to this subset.      */
name|void
name|add
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
name|set
operator|.
name|rels
operator|.
name|contains
argument_list|(
name|rel
argument_list|)
condition|)
block|{
return|return;
block|}
name|VolcanoPlanner
name|planner
init|=
operator|(
name|VolcanoPlanner
operator|)
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
if|if
condition|(
name|planner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RelEquivalenceEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RelEquivalenceEvent
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|planner
operator|.
name|listener
operator|.
name|relEquivalenceFound
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|// If this isn't the first rel in the set, it must have compatible
comment|// row type.
assert|assert
operator|(
name|set
operator|.
name|rel
operator|==
literal|null
operator|)
operator|||
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"rowtype of new rel"
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|"rowtype of set"
argument_list|,
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
assert|;
name|set
operator|.
name|addInternal
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|variablesSet
init|=
name|RelOptUtil
operator|.
name|getVariablesSet
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
init|=
name|rel
operator|.
name|getVariablesStopped
argument_list|()
decl_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|variablesPropagated
init|=
name|Util
operator|.
name|minus
argument_list|(
name|variablesSet
argument_list|,
name|variablesStopped
argument_list|)
decl_stmt|;
assert|assert
name|set
operator|.
name|variablesPropagated
operator|.
name|containsAll
argument_list|(
name|variablesPropagated
argument_list|)
assert|;
name|Set
argument_list|<
name|String
argument_list|>
name|variablesUsed
init|=
name|RelOptUtil
operator|.
name|getVariablesUsed
argument_list|(
name|rel
argument_list|)
decl_stmt|;
assert|assert
name|set
operator|.
name|variablesUsed
operator|.
name|containsAll
argument_list|(
name|variablesUsed
argument_list|)
assert|;
block|}
block|}
comment|/**      * Recursively builds a tree consisting of the cheapest plan at each node.      */
name|RelNode
name|buildCheapestPlan
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|CheapestPlanReplacer
name|replacer
init|=
operator|new
name|CheapestPlanReplacer
argument_list|(
name|planner
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|cheapest
init|=
name|replacer
operator|.
name|visit
argument_list|(
name|this
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|planner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RelChosenEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RelChosenEvent
argument_list|(
name|planner
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|planner
operator|.
name|listener
operator|.
name|relChosen
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
return|return
name|cheapest
return|;
block|}
comment|/**      * Checks whether a relexp has made its subset cheaper, and if it so,      * recursively checks whether that subset's parents have gotten cheaper.      *      * @param planner Planner      * @param rel Relational expression whose cost has improved      * @param activeSet Set of active subsets, for cycle detection      */
name|void
name|propagateCostImprovements
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|activeSet
parameter_list|)
block|{
for|for
control|(
name|RelSubset
name|subset
range|:
name|set
operator|.
name|subsets
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|subsumes
argument_list|(
name|subset
operator|.
name|traitSet
argument_list|)
condition|)
block|{
name|subset
operator|.
name|propagateCostImprovements0
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|activeSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|propagateCostImprovements0
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|activeSet
parameter_list|)
block|{
operator|++
name|timestamp
expr_stmt|;
if|if
condition|(
operator|!
name|activeSet
operator|.
name|add
argument_list|(
name|this
argument_list|)
condition|)
block|{
comment|// This subset is already in the chain being propagated to. This
comment|// means that the graph is cyclic, and therefore the cost of this
comment|// relational expression - not this subset - must be infinite.
name|tracer
operator|.
name|finer
argument_list|(
literal|"cyclic: "
operator|+
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
specifier|final
name|RelOptCost
name|cost
init|=
name|planner
operator|.
name|getCost
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|cost
operator|.
name|isLt
argument_list|(
name|bestCost
argument_list|)
condition|)
block|{
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINER
argument_list|)
condition|)
block|{
name|tracer
operator|.
name|finer
argument_list|(
literal|"Subset cost improved: subset ["
operator|+
name|this
operator|+
literal|"] cost was "
operator|+
name|bestCost
operator|+
literal|" now "
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
name|bestCost
operator|=
name|cost
expr_stmt|;
name|best
operator|=
name|rel
expr_stmt|;
comment|// Lower cost means lower importance. Other nodes will change
comment|// too, but we'll get to them later.
name|planner
operator|.
name|ruleQueue
operator|.
name|recompute
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|parent
range|:
name|getParents
argument_list|()
control|)
block|{
specifier|final
name|RelSubset
name|parentSubset
init|=
name|planner
operator|.
name|getSubset
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|parentSubset
operator|.
name|propagateCostImprovements
argument_list|(
name|planner
argument_list|,
name|parent
argument_list|,
name|activeSet
argument_list|)
expr_stmt|;
block|}
name|planner
operator|.
name|checkForSatisfiedConverters
argument_list|(
name|set
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|activeSet
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|propagateBoostRemoval
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|ruleQueue
operator|.
name|recompute
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|boosted
condition|)
block|{
name|boosted
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|RelSubset
name|parentSubset
range|:
name|getParentSubsets
argument_list|(
name|planner
argument_list|)
control|)
block|{
name|parentSubset
operator|.
name|propagateBoostRemoval
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|collectVariablesUsed
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variableSet
parameter_list|)
block|{
name|variableSet
operator|.
name|addAll
argument_list|(
name|getVariablesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|collectVariablesSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|variableSet
parameter_list|)
block|{
name|variableSet
operator|.
name|addAll
argument_list|(
name|getVariablesSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the rel nodes in this rel subset.  All rels must have the same      * traits and are logically equivalent.      * @return all the rels in the subset      */
specifier|public
name|Iterable
argument_list|<
name|RelNode
argument_list|>
name|getRels
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|RelNode
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|set
operator|.
name|rels
argument_list|)
operator|.
name|where
argument_list|(
operator|new
name|Predicate1
argument_list|<
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|RelNode
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|.
name|getTraitSet
argument_list|()
operator|.
name|subsumes
argument_list|(
name|traitSet
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** As {@link #getRels()} but returns a list. */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getRelList
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|set
operator|.
name|rels
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|subsumes
argument_list|(
name|traitSet
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Visitor which walks over a tree of {@link RelSet}s, replacing each node      * with the cheapest implementation of the expression.      */
class|class
name|CheapestPlanReplacer
block|{
name|VolcanoPlanner
name|planner
decl_stmt|;
name|CheapestPlanReplacer
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
block|}
specifier|public
name|RelNode
name|visit
parameter_list|(
name|RelNode
name|p
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|RelSubset
condition|)
block|{
name|RelSubset
name|subset
init|=
operator|(
name|RelSubset
operator|)
name|p
decl_stmt|;
name|RelNode
name|cheapest
init|=
name|subset
operator|.
name|best
decl_stmt|;
if|if
condition|(
name|cheapest
operator|==
literal|null
condition|)
block|{
comment|// Dump the planner's expression pool so we can figure
comment|// out why we reached impasse.
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"Node ["
operator|+
name|subset
operator|.
name|getDescription
argument_list|()
operator|+
literal|"] could not be implemented; planner state:\n"
argument_list|)
expr_stmt|;
name|planner
operator|.
name|dump
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|dump
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|RuntimeException
name|e
init|=
operator|new
name|RelOptPlanner
operator|.
name|CannotPlanException
argument_list|(
name|dump
argument_list|)
decl_stmt|;
name|tracer
operator|.
name|throwing
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"visit"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|p
operator|=
name|cheapest
expr_stmt|;
block|}
if|if
condition|(
name|ordinal
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|planner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RelChosenEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RelChosenEvent
argument_list|(
name|planner
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|planner
operator|.
name|listener
operator|.
name|relChosen
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|RelNode
argument_list|>
name|oldInputs
init|=
name|p
operator|.
name|getInputs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
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
name|oldInputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelNode
name|oldInput
init|=
name|oldInputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelNode
name|input
init|=
name|visit
argument_list|(
name|oldInput
argument_list|,
name|i
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|inputs
operator|.
name|add
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|inputs
operator|.
name|equals
argument_list|(
name|oldInputs
argument_list|)
condition|)
block|{
specifier|final
name|RelNode
name|pOld
init|=
name|p
decl_stmt|;
name|p
operator|=
name|p
operator|.
name|copy
argument_list|(
name|p
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
name|planner
operator|.
name|provenanceMap
operator|.
name|put
argument_list|(
name|p
argument_list|,
operator|new
name|VolcanoPlanner
operator|.
name|DirectProvenance
argument_list|(
name|pOld
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelSubset.java
end_comment

end_unit

