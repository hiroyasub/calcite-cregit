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
name|plan
operator|.
name|SubstitutionVisitor
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
name|Aggregate
operator|.
name|Group
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
name|AggregateCall
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|sql
operator|.
name|SqlAggFunction
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
name|ImmutableBitSet
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
name|mapping
operator|.
name|Mappings
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

begin_comment
comment|/**  * Planner rule that matches an {@link org.apache.calcite.rel.core.Aggregate}  * on a {@link org.apache.calcite.rel.core.Filter} and transposes them,  * pushing the aggregate below the filter.  *  *<p>In some cases, it is necessary to split the aggregate.  *  *<p>This rule does not directly improve performance. The aggregate will  * have to process more rows, to produce aggregated rows that will be thrown  * away. The rule might be beneficial if the predicate is very expensive to  * evaluate. The main use of the rule is to match a query that has a filter  * under an aggregate to an existing aggregate table.  *  * @see org.apache.calcite.rel.rules.FilterAggregateTransposeRule  */
end_comment

begin_class
specifier|public
class|class
name|AggregateFilterTransposeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateFilterTransposeRule
name|INSTANCE
init|=
operator|new
name|AggregateFilterTransposeRule
argument_list|()
decl_stmt|;
specifier|private
name|AggregateFilterTransposeRule
parameter_list|()
block|{
name|this
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an AggregateFilterTransposeRule. */
specifier|public
name|AggregateFilterTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
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
specifier|final
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Do the columns used by the filter appear in the output of the aggregate?
specifier|final
name|ImmutableBitSet
name|filterColumns
init|=
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|newGroupSet
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|union
argument_list|(
name|filterColumns
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|filter
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|Boolean
name|unique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|input
argument_list|,
name|newGroupSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
operator|!=
literal|null
operator|&&
name|unique
condition|)
block|{
comment|// The input is already unique on the grouping columns, so there's little
comment|// advantage of aggregating again. More important, without this check,
comment|// the rule fires forever: A-F => A-F-A => A-A-F-A => A-A-A-F-A => ...
return|return;
block|}
name|boolean
name|allColumnsInAggregate
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|contains
argument_list|(
name|filterColumns
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
name|newAggregate
init|=
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
argument_list|,
name|newGroupSet
argument_list|,
literal|null
argument_list|,
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|target
argument_list|(
name|newGroupSet
operator|::
name|indexOf
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|newGroupSet
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|newCondition
init|=
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|newFilter
init|=
name|filter
operator|.
name|copy
argument_list|(
name|filter
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newAggregate
argument_list|,
name|newCondition
argument_list|)
decl_stmt|;
if|if
condition|(
name|allColumnsInAggregate
operator|&&
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|==
name|Group
operator|.
name|SIMPLE
condition|)
block|{
comment|// Everything needed by the filter is returned by the aggregate.
assert|assert
name|newGroupSet
operator|.
name|equals
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
assert|;
name|call
operator|.
name|transformTo
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If aggregate uses grouping sets, we always need to split it.
comment|// Otherwise, it means that grouping sets are not used, but the
comment|// filter needs at least one extra column, and now aggregate it away.
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|topGroupSet
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|c
range|:
name|aggregate
operator|.
name|getGroupSet
argument_list|()
control|)
block|{
name|topGroupSet
operator|.
name|set
argument_list|(
name|newGroupSet
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|newGroupingSets
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|!=
name|Group
operator|.
name|SIMPLE
condition|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableBitSet
argument_list|>
name|newGroupingSetsBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|groupingSet
range|:
name|aggregate
operator|.
name|getGroupSets
argument_list|()
control|)
block|{
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|newGroupingSet
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|c
range|:
name|groupingSet
control|)
block|{
name|newGroupingSet
operator|.
name|set
argument_list|(
name|newGroupSet
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newGroupingSetsBuilder
operator|.
name|add
argument_list|(
name|newGroupingSet
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newGroupingSets
operator|=
name|newGroupingSetsBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|topAggCallList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|newGroupSet
operator|.
name|cardinality
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggregateCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
specifier|final
name|SqlAggFunction
name|rollup
init|=
name|SubstitutionVisitor
operator|.
name|getRollup
argument_list|(
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rollup
operator|==
literal|null
condition|)
block|{
comment|// This aggregate cannot be rolled up.
return|return;
block|}
if|if
condition|(
name|aggregateCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
comment|// Cannot roll up distinct.
return|return;
block|}
name|topAggCallList
operator|.
name|add
argument_list|(
name|AggregateCall
operator|.
name|create
argument_list|(
name|rollup
argument_list|,
name|aggregateCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|aggregateCall
operator|.
name|isApproximate
argument_list|()
argument_list|,
name|aggregateCall
operator|.
name|ignoreNulls
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|i
operator|++
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|aggregateCall
operator|.
name|collation
argument_list|,
name|aggregateCall
operator|.
name|type
argument_list|,
name|aggregateCall
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Aggregate
name|topAggregate
init|=
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newFilter
argument_list|,
name|topGroupSet
operator|.
name|build
argument_list|()
argument_list|,
name|newGroupingSets
argument_list|,
name|topAggCallList
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|topAggregate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AggregateFilterTransposeRule.java
end_comment

end_unit

