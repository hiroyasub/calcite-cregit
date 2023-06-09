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
name|schema
operator|.
name|impl
operator|.
name|ViewTable
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
name|ViewTableMacro
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|ElasticsearchChecker
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
name|TestUtil
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
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|BeforeAll
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|ResourceAccessMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|ResourceLock
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
name|ResultSet
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
name|regex
operator|.
name|PatternSyntaxException
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Checks renaming of fields (also upper, lower cases) during projections.  */
end_comment

begin_class
annotation|@
name|ResourceLock
argument_list|(
name|value
operator|=
literal|"elasticsearch-scrolls"
argument_list|,
name|mode
operator|=
name|ResourceAccessMode
operator|.
name|READ
argument_list|)
class|class
name|Projection2Test
block|{
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
literal|"nested"
decl_stmt|;
annotation|@
name|BeforeAll
specifier|public
specifier|static
name|void
name|setupInstance
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"long"
argument_list|,
literal|"b.a"
argument_list|,
literal|"long"
argument_list|,
literal|"b.b"
argument_list|,
literal|"long"
argument_list|,
literal|"b.c.a"
argument_list|,
literal|"keyword"
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|createIndex
argument_list|(
name|NAME
argument_list|,
name|mappings
argument_list|)
expr_stmt|;
name|String
name|doc
init|=
literal|"{'a': 1, 'b':{'a': 2, 'b':'3', 'c':{'a': 'foo'}}}"
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|insertDocument
argument_list|(
name|NAME
argument_list|,
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
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
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
name|root
operator|.
name|add
argument_list|(
literal|"elastic"
argument_list|,
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// add calcite view programmatically
specifier|final
name|String
name|viewSql
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['a'] AS \"a\", "
operator|+
literal|" _MAP['b.a']  AS \"b.a\", "
operator|+
literal|" _MAP['b.b'] AS \"b.b\", "
operator|+
literal|" _MAP['b.c.a'] AS \"b.c.a\", "
operator|+
literal|" _MAP['_id'] AS \"id\" "
comment|// _id field is implicit
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|ViewTableMacro
name|macro
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|root
argument_list|,
name|viewSql
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"elastic"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"elastic"
argument_list|,
literal|"view"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"VIEW"
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
annotation|@
name|Test
name|void
name|projection
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"a\", \"b.a\", \"b.b\", \"b.c.a\" from view"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"a=1; b.a=2; b.b=3; b.c.a=foo\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|projection2
parameter_list|()
block|{
name|String
name|sql
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['a'], _MAP['b.a'], _MAP['b.b'], "
operator|+
literal|"_MAP['b.c.a'], _MAP['missing'], _MAP['b.missing'] from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1; EXPR$1=2; EXPR$2=3; EXPR$3=foo; EXPR$4=null; EXPR$5=null\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|projection3
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select * from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"_MAP={a=1, b={a=2, b=3, c={a=foo}}}\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select *, _MAP['a'] from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"_MAP={a=1, b={a=2, b=3, c={a=foo}}}; EXPR$1=1\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that {@code _id} field is available when queried explicitly.    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-id-field.html">ID Field</a>    */
annotation|@
name|Test
name|void
name|projectionWithIdField
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|fixture
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
literal|"select \"id\" from view"
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"id=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|query
argument_list|(
literal|"select \"id\", \"id\" from view"
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"id=\\p{Graph}+; id=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|query
argument_list|(
literal|"select \"id\", \"a\" from view"
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"id=\\p{Graph}+; a=1"
argument_list|)
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|query
argument_list|(
literal|"select \"a\", \"id\" from view"
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"a=1; id=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
comment|// single _id column
specifier|final
name|String
name|sql1
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['_id'] "
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql1
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"EXPR$0=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiple columns: _id and a
specifier|final
name|String
name|sql2
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['_id'], _MAP['a'] "
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql2
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"EXPR$0=\\p{Graph}+; EXPR$1=1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiple _id columns
specifier|final
name|String
name|sql3
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['_id'], _MAP['_id'] "
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql3
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"EXPR$0=\\p{Graph}+; EXPR$1=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
comment|// _id column with same alias
specifier|final
name|String
name|sql4
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['_id'] as \"_id\" "
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql4
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"_id=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
comment|// _id field not available implicitly
name|String
name|sql5
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select * from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql5
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"_MAP={a=1, b={a=2, b=3, c={a=foo}}}"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|sql6
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select *, _MAP['_id'] from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|fixture
operator|.
name|query
argument_list|(
name|sql6
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"_MAP={a=1, b={a=2, b=3, c={a=foo}}}; EXPR$1=\\p{Graph}+"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Avoid using scripting for simple projections.    *    *<p> When projecting simple fields (without expression) no    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting.html">scripting</a>    * should be used just    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-source-filtering.html">_source</a>.    */
annotation|@
name|Test
name|void
name|simpleProjectionNoScripting
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['_id'], _MAP['a'], _MAP['b.a'] from "
operator|+
literal|" \"elastic\".\"%s\" where _MAP['b.a'] = 2"
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'query.constant_score.filter.term.b.a':2"
argument_list|,
literal|"_source:['a', 'b.a']"
argument_list|,
literal|"size:5196"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
name|regexMatch
argument_list|(
literal|"EXPR$0=\\p{Graph}+; EXPR$1=1; EXPR$2=2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4450">[CALCITE-4450]    * ElasticSearch query with varchar literal projection fails with JsonParseException</a>. */
annotation|@
name|Test
name|void
name|projectionStringLiteral
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select 'foo' as \"lit\"\n"
operator|+
literal|"from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lit=foo\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4450">[CALCITE-4450]    * ElasticSearch query with varchar literal projection fails with JsonParseException</a>. */
annotation|@
name|Test
name|void
name|projectionStringLiteralAndColumn
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Projection2Test
operator|::
name|createConnection
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select 'foo\\\"bar\\\"' as \"lit\", _MAP['a'] as \"a\"\n"
operator|+
literal|"from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lit=foo\\\"bar\\\"; a=1\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allows values to contain regular expressions instead of exact values.    *<pre>    *   {@code    *      key1=foo1; key2=\\w+; key4=\\d{3,4}    *   }    *</pre>    * @param lines lines with regexp    * @return consumer to be used in {@link org.apache.calcite.test.CalciteAssert.AssertQuery}    */
specifier|private
specifier|static
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|regexMatch
parameter_list|(
name|String
modifier|...
name|lines
parameter_list|)
block|{
return|return
name|rset
lambda|->
block|{
try|try
block|{
specifier|final
name|int
name|columnCount
init|=
name|rset
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|actual
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|processedRows
init|=
literal|0
decl_stmt|;
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|rset
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|processedRows
operator|>=
name|lines
operator|.
name|length
condition|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
name|rset
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|rset
operator|.
name|getString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|actual
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|columnCount
condition|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
comment|// don't re-check if already failed
if|if
condition|(
operator|!
name|fail
condition|)
block|{
comment|// splitting string of type: key1=val1; key2=val2
specifier|final
name|String
name|keyValue
init|=
name|lines
index|[
name|processedRows
index|]
operator|.
name|split
argument_list|(
literal|"; "
argument_list|)
index|[
name|i
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|String
index|[]
name|parts
init|=
name|keyValue
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedName
init|=
name|parts
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|String
name|expectedValue
init|=
name|parts
index|[
literal|1
index|]
decl_stmt|;
name|boolean
name|valueMatches
init|=
name|expectedValue
operator|.
name|equals
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|valueMatches
condition|)
block|{
comment|// try regex
try|try
block|{
name|valueMatches
operator|=
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|matches
argument_list|(
name|expectedValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|ignore
parameter_list|)
block|{
comment|// probably not a regular expression
block|}
block|}
name|fail
operator|=
operator|!
operator|(
name|name
operator|.
name|equals
argument_list|(
name|expectedName
argument_list|)
operator|&&
name|valueMatches
operator|)
expr_stmt|;
block|}
block|}
name|processedRows
operator|++
expr_stmt|;
block|}
comment|// also check that processed same number of rows
name|fail
operator|&=
name|processedRows
operator|==
name|lines
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|assertEquals
argument_list|(
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|lines
argument_list|)
argument_list|)
argument_list|,
name|actual
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed on previous line, but for some reason didn't"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

