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
name|redis
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link TableFactory} for Redis.  *  *<p>A table corresponds to what Redis calls a "data source".  */
end_comment

begin_class
specifier|public
class|class
name|RedisTableFactory
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
name|RedisTableFactory
name|INSTANCE
init|=
operator|new
name|RedisTableFactory
argument_list|()
decl_stmt|;
specifier|private
name|RedisTableFactory
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
name|tableName
parameter_list|,
name|Map
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RedisSchema
name|redisSchema
init|=
name|schema
operator|.
name|unwrap
argument_list|(
name|RedisSchema
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelProtoDataType
name|protoRowType
init|=
name|rowType
operator|!=
literal|null
condition|?
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|rowType
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
name|RedisTable
operator|.
name|create
argument_list|(
name|redisSchema
argument_list|,
name|tableName
argument_list|,
name|operand
argument_list|,
name|protoRowType
argument_list|)
return|;
block|}
block|}
end_class

end_unit
