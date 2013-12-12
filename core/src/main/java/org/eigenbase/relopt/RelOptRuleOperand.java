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
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|util
operator|.
name|*
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

begin_comment
comment|/**  * A<code>RelOptRuleOperand</code> determines whether a {@link  * org.eigenbase.relopt.RelOptRule} can be applied to a particular expression.  *  *<p>For example, the rule to pull a filter up from the left side of a join  * takes operands:<code>(Join (Filter) (Any))</code>.</p>  *  *<p>Note that<code>children</code> means different things if it is empty or  * it is<code>null</code>:<code>(Join (Filter<b>()</b>) (Any))</code> means  * that, to match the rule,<code>Filter</code> must have no operands.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelOptRuleOperand
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|RelOptRuleOperand
name|parent
decl_stmt|;
specifier|private
name|RelOptRule
name|rule
decl_stmt|;
comment|// REVIEW jvs 29-Aug-2004: some of these are Volcano-specific and should be
comment|// factored out
specifier|public
name|int
index|[]
name|solveOrder
decl_stmt|;
specifier|public
name|int
name|ordinalInParent
decl_stmt|;
specifier|public
name|int
name|ordinalInRule
decl_stmt|;
specifier|private
specifier|final
name|RelTrait
name|trait
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|children
decl_stmt|;
comment|/** Whether child operands can be matched in any order. */
specifier|public
specifier|final
name|RelOptRuleOperandChildPolicy
name|childPolicy
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an operand.      *      *<p>If<code>children</code> is null, the rule matches regardless of the      * number of children.      *      *<p>If<code>matchAnyChild</code> is true, child operands can be matched      * in any order. This is useful when matching a relational expression which      * can have a variable number of children. For example, the rule to      * eliminate empty children of a Union would have operands      *      *<blockquote>Operand(UnionRel, true, Operand(EmptyRel))</blockquote>      *      * and given the relational expressions      *      *<blockquote>UnionRel(FilterRel, EmptyRel, ProjectRel)</blockquote>      *      * would fire the rule with arguments      *      *<blockquote>{Union, Empty}</blockquote>      *      * It is up to the rule to deduce the other children, or indeed the position      * of the matched child.</p>      *      * @param clazz Class of relational expression to match (must not be null)      * @param trait Trait to match, or null to match any trait      * @param children Child operands; or null, meaning match any number of      * children      */
specifier|protected
name|RelOptRuleOperand
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|trait
parameter_list|,
name|RelOptRuleOperandChildren
name|children
parameter_list|)
block|{
assert|assert
name|clazz
operator|!=
literal|null
assert|;
switch|switch
condition|(
name|children
operator|.
name|policy
condition|)
block|{
case|case
name|ANY
case|:
break|break;
case|case
name|LEAF
case|:
assert|assert
name|children
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
break|break;
case|case
name|UNORDERED
case|:
assert|assert
name|children
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
break|break;
default|default:
assert|assert
name|children
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
block|}
name|this
operator|.
name|childPolicy
operator|=
name|children
operator|.
name|policy
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|trait
operator|=
name|trait
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
operator|.
name|operands
expr_stmt|;
for|for
control|(
name|RelOptRuleOperand
name|child
range|:
name|this
operator|.
name|children
control|)
block|{
assert|assert
name|child
operator|.
name|parent
operator|==
literal|null
operator|:
literal|"cannot re-use operands"
assert|;
name|child
operator|.
name|parent
operator|=
name|this
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the parent operand.      *      * @return parent operand      */
specifier|public
name|RelOptRuleOperand
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**      * Sets the parent operand.      *      * @param parent Parent operand      */
specifier|public
name|void
name|setParent
parameter_list|(
name|RelOptRuleOperand
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Returns the rule this operand belongs to.      *      * @return containing rule      */
specifier|public
name|RelOptRule
name|getRule
parameter_list|()
block|{
return|return
name|rule
return|;
block|}
comment|/**      * Sets the rule this operand belongs to      *      * @param rule containing rule      */
specifier|public
name|void
name|setRule
parameter_list|(
name|RelOptRule
name|rule
parameter_list|)
block|{
name|this
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|clazz
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
name|Util
operator|.
name|hash
argument_list|(
name|h
argument_list|,
name|trait
argument_list|)
expr_stmt|;
name|h
operator|=
name|Util
operator|.
name|hash
argument_list|(
name|h
argument_list|,
name|children
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|RelOptRuleOperand
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelOptRuleOperand
name|that
init|=
operator|(
name|RelOptRuleOperand
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|clazz
operator|==
name|that
operator|.
name|clazz
operator|)
operator|&&
name|Util
operator|.
name|equal
argument_list|(
name|this
operator|.
name|trait
argument_list|,
name|that
operator|.
name|trait
argument_list|)
operator|&&
name|this
operator|.
name|children
operator|.
name|equals
argument_list|(
name|that
operator|.
name|children
argument_list|)
return|;
block|}
comment|/**      * @return relational expression class matched by this operand      */
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|getMatchedClass
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
comment|/**      * Returns the child operands.      *      * @return child operands      */
specifier|public
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|getChildOperands
parameter_list|()
block|{
return|return
name|children
return|;
block|}
comment|/**      * Returns whether a relational expression matches this operand. It must be      * of the right class and trait.      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clazz
operator|.
name|isInstance
argument_list|(
name|rel
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
name|trait
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|trait
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptRuleOperand.java
end_comment

end_unit

