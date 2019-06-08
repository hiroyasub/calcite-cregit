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
name|RelOptListener
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
name|RelOptRuleCall
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
name|RelOptRuleOperandChildPolicy
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
import|;
end_import

begin_comment
comment|/**  *<code>VolcanoRuleCall</code> implements the {@link RelOptRuleCall} interface  * for VolcanoPlanner.  */
end_comment

begin_class
specifier|public
class|class
name|VolcanoRuleCall
extends|extends
name|RelOptRuleCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|VolcanoPlanner
name|volcanoPlanner
decl_stmt|;
comment|/**    * List of {@link RelNode} generated by this call. For debugging purposes.    */
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|generatedRelList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a rule call, internal, with array to hold bindings.    *    * @param planner Planner    * @param operand First operand of the rule    * @param rels    Array which will hold the matched relational expressions    * @param nodeInputs For each node which matched with {@code matchAnyChildren}    *                   = true, a list of the node's inputs    */
specifier|protected
name|VolcanoRuleCall
parameter_list|(
name|VolcanoPlanner
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
name|super
argument_list|(
name|planner
argument_list|,
name|operand
argument_list|,
name|rels
argument_list|,
name|nodeInputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|volcanoPlanner
operator|=
name|planner
expr_stmt|;
block|}
comment|/**    * Creates a rule call.    *    * @param planner Planner    * @param operand First operand of the rule    */
name|VolcanoRuleCall
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelOptRuleOperand
name|operand
parameter_list|)
block|{
name|this
argument_list|(
name|planner
argument_list|,
name|operand
argument_list|,
operator|new
name|RelNode
index|[
name|operand
operator|.
name|getRule
argument_list|()
operator|.
name|operands
operator|.
name|size
argument_list|()
index|]
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRuleCall
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
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Transform to: rel#{} via {}{}"
argument_list|,
name|rel
operator|.
name|getId
argument_list|()
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|equiv
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|" with equivalences "
operator|+
name|equiv
argument_list|)
expr_stmt|;
if|if
condition|(
name|generatedRelList
operator|!=
literal|null
condition|)
block|{
name|generatedRelList
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
comment|// It's possible that rel is a subset or is already registered.
comment|// Is there still a point in continuing? Yes, because we might
comment|// discover that two sets of expressions are actually equivalent.
if|if
condition|(
name|LOGGER
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// Cannot call RelNode.toString() yet, because rel has not
comment|// been registered. For now, let's make up something similar.
name|String
name|relDesc
init|=
literal|"rel#"
operator|+
name|rel
operator|.
name|getId
argument_list|()
operator|+
literal|":"
operator|+
name|rel
operator|.
name|getRelTypeName
argument_list|()
decl_stmt|;
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"call#{}: Rule {} arguments {} created {}"
argument_list|,
name|id
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|rels
argument_list|)
argument_list|,
name|relDesc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|volcanoPlanner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RuleProductionEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RuleProductionEvent
argument_list|(
name|volcanoPlanner
argument_list|,
name|rel
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|volcanoPlanner
operator|.
name|listener
operator|.
name|ruleProductionSucceeded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|// Registering the root relational expression implicitly registers
comment|// its descendants. Register any explicit equivalences first, so we
comment|// don't register twice and cause churn.
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|entry
range|:
name|equiv
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|volcanoPlanner
operator|.
name|ensureRegistered
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|volcanoPlanner
operator|.
name|ensureRegistered
argument_list|(
name|rel
argument_list|,
name|rels
index|[
literal|0
index|]
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|rels
index|[
literal|0
index|]
operator|.
name|getCluster
argument_list|()
operator|.
name|invalidateMetadataQuery
argument_list|()
expr_stmt|;
if|if
condition|(
name|volcanoPlanner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RuleProductionEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RuleProductionEvent
argument_list|(
name|volcanoPlanner
argument_list|,
name|rel
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|volcanoPlanner
operator|.
name|listener
operator|.
name|ruleProductionSucceeded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while applying rule "
operator|+
name|getRule
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Called when all operands have matched.    */
specifier|protected
name|void
name|onMatch
parameter_list|()
block|{
assert|assert
name|getRule
argument_list|()
operator|.
name|matches
argument_list|(
name|this
argument_list|)
assert|;
name|volcanoPlanner
operator|.
name|checkCancel
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|volcanoPlanner
operator|.
name|isRuleExcluded
argument_list|(
name|getRule
argument_list|()
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Rule [{}] not fired due to exclusion filter"
argument_list|,
name|getRule
argument_list|()
argument_list|)
expr_stmt|;
return|return;
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
name|rels
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RelNode
name|rel
init|=
name|rels
index|[
name|i
index|]
decl_stmt|;
name|RelSubset
name|subset
init|=
name|volcanoPlanner
operator|.
name|getSubset
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|subset
operator|==
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Rule [{}] not fired because operand #{} ({}) has no subset"
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|i
argument_list|,
name|rel
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|subset
operator|.
name|set
operator|.
name|equivalentSet
operator|!=
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Rule [{}] not fired because operand #{} ({}) belongs to obsolete set"
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|i
argument_list|,
name|rel
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Double
name|importance
init|=
name|volcanoPlanner
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
operator|(
name|importance
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|importance
operator|==
literal|0d
operator|)
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Rule [{}] not fired because operand #{} ({}) has importance=0"
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|i
argument_list|,
name|rel
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"call#{}: Apply rule [{}] to {}"
argument_list|,
name|id
argument_list|,
name|getRule
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|rels
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|volcanoPlanner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RuleAttemptedEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RuleAttemptedEvent
argument_list|(
name|volcanoPlanner
argument_list|,
name|rels
index|[
literal|0
index|]
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|volcanoPlanner
operator|.
name|listener
operator|.
name|ruleAttempted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|generatedRelList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|volcanoPlanner
operator|.
name|ruleCallStack
operator|.
name|push
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|getRule
argument_list|()
operator|.
name|onMatch
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|volcanoPlanner
operator|.
name|ruleCallStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|generatedRelList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"call#{} generated 0 successors."
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"call#{} generated {} successors: {}"
argument_list|,
name|id
argument_list|,
name|generatedRelList
operator|.
name|size
argument_list|()
argument_list|,
name|generatedRelList
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|generatedRelList
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|volcanoPlanner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RuleAttemptedEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RuleAttemptedEvent
argument_list|(
name|volcanoPlanner
argument_list|,
name|rels
index|[
literal|0
index|]
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|volcanoPlanner
operator|.
name|listener
operator|.
name|ruleAttempted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while applying rule "
operator|+
name|getRule
argument_list|()
operator|+
literal|", args "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|rels
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Applies this rule, with a given relational expression in the first slot.    */
name|void
name|match
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
assert|assert
name|getOperand0
argument_list|()
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
operator|:
literal|"precondition"
assert|;
specifier|final
name|int
name|solve
init|=
literal|0
decl_stmt|;
name|int
name|operandOrdinal
init|=
name|getOperand0
argument_list|()
operator|.
name|solveOrder
index|[
name|solve
index|]
decl_stmt|;
name|this
operator|.
name|rels
index|[
name|operandOrdinal
index|]
operator|=
name|rel
expr_stmt|;
name|matchRecurse
argument_list|(
name|solve
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Recursively matches operands above a given solve order.    *    * @param solve Solve order of operand (&gt; 0 and&le; the operand count)    */
specifier|private
name|void
name|matchRecurse
parameter_list|(
name|int
name|solve
parameter_list|)
block|{
assert|assert
name|solve
operator|>
literal|0
assert|;
assert|assert
name|solve
operator|<=
name|rule
operator|.
name|operands
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operands
init|=
name|getRule
argument_list|()
operator|.
name|operands
decl_stmt|;
if|if
condition|(
name|solve
operator|==
name|operands
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// We have matched all operands. Now ask the rule whether it
comment|// matches; this gives the rule chance to apply side-conditions.
comment|// If the side-conditions are satisfied, we have a match.
if|if
condition|(
name|getRule
argument_list|()
operator|.
name|matches
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|onMatch
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|operandOrdinal
init|=
name|operand0
operator|.
name|solveOrder
index|[
name|solve
index|]
decl_stmt|;
specifier|final
name|int
name|previousOperandOrdinal
init|=
name|operand0
operator|.
name|solveOrder
index|[
name|solve
operator|-
literal|1
index|]
decl_stmt|;
name|boolean
name|ascending
init|=
name|operandOrdinal
operator|<
name|previousOperandOrdinal
decl_stmt|;
specifier|final
name|RelOptRuleOperand
name|previousOperand
init|=
name|operands
operator|.
name|get
argument_list|(
name|previousOperandOrdinal
argument_list|)
decl_stmt|;
specifier|final
name|RelOptRuleOperand
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|operandOrdinal
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|previous
init|=
name|rels
index|[
name|previousOperandOrdinal
index|]
decl_stmt|;
specifier|final
name|RelOptRuleOperand
name|parentOperand
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|successors
decl_stmt|;
if|if
condition|(
name|ascending
condition|)
block|{
assert|assert
name|previousOperand
operator|.
name|getParent
argument_list|()
operator|==
name|operand
assert|;
name|parentOperand
operator|=
name|operand
expr_stmt|;
specifier|final
name|RelSubset
name|subset
init|=
name|volcanoPlanner
operator|.
name|getSubset
argument_list|(
name|previous
argument_list|)
decl_stmt|;
name|successors
operator|=
name|subset
operator|.
name|getParentRels
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parentOperand
operator|=
name|previousOperand
expr_stmt|;
specifier|final
name|int
name|parentOrdinal
init|=
name|operand
operator|.
name|getParent
argument_list|()
operator|.
name|ordinalInRule
decl_stmt|;
specifier|final
name|RelNode
name|parentRel
init|=
name|rels
index|[
name|parentOrdinal
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|parentRel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
comment|// if the child is unordered, then add all rels in all input subsets to the successors list
comment|// because unordered can match child in any ordinal
if|if
condition|(
name|parentOperand
operator|.
name|childPolicy
operator|==
name|RelOptRuleOperandChildPolicy
operator|.
name|UNORDERED
condition|)
block|{
if|if
condition|(
name|operand
operator|.
name|getMatchedClass
argument_list|()
operator|==
name|RelSubset
operator|.
name|class
condition|)
block|{
name|successors
operator|=
name|inputs
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|allRelsInAllSubsets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|RelNode
argument_list|>
name|duplicates
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
operator|!
name|duplicates
operator|.
name|add
argument_list|(
name|input
argument_list|)
condition|)
block|{
comment|// Ignore duplicate subsets
continue|continue;
block|}
name|RelSubset
name|inputSubset
init|=
operator|(
name|RelSubset
operator|)
name|input
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|inputSubset
operator|.
name|getRels
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|duplicates
operator|.
name|add
argument_list|(
name|rel
argument_list|)
condition|)
block|{
comment|// Ignore duplicate relations
continue|continue;
block|}
name|allRelsInAllSubsets
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
name|successors
operator|=
name|allRelsInAllSubsets
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|operand
operator|.
name|ordinalInParent
operator|<
name|inputs
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// child policy is not unordered
comment|// we need to find the exact input node based on child operand's ordinalInParent
specifier|final
name|RelSubset
name|subset
init|=
operator|(
name|RelSubset
operator|)
name|inputs
operator|.
name|get
argument_list|(
name|operand
operator|.
name|ordinalInParent
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|getMatchedClass
argument_list|()
operator|==
name|RelSubset
operator|.
name|class
condition|)
block|{
comment|// If the rule wants the whole subset, we just provide it
name|successors
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|subset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successors
operator|=
name|subset
operator|.
name|getRelList
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// The operand expects parentRel to have a certain number
comment|// of inputs and it does not.
name|successors
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|RelNode
name|rel
range|:
name|successors
control|)
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
continue|continue;
block|}
if|if
condition|(
name|ascending
operator|&&
name|operand
operator|.
name|childPolicy
operator|!=
name|RelOptRuleOperandChildPolicy
operator|.
name|UNORDERED
condition|)
block|{
comment|// We know that the previous operand was *a* child of its parent,
comment|// but now check that it is the *correct* child.
specifier|final
name|RelSubset
name|input
init|=
operator|(
name|RelSubset
operator|)
name|rel
operator|.
name|getInput
argument_list|(
name|previousOperand
operator|.
name|ordinalInParent
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputRels
init|=
name|input
operator|.
name|getRelList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|inputRels
operator|.
name|contains
argument_list|(
name|previous
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
comment|// Assign "childRels" if the operand is UNORDERED.
switch|switch
condition|(
name|parentOperand
operator|.
name|childPolicy
condition|)
block|{
case|case
name|UNORDERED
case|:
comment|// Note: below is ill-defined. Suppose there's a union with 3 inputs,
comment|// and the rule is written as Union.class, unordered(...)
comment|// What should be provided for the rest 2 arguments?
comment|// RelSubsets? Random relations from those subsets?
comment|// For now, Calcite code does not use getChildRels, so the bug is just waiting its day
if|if
condition|(
name|ascending
condition|)
block|{
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|rel
operator|.
name|getInputs
argument_list|()
argument_list|)
decl_stmt|;
name|inputs
operator|.
name|set
argument_list|(
name|previousOperand
operator|.
name|ordinalInParent
argument_list|,
name|previous
argument_list|)
expr_stmt|;
name|setChildRels
argument_list|(
name|rel
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|getChildRels
argument_list|(
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputs
operator|==
literal|null
condition|)
block|{
name|inputs
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|previous
operator|.
name|getInputs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inputs
operator|.
name|set
argument_list|(
name|operand
operator|.
name|ordinalInParent
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|setChildRels
argument_list|(
name|previous
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
block|}
block|}
name|rels
index|[
name|operandOrdinal
index|]
operator|=
name|rel
expr_stmt|;
name|matchRecurse
argument_list|(
name|solve
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End VolcanoRuleCall.java
end_comment

end_unit

