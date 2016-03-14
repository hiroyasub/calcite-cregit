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
name|calcite
operator|.
name|avatica
operator|.
name|server
operator|.
name|AvaticaHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|KrbException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|server
operator|.
name|SimpleKdcServer
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
name|security
operator|.
name|UserAuthentication
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
name|Authentication
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
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|UserIdentity
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
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileWriter
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
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
comment|/**  * Utility class for setting up SPNEGO  */
end_comment

begin_class
specifier|public
class|class
name|SpnegoTestUtil
block|{
specifier|public
specifier|static
specifier|final
name|String
name|JGSS_KERBEROS_TICKET_OID
init|=
literal|"1.2.840.113554.1.2.2"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REALM
init|=
literal|"EXAMPLE.COM"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KDC_HOST
init|=
literal|"localhost"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_PRINCIPAL
init|=
literal|"client@"
operator|+
name|REALM
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_PRINCIPAL
init|=
literal|"HTTP/"
operator|+
name|KDC_HOST
operator|+
literal|"@"
operator|+
name|REALM
decl_stmt|;
specifier|private
name|SpnegoTestUtil
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|int
name|getFreePort
parameter_list|()
throws|throws
name|IOException
block|{
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|s
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|s
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
return|return
name|port
return|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|s
condition|)
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|setupUser
parameter_list|(
name|SimpleKdcServer
name|kdc
parameter_list|,
name|File
name|keytab
parameter_list|,
name|String
name|principal
parameter_list|)
throws|throws
name|KrbException
block|{
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|kdc
operator|.
name|exportPrincipal
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
block|}
comment|/**    * Recursively deletes a {@link File}.    */
specifier|public
specifier|static
name|void
name|deleteRecursively
parameter_list|(
name|File
name|d
parameter_list|)
block|{
if|if
condition|(
name|d
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|d
operator|.
name|list
argument_list|()
control|)
block|{
name|File
name|child
init|=
operator|new
name|File
argument_list|(
name|d
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|child
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|deleteRecursively
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|d
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates the SPNEGO JAAS configuration file for the Jetty server    */
specifier|public
specifier|static
name|void
name|writeSpnegoConf
parameter_list|(
name|File
name|configFile
parameter_list|,
name|File
name|serverKeytab
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|configFile
argument_list|)
argument_list|)
init|)
block|{
comment|// Server login
name|writer
operator|.
name|write
argument_list|(
literal|"com.sun.security.jgss.accept {\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" com.sun.security.auth.module.Krb5LoginModule required\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" principal=\""
operator|+
name|SERVER_PRINCIPAL
operator|+
literal|"\"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" useKeyTab=true\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" keyTab=\""
operator|+
name|serverKeytab
operator|+
literal|"\"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|" storeKey=true \n"
argument_list|)
expr_stmt|;
comment|// Some extra debug information from JAAS
comment|//writer.write(" debug=true\n");
name|writer
operator|.
name|write
argument_list|(
literal|" isInitiator=false;\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"};\n"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|refreshJaasConfiguration
parameter_list|()
block|{
comment|// This is *extremely* important to make sure we get the right Configuration instance.
comment|// Configuration keeps a static instance of Configuration that it will return once it
comment|// has been initialized. We need to nuke that static instance to make sure our
comment|// serverSpnegoConfigFile gets read.
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Configuration
argument_list|>
argument_list|()
block|{
specifier|public
name|Configuration
name|run
parameter_list|()
block|{
return|return
name|Configuration
operator|.
name|getConfiguration
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/**    * A simple handler which returns "OK " with the client's authenticated name and HTTP/200 or    * HTTP/401 and the message "Not authenticated!".    */
specifier|public
specifier|static
class|class
name|AuthenticationRequiredAvaticaHandler
implements|implements
name|AvaticaHandler
block|{
specifier|private
specifier|final
name|Handler
name|handler
init|=
operator|new
name|DefaultHandler
argument_list|()
decl_stmt|;
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
name|Authentication
name|auth
init|=
name|baseRequest
operator|.
name|getAuthentication
argument_list|()
decl_stmt|;
if|if
condition|(
name|Authentication
operator|.
name|UNAUTHENTICATED
operator|==
name|auth
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unauthenticated users should not reach here!"
argument_list|)
throw|;
block|}
name|baseRequest
operator|.
name|setHandled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|UserAuthentication
name|userAuth
init|=
operator|(
name|UserAuthentication
operator|)
name|auth
decl_stmt|;
name|UserIdentity
name|userIdentity
init|=
name|userAuth
operator|.
name|getUserIdentity
argument_list|()
decl_stmt|;
name|Principal
name|userPrincipal
init|=
name|userIdentity
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|print
argument_list|(
literal|"OK "
operator|+
name|userPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
literal|200
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
block|}
block|}
block|}
end_class

begin_comment
comment|// End SpnegoTestUtil.java
end_comment

end_unit

