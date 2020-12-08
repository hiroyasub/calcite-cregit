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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|NotOnlyInitialized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|UnknownInitialization
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|MonotonicNonNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Operand that determines whether a {@link RelOptRule}  * can be applied to a particular expression.  *  *<p>For example, the rule to pull a filter up from the left side of a join  * takes operands:<code>Join(Filter, Any)</code>.</p>  *  *<p>Note that<code>children</code> means different things if it is empty or  * it is<code>null</code>:<code>Join(Filter<b>()</b>, Any)</code> means  * that, to match the rule,<code>Filter</code> must have no operands.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelOptRuleOperand
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
annotation|@
name|Nullable
name|RelOptRuleOperand
name|parent
decl_stmt|;
specifier|private
annotation|@
name|NotOnlyInitialized
name|RelOptRule
name|rule
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|RelNode
argument_list|>
name|predicate
decl_stmt|;
comment|// REVIEW jvs 29-Aug-2004: some of these are Volcano-specific and should be
comment|// factored out
specifier|public
name|int
annotation|@
name|MonotonicNonNull
type|[]
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
specifier|public
specifier|final
annotation|@
name|Nullable
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
comment|/**    * Whether child operands can be matched in any order.    */
specifier|public
specifier|final
name|RelOptRuleOperandChildPolicy
name|childPolicy
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an operand.    *    *<p>The {@code childOperands} argument is often populated by calling one    * of the following methods:    * {@link RelOptRule#some},    * {@link RelOptRule#none()},    * {@link RelOptRule#any},    * {@link RelOptRule#unordered},    * See {@link org.apache.calcite.plan.RelOptRuleOperandChildren} for more    * details.    *    * @param clazz    Class of relational expression to match (must not be null)    * @param trait    Trait to match, or null to match any trait    * @param predicate Predicate to apply to relational expression    * @param children Child operands    *    * @deprecated Use    * {@link RelOptRule#operand(Class, RelOptRuleOperandChildren)} or one of its    * overloaded methods.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0; see [CALCITE-1166]
specifier|protected
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|trait
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelOptRuleOperandChildren
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|predicate
argument_list|,
name|children
operator|.
name|policy
argument_list|,
name|children
operator|.
name|operands
argument_list|)
expr_stmt|;
block|}
comment|/** Private constructor.    *    *<p>Do not call from outside package, and do not create a sub-class.    *    *<p>The other constructor is deprecated; when it is removed, make fields    * {@link #parent}, {@link #ordinalInParent} and {@link #solveOrder} final,    * and add constructor parameters for them. See    *<a href="https://issues.apache.org/jira/browse/CALCITE-1166">[CALCITE-1166]    * Disallow sub-classes of RelOptRuleOperand</a>. */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"initialization.fields.uninitialized"
block|,
literal|"initialization.invalid.field.write.initialized"
block|}
argument_list|)
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
annotation|@
name|Nullable
name|RelTrait
name|trait
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelOptRuleOperandChildPolicy
name|childPolicy
parameter_list|,
name|ImmutableList
argument_list|<
name|RelOptRuleOperand
argument_list|>
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
name|childPolicy
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
name|childPolicy
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|clazz
argument_list|,
literal|"clazz"
argument_list|)
expr_stmt|;
name|this
operator|.
name|trait
operator|=
name|trait
expr_stmt|;
comment|//noinspection unchecked
name|this
operator|.
name|predicate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
operator|(
name|Predicate
operator|)
name|predicate
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
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
comment|/**    * Returns the parent operand.    *    * @return parent operand    */
specifier|public
annotation|@
name|Nullable
name|RelOptRuleOperand
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**    * Sets the parent operand.    *    * @param parent Parent operand    */
specifier|public
name|void
name|setParent
parameter_list|(
annotation|@
name|Nullable
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
comment|/**    * Returns the rule this operand belongs to.    *    * @return containing rule    */
specifier|public
name|RelOptRule
name|getRule
parameter_list|()
block|{
return|return
name|rule
return|;
block|}
comment|/**    * Sets the rule this operand belongs to.    *    * @param rule containing rule    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"initialization.invalid.field.write.initialized"
argument_list|)
specifier|public
name|void
name|setRule
parameter_list|(
annotation|@
name|UnknownInitialization
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|children
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
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
name|Objects
operator|.
name|equals
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
comment|/**    *<b>FOR DEBUG ONLY.</b>    *    *<p>To facilitate IDE shows the operand description in the debugger,    * returns the root operand description, but highlight current    * operand's matched class with '*' in the description.</p>    *    *<p>e.g. The following are examples of rule operand description for    * the operands that match with {@code LogicalFilter}.</p>    *    *<ul>    *<li>SemiJoinRule:project: Project(Join(*RelNode*, Aggregate))</li>    *<li>ProjectFilterTransposeRule: LogicalProject(*LogicalFilter*)</li>    *<li>FilterProjectTransposeRule: *Filter*(Project)</li>    *<li>ReduceExpressionsRule(Filter): *LogicalFilter*</li>    *<li>PruneEmptyJoin(right): Join(*RelNode*, Values)</li>    *</ul>    *    * @see #describeIt(RelOptRuleOperand)    */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|RelOptRuleOperand
name|root
init|=
name|this
decl_stmt|;
while|while
condition|(
name|root
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|root
operator|=
name|root
operator|.
name|parent
expr_stmt|;
block|}
name|StringBuilder
name|s
init|=
name|root
operator|.
name|describeIt
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns this rule operand description, and highlight the operand's    * class name with '*' if {@code that} operand equals current operand.    *    * @param that The rule operand that needs to be highlighted    * @return The string builder that describes this rule operand    * @see #toString()    */
specifier|private
name|StringBuilder
name|describeIt
parameter_list|(
name|RelOptRuleOperand
name|that
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
name|rule
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
operator|!
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|RelOptRuleOperand
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|child
operator|.
name|describeIt
argument_list|(
name|that
argument_list|)
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Returns relational expression class matched by this operand.    */
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
comment|/**    * Returns the child operands.    *    * @return child operands    */
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
comment|/**    * Returns whether a relational expression matches this operand. It must be    * of the right class and trait.    */
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
name|predicate
operator|.
name|test
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

