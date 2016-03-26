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
name|AuthenticationType
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
name|Callable
import|;
end_import

begin_comment
comment|/**  * A generic configuration interface that users can implement to configure the {@link HttpServer}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AvaticaServerConfiguration
block|{
comment|/**    * Returns the type of authentication the {@link HttpServer} should use.    * @return An enum corresponding to an authentication mechanism    */
name|AuthenticationType
name|getAuthenticationType
parameter_list|()
function_decl|;
comment|/**    * Returns the Kerberos realm to use for the server's login. Only relevant when    * {@link #getAuthenticationType()} returns {@link AuthenticationType#SPNEGO}.    *    * @return The Kerberos realm for the server login, or null if not applicable.    */
name|String
name|getKerberosRealm
parameter_list|()
function_decl|;
comment|/**    * Returns the Kerberos principal that the Avatica server should log in as.    *    * @return A Kerberos principal, or null if not applicable.    */
name|String
name|getKerberosPrincipal
parameter_list|()
function_decl|;
comment|/**    * Returns the array of allowed roles for login. Only applicable when    * {@link #getAuthenticationType()} returns {@link AuthenticationType#BASIC} or    * {@link AuthenticationType#DIGEST}.    *    * @return An array of allowed login roles, or null.    */
name|String
index|[]
name|getAllowedRoles
parameter_list|()
function_decl|;
comment|/**    * Returns the name of the realm to use in coordination with the properties files specified    * by {@link #getHashLoginServiceProperties()}. Only applicable when    * {@link #getAuthenticationType()} returns {@link AuthenticationType#BASIC} or    * {@link AuthenticationType#DIGEST}.    *    * @return A realm for the HashLoginService, or null.    */
name|String
name|getHashLoginServiceRealm
parameter_list|()
function_decl|;
comment|/**    * Returns the path to a properties file that contains users and realms. Only applicable when    * {@link #getAuthenticationType()} returns {@link AuthenticationType#BASIC} or    * {@link AuthenticationType#DIGEST}.    *    * @return A realm for the HashLoginService, or null.    */
name|String
name|getHashLoginServiceProperties
parameter_list|()
function_decl|;
comment|/**    * Returns true if the Avatica server should run user requests at that remote user. Otherwise,    * all requests are run as the Avatica server user (which is the default).    *    * @return True if impersonation is enabled, false otherwise.    */
name|boolean
name|supportsImpersonation
parameter_list|()
function_decl|;
comment|/**    * Invokes the given<code>action</code> as the<code>remoteUserName</code>. This will only be    * invoked if {@link #supportsImpersonation()} returns<code>true</code>.    *    * @param remoteUserName The remote user making a request to the Avatica server.    * @param remoteAddress The address the remote user is making the request from.    * @return The result from the Callable.    */
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
comment|// End AvaticaServerConfiguration.java
end_comment

end_unit

