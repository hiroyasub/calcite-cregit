begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|hep
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
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * HepInstruction represents one instruction in a HepProgram. The actual  * instruction set is defined here via inner classes; if these grow too big,  * they should be moved out to top-level classes.  */
end_comment

begin_class
specifier|abstract
class|class
name|HepInstruction
block|{
comment|//~ Methods ----------------------------------------------------------------
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
block|}
comment|// typesafe dispatch via the visitor pattern
specifier|abstract
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
function_decl|;
comment|//~ Inner Classes ----------------------------------------------------------
specifier|static
class|class
name|RuleClass
parameter_list|<
name|R
extends|extends
name|RelOptRule
parameter_list|>
extends|extends
name|HepInstruction
block|{
name|Class
argument_list|<
name|R
argument_list|>
name|ruleClass
decl_stmt|;
comment|/**      * Actual rule set instantiated during planning by filtering all of the      * planner's rules through ruleClass.      */
name|Set
argument_list|<
name|RelOptRule
argument_list|>
name|ruleSet
decl_stmt|;
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clearCache
condition|)
block|{
return|return;
block|}
name|ruleSet
operator|=
literal|null
expr_stmt|;
block|}
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|RuleCollection
extends|extends
name|HepInstruction
block|{
comment|/**      * Collection of rules to apply.      */
name|Collection
argument_list|<
name|RelOptRule
argument_list|>
name|rules
decl_stmt|;
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|ConverterRules
extends|extends
name|HepInstruction
block|{
name|boolean
name|guaranteed
decl_stmt|;
comment|/**      * Actual rule set instantiated during planning by filtering all of the      * planner's rules, looking for the desired converters.      */
name|Set
argument_list|<
name|RelOptRule
argument_list|>
name|ruleSet
decl_stmt|;
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|CommonRelSubExprRules
extends|extends
name|HepInstruction
block|{
name|Set
argument_list|<
name|RelOptRule
argument_list|>
name|ruleSet
decl_stmt|;
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|RuleInstance
extends|extends
name|HepInstruction
block|{
comment|/**      * Description to look for, or null if rule specified explicitly.      */
name|String
name|ruleDescription
decl_stmt|;
comment|/**      * Explicitly specified rule, or rule looked up by planner from      * description.      */
name|RelOptRule
name|rule
decl_stmt|;
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clearCache
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|ruleDescription
operator|!=
literal|null
condition|)
block|{
comment|// Look up anew each run.
name|rule
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|MatchOrder
extends|extends
name|HepInstruction
block|{
name|HepMatchOrder
name|order
decl_stmt|;
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|MatchLimit
extends|extends
name|HepInstruction
block|{
name|int
name|limit
decl_stmt|;
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|Subprogram
extends|extends
name|HepInstruction
block|{
name|HepProgram
name|subprogram
decl_stmt|;
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
name|subprogram
operator|.
name|initialize
argument_list|(
name|clearCache
argument_list|)
expr_stmt|;
block|}
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|BeginGroup
extends|extends
name|HepInstruction
block|{
name|EndGroup
name|endGroup
decl_stmt|;
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
block|}
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|EndGroup
extends|extends
name|HepInstruction
block|{
comment|/**      * Actual rule set instantiated during planning by collecting grouped      * rules.      */
name|Set
argument_list|<
name|RelOptRule
argument_list|>
name|ruleSet
decl_stmt|;
name|boolean
name|collecting
decl_stmt|;
name|void
name|initialize
parameter_list|(
name|boolean
name|clearCache
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clearCache
condition|)
block|{
return|return;
block|}
name|ruleSet
operator|=
operator|new
name|HashSet
argument_list|<
name|RelOptRule
argument_list|>
argument_list|()
expr_stmt|;
name|collecting
operator|=
literal|true
expr_stmt|;
block|}
name|void
name|execute
parameter_list|(
name|HepPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|executeInstruction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End HepInstruction.java
end_comment

end_unit

