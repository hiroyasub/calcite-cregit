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
name|RelOptRuleOperand
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
name|EquiJoin
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
name|Filter
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
name|RexUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Iterator
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
comment|/**  * Planner rule that pushes filters above and  * within a join node into the join node and/or its children nodes.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|FilterJoinRule
extends|extends
name|RelOptRule
block|{
comment|/** Predicate that always returns true. With this predicate, every filter    * will be pushed into the ON clause. */
specifier|public
specifier|static
specifier|final
name|Predicate
name|TRUE_PREDICATE
init|=
operator|new
name|Predicate
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Join
name|join
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|RexNode
name|exp
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/** Rule that pushes predicates from a Filter into the Join below them. */
specifier|public
specifier|static
specifier|final
name|FilterJoinRule
name|FILTER_ON_JOIN
init|=
operator|new
name|FilterIntoJoinRule
argument_list|(
literal|true
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|TRUE_PREDICATE
argument_list|)
decl_stmt|;
comment|/** Dumber version of {@link #FILTER_ON_JOIN}. Not intended for production    * use, but keeps some tests working for which {@code FILTER_ON_JOIN} is too    * smart. */
specifier|public
specifier|static
specifier|final
name|FilterJoinRule
name|DUMB_FILTER_ON_JOIN
init|=
operator|new
name|FilterIntoJoinRule
argument_list|(
literal|false
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|TRUE_PREDICATE
argument_list|)
decl_stmt|;
comment|/** Rule that pushes predicates in a Join into the inputs to the join. */
specifier|public
specifier|static
specifier|final
name|FilterJoinRule
name|JOIN
init|=
operator|new
name|JoinConditionPushRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|TRUE_PREDICATE
argument_list|)
decl_stmt|;
comment|/** Whether to try to strengthen join-type. */
specifier|private
specifier|final
name|boolean
name|smart
decl_stmt|;
comment|/** Predicate that returns whether a filter is valid in the ON clause of a    * join for this particular kind of join. If not, Calcite will push it back to    * above the join. */
specifier|private
specifier|final
name|Predicate
name|predicate
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a FilterProjectTransposeRule with an explicit root operand and    * factories.    */
specifier|protected
name|FilterJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|smart
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"FilterJoinRule:"
operator|+
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|smart
operator|=
name|smart
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a FilterJoinRule with an explicit root operand and    * factories.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|FilterJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|smart
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|,
name|id
argument_list|,
name|smart
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|,
name|TRUE_PREDICATE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a FilterProjectTransposeRule with an explicit root operand and    * factories.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|FilterJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|smart
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|,
name|id
argument_list|,
name|smart
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|void
name|perform
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Join
name|join
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|joinFilters
init|=
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|origJoinFilters
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|joinFilters
argument_list|)
decl_stmt|;
comment|// If there is only the joinRel,
comment|// make sure it does not match a cartesian product joinRel
comment|// (with "true" condition), otherwise this rule will be applied
comment|// again on the new cartesian product joinRel.
if|if
condition|(
name|filter
operator|==
literal|null
operator|&&
name|joinFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|aboveFilters
init|=
name|filter
operator|!=
literal|null
condition|?
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
else|:
name|Lists
operator|.
expr|<
name|RexNode
operator|>
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|origAboveFilters
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|aboveFilters
argument_list|)
decl_stmt|;
comment|// Simplify Outer Joins
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
name|smart
operator|&&
operator|!
name|origAboveFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|join
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
name|joinType
operator|=
name|RelOptUtil
operator|.
name|simplifyJoin
argument_list|(
name|join
argument_list|,
name|origAboveFilters
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|leftFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|rightFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO - add logic to derive additional filters.  E.g., from
comment|// (t1.a = 1 AND t2.a = 2) OR (t1.b = 3 AND t2.b = 4), you can
comment|// derive table filters:
comment|// (t1.a = 1 OR t1.b = 3)
comment|// (t2.a = 2 OR t2.b = 4)
comment|// Try to push down above filters. These are typically where clause
comment|// filters. They can be pushed down if they are not on the NULL
comment|// generating side.
name|boolean
name|filterPushed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|RelOptUtil
operator|.
name|classifyFilters
argument_list|(
name|join
argument_list|,
name|aboveFilters
argument_list|,
name|joinType
argument_list|,
operator|!
operator|(
name|join
operator|instanceof
name|EquiJoin
operator|)
argument_list|,
operator|!
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|,
operator|!
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|,
name|joinFilters
argument_list|,
name|leftFilters
argument_list|,
name|rightFilters
argument_list|)
condition|)
block|{
name|filterPushed
operator|=
literal|true
expr_stmt|;
block|}
comment|// Move join filters up if needed
name|validateJoinFilters
argument_list|(
name|aboveFilters
argument_list|,
name|joinFilters
argument_list|,
name|join
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
comment|// If no filter got pushed after validate, reset filterPushed flag
if|if
condition|(
name|leftFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rightFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|joinFilters
operator|.
name|size
argument_list|()
operator|==
name|origJoinFilters
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|joinFilters
argument_list|)
operator|.
name|equals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|origJoinFilters
argument_list|)
argument_list|)
condition|)
block|{
name|filterPushed
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// Try to push down filters in ON clause. A ON clause filter can only be
comment|// pushed down if it does not affect the non-matching set, i.e. it is
comment|// not on the side which is preserved.
if|if
condition|(
name|RelOptUtil
operator|.
name|classifyFilters
argument_list|(
name|join
argument_list|,
name|joinFilters
argument_list|,
name|joinType
argument_list|,
literal|false
argument_list|,
operator|!
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|,
operator|!
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|,
name|joinFilters
argument_list|,
name|leftFilters
argument_list|,
name|rightFilters
argument_list|)
condition|)
block|{
name|filterPushed
operator|=
literal|true
expr_stmt|;
block|}
comment|// if nothing actually got pushed and there is nothing leftover,
comment|// then this rule is a no-op
if|if
condition|(
operator|(
operator|!
name|filterPushed
operator|&&
name|joinType
operator|==
name|join
operator|.
name|getJoinType
argument_list|()
operator|)
operator|||
operator|(
name|joinFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|leftFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rightFilters
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
return|return;
block|}
comment|// create Filters on top of the children if any filters were
comment|// pushed to them
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
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|leftRel
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|leftFilters
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rightRel
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|rightFilters
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// create the new join node referencing the new children and
comment|// containing its new join filters (if there are any)
specifier|final
name|ImmutableList
argument_list|<
name|RelDataType
argument_list|>
name|fieldTypes
init|=
name|ImmutableList
operator|.
expr|<
name|RelDataType
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|leftRel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addAll
argument_list|(
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|rightRel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|joinFilter
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|RexUtil
operator|.
name|fixUp
argument_list|(
name|rexBuilder
argument_list|,
name|joinFilters
argument_list|,
name|fieldTypes
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// If nothing actually got pushed and there is nothing leftover,
comment|// then this rule is a no-op
if|if
condition|(
name|joinFilter
operator|.
name|isAlwaysTrue
argument_list|()
operator|&&
name|leftFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rightFilters
operator|.
name|isEmpty
argument_list|()
operator|&&
name|joinType
operator|==
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
return|return;
block|}
name|RelNode
name|newJoinRel
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
name|joinFilter
argument_list|,
name|leftRel
argument_list|,
name|rightRel
argument_list|,
name|joinType
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|join
argument_list|,
name|newJoinRel
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|leftFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|filter
argument_list|,
name|leftRel
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|rightFilters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|filter
argument_list|,
name|rightRel
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|push
argument_list|(
name|newJoinRel
argument_list|)
expr_stmt|;
comment|// Create a project on top of the join if some of the columns have become
comment|// NOT NULL due to the join-type getting stricter.
name|relBuilder
operator|.
name|convert
argument_list|(
name|join
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create a FilterRel on top of the join if needed
name|relBuilder
operator|.
name|filter
argument_list|(
name|RexUtil
operator|.
name|fixUp
argument_list|(
name|rexBuilder
argument_list|,
name|aboveFilters
argument_list|,
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates that target execution framework can satisfy join filters.    *    *<p>If the join filter cannot be satisfied (for example, if it is    * {@code l.c1> r.c2} and the join only supports equi-join), removes the    * filter from {@code joinFilters} and adds it to {@code aboveFilters}.    *    *<p>The default implementation does nothing; i.e. the join can handle all    * conditions.    *    * @param aboveFilters Filter above Join    * @param joinFilters Filters in join condition    * @param join Join    * @param joinType JoinRelType could be different from type in Join due to    * outer join simplification.    */
specifier|protected
name|void
name|validateJoinFilters
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|aboveFilters
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|joinFilters
parameter_list|,
name|Join
name|join
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|RexNode
argument_list|>
name|filterIter
init|=
name|joinFilters
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|filterIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RexNode
name|exp
init|=
name|filterIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|predicate
operator|.
name|apply
argument_list|(
name|join
argument_list|,
name|joinType
argument_list|,
name|exp
argument_list|)
condition|)
block|{
name|aboveFilters
operator|.
name|add
argument_list|(
name|exp
argument_list|)
expr_stmt|;
name|filterIter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Rule that pushes parts of the join condition to its inputs. */
specifier|public
specifier|static
class|class
name|JoinConditionPushRule
extends|extends
name|FilterJoinRule
block|{
specifier|public
name|JoinConditionPushRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|super
argument_list|(
name|RelOptRule
operator|.
name|operand
argument_list|(
name|Join
operator|.
name|class
argument_list|,
name|RelOptRule
operator|.
name|any
argument_list|()
argument_list|)
argument_list|,
literal|"FilterJoinRule:no-filter"
argument_list|,
literal|true
argument_list|,
name|relBuilderFactory
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinConditionPushRule
parameter_list|(
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|this
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
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
name|perform
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|join
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Rule that tries to push filter expressions into a join    * condition and into the inputs of the join. */
specifier|public
specifier|static
class|class
name|FilterIntoJoinRule
extends|extends
name|FilterJoinRule
block|{
specifier|public
name|FilterIntoJoinRule
parameter_list|(
name|boolean
name|smart
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Join
operator|.
name|class
argument_list|,
name|RelOptRule
operator|.
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"FilterJoinRule:filter"
argument_list|,
name|smart
argument_list|,
name|relBuilderFactory
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterIntoJoinRule
parameter_list|(
name|boolean
name|smart
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|,
name|Predicate
name|predicate
parameter_list|)
block|{
name|this
argument_list|(
name|smart
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|filterFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|perform
argument_list|(
name|call
argument_list|,
name|filter
argument_list|,
name|join
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Predicate that returns whether a filter is valid in the ON clause of a    * join for this particular kind of join. If not, Calcite will push it back to    * above the join. */
specifier|public
interface|interface
name|Predicate
block|{
name|boolean
name|apply
parameter_list|(
name|Join
name|join
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|RexNode
name|exp
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End FilterJoinRule.java
end_comment

end_unit

