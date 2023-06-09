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
name|MockConsumer
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
name|OffsetResetStrategy
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
name|common
operator|.
name|TopicPartition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * A mock consumer to test Kafka adapter.  */
end_comment

begin_class
specifier|public
class|class
name|KafkaMockConsumer
extends|extends
name|MockConsumer
block|{
specifier|public
name|KafkaMockConsumer
parameter_list|(
specifier|final
name|OffsetResetStrategy
name|offsetResetStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|OffsetResetStrategy
operator|.
name|EARLIEST
argument_list|)
expr_stmt|;
name|assign
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|TopicPartition
argument_list|(
literal|"testtopic"
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|TopicPartition
argument_list|,
name|Long
argument_list|>
name|beginningOffsets
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|beginningOffsets
operator|.
name|put
argument_list|(
operator|new
name|TopicPartition
argument_list|(
literal|"testtopic"
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|updateBeginningOffsets
argument_list|(
name|beginningOffsets
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|10
condition|;
operator|++
name|idx
control|)
block|{
name|addRecord
argument_list|(
operator|new
name|ConsumerRecord
argument_list|<>
argument_list|(
literal|"testtopic"
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|,
operator|(
literal|"mykey"
operator|+
name|idx
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
operator|(
literal|"myvalue"
operator|+
name|idx
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

