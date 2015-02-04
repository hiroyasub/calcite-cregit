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
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Connector
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
name|Server
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
name|ServerConnector
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
name|DefaultHandler
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
name|HandlerList
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
name|util
operator|.
name|thread
operator|.
name|QueuedThreadPool
import|;
end_import

begin_comment
comment|/**  * Avatica HTTP server.  */
end_comment

begin_class
specifier|public
class|class
name|HttpServer
block|{
specifier|private
name|Server
name|server
decl_stmt|;
specifier|private
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|Handler
name|handler
decl_stmt|;
name|HttpServer
parameter_list|(
name|int
name|port
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Server is already started"
argument_list|)
throw|;
block|}
else|else
block|{
name|QueuedThreadPool
name|threadPool
init|=
operator|new
name|QueuedThreadPool
argument_list|()
decl_stmt|;
name|threadPool
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|Server
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
name|server
operator|.
name|manage
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
specifier|final
name|ServerConnector
name|connector
init|=
operator|new
name|ServerConnector
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|connector
operator|.
name|setIdleTimeout
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setSoLingerTime
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
specifier|final
name|HandlerList
name|handlerList
init|=
operator|new
name|HandlerList
argument_list|()
decl_stmt|;
name|handlerList
operator|.
name|setHandlers
argument_list|(
operator|new
name|Handler
index|[]
block|{
name|handler
block|,
operator|new
name|DefaultHandler
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|handlerList
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|port
operator|=
name|connector
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Server is already stopped"
argument_list|)
throw|;
block|}
else|else
block|{
try|try
block|{
specifier|final
name|Server
name|server1
init|=
name|server
decl_stmt|;
name|port
operator|=
operator|-
literal|1
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
name|server1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
specifier|public
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|server
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
block|}
end_class

begin_comment
comment|// End HttpServer.java
end_comment

end_unit

