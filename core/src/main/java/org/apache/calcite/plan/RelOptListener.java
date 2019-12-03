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
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventObject
import|;
end_import

begin_comment
comment|/**  * RelOptListener defines an interface for listening to events which occur  * during the optimization process.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptListener
extends|extends
name|EventListener
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Notifies this listener that a relational expression has been registered    * with a particular equivalence class after an equivalence has been either    * detected or asserted. Equivalence classes may be either logical (all    * expressions which yield the same result set) or physical (all expressions    * which yield the same result set with a particular calling convention).    *    * @param event details about the event    */
name|void
name|relEquivalenceFound
parameter_list|(
name|RelEquivalenceEvent
name|event
parameter_list|)
function_decl|;
comment|/**    * Notifies this listener that an optimizer rule is being applied to a    * particular relational expression. This rule is called twice; once before    * the rule is invoked, and once after. Note that the rel attribute of the    * event is always the old expression.    *    * @param event details about the event    */
name|void
name|ruleAttempted
parameter_list|(
name|RuleAttemptedEvent
name|event
parameter_list|)
function_decl|;
comment|/**    * Notifies this listener that an optimizer rule has been successfully    * applied to a particular relational expression, resulting in a new    * equivalent expression (relEquivalenceFound will also be called unless the    * new expression is identical to an existing one). This rule is called    * twice; once before registration of the new rel, and once after. Note that    * the rel attribute of the event is always the new expression; to get the    * old expression, use event.getRuleCall().rels[0].    *    * @param event details about the event    */
name|void
name|ruleProductionSucceeded
parameter_list|(
name|RuleProductionEvent
name|event
parameter_list|)
function_decl|;
comment|/**    * Notifies this listener that a relational expression is no longer of    * interest to the planner.    *    * @param event details about the event    */
name|void
name|relDiscarded
parameter_list|(
name|RelDiscardedEvent
name|event
parameter_list|)
function_decl|;
comment|/**    * Notifies this listener that a relational expression has been chosen as    * part of the final implementation of the query plan. After the plan is    * complete, this is called one more time with null for the rel.    *    * @param event details about the event    */
name|void
name|relChosen
parameter_list|(
name|RelChosenEvent
name|event
parameter_list|)
function_decl|;
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Event class for abstract event dealing with a relational expression. The    * source of an event is typically the RelOptPlanner which initiated it.    */
specifier|abstract
class|class
name|RelEvent
extends|extends
name|EventObject
block|{
specifier|private
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|protected
name|RelEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|)
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
block|}
specifier|public
name|RelNode
name|getRel
parameter_list|()
block|{
return|return
name|rel
return|;
block|}
block|}
comment|/** Event indicating that a relational expression has been chosen. */
class|class
name|RelChosenEvent
extends|extends
name|RelEvent
block|{
specifier|public
name|RelChosenEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Event indicating that a relational expression has been found to    * be equivalent to an equivalence class. */
class|class
name|RelEquivalenceEvent
extends|extends
name|RelEvent
block|{
specifier|private
specifier|final
name|Object
name|equivalenceClass
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isPhysical
decl_stmt|;
specifier|public
name|RelEquivalenceEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|Object
name|equivalenceClass
parameter_list|,
name|boolean
name|isPhysical
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|this
operator|.
name|equivalenceClass
operator|=
name|equivalenceClass
expr_stmt|;
name|this
operator|.
name|isPhysical
operator|=
name|isPhysical
expr_stmt|;
block|}
specifier|public
name|Object
name|getEquivalenceClass
parameter_list|()
block|{
return|return
name|equivalenceClass
return|;
block|}
specifier|public
name|boolean
name|isPhysical
parameter_list|()
block|{
return|return
name|isPhysical
return|;
block|}
block|}
comment|/** Event indicating that a relational expression has been discarded. */
class|class
name|RelDiscardedEvent
extends|extends
name|RelEvent
block|{
specifier|public
name|RelDiscardedEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Event indicating that a planner rule has fired. */
specifier|abstract
class|class
name|RuleEvent
extends|extends
name|RelEvent
block|{
specifier|private
specifier|final
name|RelOptRuleCall
name|ruleCall
decl_stmt|;
specifier|protected
name|RuleEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelOptRuleCall
name|ruleCall
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|this
operator|.
name|ruleCall
operator|=
name|ruleCall
expr_stmt|;
block|}
specifier|public
name|RelOptRuleCall
name|getRuleCall
parameter_list|()
block|{
return|return
name|ruleCall
return|;
block|}
block|}
comment|/** Event indicating that a planner rule has been attempted. */
class|class
name|RuleAttemptedEvent
extends|extends
name|RuleEvent
block|{
specifier|private
specifier|final
name|boolean
name|before
decl_stmt|;
specifier|public
name|RuleAttemptedEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelOptRuleCall
name|ruleCall
parameter_list|,
name|boolean
name|before
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|,
name|ruleCall
argument_list|)
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBefore
parameter_list|()
block|{
return|return
name|before
return|;
block|}
block|}
comment|/** Event indicating that a planner rule has produced a result. */
class|class
name|RuleProductionEvent
extends|extends
name|RuleAttemptedEvent
block|{
specifier|public
name|RuleProductionEvent
parameter_list|(
name|Object
name|eventSource
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelOptRuleCall
name|ruleCall
parameter_list|,
name|boolean
name|before
parameter_list|)
block|{
name|super
argument_list|(
name|eventSource
argument_list|,
name|rel
argument_list|,
name|ruleCall
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

