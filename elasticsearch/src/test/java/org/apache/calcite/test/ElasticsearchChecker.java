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
name|test
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
name|SerializationFeature
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
name|Arrays
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Internal util methods for ElasticSearch tests  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchChecker
block|{
specifier|private
specifier|static
specifier|final
name|ObjectMapper
name|MAPPER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|enable
argument_list|(
name|SerializationFeature
operator|.
name|INDENT_OUTPUT
argument_list|)
operator|.
name|enable
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|)
comment|// user-friendly settings to
operator|.
name|enable
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|)
decl_stmt|;
comment|// avoid too much quoting
specifier|private
name|ElasticsearchChecker
parameter_list|()
block|{
block|}
comment|/** Returns a function that checks that a particular Elasticsearch pipeline is    * generated to implement a query.    * @param strings expected expressions    * @return validation function    */
specifier|public
specifier|static
name|Consumer
argument_list|<
name|List
argument_list|>
name|elasticsearchChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|strings
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|strings
argument_list|,
literal|"strings"
argument_list|)
expr_stmt|;
return|return
name|a
lambda|->
block|{
name|ObjectNode
name|actual
init|=
name|a
operator|==
literal|null
operator|||
name|a
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
operator|(
name|ObjectNode
operator|)
name|a
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
decl_stmt|;
name|actual
operator|=
name|expandDots
argument_list|(
name|actual
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|json
init|=
literal|"{"
operator|+
name|Arrays
operator|.
name|stream
argument_list|(
name|strings
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
operator|+
literal|"}"
decl_stmt|;
name|ObjectNode
name|expected
init|=
operator|(
name|ObjectNode
operator|)
name|MAPPER
operator|.
name|readTree
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|expected
operator|=
name|expandDots
argument_list|(
name|expected
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"expected and actual Elasticsearch queries do not match"
argument_list|,
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|actual
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
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
comment|/**    * Expands attributes with dots ({@code .}) into sub-nodes.    * Use for more friendly JSON format:    *    *<pre>    *   {'a.b.c': 1}    *   expanded to    *   {a: { b: {c: 1}}}}    *</pre>    * @param parent current node    * @param<T> type of node (usually JsonNode).    * @return copy of existing node with field {@code a.b.c} expanded.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|JsonNode
parameter_list|>
name|T
name|expandDots
parameter_list|(
name|T
name|parent
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parent
argument_list|,
literal|"parent"
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|isValueNode
argument_list|()
condition|)
block|{
return|return
name|parent
operator|.
name|deepCopy
argument_list|()
return|;
block|}
comment|// ArrayNode
if|if
condition|(
name|parent
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|ArrayNode
name|arr
init|=
operator|(
name|ArrayNode
operator|)
name|parent
decl_stmt|;
name|ArrayNode
name|copy
init|=
name|arr
operator|.
name|arrayNode
argument_list|()
decl_stmt|;
name|arr
operator|.
name|elements
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|e
lambda|->
name|copy
operator|.
name|add
argument_list|(
name|expandDots
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|copy
return|;
block|}
comment|// ObjectNode
name|ObjectNode
name|objectNode
init|=
operator|(
name|ObjectNode
operator|)
name|parent
decl_stmt|;
specifier|final
name|ObjectNode
name|copy
init|=
name|objectNode
operator|.
name|objectNode
argument_list|()
decl_stmt|;
name|objectNode
operator|.
name|fields
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|e
lambda|->
block|{
specifier|final
name|String
name|property
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|JsonNode
name|node
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|names
init|=
name|property
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|ObjectNode
name|copy2
init|=
name|copy
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|copy2
operator|=
name|copy2
operator|.
name|with
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|copy2
operator|.
name|set
argument_list|(
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|expandDots
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|copy
return|;
block|}
block|}
end_class

begin_comment
comment|// End ElasticsearchChecker.java
end_comment

end_unit

