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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|convert
operator|.
name|Converter
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
name|convert
operator|.
name|ConverterRule
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
name|core
operator|.
name|RelFactories
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
name|tools
operator|.
name|RelBuilderFactory
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * A<code>RelOptRule</code> transforms an expression into another. It has a  * list of {@link RelOptRuleOperand}s, which determine whether the rule can be  * applied to a particular section of the tree.  *  *<p>The optimizer figures out which rules are applicable, then calls  * {@link #onMatch} on each of them.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Description of rule, must be unique within planner. Default is the name    * of the class sans package name, but derived classes are encouraged to    * override.    */
specifier|protected
specifier|final
name|String
name|description
decl_stmt|;
comment|/**    * Root of operand tree.    */
specifier|private
specifier|final
name|RelOptRuleOperand
name|operand
decl_stmt|;
comment|/** Factory for a builder for relational expressions.    *    *<p>The actual builder is available via {@link RelOptRuleCall#builder()}. */
specifier|public
specifier|final
name|RelBuilderFactory
name|relBuilderFactory
decl_stmt|;
comment|/**    * Flattened list of operands.    */
specifier|public
specifier|final
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operands
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a rule.    *    * @param operand root operand, must not be null    */
specifier|public
name|RelOptRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a rule with an explicit description.    *    * @param operand     root operand, must not be null    * @param description Description, or null to guess description    */
specifier|public
name|RelOptRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a rule with an explicit description.    *    * @param operand     root operand, must not be null    * @param description Description, or null to guess description    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|RelOptRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|operand
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|operand
argument_list|)
expr_stmt|;
name|this
operator|.
name|relBuilderFactory
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|relBuilderFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|description
operator|==
literal|null
condition|)
block|{
name|description
operator|=
name|guessDescription
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|description
operator|.
name|matches
argument_list|(
literal|"[A-Za-z][-A-Za-z0-9_.(),\\[\\]\\s:]*"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Rule description '"
operator|+
name|description
operator|+
literal|"' is not valid"
argument_list|)
throw|;
block|}
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|flattenOperands
argument_list|(
name|operand
argument_list|)
expr_stmt|;
name|assignSolveOrder
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods for creating operands ------------------------------------------
comment|/**    * Creates an operand that matches a relational expression that has no    * children.    *    * @param clazz Class of relational expression to match (must not be null)    * @param operandList Child operands    * @param<R> Class of relational expression to match    * @return Operand that matches a relational expression that has no    *   children    *    * @deprecated Use {@link RelRule.OperandBuilder#operand(Class)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|RelOptRuleOperandChildren
name|operandList
parameter_list|)
block|{
return|return
operator|new
name|RelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|r
lambda|->
literal|true
argument_list|,
name|operandList
operator|.
name|policy
argument_list|,
name|operandList
operator|.
name|operands
argument_list|)
return|;
block|}
comment|/**    * Creates an operand that matches a relational expression that has no    * children.    *    * @param clazz Class of relational expression to match (must not be null)    * @param trait Trait to match, or null to match any trait    * @param operandList Child operands    * @param<R> Class of relational expression to match    * @return Operand that matches a relational expression that has no    *   children    *    * @deprecated Use {@link RelRule.OperandBuilder#operand(Class)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operand
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
name|RelOptRuleOperandChildren
name|operandList
parameter_list|)
block|{
return|return
operator|new
name|RelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|r
lambda|->
literal|true
argument_list|,
name|operandList
operator|.
name|policy
argument_list|,
name|operandList
operator|.
name|operands
argument_list|)
return|;
block|}
comment|/**    * Creates an operand that matches a relational expression that has a    * particular trait and predicate.    *    * @param clazz Class of relational expression to match (must not be null)    * @param trait Trait to match, or null to match any trait    * @param predicate Additional match predicate    * @param operandList Child operands    * @param<R> Class of relational expression to match    * @return Operand that matches a relational expression that has a    *   particular trait and predicate    *    * @deprecated Use {@link RelRule.OperandBuilder#operand(Class)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operandJ
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
name|operandList
parameter_list|)
block|{
return|return
operator|new
name|RelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|predicate
argument_list|,
name|operandList
operator|.
name|policy
argument_list|,
name|operandList
operator|.
name|operands
argument_list|)
return|;
block|}
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link #operandJ} */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operand
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelOptRuleOperandChildren
name|operandList
parameter_list|)
block|{
return|return
name|operandJ
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
operator|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
operator|)
name|predicate
operator|::
name|apply
argument_list|,
name|operandList
argument_list|)
return|;
block|}
comment|/**    * Creates an operand that matches a relational expression that has no    * children.    *    * @param clazz Class of relational expression to match (must not be null)    * @param trait Trait to match, or null to match any trait    * @param predicate Additional match predicate    * @param first First operand    * @param rest Rest operands    * @param<R> Class of relational expression to match    * @return Operand    *    * @deprecated Use {@link RelRule.OperandBuilder#operand(Class)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operandJ
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
name|RelOptRuleOperand
name|first
parameter_list|,
name|RelOptRuleOperand
modifier|...
name|rest
parameter_list|)
block|{
return|return
name|operandJ
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|predicate
argument_list|,
name|some
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operand
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelOptRuleOperand
name|first
parameter_list|,
name|RelOptRuleOperand
modifier|...
name|rest
parameter_list|)
block|{
return|return
name|operandJ
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
operator|(
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
operator|)
name|predicate
operator|::
name|apply
argument_list|,
name|first
argument_list|,
name|rest
argument_list|)
return|;
block|}
comment|/**    * Creates an operand that matches a relational expression with a given    * list of children.    *    *<p>Shorthand for<code>operand(clazz, some(...))</code>.    *    *<p>If you wish to match a relational expression that has no children    * (that is, a leaf node), write<code>operand(clazz, none())</code></p>.    *    *<p>If you wish to match a relational expression that has any number of    * children, write<code>operand(clazz, any())</code></p>.    *    * @param clazz Class of relational expression to match (must not be null)    * @param first First operand    * @param rest Rest operands    * @param<R> Class of relational expression to match    * @return Operand that matches a relational expression with a given    *   list of children    *    * @deprecated Use {@link RelRule.OperandBuilder#operand(Class)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|RelOptRuleOperand
name|operand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|RelOptRuleOperand
name|first
parameter_list|,
name|RelOptRuleOperand
modifier|...
name|rest
parameter_list|)
block|{
return|return
name|operand
argument_list|(
name|clazz
argument_list|,
name|some
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates an operand for a converter rule.    *    * @param clazz    Class of relational expression to match (must not be null)    * @param trait    Trait to match, or null to match any trait    * @param predicate Predicate to apply to relational expression    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRelOptRuleOperand
name|convertOperand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
operator|new
name|ConverterRelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|predicate
argument_list|)
return|;
block|}
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link #convertOperand(Class, Predicate, RelTrait)}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
specifier|static
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRelOptRuleOperand
name|convertOperand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
operator|new
name|ConverterRelOptRuleOperand
argument_list|(
name|clazz
argument_list|,
name|trait
argument_list|,
name|predicate
operator|::
name|apply
argument_list|)
return|;
block|}
comment|//~ Methods for creating lists of child operands ---------------------------
comment|/**    * Creates a list of child operands that matches child relational    * expressions in the order they appear.    *    * @param first First child operand    * @param rest  Remaining child operands (may be empty)    * @return List of child operands that matches child relational    *   expressions in the order    *    * @deprecated Use {@link RelRule.OperandDetailBuilder#inputs}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelOptRuleOperandChildren
name|some
parameter_list|(
name|RelOptRuleOperand
name|first
parameter_list|,
name|RelOptRuleOperand
modifier|...
name|rest
parameter_list|)
block|{
return|return
operator|new
name|RelOptRuleOperandChildren
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|SOME
argument_list|,
name|Lists
operator|.
name|asList
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a list of child operands that matches child relational    * expressions in any order.    *    *<p>This is useful when matching a relational expression which    * can have a variable number of children. For example, the rule to    * eliminate empty children of a Union would have operands</p>    *    *<blockquote>Operand(Union, true, Operand(Empty))</blockquote>    *    *<p>and given the relational expressions</p>    *    *<blockquote>Union(LogicalFilter, Empty, LogicalProject)</blockquote>    *    *<p>would fire the rule with arguments</p>    *    *<blockquote>{Union, Empty}</blockquote>    *    *<p>It is up to the rule to deduce the other children, or indeed the    * position of the matched child.</p>    *    * @param first First child operand    * @param rest  Remaining child operands (may be empty)    * @return List of child operands that matches child relational    *   expressions in any order    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelOptRuleOperandChildren
name|unordered
parameter_list|(
name|RelOptRuleOperand
name|first
parameter_list|,
name|RelOptRuleOperand
modifier|...
name|rest
parameter_list|)
block|{
return|return
operator|new
name|RelOptRuleOperandChildren
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|UNORDERED
argument_list|,
name|Lists
operator|.
name|asList
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates an empty list of child operands.    *    * @return Empty list of child operands    *    * @deprecated Use {@link RelRule.OperandDetailBuilder#noInputs()}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelOptRuleOperandChildren
name|none
parameter_list|()
block|{
return|return
name|RelOptRuleOperandChildren
operator|.
name|LEAF_CHILDREN
return|;
block|}
comment|/**    * Creates a list of child operands that signifies that the operand matches    * any number of child relational expressions.    *    * @return List of child operands that signifies that the operand matches    *   any number of child relational expressions    *    * @deprecated Use {@link RelRule.OperandDetailBuilder#anyInputs()}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelOptRuleOperandChildren
name|any
parameter_list|()
block|{
return|return
name|RelOptRuleOperandChildren
operator|.
name|ANY_CHILDREN
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a flattened list of this operand and its descendants in prefix    * order.    *    * @param rootOperand Root operand    * @return Flattened list of operands    */
specifier|private
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|flattenOperands
parameter_list|(
name|RelOptRuleOperand
name|rootOperand
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operandList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Flatten the operands into a list.
name|rootOperand
operator|.
name|setRule
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|rootOperand
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|rootOperand
operator|.
name|ordinalInParent
operator|=
literal|0
expr_stmt|;
name|rootOperand
operator|.
name|ordinalInRule
operator|=
name|operandList
operator|.
name|size
argument_list|()
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|rootOperand
argument_list|)
expr_stmt|;
name|flattenRecurse
argument_list|(
name|operandList
argument_list|,
name|rootOperand
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operandList
argument_list|)
return|;
block|}
comment|/**    * Adds the operand and its descendants to the list in prefix order.    *    * @param operandList   Flattened list of operands    * @param parentOperand Parent of this operand    */
specifier|private
name|void
name|flattenRecurse
parameter_list|(
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operandList
parameter_list|,
name|RelOptRuleOperand
name|parentOperand
parameter_list|)
block|{
name|int
name|k
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelOptRuleOperand
name|operand
range|:
name|parentOperand
operator|.
name|getChildOperands
argument_list|()
control|)
block|{
name|operand
operator|.
name|setRule
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|operand
operator|.
name|setParent
argument_list|(
name|parentOperand
argument_list|)
expr_stmt|;
name|operand
operator|.
name|ordinalInParent
operator|=
name|k
operator|++
expr_stmt|;
name|operand
operator|.
name|ordinalInRule
operator|=
name|operandList
operator|.
name|size
argument_list|()
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|operand
argument_list|)
expr_stmt|;
name|flattenRecurse
argument_list|(
name|operandList
argument_list|,
name|operand
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Builds each operand's solve-order. Start with itself, then its parent, up    * to the root, then the remaining operands in prefix order.    */
specifier|private
name|void
name|assignSolveOrder
parameter_list|()
block|{
for|for
control|(
name|RelOptRuleOperand
name|operand
range|:
name|operands
control|)
block|{
name|operand
operator|.
name|solveOrder
operator|=
operator|new
name|int
index|[
name|operands
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|m
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelOptRuleOperand
name|o
init|=
name|operand
init|;
name|o
operator|!=
literal|null
condition|;
name|o
operator|=
name|o
operator|.
name|getParent
argument_list|()
control|)
block|{
name|operand
operator|.
name|solveOrder
index|[
name|m
operator|++
index|]
operator|=
name|o
operator|.
name|ordinalInRule
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|operands
operator|.
name|size
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|m
condition|;
name|n
operator|++
control|)
block|{
if|if
condition|(
name|operand
operator|.
name|solveOrder
index|[
name|n
index|]
operator|==
name|k
condition|)
block|{
name|exists
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|exists
condition|)
block|{
name|operand
operator|.
name|solveOrder
index|[
name|m
operator|++
index|]
operator|=
name|k
expr_stmt|;
block|}
block|}
comment|// Assert: operand appears once in the sort-order.
assert|assert
name|m
operator|==
name|operands
operator|.
name|size
argument_list|()
assert|;
block|}
block|}
comment|/**    * Returns the root operand of this rule.    *    * @return the root operand of this rule    */
specifier|public
name|RelOptRuleOperand
name|getOperand
parameter_list|()
block|{
return|return
name|operand
return|;
block|}
comment|/**    * Returns a flattened list of operands of this rule.    *    * @return flattened list of operands    */
specifier|public
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|getOperands
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operands
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Conventionally, hashCode() and equals() should use the same
comment|// criteria, whereas here we only look at the description. This is
comment|// okay, because the planner requires all rule instances to have
comment|// distinct descriptions.
return|return
name|description
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|RelOptRule
operator|)
operator|&&
name|equals
argument_list|(
operator|(
name|RelOptRule
operator|)
name|obj
argument_list|)
return|;
block|}
comment|/**    * Returns whether this rule is equal to another rule.    *    *<p>The base implementation checks that the rules have the same class and    * that the operands are equal; derived classes can override.    *    * @param that Another rule    * @return Whether this rule is equal to another rule    */
specifier|protected
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nonnull
name|RelOptRule
name|that
parameter_list|)
block|{
comment|// Include operands and class in the equality criteria just in case
comment|// they have chosen a poor description.
return|return
name|this
operator|==
name|that
operator|||
name|this
operator|.
name|getClass
argument_list|()
operator|==
name|that
operator|.
name|getClass
argument_list|()
operator|&&
name|this
operator|.
name|description
operator|.
name|equals
argument_list|(
name|that
operator|.
name|description
argument_list|)
operator|&&
name|this
operator|.
name|operand
operator|.
name|equals
argument_list|(
name|that
operator|.
name|operand
argument_list|)
return|;
block|}
comment|/**    * Returns whether this rule could possibly match the given operands.    *    *<p>This method is an opportunity to apply side-conditions to a rule. The    * {@link RelOptPlanner} calls this method after matching all operands of    * the rule, and before calling {@link #onMatch(RelOptRuleCall)}.    *    *<p>In implementations of {@link RelOptPlanner} which may queue up a    * matched {@link RelOptRuleCall} for a long time before calling    * {@link #onMatch(RelOptRuleCall)}, this method is beneficial because it    * allows the planner to discard rules earlier in the process.    *    *<p>The default implementation of this method returns<code>true</code>.    * It is acceptable for any implementation of this method to give a false    * positives, that is, to say that the rule matches the operands but have    * {@link #onMatch(RelOptRuleCall)} subsequently not generate any    * successors.    *    *<p>The following script is useful to identify rules which commonly    * produce no successors. You should override this method for these rules:    *    *<blockquote>    *<pre><code>awk '    * /Apply rule/ {rule=$4; ruleCount[rule]++;}    * /generated 0 successors/ {ruleMiss[rule]++;}    * END {    *   printf "%-30s %s %s\n", "Rule", "Fire", "Miss";    *   for (i in ruleCount) {    *     printf "%-30s %5d %5d\n", i, ruleCount[i], ruleMiss[i];    *   }    * } ' FarragoTrace.log</code></pre>    *</blockquote>    *    * @param call Rule call which has been determined to match all operands of    *             this rule    * @return whether this RelOptRule matches a given RelOptRuleCall    */
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Receives notification about a rule match. At the time that this method is    * called, {@link RelOptRuleCall#rels call.rels} holds the set of relational    * expressions which match the operands to the rule;<code>    * call.rels[0]</code> is the root expression.    *    *<p>Typically a rule would check that the nodes are valid matches, creates    * a new expression, then calls back {@link RelOptRuleCall#transformTo} to    * register the expression.</p>    *    * @param call Rule call    * @see #matches(RelOptRuleCall)    */
specifier|public
specifier|abstract
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Returns the convention of the result of firing this rule, null if    * not known.    *    * @return Convention of the result of firing this rule, null if    *   not known    */
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns the trait which will be modified as a result of firing this rule,    * or null if the rule is not a converter rule.    *    * @return Trait which will be modified as a result of firing this rule,    *   or null if the rule is not a converter rule    */
specifier|public
name|RelTrait
name|getOutTrait
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns the description of this rule.    *    *<p>It must be unique (for rules that are not equal) and must consist of    * only the characters A-Z, a-z, 0-9, '_', '.', '(', ')', '-', ',', '[', ']', ':', ' '.    * It must start with a letter. */
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**    * Converts a relation expression to a given set of traits, if it does not    * already have those traits.    *    * @param rel      Relational expression to convert    * @param toTraits desired traits    * @return a relational expression with the desired traits; never null    */
specifier|public
specifier|static
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|toTraits
parameter_list|)
block|{
name|RelOptPlanner
name|planner
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|RelTraitSet
name|outTraits
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|toTraits
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelTrait
name|toTrait
init|=
name|toTraits
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|toTrait
operator|!=
literal|null
condition|)
block|{
name|outTraits
operator|=
name|outTraits
operator|.
name|replace
argument_list|(
name|i
argument_list|,
name|toTrait
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|matches
argument_list|(
name|outTraits
argument_list|)
condition|)
block|{
return|return
name|rel
return|;
block|}
return|return
name|planner
operator|.
name|changeTraits
argument_list|(
name|rel
argument_list|,
name|outTraits
argument_list|)
return|;
block|}
comment|/**    * Converts one trait of a relational expression, if it does not    * already have that trait.    *    * @param rel      Relational expression to convert    * @param toTrait  Desired trait    * @return a relational expression with the desired trait; never null    */
specifier|public
specifier|static
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelTrait
name|toTrait
parameter_list|)
block|{
name|RelOptPlanner
name|planner
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|RelTraitSet
name|outTraits
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|toTrait
operator|!=
literal|null
condition|)
block|{
name|outTraits
operator|=
name|outTraits
operator|.
name|replace
argument_list|(
name|toTrait
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|matches
argument_list|(
name|outTraits
argument_list|)
condition|)
block|{
return|return
name|rel
return|;
block|}
return|return
name|planner
operator|.
name|changeTraits
argument_list|(
name|rel
argument_list|,
name|outTraits
operator|.
name|simplify
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Converts a list of relational expressions.    *    * @param rels     Relational expressions    * @param trait   Trait to add to each relational expression    * @return List of converted relational expressions, never null    */
specifier|protected
specifier|static
name|List
argument_list|<
name|RelNode
argument_list|>
name|convertList
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
parameter_list|,
specifier|final
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|rels
argument_list|,
name|rel
lambda|->
name|convert
argument_list|(
name|rel
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|trait
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Deduces a name for a rule by taking the name of its class and returning    * the segment after the last '.' or '$'.    *    *<p>Examples:    *<ul>    *<li>"com.foo.Bar" yields "Bar";</li>    *<li>"com.flatten.Bar$Baz" yields "Baz";</li>    *<li>"com.foo.Bar$1" yields "1" (which as an integer is an invalid    * name, and writer of the rule is encouraged to give it an    * explicit name).</li>    *</ul>    *    * @param className Name of the rule's class    * @return Last segment of the class    */
specifier|static
name|String
name|guessDescription
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|String
name|description
init|=
name|className
decl_stmt|;
name|int
name|punc
init|=
name|Math
operator|.
name|max
argument_list|(
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|,
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|'$'
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|punc
operator|>=
literal|0
condition|)
block|{
name|description
operator|=
name|className
operator|.
name|substring
argument_list|(
name|punc
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|description
operator|.
name|matches
argument_list|(
literal|"[0-9]+"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Derived description of rule class "
operator|+
name|className
operator|+
literal|" is an integer, not valid. "
operator|+
literal|"Supply a description manually."
argument_list|)
throw|;
block|}
return|return
name|description
return|;
block|}
comment|/**    * Operand to an instance of the converter rule.    */
specifier|protected
specifier|static
class|class
name|ConverterRelOptRuleOperand
extends|extends
name|RelOptRuleOperand
block|{
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|ConverterRelOptRuleOperand
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|Predicate
argument_list|<
name|?
super|super
name|R
argument_list|>
name|predicate
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|,
name|predicate
argument_list|,
name|RelOptRuleOperandChildPolicy
operator|.
name|ANY
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
comment|// Don't apply converters to converters that operate
comment|// on the same RelTraitDef -- otherwise we get
comment|// an n^2 effect.
if|if
condition|(
name|rel
operator|instanceof
name|Converter
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|ConverterRule
operator|)
name|getRule
argument_list|()
operator|)
operator|.
name|getTraitDef
argument_list|()
operator|==
operator|(
operator|(
name|Converter
operator|)
name|rel
operator|)
operator|.
name|getTraitDef
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|super
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

