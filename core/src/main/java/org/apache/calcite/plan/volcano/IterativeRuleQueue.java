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
name|rel
operator|.
name|rules
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|IterativeRuleQueue
extends|extends
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
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * The list of rule-matches. Initially, there is an empty {@link MatchList}.    * As the planner invokes {@link #addMatch(VolcanoRuleMatch)} the rule-match    * is added to the appropriate MatchList(s). As the planner completes the    * match, the matching entry is removed from this list to avoid unused work.    */
specifier|final
name|MatchList
name|matchList
init|=
operator|new
name|MatchList
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|IterativeRuleQueue
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|super
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Clear internal data structure for this rule queue.    */
annotation|@
name|Override
specifier|public
name|boolean
name|clear
parameter_list|()
block|{
name|boolean
name|empty
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|!
name|matchList
operator|.
name|queue
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|matchList
operator|.
name|preQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|empty
operator|=
literal|false
expr_stmt|;
block|}
name|matchList
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
operator|!
name|empty
return|;
block|}
comment|/**    * Add a rule match.    */
annotation|@
name|Override
specifier|public
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
return|return;
block|}
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"Rule-match queued: {}"
argument_list|,
name|matchName
argument_list|)
expr_stmt|;
name|matchList
operator|.
name|offer
argument_list|(
name|match
argument_list|)
expr_stmt|;
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
comment|/**    * Removes the rule match from the head of match list, and returns it.    *    *<p>Returns {@code null} if there are no more matches.</p>    *    *<p>Note that the VolcanoPlanner may still decide to reject rule matches    * which have become invalid, say if one of their operands belongs to an    * obsolete set or has been pruned.    *    */
specifier|public
name|VolcanoRuleMatch
name|popMatch
parameter_list|()
block|{
name|dumpPlannerState
argument_list|()
expr_stmt|;
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
name|matchList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|dumpRuleQueue
argument_list|(
name|matchList
argument_list|)
expr_stmt|;
name|match
operator|=
name|matchList
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
name|matchList
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
comment|/**    * Dumps rules queue to the logger when debug level is set to {@code TRACE}.    */
specifier|private
name|void
name|dumpRuleQueue
parameter_list|(
name|MatchList
name|matchList
parameter_list|)
block|{
if|if
condition|(
name|LOGGER
operator|.
name|isTraceEnabled
argument_list|()
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
literal|"Rule queue:"
argument_list|)
expr_stmt|;
for|for
control|(
name|VolcanoRuleMatch
name|rule
range|:
name|matchList
operator|.
name|preQueue
control|)
block|{
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
name|rule
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|VolcanoRuleMatch
name|rule
range|:
name|matchList
operator|.
name|queue
control|)
block|{
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
name|rule
argument_list|)
expr_stmt|;
block|}
name|LOGGER
operator|.
name|trace
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Dumps planner's state to the logger when debug level is set to {@code TRACE}.    */
specifier|private
name|void
name|dumpPlannerState
parameter_list|()
block|{
if|if
condition|(
name|LOGGER
operator|.
name|isTraceEnabled
argument_list|()
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
name|LOGGER
operator|.
name|trace
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|planner
operator|.
name|getRoot
argument_list|()
operator|.
name|getCluster
argument_list|()
operator|.
name|invalidateMetadataQuery
argument_list|()
expr_stmt|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * MatchList represents a set of {@link VolcanoRuleMatch rule-matches}.    */
specifier|private
specifier|static
class|class
name|MatchList
block|{
comment|/**      * Rule match queue for SubstitutionRule.      */
specifier|private
specifier|final
name|Queue
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|preQueue
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Current list of VolcanoRuleMatches for this phase. New rule-matches      * are appended to the end of this queue.      * The rules are not sorted in any way.      */
specifier|private
specifier|final
name|Queue
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|queue
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * A set of rule-match names contained in {@link #queue}. Allows fast      * detection of duplicate rule-matches.      */
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
name|int
name|size
parameter_list|()
block|{
return|return
name|preQueue
operator|.
name|size
argument_list|()
operator|+
name|queue
operator|.
name|size
argument_list|()
return|;
block|}
name|VolcanoRuleMatch
name|poll
parameter_list|()
block|{
name|VolcanoRuleMatch
name|match
init|=
name|preQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
name|match
operator|=
name|queue
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
name|void
name|offer
parameter_list|(
name|VolcanoRuleMatch
name|match
parameter_list|)
block|{
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
name|preQueue
operator|.
name|offer
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|offer
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|clear
parameter_list|()
block|{
name|preQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|queue
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

