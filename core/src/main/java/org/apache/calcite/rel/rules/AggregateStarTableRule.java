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
name|jdbc
operator|.
name|CalciteSchema
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
name|materialize
operator|.
name|Lattice
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
name|materialize
operator|.
name|TileKey
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
name|RelOptCluster
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
name|RelOptLattice
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
name|RelOptTable
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
name|prepare
operator|.
name|CalcitePrepareImpl
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
name|prepare
operator|.
name|RelOptTableImpl
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|StarTable
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
name|Pair
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
name|AbstractSourceMapping
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
name|List
import|;
end_import

begin_comment
comment|/**  * Planner rule that matches an {@link org.apache.calcite.rel.core.Aggregate} on  * top of a {@link org.apache.calcite.schema.impl.StarTable.StarTableScan}.  *  *<p>This pattern indicates that an aggregate table may exist. The rule asks  * the star table for an aggregate table at the required level of aggregation.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateStarTableRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateStarTableRule
name|INSTANCE
init|=
operator|new
name|AggregateStarTableRule
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|.
name|IS_SIMPLE
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|StarTable
operator|.
name|StarTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"AggregateStarTableRule"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|AggregateStarTableRule
name|INSTANCE2
init|=
operator|new
name|AggregateStarTableRule
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|.
name|IS_SIMPLE
argument_list|,
name|operand
argument_list|(
name|Project
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|StarTable
operator|.
name|StarTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"AggregateStarTableRule:project"
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
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|StarTable
operator|.
name|StarTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|AggregateProjectMergeRule
operator|.
name|apply
argument_list|(
name|call
argument_list|,
name|aggregate
argument_list|,
name|project
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
name|aggregate2
decl_stmt|;
specifier|final
name|Project
name|project2
decl_stmt|;
if|if
condition|(
name|rel
operator|instanceof
name|Aggregate
condition|)
block|{
name|project2
operator|=
literal|null
expr_stmt|;
name|aggregate2
operator|=
operator|(
name|Aggregate
operator|)
name|rel
expr_stmt|;
block|}
if|else if
condition|(
name|rel
operator|instanceof
name|Project
condition|)
block|{
name|project2
operator|=
operator|(
name|Project
operator|)
name|rel
expr_stmt|;
name|aggregate2
operator|=
operator|(
name|Aggregate
operator|)
name|project2
operator|.
name|getInput
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
name|apply
argument_list|(
name|call
argument_list|,
name|project2
argument_list|,
name|aggregate2
argument_list|,
name|scan
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
name|AggregateStarTableRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|description
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
name|StarTable
operator|.
name|StarTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|aggregate
argument_list|,
name|scan
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|apply
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Project
name|postProject
parameter_list|,
specifier|final
name|Aggregate
name|aggregate
parameter_list|,
name|StarTable
operator|.
name|StarTableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|scan
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|table
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|RelOptLattice
name|lattice
init|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|getLattice
argument_list|(
name|table
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measures
init|=
name|lattice
operator|.
name|lattice
operator|.
name|toMeasures
argument_list|(
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|CalciteSchema
operator|.
name|TableEntry
argument_list|,
name|TileKey
argument_list|>
name|pair
init|=
name|lattice
operator|.
name|getAggregate
argument_list|(
name|call
operator|.
name|getPlanner
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|measures
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
init|=
name|pair
operator|.
name|left
decl_stmt|;
specifier|final
name|TileKey
name|tileKey
init|=
name|pair
operator|.
name|right
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
name|double
name|rowCount
init|=
name|aggregate
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|aggregateTable
init|=
name|tableEntry
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|aggregateTableRowType
init|=
name|aggregateTable
operator|.
name|getRowType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelOptTable
name|aggregateRelOptTable
init|=
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|table
operator|.
name|getRelOptSchema
argument_list|()
argument_list|,
name|aggregateTableRowType
argument_list|,
name|tableEntry
argument_list|,
name|rowCount
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|aggregateRelOptTable
operator|.
name|toRel
argument_list|(
name|RelOptUtil
operator|.
name|getContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tileKey
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using materialization "
operator|+
name|aggregateRelOptTable
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|" (exact match)"
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
operator|!
name|tileKey
operator|.
name|dimensions
operator|.
name|equals
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
condition|)
block|{
comment|// Aggregate has finer granularity than we need. Roll up.
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using materialization "
operator|+
name|aggregateRelOptTable
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|", rolling up "
operator|+
name|tileKey
operator|.
name|dimensions
operator|+
literal|" to "
operator|+
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
name|tileKey
operator|.
name|dimensions
operator|.
name|contains
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
assert|;
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|ImmutableBitSet
operator|.
name|Builder
name|groupSet
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|aggregate
operator|.
name|getGroupSet
argument_list|()
control|)
block|{
name|groupSet
operator|.
name|set
argument_list|(
name|tileKey
operator|.
name|dimensions
operator|.
name|indexOf
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
specifier|final
name|AggregateCall
name|copy
init|=
name|rollUp
argument_list|(
name|groupSet
operator|.
name|cardinality
argument_list|()
argument_list|,
name|relBuilder
argument_list|,
name|aggCall
argument_list|,
name|tileKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|copy
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|aggCalls
operator|.
name|add
argument_list|(
name|copy
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|push
argument_list|(
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|false
argument_list|,
name|groupSet
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
name|aggCalls
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|tileKey
operator|.
name|measures
operator|.
name|equals
argument_list|(
name|measures
argument_list|)
condition|)
block|{
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using materialization "
operator|+
name|aggregateRelOptTable
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|", right granularity, but different measures "
operator|+
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
operator|new
name|AbstractSourceMapping
argument_list|(
name|tileKey
operator|.
name|dimensions
operator|.
name|cardinality
argument_list|()
operator|+
name|tileKey
operator|.
name|measures
operator|.
name|size
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
block|{
specifier|public
name|int
name|getSourceOpt
parameter_list|(
name|int
name|source
parameter_list|)
block|{
assert|assert
name|aggregate
operator|.
name|getIndicatorCount
argument_list|()
operator|==
literal|0
assert|;
if|if
condition|(
name|source
operator|<
name|aggregate
operator|.
name|getGroupCount
argument_list|()
condition|)
block|{
name|int
name|in
init|=
name|tileKey
operator|.
name|dimensions
operator|.
name|nth
argument_list|(
name|source
argument_list|)
decl_stmt|;
return|return
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|indexOf
argument_list|(
name|in
argument_list|)
return|;
block|}
name|Lattice
operator|.
name|Measure
name|measure
init|=
name|measures
operator|.
name|get
argument_list|(
name|source
operator|-
name|aggregate
operator|.
name|getGroupCount
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
name|tileKey
operator|.
name|measures
operator|.
name|indexOf
argument_list|(
name|measure
argument_list|)
decl_stmt|;
assert|assert
name|i
operator|>=
literal|0
assert|;
return|return
name|tileKey
operator|.
name|dimensions
operator|.
name|cardinality
argument_list|()
operator|+
name|i
return|;
block|}
block|}
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postProject
operator|!=
literal|null
condition|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|postProject
operator|.
name|copy
argument_list|(
name|postProject
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
argument_list|)
argument_list|)
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
specifier|private
specifier|static
name|AggregateCall
name|rollUp
parameter_list|(
name|int
name|groupCount
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|,
name|TileKey
name|tileKey
parameter_list|)
block|{
if|if
condition|(
name|aggregateCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SqlAggFunction
name|aggregation
init|=
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|SqlAggFunction
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|seek
init|=
name|Pair
operator|.
name|of
argument_list|(
name|aggregation
argument_list|,
name|aggregateCall
operator|.
name|getArgList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|tileKey
operator|.
name|dimensions
operator|.
name|cardinality
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measures
init|=
name|tileKey
operator|.
name|measures
decl_stmt|;
comment|// First, try to satisfy the aggregation by rolling up an aggregate in the
comment|// materialization.
specifier|final
name|int
name|i
init|=
name|find
argument_list|(
name|measures
argument_list|,
name|seek
argument_list|)
decl_stmt|;
name|tryRoll
label|:
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
specifier|final
name|SqlAggFunction
name|roll
init|=
name|SubstitutionVisitor
operator|.
name|getRollup
argument_list|(
name|aggregation
argument_list|)
decl_stmt|;
if|if
condition|(
name|roll
operator|==
literal|null
condition|)
block|{
break|break
name|tryRoll
break|;
block|}
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|roll
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|offset
operator|+
name|i
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|groupCount
argument_list|,
name|relBuilder
operator|.
name|peek
argument_list|()
argument_list|,
literal|null
argument_list|,
name|aggregateCall
operator|.
name|name
argument_list|)
return|;
block|}
comment|// Second, try to satisfy the aggregation based on group set columns.
name|tryGroup
label|:
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|newArgs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|arg
range|:
name|aggregateCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|int
name|z
init|=
name|tileKey
operator|.
name|dimensions
operator|.
name|indexOf
argument_list|(
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
name|z
operator|<
literal|0
condition|)
block|{
break|break
name|tryGroup
break|;
block|}
name|newArgs
operator|.
name|add
argument_list|(
name|z
argument_list|)
expr_stmt|;
block|}
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|aggregation
argument_list|,
literal|false
argument_list|,
name|newArgs
argument_list|,
operator|-
literal|1
argument_list|,
name|groupCount
argument_list|,
name|relBuilder
operator|.
name|peek
argument_list|()
argument_list|,
literal|null
argument_list|,
name|aggregateCall
operator|.
name|name
argument_list|)
return|;
block|}
comment|// No roll up possible.
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|int
name|find
parameter_list|(
name|ImmutableList
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measures
parameter_list|,
name|Pair
argument_list|<
name|SqlAggFunction
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|seek
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|measures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Lattice
operator|.
name|Measure
name|measure
init|=
name|measures
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|measure
operator|.
name|agg
operator|.
name|equals
argument_list|(
name|seek
operator|.
name|left
argument_list|)
operator|&&
name|measure
operator|.
name|argOrdinals
argument_list|()
operator|.
name|equals
argument_list|(
name|seek
operator|.
name|right
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateStarTableRule.java
end_comment

end_unit

