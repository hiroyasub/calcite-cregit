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
name|rel
operator|.
name|rel2sql
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
name|plan
operator|.
name|RelTraitDef
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
name|RelNode
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
name|sql
operator|.
name|SqlDialect
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|parser
operator|.
name|SqlParser
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
name|tools
operator|.
name|FrameworkConfig
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|Planner
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
name|tools
operator|.
name|Program
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for utility {@link RelToSqlConverter}  */
end_comment

begin_class
specifier|public
class|class
name|RelToSqlConverterTest
block|{
specifier|private
name|Planner
name|logicalPlanner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
name|void
name|checkRel2Sql
parameter_list|(
name|Planner
name|planner
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|expectedQeury
parameter_list|)
block|{
try|try
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
name|RelNode
name|rel
init|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
operator|.
name|rel
decl_stmt|;
specifier|final
name|RelToSqlConverter
name|converter
init|=
operator|new
name|RelToSqlConverter
argument_list|(
name|SqlDialect
operator|.
name|CALCITE
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|converter
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|rel
argument_list|)
operator|.
name|asQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|SqlDialect
operator|.
name|CALCITE
argument_list|)
operator|.
name|getSql
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedQeury
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Parsing failed throwing error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Planner
name|getPlanner
parameter_list|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
name|Program
modifier|...
name|programs
parameter_list|)
block|{
return|return
name|getPlanner
argument_list|(
name|traitDefs
argument_list|,
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|programs
argument_list|)
return|;
block|}
specifier|private
name|Planner
name|getPlanner
parameter_list|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|Program
modifier|...
name|programs
parameter_list|)
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|parserConfig
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|)
argument_list|)
operator|.
name|traitDefs
argument_list|(
name|traitDefs
argument_list|)
operator|.
name|programs
argument_list|(
name|programs
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleSelectStarFromProductTable
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"product\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT *\nFROM \"foodmart\".\"product\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleSelectQueryFromProductTable
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\", \"product_class_id\" from \"product\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"product_class_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
argument_list|)
expr_stmt|;
block|}
comment|//TODO: add test for query -> select * from product
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithWhereClauseOfLessThan
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\", \"shelf_width\"  from \"product\" where \"product_id\"< 10"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"shelf_width\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\"< 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithWhereClauseOfBasicOperators
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"product\" "
operator|+
literal|"where (\"product_id\" = 10 OR \"product_id\"<= 5) "
operator|+
literal|"AND (80>= \"shelf_width\" OR \"shelf_width\"> 30)"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE (\"product_id\" = 10 OR \"product_id\"<= 5) "
operator|+
literal|"AND (80>= \"shelf_width\" OR \"shelf_width\"> 30)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupBy
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*) from \"product\" group by \"product_class_id\", \"product_id\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithMinAggregateFunction
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select min(\"net_weight\") from \"product\" group by \"product_class_id\" "
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT MIN(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithMinAggregateFunction1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_class_id\", min(\"net_weight\") from"
operator|+
literal|" \"product\" group by \"product_class_id\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_class_id\", MIN(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithSumAggregateFunction
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select sum(\"net_weight\") from \"product\" group by \"product_class_id\" "
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT SUM(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithMultipleAggregateFunction
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select sum(\"net_weight\"), min(\"low_fat\"), count(*)"
operator|+
literal|" from \"product\" group by \"product_class_id\" "
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT SUM(\"net_weight\"), MIN(\"low_fat\"), COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithMultipleAggregateFunction1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_class_id\", sum(\"net_weight\"), min(\"low_fat\"), count(*)"
operator|+
literal|" from \"product\" group by \"product_class_id\" "
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_class_id\", SUM(\"net_weight\"), MIN(\"low_fat\"), COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByAndProjectList
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_class_id\", \"product_id\", count(*) from \"product\" group "
operator|+
literal|"by \"product_class_id\", \"product_id\"  "
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_class_id\", \"product_id\", COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByAndProjectList1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*)  from \"product\" group by \"product_class_id\", \"product_id\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByHaving
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*) from \"product\" group by \"product_class_id\","
operator|+
literal|" \"product_id\"  having \"product_id\"> 10"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM (SELECT \"product_class_id\", \"product_id\", COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\") AS \"t0\"\n"
operator|+
literal|"WHERE \"product_id\"> 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithOrderByClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" order by \"net_weight\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithOrderByClause1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\", \"net_weight\" from \"product\" order by \"net_weight\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithTwoOrderByClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" order by \"net_weight\", \"gross_weight\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"net_weight\", \"gross_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\", \"gross_weight\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithAscDescOrderByClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\" order by \"net_weight\" asc, "
operator|+
literal|"\"gross_weight\" desc, \"low_fat\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\", \"net_weight\", \"gross_weight\", \"low_fat\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\", \"gross_weight\" DESC, \"low_fat\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithLimitClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" limit 100 offset 10"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"LIMIT 100 OFFSET 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryComplex
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*), \"units_per_case\" from \"product\" where \"cases_per_pallet\"> 100 "
operator|+
literal|"group by \"product_id\", \"units_per_case\" order by \"units_per_case\" desc"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT COUNT(*), \"units_per_case\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"cases_per_pallet\"> 100\n"
operator|+
literal|"GROUP BY \"product_id\", \"units_per_case\"\n"
operator|+
literal|"ORDER BY \"units_per_case\" DESC"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroup
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*), sum(\"employee_id\") from \"reserve_employee\" "
operator|+
literal|"where \"hire_date\"> '2015-01-01' "
operator|+
literal|"and (\"position_title\" = 'SDE' or \"position_title\" = 'SDM') "
operator|+
literal|"group by \"store_id\", \"position_title\""
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT COUNT(*), SUM(\"employee_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"reserve_employee\"\n"
operator|+
literal|"WHERE \"hire_date\"> '2015-01-01' "
operator|+
literal|"AND (\"position_title\" = 'SDE' OR \"position_title\" = 'SDM')\n"
operator|+
literal|"GROUP BY \"store_id\", \"position_title\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleJoin
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select *\n"
operator|+
literal|"from \"sales_fact_1997\" as s\n"
operator|+
literal|"  join \"customer\" as c using (\"customer_id\")\n"
operator|+
literal|"  join \"product\" as p using (\"product_id\")\n"
operator|+
literal|"  join \"product_class\" as pc using (\"product_class_id\")\n"
operator|+
literal|"where c.\"city\" = 'San Francisco'\n"
operator|+
literal|"and pc.\"product_department\" = 'Snacks'\n"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT *\nFROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"customer\" "
operator|+
literal|"ON \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"product\" "
operator|+
literal|"ON \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"product_class\" "
operator|+
literal|"ON \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"WHERE \"customer\".\"city\" = 'San Francisco' AND "
operator|+
literal|"\"product_class\".\"product_department\" = 'Snacks'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleIn
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"department\" where \"department_id\" in (\n"
operator|+
literal|"  select \"department_id\" from \"employee\"\n"
operator|+
literal|"  where \"store_id\"< 150)"
decl_stmt|;
name|checkRel2Sql
argument_list|(
name|this
operator|.
name|logicalPlanner
argument_list|,
name|query
argument_list|,
literal|"SELECT \"department\".\"department_id\", \"department\".\"department_description\"\n"
operator|+
literal|"FROM \"foodmart\".\"department\"\nINNER JOIN "
operator|+
literal|"(SELECT \"department_id\"\nFROM \"foodmart\".\"employee\"\n"
operator|+
literal|"WHERE \"store_id\"< 150\nGROUP BY \"department_id\") AS \"t1\" "
operator|+
literal|"ON \"department\".\"department_id\" = \"t1\".\"department_id\""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelToSqlConverterTest.java
end_comment

end_unit

