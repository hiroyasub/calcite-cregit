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
name|runtime
operator|.
name|Hook
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
name|HttpEntityEnclosingRequest
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
name|HttpRequest
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
name|HttpStatus
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
name|client
operator|.
name|methods
operator|.
name|HttpDelete
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
name|client
operator|.
name|methods
operator|.
name|HttpEntityEnclosingRequestBase
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
name|client
operator|.
name|methods
operator|.
name|HttpGet
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
name|client
operator|.
name|methods
operator|.
name|HttpPost
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
name|client
operator|.
name|utils
operator|.
name|URIBuilder
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|JsonProcessingException
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
name|ArrayNode
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
name|TextNode
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|function
operator|.
name|Function
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
name|StreamSupport
import|;
end_import

begin_comment
comment|/**  * Set of predefined functions for REST interaction with elastic search API. Performs  * HTTP requests and JSON (de)serialization.  */
end_comment

begin_class
specifier|final
class|class
name|ElasticsearchTransport
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
name|ElasticsearchTable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|int
name|DEFAULT_FETCH_SIZE
init|=
literal|5196
decl_stmt|;
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|final
name|ElasticsearchVersion
name|version
decl_stmt|;
specifier|final
name|ElasticsearchMapping
name|mapping
decl_stmt|;
comment|/**    * Default batch size    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html">Scrolling API</a>    */
specifier|final
name|int
name|fetchSize
decl_stmt|;
name|ElasticsearchTransport
parameter_list|(
specifier|final
name|RestClient
name|restClient
parameter_list|,
specifier|final
name|ObjectMapper
name|mapper
parameter_list|,
specifier|final
name|String
name|indexName
parameter_list|,
specifier|final
name|int
name|fetchSize
parameter_list|)
block|{
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
name|restClient
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|restClient
argument_list|,
literal|"restClient"
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indexName
argument_list|,
literal|"indexName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchSize
operator|=
name|fetchSize
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
argument_list|()
expr_stmt|;
comment|// cache version
name|this
operator|.
name|mapping
operator|=
name|fetchAndCreateMapping
argument_list|()
expr_stmt|;
comment|// cache mapping
block|}
name|RestClient
name|restClient
parameter_list|()
block|{
return|return
name|this
operator|.
name|restClient
return|;
block|}
comment|/**    * Detects current Elastic Search version by connecting to a existing instance.    * It is a {@code GET} request to {@code /}. Returned JSON has server information    * (including version).    *    * @return parsed version from ES, or {@link ElasticsearchVersion#UNKNOWN}    */
specifier|private
name|ElasticsearchVersion
name|version
parameter_list|()
block|{
specifier|final
name|HttpRequest
name|request
init|=
operator|new
name|HttpGet
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|// version extract function
specifier|final
name|Function
argument_list|<
name|ObjectNode
argument_list|,
name|ElasticsearchVersion
argument_list|>
name|fn
init|=
name|node
lambda|->
name|ElasticsearchVersion
operator|.
name|fromString
argument_list|(
name|node
operator|.
name|get
argument_list|(
literal|"version"
argument_list|)
operator|.
name|get
argument_list|(
literal|"number"
argument_list|)
operator|.
name|asText
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|rawHttp
argument_list|(
name|ObjectNode
operator|.
name|class
argument_list|)
operator|.
name|andThen
argument_list|(
name|fn
argument_list|)
operator|.
name|apply
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**    * Build index mapping returning new instance of {@link ElasticsearchMapping}.    */
specifier|private
name|ElasticsearchMapping
name|fetchAndCreateMapping
parameter_list|()
block|{
specifier|final
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
literal|"/%s/_mapping"
argument_list|,
name|indexName
argument_list|)
decl_stmt|;
specifier|final
name|ObjectNode
name|root
init|=
name|rawHttp
argument_list|(
name|ObjectNode
operator|.
name|class
argument_list|)
operator|.
name|apply
argument_list|(
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
name|ObjectNode
name|properties
init|=
operator|(
name|ObjectNode
operator|)
name|root
operator|.
name|elements
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
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ElasticsearchJson
operator|.
name|visitMappingProperties
argument_list|(
name|properties
argument_list|,
name|builder
operator|::
name|put
argument_list|)
expr_stmt|;
return|return
operator|new
name|ElasticsearchMapping
argument_list|(
name|indexName
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
name|ObjectMapper
name|mapper
parameter_list|()
block|{
return|return
name|mapper
return|;
block|}
name|Function
argument_list|<
name|HttpRequest
argument_list|,
name|Response
argument_list|>
name|rawHttp
parameter_list|()
block|{
return|return
operator|new
name|HttpFunction
argument_list|(
name|restClient
argument_list|)
return|;
block|}
parameter_list|<
name|T
parameter_list|>
name|Function
argument_list|<
name|HttpRequest
argument_list|,
name|T
argument_list|>
name|rawHttp
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|responseType
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|responseType
argument_list|,
literal|"responseType"
argument_list|)
expr_stmt|;
return|return
name|rawHttp
argument_list|()
operator|.
name|andThen
argument_list|(
operator|new
name|JsonParserFn
argument_list|<>
argument_list|(
name|mapper
argument_list|,
name|responseType
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Fetches search results given a scrollId.    */
name|Function
argument_list|<
name|String
argument_list|,
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|scroll
parameter_list|()
block|{
return|return
name|scrollId
lambda|->
block|{
comment|// fetch next scroll
specifier|final
name|HttpPost
name|request
init|=
operator|new
name|HttpPost
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"/_search/scroll"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ObjectNode
name|payload
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
operator|.
name|put
argument_list|(
literal|"scroll"
argument_list|,
literal|"1m"
argument_list|)
operator|.
name|put
argument_list|(
literal|"scroll_id"
argument_list|,
name|scrollId
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|json
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|request
operator|.
name|setEntity
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|json
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rawHttp
argument_list|(
name|ElasticsearchJson
operator|.
name|Result
operator|.
name|class
argument_list|)
operator|.
name|apply
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
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
literal|"Couldn't fetch next scroll %s"
argument_list|,
name|scrollId
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
name|void
name|closeScroll
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|scrollIds
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scrollIds
argument_list|,
literal|"scrollIds"
argument_list|)
expr_stmt|;
comment|// delete current scroll
specifier|final
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"/_search/scroll"
argument_list|)
decl_stmt|;
comment|// http DELETE with payload
specifier|final
name|HttpEntityEnclosingRequestBase
name|request
init|=
operator|new
name|HttpEntityEnclosingRequestBase
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|HttpDelete
operator|.
name|METHOD_NAME
return|;
block|}
block|}
decl_stmt|;
name|request
operator|.
name|setURI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
specifier|final
name|ObjectNode
name|payload
init|=
name|mapper
argument_list|()
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
comment|// ES2 expects json array for DELETE scroll API
specifier|final
name|ArrayNode
name|array
init|=
name|payload
operator|.
name|withArray
argument_list|(
literal|"scroll_id"
argument_list|)
decl_stmt|;
name|StreamSupport
operator|.
name|stream
argument_list|(
name|scrollIds
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|TextNode
operator|::
operator|new
argument_list|)
operator|.
name|forEach
argument_list|(
name|array
operator|::
name|add
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|json
init|=
name|mapper
argument_list|()
operator|.
name|writeValueAsString
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|request
operator|.
name|setEntity
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|json
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|rawHttp
argument_list|()
operator|.
name|apply
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|UncheckedIOException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Failed to close scroll(s): {}"
argument_list|,
name|scrollIds
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|Function
argument_list|<
name|ObjectNode
argument_list|,
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|search
parameter_list|()
block|{
return|return
name|search
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Search request using HTTP post.    */
name|Function
argument_list|<
name|ObjectNode
argument_list|,
name|ElasticsearchJson
operator|.
name|Result
argument_list|>
name|search
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|httpParams
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|httpParams
argument_list|,
literal|"httpParams"
argument_list|)
expr_stmt|;
return|return
name|query
lambda|->
block|{
name|Hook
operator|.
name|QUERY_PLAN
operator|.
name|run
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"/%s/_search"
argument_list|,
name|indexName
argument_list|)
decl_stmt|;
specifier|final
name|HttpPost
name|post
decl_stmt|;
try|try
block|{
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|httpParams
operator|.
name|forEach
argument_list|(
name|builder
operator|::
name|addParameter
argument_list|)
expr_stmt|;
name|post
operator|=
operator|new
name|HttpPost
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|json
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Elasticsearch Query: {}"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|post
operator|.
name|setEntity
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|json
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
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
catch|catch
parameter_list|(
name|JsonProcessingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|rawHttp
argument_list|(
name|ElasticsearchJson
operator|.
name|Result
operator|.
name|class
argument_list|)
operator|.
name|apply
argument_list|(
name|post
argument_list|)
return|;
block|}
return|;
block|}
comment|/**    * Parses HTTP response into some class using jackson API.    * @param<T> result type    */
specifier|private
specifier|static
class|class
name|JsonParserFn
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Function
argument_list|<
name|Response
argument_list|,
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|klass
decl_stmt|;
name|JsonParserFn
parameter_list|(
specifier|final
name|ObjectMapper
name|mapper
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|klass
operator|=
name|klass
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|apply
parameter_list|(
specifier|final
name|Response
name|response
parameter_list|)
block|{
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
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|is
argument_list|,
name|klass
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Couldn't parse HTTP response %s into %s"
argument_list|,
name|response
argument_list|,
name|klass
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Basic rest operations interacting with elastic cluster.    */
specifier|private
specifier|static
class|class
name|HttpFunction
implements|implements
name|Function
argument_list|<
name|HttpRequest
argument_list|,
name|Response
argument_list|>
block|{
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
name|HttpFunction
parameter_list|(
specifier|final
name|RestClient
name|restClient
parameter_list|)
block|{
name|this
operator|.
name|restClient
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|restClient
argument_list|,
literal|"restClient"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Response
name|apply
parameter_list|(
specifier|final
name|HttpRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|applyInternal
argument_list|(
name|request
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
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Response
name|applyInternal
parameter_list|(
specifier|final
name|HttpRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|request
argument_list|,
literal|"request"
argument_list|)
expr_stmt|;
specifier|final
name|HttpEntity
name|entity
init|=
name|request
operator|instanceof
name|HttpEntityEnclosingRequest
condition|?
operator|(
operator|(
name|HttpEntityEnclosingRequest
operator|)
name|request
operator|)
operator|.
name|getEntity
argument_list|()
else|:
literal|null
decl_stmt|;
specifier|final
name|Request
name|r
init|=
operator|new
name|Request
argument_list|(
name|request
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getMethod
argument_list|()
argument_list|,
name|request
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
specifier|final
name|Response
name|response
init|=
name|restClient
operator|.
name|performRequest
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|String
name|payload
init|=
name|entity
operator|!=
literal|null
operator|&&
name|entity
operator|.
name|isRepeatable
argument_list|()
condition|?
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
else|:
literal|"<empty>"
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
specifier|final
name|String
name|error
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Error while querying Elastic (on %s/%s) status: %s\nPayload:\n%s\nError:\n%s\n"
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
name|response
operator|.
name|getStatusLine
argument_list|()
argument_list|,
name|payload
argument_list|,
name|error
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
block|}
block|}
end_class

end_unit

