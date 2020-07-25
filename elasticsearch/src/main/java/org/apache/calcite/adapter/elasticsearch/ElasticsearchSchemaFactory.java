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
name|schema
operator|.
name|Schema
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
name|SchemaFactory
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
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|type
operator|.
name|TypeReference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|client
operator|.
name|RestClient
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
name|RestClientBuilder
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Factory that creates an {@link ElasticsearchSchema}.  *  *<p>Allows a custom schema to be included in a model.json file.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|ElasticsearchSchemaFactory
implements|implements
name|SchemaFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ElasticsearchSchemaFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|ElasticsearchSchemaFactory
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
specifier|final
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|operand
decl_stmt|;
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"hosts"
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|configHosts
init|=
name|mapper
operator|.
name|readValue
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"hosts"
argument_list|)
argument_list|,
operator|new
name|TypeReference
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{ }
argument_list|)
decl_stmt|;
name|hosts
operator|=
name|configHosts
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|host
lambda|->
name|HttpHost
operator|.
name|create
argument_list|(
name|host
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"coordinates"
argument_list|)
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|coordinates
init|=
name|mapper
operator|.
name|readValue
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"coordinates"
argument_list|)
argument_list|,
operator|new
name|TypeReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
block|{ }
argument_list|)
decl_stmt|;
name|hosts
operator|=
name|coordinates
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|entry
lambda|->
operator|new
name|HttpHost
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Prefer using hosts, coordinates is deprecated."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both 'coordinates' and 'hosts' is missing in configuration. Provide one of them."
argument_list|)
throw|;
block|}
specifier|final
name|String
name|pathPrefix
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"pathPrefix"
argument_list|)
decl_stmt|;
comment|// create client
specifier|final
name|RestClient
name|client
init|=
name|connect
argument_list|(
name|hosts
argument_list|,
name|pathPrefix
argument_list|)
decl_stmt|;
specifier|final
name|String
name|index
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
return|return
operator|new
name|ElasticsearchSchema
argument_list|(
name|client
argument_list|,
operator|new
name|ObjectMapper
argument_list|()
argument_list|,
name|index
argument_list|)
return|;
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
literal|"Cannot parse values from json"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Builds Elastic rest client from user configuration.    *    * @param hosts list of ES HTTP Hosts to connect to    * @return newly initialized low-level rest http client for ES    */
specifier|private
specifier|static
name|RestClient
name|connect
parameter_list|(
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
parameter_list|,
name|String
name|pathPrefix
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hosts
argument_list|,
literal|"hosts or coordinates"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|hosts
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"no ES hosts specified"
argument_list|)
expr_stmt|;
name|RestClientBuilder
name|builder
init|=
name|RestClient
operator|.
name|builder
argument_list|(
name|hosts
operator|.
name|toArray
argument_list|(
operator|new
name|HttpHost
index|[
name|hosts
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathPrefix
operator|!=
literal|null
operator|&&
operator|!
name|pathPrefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setPathPrefix
argument_list|(
name|pathPrefix
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

