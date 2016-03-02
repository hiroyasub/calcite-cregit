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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Generic interface to support parsing of serialized protocol buffers between client and server.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ProtobufTranslation
block|{
comment|/**    * Serializes a {@link Response} as a protocol buffer.    *    * @param response The response to serialize    * @throws IOException If there are errors during serialization    */
name|byte
index|[]
name|serializeResponse
parameter_list|(
name|Response
name|response
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Serializes a {@link Request} as a protocol buffer.    *    * @param request The request to serialize    * @throws IOException If there are errors during serialization    */
name|byte
index|[]
name|serializeRequest
parameter_list|(
name|Request
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Parses a serialized protocol buffer request into a {@link Request}.    *    * @param bytes Serialized protocol buffer request from client    * @return A Request object for the given bytes    * @throws IOException If the protocol buffer cannot be deserialized    */
name|Request
name|parseRequest
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Parses a serialized protocol buffer response into a {@link Response}.    *    * @param bytes Serialized protocol buffer request from server    * @return The Response object for the given bytes    * @throws IOException If the protocol buffer cannot be deserialized    */
name|Response
name|parseResponse
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

begin_comment
comment|// End ProtobufTranslation.java
end_comment

end_unit

