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
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|Pair
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
name|base
operator|.
name|Throwables
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Ordering
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|List
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
comment|/**  * Tests for the {@code org.apache.calcite.adapter.mongodb} package.  *  *<p>Before calling this test, you need to populate MongoDB, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-test-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with MongoDB and "zips" and "foodmart"  * data sets.  */
end_comment

begin_class
specifier|public
class|class
name|MongoAdapterIT
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
literal|"       factory: 'org.apache.calcite.adapter.mongodb.MongoSchemaFactory',\n"
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
name|MongoAdapterIT
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
name|MongoAdapterIT
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
comment|/** Whether to run Mongo tests. Enabled by default, however test is only    * included if "it" profile is activated ({@code -Pit}). To disable,    * specify {@code -Dcalcite.test.mongodb=false} on the Java command line. */
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Util
operator|.
name|getBooleanProperty
argument_list|(
literal|"calcite.test.mongodb"
argument_list|,
literal|true
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
name|Object
index|[]
name|actualArray
init|=
name|actual
operator|==
literal|null
operator|||
name|actual
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
operator|(
name|List
operator|)
name|actual
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|assertArrayEqual
argument_list|(
literal|"expected MongoDB query not found"
argument_list|,
name|strings
argument_list|,
name|actualArray
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/** Similar to {@link CalciteAssert#checkResultUnordered}, but filters strings    * before comparing them. */
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
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|lines
argument_list|)
argument_list|)
decl_stmt|;
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
name|CalciteAssert
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
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|actualList
argument_list|)
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
name|CalciteAssert
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
literal|29353
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=MongoToEnumerableConverter\n"
operator|+
literal|"  MongoSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    MongoProject(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], ID=[CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
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
name|CalciteAssert
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
literal|"STATE=AK; ID=99503\n"
operator|+
literal|"STATE=AK; ID=99504\n"
operator|+
literal|"STATE=AK; ID=99505\n"
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
name|CalciteAssert
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
name|CalciteAssert
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
name|Ignore
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
name|CalciteAssert
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
literal|"    MongoProject(CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], LONGITUDE=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], LATITUDE=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], POP=[CAST(ITEM($0, 'pop')):INTEGER], STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], ID=[CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      MongoFilter(condition=[AND(=(CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'SPRINGFIELD'),>=(CAST(ITEM($0, '_id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '70000'))])\n"
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
name|CalciteAssert
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
literal|""
operator|+
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
name|CalciteAssert
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
name|Ignore
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|"  \"$match\": {\n"
operator|+
literal|"    \"state\": \"OK\"\n"
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
name|CalciteAssert
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
literal|"  MongoProject(warehouse_id=[CAST(ITEM($0, 'warehouse_id')):DOUBLE], warehouse_state_province=[CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    MongoFilter(condition=[=(CAST(ITEM($0, 'warehouse_state_province')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|29353
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
name|CalciteAssert
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
literal|"EXPR$0=29353\n"
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
name|CalciteAssert
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
literal|"select count(*)*2 from zips"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=58706\n"
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
name|CalciteAssert
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
literal|"select count(*) from zips group by state order by 1"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=24\n"
operator|+
literal|"EXPR$0=53\n"
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
name|CalciteAssert
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
literal|"select state, count(*) as c from zips group by state order by state"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AK; C=195\n"
operator|+
literal|"STATE=AL; C=567\n"
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
name|CalciteAssert
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
literal|"C=195; STATE=AK\n"
operator|+
literal|"C=567; STATE=AL\n"
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
name|CalciteAssert
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
literal|"STATE=AK; A=2793\n"
operator|+
literal|"STATE=AL; A=7126\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {POP: '$pop', STATE: '$state'}}"
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
name|CalciteAssert
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
literal|"STATE=AK; A=2793; S=544698; C=195\n"
operator|+
literal|"STATE=AL; A=7126; S=4040587; C=567\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {POP: '$pop', STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: '$STATE', _1: {$sum: '$POP'}, _2: {$sum: {$cond: [ {$eq: ['POP', null]}, 0, 1]}}}}"
argument_list|,
literal|"{$project: {STATE: '$_id', _1: '$_1', _2: '$_2'}}"
argument_list|,
literal|"{$sort: {STATE: 1}}"
argument_list|,
literal|"{$project: {STATE: 1, A: {$divide: [{$cond:[{$eq: ['$_2', {$literal: 0}]},null,'$_1']}, '$_2']}, S: {$cond:[{$eq: ['$_2', {$literal: 0}]},null,'$_1']}, C: '$_2'}}"
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
name|CalciteAssert
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
literal|"group by state having count(*)> 1500 order by state"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=CA; C=1516\n"
operator|+
literal|"STATE=NY; C=1595\n"
operator|+
literal|"STATE=TX; C=1671\n"
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
literal|"      \"$gt\": 1500\n"
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
name|Ignore
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|"C=195; STATE=AK; MIN_POP=0; MAX_POP=32383; SUM_POP=544698\n"
operator|+
literal|"C=567; STATE=AL; MIN_POP=0; MAX_POP=44165; SUM_POP=4040587\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|mongoChecker
argument_list|(
literal|"{$project: {POP: '$pop', STATE: '$state'}}"
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
name|CalciteAssert
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
literal|"{$project: {CITY: '$city', STATE: '$state'}}"
argument_list|,
literal|"{$group: {_id: {CITY: '$CITY', STATE: '$STATE'}, C: {$sum: 1}}}"
argument_list|,
literal|"{$project: {_id: 0, CITY: '$_id.CITY', STATE: '$_id.STATE', C: '$C'}}"
argument_list|,
literal|"{$sort: {C: -1}}"
argument_list|,
literal|"{$limit: 2}"
argument_list|,
literal|"{$project: {C: 1, STATE: 1, CITY: 1}}"
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|"STATE=NY; CDC=1370\n"
operator|+
literal|"STATE=PA; CDC=1369\n"
operator|+
literal|"STATE=TX; CDC=1233\n"
operator|+
literal|"STATE=IL; CDC=1148\n"
operator|+
literal|"STATE=CA; CDC=1072\n"
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|"  MongoProject(STATE=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], CITY=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    MongoFilter(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
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
name|CalciteAssert
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
name|CalciteAssert
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
literal|8125
decl_stmt|;
specifier|final
name|int
name|lt9k
init|=
literal|21227
decl_stmt|;
specifier|final
name|int
name|gt8k
init|=
literal|8707
decl_stmt|;
specifier|final
name|int
name|lt8k
init|=
literal|20645
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
name|CalciteAssert
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
name|CalciteAssert
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
name|q
argument_list|)
operator|.
name|returnsCount
argument_list|(
name|expected
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
name|CalciteAssert
operator|.
name|AssertQuery
name|query1
init|=
name|CalciteAssert
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-286">[CALCITE-286]    * Error casting MongoDB date</a>. */
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
name|CalciteAssert
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
name|CalciteAssert
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
name|input
parameter_list|)
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
name|CoreMatchers
operator|.
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
name|CoreMatchers
operator|.
name|is
argument_list|(
literal|29353
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
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MongoAdapterIT.java
end_comment

end_unit

