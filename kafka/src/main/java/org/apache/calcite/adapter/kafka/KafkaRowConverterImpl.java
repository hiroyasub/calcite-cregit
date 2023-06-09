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
name|kafka
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
name|RelDataTypeSystem
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
name|SqlTypeFactoryImpl
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
name|kafka
operator|.
name|clients
operator|.
name|consumer
operator|.
name|ConsumerRecord
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link KafkaRowConverter}, both key and value are byte[].  */
end_comment

begin_class
specifier|public
class|class
name|KafkaRowConverterImpl
implements|implements
name|KafkaRowConverter
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
block|{
comment|/**    * Generates the row schema for a given Kafka topic.    *    * @param topicName Kafka topic name    * @return row type    */
annotation|@
name|Override
specifier|public
name|RelDataType
name|rowDataType
parameter_list|(
specifier|final
name|String
name|topicName
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|fieldInfo
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
literal|"MSG_PARTITION"
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
literal|"MSG_TIMESTAMP"
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
literal|"MSG_OFFSET"
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
literal|"MSG_KEY_BYTES"
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
literal|"MSG_VALUE_BYTES"
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|fieldInfo
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parses and reformats a Kafka message from the consumer, to align with the    * row schema defined as {@link #rowDataType(String)}.    *    * @param message Raw Kafka message record    * @return fields in the row    */
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|toRow
parameter_list|(
specifier|final
name|ConsumerRecord
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|message
parameter_list|)
block|{
name|Object
index|[]
name|fields
init|=
operator|new
name|Object
index|[
literal|5
index|]
decl_stmt|;
name|fields
index|[
literal|0
index|]
operator|=
name|message
operator|.
name|partition
argument_list|()
expr_stmt|;
name|fields
index|[
literal|1
index|]
operator|=
name|message
operator|.
name|timestamp
argument_list|()
expr_stmt|;
name|fields
index|[
literal|2
index|]
operator|=
name|message
operator|.
name|offset
argument_list|()
expr_stmt|;
name|fields
index|[
literal|3
index|]
operator|=
name|message
operator|.
name|key
argument_list|()
expr_stmt|;
name|fields
index|[
literal|4
index|]
operator|=
name|message
operator|.
name|value
argument_list|()
expr_stmt|;
return|return
name|fields
return|;
block|}
block|}
end_class

end_unit

