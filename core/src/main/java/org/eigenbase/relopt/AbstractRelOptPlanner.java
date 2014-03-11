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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
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
name|CancelFlag
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
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Executor
name|executor
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AbstractRelOptPlanner.    */
specifier|protected
name|AbstractRelOptPlanner
parameter_list|(
name|RelOptCostFactory
name|costFactory
parameter_list|)
block|{
name|this
operator|.
name|costFactory
operator|=
name|costFactory
expr_stmt|;
name|mapDescToRule
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RelOptRule
argument_list|>
argument_list|()
expr_stmt|;
comment|// In case no one calls setCancelFlag, set up a
comment|// dummy here.
name|cancelFlag
operator|=
operator|new
name|CancelFlag
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelOptCostFactory
name|getCostFactory
parameter_list|()
block|{
return|return
name|costFactory
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|void
name|setCancelFlag
parameter_list|(
name|CancelFlag
name|cancelFlag
parameter_list|)
block|{
name|this
operator|.
name|cancelFlag
operator|=
name|cancelFlag
expr_stmt|;
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
name|isCancelRequested
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
comment|// implement RelOptPlanner
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
comment|// implement RelOptPlanner
specifier|public
name|RelOptPlanner
name|chooseDelegate
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|// implement RelOptPlanner
specifier|public
name|void
name|registerSchema
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|)
block|{
block|}
comment|// implement RelOptPlanner
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
name|node
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
comment|// implement RelOptPlanner
specifier|public
name|RelOptCost
name|getCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getCumulativeCost
argument_list|(
name|rel
argument_list|)
return|;
block|}
comment|// implement RelOptPlanner
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
comment|// implement RelOptPlanner
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
comment|// implement RelOptPlanner
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
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|getRelTraitDefs
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|void
name|setExecutor
parameter_list|(
name|Executor
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
name|Executor
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
comment|/**    * Fires a rule, taking care of tracing and listener notification.    *    * @param ruleCall description of rule call    * @pre ruleCall.getRule().matches(ruleCall)    */
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
if|if
condition|(
name|LOGGER
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|fine
argument_list|(
literal|"call#"
operator|+
name|ruleCall
operator|.
name|id
operator|+
literal|": Rule ["
operator|+
name|ruleCall
operator|.
name|getRule
argument_list|()
operator|+
literal|"] not fired"
operator|+
literal|" due to exclusion filter"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|fine
argument_list|(
literal|"call#"
operator|+
name|ruleCall
operator|.
name|id
operator|+
literal|": Apply rule ["
operator|+
name|ruleCall
operator|.
name|getRule
argument_list|()
operator|+
literal|"] to "
operator|+
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
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|fine
argument_list|(
literal|"call#"
operator|+
name|ruleCall
operator|.
name|id
operator|+
literal|": Rule "
operator|+
name|ruleCall
operator|.
name|getRule
argument_list|()
operator|+
literal|" arguments "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|ruleCall
operator|.
name|rels
argument_list|)
operator|+
literal|" produced "
operator|+
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
if|if
condition|(
name|LOGGER
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|fine
argument_list|(
literal|"For final plan, using "
operator|+
name|rel
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
block|}
end_class

begin_comment
comment|// End AbstractRelOptPlanner.java
end_comment

end_unit

