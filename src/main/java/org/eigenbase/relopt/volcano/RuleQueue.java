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
comment|/**  * Priority queue of relexps whose rules have not been called, and rule-matches  * which have not yet been acted upon.  */
end_comment

begin_class
class|class
name|RuleQueue
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
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allRules
init|=
name|Collections
operator|.
name|singleton
argument_list|(
literal|"<ALL RULES>"
argument_list|)
decl_stmt|;
comment|/**      * Largest value which is less than one.      */
specifier|private
specifier|static
specifier|final
name|double
name|OneMinusEpsilon
init|=
name|computeOneMinusEpsilon
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * The importance of each subset.      */
specifier|final
name|Map
argument_list|<
name|RelSubset
argument_list|,
name|Double
argument_list|>
name|subsetImportances
init|=
operator|new
name|HashMap
argument_list|<
name|RelSubset
argument_list|,
name|Double
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The set of RelSubsets whose importance is currently in an artificially      * raised state. Typically this only includes RelSubsets which have only      * logical RelNodes.      */
specifier|final
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|boostedSubsets
init|=
operator|new
name|HashSet
argument_list|<
name|RelSubset
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Map of {@link VolcanoPlannerPhase} to a list of rule-matches. Initially,      * there is an empty {@link PhaseMatchList} for each planner phase. As the      * planner invokes {@link #addMatch(VolcanoRuleMatch)} the rule-match is      * added to the appropriate PhaseMatchList(s). As the planner completes      * phases, the matching entry is removed from this list to avoid unused      * work.      */
specifier|final
name|Map
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|PhaseMatchList
argument_list|>
name|matchListMap
init|=
operator|new
name|HashMap
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|PhaseMatchList
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Sorts rule-matches into decreasing order of importance.      */
specifier|private
specifier|final
name|Comparator
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|ruleMatchImportanceComparator
init|=
operator|new
name|RuleMatchImportanceComparator
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|VolcanoPlanner
name|planner
decl_stmt|;
comment|/**      * Compares relexps according to their cached 'importance'.      */
specifier|private
specifier|final
name|Comparator
argument_list|<
name|RelSubset
argument_list|>
name|relImportanceComparator
init|=
operator|new
name|RelImportanceComparator
argument_list|()
decl_stmt|;
comment|/**      * Maps a {@link VolcanoPlannerPhase} to a set of rule names.  Named rules      * may be invoked in their corresponding phase.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|phaseRuleMapping
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|RuleQueue
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|phaseRuleMapping
operator|=
operator|new
name|HashMap
argument_list|<
name|VolcanoPlannerPhase
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
comment|// init empty sets for all phases
for|for
control|(
name|VolcanoPlannerPhase
name|phase
range|:
name|VolcanoPlannerPhase
operator|.
name|values
argument_list|()
control|)
block|{
name|phaseRuleMapping
operator|.
name|put
argument_list|(
name|phase
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// configure phases
name|planner
operator|.
name|getPhaseRuleMappingInitializer
argument_list|()
operator|.
name|initialize
argument_list|(
name|phaseRuleMapping
argument_list|)
expr_stmt|;
for|for
control|(
name|VolcanoPlannerPhase
name|phase
range|:
name|VolcanoPlannerPhase
operator|.
name|values
argument_list|()
control|)
block|{
comment|// empty phases get converted to "all rules"
if|if
condition|(
name|phaseRuleMapping
operator|.
name|get
argument_list|(
name|phase
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|phaseRuleMapping
operator|.
name|put
argument_list|(
name|phase
argument_list|,
name|allRules
argument_list|)
expr_stmt|;
block|}
comment|// create a match list data structure for each phase
name|PhaseMatchList
name|matchList
init|=
operator|new
name|PhaseMatchList
argument_list|(
name|phase
argument_list|)
decl_stmt|;
name|matchListMap
operator|.
name|put
argument_list|(
name|phase
argument_list|,
name|matchList
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Removes the {@link PhaseMatchList rule-match list} for the given planner      * phase.      */
specifier|public
name|void
name|phaseCompleted
parameter_list|(
name|VolcanoPlannerPhase
name|phase
parameter_list|)
block|{
name|matchListMap
operator|.
name|remove
argument_list|(
name|phase
argument_list|)
expr_stmt|;
block|}
comment|/**      * Computes the importance of a set (which is that of its most important      * subset).      */
specifier|public
name|double
name|getImportance
parameter_list|(
name|RelSet
name|set
parameter_list|)
block|{
name|double
name|importance
init|=
literal|0
decl_stmt|;
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
name|importance
operator|=
name|Math
operator|.
name|max
argument_list|(
name|importance
argument_list|,
name|getImportance
argument_list|(
name|subset
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|importance
return|;
block|}
comment|/**      * Returns whether there is a rule match in the queue for the given phase.      *      *<p>Note that the VolcanoPlanner may still decide to reject rule matches      * which have become invalid, say if one of their operands belongs to an      * obsolete set or has importance=0.      *      * @exception NullPointerException if this method is called with a phase      * previously marked as completed via {@link      * #phaseCompleted(VolcanoPlannerPhase)}.      */
specifier|public
name|boolean
name|hasNextMatch
parameter_list|(
name|VolcanoPlannerPhase
name|phase
parameter_list|)
block|{
return|return
operator|!
name|matchListMap
operator|.
name|get
argument_list|(
name|phase
argument_list|)
operator|.
name|list
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Recomputes the importance of the given RelSubset.      *      * @param subset RelSubset whose importance is to be recomputed      * @param force if true, forces an importance update even if the subset has      * not been registered      */
specifier|public
name|void
name|recompute
parameter_list|(
name|RelSubset
name|subset
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
name|Double
name|previousImportance
init|=
name|subsetImportances
operator|.
name|get
argument_list|(
name|subset
argument_list|)
decl_stmt|;
if|if
condition|(
name|previousImportance
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|force
condition|)
block|{
comment|// Subset has not been registered yet. Don't worry about it.
return|return;
block|}
name|previousImportance
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
name|double
name|importance
init|=
name|computeImportance
argument_list|(
name|subset
argument_list|)
decl_stmt|;
if|if
condition|(
name|previousImportance
operator|.
name|doubleValue
argument_list|()
operator|==
name|importance
condition|)
block|{
return|return;
block|}
name|updateImportance
argument_list|(
name|subset
argument_list|,
name|importance
argument_list|)
expr_stmt|;
block|}
comment|/**      * Equivalent to {@link #recompute(RelSubset, boolean) recompute(subset,      * false)}.      */
specifier|public
name|void
name|recompute
parameter_list|(
name|RelSubset
name|subset
parameter_list|)
block|{
name|recompute
argument_list|(
name|subset
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Artificially boosts the importance of the given {@link RelSubset}s by a      * given factor.      *      *<p>Iterates over the currently boosted RelSubsets and removes their      * importance boost, forcing a recalculation of the RelSubsets' importances      * (see {@link #recompute(RelSubset)}).      *      *<p>Once RelSubsets have been restored to their normal importance, the      * given RelSubsets have their importances boosted. A RelSubset's boosted      * importance is always less than 1.0 (and never equal to 1.0).      *      * @param subsets RelSubsets to boost importance (priority)      * @param factor the amount to boost their importances (e.g., 1.25 increases      * importance by 25%)      */
specifier|public
name|void
name|boostImportance
parameter_list|(
name|Collection
argument_list|<
name|RelSubset
argument_list|>
name|subsets
parameter_list|,
name|double
name|factor
parameter_list|)
block|{
name|tracer
operator|.
name|finer
argument_list|(
literal|"boostImportance("
operator|+
name|factor
operator|+
literal|", "
operator|+
name|subsets
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|RelSubset
argument_list|>
name|boostRemovals
init|=
operator|new
name|ArrayList
argument_list|<
name|RelSubset
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|RelSubset
argument_list|>
name|iter
init|=
name|boostedSubsets
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RelSubset
name|subset
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|subsets
operator|.
name|contains
argument_list|(
name|subset
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|boostRemovals
operator|.
name|add
argument_list|(
name|subset
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|boostRemovals
argument_list|,
operator|new
name|Comparator
argument_list|<
name|RelSubset
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|RelSubset
name|o1
parameter_list|,
name|RelSubset
name|o2
parameter_list|)
block|{
name|int
name|o1children
init|=
name|countChildren
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|int
name|o2children
init|=
name|countChildren
argument_list|(
name|o2
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|compare
argument_list|(
name|o1children
argument_list|,
name|o2children
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
comment|// for determinism
name|c
operator|=
name|compare
argument_list|(
name|o1
operator|.
name|getId
argument_list|()
argument_list|,
name|o2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
specifier|private
name|int
name|compare
parameter_list|(
name|int
name|i1
parameter_list|,
name|int
name|i2
parameter_list|)
block|{
return|return
operator|(
name|i1
operator|<
name|i2
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|i1
operator|==
name|i2
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
specifier|private
name|int
name|countChildren
parameter_list|(
name|RelSubset
name|subset
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|subset
operator|.
name|getRels
argument_list|()
control|)
block|{
name|count
operator|+=
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|RelSubset
name|subset
range|:
name|boostRemovals
control|)
block|{
name|subset
operator|.
name|propagateBoostRemoval
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelSubset
name|subset
range|:
name|subsets
control|)
block|{
name|double
name|importance
init|=
name|subsetImportances
operator|.
name|get
argument_list|(
name|subset
argument_list|)
decl_stmt|;
name|updateImportance
argument_list|(
name|subset
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|OneMinusEpsilon
argument_list|,
name|importance
operator|*
name|factor
argument_list|)
argument_list|)
expr_stmt|;
name|subset
operator|.
name|boosted
operator|=
literal|true
expr_stmt|;
name|boostedSubsets
operator|.
name|add
argument_list|(
name|subset
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|updateImportance
parameter_list|(
name|RelSubset
name|subset
parameter_list|,
name|Double
name|importance
parameter_list|)
block|{
name|subsetImportances
operator|.
name|put
argument_list|(
name|subset
argument_list|,
name|importance
argument_list|)
expr_stmt|;
for|for
control|(
name|PhaseMatchList
name|matchList
range|:
name|matchListMap
operator|.
name|values
argument_list|()
control|)
block|{
name|MultiMap
argument_list|<
name|RelSubset
argument_list|,
name|VolcanoRuleMatch
argument_list|>
name|relMatchMap
init|=
name|matchList
operator|.
name|matchMap
decl_stmt|;
if|if
condition|(
name|relMatchMap
operator|.
name|containsKey
argument_list|(
name|subset
argument_list|)
condition|)
block|{
for|for
control|(
name|VolcanoRuleMatch
name|match
range|:
name|relMatchMap
operator|.
name|getMulti
argument_list|(
name|subset
argument_list|)
control|)
block|{
name|match
operator|.
name|clearCachedImportance
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Returns the importance of an equivalence class of relational expressions.      * Subset importances are held in a lookup table, and importance changes      * gradually propagate through that table.      *      *<p>If a subset in the same set but with a different calling convention is      * deemed to be important, then this subset has at least half of its      * importance. (This rule is designed to encourage conversions to take      * place.)</p>      */
name|double
name|getImportance
parameter_list|(
name|RelSubset
name|rel
parameter_list|)
block|{
assert|assert
name|rel
operator|!=
literal|null
assert|;
name|double
name|importance
init|=
literal|0
decl_stmt|;
specifier|final
name|RelSet
name|set
init|=
name|planner
operator|.
name|getSet
argument_list|(
name|rel
argument_list|)
decl_stmt|;
assert|assert
name|set
operator|!=
literal|null
assert|;
assert|assert
name|set
operator|.
name|subsets
operator|!=
literal|null
assert|;
for|for
control|(
name|RelSubset
name|subset2
range|:
name|set
operator|.
name|subsets
control|)
block|{
specifier|final
name|Double
name|d
init|=
name|subsetImportances
operator|.
name|get
argument_list|(
name|subset2
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|double
name|subsetImportance
init|=
name|d
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|subset2
operator|!=
name|rel
condition|)
block|{
name|subsetImportance
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|subsetImportance
operator|>
name|importance
condition|)
block|{
name|importance
operator|=
name|subsetImportance
expr_stmt|;
block|}
block|}
return|return
name|importance
return|;
block|}
comment|/**      * Adds a rule match. The rule-matches are automatically added to all      * existing {@link PhaseMatchList per-phase rule-match lists} which allow      * the rule referenced by the match.      */
name|void
name|addMatch
parameter_list|(
name|VolcanoRuleMatch
name|match
parameter_list|)
block|{
specifier|final
name|String
name|matchName
init|=
name|match
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|PhaseMatchList
name|matchList
range|:
name|matchListMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|matchList
operator|.
name|names
operator|.
name|add
argument_list|(
name|matchName
argument_list|)
condition|)
block|{
comment|// Identical match has already been added.
continue|continue;
block|}
name|String
name|ruleClassName
init|=
name|match
operator|.
name|getRule
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|phaseRuleSet
init|=
name|phaseRuleMapping
operator|.
name|get
argument_list|(
name|matchList
operator|.
name|phase
argument_list|)
decl_stmt|;
if|if
condition|(
name|phaseRuleSet
operator|!=
name|allRules
condition|)
block|{
if|if
condition|(
operator|!
name|phaseRuleSet
operator|.
name|contains
argument_list|(
name|ruleClassName
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|tracer
operator|.
name|finest
argument_list|(
name|matchList
operator|.
name|phase
operator|.
name|toString
argument_list|()
operator|+
literal|" Rule-match queued: "
operator|+
name|matchName
argument_list|)
expr_stmt|;
block|}
name|matchList
operator|.
name|list
operator|.
name|add
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|matchList
operator|.
name|matchMap
operator|.
name|putMulti
argument_list|(
name|planner
operator|.
name|getSubset
argument_list|(
name|match
operator|.
name|rels
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Computes the<dfn>importance</dfn> of a node. Importance is defined as      * follows:      *      *<ul>      *<li>the root {@link RelSubset} has an importance of 1</li>      *<li>the importance of any other subset is the sum of its importance to      * its parents</li>      *<li>The importance of children is pro-rated according to the cost of the      * children. Consider a node which has a cost of 3, and children with costs      * of 2 and 5. The total cost is 10. If the node has an importance of .5,      * then the children will have importance of .1 and .25. The retains .15      * importance points, to reflect the fact that work needs to be done on the      * node's algorithm.</li>      *</ul>      *      * The formula for the importance I of node n is:      *      *<blockquote>I<sub>n</sub> = Sum<sub>parents p of n</sub>{I<sub>p</sub> .      * W<sub>n, p</sub>}</blockquote>      *      * where W<sub>n, p</sub>, the weight of n within its parent p, is      *      *<blockquote>W<sub>n, p</sub> = Cost<sub>n</sub> / (SelfCost<sub>p</sub> +      * Cost<sub>n<sub>0</sub></sub> + ... + Cost<sub>n<sub>k</sub></sub>)      *</blockquote>      */
name|double
name|computeImportance
parameter_list|(
name|RelSubset
name|subset
parameter_list|)
block|{
name|double
name|importance
decl_stmt|;
if|if
condition|(
name|subset
operator|==
name|planner
operator|.
name|root
condition|)
block|{
comment|// The root always has importance = 1
name|importance
operator|=
literal|1.0
expr_stmt|;
block|}
else|else
block|{
comment|// The importance of a subset is the max of its importance to its
comment|// parents
name|importance
operator|=
literal|0.0
expr_stmt|;
for|for
control|(
name|RelSubset
name|parent
range|:
name|subset
operator|.
name|getParentSubsets
argument_list|(
name|planner
argument_list|)
control|)
block|{
specifier|final
name|double
name|childImportance
init|=
name|computeImportanceOfChild
argument_list|(
name|subset
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|importance
operator|=
name|Math
operator|.
name|max
argument_list|(
name|importance
argument_list|,
name|childImportance
argument_list|)
expr_stmt|;
block|}
block|}
name|tracer
operator|.
name|finest
argument_list|(
literal|"Importance of ["
operator|+
name|subset
operator|+
literal|"] is "
operator|+
name|importance
argument_list|)
expr_stmt|;
return|return
name|importance
return|;
block|}
specifier|private
name|void
name|dump
parameter_list|()
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
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
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
name|tracer
operator|.
name|finer
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dump
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|planner
operator|.
name|dump
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"Importances: {"
argument_list|)
expr_stmt|;
specifier|final
name|RelSubset
index|[]
name|subsets
init|=
name|subsetImportances
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|RelSubset
index|[
name|subsetImportances
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|subsets
argument_list|,
name|relImportanceComparator
argument_list|)
expr_stmt|;
for|for
control|(
name|RelSubset
name|subset
range|:
name|subsets
control|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|subset
operator|.
name|toString
argument_list|()
operator|+
literal|"="
operator|+
name|subsetImportances
operator|.
name|get
argument_list|(
name|subset
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes the rule match with the highest importance, and returns it.      *      * @pre hasNextMatch()      */
name|VolcanoRuleMatch
name|popMatch
parameter_list|(
name|VolcanoPlannerPhase
name|phase
parameter_list|)
block|{
name|dump
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|hasNextMatch
argument_list|(
name|phase
argument_list|)
operator|)
assert|;
name|PhaseMatchList
name|phaseMatchList
init|=
name|matchListMap
operator|.
name|get
argument_list|(
name|phase
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|phaseMatchList
operator|!=
literal|null
operator|)
operator|:
literal|"Used match list for phase "
operator|+
name|phase
operator|+
literal|" after phase complete"
assert|;
name|List
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|matchList
init|=
name|phaseMatchList
operator|.
name|list
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|matchList
argument_list|,
name|ruleMatchImportanceComparator
argument_list|)
expr_stmt|;
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"Sorted rule queue:"
argument_list|)
expr_stmt|;
for|for
control|(
name|VolcanoRuleMatch
name|match
range|:
name|matchList
control|)
block|{
specifier|final
name|double
name|importance
init|=
name|match
operator|.
name|computeImportance
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" importance "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|importance
argument_list|)
expr_stmt|;
block|}
name|tracer
operator|.
name|finest
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|VolcanoRuleMatch
name|match
init|=
name|matchList
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// A rule match's digest is composed of the operand RelNodes' digests,
comment|// which may have changed if sets have merged since the rule match was
comment|// enqueued.
name|match
operator|.
name|recomputeDigest
argument_list|()
expr_stmt|;
name|phaseMatchList
operator|.
name|matchMap
operator|.
name|removeMulti
argument_list|(
name|planner
operator|.
name|getSubset
argument_list|(
name|match
operator|.
name|rels
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|match
argument_list|)
expr_stmt|;
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|tracer
operator|.
name|fine
argument_list|(
literal|"Pop match: "
operator|+
name|match
argument_list|)
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
comment|/**      * Returns the importance of a child to a parent. This is defined by the      * importance of the parent, pro-rated by the cost of the child. For      * example, if the parent has importance = 0.8 and cost 100, then a child      * with cost 50 will have importance 0.4, and a child with cost 25 will have      * importance 0.2.      */
specifier|private
name|double
name|computeImportanceOfChild
parameter_list|(
name|RelSubset
name|child
parameter_list|,
name|RelSubset
name|parent
parameter_list|)
block|{
specifier|final
name|double
name|parentImportance
init|=
name|getImportance
argument_list|(
name|parent
argument_list|)
decl_stmt|;
specifier|final
name|double
name|childCost
init|=
name|toDouble
argument_list|(
name|planner
operator|.
name|getCost
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|parentCost
init|=
name|toDouble
argument_list|(
name|planner
operator|.
name|getCost
argument_list|(
name|parent
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|alpha
init|=
operator|(
name|childCost
operator|/
name|parentCost
operator|)
decl_stmt|;
if|if
condition|(
name|alpha
operator|>=
literal|1.0
condition|)
block|{
comment|// child is always less important than parent
name|alpha
operator|=
literal|0.99
expr_stmt|;
block|}
specifier|final
name|double
name|importance
init|=
name|parentImportance
operator|*
name|alpha
decl_stmt|;
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|tracer
operator|.
name|finest
argument_list|(
literal|"Importance of ["
operator|+
name|child
operator|+
literal|"] to its parent ["
operator|+
name|parent
operator|+
literal|"] is "
operator|+
name|importance
operator|+
literal|" (parent importance="
operator|+
name|parentImportance
operator|+
literal|", child cost="
operator|+
name|childCost
operator|+
literal|", parent cost="
operator|+
name|parentCost
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|importance
return|;
block|}
comment|/**      * Converts a cost to a scalar quantity.      */
specifier|private
name|double
name|toDouble
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
if|if
condition|(
name|cost
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
return|return
literal|1e+30
return|;
block|}
else|else
block|{
return|return
name|cost
operator|.
name|getCpu
argument_list|()
operator|+
name|cost
operator|.
name|getRows
argument_list|()
operator|+
name|cost
operator|.
name|getIo
argument_list|()
return|;
block|}
block|}
specifier|static
name|int
name|compareRels
parameter_list|(
name|RelNode
index|[]
name|rels0
parameter_list|,
name|RelNode
index|[]
name|rels1
parameter_list|)
block|{
name|int
name|c
init|=
name|rels0
operator|.
name|length
operator|-
name|rels1
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
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
name|rels0
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|rels0
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
operator|-
name|rels1
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
specifier|private
specifier|static
name|double
name|computeOneMinusEpsilon
parameter_list|()
block|{
if|if
condition|(
literal|true
condition|)
block|{
return|return
literal|1.0
operator|-
name|Double
operator|.
name|MIN_VALUE
return|;
block|}
name|double
name|d
init|=
literal|.5
decl_stmt|;
while|while
condition|(
operator|(
literal|1.0
operator|-
name|d
operator|)
operator|<
literal|1.0
condition|)
block|{
name|d
operator|/=
literal|2.0
expr_stmt|;
block|}
return|return
literal|1.0
operator|-
operator|(
name|d
operator|*
literal|2.0
operator|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Compares {@link RelNode} objects according to their cached 'importance'.      */
specifier|private
class|class
name|RelImportanceComparator
implements|implements
name|Comparator
argument_list|<
name|RelSubset
argument_list|>
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|RelSubset
name|rel1
parameter_list|,
name|RelSubset
name|rel2
parameter_list|)
block|{
name|double
name|imp1
init|=
name|getImportance
argument_list|(
name|rel1
argument_list|)
decl_stmt|;
name|double
name|imp2
init|=
name|getImportance
argument_list|(
name|rel2
argument_list|)
decl_stmt|;
name|int
name|c
init|=
name|Double
operator|.
name|compare
argument_list|(
name|imp2
argument_list|,
name|imp1
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|rel1
operator|.
name|getId
argument_list|()
operator|-
name|rel2
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
comment|/**      * Compares {@link VolcanoRuleMatch} objects according to their importance.      * Matches which are more important collate earlier. Ties are adjudicated by      * comparing the {@link RelNode#getId id}s of the relational expressions      * matched.      */
specifier|private
class|class
name|RuleMatchImportanceComparator
implements|implements
name|Comparator
argument_list|<
name|VolcanoRuleMatch
argument_list|>
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|VolcanoRuleMatch
name|match1
parameter_list|,
name|VolcanoRuleMatch
name|match2
parameter_list|)
block|{
name|double
name|imp1
init|=
name|match1
operator|.
name|getImportance
argument_list|()
decl_stmt|;
name|double
name|imp2
init|=
name|match2
operator|.
name|getImportance
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|Double
operator|.
name|compare
argument_list|(
name|imp1
argument_list|,
name|imp2
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|compareRels
argument_list|(
name|match1
operator|.
name|rels
argument_list|,
name|match2
operator|.
name|rels
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
name|c
return|;
block|}
block|}
comment|/**      * PhaseMatchList represents a set of {@link VolcanoRuleMatch rule-matches}      * for a particular {@link VolcanoPlannerPhase phase of the planner's      * execution}.      */
specifier|private
specifier|static
class|class
name|PhaseMatchList
block|{
comment|/**          * The VolcanoPlannerPhase that this PhaseMatchList is used in.          */
specifier|final
name|VolcanoPlannerPhase
name|phase
decl_stmt|;
comment|/**          * Current list of VolcanoRuleMatches for this phase. New rule-matches          * are appended to the end of this list. When removing a rule-match, the          * list is sorted and the highest importance rule-match removed. It is          * important for performance that this list remain mostly sorted.          */
specifier|final
name|List
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|list
decl_stmt|;
comment|/**          * A set of rule-match names contained in {@link #list}. Allows fast          * detection of duplicate rule-matches.          */
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
comment|/**          * Multi-map of RelSubset to VolcanoRuleMatches. Used to {@link          * VolcanoRuleMatch#clearCachedImportance() clear} the rule-match's          * cached importance related RelSubset importances are modified (e.g.,          * due to invocation of {@link RuleQueue#boostImportance(Collection,          * double)}).          */
specifier|final
name|MultiMap
argument_list|<
name|RelSubset
argument_list|,
name|VolcanoRuleMatch
argument_list|>
name|matchMap
decl_stmt|;
name|PhaseMatchList
parameter_list|(
name|VolcanoPlannerPhase
name|phase
parameter_list|)
block|{
name|this
operator|.
name|phase
operator|=
name|phase
expr_stmt|;
comment|// Use a double-linked list because an array-list does not
comment|// implement remove(0) efficiently.
name|this
operator|.
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|VolcanoRuleMatch
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|names
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|matchMap
operator|=
operator|new
name|MultiMap
argument_list|<
name|RelSubset
argument_list|,
name|VolcanoRuleMatch
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RuleQueue.java
end_comment

end_unit

