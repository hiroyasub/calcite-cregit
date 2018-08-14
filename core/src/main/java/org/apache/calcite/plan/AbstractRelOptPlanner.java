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
name|plan
operator|.
name|volcano
operator|.
name|RelSubset
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
name|Util
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Abstract base for implementations of the {@link RelOptPlanner} interface.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRelOptPlanner
implements|implements
name|RelOptPlanner
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** Regular expression for integer. */
specifier|private
specifier|static
specifier|final
name|Pattern
name|INTEGER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9]+"
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Maps rule description to rule, just to ensure that rules' descriptions    * are unique.    */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelOptRule
argument_list|>
name|mapDescToRule
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|RelOptCostFactory
name|costFactory
decl_stmt|;
specifier|private
name|MulticastRelOptListener
name|listener
decl_stmt|;
specifier|private
name|Pattern
name|ruleDescExclusionFilter
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|cancelFlag
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|>
name|classes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|RelTrait
argument_list|>
name|traits
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** External context. Never null. */
specifier|protected
specifier|final
name|Context
name|context
decl_stmt|;
specifier|private
name|RexExecutor
name|executor
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AbstractRelOptPlanner.    */
specifier|protected
name|AbstractRelOptPlanner
parameter_list|(
name|RelOptCostFactory
name|costFactory
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
assert|assert
name|costFactory
operator|!=
literal|null
assert|;
name|this
operator|.
name|costFactory
operator|=
name|costFactory
expr_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|context
operator|=
name|Contexts
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
specifier|final
name|CancelFlag
name|cancelFlag
init|=
name|context
operator|.
name|unwrap
argument_list|(
name|CancelFlag
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|cancelFlag
operator|=
name|cancelFlag
operator|!=
literal|null
condition|?
name|cancelFlag
operator|.
name|atomicBoolean
else|:
operator|new
name|AtomicBoolean
argument_list|()
expr_stmt|;
comment|// Add abstract RelNode classes. No RelNodes will ever be registered with
comment|// these types, but some operands may use them.
name|classes
operator|.
name|add
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
expr_stmt|;
name|classes
operator|.
name|add
argument_list|(
name|RelSubset
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|clear
parameter_list|()
block|{
block|}
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|RelOptCostFactory
name|getCostFactory
parameter_list|()
block|{
return|return
name|costFactory
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|void
name|setCancelFlag
parameter_list|(
name|CancelFlag
name|cancelFlag
parameter_list|)
block|{
comment|// ignored
block|}
comment|/**    * Checks to see whether cancellation has been requested, and if so, throws    * an exception.    */
specifier|public
name|void
name|checkCancel
parameter_list|()
block|{
if|if
condition|(
name|cancelFlag
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
name|RESOURCE
operator|.
name|preparationAborted
argument_list|()
operator|.
name|ex
argument_list|()
throw|;
block|}
block|}
comment|/**    * Registers a rule's description.    *    * @param rule Rule    */
specifier|protected
name|void
name|mapRuleDescription
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
comment|// Check that there isn't a rule with the same description,
comment|// also validating description string.
specifier|final
name|String
name|description
init|=
name|rule
operator|.
name|toString
argument_list|()
decl_stmt|;
assert|assert
name|description
operator|!=
literal|null
assert|;
assert|assert
operator|!
name|description
operator|.
name|contains
argument_list|(
literal|"$"
argument_list|)
operator|:
literal|"Rule's description should not contain '$': "
operator|+
name|description
assert|;
assert|assert
operator|!
name|INTEGER_PATTERN
operator|.
name|matcher
argument_list|(
name|description
argument_list|)
operator|.
name|matches
argument_list|()
operator|:
literal|"Rule's description should not be an integer: "
operator|+
name|rule
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|description
assert|;
name|RelOptRule
name|existingRule
init|=
name|mapDescToRule
operator|.
name|put
argument_list|(
name|description
argument_list|,
name|rule
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingRule
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existingRule
operator|==
name|rule
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Rule should not already be registered"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// This rule has the same description as one previously
comment|// registered, yet it is not equal. You may need to fix the
comment|// rule's equals and hashCode methods.
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Rule's description should be unique; "
operator|+
literal|"existing rule="
operator|+
name|existingRule
operator|+
literal|"; new rule="
operator|+
name|rule
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Removes the mapping between a rule and its description.    *    * @param rule Rule    */
specifier|protected
name|void
name|unmapRuleDescription
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
name|String
name|description
init|=
name|rule
operator|.
name|toString
argument_list|()
decl_stmt|;
name|mapDescToRule
operator|.
name|remove
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the rule with a given description    *    * @param description Description    * @return Rule with given description, or null if not found    */
specifier|protected
name|RelOptRule
name|getRuleByDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
return|return
name|mapDescToRule
operator|.
name|get
argument_list|(
name|description
argument_list|)
return|;
block|}
specifier|public
name|void
name|setRuleDescExclusionFilter
parameter_list|(
name|Pattern
name|exclusionFilter
parameter_list|)
block|{
name|ruleDescExclusionFilter
operator|=
name|exclusionFilter
expr_stmt|;
block|}
comment|/**    * Determines whether a given rule is excluded by ruleDescExclusionFilter.    *    * @param rule rule to test    * @return true iff rule should be excluded    */
specifier|public
name|boolean
name|isRuleExcluded
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
return|return
name|ruleDescExclusionFilter
operator|!=
literal|null
operator|&&
name|ruleDescExclusionFilter
operator|.
name|matcher
argument_list|(
name|rule
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
specifier|public
name|RelOptPlanner
name|chooseDelegate
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|void
name|addMaterialization
parameter_list|(
name|RelOptMaterialization
name|materialization
parameter_list|)
block|{
comment|// ignore - this planner does not support materializations
block|}
specifier|public
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|getMaterializations
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|void
name|addLattice
parameter_list|(
name|RelOptLattice
name|lattice
parameter_list|)
block|{
comment|// ignore - this planner does not support lattices
block|}
specifier|public
name|RelOptLattice
name|getLattice
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
comment|// this planner does not support lattices
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|registerSchema
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|)
block|{
block|}
specifier|public
name|long
name|getRelMetadataTimestamp
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|setImportance
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|double
name|importance
parameter_list|)
block|{
block|}
specifier|public
name|void
name|registerClass
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
init|=
name|node
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|classes
operator|.
name|add
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|onNewClass
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelTrait
name|trait
range|:
name|node
operator|.
name|getTraitSet
argument_list|()
control|)
block|{
if|if
condition|(
name|traits
operator|.
name|add
argument_list|(
name|trait
argument_list|)
condition|)
block|{
name|trait
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Called when a new class of {@link RelNode} is seen. */
specifier|protected
name|void
name|onNewClass
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
name|node
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelTraitSet
name|emptyTraitSet
parameter_list|()
block|{
return|return
name|RelTraitSet
operator|.
name|createEmpty
argument_list|()
return|;
block|}
specifier|public
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getCumulativeCost
argument_list|(
name|rel
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|getCost
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|void
name|addListener
parameter_list|(
name|RelOptListener
name|newListener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|MulticastRelOptListener
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|addListener
argument_list|(
name|newListener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|registerMetadataProviders
parameter_list|(
name|List
argument_list|<
name|RelMetadataProvider
argument_list|>
name|list
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|addRelTraitDef
parameter_list|(
name|RelTraitDef
name|relTraitDef
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|clearRelTraitDefs
parameter_list|()
block|{
block|}
specifier|public
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|getRelTraitDefs
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|void
name|setExecutor
parameter_list|(
name|RexExecutor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
specifier|public
name|RexExecutor
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
specifier|public
name|void
name|onCopy
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelNode
name|newRel
parameter_list|)
block|{
comment|// do nothing
block|}
comment|/**    * Fires a rule, taking care of tracing and listener notification.    *    * @param ruleCall description of rule call    */
specifier|protected
name|void
name|fireRule
parameter_list|(
name|RelOptRuleCall
name|ruleCall
parameter_list|)
block|{
name|checkCancel
argument_list|()
expr_stmt|;
assert|assert
name|ruleCall
operator|.
name|getRule
argument_list|()
operator|.
name|matches
argument_list|(
name|ruleCall
argument_list|)
assert|;
if|if
condition|(
name|isRuleExcluded
argument_list|(
name|ruleCall
operator|.
name|getRule
argument_list|()
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"call#{}: Rule [{}] not fired due to exclusion filter"
argument_list|,
name|ruleCall
operator|.
name|id
argument_list|,
name|ruleCall
operator|.
name|getRule
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// Leave this wrapped in a conditional to prevent unnecessarily calling Arrays.toString(...)
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"call#{}: Apply rule [{}] to {}"
argument_list|,
name|ruleCall
operator|.
name|id
argument_list|,
name|ruleCall
operator|.
name|getRule
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|ruleCall
operator|.
name|rels
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
name|this
argument_list|,
name|ruleCall
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ruleCall
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|listener
operator|.
name|ruleAttempted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|ruleCall
operator|.
name|getRule
argument_list|()
operator|.
name|onMatch
argument_list|(
name|ruleCall
argument_list|)
expr_stmt|;
if|if
condition|(
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
name|this
argument_list|,
name|ruleCall
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ruleCall
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|listener
operator|.
name|ruleAttempted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Takes care of tracing and listener notification when a rule's    * transformation is applied.    *    * @param ruleCall description of rule call    * @param newRel   result of transformation    * @param before   true before registration of new rel; false after    */
specifier|protected
name|void
name|notifyTransformation
parameter_list|(
name|RelOptRuleCall
name|ruleCall
parameter_list|,
name|RelNode
name|newRel
parameter_list|,
name|boolean
name|before
parameter_list|)
block|{
if|if
condition|(
name|before
operator|&&
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
literal|"call#{}: Rule {} arguments {} produced {}"
argument_list|,
name|ruleCall
operator|.
name|id
argument_list|,
name|ruleCall
operator|.
name|getRule
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|ruleCall
operator|.
name|rels
argument_list|)
argument_list|,
name|newRel
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
name|this
argument_list|,
name|newRel
argument_list|,
name|ruleCall
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|listener
operator|.
name|ruleProductionSucceeded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Takes care of tracing and listener notification when a rel is chosen as    * part of the final plan.    *    * @param rel chosen rel    */
specifier|protected
name|void
name|notifyChosen
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"For final plan, using {}"
argument_list|,
name|rel
argument_list|)
expr_stmt|;
if|if
condition|(
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
name|this
argument_list|,
name|rel
argument_list|)
decl_stmt|;
name|listener
operator|.
name|relChosen
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Takes care of tracing and listener notification when a rel equivalence is    * detected.    *    * @param rel chosen rel    */
specifier|protected
name|void
name|notifyEquivalence
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Object
name|equivalenceClass
parameter_list|,
name|boolean
name|physical
parameter_list|)
block|{
if|if
condition|(
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
name|this
argument_list|,
name|rel
argument_list|,
name|equivalenceClass
argument_list|,
name|physical
argument_list|)
decl_stmt|;
name|listener
operator|.
name|relEquivalenceFound
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Takes care of tracing and listener notification when a rel is discarded    *    * @param rel discarded rel    */
specifier|protected
name|void
name|notifyDiscard
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|RelOptListener
operator|.
name|RelDiscardedEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RelDiscardedEvent
argument_list|(
name|this
argument_list|,
name|rel
argument_list|)
decl_stmt|;
name|listener
operator|.
name|relDiscarded
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|MulticastRelOptListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
comment|/** Returns sub-classes of relational expression. */
specifier|public
name|Iterable
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|>
name|subClasses
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|Util
operator|.
name|filter
argument_list|(
name|classes
argument_list|,
name|clazz
operator|::
name|isAssignableFrom
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractRelOptPlanner.java
end_comment

end_unit

