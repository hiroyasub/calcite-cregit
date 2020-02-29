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
name|mongodb
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
name|schema
operator|.
name|Schema
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
name|SchemaFactory
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|MongoAssertions
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
name|Bug
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
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
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|foodmart
operator|.
name|data
operator|.
name|json
operator|.
name|FoodmartJson
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonDateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonInt32
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|json
operator|.
name|JsonWriterSettings
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
name|Disabled
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
name|extension
operator|.
name|RegisterExtension
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
name|net
operator|.
name|URL
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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalDate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
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
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
comment|/**  * Testing mongo adapter functionality. By default runs with  *<a href="https://github.com/fakemongo/fongo">Fongo</a> unless {@code IT} maven profile is enabled  * (via {@code $ mvn -Pit install}).  *  * @see MongoDatabasePolicy  */
end_comment

begin_class
specifier|public
class|class
name|MongoAdapterTest
implements|implements
name|SchemaFactory
block|{
comment|/** Connection factory based on the "mongo-zips" model. */
specifier|protected
specifier|static
specifier|final
name|URL
name|MODEL
init|=
name|MongoAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/mongo-model.json"
argument_list|)
decl_stmt|;
comment|/** Number of records in local file */
specifier|protected
specifier|static
specifier|final
name|int
name|ZIPS_SIZE
init|=
literal|149
decl_stmt|;
annotation|@
name|RegisterExtension
specifier|public
specifier|static
specifier|final
name|MongoDatabasePolicy
name|POLICY
init|=
name|MongoDatabasePolicy
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|MongoSchema
name|schema
decl_stmt|;
annotation|@
name|BeforeAll
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoDatabase
name|database
init|=
name|POLICY
operator|.
name|database
argument_list|()
decl_stmt|;
name|populate
argument_list|(
name|database
operator|.
name|getCollection
argument_list|(
literal|"zips"
argument_list|)
argument_list|,
name|MongoAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/zips-mini.json"
argument_list|)
argument_list|)
expr_stmt|;
name|populate
argument_list|(
name|database
operator|.
name|getCollection
argument_list|(
literal|"store"
argument_list|)
argument_list|,
name|FoodmartJson
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/store.json"
argument_list|)
argument_list|)
expr_stmt|;
name|populate
argument_list|(
name|database
operator|.
name|getCollection
argument_list|(
literal|"warehouse"
argument_list|)
argument_list|,
name|FoodmartJson
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/warehouse.json"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Manually insert data for data-time test.
name|MongoCollection
argument_list|<
name|BsonDocument
argument_list|>
name|datatypes
init|=
name|database
operator|.
name|getCollection
argument_list|(
literal|"datatypes"
argument_list|)
operator|.
name|withDocumentClass
argument_list|(
name|BsonDocument
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|datatypes
operator|.
name|countDocuments
argument_list|()
operator|>
literal|0
condition|)
block|{
name|datatypes
operator|.
name|deleteMany
argument_list|(
operator|new
name|BsonDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BsonDocument
name|doc
init|=
operator|new
name|BsonDocument
argument_list|()
decl_stmt|;
name|Instant
name|instant
init|=
name|LocalDate
operator|.
name|of
argument_list|(
literal|2012
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|)
operator|.
name|atStartOfDay
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|toInstant
argument_list|()
decl_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
operator|new
name|BsonDateTime
argument_list|(
name|instant
operator|.
name|toEpochMilli
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
operator|new
name|BsonInt32
argument_list|(
literal|1231
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
literal|"ownerId"
argument_list|,
operator|new
name|BsonString
argument_list|(
literal|"531e7789e4b0853ddb861313"
argument_list|)
argument_list|)
expr_stmt|;
name|datatypes
operator|.
name|insertOne
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|schema
operator|=
operator|new
name|MongoSchema
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|populate
parameter_list|(
name|MongoCollection
argument_list|<
name|Document
argument_list|>
name|collection
parameter_list|,
name|URL
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|collection
argument_list|,
literal|"collection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|.
name|countDocuments
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// delete any existing documents (run from a clean set)
name|collection
operator|.
name|deleteMany
argument_list|(
operator|new
name|BsonDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|MongoCollection
argument_list|<
name|BsonDocument
argument_list|>
name|bsonCollection
init|=
name|collection
operator|.
name|withDocumentClass
argument_list|(
name|BsonDocument
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|readLines
argument_list|(
name|resource
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
name|bsonCollection
operator|.
name|insertOne
argument_list|(
name|BsonDocument
operator|.
name|parse
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
block|}
comment|/**    *  Returns always the same schema to avoid initialization costs.    */
annotation|@
name|Override
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
return|return
name|schema
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|assertModel
parameter_list|(
name|String
name|model
parameter_list|)
block|{
comment|// ensure that Schema from this instance is being used
name|model
operator|=
name|model
operator|.
name|replace
argument_list|(
name|MongoSchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|MongoAdapterTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|model
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|assertModel
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|assertModel
argument_list|(
name|Resources
operator|.
name|toString
argument_list|(
name|url
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
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
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    MongoProject(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20)], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2)], ID=[CAST(ITEM($0, '_id')):VARCHAR(5)])\n"
operator|+
literal|"      MongoTableScan(table=[[mongo_raw, zips]])"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, id from zips\n"
operator|+
literal|"order by state, id offset 2 rows fetch next 3 rows only"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"STATE=AK; ID=99801"
argument_list|,
literal|"STATE=AL; ID=35215"
argument_list|,
literal|"STATE=AL; ID=35401"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', ID: '$_id'}}"
argument_list|,
literal|"{$sort: {STATE: 1, ID: 1}}"
argument_list|,
literal|"{$skip: 2}"
argument_list|,
literal|"{$limit: 3}"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, id from zips\n"
operator|+
literal|"offset 2 fetch next 3 rows only"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$skip: 2}"
argument_list|,
literal|"{$limit: 3}"
argument_list|,
literal|"{$project: {STATE: '$state', ID: '$_id'}}"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, id from zips\n"
operator|+
literal|"fetch next 3 rows only"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$limit: 3}"
argument_list|,
literal|"{$project: {STATE: '$state', ID: '$_id'}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testFilterSort
parameter_list|()
block|{
comment|// LONGITUDE and LATITUDE are null because of CALCITE-194.
name|Util
operator|.
name|discard
argument_list|(
name|Bug
operator|.
name|CALCITE_194_FIXED
argument_list|)
expr_stmt|;
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips\n"
operator|+
literal|"where city = 'SPRINGFIELD' and id>= '70000'\n"
operator|+
literal|"order by state, id"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=752; STATE=AR; ID=72157\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=1992; STATE=CO; ID=81073\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=5597; STATE=LA; ID=70462\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=32384; STATE=OR; ID=97477\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=27521; STATE=OR; ID=97478\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{\n"
operator|+
literal|"  $match: {\n"
operator|+
literal|"    city: \"SPRINGFIELD\",\n"
operator|+
literal|"    _id: {\n"
operator|+
literal|"      $gte: \"70000\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {CITY: '$city', LONGITUDE: '$loc[0]', LATITUDE: '$loc[1]', POP: '$pop', STATE: '$state', ID: '$_id'}}"
argument_list|,
literal|"{$sort: {STATE: 1, ID: 1}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoSort(sort0=[$4], sort1=[$5], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    MongoProject(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20)], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2)], ID=[CAST(ITEM($0, '_id')):VARCHAR(5)])\n"
operator|+
literal|"      MongoFilter(condition=[AND(=(CAST(ITEM($0, 'city')):VARCHAR(20), 'SPRINGFIELD'),>=(CAST(ITEM($0, '_id')):VARCHAR(5), '70000'))])\n"
operator|+
literal|"        MongoTableScan(table=[[mongo_raw, zips]])"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips\n"
operator|+
literal|"where pop BETWEEN 45000 AND 46000\n"
operator|+
literal|"order by state desc, pop"
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"CITY=BECKLEY; LONGITUDE=null; LATITUDE=null; POP=45196; STATE=WV; ID=25801"
argument_list|,
literal|"CITY=ROCKERVILLE; LONGITUDE=null; LATITUDE=null; POP=45328; STATE=SD; ID=57701"
argument_list|,
literal|"CITY=PAWTUCKET; LONGITUDE=null; LATITUDE=null; POP=45442; STATE=RI; ID=02860"
argument_list|,
literal|"CITY=LAWTON; LONGITUDE=null; LATITUDE=null; POP=45542; STATE=OK; ID=73505"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"broken; [CALCITE-2115] is logged to fix it"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testUnionPlan
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"sales_fact_1997\"\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"sales_fact_1998\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableUnion(all=[true])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoProject(product_id=[CAST(ITEM($0, 'product_id')):DOUBLE])\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1997]])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoProject(product_id=[CAST(ITEM($0, 'product_id')):DOUBLE])\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1998]])"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
name|MongoAssertions
operator|.
name|checkResultUnordered
argument_list|(
literal|"product_id=337"
argument_list|,
literal|"product_id=1512"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Double"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from (\n"
operator|+
literal|"  select * from \"sales_fact_1997\"\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select * from \"sales_fact_1998\")\n"
operator|+
literal|"where \"product_id\" = 1"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that mongo query is empty when filter simplified to false.    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterRedundant
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips where state> 'CA' and state< 'AZ' and state = 'OK'"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWhere
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"warehouse\" where \"warehouse_state_province\" = 'CA'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoProject(warehouse_id=[CAST(ITEM($0, 'warehouse_id')):DOUBLE], warehouse_state_province=[CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20)])\n"
operator|+
literal|"    MongoFilter(condition=[=(CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20), 'CA')])\n"
operator|+
literal|"      MongoTableScan(table=[[mongo_raw, warehouse]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|MongoAssertions
operator|.
name|checkResultUnordered
argument_list|(
literal|"warehouse_id=6; warehouse_state_province=CA"
argument_list|,
literal|"warehouse_id=7; warehouse_state_province=CA"
argument_list|,
literal|"warehouse_id=14; warehouse_state_province=CA"
argument_list|,
literal|"warehouse_id=24; warehouse_state_province=CA"
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
comment|// Per https://issues.apache.org/jira/browse/CALCITE-164,
comment|// $match must occur before $project for good performance.
name|mongoChecker
argument_list|(
literal|"{\n"
operator|+
literal|"  \"$match\": {\n"
operator|+
literal|"    \"warehouse_state_province\": \"CA\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {warehouse_id: 1, warehouse_state_province: 1}}"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"store_name\" from \"store\"\n"
operator|+
literal|"where \"store_name\" in ('Store 1', 'Store 10', 'Store 11', 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')"
argument_list|)
operator|.
name|returns
argument_list|(
name|MongoAssertions
operator|.
name|checkResultUnordered
argument_list|(
literal|"store_id=1; store_name=Store 1"
argument_list|,
literal|"store_id=3; store_name=Store 3"
argument_list|,
literal|"store_id=7; store_name=Store 7"
argument_list|,
literal|"store_id=10; store_name=Store 10"
argument_list|,
literal|"store_id=11; store_name=Store 11"
argument_list|,
literal|"store_id=15; store_name=Store 15"
argument_list|,
literal|"store_id=16; store_name=Store 16"
argument_list|,
literal|"store_id=24; store_name=Store 24"
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{\n"
operator|+
literal|"  \"$match\": {\n"
operator|+
literal|"    \"$or\": [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 1\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 10\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 11\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 15\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 16\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 24\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 3\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"store_name\": \"Store 7\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    ]\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {store_id: 1, store_name: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Simple query based on the "mongo-zips" model. */
annotation|@
name|Test
specifier|public
name|void
name|testZips
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
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
name|testCountGroupByEmpty
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from zips"
argument_list|)
operator|.
name|returns
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"EXPR$0=%d\n"
argument_list|,
name|ZIPS_SIZE
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoAggregate(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"    MongoTableScan(table=[[mongo_raw, zips]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$group: {_id: {}, 'EXPR$0': {$sum: 1}}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountGroupByEmptyMultiplyBy2
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*)*2 from zips"
argument_list|)
operator|.
name|returns
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"EXPR$0=%d\n"
argument_list|,
name|ZIPS_SIZE
operator|*
literal|2
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$group: {_id: {}, _0: {$sum: 1}}}"
argument_list|,
literal|"{$project: {'EXPR$0': {$multiply: ['$_0', {$literal: 2}]}}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumnNotProjected
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from zips group by state order by 1"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2"
argument_list|,
literal|"EXPR$0=2"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', 'EXPR$0': {$sum: 1}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', 'EXPR$0': '$EXPR$0'}}"
argument_list|,
literal|"{$project: {'EXPR$0': 1}}"
argument_list|,
literal|"{$sort: {EXPR$0: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumn
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(*) as c from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; C=3\nSTATE=AL; C=3\nSTATE=AR; C=3\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', C: {$sum: 1}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', C: '$C'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumnReversed
parameter_list|()
block|{
comment|// Note extra $project compared to testGroupByOneColumn.
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=3; STATE=AK\nC=3; STATE=AL\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', C: {$sum: 1}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', C: '$C'}}"
argument_list|,
literal|"{$project: {C: 1, STATE: 1}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAvg
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, avg(pop) as a from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; A=26856\nSTATE=AL; A=43383\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', POP: '$pop'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', A: {$avg: '$POP'}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', A: '$A'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAvgSumCount
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, avg(pop) as a, sum(pop) as s, count(pop) as c from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; A=26856; S=80568; C=3\n"
operator|+
literal|"STATE=AL; A=43383; S=130151; C=3\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', POP: '$pop'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', _1: {$sum: '$POP'}, _2: {$sum: {$cond: [ {$eq: ['POP', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', _1: '$_1', _2: '$_2'}}"
argument_list|,
literal|"{$project: {STATE: 1, A: {$divide: [{$cond:[{$eq: ['$_2', {$literal: 0}]},null,'$_1']}, '$_2']}, S: {$cond:[{$eq: ['$_2', {$literal: 0}]},null,'$_1']}, C: '$_2'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByHaving
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(*) as c from zips\n"
operator|+
literal|"group by state having count(*)> 2 order by state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|47
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', C: {$sum: 1}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', C: '$C'}}"
argument_list|,
literal|"{\n"
operator|+
literal|"  \"$match\": {\n"
operator|+
literal|"    \"C\": {\n"
operator|+
literal|"      \"$gt\": 2\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"https://issues.apache.org/jira/browse/CALCITE-270"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testGroupByHaving2
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(*) as c from zips\n"
operator|+
literal|"group by state having sum(pop)> 12000000"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=NY; C=1596\n"
operator|+
literal|"STATE=TX; C=1676\n"
operator|+
literal|"STATE=FL; C=826\n"
operator|+
literal|"STATE=CA; C=1523\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', POP: '$pop'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', C: {$sum: 1}, _2: {$sum: '$POP'}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', C: '$C', _2: '$_2'}}"
argument_list|,
literal|"{\n"
operator|+
literal|"  $match: {\n"
operator|+
literal|"    _2: {\n"
operator|+
literal|"      $gt: 12000000\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {STATE: 1, C: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMinMaxSum
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state,\n"
operator|+
literal|" min(pop) as min_pop, max(pop) as max_pop, sum(pop) as sum_pop\n"
operator|+
literal|"from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=3; STATE=AK; MIN_POP=23238; MAX_POP=32383; SUM_POP=80568\n"
operator|+
literal|"C=3; STATE=AL; MIN_POP=42124; MAX_POP=44165; SUM_POP=130151\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', POP: '$pop'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', C: {$sum: 1}, MIN_POP: {$min: '$POP'}, MAX_POP: {$max: '$POP'}, SUM_POP: {$sum: '$POP'}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', C: '$C', MIN_POP: '$MIN_POP', MAX_POP: '$MAX_POP', SUM_POP: '$SUM_POP'}}"
argument_list|,
literal|"{$project: {C: 1, STATE: 1, MIN_POP: 1, MAX_POP: 1, SUM_POP: 1}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupComposite
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state, city from zips\n"
operator|+
literal|"group by state, city\n"
operator|+
literal|"order by c desc, city\n"
operator|+
literal|"limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=1; STATE=SD; CITY=ABERDEEN\n"
operator|+
literal|"C=1; STATE=SC; CITY=AIKEN\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', CITY: '$city'}}"
argument_list|,
literal|"{$group: {_id: {STATE: '$STATE', CITY: '$CITY'}, C: {$sum: 1}}}"
argument_list|,
literal|"{$project: {_id: 0, STATE: '$_id.STATE', CITY: '$_id.CITY', C: '$C'}}"
argument_list|,
literal|"{$sort: {C: -1, CITY: 1}}"
argument_list|,
literal|"{$limit: 2}"
argument_list|,
literal|"{$project: {C: 1, STATE: 1, CITY: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"broken; [CALCITE-2115] is logged to fix it"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(distinct city) as cdc from zips\n"
operator|+
literal|"where state in ('CA', 'TX') group by state order by state"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=CA; CDC=1072\n"
operator|+
literal|"STATE=TX; CDC=1233\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{\n"
operator|+
literal|"  \"$match\": {\n"
operator|+
literal|"    \"$or\": [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"state\": \"CA\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        \"state\": \"TX\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    ]\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {CITY: '$city', STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: {CITY: '$CITY', STATE: '$STATE'}}}"
argument_list|,
literal|"{$project: {_id: 0, CITY: '$_id.CITY', STATE: '$_id.STATE'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', CDC: {$sum: {$cond: [ {$eq: ['CITY', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', CDC: '$CDC'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountOrderBy
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(distinct city) as cdc\n"
operator|+
literal|"from zips\n"
operator|+
literal|"group by state\n"
operator|+
literal|"order by cdc desc, state\n"
operator|+
literal|"limit 5"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; CDC=3\n"
operator|+
literal|"STATE=AL; CDC=3\n"
operator|+
literal|"STATE=AR; CDC=3\n"
operator|+
literal|"STATE=AZ; CDC=3\n"
operator|+
literal|"STATE=CA; CDC=3\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {CITY: '$city', STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: {CITY: '$CITY', STATE: '$STATE'}}}"
argument_list|,
literal|"{$project: {_id: 0, CITY: '$_id.CITY', STATE: '$_id.STATE'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', CDC: {$sum: {$cond: [ {$eq: ['CITY', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', CDC: '$CDC'}}"
argument_list|,
literal|"{$sort: {CDC: -1, STATE: 1}}"
argument_list|,
literal|"{$limit: 5}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"broken; [CALCITE-2115] is logged to fix it"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testProject
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city, 0 as zero from zips order by state, city"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; CITY=AKHIOK; ZERO=0\n"
operator|+
literal|"STATE=AK; CITY=AKIACHAK; ZERO=0\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {CITY: '$city', STATE: '$state'}}"
argument_list|,
literal|"{$sort: {STATE: 1, CITY: 1}}"
argument_list|,
literal|"{$project: {STATE: 1, CITY: 1, ZERO: {$literal: 0}}}"
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
name|assertModel
argument_list|(
name|MODEL
argument_list|)
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
literal|"STATE=CA; CITY=LOS ANGELES"
argument_list|,
literal|"STATE=CA; CITY=BELL GARDENS"
argument_list|,
literal|"STATE=CA; CITY=NORWALK"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoProject(STATE=[CAST(ITEM($0, 'state')):VARCHAR(2)], CITY=[CAST(ITEM($0, 'city')):VARCHAR(20)])\n"
operator|+
literal|"    MongoFilter(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2), 'CA')])\n"
operator|+
literal|"      MongoTableScan(table=[[mongo_raw, zips]])"
argument_list|)
expr_stmt|;
block|}
comment|/** MongoDB's predicates are handed (they can only accept literals on the    * right-hand size) so it's worth testing that we handle them right both    * ways around. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterReversed
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips where 'WI'< state order by state, city"
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"STATE=WV; CITY=BECKLEY"
argument_list|,
literal|"STATE=WV; CITY=ELM GROVE"
argument_list|,
literal|"STATE=WV; CITY=STAR CITY"
argument_list|)
expr_stmt|;
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips where state> 'WI' order by state, city"
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"STATE=WV; CITY=BECKLEY"
argument_list|,
literal|"STATE=WV; CITY=ELM GROVE"
argument_list|,
literal|"STATE=WV; CITY=STAR CITY"
argument_list|)
expr_stmt|;
block|}
comment|/** MongoDB's predicates are handed (they can only accept literals on the    * right-hand size) so it's worth testing that we handle them right both    * ways around.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-740">[CALCITE-740]    * Redundant WHERE clause causes wrong result in MongoDB adapter</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterPair
parameter_list|()
block|{
specifier|final
name|int
name|gt9k
init|=
literal|148
decl_stmt|;
specifier|final
name|int
name|lt9k
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|gt8k
init|=
literal|148
decl_stmt|;
specifier|final
name|int
name|lt8k
init|=
literal|1
decl_stmt|;
name|checkPredicate
argument_list|(
name|gt9k
argument_list|,
literal|"where pop> 8000 and pop> 9000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|gt9k
argument_list|,
literal|"where pop> 9000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|lt9k
argument_list|,
literal|"where pop< 9000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|gt8k
argument_list|,
literal|"where pop> 8000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|lt8k
argument_list|,
literal|"where pop< 8000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|gt9k
argument_list|,
literal|"where pop> 9000 and pop> 8000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|gt8k
argument_list|,
literal|"where pop> 9000 or pop> 8000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|gt8k
argument_list|,
literal|"where pop> 8000 or pop> 9000"
argument_list|)
expr_stmt|;
name|checkPredicate
argument_list|(
name|lt8k
argument_list|,
literal|"where pop< 8000 and pop< 9000"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkPredicate
parameter_list|(
name|int
name|expected
parameter_list|,
name|String
name|q
parameter_list|)
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from zips\n"
operator|+
name|q
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C="
operator|+
name|expected
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips\n"
operator|+
name|q
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-286">[CALCITE-286]    * Error casting MongoDB date</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testDate
parameter_list|()
block|{
name|assertModel
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'test',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'test',\n"
operator|+
literal|"       factory: 'org.apache.calcite.adapter.mongodb.MongoSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         host: 'localhost',\n"
operator|+
literal|"         database: 'test'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cast(_MAP['date'] as DATE) from \"datatypes\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2012-09-05"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-665">[CALCITE-665]    * ClassCastException in MongoDB adapter</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCountViaInt
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from zips"
argument_list|)
operator|.
name|returns
argument_list|(
name|input
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|input
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|ZIPS_SIZE
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a function that checks that a particular MongoDB query    * has been called.    *    * @param expected Expected query (as array)    * @return validation function    */
specifier|private
specifier|static
name|Consumer
argument_list|<
name|List
argument_list|>
name|mongoChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|expected
parameter_list|)
block|{
return|return
name|actual
lambda|->
block|{
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
literal|"null mongo Query"
argument_list|,
name|actual
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|expected
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|CalciteAssert
operator|.
name|assertArrayEqual
argument_list|(
literal|"empty Mongo query"
argument_list|,
name|expected
argument_list|,
name|actual
operator|.
name|toArray
argument_list|(
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// comparing list of Bsons (expected and actual)
specifier|final
name|List
argument_list|<
name|BsonDocument
argument_list|>
name|expectedBsons
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|expected
argument_list|)
operator|.
name|map
argument_list|(
name|BsonDocument
operator|::
name|parse
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BsonDocument
argument_list|>
name|actualBsons
init|=
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|actual
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Objects
operator|::
name|toString
argument_list|)
operator|.
name|map
argument_list|(
name|BsonDocument
operator|::
name|parse
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
comment|// compare Bson (not string) representation
if|if
condition|(
operator|!
name|expectedBsons
operator|.
name|equals
argument_list|(
name|actualBsons
argument_list|)
condition|)
block|{
specifier|final
name|JsonWriterSettings
name|settings
init|=
name|JsonWriterSettings
operator|.
name|builder
argument_list|()
operator|.
name|indent
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// outputs Bson in pretty Json format (with new lines)
comment|// so output is human friendly in IDE diff tool
specifier|final
name|Function
argument_list|<
name|List
argument_list|<
name|BsonDocument
argument_list|>
argument_list|,
name|String
argument_list|>
name|prettyFn
init|=
name|bsons
lambda|->
name|bsons
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|b
lambda|->
name|b
operator|.
name|toJson
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
decl_stmt|;
comment|// used to pretty print Assertion error
name|assertEquals
argument_list|(
name|prettyFn
operator|.
name|apply
argument_list|(
name|expectedBsons
argument_list|)
argument_list|,
name|prettyFn
operator|.
name|apply
argument_list|(
name|actualBsons
argument_list|)
argument_list|,
literal|"expected and actual Mongo queries (pipelines) do not match"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed previously because expected != actual is known to be true"
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

