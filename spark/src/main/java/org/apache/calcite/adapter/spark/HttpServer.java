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
name|adapter
operator|.
name|spark
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
name|server
operator|.
name|handler
operator|.
name|ResourceHandler
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Inet4Address
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InterfaceAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|NetworkInterface
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * An HTTP server for static content used to allow worker nodes to access JARs.  *  *<p>Based on Spark HttpServer, wraps a Jetty server.</p>  */
end_comment

begin_class
class|class
name|HttpServer
block|{
specifier|private
specifier|static
name|String
name|localIpAddress
decl_stmt|;
specifier|private
specifier|final
name|File
name|resourceBase
decl_stmt|;
name|HttpServer
parameter_list|(
name|File
name|resourceBase
parameter_list|)
block|{
name|this
operator|.
name|resourceBase
operator|=
name|resourceBase
expr_stmt|;
block|}
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
literal|0
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
name|ResourceHandler
name|resHandler
init|=
operator|new
name|ResourceHandler
argument_list|()
decl_stmt|;
name|resHandler
operator|.
name|setResourceBase
argument_list|(
name|resourceBase
operator|.
name|getAbsolutePath
argument_list|()
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
name|resHandler
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
comment|/**    * Returns the URI of this HTTP server ("http://host:port").    */
name|String
name|uri
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
literal|"Server is not started"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|"http://"
operator|+
name|localIpAddress
argument_list|()
operator|+
literal|":"
operator|+
name|port
return|;
block|}
block|}
comment|/**    * Get the local host's IP address in dotted-quad format (e.g. 1.2.3.4).    * Note, this is typically not used from within core spark.    */
specifier|static
specifier|synchronized
name|String
name|localIpAddress
parameter_list|()
block|{
synchronized|synchronized
init|(
name|HttpServer
operator|.
name|class
init|)
block|{
if|if
condition|(
name|localIpAddress
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|localIpAddress
operator|=
name|findLocalIpAddress
argument_list|()
expr_stmt|;
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
return|return
name|localIpAddress
return|;
block|}
specifier|private
specifier|static
name|String
name|findLocalIpAddress
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|defaultIpOverride
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"CALCITE_LOCAL_IP"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultIpOverride
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultIpOverride
return|;
block|}
else|else
block|{
specifier|final
name|InetAddress
name|address
init|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
comment|// Address resolves to something like 127.0.1.1, which happens on
comment|// Debian; try to find a better address using the local network
comment|// interfaces.
for|for
control|(
name|NetworkInterface
name|ni
range|:
name|iterable
argument_list|(
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
argument_list|)
control|)
block|{
for|for
control|(
name|InterfaceAddress
name|interfaceAddress
range|:
name|ni
operator|.
name|getInterfaceAddresses
argument_list|()
control|)
block|{
specifier|final
name|InetAddress
name|addr
init|=
name|interfaceAddress
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|addr
operator|.
name|isLinkLocalAddress
argument_list|()
operator|&&
operator|!
name|addr
operator|.
name|isLoopbackAddress
argument_list|()
operator|&&
name|addr
operator|instanceof
name|Inet4Address
condition|)
block|{
comment|// We've found an address that looks reasonable!
name|logWarning
argument_list|(
literal|"Your hostname, "
operator|+
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|" resolves to a loopback address: "
operator|+
name|address
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|"; using "
operator|+
name|addr
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|" instead (on interface "
operator|+
name|ni
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|logWarning
argument_list|(
literal|"Set CALCITE_LOCAL_IP if you need to bind to another address"
argument_list|)
expr_stmt|;
return|return
name|addr
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
block|}
block|}
name|logWarning
argument_list|(
literal|"Your hostname, "
operator|+
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|" resolves to a loopback address: "
operator|+
name|address
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|", but we couldn't find any external IP address!"
argument_list|)
expr_stmt|;
name|logWarning
argument_list|(
literal|"Set CALCITE_LOCAL_IP if you need to bind to another address"
argument_list|)
expr_stmt|;
block|}
return|return
name|address
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Iterable
argument_list|<
name|E
argument_list|>
name|iterable
parameter_list|(
specifier|final
name|Enumeration
argument_list|<
name|E
argument_list|>
name|enumeration
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
operator|new
name|Iterator
argument_list|<
name|E
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
argument_list|()
block|{
block_content|return enumeration.hasMoreElements(
block_content|)
function|;
block|}
end_class

begin_function
specifier|public
name|E
name|next
parameter_list|()
block|{
return|return
name|enumeration
operator|.
name|nextElement
argument_list|()
return|;
block|}
end_function

begin_function
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
end_function

begin_function
unit|};   }    private
specifier|static
name|void
name|logWarning
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

