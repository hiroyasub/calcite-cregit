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
name|SchemaPlus
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
name|TableFactory
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|Interval
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|chrono
operator|.
name|ISOChronology
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
comment|/**  * Implementation of {@link TableFactory} for Druid.  *  *<p>A table corresponds to what Druid calls a "data source".  */
end_comment

begin_class
specifier|public
class|class
name|DruidTableFactory
implements|implements
name|TableFactory
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
specifier|static
specifier|final
name|DruidTableFactory
name|INSTANCE
init|=
operator|new
name|DruidTableFactory
argument_list|()
decl_stmt|;
specifier|private
name|DruidTableFactory
parameter_list|()
block|{
block|}
comment|// name that is also the same name as a complex metric
annotation|@
name|Override
specifier|public
name|Table
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|DruidSchema
name|druidSchema
init|=
name|schema
operator|.
name|unwrap
argument_list|(
name|DruidSchema
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// If "dataSource" operand is present it overrides the table name.
specifier|final
name|String
name|dataSource
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"dataSource"
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|metricNameBuilder
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SqlTypeName
argument_list|>
name|fieldBuilder
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
name|timestampColumnName
decl_stmt|;
specifier|final
name|SqlTypeName
name|timestampColumnType
decl_stmt|;
specifier|final
name|Object
name|timestampInfo
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"timestampColumn"
argument_list|)
decl_stmt|;
if|if
condition|(
name|timestampInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|timestampInfo
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|timestampInfo
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"timestampColumn array must have name"
argument_list|)
throw|;
block|}
name|timestampColumnName
operator|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|map
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|instanceof
name|String
operator|)
operator|||
name|map
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"timestamp with local time zone"
argument_list|)
condition|)
block|{
name|timestampColumnType
operator|=
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
expr_stmt|;
block|}
if|else if
condition|(
name|map
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|timestampColumnType
operator|=
name|SqlTypeName
operator|.
name|TIMESTAMP
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unexpected type for timestampColumn array"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// String (for backwards compatibility)
name|timestampColumnName
operator|=
operator|(
name|String
operator|)
name|timestampInfo
expr_stmt|;
name|timestampColumnType
operator|=
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
expr_stmt|;
block|}
block|}
else|else
block|{
name|timestampColumnName
operator|=
name|DruidTable
operator|.
name|DEFAULT_TIMESTAMP_COLUMN
expr_stmt|;
name|timestampColumnType
operator|=
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
expr_stmt|;
block|}
name|fieldBuilder
operator|.
name|put
argument_list|(
name|timestampColumnName
argument_list|,
name|timestampColumnType
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|dimensionsRaw
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"dimensions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimensionsRaw
operator|instanceof
name|List
condition|)
block|{
comment|// noinspection unchecked
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|dimensions
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|dimensionsRaw
decl_stmt|;
for|for
control|(
name|String
name|dimension
range|:
name|dimensions
control|)
block|{
name|fieldBuilder
operator|.
name|put
argument_list|(
name|dimension
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
block|}
block|}
comment|// init the complex metric map
specifier|final
name|Object
name|complexMetricsRaw
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"complexMetrics"
argument_list|)
decl_stmt|;
if|if
condition|(
name|complexMetricsRaw
operator|instanceof
name|List
condition|)
block|{
comment|// noinspection unchecked
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|complexMetricList
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|complexMetricsRaw
decl_stmt|;
for|for
control|(
name|String
name|metric
range|:
name|complexMetricList
control|)
block|{
name|complexMetrics
operator|.
name|put
argument_list|(
name|metric
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Object
name|metricsRaw
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
decl_stmt|;
if|if
condition|(
name|metricsRaw
operator|instanceof
name|List
condition|)
block|{
specifier|final
name|List
name|metrics
init|=
operator|(
name|List
operator|)
name|metricsRaw
decl_stmt|;
for|for
control|(
name|Object
name|metric
range|:
name|metrics
control|)
block|{
name|DruidType
name|druidType
init|=
name|DruidType
operator|.
name|LONG
decl_stmt|;
specifier|final
name|String
name|metricName
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|metric
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map2
init|=
operator|(
name|Map
operator|)
name|metric
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|map2
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"metric must have name"
argument_list|)
throw|;
block|}
name|metricName
operator|=
operator|(
name|String
operator|)
name|map2
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|type
init|=
operator|(
name|String
operator|)
name|map2
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|fieldName
operator|=
operator|(
name|String
operator|)
name|map2
operator|.
name|get
argument_list|(
literal|"fieldName"
argument_list|)
expr_stmt|;
name|druidType
operator|=
name|DruidType
operator|.
name|getTypeFromMetric
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metricName
operator|=
operator|(
name|String
operator|)
name|metric
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|druidType
operator|.
name|isComplex
argument_list|()
condition|)
block|{
name|fieldBuilder
operator|.
name|put
argument_list|(
name|metricName
argument_list|,
name|druidType
operator|.
name|sqlType
argument_list|)
expr_stmt|;
name|metricNameBuilder
operator|.
name|add
argument_list|(
name|metricName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|fieldName
operator|!=
literal|null
assert|;
comment|// Only add the complex metric if there exists an alias for it
if|if
condition|(
name|complexMetrics
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|SqlTypeName
name|type
init|=
name|fieldBuilder
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|SqlTypeName
operator|.
name|VARCHAR
condition|)
block|{
name|fieldBuilder
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
expr_stmt|;
comment|// else, this complex metric is also a dimension, so it's type should remain as
comment|// VARCHAR, but it'll also be added as a complex metric.
block|}
name|complexMetrics
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ComplexMetric
argument_list|(
name|metricName
argument_list|,
name|druidType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|final
name|Object
name|interval
init|=
name|operand
operator|.
name|get
argument_list|(
literal|"interval"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Interval
argument_list|>
name|intervals
decl_stmt|;
if|if
condition|(
name|interval
operator|instanceof
name|String
condition|)
block|{
name|intervals
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|Interval
argument_list|(
operator|(
name|String
operator|)
name|interval
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|intervals
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|String
name|dataSourceName
init|=
name|Util
operator|.
name|first
argument_list|(
name|dataSource
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimensionsRaw
operator|==
literal|null
operator|||
name|metricsRaw
operator|==
literal|null
condition|)
block|{
name|DruidConnectionImpl
name|connection
init|=
operator|new
name|DruidConnectionImpl
argument_list|(
name|druidSchema
operator|.
name|url
argument_list|,
name|druidSchema
operator|.
name|url
operator|.
name|replace
argument_list|(
literal|":8082"
argument_list|,
literal|":8081"
argument_list|)
argument_list|)
decl_stmt|;
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
name|fieldBuilder
argument_list|,
name|metricNameBuilder
argument_list|,
name|timestampColumnName
argument_list|,
name|connection
argument_list|,
name|complexMetrics
argument_list|)
return|;
block|}
else|else
block|{
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
name|fieldBuilder
argument_list|,
name|metricNameBuilder
argument_list|,
name|timestampColumnName
argument_list|,
name|complexMetrics
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

