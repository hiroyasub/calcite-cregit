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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A Service implementation that performs protocol buffer serialization on request and responses  * on either side of computing a response from a request to mimic some transport to a server which  * would normally perform such computation.  */
end_comment

begin_class
specifier|public
class|class
name|LocalProtobufService
extends|extends
name|ProtobufService
block|{
specifier|private
specifier|final
name|Service
name|service
decl_stmt|;
specifier|private
specifier|final
name|ProtobufTranslation
name|translation
decl_stmt|;
specifier|public
name|LocalProtobufService
parameter_list|(
name|Service
name|service
parameter_list|,
name|ProtobufTranslation
name|translation
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|translation
operator|=
name|translation
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Response
name|_apply
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
try|try
block|{
comment|// Serialize the request to "send to the server"
name|byte
index|[]
name|serializedRequest
init|=
name|translation
operator|.
name|serializeRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// *some transport would normally happen here*
comment|// Fake deserializing that request somewhere else
name|Request
name|request2
init|=
name|translation
operator|.
name|parseRequest
argument_list|(
name|serializedRequest
argument_list|)
decl_stmt|;
comment|// Serialize the response from the service to "send to the client"
name|byte
index|[]
name|serializedResponse
init|=
name|translation
operator|.
name|serializeResponse
argument_list|(
name|request2
operator|.
name|accept
argument_list|(
name|service
argument_list|)
argument_list|)
decl_stmt|;
comment|// *some transport would normally happen here*
comment|// Deserialize the response on "the client"
return|return
name|translation
operator|.
name|parseResponse
argument_list|(
name|serializedResponse
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End LocalProtobufService.java
end_comment

end_unit

