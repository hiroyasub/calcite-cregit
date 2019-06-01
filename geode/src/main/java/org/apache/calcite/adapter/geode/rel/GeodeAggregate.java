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
name|adapter
operator|.
name|geode
operator|.
name|rel
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelTraitSet
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
name|ImmutableMap
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
name|ImmutableMap
operator|.
name|Builder
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
comment|/**  * Implementation of  * {@link org.apache.calcite.rel.core.Aggregate} relational expression  * in Geode.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeAggregate
extends|extends
name|Aggregate
implements|implements
name|GeodeRel
block|{
comment|/** Creates a GeodeAggregate. */
specifier|public
name|GeodeAggregate
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|GeodeRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|this
operator|.
name|input
operator|.
name|getConvention
argument_list|()
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|input
operator|.
name|getConvention
argument_list|()
assert|;
assert|assert
name|this
operator|.
name|groupSets
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|:
literal|"Grouping sets not supported"
assert|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
if|if
condition|(
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DISTINCT based aggregation!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|GeodeAggregate
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
name|checkIndicator
argument_list|(
name|indicator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Aggregate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
return|return
operator|new
name|GeodeAggregate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|0.1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|GeodeImplementContext
name|geodeImplementContext
parameter_list|)
block|{
name|geodeImplementContext
operator|.
name|visitChild
argument_list|(
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|inputFields
init|=
name|fieldNames
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groupByFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|group
range|:
name|groupSet
control|)
block|{
name|groupByFields
operator|.
name|add
argument_list|(
name|inputFields
operator|.
name|get
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|geodeImplementContext
operator|.
name|addGroupBy
argument_list|(
name|groupByFields
argument_list|)
expr_stmt|;
comment|// Find the aggregate functions (e.g. MAX, SUM ...)
name|Builder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aggregateFunctionMap
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|aggCallFieldNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|aggCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|aggCallFieldNames
operator|.
name|add
argument_list|(
name|inputFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|functionName
init|=
name|aggCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Workaround to handle count(*) case. Geode doesn't allow "AS" aliases on
comment|// 'count(*)' but allows it for count('any column name'). So we are
comment|// converting the count(*) into count (first input ColumnName).
if|if
condition|(
literal|"COUNT"
operator|.
name|equalsIgnoreCase
argument_list|(
name|functionName
argument_list|)
operator|&&
name|aggCallFieldNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|aggCallFieldNames
operator|.
name|add
argument_list|(
name|inputFields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|oqlAggregateCall
init|=
name|Util
operator|.
name|toString
argument_list|(
name|aggCallFieldNames
argument_list|,
name|functionName
operator|+
literal|"("
argument_list|,
literal|", "
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|aggregateFunctionMap
operator|.
name|put
argument_list|(
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|,
name|oqlAggregateCall
argument_list|)
expr_stmt|;
block|}
name|geodeImplementContext
operator|.
name|addAggregateFunctions
argument_list|(
name|aggregateFunctionMap
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|(
name|RelDataType
name|relDataType
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|rdtf
range|:
name|relDataType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|rdtf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeAggregate.java
end_comment

end_unit

