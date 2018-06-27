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
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonNode
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
name|collect
operator|.
name|ImmutableMap
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
name|Sets
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
name|Response
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Schema mapped onto an index of ELASTICSEARCH types.  *  *<p>Each table in the schema is an ELASTICSEARCH type in that index.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchSchema
extends|extends
name|AbstractSchema
block|{
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
specifier|private
specifier|final
name|RestClient
name|client
decl_stmt|;
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
comment|/**    * Allows schema to be instantiated from existing elastic search client.    * This constructor is used in tests.    * @param client existing client instance    * @param mapper mapper for JSON (de)serialization    * @param index name of ES index    */
specifier|public
name|ElasticsearchSchema
parameter_list|(
name|RestClient
name|client
parameter_list|,
name|ObjectMapper
name|mapper
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
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
name|mapper
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mapper
argument_list|,
literal|"mapper"
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
for|for
control|(
name|String
name|type
range|:
name|listTypes
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|type
argument_list|,
operator|new
name|ElasticsearchTable
argument_list|(
name|client
argument_list|,
name|mapper
argument_list|,
name|index
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
literal|"Failed to get types for "
operator|+
name|index
argument_list|,
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
comment|/**    * Queries {@code _mapping} definition to automatically detect all types for an index    *    * @return list of types associated with this index    * @throws IOException for any IO related issues    * @throws IllegalStateException if reply is not understood    */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|listTypes
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|endpoint
init|=
literal|"/"
operator|+
name|index
operator|+
literal|"/_mapping"
decl_stmt|;
specifier|final
name|Response
name|response
init|=
name|client
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
name|endpoint
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
init|)
block|{
name|JsonNode
name|root
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|isObject
argument_list|()
operator|||
name|root
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid response for %s/%s "
operator|+
literal|"Expected object of size 1 got %s (of size %d)"
argument_list|,
name|response
operator|.
name|getHost
argument_list|()
argument_list|,
name|response
operator|.
name|getRequestLine
argument_list|()
argument_list|,
name|root
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|root
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|JsonNode
name|mappings
init|=
name|root
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
literal|"mappings"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappings
operator|==
literal|null
operator|||
name|mappings
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Index %s does not have any types"
argument_list|,
name|index
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|mappings
operator|.
name|fieldNames
argument_list|()
argument_list|)
decl_stmt|;
name|types
operator|.
name|remove
argument_list|(
literal|"_default_"
argument_list|)
expr_stmt|;
return|return
name|types
return|;
block|}
block|}
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
comment|// End ElasticsearchSchema.java
end_comment

end_unit

