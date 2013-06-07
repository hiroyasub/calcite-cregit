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

begin_comment
comment|/**  * A<code>RelOptRuleOperand</code> determines whether a {@link  * org.eigenbase.relopt.RelOptRule} can be applied to a particular expression.  *  *<p>For example, the rule to pull a filter up from the left side of a join  * takes operands:<code>(Join (Filter) (Any))</code>.</p>  *  *<p>Note that<code>children</code> means different things if it is empty or  * it is<code>null</code>:<code>(Join (Filter<b>()</b>) (Any))</code> means  * that, to match the rule,<code>Filter</code> must have no operands.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelOptRuleOperand
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|/**      * Dummy type, containing a single value, for parameters to overloaded forms      * of the {@link org.eigenbase.relopt.RelOptRuleOperand} constructor      * signifying operands that will be matched by relational expressions with      * any number of children.      */
enum|enum
name|Dummy
block|{
comment|/** Signifies that operand can have any number of children. */
name|ANY
block|,
comment|/** Signifies that operand has no children. Therefore it matches a          * leaf node, such as a table scan or VALUES operator.          *          *<p>{@code RelOptRuleOperand(Foo.class, NONE)} is equivalent to          * {@code RelOptRuleOperand(Foo.class)} but we prefer the former because          * it is more explicit.</p> */
name|LEAF
block|,
name|SOME
block|,
comment|/** Signifies that the rule matches any one of its parents' children.          * The parent may have one or more children. */
name|UNORDERED
block|,     }
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
name|RelOptRuleOperand
index|[]
name|children
decl_stmt|;
comment|/** Whether child operands can be matched in any order. */
specifier|public
specifier|final
name|boolean
name|matchAnyChildren
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
name|Dummy
name|dummy
parameter_list|,
name|RelOptRuleOperand
index|[]
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
name|dummy
condition|)
block|{
case|case
name|ANY
case|:
name|this
operator|.
name|matchAnyChildren
operator|=
literal|false
expr_stmt|;
assert|assert
name|children
operator|==
literal|null
assert|;
break|break;
case|case
name|LEAF
case|:
name|this
operator|.
name|matchAnyChildren
operator|=
literal|false
expr_stmt|;
assert|assert
name|children
operator|.
name|length
operator|==
literal|0
assert|;
break|break;
case|case
name|UNORDERED
case|:
name|this
operator|.
name|matchAnyChildren
operator|=
literal|true
expr_stmt|;
assert|assert
name|children
operator|.
name|length
operator|==
literal|1
assert|;
break|break;
default|default:
name|this
operator|.
name|matchAnyChildren
operator|=
literal|false
expr_stmt|;
assert|assert
name|children
operator|.
name|length
operator|==
literal|1
assert|;
block|}
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
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
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
block|}
comment|/**      * Creates an operand which matches a given trait and matches child operands      * in the order they appear.      *      * @param clazz Class of relational expression to match (must not be null)      * @param trait Trait to match, or null to match any trait      * @param children Child operands; must not be null      *      * @deprecated Use {@link RelOptRule#some}      */
specifier|public
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
name|RelOptRuleOperand
modifier|...
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|Dummy
operator|.
name|SOME
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an operand that matches a given trait and any number of children.      *      * @param clazz Class of relational expression to match (must not be null)      * @param trait Trait to match, or null to match any trait      * @param dummy Dummy argument to distinguish this constructor from other      * overloaded forms; must be ANY.      *      * @deprecated Use {@link RelOptRule#any(Class, RelTrait)}      */
specifier|public
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
name|Dummy
name|dummy
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|dummy
argument_list|,
literal|null
argument_list|)
expr_stmt|;
assert|assert
name|dummy
operator|==
name|Dummy
operator|.
name|ANY
assert|;
block|}
comment|/**      * Creates an operand that matches child operands in the order they appear.      *      *<p>There must be at least one child operand. If your rule is intended      * to match a relational expression that has no children, use      * {@code RelOptRuleOperand(Class, NONE)}.      *      * @param clazz Class of relational expression to match (must not be null)      * @param children Child operands; must not be null      *      * @deprecated Use {@link RelOptRule#some}      */
specifier|public
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
name|RelOptRuleOperand
modifier|...
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|Dummy
operator|.
name|SOME
argument_list|,
name|children
argument_list|)
expr_stmt|;
assert|assert
name|children
operator|!=
literal|null
assert|;
block|}
comment|/**      * Creates an operand that matches any number of children.      *      * @param clazz Class of relational expression to match (must not be null)      * @param dummy Dummy argument to distinguish this constructor from other      * overloaded forms. Must be ANY.      *      * @deprecated Use {@link RelOptRule#any} or {@link RelOptRule#leaf}      */
specifier|public
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
name|Dummy
name|dummy
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|dummy
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|=
name|Util
operator|.
name|hashArray
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
name|boolean
name|equalTraits
init|=
operator|(
name|this
operator|.
name|trait
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|trait
operator|.
name|equals
argument_list|(
name|that
operator|.
name|trait
argument_list|)
else|:
operator|(
name|that
operator|.
name|trait
operator|==
literal|null
operator|)
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
name|equalTraits
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|children
argument_list|,
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
name|RelOptRuleOperand
index|[]
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

