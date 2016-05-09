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
name|metrics
operator|.
name|noop
operator|.
name|NoopMetricsSystem
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
name|Handler
operator|.
name|HandlerResponse
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
name|MetricsHelper
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
name|ProtobufHandler
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
name|ProtobufTranslation
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
name|ProtobufTranslationImpl
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|util
operator|.
name|UnsynchronizedBuffer
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|Objects
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
name|AvaticaProtobufHandler
extends|extends
name|AbstractAvaticaHandler
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AvaticaJsonHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Service
name|service
decl_stmt|;
specifier|private
specifier|final
name|ProtobufHandler
name|pbHandler
decl_stmt|;
specifier|private
specifier|final
name|ProtobufTranslation
name|protobufTranslation
decl_stmt|;
specifier|private
specifier|final
name|MetricsSystem
name|metrics
decl_stmt|;
specifier|private
specifier|final
name|Timer
name|requestTimer
decl_stmt|;
specifier|private
specifier|final
name|AvaticaServerConfiguration
name|serverConfig
decl_stmt|;
specifier|final
name|ThreadLocal
argument_list|<
name|UnsynchronizedBuffer
argument_list|>
name|threadLocalBuffer
decl_stmt|;
specifier|public
name|AvaticaProtobufHandler
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
argument_list|(
name|service
argument_list|,
name|NoopMetricsSystem
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AvaticaProtobufHandler
parameter_list|(
name|Service
name|service
parameter_list|,
name|MetricsSystem
name|metrics
parameter_list|)
block|{
name|this
argument_list|(
name|service
argument_list|,
name|metrics
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AvaticaProtobufHandler
parameter_list|(
name|Service
name|service
parameter_list|,
name|MetricsSystem
name|metrics
parameter_list|,
name|AvaticaServerConfiguration
name|serverConfig
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestTimer
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
name|AvaticaProtobufHandler
operator|.
name|class
argument_list|,
name|MetricsAwareAvaticaHandler
operator|.
name|REQUEST_TIMER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|protobufTranslation
operator|=
operator|new
name|ProtobufTranslationImpl
argument_list|()
expr_stmt|;
name|this
operator|.
name|pbHandler
operator|=
operator|new
name|ProtobufHandler
argument_list|(
name|service
argument_list|,
name|protobufTranslation
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadLocalBuffer
operator|=
operator|new
name|ThreadLocal
argument_list|<
name|UnsynchronizedBuffer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|UnsynchronizedBuffer
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|UnsynchronizedBuffer
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|serverConfig
operator|=
name|serverConfig
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
try|try
init|(
specifier|final
name|Context
name|ctx
init|=
name|this
operator|.
name|requestTimer
operator|.
name|start
argument_list|()
init|)
block|{
comment|// Check if the user is OK to proceed.
if|if
condition|(
operator|!
name|isUserPermitted
argument_list|(
name|serverConfig
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"HTTP request from {} is unauthenticated and authentication is required"
argument_list|,
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/octet-stream;charset=utf-8"
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
specifier|final
name|byte
index|[]
name|requestBytes
decl_stmt|;
comment|// Avoid a new buffer creation for every HTTP request
specifier|final
name|UnsynchronizedBuffer
name|buffer
init|=
name|threadLocalBuffer
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|requestBytes
operator|=
name|AvaticaUtils
operator|.
name|readFullyToBytes
argument_list|(
name|inputStream
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|HandlerResponse
argument_list|<
name|byte
index|[]
argument_list|>
name|handlerResponse
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|null
operator|!=
name|serverConfig
operator|&&
name|serverConfig
operator|.
name|supportsImpersonation
argument_list|()
condition|)
block|{
comment|// Invoke the ProtobufHandler inside as doAs for the remote user.
name|handlerResponse
operator|=
name|serverConfig
operator|.
name|doAsRemoteUser
argument_list|(
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|,
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|,
operator|new
name|Callable
argument_list|<
name|HandlerResponse
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HandlerResponse
argument_list|<
name|byte
index|[]
argument_list|>
name|call
parameter_list|()
block|{
return|return
name|pbHandler
operator|.
name|apply
argument_list|(
name|requestBytes
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handlerResponse
operator|=
name|pbHandler
operator|.
name|apply
argument_list|(
name|requestBytes
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Catch at the highest level of exceptions
name|handlerResponse
operator|=
name|pbHandler
operator|.
name|convertToErrorResponse
argument_list|(
name|e
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
name|setStatus
argument_list|(
name|handlerResponse
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|handlerResponse
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setServerRpcMetadata
parameter_list|(
name|RpcMetadataResponse
name|metadata
parameter_list|)
block|{
comment|// Set the metadata for the normal service calls
name|service
operator|.
name|setRpcMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
comment|// Also add it to the handler to include with exceptions
name|pbHandler
operator|.
name|setRpcMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MetricsSystem
name|getMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|metrics
return|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaProtobufHandler.java
end_comment

end_unit

