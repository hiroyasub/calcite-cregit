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
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/**  * A callback which the server can invoke to allow implementations to control additional logic  * about whether or not a client from a specific host should be run.  *<p>For example, complex logic could be supplied to control whether clientA from host1 is  * permitted to execute queries. This logic is deferred upon the user to implement.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DoAsRemoteUserCallback
block|{
comment|/**    * Invokes the given<code>action</code> as the<code>remoteUserName</code>.    *    * @param remoteUserName The remote user making a request to the Avatica server.    * @param remoteAddress The address the remote user is making the request from.    * @param action The operation the Avatica server wants to run as<code>remoteUserName</code>.    * @return The result from the Callable.    */
parameter_list|<
name|T
parameter_list|>
name|T
name|doAsRemoteUser
parameter_list|(
name|String
name|remoteUserName
parameter_list|,
name|String
name|remoteAddress
parameter_list|,
name|Callable
argument_list|<
name|T
argument_list|>
name|action
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

begin_comment
comment|// End DoAsRemoteUserCallback.java
end_comment

end_unit

