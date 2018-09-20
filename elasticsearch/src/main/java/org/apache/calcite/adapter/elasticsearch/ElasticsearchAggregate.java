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
name|elasticsearch
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
name|InvalidRelException
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
name|sql
operator|.
name|SqlKind
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
name|EnumSet
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Implementation of  * {@link org.apache.calcite.rel.core.Aggregate} relational expression  * for ElasticSearch.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchAggregate
extends|extends
name|Aggregate
implements|implements
name|ElasticsearchRel
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|SUPPORTED_AGGREGATIONS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|SqlKind
operator|.
name|COUNT
argument_list|,
name|SqlKind
operator|.
name|MAX
argument_list|,
name|SqlKind
operator|.
name|MIN
argument_list|,
name|SqlKind
operator|.
name|AVG
argument_list|,
name|SqlKind
operator|.
name|SUM
argument_list|)
decl_stmt|;
comment|/** Creates a ElasticsearchAggregate */
name|ElasticsearchAggregate
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
throws|throws
name|InvalidRelException
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
if|if
condition|(
name|getConvention
argument_list|()
operator|!=
name|input
operator|.
name|getConvention
argument_list|()
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s != %s"
argument_list|,
name|getConvention
argument_list|()
argument_list|,
name|input
operator|.
name|getConvention
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|message
argument_list|)
throw|;
block|}
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
name|getConvention
argument_list|()
operator|==
name|ElasticsearchRel
operator|.
name|CONVENTION
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
throw|throw
operator|new
name|InvalidRelException
argument_list|(
literal|"distinct aggregation not supported"
argument_list|)
throw|;
block|}
name|SqlKind
name|kind
init|=
name|aggCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getKind
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|SUPPORTED_AGGREGATIONS
operator|.
name|contains
argument_list|(
name|kind
argument_list|)
condition|)
block|{
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Aggregation %s not supported (use one of %s)"
argument_list|,
name|kind
argument_list|,
name|SUPPORTED_AGGREGATIONS
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|InvalidRelException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|getGroupType
argument_list|()
operator|!=
name|Group
operator|.
name|SIMPLE
condition|)
block|{
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Only %s grouping is supported. "
operator|+
literal|"Yours is %s"
argument_list|,
name|Group
operator|.
name|SIMPLE
argument_list|,
name|getGroupType
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|InvalidRelException
argument_list|(
name|message
argument_list|)
throw|;
block|}
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
try|try
block|{
return|return
operator|new
name|ElasticsearchAggregate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
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
for|for
control|(
name|int
name|group
range|:
name|groupSet
control|)
block|{
name|implementor
operator|.
name|addGroupBy
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
name|names
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
name|names
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
specifier|final
name|String
name|name
init|=
name|names
operator|.
name|isEmpty
argument_list|()
condition|?
name|ElasticsearchConstants
operator|.
name|ID
else|:
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|op
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"\"%s\":{\"field\": \"%s\"}"
argument_list|,
name|toElasticAggregate
argument_list|(
name|aggCall
argument_list|)
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|implementor
operator|.
name|addAggregation
argument_list|(
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Most of the aggregations can be retrieved with single    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-stats-aggregation.html">stats</a>    * function. But currently only one-to-one mapping is supported between sql agg and elastic    * aggregation.    */
specifier|private
name|String
name|toElasticAggregate
parameter_list|(
name|AggregateCall
name|call
parameter_list|)
block|{
name|SqlKind
name|kind
init|=
name|call
operator|.
name|getAggregation
argument_list|()
operator|.
name|getKind
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|COUNT
case|:
return|return
name|call
operator|.
name|isApproximate
argument_list|()
condition|?
literal|"cardinality"
else|:
literal|"value_count"
return|;
case|case
name|SUM
case|:
return|return
literal|"sum"
return|;
case|case
name|MIN
case|:
return|return
literal|"min"
return|;
case|case
name|MAX
case|:
return|return
literal|"max"
return|;
case|case
name|AVG
case|:
return|return
literal|"avg"
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown aggregation kind "
operator|+
name|kind
operator|+
literal|" for "
operator|+
name|call
argument_list|)
throw|;
block|}
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
name|List
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
comment|// End ElasticsearchAggregate.java
end_comment

end_unit
