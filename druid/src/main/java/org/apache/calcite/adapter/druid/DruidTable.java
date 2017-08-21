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
name|druid
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|interpreter
operator|.
name|BindableConvention
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
name|TableScan
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
name|LogicalTableScan
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
name|RelDataTypeFactory
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
name|RelProtoDataType
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
name|TranslatableTable
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
name|AbstractTable
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
name|SqlCall
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
name|sql
operator|.
name|SqlNode
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
name|SqlSelectKeyword
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
name|type
operator|.
name|SqlTypeName
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
name|ImmutableSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Table mapped onto a Druid table.  */
end_comment

begin_class
specifier|public
class|class
name|DruidTable
extends|extends
name|AbstractTable
implements|implements
name|TranslatableTable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TIMESTAMP_COLUMN
init|=
literal|"__time"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|LocalInterval
name|DEFAULT_INTERVAL
init|=
name|LocalInterval
operator|.
name|create
argument_list|(
literal|"1900-01-01"
argument_list|,
literal|"3000-01-01"
argument_list|)
decl_stmt|;
specifier|final
name|DruidSchema
name|schema
decl_stmt|;
specifier|final
name|String
name|dataSource
decl_stmt|;
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|metricFieldNames
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|LocalInterval
argument_list|>
name|intervals
decl_stmt|;
specifier|final
name|String
name|timestampFieldName
decl_stmt|;
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ComplexMetric
argument_list|>
argument_list|>
name|complexMetrics
decl_stmt|;
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|allFields
decl_stmt|;
comment|/**    * Creates a Druid table.    *    * @param schema Druid schema that contains this table    * @param dataSource Druid data source name    * @param protoRowType Field names and types    * @param metricFieldNames Names of fields that are metrics    * @param intervals Default interval if query does not constrain the time, or null    * @param timestampFieldName Name of the column that contains the time    */
specifier|public
name|DruidTable
parameter_list|(
name|DruidSchema
name|schema
parameter_list|,
name|String
name|dataSource
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricFieldNames
parameter_list|,
name|String
name|timestampFieldName
parameter_list|,
name|List
argument_list|<
name|LocalInterval
argument_list|>
name|intervals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ComplexMetric
argument_list|>
argument_list|>
name|complexMetrics
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|allFields
parameter_list|)
block|{
name|this
operator|.
name|timestampFieldName
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|timestampFieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataSource
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|protoRowType
expr_stmt|;
name|this
operator|.
name|metricFieldNames
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|metricFieldNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|intervals
operator|=
name|intervals
operator|!=
literal|null
condition|?
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|intervals
argument_list|)
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|DEFAULT_INTERVAL
argument_list|)
expr_stmt|;
name|this
operator|.
name|complexMetrics
operator|=
name|complexMetrics
operator|==
literal|null
condition|?
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|List
argument_list|<
name|ComplexMetric
argument_list|>
operator|>
name|of
argument_list|()
operator|:
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|complexMetrics
argument_list|)
expr_stmt|;
name|this
operator|.
name|allFields
operator|=
name|allFields
operator|==
literal|null
condition|?
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|SqlTypeName
operator|>
name|of
argument_list|()
operator|:
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|allFields
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link DruidTable}    *    * @param druidSchema Druid schema    * @param dataSourceName Data source name in Druid, also table name    * @param intervals Intervals, or null to use default    * @param fieldMap Mutable map of fields (dimensions plus metrics);    *        may be partially populated already    * @param metricNameSet Mutable set of metric names;    *        may be partially populated already    * @param timestampColumnName Name of timestamp column, or null    * @param connection connection used to find column definitions. Must be non-null.    *    * @return A table    */
specifier|static
name|Table
name|create
parameter_list|(
name|DruidSchema
name|druidSchema
parameter_list|,
name|String
name|dataSourceName
parameter_list|,
name|List
argument_list|<
name|LocalInterval
argument_list|>
name|intervals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fieldMap
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricNameSet
parameter_list|,
name|String
name|timestampColumnName
parameter_list|,
name|DruidConnectionImpl
name|connection
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ComplexMetric
argument_list|>
argument_list|>
name|complexMetrics
parameter_list|)
block|{
assert|assert
name|connection
operator|!=
literal|null
assert|;
name|connection
operator|.
name|metadata
argument_list|(
name|dataSourceName
argument_list|,
name|timestampColumnName
argument_list|,
name|intervals
argument_list|,
name|fieldMap
argument_list|,
name|metricNameSet
argument_list|,
name|complexMetrics
argument_list|)
expr_stmt|;
return|return
name|DruidTable
operator|.
name|create
argument_list|(
name|druidSchema
argument_list|,
name|dataSourceName
argument_list|,
name|intervals
argument_list|,
name|fieldMap
argument_list|,
name|metricNameSet
argument_list|,
name|timestampColumnName
argument_list|,
name|complexMetrics
argument_list|)
return|;
block|}
comment|/** Creates a {@link DruidTable}    *    * @param druidSchema Druid schema    * @param dataSourceName Data source name in Druid, also table name    * @param intervals Intervals, or null to use default    * @param fieldMap Mutable map of fields (dimensions plus metrics);    *        may be partially populated already    * @param metricNameSet Mutable set of metric names;    *        may be partially populated already    * @param timestampColumnName Name of timestamp column, or null    * @return A table    */
specifier|static
name|Table
name|create
parameter_list|(
name|DruidSchema
name|druidSchema
parameter_list|,
name|String
name|dataSourceName
parameter_list|,
name|List
argument_list|<
name|LocalInterval
argument_list|>
name|intervals
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fieldMap
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricNameSet
parameter_list|,
name|String
name|timestampColumnName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ComplexMetric
argument_list|>
argument_list|>
name|complexMetrics
parameter_list|)
block|{
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fields
init|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|fieldMap
argument_list|)
decl_stmt|;
return|return
operator|new
name|DruidTable
argument_list|(
name|druidSchema
argument_list|,
name|dataSourceName
argument_list|,
operator|new
name|MapRelProtoDataType
argument_list|(
name|fields
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|metricNameSet
argument_list|)
argument_list|,
name|timestampColumnName
argument_list|,
name|intervals
argument_list|,
name|complexMetrics
argument_list|,
name|fieldMap
argument_list|)
return|;
block|}
comment|/**    * Returns the appropriate {@link ComplexMetric} that is mapped from the given<code>alias</code>    * if it exists, and is used in the expected context with the given {@link AggregateCall}.    * Otherwise returns<code>null</code>.    * */
specifier|public
name|ComplexMetric
name|resolveComplexMetric
parameter_list|(
name|String
name|alias
parameter_list|,
name|AggregateCall
name|call
parameter_list|)
block|{
name|List
argument_list|<
name|ComplexMetric
argument_list|>
name|potentialMetrics
init|=
name|getComplexMetricsFrom
argument_list|(
name|alias
argument_list|)
decl_stmt|;
comment|// It's possible that multiple complex metrics match the AggregateCall,
comment|// but for now we only return the first that matches
for|for
control|(
name|ComplexMetric
name|complexMetric
range|:
name|potentialMetrics
control|)
block|{
if|if
condition|(
name|complexMetric
operator|.
name|canBeUsed
argument_list|(
name|call
argument_list|)
condition|)
block|{
return|return
name|complexMetric
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
comment|// The only rolled up columns we care about are Complex Metrics (aka sketches).
comment|// But we also need to check if this column name is a dimension
return|return
name|complexMetrics
operator|.
name|get
argument_list|(
name|column
argument_list|)
operator|!=
literal|null
operator|&&
name|allFields
operator|.
name|get
argument_list|(
name|column
argument_list|)
operator|!=
name|SqlTypeName
operator|.
name|VARCHAR
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
assert|assert
name|isRolledUp
argument_list|(
name|column
argument_list|)
assert|;
comment|// Our rolled up columns are only allowed in COUNT(DISTINCT ...) aggregate functions.
comment|// We only allow this when approximate results are acceptable.
return|return
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|approximateDistinctCount
argument_list|()
operator|&&
name|isCountDistinct
argument_list|(
name|call
argument_list|)
operator|&&
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
comment|// for COUNT(a_1, a_2, ... a_n). n should be 1
operator|&&
name|isValidParentKind
argument_list|(
name|parent
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isValidParentKind
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
return|return
name|node
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|SELECT
operator|||
name|node
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|FILTER
operator|||
name|isSupportedPostAggOperation
argument_list|(
name|node
operator|.
name|getKind
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isCountDistinct
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|COUNT
operator|&&
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
operator|!=
literal|null
operator|&&
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
operator|.
name|getValue
argument_list|()
operator|==
name|SqlSelectKeyword
operator|.
name|DISTINCT
return|;
block|}
comment|// Post aggs support +, -, /, * so we should allow the parent of a count distinct to be any one of
comment|// those.
specifier|private
name|boolean
name|isSupportedPostAggOperation
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
name|kind
operator|==
name|SqlKind
operator|.
name|PLUS
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|MINUS
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|DIVIDE
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|TIMES
return|;
block|}
comment|/**    * Returns the list of {@link ComplexMetric} that match the given<code>alias</code> if it exists,    * otherwise returns an empty list, never<code>null</code>    * */
specifier|public
name|List
argument_list|<
name|ComplexMetric
argument_list|>
name|getComplexMetricsFrom
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
return|return
name|complexMetrics
operator|.
name|containsKey
argument_list|(
name|alias
argument_list|)
condition|?
name|complexMetrics
operator|.
name|get
argument_list|(
name|alias
argument_list|)
else|:
operator|new
name|ArrayList
argument_list|<
name|ComplexMetric
argument_list|>
argument_list|()
return|;
block|}
comment|/**    * Returns true if and only if the given<code>alias</code> is a reference to a registered    * {@link ComplexMetric}    * */
specifier|public
name|boolean
name|isComplexMetric
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
return|return
name|complexMetrics
operator|.
name|get
argument_list|(
name|alias
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|rowType
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fieldNames
operator|.
name|contains
argument_list|(
name|timestampFieldName
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fieldNames
operator|.
name|containsAll
argument_list|(
name|metricFieldNames
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rowType
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|context
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|TableScan
name|scan
init|=
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptTable
argument_list|)
decl_stmt|;
return|return
name|DruidQuery
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|BindableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelNode
operator|>
name|of
argument_list|(
name|scan
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|metricFieldNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Creates a {@link RelDataType} from a map of    * field names and types. */
specifier|private
specifier|static
class|class
name|MapRelProtoDataType
implements|implements
name|RelProtoDataType
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fields
decl_stmt|;
name|MapRelProtoDataType
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|field
range|:
name|fields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DruidTable.java
end_comment

end_unit

