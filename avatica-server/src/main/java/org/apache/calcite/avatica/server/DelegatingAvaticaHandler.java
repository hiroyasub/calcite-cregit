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
name|Handler
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
name|Server
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
comment|/**  * An AvaticaHandler implementation that delegates to a provided Jetty Handler instance.  *  *<p>This implementation provides a no-op implementation for  * {@link #setServerRpcMetadata(org.apache.calcite.avatica.remote.Service.RpcMetadataResponse)}.  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingAvaticaHandler
implements|implements
name|AvaticaHandler
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
name|DelegatingAvaticaHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Handler
name|handler
decl_stmt|;
specifier|public
name|DelegatingAvaticaHandler
parameter_list|(
name|Handler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|handler
operator|.
name|handle
argument_list|(
name|target
argument_list|,
name|baseRequest
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setServer
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
name|handler
operator|.
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Server
name|getServer
parameter_list|()
block|{
return|return
name|handler
operator|.
name|getServer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|handler
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|handler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isRunning
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isStarted
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStarting
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isStarting
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStopping
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isStopping
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isStopped
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|handler
operator|.
name|isFailed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addLifeCycleListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|handler
operator|.
name|addLifeCycleListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeLifeCycleListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|handler
operator|.
name|removeLifeCycleListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Setting RpcMetadata is not implemented for DelegatingAvaticaHandler"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingAvaticaHandler.java
end_comment

end_unit

