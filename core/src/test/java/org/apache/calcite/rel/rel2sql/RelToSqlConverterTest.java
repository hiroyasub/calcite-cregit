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
name|config
operator|.
name|NullCollation
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
name|plan
operator|.
name|RelOptLattice
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
name|plan
operator|.
name|RelOptMaterialization
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
name|plan
operator|.
name|RelOptPlanner
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
name|plan
operator|.
name|hep
operator|.
name|HepPlanner
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
name|plan
operator|.
name|hep
operator|.
name|HepProgram
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
name|plan
operator|.
name|hep
operator|.
name|HepProgramBuilder
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
name|rel
operator|.
name|rules
operator|.
name|UnionMergeRule
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
name|runtime
operator|.
name|FlatLists
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
name|dialect
operator|.
name|CalciteSqlDialect
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
name|dialect
operator|.
name|HiveSqlDialect
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
name|dialect
operator|.
name|MysqlSqlDialect
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|Programs
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
name|RuleSet
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
name|RuleSets
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
name|collect
operator|.
name|ImmutableList
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
import|import
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
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
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for {@link RelToSqlConverter}.  */
end_comment

begin_class
specifier|public
class|class
name|RelToSqlConverterTest
block|{
specifier|static
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|DEFAULT_REL_CONFIG
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
operator|.
name|withConvertTableAccess
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|static
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|NO_EXPAND_CONFIG
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
operator|.
name|withConvertTableAccess
argument_list|(
literal|false
argument_list|)
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/** Initiates a test case with a given SQL query. */
specifier|private
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|,
name|sql
argument_list|,
name|CalciteSqlDialect
operator|.
name|DEFAULT
argument_list|,
name|DEFAULT_REL_CONFIG
argument_list|,
name|ImmutableList
operator|.
expr|<
name|Function
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
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
name|CalciteAssert
operator|.
name|SchemaSpec
name|schemaSpec
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|sqlToRelConf
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
name|schemaSpec
argument_list|)
argument_list|)
operator|.
name|traitDefs
argument_list|(
name|traitDefs
argument_list|)
operator|.
name|sqlToRelConverterConfig
argument_list|(
name|sqlToRelConf
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
specifier|private
specifier|static
name|MysqlSqlDialect
name|mySqlDialect
parameter_list|(
name|NullCollation
name|nullCollation
parameter_list|)
block|{
return|return
operator|new
name|MysqlSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"`"
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|nullCollation
argument_list|)
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
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"product_class_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"shelf_width\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\"< 10"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE (\"product_id\" = 10 OR \"product_id\"<= 5) "
operator|+
literal|"AND (80>= \"shelf_width\" OR \"shelf_width\"> 30)"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT MIN(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", MIN(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUM(\"net_weight\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUM(\"net_weight\"), MIN(\"low_fat\"),"
operator|+
literal|" COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
literal|"select \"product_class_id\","
operator|+
literal|" sum(\"net_weight\"), min(\"low_fat\"), count(*)"
operator|+
literal|" from \"product\" group by \"product_class_id\" "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\","
operator|+
literal|" SUM(\"net_weight\"), MIN(\"low_fat\"), COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
literal|"select \"product_class_id\", \"product_id\", count(*) "
operator|+
literal|"from \"product\" group by \"product_class_id\", \"product_id\"  "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", \"product_id\","
operator|+
literal|" COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1946">[CALCITE-1946]    * JDBC adapter should generate sub-SELECT if dialect does not support nested    * aggregate functions</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testNestedAggregates
parameter_list|()
block|{
comment|// PostgreSQL, MySQL, Vertica do not support nested aggregate functions, so
comment|// for these, the JDBC adapter generates a SELECT in the FROM clause.
comment|// Oracle can do it in a single SELECT.
specifier|final
name|String
name|query
init|=
literal|"select\n"
operator|+
literal|"    SUM(\"net_weight1\") as \"net_weight_converted\"\n"
operator|+
literal|"  from ("
operator|+
literal|"    select\n"
operator|+
literal|"       SUM(\"net_weight\") as \"net_weight1\"\n"
operator|+
literal|"    from \"foodmart\".\"product\"\n"
operator|+
literal|"    group by \"product_id\")"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT SUM(SUM(\"net_weight\")) \"net_weight_converted\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_id\""
decl_stmt|;
specifier|final
name|String
name|expectedMySQL
init|=
literal|"SELECT SUM(`net_weight1`) AS `net_weight_converted`\n"
operator|+
literal|"FROM (SELECT SUM(`net_weight`) AS `net_weight1`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_id`) AS `t1`"
decl_stmt|;
specifier|final
name|String
name|expectedVertica
init|=
literal|"SELECT SUM(\"net_weight1\") AS \"net_weight_converted\"\n"
operator|+
literal|"FROM (SELECT SUM(\"net_weight\") AS \"net_weight1\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_id\") AS \"t1\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT SUM(\"net_weight1\") AS \"net_weight_converted\"\n"
operator|+
literal|"FROM (SELECT SUM(\"net_weight\") AS \"net_weight1\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_id\") AS \"t1\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySQL
argument_list|)
operator|.
name|withVertica
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedVertica
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\"\n"
operator|+
literal|"HAVING \"product_id\"> 10"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1665">[CALCITE-1665]    * Aggregates and having cannot be combined</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByHaving2
parameter_list|()
block|{
name|String
name|query
init|=
literal|" select \"product\".\"product_id\",\n"
operator|+
literal|"    min(\"sales_fact_1997\".\"store_id\")\n"
operator|+
literal|"    from \"product\"\n"
operator|+
literal|"    inner join \"sales_fact_1997\"\n"
operator|+
literal|"    on \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\"\n"
operator|+
literal|"    group by \"product\".\"product_id\"\n"
operator|+
literal|"    having count(*)> 1"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product\".\"product_id\", "
operator|+
literal|"MIN(\"sales_fact_1997\".\"store_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"sales_fact_1997\" "
operator|+
literal|"ON \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\"\n"
operator|+
literal|"GROUP BY \"product\".\"product_id\"\n"
operator|+
literal|"HAVING COUNT(*)> 1"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1665">[CALCITE-1665]    * Aggregates and having cannot be combined</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByHaving3
parameter_list|()
block|{
name|String
name|query
init|=
literal|" select * from (select \"product\".\"product_id\",\n"
operator|+
literal|"    min(\"sales_fact_1997\".\"store_id\")\n"
operator|+
literal|"    from \"product\"\n"
operator|+
literal|"    inner join \"sales_fact_1997\"\n"
operator|+
literal|"    on \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\"\n"
operator|+
literal|"    group by \"product\".\"product_id\"\n"
operator|+
literal|"    having count(*)> 1) where \"product_id\"> 100"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT \"product\".\"product_id\", MIN(\"sales_fact_1997\".\"store_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"sales_fact_1997\" ON \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\"\n"
operator|+
literal|"GROUP BY \"product\".\"product_id\"\n"
operator|+
literal|"HAVING COUNT(*)> 1) AS \"t2\"\n"
operator|+
literal|"WHERE \"t2\".\"product_id\"> 100"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"net_weight\","
operator|+
literal|" \"gross_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\", \"gross_weight\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
literal|"select \"product_id\" from \"product\" "
operator|+
literal|"order by \"net_weight\" asc, \"gross_weight\" desc, \"low_fat\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" \"product_id\", \"net_weight\", \"gross_weight\", \"low_fat\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\", \"gross_weight\" DESC, \"low_fat\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"LIMIT 100\nOFFSET 10"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id IS NULL DESC, product_id DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|HiveSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id IS NULL, product_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|HiveSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByAscNullsFirstShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|HiveSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByDescNullsLastShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|HiveSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByDescAndHighNullsWithVersionGreaterThanOrEq21
parameter_list|()
block|{
specifier|final
name|HiveSqlDialect
name|hive2_1Dialect
init|=
operator|new
name|HiveSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseMajorVersion
argument_list|(
literal|2
argument_list|)
operator|.
name|withDatabaseMinorVersion
argument_list|(
literal|1
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|HiveSqlDialect
name|hive2_2_Dialect
init|=
operator|new
name|HiveSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseMajorVersion
argument_list|(
literal|2
argument_list|)
operator|.
name|withDatabaseMinorVersion
argument_list|(
literal|2
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id DESC NULLS FIRST"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|hive2_1Dialect
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|hive2_2_Dialect
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHiveSelectQueryWithOrderByDescAndHighNullsWithVersion20
parameter_list|()
block|{
specifier|final
name|HiveSqlDialect
name|hive2_1_0_Dialect
init|=
operator|new
name|HiveSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseMajorVersion
argument_list|(
literal|2
argument_list|)
operator|.
name|withDatabaseMinorVersion
argument_list|(
literal|0
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|LOW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT product_id\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY product_id IS NULL DESC, product_id DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|hive2_1_0_Dialect
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlSelectQueryWithOrderByDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL DESC, `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|MysqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlSelectQueryWithOrderByAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL, `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|MysqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlSelectQueryWithOrderByAscNullsFirstShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|MysqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlSelectQueryWithOrderByDescNullsLastShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|MysqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithHighNullsSelectWithOrderByAscNullsLastAndNoEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithHighNullsSelectWithOrderByAscNullsFirstAndNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL DESC, `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithHighNullsSelectWithOrderByDescNullsFirstAndNoEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithHighNullsSelectWithOrderByDescNullsLastAndNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL, `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithFirstNullsSelectWithOrderByDescAndNullsFirstShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|FIRST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithFirstNullsSelectWithOrderByAscAndNullsFirstShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|FIRST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithFirstNullsSelectWithOrderByDescAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL, `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|FIRST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithFirstNullsSelectWithOrderByAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL, `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|FIRST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithLastNullsSelectWithOrderByDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL DESC, `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|LAST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithLastNullsSelectWithOrderByAscAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls first"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL DESC, `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|LAST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithLastNullsSelectWithOrderByDescAndNullsLastShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" desc nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|LAST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMySqlWithLastNullsSelectWithOrderByAscAndNullsLastShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|LAST
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithLimitClauseWithoutOrder
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" limit 100 offset 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"OFFSET 10 ROWS\n"
operator|+
literal|"FETCH NEXT 100 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithLimitOffsetClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" order by \"net_weight\" asc"
operator|+
literal|" limit 100 offset 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"net_weight\"\n"
operator|+
literal|"OFFSET 10 ROWS\n"
operator|+
literal|"FETCH NEXT 100 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithParameters
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"product\" "
operator|+
literal|"where \"product_id\" = ? "
operator|+
literal|"AND ?>= \"shelf_width\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\" = ?"
operator|+
literal|"AND ?>= \"shelf_width\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithFetchOffsetClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"  from \"product\" order by \"product_id\""
operator|+
literal|" offset 10 rows fetch next 100 rows only"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"product_id\"\n"
operator|+
literal|"OFFSET 10 ROWS\n"
operator|+
literal|"FETCH NEXT 100 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*), \"units_per_case\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"cases_per_pallet\"> 100\n"
operator|+
literal|"GROUP BY \"product_id\", \"units_per_case\"\n"
operator|+
literal|"ORDER BY \"units_per_case\" DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
literal|"select"
operator|+
literal|" count(*), sum(\"employee_id\") from \"reserve_employee\" "
operator|+
literal|"where \"hire_date\"> '2015-01-01' "
operator|+
literal|"and (\"position_title\" = 'SDE' or \"position_title\" = 'SDM') "
operator|+
literal|"group by \"store_id\", \"position_title\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*), SUM(\"employee_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"reserve_employee\"\n"
operator|+
literal|"WHERE \"hire_date\"> '2015-01-01' "
operator|+
literal|"AND (\"position_title\" = 'SDE' OR \"position_title\" = 'SDM')\n"
operator|+
literal|"GROUP BY \"store_id\", \"position_title\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\nFROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"customer\" "
operator|+
literal|"ON \"sales_fact_1997\".\"customer_id\" = \"customer\""
operator|+
literal|".\"customer_id\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"product\" "
operator|+
literal|"ON \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"product_class\" "
operator|+
literal|"ON \"product\".\"product_class_id\" = \"product_class\""
operator|+
literal|".\"product_class_id\"\n"
operator|+
literal|"WHERE \"customer\".\"city\" = 'San Francisco' AND "
operator|+
literal|"\"product_class\".\"product_department\" = 'Snacks'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1636">[CALCITE-1636]    * JDBC adapter generates wrong SQL for self join with sub-query</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryAlias
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select t1.\"customer_id\", t2.\"customer_id\" \n"
operator|+
literal|"from (select \"customer_id\" from \"sales_fact_1997\") as t1 \n"
operator|+
literal|"inner join (select \"customer_id\" from \"sales_fact_1997\") t2 \n"
operator|+
literal|"on t1.\"customer_id\" = t2.\"customer_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT sales_fact_1997.customer_id\n"
operator|+
literal|"FROM foodmart.sales_fact_1997 AS sales_fact_1997) AS t\n"
operator|+
literal|"INNER JOIN (SELECT sales_fact_19970.customer_id\n"
operator|+
literal|"FROM foodmart.sales_fact_1997 AS sales_fact_19970) AS t0 ON t.customer_id = t0.customer_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCartesianProductWithCommaSyntax
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"department\" , \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"department\",\n"
operator|+
literal|"\"foodmart\".\"employee\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCartesianProductWithInnerJoinSyntax
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"department\"\n"
operator|+
literal|"INNER JOIN \"employee\" ON TRUE"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"department\",\n"
operator|+
literal|"\"foodmart\".\"employee\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFullJoinOnTrueCondition
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"department\"\n"
operator|+
literal|"FULL JOIN \"employee\" ON TRUE"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"department\"\n"
operator|+
literal|"FULL JOIN \"foodmart\".\"employee\" ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
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
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"\"department\".\"department_id\", \"department\""
operator|+
literal|".\"department_description\"\n"
operator|+
literal|"FROM \"foodmart\".\"department\"\nINNER JOIN "
operator|+
literal|"(SELECT \"department_id\"\nFROM \"foodmart\".\"employee\"\n"
operator|+
literal|"WHERE \"store_id\"< 150\nGROUP BY \"department_id\") AS \"t1\" "
operator|+
literal|"ON \"department\".\"department_id\" = \"t1\".\"department_id\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1332">[CALCITE-1332]    * DB2 should always use aliases for tables: x.y.z AS z</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectJoinStar
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * "
operator|+
literal|"from \"foodmart\".\"employee\" A "
operator|+
literal|"join \"foodmart\".\"department\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.department AS department "
operator|+
literal|"ON employee.department_id = department.department_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelfJoinStar
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * "
operator|+
literal|"from \"foodmart\".\"employee\" A join \"foodmart\".\"employee\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.employee AS employee0 "
operator|+
literal|"ON employee.department_id = employee0.department_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectJoin
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"employee_id\", B.\"department_id\" "
operator|+
literal|"from \"foodmart\".\"employee\" A join \"foodmart\".\"department\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" employee.employee_id, department.department_id\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.department AS department "
operator|+
literal|"ON employee.department_id = department.department_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelfJoin
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"employee_id\", B.\"employee_id\" from "
operator|+
literal|"\"foodmart\".\"employee\" A join \"foodmart\".\"employee\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" employee.employee_id, employee0.employee_id AS employee_id0\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.employee AS employee0 "
operator|+
literal|"ON employee.department_id = employee0.department_id"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectWhere
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"employee_id\" from "
operator|+
literal|"\"foodmart\".\"employee\" A where A.\"department_id\"< 1000"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT employee.employee_id\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"WHERE employee.department_id< 1000"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectJoinWhere
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"employee_id\", B.\"department_id\" "
operator|+
literal|"from \"foodmart\".\"employee\" A join \"foodmart\".\"department\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\" "
operator|+
literal|"where A.\"employee_id\"< 1000"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" employee.employee_id, department.department_id\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.department AS department "
operator|+
literal|"ON employee.department_id = department.department_id\n"
operator|+
literal|"WHERE employee.employee_id< 1000"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelfJoinWhere
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"employee_id\", B.\"employee_id\" from "
operator|+
literal|"\"foodmart\".\"employee\" A join \"foodmart\".\"employee\" B\n"
operator|+
literal|"on A.\"department_id\" = B.\"department_id\" "
operator|+
literal|"where B.\"employee_id\"< 2000"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"employee.employee_id, employee0.employee_id AS employee_id0\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"INNER JOIN foodmart.employee AS employee0 "
operator|+
literal|"ON employee.department_id = employee0.department_id\n"
operator|+
literal|"WHERE employee0.employee_id< 2000"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectCast
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"hire_date\", cast(\"hire_date\" as varchar(10)) "
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT reserve_employee.hire_date, "
operator|+
literal|"CAST(reserve_employee.hire_date AS VARCHAR(10))\n"
operator|+
literal|"FROM foodmart.reserve_employee AS reserve_employee"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelectQueryWithGroupByHaving
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*) from \"product\" "
operator|+
literal|"group by \"product_class_id\", \"product_id\" "
operator|+
literal|"having \"product_id\"> 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM foodmart.product AS product\n"
operator|+
literal|"GROUP BY product.product_class_id, product.product_id\n"
operator|+
literal|"HAVING product.product_id> 10"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelectQueryComplex
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*), \"units_per_case\" "
operator|+
literal|"from \"product\" where \"cases_per_pallet\"> 100 "
operator|+
literal|"group by \"product_id\", \"units_per_case\" "
operator|+
literal|"order by \"units_per_case\" desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*), product.units_per_case\n"
operator|+
literal|"FROM foodmart.product AS product\n"
operator|+
literal|"WHERE product.cases_per_pallet> 100\n"
operator|+
literal|"GROUP BY product.product_id, product.units_per_case\n"
operator|+
literal|"ORDER BY product.units_per_case DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDb2DialectSelectQueryWithGroup
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*), sum(\"employee_id\") "
operator|+
literal|"from \"reserve_employee\" "
operator|+
literal|"where \"hire_date\"> '2015-01-01' "
operator|+
literal|"and (\"position_title\" = 'SDE' or \"position_title\" = 'SDM') "
operator|+
literal|"group by \"store_id\", \"position_title\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" COUNT(*), SUM(reserve_employee.employee_id)\n"
operator|+
literal|"FROM foodmart.reserve_employee AS reserve_employee\n"
operator|+
literal|"WHERE reserve_employee.hire_date> '2015-01-01' "
operator|+
literal|"AND (reserve_employee.position_title = 'SDE' OR "
operator|+
literal|"reserve_employee.position_title = 'SDM')\n"
operator|+
literal|"GROUP BY reserve_employee.store_id, reserve_employee.position_title"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1372">[CALCITE-1372]    * JDBC adapter generates SQL with wrong field names</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinPlan2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT v1.deptno, v2.deptno\n"
operator|+
literal|"FROM dept v1 LEFT JOIN emp v2 ON v1.deptno = v2.deptno\n"
operator|+
literal|"WHERE v2.job LIKE 'PRESIDENT'"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"DEPT\".\"DEPTNO\","
operator|+
literal|" \"EMP\".\"DEPTNO\" AS \"DEPTNO0\"\n"
operator|+
literal|"FROM \"JDBC_SCOTT\".\"DEPT\"\n"
operator|+
literal|"LEFT JOIN \"JDBC_SCOTT\".\"EMP\""
operator|+
literal|" ON \"DEPT\".\"DEPTNO\" = \"EMP\".\"DEPTNO\"\n"
operator|+
literal|"WHERE \"EMP\".\"JOB\" LIKE 'PRESIDENT'"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT DEPT.DEPTNO, EMP.DEPTNO AS DEPTNO0\n"
operator|+
literal|"FROM JDBC_SCOTT.DEPT AS DEPT\n"
operator|+
literal|"LEFT JOIN JDBC_SCOTT.EMP AS EMP ON DEPT.DEPTNO = EMP.DEPTNO\n"
operator|+
literal|"WHERE EMP.JOB LIKE 'PRESIDENT'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|schema
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_SCOTT
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1422">[CALCITE-1422]    * In JDBC adapter, allow IS NULL and IS NOT NULL operators in generated SQL    * join condition</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSimpleJoinConditionWithIsNullOperators
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as \"t1\"\n"
operator|+
literal|"inner join \"foodmart\".\"customer\" as \"t2\"\n"
operator|+
literal|"on \"t1\".\"customer_id\" = \"t2\".\"customer_id\" or "
operator|+
literal|"(\"t1\".\"customer_id\" is null "
operator|+
literal|"and \"t2\".\"customer_id\" is null) or\n"
operator|+
literal|"\"t2\".\"occupation\" is null\n"
operator|+
literal|"inner join \"foodmart\".\"product\" as \"t3\"\n"
operator|+
literal|"on \"t1\".\"product_id\" = \"t3\".\"product_id\" or "
operator|+
literal|"(\"t1\".\"product_id\" is not null or "
operator|+
literal|"\"t3\".\"product_id\" is not null)"
decl_stmt|;
comment|// Some of the "IS NULL" and "IS NOT NULL" are reduced to TRUE or FALSE,
comment|// but not all.
name|String
name|expected
init|=
literal|"SELECT *\nFROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"customer\" "
operator|+
literal|"ON \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\""
operator|+
literal|" OR FALSE AND FALSE"
operator|+
literal|" OR \"customer\".\"occupation\" IS NULL\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"product\" "
operator|+
literal|"ON \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\""
operator|+
literal|" OR TRUE"
operator|+
literal|" OR TRUE"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1586">[CALCITE-1586]    * JDBC adapter generates wrong SQL if UNION has more than two inputs</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testThreeQueryUnion
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT \"product_id\" FROM \"product\" "
operator|+
literal|" UNION ALL "
operator|+
literal|"SELECT \"product_id\" FROM \"sales_fact_1997\" "
operator|+
literal|" UNION ALL "
operator|+
literal|"SELECT \"product_class_id\" AS product_id FROM \"product_class\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT \"product_class_id\" AS \"PRODUCT_ID\"\n"
operator|+
literal|"FROM \"foodmart\".\"product_class\""
decl_stmt|;
specifier|final
name|HepProgram
name|program
init|=
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleClass
argument_list|(
name|UnionMergeRule
operator|.
name|class
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RuleSet
name|rules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|UnionMergeRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1800">[CALCITE-1800]    * JDBC adapter fails to SELECT FROM a UNION query</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testUnionWrappedInASelect
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select sum(\n"
operator|+
literal|"  case when \"product_id\"=0 then \"net_weight\" else 0 end)"
operator|+
literal|" as net_weight\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select \"product_id\", \"net_weight\"\n"
operator|+
literal|"  from \"product\"\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select \"product_id\", 0 as \"net_weight\"\n"
operator|+
literal|"  from \"sales_fact_1997\") t0"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUM(CASE WHEN \"product_id\" = 0"
operator|+
literal|" THEN \"net_weight\" ELSE 0 END) AS \"NET_WEIGHT\"\n"
operator|+
literal|"FROM (SELECT \"product_id\", \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT \"product_id\", 0 AS \"net_weight\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\") AS \"t1\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLiteral
parameter_list|()
block|{
name|checkLiteral
argument_list|(
literal|"DATE '1978-05-02'"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"DATE '1978-5-2'"
argument_list|,
literal|"DATE '1978-05-02'"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"TIME '12:34:56'"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"TIME '12:34:56.78'"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"TIME '1:4:6.080'"
argument_list|,
literal|"TIME '01:04:06.080'"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"TIMESTAMP '1978-05-02 12:34:56.78'"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"TIMESTAMP '1978-5-2 2:4:6.80'"
argument_list|,
literal|"TIMESTAMP '1978-05-02 02:04:06.80'"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"'I can''t explain'"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"''"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"TRUE"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"123"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"123.45"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"-123.45"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL -'1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12-11' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1' MONTH"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12' DAY"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL -'12' DAY"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|,
literal|"INTERVAL '1 02' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:10' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 02:10' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:00' DAY TO MINUTE"
argument_list|,
literal|"INTERVAL '1 02:00' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:34:56' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 02:34:56' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:34:56.789' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 02:34:56.789' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:34:56.78' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 02:34:56.78' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:34:56.078' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 02:34:56.078' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL -'1 2:34:56.078' DAY TO SECOND"
argument_list|,
literal|"INTERVAL -'1 02:34:56.078' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral2
argument_list|(
literal|"INTERVAL '1 2:3:5.070' DAY TO SECOND"
argument_list|,
literal|"INTERVAL '1 02:03:05.07' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:23' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:02' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL -'1:02' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:23:45' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:03:05' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:23:45.678' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '1:03:05.06' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12' MINUTE"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12:34' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12:34.567' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12' SECOND"
argument_list|)
expr_stmt|;
name|checkLiteral
argument_list|(
literal|"INTERVAL '12.345' SECOND"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkLiteral
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
name|checkLiteral2
argument_list|(
name|expression
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkLiteral2
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|sql
argument_list|(
literal|"VALUES "
operator|+
name|expression
argument_list|)
operator|.
name|withHsqldb
argument_list|()
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (VALUES  ("
operator|+
name|expected
operator|+
literal|")) AS t (EXPR$0)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1798">[CALCITE-1798]    * Generate dialect-specific SQL for FLOOR operator</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFloor
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MINUTE) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT TRUNC(hire_date, 'MI')\nFROM foodmart.employee"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withHsqldb
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorPostgres
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MINUTE) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT DATE_TRUNC('MINUTE', \"hire_date\")\nFROM \"foodmart\".\"employee\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MINUTE) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT TRUNC(\"hire_date\", 'MINUTE')\nFROM \"foodmart\".\"employee\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorMssqlWeek
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO WEEK) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT CONVERT(DATETIME, CONVERT(VARCHAR(10), "
operator|+
literal|"DATEADD(day, - (6 + DATEPART(weekday, [hire_date] )) % 7, [hire_date] ), 126))\n"
operator|+
literal|"FROM [foodmart].[employee]"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorMssqlMonth
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MONTH) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT CONVERT(DATETIME, CONVERT(VARCHAR(7), [hire_date] , 126)+'-01')\n"
operator|+
literal|"FROM [foodmart].[employee]"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorMysqlMonth
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MONTH) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT DATE_FORMAT(`hire_date`, '%Y-%m-01')\n"
operator|+
literal|"FROM `foodmart`.`employee`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorMysqlWeek
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO WEEK) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT STR_TO_DATE(DATE_FORMAT(`hire_date` , '%x%v-1'), '%x%v-%w')\n"
operator|+
literal|"FROM `foodmart`.`employee`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1826">[CALCITE-1826]    * JDBC dialect-specific FLOOR fails when in GROUP BY</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFloorWithGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MINUTE)\n"
operator|+
literal|"FROM \"employee\"\n"
operator|+
literal|"GROUP BY floor(\"hire_date\" TO MINUTE)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT TRUNC(hire_date, 'MI')\n"
operator|+
literal|"FROM foodmart.employee\n"
operator|+
literal|"GROUP BY TRUNC(hire_date, 'MI')"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT TRUNC(\"hire_date\", 'MINUTE')\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"GROUP BY TRUNC(\"hire_date\", 'MINUTE')"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT DATE_TRUNC('MINUTE', \"hire_date\")\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"GROUP BY DATE_TRUNC('MINUTE', \"hire_date\")"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT"
operator|+
literal|" DATE_FORMAT(`hire_date`, '%Y-%m-%d %k:%i:00')\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"GROUP BY DATE_FORMAT(`hire_date`, '%Y-%m-%d %k:%i:00')"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withHsqldb
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubstring
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select substring(\"brand_name\" from 2) "
operator|+
literal|"from \"product\"\n"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT SUBSTR(\"brand_name\", 2)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT SUBSTRING(\"brand_name\" FROM 2)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT SUBSTRING(`brand_name` FROM 2)\n"
operator|+
literal|"FROM `foodmart`.`product`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysql
argument_list|)
operator|.
name|withMssql
argument_list|()
comment|// mssql does not support this syntax and so should fail
operator|.
name|throws_
argument_list|(
literal|"MSSQL SUBSTRING requires FROM and FOR arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubstringWithFor
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select substring(\"brand_name\" from 2 for 3) "
operator|+
literal|"from \"product\"\n"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT SUBSTR(\"brand_name\", 2, 3)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT SUBSTRING(\"brand_name\" FROM 2 FOR 3)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT SUBSTRING(`brand_name` FROM 2 FOR 3)\n"
operator|+
literal|"FROM `foodmart`.`product`"
decl_stmt|;
specifier|final
name|String
name|expectedMssql
init|=
literal|"SELECT SUBSTRING([brand_name], 2, 3)\n"
operator|+
literal|"FROM [foodmart].[product]"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysql
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMssql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1849">[CALCITE-1849]    * Support sub-queries (RexSubQuery) in RelToSqlConverter</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testExistsWithExpand
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where exists (select count(*) "
operator|+
literal|"from \"sales_fact_1997\"b "
operator|+
literal|"where b.\"product_id\" = a.\"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE EXISTS (SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = \"product\".\"product_id\")"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|config
argument_list|(
name|NO_EXPAND_CONFIG
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotExistsWithExpand
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where not exists (select count(*) "
operator|+
literal|"from \"sales_fact_1997\"b "
operator|+
literal|"where b.\"product_id\" = a.\"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE NOT EXISTS (SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = \"product\".\"product_id\")"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|config
argument_list|(
name|NO_EXPAND_CONFIG
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryInWithExpand
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_id\" in (select \"product_id\" "
operator|+
literal|"from \"sales_fact_1997\"b "
operator|+
literal|"where b.\"product_id\" = a.\"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\" IN (SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = \"product\".\"product_id\")"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|config
argument_list|(
name|NO_EXPAND_CONFIG
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryInWithExpand2
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_id\" in (1, 2)"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\" = 1 OR \"product_id\" = 2"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|config
argument_list|(
name|NO_EXPAND_CONFIG
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryNotInWithExpand
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_id\" not in (select \"product_id\" "
operator|+
literal|"from \"sales_fact_1997\"b "
operator|+
literal|"where b.\"product_id\" = a.\"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\" NOT IN (SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"WHERE \"product_id\" = \"product\".\"product_id\")"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|config
argument_list|(
name|NO_EXPAND_CONFIG
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" like 'abc'"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" LIKE 'abc'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotLike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" not like 'abc'"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" NOT LIKE 'abc'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    partition by \"product_class_id\", \"brand_name\" \n"
operator|+
literal|"    order by \"product_class_id\" asc, \"brand_name\" desc \n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"PARTITION BY \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"ORDER BY \"product_class_id\", \"brand_name\" DESC\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+$)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" + $)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (^strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (^ \"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (^strt down+ up+$)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (^ \"STRT\" \"DOWN\" + \"UP\" + $)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down* up?)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" * \"UP\" ?)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt {-down-} up?)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" {- \"DOWN\" -} \"UP\" ?)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression7
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down{2} up{3,})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" { 2 } \"UP\" { 3, })\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression8
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down{,2} up{3,5})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" { , 2 } \"UP\" { 3, 5 })\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression9
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt {-down+-} {-up*-})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" {- \"DOWN\" + -} {- \"UP\" * -})\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression10
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (A B C | A C B | B A C | B C A | C A B | C B A)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      A as A.\"net_weight\"< PREV(A.\"net_weight\"),\n"
operator|+
literal|"      B as B.\"net_weight\"> PREV(B.\"net_weight\"),\n"
operator|+
literal|"      C as C.\"net_weight\"< PREV(C.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN "
operator|+
literal|"(\"A\" \"B\" \"C\" | \"A\" \"C\" \"B\" | \"B\" \"A\" \"C\" "
operator|+
literal|"| \"B\" \"C\" \"A\" | \"C\" \"A\" \"B\" | \"C\" \"B\" \"A\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"A\" AS PREV(\"A\".\"net_weight\", 0)< PREV(\"A\".\"net_weight\", 1), "
operator|+
literal|"\"B\" AS PREV(\"B\".\"net_weight\", 0)> PREV(\"B\".\"net_weight\", 1), "
operator|+
literal|"\"C\" AS PREV(\"C\".\"net_weight\", 0)< PREV(\"C\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression11
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from (select * from \"product\") match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression12
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr order by MR.\"net_weight\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))\n"
operator|+
literal|"ORDER BY \"net_weight\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternExpression13
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from (\n"
operator|+
literal|"select *\n"
operator|+
literal|"from \"sales_fact_1997\" as s\n"
operator|+
literal|"join \"customer\" as c using (\"customer_id\")\n"
operator|+
literal|"join \"product\" as p using (\"product_id\")\n"
operator|+
literal|"join \"product_class\" as pc using (\"product_class_id\")\n"
operator|+
literal|"where c.\"city\" = 'San Francisco'\n"
operator|+
literal|"and pc.\"product_department\" = 'Snacks'"
operator|+
literal|") match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr order by MR.\"net_weight\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
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
literal|"WHERE \"customer\".\"city\" = 'San Francisco' "
operator|+
literal|"AND \"product_class\".\"product_department\" = 'Snacks') "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))\n"
operator|+
literal|"ORDER BY \"net_weight\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefineClause
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefineClause2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< FIRST(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> LAST(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"FIRST(\"DOWN\".\"net_weight\", 0), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"LAST(\"UP\".\"net_weight\", 0))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefineClause3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\",1),\n"
operator|+
literal|"      up as up.\"net_weight\"> LAST(up.\"net_weight\" + up.\"gross_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"LAST(\"UP\".\"net_weight\", 0) + LAST(\"UP\".\"gross_weight\", 0))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeDefineClause4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\",1),\n"
operator|+
literal|"      up as up.\"net_weight\"> "
operator|+
literal|"PREV(LAST(up.\"net_weight\" + up.\"gross_weight\"),3)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(LAST(\"UP\".\"net_weight\", 0) + "
operator|+
literal|"LAST(\"UP\".\"gross_weight\", 0), 3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures MATCH_NUMBER() as match_num, "
operator|+
literal|"   CLASSIFIER() as var_match, "
operator|+
literal|"   STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   LAST(up.\"net_weight\") as end_nw"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL MATCH_NUMBER () AS \"MATCH_NUM\", "
operator|+
literal|"FINAL CLASSIFIER() AS \"VAR_MATCH\", "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL LAST(\"UP\".\"net_weight\", 0) AS \"END_NW\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   FINAL LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   LAST(up.\"net_weight\") as end_nw"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL LAST(\"UP\".\"net_weight\", 0) AS \"END_NW\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   RUNNING LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   LAST(up.\"net_weight\") as end_nw"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL (RUNNING LAST(\"DOWN\".\"net_weight\", 0)) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL LAST(\"UP\".\"net_weight\", 0) AS \"END_NW\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   FINAL COUNT(up.\"net_weight\") as up_cnt,"
operator|+
literal|"   FINAL COUNT(\"net_weight\") as down_cnt,"
operator|+
literal|"   RUNNING COUNT(\"net_weight\") as running_cnt"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL COUNT(\"UP\".\"net_weight\") AS \"UP_CNT\", "
operator|+
literal|"FINAL COUNT(\"*\".\"net_weight\") AS \"DOWN_CNT\", "
operator|+
literal|"FINAL (RUNNING COUNT(\"*\".\"net_weight\")) AS \"RUNNING_CNT\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"   FIRST(STRT.\"net_weight\") as start_nw,"
operator|+
literal|"   LAST(UP.\"net_weight\") as up_cnt,"
operator|+
literal|"   AVG(DOWN.\"net_weight\") as down_cnt"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL FIRST(\"STRT\".\"net_weight\", 0) AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"UP\".\"net_weight\", 0) AS \"UP_CNT\", "
operator|+
literal|"FINAL (SUM(\"DOWN\".\"net_weight\") / "
operator|+
literal|"COUNT(\"DOWN\".\"net_weight\")) AS \"DOWN_CNT\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"   FIRST(STRT.\"net_weight\") as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as up_cnt,"
operator|+
literal|"   FINAL SUM(DOWN.\"net_weight\") as down_cnt"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL FIRST(\"STRT\".\"net_weight\", 0) AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"UP_CNT\", "
operator|+
literal|"FINAL SUM(\"DOWN\".\"net_weight\") AS \"DOWN_CNT\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN "
operator|+
literal|"(\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeMeasures7
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"   FIRST(STRT.\"net_weight\") as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as up_cnt,"
operator|+
literal|"   FINAL SUM(DOWN.\"net_weight\") as down_cnt"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr order by start_nw, up_cnt"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL FIRST(\"STRT\".\"net_weight\", 0) AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"UP_CNT\", "
operator|+
literal|"FINAL SUM(\"DOWN\".\"net_weight\") AS \"DOWN_CNT\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN "
operator|+
literal|"(\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))\n"
operator|+
literal|"ORDER BY \"START_NW\", \"UP_CNT\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternSkip1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to next row\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternSkip2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip past last row\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP PAST LAST ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternSkip3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to FIRST down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO FIRST \"DOWN\"\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE \"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternSkip4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to last down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST \"DOWN\"\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizePatternSkip5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST \"DOWN\"\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSubset1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    after match skip to down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> NEXT(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") MATCH_RECOGNIZE(\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST \"DOWN\"\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"NEXT(PREV(\"UP\".\"net_weight\", 0), 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSubset2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   AVG(STDN.\"net_weight\") as avg_stdn"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL (SUM(\"STDN\".\"net_weight\") / "
operator|+
literal|"COUNT(\"STDN\".\"net_weight\")) AS \"AVG_STDN\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSubset3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   SUM(STDN.\"net_weight\") as avg_stdn"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL SUM(\"STDN\".\"net_weight\") AS \"AVG_STDN\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeSubset4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   SUM(STDN.\"net_weight\") as avg_stdn"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL SUM(\"STDN\".\"net_weight\") AS \"AVG_STDN\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\"), \"STDN2\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeRowsPerMatch1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   SUM(STDN.\"net_weight\") as avg_stdn"
operator|+
literal|"    ONE ROW PER MATCH\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"FINAL \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"FINAL LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"FINAL SUM(\"STDN\".\"net_weight\") AS \"AVG_STDN\"\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\"), \"STDN2\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeRowsPerMatch2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"product\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.\"net_weight\" as start_nw,"
operator|+
literal|"   LAST(DOWN.\"net_weight\") as bottom_nw,"
operator|+
literal|"   SUM(STDN.\"net_weight\") as avg_stdn"
operator|+
literal|"    ALL ROWS PER MATCH\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\"< PREV(down.\"net_weight\"),\n"
operator|+
literal|"      up as up.\"net_weight\"> prev(up.\"net_weight\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES "
operator|+
literal|"RUNNING \"STRT\".\"net_weight\" AS \"START_NW\", "
operator|+
literal|"RUNNING LAST(\"DOWN\".\"net_weight\", 0) AS \"BOTTOM_NW\", "
operator|+
literal|"RUNNING SUM(\"STDN\".\"net_weight\") AS \"AVG_STDN\"\n"
operator|+
literal|"ALL ROWS PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +)\n"
operator|+
literal|"SUBSET \"STDN\" = (\"DOWN\", \"STRT\"), \"STDN2\" = (\"DOWN\", \"STRT\")\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"net_weight\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"net_weight\", 0)> "
operator|+
literal|"PREV(\"UP\".\"net_weight\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchRecognizeWithin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from \"employee\" match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   order by \"hire_date\"\n"
operator|+
literal|"   ALL ROWS PER MATCH\n"
operator|+
literal|"   pattern (strt down+ up+) within interval '3:12:22.123' hour to second\n"
operator|+
literal|"   define\n"
operator|+
literal|"     down as down.\"salary\"< PREV(down.\"salary\"),\n"
operator|+
literal|"     up as up.\"salary\"> prev(up.\"salary\")\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"employee\") "
operator|+
literal|"MATCH_RECOGNIZE(\n"
operator|+
literal|"ORDER BY \"hire_date\"\n"
operator|+
literal|"ALL ROWS PER MATCH\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (\"STRT\" \"DOWN\" + \"UP\" +) WITHIN INTERVAL '3:12:22.123' HOUR TO SECOND\n"
operator|+
literal|"DEFINE "
operator|+
literal|"\"DOWN\" AS PREV(\"DOWN\".\"salary\", 0)< "
operator|+
literal|"PREV(\"DOWN\".\"salary\", 1), "
operator|+
literal|"\"UP\" AS PREV(\"UP\".\"salary\", 0)> "
operator|+
literal|"PREV(\"UP\".\"salary\", 1))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"a\"\n"
operator|+
literal|"from (values (1, 'x'), (2, 'yy')) as t(\"a\", \"b\")"
decl_stmt|;
specifier|final
name|String
name|expectedHsqldb
init|=
literal|"SELECT a\n"
operator|+
literal|"FROM (VALUES  (1, 'x '),\n"
operator|+
literal|" (2, 'yy')) AS t (a, b)"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT \"a\"\n"
operator|+
literal|"FROM (VALUES  (1, 'x '),\n"
operator|+
literal|" (2, 'yy')) AS \"t\" (\"a\", \"b\")"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT \"a\"\n"
operator|+
literal|"FROM (SELECT 1 \"a\", 'x ' \"b\"\n"
operator|+
literal|"FROM \"DUAL\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2 \"a\", 'yy' \"b\"\n"
operator|+
literal|"FROM \"DUAL\")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withHsqldb
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedHsqldb
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
expr_stmt|;
block|}
comment|/** Fluid interface to run tests. */
specifier|private
specifier|static
class|class
name|Sql
block|{
specifier|private
name|CalciteAssert
operator|.
name|SchemaSpec
name|schemaSpec
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Function
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
argument_list|>
name|transforms
decl_stmt|;
specifier|private
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
decl_stmt|;
name|Sql
parameter_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
name|schemaSpec
parameter_list|,
name|String
name|sql
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|SqlToRelConverter
operator|.
name|Config
name|config
parameter_list|,
name|List
argument_list|<
name|Function
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
argument_list|>
name|transforms
parameter_list|)
block|{
name|this
operator|.
name|schemaSpec
operator|=
name|schemaSpec
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|transforms
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|transforms
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
name|Sql
name|dialect
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|schemaSpec
argument_list|,
name|sql
argument_list|,
name|dialect
argument_list|,
name|config
argument_list|,
name|transforms
argument_list|)
return|;
block|}
name|Sql
name|withDb2
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|DB2
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withHive
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|HIVE
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withHsqldb
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|HSQLDB
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withMssql
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MSSQL
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withMysql
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withOracle
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|ORACLE
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withPostgresql
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withVertica
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|VERTICA
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|config
parameter_list|(
name|SqlToRelConverter
operator|.
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|schemaSpec
argument_list|,
name|sql
argument_list|,
name|dialect
argument_list|,
name|config
argument_list|,
name|transforms
argument_list|)
return|;
block|}
name|Sql
name|optimize
parameter_list|(
specifier|final
name|RuleSet
name|ruleSet
parameter_list|,
specifier|final
name|RelOptPlanner
name|relOptPlanner
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|schemaSpec
argument_list|,
name|sql
argument_list|,
name|dialect
argument_list|,
name|config
argument_list|,
name|FlatLists
operator|.
name|append
argument_list|(
name|transforms
argument_list|,
operator|new
name|Function
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RelNode
name|apply
parameter_list|(
name|RelNode
name|r
parameter_list|)
block|{
name|Program
name|program
init|=
name|Programs
operator|.
name|of
argument_list|(
name|ruleSet
argument_list|)
decl_stmt|;
return|return
name|program
operator|.
name|run
argument_list|(
name|relOptPlanner
argument_list|,
name|r
argument_list|,
name|r
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptMaterialization
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptLattice
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|ok
parameter_list|(
name|String
name|expectedQuery
parameter_list|)
block|{
name|assertThat
argument_list|(
name|exec
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Sql
name|throws_
parameter_list|(
name|String
name|errorMessage
parameter_list|)
block|{
try|try
block|{
specifier|final
name|String
name|s
init|=
name|exec
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionFailedError
argument_list|(
literal|"Expected exception with message `"
operator|+
name|errorMessage
operator|+
literal|"` but nothing was thrown; got "
operator|+
name|s
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|errorMessage
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
name|String
name|exec
parameter_list|()
block|{
specifier|final
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|schemaSpec
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
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
for|for
control|(
name|Function
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|transform
range|:
name|transforms
control|)
block|{
name|rel
operator|=
name|transform
operator|.
name|apply
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelToSqlConverter
name|converter
init|=
operator|new
name|RelToSqlConverter
argument_list|(
name|dialect
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
name|asStatement
argument_list|()
decl_stmt|;
return|return
name|Util
operator|.
name|toLinux
argument_list|(
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
operator|.
name|getSql
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
specifier|public
name|Sql
name|schema
parameter_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
name|schemaSpec
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|schemaSpec
argument_list|,
name|sql
argument_list|,
name|dialect
argument_list|,
name|config
argument_list|,
name|transforms
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelToSqlConverterTest.java
end_comment

end_unit

