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
name|linq4j
operator|.
name|Ord
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
name|logical
operator|.
name|LogicalAggregate
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
name|sql
operator|.
name|SqlSplittableAggFunction
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
name|Mapping
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
name|base
operator|.
name|Function
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
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes an  * {@link org.apache.calcite.rel.core.Aggregate}  * past a {@link org.apache.calcite.rel.core.Join}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateJoinTransposeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateJoinTransposeRule
name|INSTANCE
init|=
operator|new
name|AggregateJoinTransposeRule
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/** Extended instance of the rule that can push down aggregate functions. */
specifier|public
specifier|static
specifier|final
name|AggregateJoinTransposeRule
name|EXTENDED
init|=
operator|new
name|AggregateJoinTransposeRule
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|allowFunctions
decl_stmt|;
comment|/** Creates an AggregateJoinTransposeRule. */
specifier|public
name|AggregateJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|boolean
name|allowFunctions
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|aggregateClass
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|.
name|IS_SIMPLE
argument_list|,
name|operand
argument_list|(
name|joinClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowFunctions
operator|=
name|allowFunctions
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelFactories
operator|.
name|AggregateFactory
name|aggregateFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|joinClass
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|aggregateFactory
argument_list|,
name|joinFactory
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
name|AggregateJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelFactories
operator|.
name|AggregateFactory
name|aggregateFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|,
name|boolean
name|allowFunctions
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|joinClass
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|aggregateFactory
argument_list|,
name|joinFactory
argument_list|)
argument_list|,
name|allowFunctions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelFactories
operator|.
name|AggregateFactory
name|aggregateFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|joinClass
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|aggregateFactory
argument_list|,
name|joinFactory
argument_list|,
name|projectFactory
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
name|AggregateJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelFactories
operator|.
name|AggregateFactory
name|aggregateFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|,
name|boolean
name|allowFunctions
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|joinClass
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|aggregateFactory
argument_list|,
name|joinFactory
argument_list|,
name|projectFactory
argument_list|)
argument_list|,
name|allowFunctions
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
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|aggregate
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
comment|// If any aggregate functions do not support splitting, bail out
comment|// If any aggregate call has a filter, bail out
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
if|if
condition|(
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|unwrap
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|aggregateCall
operator|.
name|filterArg
operator|>=
literal|0
condition|)
block|{
return|return;
block|}
block|}
comment|// If it is not an inner join, we do not push the
comment|// aggregate operator
if|if
condition|(
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
return|return;
block|}
if|if
condition|(
operator|!
name|allowFunctions
operator|&&
operator|!
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Do the columns used by the join appear in the output of the aggregate?
specifier|final
name|ImmutableBitSet
name|aggregateColumns
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|keyColumns
init|=
name|keyColumns
argument_list|(
name|aggregateColumns
argument_list|,
name|RelMetadataQuery
operator|.
name|getPulledUpPredicates
argument_list|(
name|join
argument_list|)
operator|.
name|pulledUpPredicates
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|joinColumns
init|=
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|allColumnsInAggregate
init|=
name|keyColumns
operator|.
name|contains
argument_list|(
name|joinColumns
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|belowAggregateColumns
init|=
name|aggregateColumns
operator|.
name|union
argument_list|(
name|joinColumns
argument_list|)
decl_stmt|;
comment|// Split join condition
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|RexNode
name|nonEquiConj
init|=
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
comment|// If it contains non-equi join conditions, we bail out
if|if
condition|(
operator|!
name|nonEquiConj
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Push each aggregate function down to each side that contains all of its
comment|// arguments. Note that COUNT(*), because it has no arguments, can go to
comment|// both sides.
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Side
argument_list|>
name|sides
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|uniqueCount
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|int
name|belowOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
literal|2
condition|;
name|s
operator|++
control|)
block|{
specifier|final
name|Side
name|side
init|=
operator|new
name|Side
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|joinInput
init|=
name|join
operator|.
name|getInput
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|int
name|fieldCount
init|=
name|joinInput
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|fieldSet
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|belowAggregateKeyNotShifted
init|=
name|belowAggregateColumns
operator|.
name|intersect
argument_list|(
name|fieldSet
argument_list|)
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|Integer
argument_list|>
name|c
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|belowAggregateKeyNotShifted
argument_list|)
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|c
operator|.
name|e
argument_list|,
name|belowOffset
operator|+
name|c
operator|.
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ImmutableBitSet
name|belowAggregateKey
init|=
name|belowAggregateKeyNotShifted
operator|.
name|shift
argument_list|(
operator|-
name|offset
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|unique
decl_stmt|;
if|if
condition|(
operator|!
name|allowFunctions
condition|)
block|{
assert|assert
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
assert|;
comment|// If there are no functions, it doesn't matter as much whether we
comment|// aggregate the inputs before the join, because there will not be
comment|// any functions experiencing a cartesian product effect.
comment|//
comment|// But finding out whether the input is already unique requires a call
comment|// to areColumnsUnique that currently (until [CALCITE-794] "Detect
comment|// cycles when computing statistics" is fixed) places a heavy load on
comment|// the metadata system.
comment|//
comment|// So we choose to imagine the the input is already unique, which is
comment|// untrue but harmless.
comment|//
name|unique
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Boolean
name|unique0
init|=
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|joinInput
argument_list|,
name|belowAggregateKey
argument_list|)
decl_stmt|;
name|unique
operator|=
name|unique0
operator|!=
literal|null
operator|&&
name|unique0
expr_stmt|;
block|}
if|if
condition|(
name|unique
condition|)
block|{
operator|++
name|uniqueCount
expr_stmt|;
name|side
operator|.
name|aggregate
operator|=
literal|false
expr_stmt|;
name|side
operator|.
name|newInput
operator|=
name|joinInput
expr_stmt|;
block|}
else|else
block|{
name|side
operator|.
name|aggregate
operator|=
literal|true
expr_stmt|;
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|belowAggCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlSplittableAggFunction
operator|.
name|Registry
argument_list|<
name|AggregateCall
argument_list|>
name|belowAggCallRegistry
init|=
name|registry
argument_list|(
name|belowAggCalls
argument_list|)
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|s
operator|==
literal|0
condition|?
name|Mappings
operator|.
name|createIdentity
argument_list|(
name|fieldCount
argument_list|)
else|:
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|fieldCount
operator|+
name|offset
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|,
name|fieldCount
argument_list|)
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|AggregateCall
argument_list|>
name|aggCall
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|SqlAggFunction
name|aggregation
init|=
name|aggCall
operator|.
name|e
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
specifier|final
name|SqlSplittableAggFunction
name|splitter
init|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|aggregation
operator|.
name|unwrap
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AggregateCall
name|call1
decl_stmt|;
if|if
condition|(
name|fieldSet
operator|.
name|contains
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|aggCall
operator|.
name|e
operator|.
name|getArgList
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|call1
operator|=
name|splitter
operator|.
name|split
argument_list|(
name|aggCall
operator|.
name|e
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call1
operator|=
name|splitter
operator|.
name|other
argument_list|(
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|aggCall
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|call1
operator|!=
literal|null
condition|)
block|{
name|side
operator|.
name|split
operator|.
name|put
argument_list|(
name|aggCall
operator|.
name|i
argument_list|,
name|belowAggregateKey
operator|.
name|cardinality
argument_list|()
operator|+
name|belowAggCallRegistry
operator|.
name|register
argument_list|(
name|call1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|side
operator|.
name|newInput
operator|=
name|relBuilder
operator|.
name|push
argument_list|(
name|joinInput
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|belowAggregateKey
argument_list|,
literal|null
argument_list|)
argument_list|,
name|belowAggCalls
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|offset
operator|+=
name|fieldCount
expr_stmt|;
name|belowOffset
operator|+=
name|side
operator|.
name|newInput
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
name|sides
operator|.
name|add
argument_list|(
name|side
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uniqueCount
operator|==
literal|2
condition|)
block|{
comment|// Both inputs to the join are unique. There is nothing to be gained by
comment|// this rule. In fact, this aggregate+join may be the result of a previous
comment|// invocation of this rule; if we continue we might loop forever.
return|return;
block|}
comment|// Update condition
specifier|final
name|Mapping
name|mapping
init|=
operator|(
name|Mapping
operator|)
name|Mappings
operator|.
name|target
argument_list|(
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Integer
name|a0
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|belowOffset
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
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create new join
name|relBuilder
operator|.
name|push
argument_list|(
name|sides
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|newInput
argument_list|)
operator|.
name|push
argument_list|(
name|sides
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|newInput
argument_list|)
operator|.
name|join
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|newCondition
argument_list|)
expr_stmt|;
comment|// Aggregate above to sum up the sub-totals
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|groupIndicatorCount
init|=
name|aggregate
operator|.
name|getGroupCount
argument_list|()
operator|+
name|aggregate
operator|.
name|getIndicatorCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|newLeftWidth
init|=
name|sides
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|newInput
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|rexBuilder
operator|.
name|identityProjects
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
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|AggregateCall
argument_list|>
name|aggCall
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|SqlAggFunction
name|aggregation
init|=
name|aggCall
operator|.
name|e
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
specifier|final
name|SqlSplittableAggFunction
name|splitter
init|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|aggregation
operator|.
name|unwrap
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|leftSubTotal
init|=
name|sides
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|split
operator|.
name|get
argument_list|(
name|aggCall
operator|.
name|i
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|rightSubTotal
init|=
name|sides
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|split
operator|.
name|get
argument_list|(
name|aggCall
operator|.
name|i
argument_list|)
decl_stmt|;
name|newAggCalls
operator|.
name|add
argument_list|(
name|splitter
operator|.
name|topSplit
argument_list|(
name|rexBuilder
argument_list|,
name|registry
argument_list|(
name|projects
argument_list|)
argument_list|,
name|groupIndicatorCount
argument_list|,
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|aggCall
operator|.
name|e
argument_list|,
name|leftSubTotal
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|leftSubTotal
argument_list|,
name|rightSubTotal
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|rightSubTotal
operator|+
name|newLeftWidth
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|b
label|:
if|if
condition|(
name|allColumnsInAggregate
operator|&&
name|newAggCalls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no need to aggregate
block|}
else|else
block|{
name|relBuilder
operator|.
name|project
argument_list|(
name|projects
argument_list|)
expr_stmt|;
if|if
condition|(
name|allColumnsInAggregate
condition|)
block|{
comment|// let's see if we can convert
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
control|)
block|{
name|projects2
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AggregateCall
name|newAggCall
range|:
name|newAggCalls
control|)
block|{
specifier|final
name|SqlSplittableAggFunction
name|splitter
init|=
name|newAggCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|unwrap
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitter
operator|!=
literal|null
condition|)
block|{
name|projects2
operator|.
name|add
argument_list|(
name|splitter
operator|.
name|singleton
argument_list|(
name|rexBuilder
argument_list|,
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|newAggCall
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|projects2
operator|.
name|size
argument_list|()
operator|==
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|cardinality
argument_list|()
operator|+
name|newAggCalls
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// We successfully converted agg calls into projects.
name|relBuilder
operator|.
name|project
argument_list|(
name|projects2
argument_list|)
expr_stmt|;
break|break
name|b
break|;
block|}
block|}
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|,
name|Mappings
operator|.
name|apply2
argument_list|(
name|mapping
argument_list|,
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|newAggCalls
argument_list|)
expr_stmt|;
block|}
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
comment|/** Computes the closure of a set of columns according to a given list of    * constraints. Each 'x = y' constraint causes bit y to be set if bit x is    * set, and vice versa. */
specifier|private
specifier|static
name|ImmutableBitSet
name|keyColumns
parameter_list|(
name|ImmutableBitSet
name|aggregateColumns
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|predicates
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|pred
range|:
name|predicates
control|)
block|{
name|populateEquivalences
argument_list|(
name|equivalence
argument_list|,
name|pred
argument_list|)
expr_stmt|;
block|}
name|ImmutableBitSet
name|keyColumns
init|=
name|aggregateColumns
decl_stmt|;
for|for
control|(
name|Integer
name|aggregateColumn
range|:
name|aggregateColumns
control|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
name|equivalence
operator|.
name|get
argument_list|(
name|aggregateColumn
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitSet
operator|!=
literal|null
condition|)
block|{
name|keyColumns
operator|=
name|keyColumns
operator|.
name|union
argument_list|(
name|bitSet
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|keyColumns
return|;
block|}
specifier|private
specifier|static
name|void
name|populateEquivalences
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
switch|switch
condition|(
name|predicate
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EQUALS
case|:
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|predicate
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperands
argument_list|()
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|RexInputRef
condition|)
block|{
specifier|final
name|RexInputRef
name|ref0
init|=
operator|(
name|RexInputRef
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|RexInputRef
condition|)
block|{
specifier|final
name|RexInputRef
name|ref1
init|=
operator|(
name|RexInputRef
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|populateEquivalence
argument_list|(
name|equivalence
argument_list|,
name|ref0
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ref1
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|populateEquivalence
argument_list|(
name|equivalence
argument_list|,
name|ref1
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ref0
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|populateEquivalence
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|BitSet
argument_list|>
name|equivalence
parameter_list|,
name|int
name|i0
parameter_list|,
name|int
name|i1
parameter_list|)
block|{
name|BitSet
name|bitSet
init|=
name|equivalence
operator|.
name|get
argument_list|(
name|i0
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitSet
operator|==
literal|null
condition|)
block|{
name|bitSet
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
name|equivalence
operator|.
name|put
argument_list|(
name|i0
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
block|}
name|bitSet
operator|.
name|set
argument_list|(
name|i1
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link org.apache.calcite.sql.SqlSplittableAggFunction.Registry}    * that is a view of a list. */
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|SqlSplittableAggFunction
operator|.
name|Registry
argument_list|<
name|E
argument_list|>
name|registry
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|SqlSplittableAggFunction
operator|.
name|Registry
argument_list|<
name|E
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|register
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|int
name|i
init|=
name|list
operator|.
name|indexOf
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
block|}
return|;
block|}
comment|/** Work space for an input to a join. */
specifier|private
specifier|static
class|class
name|Side
block|{
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|split
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|RelNode
name|newInput
decl_stmt|;
name|boolean
name|aggregate
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateJoinTransposeRule.java
end_comment

end_unit

