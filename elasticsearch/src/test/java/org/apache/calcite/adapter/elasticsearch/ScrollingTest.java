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
name|jdbc
operator|.
name|CalciteConnection
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
name|test
operator|.
name|CalciteAssert
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
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Arrays
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
name|stream
operator|.
name|IntStream
import|;
end_import

begin_comment
comment|/**  * Tests usage of scrolling API like correct results and resource cleanup  * (delete scroll after scan).  */
end_comment

begin_class
specifier|public
class|class
name|ScrollingTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|EmbeddedElasticsearchPolicy
name|NODE
init|=
name|EmbeddedElasticsearchPolicy
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"scroll"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|10
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupInstance
parameter_list|()
throws|throws
name|Exception
block|{
name|NODE
operator|.
name|createIndex
argument_list|(
name|NAME
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"value"
argument_list|,
literal|"long"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ObjectNode
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|SIZE
condition|;
name|i
operator|++
control|)
block|{
name|String
name|json
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"{\"value\": %d}"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
operator|(
name|ObjectNode
operator|)
name|NODE
operator|.
name|mapper
argument_list|()
operator|.
name|readTree
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NODE
operator|.
name|insertBulk
argument_list|(
name|NAME
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|ConnectionFactory
name|newConnectionFactory
parameter_list|(
name|int
name|fetchSize
parameter_list|)
block|{
return|return
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|root
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|ElasticsearchSchema
name|schema
init|=
operator|new
name|ElasticsearchSchema
argument_list|(
name|NODE
operator|.
name|restClient
argument_list|()
argument_list|,
name|NODE
operator|.
name|mapper
argument_list|()
argument_list|,
name|NAME
argument_list|,
literal|null
argument_list|,
name|fetchSize
argument_list|)
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"elastic"
argument_list|,
name|schema
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|scrolling
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
index|[]
name|expected
init|=
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|SIZE
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
literal|"V="
operator|+
name|i
argument_list|)
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['value'] as v from "
operator|+
literal|"\"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|fetchSize
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
name|SIZE
operator|/
literal|2
argument_list|,
name|SIZE
operator|-
literal|1
argument_list|,
name|SIZE
argument_list|,
name|SIZE
operator|+
literal|1
argument_list|,
literal|2
operator|*
name|SIZE
argument_list|)
control|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|(
name|fetchSize
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertNoActiveScrolls
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Ensures there are no pending scroll contexts in elastic search cluster.    * Queries {@code /_nodes/stats/indices/search} endpoint.    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-stats.html">Indices Stats</a>    */
specifier|private
name|void
name|assertNoActiveScrolls
parameter_list|()
throws|throws
name|IOException
block|{
comment|// get node stats
specifier|final
name|Response
name|response
init|=
name|NODE
operator|.
name|restClient
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/_nodes/stats/indices/search"
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
specifier|final
name|ObjectNode
name|node
init|=
name|NODE
operator|.
name|mapper
argument_list|()
operator|.
name|readValue
argument_list|(
name|is
argument_list|,
name|ObjectNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|path
init|=
literal|"/indices/search/scroll_current"
decl_stmt|;
specifier|final
name|JsonNode
name|scrollCurrent
init|=
name|node
operator|.
name|with
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|elements
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|at
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|scrollCurrent
operator|.
name|isMissingNode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Couldn't find node at "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
name|scrollCurrent
operator|.
name|asInt
argument_list|()
operator|!=
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
literal|"Expected no active scrolls "
operator|+
literal|"but got %d. Current index stats %s"
argument_list|,
name|scrollCurrent
operator|.
name|asInt
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End ScrollingTest.java
end_comment

end_unit

