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
name|RelOptRule
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
name|RexCall
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
name|List
import|;
end_import

begin_comment
comment|/**  * Planner rule that permutes the inputs to a  * {@link org.apache.calcite.rel.core.Join}.  *  *<p>Outer joins cannot be permuted.  *  *<p>To preserve the order of columns in the output row, the rule adds a  * {@link org.apache.calcite.rel.core.Project}.  */
end_comment

begin_class
specifier|public
class|class
name|JoinCommuteRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** The singleton. */
specifier|public
specifier|static
specifier|final
name|JoinCommuteRule
name|INSTANCE
init|=
operator|new
name|JoinCommuteRule
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ProjectFactory
name|projectFactory
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a JoinCommuteRule.    */
specifier|private
name|JoinCommuteRule
parameter_list|()
block|{
name|this
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|DEFAULT_PROJECT_FACTORY
argument_list|)
expr_stmt|;
block|}
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
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectFactory
operator|=
name|projectFactory
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns a relational expression with the inputs switched round. Does not    * modify<code>join</code>. Returns null if the join cannot be swapped (for    * example, because it is an outer join).    */
specifier|public
specifier|static
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
argument_list|)
return|;
block|}
comment|/**    * @param join           join to be swapped    * @param swapOuterJoins whether outer joins should be swapped    * @return swapped join if swapping possible; else null    */
specifier|public
specifier|static
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
name|go
argument_list|(
name|oldCondition
argument_list|)
decl_stmt|;
comment|// NOTE jvs 14-Mar-2006: We preserve attribute semiJoinDone after the
comment|// swap.  This way, we will generate one semijoin for the original
comment|// join, and one for the swapped join, and no more.  This
comment|// doesn't prevent us from seeing any new combinations assuming
comment|// that the planner tries the desired order (semijoins after swaps).
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
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|newJoin
argument_list|,
name|exps
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
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
if|if
condition|(
operator|!
name|join
operator|.
name|getSystemFieldList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// FIXME Enable this rule for joins with system fields
return|return;
block|}
specifier|final
name|RelNode
name|swapped
init|=
name|swap
argument_list|(
name|join
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
name|RelNode
name|project
init|=
name|projectFactory
operator|.
name|createProject
argument_list|(
name|swapped
argument_list|,
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
decl_stmt|;
name|RelNode
name|rel
init|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|ensureRegistered
argument_list|(
name|project
argument_list|,
name|newJoin
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Walks over an expression, replacing references to fields of the left and    * right inputs.    *    *<p>If the field index is less than leftFieldCount, it must be from the    * left, and so has rightFieldCount added to it; if the field index is    * greater than leftFieldCount, it must be from the right, so we subtract    * leftFieldCount from it.</p>    */
specifier|private
specifier|static
class|class
name|VariableReplacer
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
specifier|public
name|RexNode
name|go
parameter_list|(
name|RexNode
name|rex
parameter_list|)
block|{
if|if
condition|(
name|rex
operator|instanceof
name|RexCall
condition|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rex
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|go
argument_list|(
name|operand
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|call
operator|.
name|clone
argument_list|(
name|call
operator|.
name|getType
argument_list|()
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|rex
operator|instanceof
name|RexInputRef
condition|)
block|{
name|RexInputRef
name|var
init|=
operator|(
name|RexInputRef
operator|)
name|rex
decl_stmt|;
name|int
name|index
init|=
name|var
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
name|Util
operator|.
name|newInternal
argument_list|(
literal|"Bad field offset: index="
operator|+
name|var
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
else|else
block|{
return|return
name|rex
return|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End JoinCommuteRule.java
end_comment

end_unit

