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
name|elasticsearch
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
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodesInfoResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|InternalSettingsPreparer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|PainlessPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|Netty4Plugin
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_comment
comment|/**  * Represents a single elastic search node which can run embedded in a java application.  *  *<p>Intended for unit and integration tests. Settings and plugins are crafted for Calcite.  */
end_comment

begin_class
class|class
name|EmbeddedElasticsearchNode
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|isStarted
decl_stmt|;
specifier|private
name|EmbeddedElasticsearchNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|node
argument_list|,
literal|"node"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an instance with existing settings.    *    * @param settings Configuration parameters of ES instance    *    * @return instance that needs to be explicitly started (using    * {@link #start()})    */
specifier|private
specifier|static
name|EmbeddedElasticsearchNode
name|create
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
comment|// ensure PainlessPlugin is installed or otherwise scripted fields would not work
name|Node
name|node
init|=
operator|new
name|LocalNode
argument_list|(
name|settings
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|Netty4Plugin
operator|.
name|class
argument_list|,
name|PainlessPlugin
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EmbeddedElasticsearchNode
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/**    * Creates elastic node as single member of a cluster. Node will not be started    * unless {@link #start()} is explicitly called.    *<p>Need {@code synchronized} because of static caches inside ES (which are not thread safe).    * @return instance which needs to be explicitly started (using {@link #start()})    */
specifier|public
specifier|static
specifier|synchronized
name|EmbeddedElasticsearchNode
name|create
parameter_list|()
block|{
name|File
name|data
decl_stmt|;
name|File
name|home
decl_stmt|;
try|try
block|{
name|data
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"es-data"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|data
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|home
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"es-home"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|home
operator|.
name|deleteOnExit
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
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"fake-elastic"
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|home
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.data"
argument_list|,
name|data
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"http.type"
argument_list|,
literal|"netty4"
argument_list|)
comment|// allow multiple instances to run in parallel
operator|.
name|put
argument_list|(
literal|"transport.tcp.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"http.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.host"
argument_list|,
literal|"localhost"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|create
argument_list|(
name|settings
argument_list|)
return|;
block|}
comment|/** Starts the current node. */
specifier|public
name|void
name|start
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isStarted
argument_list|,
literal|"already started"
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|isStarted
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeValidationException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns current address to connect to with HTTP client.    * @return hostname/port for HTTP connection    */
specifier|public
name|TransportAddress
name|httpAddress
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isStarted
argument_list|,
literal|"node is not started"
argument_list|)
expr_stmt|;
name|NodesInfoResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Expected single node but got "
operator|+
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|NodeInfo
name|node
init|=
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|node
operator|.
name|getHttp
argument_list|()
operator|.
name|address
argument_list|()
operator|.
name|boundAddresses
argument_list|()
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Exposes elastic    *<a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html">transport client</a>    * (use of HTTP client is preferred).    *    * @return current elastic search client    */
specifier|public
name|Client
name|client
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isStarted
argument_list|,
literal|"node is not started"
argument_list|)
expr_stmt|;
return|return
name|node
operator|.
name|client
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// cleanup data dirs
for|for
control|(
name|String
name|name
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"path.data"
argument_list|,
literal|"path.home"
argument_list|)
control|)
block|{
if|if
condition|(
name|node
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|node
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Having separate class to expose (protected) constructor which allows to install    * different plugins. In our case it is {@code GroovyPlugin} for scripted fields    * like {@code loc[0]} or {@code loc[1]['foo']}.    *    *<p>This class is intended solely for tests    */
specifier|private
specifier|static
class|class
name|LocalNode
extends|extends
name|Node
block|{
specifier|private
name|LocalNode
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|classpathPlugins
parameter_list|)
block|{
name|super
argument_list|(
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|settings
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
parameter_list|()
lambda|->
literal|"default_node_name"
argument_list|)
argument_list|,
name|classpathPlugins
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

