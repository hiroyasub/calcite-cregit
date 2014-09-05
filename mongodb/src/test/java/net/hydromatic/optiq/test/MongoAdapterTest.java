begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
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
name|base
operator|.
name|Function
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
name|ImmutableList
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
name|*
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
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code net.hydromatic.optiq.impl.mongodb} package.  *  *<p>Before calling this test, you need to populate MongoDB with the "zips"  * data set (as described in HOWTO.md)  * and "foodmart" data set, as follows:</p>  *  *<blockquote><code>  * JAR=~/.m2/repository/pentaho/mondrian-data-foodmart-json/  * 0.3/mondrian-data-foodmart-json-0.3.jar<br>  * mkdir /tmp/foodmart<br>  * cd /tmp/foodmart<br>  * jar xvf $JAR<br>  * for i in *.json; do<br>  *&nbsp;&nbsp;mongoimport --db foodmart --collection ${i/.json/} --file $i<br>  * done<br>  *</code></blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|MongoAdapterTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MONGO_FOODMART_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: '_foodmart',\n"
operator|+
literal|"       factory: 'net.hydromatic.optiq.impl.mongodb.MongoSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         host: 'localhost',\n"
operator|+
literal|"         database: 'foodmart'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     },\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'foodmart',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'sales_fact_1997',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select cast(_MAP[\\'product_id\\'] AS double) AS \"product_id\" from \"_foodmart\".\"sales_fact_1997\"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'sales_fact_1998',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select cast(_MAP[\\'product_id\\'] AS double) AS \"product_id\" from \"_foodmart\".\"sales_fact_1998\"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'store',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select cast(_MAP[\\'store_id\\'] AS double) AS \"store_id\", cast(_MAP[\\'store_name\\'] AS varchar(20)) AS \"store_name\" from \"_foodmart\".\"store\"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'warehouse',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select cast(_MAP[\\'warehouse_id\\'] AS double) AS \"warehouse_id\", cast(_MAP[\\'warehouse_state_province\\'] AS varchar(20)) AS \"warehouse_state_province\" from \"_foodmart\".\"warehouse\"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MONGO_FOODMART_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'foodmart',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|MONGO_FOODMART_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
comment|/** Connection factory based on the "mongo-zips" model. */
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ZIPS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|MongoAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/mongo-zips-model.json"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Connection factory based on the "mongo-zips" model. */
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|FOODMART
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|MongoAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/mongo-foodmart-model.json"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Whether to run Mongo tests. Disabled by default, because we do not expect    * Mongo to be installed and populated with the FoodMart data set. To enable,    * specify {@code -Doptiq.test.mongodb=true} on the Java command line. */
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"optiq.test.mongodb"
argument_list|)
decl_stmt|;
comment|/** Whether to run this test. */
specifier|protected
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|ENABLED
return|;
block|}
comment|/** Returns a function that checks that a particular MongoDB pipeline is    * generated to implement a query. */
specifier|private
specifier|static
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
name|mongoChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|strings
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|List
name|actual
parameter_list|)
block|{
if|if
condition|(
operator|!
name|actual
operator|.
name|contains
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|strings
argument_list|)
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected MongoDB query not found; actual: "
operator|+
name|actual
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/** Similar to {@link OptiqAssert#checkResultUnordered}, but filters strings    * before comparing them. */
specifier|static
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|checkResultUnordered
parameter_list|(
specifier|final
name|String
modifier|...
name|lines
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|lines
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedList
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|OptiqAssert
operator|.
name|toStringList
argument_list|(
name|resultSet
argument_list|,
name|actualList
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actualList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|actualList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|actualList
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\\.0;"
argument_list|,
literal|";"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\.0$"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|actualList
argument_list|,
name|equalTo
argument_list|(
name|expectedList
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips order by state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|29467
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoSortRel(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    MongoProjectRel(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT NOT NULL], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT NOT NULL], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], ID=[CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, id from zips\n"
operator|+
literal|"order by state, id offset 2 rows fetch next 3 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; ID=99502\n"
operator|+
literal|"STATE=AK; ID=99503\n"
operator|+
literal|"STATE=AK; ID=99504\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
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
name|Test
specifier|public
name|void
name|testFilterSort
parameter_list|()
block|{
comment|// LONGITUDE and LATITUDE are null because of OPTIQ-194.
name|Util
operator|.
name|discard
argument_list|(
name|Bug
operator|.
name|OPTIQ_194_FIXED
argument_list|)
expr_stmt|;
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips\n"
operator|+
literal|"where city = 'SPRINGFIELD' and id between '20000' and '30000'\n"
operator|+
literal|"order by state"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=2184; STATE=SC; ID=29146\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=16811; STATE=VA; ID=22150\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=32161; STATE=VA; ID=22153\n"
operator|+
literal|"CITY=SPRINGFIELD; LONGITUDE=null; LATITUDE=null; POP=1321; STATE=WV; ID=26763\n"
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
literal|"      $lte: \"30000\",\n"
operator|+
literal|"      $gte: \"20000\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {CITY: '$city', LONGITUDE: '$loc[0]', LATITUDE: '$loc[1]', POP: '$pop', STATE: '$state', ID: '$_id'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoSortRel(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    MongoProjectRel(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT NOT NULL], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT NOT NULL], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], ID=[CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      MongoFilterRel(condition=[AND(=(CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'SPRINGFIELD'),>=(CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '20000'),<=(CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '30000'))])\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips\n"
operator|+
literal|"where pop BETWEEN 20000 AND 20100\n"
operator|+
literal|"order by state desc, pop"
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returns
argument_list|(
literal|"CITY=SHERIDAN; LONGITUDE=null; LATITUDE=null; POP=20025; STATE=WY; ID=82801\n"
operator|+
literal|"CITY=MOUNTLAKE TERRAC; LONGITUDE=null; LATITUDE=null; POP=20059; STATE=WA; ID=98043\n"
operator|+
literal|"CITY=FALMOUTH; LONGITUDE=null; LATITUDE=null; POP=20039; STATE=VA; ID=22405\n"
operator|+
literal|"CITY=FORT WORTH; LONGITUDE=null; LATITUDE=null; POP=20012; STATE=TX; ID=76104\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionPlan
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|withModel
argument_list|(
name|MONGO_FOODMART_MODEL
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
literal|"PLAN=EnumerableUnionRel(all=[true])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoProjectRel(product_id=[CAST(ITEM($0, 'product_id')):DOUBLE])\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1997]])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoProjectRel(product_id=[CAST(ITEM($0, 'product_id')):DOUBLE])\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1998]])\n"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
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
name|Test
specifier|public
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|withModel
argument_list|(
name|MONGO_FOODMART_MODEL
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
comment|/** Tests that we don't generate multiple constraints on the same column.    * MongoDB doesn't like it. If there is an '=', it supersedes all other    * operators. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterRedundant
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
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
argument_list|(
literal|"{\n"
operator|+
literal|"  $match: {\n"
operator|+
literal|"    state: \"OK\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {CITY: '$city', LONGITUDE: '$loc[0]', LATITUDE: '$loc[1]', POP: '$pop', STATE: '$state', ID: '$_id'}}"
argument_list|)
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|withModel
argument_list|(
name|MONGO_FOODMART_MODEL
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
literal|"  MongoProjectRel(warehouse_id=[CAST(ITEM($0, 'warehouse_id')):DOUBLE], warehouse_state_province=[CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    MongoFilterRel(condition=[=(CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, warehouse]])"
argument_list|)
operator|.
name|returns
argument_list|(
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
comment|// Per https://issues.apache.org/jira/browse/OPTIQ-164,
comment|// $match must occur before $project for good performance.
name|mongoChecker
argument_list|(
literal|"{\n"
operator|+
literal|"  $match: {\n"
operator|+
literal|"    warehouse_state_province: \"CA\"\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|withModel
argument_list|(
name|MONGO_FOODMART_MODEL
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
literal|"  $match: {\n"
operator|+
literal|"    $or: [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 1\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 10\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 11\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 15\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 16\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 24\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 3\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        store_name: \"Store 7\"\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|29467
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from zips"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=29467\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoAggregateRel(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"    MongoProjectRel(DUMMY=[0])\n"
operator|+
literal|"      MongoTableScan(table=[[mongo_raw, zips]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {DUMMY: {$ifNull: [null, 0]}}}"
argument_list|,
literal|"{$group: {_id: {}, 'EXPR$0': {$sum: 1}}}"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from zips group by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=659\n"
operator|+
literal|"EXPR$0=484\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(*) as c from zips group by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=WV; C=659\n"
operator|+
literal|"STATE=WA; C=484\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state from zips group by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=659; STATE=WV\n"
operator|+
literal|"C=484; STATE=WA\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(*) as c from zips\n"
operator|+
literal|"group by state having count(*)> 1500"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=NY; C=1596\n"
operator|+
literal|"STATE=TX; C=1676\n"
operator|+
literal|"STATE=CA; C=1523\n"
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
literal|"  $match: {\n"
operator|+
literal|"    C: {\n"
operator|+
literal|"      $gt: 1500\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"https://issues.apache.org/jira/browse/OPTIQ-270"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testGroupByHaving2
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state,\n"
operator|+
literal|" min(pop) as min_pop, max(pop) as max_pop, sum(pop) as sum_pop\n"
operator|+
literal|"from zips group by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=659; STATE=WV; MIN_POP=0; MAX_POP=70185; SUM_POP=1793477\n"
operator|+
literal|"C=484; STATE=WA; MIN_POP=2; MAX_POP=50515; SUM_POP=4866692\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c, state, city from zips\n"
operator|+
literal|"group by state, city order by c desc limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=93; STATE=TX; CITY=HOUSTON\n"
operator|+
literal|"C=56; STATE=CA; CITY=LOS ANGELES\n"
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
literal|"{$project: {C: 1, STATE: 1, CITY: 1}}"
argument_list|,
literal|"{$sort: {C: -1}}"
argument_list|,
literal|"{$limit: 2}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, count(distinct city) as cdc from zips\n"
operator|+
literal|"where state in ('CA', 'TX') group by state"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=CA; CDC=1079\n"
operator|+
literal|"STATE=TX; CDC=1238\n"
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
literal|"    $or: [\n"
operator|+
literal|"      {\n"
operator|+
literal|"        state: \"CA\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"        state: \"TX\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    ]\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
literal|"{$project: {STATE: '$state', CITY: '$city'}}"
argument_list|,
literal|"{$group: {_id: {STATE: '$STATE', CITY: '$CITY'}}}"
argument_list|,
literal|"{$project: {_id: 0, STATE: '$_id.STATE', CITY: '$_id.CITY'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', CDC: {$sum: {$cond: [ {$eq: ['CITY', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', CDC: '$CDC'}}"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
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
literal|"order by cdc desc limit 5"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=NY; CDC=1371\n"
operator|+
literal|"STATE=PA; CDC=1369\n"
operator|+
literal|"STATE=TX; CDC=1238\n"
operator|+
literal|"STATE=IL; CDC=1151\n"
operator|+
literal|"STATE=CA; CDC=1079\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', CITY: '$city'}}"
argument_list|,
literal|"{$group: {_id: {STATE: '$STATE', CITY: '$CITY'}}}"
argument_list|,
literal|"{$project: {_id: 0, STATE: '$_id.STATE', CITY: '$_id.CITY'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', CDC: {$sum: {$cond: [ {$eq: ['CITY', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', CDC: '$CDC'}}"
argument_list|,
literal|"{$sort: {CDC: -1}}"
argument_list|,
literal|"{$limit: 5}"
argument_list|)
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city, 0 as zero from zips"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AL; CITY=ACMAR; ZERO=0\n"
operator|+
literal|"STATE=AL; CITY=ADAMSVILLE; ZERO=0\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {STATE: '$state', CITY: '$city', ZERO: {$ifNull: [null, 0]}}}"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips where state = 'CA'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=CA; CITY=LOS ANGELES\n"
operator|+
literal|"STATE=CA; CITY=LOS ANGELES\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoProjectRel(STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    MongoFilterRel(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips where 'WI'< state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=WV; CITY=BLUEWELL\n"
operator|+
literal|"STATE=WV; CITY=ATHENS\n"
argument_list|)
expr_stmt|;
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select state, city from zips where state> 'WI'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=WV; CITY=BLUEWELL\n"
operator|+
literal|"STATE=WV; CITY=ATHENS\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testFoodmartQueries
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|queries
init|=
name|JdbcTest
operator|.
name|getFoodmartQueries
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|query
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|queries
argument_list|)
control|)
block|{
comment|//      if (query.i != 29) continue;
if|if
condition|(
name|query
operator|.
name|e
operator|.
name|left
operator|.
name|contains
argument_list|(
literal|"agg_"
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|OptiqAssert
operator|.
name|AssertQuery
name|query1
init|=
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|FOODMART
argument_list|)
operator|.
name|query
argument_list|(
name|query
operator|.
name|e
operator|.
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|e
operator|.
name|right
operator|!=
literal|null
condition|)
block|{
name|query1
operator|.
name|returns
argument_list|(
name|query
operator|.
name|e
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query1
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/OPTIQ-286">OPTIQ-286</a>,    * "Error casting MongoDB date". */
annotation|@
name|Test
specifier|public
name|void
name|testDate
parameter_list|()
block|{
comment|// Assumes that you have created the following collection before running
comment|// this test:
comment|//
comment|// $ mongo
comment|//> use test
comment|// switched to db test
comment|//> db.createCollection("datatypes")
comment|// { "ok" : 1 }
comment|//> db.datatypes.insert( {
comment|//     "_id" : ObjectId("53655599e4b0c980df0a8c27"),
comment|//     "_class" : "com.ericblue.Test",
comment|//     "date" : ISODate("2012-09-05T07:00:00Z"),
comment|//     "value" : 1231,
comment|//     "ownerId" : "531e7789e4b0853ddb861313"
comment|//   } )
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|withModel
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
literal|"       factory: 'net.hydromatic.optiq.impl.mongodb.MongoSchemaFactory',\n"
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
block|}
end_class

begin_comment
comment|// End MongoAdapterTest.java
end_comment

end_unit

