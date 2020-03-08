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
operator|.
name|volcano
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
name|plan
operator|.
name|RelOptRuleOperand
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
name|plan
operator|.
name|SubstitutionRule
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
name|util
operator|.
name|Util
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
name|HashMultimap
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
name|ImmutableSet
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
name|Multimap
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
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
name|LOGGER
init|=
name|CalciteTrace
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
name|ALL_RULES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"<ALL RULES>"
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Map of {@link VolcanoPlannerPhase} to a list of rule-matches. Initially,    * there is an empty {@link PhaseMatchList} for each planner phase. As the    * planner invokes {@link #addMatch(VolcanoRuleMatch)} the rule-match is    * added to the appropriate PhaseMatchList(s). As the planner completes    * phases, the matching entry is removed from this list to avoid unused    * work.    */
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
name|EnumMap
argument_list|<>
argument_list|(
name|VolcanoPlannerPhase
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|VolcanoPlanner
name|planner
decl_stmt|;
comment|/**    * Maps a {@link VolcanoPlannerPhase} to a set of rule descriptions. Named rules    * may be invoked in their corresponding phase.    *    *<p>See {@link VolcanoPlannerPhaseRuleMappingInitializer} for more    * information regarding the contents of this Map and how it is initialized.    */
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
name|EnumMap
argument_list|<>
argument_list|(
name|VolcanoPlannerPhase
operator|.
name|class
argument_list|)
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
argument_list|<>
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
name|ALL_RULES
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
comment|/**    * Clear internal data structure for this rule queue.    */
specifier|public
name|void
name|clear
parameter_list|()
block|{
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
name|matchList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Removes the {@link PhaseMatchList rule-match list} for the given planner    * phase.    */
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
name|get
argument_list|(
name|phase
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a rule match. The rule-matches are automatically added to all    * existing {@link PhaseMatchList per-phase rule-match lists} which allow    * the rule referenced by the match.    */
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
name|ALL_RULES
condition|)
block|{
name|String
name|ruleDescription
init|=
name|match
operator|.
name|getRule
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|phaseRuleSet
operator|.
name|contains
argument_list|(
name|ruleDescription
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
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
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"{} Rule-match queued: {}"
argument_list|,
name|matchList
operator|.
name|phase
operator|.
name|toString
argument_list|()
argument_list|,
name|matchName
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|.
name|getRule
argument_list|()
operator|instanceof
name|SubstitutionRule
condition|)
block|{
name|matchList
operator|.
name|list
operator|.
name|offerFirst
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matchList
operator|.
name|list
operator|.
name|offer
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
name|matchList
operator|.
name|matchMap
operator|.
name|put
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
comment|/**    * Removes the rule match from the head of match list, and returns it.    *    *<p>Returns {@code null} if there are no more matches.</p>    *    *<p>Note that the VolcanoPlanner may still decide to reject rule matches    * which have become invalid, say if one of their operands belongs to an    * obsolete set or has importance=0.    *    * @throws java.lang.AssertionError if this method is called with a phase    *                              previously marked as completed via    *                              {@link #phaseCompleted(VolcanoPlannerPhase)}.    */
name|VolcanoRuleMatch
name|popMatch
parameter_list|(
name|VolcanoPlannerPhase
name|phase
parameter_list|)
block|{
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
if|if
condition|(
name|phaseMatchList
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Used match list for phase "
operator|+
name|phase
operator|+
literal|" after phase complete"
argument_list|)
throw|;
block|}
name|VolcanoRuleMatch
name|match
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|phaseMatchList
operator|.
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|match
operator|=
name|phaseMatchList
operator|.
name|list
operator|.
name|poll
argument_list|()
expr_stmt|;
if|if
condition|(
name|skipMatch
argument_list|(
name|match
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Skip match: {}"
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
comment|// If sets have merged since the rule match was enqueued, the match
comment|// may not be removed from the matchMap because the subset may have
comment|// changed, it is OK to leave it since the matchMap will be cleared
comment|// at the end.
name|phaseMatchList
operator|.
name|matchMap
operator|.
name|remove
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
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Pop match: {}"
argument_list|,
name|match
argument_list|)
expr_stmt|;
return|return
name|match
return|;
block|}
comment|/** Returns whether to skip a match. This happens if any of the    * {@link RelNode}s have importance zero. */
specifier|private
name|boolean
name|skipMatch
parameter_list|(
name|VolcanoRuleMatch
name|match
parameter_list|)
block|{
for|for
control|(
name|RelNode
name|rel
range|:
name|match
operator|.
name|rels
control|)
block|{
name|Double
name|importance
init|=
name|planner
operator|.
name|relImportances
operator|.
name|get
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|importance
operator|!=
literal|null
operator|&&
name|importance
operator|==
literal|0d
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// If the same subset appears more than once along any path from root
comment|// operand to a leaf operand, we have matched a cycle. A relational
comment|// expression that consumes its own output can never be implemented, and
comment|// furthermore, if we fire rules on it we may generate lots of garbage.
comment|// For example, if
comment|//   Project(A, X = X + 0)
comment|// is in the same subset as A, then we would generate
comment|//   Project(A, X = X + 0 + 0)
comment|//   Project(A, X = X + 0 + 0 + 0)
comment|// also in the same subset. They are valid but useless.
specifier|final
name|Deque
argument_list|<
name|RelSubset
argument_list|>
name|subsets
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|checkDuplicateSubsets
argument_list|(
name|subsets
argument_list|,
name|match
operator|.
name|rule
operator|.
name|getOperand
argument_list|()
argument_list|,
name|match
operator|.
name|rels
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Recursively checks whether there are any duplicate subsets along any path    * from root of the operand tree to one of the leaves.    *    *<p>It is OK for a match to have duplicate subsets if they are not on the    * same path. For example,    *    *<blockquote><pre>    *   Join    *  /   \    * X     X    *</pre></blockquote>    *    *<p>is a valid match.    *    * @throws org.apache.calcite.util.Util.FoundOne on match    */
specifier|private
name|void
name|checkDuplicateSubsets
parameter_list|(
name|Deque
argument_list|<
name|RelSubset
argument_list|>
name|subsets
parameter_list|,
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelNode
index|[]
name|rels
parameter_list|)
block|{
specifier|final
name|RelSubset
name|subset
init|=
name|planner
operator|.
name|getSubset
argument_list|(
name|rels
index|[
name|operand
operator|.
name|ordinalInRule
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|subsets
operator|.
name|contains
argument_list|(
name|subset
argument_list|)
condition|)
block|{
throw|throw
name|Util
operator|.
name|FoundOne
operator|.
name|NULL
throw|;
block|}
if|if
condition|(
operator|!
name|operand
operator|.
name|getChildOperands
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|subsets
operator|.
name|push
argument_list|(
name|subset
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRuleOperand
name|childOperand
range|:
name|operand
operator|.
name|getChildOperands
argument_list|()
control|)
block|{
name|checkDuplicateSubsets
argument_list|(
name|subsets
argument_list|,
name|childOperand
argument_list|,
name|rels
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelSubset
name|x
init|=
name|subsets
operator|.
name|pop
argument_list|()
decl_stmt|;
assert|assert
name|x
operator|==
name|subset
assert|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * PhaseMatchList represents a set of {@link VolcanoRuleMatch rule-matches}    * for a particular    * {@link VolcanoPlannerPhase phase of the planner's execution}.    */
specifier|private
specifier|static
class|class
name|PhaseMatchList
block|{
comment|/**      * The VolcanoPlannerPhase that this PhaseMatchList is used in.      */
specifier|final
name|VolcanoPlannerPhase
name|phase
decl_stmt|;
comment|/**      * Current list of VolcanoRuleMatches for this phase. New rule-matches      * are appended to the end of this list.      * The rules are not sorted in any way.      */
specifier|final
name|Deque
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * A set of rule-match names contained in {@link #list}. Allows fast      * detection of duplicate rule-matches.      */
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Multi-map of RelSubset to VolcanoRuleMatches.      */
specifier|final
name|Multimap
argument_list|<
name|RelSubset
argument_list|,
name|VolcanoRuleMatch
argument_list|>
name|matchMap
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
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
block|}
name|void
name|clear
parameter_list|()
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
name|matchMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

