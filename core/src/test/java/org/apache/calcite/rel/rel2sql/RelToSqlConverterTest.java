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
name|core
operator|.
name|JoinRelType
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|RelDataTypeSystem
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
name|type
operator|.
name|RelDataTypeSystemImpl
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
name|rex
operator|.
name|RexNode
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
name|SqlCall
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
name|SqlDialect
operator|.
name|Context
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
operator|.
name|DatabaseProduct
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
name|SqlSelect
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
name|SqlWriter
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
name|JethroDataSqlDialect
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
name|dialect
operator|.
name|OracleSqlDialect
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
name|PostgresqlSqlDialect
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeFactoryImpl
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
name|type
operator|.
name|SqlTypeName
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
name|test
operator|.
name|RelBuilderTest
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
name|RelBuilder
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
name|TestUtil
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|isLinux
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
name|notNullValue
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
name|assertFalse
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
name|SchemaPlus
name|schema
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
name|schema
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
name|JethroDataSqlDialect
name|jethroDataSqlDialect
parameter_list|()
block|{
name|Context
name|dummyContext
init|=
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
name|JETHRO
argument_list|)
operator|.
name|withDatabaseMajorVersion
argument_list|(
literal|1
argument_list|)
operator|.
name|withDatabaseMinorVersion
argument_list|(
literal|0
argument_list|)
operator|.
name|withDatabaseVersion
argument_list|(
literal|"1.0"
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
operator|.
name|withJethroInfo
argument_list|(
name|JethroDataSqlDialect
operator|.
name|JethroInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
return|return
operator|new
name|JethroDataSqlDialect
argument_list|(
name|dummyContext
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
comment|/** Creates a RelBuilder. */
specifier|private
specifier|static
name|RelBuilder
name|relBuilder
parameter_list|()
block|{
return|return
name|RelBuilder
operator|.
name|create
argument_list|(
name|RelBuilderTest
operator|.
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a relational expression to SQL. */
specifier|private
name|String
name|toSql
parameter_list|(
name|RelNode
name|root
parameter_list|)
block|{
return|return
name|toSql
argument_list|(
name|root
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a relational expression to SQL in a given dialect. */
specifier|private
specifier|static
name|String
name|toSql
parameter_list|(
name|RelNode
name|root
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|)
block|{
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
name|root
argument_list|)
operator|.
name|asStatement
argument_list|()
decl_stmt|;
return|return
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
operator|.
name|getSql
argument_list|()
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
name|testSelectQueryWithGroupByEmpty
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select count(*) from \"product\" group by ()"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select count(*) from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM `foodmart`.`product`"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByEmpty2
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select 42 as c from \"product\" group by ()"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT 42 AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ()"
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT 42 AS `C`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY ()"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3097">[CALCITE-3097]    * GROUPING SETS breaks on sets of size&gt; 1 due to precedence issues</a>,    * in particular, that we maintain proper precedence around nested lists. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByGroupingSets
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by GROUPING SETS ((\"product_class_id\", \"brand_name\"),"
operator|+
literal|" (\"product_class_id\"))\n"
operator|+
literal|"order by 2, 1"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY GROUPING SETS((\"product_class_id\", \"brand_name\"),"
operator|+
literal|" \"product_class_id\")\n"
operator|+
literal|"ORDER BY \"brand_name\", \"product_class_id\""
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
comment|/** Tests GROUP BY ROLLUP of two columns. The SQL for MySQL has    * "GROUP BY ... ROLLUP" but no "ORDER BY". */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByRollup
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by rollup(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"order by 1, 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"ORDER BY \"product_class_id\", \"brand_name\""
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, `brand_name`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id`, `brand_name` WITH ROLLUP"
decl_stmt|;
specifier|final
name|String
name|expectedMySql8
init|=
literal|"SELECT `product_class_id`, `brand_name`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY ROLLUP(`product_class_id`, `brand_name`)\n"
operator|+
literal|"ORDER BY `product_class_id` NULLS LAST, `brand_name` NULLS LAST"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
operator|.
name|withMysql8
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql8
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testSelectQueryWithGroupByRollup()},    * but ORDER BY columns reversed. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithGroupByRollup2
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by rollup(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"order by 2, 1"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"ORDER BY \"brand_name\", \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, `brand_name`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `brand_name`, `product_class_id` WITH ROLLUP"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
comment|/** CUBE of one column is equivalent to ROLLUP, and Calcite recognizes    * this. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithSingletonCube
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", count(*) as c\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by cube(\"product_class_id\")\n"
operator|+
literal|"order by 1, 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")\n"
operator|+
literal|"ORDER BY \"product_class_id\", COUNT(*)"
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, COUNT(*) AS `C`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id` WITH ROLLUP\n"
operator|+
literal|"ORDER BY `product_class_id` IS NULL, `product_class_id`,"
operator|+
literal|" COUNT(*) IS NULL, COUNT(*)"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testSelectQueryWithSingletonCube()}, but no ORDER BY    * clause. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithSingletonCubeNoOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", count(*) as c\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by cube(\"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, COUNT(*) AS `C`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id` WITH ROLLUP"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
comment|/** Cannot rewrite if ORDER BY contains a column not in GROUP BY (in this    * case COUNT(*)). */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithRollupOrderByCount
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", \"brand_name\",\n"
operator|+
literal|" count(*) as c\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by rollup(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"order by 1, 2, 3"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", \"brand_name\","
operator|+
literal|" COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\", \"brand_name\")\n"
operator|+
literal|"ORDER BY \"product_class_id\", \"brand_name\", COUNT(*)"
decl_stmt|;
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, `brand_name`,"
operator|+
literal|" COUNT(*) AS `C`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id`, `brand_name` WITH ROLLUP\n"
operator|+
literal|"ORDER BY `product_class_id` IS NULL, `product_class_id`,"
operator|+
literal|" `brand_name` IS NULL, `brand_name`,"
operator|+
literal|" COUNT(*) IS NULL, COUNT(*)"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testSelectQueryWithSingletonCube()}, but with LIMIT. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectQueryWithCubeLimit
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", count(*) as c\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by cube(\"product_class_id\")\n"
operator|+
literal|"limit 5"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")\n"
operator|+
literal|"FETCH NEXT 5 ROWS ONLY"
decl_stmt|;
comment|// If a MySQL 5 query has GROUP BY ... ROLLUP, you cannot add ORDER BY,
comment|// but you can add LIMIT.
specifier|final
name|String
name|expectedMySql
init|=
literal|"SELECT `product_class_id`, COUNT(*) AS `C`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id` WITH ROLLUP\n"
operator|+
literal|"LIMIT 5"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMySql
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
annotation|@
name|Test
specifier|public
name|void
name|testCastDecimal1
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select -0.0000000123\n"
operator|+
literal|" from \"expense_fact\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT -1.23E-8\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2713">[CALCITE-2713]    * JDBC adapter may generate casts on PostgreSQL for VARCHAR type exceeding    * max length</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCastLongVarchar1
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select cast(\"store_id\" as VARCHAR(10485761))\n"
operator|+
literal|" from \"expense_fact\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgreSQL
init|=
literal|"SELECT CAST(\"store_id\" AS VARCHAR(256))\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withPostgresqlModifiedTypeSystem
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgreSQL
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT CAST(\"store_id\" AS VARCHAR(512))\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracleModifiedTypeSystem
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2713">[CALCITE-2713]    * JDBC adapter may generate casts on PostgreSQL for VARCHAR type exceeding    * max length</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCastLongVarchar2
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select cast(\"store_id\" as VARCHAR(175))\n"
operator|+
literal|" from \"expense_fact\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgreSQL
init|=
literal|"SELECT CAST(\"store_id\" AS VARCHAR(175))\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withPostgresqlModifiedTypeSystem
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgreSQL
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT CAST(\"store_id\" AS VARCHAR(175))\n"
operator|+
literal|"FROM \"foodmart\".\"expense_fact\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withOracleModifiedTypeSystem
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1174">[CALCITE-1174]    * When generating SQL, translate SUM0(x) to COALESCE(SUM(x), 0)</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSum0BecomesCoalesce
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUM0
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
literal|"s"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT COALESCE(SUM(`MGR`), 0) AS `s`\n"
operator|+
literal|"FROM `scott`.`EMP`"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedMysql
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT COALESCE(SUM(\"MGR\"), 0) AS \"s\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\""
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
operator|.
name|getDialect
argument_list|()
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedPostgresql
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testSum0BecomesCoalesce()} but for windowed aggregates. */
annotation|@
name|Test
specifier|public
name|void
name|testWindowedSum0BecomesCoalesce
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select\n"
operator|+
literal|"  AVG(\"net_weight\") OVER (order by \"product_id\" rows 3 preceding)\n"
operator|+
literal|"from \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT CASE WHEN (COUNT(\"net_weight\")"
operator|+
literal|" OVER (ORDER BY \"product_id\" ROWS BETWEEN 3 PRECEDING AND CURRENT ROW))> 0 "
operator|+
literal|"THEN COALESCE(SUM(\"net_weight\")"
operator|+
literal|" OVER (ORDER BY \"product_id\" ROWS BETWEEN 3 PRECEDING AND CURRENT ROW), 0)"
operator|+
literal|" ELSE NULL END / (COUNT(\"net_weight\")"
operator|+
literal|" OVER (ORDER BY \"product_id\" ROWS BETWEEN 3 PRECEDING AND CURRENT ROW))\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
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
name|expectedPostgresql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2722">[CALCITE-2722]    * SqlImplementor createLeftCall method throws StackOverflowError</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testStack
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|or
argument_list|(
name|IntStream
operator|.
name|range
argument_list|(
literal|1
argument_list|,
literal|10000
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
specifier|final
name|String
name|expectedVertica
init|=
name|expectedPostgresql
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2628">[CALCITE-2628]    * JDBC adapter throws NullPointerException while generating GROUP BY query    * for MySQL</a>.    *    *<p>MySQL does not support nested aggregates, so {@link RelToSqlConverter}    * performs some extra checks, looking for aggregates in the input    * sub-query, and these would fail with {@code NullPointerException}    * and {@code ClassCastException} in some cases. */
annotation|@
name|Test
specifier|public
name|void
name|testNestedAggregatesMySqlTable
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT COUNT(`MGR`) AS `c`\n"
operator|+
literal|"FROM `scott`.`EMP`"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|,
name|dialect
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testNestedAggregatesMySqlTable()}, but input is a sub-query,    * not a table. */
annotation|@
name|Test
specifier|public
name|void
name|testNestedAggregatesMySqlStar
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT COUNT(`MGR`) AS `c`\n"
operator|+
literal|"FROM `scott`.`EMP`\n"
operator|+
literal|"WHERE `DEPTNO` = 10"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|,
name|dialect
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3207">[CALCITE-3207]    * Fail to convert Join RelNode with like condition to sql statement</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testJoinWithLikeConditionRel2Sql
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"DEPT"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|LEFT
argument_list|,
name|builder
operator|.
name|and
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|"DEPTNO"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|"DNAME"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"ACCOUNTING"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|rel
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"LEFT JOIN \"scott\".\"DEPT\" "
operator|+
literal|"ON \"EMP\".\"DEPTNO\" = \"DEPT\".\"DEPTNO\" "
operator|+
literal|"AND \"DEPT\".\"DNAME\" LIKE 'ACCOUNTING'"
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
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
name|testHiveSelectCharset
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
literal|"SELECT hire_date, CAST(hire_date AS VARCHAR(10))\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2715">[CALCITE-2715]    * MS SQL Server does not support character set as part of data type</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testMssqlCharacterSet
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"hire_date\", cast(\"hire_date\" as varchar(10))\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT [hire_date], CAST([hire_date] AS VARCHAR(10))\n"
operator|+
literal|"FROM [foodmart].[reserve_employee]"
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
comment|/**    * Tests that IN can be un-parsed.    *    *<p>This cannot be tested using "sql", because because Calcite's SQL parser    * replaces INs with ORs or sub-queries.    */
annotation|@
name|Test
specifier|public
name|void
name|testUnparseIn1
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|condition
init|=
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|21
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|condition
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPTNO\" IN (21)"
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnparseIn2
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|20
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|21
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|rel
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPTNO\" IN (20, 21)"
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnparseInStruct1
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|condition
init|=
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"JOB"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|condition
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE ROW(\"DEPTNO\", \"JOB\") IN (ROW(1, 'PRESIDENT'))"
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnparseInStruct2
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|condition
init|=
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"JOB"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|relBuilder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|condition
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
name|toSql
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE ROW(\"DEPTNO\", \"JOB\") IN (ROW(1, 'PRESIDENT'), ROW(2, 'PRESIDENT'))"
decl_stmt|;
name|assertThat
argument_list|(
name|sql
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
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
name|testPositionFunctionForHive
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select position('A' IN 'ABC') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT INSTR('ABC', 'A')\n"
operator|+
literal|"FROM foodmart.product"
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
name|testPositionFunctionForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select position('A' IN 'ABC') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT STRPOS('ABC', 'A')\n"
operator|+
literal|"FROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withBigQuery
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
name|testModFunctionForHive
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mod(11,3) from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT 11 % 3\n"
operator|+
literal|"FROM foodmart.product"
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
name|testUnionOperatorForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mod(11,3) from \"product\"\n"
operator|+
literal|"UNION select 1 from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT MOD(11, 3)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"UNION DISTINCT\nSELECT 1\nFROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withBigQuery
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
name|testIntersectOperatorForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mod(11,3) from \"product\"\n"
operator|+
literal|"INTERSECT select 1 from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT MOD(11, 3)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"INTERSECT DISTINCT\nSELECT 1\nFROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withBigQuery
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
name|testExceptOperatorForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mod(11,3) from \"product\"\n"
operator|+
literal|"EXCEPT select 1 from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT MOD(11, 3)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"EXCEPT DISTINCT\nSELECT 1\nFROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withBigQuery
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
name|testMysqlCastToBigint
parameter_list|()
block|{
comment|// MySQL does not allow cast to BIGINT; instead cast to SIGNED.
specifier|final
name|String
name|query
init|=
literal|"select cast(\"product_id\" as bigint) from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(`product_id` AS SIGNED)\n"
operator|+
literal|"FROM `foodmart`.`product`"
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
name|testMysqlCastToInteger
parameter_list|()
block|{
comment|// MySQL does not allow cast to INTEGER; instead cast to SIGNED.
specifier|final
name|String
name|query
init|=
literal|"select \"employee_id\",\n"
operator|+
literal|"  cast(\"salary_paid\" * 10000 as integer)\n"
operator|+
literal|"from \"salary\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `employee_id`,"
operator|+
literal|" CAST(`salary_paid` * 10000 AS SIGNED)\n"
operator|+
literal|"FROM `foodmart`.`salary`"
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
name|testJethroDataSelectQueryWithOrderByDescAndNullsFirstShouldBeEmulated
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
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY \"product_id\", \"product_id\" DESC"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|jethroDataSqlDialect
argument_list|()
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
literal|"WHERE \"product_id\" = ? "
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
literal|"join \"customer\" as c on s.\"customer_id\" = c.\"customer_id\"\n"
operator|+
literal|"join \"product\" as p on s.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"join \"product_class\" as pc\n"
operator|+
literal|"  on p.\"product_class_id\" = pc.\"product_class_id\"\n"
operator|+
literal|"where c.\"city\" = 'San Francisco'\n"
operator|+
literal|"and pc.\"product_department\" = 'Snacks'\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
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
annotation|@
name|Test
specifier|public
name|void
name|testSimpleJoinUsing
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
literal|"SELECT"
operator|+
literal|" \"product\".\"product_class_id\","
operator|+
literal|" \"sales_fact_1997\".\"product_id\","
operator|+
literal|" \"sales_fact_1997\".\"customer_id\","
operator|+
literal|" \"sales_fact_1997\".\"time_id\","
operator|+
literal|" \"sales_fact_1997\".\"promotion_id\","
operator|+
literal|" \"sales_fact_1997\".\"store_id\","
operator|+
literal|" \"sales_fact_1997\".\"store_sales\","
operator|+
literal|" \"sales_fact_1997\".\"store_cost\","
operator|+
literal|" \"sales_fact_1997\".\"unit_sales\","
operator|+
literal|" \"customer\".\"account_num\","
operator|+
literal|" \"customer\".\"lname\","
operator|+
literal|" \"customer\".\"fname\","
operator|+
literal|" \"customer\".\"mi\","
operator|+
literal|" \"customer\".\"address1\","
operator|+
literal|" \"customer\".\"address2\","
operator|+
literal|" \"customer\".\"address3\","
operator|+
literal|" \"customer\".\"address4\","
operator|+
literal|" \"customer\".\"city\","
operator|+
literal|" \"customer\".\"state_province\","
operator|+
literal|" \"customer\".\"postal_code\","
operator|+
literal|" \"customer\".\"country\","
operator|+
literal|" \"customer\".\"customer_region_id\","
operator|+
literal|" \"customer\".\"phone1\","
operator|+
literal|" \"customer\".\"phone2\","
operator|+
literal|" \"customer\".\"birthdate\","
operator|+
literal|" \"customer\".\"marital_status\","
operator|+
literal|" \"customer\".\"yearly_income\","
operator|+
literal|" \"customer\".\"gender\","
operator|+
literal|" \"customer\".\"total_children\","
operator|+
literal|" \"customer\".\"num_children_at_home\","
operator|+
literal|" \"customer\".\"education\","
operator|+
literal|" \"customer\".\"date_accnt_opened\","
operator|+
literal|" \"customer\".\"member_card\","
operator|+
literal|" \"customer\".\"occupation\","
operator|+
literal|" \"customer\".\"houseowner\","
operator|+
literal|" \"customer\".\"num_cars_owned\","
operator|+
literal|" \"customer\".\"fullname\","
operator|+
literal|" \"product\".\"brand_name\","
operator|+
literal|" \"product\".\"product_name\","
operator|+
literal|" \"product\".\"SKU\","
operator|+
literal|" \"product\".\"SRP\","
operator|+
literal|" \"product\".\"gross_weight\","
operator|+
literal|" \"product\".\"net_weight\","
operator|+
literal|" \"product\".\"recyclable_package\","
operator|+
literal|" \"product\".\"low_fat\","
operator|+
literal|" \"product\".\"units_per_case\","
operator|+
literal|" \"product\".\"cases_per_pallet\","
operator|+
literal|" \"product\".\"shelf_width\","
operator|+
literal|" \"product\".\"shelf_height\","
operator|+
literal|" \"product\".\"shelf_depth\","
operator|+
literal|" \"product_class\".\"product_subcategory\","
operator|+
literal|" \"product_class\".\"product_category\","
operator|+
literal|" \"product_class\".\"product_department\","
operator|+
literal|" \"product_class\".\"product_family\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2652">[CALCITE-2652]    * SqlNode to SQL conversion fails if the join condition references a BOOLEAN    * column</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnBoolean
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT 1\n"
operator|+
literal|"from emps\n"
operator|+
literal|"join emp on (emp.deptno = emps.empno and manager)"
decl_stmt|;
specifier|final
name|String
name|s
init|=
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
name|POST
argument_list|)
operator|.
name|exec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// sufficient that conversion did not throw
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
literal|"FROM \"SCOTT\".\"DEPT\"\n"
operator|+
literal|"LEFT JOIN \"SCOTT\".\"EMP\""
operator|+
literal|" ON \"DEPT\".\"DEPTNO\" = \"EMP\".\"DEPTNO\"\n"
operator|+
literal|"WHERE \"EMP\".\"JOB\" LIKE 'PRESIDENT'"
decl_stmt|;
comment|// DB2 does not have implicit aliases, so generates explicit "AS DEPT"
comment|// and "AS EMP"
specifier|final
name|String
name|expectedDb2
init|=
literal|"SELECT DEPT.DEPTNO, EMP.DEPTNO AS DEPTNO0\n"
operator|+
literal|"FROM SCOTT.DEPT AS DEPT\n"
operator|+
literal|"LEFT JOIN SCOTT.EMP AS EMP ON DEPT.DEPTNO = EMP.DEPTNO\n"
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
name|expectedDb2
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2625">[CALCITE-2625]    * Removing Window Boundaries from SqlWindow of Aggregate Function which do not allow Framing</a>    * */
annotation|@
name|Test
specifier|public
name|void
name|testRowNumberFunctionForPrintingOfFrameBoundary
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT row_number() over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY \"hire_date\")\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testRankFunctionForPrintingOfFrameBoundary
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT rank() over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT RANK() OVER (ORDER BY \"hire_date\")\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testLeadFunctionForPrintingOfFrameBoundary
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT lead(\"employee_id\",1,'NA') over "
operator|+
literal|"(partition by \"hire_date\" order by \"employee_id\") FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT LEAD(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" ORDER BY \"employee_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testLagFunctionForPrintingOfFrameBoundary
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT lag(\"employee_id\",1,'NA') over "
operator|+
literal|"(partition by \"hire_date\" order by \"employee_id\") FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" ORDER BY \"employee_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testUnparseSqlIntervalQualifierDb2
parameter_list|()
block|{
name|String
name|queryDatePlus
init|=
literal|"select  * from \"employee\" where  \"hire_date\" + "
operator|+
literal|"INTERVAL '19800' SECOND(5)> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|String
name|expectedDatePlus
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"WHERE (employee.hire_date + 19800 SECOND)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|queryDatePlus
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDatePlus
argument_list|)
expr_stmt|;
name|String
name|queryDateMinus
init|=
literal|"select  * from \"employee\" where  \"hire_date\" - "
operator|+
literal|"INTERVAL '19800' SECOND(5)> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|String
name|expectedDateMinus
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.employee AS employee\n"
operator|+
literal|"WHERE (employee.hire_date - 19800 SECOND)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|queryDateMinus
argument_list|)
operator|.
name|withDb2
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDateMinus
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnparseSqlIntervalQualifierMySql
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select  * from \"employee\" where  \"hire_date\" - "
operator|+
literal|"INTERVAL '19800' SECOND(5)> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
specifier|final
name|String
name|expect0
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"WHERE (`hire_date` - INTERVAL '19800' SECOND)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expect0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select  * from \"employee\" where  \"hire_date\" + "
operator|+
literal|"INTERVAL '10' HOUR> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
specifier|final
name|String
name|expect1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"WHERE (`hire_date` + INTERVAL '10' HOUR)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expect1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select  * from \"employee\" where  \"hire_date\" + "
operator|+
literal|"INTERVAL '1-2' year to month> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
specifier|final
name|String
name|expect2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"WHERE (`hire_date` + INTERVAL '1-2' YEAR_MONTH)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expect2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select  * from \"employee\" "
operator|+
literal|"where  \"hire_date\" + INTERVAL '39:12' MINUTE TO SECOND"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
specifier|final
name|String
name|expect3
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"WHERE (`hire_date` + INTERVAL '39:12' MINUTE_SECOND)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expect3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnparseSqlIntervalQualifierMsSql
parameter_list|()
block|{
name|String
name|queryDatePlus
init|=
literal|"select  * from \"employee\" where  \"hire_date\" +"
operator|+
literal|"INTERVAL '19800' SECOND(5)> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|String
name|expectedDatePlus
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM [foodmart].[employee]\n"
operator|+
literal|"WHERE DATEADD(SECOND, 19800, [hire_date])> '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|queryDatePlus
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDatePlus
argument_list|)
expr_stmt|;
name|String
name|queryDateMinus
init|=
literal|"select  * from \"employee\" where  \"hire_date\" -"
operator|+
literal|"INTERVAL '19800' SECOND(5)> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|String
name|expectedDateMinus
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM [foodmart].[employee]\n"
operator|+
literal|"WHERE DATEADD(SECOND, -19800, [hire_date])> '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|queryDateMinus
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDateMinus
argument_list|)
expr_stmt|;
name|String
name|queryDateMinusNegate
init|=
literal|"select  * from \"employee\" "
operator|+
literal|"where  \"hire_date\" -INTERVAL '-19800' SECOND(5)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|String
name|expectedDateMinusNegate
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM [foodmart].[employee]\n"
operator|+
literal|"WHERE DATEADD(SECOND, 19800, [hire_date])> '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|queryDateMinusNegate
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDateMinusNegate
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
annotation|@
name|Test
specifier|public
name|void
name|testFloorMysqlHour
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO HOUR) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT DATE_FORMAT(`hire_date`, '%Y-%m-%d %H:00:00')\n"
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
name|testFloorMysqlMinute
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
literal|"SELECT DATE_FORMAT(`hire_date`, '%Y-%m-%d %H:%i:00')\n"
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
name|testFloorMysqlSecond
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO SECOND) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT DATE_FORMAT(`hire_date`, '%Y-%m-%d %H:%i:%s')\n"
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
literal|" DATE_FORMAT(`hire_date`, '%Y-%m-%d %H:%i:00')\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"GROUP BY DATE_FORMAT(`hire_date`, '%Y-%m-%d %H:%i:00')"
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
name|expectedSnowflake
init|=
name|expectedPostgresql
decl_stmt|;
specifier|final
name|String
name|expectedRedshift
init|=
name|expectedPostgresql
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
name|withSnowflake
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSnowflake
argument_list|)
operator|.
name|withRedshift
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedRedshift
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
name|expectedSnowflake
init|=
name|expectedPostgresql
decl_stmt|;
specifier|final
name|String
name|expectedRedshift
init|=
name|expectedPostgresql
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
name|withSnowflake
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSnowflake
argument_list|)
operator|.
name|withRedshift
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedRedshift
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
literal|"join \"customer\" as c\n"
operator|+
literal|"  on s.\"customer_id\" = c.\"customer_id\"\n"
operator|+
literal|"join \"product\" as p\n"
operator|+
literal|"  on s.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"join \"product_class\" as pc\n"
operator|+
literal|"  on p.\"product_class_id\" = pc.\"product_class_id\"\n"
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
name|testMatchRecognizeIn
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
literal|"    partition by \"product_class_id\", \"brand_name\" \n"
operator|+
literal|"    order by \"product_class_id\" asc, \"brand_name\" desc \n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.\"net_weight\" in (0, 1),\n"
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
literal|"\"DOWN\" AS PREV(\"DOWN\".\"net_weight\", 0) = "
operator|+
literal|"0 OR PREV(\"DOWN\".\"net_weight\", 0) = 1, "
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
specifier|final
name|String
name|expectedSnowflake
init|=
name|expectedPostgresql
decl_stmt|;
specifier|final
name|String
name|expectedRedshift
init|=
name|expectedPostgresql
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
operator|.
name|withSnowflake
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSnowflake
argument_list|)
operator|.
name|withRedshift
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedRedshift
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2118">[CALCITE-2118]    * RelToSqlConverter should only generate "*" if field names match</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testPreserveAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"warehouse_class_id\" as \"id\",\n"
operator|+
literal|" \"description\"\n"
operator|+
literal|"from \"warehouse_class\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"SELECT \"warehouse_class_id\" AS \"id\", \"description\"\n"
operator|+
literal|"FROM \"foodmart\".\"warehouse_class\""
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
specifier|final
name|String
name|sql2
init|=
literal|"select \"warehouse_class_id\", \"description\"\n"
operator|+
literal|"from \"warehouse_class\""
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"warehouse_class\""
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPreservePermutation
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"description\", \"warehouse_class_id\"\n"
operator|+
literal|"from \"warehouse_class\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"description\", \"warehouse_class_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"warehouse_class\""
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
name|testFieldNamesWithAggregateSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mytable.\"city\",\n"
operator|+
literal|"  sum(mytable.\"store_sales\") as \"my-alias\"\n"
operator|+
literal|"from (select c.\"city\", s.\"store_sales\"\n"
operator|+
literal|"  from \"sales_fact_1997\" as s\n"
operator|+
literal|"    join \"customer\" as c using (\"customer_id\")\n"
operator|+
literal|"  group by c.\"city\", s.\"store_sales\") AS mytable\n"
operator|+
literal|"group by mytable.\"city\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"t0\".\"city\","
operator|+
literal|" SUM(\"t0\".\"store_sales\") AS \"my-alias\"\n"
operator|+
literal|"FROM (SELECT \"customer\".\"city\","
operator|+
literal|" \"sales_fact_1997\".\"store_sales\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"customer\""
operator|+
literal|" ON \"sales_fact_1997\".\"customer_id\""
operator|+
literal|" = \"customer\".\"customer_id\"\n"
operator|+
literal|"GROUP BY \"customer\".\"city\","
operator|+
literal|" \"sales_fact_1997\".\"store_sales\") AS \"t0\"\n"
operator|+
literal|"GROUP BY \"t0\".\"city\""
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
name|testUnparseSelectMustUseDialect
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select * from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.product"
decl_stmt|;
specifier|final
name|boolean
index|[]
name|callsUnparseCallOnSqlSelect
init|=
block|{
literal|false
block|}
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
init|=
operator|new
name|SqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparseCall
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
if|if
condition|(
name|call
operator|instanceof
name|SqlSelect
condition|)
block|{
name|callsUnparseCallOnSqlSelect
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
name|super
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|dialect
argument_list|(
name|dialect
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Dialect must be able to customize unparseCall() for SqlSelect"
argument_list|,
name|callsUnparseCallOnSqlSelect
index|[
literal|0
index|]
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroup1
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", collect(\"net_weight\") "
operator|+
literal|"within group (order by \"net_weight\" desc) "
operator|+
literal|"from \"product\" group by \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COLLECT(\"net_weight\") "
operator|+
literal|"WITHIN GROUP (ORDER BY \"net_weight\" DESC)\n"
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
name|testWithinGroup2
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", collect(\"net_weight\") "
operator|+
literal|"within group (order by \"low_fat\", \"net_weight\" desc nulls last) "
operator|+
literal|"from \"product\" group by \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COLLECT(\"net_weight\") "
operator|+
literal|"WITHIN GROUP (ORDER BY \"low_fat\", \"net_weight\" DESC NULLS LAST)\n"
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
name|testWithinGroup3
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", collect(\"net_weight\") "
operator|+
literal|"within group (order by \"net_weight\" desc), "
operator|+
literal|"min(\"low_fat\")"
operator|+
literal|"from \"product\" group by \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COLLECT(\"net_weight\") "
operator|+
literal|"WITHIN GROUP (ORDER BY \"net_weight\" DESC), MIN(\"low_fat\")\n"
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
name|testWithinGroup4
parameter_list|()
block|{
comment|// filter in AggregateCall is not unparsed
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", collect(\"net_weight\") "
operator|+
literal|"within group (order by \"net_weight\" desc) filter (where \"net_weight\"> 0)"
operator|+
literal|"from \"product\" group by \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", COLLECT(\"net_weight\") "
operator|+
literal|"WITHIN GROUP (ORDER BY \"net_weight\" DESC)\n"
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
name|testJsonValueExpressionOperator
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" format json, "
operator|+
literal|"\"product_name\" format json encoding utf8, "
operator|+
literal|"\"product_name\" format json encoding utf16, "
operator|+
literal|"\"product_name\" format json encoding utf32 from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_name\" FORMAT JSON, "
operator|+
literal|"\"product_name\" FORMAT JSON, "
operator|+
literal|"\"product_name\" FORMAT JSON, "
operator|+
literal|"\"product_name\" FORMAT JSON\n"
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
name|testJsonExists
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_exists(\"product_name\", 'lax $') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_EXISTS(\"product_name\", 'lax $')\n"
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
name|testJsonPretty
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_pretty(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_PRETTY(\"product_name\")\n"
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
name|testJsonValue
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_value(\"product_name\", 'lax $') from \"product\""
decl_stmt|;
comment|// todo translate to JSON_VALUE rather than CAST
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(JSON_VALUE_ANY(\"product_name\", "
operator|+
literal|"'lax $' NULL ON EMPTY NULL ON ERROR) AS VARCHAR(2000) CHARACTER SET \"ISO-8859-1\")\n"
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
name|testJsonQuery
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_query(\"product_name\", 'lax $') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_QUERY(\"product_name\", 'lax $' "
operator|+
literal|"WITHOUT ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)\n"
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
name|testJsonArray
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_array(\"product_name\", \"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_ARRAY(\"product_name\", \"product_name\" ABSENT ON NULL)\n"
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
name|testJsonArrayAgg
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_arrayagg(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_ARRAYAGG(\"product_name\" ABSENT ON NULL)\n"
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
name|testJsonObject
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_object(\"product_name\": \"product_id\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"JSON_OBJECT(KEY \"product_name\" VALUE \"product_id\" NULL ON NULL)\n"
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
name|testJsonObjectAgg
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_objectagg(\"product_name\": \"product_id\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"JSON_OBJECTAGG(KEY \"product_name\" VALUE \"product_id\" NULL ON NULL)\n"
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
name|testJsonPredicate
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select "
operator|+
literal|"\"product_name\" is json, "
operator|+
literal|"\"product_name\" is json value, "
operator|+
literal|"\"product_name\" is json object, "
operator|+
literal|"\"product_name\" is json array, "
operator|+
literal|"\"product_name\" is json scalar, "
operator|+
literal|"\"product_name\" is not json, "
operator|+
literal|"\"product_name\" is not json value, "
operator|+
literal|"\"product_name\" is not json object, "
operator|+
literal|"\"product_name\" is not json array, "
operator|+
literal|"\"product_name\" is not json scalar "
operator|+
literal|"from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"\"product_name\" IS JSON VALUE, "
operator|+
literal|"\"product_name\" IS JSON VALUE, "
operator|+
literal|"\"product_name\" IS JSON OBJECT, "
operator|+
literal|"\"product_name\" IS JSON ARRAY, "
operator|+
literal|"\"product_name\" IS JSON SCALAR, "
operator|+
literal|"\"product_name\" IS NOT JSON VALUE, "
operator|+
literal|"\"product_name\" IS NOT JSON VALUE, "
operator|+
literal|"\"product_name\" IS NOT JSON OBJECT, "
operator|+
literal|"\"product_name\" IS NOT JSON ARRAY, "
operator|+
literal|"\"product_name\" IS NOT JSON SCALAR\n"
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
name|testCrossJoinEmulationForSpark
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from \"employee\", \"department\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM foodmart.employee\n"
operator|+
literal|"CROSS JOIN foodmart.department"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withSpark
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
name|testSubstringInSpark
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
name|expected
init|=
literal|"SELECT SUBSTRING(brand_name, 2)\n"
operator|+
literal|"FROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withSpark
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
name|testSubstringWithForInSpark
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
name|expected
init|=
literal|"SELECT SUBSTRING(brand_name, 2, 3)\n"
operator|+
literal|"FROM foodmart.product"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withSpark
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
name|testFloorInSpark
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select floor(\"hire_date\" TO MINUTE) "
operator|+
literal|"from \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT DATE_TRUNC('MINUTE', hire_date)\n"
operator|+
literal|"FROM foodmart.employee"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withSpark
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
name|testNumericFloorInSpark
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select floor(\"salary\") "
operator|+
literal|"from \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT FLOOR(salary)\n"
operator|+
literal|"FROM foodmart.employee"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withSpark
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
name|testJsonStorageSize
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_storage_size(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_STORAGE_SIZE(\"product_name\")\n"
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
name|testCubeInSpark
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select count(*) "
operator|+
literal|"from \"foodmart\".\"product\" "
operator|+
literal|"group by cube(\"product_id\",\"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY CUBE(\"product_id\", \"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expectedInSpark
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id, product_class_id WITH CUBE"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedInSpark
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollupInSpark
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select count(*) "
operator|+
literal|"from \"foodmart\".\"product\" "
operator|+
literal|"group by rollup(\"product_id\",\"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_id\", \"product_class_id\")"
decl_stmt|;
specifier|final
name|String
name|expectedInSpark
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id, product_class_id WITH ROLLUP"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedInSpark
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonType
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_type(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"JSON_TYPE(\"product_name\")\n"
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
name|testJsonDepth
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_depth(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT "
operator|+
literal|"JSON_DEPTH(\"product_name\")\n"
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
name|testJsonLength
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_length(\"product_name\", 'lax $'), "
operator|+
literal|"json_length(\"product_name\") from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_LENGTH(\"product_name\", 'lax $'), "
operator|+
literal|"JSON_LENGTH(\"product_name\")\n"
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
name|testJsonKeys
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_keys(\"product_name\", 'lax $') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_KEYS(\"product_name\", 'lax $')\n"
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
name|testJsonRemove
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_remove(\"product_name\", '$[0]') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_REMOVE(\"product_name\", '$[0]')\n"
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
name|testUnionAllWithNoOperandsUsingOracleDialect
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"department_id\" "
operator|+
literal|"from \"foodmart\".\"employee\" A "
operator|+
literal|" where A.\"department_id\" = ( select min( A.\"department_id\") from \"foodmart\".\"department\" B where 1=2 )"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"employee\".\"department_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"INNER JOIN (SELECT \"t1\".\"department_id\" \"department_id0\", MIN(\"t1\".\"department_id\")\n"
operator|+
literal|"FROM (SELECT NULL \"department_id\", NULL \"department_description\"\nFROM \"DUAL\"\nWHERE 1 = 0) \"t\",\n"
operator|+
literal|"(SELECT \"department_id\"\nFROM \"foodmart\".\"employee\"\nGROUP BY \"department_id\") \"t1\"\n"
operator|+
literal|"GROUP BY \"t1\".\"department_id\") \"t3\" ON \"employee\".\"department_id\" = \"t3\".\"department_id0\""
operator|+
literal|" AND \"employee\".\"department_id\" = MIN(\"t1\".\"department_id\")"
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
name|testUnionAllWithNoOperands
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select A.\"department_id\" "
operator|+
literal|"from \"foodmart\".\"employee\" A "
operator|+
literal|" where A.\"department_id\" = ( select min( A.\"department_id\") from \"foodmart\".\"department\" B where 1=2 )"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"employee\".\"department_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"INNER JOIN (SELECT \"t1\".\"department_id\" AS \"department_id0\","
operator|+
literal|" MIN(\"t1\".\"department_id\")\n"
operator|+
literal|"FROM (SELECT *\nFROM (VALUES  (NULL, NULL))"
operator|+
literal|" AS \"t\" (\"department_id\", \"department_description\")"
operator|+
literal|"\nWHERE 1 = 0) AS \"t\","
operator|+
literal|"\n(SELECT \"department_id\"\nFROM \"foodmart\".\"employee\""
operator|+
literal|"\nGROUP BY \"department_id\") AS \"t1\""
operator|+
literal|"\nGROUP BY \"t1\".\"department_id\") AS \"t3\" "
operator|+
literal|"ON \"employee\".\"department_id\" = \"t3\".\"department_id0\""
operator|+
literal|" AND \"employee\".\"department_id\" = MIN(\"t1\".\"department_id\")"
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
name|testSmallintOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT CAST(\"department_id\" AS SMALLINT) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT CAST(\"department_id\" AS NUMBER(5))\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testBigintOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT CAST(\"department_id\" AS BIGINT) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT CAST(\"department_id\" AS NUMBER(19))\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testDoubleOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT CAST(\"department_id\" AS DOUBLE) FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT CAST(\"department_id\" AS DOUBLE PRECISION)\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testDateLiteralOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT DATE '1978-05-02' FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT TO_DATE ('1978-05-02', 'YYYY-MM-DD')\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testTimestampLiteralOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT TIMESTAMP '1978-05-02 12:34:56.78' FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT TO_TIMESTAMP ('1978-05-02 12:34:56.78', 'YYYY-MM-DD HH24:MI:SS.FF')\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testTimeLiteralOracle
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT TIME '12:34:56.78' FROM \"employee\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT TO_TIME ('12:34:56.78', 'HH24:MI:SS.FF')\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|testSupportsDataType
parameter_list|()
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|booleanDataType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|integerDataType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|final
name|SqlDialect
name|oracleDialect
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|ORACLE
operator|.
name|getDialect
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|oracleDialect
operator|.
name|supportsDataType
argument_list|(
name|booleanDataType
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|oracleDialect
operator|.
name|supportsDataType
argument_list|(
name|integerDataType
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SqlDialect
name|postgresqlDialect
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|postgresqlDialect
operator|.
name|supportsDataType
argument_list|(
name|booleanDataType
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|postgresqlDialect
operator|.
name|supportsDataType
argument_list|(
name|integerDataType
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Fluid interface to run tests. */
specifier|static
class|class
name|Sql
block|{
specifier|private
specifier|final
name|SchemaPlus
name|schema
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
name|this
operator|.
name|schema
operator|=
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|schemaSpec
argument_list|)
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
parameter_list|(
name|SchemaPlus
name|schema
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
name|schema
operator|=
name|schema
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
name|schema
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
name|withMysql8
parameter_list|()
block|{
specifier|final
name|SqlDialect
name|mysqlDialect
init|=
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
return|return
name|dialect
argument_list|(
operator|new
name|SqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|MYSQL
argument_list|)
operator|.
name|withDatabaseMajorVersion
argument_list|(
literal|8
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
name|mysqlDialect
operator|.
name|quoteIdentifier
argument_list|(
literal|""
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|mysqlDialect
operator|.
name|getNullCollation
argument_list|()
argument_list|)
argument_list|)
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
name|withRedshift
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|DatabaseProduct
operator|.
name|REDSHIFT
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withSnowflake
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|DatabaseProduct
operator|.
name|SNOWFLAKE
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
name|withBigQuery
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|BIG_QUERY
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withSpark
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|DatabaseProduct
operator|.
name|SPARK
operator|.
name|getDialect
argument_list|()
argument_list|)
return|;
block|}
name|Sql
name|withPostgresqlModifiedTypeSystem
parameter_list|()
block|{
comment|// Postgresql dialect with max length for varchar set to 256
specifier|final
name|PostgresqlSqlDialect
name|postgresqlSqlDialect
init|=
operator|new
name|PostgresqlSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
operator|.
name|withDataTypeSystem
argument_list|(
operator|new
name|RelDataTypeSystemImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|VARCHAR
case|:
return|return
literal|256
return|;
default|default:
return|return
name|super
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|dialect
argument_list|(
name|postgresqlSqlDialect
argument_list|)
return|;
block|}
name|Sql
name|withOracleModifiedTypeSystem
parameter_list|()
block|{
comment|// Oracle dialect with max length for varchar set to 512
specifier|final
name|OracleSqlDialect
name|oracleSqlDialect
init|=
operator|new
name|OracleSqlDialect
argument_list|(
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|ORACLE
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
operator|.
name|withDataTypeSystem
argument_list|(
operator|new
name|RelDataTypeSystemImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|VARCHAR
case|:
return|return
literal|512
return|;
default|default:
return|return
name|super
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|dialect
argument_list|(
name|oracleSqlDialect
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
name|schema
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
name|schema
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
name|r
lambda|->
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
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
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
name|isLinux
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
name|AssertionError
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
name|schema
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
return|return
name|toSql
argument_list|(
name|rel
argument_list|,
name|dialect
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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

