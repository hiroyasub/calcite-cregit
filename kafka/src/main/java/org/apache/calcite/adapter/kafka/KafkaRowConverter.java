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
name|kafka
operator|.
name|clients
operator|.
name|consumer
operator|.
name|ConsumerConfig
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
comment|/**  * Interface to handle formatting between Kafka message and Calcite row.  *  * @param<K> type for Kafka message key,  *           refer to {@link ConsumerConfig#KEY_DESERIALIZER_CLASS_CONFIG};  * @param<V> type for Kafka message value,  *           refer to {@link ConsumerConfig#VALUE_DESERIALIZER_CLASS_CONFIG};  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|KafkaRowConverter
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**    * Generates row type for a given Kafka topic.    *    * @param topicName, Kafka topic name;    * @return row type    */
name|RelDataType
name|rowDataType
parameter_list|(
name|String
name|topicName
parameter_list|)
function_decl|;
comment|/**    * Parses and reformats Kafka message from consumer,    * to align with row type defined as {@link #rowDataType(String)}.    *    * @param message, the raw Kafka message record;    * @return fields in the row    */
name|Object
index|[]
name|toRow
parameter_list|(
name|ConsumerRecord
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|message
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End KafkaRowConverter.java
end_comment

end_unit

