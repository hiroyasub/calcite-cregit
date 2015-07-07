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
name|AvaticaUtils
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
name|JsonHandler
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
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|Request
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
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletInputStream
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
comment|/**  * Jetty handler that executes Avatica JSON request-responses.  */
end_comment

begin_class
specifier|public
class|class
name|AvaticaHandler
extends|extends
name|AbstractHandler
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AvaticaHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|JsonHandler
name|jsonHandler
decl_stmt|;
specifier|public
name|AvaticaHandler
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|jsonHandler
operator|=
operator|new
name|JsonHandler
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|target
parameter_list|,
name|Request
name|baseRequest
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json;charset=utf-8"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
comment|// First look for a request in the header, then look in the body.
comment|// The latter allows very large requests without hitting HTTP 413.
name|String
name|rawRequest
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"request"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rawRequest
operator|==
literal|null
condition|)
block|{
try|try
init|(
name|ServletInputStream
name|inputStream
init|=
name|request
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|rawRequest
operator|=
name|AvaticaUtils
operator|.
name|readFully
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|jsonRequest
init|=
operator|new
name|String
argument_list|(
name|rawRequest
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"request: "
operator|+
name|jsonRequest
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|jsonResponse
init|=
name|jsonHandler
operator|.
name|apply
argument_list|(
name|jsonRequest
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"response: "
operator|+
name|jsonResponse
argument_list|)
expr_stmt|;
block|}
name|baseRequest
operator|.
name|setHandled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|println
argument_list|(
name|jsonResponse
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AvaticaHandler.java
end_comment

end_unit

