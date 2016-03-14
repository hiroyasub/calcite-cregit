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
name|AvaticaSeverity
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
name|ErrorResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|handler
operator|.
name|AbstractHandler
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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_comment
comment|/**  * Base-class for Avatica implemented Jetty Handlers.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAvaticaHandler
extends|extends
name|AbstractHandler
implements|implements
name|MetricsAwareAvaticaHandler
block|{
specifier|private
specifier|static
specifier|final
name|ErrorResponse
name|UNAUTHORIZED_ERROR
init|=
operator|new
name|ErrorResponse
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|"User is not authenticated"
argument_list|,
name|ErrorResponse
operator|.
name|UNAUTHORIZED_ERROR_CODE
argument_list|,
name|ErrorResponse
operator|.
name|UNAUTHORIZED_SQL_STATE
argument_list|,
name|AvaticaSeverity
operator|.
name|ERROR
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**    * Determines if a request is permitted to be executed. The server may require authentication    * and the login mechanism might have failed. This check verifies that only authenticated    * users are permitted through when the server is requiring authentication. When a user    * is disallowed, a status code and response will be automatically written to the provided    *<code>response</code> and the caller should return immediately.    *    * @param serverConfig The server's configuration    * @param request The user's request    * @param response The response to the user's request    * @return True if request can proceed, false otherwise.    */
specifier|public
name|boolean
name|isUserPermitted
parameter_list|(
name|AvaticaServerConfiguration
name|serverConfig
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make sure that we drop any unauthenticated users out first.
if|if
condition|(
literal|null
operator|!=
name|serverConfig
operator|&&
name|AuthenticationType
operator|.
name|SPNEGO
operator|==
name|serverConfig
operator|.
name|getAuthenticationType
argument_list|()
condition|)
block|{
name|String
name|remoteUser
init|=
name|request
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|remoteUser
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|)
expr_stmt|;
name|response
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|UNAUTHORIZED_ERROR
operator|.
name|serialize
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractAvaticaHandler.java
end_comment

end_unit

