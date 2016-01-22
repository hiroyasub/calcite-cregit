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
name|avatica
operator|.
name|remote
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
name|avatica
operator|.
name|metrics
operator|.
name|MetricsSystem
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
name|avatica
operator|.
name|metrics
operator|.
name|Timer
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
name|avatica
operator|.
name|metrics
operator|.
name|Timer
operator|.
name|Context
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
name|avatica
operator|.
name|remote
operator|.
name|Service
operator|.
name|Request
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
name|avatica
operator|.
name|remote
operator|.
name|Service
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.remote.Handler}  * that decodes JSON requests, sends them to a {@link Service},  * and encodes the responses into JSON.  *  * @see org.apache.calcite.avatica.remote.JsonService  */
end_comment

begin_class
specifier|public
class|class
name|JsonHandler
extends|extends
name|AbstractHandler
argument_list|<
name|String
argument_list|>
block|{
specifier|protected
specifier|static
specifier|final
name|ObjectMapper
name|MAPPER
init|=
name|JsonService
operator|.
name|MAPPER
decl_stmt|;
specifier|final
name|MetricsSystem
name|metrics
decl_stmt|;
specifier|final
name|Timer
name|serializationTimer
decl_stmt|;
specifier|public
name|JsonHandler
parameter_list|(
name|Service
name|service
parameter_list|,
name|MetricsSystem
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|serializationTimer
operator|=
name|this
operator|.
name|metrics
operator|.
name|getTimer
argument_list|(
name|MetricsHelper
operator|.
name|concat
argument_list|(
name|JsonHandler
operator|.
name|class
argument_list|,
name|HANDLER_SERIALIZATION_METRICS_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|HandlerResponse
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|String
name|jsonRequest
parameter_list|)
block|{
return|return
name|super
operator|.
name|apply
argument_list|(
name|jsonRequest
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Request
name|decode
parameter_list|(
name|String
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|Context
name|ctx
init|=
name|serializationTimer
operator|.
name|start
argument_list|()
init|)
block|{
return|return
name|MAPPER
operator|.
name|readValue
argument_list|(
name|request
argument_list|,
name|Service
operator|.
name|Request
operator|.
name|class
argument_list|)
return|;
block|}
block|}
comment|/**    * Serializes the provided object as JSON.    *    * @param response The object to serialize.    * @return A JSON string.    */
annotation|@
name|Override
name|String
name|encode
parameter_list|(
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|Context
name|ctx
init|=
name|serializationTimer
operator|.
name|start
argument_list|()
init|)
block|{
specifier|final
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|MAPPER
operator|.
name|writeValue
argument_list|(
name|w
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
name|w
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JsonHandler.java
end_comment

end_unit

