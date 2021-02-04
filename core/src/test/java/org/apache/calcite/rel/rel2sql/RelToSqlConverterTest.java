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
name|RelOptRule
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
name|logical
operator|.
name|LogicalAggregate
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
name|logical
operator|.
name|LogicalFilter
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
name|AggregateJoinTransposeRule
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
name|AggregateProjectMergeRule
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
name|CoreRules
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
name|FilterJoinRule
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
name|ProjectToWindowRule
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
name|PruneEmptyRules
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
name|RexFieldCollation
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
name|RexWindowBounds
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
name|runtime
operator|.
name|Hook
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
name|SqlWriterConfig
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
name|MssqlSqlDialect
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
name|SqlLibrary
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
name|SqlLibraryOperatorTableFactory
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
name|sql
operator|.
name|util
operator|.
name|SqlOperatorTables
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
name|util
operator|.
name|SqlShuttle
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
name|validate
operator|.
name|SqlConformance
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
name|MockSqlOperatorTable
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
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|function
operator|.
name|UnaryOperator
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for {@link RelToSqlConverter}.  */
end_comment

begin_class
class|class
name|RelToSqlConverterTest
block|{
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
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Initiates a test case with a given {@link RelNode} supplier. */
specifier|private
name|Sql
name|relFn
parameter_list|(
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
parameter_list|)
block|{
return|return
name|sql
argument_list|(
literal|"?"
argument_list|)
operator|.
name|relFn
argument_list|(
name|relFn
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
name|Collection
argument_list|<
name|SqlLibrary
argument_list|>
name|librarySet
parameter_list|,
name|Program
modifier|...
name|programs
parameter_list|)
block|{
specifier|final
name|MockSqlOperatorTable
name|operatorTable
init|=
operator|new
name|MockSqlOperatorTable
argument_list|(
name|SqlOperatorTables
operator|.
name|chain
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|SqlLibraryOperatorTableFactory
operator|.
name|INSTANCE
operator|.
name|getOperatorTable
argument_list|(
name|librarySet
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MockSqlOperatorTable
operator|.
name|addRamp
argument_list|(
name|operatorTable
argument_list|)
expr_stmt|;
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
name|operatorTable
argument_list|(
name|operatorTable
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
name|MysqlSqlDialect
operator|.
name|DEFAULT_CONTEXT
operator|.
name|withNullCollation
argument_list|(
name|nullCollation
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a collection of common dialects, and the database products they    * represent. */
specifier|private
specifier|static
name|Map
argument_list|<
name|SqlDialect
argument_list|,
name|DatabaseProduct
argument_list|>
name|dialects
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
expr|<
name|SqlDialect
operator|,
name|DatabaseProduct
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|BIG_QUERY
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CALCITE
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|DB2
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|DB2
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|HIVE
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|HIVE
argument_list|)
operator|.
name|put
argument_list|(
name|jethroDataSqlDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|JETHRO
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MSSQL
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MSSQL
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
argument_list|)
operator|.
name|put
argument_list|(
name|mySqlDialect
argument_list|(
name|NullCollation
operator|.
name|HIGH
argument_list|)
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|ORACLE
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|ORACLE
argument_list|)
operator|.
name|put
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
operator|.
name|getDialect
argument_list|()
argument_list|,
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|put
argument_list|(
name|DatabaseProduct
operator|.
name|PRESTO
operator|.
name|getDialect
argument_list|()
argument_list|,
name|DatabaseProduct
operator|.
name|PRESTO
argument_list|)
operator|.
name|build
argument_list|()
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
return|return
name|toSql
argument_list|(
name|root
argument_list|,
name|dialect
argument_list|,
name|c
lambda|->
name|c
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|false
argument_list|)
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|false
argument_list|)
operator|.
name|withUpdateSetListNewline
argument_list|(
literal|false
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts a relational expression to SQL in a given dialect    * and with a particular writer configuration. */
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
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform
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
name|visitRoot
argument_list|(
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
name|c
lambda|->
name|transform
operator|.
name|apply
argument_list|(
name|c
operator|.
name|withDialect
argument_list|(
name|dialect
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getSql
argument_list|()
return|;
block|}
annotation|@
name|Test
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
name|void
name|testAggregateFilterWhereToSqlFromProductTable
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select\n"
operator|+
literal|"  sum(\"shelf_width\") filter (where \"net_weight\"> 0),\n"
operator|+
literal|"  sum(\"shelf_width\")\n"
operator|+
literal|"from \"foodmart\".\"product\"\n"
operator|+
literal|"where \"product_id\"> 0\n"
operator|+
literal|"group by \"product_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" SUM(\"shelf_width\") FILTER (WHERE \"net_weight\"> 0 IS TRUE),"
operator|+
literal|" SUM(\"shelf_width\")\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_id\"> 0\n"
operator|+
literal|"GROUP BY \"product_id\""
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
name|void
name|testAggregateFilterWhereToBigQuerySqlFromProductTable
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select\n"
operator|+
literal|"  sum(\"shelf_width\") filter (where \"net_weight\"> 0),\n"
operator|+
literal|"  sum(\"shelf_width\")\n"
operator|+
literal|"from \"foodmart\".\"product\"\n"
operator|+
literal|"where \"product_id\"> 0\n"
operator|+
literal|"group by \"product_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUM(CASE WHEN net_weight> 0 IS TRUE"
operator|+
literal|" THEN shelf_width ELSE NULL END), "
operator|+
literal|"SUM(shelf_width)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"WHERE product_id> 0\n"
operator|+
literal|"GROUP BY product_id"
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
name|void
name|testPivotToSqlFromProductTable
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select * from (\n"
operator|+
literal|"  select \"shelf_width\", \"net_weight\", \"product_id\"\n"
operator|+
literal|"  from \"foodmart\".\"product\")\n"
operator|+
literal|"  pivot (sum(\"shelf_width\") as w, count(*) as c\n"
operator|+
literal|"    for (\"product_id\") in (10, 20))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"net_weight\","
operator|+
literal|" SUM(\"shelf_width\") FILTER (WHERE \"product_id\" = 10) AS \"10_W\","
operator|+
literal|" COUNT(*) FILTER (WHERE \"product_id\" = 10) AS \"10_C\","
operator|+
literal|" SUM(\"shelf_width\") FILTER (WHERE \"product_id\" = 20) AS \"20_W\","
operator|+
literal|" COUNT(*) FILTER (WHERE \"product_id\" = 20) AS \"20_C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"net_weight\""
decl_stmt|;
comment|// BigQuery does not support FILTER, so we generate CASE around the
comment|// arguments to the aggregate functions.
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT net_weight,"
operator|+
literal|" SUM(CASE WHEN product_id = 10 "
operator|+
literal|"THEN shelf_width ELSE NULL END) AS `10_W`,"
operator|+
literal|" COUNT(CASE WHEN product_id = 10 THEN 1 ELSE NULL END) AS `10_C`,"
operator|+
literal|" SUM(CASE WHEN product_id = 20 "
operator|+
literal|"THEN shelf_width ELSE NULL END) AS `20_W`,"
operator|+
literal|" COUNT(CASE WHEN product_id = 20 THEN 1 ELSE NULL END) AS `20_C`\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY net_weight"
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
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testSelectQueryWithWhereClauseOfLessThan
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\", \"shelf_width\"\n"
operator|+
literal|"from \"product\" where \"product_id\"< 10"
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
name|void
name|testSelectWhereNotEqualsOrNull
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\", \"shelf_width\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"where \"net_weight\"<> 10 or \"net_weight\" is null"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\", \"shelf_width\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"net_weight\"<> 10 OR \"net_weight\" IS NULL"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4449">[CALCITE-4449]    * Calcite generates incorrect SQL for Sarg 'x IS NULL OR x NOT IN    * (1, 2)'</a>. */
annotation|@
name|Test
name|void
name|testSelectWhereNotIn
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|or
argument_list|(
name|b
operator|.
name|isNull
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"COMM"
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|not
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"COMM"
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
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
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"COMM\" IS NULL OR \"COMM\" NOT IN (1, 2)"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
name|void
name|testSelectWhereNotEquals
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|or
argument_list|(
name|b
operator|.
name|isNull
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"COMM"
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|not
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"COMM"
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
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
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"COMM\" IS NULL OR \"COMM\"<> 1"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
name|void
name|testSelectQueryWithHiveCube
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_class_id\", \"product_id\", count(*) "
operator|+
literal|"from \"product\" group by cube(\"product_class_id\", \"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT product_class_id, product_id, COUNT(*)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_class_id, product_id WITH CUBE"
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
name|SqlDialect
name|sqlDialect
init|=
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|dialect
decl_stmt|;
name|assertTrue
argument_list|(
name|sqlDialect
operator|.
name|supportsGroupByWithCube
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectQueryWithHiveRollup
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_class_id\", \"product_id\", count(*) "
operator|+
literal|"from \"product\" group by rollup(\"product_class_id\", \"product_id\")"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT product_class_id, product_id, COUNT(*)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_class_id, product_id WITH ROLLUP"
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
name|SqlDialect
name|sqlDialect
init|=
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|dialect
decl_stmt|;
name|assertTrue
argument_list|(
name|sqlDialect
operator|.
name|supportsGroupByWithRollup
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT 42 AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3097">[CALCITE-3097]    * GROUPING SETS breaks on sets of size&gt; 1 due to precedence issues</a>,    * in particular, that we maintain proper precedence around nested lists. */
annotation|@
name|Test
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
comment|/** Tests a query with GROUP BY and a sub-query which is also with GROUP BY.    * If we flatten sub-queries, the number of rows going into AVG becomes    * incorrect. */
annotation|@
name|Test
name|void
name|testSelectQueryWithGroupBySubQuery1
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_class_id\", avg(\"product_id\")\n"
operator|+
literal|"from (select \"product_class_id\", \"product_id\", avg(\"product_class_id\")\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by \"product_class_id\", \"product_id\") as t\n"
operator|+
literal|"group by \"product_class_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_class_id\", AVG(\"product_id\")\n"
operator|+
literal|"FROM (SELECT \"product_class_id\", \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\") AS \"t1\"\n"
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
comment|/** Tests query without GROUP BY but an aggregate function    * and a sub-query which is with GROUP BY. */
annotation|@
name|Test
name|void
name|testSelectQueryWithGroupBySubQuery2
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select sum(\"product_id\")\n"
operator|+
literal|"from (select \"product_class_id\", \"product_id\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by \"product_class_id\", \"product_id\") as t"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUM(\"product_id\")\n"
operator|+
literal|"FROM (SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_class_id\", \"product_id\") AS \"t1\""
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT SUM(`product_id`)\n"
operator|+
literal|"FROM (SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_class_id`, `product_id`) AS `t1`"
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
name|expectedMysql
argument_list|)
expr_stmt|;
comment|// Equivalent sub-query that uses SELECT DISTINCT
specifier|final
name|String
name|query2
init|=
literal|"select sum(\"product_id\")\n"
operator|+
literal|"from (select distinct \"product_class_id\", \"product_id\"\n"
operator|+
literal|"    from \"product\") as t"
decl_stmt|;
name|sql
argument_list|(
name|query2
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
name|expectedMysql
argument_list|)
expr_stmt|;
block|}
comment|/** CUBE of one column is equivalent to ROLLUP, and Calcite recognizes    * this. */
annotation|@
name|Test
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")\n"
operator|+
literal|"ORDER BY \"product_class_id\" IS NULL, \"product_class_id\", "
operator|+
literal|"COUNT(*) IS NULL, COUNT(*)"
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
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testSelectQueryWithSingletonCube()}, but no ORDER BY    * clause. */
annotation|@
name|Test
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")"
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
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
comment|/** Cannot rewrite if ORDER BY contains a column not in GROUP BY (in this    * case COUNT(*)). */
annotation|@
name|Test
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT \"product_class_id\", COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_class_id\")\n"
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testSum0BecomesCoalesce
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|()
argument_list|,
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUM0
argument_list|,
name|b
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
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT COALESCE(SUM(\"MGR\"), 0) AS \"s\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\""
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
comment|/** As {@link #testSum0BecomesCoalesce()} but for windowed aggregates. */
annotation|@
name|Test
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
name|void
name|testStack
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
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
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|b
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
specifier|final
name|SqlDialect
name|dialect
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|relFn
operator|.
name|apply
argument_list|(
name|relBuilder
argument_list|()
argument_list|)
decl_stmt|;
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
name|visitRoot
argument_list|(
name|root
argument_list|)
operator|.
name|asStatement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sqlString
init|=
name|sqlNode
operator|.
name|accept
argument_list|(
operator|new
name|SqlShuttle
argument_list|()
argument_list|)
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sqlString
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAntiJoin
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
literal|"DEPT"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|ANTI
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"DEPT\"\n"
operator|+
literal|"WHERE NOT EXISTS (SELECT 1\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPT\".\"DEPTNO\" = \"EMP\".\"DEPTNO\")"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4491">[CALCITE-4491]    * Aggregation of window function produces invalid SQL for PostgreSQL</a>. */
annotation|@
name|Test
name|void
name|testAggregatedWindowFunction
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"SAL"
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|b
operator|.
name|alias
argument_list|(
name|b
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeOver
argument_list|(
name|b
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|,
name|SqlStdOperatorTable
operator|.
name|RANK
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
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|RexFieldCollation
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"SAL"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RexWindowBounds
operator|.
name|UNBOUNDED_PRECEDING
argument_list|,
name|RexWindowBounds
operator|.
name|UNBOUNDED_FOLLOWING
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"rank"
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
literal|"t"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|()
argument_list|,
name|b
operator|.
name|count
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"t"
argument_list|,
literal|"rank"
argument_list|)
argument_list|)
operator|.
name|distinct
argument_list|()
operator|.
name|as
argument_list|(
literal|"c"
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// PostgreSQL does not not support nested aggregations
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT COUNT(DISTINCT \"rank\") AS \"c\"\n"
operator|+
literal|"FROM (SELECT RANK() OVER (ORDER BY \"SAL\") AS \"rank\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\") AS \"t\"\n"
operator|+
literal|"HAVING COUNT(DISTINCT \"rank\")>= 10"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
comment|// Oracle does support nested aggregations
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT COUNT(DISTINCT RANK() OVER (ORDER BY \"SAL\")) \"c\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"HAVING COUNT(DISTINCT RANK() OVER (ORDER BY \"SAL\"))>= 10"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
annotation|@
name|Test
name|void
name|testSemiJoin
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
literal|"DEPT"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|SEMI
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"DEPT\"\n"
operator|+
literal|"WHERE EXISTS (SELECT 1\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPT\".\"DEPTNO\" = \"EMP\".\"DEPTNO\")"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
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
name|void
name|testSemiJoinFilter
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
literal|"DEPT"
argument_list|)
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
name|GREATER_THAN
argument_list|,
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
operator|(
name|short
operator|)
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|SEMI
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"DEPT\"\n"
operator|+
literal|"WHERE EXISTS (SELECT 1\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"EMPNO\"> 10) AS \"t\"\n"
operator|+
literal|"WHERE \"DEPT\".\"DEPTNO\" = \"t\".\"DEPTNO\")"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
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
name|void
name|testSemiJoinProject
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
literal|"DEPT"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
name|builder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getField
argument_list|(
literal|"EMPNO"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
name|builder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getField
argument_list|(
literal|"DEPTNO"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|SEMI
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"DEPT\"\n"
operator|+
literal|"WHERE EXISTS (SELECT 1\n"
operator|+
literal|"FROM (SELECT \"EMPNO\", \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\") AS \"t\"\n"
operator|+
literal|"WHERE \"DEPT\".\"DEPTNO\" = \"t\".\"DEPTNO\")"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
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
name|void
name|testSemiNestedJoin
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
name|base
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
literal|"EMP"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|"EMPNO"
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
literal|"EMPNO"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
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
literal|"DEPT"
argument_list|)
operator|.
name|push
argument_list|(
name|base
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|SEMI
argument_list|,
name|builder
operator|.
name|equals
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"DEPTNO\"\n"
operator|+
literal|"FROM \"scott\".\"DEPT\"\n"
operator|+
literal|"WHERE EXISTS (SELECT 1\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"INNER JOIN \"scott\".\"EMP\" AS \"EMP0\" ON \"EMP\".\"EMPNO\" = \"EMP0\".\"EMPNO\"\n"
operator|+
literal|"WHERE \"DEPT\".\"DEPTNO\" = \"EMP\".\"DEPTNO\")"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2792">[CALCITE-2792]    * StackOverflowError while evaluating filter with large number of OR    * conditions</a>. */
annotation|@
name|Test
name|void
name|testBalancedBinaryCall
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|and
argument_list|(
name|b
operator|.
name|or
argument_list|(
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|b
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
argument_list|,
name|b
operator|.
name|or
argument_list|(
name|IntStream
operator|.
name|range
argument_list|(
literal|5
argument_list|,
literal|8
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
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
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"EMPNO\" IN (0, 1, 2, 3) AND \"DEPTNO\" IN (5, 6, 7)"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
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
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT SUM(net_weight1) AS net_weight_converted\n"
operator|+
literal|"FROM (SELECT SUM(net_weight) AS net_weight1\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id) AS t1"
decl_stmt|;
specifier|final
name|String
name|expectedHive
init|=
literal|"SELECT SUM(net_weight1) net_weight_converted\n"
operator|+
literal|"FROM (SELECT SUM(net_weight) net_weight1\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id) t1"
decl_stmt|;
specifier|final
name|String
name|expectedSpark
init|=
name|expectedHive
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
operator|.
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedHive
argument_list|)
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSpark
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2628">[CALCITE-2628]    * JDBC adapter throws NullPointerException while generating GROUP BY query    * for MySQL</a>.    *    *<p>MySQL does not support nested aggregates, so {@link RelToSqlConverter}    * performs some extra checks, looking for aggregates in the input    * sub-query, and these would fail with {@code NullPointerException}    * and {@code ClassCastException} in some cases. */
annotation|@
name|Test
name|void
name|testNestedAggregatesMySqlTable
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|()
argument_list|,
name|b
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|b
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
name|String
name|expectedSql
init|=
literal|"SELECT COUNT(`MGR`) AS `c`\n"
operator|+
literal|"FROM `scott`.`EMP`"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testNestedAggregatesMySqlTable()}, but input is a sub-query,    * not a table. */
annotation|@
name|Test
name|void
name|testNestedAggregatesMySqlStar
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
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
name|b
operator|.
name|groupKey
argument_list|()
argument_list|,
name|b
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|b
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
name|String
name|expectedSql
init|=
literal|"SELECT COUNT(`MGR`) AS `c`\n"
operator|+
literal|"FROM `scott`.`EMP`\n"
operator|+
literal|"WHERE `DEPTNO` = 10"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3207">[CALCITE-3207]    * Fail to convert Join RelNode with like condition to sql statement</a>.    */
annotation|@
name|Test
name|void
name|testJoinWithLikeConditionRel2Sql
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
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
name|b
operator|.
name|and
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|b
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
name|b
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
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|,
name|b
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
name|b
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
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectQueryWithGroupByAndProjectList1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(*) from \"product\"\n"
operator|+
literal|"group by \"product_class_id\", \"product_id\""
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
literal|"FROM (SELECT \"product\".\"product_id\","
operator|+
literal|" MIN(\"sales_fact_1997\".\"store_id\") AS \"EXPR$1\"\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3811">[CALCITE-3811]    * JDBC adapter generates SQL with invalid field names if Filter's row type    * is different from its input</a>. */
annotation|@
name|Test
name|void
name|testHavingAlias
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|relBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|alias
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
literal|"D"
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"D"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|countStar
argument_list|(
literal|"emps.count"
argument_list|)
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
name|LESS_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"emps.count"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|LogicalFilter
name|filter
init|=
operator|(
name|LogicalFilter
operator|)
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[D, emps.count]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a LogicalAggregate similar to the input of filter, but with different
comment|// field names.
specifier|final
name|LogicalAggregate
name|newAggregate
init|=
operator|(
name|LogicalAggregate
operator|)
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|alias
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
literal|"D2"
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"D2"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|countStar
argument_list|(
literal|"emps.count"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|newAggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[D2, emps.count]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Change filter's input. Its row type does not change.
name|filter
operator|.
name|replaceInput
argument_list|(
literal|0
argument_list|,
name|newAggregate
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[D, emps.count]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|push
argument_list|(
name|filter
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|alias
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"D"
argument_list|)
argument_list|,
literal|"emps.deptno"
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
literal|"SELECT `D2` AS `emps.deptno`\n"
operator|+
literal|"FROM (SELECT `DEPTNO` AS `D2`, COUNT(*) AS `emps.count`\n"
operator|+
literal|"FROM `scott`.`EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`\n"
operator|+
literal|"HAVING `emps.count`< 2) AS `t1`"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT \"DEPTNO\" AS \"emps.deptno\"\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"GROUP BY \"DEPTNO\"\n"
operator|+
literal|"HAVING COUNT(*)< 2"
decl_stmt|;
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT D2 AS `emps.deptno`\n"
operator|+
literal|"FROM (SELECT DEPTNO AS D2, COUNT(*) AS `emps.count`\n"
operator|+
literal|"FROM scott.EMP\n"
operator|+
literal|"GROUP BY DEPTNO\n"
operator|+
literal|"HAVING `emps.count`< 2) AS t1"
decl_stmt|;
name|relFn
argument_list|(
name|b
lambda|->
name|root
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
name|withPostgresql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPostgresql
argument_list|)
operator|.
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3896">[CALCITE-3896]    * JDBC adapter, when generating SQL, changes target of ambiguous HAVING    * clause with a Project on Filter on Aggregate</a>.    *    *<p>The alias is ambiguous in dialects such as MySQL and BigQuery that    * have {@link SqlConformance#isHavingAlias()} = true. When the HAVING clause    * tries to reference a column, it sees the alias instead. */
annotation|@
name|Test
name|void
name|testHavingAliasSameAsColumnIgnoringCase
parameter_list|()
block|{
name|checkHavingAliasSameAsColumn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHavingAliasSameAsColumn
parameter_list|()
block|{
name|checkHavingAliasSameAsColumn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkHavingAliasSameAsColumn
parameter_list|(
name|boolean
name|upperAlias
parameter_list|)
block|{
specifier|final
name|String
name|alias
init|=
name|upperAlias
condition|?
literal|"GROSS_WEIGHT"
else|:
literal|"gross_weight"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\" + 1,\n"
operator|+
literal|"  sum(\"gross_weight\") as \""
operator|+
name|alias
operator|+
literal|"\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"group by \"product_id\"\n"
operator|+
literal|"having sum(\"product\".\"gross_weight\")< 200"
decl_stmt|;
comment|// PostgreSQL has isHavingAlias=false, case-sensitive=true
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT \"product_id\" + 1,"
operator|+
literal|" SUM(\"gross_weight\") AS \""
operator|+
name|alias
operator|+
literal|"\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY \"product_id\"\n"
operator|+
literal|"HAVING SUM(\"gross_weight\")< 200"
decl_stmt|;
comment|// MySQL has isHavingAlias=true, case-sensitive=true
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT `product_id` + 1, `"
operator|+
name|alias
operator|+
literal|"`\n"
operator|+
literal|"FROM (SELECT `product_id`, SUM(`gross_weight`) AS `"
operator|+
name|alias
operator|+
literal|"`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"GROUP BY `product_id`\n"
operator|+
literal|"HAVING `"
operator|+
name|alias
operator|+
literal|"`< 200) AS `t1`"
decl_stmt|;
comment|// BigQuery has isHavingAlias=true, case-sensitive=false
specifier|final
name|String
name|expectedBigQuery
init|=
name|upperAlias
condition|?
literal|"SELECT product_id + 1, GROSS_WEIGHT\n"
operator|+
literal|"FROM (SELECT product_id, SUM(gross_weight) AS GROSS_WEIGHT\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id\n"
operator|+
literal|"HAVING GROSS_WEIGHT< 200) AS t1"
comment|// Before [CALCITE-3896] was fixed, we got
comment|// "HAVING SUM(gross_weight)< 200) AS t1"
comment|// which on BigQuery gives you an error about aggregating aggregates
else|:
literal|"SELECT product_id + 1, gross_weight\n"
operator|+
literal|"FROM (SELECT product_id, SUM(gross_weight) AS gross_weight\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"GROUP BY product_id\n"
operator|+
literal|"HAVING gross_weight< 200) AS t1"
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
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysql
argument_list|)
operator|.
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHaving4
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"product_id\"\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select \"product_id\", avg(\"gross_weight\") as agw\n"
operator|+
literal|"  from \"product\"\n"
operator|+
literal|"  where \"net_weight\"< 100\n"
operator|+
literal|"  group by \"product_id\")\n"
operator|+
literal|"where agw> 50\n"
operator|+
literal|"group by \"product_id\"\n"
operator|+
literal|"having avg(agw)> 60\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM (SELECT \"product_id\", AVG(\"gross_weight\") AS \"AGW\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"net_weight\"< 100\n"
operator|+
literal|"GROUP BY \"product_id\"\n"
operator|+
literal|"HAVING AVG(\"gross_weight\")> 50) AS \"t2\"\n"
operator|+
literal|"GROUP BY \"product_id\"\n"
operator|+
literal|"HAVING AVG(\"AGW\")> 60"
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
name|void
name|testSelectQueryWithOrderByClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"net_weight\""
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
name|void
name|testSelectQueryWithTwoOrderByClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"net_weight\", \"gross_weight\""
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3440">[CALCITE-3440]    * RelToSqlConverter does not properly alias ambiguous ORDER BY</a>. */
annotation|@
name|Test
name|void
name|testOrderByColumnWithSameNameAsAlias
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" as \"p\",\n"
operator|+
literal|" \"net_weight\" as \"product_id\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"order by 1"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"product_id\" AS \"p\","
operator|+
literal|" \"net_weight\" AS \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY 1"
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
name|void
name|testOrderByColumnWithSameNameAsAlias2
parameter_list|()
block|{
comment|// We use ordinal "2" because the column name "product_id" is obscured
comment|// by alias "product_id".
name|String
name|query
init|=
literal|"select \"net_weight\" as \"product_id\",\n"
operator|+
literal|"  \"product_id\" as \"product_id\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"order by \"product\".\"product_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"net_weight\" AS \"product_id\","
operator|+
literal|" \"product_id\" AS \"product_id0\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"ORDER BY 2"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT `net_weight` AS `product_id`,"
operator|+
literal|" `product_id` AS `product_id0`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"ORDER BY `product_id` IS NULL, 2"
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
name|expectedMysql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3282">[CALCITE-3282]    * HiveSqlDialect unparse Interger type as Int in order    * to be compatible with Hive1.x</a>. */
annotation|@
name|Test
name|void
name|testHiveCastAsInt
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select cast( cast(\"employee_id\" as varchar) as int) "
operator|+
literal|"from \"foodmart\".\"reserve_employee\" "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(CAST(employee_id AS VARCHAR) AS INT)\n"
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
annotation|@
name|Test
name|void
name|testBigQueryCast
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select cast(cast(\"employee_id\" as varchar) as bigint), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as smallint), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as tinyint), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as integer), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as float), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as char), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as binary), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as varbinary), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as timestamp), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as double), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as decimal), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as date), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as time), "
operator|+
literal|"cast(cast(\"employee_id\" as varchar) as boolean) "
operator|+
literal|"from \"foodmart\".\"reserve_employee\" "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(CAST(employee_id AS STRING) AS INT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS INT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS INT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS INT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS FLOAT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS STRING), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS BYTES), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS BYTES), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS TIMESTAMP), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS FLOAT64), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS NUMERIC), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS DATE), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS TIME), "
operator|+
literal|"CAST(CAST(employee_id AS STRING) AS BOOL)\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3220">[CALCITE-3220]    * HiveSqlDialect should transform the SQL-standard TRIM function to TRIM,    * LTRIM or RTRIM</a>,    *<a href="https://issues.apache.org/jira/browse/CALCITE-3663">[CALCITE-3663]    * Support for TRIM function in BigQuery dialect</a>, and    *<a href="https://issues.apache.org/jira/browse/CALCITE-3771">[CALCITE-3771]    * Support of TRIM function for SPARK dialect and improvement in HIVE    * Dialect</a>. */
annotation|@
name|Test
name|void
name|testHiveSparkAndBqTrim
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(' str ')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT TRIM(' str ')\n"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
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
name|void
name|testHiveSparkAndBqTrimWithBoth
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(both ' ' from ' str ')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT TRIM(' str ')\n"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
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
name|void
name|testHiveSparkAndBqTrimWithLeading
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(LEADING ' ' from ' str ')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT LTRIM(' str ')\n"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
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
name|void
name|testHiveSparkAndBqTrimWithTailing
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(TRAILING ' ' from ' str ')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT RTRIM(' str ')\n"
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
operator|.
name|withSpark
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3663">[CALCITE-3663]    * Support for TRIM function in BigQuery dialect</a>. */
annotation|@
name|Test
name|void
name|testBqTrimWithLeadingChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(LEADING 'a' from 'abcd')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT LTRIM('abcd', 'a')\n"
operator|+
literal|"FROM foodmart.reserve_employee"
decl_stmt|;
specifier|final
name|String
name|expectedHS
init|=
literal|"SELECT REGEXP_REPLACE('abcd', '^(a)*', '')\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3771">[CALCITE-3771]    * Support of TRIM function for SPARK dialect and improvement in HIVE Dialect</a>. */
annotation|@
name|Test
name|void
name|testHiveAndSparkTrimWithLeadingChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(LEADING 'a' from 'abcd')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT REGEXP_REPLACE('abcd', '^(a)*', '')\n"
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
name|void
name|testBqTrimWithBothChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(both 'a' from 'abcda')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT TRIM('abcda', 'a')\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
name|void
name|testHiveAndSparkTrimWithBothChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(both 'a' from 'abcda')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT REGEXP_REPLACE('abcda', '^(a)*|(a)*$', '')\n"
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
name|void
name|testHiveBqTrimWithTailingChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(TRAILING 'a' from 'abcd')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT RTRIM('abcd', 'a')\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
name|void
name|testHiveAndSparkTrimWithTailingChar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(TRAILING 'a' from 'abcd')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT REGEXP_REPLACE('abcd', '(a)*$', '')\n"
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
name|void
name|testBqTrimWithBothSpecialCharacter
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(BOTH '$@*A' from '$@*AABC$@*AADCAA$@*A')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT TRIM('$@*AABC$@*AADCAA$@*A', '$@*A')\n"
operator|+
literal|"FROM foodmart.reserve_employee"
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
name|void
name|testHiveAndSparkTrimWithBothSpecialCharacter
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT TRIM(BOTH '$@*A' from '$@*AABC$@*AADCAA$@*A')\n"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT REGEXP_REPLACE('$@*AABC$@*AADCAA$@*A',"
operator|+
literal|" '^(\\$\\@\\*A)*|(\\$\\@\\*A)*$', '')\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2715">[CALCITE-2715]    * MS SQL Server does not support character set as part of data type</a>. */
annotation|@
name|Test
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
name|void
name|testUnparseIn1
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
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
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPTNO\" = 21"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnparseIn2
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|20
argument_list|)
argument_list|,
name|b
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
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE \"DEPTNO\" IN (20, 21)"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnparseInStruct1
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"JOB"
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
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
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE ROW(\"DEPTNO\", \"JOB\") = ROW(1, 'PRESIDENT')"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnparseInStruct2
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|in
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"JOB"
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|1
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|2
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|"PRESIDENT"
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
name|expectedSql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"scott\".\"EMP\"\n"
operator|+
literal|"WHERE ROW(\"DEPTNO\", \"JOB\") IN (ROW(1, 'PRESIDENT'), ROW(2, 'PRESIDENT'))"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectQueryWithLimitClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\" limit 100 offset 10"
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
comment|/** Tests that we escape single-quotes in character literals using back-slash    * in BigQuery. The norm is to escape single-quotes with single-quotes. */
annotation|@
name|Test
name|void
name|testCharLiteralForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select 'that''s all folks!' from \"product\""
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT 'that''s all folks!'\n"
operator|+
literal|"FROM \"foodmart\".\"product\""
decl_stmt|;
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT 'that\\'s all folks!'\n"
operator|+
literal|"FROM foodmart.product"
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
operator|.
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIdentifier
parameter_list|()
block|{
comment|// Note that IGNORE is reserved in BigQuery but not in standard SQL
specifier|final
name|String
name|query
init|=
literal|"select *\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select 1 as \"one\", 2 as \"tWo\", 3 as \"THREE\",\n"
operator|+
literal|"    4 as \"fo$ur\", 5 as \"ignore\"\n"
operator|+
literal|"  from \"foodmart\".\"days\") as \"my$table\"\n"
operator|+
literal|"where \"one\"< \"tWo\" and \"THREE\"< \"fo$ur\""
decl_stmt|;
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT 1 AS one, 2 AS tWo, 3 AS THREE,"
operator|+
literal|" 4 AS `fo$ur`, 5 AS `ignore`\n"
operator|+
literal|"FROM foodmart.days) AS t\n"
operator|+
literal|"WHERE one< tWo AND THREE< `fo$ur`"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT 1 AS `one`, 2 AS `tWo`, 3 AS `THREE`,"
operator|+
literal|" 4 AS `fo$ur`, 5 AS `ignore`\n"
operator|+
literal|"FROM `foodmart`.`days`) AS `t`\n"
operator|+
literal|"WHERE `one`< `tWo` AND `THREE`< `fo$ur`"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT 1 AS \"one\", 2 AS \"tWo\", 3 AS \"THREE\","
operator|+
literal|" 4 AS \"fo$ur\", 5 AS \"ignore\"\n"
operator|+
literal|"FROM \"foodmart\".\"days\") AS \"t\"\n"
operator|+
literal|"WHERE \"one\"< \"tWo\" AND \"THREE\"< \"fo$ur\""
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
name|expectedPostgresql
operator|.
name|replace
argument_list|(
literal|" AS "
argument_list|,
literal|" "
argument_list|)
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
name|expectedBigQuery
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
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"UNION DISTINCT\n"
operator|+
literal|"SELECT 1\n"
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
name|void
name|testUnionAllOperatorForBigQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select mod(11,3) from \"product\"\n"
operator|+
literal|"UNION ALL select 1 from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT MOD(11, 3)\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 1\n"
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
literal|"INTERSECT DISTINCT\n"
operator|+
literal|"SELECT 1\n"
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
literal|"EXCEPT DISTINCT\n"
operator|+
literal|"SELECT 1\n"
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
name|void
name|testSelectOrderByDescNullsFirst
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
comment|// Hive and MSSQL do not support NULLS FIRST, so need to emulate
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
specifier|final
name|String
name|mssqlExpected
init|=
literal|"SELECT [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY CASE WHEN [product_id] IS NULL THEN 0 ELSE 1 END, [product_id] DESC"
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
operator|.
name|dialect
argument_list|(
name|MssqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|mssqlExpected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectOrderByAscNullsLast
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
comment|// Hive and MSSQL do not support NULLS LAST, so need to emulate
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
specifier|final
name|String
name|mssqlExpected
init|=
literal|"SELECT [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY CASE WHEN [product_id] IS NULL THEN 1 ELSE 0 END, [product_id]"
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
operator|.
name|dialect
argument_list|(
name|MssqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|mssqlExpected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectOrderByAscNullsFirst
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
comment|// Hive and MSSQL do not support NULLS FIRST, but nulls sort low, so no
comment|// need to emulate
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
specifier|final
name|String
name|mssqlExpected
init|=
literal|"SELECT [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY [product_id]"
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
operator|.
name|dialect
argument_list|(
name|MssqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|mssqlExpected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectOrderByDescNullsLast
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
comment|// Hive and MSSQL do not support NULLS LAST, but nulls sort low, so no
comment|// need to emulate
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
specifier|final
name|String
name|mssqlExpected
init|=
literal|"SELECT [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY [product_id] DESC"
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
operator|.
name|dialect
argument_list|(
name|MssqlSqlDialect
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|(
name|mssqlExpected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHiveSelectQueryWithOverDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY hire_date IS NULL DESC, hire_date DESC)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|void
name|testHiveSelectQueryWithOverAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY hire_date IS NULL, hire_date)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|void
name|testHiveSelectQueryWithOverAscNullsFirstShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY hire_date)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|void
name|testHiveSubstring
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT SUBSTRING('ABC', 2)"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUBSTRING('ABC', 2)\n"
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
annotation|@
name|Test
name|void
name|testHiveSubstringWithLength
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT SUBSTRING('ABC', 2, 3)"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUBSTRING('ABC', 2, 3)\n"
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
annotation|@
name|Test
name|void
name|testHiveSubstringWithANSI
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT SUBSTRING('ABC' FROM 2)"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUBSTRING('ABC', 2)\n"
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
annotation|@
name|Test
name|void
name|testHiveSubstringWithANSIAndLength
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT SUBSTRING('ABC' FROM 2 FOR 3)"
operator|+
literal|"from \"foodmart\".\"reserve_employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT SUBSTRING('ABC', 2, 3)\n"
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
annotation|@
name|Test
name|void
name|testHiveSelectQueryWithOverDescNullsLastShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY hire_date DESC)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|HiveSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|HiveSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|void
name|testHiveSelectQueryWithOverDescAndHighNullsWithVersionGreaterThanOrEq21
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
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY hire_date DESC NULLS FIRST)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|HiveSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|void
name|testHiveSelectQueryWithOverDescAndHighNullsWithVersion20
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
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER "
operator|+
literal|"(ORDER BY hire_date IS NULL DESC, hire_date DESC)\n"
operator|+
literal|"FROM foodmart.employee"
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
name|void
name|testJethroDataSelectQueryWithOverDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER "
operator|+
literal|"(ORDER BY \"hire_date\", \"hire_date\" DESC)\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
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
name|void
name|testMySqlSelectQueryWithOverDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER "
operator|+
literal|"(ORDER BY `hire_date` IS NULL DESC, `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlSelectQueryWithOverAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER "
operator|+
literal|"(ORDER BY `hire_date` IS NULL, `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlSelectQueryWithOverAscNullsFirstShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlSelectQueryWithOverDescNullsLastShouldNotAddNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlCastToVarcharWithLessThanMaxPrecision
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select cast(\"product_id\" as varchar(50)), \"product_id\" "
operator|+
literal|"from \"product\" "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(`product_id` AS CHAR(50)), `product_id`\n"
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
name|void
name|testMySqlCastToTimestamp
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select  * from \"employee\" where  \"hire_date\" - "
operator|+
literal|"INTERVAL '19800' SECOND(5)> cast(\"hire_date\" as TIMESTAMP) "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\nFROM `foodmart`.`employee`"
operator|+
literal|"\nWHERE (`hire_date` - INTERVAL '19800' SECOND)> CAST(`hire_date` AS DATETIME)"
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
name|void
name|testMySqlCastToVarcharWithGreaterThanMaxPrecision
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select cast(\"product_id\" as varchar(500)), \"product_id\" "
operator|+
literal|"from \"product\" "
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST(`product_id` AS CHAR(255)), `product_id`\n"
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
name|void
name|testMySqlWithHighNullsSelectWithOverAscNullsLastAndNoEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithHighNullsSelectWithOverAscNullsFirstAndNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL DESC, `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithHighNullsSelectWithOverDescNullsFirstAndNoEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithHighNullsSelectWithOverDescNullsLastAndNullEmulation
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL, `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithFirstNullsSelectWithOverDescAndNullsFirstShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithFirstNullsSelectWithOverAscAndNullsFirstShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithFirstNullsSelectWithOverDescAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL, `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithFirstNullsSelectWithOverAscAndNullsLastShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL, `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithLastNullsSelectWithOverDescAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL DESC, `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithLastNullsSelectWithOverAscAndNullsFirstShouldBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" nulls first) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() "
operator|+
literal|"OVER (ORDER BY `hire_date` IS NULL DESC, `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithLastNullsSelectWithOverDescAndNullsLastShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() "
operator|+
literal|"over (order by \"hire_date\" desc nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date` DESC)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testMySqlWithLastNullsSelectWithOverAscAndNullsLastShouldNotBeEmulated
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT row_number() over "
operator|+
literal|"(order by \"hire_date\" nulls last) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY `hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
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
name|void
name|testCastToVarchar
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select cast(\"product_id\" as varchar) from \"product\""
decl_stmt|;
specifier|final
name|String
name|expectedClickHouse
init|=
literal|"SELECT CAST(`product_id` AS `String`)\n"
operator|+
literal|"FROM `foodmart`.`product`"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT CAST(`product_id` AS CHAR)\n"
operator|+
literal|"FROM `foodmart`.`product`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
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
name|void
name|testSelectQueryWithLimitClauseWithoutOrder
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\" limit 100 offset 10"
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
specifier|final
name|String
name|expectedClickHouse
init|=
literal|"SELECT `product_id`\n"
operator|+
literal|"FROM `foodmart`.`product`\n"
operator|+
literal|"LIMIT 10, 100"
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
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"OFFSET 10\n"
operator|+
literal|"LIMIT 100"
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
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectQueryWithLimitOffsetClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"net_weight\" asc limit 100 offset 10"
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
comment|// BigQuery uses LIMIT/OFFSET, and nulls sort low by default
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT product_id, net_weight\n"
operator|+
literal|"FROM foodmart.product\n"
operator|+
literal|"ORDER BY net_weight IS NULL, net_weight\n"
operator|+
literal|"LIMIT 100\n"
operator|+
literal|"OFFSET 10"
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
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testSelectQueryWithFetchOffsetClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\" from \"product\"\n"
operator|+
literal|"order by \"product_id\" offset 10 rows fetch next 100 rows only"
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
name|void
name|testSelectQueryWithFetchClause
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_id\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"order by \"product_id\" fetch next 100 rows only"
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
literal|"FETCH NEXT 100 ROWS ONLY"
decl_stmt|;
specifier|final
name|String
name|expectedMssql10
init|=
literal|"SELECT TOP (100) [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY CASE WHEN [product_id] IS NULL THEN 1 ELSE 0 END, [product_id]"
decl_stmt|;
specifier|final
name|String
name|expectedMssql
init|=
literal|"SELECT [product_id]\n"
operator|+
literal|"FROM [foodmart].[product]\n"
operator|+
literal|"ORDER BY CASE WHEN [product_id] IS NULL THEN 1 ELSE 0 END, [product_id]\n"
operator|+
literal|"FETCH NEXT 100 ROWS ONLY"
decl_stmt|;
specifier|final
name|String
name|expectedSybase
init|=
literal|"SELECT TOP (100) product_id\n"
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
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withMssql
argument_list|(
literal|10
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedMssql10
argument_list|)
operator|.
name|withMssql
argument_list|(
literal|11
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedMssql
argument_list|)
operator|.
name|withMssql
argument_list|(
literal|14
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedMssql
argument_list|)
operator|.
name|withSybase
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedSybase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testSubQueryAlias
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select t1.\"customer_id\", t2.\"customer_id\"\n"
operator|+
literal|"from (select \"customer_id\" from \"sales_fact_1997\") as t1\n"
operator|+
literal|"inner join (select \"customer_id\" from \"sales_fact_1997\") t2\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4249">[CALCITE-4249]    * JDBC adapter cannot translate NOT LIKE in join condition</a>. */
annotation|@
name|Test
name|void
name|testJoinOnNotLike
parameter_list|()
block|{
specifier|final
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
init|=
name|b
lambda|->
name|b
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
name|b
operator|.
name|and
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|b
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
name|b
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
name|b
operator|.
name|not
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|,
name|b
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
name|b
operator|.
name|literal
argument_list|(
literal|"ACCOUNTING"
argument_list|)
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
literal|"AND \"DEPT\".\"DNAME\" NOT LIKE 'ACCOUNTING'"
decl_stmt|;
name|relFn
argument_list|(
name|relFn
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testCaseOnSubQuery
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT CASE WHEN v.g IN (0, 1) THEN 0 ELSE 1 END\n"
operator|+
literal|"FROM (SELECT * FROM \"foodmart\".\"customer\") AS c,\n"
operator|+
literal|"  (SELECT 0 AS g) AS v\n"
operator|+
literal|"GROUP BY v.g"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" CASE WHEN \"t0\".\"G\" IN (0, 1) THEN 0 ELSE 1 END\n"
operator|+
literal|"FROM (SELECT *\nFROM \"foodmart\".\"customer\") AS \"t\",\n"
operator|+
literal|"(VALUES (0)) AS \"t0\" (\"G\")\n"
operator|+
literal|"GROUP BY \"t0\".\"G\""
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4090">[CALCITE-4090]    * DB2 aliasing breaks with a complex SELECT above a sub-query</a>. */
annotation|@
name|Test
name|void
name|testDb2SubQueryAlias
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(foo), \"units_per_case\"\n"
operator|+
literal|"from (select \"units_per_case\", \"cases_per_pallet\",\n"
operator|+
literal|"      \"product_id\", 1 as foo\n"
operator|+
literal|"  from \"product\")\n"
operator|+
literal|"where \"cases_per_pallet\"> 100\n"
operator|+
literal|"group by \"product_id\", \"units_per_case\"\n"
operator|+
literal|"order by \"units_per_case\" desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*), t.units_per_case\n"
operator|+
literal|"FROM (SELECT product.units_per_case, product.cases_per_pallet, "
operator|+
literal|"product.product_id, 1 AS FOO\n"
operator|+
literal|"FROM foodmart.product AS product) AS t\n"
operator|+
literal|"WHERE t.cases_per_pallet> 100\n"
operator|+
literal|"GROUP BY t.product_id, t.units_per_case\n"
operator|+
literal|"ORDER BY t.units_per_case DESC"
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
name|void
name|testDb2SubQueryFromUnion
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select count(foo), \"units_per_case\"\n"
operator|+
literal|"from (select \"units_per_case\", \"cases_per_pallet\",\n"
operator|+
literal|"      \"product_id\", 1 as foo\n"
operator|+
literal|"  from \"product\"\n"
operator|+
literal|"  where \"cases_per_pallet\"> 100\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select \"units_per_case\", \"cases_per_pallet\",\n"
operator|+
literal|"      \"product_id\", 1 as foo\n"
operator|+
literal|"  from \"product\"\n"
operator|+
literal|"  where \"cases_per_pallet\"< 100)\n"
operator|+
literal|"where \"cases_per_pallet\"> 100\n"
operator|+
literal|"group by \"product_id\", \"units_per_case\"\n"
operator|+
literal|"order by \"units_per_case\" desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(*), t3.units_per_case\n"
operator|+
literal|"FROM (SELECT product.units_per_case, product.cases_per_pallet, "
operator|+
literal|"product.product_id, 1 AS FOO\n"
operator|+
literal|"FROM foodmart.product AS product\n"
operator|+
literal|"WHERE product.cases_per_pallet> 100\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT product0.units_per_case, product0.cases_per_pallet, "
operator|+
literal|"product0.product_id, 1 AS FOO\n"
operator|+
literal|"FROM foodmart.product AS product0\n"
operator|+
literal|"WHERE product0.cases_per_pallet< 100) AS t3\n"
operator|+
literal|"WHERE t3.cases_per_pallet> 100\n"
operator|+
literal|"GROUP BY t3.product_id, t3.units_per_case\n"
operator|+
literal|"ORDER BY t3.units_per_case DESC"
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
comment|// The hook prevents RelBuilder from removing "FALSE AND FALSE" and such
try|try
init|(
name|Hook
operator|.
name|Closeable
name|ignore
init|=
name|Hook
operator|.
name|REL_BUILDER_SIMPLIFY
operator|.
name|addThread
argument_list|(
name|Hook
operator|.
name|propertyJ
argument_list|(
literal|false
argument_list|)
argument_list|)
init|)
block|{
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
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1586">[CALCITE-1586]    * JDBC adapter generates wrong SQL if UNION has more than two inputs</a>. */
annotation|@
name|Test
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
name|RuleSet
name|rules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|CoreRules
operator|.
name|UNION_MERGE
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
literal|null
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
literal|"FROM (VALUES ("
operator|+
name|expected
operator|+
literal|")) AS t (EXPR$0)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2625">[CALCITE-2625]    * Removing Window Boundaries from SqlWindow of Aggregate Function which do    * not allow Framing</a>. */
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3112">[CALCITE-3112]    * Support Window in RelToSqlConverter</a>. */
annotation|@
name|Test
name|void
name|testConvertWindowToSql
parameter_list|()
block|{
name|String
name|query0
init|=
literal|"SELECT row_number() over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected0
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY \"hire_date\") AS \"$0\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query1
init|=
literal|"SELECT rank() over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected1
init|=
literal|"SELECT RANK() OVER (ORDER BY \"hire_date\") AS \"$0\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query2
init|=
literal|"SELECT lead(\"employee_id\",1,'NA') over "
operator|+
literal|"(partition by \"hire_date\" order by \"employee_id\")\n"
operator|+
literal|"FROM \"employee\""
decl_stmt|;
name|String
name|expected2
init|=
literal|"SELECT LEAD(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" "
operator|+
literal|"ORDER BY \"employee_id\") AS \"$0\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query3
init|=
literal|"SELECT lag(\"employee_id\",1,'NA') over "
operator|+
literal|"(partition by \"hire_date\" order by \"employee_id\")\n"
operator|+
literal|"FROM \"employee\""
decl_stmt|;
name|String
name|expected3
init|=
literal|"SELECT LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" ORDER BY \"employee_id\") AS \"$0\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query4
init|=
literal|"SELECT lag(\"employee_id\",1,'NA') "
operator|+
literal|"over (partition by \"hire_date\" order by \"employee_id\") as lag1, "
operator|+
literal|"lag(\"employee_id\",1,'NA') "
operator|+
literal|"over (partition by \"birth_date\" order by \"employee_id\") as lag2, "
operator|+
literal|"count(*) over (partition by \"hire_date\" order by \"employee_id\") as count1, "
operator|+
literal|"count(*) over (partition by \"birth_date\" order by \"employee_id\") as count2\n"
operator|+
literal|"FROM \"employee\""
decl_stmt|;
name|String
name|expected4
init|=
literal|"SELECT LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" ORDER BY \"employee_id\") AS \"$0\", "
operator|+
literal|"LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"birth_date\" ORDER BY \"employee_id\") AS \"$1\", "
operator|+
literal|"COUNT(*) OVER (PARTITION BY \"hire_date\" ORDER BY \"employee_id\" "
operator|+
literal|"RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS \"$2\", "
operator|+
literal|"COUNT(*) OVER (PARTITION BY \"birth_date\" ORDER BY \"employee_id\" "
operator|+
literal|"RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS \"$3\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query5
init|=
literal|"SELECT lag(\"employee_id\",1,'NA') "
operator|+
literal|"over (partition by \"hire_date\" order by \"employee_id\") as lag1, "
operator|+
literal|"lag(\"employee_id\",1,'NA') "
operator|+
literal|"over (partition by \"birth_date\" order by \"employee_id\") as lag2, "
operator|+
literal|"max(sum(\"employee_id\")) over (partition by \"hire_date\" order by \"employee_id\") as count1, "
operator|+
literal|"max(sum(\"employee_id\")) over (partition by \"birth_date\" order by \"employee_id\") as count2\n"
operator|+
literal|"FROM \"employee\" group by \"employee_id\", \"hire_date\", \"birth_date\""
decl_stmt|;
name|String
name|expected5
init|=
literal|"SELECT LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"hire_date\" ORDER BY \"employee_id\") AS \"$0\", "
operator|+
literal|"LAG(\"employee_id\", 1, 'NA') OVER "
operator|+
literal|"(PARTITION BY \"birth_date\" ORDER BY \"employee_id\") AS \"$1\", "
operator|+
literal|"MAX(SUM(\"employee_id\")) OVER (PARTITION BY \"hire_date\" ORDER BY \"employee_id\" "
operator|+
literal|"RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS \"$2\", "
operator|+
literal|"MAX(SUM(\"employee_id\")) OVER (PARTITION BY \"birth_date\" ORDER BY \"employee_id\" "
operator|+
literal|"RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS \"$3\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"GROUP BY \"employee_id\", \"hire_date\", \"birth_date\""
decl_stmt|;
name|String
name|query6
init|=
literal|"SELECT lag(\"employee_id\",1,'NA') over "
operator|+
literal|"(partition by \"hire_date\" order by \"employee_id\"), \"hire_date\"\n"
operator|+
literal|"FROM \"employee\"\n"
operator|+
literal|"group by \"hire_date\", \"employee_id\""
decl_stmt|;
name|String
name|expected6
init|=
literal|"SELECT LAG(\"employee_id\", 1, 'NA') "
operator|+
literal|"OVER (PARTITION BY \"hire_date\" ORDER BY \"employee_id\"), \"hire_date\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"GROUP BY \"hire_date\", \"employee_id\""
decl_stmt|;
name|String
name|query7
init|=
literal|"SELECT "
operator|+
literal|"count(distinct \"employee_id\") over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected7
init|=
literal|"SELECT "
operator|+
literal|"COUNT(DISTINCT \"employee_id\") "
operator|+
literal|"OVER (ORDER BY \"hire_date\" RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS \"$0\""
operator|+
literal|"\nFROM \"foodmart\".\"employee\""
decl_stmt|;
name|String
name|query8
init|=
literal|"SELECT "
operator|+
literal|"sum(distinct \"position_id\") over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expected8
init|=
literal|"SELECT CASE WHEN (COUNT(DISTINCT \"position_id\") OVER (ORDER BY \"hire_date\" "
operator|+
literal|"RANGE"
operator|+
literal|" BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW))> 0 THEN COALESCE(SUM(DISTINCT "
operator|+
literal|"\"position_id\") OVER (ORDER BY \"hire_date\" RANGE BETWEEN UNBOUNDED "
operator|+
literal|"PRECEDING AND CURRENT ROW), 0) ELSE NULL END\n"
operator|+
literal|"FROM \"foodmart\".\"employee\""
decl_stmt|;
name|HepProgramBuilder
name|builder
init|=
operator|new
name|HepProgramBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addRuleClass
argument_list|(
name|ProjectToWindowRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|RuleSet
name|rules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_LOGICAL_PROJECT_AND_WINDOW
argument_list|)
decl_stmt|;
name|sql
argument_list|(
name|query0
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query1
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query2
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query3
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query4
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected4
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query5
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected5
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query6
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected6
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query7
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected7
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|query8
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expected8
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3866">[CALCITE-3866]    * "numeric field overflow" when running the generated SQL in PostgreSQL</a>.    */
annotation|@
name|Test
name|void
name|testSumReturnType
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select sum(e1.\"store_sales\"), sum(e2.\"store_sales\") from \"sales_fact_dec_1998\" as "
operator|+
literal|"e1 , \"sales_fact_dec_1998\" as e2 where e1.\"product_id\" = e2.\"product_id\""
decl_stmt|;
name|String
name|expect
init|=
literal|"SELECT SUM(CAST(\"t\".\"EXPR$0\" * \"t0\".\"$f1\" AS DECIMAL"
operator|+
literal|"(19, 4))), SUM(CAST(\"t\".\"$f2\" * \"t0\".\"EXPR$1\" AS DECIMAL(19, 4)))\n"
operator|+
literal|"FROM (SELECT \"product_id\", SUM(\"store_sales\") AS \"EXPR$0\", COUNT(*) AS \"$f2\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_dec_1998\"\n"
operator|+
literal|"GROUP BY \"product_id\") AS \"t\"\n"
operator|+
literal|"INNER JOIN "
operator|+
literal|"(SELECT \"product_id\", COUNT(*) AS \"$f1\", SUM(\"store_sales\") AS \"EXPR$1\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_dec_1998\"\n"
operator|+
literal|"GROUP BY \"product_id\") AS \"t0\" ON \"t\".\"product_id\" = \"t0\".\"product_id\""
decl_stmt|;
name|HepProgramBuilder
name|builder
init|=
operator|new
name|HepProgramBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addRuleClass
argument_list|(
name|FilterJoinRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addRuleClass
argument_list|(
name|AggregateProjectMergeRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addRuleClass
argument_list|(
name|AggregateJoinTransposeRule
operator|.
name|class
argument_list|)
expr_stmt|;
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|RuleSet
name|rules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|CoreRules
operator|.
name|FILTER_INTO_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_CONDITION_PUSH
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_PROJECT_MERGE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_JOIN_TRANSPOSE_EXTENDED
argument_list|)
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withPostgresql
argument_list|()
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
name|hepPlanner
argument_list|)
operator|.
name|ok
argument_list|(
name|expect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3876">[CALCITE-3876]    * RelToSqlConverter should not combine Projects when top Project contains    * window function referencing window function from bottom Project</a>. */
annotation|@
name|Test
name|void
name|testWindowOnWindowDoesNotCombineProjects
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY rn)\n"
operator|+
literal|"FROM (SELECT *,\n"
operator|+
literal|"  ROW_NUMBER() OVER (ORDER BY \"product_id\") as rn\n"
operator|+
literal|"  FROM \"foodmart\".\"product\")"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT ROW_NUMBER() OVER (ORDER BY \"RN\")\n"
operator|+
literal|"FROM (SELECT \"product_class_id\", \"product_id\", \"brand_name\","
operator|+
literal|" \"product_name\", \"SKU\", \"SRP\", \"gross_weight\","
operator|+
literal|" \"net_weight\", \"recyclable_package\", \"low_fat\","
operator|+
literal|" \"units_per_case\", \"cases_per_pallet\", \"shelf_width\","
operator|+
literal|" \"shelf_height\", \"shelf_depth\","
operator|+
literal|" ROW_NUMBER() OVER (ORDER BY \"product_id\") AS \"RN\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\") AS \"t\""
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1798">[CALCITE-1798]    * Generate dialect-specific SQL for FLOOR operator</a>. */
annotation|@
name|Test
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
name|void
name|testFloorClickHouse
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
literal|"SELECT toStartOfMinute(`hire_date`)\nFROM `foodmart`.`employee`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withClickHouse
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
name|void
name|testFloorPresto
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
name|withPresto
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
name|void
name|testFloorWeek
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO WEEK) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expectedClickHouse
init|=
literal|"SELECT toMonday(`hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
decl_stmt|;
specifier|final
name|String
name|expectedMssql
init|=
literal|"SELECT CONVERT(DATETIME, CONVERT(VARCHAR(10), "
operator|+
literal|"DATEADD(day, - (6 + DATEPART(weekday, [hire_date] )) % 7, [hire_date] ), 126))\n"
operator|+
literal|"FROM [foodmart].[employee]"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT STR_TO_DATE(DATE_FORMAT(`hire_date` , '%x%v-1'), "
operator|+
literal|"'%x%v-%w')\n"
operator|+
literal|"FROM `foodmart`.`employee`"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMssql
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
name|void
name|testUnparseSqlIntervalQualifierBigQuery
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
literal|"FROM foodmart.employee\n"
operator|+
literal|"WHERE (hire_date - INTERVAL 19800 SECOND)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|withBigQuery
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
literal|"FROM foodmart.employee\n"
operator|+
literal|"WHERE (hire_date + INTERVAL 10 HOUR)"
operator|+
literal|"> TIMESTAMP '2005-10-17 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|withBigQuery
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
literal|"INTERVAL '1 2:34:56.78' DAY TO SECOND> TIMESTAMP '2005-10-17 00:00:00' "
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withBigQuery
argument_list|()
operator|.
name|throws_
argument_list|(
literal|"Only INT64 is supported as the interval value for BigQuery."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|void
name|testFloorMonth
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT floor(\"hire_date\" TO MONTH) FROM \"employee\""
decl_stmt|;
specifier|final
name|String
name|expectedClickHouse
init|=
literal|"SELECT toStartOfMonth(`hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`"
decl_stmt|;
specifier|final
name|String
name|expectedMssql
init|=
literal|"SELECT CONVERT(DATETIME, CONVERT(VARCHAR(7), [hire_date] , "
operator|+
literal|"126)+'-01')\n"
operator|+
literal|"FROM [foodmart].[employee]"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
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
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMssql
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
name|expectedClickHouse
init|=
literal|"SELECT toStartOfMinute(`hire_date`)\n"
operator|+
literal|"FROM `foodmart`.`employee`\n"
operator|+
literal|"GROUP BY toStartOfMinute(`hire_date`)"
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
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
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
name|expectedClickHouse
init|=
literal|"SELECT substring(`brand_name`, 2)\n"
operator|+
literal|"FROM `foodmart`.`product`"
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
name|expectedPresto
init|=
literal|"SELECT SUBSTR(\"brand_name\", 2)\n"
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
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
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
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
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
name|expectedClickHouse
init|=
literal|"SELECT substring(`brand_name`, 2, 3)\n"
operator|+
literal|"FROM `foodmart`.`product`"
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
name|expectedPresto
init|=
literal|"SELECT SUBSTR(\"brand_name\", 2, 3)\n"
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
name|withClickHouse
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedClickHouse
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
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
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
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
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
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
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
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
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
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
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
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
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
name|void
name|testIlike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" ilike 'abC'"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" ILIKE 'abC'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
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
name|void
name|testRlike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" rlike '.+@.+\\\\..+'"
decl_stmt|;
name|String
name|expectedSpark
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" RLIKE '.+@.+\\\\..+'"
decl_stmt|;
name|String
name|expectedHive
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" RLIKE '.+@.+\\\\..+'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSpark
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|HIVE
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedHive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotRlike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" not rlike '.+@.+\\\\..+'"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" NOT RLIKE '.+@.+\\\\..+'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
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
name|void
name|testNotIlike
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select \"product_name\" from \"product\" a "
operator|+
literal|"where \"product_name\" not ilike 'abC'"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"product_name\" NOT ILIKE 'abC'"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
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
literal|"    partition by \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"    order by \"product_class_id\" asc, \"brand_name\" desc\n"
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
literal|"    partition by \"product_class_id\", \"brand_name\"\n"
operator|+
literal|"    order by \"product_class_id\" asc, \"brand_name\" desc\n"
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
literal|"FROM (VALUES (1, 'x '),\n"
operator|+
literal|"(2, 'yy')) AS t (a, b)"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT `a`\n"
operator|+
literal|"FROM (SELECT 1 AS `a`, 'x ' AS `b`\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2 AS `a`, 'yy' AS `b`) AS `t`"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT \"a\"\n"
operator|+
literal|"FROM (VALUES (1, 'x '),\n"
operator|+
literal|"(2, 'yy')) AS \"t\" (\"a\", \"b\")"
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
name|expectedHive
init|=
literal|"SELECT a\n"
operator|+
literal|"FROM (SELECT 1 a, 'x ' b\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2 a, 'yy' b)"
decl_stmt|;
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT a\n"
operator|+
literal|"FROM (SELECT 1 AS a, 'x ' AS b\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2 AS a, 'yy' AS b)"
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
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysql
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
name|withHive
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedHive
argument_list|)
operator|.
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBigQuery
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
annotation|@
name|Test
name|void
name|testValuesEmpty
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (values (1, 'a'), (2, 'bb')) as t(x, y)\n"
operator|+
literal|"limit 0"
decl_stmt|;
specifier|final
name|RuleSet
name|rules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|PruneEmptyRules
operator|.
name|SORT_FETCH_ZERO_INSTANCE
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT NULL AS `X`, NULL AS `Y`) AS `t`\n"
operator|+
literal|"WHERE 1 = 0"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"SELECT NULL \"X\", NULL \"Y\"\n"
operator|+
literal|"FROM \"DUAL\"\n"
operator|+
literal|"WHERE 1 = 0"
decl_stmt|;
specifier|final
name|String
name|expectedPostgresql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (VALUES (NULL, NULL)) AS \"t\" (\"X\", \"Y\")\n"
operator|+
literal|"WHERE 1 = 0"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|optimize
argument_list|(
name|rules
argument_list|,
literal|null
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
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3840">[CALCITE-3840]    * Re-aliasing of VALUES that has column aliases produces wrong SQL in the    * JDBC adapter</a>. */
annotation|@
name|Test
name|void
name|testValuesReAlias
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
name|values
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|1
argument_list|,
literal|"x "
argument_list|,
literal|2
argument_list|,
literal|"yy"
argument_list|)
operator|.
name|values
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|1
argument_list|,
literal|"x "
argument_list|,
literal|2
argument_list|,
literal|"yy"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|FULL
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"a"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT \"t\".\"a\"\n"
operator|+
literal|"FROM (VALUES (1, 'x '),\n"
operator|+
literal|"(2, 'yy')) AS \"t\" (\"a\", \"b\")\n"
operator|+
literal|"FULL JOIN (VALUES (1, 'x '),\n"
operator|+
literal|"(2, 'yy')) AS \"t0\" (\"a\", \"b\") ON TRUE"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now with indentation.
specifier|final
name|String
name|expectedSql2
init|=
literal|"SELECT \"t\".\"a\"\n"
operator|+
literal|"FROM (VALUES (1, 'x '),\n"
operator|+
literal|"        (2, 'yy')) AS \"t\" (\"a\", \"b\")\n"
operator|+
literal|"  FULL JOIN (VALUES (1, 'x '),\n"
operator|+
literal|"        (2, 'yy')) AS \"t0\" (\"a\", \"b\") ON TRUE"
decl_stmt|;
name|assertThat
argument_list|(
name|toSql
argument_list|(
name|root
argument_list|,
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
argument_list|,
name|c
lambda|->
name|c
operator|.
name|withIndentation
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|isLinux
argument_list|(
name|expectedSql2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFromEmulationForHiveAndBigQuery
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select 2 + 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT 2 + 2"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2118">[CALCITE-2118]    * RelToSqlConverter should only generate "*" if field names match</a>. */
annotation|@
name|Test
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
name|void
name|testCorrelate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select d.\"department_id\", d_plusOne "
operator|+
literal|"from \"department\" as d, "
operator|+
literal|"       lateral (select d.\"department_id\" + 1 as d_plusOne"
operator|+
literal|"                from (values(true)))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"$cor0\".\"department_id\", \"$cor0\".\"D_PLUSONE\"\n"
operator|+
literal|"FROM \"foodmart\".\"department\" AS \"$cor0\",\n"
operator|+
literal|"LATERAL (SELECT \"$cor0\".\"department_id\" + 1 AS \"D_PLUSONE\"\n"
operator|+
literal|"FROM (VALUES (TRUE)) AS \"t\" (\"EXPR$0\")) AS \"t0\""
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3651">[CALCITE-3651]    * NullPointerException when convert relational algebra that correlates TableFunctionScan</a>. */
annotation|@
name|Test
name|void
name|testLateralCorrelate
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select * from \"product\",\n"
operator|+
literal|"lateral table(RAMP(\"product\".\"product_id\"))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM \"foodmart\".\"product\" AS \"$cor0\",\n"
operator|+
literal|"LATERAL (SELECT *\n"
operator|+
literal|"FROM TABLE(RAMP(\"$cor0\".\"product_id\"))) AS \"t\""
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
name|void
name|testUncollectExplicitAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select did + 1\n"
operator|+
literal|"from unnest(select collect(\"department_id\") as deptid"
operator|+
literal|"            from \"department\") as t(did)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"DEPTID\" + 1\n"
operator|+
literal|"FROM UNNEST (SELECT COLLECT(\"department_id\") AS \"DEPTID\"\n"
operator|+
literal|"FROM \"foodmart\".\"department\") AS \"t0\" (\"DEPTID\")"
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
name|void
name|testUncollectImplicitAlias
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select did + 1\n"
operator|+
literal|"from unnest(select collect(\"department_id\") "
operator|+
literal|"            from \"department\") as t(did)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"col_0\" + 1\n"
operator|+
literal|"FROM UNNEST (SELECT COLLECT(\"department_id\")\n"
operator|+
literal|"FROM \"foodmart\".\"department\") AS \"t0\" (\"col_0\")"
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
name|void
name|testWithinGroup4
parameter_list|()
block|{
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
literal|"FILTER (WHERE \"net_weight\"> 0 IS TRUE) "
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
name|void
name|testJsonValue
parameter_list|()
block|{
name|String
name|query
init|=
literal|"select json_value(\"product_name\", 'lax $') from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT JSON_VALUE(\"product_name\", 'lax $')\n"
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
name|void
name|testCubeWithGroupBy
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY CUBE(\"product_id\", \"product_class_id\")"
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRollupWithGroupBy
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
specifier|final
name|String
name|expectedPresto
init|=
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"GROUP BY ROLLUP(\"product_id\", \"product_class_id\")"
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
operator|.
name|withPresto
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedPresto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
literal|"INNER JOIN (SELECT \"t1\".\"department_id\" \"department_id0\", MIN(\"t1\".\"department_id\") \"EXPR$0\"\n"
operator|+
literal|"FROM (SELECT NULL \"department_id\", NULL \"department_description\"\nFROM \"DUAL\"\nWHERE 1 = 0) \"t\",\n"
operator|+
literal|"(SELECT \"department_id\"\nFROM \"foodmart\".\"employee\"\nGROUP BY \"department_id\") \"t1\"\n"
operator|+
literal|"GROUP BY \"t1\".\"department_id\") \"t3\" ON \"employee\".\"department_id\" = \"t3\".\"department_id0\""
operator|+
literal|" AND \"employee\".\"department_id\" = \"t3\".\"EXPR$0\""
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
literal|" MIN(\"t1\".\"department_id\") AS \"EXPR$0\"\n"
operator|+
literal|"FROM (SELECT *\nFROM (VALUES (NULL, NULL))"
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
literal|" AND \"employee\".\"department_id\" = \"t3\".\"EXPR$0\""
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
literal|"SELECT TO_DATE('1978-05-02', 'YYYY-MM-DD')\n"
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
literal|"SELECT TO_TIMESTAMP('1978-05-02 12:34:56.78',"
operator|+
literal|" 'YYYY-MM-DD HH24:MI:SS.FF')\n"
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
literal|"SELECT TO_TIME('12:34:56.78', 'HH24:MI:SS.FF')\n"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4150">[CALCITE-4150]    * JDBC adapter throws UnsupportedOperationException when generating SQL    * for untyped NULL literal</a>. */
annotation|@
name|Test
name|void
name|testSelectRawNull
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT NULL FROM \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT NULL\n"
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
name|void
name|testSelectRawNullWithAlias
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT NULL AS DUMMY FROM \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT NULL AS \"DUMMY\"\n"
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
name|void
name|testSelectNullWithCast
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT CAST(NULL AS INT)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (VALUES (NULL)) AS \"t\" (\"EXPR$0\")"
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithCount
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT COUNT(CAST(NULL AS INT))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(\"$f0\")\n"
operator|+
literal|"FROM (VALUES (NULL)) AS \"t\" (\"$f0\")"
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithGroupByNull
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT COUNT(CAST(NULL AS INT))\n"
operator|+
literal|"FROM (VALUES  (0))AS \"t\"\n"
operator|+
literal|"GROUP BY CAST(NULL AS VARCHAR CHARACTER SET \"ISO-8859-1\")"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(\"$f1\")\n"
operator|+
literal|"FROM (VALUES (NULL, NULL)) AS \"t\" (\"$f0\", \"$f1\")\n"
operator|+
literal|"GROUP BY \"$f0\""
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithGroupByVar
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT COUNT(CAST(NULL AS INT))\n"
operator|+
literal|"FROM \"account\" AS \"t\"\n"
operator|+
literal|"GROUP BY \"account_type\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT COUNT(CAST(NULL AS INTEGER))\n"
operator|+
literal|"FROM \"foodmart\".\"account\"\n"
operator|+
literal|"GROUP BY \"account_type\""
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithInsert
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"insert into\n"
operator|+
literal|"\"account\"(\"account_id\",\"account_parent\",\"account_type\",\"account_rollup\")\n"
operator|+
literal|"select 1, cast(NULL AS INT), cast(123 as varchar), cast(123 as varchar)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO \"foodmart\".\"account\" ("
operator|+
literal|"\"account_id\", \"account_parent\", \"account_description\", "
operator|+
literal|"\"account_type\", \"account_rollup\", \"Custom_Members\")\n"
operator|+
literal|"(SELECT \"EXPR$0\" AS \"account_id\","
operator|+
literal|" \"EXPR$1\" AS \"account_parent\","
operator|+
literal|" CAST(NULL AS VARCHAR(30) CHARACTER SET \"ISO-8859-1\") "
operator|+
literal|"AS \"account_description\","
operator|+
literal|" \"EXPR$2\" AS \"account_type\","
operator|+
literal|" \"EXPR$3\" AS \"account_rollup\","
operator|+
literal|" CAST(NULL AS VARCHAR(255) CHARACTER SET \"ISO-8859-1\") "
operator|+
literal|"AS \"Custom_Members\"\n"
operator|+
literal|"FROM (VALUES (1, NULL, '123', '123')) "
operator|+
literal|"AS \"t\" (\"EXPR$0\", \"EXPR$1\", \"EXPR$2\", \"EXPR$3\"))"
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNullWithInsertFromJoin
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"insert into\n"
operator|+
literal|"\"account\"(\"account_id\",\"account_parent\",\n"
operator|+
literal|"\"account_type\",\"account_rollup\")\n"
operator|+
literal|"select \"product\".\"product_id\",\n"
operator|+
literal|"cast(NULL AS INT),\n"
operator|+
literal|"cast(\"product\".\"product_id\" as varchar),\n"
operator|+
literal|"cast(\"sales_fact_1997\".\"store_id\" as varchar)\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"inner join \"sales_fact_1997\"\n"
operator|+
literal|"on \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO \"foodmart\".\"account\" "
operator|+
literal|"(\"account_id\", \"account_parent\", \"account_description\", "
operator|+
literal|"\"account_type\", \"account_rollup\", \"Custom_Members\")\n"
operator|+
literal|"(SELECT \"product\".\"product_id\" AS \"account_id\", "
operator|+
literal|"CAST(NULL AS INTEGER) AS \"account_parent\", CAST(NULL AS VARCHAR"
operator|+
literal|"(30) CHARACTER SET \"ISO-8859-1\") AS \"account_description\", "
operator|+
literal|"CAST(\"product\".\"product_id\" AS VARCHAR CHARACTER SET "
operator|+
literal|"\"ISO-8859-1\") AS \"account_type\", "
operator|+
literal|"CAST(\"sales_fact_1997\".\"store_id\" AS VARCHAR CHARACTER SET \"ISO-8859-1\") AS "
operator|+
literal|"\"account_rollup\", "
operator|+
literal|"CAST(NULL AS VARCHAR(255) CHARACTER SET \"ISO-8859-1\") AS \"Custom_Members\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"INNER JOIN \"foodmart\".\"sales_fact_1997\" "
operator|+
literal|"ON \"product\".\"product_id\" = \"sales_fact_1997\".\"product_id\")"
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
comment|// validate
name|sql
argument_list|(
name|expected
argument_list|)
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastDecimalOverflow
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT CAST('11111111111111111111111111111111.111111' AS DECIMAL(38,6)) AS \"num\" from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT CAST('11111111111111111111111111111111.111111' AS DECIMAL(19, 6)) AS \"num\"\n"
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
specifier|final
name|String
name|query2
init|=
literal|"SELECT CAST(1111111 AS DECIMAL(5,2)) AS \"num\" from \"product\""
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT CAST(1111111 AS DECIMAL(5, 2)) AS \"num\"\nFROM \"foodmart\".\"product\""
decl_stmt|;
name|sql
argument_list|(
name|query2
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
name|void
name|testCastInStringIntegerComparison
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select \"employee_id\" "
operator|+
literal|"from \"foodmart\".\"employee\" "
operator|+
literal|"where 10 = cast('10' as int) and \"birth_date\" = cast('1914-02-02' as date) or "
operator|+
literal|"\"hire_date\" = cast('1996-01-01 '||'00:00:00' as timestamp)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT \"employee_id\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"WHERE 10 = '10' AND \"birth_date\" = '1914-02-02' OR \"hire_date\" = '1996-01-01 ' || "
operator|+
literal|"'00:00:00'"
decl_stmt|;
specifier|final
name|String
name|expectedBiqquery
init|=
literal|"SELECT employee_id\n"
operator|+
literal|"FROM foodmart.employee\n"
operator|+
literal|"WHERE 10 = CAST('10' AS INT64) AND birth_date = '1914-02-02' OR hire_date = "
operator|+
literal|"CAST('1996-01-01 ' || '00:00:00' AS TIMESTAMP)"
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
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedBiqquery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDialectQuoteStringLiteral
parameter_list|()
block|{
name|dialects
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|dialect
parameter_list|,
name|databaseProduct
parameter_list|)
lambda|->
block|{
name|assertThat
argument_list|(
name|dialect
operator|.
name|quoteStringLiteral
argument_list|(
literal|""
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"''"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|dialect
operator|.
name|quoteStringLiteral
argument_list|(
literal|"can't run"
argument_list|)
argument_list|,
name|databaseProduct
operator|==
name|DatabaseProduct
operator|.
name|BIG_QUERY
condition|?
name|is
argument_list|(
literal|"'can\\'t run'"
argument_list|)
else|:
name|is
argument_list|(
literal|"'can''t run'"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|dialect
operator|.
name|unquoteStringLiteral
argument_list|(
literal|"''"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|databaseProduct
operator|==
name|DatabaseProduct
operator|.
name|BIG_QUERY
condition|)
block|{
name|assertThat
argument_list|(
name|dialect
operator|.
name|unquoteStringLiteral
argument_list|(
literal|"'can\\'t run'"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"can't run"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|dialect
operator|.
name|unquoteStringLiteral
argument_list|(
literal|"'can't run'"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"can't run"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectCountStar
parameter_list|()
block|{
specifier|final
name|String
name|query
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
name|Sql
name|sql
init|=
name|sql
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|sql
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowValueExpression
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"insert into \"DEPT\"\n"
operator|+
literal|"values ROW(1,'Fred', 'San Francisco'),\n"
operator|+
literal|"  ROW(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedDefault
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedDefaultX
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"SELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")"
decl_stmt|;
specifier|final
name|String
name|expectedHive
init|=
literal|"INSERT INTO SCOTT.DEPT (DEPTNO, DNAME, LOC)\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedHiveX
init|=
literal|"INSERT INTO SCOTT.DEPT (DEPTNO, DNAME, LOC)\n"
operator|+
literal|"SELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'"
decl_stmt|;
specifier|final
name|String
name|expectedMysql
init|=
literal|"INSERT INTO `SCOTT`.`DEPT`"
operator|+
literal|" (`DEPTNO`, `DNAME`, `LOC`)\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedMysqlX
init|=
literal|"INSERT INTO `SCOTT`.`DEPT`"
operator|+
literal|" (`DEPTNO`, `DNAME`, `LOC`)\nSELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'"
decl_stmt|;
specifier|final
name|String
name|expectedOracle
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedOracleX
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"SELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"FROM \"DUAL\"\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'\n"
operator|+
literal|"FROM \"DUAL\""
decl_stmt|;
specifier|final
name|String
name|expectedMssql
init|=
literal|"INSERT INTO [SCOTT].[DEPT]"
operator|+
literal|" ([DEPTNO], [DNAME], [LOC])\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedMssqlX
init|=
literal|"INSERT INTO [SCOTT].[DEPT]"
operator|+
literal|" ([DEPTNO], [DNAME], [LOC])\n"
operator|+
literal|"SELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"FROM (VALUES (0)) AS [t] ([ZERO])\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'\n"
operator|+
literal|"FROM (VALUES (0)) AS [t] ([ZERO])"
decl_stmt|;
specifier|final
name|String
name|expectedCalcite
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"VALUES (1, 'Fred', 'San Francisco'),\n"
operator|+
literal|"(2, 'Eric', 'Washington')"
decl_stmt|;
specifier|final
name|String
name|expectedCalciteX
init|=
literal|"INSERT INTO \"SCOTT\".\"DEPT\""
operator|+
literal|" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"SELECT 1, 'Fred', 'San Francisco'\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT 2, 'Eric', 'Washington'\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")"
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
name|expectedDefault
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedHive
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
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracle
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMssql
argument_list|)
operator|.
name|withCalcite
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedCalcite
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withRelBuilderConfigTransform
argument_list|(
name|b
lambda|->
name|b
operator|.
name|withSimplifyValues
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withCalcite
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedDefaultX
argument_list|)
operator|.
name|withHive
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedHiveX
argument_list|)
operator|.
name|withMysql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMysqlX
argument_list|)
operator|.
name|withOracle
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedOracleX
argument_list|)
operator|.
name|withMssql
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedMssqlX
argument_list|)
operator|.
name|withCalcite
argument_list|()
operator|.
name|ok
argument_list|(
name|expectedCalciteX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertValuesWithDynamicParams
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into \"DEPT\" values (?,?,?), (?,?,?)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"INSERT INTO \"SCOTT\".\"DEPT\" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"SELECT ?, ?, ?\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT ?, ?, ?\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")"
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
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertValuesWithExplicitColumnsAndDynamicParams
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"insert into \"DEPT\" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"values (?,?,?), (?,?,?)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"INSERT INTO \"SCOTT\".\"DEPT\" (\"DEPTNO\", \"DNAME\", \"LOC\")\n"
operator|+
literal|"SELECT ?, ?, ?\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT ?, ?, ?\n"
operator|+
literal|"FROM (VALUES (0)) AS \"t\" (\"ZERO\")"
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
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableFunctionScan
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(DEDUP(CURSOR(select \"product_id\", \"product_name\"\n"
operator|+
literal|"from \"product\"), CURSOR(select  \"employee_id\", \"full_name\"\n"
operator|+
literal|"from \"employee\"), 'NAME'))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(DEDUP(CURSOR ((SELECT \"product_id\", \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\")), CURSOR ((SELECT \"employee_id\", \"full_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\")), 'NAME'))"
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
specifier|final
name|String
name|query2
init|=
literal|"select * from table(ramp(3))"
decl_stmt|;
name|sql
argument_list|(
name|query2
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(RAMP(3))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableFunctionScanWithComplexQuery
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(DEDUP(CURSOR(select \"product_id\", \"product_name\"\n"
operator|+
literal|"from \"product\"\n"
operator|+
literal|"where \"net_weight\"> 100 and \"product_name\" = 'Hello World')\n"
operator|+
literal|",CURSOR(select  \"employee_id\", \"full_name\"\n"
operator|+
literal|"from \"employee\"\n"
operator|+
literal|"group by \"employee_id\", \"full_name\"), 'NAME'))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(DEDUP(CURSOR ((SELECT \"product_id\", \"product_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"product\"\n"
operator|+
literal|"WHERE \"net_weight\"> 100 AND \"product_name\" = 'Hello World')), "
operator|+
literal|"CURSOR ((SELECT \"employee_id\", \"full_name\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"GROUP BY \"employee_id\", \"full_name\")), 'NAME'))"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3593">[CALCITE-3593]    * RelToSqlConverter changes target of ambiguous HAVING clause with a Project    * on Filter on Aggregate</a>. */
annotation|@
name|Test
name|void
name|testBigQueryHaving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT \"DEPTNO\" - 10 \"DEPTNO\"\n"
operator|+
literal|"FROM \"EMP\"\n"
operator|+
literal|"GROUP BY \"DEPTNO\"\n"
operator|+
literal|"HAVING \"DEPTNO\"> 0"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"SELECT DEPTNO - 10 AS DEPTNO\n"
operator|+
literal|"FROM (SELECT DEPTNO\n"
operator|+
literal|"FROM SCOTT.EMP\n"
operator|+
literal|"GROUP BY DEPTNO\n"
operator|+
literal|"HAVING DEPTNO> 0) AS t1"
decl_stmt|;
comment|// Parse the input SQL with PostgreSQL dialect,
comment|// in which "isHavingAlias" is false.
specifier|final
name|SqlParser
operator|.
name|Config
name|parserConfig
init|=
name|PostgresqlSqlDialect
operator|.
name|DEFAULT
operator|.
name|configureParser
argument_list|(
name|SqlParser
operator|.
name|config
argument_list|()
argument_list|)
decl_stmt|;
comment|// Convert rel node to SQL with BigQuery dialect,
comment|// in which "isHavingAlias" is true.
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|parserConfig
argument_list|(
name|parserConfig
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
name|withBigQuery
argument_list|()
operator|.
name|ok
argument_list|(
name|expected
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
name|Set
argument_list|<
name|SqlLibrary
argument_list|>
name|librarySet
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
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
name|SqlParser
operator|.
name|Config
name|parserConfig
decl_stmt|;
specifier|private
specifier|final
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
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
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|Set
argument_list|<
name|SqlLibrary
argument_list|>
name|librarySet
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|config
parameter_list|,
annotation|@
name|Nullable
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
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
name|librarySet
operator|=
name|librarySet
expr_stmt|;
name|this
operator|.
name|relFn
operator|=
name|relFn
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
name|parserConfig
operator|=
name|parserConfig
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
name|SqlParser
operator|.
name|Config
name|parserConfig
parameter_list|,
name|Set
argument_list|<
name|SqlLibrary
argument_list|>
name|librarySet
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|config
parameter_list|,
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
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
name|librarySet
operator|=
name|librarySet
expr_stmt|;
name|this
operator|.
name|relFn
operator|=
name|relFn
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
name|parserConfig
operator|=
name|parserConfig
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
argument_list|)
return|;
block|}
name|Sql
name|relFn
parameter_list|(
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
argument_list|)
return|;
block|}
name|Sql
name|withCalcite
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
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
name|Sql
name|withClickHouse
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CLICKHOUSE
operator|.
name|getDialect
argument_list|()
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
name|withMssql
argument_list|(
literal|14
argument_list|)
return|;
comment|// MSSQL 2008 = 10.0, 2012 = 11.0, 2017 = 14.0
block|}
name|Sql
name|withMssql
parameter_list|(
name|int
name|majorVersion
parameter_list|)
block|{
specifier|final
name|SqlDialect
name|mssqlDialect
init|=
name|DatabaseProduct
operator|.
name|MSSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
return|return
name|dialect
argument_list|(
operator|new
name|MssqlSqlDialect
argument_list|(
name|MssqlSqlDialect
operator|.
name|DEFAULT_CONTEXT
operator|.
name|withDatabaseMajorVersion
argument_list|(
name|majorVersion
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
name|mssqlDialect
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
name|mssqlDialect
operator|.
name|getNullCollation
argument_list|()
argument_list|)
argument_list|)
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
name|MysqlSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|withPresto
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|DatabaseProduct
operator|.
name|PRESTO
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
name|withSybase
parameter_list|()
block|{
return|return
name|dialect
argument_list|(
name|DatabaseProduct
operator|.
name|SYBASE
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
name|PostgresqlSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|OracleSqlDialect
operator|.
name|DEFAULT_CONTEXT
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
name|parserConfig
parameter_list|(
name|SqlParser
operator|.
name|Config
name|parserConfig
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
argument_list|)
return|;
block|}
name|Sql
name|withConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
argument_list|)
return|;
block|}
specifier|final
name|Sql
name|withLibrary
parameter_list|(
name|SqlLibrary
name|library
parameter_list|)
block|{
return|return
name|withLibrarySet
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|library
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withLibrarySet
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|SqlLibrary
argument_list|>
name|librarySet
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
name|parserConfig
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|librarySet
argument_list|)
argument_list|,
name|config
argument_list|,
name|relFn
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
init|=
name|FlatLists
operator|.
name|append
argument_list|(
name|this
operator|.
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
specifier|final
name|RelOptPlanner
name|p
init|=
name|Util
operator|.
name|first
argument_list|(
name|relOptPlanner
argument_list|,
operator|new
name|HepPlanner
argument_list|(
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleClass
argument_list|(
name|RelOptRule
operator|.
name|class
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|program
operator|.
name|run
argument_list|(
name|p
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
decl_stmt|;
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
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
try|try
block|{
name|RelNode
name|rel
decl_stmt|;
if|if
condition|(
name|relFn
operator|!=
literal|null
condition|)
block|{
name|rel
operator|=
name|relFn
operator|.
name|apply
argument_list|(
name|relBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|config
init|=
name|this
operator|.
name|config
operator|.
name|apply
argument_list|(
name|SqlToRelConverter
operator|.
name|config
argument_list|()
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Planner
name|planner
init|=
name|getPlanner
argument_list|(
literal|null
argument_list|,
name|parserConfig
argument_list|,
name|schema
argument_list|,
name|config
argument_list|,
name|librarySet
argument_list|)
decl_stmt|;
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
name|rel
operator|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
operator|.
name|rel
expr_stmt|;
block|}
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
name|parserConfig
argument_list|,
name|librarySet
argument_list|,
name|config
argument_list|,
name|relFn
argument_list|,
name|transforms
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

