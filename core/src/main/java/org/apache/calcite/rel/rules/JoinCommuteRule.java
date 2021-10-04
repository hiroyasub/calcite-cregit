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
name|rel
operator|.
name|rules
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
name|Contexts
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
name|RelOptUtil
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
name|RelRule
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
name|core
operator|.
name|Join
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
name|JoinRelType
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
name|rel
operator|.
name|core
operator|.
name|RelFactories
operator|.
name|ProjectFactory
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
name|logical
operator|.
name|LogicalJoin
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeField
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
name|RexBuilder
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
name|RexInputRef
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
name|RexNode
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
name|RexShuttle
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
name|RelBuilder
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
name|ImmutableBeans
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
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
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

begin_comment
comment|/**  * Planner rule that permutes the inputs to a  * {@link org.apache.calcite.rel.core.Join}.  *  *<p>Permutation of outer joins can be turned on/off by specifying the  * swapOuter flag in the constructor.  *  *<p>To preserve the order of columns in the output row, the rule adds a  * {@link org.apache.calcite.rel.core.Project}.  *  * @see CoreRules#JOIN_COMMUTE  * @see CoreRules#JOIN_COMMUTE_OUTER  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|JoinCommuteRule
extends|extends
name|RelRule
argument_list|<
name|JoinCommuteRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a JoinCommuteRule. */
specifier|protected
name|JoinCommuteRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinCommuteRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|boolean
name|swapOuter
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|clazz
argument_list|)
operator|.
name|withSwapOuter
argument_list|(
name|swapOuter
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinCommuteRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|projectFactory
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinCommuteRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|ProjectFactory
name|projectFactory
parameter_list|,
name|boolean
name|swapOuter
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|projectFactory
argument_list|)
argument_list|)
argument_list|,
name|swapOuter
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
annotation|@
name|Nullable
name|RelNode
name|swap
parameter_list|(
name|Join
name|join
parameter_list|)
block|{
return|return
name|swap
argument_list|(
name|join
argument_list|,
literal|false
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|join
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
annotation|@
name|Nullable
name|RelNode
name|swap
parameter_list|(
name|Join
name|join
parameter_list|,
name|boolean
name|swapOuterJoins
parameter_list|)
block|{
return|return
name|swap
argument_list|(
name|join
argument_list|,
name|swapOuterJoins
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|join
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a relational expression with the inputs switched round. Does not    * modify<code>join</code>. Returns null if the join cannot be swapped (for    * example, because it is an outer join).    *    * @param join              join to be swapped    * @param swapOuterJoins    whether outer joins should be swapped    * @param relBuilder        Builder for relational expressions    * @return swapped join if swapping possible; else null    */
specifier|public
specifier|static
annotation|@
name|Nullable
name|RelNode
name|swap
parameter_list|(
name|Join
name|join
parameter_list|,
name|boolean
name|swapOuterJoins
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|)
block|{
specifier|final
name|JoinRelType
name|joinType
init|=
name|join
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|swapOuterJoins
operator|&&
name|joinType
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|leftRowType
init|=
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rightRowType
init|=
name|join
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|VariableReplacer
name|variableReplacer
init|=
operator|new
name|VariableReplacer
argument_list|(
name|rexBuilder
argument_list|,
name|leftRowType
argument_list|,
name|rightRowType
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|oldCondition
init|=
name|join
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|RexNode
name|condition
init|=
name|variableReplacer
operator|.
name|apply
argument_list|(
name|oldCondition
argument_list|)
decl_stmt|;
comment|// NOTE jvs 14-Mar-2006: We preserve attribute semiJoinDone after the
comment|// swap.  This way, we will generate one semijoin for the original
comment|// join, and one for the swapped join, and no more.  This
comment|// doesn't prevent us from seeing any new combinations assuming
comment|// that the planner tries the desired order (semi-joins after swaps).
name|Join
name|newJoin
init|=
name|join
operator|.
name|copy
argument_list|(
name|join
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|condition
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|joinType
operator|.
name|swap
argument_list|()
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
init|=
name|RelOptUtil
operator|.
name|createSwappedJoinExprs
argument_list|(
name|newJoin
argument_list|,
name|join
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|newJoin
argument_list|)
operator|.
name|project
argument_list|(
name|exps
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// SEMI and ANTI join cannot be swapped.
if|if
condition|(
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|projectsRight
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Suppress join with "true" condition (that is, cartesian joins).
return|return
name|config
operator|.
name|isAllowAlwaysTrueCondition
argument_list|()
operator|||
operator|!
name|join
operator|.
name|getCondition
argument_list|()
operator|.
name|isAlwaysTrue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
specifier|final
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|swapped
init|=
name|swap
argument_list|(
name|join
argument_list|,
name|config
operator|.
name|isSwapOuter
argument_list|()
argument_list|,
name|call
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|swapped
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// The result is either a Project or, if the project is trivial, a
comment|// raw Join.
specifier|final
name|Join
name|newJoin
init|=
name|swapped
operator|instanceof
name|Join
condition|?
operator|(
name|Join
operator|)
name|swapped
else|:
operator|(
name|Join
operator|)
name|swapped
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|swapped
argument_list|)
expr_stmt|;
comment|// We have converted join='a join b' into swapped='select
comment|// a0,a1,a2,b0,b1 from b join a'. Now register that project='select
comment|// b0,b1,a0,a1,a2 from (select a0,a1,a2,b0,b1 from b join a)' is the
comment|// same as 'b join a'. If we didn't do this, the swap join rule
comment|// would fire on the new join, ad infinitum.
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
init|=
name|RelOptUtil
operator|.
name|createSwappedJoinExprs
argument_list|(
name|newJoin
argument_list|,
name|join
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|swapped
argument_list|)
operator|.
name|project
argument_list|(
name|exps
argument_list|,
name|newJoin
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|ensureRegistered
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|newJoin
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Walks over an expression, replacing references to fields of the left and    * right inputs.    *    *<p>If the field index is less than leftFieldCount, it must be from the    * left, and so has rightFieldCount added to it; if the field index is    * greater than leftFieldCount, it must be from the right, so we subtract    * leftFieldCount from it.</p>    */
specifier|private
specifier|static
class|class
name|VariableReplacer
extends|extends
name|RexShuttle
block|{
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|leftFields
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|rightFields
decl_stmt|;
name|VariableReplacer
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|leftType
parameter_list|,
name|RelDataType
name|rightType
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|this
operator|.
name|leftFields
operator|=
name|leftType
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
name|this
operator|.
name|rightFields
operator|=
name|rightType
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
name|int
name|index
init|=
name|inputRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|leftFields
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Field came from left side of join. Move it to the right.
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|leftFields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|rightFields
operator|.
name|size
argument_list|()
operator|+
name|index
argument_list|)
return|;
block|}
name|index
operator|-=
name|leftFields
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|rightFields
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Field came from right side of join. Move it to the left.
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|rightFields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|index
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Bad field offset: index="
operator|+
name|inputRef
operator|.
name|getIndex
argument_list|()
operator|+
literal|", leftFieldCount="
operator|+
name|leftFields
operator|.
name|size
argument_list|()
operator|+
literal|", rightFieldCount="
operator|+
name|rightFields
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableJoinCommuteRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|withSwapOuter
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|JoinCommuteRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|JoinCommuteRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|joinClass
argument_list|)
comment|// FIXME Enable this rule for joins with system fields
operator|.
name|predicate
argument_list|(
name|j
lambda|->
name|j
operator|.
name|getLeft
argument_list|()
operator|.
name|getId
argument_list|()
operator|!=
name|j
operator|.
name|getRight
argument_list|()
operator|.
name|getId
argument_list|()
operator|&&
name|j
operator|.
name|getSystemFieldList
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
comment|/** Whether to swap outer joins; default false. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isSwapOuter
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Sets {@link #isSwapOuter()}. */
name|Config
name|withSwapOuter
parameter_list|(
name|boolean
name|swapOuter
parameter_list|)
function_decl|;
comment|/** Whether to emit the new join tree if the join condition is {@code TRUE}      * (that is, cartesian joins); default true. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isAllowAlwaysTrueCondition
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Sets {@link #isAllowAlwaysTrueCondition()}. */
name|Config
name|withAllowAlwaysTrueCondition
parameter_list|(
name|boolean
name|allowAlwaysTrueCondition
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

