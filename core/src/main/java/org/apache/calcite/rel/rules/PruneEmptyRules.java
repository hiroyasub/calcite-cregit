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
name|plan
operator|.
name|hep
operator|.
name|HepRelVertex
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
name|volcano
operator|.
name|RelSubset
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
name|SingleRel
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
name|Aggregate
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
name|Project
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
name|Sort
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
name|Values
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
name|LogicalIntersect
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
name|LogicalMinus
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
name|LogicalUnion
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
name|LogicalValues
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
name|RexDynamicParam
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
name|RexLiteral
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
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|none
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|operand
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|operandJ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|some
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRule
operator|.
name|unordered
import|;
end_import

begin_comment
comment|/**  * Collection of rules which remove sections of a query plan known never to  * produce any rows.  *  *<p>Conventionally, the way to represent an empty relational expression is  * with a {@link Values} that has no tuples.  *  * @see LogicalValues#createEmpty  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PruneEmptyRules
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Rule that removes empty children of a    * {@link org.apache.calcite.rel.logical.LogicalUnion}.    *    *<p>Examples:    *    *<ul>    *<li>Union(Rel, Empty, Rel2) becomes Union(Rel, Rel2)    *<li>Union(Rel, Empty, Empty) becomes Rel    *<li>Union(Empty, Empty) becomes Empty    *</ul>    */
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
name|LogicalUnion
operator|.
name|class
argument_list|,
name|unordered
argument_list|(
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
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
specifier|final
name|LogicalUnion
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
name|inputs
init|=
name|union
operator|.
name|getInputs
argument_list|()
decl_stmt|;
assert|assert
name|inputs
operator|!=
literal|null
assert|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|nonEmptyInputs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|input
argument_list|)
condition|)
block|{
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|nonEmptyInputs
operator|++
expr_stmt|;
block|}
block|}
assert|assert
name|nonEmptyInputs
operator|<
name|inputs
operator|.
name|size
argument_list|()
operator|:
literal|"planner promised us at least one Empty child: "
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|union
argument_list|)
assert|;
if|if
condition|(
name|nonEmptyInputs
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|push
argument_list|(
name|union
argument_list|)
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|union
argument_list|(
name|union
operator|.
name|all
argument_list|,
name|nonEmptyInputs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|convert
argument_list|(
name|union
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Rule that removes empty children of a    * {@link org.apache.calcite.rel.logical.LogicalMinus}.    *    *<p>Examples:    *    *<ul>    *<li>Minus(Rel, Empty, Rel2) becomes Minus(Rel, Rel2)    *<li>Minus(Empty, Rel) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|MINUS_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|LogicalMinus
operator|.
name|class
argument_list|,
name|unordered
argument_list|(
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"Minus"
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
specifier|final
name|LogicalMinus
name|minus
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
name|inputs
init|=
name|minus
operator|.
name|getInputs
argument_list|()
decl_stmt|;
assert|assert
name|inputs
operator|!=
literal|null
assert|;
name|int
name|nonEmptyInputs
init|=
literal|0
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|input
argument_list|)
condition|)
block|{
name|builder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|nonEmptyInputs
operator|++
expr_stmt|;
block|}
if|else if
condition|(
name|nonEmptyInputs
operator|==
literal|0
condition|)
block|{
comment|// If the first input of Minus is empty, the whole thing is
comment|// empty.
break|break;
block|}
block|}
assert|assert
name|nonEmptyInputs
operator|<
name|inputs
operator|.
name|size
argument_list|()
operator|:
literal|"planner promised us at least one Empty child: "
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|minus
argument_list|)
assert|;
if|if
condition|(
name|nonEmptyInputs
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|push
argument_list|(
name|minus
argument_list|)
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|minus
argument_list|(
name|minus
operator|.
name|all
argument_list|,
name|nonEmptyInputs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|convert
argument_list|(
name|minus
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts a    * {@link org.apache.calcite.rel.logical.LogicalIntersect} to    * empty if any of its children are empty.    *    *<p>Examples:    *    *<ul>    *<li>Intersect(Rel, Empty, Rel2) becomes Empty    *<li>Intersect(Empty, Rel) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|INTERSECT_INSTANCE
init|=
operator|new
name|RelOptRule
argument_list|(
name|operand
argument_list|(
name|LogicalIntersect
operator|.
name|class
argument_list|,
name|unordered
argument_list|(
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"Intersect"
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
name|LogicalIntersect
name|intersect
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|intersect
argument_list|)
operator|.
name|empty
argument_list|()
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|isEmpty
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Values
condition|)
block|{
return|return
operator|(
operator|(
name|Values
operator|)
name|node
operator|)
operator|.
name|getTuples
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|HepRelVertex
condition|)
block|{
return|return
name|isEmpty
argument_list|(
operator|(
operator|(
name|HepRelVertex
operator|)
name|node
operator|)
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
comment|// Note: relation input might be a RelSubset, so we just iterate over the relations
comment|// in order to check if the subset is equivalent to an empty relation.
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|RelSubset
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelSubset
name|subset
init|=
operator|(
name|RelSubset
operator|)
name|node
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|subset
operator|.
name|getRels
argument_list|()
control|)
block|{
if|if
condition|(
name|isEmpty
argument_list|(
name|rel
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.logical.LogicalProject}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Project(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|PROJECT_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|Project
operator|.
name|class
argument_list|,
operator|(
name|Predicate
argument_list|<
name|Project
argument_list|>
operator|)
name|project
lambda|->
literal|true
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"PruneEmptyProject"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.logical.LogicalFilter}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Filter(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|FILTER_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
literal|"PruneEmptyFilter"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.core.Sort}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Sort(Empty) becomes Empty    *</ul>    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|SORT_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|Sort
operator|.
name|class
argument_list|,
literal|"PruneEmptySort"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.core.Sort}    * to empty if it has {@code LIMIT 0}.    *    *<p>Examples:    *    *<ul>    *<li>Sort(Empty) becomes Empty    *</ul>    */
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
name|Sort
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
name|Sort
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
operator|!
operator|(
name|sort
operator|.
name|fetch
operator|instanceof
name|RexDynamicParam
operator|)
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
name|call
operator|.
name|builder
argument_list|()
operator|.
name|push
argument_list|(
name|sort
argument_list|)
operator|.
name|empty
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts an {@link org.apache.calcite.rel.core.Aggregate}    * to empty if its child is empty.    *    *<p>Examples:    *    *<ul>    *<li>{@code Aggregate(key: [1, 3], Empty)}&rarr; {@code Empty}    *    *<li>{@code Aggregate(key: [], Empty)} is unchanged, because an aggregate    * without a GROUP BY key always returns 1 row, even over empty input    *</ul>    *    * @see AggregateValuesRule    */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|AGGREGATE_INSTANCE
init|=
operator|new
name|RemoveEmptySingleRule
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
operator|(
name|Predicate
argument_list|<
name|Aggregate
argument_list|>
operator|)
name|Aggregate
operator|::
name|isNotGrandTotal
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"PruneEmptyAggregate"
argument_list|)
decl_stmt|;
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.core.Join}    * to empty if its left child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Join(Empty, Scan(Dept), INNER) becomes Empty    *<li>Join(Empty, Scan(Dept), LEFT) becomes Empty    *<li>Join(Empty, Scan(Dept), SEMI) becomes Empty    *<li>Join(Empty, Scan(Dept), ANTI) becomes Empty    *</ul>    */
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
name|Join
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
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
name|call
operator|.
name|builder
argument_list|()
operator|.
name|push
argument_list|(
name|join
argument_list|)
operator|.
name|empty
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Rule that converts a {@link org.apache.calcite.rel.core.Join}    * to empty if its right child is empty.    *    *<p>Examples:    *    *<ul>    *<li>Join(Scan(Emp), Empty, INNER) becomes Empty    *<li>Join(Scan(Emp), Empty, RIGHT) becomes Empty    *<li>Join(Scan(Emp), Empty, SEMI) becomes Empty    *<li>Join(Scan(Emp), Empty, ANTI) becomes Scan(Emp)    *</ul>    */
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
name|Join
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
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
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
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|ANTI
condition|)
block|{
comment|// "select * from emp anti join dept" is not necessarily empty if dept is empty
if|if
condition|(
name|join
operator|.
name|analyzeCondition
argument_list|()
operator|.
name|isEqui
argument_list|()
condition|)
block|{
comment|// In case of anti (equi) join: Join(X, Empty, ANTI) becomes X
name|call
operator|.
name|transformTo
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|call
operator|.
name|builder
argument_list|()
operator|.
name|push
argument_list|(
name|join
argument_list|)
operator|.
name|empty
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/** Planner rule that converts a single-rel (e.g. project, sort, aggregate or    * filter) on top of the empty relational expression into empty. */
specifier|public
specifier|static
class|class
name|RemoveEmptySingleRule
extends|extends
name|RelOptRule
block|{
comment|/** Creates a simple RemoveEmptySingleRule. */
specifier|public
parameter_list|<
name|R
extends|extends
name|SingleRel
parameter_list|>
name|RemoveEmptySingleRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|(
name|Predicate
argument_list|<
name|R
argument_list|>
operator|)
name|project
lambda|->
literal|true
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a RemoveEmptySingleRule. */
specifier|public
parameter_list|<
name|R
extends|extends
name|SingleRel
parameter_list|>
name|RemoveEmptySingleRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|Predicate
argument_list|<
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|predicate
argument_list|,
name|operandJ
argument_list|(
name|Values
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|::
name|isEmpty
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
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
parameter_list|<
name|R
extends|extends
name|SingleRel
parameter_list|>
name|RemoveEmptySingleRule
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
name|R
argument_list|>
name|predicate
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|(
name|Predicate
argument_list|<
name|R
argument_list|>
operator|)
name|predicate
operator|::
name|apply
argument_list|,
name|relBuilderFactory
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
name|call
operator|.
name|builder
argument_list|()
operator|.
name|push
argument_list|(
name|single
argument_list|)
operator|.
name|empty
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

