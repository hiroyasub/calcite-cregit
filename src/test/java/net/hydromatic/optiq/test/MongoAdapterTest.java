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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Tests for the {@link net.hydromatic.optiq.impl.mongodb} package.  */
end_comment

begin_class
specifier|public
class|class
name|MongoAdapterTest
extends|extends
name|TestCase
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
literal|"           name: 'warehouse',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'select cast(_MAP[\\'warehouse_id\\'] AS double) AS \"warehouse_id\", cast(_MAP[\\'warehouse_state_province\\'] AS varchar(20)) AS \"warehouse_state_province\" from \"_foodmart\".\"warehouse\"'\n"
operator|+
literal|"         },\n"
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
comment|/** Disabled by default, because we do not expect Mongo to be installed and    * populated with the FoodMart data set. */
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|<
literal|0
decl_stmt|;
specifier|public
name|void
name|testUnionPlan
parameter_list|()
block|{
if|if
condition|(
operator|!
name|ENABLED
condition|)
block|{
return|return;
block|}
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
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
literal|"  EnumerableCalcRel(expr#0=[{inputs}], expr#1=['product_id'], expr#2=[ITEM($t0, $t1)], expr#3=[CAST($t2):DOUBLE NOT NULL], product_id=[$t3])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[_foodmart, sales_fact_1997]])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0=[{inputs}], expr#1=['product_id'], expr#2=[ITEM($t0, $t1)], expr#3=[CAST($t2):DOUBLE NOT NULL], product_id=[$t3])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[_foodmart, sales_fact_1998]])"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
if|if
condition|(
operator|!
name|ENABLED
condition|)
block|{
return|return;
block|}
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
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
specifier|public
name|void
name|testSelectWhere
parameter_list|()
block|{
if|if
condition|(
operator|!
name|ENABLED
condition|)
block|{
return|return;
block|}
name|OptiqAssert
operator|.
name|assertThat
argument_list|()
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
literal|"PLAN=EnumerableCalcRel(expr#0=[{inputs}], expr#1=['warehouse_id'], expr#2=[ITEM($t0, $t1)], expr#3=[CAST($t2):DOUBLE NOT NULL], expr#4=['warehouse_state_province'], expr#5=[ITEM($t0, $t4)], expr#6=[CAST($t5):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL], expr#7=['CA'], expr#8=[=($t6, $t7)], warehouse_id=[$t3], warehouse_state_province=[$t6], $condition=[$t8])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[_foodmart, warehouse]])"
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
block|}
end_class

begin_comment
comment|// End MongoAdapterTest.java
end_comment

end_unit

