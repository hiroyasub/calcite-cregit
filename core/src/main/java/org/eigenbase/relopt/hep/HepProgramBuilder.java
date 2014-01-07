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
name|rel
operator|.
name|convert
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
comment|/**  * HepProgramBuilder creates instances of {@link HepProgram}.  */
end_comment

begin_class
specifier|public
class|class
name|HepProgramBuilder
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|HepInstruction
argument_list|>
name|instructions
init|=
operator|new
name|ArrayList
argument_list|<
name|HepInstruction
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HepInstruction
operator|.
name|BeginGroup
name|group
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new HepProgramBuilder with an initially empty program. The    * program under construction has an initial match order of {@link    * HepMatchOrder#ARBITRARY}, and an initial match limit of {@link    * HepProgram#MATCH_UNTIL_FIXPOINT}.    */
specifier|public
name|HepProgramBuilder
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
name|void
name|clear
parameter_list|()
block|{
name|instructions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|group
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Adds an instruction to attempt to match any rules of a given class. The    * order in which the rules within a class will be attempted is arbitrary,    * so if more control is needed, use addRuleInstance instead.    *    *<p>Note that when this method is used, it is also necessary to add the    * actual rule objects of interest to the planner via {@link    * RelOptPlanner#addRule}. If the planner does not have any rules of the    * given class, this instruction is a nop.    *    *<p>TODO: support classification via rule annotations.    *    * @param ruleClass class of rules to fire, e.g. ConverterRule.class    */
specifier|public
parameter_list|<
name|R
extends|extends
name|RelOptRule
parameter_list|>
name|HepProgramBuilder
name|addRuleClass
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|ruleClass
parameter_list|)
block|{
name|HepInstruction
operator|.
name|RuleClass
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|RuleClass
argument_list|<
name|R
argument_list|>
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|ruleClass
operator|=
name|ruleClass
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to attempt to match any rules in a given collection.    * The order in which the rules within a collection will be attempted is    * arbitrary, so if more control is needed, use addRuleInstance instead. The    * collection can be "live" in the sense that not all rule instances need to    * have been added to it at the time this method is called. The collection    * contents are reevaluated for each execution of the program.    *    *<p>Note that when this method is used, it is NOT necessary to add the    * rules to the planner via {@link RelOptPlanner#addRule}; the instances    * supplied here will be used. However, adding the rules to the planner    * redundantly is good form since other planners may require it.    *    * @param rules collection of rules to fire    */
specifier|public
name|HepProgramBuilder
name|addRuleCollection
parameter_list|(
name|Collection
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
name|HepInstruction
operator|.
name|RuleCollection
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|RuleCollection
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|rules
operator|=
name|rules
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to attempt to match a specific rule object.    *    *<p>Note that when this method is used, it is NOT necessary to add the    * rule to the planner via {@link RelOptPlanner#addRule}; the instance    * supplied here will be used. However, adding the rule to the planner    * redundantly is good form since other planners may require it.    *    * @param rule rule to fire    */
specifier|public
name|HepProgramBuilder
name|addRuleInstance
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
name|HepInstruction
operator|.
name|RuleInstance
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|RuleInstance
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to attempt to match a specific rule identified by its    * unique description.    *    *<p>Note that when this method is used, it is necessary to also add the    * rule object of interest to the planner via {@link RelOptPlanner#addRule}.    * This allows for some decoupling between optimizers and plugins: the    * optimizer only knows about rule descriptions, while the plugins supply    * the actual instances. If the planner does not have a rule matching the    * description, this instruction is a nop.    *    * @param ruleDescription description of rule to fire    */
specifier|public
name|HepProgramBuilder
name|addRuleByDescription
parameter_list|(
name|String
name|ruleDescription
parameter_list|)
block|{
name|HepInstruction
operator|.
name|RuleInstance
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|RuleInstance
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|ruleDescription
operator|=
name|ruleDescription
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to begin a group of rules. All subsequent rules added    * (until the next endRuleGroup) will be collected into the group rather    * than firing individually. After addGroupBegin has been called, only    * addRuleXXX methods may be called until the next addGroupEnd.    */
specifier|public
name|HepProgramBuilder
name|addGroupBegin
parameter_list|()
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|BeginGroup
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|BeginGroup
argument_list|()
decl_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
name|group
operator|=
name|instruction
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to end a group of rules, firing the group    * collectively. The order in which the rules within a group will be    * attempted is arbitrary. Match order and limit applies to the group as a    * whole.    */
specifier|public
name|HepProgramBuilder
name|addGroupEnd
parameter_list|()
block|{
assert|assert
operator|(
name|group
operator|!=
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|EndGroup
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|EndGroup
argument_list|()
decl_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
name|group
operator|.
name|endGroup
operator|=
name|instruction
expr_stmt|;
name|group
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to attempt to match instances of {@link    * ConverterRule}, but only where a conversion is actually required.    *    * @param guaranteed if true, use only guaranteed converters; if false, use    *                   only non-guaranteed converters    */
specifier|public
name|HepProgramBuilder
name|addConverters
parameter_list|(
name|boolean
name|guaranteed
parameter_list|)
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|ConverterRules
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|ConverterRules
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|guaranteed
operator|=
name|guaranteed
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to attempt to match instances of    * {@link CommonRelSubExprRule}, but only in cases where vertices have more    * than one parent.    */
specifier|public
name|HepProgramBuilder
name|addCommonRelSubExprInstruction
parameter_list|()
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|CommonRelSubExprRules
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|CommonRelSubExprRules
argument_list|()
decl_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to change the order of pattern matching for    * subsequent instructions. The new order will take effect for the rest of    * the program (not counting subprograms) or until another match order    * instruction is encountered.    *    * @param order new match direction to set    */
specifier|public
name|HepProgramBuilder
name|addMatchOrder
parameter_list|(
name|HepMatchOrder
name|order
parameter_list|)
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|MatchOrder
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|MatchOrder
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to limit the number of pattern matches for subsequent    * instructions. The limit will take effect for the rest of the program (not    * counting subprograms) or until another limit instruction is encountered.    *    * @param limit limit to set; use {@link HepProgram#MATCH_UNTIL_FIXPOINT} to    *              remove limit    */
specifier|public
name|HepProgramBuilder
name|addMatchLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|MatchLimit
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|MatchLimit
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds an instruction to execute a subprogram. Note that this is different    * from adding the instructions from the subprogram individually. When added    * as a subprogram, the sequence will execute repeatedly until a fixpoint is    * reached, whereas when the instructions are added individually, the    * sequence will only execute once (with a separate fixpoint for each    * instruction).    *    *<p>The subprogram has its own state for match order and limit    * (initialized to the defaults every time the subprogram is executed) and    * any changes it makes to those settings do not affect the parent program.    *    * @param program subprogram to execute    */
specifier|public
name|HepProgramBuilder
name|addSubprogram
parameter_list|(
name|HepProgram
name|program
parameter_list|)
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepInstruction
operator|.
name|Subprogram
name|instruction
init|=
operator|new
name|HepInstruction
operator|.
name|Subprogram
argument_list|()
decl_stmt|;
name|instruction
operator|.
name|subprogram
operator|=
name|program
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
name|instruction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the constructed program, clearing the state of this program    * builder as a side-effect.    *    * @return immutable program    */
specifier|public
name|HepProgram
name|build
parameter_list|()
block|{
assert|assert
operator|(
name|group
operator|==
literal|null
operator|)
assert|;
name|HepProgram
name|program
init|=
operator|new
name|HepProgram
argument_list|(
name|instructions
argument_list|)
decl_stmt|;
name|clear
argument_list|()
expr_stmt|;
return|return
name|program
return|;
block|}
block|}
end_class

begin_comment
comment|// End HepProgramBuilder.java
end_comment

end_unit

