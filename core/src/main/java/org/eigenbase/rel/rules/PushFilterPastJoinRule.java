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
name|rel
operator|.
name|rules
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
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
name|util
operator|.
name|Bug
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
name|Holder
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
comment|/**  * PushFilterPastJoinRule implements the rule for pushing filters above and  * within a join node into the join node and/or its children nodes.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PushFilterPastJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushFilterPastJoinRule
name|FILTER_ON_JOIN
init|=
operator|new
name|PushFilterIntoJoinRule
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/** Dumber version of {@link #FILTER_ON_JOIN}. Not intended for production    * use, but keeps some tests working for which {@code FILTER_ON_JOIN} is too    * smart. */
specifier|public
specifier|static
specifier|final
name|PushFilterPastJoinRule
name|DUMB_FILTER_ON_JOIN
init|=
operator|new
name|PushFilterIntoJoinRule
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PushFilterPastJoinRule
name|JOIN
init|=
operator|new
name|PushDownJoinConditionRule
argument_list|()
decl_stmt|;
comment|/** Whether to try to strengthen join-type. */
specifier|private
specifier|final
name|boolean
name|smart
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushFilterPastJoinRule with an explicit root operand.    */
specifier|private
name|PushFilterPastJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|smart
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
literal|"PushFilterRule: "
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
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|void
name|perform
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|FilterRel
name|filter
parameter_list|,
name|JoinRelBase
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
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
comment|// There is only the joinRel
comment|// make sure it does not match a cartesian product joinRel
comment|// (with "true" condition) otherwise this rule will be applied
comment|// again on the new cartesian product joinRel.
name|boolean
name|onlyTrueFilter
init|=
literal|true
decl_stmt|;
for|for
control|(
name|RexNode
name|joinFilter
range|:
name|joinFilters
control|)
block|{
if|if
condition|(
operator|!
name|joinFilter
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|onlyTrueFilter
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|onlyTrueFilter
condition|)
block|{
return|return;
block|}
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
name|ImmutableList
operator|.
expr|<
name|RexNode
operator|>
name|of
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|leftFilters
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|rightFilters
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
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
specifier|final
name|Holder
argument_list|<
name|JoinRelType
argument_list|>
name|joinTypeHolder
init|=
name|Holder
operator|.
name|of
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
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
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|,
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|,
name|joinFilters
argument_list|,
name|leftFilters
argument_list|,
name|rightFilters
argument_list|,
name|joinTypeHolder
argument_list|,
name|smart
argument_list|)
condition|)
block|{
name|filterPushed
operator|=
literal|true
expr_stmt|;
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
literal|null
argument_list|,
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|,
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|,
name|joinFilters
argument_list|,
name|leftFilters
argument_list|,
name|rightFilters
argument_list|,
name|joinTypeHolder
argument_list|,
name|smart
argument_list|)
condition|)
block|{
name|filterPushed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|filterPushed
condition|)
block|{
return|return;
block|}
comment|// create FilterRels on top of the children if any filters were
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
name|RelNode
name|leftRel
init|=
name|createFilterOnRel
argument_list|(
name|rexBuilder
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|leftFilters
argument_list|)
decl_stmt|;
name|RelNode
name|rightRel
init|=
name|createFilterOnRel
argument_list|(
name|rexBuilder
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|rightFilters
argument_list|)
decl_stmt|;
comment|// create the new join node referencing the new children and
comment|// containing its new join filters (if there are any)
name|RexNode
name|joinFilter
decl_stmt|;
if|if
condition|(
name|joinFilters
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// if nothing actually got pushed and there is nothing leftover,
comment|// then this rule is a no-op
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
name|joinTypeHolder
operator|.
name|get
argument_list|()
operator|==
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
return|return;
block|}
name|joinFilter
operator|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinFilter
operator|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|joinFilters
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|getCluster
argument_list|()
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|joinFilter
argument_list|,
name|leftRel
argument_list|,
name|rightRel
argument_list|,
name|joinTypeHolder
operator|.
name|get
argument_list|()
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
comment|// The pushed filters are not exact copies of the original filter, but
comment|// telling the planner about them seems to help the RelDecorrelator more
comment|// often than not. The real solution is to fix OPTIQ-443.
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"OPTIQ-443"
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
comment|// Create a project on top of the join if some of the columns have become
comment|// NOT NULL due to the join-type getting stricter.
name|newJoinRel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|newJoinRel
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create a FilterRel on top of the join if needed
name|RelNode
name|newRel
init|=
name|createFilterOnRel
argument_list|(
name|rexBuilder
argument_list|,
name|newJoinRel
argument_list|,
name|aboveFilters
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
comment|/**    * If the filter list passed in is non-empty, creates a FilterRel on top of    * the existing RelNode; otherwise, just returns the RelNode    *    * @param rexBuilder rex builder    * @param rel        the RelNode that the filter will be put on top of    * @param filters    list of filters    * @return new RelNode or existing one if no filters    */
specifier|private
name|RelNode
name|createFilterOnRel
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
parameter_list|)
block|{
name|RexNode
name|andFilters
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|filters
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|andFilters
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
return|return
name|rel
return|;
block|}
return|return
name|CalcRel
operator|.
name|createFilter
argument_list|(
name|rel
argument_list|,
name|andFilters
argument_list|)
return|;
block|}
comment|/** Rule that pushes parts of the join condition to its inputs. */
specifier|private
specifier|static
class|class
name|PushDownJoinConditionRule
extends|extends
name|PushFilterPastJoinRule
block|{
specifier|public
name|PushDownJoinConditionRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelOptRule
operator|.
name|operand
argument_list|(
name|JoinRelBase
operator|.
name|class
argument_list|,
name|RelOptRule
operator|.
name|any
argument_list|()
argument_list|)
argument_list|,
literal|"PushFilterPastJoinRule:no-filter"
argument_list|,
literal|true
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
name|JoinRelBase
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
specifier|private
specifier|static
class|class
name|PushFilterIntoJoinRule
extends|extends
name|PushFilterPastJoinRule
block|{
specifier|public
name|PushFilterIntoJoinRule
parameter_list|(
name|boolean
name|smart
parameter_list|)
block|{
name|super
argument_list|(
name|RelOptRule
operator|.
name|operand
argument_list|(
name|FilterRel
operator|.
name|class
argument_list|,
name|RelOptRule
operator|.
name|operand
argument_list|(
name|JoinRelBase
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
literal|"PushFilterPastJoinRule:filter"
argument_list|,
name|smart
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
name|FilterRel
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|JoinRelBase
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
block|}
end_class

begin_comment
comment|// End PushFilterPastJoinRule.java
end_comment

end_unit

