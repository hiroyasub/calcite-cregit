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
name|remote
operator|.
name|Service
operator|.
name|RpcMetadataResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * API for text request-response calls to an Avatica server.  *  * @param<T> The type this handler accepts and returns  */
end_comment

begin_interface
specifier|public
interface|interface
name|Handler
parameter_list|<
name|T
parameter_list|>
block|{
name|int
name|HTTP_OK
init|=
literal|200
decl_stmt|;
name|int
name|HTTP_INTERNAL_SERVER_ERROR
init|=
literal|500
decl_stmt|;
name|String
name|HANDLER_SERIALIZATION_METRICS_NAME
init|=
literal|"Handler.Serialization"
decl_stmt|;
comment|/**    * Struct that encapsulates the context of the result of a request to Avatica.    */
specifier|public
class|class
name|HandlerResponse
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|T
name|response
decl_stmt|;
specifier|private
specifier|final
name|int
name|statusCode
decl_stmt|;
specifier|public
name|HandlerResponse
parameter_list|(
name|T
name|response
parameter_list|,
name|int
name|statusCode
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusCode
operator|=
name|statusCode
expr_stmt|;
block|}
specifier|public
name|T
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
specifier|public
name|int
name|getStatusCode
parameter_list|()
block|{
return|return
name|statusCode
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Response: "
operator|+
name|response
operator|+
literal|", Status:"
operator|+
name|statusCode
return|;
block|}
block|}
name|HandlerResponse
argument_list|<
name|T
argument_list|>
name|apply
parameter_list|(
name|T
name|request
parameter_list|)
function_decl|;
comment|/**    * Sets some general server information to return to the client in all responses.    *    * @param metadata Server-wide information    */
name|void
name|setRpcMetadata
parameter_list|(
name|RpcMetadataResponse
name|metadata
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Handler.java
end_comment

end_unit

