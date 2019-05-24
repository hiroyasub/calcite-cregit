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

begin_comment
comment|/**  * Parameter constants used to define a Kafka table.  */
end_comment

begin_interface
interface|interface
name|KafkaTableConstants
block|{
name|String
name|SCHEMA_TOPIC_NAME
init|=
literal|"topic.name"
decl_stmt|;
name|String
name|SCHEMA_BOOTSTRAP_SERVERS
init|=
literal|"bootstrap.servers"
decl_stmt|;
name|String
name|SCHEMA_ROW_CONVERTER
init|=
literal|"row.converter"
decl_stmt|;
name|String
name|SCHEMA_CUST_CONSUMER
init|=
literal|"consumer.cust"
decl_stmt|;
name|String
name|SCHEMA_CONSUMER_PARAMS
init|=
literal|"consumer.params"
decl_stmt|;
block|}
end_interface

begin_comment
comment|// End KafkaTableConstants.java
end_comment

end_unit

