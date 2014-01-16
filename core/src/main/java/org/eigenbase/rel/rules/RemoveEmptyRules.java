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
name|RexLiteral
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptRule
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Collection of rules which remove sections of a query plan known never to  * produce any rows.  *  * @see EmptyRel  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RemoveEmptyRules
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Rule that removes empty children of a    * {@link UnionRel}.    *    *<p>Examples:    *    *<ul>    *<li>Union(Rel, Empty, Rel2) becomes Union(Rel, Rel2)    *<li>Union(Rel, Empty, Empty) becomes Rel    *<li>Union(Empty, Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|UNION_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
name|unordered
argument_list|(
name|operand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"Union"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|UnionRel
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|childRels
init|=
name|call
operator|.
name|getChildRels
argument_list|(
name|union
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newChildRels
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|childRel
range|:
name|childRels
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|childRel
operator|instanceof
name|EmptyRel
operator|)
condition|)
block|{
name|newChildRels
operator|.
name|add
argument_list|(
name|childRel
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|newChildRels
operator|.
name|size
argument_list|()
operator|<
name|childRels
operator|.
name|size
argument_list|()
operator|:
literal|"planner promised us at least one EmptyRel child"
assert|;
name|RelNode
name|newRel
decl_stmt|;
switch|switch
condition|(
name|newChildRels
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|newRel
operator|=
name|empty
argument_list|(
name|union
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|newRel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|newChildRels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|union
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
name|newRel
operator|=
operator|new
name|UnionRel
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newChildRels
argument_list|,
name|union
operator|.
name|all
argument_list|)
expr_stmt|;
break|break;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts a {@link ProjectRel}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Project(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|PROJECT_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|ProjectRelBase
operator|.
name|class
argument_list|,
literal|"PruneEmptyProject"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link FilterRel}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Filter(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|FILTER_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|FilterRelBase
operator|.
name|class
argument_list|,
literal|"PruneEmptyFilter"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link SortRel}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Sort(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|SORT_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|SortRel
operator|.
name|class
argument_list|,
literal|"PruneEmptySort"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link SortRel}    * to empty if it has {@code LIMIT 0}.    *    *<p>Examples:    *    *<ul>    *<li>Sort(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|SORT_FETCH_ZERO_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|SortRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
literal|"PruneSortLimit0"
argument_list|)
block|{
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
name|SortRel
name|sort
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
name|sort
operator|.
name|fetch
operator|!=
literal|null
operator|&&
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|sort
operator|.
name|fetch
argument_list|)
operator|==
literal|0
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|empty
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts an {@link AggregateRelBase}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Aggregate(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|AGGREGATE_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|AggregateRelBase
operator|.
name|class
argument_list|,
literal|"PruneEmptyAggregate"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link JoinRelBase}    * to empty if its left child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Join(Empty, Scan(Dept), INNER) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|JOIN_LEFT_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|JoinRelBase
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"PruneEmptyJoin(left)"
argument_list|)
block|{
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
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
condition|)
block|{
comment|// "select * from emp right join dept" is not necessarily empty if
comment|// emp is empty
return|return;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|empty
argument_list|(
name|join
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts a {@link JoinRelBase}    * to empty if its right child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Join(Scan(Emp), Empty, INNER) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|JOIN_RIGHT_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|JoinRelBase
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"PruneEmptyJoin(right)"
argument_list|)
block|{
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
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
condition|)
block|{
comment|// "select * from emp left join dept" is not necessarily empty if
comment|// dept is empty
return|return;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|empty
argument_list|(
name|join
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/** Creates an {@link EmptyRel} to replace {@code node}. */
specifier|private
specifier|static
name|EmptyRel
name|empty
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
return|return
operator|new
name|EmptyRel
argument_list|(
name|node
operator|.
name|getCluster
argument_list|()
argument_list|,
name|node
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|RemoveEmptySingleRule
extends|extends
name|RelOptRule
block|{
specifier|public
name|RemoveEmptySingleRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SingleRel
argument_list|>
name|clazz
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
argument_list|,
name|operand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|SingleRel
name|single
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|empty
argument_list|(
name|single
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RemoveEmptyRules.java
end_comment

end_unit

