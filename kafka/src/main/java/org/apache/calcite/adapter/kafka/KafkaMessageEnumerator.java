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
name|linq4j
operator|.
name|Enumerator
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
name|Consumer
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
name|ConsumerRecords
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Enumerator to read data from {@link Consumer},  * and converted into SQL rows with {@link KafkaRowConverter}.  * @param<K>: type for Kafka message key,  *           refer to {@link ConsumerConfig#KEY_DESERIALIZER_CLASS_CONFIG};  * @param<V>: type for Kafka message value,  *           refer to {@link ConsumerConfig#VALUE_DESERIALIZER_CLASS_CONFIG};  */
end_comment

begin_class
specifier|public
class|class
name|KafkaMessageEnumerator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|final
name|Consumer
name|consumer
decl_stmt|;
specifier|final
name|KafkaRowConverter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|rowConverter
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|cancelFlag
decl_stmt|;
comment|//runtime
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|ConsumerRecord
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|bufferedRecords
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|ConsumerRecord
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|curRecord
decl_stmt|;
name|KafkaMessageEnumerator
parameter_list|(
specifier|final
name|Consumer
name|consumer
parameter_list|,
specifier|final
name|KafkaRowConverter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|rowConverter
parameter_list|,
specifier|final
name|AtomicBoolean
name|cancelFlag
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|rowConverter
operator|=
name|rowConverter
expr_stmt|;
name|this
operator|.
name|cancelFlag
operator|=
name|cancelFlag
expr_stmt|;
block|}
comment|/**    * It returns an Array of Object, with each element represents a field of row.    */
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
return|return
name|rowConverter
operator|.
name|toRow
argument_list|(
name|curRecord
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|cancelFlag
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
name|bufferedRecords
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pullRecords
argument_list|()
expr_stmt|;
block|}
name|curRecord
operator|=
name|bufferedRecords
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|pullRecords
parameter_list|()
block|{
name|ConsumerRecords
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|records
init|=
name|consumer
operator|.
name|poll
argument_list|(
name|Duration
operator|.
name|ofMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|ConsumerRecord
name|record
range|:
name|records
control|)
block|{
name|bufferedRecords
operator|.
name|add
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|bufferedRecords
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pullRecords
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

