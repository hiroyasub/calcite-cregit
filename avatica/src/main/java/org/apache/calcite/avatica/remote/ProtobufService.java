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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Descriptors
operator|.
name|Descriptor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Service implementation that encodes requests and responses as protocol buffers.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ProtobufService
extends|extends
name|AbstractService
block|{
comment|/**    * Derived class should implement this method to transport requests and    * responses to and from the peer service.    */
specifier|public
specifier|abstract
name|Response
name|_apply
parameter_list|(
name|Request
name|request
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|CatalogsRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|SchemasRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|TablesRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|TableTypesRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|TypeInfoRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|ColumnsRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ResultSetResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PrepareResponse
name|apply
parameter_list|(
name|PrepareRequest
name|request
parameter_list|)
block|{
return|return
name|finagle
argument_list|(
operator|(
name|PrepareResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ExecuteResponse
name|apply
parameter_list|(
name|PrepareAndExecuteRequest
name|request
parameter_list|)
block|{
return|return
name|finagle
argument_list|(
operator|(
name|ExecuteResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FetchResponse
name|apply
parameter_list|(
name|FetchRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|FetchResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CreateStatementResponse
name|apply
parameter_list|(
name|CreateStatementRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|CreateStatementResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CloseStatementResponse
name|apply
parameter_list|(
name|CloseStatementRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|CloseStatementResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CloseConnectionResponse
name|apply
parameter_list|(
name|CloseConnectionRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|CloseConnectionResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConnectionSyncResponse
name|apply
parameter_list|(
name|ConnectionSyncRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|ConnectionSyncResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DatabasePropertyResponse
name|apply
parameter_list|(
name|DatabasePropertyRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|DatabasePropertyResponse
operator|)
name|_apply
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**    * Determines whether the given message has the field, denoted by the provided number, set.    *    * @param msg The protobuf message    * @param desc The descriptor for the message    * @param fieldNum The identifier for the field    * @return True if the message contains the field, false otherwise    */
specifier|public
specifier|static
name|boolean
name|hasField
parameter_list|(
name|Message
name|msg
parameter_list|,
name|Descriptor
name|desc
parameter_list|,
name|int
name|fieldNum
parameter_list|)
block|{
return|return
name|msg
operator|.
name|hasField
argument_list|(
name|desc
operator|.
name|findFieldByNumber
argument_list|(
name|fieldNum
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ProtobufService.java
end_comment

end_unit

