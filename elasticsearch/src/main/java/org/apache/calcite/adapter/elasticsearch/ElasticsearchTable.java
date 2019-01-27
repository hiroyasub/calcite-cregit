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
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelOptTable
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
name|rel
operator|.
name|RelFieldCollation
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
name|rel
operator|.
name|RelNode
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|calcite
operator|.
name|schema
operator|.
name|TranslatableTable
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
name|AbstractTableQueryable
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
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
name|UncheckedIOException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Set
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
name|Consumer
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
name|Predicate
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
comment|/**  * Table based on an Elasticsearch type.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
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
comment|/**    * Used for constructing (possibly nested) Elastic aggregation nodes.    */
specifier|private
specifier|static
specifier|final
name|String
name|AGGREGATIONS
init|=
literal|"aggregations"
decl_stmt|;
specifier|private
specifier|final
name|ElasticsearchVersion
name|version
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|private
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
specifier|final
name|ElasticsearchTransport
name|transport
decl_stmt|;
comment|/**    * Creates an ElasticsearchTable.    */
name|ElasticsearchTable
parameter_list|(
name|ElasticsearchTransport
name|transport
parameter_list|)
block|{
name|super
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transport
argument_list|,
literal|"transport"
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|transport
operator|.
name|version
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|transport
operator|.
name|indexName
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|transport
operator|.
name|typeName
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|transport
operator|.
name|mapper
argument_list|()
expr_stmt|;
block|}
comment|/**    * In ES 5.x scripted fields start with {@code params._source.foo} while in ES2.x    * {@code _source.foo}. Helper method to build correct query based on runtime version of elastic.    * Used to keep backwards compatibility with ES2.    *    * @see<a href="https://github.com/elastic/elasticsearch/issues/20068">_source variable</a>    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting-fields.html">Scripted Fields</a>    * @return string to be used for scripted fields    */
name|String
name|scriptedFieldPrefix
parameter_list|()
block|{
comment|// ES2 vs ES5 scripted field difference
return|return
name|version
operator|==
name|ElasticsearchVersion
operator|.
name|ES2
condition|?
name|ElasticsearchConstants
operator|.
name|SOURCE_GROOVY
else|:
name|ElasticsearchConstants
operator|.
name|SOURCE_PAINLESS
return|;
block|}
comment|/**    * Executes a "find" operation on the underlying type.    *    *<p>For example,    *<code>client.prepareSearch(index).setTypes(type)    * .setSource("{\"fields\" : [\"state\"]}")</code></p>    *    * @param ops List of operations represented as Json strings.    * @param fields List of fields to project; or null to return map    * @param sort list of fields to sort and their direction (asc/desc)    * @param aggregations aggregation functions    * @return Enumerator of results    */
specifier|private
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|find
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ops
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
argument_list|>
argument_list|>
name|sort
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|groupBy
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|aggregations
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|,
name|Long
name|offset
parameter_list|,
name|Long
name|fetch
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|aggregations
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|groupBy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// process aggregations separately
return|return
name|aggregate
argument_list|(
name|ops
argument_list|,
name|fields
argument_list|,
name|sort
argument_list|,
name|groupBy
argument_list|,
name|aggregations
argument_list|,
name|mappings
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|final
name|ObjectNode
name|query
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
comment|// manually parse from previously concatenated string
for|for
control|(
name|String
name|op
range|:
name|ops
control|)
block|{
name|query
operator|.
name|setAll
argument_list|(
operator|(
name|ObjectNode
operator|)
name|mapper
operator|.
name|readTree
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|sort
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ArrayNode
name|sortNode
init|=
name|query
operator|.
name|withArray
argument_list|(
literal|"sort"
argument_list|)
decl_stmt|;
name|sort
operator|.
name|forEach
argument_list|(
name|e
lambda|->
name|sortNode
operator|.
name|add
argument_list|(
name|mapper
operator|.
name|createObjectNode
argument_list|()
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|isDescending
argument_list|()
condition|?
literal|"desc"
else|:
literal|"asc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|put
argument_list|(
literal|"from"
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fetch
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Object
argument_list|>
name|getter
init|=
name|ElasticsearchEnumerators
operator|.
name|getter
argument_list|(
name|fields
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mappings
argument_list|)
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|>
name|iter
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|null
condition|)
block|{
comment|// apply scrolling when there is no offsets
name|iter
operator|=
parameter_list|()
lambda|->
operator|new
name|Scrolling
argument_list|(
name|transport
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ElasticsearchJson
operator|.
name|Result
name|search
init|=
name|transport
operator|.
name|search
argument_list|()
operator|.
name|apply
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|iter
operator|=
parameter_list|()
lambda|->
name|search
operator|.
name|searchHits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|iter
argument_list|)
operator|.
name|select
argument_list|(
name|getter
argument_list|)
return|;
block|}
specifier|private
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|aggregate
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ops
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
argument_list|>
argument_list|>
name|sort
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|groupBy
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|aggregations
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
parameter_list|,
name|Long
name|offset
parameter_list|,
name|Long
name|fetch
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|groupBy
operator|.
name|isEmpty
argument_list|()
operator|&&
name|offset
operator|!=
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Currently ES doesn't support generic pagination "
operator|+
literal|"with aggregations. You can still use LIMIT keyword (without OFFSET). "
operator|+
literal|"For more details see https://github.com/elastic/elasticsearch/issues/4915"
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
specifier|final
name|ObjectNode
name|query
init|=
name|mapper
operator|.
name|createObjectNode
argument_list|()
decl_stmt|;
comment|// manually parse into JSON from previously concatenated strings
for|for
control|(
name|String
name|op
range|:
name|ops
control|)
block|{
name|query
operator|.
name|setAll
argument_list|(
operator|(
name|ObjectNode
operator|)
name|mapper
operator|.
name|readTree
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove / override attributes which are not applicable to aggregations
name|query
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|query
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"script_fields"
argument_list|)
expr_stmt|;
comment|// allows to detect aggregation for count(*)
specifier|final
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|isCountStar
init|=
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"\""
operator|+
name|ElasticsearchConstants
operator|.
name|ID
operator|+
literal|"\""
argument_list|)
decl_stmt|;
comment|// list of expressions which are count(*)
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|countAll
init|=
name|aggregations
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|isCountStar
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// due to ES aggregation format. fields in "order by" clause should go first
comment|// if "order by" is missing. order in "group by" is un-important
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|orderedGroupBy
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|orderedGroupBy
operator|.
name|addAll
argument_list|(
name|sort
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|orderedGroupBy
operator|.
name|addAll
argument_list|(
name|groupBy
argument_list|)
expr_stmt|;
comment|// construct nested aggregations node(s)
name|ObjectNode
name|parent
init|=
name|query
operator|.
name|with
argument_list|(
name|AGGREGATIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|orderedGroupBy
control|)
block|{
specifier|final
name|String
name|aggName
init|=
literal|"g_"
operator|+
name|name
decl_stmt|;
name|fieldMap
operator|.
name|put
argument_list|(
name|aggName
argument_list|,
name|name
argument_list|)
expr_stmt|;
specifier|final
name|ObjectNode
name|section
init|=
name|parent
operator|.
name|with
argument_list|(
name|aggName
argument_list|)
decl_stmt|;
specifier|final
name|ObjectNode
name|terms
init|=
name|section
operator|.
name|with
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|terms
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|transport
operator|.
name|mapping
operator|.
name|missingValueFor
argument_list|(
name|name
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|m
lambda|->
block|{
comment|// expose missing terms. each type has a different missing value
name|terms
operator|.
name|set
argument_list|(
literal|"missing"
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|fetch
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
block|}
name|sort
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|findAny
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|s
lambda|->
name|terms
operator|.
name|with
argument_list|(
literal|"order"
argument_list|)
operator|.
name|put
argument_list|(
literal|"_key"
argument_list|,
name|s
operator|.
name|getValue
argument_list|()
operator|.
name|isDescending
argument_list|()
condition|?
literal|"desc"
else|:
literal|"asc"
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|=
name|section
operator|.
name|with
argument_list|(
name|AGGREGATIONS
argument_list|)
expr_stmt|;
block|}
comment|// simple version for queries like "select count(*), max(col1) from table" (no GROUP BY cols)
if|if
condition|(
operator|!
name|groupBy
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|aggregations
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|isCountStar
argument_list|)
condition|)
block|{
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
name|aggregation
range|:
name|aggregations
control|)
block|{
name|JsonNode
name|value
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|aggregation
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|parent
operator|.
name|set
argument_list|(
name|aggregation
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Consumer
argument_list|<
name|JsonNode
argument_list|>
name|emptyAggRemover
init|=
operator|new
name|Consumer
argument_list|<
name|JsonNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|JsonNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|has
argument_list|(
name|AGGREGATIONS
argument_list|)
condition|)
block|{
name|node
operator|.
name|elements
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
name|JsonNode
name|agg
init|=
name|node
operator|.
name|get
argument_list|(
name|AGGREGATIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|agg
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
operator|(
operator|(
name|ObjectNode
operator|)
name|node
operator|)
operator|.
name|remove
argument_list|(
name|AGGREGATIONS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|accept
argument_list|(
name|agg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
comment|// cleanup query. remove empty AGGREGATIONS element (if empty)
name|emptyAggRemover
operator|.
name|accept
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|ElasticsearchJson
operator|.
name|Result
name|res
init|=
name|transport
operator|.
name|search
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
operator|.
name|apply
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|aggregations
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// collect values
name|ElasticsearchJson
operator|.
name|visitValueNodes
argument_list|(
name|res
operator|.
name|aggregations
argument_list|()
argument_list|,
name|m
lambda|->
block|{
comment|// using 'Collectors.toMap' will trigger Java 8 bug here
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|newMap
operator|.
name|put
argument_list|(
name|fieldMap
operator|.
name|getOrDefault
argument_list|(
name|key
argument_list|,
name|key
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|newMap
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// probably no group by. add single result
name|result
operator|.
name|add
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// elastic exposes total number of documents matching a query in "/hits/total" path
comment|// this can be used for simple "select count(*) from table"
specifier|final
name|long
name|total
init|=
name|res
operator|.
name|searchHits
argument_list|()
operator|.
name|total
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupBy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// put totals automatically for count(*) expression(s), unless they contain group by
for|for
control|(
name|String
name|expr
range|:
name|countAll
control|)
block|{
name|result
operator|.
name|forEach
argument_list|(
name|m
lambda|->
name|m
operator|.
name|put
argument_list|(
name|expr
argument_list|,
name|total
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Function1
argument_list|<
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|,
name|Object
argument_list|>
name|getter
init|=
name|ElasticsearchEnumerators
operator|.
name|getter
argument_list|(
name|fields
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ElasticsearchJson
operator|.
name|SearchHits
name|hits
init|=
operator|new
name|ElasticsearchJson
operator|.
name|SearchHits
argument_list|(
name|total
argument_list|,
name|result
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|r
lambda|->
operator|new
name|ElasticsearchJson
operator|.
name|SearchHit
argument_list|(
literal|"_id"
argument_list|,
name|r
argument_list|,
literal|null
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
argument_list|)
decl_stmt|;
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|hits
operator|.
name|hits
argument_list|()
argument_list|)
operator|.
name|select
argument_list|(
name|getter
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|relDataTypeFactory
parameter_list|)
block|{
specifier|final
name|RelDataType
name|mapType
init|=
name|relDataTypeFactory
operator|.
name|createMapType
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|relDataTypeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|relDataTypeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"_MAP"
argument_list|,
name|mapType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ElasticsearchTable{"
operator|+
name|indexName
operator|+
literal|"/"
operator|+
name|typeName
operator|+
literal|"}"
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|ElasticsearchQueryable
argument_list|<>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|context
operator|.
name|getCluster
argument_list|()
decl_stmt|;
return|return
operator|new
name|ElasticsearchTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Implementation of {@link Queryable} based on    * a {@link ElasticsearchTable}.    *    * @param<T> element type    */
specifier|public
specifier|static
class|class
name|ElasticsearchQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
block|{
name|ElasticsearchQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|ElasticsearchTable
name|table
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|table
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|private
name|ElasticsearchTable
name|getTable
parameter_list|()
block|{
return|return
operator|(
name|ElasticsearchTable
operator|)
name|table
return|;
block|}
comment|/** Called via code-generation.      * @param ops list of queries (as strings)      * @param fields projection      * @see ElasticsearchMethod#ELASTICSEARCH_QUERYABLE_FIND      * @return result as enumerable      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|find
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ops
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
argument_list|>
argument_list|>
name|sort
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|groupBy
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|aggregations
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|,
name|Long
name|offset
parameter_list|,
name|Long
name|fetch
parameter_list|)
block|{
try|try
block|{
return|return
name|getTable
argument_list|()
operator|.
name|find
argument_list|(
name|ops
argument_list|,
name|fields
argument_list|,
name|sort
argument_list|,
name|groupBy
argument_list|,
name|aggregations
argument_list|,
name|mappings
argument_list|,
name|offset
argument_list|,
name|fetch
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
literal|"Failed to query "
operator|+
name|getTable
argument_list|()
operator|.
name|indexName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End ElasticsearchTable.java
end_comment

end_unit

