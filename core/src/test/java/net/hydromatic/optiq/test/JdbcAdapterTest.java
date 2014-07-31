begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code net.hydromatic.optiq.impl.jdbc} package.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcAdapterTest
block|{
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
name|withModel
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
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
literal|"PLAN=JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcUnionRel(all=[true])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, sales_fact_1997]])\n"
operator|+
literal|"    JdbcTableScan(table=[[foodmart, sales_fact_1998]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|OptiqAssert
operator|.
name|CONNECTION_SPEC
operator|.
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:hsqldb:"
argument_list|)
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1998\""
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
name|withModel
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
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
operator|.
name|enable
argument_list|(
name|OptiqAssert
operator|.
name|CONNECTION_SPEC
operator|.
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:hsqldb:"
argument_list|)
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = 1\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1998\"\n"
operator|+
literal|"WHERE \"product_id\" = 1"
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
name|withModel
argument_list|(
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"store_name\" from \"store\"\n"
operator|+
literal|"where \"store_name\" in ('Store 1', 'Store 10', 'Store 11', 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|enable
argument_list|(
name|OptiqAssert
operator|.
name|CONNECTION_SPEC
operator|.
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:hsqldb:"
argument_list|)
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT \"store_id\", \"store_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"store\"\n"
operator|+
literal|"WHERE \"store_name\" = 'Store 1' OR \"store_name\" = 'Store 10' OR \"store_name\" = 'Store 11' OR \"store_name\" = 'Store 15' OR \"store_name\" = 'Store 16' OR \"store_name\" = 'Store 24' OR \"store_name\" = 'Store 3' OR \"store_name\" = 'Store 7'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=1; store_name=Store 1\n"
operator|+
literal|"store_id=3; store_name=Store 3\n"
operator|+
literal|"store_id=7; store_name=Store 7\n"
operator|+
literal|"store_id=10; store_name=Store 10\n"
operator|+
literal|"store_id=11; store_name=Store 11\n"
operator|+
literal|"store_id=15; store_name=Store 15\n"
operator|+
literal|"store_id=16; store_name=Store 16\n"
operator|+
literal|"store_id=24; store_name=Store 24\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcAdapterTest.java
end_comment

end_unit

