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
name|plan
operator|.
name|Convention
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|util
operator|.
name|Pair
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
comment|/**  * Relational expression that uses Elasticsearch calling convention.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ElasticsearchRel
extends|extends
name|RelNode
block|{
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
function_decl|;
comment|/**    * Calling convention for relational operations that occur in Elasticsearch.    */
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"ELASTICSEARCH"
argument_list|,
name|ElasticsearchRel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Callback for the implementation process that converts a tree of    * {@link ElasticsearchRel} nodes into an Elasticsearch query.    */
class|class
name|Implementor
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Sorting clauses.      * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-sort.html">Sort</a>      */
specifier|final
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
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Elastic aggregation ({@code MIN / MAX / COUNT} etc.) statements (functions).      * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html">aggregations</a>      */
specifier|final
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
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Allows bucketing documents together. Similar to {@code select ... from table group by field1}      * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/6.3/search-aggregations-bucket.html">Bucket Aggregrations</a>      */
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupBy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Keeps mapping between calcite expression identifier (like {@code EXPR$0}) and      * original item call like {@code _MAP['foo.bar']} ({@code foo.bar} really).      * This information otherwise might be lost during query translation.      *      * @see SqlStdOperatorTable#ITEM      */
specifier|final
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
name|expressionItemMap
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Starting index (default {@code 0}). Equivalent to {@code start} in ES query.      * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-from-size.html">From/Size</a>      */
name|Long
name|offset
decl_stmt|;
comment|/**      * Number of records to return. Equivalent to {@code size} in ES query.      * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-from-size.html">From/Size</a>      */
name|Long
name|fetch
decl_stmt|;
name|RelOptTable
name|table
decl_stmt|;
name|ElasticsearchTable
name|elasticsearchTable
decl_stmt|;
name|void
name|add
parameter_list|(
name|String
name|findOp
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|findOp
argument_list|)
expr_stmt|;
block|}
name|void
name|addGroupBy
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"field"
argument_list|)
expr_stmt|;
name|groupBy
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|void
name|addSort
parameter_list|(
name|String
name|field
parameter_list|,
name|RelFieldCollation
operator|.
name|Direction
name|direction
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"field"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|field
argument_list|,
name|direction
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|addAggregation
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"field"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expression
argument_list|,
literal|"expression"
argument_list|)
expr_stmt|;
name|aggregations
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|field
argument_list|,
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|addExpressionItemMapping
parameter_list|(
name|String
name|expressionId
parameter_list|,
name|String
name|item
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expressionId
argument_list|,
literal|"expressionId"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|item
argument_list|,
literal|"item"
argument_list|)
expr_stmt|;
name|expressionItemMap
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|expressionId
argument_list|,
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|offset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
name|void
name|fetch
parameter_list|(
name|long
name|fetch
parameter_list|)
block|{
name|this
operator|.
name|fetch
operator|=
name|fetch
expr_stmt|;
block|}
name|void
name|visitChild
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
assert|assert
name|ordinal
operator|==
literal|0
assert|;
operator|(
operator|(
name|ElasticsearchRel
operator|)
name|input
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End ElasticsearchRel.java
end_comment

end_unit

