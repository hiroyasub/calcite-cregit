begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
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
name|Pair
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
name|DriverManager
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Tests for the {@link net.hydromatic.optiq.impl.mongodb} package.  */
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
name|OptiqAssert
operator|.
name|ConnectionFactory
name|ZIPS
init|=
operator|new
name|OptiqAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
specifier|final
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|setProperty
argument_list|(
literal|"model"
argument_list|,
literal|"target/test-classes/mongo-zips-model.json"
argument_list|)
expr_stmt|;
return|return
operator|(
name|OptiqConnection
operator|)
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Connection factory based on the "mongo-zips" model. */
specifier|public
specifier|static
specifier|final
name|OptiqAssert
operator|.
name|ConnectionFactory
name|FOODMART
init|=
operator|new
name|OptiqAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|OptiqConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
specifier|final
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|setProperty
argument_list|(
literal|"model"
argument_list|,
literal|"target/test-classes/mongo-foodmart-model.json"
argument_list|)
expr_stmt|;
return|return
operator|(
name|OptiqConnection
operator|)
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Disabled by default, because we do not expect Mongo to be installed and    * populated with the FoodMart data set. */
specifier|private
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|true
return|;
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
name|assertThat
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
literal|"  EnumerableCalcRel(expr#0=[{inputs}], product_id=[$t0])\n"
operator|+
literal|"    MongoToEnumerableConverter\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1997]], ops=[[<{product_id: 1}, {$project ...}>]])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0=[{inputs}], product_id=[$t0])\n"
operator|+
literal|"    MongoToEnumerableConverter\n"
operator|+
literal|"      MongoTableScan(table=[[_foodmart, sales_fact_1998]], ops=[[<{product_id: 1}, {$project ...}>]])"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"product_id=337.0\n"
operator|+
literal|"product_id=1512.0\n"
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
name|assertThat
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
annotation|@
name|Test
specifier|public
name|void
name|testSelectWhere
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
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
literal|"PLAN=EnumerableCalcRel(expr#0..1=[{inputs}], expr#2=['CA'], expr#3=[=($t1, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoTableScan(table=[[_foodmart, warehouse]], ops=[[<{warehouse_id: 1, warehouse_state_province: 1}, {$project ...}>]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"warehouse_id=6.0; warehouse_state_province=CA\n"
operator|+
literal|"warehouse_id=7.0; warehouse_state_province=CA\n"
operator|+
literal|"warehouse_id=14.0; warehouse_state_province=CA\n"
operator|+
literal|"warehouse_id=24.0; warehouse_state_province=CA\n"
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
name|assertThat
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
literal|"store_id=1.0; store_name=Store 1\n"
operator|+
literal|"store_id=3.0; store_name=Store 3\n"
operator|+
literal|"store_id=7.0; store_name=Store 7\n"
operator|+
literal|"store_id=10.0; store_name=Store 10\n"
operator|+
literal|"store_id=11.0; store_name=Store 11\n"
operator|+
literal|"store_id=15.0; store_name=Store 15\n"
operator|+
literal|"store_id=16.0; store_name=Store 16\n"
operator|+
literal|"store_id=24.0; store_name=Store 24\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Query based on the "mongo-zips" model. */
annotation|@
name|Test
specifier|public
name|void
name|testZips
parameter_list|()
block|{
name|OptiqAssert
operator|.
name|assertThat
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
literal|"PLAN=EnumerableAggregateRel(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..4=[{inputs}], expr#5=[0], $f0=[$t5])\n"
operator|+
literal|"    MongoToEnumerableConverter\n"
operator|+
literal|"      MongoTableScan(table=[[mongo_raw, zips]], ops=[[<{city: 1, loc: 1, pop: 1, state: 1, _id: 1}, {$project ...}>]])"
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
name|assertThat
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
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"STATE=AL; CITY=ACMAR\n"
operator|+
literal|"STATE=AL; CITY=ADAMSVILLE\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableCalcRel(expr#0..4=[{inputs}], STATE=[$t3], CITY=[$t0])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoTableScan(table=[[mongo_raw, zips]], ops=[[<{city: 1, loc: 1, pop: 1, state: 1, _id: 1}, {$project ...}>]])"
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
name|assertThat
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
literal|"PLAN=EnumerableCalcRel(expr#0..4=[{inputs}], expr#5=['CA'], expr#6=[=($t3, $t5)], STATE=[$t3], CITY=[$t0], $condition=[$t6])\n"
operator|+
literal|"  MongoToEnumerableConverter\n"
operator|+
literal|"    MongoTableScan(table=[[mongo_raw, zips]], ops=[[<{city: 1, loc: 1, pop: 1, state: 1, _id: 1}, {$project ...}>]])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testFoodmartQueries
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
name|assertThat
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
block|}
end_class

begin_comment
comment|// End MongoAdapterTest.java
end_comment

end_unit

