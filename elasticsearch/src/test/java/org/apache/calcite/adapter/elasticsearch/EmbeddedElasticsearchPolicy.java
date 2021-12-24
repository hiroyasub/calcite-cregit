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
name|Closer
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
name|HttpEntity
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ContentType
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
name|entity
operator|.
name|StringEntity
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
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ObjectNode
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
name|Request
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
name|common
operator|.
name|transport
operator|.
name|TransportAddress
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

begin_comment
comment|/**  * Used to initialize a single elastic node. For performance reasons (node startup costs),  * same instance is shared across multiple tests (Elasticsearch does not allow multiple  * instances per JVM).  *  *<p>This rule should be used as follows:  *<pre>  *  public class MyTest {  *    public static final EmbeddedElasticsearchPolicy RULE = EmbeddedElasticsearchPolicy.create();  *  *&#64;BeforeClass  *    public static void setup() {  *       // ... populate instance  *       // The collections must have different names so the tests could be executed concurrently  *    }  *  *&#64;Test  *    public void myTest() {  *      RestClient client = RULE.restClient();  *      // ....  *    }  *  }  *</pre>  */
end_comment

begin_class
class|class
name|EmbeddedElasticsearchPolicy
block|{
specifier|private
specifier|final
name|EmbeddedElasticsearchNode
name|node
decl_stmt|;
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|private
name|RestClient
name|client
decl_stmt|;
comment|/** Holds the singleton policy instance. */
specifier|static
class|class
name|Singleton
block|{
specifier|static
specifier|final
name|EmbeddedElasticsearchPolicy
name|INSTANCE
init|=
operator|new
name|EmbeddedElasticsearchPolicy
argument_list|(
name|EmbeddedElasticsearchNode
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
block|}
specifier|private
name|EmbeddedElasticsearchPolicy
parameter_list|(
name|EmbeddedElasticsearchNode
name|resource
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
name|resource
argument_list|,
literal|"resource"
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|this
operator|.
name|closer
operator|=
operator|new
name|Closer
argument_list|()
expr_stmt|;
name|closer
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// initialize client
name|restClient
argument_list|()
expr_stmt|;
block|}
comment|/**    * Factory method to create this rule.    * @return managed resource to be used in unit tests    */
specifier|public
specifier|static
name|EmbeddedElasticsearchPolicy
name|create
parameter_list|()
block|{
return|return
name|Singleton
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Creates index in elastic search given a mapping. Mapping can contain nested fields expressed    * as dots({@code .}).    *    *<p>Example    *<pre>    *  {@code    *     b.a: long    *     b.b: keyword    *  }    *</pre>    *    * @param index index of the index    * @param mapping field and field type mapping    * @throws IOException if there is an error    */
name|void
name|createIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mapping
argument_list|,
literal|"mapping"
argument_list|)
expr_stmt|;
name|ObjectNode
name|mappings
init|=
name|mapper
argument_list|()
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
name|ObjectNode
name|properties
init|=
name|mappings
operator|.
name|with
argument_list|(
literal|"mappings"
argument_list|)
operator|.
name|with
argument_list|(
literal|"properties"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|mapping
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|applyMapping
argument_list|(
name|properties
argument_list|,
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
expr_stmt|;
block|}
comment|// create index and mapping
specifier|final
name|HttpEntity
name|entity
init|=
operator|new
name|StringEntity
argument_list|(
name|mapper
argument_list|()
operator|.
name|writeValueAsString
argument_list|(
name|mappings
argument_list|)
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|r
init|=
operator|new
name|Request
argument_list|(
literal|"PUT"
argument_list|,
literal|"/"
operator|+
name|index
argument_list|)
decl_stmt|;
name|r
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|restClient
argument_list|()
operator|.
name|performRequest
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates nested mappings for an index. This function is called recursively for each level.    *    * @param parent current parent    * @param key field name    * @param type ES mapping type ({@code keyword}, {@code long} etc.)    */
specifier|private
specifier|static
name|void
name|applyMapping
parameter_list|(
name|ObjectNode
name|parent
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|type
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>
operator|-
literal|1
condition|)
block|{
name|String
name|prefix
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
name|suffix
init|=
name|key
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|,
name|key
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|applyMapping
argument_list|(
name|parent
operator|.
name|with
argument_list|(
name|prefix
argument_list|)
operator|.
name|with
argument_list|(
literal|"properties"
argument_list|)
argument_list|,
name|suffix
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|with
argument_list|(
name|key
argument_list|)
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|insertDocument
parameter_list|(
name|String
name|index
parameter_list|,
name|ObjectNode
name|document
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|document
argument_list|,
literal|"document"
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"/%s/_doc?refresh"
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|StringEntity
name|entity
init|=
operator|new
name|StringEntity
argument_list|(
name|mapper
argument_list|()
operator|.
name|writeValueAsString
argument_list|(
name|document
argument_list|)
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|r
init|=
operator|new
name|Request
argument_list|(
literal|"POST"
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|r
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|restClient
argument_list|()
operator|.
name|performRequest
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|void
name|insertBulk
parameter_list|(
name|String
name|index
parameter_list|,
name|List
argument_list|<
name|ObjectNode
argument_list|>
name|documents
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|documents
argument_list|,
literal|"documents"
argument_list|)
expr_stmt|;
if|if
condition|(
name|documents
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing to process
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|bulk
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|documents
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectNode
name|doc
range|:
name|documents
control|)
block|{
name|bulk
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"{\"index\": {\"_index\":\"%s\"}}"
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|bulk
operator|.
name|add
argument_list|(
name|mapper
argument_list|()
operator|.
name|writeValueAsString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringEntity
name|entity
init|=
operator|new
name|StringEntity
argument_list|(
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|bulk
argument_list|)
operator|+
literal|"\n"
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|r
init|=
operator|new
name|Request
argument_list|(
literal|"POST"
argument_list|,
literal|"/_bulk?refresh"
argument_list|)
decl_stmt|;
name|r
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|restClient
argument_list|()
operator|.
name|performRequest
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
comment|/**    * Exposes Jackson API to be used to parse search results.    * @return existing instance of ObjectMapper    */
name|ObjectMapper
name|mapper
parameter_list|()
block|{
return|return
name|mapper
return|;
block|}
comment|/**    * Low-level http rest client connected to current embedded elastic search instance.    * @return http client connected to ES cluster    */
name|RestClient
name|restClient
parameter_list|()
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
return|return
name|client
return|;
block|}
specifier|final
name|RestClient
name|client
init|=
name|RestClient
operator|.
name|builder
argument_list|(
name|httpHost
argument_list|()
argument_list|)
operator|.
name|setRequestConfigCallback
argument_list|(
name|requestConfigBuilder
lambda|->
name|requestConfigBuilder
operator|.
name|setConnectTimeout
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
comment|// default 1000
operator|.
name|setSocketTimeout
argument_list|(
literal|3
operator|*
literal|60
operator|*
literal|1000
argument_list|)
argument_list|)
comment|// default 30000
operator|.
name|build
argument_list|()
decl_stmt|;
name|closer
operator|.
name|add
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
return|return
name|client
return|;
block|}
name|HttpHost
name|httpHost
parameter_list|()
block|{
specifier|final
name|TransportAddress
name|address
init|=
name|httpAddress
argument_list|()
decl_stmt|;
return|return
operator|new
name|HttpHost
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * HTTP address for rest clients (can be ES native or any other).    * @return http address to connect to    */
specifier|private
name|TransportAddress
name|httpAddress
parameter_list|()
block|{
return|return
name|node
operator|.
name|httpAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

