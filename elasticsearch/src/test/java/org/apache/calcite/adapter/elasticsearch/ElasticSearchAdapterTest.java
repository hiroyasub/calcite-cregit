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
name|Ignore
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Set of tests for ES adapter. Uses real instance via {@link EmbeddedElasticsearchPolicy}. Document  * source is local {@code zips-mini.json} file (located in test classpath).  */
end_comment

begin_class
specifier|public
class|class
name|ElasticSearchAdapterTest
block|{
annotation|@
name|ClassRule
comment|//init once for all tests
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
comment|/**    * Used to create {@code zips} index and insert zip data in bulk.    * @throws Exception when instance setup failed    */
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
name|replaceAll
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
literal|"ZIPS"
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
literal|"select * from zips where \"city\" = 'BROOKLYN'"
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
literal|"select * from \"elastic\".\"zips\" where _MAP['Foo'] = '_MISSING_'"
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
throws|throws
name|Exception
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
literal|"select * from \"elastic\".\"zips\" where _MAP['city'] = 'BROOKLYN'"
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
literal|"select * from \"elastic\".\"zips\" where"
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
literal|"select * from \"elastic\".\"zips\" where "
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
literal|"select * from \"elastic\".\"zips\" where _MAP['CITY'] = 'BROOKLYN'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// limit works
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
literal|"select * from \"elastic\".\"zips\" limit 42"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|42
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
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], id=[CAST(ITEM($0, 'id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elastic, zips]])"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from zips order by \"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
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
literal|"select \"state\", \"pop\" from zips\n"
operator|+
literal|"order by \"state\", \"pop\" offset 2 rows fetch next 3 rows only"
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
literal|"\"_source\" : [\"state\", \"pop\"]"
argument_list|,
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"pop\": \"asc\"}]"
argument_list|,
literal|"\"from\": 2"
argument_list|,
literal|"\"size\": 3"
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
literal|"select \"state\", \"id\" from zips\n"
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
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"\"from\": 2"
argument_list|,
literal|"\"size\": 3"
argument_list|,
literal|"\"_source\" : [\"state\", \"id\"]"
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
literal|"select \"state\", \"id\" from zips\n"
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
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"\"size\": 3"
argument_list|,
literal|"\"_source\" : [\"state\", \"id\"]"
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
literal|"where \"state\" = 'CA' and \"pop\">= 94000\n"
operator|+
literal|"order by \"state\", \"pop\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchSort(sort0=[$4], sort1=[$3], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], id=[CAST(ITEM($0, 'id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      ElasticsearchFilter(condition=[AND(=(CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA'),>=(CAST(ITEM($0, 'pop')):INTEGER, 94000))])\n"
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
literal|"\"query\" : "
operator|+
literal|"{\"constant_score\":{\"filter\":{\"bool\":"
operator|+
literal|"{\"must\":[{\"term\":{\"state\":\"CA\"}},"
operator|+
literal|"{\"range\":{\"pop\":{\"gte\":94000}}}]}}}}"
argument_list|,
literal|"\"script_fields\": {\"longitude\":{\"script\":\"params._source.loc[0]\"}, "
operator|+
literal|"\"latitude\":{\"script\":\"params._source.loc[1]\"}, "
operator|+
literal|"\"city\":{\"script\": \"params._source.city\"}, "
operator|+
literal|"\"pop\":{\"script\": \"params._source.pop\"}, "
operator|+
literal|"\"state\":{\"script\": \"params._source.state\"}, "
operator|+
literal|"\"id\":{\"script\": \"params._source.id\"}}"
argument_list|,
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"pop\": \"asc\"}]"
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
literal|"where \"pop\" BETWEEN 95000 AND 100000\n"
operator|+
literal|"order by \"state\" desc, \"pop\""
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
name|Ignore
argument_list|(
literal|"Known issue when predicate analyzer doesn't simplify the expression (a = 1 and a> 0) "
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFilterRedundant
parameter_list|()
block|{
comment|// known issue when PredicateAnalyzer doesn't simplify expressions
comment|// (a< 3 and and a> 0 and a = 1) equivalent to (a = 1)
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where \"state\"> 'CA' and \"state\"< 'AZ' and \"state\" = 'OK'"
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
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|""
operator|+
literal|"\"query\" : {\"constant_score\":{\"filter\":{\"bool\":"
operator|+
literal|"{\"must\":[{\"term\":{\"state\":\"OK\"}}]}}}}"
argument_list|,
literal|"\"script_fields\": {\"longitude\":{\"script\":\"params._source.loc[0]\"}, "
operator|+
literal|"\"latitude\":{\"script\":\"params._source.loc[1]\"}, "
operator|+
literal|"\"city\":{\"script\": \"params._source.city\"}, "
operator|+
literal|"\"pop\":{\"script\": \"params._source.pop\"}, \"state\":{\"script\": \"params._source.state\"}, "
operator|+
literal|"\"id\":{\"script\": \"params._source.id\"}}"
argument_list|)
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
literal|"\"query\" : {\"constant_score\":{\"filter\":{\"bool\":{\"should\":"
operator|+
literal|"[{\"term\":{\"pop\":96074}},{\"term\":{\"pop\":99568}}]}}}}"
block|,
literal|"\"script_fields\": {\"longitude\":{\"script\":\"params._source.loc[0]\"}, "
operator|+
literal|"\"latitude\":{\"script\":\"params._source.loc[1]\"}, "
operator|+
literal|"\"city\":{\"script\": \"params._source.city\"}, "
operator|+
literal|"\"pop\":{\"script\": \"params._source.pop\"}, "
operator|+
literal|"\"state\":{\"script\": \"params._source.state\"}, "
operator|+
literal|"\"id\":{\"script\": \"params._source.id\"}}"
block|}
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from zips where \"pop\" in (96074, 99568)"
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
literal|"select \"state\", \"city\" from zips"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
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
literal|"select \"state\", \"city\", 0 as \"zero\"\n"
operator|+
literal|"from zips\n"
operator|+
literal|"order by \"state\", \"city\""
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
literal|"\"script_fields\": "
operator|+
literal|"{\"zero\":{\"script\": \"0\"}, "
operator|+
literal|"\"state\":{\"script\": \"params._source.state\"}, "
operator|+
literal|"\"city\":{\"script\": \"params._source.city\"}}"
argument_list|,
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"city\": \"asc\"}]"
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
literal|"  ElasticsearchProject(state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    ElasticsearchFilter(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elastic, zips]])"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"state\", \"city\" from zips where \"state\" = 'CA'"
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
literal|"select \"state\", \"city\" from zips where 'WI'< \"state\" order by \"city\""
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
literal|"select \"state\", \"city\" from zips where \"state\"> 'WI' order by \"city\""
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
block|}
end_class

begin_comment
comment|// End ElasticSearchAdapterTest.java
end_comment

end_unit
