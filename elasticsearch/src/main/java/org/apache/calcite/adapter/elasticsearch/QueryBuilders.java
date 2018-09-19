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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Utility class to generate elastic search queries. Most query builders have  * been copied from ES distribution. The reason we have separate definition is  * high-level client dependency on core modules (like lucene, netty, XContent etc.) which  * is not compatible between different major versions.  *  *<p>The goal of ES adapter is to  * be compatible with any elastic version or even to connect to clusters with different  * versions simultaneously.  *  *<p>Jackson API is used to generate ES query as JSON document.  */
end_comment

begin_class
class|class
name|QueryBuilders
block|{
specifier|private
name|QueryBuilders
parameter_list|()
block|{
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a single character term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|char
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing a term.    *    * @param name  The name of the field    * @param value The value of the term    */
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * A filer for a field based on several terms matching on any of them.    *    * @param name   The field name    * @param values The terms    */
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents within an range of terms.    *    * @param name The field name    */
specifier|static
name|RangeQueryBuilder
name|rangeQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|RangeQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents containing terms with a specified regular expression.    *    * @param name   The name of the field    * @param regexp The regular expression    */
specifier|static
name|RegexpQueryBuilder
name|regexpQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|regexp
parameter_list|)
block|{
return|return
operator|new
name|RegexpQueryBuilder
argument_list|(
name|name
argument_list|,
name|regexp
argument_list|)
return|;
block|}
comment|/**    * A Query that matches documents matching boolean combinations of other queries.    */
specifier|static
name|BoolQueryBuilder
name|boolQuery
parameter_list|()
block|{
return|return
operator|new
name|BoolQueryBuilder
argument_list|()
return|;
block|}
comment|/**    * A query that wraps another query and simply returns a constant score equal to the    * query boost for every document in the query.    *    * @param queryBuilder The query to wrap in a constant score query    */
specifier|static
name|ConstantScoreQueryBuilder
name|constantScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
return|;
block|}
comment|/**    * A filter to filter only documents where a field exists in them.    *    * @param name The name of the field    */
specifier|static
name|ExistsQueryBuilder
name|existsQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ExistsQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * A query that matches on all documents.    */
specifier|static
name|MatchAllQueryBuilder
name|matchAll
parameter_list|()
block|{
return|return
operator|new
name|MatchAllQueryBuilder
argument_list|()
return|;
block|}
comment|/**    * Base class to build ES queries    */
specifier|abstract
specifier|static
class|class
name|QueryBuilder
block|{
comment|/**      * Convert existing query to JSON format using jackson API.      * @param generator used to generate JSON elements      * @throws IOException if IO error occurred      */
specifier|abstract
name|void
name|writeJson
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Query for boolean logic    */
specifier|static
class|class
name|BoolQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|mustClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|mustNotClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|filterClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|shouldClauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BoolQueryBuilder
name|must
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryBuilder
argument_list|,
literal|"queryBuilder"
argument_list|)
expr_stmt|;
name|mustClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|BoolQueryBuilder
name|filter
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryBuilder
argument_list|,
literal|"queryBuilder"
argument_list|)
expr_stmt|;
name|filterClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|BoolQueryBuilder
name|mustNot
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryBuilder
argument_list|,
literal|"queryBuilder"
argument_list|)
expr_stmt|;
name|mustNotClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|BoolQueryBuilder
name|should
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryBuilder
argument_list|,
literal|"queryBuilder"
argument_list|)
expr_stmt|;
name|shouldClauses
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeJson
parameter_list|(
name|JsonGenerator
name|gen
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeFieldName
argument_list|(
literal|"bool"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|writeJsonArray
argument_list|(
literal|"must"
argument_list|,
name|mustClauses
argument_list|,
name|gen
argument_list|)
expr_stmt|;
name|writeJsonArray
argument_list|(
literal|"filter"
argument_list|,
name|filterClauses
argument_list|,
name|gen
argument_list|)
expr_stmt|;
name|writeJsonArray
argument_list|(
literal|"must_not"
argument_list|,
name|mustNotClauses
argument_list|,
name|gen
argument_list|)
expr_stmt|;
name|writeJsonArray
argument_list|(
literal|"should"
argument_list|,
name|shouldClauses
argument_list|,
name|gen
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|writeJsonArray
parameter_list|(
name|String
name|field
parameter_list|,
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|clauses
parameter_list|,
name|JsonGenerator
name|gen
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|clauses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|gen
operator|.
name|writeFieldName
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|writeJson
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gen
operator|.
name|writeArrayFieldStart
argument_list|(
name|field
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
name|clause
range|:
name|clauses
control|)
block|{
name|clause
operator|.
name|writeJson
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
name|gen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * A Query that matches documents containing a term.    */
specifier|static
class|class
name|TermQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
specifier|private
name|TermQueryBuilder
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldName
argument_list|,
literal|"fieldName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"term"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|generator
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A filter for a field based on several terms matching on any of them.    */
specifier|private
specifier|static
class|class
name|TermsQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
decl_stmt|;
specifier|private
name|TermsQueryBuilder
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldName
argument_list|,
literal|"fieldName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|values
argument_list|,
literal|"values"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"terms"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|writeObject
argument_list|(
name|generator
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Write usually simple (scalar) value (string, number, boolean or null) to json output.    * In case of complex objects delegates to jackson serialization.    *    * @param generator api to generate JSON document    * @param value JSON value to write    * @throws IOException if can't write to output    */
specifier|private
specifier|static
name|void
name|writeObject
parameter_list|(
name|JsonGenerator
name|generator
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * A Query that matches documents within an range of terms.    */
specifier|static
class|class
name|RangeQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
specifier|private
name|Object
name|lt
decl_stmt|;
specifier|private
name|boolean
name|lte
decl_stmt|;
specifier|private
name|Object
name|gt
decl_stmt|;
specifier|private
name|boolean
name|gte
decl_stmt|;
specifier|private
name|String
name|format
decl_stmt|;
specifier|private
name|RangeQueryBuilder
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldName
argument_list|,
literal|"fieldName"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RangeQueryBuilder
name|to
parameter_list|(
name|Object
name|value
parameter_list|,
name|boolean
name|lte
parameter_list|)
block|{
name|this
operator|.
name|lt
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|this
operator|.
name|lte
operator|=
name|lte
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|RangeQueryBuilder
name|from
parameter_list|(
name|Object
name|value
parameter_list|,
name|boolean
name|gte
parameter_list|)
block|{
name|this
operator|.
name|gt
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|value
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|this
operator|.
name|gte
operator|=
name|gte
expr_stmt|;
return|return
name|this
return|;
block|}
name|RangeQueryBuilder
name|lt
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|to
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
name|RangeQueryBuilder
name|lte
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|to
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|RangeQueryBuilder
name|gt
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
name|RangeQueryBuilder
name|gte
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|RangeQueryBuilder
name|format
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lt
operator|==
literal|null
operator|&&
name|gt
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Either lower or upper bound should be provided"
argument_list|)
throw|;
block|}
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"range"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|gt
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|op
init|=
name|gte
condition|?
literal|"gte"
else|:
literal|"gt"
decl_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|generator
argument_list|,
name|gt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lt
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|op
init|=
name|lte
condition|?
literal|"lte"
else|:
literal|"lt"
decl_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|generator
argument_list|,
name|lt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"format"
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A Query that does fuzzy matching for a specific value.    */
specifier|static
class|class
name|RegexpQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
name|RegexpQueryBuilder
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Constructs a query that only match on documents that the field has a value in them.    */
specifier|static
class|class
name|ExistsQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
name|ExistsQueryBuilder
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fieldName
argument_list|,
literal|"fieldName"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"exists"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"field"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A query that wraps a filter and simply returns a constant score equal to the    * query boost for every document in the filter.    */
specifier|static
class|class
name|ConstantScoreQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
specifier|final
name|QueryBuilder
name|builder
decl_stmt|;
specifier|private
name|ConstantScoreQueryBuilder
parameter_list|(
specifier|final
name|QueryBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|builder
argument_list|,
literal|"builder"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"constant_score"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"filter"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|writeJson
argument_list|(
name|generator
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A query that matches on all documents.    *<pre>    *   {    *     "match_all": {}    *   }    *</pre>    */
specifier|static
class|class
name|MatchAllQueryBuilder
extends|extends
name|QueryBuilder
block|{
specifier|private
name|MatchAllQueryBuilder
parameter_list|()
block|{
block|}
annotation|@
name|Override
name|void
name|writeJson
parameter_list|(
specifier|final
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeFieldName
argument_list|(
literal|"match_all"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End QueryBuilders.java
end_comment

end_unit

