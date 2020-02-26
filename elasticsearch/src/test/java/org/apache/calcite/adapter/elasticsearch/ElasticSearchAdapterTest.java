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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|LineProcessor
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
name|io
operator|.
name|Resources
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Set of tests for ES adapter. Uses real instance via {@link EmbeddedElasticsearchPolicy}. Document  * source is local {@code zips-mini.json} file (located in test classpath).  */
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
specifier|public
class|class
name|ElasticSearchAdapterTest
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
comment|/** Default index/type name */
specifier|private
specifier|static
specifier|final
name|String
name|ZIPS
init|=
literal|"zips"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ZIPS_SIZE
init|=
literal|149
decl_stmt|;
comment|/**    * Used to create {@code zips} index and insert zip data in bulk.    * @throws Exception when instance setup failed    */
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
name|mapping
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"city"
argument_list|,
literal|"keyword"
argument_list|,
literal|"state"
argument_list|,
literal|"keyword"
argument_list|,
literal|"pop"
argument_list|,
literal|"long"
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|createIndex
argument_list|(
name|ZIPS
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
comment|// load records from file
specifier|final
name|List
argument_list|<
name|ObjectNode
argument_list|>
name|bulk
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Resources
operator|.
name|readLines
argument_list|(
name|ElasticSearchAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/zips-mini.json"
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|,
operator|new
name|LineProcessor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|processLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
block|{
name|line
operator|=
name|line
operator|.
name|replace
argument_list|(
literal|"_id"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
comment|// _id is a reserved attribute in ES
name|bulk
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
name|line
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|getResult
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|bulk
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No records to index. Empty file ?"
argument_list|)
throw|;
block|}
name|NODE
operator|.
name|insertBulk
argument_list|(
name|ZIPS
argument_list|,
name|bulk
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|ConnectionFactory
name|newConnectionFactory
parameter_list|()
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
literal|"jdbc:calcite:lex=JAVA"
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
name|ZIPS
argument_list|)
argument_list|)
expr_stmt|;
comment|// add calcite view programmatically
specifier|final
name|String
name|viewSql
init|=
literal|"select cast(_MAP['city'] AS varchar(20)) AS \"city\", "
operator|+
literal|" cast(_MAP['loc'][0] AS float) AS \"longitude\",\n"
operator|+
literal|" cast(_MAP['loc'][1] AS float) AS \"latitude\",\n"
operator|+
literal|" cast(_MAP['pop'] AS integer) AS \"pop\", "
operator|+
literal|" cast(_MAP['state'] AS varchar(2)) AS \"state\", "
operator|+
literal|" cast(_MAP['id'] AS varchar(5)) AS \"id\" "
operator|+
literal|"from \"elastic\".\"zips\""
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
literal|"zips"
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|calciteAssert
parameter_list|()
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Tests using calcite view    */
annotation|@
name|Test
specifier|public
name|void
name|view
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from zips where city = 'BROOKLYN'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"city=BROOKLYN; longitude=-73.956985; latitude=40.646694; "
operator|+
literal|"pop=111396; state=NY; id=11226\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyResult
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips limit 0"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where _MAP['Foo'] = '_MISSING_'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basic
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
comment|// by default elastic returns max 10 records
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where _MAP['city'] = 'BROOKLYN'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where"
operator|+
literal|" _MAP['city'] in ('BROOKLYN', 'WASHINGTON')"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// lower-case
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where "
operator|+
literal|"_MAP['city'] in ('brooklyn', 'Brooklyn', 'BROOK') "
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// missing field
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where _MAP['CITY'] = 'BROOKLYN'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// limit 0
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips limit 0"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20)], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2)], id=[CAST(ITEM($0, 'id')):VARCHAR(5)])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elastic, zips]])"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from zips order by state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
operator|.
name|returns
argument_list|(
name|sortedResultSetChecker
argument_list|(
literal|"state"
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select state, pop from zips\n"
operator|+
literal|"order by state, pop offset 2 rows fetch next 3 rows only"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=AK; pop=32383"
argument_list|,
literal|"state=AL; pop=42124"
argument_list|,
literal|"state=AL; pop=43862"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source' : ['state', 'pop']"
argument_list|,
literal|"sort: [ {state: 'asc'}, {pop: 'asc'}]"
argument_list|,
literal|"from: 2"
argument_list|,
literal|"size: 3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Throws {@code AssertionError} if result set is not sorted by {@code column}.    * {@code null}s are ignored.    *    * @param column column to be extracted (as comparable object).    * @param direction ascending / descending    * @return consumer which throws exception    */
specifier|private
specifier|static
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|sortedResultSetChecker
parameter_list|(
name|String
name|column
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
name|column
argument_list|,
literal|"column"
argument_list|)
expr_stmt|;
return|return
name|rset
lambda|->
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|>
name|states
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rset
operator|.
name|next
argument_list|()
condition|)
block|{
name|Object
name|object
init|=
name|rset
operator|.
name|getObject
argument_list|(
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|object
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|object
operator|instanceof
name|Comparable
operator|)
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
literal|"%s is not comparable"
argument_list|,
name|object
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
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
comment|//noinspection rawtypes
name|states
operator|.
name|add
argument_list|(
operator|(
name|Comparable
operator|)
name|object
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|states
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|//noinspection rawtypes
specifier|final
name|Comparable
name|current
init|=
name|states
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//noinspection rawtypes
specifier|final
name|Comparable
name|next
init|=
name|states
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
specifier|final
name|int
name|cmp
init|=
name|current
operator|.
name|compareTo
argument_list|(
name|next
argument_list|)
decl_stmt|;
if|if
condition|(
name|direction
operator|==
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
condition|?
name|cmp
operator|>
literal|0
else|:
name|cmp
operator|<
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
literal|"Column %s NOT sorted (%s): %s (index:%d)> %s (index:%d) count: %d"
argument_list|,
name|column
argument_list|,
name|direction
argument_list|,
name|current
argument_list|,
name|i
argument_list|,
name|next
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|states
operator|.
name|size
argument_list|()
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
comment|/**    * Sorting (and aggregating) directly on items without a view.    *    *<p>Queries of type:    * {@code select _MAP['a'] from elastic order by _MAP['b']}    */
annotation|@
name|Test
specifier|public
name|void
name|testSortNoSchema
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips order by _MAP['city']"
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from elastic.zips where _MAP['state'] = 'NY' order by _MAP['city']"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"query:{'constant_score':{filter:{term:{state:'NY'}}}}"
argument_list|,
literal|"sort:[{city:'asc'}]"
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"size:%s"
argument_list|,
name|ElasticsearchTransport
operator|.
name|DEFAULT_FETCH_SIZE
argument_list|)
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"_MAP={id=11226, city=BROOKLYN, loc=[-73.956985, 40.646694], pop=111396, state=NY}"
argument_list|,
literal|"_MAP={id=11373, city=JACKSON HEIGHTS, loc=[-73.878551, 40.740388], pop=88241, state=NY}"
argument_list|,
literal|"_MAP={id=10021, city=NEW YORK, loc=[-73.958805, 40.768476], pop=106564, state=NY}"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select _MAP['state'] from elastic.zips order by _MAP['city']"
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select _MAP['city'] from elastic.zips where _MAP['state'] = 'NY' "
operator|+
literal|"order by _MAP['city']"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=BROOKLYN"
argument_list|,
literal|"EXPR$0=JACKSON HEIGHTS"
argument_list|,
literal|"EXPR$0=NEW YORK"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select _MAP['city'] as city, _MAP['state'] from elastic.zips "
operator|+
literal|"order by _MAP['city'] asc"
argument_list|)
operator|.
name|returns
argument_list|(
name|sortedResultSetChecker
argument_list|(
literal|"city"
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|)
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select _MAP['city'] as city, _MAP['state'] from elastic.zips "
operator|+
literal|"order by _MAP['city'] desc"
argument_list|)
operator|.
name|returns
argument_list|(
name|sortedResultSetChecker
argument_list|(
literal|"city"
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
argument_list|)
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select max(_MAP['pop']), min(_MAP['pop']), _MAP['state'] from elastic.zips "
operator|+
literal|"group by _MAP['state'] order by _MAP['state'] limit 3"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=32383.0; EXPR$1=23238.0; EXPR$2=AK"
argument_list|,
literal|"EXPR$0=44165.0; EXPR$1=42124.0; EXPR$2=AL"
argument_list|,
literal|"EXPR$0=53532.0; EXPR$1=37428.0; EXPR$2=AR"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select max(_MAP['pop']), min(_MAP['pop']), _MAP['state'] from elastic.zips "
operator|+
literal|"where _MAP['state'] = 'NY' group by _MAP['state'] order by _MAP['state'] limit 3"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=111396.0; EXPR$1=88241.0; EXPR$2=NY\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sort by multiple fields (in different direction: asc/desc)    */
annotation|@
name|Test
specifier|public
name|void
name|sortAscDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select city, state, pop from zips\n"
operator|+
literal|"order by pop desc, state asc, city desc limit 3"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"city=CHICAGO; state=IL; pop=112047"
argument_list|,
literal|"city=BROOKLYN; state=NY; pop=111396"
argument_list|,
literal|"city=NEW YORK; state=NY; pop=106564"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':['city','state','pop']"
argument_list|,
literal|"sort:[{pop:'desc'}, {state:'asc'}, {city:'desc'}]"
argument_list|,
literal|"size:3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffsetLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select state, id from zips\n"
operator|+
literal|"offset 2 fetch next 3 rows only"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source : ['state', 'id']"
argument_list|,
literal|"from: 2"
argument_list|,
literal|"size: 3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select state, id from zips\n"
operator|+
literal|"fetch next 3 rows only"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':['state','id']"
argument_list|,
literal|"size:3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|limit2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select id from zips limit 5"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsCount
argument_list|(
literal|5
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':['id']"
argument_list|,
literal|"size:5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterSort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where state = 'CA' and pop>= 94000\n"
operator|+
literal|"order by state, pop"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchSort(sort0=[$4], sort1=[$3], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20)], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2)], id=[CAST(ITEM($0, 'id')):VARCHAR(5)])\n"
operator|+
literal|"      ElasticsearchFilter(condition=[AND(=(CAST(ITEM($0, 'state')):VARCHAR(2), 'CA'),>=(CAST(ITEM($0, 'pop')):INTEGER, 94000))])\n"
operator|+
literal|"        ElasticsearchTableScan(table=[[elastic, zips]])\n\n"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"city=NORWALK; longitude=-118.081767; latitude=33.90564;"
operator|+
literal|" pop=94188; state=CA; id=90650"
argument_list|,
literal|"city=LOS ANGELES; longitude=-118.258189; latitude=34.007856;"
operator|+
literal|" pop=96074; state=CA; id=90011"
argument_list|,
literal|"city=BELL GARDENS; longitude=-118.17205; latitude=33.969177;"
operator|+
literal|" pop=99568; state=CA; id=90201"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'query' : "
operator|+
literal|"{'constant_score':{filter:{bool:"
operator|+
literal|"{must:[{term:{state:'CA'}},"
operator|+
literal|"{range:{pop:{gte:94000}}}]}}}}"
argument_list|,
literal|"'script_fields': {longitude:{script:'params._source.loc[0]'}, "
operator|+
literal|"latitude:{script:'params._source.loc[1]'}, "
operator|+
literal|"city:{script: 'params._source.city'}, "
operator|+
literal|"pop:{script: 'params._source.pop'}, "
operator|+
literal|"state:{script: 'params._source.state'}, "
operator|+
literal|"id:{script: 'params._source.id'}}"
argument_list|,
literal|"sort: [ {state: 'asc'}, {pop: 'asc'}]"
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"size:%s"
argument_list|,
name|ElasticsearchTransport
operator|.
name|DEFAULT_FETCH_SIZE
argument_list|)
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterSortDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where pop BETWEEN 95000 AND 100000\n"
operator|+
literal|"order by state desc, pop"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"city=LOS ANGELES; longitude=-118.258189; latitude=34.007856; pop=96074; state=CA; id=90011"
argument_list|,
literal|"city=BELL GARDENS; longitude=-118.17205; latitude=33.969177; pop=99568; state=CA; id=90201"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInPlan
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|searches
init|=
block|{
literal|"query: {'constant_score':{filter:{bool:{should:"
operator|+
literal|"[{term:{pop:96074}},{term:{pop:99568}}]}}}}"
block|,
literal|"script_fields: {longitude:{script:'params._source.loc[0]'}, "
operator|+
literal|"latitude:{script:'params._source.loc[1]'}, "
operator|+
literal|"city:{script: 'params._source.city'}, "
operator|+
literal|"pop:{script: 'params._source.pop'}, "
operator|+
literal|"state:{script: 'params._source.state'}, "
operator|+
literal|"id:{script: 'params._source.id'}}"
block|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"size:%d"
argument_list|,
name|ElasticsearchTransport
operator|.
name|DEFAULT_FETCH_SIZE
argument_list|)
block|}
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from zips where pop in (96074, 99568)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"city=BELL GARDENS; longitude=-118.17205; latitude=33.969177; pop=99568; state=CA; id=90201"
argument_list|,
literal|"city=LOS ANGELES; longitude=-118.258189; latitude=34.007856; pop=96074; state=CA; id=90011"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
name|searches
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testZips
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, city from zips"
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|ZIPS_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select state, city, 0 as zero\n"
operator|+
literal|"from zips\n"
operator|+
literal|"order by state, city"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=AK; city=ANCHORAGE; zero=0"
argument_list|,
literal|"state=AK; city=FAIRBANKS; zero=0"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"script_fields:"
operator|+
literal|"{zero:{script:'0'},"
operator|+
literal|"state:{script:'params._source.state'},"
operator|+
literal|"city:{script:'params._source.city'}}"
argument_list|,
literal|"sort:[{state:'asc'},{city:'asc'}]"
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"size:%d"
argument_list|,
name|ElasticsearchTransport
operator|.
name|DEFAULT_FETCH_SIZE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchProject(state=[CAST(ITEM($0, 'state')):VARCHAR(2)], city=[CAST(ITEM($0, 'city')):VARCHAR(20)])\n"
operator|+
literal|"    ElasticsearchFilter(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2), 'CA')])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elastic, zips]])"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, city from zips where state = 'CA'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=CA; city=BELL GARDENS"
argument_list|,
literal|"state=CA; city=LOS ANGELES"
argument_list|,
literal|"state=CA; city=NORWALK"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterReversed
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, city from zips where 'WI'< state order by city"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=WV; city=BECKLEY"
argument_list|,
literal|"state=WY; city=CHEYENNE"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, city from zips where state> 'WI' order by city"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=WV; city=BECKLEY"
argument_list|,
literal|"state=WY; city=CHEYENNE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|agg1
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) from zips"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"track_total_hits:true"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=149\n"
argument_list|)
expr_stmt|;
comment|// check with limit (should still return correct result).
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) from zips limit 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=149\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) as cnt from zips"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"size:0"
argument_list|,
literal|"track_total_hits:true"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"cnt=149\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(pop), max(pop) from zips"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"track_total_hits:true"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'EXPR$0':{min:{field:'pop'}},'EXPR$1':{max:"
operator|+
literal|"{field:'pop'}}}"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=21; EXPR$1=112047\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(pop) as min1, max(pop) as max1 from zips"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"min1=21; max1=112047\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop), min(pop), sum(pop), avg(pop) from zips"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=149; EXPR$1=112047; EXPR$2=21; EXPR$3=7865489; EXPR$4=52788\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|groupBy
parameter_list|()
block|{
comment|// distinct
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select distinct state\n"
operator|+
literal|"from zips\n"
operator|+
literal|"limit 6"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source:false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{'terms':{'field':'state','missing':'__MISSING__', 'size' : 6}}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"state=AK"
argument_list|,
literal|"state=AL"
argument_list|,
literal|"state=AR"
argument_list|,
literal|"state=AZ"
argument_list|,
literal|"state=CA"
argument_list|,
literal|"state=CO"
argument_list|)
expr_stmt|;
comment|// without aggregate function
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, city\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state, city\n"
operator|+
literal|"order by city limit 10"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_city':{'terms':{'field':'city','missing':'__MISSING__','size':10,'order':{'_key':'asc'}}"
argument_list|,
literal|"aggregations:{'g_state':{'terms':{'field':'state','missing':'__MISSING__','size':10}}}}}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"state=SD; city=ABERDEEN"
argument_list|,
literal|"state=SC; city=AIKEN"
argument_list|,
literal|"state=TX; city=ALTON"
argument_list|,
literal|"state=IA; city=AMES"
argument_list|,
literal|"state=AK; city=ANCHORAGE"
argument_list|,
literal|"state=MD; city=BALTIMORE"
argument_list|,
literal|"state=ME; city=BANGOR"
argument_list|,
literal|"state=KS; city=BAVARIA"
argument_list|,
literal|"state=NJ; city=BAYONNE"
argument_list|,
literal|"state=OR; city=BEAVERTON"
argument_list|)
expr_stmt|;
comment|// ascending
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(pop), max(pop), state\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state\n"
operator|+
literal|"order by state limit 3"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{terms:{field:'state',missing:'__MISSING__',size:3,"
operator|+
literal|" order:{'_key':'asc'}}"
argument_list|,
literal|"aggregations:{'EXPR$0':{min:{field:'pop'}},'EXPR$1':{max:{field:'pop'}}}}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=23238; EXPR$1=32383; state=AK"
argument_list|,
literal|"EXPR$0=42124; EXPR$1=44165; state=AL"
argument_list|,
literal|"EXPR$0=37428; EXPR$1=53532; state=AR"
argument_list|)
expr_stmt|;
comment|// just one aggregation function
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(pop), state\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state\n"
operator|+
literal|"order by state limit 3"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{terms:{field:'state',missing:'__MISSING__',"
operator|+
literal|"size:3, order:{'_key':'asc'}}"
argument_list|,
literal|"aggregations:{'EXPR$0':{min:{field:'pop'}} }}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=23238; state=AK"
argument_list|,
literal|"EXPR$0=42124; state=AL"
argument_list|,
literal|"EXPR$0=37428; state=AR"
argument_list|)
expr_stmt|;
comment|// group by count
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(city), state\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state\n"
operator|+
literal|"order by state limit 3"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{terms:{field:'state',missing:'__MISSING__',"
operator|+
literal|" size:3, order:{'_key':'asc'}}"
argument_list|,
literal|"aggregations:{'EXPR$0':{'value_count':{field:'city'}} }}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=3; state=AK"
argument_list|,
literal|"EXPR$0=3; state=AL"
argument_list|,
literal|"EXPR$0=3; state=AR"
argument_list|)
expr_stmt|;
comment|// descending
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(pop), max(pop), state\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state\n"
operator|+
literal|"order by state desc limit 3"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{terms:{field:'state',missing:'__MISSING__',"
operator|+
literal|"size:3, order:{'_key':'desc'}}"
argument_list|,
literal|"aggregations:{'EXPR$0':{min:{field:'pop'}},'EXPR$1':"
operator|+
literal|"{max:{field:'pop'}}}}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=25968; EXPR$1=33107; state=WY"
argument_list|,
literal|"EXPR$0=45196; EXPR$1=70185; state=WV"
argument_list|,
literal|"EXPR$0=51008; EXPR$1=57187; state=WI"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Testing {@code NOT} operator    */
annotation|@
name|Test
specifier|public
name|void
name|notOperator
parameter_list|()
block|{
comment|// largest zips (states) in mini-zip by pop (sorted) : IL, NY, CA, MI
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop) from zips where state not in ('IL')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=146; EXPR$1=111396\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop) from zips where not state in ('IL')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=146; EXPR$1=111396\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop) from zips where not state not in ('IL')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3; EXPR$1=112047\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop) from zips where state not in ('IL', 'NY')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=143; EXPR$1=99568\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), max(pop) from zips where state not in ('IL', 'NY', 'CA')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=140; EXPR$1=84712\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test of {@link org.apache.calcite.sql.fun.SqlStdOperatorTable#APPROX_COUNT_DISTINCT} which    * will be translated to    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html">Cardinality Aggregation</a>    * (approximate counts using HyperLogLog++ algorithm).    */
annotation|@
name|Test
specifier|public
name|void
name|approximateCount
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select state, approx_count_distinct(city), approx_count_distinct(pop) from zips"
operator|+
literal|" group by state order by state limit 3"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"'_source':false"
argument_list|,
literal|"size:0"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'g_state':{terms:{field:'state', missing:'__MISSING__', size:3, "
operator|+
literal|"order:{'_key':'asc'}}"
argument_list|,
literal|"aggregations:{'EXPR$1':{cardinality:{field:'city'}}"
argument_list|,
literal|"'EXPR$2':{cardinality:{field:'pop'}} "
operator|+
literal|" }}}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"state=AK; EXPR$1=3; EXPR$2=3"
argument_list|,
literal|"state=AL; EXPR$1=3; EXPR$2=3"
argument_list|,
literal|"state=AR; EXPR$1=3; EXPR$2=3"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

