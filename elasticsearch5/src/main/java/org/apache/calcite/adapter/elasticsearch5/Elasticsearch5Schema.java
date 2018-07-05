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
name|elasticsearch5
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
name|adapter
operator|.
name|elasticsearch
operator|.
name|ElasticsearchSchema
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
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
name|annotations
operator|.
name|VisibleForTesting
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|indices
operator|.
name|get
operator|.
name|GetIndexRequest
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
name|indices
operator|.
name|mapping
operator|.
name|get
operator|.
name|GetMappingsRequest
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
name|indices
operator|.
name|mapping
operator|.
name|get
operator|.
name|GetMappingsResponse
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
name|client
operator|.
name|transport
operator|.
name|TransportClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MappingMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|InetSocketTransportAddress
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
name|transport
operator|.
name|client
operator|.
name|PreBuiltTransportClient
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Schema mapped onto an index of ELASTICSEARCH types.  *  *<p>Each table in the schema is an ELASTICSEARCH type in that index.  */
end_comment

begin_class
specifier|public
class|class
name|Elasticsearch5Schema
extends|extends
name|AbstractSchema
implements|implements
name|ElasticsearchSchema
block|{
specifier|final
name|String
name|index
decl_stmt|;
specifier|private
specifier|transient
name|Client
name|client
decl_stmt|;
comment|/**    * Creates an Elasticsearch5 schema.    *    * @param coordinates Map of Elasticsearch node locations (host, port)    * @param userConfig Map of user-specified configurations    * @param indexName Elasticsearch database name, e.g. "usa".    */
name|Elasticsearch5Schema
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|coordinates
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userConfig
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|transportAddresses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|coordinate
range|:
name|coordinates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|transportAddresses
operator|.
name|add
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|coordinate
operator|.
name|getKey
argument_list|()
argument_list|,
name|coordinate
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|open
argument_list|(
name|transportAddresses
argument_list|,
name|userConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
index|[]
name|indices
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|getIndex
argument_list|(
operator|new
name|GetIndexRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|indexName
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
operator|.
name|getIndices
argument_list|()
decl_stmt|;
if|if
condition|(
name|indices
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|index
operator|=
name|indices
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|index
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Allows schema to be instantiated from existing elastic search client.    * This constructor is used in tests.    * @param client existing client instance    * @param index name of ES index    */
annotation|@
name|VisibleForTesting
name|Elasticsearch5Schema
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|client
argument_list|,
literal|"client"
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|GetMappingsResponse
name|response
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|getMappings
argument_list|(
operator|new
name|GetMappingsRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mapping
init|=
name|response
operator|.
name|getMappings
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|c
range|:
name|mapping
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|c
operator|.
name|key
argument_list|,
operator|new
name|Elasticsearch5Table
argument_list|(
name|client
argument_list|,
name|index
argument_list|,
name|c
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
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
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|void
name|open
parameter_list|(
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|transportAddresses
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userConfig
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|TransportAddress
argument_list|>
name|transportNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|transportAddresses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|transportAddresses
control|)
block|{
name|transportNodes
operator|.
name|add
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
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
name|userConfig
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|TransportClient
name|transportClient
init|=
operator|new
name|PreBuiltTransportClient
argument_list|(
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|TransportAddress
name|transport
range|:
name|transportNodes
control|)
block|{
name|transportClient
operator|.
name|addTransportAddress
argument_list|(
name|transport
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|transportClient
operator|.
name|connectedNodes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot connect to any elasticsearch node from "
operator|+
name|transportNodes
argument_list|)
throw|;
block|}
name|client
operator|=
name|transportClient
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
block|}
end_class

begin_comment
comment|// End Elasticsearch5Schema.java
end_comment

end_unit

