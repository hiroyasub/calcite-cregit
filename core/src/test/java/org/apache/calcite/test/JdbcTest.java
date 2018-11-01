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
name|adapter
operator|.
name|clone
operator|.
name|CloneSchema
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
name|adapter
operator|.
name|generate
operator|.
name|RangeTable
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
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|adapter
operator|.
name|java
operator|.
name|ReflectiveSchema
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcConvention
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcSchema
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
name|avatica
operator|.
name|AvaticaConnection
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
name|avatica
operator|.
name|AvaticaStatement
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
name|avatica
operator|.
name|Handler
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
name|avatica
operator|.
name|HandlerImpl
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
name|avatica
operator|.
name|Meta
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
name|avatica
operator|.
name|util
operator|.
name|Casing
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
name|avatica
operator|.
name|util
operator|.
name|Quoting
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|config
operator|.
name|Lex
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
name|jdbc
operator|.
name|CalciteConnection
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
name|jdbc
operator|.
name|CalciteMetaImpl
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|jdbc
operator|.
name|CalciteSchema
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
name|jdbc
operator|.
name|Driver
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|linq4j
operator|.
name|function
operator|.
name|Function0
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
name|RelOptCluster
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
name|RelOptTable
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
name|RelOptUtil
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
name|prepare
operator|.
name|CalcitePrepareImpl
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
name|prepare
operator|.
name|Prepare
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
name|TableModify
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
name|LogicalTableModify
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
name|IntersectToDistinctRule
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
name|runtime
operator|.
name|SqlFunctions
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
name|ModifiableTable
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
name|ModifiableView
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
name|QueryableTable
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
name|schema
operator|.
name|Table
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
name|TableFactory
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
name|TableMacro
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
name|TranslatableTable
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
name|impl
operator|.
name|AbstractSchema
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
name|impl
operator|.
name|AbstractTable
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
name|impl
operator|.
name|AbstractTableQueryable
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
name|impl
operator|.
name|TableMacroImpl
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
name|impl
operator|.
name|ViewTable
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
name|SqlKind
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
name|SqlOperator
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
name|SqlSpecialOperator
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
name|parser
operator|.
name|SqlParserPos
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
name|impl
operator|.
name|SqlParserImpl
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
name|JsonBuilder
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
name|Smalls
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
name|TryThreadLocal
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
name|LinkedListMultimap
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hsqldb
operator|.
name|jdbcDriver
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
name|io
operator|.
name|File
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Date
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
name|sql
operator|.
name|DriverPropertyInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ParameterMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
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
name|ResultSetMetaData
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
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Calendar
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Properties
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
name|TimeZone
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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
name|containsString
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|hasItem
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
name|instanceOf
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
name|not
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
name|CoreMatchers
operator|.
name|startsWith
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
name|assertArrayEquals
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
name|assertEquals
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
name|assertNotNull
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests for using Calcite via JDBC.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FOODMART_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'foodmart',\n"
operator|+
literal|"       jdbcDriver: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|driver
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcUser: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|username
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcPassword: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|password
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcUrl: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|url
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcCatalog: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|catalog
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcSchema: "
operator|+
name|q
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|foodmart
operator|.
name|schema
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FOODMART_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'foodmart',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|FOODMART_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ConnectionSpec
name|SCOTT
init|=
name|Util
operator|.
name|first
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|.
name|scott
argument_list|,
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|HSQLDB
operator|.
name|scott
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCOTT_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'SCOTT',\n"
operator|+
literal|"       jdbcDriver: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|driver
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcUser: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|username
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcPassword: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|password
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcUrl: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|url
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcCatalog: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|catalog
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"       jdbcSchema: "
operator|+
name|q
argument_list|(
name|SCOTT
operator|.
name|schema
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCOTT_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'SCOTT',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|SCOTT_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HR_SCHEMA
init|=
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'hr',\n"
operator|+
literal|"       factory: '"
operator|+
name|ReflectiveSchema
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         class: '"
operator|+
name|HrSchema
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HR_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'hr',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|HR_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|START_OF_GROUP_DATA
init|=
literal|"(values"
operator|+
literal|"(1,0,1),\n"
operator|+
literal|"(2,0,1),\n"
operator|+
literal|"(3,1,2),\n"
operator|+
literal|"(4,0,3),\n"
operator|+
literal|"(5,0,3),\n"
operator|+
literal|"(6,0,3),\n"
operator|+
literal|"(7,1,4),\n"
operator|+
literal|"(8,1,4))\n"
operator|+
literal|" as t(rn,val,expected)"
decl_stmt|;
specifier|private
specifier|static
name|String
name|q
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|==
literal|null
condition|?
literal|"null"
else|:
literal|"'"
operator|+
name|s
operator|+
literal|"'"
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getFoodmartQueries
parameter_list|()
block|{
return|return
name|FOODMART_QUERIES
return|;
block|}
comment|/** Tests a modifiable view. */
annotation|@
name|Test
specifier|public
name|void
name|testModelWithModifiableView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|employees
operator|.
name|add
argument_list|(
operator|new
name|Employee
argument_list|(
literal|135
argument_list|,
literal|10
argument_list|,
literal|"Simon"
argument_list|,
literal|56.7f
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignore
init|=
name|EmpDeptTableFactory
operator|.
name|THREAD_COLLECTION
operator|.
name|push
argument_list|(
name|employees
argument_list|)
init|)
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"deptno\" = 10"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"name=Simon\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"explain plan for\n"
operator|+
literal|"insert into \"adhoc\".V\n"
operator|+
literal|"values ('Fred', 56, 123.4)"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|isLinux
argument_list|(
literal|"EnumerableTableModify(table=[[adhoc, MUTABLE_EMPLOYEES]], operation=[INSERT], flattened=[false])\n"
operator|+
literal|"  EnumerableCalc(expr#0..2=[{inputs}], expr#3=[CAST($t1):JavaType(int) NOT NULL], expr#4=[10], expr#5=[CAST($t0):JavaType(class java.lang.String)], expr#6=[CAST($t2):JavaType(float) NOT NULL], expr#7=[null:JavaType(class java.lang.Integer)], empid=[$t3], deptno=[$t4], name=[$t5], salary=[$t6], commission=[$t7])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 'Fred', 56, 123.4 }]])\n"
argument_list|)
argument_list|)
expr_stmt|;
comment|// With named columns
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"explain plan for\n"
operator|+
literal|"insert into \"adhoc\".V (\"name\", e, \"salary\")\n"
operator|+
literal|"values ('Fred', 56, 123.4)"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
comment|// With named columns, in different order
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"explain plan for\n"
operator|+
literal|"insert into \"adhoc\".V (e, \"salary\", \"name\")\n"
operator|+
literal|"values (56, 123.4, 'Fred')"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
comment|// Mis-named column
try|try
block|{
specifier|final
name|PreparedStatement
name|s
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"explain plan for\n"
operator|+
literal|"insert into \"adhoc\".V (empno, \"salary\", \"name\")\n"
operator|+
literal|"values (56, 123.4, 'Fred')"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|startsWith
argument_list|(
literal|"Error while preparing statement"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Fail to provide mandatory column
try|try
block|{
specifier|final
name|PreparedStatement
name|s
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"explain plan for\n"
operator|+
literal|"insert into \"adhoc\".V (e, name)\n"
operator|+
literal|"values (56, 'Fred')"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|startsWith
argument_list|(
literal|"Error while preparing statement"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests a few cases where modifiable views are invalid. */
annotation|@
name|Test
specifier|public
name|void
name|testModelWithInvalidModifiableView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|employees
operator|.
name|add
argument_list|(
operator|new
name|Employee
argument_list|(
literal|135
argument_list|,
literal|10
argument_list|,
literal|"Simon"
argument_list|,
literal|56.7f
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignore
init|=
name|EmpDeptTableFactory
operator|.
name|THREAD_COLLECTION
operator|.
name|push
argument_list|(
name|employees
argument_list|)
init|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|RESOURCE
operator|.
name|noValueSuppliedForViewColumn
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"commission\" = 10"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"View is not modifiable. No value is supplied for NOT NULL "
operator|+
literal|"column 'deptno' of base table 'MUTABLE_EMPLOYEES'"
argument_list|)
expr_stmt|;
comment|// no error if we do not claim that the view is modifiable
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"commission\" = 10"
argument_list|,
literal|null
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"deptno\" IN (10, 20)"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Modifiable view must be predicated only on equality expressions"
argument_list|)
expr_stmt|;
comment|// Deduce "deptno = 10" from the constraint, and add a further
comment|// condition "deptno< 20 OR commission> 1000".
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\"\n"
operator|+
literal|"where \"deptno\" = 10 AND (\"deptno\"< 20 OR \"commission\"> 1000)"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"insert into \"adhoc\".v values ('n',1,2)"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Modifiable view must be predicated only on equality expressions"
argument_list|)
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\"\n"
operator|+
literal|"where \"deptno\" = 10 AND (\"deptno\"> 20 AND \"commission\"> 1000)"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"insert into \"adhoc\".v values ('n',1,2)"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Modifiable view must be predicated only on equality expressions"
argument_list|)
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\" "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\"\n"
operator|+
literal|"where \"commission\" = 100 AND \"deptno\" = 20"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\", \"empid\" + 3 as e3, 1 as uno\n"
operator|+
literal|"from \"MUTABLE_EMPLOYEES\"\n"
operator|+
literal|"where \"commission\" = 100 AND \"deptno\" = 20"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|RESOURCE
operator|.
name|moreThanOneMappedColumn
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\", \"name\" as n2 "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"deptno\" IN (10, 20)"
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"View is not modifiable. More than one expression maps to "
operator|+
literal|"column 'name' of base table 'MUTABLE_EMPLOYEES'"
argument_list|)
expr_stmt|;
comment|// no error if we do not claim that the view is modifiable
name|modelWithView
argument_list|(
literal|"select \"name\", \"empid\" as e, \"salary\", \"name\" as n2 "
operator|+
literal|"from \"MUTABLE_EMPLOYEES\" where \"deptno\" IN (10, 20)"
argument_list|,
literal|null
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests a relation that is accessed via method syntax.    *    *<p>The function ({@link Smalls#view(String)} has a return type    * {@link Table} and the actual returned value implements    * {@link org.apache.calcite.schema.TranslatableTable}.    */
annotation|@
name|Test
specifier|public
name|void
name|testTableMacro
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|SchemaPlus
name|schema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TableMacro
name|tableMacro
init|=
name|TableMacroImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|VIEW_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"View"
argument_list|,
name|tableMacro
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"View\"('(10), (20)')) as t(n)\n"
operator|+
literal|"where n< 15"
argument_list|)
decl_stmt|;
comment|// The call to "View('(10), (2)')" expands to 'values (1), (3), (10), (20)'.
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"N=1\n"
operator|+
literal|"N=3\n"
operator|+
literal|"N=10\n"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Table macro that takes a MAP as a parameter.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-588">[CALCITE-588]    * Allow TableMacro to consume Maps and Collections</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTableMacroMap
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|SchemaPlus
name|schema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TableMacro
name|tableMacro
init|=
name|TableMacroImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|STR_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Str"
argument_list|,
name|tableMacro
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Str\"(MAP['a', 1, 'baz', 2],\n"
operator|+
literal|"                         ARRAY[3, 4, CAST(null AS INTEGER)])) as t(n)"
argument_list|)
decl_stmt|;
comment|// The call to "View('(10), (2)')" expands to 'values (1), (3), (10), (20)'.
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"N={'a'=1, 'baz'=2}\n"
operator|+
literal|"N=[3, 4, null]    \n"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a table macro with named and optional parameters. */
annotation|@
name|Test
specifier|public
name|void
name|testTableMacroWithNamedParameters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// View(String r optional, String s, int t optional)
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|assertWithMacro
argument_list|(
name|Smalls
operator|.
name|TableMacroFunctionWithNamedParameters
operator|.
name|class
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"('(5)'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature View(<CHARACTER>)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"c=1\n"
operator|+
literal|"c=3\n"
operator|+
literal|"c=5\n"
operator|+
literal|"c=6\n"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"('5', '6'))"
argument_list|)
operator|.
name|returns
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"c=1\n"
operator|+
literal|"c=3\n"
operator|+
literal|"c=5\n"
operator|+
literal|"c=6\n"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"(r=>'5', s=>'6'))"
argument_list|)
operator|.
name|returns
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"(t=>'5', t=>'6'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Duplicate argument name 'T'"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"(t=>'5', s=>'6'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature View(T =><CHARACTER>, S =><CHARACTER>)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"c=1\n"
operator|+
literal|"c=3\n"
operator|+
literal|"c=6\n"
operator|+
literal|"c=5\n"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"(t=>5, s=>'6'))"
argument_list|)
operator|.
name|returns
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a table    *  macro. */
annotation|@
name|Test
specifier|public
name|void
name|testTableMacroInModel
parameter_list|()
throws|throws
name|Exception
block|{
name|checkTableMacroInModel
argument_list|(
name|Smalls
operator|.
name|TableMacroFunction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a table    *  macro defined as a static method. */
annotation|@
name|Test
specifier|public
name|void
name|testStaticTableMacroInModel
parameter_list|()
throws|throws
name|Exception
block|{
name|checkTableMacroInModel
argument_list|(
name|Smalls
operator|.
name|StaticTableMacroFunction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a table    *  function. */
annotation|@
name|Test
specifier|public
name|void
name|testTableFunctionInModel
parameter_list|()
throws|throws
name|Exception
block|{
name|checkTableFunctionInModel
argument_list|(
name|Smalls
operator|.
name|MyTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a table    *  function defined as a static method. */
annotation|@
name|Test
specifier|public
name|void
name|testStaticTableFunctionInModel
parameter_list|()
throws|throws
name|Exception
block|{
name|checkTableFunctionInModel
argument_list|(
name|Smalls
operator|.
name|TestStaticTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|assertWithMacro
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'View',\n"
operator|+
literal|"           className: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkTableMacroInModel
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|assertWithMacro
argument_list|(
name|clazz
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from table(\"adhoc\".\"View\"('(30)'))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"c=1\n"
operator|+
literal|"c=3\n"
operator|+
literal|"c=30\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkTableFunctionInModel
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|checkTableMacroInModel
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|assertWithMacro
argument_list|(
name|clazz
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"a\".\"c\" a, \"b\".\"c\" b\n"
operator|+
literal|"  from table(\"adhoc\".\"View\"('(30)')) \"a\",\n"
operator|+
literal|" lateral(select *\n"
operator|+
literal|"   from table(\"adhoc\".\"View\"('('||\n"
operator|+
literal|"          cast(\"a\".\"c\" as varchar(10))||')'))) \"b\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=1; B=1"
argument_list|,
literal|"A=1; B=3"
argument_list|,
literal|"A=1; B=1"
argument_list|,
literal|"A=3; B=1"
argument_list|,
literal|"A=3; B=3"
argument_list|,
literal|"A=3; B=3"
argument_list|,
literal|"A=30; B=1"
argument_list|,
literal|"A=30; B=3"
argument_list|,
literal|"A=30; B=30"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link org.apache.calcite.avatica.Handler#onConnectionClose}    * and  {@link org.apache.calcite.avatica.Handler#onStatementClose}. */
annotation|@
name|Test
specifier|public
name|void
name|testOnConnectionClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
index|[]
name|closeCount
init|=
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|int
index|[]
name|statementCloseCount
init|=
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|HandlerImpl
name|h
init|=
operator|new
name|HandlerImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onConnectionClose
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
operator|++
name|closeCount
index|[
literal|0
index|]
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onStatementClose
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|)
block|{
operator|++
name|statementCloseCount
index|[
literal|0
index|]
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignore
init|=
name|HandlerDriver
operator|.
name|HANDLERS
operator|.
name|push
argument_list|(
name|h
argument_list|)
init|)
block|{
specifier|final
name|HandlerDriver
name|driver
init|=
operator|new
name|HandlerDriver
argument_list|()
decl_stmt|;
name|CalciteConnection
name|connection
init|=
operator|(
name|CalciteConnection
operator|)
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"emps\""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|resultSet
operator|.
name|next
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"resultSet.next() should throw SQLException when closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|containsString
argument_list|(
literal|"ResultSet closed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close statement. It throws SQLException, but statement is still closed.
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expecting error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close connection. It throws SQLException, but connection is still closed.
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expecting error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Close a closed connection. Handler is not called again.
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statementCloseCount
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests {@link java.sql.Statement}.{@code closeOnCompletion()}. */
annotation|@
name|Test
specifier|public
name|void
name|testStatementCloseOnCompletion
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|javaVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaVersion
operator|.
name|compareTo
argument_list|(
literal|"1.7"
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Statement.closeOnCompletion was introduced in JDK 1.7.
return|return;
block|}
specifier|final
name|Driver
name|driver
init|=
operator|new
name|Driver
argument_list|()
decl_stmt|;
name|CalciteConnection
name|connection
init|=
operator|(
name|CalciteConnection
operator|)
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
operator|(
name|Boolean
operator|)
name|CalciteAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"isCloseOnCompletion"
argument_list|)
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"closeOnCompletion"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Boolean
operator|)
name|CalciteAssert
operator|.
name|call
argument_list|(
name|statement
argument_list|,
literal|"isCloseOnCompletion"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"emps\""
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
comment|// when result set is closed, statement is closed automatically
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|statement
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|connection
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2071">[CALCITE-2071]    * Query with IN and OR in WHERE clause returns wrong result</a>.    * More cases in sub-query.iq. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereInOr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"empid\"\n"
operator|+
literal|"from \"hr\".\"emps\" t\n"
operator|+
literal|"where (\"empid\" in (select \"empid\" from \"hr\".\"emps\")\n"
operator|+
literal|"    or \"empid\" in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,\n"
operator|+
literal|"        12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25))\n"
operator|+
literal|"and \"empid\" in (100, 200, 150)"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100"
argument_list|,
literal|"empid=200"
argument_list|,
literal|"empid=150"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that a driver can be extended with its own parser and can execute    * its own flavor of DDL. */
annotation|@
name|Test
specifier|public
name|void
name|testMockDdl
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MockDdlDriver
name|driver
init|=
operator|new
name|MockDdlDriver
argument_list|()
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
init|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|assertThat
argument_list|(
name|driver
operator|.
name|counter
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|statement
operator|.
name|executeUpdate
argument_list|(
literal|"COMMIT"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|driver
operator|.
name|counter
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The example in the README.    */
annotation|@
name|Test
specifier|public
name|void
name|testReadme
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
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
literal|"lex"
argument_list|,
literal|"JAVA"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|calciteConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select d.deptno, min(e.empid)\n"
operator|+
literal|"from hr.emps as e\n"
operator|+
literal|"join hr.depts as d\n"
operator|+
literal|"  on e.deptno = d.deptno\n"
operator|+
literal|"group by d.deptno\n"
operator|+
literal|"having count(*)> 1"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test for {@link Driver#getPropertyInfo(String, Properties)}. */
annotation|@
name|Test
specifier|public
name|void
name|testConnectionProperties
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|java
operator|.
name|sql
operator|.
name|Driver
name|driver
init|=
name|DriverManager
operator|.
name|getDriver
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
specifier|final
name|DriverPropertyInfo
index|[]
name|propertyInfo
init|=
name|driver
operator|.
name|getPropertyInfo
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DriverPropertyInfo
name|info
range|:
name|propertyInfo
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"SCHEMA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"TIME_ZONE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"MATERIALIZATIONS_ENABLED"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure that the properties look sane.    */
annotation|@
name|Test
specifier|public
name|void
name|testVersion
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|DatabaseMetaData
name|metaData
init|=
name|calciteConnection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Calcite JDBC Driver"
argument_list|,
name|metaData
operator|.
name|getDriverName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|driverVersion
init|=
name|metaData
operator|.
name|getDriverVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|driverMajor
init|=
name|metaData
operator|.
name|getDriverMajorVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|driverMinor
init|=
name|metaData
operator|.
name|getDriverMinorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|driverMajor
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|driverMinor
operator|>=
literal|0
operator|&&
name|driverMinor
operator|<
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Calcite"
argument_list|,
name|metaData
operator|.
name|getDatabaseProductName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|databaseVersion
init|=
name|metaData
operator|.
name|getDatabaseProductVersion
argument_list|()
decl_stmt|;
specifier|final
name|int
name|databaseMajor
init|=
name|metaData
operator|.
name|getDatabaseMajorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|driverMajor
argument_list|,
name|databaseMajor
argument_list|)
expr_stmt|;
specifier|final
name|int
name|databaseMinor
init|=
name|metaData
operator|.
name|getDatabaseMinorVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|driverMinor
argument_list|,
name|databaseMinor
argument_list|)
expr_stmt|;
comment|// Check how version is composed of major and minor version. Note that
comment|// version is stored in pom.xml; major and minor version are
comment|// stored in org-apache-calcite-jdbc.properties, but derived from
comment|// version.major and version.minor in pom.xml.
comment|//
comment|// We are more permissive for snapshots.
comment|// For instance, we allow 1.4.0-SNAPSHOT to match {major=1, minor=3}.
comment|// Previously, this test would break the first build after a release.
name|assertTrue
argument_list|(
name|driverVersion
operator|.
name|startsWith
argument_list|(
name|driverMajor
operator|+
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|driverVersion
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
operator|.
name|length
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|driverVersion
operator|.
name|equals
argument_list|(
name|mm
argument_list|(
name|driverMajor
argument_list|,
name|driverMinor
argument_list|)
argument_list|)
operator|||
name|driverVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|driverMajor
argument_list|,
name|driverMinor
argument_list|)
operator|+
literal|"."
argument_list|)
operator|||
name|driverVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|driverMajor
argument_list|,
name|driverMinor
argument_list|)
operator|+
literal|"-"
argument_list|)
operator|||
name|driverVersion
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
operator|&&
name|driverVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|driverMajor
argument_list|,
name|driverMinor
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|databaseVersion
operator|.
name|startsWith
argument_list|(
literal|"1."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|databaseVersion
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
operator|.
name|length
operator|>=
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|databaseVersion
operator|.
name|equals
argument_list|(
name|mm
argument_list|(
name|databaseMajor
argument_list|,
name|databaseMinor
argument_list|)
argument_list|)
operator|||
name|databaseVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|databaseMajor
argument_list|,
name|databaseMinor
argument_list|)
operator|+
literal|"."
argument_list|)
operator|||
name|databaseVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|databaseMajor
argument_list|,
name|databaseMinor
argument_list|)
operator|+
literal|"-"
argument_list|)
operator|||
name|databaseVersion
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
operator|&&
name|databaseVersion
operator|.
name|startsWith
argument_list|(
name|mm
argument_list|(
name|driverMajor
argument_list|,
name|driverMinor
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|mm
parameter_list|(
name|int
name|majorVersion
parameter_list|,
name|int
name|minorVersion
parameter_list|)
block|{
return|return
name|majorVersion
operator|+
literal|"."
operator|+
name|minorVersion
return|;
block|}
comment|/** Tests driver's implementation of {@link DatabaseMetaData#getColumns}. */
annotation|@
name|Test
specifier|public
name|void
name|testMetaDataColumns
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|connect
argument_list|()
decl_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|metaData
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// there's something
name|String
name|name
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|typeName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|int
name|columnSize
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|int
name|decimalDigits
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|int
name|numPrecRadix
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|charOctetLength
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|String
name|isNullable
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|18
argument_list|)
decl_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests driver's implementation of {@link DatabaseMetaData#getPrimaryKeys}.    * It is empty but it should still have column definitions. */
annotation|@
name|Test
specifier|public
name|void
name|testMetaDataPrimaryKeys
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|connect
argument_list|()
decl_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|metaData
operator|.
name|getPrimaryKeys
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// catalog never contains primary keys
name|ResultSetMetaData
name|resultSetMetaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|resultSetMetaData
operator|.
name|getColumnCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TABLE_CAT"
argument_list|,
name|resultSetMetaData
operator|.
name|getColumnName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|java
operator|.
name|sql
operator|.
name|Types
operator|.
name|VARCHAR
argument_list|,
name|resultSetMetaData
operator|.
name|getColumnType
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PK_NAME"
argument_list|,
name|resultSetMetaData
operator|.
name|getColumnName
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Unit test for    * {@link org.apache.calcite.jdbc.CalciteMetaImpl#likeToRegex(org.apache.calcite.avatica.Meta.Pat)}. */
annotation|@
name|Test
specifier|public
name|void
name|testLikeToRegex
parameter_list|()
block|{
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"%"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"abc"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"abc"
argument_list|,
literal|"abcd"
argument_list|)
expr_stmt|;
comment|// trailing char fails match
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"abc"
argument_list|,
literal|"0abc"
argument_list|)
expr_stmt|;
comment|// leading char fails match
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"abc"
argument_list|,
literal|"aBc"
argument_list|)
expr_stmt|;
comment|// case-sensitive match
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a[b]c"
argument_list|,
literal|"a[b]c"
argument_list|)
expr_stmt|;
comment|// nothing special about brackets
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a$c"
argument_list|,
literal|"a$c"
argument_list|)
expr_stmt|;
comment|// nothing special about dollar
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"a$"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
comment|// nothing special about dollar
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a%c"
argument_list|,
literal|"ac"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a%c"
argument_list|,
literal|"abbbc"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"a%c"
argument_list|,
literal|"acccd"
argument_list|)
expr_stmt|;
comment|// escape using back-slash
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a\\%c"
argument_list|,
literal|"a%c"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"a\\%c"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"a\\%c"
argument_list|,
literal|"a\\%c"
argument_list|)
expr_stmt|;
comment|// multiple wild-cards
name|checkLikeToRegex
argument_list|(
literal|true
argument_list|,
literal|"a%c%d"
argument_list|,
literal|"abcdaaad"
argument_list|)
expr_stmt|;
name|checkLikeToRegex
argument_list|(
literal|false
argument_list|,
literal|"a%c%d"
argument_list|,
literal|"abcdc"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkLikeToRegex
parameter_list|(
name|boolean
name|b
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
name|abc
parameter_list|)
block|{
specifier|final
name|Pattern
name|regex
init|=
name|CalciteMetaImpl
operator|.
name|likeToRegex
argument_list|(
name|Meta
operator|.
name|Pat
operator|.
name|of
argument_list|(
name|pattern
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|==
name|regex
operator|.
name|matcher
argument_list|(
name|abc
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Tests driver's implementation of {@link DatabaseMetaData#getColumns},    * and also    *<a href="https://issues.apache.org/jira/browse/CALCITE-1222">[CALCITE-1222]    * DatabaseMetaData.getColumnLabel returns null when query has ORDER    * BY</a>, */
annotation|@
name|Test
specifier|public
name|void
name|testResultSetMetaData
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
try|try
init|(
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|connect
argument_list|()
init|)
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select \"empid\", \"deptno\" as x, 1 as y\n"
operator|+
literal|"from \"hr\".\"emps\""
decl_stmt|;
name|checkResultSetMetaData
argument_list|(
name|connection
argument_list|,
name|sql0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select \"empid\", \"deptno\" as x, 1 as y\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"order by 1"
decl_stmt|;
name|checkResultSetMetaData
argument_list|(
name|connection
argument_list|,
name|sql1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkResultSetMetaData
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|ResultSetMetaData
name|metaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|metaData
operator|.
name|getColumnCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"empid"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"empid"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"emps"
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"X"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deptno"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"emps"
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Y"
argument_list|,
name|metaData
operator|.
name|getColumnLabel
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Y"
argument_list|,
name|metaData
operator|.
name|getColumnName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metaData
operator|.
name|getTableName
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests some queries that have expedited processing because connection pools    * like to use them to check whether the connection is alive.    */
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests accessing columns by name. */
annotation|@
name|Test
specifier|public
name|void
name|testGetByName
parameter_list|()
throws|throws
name|Exception
block|{
comment|// JDBC 3.0 specification: "Column names supplied to getter methods are case
comment|// insensitive. If a select list contains the same column more than once,
comment|// the first instance of the column will be returned."
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|doWithConnection
argument_list|(
name|c
lambda|->
block|{
try|try
block|{
name|Statement
name|s
init|=
name|c
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
init|=
name|s
operator|.
name|executeQuery
argument_list|(
literal|""
operator|+
literal|"SELECT 1 as \"a\", 2 as \"b\", 3 as \"a\", 4 as \"B\"\n"
operator|+
literal|"FROM (VALUES (0))"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|x
init|=
name|rs
operator|.
name|getInt
argument_list|(
literal|"z"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|findColumn
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|findColumn
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|findColumn
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|findColumn
argument_list|(
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|x
init|=
name|rs
operator|.
name|findColumn
argument_list|(
literal|"z"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|equalTo
argument_list|(
literal|"column 'z' not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|int
name|x
init|=
name|rs
operator|.
name|getInt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|equalTo
argument_list|(
literal|"invalid column ordinal: 0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|int
name|x
init|=
name|rs
operator|.
name|getInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|equalTo
argument_list|(
literal|"invalid column ordinal: 5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloneSchema
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
specifier|final
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|connect
argument_list|()
decl_stmt|;
specifier|final
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|foodmart
init|=
name|rootSchema
operator|.
name|getSubSchema
argument_list|(
literal|"foodmart"
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"foodmart2"
argument_list|,
operator|new
name|CloneSchema
argument_list|(
name|foodmart
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select count(*) from \"foodmart2\".\"time_by_day\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|730
argument_list|,
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"the_year\", count(*) as c, min(\"the_month\") as m\n"
operator|+
literal|"from \"foodmart2\".\"time_by_day\"\n"
operator|+
literal|"group by \"the_year\"\n"
operator|+
literal|"order by 1, 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"the_year=1997; C=365; M=April\n"
operator|+
literal|"the_year=1998; C=365; M=April\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"The test returns expected results. Not sure why it is disabled"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\", \"time_by_day\".\"quarter\" as \"c1\", \"product_class\".\"product_family\" as \"c2\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" group by \"time_by_day\".\"the_year\", \"time_by_day\".\"quarter\", \"product_class\".\"product_family\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"c0=1997; c1=Q2; c2=Drink; m0=5895.0000"
argument_list|,
literal|"c0=1997; c1=Q1; c2=Food; m0=47809.0000"
argument_list|,
literal|"c0=1997; c1=Q3; c2=Drink; m0=6065.0000"
argument_list|,
literal|"c0=1997; c1=Q4; c2=Drink; m0=6661.0000"
argument_list|,
literal|"c0=1997; c1=Q4; c2=Food; m0=51866.0000"
argument_list|,
literal|"c0=1997; c1=Q1; c2=Drink; m0=5976.0000"
argument_list|,
literal|"c0=1997; c1=Q3; c2=Non-Consumable; m0=12343.0000"
argument_list|,
literal|"c0=1997; c1=Q4; c2=Non-Consumable; m0=13497.0000"
argument_list|,
literal|"c0=1997; c1=Q2; c2=Non-Consumable; m0=11890.0000"
argument_list|,
literal|"c0=1997; c1=Q2; c2=Food; m0=44825.0000"
argument_list|,
literal|"c0=1997; c1=Q3; c2=Food; m0=47440.0000"
argument_list|,
literal|"c0=1997; c1=Q1; c2=Non-Consumable; m0=12506.0000"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests plan for a query with 4 tables, 3 joins. */
annotation|@
name|Ignore
argument_list|(
literal|"The actual and expected plan differ"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCloneGroupBy2Plan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"explain plan for select \"time_by_day\".\"the_year\" as \"c0\", \"time_by_day\".\"quarter\" as \"c1\", \"product_class\".\"product_family\" as \"c2\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" group by \"time_by_day\".\"the_year\", \"time_by_day\".\"quarter\", \"product_class\".\"product_family\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableAggregate(group=[{0, 1, 2}], m0=[SUM($3)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..37=[{inputs}], c0=[$t9], c1=[$t13], c2=[$t4], unit_sales=[$t22])\n"
operator|+
literal|"    EnumerableJoin(condition=[=($23, $0)], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, product_class]])\n"
operator|+
literal|"      EnumerableJoin(condition=[=($10, $19)], joinType=[inner])\n"
operator|+
literal|"        EnumerableJoin(condition=[=($11, $0)], joinType=[inner])\n"
operator|+
literal|"          EnumerableCalc(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], proj#0..9=[{exprs}], $condition=[$t12])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, product]])\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByCase
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\" from \"time_by_day\" as \"time_by_day\" group by \"time_by_day\".\"the_year\" order by CASE WHEN \"time_by_day\".\"the_year\" IS NULL THEN 1 ELSE 0 END, \"time_by_day\".\"the_year\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997\n"
operator|+
literal|"c0=1998\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Just short of bushy. */
annotation|@
name|Test
specifier|public
name|void
name|testAlmostBushy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
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
literal|"where c.\"city\" = 'San Francisco'\n"
operator|+
literal|"and p.\"brand_name\" = 'Washington'"
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|"including all attributes "
argument_list|,
name|CalciteAssert
operator|.
name|checkMaskedResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableJoin(condition=[=($0, $38)], joinType=[inner]): rowcount = 7.050660528307499E8, cumulative cost = {1.0640240216183146E9 rows, 777302.0 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableJoin(condition=[=($2, $8)], joinType=[inner]): rowcount = 2.0087351932499997E7, cumulative cost = {2.117504719375143E7 rows, 724261.0 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableTableScan(table=[[foodmart2, sales_fact_1997]]): rowcount = 86837.0, cumulative cost = {86837.0 rows, 86838.0 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableCalc(expr#0..28=[{inputs}], expr#29=['San Francisco':VARCHAR(30)], expr#30=[=($t9, $t29)], proj#0..28=[{exprs}], $condition=[$t30]): rowcount = 1542.1499999999999, cumulative cost = {11823.15 rows, 637423.0 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, customer]]): rowcount = 10281.0, cumulative cost = {10281.0 rows, 10282.0 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableCalc(expr#0..14=[{inputs}], expr#15=['Washington':VARCHAR(60)], expr#16=[=($t2, $t15)], proj#0..14=[{exprs}], $condition=[$t16]): rowcount = 234.0, cumulative cost = {1794.0 rows, 53041.0 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableTableScan(table=[[foodmart2, product]]): rowcount = 1560.0, cumulative cost = {1560.0 rows, 1561.0 cpu, 0.0 io}\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a query whose best plan is a bushy join.    * First join sales_fact_1997 to customer;    * in parallel join product to product_class;    * then join the results. */
annotation|@
name|Ignore
argument_list|(
literal|"extremely slow - a bit better if you disable ProjectMergeRule"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testBushy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
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
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|"including all attributes "
argument_list|,
name|CalciteAssert
operator|.
name|checkMaskedResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalcRel(expr#0..56=[{inputs}], expr#57=['San Francisco'], expr#58=[=($t9, $t57)], expr#59=['Snacks'], expr#60=[=($t32, $t59)], expr#61=[AND($t58, $t60)], product_id=[$t49], time_id=[$t50], customer_id=[$t51], promotion_id=[$t52], store_id=[$t53], store_sales=[$t54], store_cost=[$t55], unit_sales=[$t56], customer_id0=[$t0], account_num=[$t1], lname=[$t2], fname=[$t3], mi=[$t4], address1=[$t5], address2=[$t6], address3=[$t7], address4=[$t8], city=[$t9], state_province=[$t10], postal_code=[$t11], country=[$t12], customer_region_id=[$t13], phone1=[$t14], phone2=[$t15], birthdate=[$t16], marital_status=[$t17], yearly_income=[$t18], gender=[$t19], total_children=[$t20], num_children_at_home=[$t21], education=[$t22], date_accnt_opened=[$t23], member_card=[$t24], occupation=[$t25], houseowner=[$t26], num_cars_owned=[$t27], fullname=[$t28], product_class_id=[$t34], product_id0=[$t35], brand_name=[$t36], product_name=[$t37], SKU=[$t38], SRP=[$t39], gross_weight=[$t40], net_weight=[$t41], recyclable_package=[$t42], low_fat=[$t43], units_per_case=[$t44], cases_per_pallet=[$t45], shelf_width=[$t46], shelf_height=[$t47], shelf_depth=[$t48], product_class_id0=[$t29], product_subcategory=[$t30], product_category=[$t31], product_department=[$t32], product_family=[$t33], $condition=[$t61]): rowcount = 1953.8325, cumulative cost = {728728.1144018068 rows, 1.0519232E7 cpu, 0.0 io}\n"
operator|+
literal|"  EnumerableJoinRel(condition=[=($51, $0)], joinType=[inner]): rowcount = 86837.0, cumulative cost = {726774.2819018068 rows, 98792.0 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableTableScan(table=[[foodmart2, customer]]): rowcount = 10281.0, cumulative cost = {10281.0 rows, 10282.0 cpu, 0.0 io}\n"
operator|+
literal|"    EnumerableJoinRel(condition=[=($5, $0)], joinType=[inner]): rowcount = 86837.0, cumulative cost = {447842.86095661717 rows, 88510.0 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, product_class]]): rowcount = 110.0, cumulative cost = {110.0 rows, 111.0 cpu, 0.0 io}\n"
operator|+
literal|"      EnumerableJoinRel(condition=[=($15, $1)], joinType=[inner]): rowcount = 86837.0, cumulative cost = {273541.80811638 rows, 88399.0 cpu, 0.0 io}\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, product]]): rowcount = 1560.0, cumulative cost = {1560.0 rows, 1561.0 cpu, 0.0 io}\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, sales_fact_1997]]): rowcount = 86837.0, cumulative cost = {86837.0 rows, 86838.0 cpu, 0.0 io}\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|QUERIES
init|=
block|{
literal|"select count(*) from (select 1 as \"c0\" from \"salary\" as \"salary\") as \"init\""
block|,
literal|"EXPR$0=21252\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"salary\" as \"salary2\") as \"init\""
block|,
literal|"EXPR$0=21252\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"department\" as \"department\") as \"init\""
block|,
literal|"EXPR$0=12\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"employee\" as \"employee\") as \"init\""
block|,
literal|"EXPR$0=1155\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"employee_closure\" as \"employee_closure\") as \"init\""
block|,
literal|"EXPR$0=7179\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"position\" as \"position\") as \"init\""
block|,
literal|"EXPR$0=18\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"promotion\" as \"promotion\") as \"init\""
block|,
literal|"EXPR$0=1864\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"store\" as \"store\") as \"init\""
block|,
literal|"EXPR$0=25\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"product\" as \"product\") as \"init\""
block|,
literal|"EXPR$0=1560\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"product_class\" as \"product_class\") as \"init\""
block|,
literal|"EXPR$0=110\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"time_by_day\" as \"time_by_day\") as \"init\""
block|,
literal|"EXPR$0=730\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"customer\" as \"customer\") as \"init\""
block|,
literal|"EXPR$0=10281\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"sales_fact_1997\" as \"sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86837\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"inventory_fact_1997\" as \"inventory_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=4070\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"warehouse\" as \"warehouse\") as \"init\""
block|,
literal|"EXPR$0=24\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_c_special_sales_fact_1997\" as \"agg_c_special_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86805\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_pl_01_sales_fact_1997\" as \"agg_pl_01_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86829\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_l_05_sales_fact_1997\" as \"agg_l_05_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86154\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_g_ms_pcat_sales_fact_1997\" as \"agg_g_ms_pcat_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=2637\n"
block|,
literal|"select count(*) from (select 1 as \"c0\" from \"agg_c_14_sales_fact_1997\" as \"agg_c_14_sales_fact_1997\") as \"init\""
block|,
literal|"EXPR$0=86805\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\" from \"time_by_day\" as \"time_by_day\" group by \"time_by_day\".\"the_year\" order by \"time_by_day\".\"the_year\" ASC"
block|,
literal|"c0=1997\n"
operator|+
literal|"c0=1998\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_country\") = UPPER('USA') group by \"store\".\"store_country\" order by \"store\".\"store_country\" ASC"
block|,
literal|"c0=USA\n"
block|,
literal|"select \"store\".\"store_state\" as \"c0\" from \"store\" as \"store\" where (\"store\".\"store_country\" = 'USA') and UPPER(\"store\".\"store_state\") = UPPER('CA') group by \"store\".\"store_state\" order by \"store\".\"store_state\" ASC"
block|,
literal|"c0=CA\n"
block|,
literal|"select \"store\".\"store_city\" as \"c0\", \"store\".\"store_state\" as \"c1\" from \"store\" as \"store\" where (\"store\".\"store_state\" = 'CA' and \"store\".\"store_country\" = 'USA') and UPPER(\"store\".\"store_city\") = UPPER('Los Angeles') group by \"store\".\"store_city\", \"store\".\"store_state\" order by \"store\".\"store_city\" ASC"
block|,
literal|"c0=Los Angeles; c1=CA\n"
block|,
literal|"select \"customer\".\"country\" as \"c0\" from \"customer\" as \"customer\" where UPPER(\"customer\".\"country\") = UPPER('USA') group by \"customer\".\"country\" order by \"customer\".\"country\" ASC"
block|,
literal|"c0=USA\n"
block|,
literal|"select \"customer\".\"state_province\" as \"c0\", \"customer\".\"country\" as \"c1\" from \"customer\" as \"customer\" where (\"customer\".\"country\" = 'USA') and UPPER(\"customer\".\"state_province\") = UPPER('CA') group by \"customer\".\"state_province\", \"customer\".\"country\" order by \"customer\".\"state_province\" ASC"
block|,
literal|"c0=CA; c1=USA\n"
block|,
literal|"select \"customer\".\"city\" as \"c0\", \"customer\".\"country\" as \"c1\", \"customer\".\"state_province\" as \"c2\" from \"customer\" as \"customer\" where (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\" = 'CA' and \"customer\".\"country\" = 'USA' and \"customer\".\"state_province\" = 'CA' and \"customer\".\"country\" = 'USA') and UPPER(\"customer\".\"city\") = UPPER('Los Angeles') group by \"customer\".\"city\", \"customer\".\"country\", \"customer\".\"state_province\" order by \"customer\".\"city\" ASC"
block|,
literal|"c0=Los Angeles; c1=USA; c2=CA\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_country\") = UPPER('Gender') group by \"store\".\"store_country\" order by \"store\".\"store_country\" ASC"
block|,
literal|""
block|,
literal|"select \"store\".\"store_type\" as \"c0\" from \"store\" as \"store\" where UPPER(\"store\".\"store_type\") = UPPER('Gender') group by \"store\".\"store_type\" order by \"store\".\"store_type\" ASC"
block|,
literal|""
block|,
literal|"select \"product_class\".\"product_family\" as \"c0\" from \"product\" as \"product\", \"product_class\" as \"product_class\" where \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" and UPPER(\"product_class\".\"product_family\") = UPPER('Gender') group by \"product_class\".\"product_family\" order by \"product_class\".\"product_family\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"media_type\") = UPPER('Gender') group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"promotion_name\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"promotion_name\") = UPPER('Gender') group by \"promotion\".\"promotion_name\" order by \"promotion\".\"promotion_name\" ASC"
block|,
literal|""
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" where UPPER(\"promotion\".\"media_type\") = UPPER('No Media') group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|"c0=No Media\n"
block|,
literal|"select \"promotion\".\"media_type\" as \"c0\" from \"promotion\" as \"promotion\" group by \"promotion\".\"media_type\" order by \"promotion\".\"media_type\" ASC"
block|,
literal|"c0=Bulk Mail\n"
operator|+
literal|"c0=Cash Register Handout\n"
operator|+
literal|"c0=Daily Paper\n"
operator|+
literal|"c0=Daily Paper, Radio\n"
operator|+
literal|"c0=Daily Paper, Radio, TV\n"
operator|+
literal|"c0=In-Store Coupon\n"
operator|+
literal|"c0=No Media\n"
operator|+
literal|"c0=Product Attachment\n"
operator|+
literal|"c0=Radio\n"
operator|+
literal|"c0=Street Handout\n"
operator|+
literal|"c0=Sunday Paper\n"
operator|+
literal|"c0=Sunday Paper, Radio\n"
operator|+
literal|"c0=Sunday Paper, Radio, TV\n"
operator|+
literal|"c0=TV\n"
block|,
literal|"select count(distinct \"the_year\") from \"time_by_day\""
block|,
literal|"EXPR$0=2\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 group by \"time_by_day\".\"the_year\""
block|,
literal|"c0=1997; m0=266773.0000\n"
block|,
literal|"select \"time_by_day\".\"the_year\" as \"c0\", \"promotion\".\"media_type\" as \"c1\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"promotion\" as \"promotion\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\" group by \"time_by_day\".\"the_year\", \"promotion\".\"media_type\""
block|,
literal|"c0=1997; c1=Bulk Mail; m0=4320.0000\n"
operator|+
literal|"c0=1997; c1=Radio; m0=2454.0000\n"
operator|+
literal|"c0=1997; c1=Street Handout; m0=5753.0000\n"
operator|+
literal|"c0=1997; c1=TV; m0=3607.0000\n"
operator|+
literal|"c0=1997; c1=No Media; m0=195448.0000\n"
operator|+
literal|"c0=1997; c1=In-Store Coupon; m0=3798.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper, Radio, TV; m0=2726.0000\n"
operator|+
literal|"c0=1997; c1=Product Attachment; m0=7544.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper; m0=7738.0000\n"
operator|+
literal|"c0=1997; c1=Cash Register Handout; m0=6697.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper, Radio; m0=6891.0000\n"
operator|+
literal|"c0=1997; c1=Daily Paper, Radio, TV; m0=9513.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper, Radio; m0=5945.0000\n"
operator|+
literal|"c0=1997; c1=Sunday Paper; m0=4339.0000\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\", sum(\"inventory_fact_1997\".\"supply_time\") as \"m0\" from \"store\" as \"store\", \"inventory_fact_1997\" as \"inventory_fact_1997\" where \"inventory_fact_1997\".\"store_id\" = \"store\".\"store_id\" group by \"store\".\"store_country\""
block|,
literal|"c0=USA; m0=10425\n"
block|,
literal|"select \"sn\".\"desc\" as \"c0\" from (SELECT * FROM (VALUES (1, 'SameName')) AS \"t\" (\"id\", \"desc\")) as \"sn\" group by \"sn\".\"desc\" order by \"sn\".\"desc\" ASC NULLS LAST"
block|,
literal|"c0=SameName\n"
block|,
literal|"select \"the_year\", count(*) as c, min(\"the_month\") as m\n"
operator|+
literal|"from \"foodmart2\".\"time_by_day\"\n"
operator|+
literal|"group by \"the_year\"\n"
operator|+
literal|"order by 1, 2"
block|,
literal|"the_year=1997; C=365; M=April\n"
operator|+
literal|"the_year=1998; C=365; M=April\n"
block|,
literal|"select\n"
operator|+
literal|" \"store\".\"store_state\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_sales\") as \"m1\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_state\" in ('DF', 'WA')\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_state\", \"time_by_day\".\"the_year\""
block|,
literal|"c0=WA; c1=1997; m0=124366.0000; m1=263793.2200\n"
block|,
literal|"select count(distinct \"product_id\") from \"product\""
block|,
literal|"EXPR$0=1560\n"
block|,
literal|"select \"store\".\"store_name\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_sales\") as \"m0\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_name\" in ('Store 1', 'Store 10', 'Store 11', 'Store 15', 'Store 16', 'Store 24', 'Store 3', 'Store 7')\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_name\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\"\n"
block|,
literal|"c0=Store 7; c1=1997; m0=54545.2800\n"
operator|+
literal|"c0=Store 24; c1=1997; m0=54431.1400\n"
operator|+
literal|"c0=Store 16; c1=1997; m0=49634.4600\n"
operator|+
literal|"c0=Store 3; c1=1997; m0=52896.3000\n"
operator|+
literal|"c0=Store 15; c1=1997; m0=52644.0700\n"
operator|+
literal|"c0=Store 11; c1=1997; m0=55058.7900\n"
block|,
literal|"select \"customer\".\"yearly_income\" as \"c0\","
operator|+
literal|" \"customer\".\"education\" as \"c1\"\n"
operator|+
literal|"from \"customer\" as \"customer\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|" and ((not (\"customer\".\"yearly_income\" in ('$10K - $30K', '$50K - $70K'))\n"
operator|+
literal|" or (\"customer\".\"yearly_income\" is null)))\n"
operator|+
literal|"group by \"customer\".\"yearly_income\",\n"
operator|+
literal|" \"customer\".\"education\"\n"
operator|+
literal|"order by \"customer\".\"yearly_income\" ASC NULLS LAST,\n"
operator|+
literal|" \"customer\".\"education\" ASC NULLS LAST"
block|,
literal|"c0=$110K - $130K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=Graduate Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=High School Degree\n"
operator|+
literal|"c0=$110K - $130K; c1=Partial College\n"
operator|+
literal|"c0=$110K - $130K; c1=Partial High School\n"
operator|+
literal|"c0=$130K - $150K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=Graduate Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=High School Degree\n"
operator|+
literal|"c0=$130K - $150K; c1=Partial College\n"
operator|+
literal|"c0=$130K - $150K; c1=Partial High School\n"
operator|+
literal|"c0=$150K +; c1=Bachelors Degree\n"
operator|+
literal|"c0=$150K +; c1=Graduate Degree\n"
operator|+
literal|"c0=$150K +; c1=High School Degree\n"
operator|+
literal|"c0=$150K +; c1=Partial College\n"
operator|+
literal|"c0=$150K +; c1=Partial High School\n"
operator|+
literal|"c0=$30K - $50K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=Graduate Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=High School Degree\n"
operator|+
literal|"c0=$30K - $50K; c1=Partial College\n"
operator|+
literal|"c0=$30K - $50K; c1=Partial High School\n"
operator|+
literal|"c0=$70K - $90K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=Graduate Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=High School Degree\n"
operator|+
literal|"c0=$70K - $90K; c1=Partial College\n"
operator|+
literal|"c0=$70K - $90K; c1=Partial High School\n"
operator|+
literal|"c0=$90K - $110K; c1=Bachelors Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=Graduate Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=High School Degree\n"
operator|+
literal|"c0=$90K - $110K; c1=Partial College\n"
operator|+
literal|"c0=$90K - $110K; c1=Partial High School\n"
block|,
literal|"ignore:select \"time_by_day\".\"the_year\" as \"c0\", \"product_class\".\"product_family\" as \"c1\", \"customer\".\"state_province\" as \"c2\", \"customer\".\"city\" as \"c3\", sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\" from \"time_by_day\" as \"time_by_day\", \"sales_fact_1997\" as \"sales_fact_1997\", \"product_class\" as \"product_class\", \"product\" as \"product\", \"customer\" as \"customer\" where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\" and \"time_by_day\".\"the_year\" = 1997 and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\" and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\" and \"product_class\".\"product_family\" = 'Drink' and \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\" and \"customer\".\"state_province\" = 'WA' and \"customer\".\"city\" in ('Anacortes', 'Ballard', 'Bellingham', 'Bremerton', 'Burien', 'Edmonds', 'Everett', 'Issaquah', 'Kirkland', 'Lynnwood', 'Marysville', 'Olympia', 'Port Orchard', 'Puyallup', 'Redmond', 'Renton', 'Seattle', 'Sedro Woolley', 'Spokane', 'Tacoma', 'Walla Walla', 'Yakima') group by \"time_by_day\".\"the_year\", \"product_class\".\"product_family\", \"customer\".\"state_province\", \"customer\".\"city\""
block|,
literal|"c0=1997; c1=Drink; c2=WA; c3=Sedro Woolley; m0=58.0000\n"
block|,
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"store_cost\") as \"m0\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m1\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m2\",\n"
operator|+
literal|" sum((case when \"sales_fact_1997\".\"promotion_id\" = 0 then 0\n"
operator|+
literal|"     else \"sales_fact_1997\".\"store_sales\" end)) as \"m3\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"store\".\"store_country\", \"time_by_day\".\"the_year\""
block|,
literal|"c0=USA; c1=1997; m0=225627.2336; m1=86837; m2=5581; m3=151211.2100\n"
block|,
comment|// query 6077
comment|// disabled (runs out of memory)
literal|"ignore:select \"time_by_day\".\"the_year\" as \"c0\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m0\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and (((\"product\".\"brand_name\" = 'Cormorant'\n"
operator|+
literal|"   and \"product_class\".\"product_subcategory\" = 'Pot Scrubbers'\n"
operator|+
literal|"   and \"product_class\".\"product_category\" = 'Kitchen Products'\n"
operator|+
literal|"   and \"product_class\".\"product_department\" = 'Household'\n"
operator|+
literal|"   and \"product_class\".\"product_family\" = 'Non-Consumable')\n"
operator|+
literal|" or (\"product\".\"brand_name\" = 'Denny'\n"
operator|+
literal|"   and \"product_class\".\"product_subcategory\" = 'Pot Scrubbers'\n"
operator|+
literal|"   and \"product_class\".\"product_category\" = 'Kitchen Products'\n"
operator|+
literal|"   and \"product_class\".\"product_department\" = 'Household'\n"
operator|+
literal|"   and \"product_class\".\"product_family\" = 'Non-Consumable')\n"
operator|+
literal|" or (\"product\".\"brand_name\" = 'High Quality'\n"
operator|+
literal|"   and \"product_class\".\"product_subcategory\" = 'Pot Scrubbers'\n"
operator|+
literal|"   and \"product_class\".\"product_category\" = 'Kitchen Products'\n"
operator|+
literal|"   and \"product_class\".\"product_department\" = 'Household'\n"
operator|+
literal|"   and \"product_class\".\"product_family\" = 'Non-Consumable')\n"
operator|+
literal|" or (\"product\".\"brand_name\" = 'Red Wing'\n"
operator|+
literal|"   and \"product_class\".\"product_subcategory\" = 'Pot Scrubbers'\n"
operator|+
literal|"   and \"product_class\".\"product_category\" = 'Kitchen Products'\n"
operator|+
literal|"   and \"product_class\".\"product_department\" = 'Household'\n"
operator|+
literal|"   and \"product_class\".\"product_family\" = 'Non-Consumable'))\n"
operator|+
literal|" or (\"product_class\".\"product_subcategory\" = 'Pots and Pans'\n"
operator|+
literal|"   and \"product_class\".\"product_category\" = 'Kitchen Products'\n"
operator|+
literal|"   and \"product_class\".\"product_department\" = 'Household'\n"
operator|+
literal|"   and \"product_class\".\"product_family\" = 'Non-Consumable'))\n"
operator|+
literal|"group by \"time_by_day\".\"the_year\"\n"
block|,
literal|"xxtodo"
block|,
comment|// query 6077, simplified
comment|// disabled (slow)
literal|"ignore:select count(\"sales_fact_1997\".\"customer_id\") as \"m0\"\n"
operator|+
literal|"from \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and ((\"product\".\"brand_name\" = 'Cormorant'\n"
operator|+
literal|"   and \"product_class\".\"product_subcategory\" = 'Pot Scrubbers')\n"
operator|+
literal|" or (\"product_class\".\"product_subcategory\" = 'Pots and Pans'))\n"
block|,
literal|"xxxx"
block|,
comment|// query 6077, simplified further
literal|"select count(distinct \"sales_fact_1997\".\"customer_id\") as \"m0\"\n"
operator|+
literal|"from \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and \"product\".\"brand_name\" = 'Cormorant'\n"
block|,
literal|"m0=1298"
block|,
comment|// query 193
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" \"time_by_day\".\"quarter\" as \"c2\",\n"
operator|+
literal|" \"product_class\".\"product_family\" as \"c3\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m0\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m1\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_country\" = 'USA'\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"time_by_day\".\"quarter\" = 'Q3'\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and \"product_class\".\"product_family\" = 'Food'\n"
operator|+
literal|"group by \"store\".\"store_country\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\",\n"
operator|+
literal|" \"time_by_day\".\"quarter\",\n"
operator|+
literal|" \"product_class\".\"product_family\""
block|,
literal|"c0=USA; c1=1997; c2=Q3; c3=Food; m0=15449; m1=2939"
block|,   }
decl_stmt|;
specifier|public
specifier|static
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
name|FOODMART_QUERIES
init|=
name|querify
argument_list|(
name|QUERIES
argument_list|)
decl_stmt|;
comment|/** Janino bug    *<a href="https://jira.codehaus.org/browse/JANINO-169">[JANINO-169]</a>    * running queries against the JDBC adapter. The bug is not present with    * janino-3.0.9 so the workaround in EnumerableRelImplementor was removed.    */
annotation|@
name|Test
specifier|public
name|void
name|testJanino169
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_id\" from \"foodmart\".\"time_by_day\" as \"t\"\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|730
argument_list|)
expr_stmt|;
block|}
comment|/** Tests 3-way AND.    *    *<p>With    *<a href="https://issues.apache.org/jira/browse/CALCITE-127">[CALCITE-127]    * EnumerableCalcRel can't support 3+ AND conditions</a>, the last condition    * is ignored and rows with deptno=10 are wrongly returned.</p>    */
annotation|@
name|Test
specifier|public
name|void
name|testAnd3
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\" from \"hr\".\"emps\"\n"
operator|+
literal|"where \"emps\".\"empid\"< 240\n"
operator|+
literal|"and \"salary\"> 7500.0"
operator|+
literal|"and \"emps\".\"deptno\"> 10\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=20"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a date literal against a JDBC data source. */
annotation|@
name|Test
specifier|public
name|void
name|testJdbcDate
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from (\n"
operator|+
literal|"  select 1 from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  where \"position_title\" = 'VP Country Manager'\n"
operator|+
literal|"  and \"birth_date\"< DATE '1950-01-01'\n"
operator|+
literal|"  and \"gender\" = 'F')"
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returns2
argument_list|(
literal|"C=1\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a timestamp literal against JDBC data source. */
annotation|@
name|Test
specifier|public
name|void
name|testJdbcTimestamp
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from (\n"
operator|+
literal|"  select 1 from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  where \"hire_date\"< TIMESTAMP '1996-06-05 00:00:00'\n"
operator|+
literal|"  and \"gender\" = 'F')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=287\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-281">[CALCITE-281]    * SQL type of EXTRACT is BIGINT but it is implemented as int</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testExtract
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"values extract(year from date '2008-2-23')"
argument_list|)
operator|.
name|returns
argument_list|(
name|resultSet
lambda|->
block|{
comment|// The following behavior is not quite correct. See
comment|//   [CALCITE-508] Reading from ResultSet before calling next()
comment|//   should throw SQLException not NoSuchElementException
comment|// for details.
try|try
block|{
specifier|final
name|BigDecimal
name|bigDecimal
init|=
name|resultSet
operator|.
name|getBigDecimal
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|bigDecimal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
literal|"java.util.NoSuchElementException: Expecting cursor "
operator|+
literal|"position to be Position.OK, actual "
operator|+
literal|"is Position.BEFORE_START"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|BigDecimal
name|bigDecimal
init|=
name|resultSet
operator|.
name|getBigDecimal
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bigDecimal
argument_list|,
name|equalTo
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|2008
argument_list|)
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractMonthFromTimestamp
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select extract(month from \"birth_date\") as c \n"
operator|+
literal|"from \"foodmart\".\"employee\" where \"employee_id\"=1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=8\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearFromTimestamp
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select extract(year from \"birth_date\") as c \n"
operator|+
literal|"from \"foodmart\".\"employee\" where \"employee_id\"=1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=1961\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractFromInterval
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select extract(month from interval '2-3' year to month) as c \n"
operator|+
literal|"from \"foodmart\".\"employee\" where \"employee_id\"=1"
argument_list|)
comment|// disable for MySQL, H2; cannot handle EXTRACT yet
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
operator|&&
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|H2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1188">[CALCITE-1188]    * NullPointerException when EXTRACT is applied to NULL date field</a>.    * The problem occurs when EXTRACT appears in both SELECT and WHERE ... IN    * clauses, the latter with at least two values. */
annotation|@
name|Test
specifier|public
name|void
name|testExtractOnNullDateField
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  extract(year from \"end_date\"), \"hire_date\", \"birth_date\"\n"
operator|+
literal|"from \"foodmart\".\"employee\"\n"
operator|+
literal|"where extract(year from \"end_date\") in (1994, 1995, 1996)\n"
operator|+
literal|"group by\n"
operator|+
literal|"  extract(year from \"end_date\"), \"hire_date\", \"birth_date\"\n"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
name|sql
operator|+
literal|"\n"
operator|+
literal|"limit 10000"
decl_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"employee\"\n"
operator|+
literal|"where extract(year from \"end_date\") in (1994, 1995, 1996)"
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql2
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql3
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorDate
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select floor(timestamp '2011-9-14 19:27:23' to month) as c \n"
operator|+
literal|"from \"foodmart\".\"employee\" limit 1"
argument_list|)
comment|// disable for MySQL; birth_date suffers timezone shift
comment|// disable for H2; Calcite generates incorrect FLOOR syntax
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
operator|&&
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|H2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=2011-09-01 00:00:00\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-387">[CALCITE-387]    * CompileException when cast TRUE to nullable boolean</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTrue
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
decl_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select case when deptno = 10 then null else true end as x\n"
operator|+
literal|"from (values (10), (20)) as t(deptno)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=null"
argument_list|,
literal|"X=true"
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select case when deptno = 10 then null else 100 end as x\n"
operator|+
literal|"from (values (10), (20)) as t(deptno)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=null"
argument_list|,
literal|"X=100"
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select case when deptno = 10 then null else 'xy' end as x\n"
operator|+
literal|"from (values (10), (20)) as t(deptno)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=null"
argument_list|,
literal|"X=xy"
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for self-join. Left and right children of the join are the same    * relational expression. */
annotation|@
name|Test
specifier|public
name|void
name|testSelfJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from (\n"
operator|+
literal|"  select 1 from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  join \"foodmart\".\"employee\" as e2 using (\"position_title\"))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=247149\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Self-join on different columns, select a different column, and sort and    * limit on yet another column. */
annotation|@
name|Test
specifier|public
name|void
name|testSelfJoinDifferentColumns
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select e1.\"full_name\"\n"
operator|+
literal|"  from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  join \"foodmart\".\"employee\" as e2 on e1.\"first_name\" = e2.\"last_name\"\n"
operator|+
literal|"order by e1.\"last_name\" limit 3"
argument_list|)
comment|// disable for H2; gives "Unexpected code path" internal error
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|H2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"full_name=James Aguilar\n"
operator|+
literal|"full_name=Carol Amyotte\n"
operator|+
literal|"full_name=Terry Anderson\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2029">[CALCITE-2029]    * Query with "is distinct from" condition in where or join clause fails    * with AssertionError: Cast for just nullability not allowed</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testIsNotDistinctInFilter
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"  from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  where e1.\"last_name\" is distinct from e1.\"last_name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2029">[CALCITE-2029]    * Query with "is distinct from" condition in where or join clause fails    * with AssertionError: Cast for just nullability not allowed</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testMixedEqualAndIsNotDistinctJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"  from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  join \"foodmart\".\"employee\" as e2 on\n"
operator|+
literal|"  e1.\"first_name\" = e1.\"first_name\"\n"
operator|+
literal|"  and e1.\"last_name\" is distinct from e2.\"last_name\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** A join that has both equi and non-equi conditions.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-371">[CALCITE-371]    * Cannot implement JOIN whose ON clause contains mixed equi and theta</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testEquiThetaJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"empid\", d.\"name\", e.\"name\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"join \"hr\".\"depts\" as d\n"
operator|+
literal|"on e.\"deptno\" = d.\"deptno\"\n"
operator|+
literal|"and e.\"name\"<> d.\"name\"\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; name=Sales; name=Bill\n"
operator|+
literal|"empid=150; name=Sales; name=Sebastian\n"
operator|+
literal|"empid=110; name=Sales; name=Theodore\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-451">[CALCITE-451]    * Implement theta join, inner and outer, in enumerable convention</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testThetaJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"empid\", d.\"name\", e.\"name\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"left join \"hr\".\"depts\" as d\n"
operator|+
literal|"on e.\"deptno\"< d.\"deptno\"\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Marketing; name=Bill"
argument_list|,
literal|"empid=100; name=HR; name=Bill"
argument_list|,
literal|"empid=200; name=Marketing; name=Eric"
argument_list|,
literal|"empid=200; name=HR; name=Eric"
argument_list|,
literal|"empid=150; name=Marketing; name=Sebastian"
argument_list|,
literal|"empid=150; name=HR; name=Sebastian"
argument_list|,
literal|"empid=110; name=Marketing; name=Theodore"
argument_list|,
literal|"empid=110; name=HR; name=Theodore"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-35">[CALCITE-35]    * Support parenthesized sub-clause in JOIN</a>. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testJoinJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|"   \"product_class\".\"product_family\" as \"c0\",\n"
operator|+
literal|"   \"product_class\".\"product_department\" as \"c1\",\n"
operator|+
literal|"   \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|"   \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|"   \"customer\".\"city\" as \"c4\"\n"
operator|+
literal|"from\n"
operator|+
literal|"   \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"join (\"product\" as \"product\"\n"
operator|+
literal|"     join \"product_class\" as \"product_class\"\n"
operator|+
literal|"     on \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\")\n"
operator|+
literal|"on  \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"join \"customer\" as \"customer\"\n"
operator|+
literal|"on  \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"join \"promotion\" as \"promotion\"\n"
operator|+
literal|"on \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\"\n"
operator|+
literal|"where (\"promotion\".\"media_type\" = 'Radio'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'TV'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Sunday Paper'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Street Handout')\n"
operator|+
literal|" and (\"product_class\".\"product_family\" = 'Drink')\n"
operator|+
literal|" and (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\""
operator|+
literal|" = 'WA' and \"customer\".\"city\" = 'Bellingham')\n"
operator|+
literal|"group by \"product_class\".\"product_family\",\n"
operator|+
literal|"   \"product_class\".\"product_department\",\n"
operator|+
literal|"   \"customer\".\"country\",\n"
operator|+
literal|"   \"customer\".\"state_province\",\n"
operator|+
literal|"   \"customer\".\"city\"\n"
operator|+
literal|"order by ISNULL(\"product_class\".\"product_family\") ASC,   \"product_class\".\"product_family\" ASC,\n"
operator|+
literal|"   ISNULL(\"product_class\".\"product_department\") ASC,   \"product_class\".\"product_department\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"country\") ASC,   \"customer\".\"country\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"state_province\") ASC,   \"customer\".\"state_province\" ASC,\n"
operator|+
literal|"   ISNULL(\"customer\".\"city\") ASC,   \"customer\".\"city\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| c0    | c1                  | c2  | c3   | c4         |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| Drink | Alcoholic Beverages | USA | WA   | Bellingham |\n"
operator|+
literal|"| Drink | Dairy               | USA | WA   | Bellingham |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+"
argument_list|)
expr_stmt|;
block|}
comment|/** Four-way join. Used to take 80 seconds. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testJoinFiveWay
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" \"product_class\".\"product_family\" as \"c2\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m0\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_country\" = 'USA'\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"group by \"store\".\"store_country\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\",\n"
operator|+
literal|" \"product_class\".\"product_family\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregateRel(group=[{0, 1, 2}], m0=[COUNT($3)])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..61=[{inputs}], c0=[$t19], c1=[$t4], c2=[$t46], product_id=[$t34])\n"
operator|+
literal|"    EnumerableJoinRel(condition=[=($35, $0)], joinType=[inner])\n"
operator|+
literal|"      EnumerableCalcRel(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], proj#0..9=[{exprs}], $condition=[$t12])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"      EnumerableCalcRel(expr#0..51=[{inputs}], proj#0..23=[{exprs}], product_id=[$t44], time_id=[$t45], customer_id=[$t46], promotion_id=[$t47], store_id0=[$t48], store_sales=[$t49], store_cost=[$t50], unit_sales=[$t51], product_class_id=[$t24], product_subcategory=[$t25], product_category=[$t26], product_department=[$t27], product_family=[$t28], product_class_id0=[$t29], product_id0=[$t30], brand_name=[$t31], product_name=[$t32], SKU=[$t33], SRP=[$t34], gross_weight=[$t35], net_weight=[$t36], recyclable_package=[$t37], low_fat=[$t38], units_per_case=[$t39], cases_per_pallet=[$t40], shelf_width=[$t41], shelf_height=[$t42], shelf_depth=[$t43])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($48, $0)], joinType=[inner])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..23=[{inputs}], expr#24=['USA'], expr#25=[=($t9, $t24)], proj#0..23=[{exprs}], $condition=[$t25])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, store]])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..27=[{inputs}], proj#0..4=[{exprs}], product_class_id0=[$t13], product_id=[$t14], brand_name=[$t15], product_name=[$t16], SKU=[$t17], SRP=[$t18], gross_weight=[$t19], net_weight=[$t20], recyclable_package=[$t21], low_fat=[$t22], units_per_case=[$t23], cases_per_pallet=[$t24], shelf_width=[$t25], shelf_height=[$t26], shelf_depth=[$t27], product_id0=[$t5], time_id=[$t6], customer_id=[$t7], promotion_id=[$t8], store_id=[$t9], store_sales=[$t10], store_cost=[$t11], unit_sales=[$t12])\n"
operator|+
literal|"            EnumerableJoinRel(condition=[=($13, $0)], joinType=[inner])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, product_class]])\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($0, $9)], joinType=[inner])\n"
operator|+
literal|"                EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"                EnumerableTableScan(table=[[foodmart2, product]])\n"
operator|+
literal|"\n"
operator|+
literal|"]>"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| c0    | c1                  | c2  | c3   | c4         |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| Drink | Alcoholic Beverages | USA | WA   | Bellingham |\n"
operator|+
literal|"| Drink | Dairy               | USA | WA   | Bellingham |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a simple (primary key to primary key) N-way join, with arbitrary    * N. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinManyWay
parameter_list|()
block|{
comment|// Timings without LoptOptimizeJoinRule
comment|//    N  Time
comment|//   == =====
comment|//    6     2
comment|//   10    10
comment|//   11    19
comment|//   12    36
comment|//   13   116 - OOM did not complete
name|checkJoinNWay
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkJoinNWay
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|checkJoinNWay
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkJoinNWay
parameter_list|(
name|int
name|n
parameter_list|)
block|{
assert|assert
name|n
operator|>
literal|0
assert|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"select count(*)"
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|i
operator|==
literal|0
condition|?
literal|"\nfrom "
else|:
literal|",\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"hr\".\"depts\" as d"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|i
operator|==
literal|1
condition|?
literal|"\nwhere"
else|:
literal|"\nand"
argument_list|)
operator|.
name|append
argument_list|(
literal|" d"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|".\"deptno\" = d"
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|append
argument_list|(
literal|".\"deptno\""
argument_list|)
expr_stmt|;
block|}
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a list of (query, expected) pairs. The expected result is    * sometimes null. */
specifier|private
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|querify
parameter_list|(
name|String
index|[]
name|queries1
parameter_list|)
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
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|queries1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|query
init|=
name|queries1
index|[
name|i
index|]
decl_stmt|;
name|String
name|expected
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|queries1
operator|.
name|length
operator|&&
name|queries1
index|[
name|i
operator|+
literal|1
index|]
operator|!=
literal|null
operator|&&
operator|!
name|queries1
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"select"
argument_list|)
condition|)
block|{
name|expected
operator|=
name|queries1
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|query
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/** A selection of queries generated by Mondrian. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testCloneQueries
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
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
name|FOODMART_QUERIES
argument_list|)
control|)
block|{
try|try
block|{
comment|// uncomment to run specific queries:
comment|//      if (query.i != FOODMART_QUERIES.size() - 1) continue;
specifier|final
name|String
name|sql
init|=
name|query
operator|.
name|e
operator|.
name|left
decl_stmt|;
if|if
condition|(
name|sql
operator|.
name|startsWith
argument_list|(
literal|"ignore:"
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|String
name|expected
init|=
name|query
operator|.
name|e
operator|.
name|right
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertQuery
name|query1
init|=
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sql
operator|.
name|contains
argument_list|(
literal|"order by"
argument_list|)
condition|)
block|{
name|query1
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query1
operator|.
name|returnsUnordered
argument_list|(
name|expected
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while running query #"
operator|+
name|query
operator|.
name|i
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Tests accessing a column in a JDBC source whose type is ARRAY. */
annotation|@
name|Test
specifier|public
name|void
name|testArray
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|url
init|=
name|MultiJdbcSchemaJoinTest
operator|.
name|TempDb
operator|.
name|INSTANCE
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Connection
name|baseConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Statement
name|baseStmt
init|=
name|baseConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE ARR_TABLE (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS INTEGER ARRAY)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO ARR_TABLE VALUES (1, ARRAY[1,2,3])"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE ARR_TABLE2 (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS INTEGER ARRAY,\n"
operator|+
literal|"VALVALS VARCHAR(10) ARRAY)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO ARR_TABLE2 VALUES (1, ARRAY[1,2,3], ARRAY['x','y'])"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|baseConnection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'BASEJDBC',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'BASEJDBC',\n"
operator|+
literal|"       jdbcDriver: '"
operator|+
name|jdbcDriver
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       jdbcUrl: '"
operator|+
name|url
operator|+
literal|"',\n"
operator|+
literal|"       jdbcCatalog: null,\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|Connection
name|calciteConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|Statement
name|calciteStatement
init|=
name|calciteConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"SELECT ID, VALS FROM ARR_TABLE"
decl_stmt|;
name|ResultSet
name|rs
init|=
name|calciteStatement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Array
name|array
init|=
name|rs
operator|.
name|getArray
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|,
operator|(
name|int
index|[]
operator|)
name|array
operator|.
name|getArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|rs
operator|=
name|calciteStatement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT ID, CARDINALITY(VALS), VALS[2] FROM ARR_TABLE"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|rs
operator|=
name|calciteStatement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM ARR_TABLE2"
argument_list|)
expr_stmt|;
specifier|final
name|ResultSetMetaData
name|metaData
init|=
name|rs
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getColumnTypeName
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getColumnTypeName
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"INTEGER ARRAY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getColumnTypeName
argument_list|(
literal|3
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"VARCHAR(10) ARRAY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|getArray
argument_list|(
literal|2
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|getArray
argument_list|(
literal|3
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|calciteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the {@code CARDINALITY} function applied to an array column. */
annotation|@
name|Test
specifier|public
name|void
name|testArray2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", cardinality(\"employees\") as c\n"
operator|+
literal|"from \"hr\".\"depts\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; C=2"
argument_list|,
literal|"deptno=30; C=0"
argument_list|,
literal|"deptno=40; C=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests JDBC support for nested arrays. */
annotation|@
name|Test
specifier|public
name|void
name|testNestedArray
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select \"empid\",\n"
operator|+
literal|"  array[\n"
operator|+
literal|"    array['x', 'y', 'z'],\n"
operator|+
literal|"    array[\"name\"]] as a\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"[[x, y, z], [Bill]]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Array
name|array
init|=
name|resultSet
operator|.
name|getArray
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|array
operator|.
name|getBaseType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Types
operator|.
name|ARRAY
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|arrayValues
init|=
operator|(
name|Object
index|[]
operator|)
name|array
operator|.
name|getArray
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|arrayValues
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Array
name|subArray
init|=
operator|(
name|Array
operator|)
name|arrayValues
index|[
literal|0
index|]
decl_stmt|;
name|assertThat
argument_list|(
name|subArray
operator|.
name|getBaseType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|subArrayValues
init|=
operator|(
name|Object
index|[]
operator|)
name|subArray
operator|.
name|getArray
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|subArrayValues
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|subArrayValues
index|[
literal|2
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ResultSet
name|subResultSet
init|=
name|subArray
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|subResultSet
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
name|subResultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|string
init|=
name|subResultSet
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|string
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|equalTo
argument_list|(
literal|"invalid column ordinal: 2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|subResultSet
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
name|subResultSet
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
name|subResultSet
operator|.
name|isAfterLast
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|subResultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|subResultSet
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|subResultSet
operator|.
name|isAfterLast
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArrayConstructor
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select array[1,2] as a from (values (1))"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=[1, 2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetConstructor
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select multiset[1,2] as a from (values (1))"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=[1, 2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetQuery
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select multiset(\n"
operator|+
literal|"  select \"deptno\", \"empid\" from \"hr\".\"emps\") as a\n"
operator|+
literal|"from (values (1))"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=[{10, 100}, {20, 200}, {10, 150}, {10, 110}]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultisetQueryWithSingleColumn
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select multiset(\n"
operator|+
literal|"  select \"deptno\" from \"hr\".\"emps\") as a\n"
operator|+
literal|"from (values (1))"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=[{10}, {20}, {10}, {10}]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArray
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select*from unnest(array[1,2])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=1"
argument_list|,
literal|"EXPR$0=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArrayWithOrdinality
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select*from unnest(array[10,20]) with ordinality as t(i, o)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"I=10; O=1"
argument_list|,
literal|"I=20; O=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestMultiset
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select*from unnest(multiset[1,2]) as t(c)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=1"
argument_list|,
literal|"C=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestMultiset2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select*from unnest(\n"
operator|+
literal|" select \"employees\" from \"hr\".\"depts\"\n"
operator|+
literal|" where \"deptno\" = 10)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000"
argument_list|,
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2381">[CALCITE-2391]    * Aggregate query with UNNEST or LATERAL fails with    * ClassCastException</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testAggUnnestColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(d.\"name\") as c\n"
operator|+
literal|"from \"hr\".\"depts\" as d,\n"
operator|+
literal|" UNNEST(d.\"employees\") as e"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArrayElement
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select element(\"employees\") from \"hr\".\"depts\"\n"
operator|+
literal|"where cardinality(\"employees\")< 2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0={200, 20, Eric, 8000.0, 500}"
argument_list|,
literal|"EXPR$0=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLateral
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\",\n"
operator|+
literal|" LATERAL (select * from \"hr\".\"depts\" where \"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000; deptno0=10; name0=Sales; employees=[{100, 10, Bill, 10000.0, 1000}, {150, 10, Sebastian, 7000.0, null}]; location={-122, 38}"
argument_list|,
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250; deptno0=10; name0=Sales; employees=[{100, 10, Bill, 10000.0, 1000}, {150, 10, Sebastian, 7000.0, null}]; location={-122, 38}"
argument_list|,
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null; deptno0=10; name0=Sales; employees=[{100, 10, Bill, 10000.0, 1000}, {150, 10, Sebastian, 7000.0, null}]; location={-122, 38}"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-531">[CALCITE-531]    * Window function does not work in LATERAL</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testLateralWithOver
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"emps\".\"name\", d.\"deptno\", d.m\n"
operator|+
literal|"from \"hr\".\"emps\",\n"
operator|+
literal|"  LATERAL (\n"
operator|+
literal|"    select \"depts\".\"deptno\",\n"
operator|+
literal|"      max(\"deptno\" + \"emps\".\"empid\") over (\n"
operator|+
literal|"        partition by \"emps\".\"deptno\") as m\n"
operator|+
literal|"     from \"hr\".\"depts\"\n"
operator|+
literal|"     where \"emps\".\"deptno\" = \"depts\".\"deptno\") as d"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; deptno=10; M=190"
argument_list|,
literal|"name=Bill; deptno=30; M=190"
argument_list|,
literal|"name=Bill; deptno=40; M=190"
argument_list|,
literal|"name=Eric; deptno=10; M=240"
argument_list|,
literal|"name=Eric; deptno=30; M=240"
argument_list|,
literal|"name=Eric; deptno=40; M=240"
argument_list|,
literal|"name=Sebastian; deptno=10; M=190"
argument_list|,
literal|"name=Sebastian; deptno=30; M=190"
argument_list|,
literal|"name=Sebastian; deptno=40; M=190"
argument_list|,
literal|"name=Theodore; deptno=10; M=190"
argument_list|,
literal|"name=Theodore; deptno=30; M=190"
argument_list|,
literal|"name=Theodore; deptno=40; M=190"
argument_list|)
expr_stmt|;
block|}
comment|/** Per SQL std, UNNEST is implicitly LATERAL. */
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArrayColumn
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select d.\"name\", e.*\n"
operator|+
literal|"from \"hr\".\"depts\" as d,\n"
operator|+
literal|" UNNEST(d.\"employees\") as e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=HR; empid=200; deptno=20; name0=Eric; salary=8000.0; commission=500"
argument_list|,
literal|"name=Sales; empid=100; deptno=10; name0=Bill; salary=10000.0; commission=1000"
argument_list|,
literal|"name=Sales; empid=150; deptno=10; name0=Sebastian; salary=7000.0; commission=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArrayScalarArray
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select d.\"name\", e.*\n"
operator|+
literal|"from \"hr\".\"depts\" as d,\n"
operator|+
literal|" UNNEST(d.\"employees\", array[1, 2]) as e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=HR; empid=200; deptno=20; name0=Eric; salary=8000.0; commission=500; EXPR$1=1"
argument_list|,
literal|"name=HR; empid=200; deptno=20; name0=Eric; salary=8000.0; commission=500; EXPR$1=2"
argument_list|,
literal|"name=Sales; empid=100; deptno=10; name0=Bill; salary=10000.0; commission=1000; EXPR$1=1"
argument_list|,
literal|"name=Sales; empid=100; deptno=10; name0=Bill; salary=10000.0; commission=1000; EXPR$1=2"
argument_list|,
literal|"name=Sales; empid=150; deptno=10; name0=Sebastian; salary=7000.0; commission=null; EXPR$1=1"
argument_list|,
literal|"name=Sales; empid=150; deptno=10; name0=Sebastian; salary=7000.0; commission=null; EXPR$1=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArrayScalarArrayAliased
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select d.\"name\", e.*\n"
operator|+
literal|"from \"hr\".\"depts\" as d,\n"
operator|+
literal|" UNNEST(d.\"employees\", array[1, 2]) as e (ei, d, n, s, c, i)\n"
operator|+
literal|"where ei + i> 151"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=HR; EI=200; D=20; N=Eric; S=8000.0; C=500; I=1"
argument_list|,
literal|"name=HR; EI=200; D=20; N=Eric; S=8000.0; C=500; I=2"
argument_list|,
literal|"name=Sales; EI=150; D=10; N=Sebastian; S=7000.0; C=null; I=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestArrayScalarArrayWithOrdinal
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select d.\"name\", e.*\n"
operator|+
literal|"from \"hr\".\"depts\" as d,\n"
operator|+
literal|" UNNEST(d.\"employees\", array[1, 2]) with ordinality as e (ei, d, n, s, c, i, o)\n"
operator|+
literal|"where ei + i> 151"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=HR; EI=200; D=20; N=Eric; S=8000.0; C=500; I=1; O=2"
argument_list|,
literal|"name=HR; EI=200; D=20; N=Eric; S=8000.0; C=500; I=2; O=4"
argument_list|,
literal|"name=Sales; EI=150; D=10; N=Sebastian; S=7000.0; C=null; I=2; O=5"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1250">[CALCITE-1250]    * UNNEST applied to MAP data type</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testUnnestItemsInMap
parameter_list|()
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from unnest(MAP['a', 1, 'b', 2]) as um(k, v)"
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"K=a; V=1\n"
operator|+
literal|"K=b; V=2\n"
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestItemsInMapWithOrdinality
parameter_list|()
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from unnest(MAP['a', 1, 'b', 2]) with ordinality as um(k, v, i)"
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"K=a; V=1; I=1\n"
operator|+
literal|"K=b; V=2; I=2\n"
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnnestItemsInMapWithNoAliasAndAdditionalArgument
parameter_list|()
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from unnest(MAP['a', 1, 'b', 2], array[5, 6, 7])"
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|map
init|=
name|FlatLists
operator|.
name|of
argument_list|(
literal|"KEY=a; VALUE=1"
argument_list|,
literal|"KEY=b; VALUE=2"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|array
init|=
name|FlatLists
operator|.
name|of
argument_list|(
literal|" EXPR$1=5"
argument_list|,
literal|" EXPR$1=6"
argument_list|,
literal|" EXPR$1=7"
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|row
range|:
name|Linq4j
operator|.
name|product
argument_list|(
name|FlatLists
operator|.
name|of
argument_list|(
name|map
argument_list|,
name|array
argument_list|)
argument_list|)
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|";"
argument_list|)
operator|.
name|append
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|expected
init|=
name|b
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|withFoodMartQuery
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FoodMartQuerySet
name|set
init|=
name|FoodMartQuerySet
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|set
operator|.
name|queries
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|sql
argument_list|)
return|;
block|}
comment|/** Makes sure that a projection introduced by a call to    * {@link org.apache.calcite.rel.rules.JoinCommuteRule} does not    * manifest as an    * {@link org.apache.calcite.adapter.enumerable.EnumerableCalc} in the    * plan.    *    *<p>Test case for (not yet fixed)    *<a href="https://issues.apache.org/jira/browse/CALCITE-92">[CALCITE-92]    * Project should be optimized away, not converted to EnumerableCalcRel</a>.    */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testNoCalcBetweenJoins
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FoodMartQuerySet
name|set
init|=
name|FoodMartQuerySet
operator|.
name|instance
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|set
operator|.
name|queries
operator|.
name|get
argument_list|(
literal|16
argument_list|)
operator|.
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableSortRel(sort0=[$0], sort1=[$1], sort2=[$2], sort3=[$4], sort4=[$10], sort5=[$11], sort6=[$12], sort7=[$13], sort8=[$22], sort9=[$23], sort10=[$24], sort11=[$25], sort12=[$26], sort13=[$27], dir0=[Ascending-nulls-last], dir1=[Ascending-nulls-last], dir2=[Ascending-nulls-last], dir3=[Ascending-nulls-last], dir4=[Ascending-nulls-last], dir5=[Ascending-nulls-last], dir6=[Ascending-nulls-last], dir7=[Ascending-nulls-last], dir8=[Ascending-nulls-last], dir9=[Ascending-nulls-last], dir10=[Ascending-nulls-last], dir11=[Ascending-nulls-last], dir12=[Ascending-nulls-last], dir13=[Ascending-nulls-last])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..26=[{inputs}], proj#0..4=[{exprs}], c5=[$t4], c6=[$t5], c7=[$t6], c8=[$t7], c9=[$t8], c10=[$t9], c11=[$t10], c12=[$t11], c13=[$t12], c14=[$t13], c15=[$t14], c16=[$t15], c17=[$t16], c18=[$t17], c19=[$t18], c20=[$t19], c21=[$t20], c22=[$t21], c23=[$t22], c24=[$t23], c25=[$t24], c26=[$t25], c27=[$t26])\n"
operator|+
literal|"    EnumerableAggregateRel(group=[{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26}])\n"
operator|+
literal|"      EnumerableCalcRel(expr#0..80=[{inputs}], c0=[$t12], c1=[$t10], c2=[$t9], c3=[$t0], fullname=[$t28], c6=[$t19], c7=[$t17], c8=[$t22], c9=[$t18], c10=[$t46], c11=[$t44], c12=[$t43], c13=[$t40], c14=[$t38], c15=[$t47], c16=[$t52], c17=[$t53], c18=[$t54], c19=[$t55], c20=[$t56], c21=[$t42], c22=[$t80], c23=[$t79], c24=[$t78], c25=[$t77], c26=[$t63], c27=[$t64])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($61, $76)], joinType=[inner])\n"
operator|+
literal|"          EnumerableJoinRel(condition=[=($29, $62)], joinType=[inner])\n"
operator|+
literal|"            EnumerableJoinRel(condition=[=($33, $37)], joinType=[inner])\n"
operator|+
literal|"              EnumerableCalcRel(expr#0..36=[{inputs}], customer_id=[$t8], account_num=[$t9], lname=[$t10], fname=[$t11], mi=[$t12], address1=[$t13], address2=[$t14], address3=[$t15], address4=[$t16], city=[$t17], state_province=[$t18], postal_code=[$t19], country=[$t20], customer_region_id=[$t21], phone1=[$t22], phone2=[$t23], birthdate=[$t24], marital_status=[$t25], yearly_income=[$t26], gender=[$t27], total_children=[$t28], num_children_at_home=[$t29], education=[$t30], date_accnt_opened=[$t31], member_card=[$t32], occupation=[$t33], houseowner=[$t34], num_cars_owned=[$t35], fullname=[$t36], product_id=[$t0], time_id=[$t1], customer_id0=[$t2], promotion_id=[$t3], store_id=[$t4], store_sales=[$t5], store_cost=[$t6], unit_sales=[$t7])\n"
operator|+
literal|"                EnumerableJoinRel(condition=[=($2, $8)], joinType=[inner])\n"
operator|+
literal|"                  EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"                  EnumerableTableScan(table=[[foodmart2, customer]])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, store]])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, product]])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, product_class]])\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a 3-way join is re-ordered so that join conditions can be    * applied. The plan must not contain cartesian joins.    * {@link org.apache.calcite.rel.rules.JoinPushThroughJoinRule} makes this    * possible. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testExplainJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|FOODMART_QUERIES
operator|.
name|get
argument_list|(
literal|48
argument_list|)
operator|.
name|left
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregateRel(group=[{}], m0=[COUNT($0)])\n"
operator|+
literal|"  EnumerableAggregateRel(group=[{0}])\n"
operator|+
literal|"    EnumerableCalcRel(expr#0..27=[{inputs}], customer_id=[$t7])\n"
operator|+
literal|"      EnumerableJoinRel(condition=[=($13, $0)], joinType=[inner])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, product_class]])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($0, $9)], joinType=[inner])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..14=[{inputs}], expr#15=['Cormorant'], expr#16=[=($t2, $t15)], proj#0..14=[{exprs}], $condition=[$t16])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, product]]"
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a 3-way join is re-ordered so that join conditions can be    * applied. The plan is left-deep (agg_c_14_sales_fact_1997 the most    * rows, then time_by_day, then store). This makes for efficient    * hash-joins. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testExplainJoin2
parameter_list|()
throws|throws
name|IOException
block|{
name|withFoodMartQuery
argument_list|(
literal|2482
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableSortRel(sort0=[$0], sort1=[$1], dir0=[Ascending-nulls-last], dir1=[Ascending-nulls-last])\n"
operator|+
literal|"  EnumerableAggregateRel(group=[{0, 1}])\n"
operator|+
literal|"    EnumerableCalcRel(expr#0..5=[{inputs}], c0=[$t4], c1=[$t1])\n"
operator|+
literal|"      EnumerableJoinRel(condition=[=($3, $5)], joinType=[inner])\n"
operator|+
literal|"        EnumerableCalcRel(expr#0..3=[{inputs}], store_id=[$t2], store_country=[$t3], store_id0=[$t0], month_of_year=[$t1])\n"
operator|+
literal|"          EnumerableJoinRel(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"            EnumerableCalcRel(expr#0..10=[{inputs}], store_id=[$t2], month_of_year=[$t4])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, agg_c_14_sales_fact_1997]])\n"
operator|+
literal|"            EnumerableCalcRel(expr#0..23=[{inputs}], store_id=[$t0], store_country=[$t9])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, store]])\n"
operator|+
literal|"        EnumerableCalcRel(expr#0..9=[{inputs}], the_year=[$t4], month_of_year=[$t7])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** One of the most expensive foodmart queries. */
annotation|@
name|Ignore
comment|// OOME on Travis; works on most other machines
annotation|@
name|Test
specifier|public
name|void
name|testExplainJoin3
parameter_list|()
throws|throws
name|IOException
block|{
name|withFoodMartQuery
argument_list|(
literal|8
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableSortRel(sort0=[$0], sort1=[$1], sort2=[$2], sort3=[$4], dir0=[Ascending-nulls-last], dir1=[Ascending-nulls-last], dir2=[Ascending-nulls-last], dir3=[Ascending-nulls-last])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..8=[{inputs}], expr#9=['%Jeanne%'], expr#10=[LIKE($t4, $t9)], proj#0..4=[{exprs}], c5=[$t4], c6=[$t5], c7=[$t6], c8=[$t7], c9=[$t8], $condition=[$t10])\n"
operator|+
literal|"    EnumerableAggregateRel(group=[{0, 1, 2, 3, 4, 5, 6, 7, 8}])\n"
operator|+
literal|"      EnumerableCalcRel(expr#0..46=[{inputs}], c0=[$t12], c1=[$t10], c2=[$t9], c3=[$t0], fullname=[$t28], c6=[$t19], c7=[$t17], c8=[$t22], c9=[$t18])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($30, $37)], joinType=[inner])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..36=[{inputs}], customer_id=[$t8], account_num=[$t9], lname=[$t10], fname=[$t11], mi=[$t12], address1=[$t13], address2=[$t14], address3=[$t15], address4=[$t16], city=[$t17], state_province=[$t18], postal_code=[$t19], country=[$t20], customer_region_id=[$t21], phone1=[$t22], phone2=[$t23], birthdate=[$t24], marital_status=[$t25], yearly_income=[$t26], gender=[$t27], total_children=[$t28], num_children_at_home=[$t29], education=[$t30], date_accnt_opened=[$t31], member_card=[$t32], occupation=[$t33], houseowner=[$t34], num_cars_owned=[$t35], fullname=[$t36], product_id=[$t0], time_id=[$t1], customer_id0=[$t2], promotion_id=[$t3], store_id=[$t4], store_sales=[$t5], store_cost=[$t6], unit_sales=[$t7])\n"
operator|+
literal|"            EnumerableJoinRel(condition=[=($2, $8)], joinType=[inner])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, customer]])\n"
operator|+
literal|"          EnumerableCalcRel(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], proj#0..9=[{exprs}], $condition=[$t12])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, time_by_day]])"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that a relatively complex query on the foodmart schema creates    * an in-memory aggregate table and then uses it. */
annotation|@
name|Ignore
comment|// DO NOT CHECK IN
annotation|@
name|Test
specifier|public
name|void
name|testFoodmartLattice
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 8: select ... from customer, sales, time ... group by ...
specifier|final
name|FoodMartQuerySet
name|set
init|=
name|FoodMartQuerySet
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|final
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
name|query
init|=
name|set
operator|.
name|queries
operator|.
name|get
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART_WITH_LATTICE
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|pooled
argument_list|()
operator|.
name|query
argument_list|(
name|query
operator|.
name|sql
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..8=[{inputs}], c0=[$t3], c1=[$t2], c2=[$t1], c3=[$t0], c4=[$t8], c5=[$t8], c6=[$t6], c7=[$t4], c8=[$t7], c9=[$t5])\n"
operator|+
literal|"  EnumerableSort(sort0=[$3], sort1=[$2], sort2=[$1], sort3=[$8], dir0=[ASC-nulls-last], dir1=[ASC-nulls-last], dir2=[ASC-nulls-last], dir3=[ASC-nulls-last])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0, 1, 2, 3, 4, 5, 6, 7, 8}])\n"
operator|+
literal|"      EnumerableCalc(expr#0..9=[{inputs}], expr#10=[CAST($t0):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], expr#13=['%Jeanne%'], expr#14=[LIKE($t9, $t13)], expr#15=[AND($t12, $t14)], $f0=[$t1], $f1=[$t2], $f2=[$t3], $f3=[$t4], $f4=[$t5], $f5=[$t6], $f6=[$t7], $f7=[$t8], $f8=[$t9], $f9=[$t0], $condition=[$t15])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart, m{12, 18, 27, 28, 30, 35, 36, 37, 40, 46}]])"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for (not yet fixed)    *<a href="https://issues.apache.org/jira/browse/CALCITE-99">[CALCITE-99]    * Recognize semi-join that has high selectivity and push it down</a>. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testExplainJoin4
parameter_list|()
throws|throws
name|IOException
block|{
name|withFoodMartQuery
argument_list|(
literal|5217
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregateRel(group=[{0, 1, 2, 3}], m0=[COUNT($4)])\n"
operator|+
literal|"  EnumerableCalcRel(expr#0..69=[{inputs}], c0=[$t4], c1=[$t27], c2=[$t61], c3=[$t66], $f11=[$t11])\n"
operator|+
literal|"    EnumerableJoinRel(condition=[=($68, $69)], joinType=[inner])\n"
operator|+
literal|"      EnumerableCalcRel(expr#0..67=[{inputs}], proj#0..67=[{exprs}], $f68=[$t66])\n"
operator|+
literal|"        EnumerableJoinRel(condition=[=($11, $65)], joinType=[inner])\n"
operator|+
literal|"          EnumerableJoinRel(condition=[=($46, $59)], joinType=[inner])\n"
operator|+
literal|"            EnumerableCalcRel(expr#0..58=[{inputs}], $f0=[$t49], $f1=[$t50], $f2=[$t51], $f3=[$t52], $f4=[$t53], $f5=[$t54], $f6=[$t55], $f7=[$t56], $f8=[$t57], $f9=[$t58], $f10=[$t41], $f11=[$t42], $f12=[$t43], $f13=[$t44], $f14=[$t45], $f15=[$t46], $f16=[$t47], $f17=[$t48], $f18=[$t0], $f19=[$t1], $f20=[$t2], $f21=[$t3], $f22=[$t4], $f23=[$t5], $f24=[$t6], $f25=[$t7], $f26=[$t8], $f27=[$t9], $f28=[$t10], $f29=[$t11], $f30=[$t12], $f31=[$t13], $f32=[$t14], $f33=[$t15], $f34=[$t16], $f35=[$t17], $f36=[$t18], $f37=[$t19], $f38=[$t20], $f39=[$t21], $f40=[$t22], $f41=[$t23], $f42=[$t24], $f43=[$t25], $f44=[$t26], $f45=[$t27], $f46=[$t28], $f47=[$t29], $f48=[$t30], $f49=[$t31], $f50=[$t32], $f51=[$t33], $f52=[$t34], $f53=[$t35], $f54=[$t36], $f55=[$t37], $f56=[$t38], $f57=[$t39], $f58=[$t40])\n"
operator|+
literal|"              EnumerableJoinRel(condition=[=($41, $50)], joinType=[inner])\n"
operator|+
literal|"                EnumerableCalcRel(expr#0..48=[{inputs}], $f0=[$t25], $f1=[$t26], $f2=[$t27], $f3=[$t28], $f4=[$t29], $f5=[$t30], $f6=[$t31], $f7=[$t32], $f8=[$t33], $f9=[$t34], $f10=[$t35], $f11=[$t36], $f12=[$t37], $f13=[$t38], $f14=[$t39], $f15=[$t40], $f16=[$t41], $f17=[$t42], $f18=[$t43], $f19=[$t44], $f20=[$t45], $f21=[$t46], $f22=[$t47], $f23=[$t48], $f24=[$t8], $f25=[$t9], $f26=[$t10], $f27=[$t11], $f28=[$t12], $f29=[$t13], $f30=[$t14], $f31=[$t15], $f32=[$t16], $f33=[$t17], $f34=[$t18], $f35=[$t19], $f36=[$t20], $f37=[$t21], $f38=[$t22], $f39=[$t23], $f40=[$t24], $f41=[$t0], $f42=[$t1], $f43=[$t2], $f44=[$t3], $f45=[$t4], $f46=[$t5], $f47=[$t6], $f48=[$t7])\n"
operator|+
literal|"                  EnumerableJoinRel(condition=[=($14, $25)], joinType=[inner])\n"
operator|+
literal|"                    EnumerableJoinRel(condition=[=($1, $8)], joinType=[inner])\n"
operator|+
literal|"                      EnumerableTableScan(table=[[foodmart2, salary]])\n"
operator|+
literal|"                      EnumerableTableScan(table=[[foodmart2, employee]])\n"
operator|+
literal|"                    EnumerableTableScan(table=[[foodmart2, store]])\n"
operator|+
literal|"                EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"            EnumerableTableScan(table=[[foodmart2, position]])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, employee_closure]])\n"
operator|+
literal|"      EnumerableAggregateRel(group=[{0}])\n"
operator|+
literal|"        EnumerableValuesRel(tuples=[[{ 1 }, { 2 }, { 20 }, { 21 }, { 22 }, { 23 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 }, { 29 }, { 30 }, { 31 }, { 53 }, { 54 }, { 55 }, { 56 }, { 57 }, { 58 }, { 59 }, { 60 }, { 61 }, { 62 }, { 63 }, { 64 }, { 65 }, { 66 }, { 67 }, { 68 }, { 69 }, { 70 }, { 71 }, { 72 }, { 73 }, { 74 }, { 75 }, { 76 }, { 77 }, { 78 }, { 79 }, { 80 }, { 81 }, { 82 }, { 83 }, { 84 }, { 85 }, { 86 }, { 87 }, { 88 }, { 89 }, { 90 }, { 91 }, { 92 }, { 93 }, { 94 }, { 95 }, { 96 }, { 97 }, { 98 }, { 99 }, { 100 }, { 101 }, { 102 }, { 103 }, { 104 }, { 105 }, { 106 }, { 107 }, { 108 }, { 109 }, { 110 }, { 111 }, { 112 }, { 113 }, { 114 }, { 115 }, { 116 }, { 117 }, { 118 }, { 119 }, { 120 }, { 121 }, { 122 }, { 123 }, { 124 }, { 125 }, { 126 }, { 127 }, { 128 }, { 129 }, { 130 }, { 131 }, { 132 }, { 133 }, { 134 }, { 135 }, { 136 }, { 137 }, { 138 }, { 139 }, { 140 }, { 141 }, { 142 }, { 143 }, { 144 }, { 145 }, { 146 }, { 147 }, { 148 }, { 149 }, { 150 }, { 151 }, { 152 }, { 153 }, { 154 }, { 155 }, { 156 }, { 157 }, { 158 }, { 159 }, { 160 }, { 161 }, { 162 }, { 163 }, { 164 }, { 165 }, { 166 }, { 167 }, { 168 }, { 169 }, { 170 }, { 171 }, { 172 }, { 173 }, { 174 }, { 175 }, { 176 }, { 177 }, { 178 }, { 179 }, { 180 }, { 181 }, { 182 }, { 183 }, { 184 }, { 185 }, { 186 }, { 187 }, { 188 }, { 189 }, { 190 }, { 191 }, { 192 }, { 193 }, { 194 }, { 195 }, { 196 }, { 197 }, { 198 }, { 199 }, { 200 }, { 201 }, { 202 }, { 203 }, { 204 }, { 205 }, { 206 }, { 207 }, { 208 }, { 209 }, { 210 }, { 211 }, { 212 }, { 213 }, { 214 }, { 215 }, { 216 }, { 217 }, { 218 }, { 219 }, { 220 }, { 221 }, { 222 }, { 223 }, { 224 }, { 225 }, { 226 }, { 227 }, { 228 }, { 229 }, { 230 }, { 231 }, { 232 }, { 233 }, { 234 }, { 235 }, { 236 }, { 237 }, { 238 }, { 239 }, { 240 }, { 241 }, { 242 }, { 243 }, { 244 }, { 245 }, { 246 }, { 247 }, { 248 }, { 249 }, { 250 }, { 251 }, { 252 }, { 253 }, { 254 }, { 255 }, { 256 }, { 257 }, { 258 }, { 259 }, { 260 }, { 261 }, { 262 }, { 263 }, { 264 }, { 265 }, { 266 }, { 267 }, { 268 }, { 269 }, { 270 }, { 271 }, { 272 }, { 273 }, { 274 }, { 275 }, { 276 }, { 277 }, { 278 }, { 279 }, { 280 }, { 281 }, { 282 }, { 283 }, { 284 }, { 285 }, { 286 }, { 287 }, { 288 }, { 289 }, { 290 }, { 291 }, { 292 }, { 293 }, { 294 }, { 295 }, { 296 }, { 297 }, { 298 }, { 299 }, { 300 }, { 301 }, { 302 }, { 303 }, { 304 }, { 305 }, { 306 }, { 307 }, { 308 }, { 309 }, { 310 }, { 311 }, { 312 }, { 313 }, { 314 }, { 315 }, { 316 }, { 317 }, { 318 }, { 319 }, { 320 }, { 321 }, { 322 }, { 323 }, { 324 }, { 325 }, { 326 }, { 327 }, { 328 }, { 329 }, { 330 }, { 331 }, { 332 }, { 333 }, { 334 }, { 335 }, { 336 }, { 337 }, { 338 }, { 339 }, { 340 }, { 341 }, { 342 }, { 343 }, { 344 }, { 345 }, { 346 }, { 347 }, { 348 }, { 349 }, { 350 }, { 351 }, { 352 }, { 353 }, { 354 }, { 355 }, { 356 }, { 357 }, { 358 }, { 359 }, { 360 }, { 361 }, { 362 }, { 363 }, { 364 }, { 365 }, { 366 }, { 367 }, { 368 }, { 369 }, { 370 }, { 371 }, { 372 }, { 373 }, { 374 }, { 375 }, { 376 }, { 377 }, { 378 }, { 379 }, { 380 }, { 381 }, { 382 }, { 383 }, { 384 }, { 385 }, { 386 }, { 387 }, { 388 }, { 389 }, { 390 }, { 391 }, { 392 }, { 393 }, { 394 }, { 395 }, { 396 }, { 397 }, { 398 }, { 399 }, { 400 }, { 401 }, { 402 }, { 403 }, { 404 }, { 405 }, { 406 }, { 407 }, { 408 }, { 409 }, { 410 }, { 411 }, { 412 }, { 413 }, { 414 }, { 415 }, { 416 }, { 417 }, { 418 }, { 419 }, { 420 }, { 421 }, { 422 }, { 423 }, { 424 }, { 425 }, { 430 }, { 431 }, { 432 }, { 433 }, { 434 }, { 435 }, { 436 }, { 437 }, { 442 }, { 443 }, { 444 }, { 445 }, { 446 }, { 447 }, { 448 }, { 449 }, { 450 }, { 451 }, { 457 }, { 458 }, { 459 }, { 460 }, { 461 }, { 462 }, { 463 }, { 469 }, { 470 }, { 471 }, { 472 }, { 473 }]])\n"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Condition involving OR makes this more complex than    * {@link #testExplainJoin()}. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testExplainJoinOrderingWithOr
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|FOODMART_QUERIES
operator|.
name|get
argument_list|(
literal|47
argument_list|)
operator|.
name|left
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"xxx"
argument_list|)
expr_stmt|;
block|}
comment|/** There was a bug representing a nullable timestamp using a {@link Long}    * internally. */
annotation|@
name|Test
specifier|public
name|void
name|testNullableTimestamp
parameter_list|()
block|{
name|checkNullableTimestamp
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
expr_stmt|;
block|}
comment|/** Similar to {@link #testNullableTimestamp} but directly off JDBC. */
annotation|@
name|Test
specifier|public
name|void
name|testNullableTimestamp2
parameter_list|()
block|{
name|checkNullableTimestamp
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkNullableTimestamp
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|config
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|config
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"hire_date\", \"end_date\", \"birth_date\" from \"foodmart\".\"employee\" where \"employee_id\" = 1"
argument_list|)
comment|// disable for MySQL; birth_date suffers timezone shift
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
argument_list|)
operator|.
name|returns2
argument_list|(
literal|"hire_date=1994-12-01; end_date=null; birth_date=1961-08-26\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionWhenNullChecking
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select upper((case when \"empid\">\"deptno\"*10 then 'y' else null end)) T from \"hr\".\"emps\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final String "
operator|+
literal|"$L4J$C$org_apache_calcite_runtime_SqlFunctions_upper_y_ = "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.upper(\"y\");"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return current.empid<= current.deptno * 10 "
operator|+
literal|"? (String) null "
operator|+
literal|": $L4J$C$org_apache_calcite_runtime_SqlFunctions_upper_y_;"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"T=null\n"
operator|+
literal|"T=null\n"
operator|+
literal|"T=Y\n"
operator|+
literal|"T=Y\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionWhenNullChecking2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select upper((case when \"empid\">\"deptno\"*10 then \"name\" end)) T from \"hr\".\"emps\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final String inp2_ = current.name;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return current.empid<= current.deptno * 10 "
operator|+
literal|"|| inp2_ == null "
operator|+
literal|"? (String) null "
operator|+
literal|": org.apache.calcite.runtime.SqlFunctions.upper(inp2_);"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"T=null\n"
operator|+
literal|"T=null\n"
operator|+
literal|"T=SEBASTIAN\n"
operator|+
literal|"T=THEODORE\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionWhenNullChecking3
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(\"name\", \"deptno\"+case when user<> 'sa' then 1 end) from \"hr\".\"emps\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final String inp2_ = current.name;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$org_apache_calcite_runtime_SqlFunctions_ne_sa_sa_ = "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.ne(\"sa\", \"sa\");"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$_org_apache_calcite_runtime_SqlFunctions_ne_sa_sa_ = "
operator|+
literal|"!$L4J$C$org_apache_calcite_runtime_SqlFunctions_ne_sa_sa_;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return inp2_ == null "
operator|+
literal|"|| $L4J$C$_org_apache_calcite_runtime_SqlFunctions_ne_sa_sa_ ? (String) null"
operator|+
literal|" : org.apache.calcite.runtime.SqlFunctions.substring(inp2_, "
operator|+
literal|"current.deptno + 1);"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionWhenNullChecking4
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(trim(\n"
operator|+
literal|"substring(\"name\",\n"
operator|+
literal|"  \"deptno\"*0+case when user = 'sa' then 1 end)\n"
operator|+
literal|"), case when \"empid\">\"deptno\" then 4\n"
comment|/* diff from 5 */
operator|+
literal|"   else\n"
operator|+
literal|"     case when \"deptno\"*8>8 then 5 end\n"
operator|+
literal|"   end-2) T\n"
operator|+
literal|"from\n"
operator|+
literal|"\"hr\".\"emps\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final String inp2_ = current.name;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final int inp1_ = current.deptno;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ = "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.eq(\"sa\", \"sa\");"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$_org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ = "
operator|+
literal|"!$L4J$C$org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return inp2_ == null "
operator|+
literal|"|| $L4J$C$_org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ "
operator|+
literal|"|| !v5&& inp1_ * 8<= 8 "
operator|+
literal|"? (String) null "
operator|+
literal|": org.apache.calcite.runtime.SqlFunctions.substring("
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.trim(true, true, \" \", "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.substring(inp2_, "
operator|+
literal|"inp1_ * 0 + 1), true), (v5 ? 4 : 5) - 2);"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"T=ill\n"
operator|+
literal|"T=ric\n"
operator|+
literal|"T=ebastian\n"
operator|+
literal|"T=heodore\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionWhenNullChecking5
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select substring(trim(\n"
operator|+
literal|"substring(\"name\",\n"
operator|+
literal|"  \"deptno\"*0+case when user = 'sa' then 1 end)\n"
operator|+
literal|"), case when \"empid\">\"deptno\" then 5\n"
comment|/* diff from 4 */
operator|+
literal|"   else\n"
operator|+
literal|"     case when \"deptno\"*8>8 then 5 end\n"
operator|+
literal|"   end-2) T\n"
operator|+
literal|"from\n"
operator|+
literal|"\"hr\".\"emps\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final String inp2_ = current.name;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"final int inp1_ = current.deptno;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final int $L4J$C$5_2 = 5 - 2;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ = "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.eq(\"sa\", \"sa\");"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"static final boolean "
operator|+
literal|"$L4J$C$_org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ = "
operator|+
literal|"!$L4J$C$org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_;"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return inp2_ == null "
operator|+
literal|"|| $L4J$C$_org_apache_calcite_runtime_SqlFunctions_eq_sa_sa_ "
operator|+
literal|"|| current.empid<= inp1_&& inp1_ * 8<= 8 "
operator|+
literal|"? (String) null "
operator|+
literal|": org.apache.calcite.runtime.SqlFunctions.substring("
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.trim(true, true, \" \", "
operator|+
literal|"org.apache.calcite.runtime.SqlFunctions.substring(inp2_, "
operator|+
literal|"inp1_ * 0 + 1), true), $L4J$C$5_2);"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"T=ll\n"
operator|+
literal|"T=ic\n"
operator|+
literal|"T=bastian\n"
operator|+
literal|"T=eodore\n"
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
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (1), (2)"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
operator|+
literal|"EXPR$0=2\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesAlias
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"desc\" from (VALUES ROW(1, 'SameName')) AS \"t\" (\"id\", \"desc\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"desc=SameName\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesMinus
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (-2-1)"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=-3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1120">[CALCITE-1120]    * Support SELECT without FROM</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithoutFrom
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select 2+2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=4\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a table constructor that has multiple rows and multiple columns.    *    *<p>Note that the character literals become CHAR(3) and that the first is    * correctly rendered with trailing spaces: 'a  '. If we were inserting    * into a VARCHAR column the behavior would be different; the literals    * would be converted into VARCHAR(3) values and the implied cast from    * CHAR(1) to CHAR(3) that appends trailing spaces does not occur. See    * "contextually typed value specification" in the SQL spec.</p>    */
annotation|@
name|Test
specifier|public
name|void
name|testValuesComposite
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"values (1, 'a'), (2, 'abc')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1; EXPR$1=a  \n"
operator|+
literal|"EXPR$0=2; EXPR$1=abc\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that even though trivial "rename columns" projection is removed,    * the query still returns proper column names.    */
annotation|@
name|Test
specifier|public
name|void
name|testValuesCompositeRenamed
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select EXPR$0 q, EXPR$1 w from (values (1, 'a'), (2, 'abc'))"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableValues(tuples=[[{ 1, 'a  ' }, { 2, 'abc' }]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"Q=1; W=a  \n"
operator|+
literal|"Q=2; W=abc\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that even though trivial "rename columns" projection is removed,    * the query still returns proper column names.    */
annotation|@
name|Test
specifier|public
name|void
name|testValuesCompositeRenamedSameNames
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select EXPR$0 q, EXPR$1 q from (values (1, 'a'), (2, 'abc'))"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableValues(tuples=[[{ 1, 'a  ' }, { 2, 'abc' }]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"Q=1; Q=a  "
argument_list|,
literal|"Q=2; Q=abc"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that even though trivial "rename columns" projection is removed,    * the query still returns proper column names.    */
annotation|@
name|Test
specifier|public
name|void
name|testUnionWithSameColumnNames
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", \"deptno\" from \"hr\".\"depts\" union select \"deptno\", \"empid\" from \"hr\".\"emps\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableUnion(all=[false])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t0], deptno0=[$t0])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], deptno=[$t1], empid=[$t0])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; deptno=110"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=20; deptno=200"
argument_list|,
literal|"deptno=10; deptno=100"
argument_list|,
literal|"deptno=10; deptno=150"
argument_list|,
literal|"deptno=30; deptno=30"
argument_list|,
literal|"deptno=40; deptno=40"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests inner join to an inline table ({@code VALUES} clause). */
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoinValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|LINGUAL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, desc from sales.emps,\n"
operator|+
literal|"  (SELECT * FROM (VALUES (10, 'SameName')) AS t (id, desc)) as sn\n"
operator|+
literal|"where emps.deptno = sn.id and sn.desc = 'SameName' group by empno, desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], EMPNO=[$t1], DESC=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1, 2}])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[CAST($t3):INTEGER NOT NULL], expr#5=[=($t4, $t0)], expr#6=['SameName'], expr#7=[=($t1, $t6)], expr#8=[AND($t5, $t7)], proj#0..3=[{exprs}], $condition=[$t8])\n"
operator|+
literal|"      EnumerableJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"        EnumerableValues(tuples=[[{ 10, 'SameName' }]])\n"
operator|+
literal|"        EnumerableTableScan(table=[[SALES, EMPS]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=1; DESC=SameName\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a merge-join. */
annotation|@
name|Test
specifier|public
name|void
name|testMergeJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"emps\".\"empid\",\n"
operator|+
literal|" \"depts\".\"deptno\", \"depts\".\"name\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|" join \"hr\".\"depts\" using (\"deptno\")"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], empid=[$t2], deptno=[$t0], name=[$t1])\n"
operator|+
literal|"  EnumerableJoin(condition=[=($0, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Sales\n"
operator|+
literal|"empid=150; deptno=10; name=Sales\n"
operator|+
literal|"empid=110; deptno=10; name=Sales\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a cartesian product aka cross join. */
annotation|@
name|Test
specifier|public
name|void
name|testCartesianJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\", \"hr\".\"depts\" where \"emps\".\"empid\"< 140 and \"depts\".\"deptno\"> 20"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000; deptno0=30; name0=Marketing; employees=[]; location={0, 52}"
argument_list|,
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000; deptno0=40; name0=HR; employees=[{200, 20, Eric, 8000.0, 500}]; location=null"
argument_list|,
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250; deptno0=30; name0=Marketing; employees=[]; location={0, 52}"
argument_list|,
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250; deptno0=40; name0=HR; employees=[{200, 20, Eric, 8000.0, 500}]; location=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountSimple
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"select count(distinct \"sales_fact_1997\".\"unit_sales\") as \"m0\"\n"
operator|+
literal|"from \"sales_fact_1997\" as \"sales_fact_1997\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableAggregate(group=[{}], m0=[COUNT($0)])\n"
operator|+
literal|"  EnumerableAggregate(group=[{7}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"m0=6\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount2
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"select cast(\"unit_sales\" as integer) as \"u\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"customer_id\") as \"m0\"\n"
operator|+
literal|"from \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"group by \"unit_sales\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t0):INTEGER NOT NULL], u=[$t2], m0=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], m0=[COUNT($0)])\n"
operator|+
literal|"    EnumerableAggregate(group=[{2, 7}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"u=1; m0=523"
argument_list|,
literal|"u=5; m0=1059"
argument_list|,
literal|"u=4; m0=4459"
argument_list|,
literal|"u=6; m0=19"
argument_list|,
literal|"u=3; m0=4895"
argument_list|,
literal|"u=2; m0=4735"
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
specifier|final
name|String
name|s
init|=
literal|"select \"time_by_day\".\"the_year\" as \"c0\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"unit_sales\") as \"m0\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"time_by_day\".\"the_year\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], m0=[COUNT($1)])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1, 3}])\n"
operator|+
literal|"    EnumerableJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"      EnumerableCalc(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], expr#11=[1997], expr#12=[=($t10, $t11)], time_id=[$t0], the_year=[$t4], $condition=[$t12])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"      EnumerableCalc(expr#0..7=[{inputs}], time_id=[$t1], unit_sales=[$t7])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; m0=6\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountComposite
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"select \"time_by_day\".\"the_year\" as \"c0\",\n"
operator|+
literal|" count(distinct \"sales_fact_1997\".\"product_id\",\n"
operator|+
literal|"       \"sales_fact_1997\".\"customer_id\") as \"m0\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"time_by_day\".\"the_year\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; m0=85452\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateFilter
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
literal|"select \"the_month\",\n"
operator|+
literal|" count(*) as \"c\",\n"
operator|+
literal|" count(*) filter (where \"day_of_month\"> 20) as \"c2\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\"\n"
operator|+
literal|"where \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"group by \"time_by_day\".\"the_month\"\n"
operator|+
literal|"order by \"time_by_day\".\"the_month\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
name|s
argument_list|)
operator|.
name|returns
argument_list|(
literal|"the_month=April; c=30; c2=10\n"
operator|+
literal|"the_month=August; c=31; c2=11\n"
operator|+
literal|"the_month=December; c=31; c2=11\n"
operator|+
literal|"the_month=February; c=28; c2=8\n"
operator|+
literal|"the_month=January; c=31; c2=11\n"
operator|+
literal|"the_month=July; c=31; c2=11\n"
operator|+
literal|"the_month=June; c=30; c2=10\n"
operator|+
literal|"the_month=March; c=31; c2=11\n"
operator|+
literal|"the_month=May; c=31; c2=11\n"
operator|+
literal|"the_month=November; c=30; c2=10\n"
operator|+
literal|"the_month=October; c=31; c2=11\n"
operator|+
literal|"the_month=September; c=30; c2=10\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a simple IN query implemented as a semi-join. */
annotation|@
name|Test
specifier|public
name|void
name|testSimpleIn
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"depts\" where \"deptno\" in (\n"
operator|+
literal|"  select \"deptno\" from \"hr\".\"emps\"\n"
operator|+
literal|"  where \"empid\"< 150)"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|""
operator|+
literal|"LogicalProject(deptno=[$0], name=[$1], employees=[$2], location=[$3])\n"
operator|+
literal|"  LogicalFilter(condition=[IN($0, {\n"
operator|+
literal|"LogicalProject(deptno=[$1])\n"
operator|+
literal|"  LogicalFilter(condition=[<($0, 150)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])\n"
operator|+
literal|"})])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, depts]])"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableSemiJoin(condition=[=($0, $5)], joinType=[inner])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[150], expr#6=[<($t0, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; name=Sales; employees=[{100, 10, Bill, 10000.0, 1000}, {150, 10, Sebastian, 7000.0, null}]; location={-122, 38}"
argument_list|)
expr_stmt|;
block|}
comment|/** A difficult query: an IN list so large that the planner promotes it    * to a semi-join against a VALUES relation. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testIn
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_by_day\".\"the_year\" as \"c0\",\n"
operator|+
literal|" \"product_class\".\"product_family\" as \"c1\",\n"
operator|+
literal|" \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|" \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|" \"customer\".\"city\" as \"c4\",\n"
operator|+
literal|" sum(\"sales_fact_1997\".\"unit_sales\") as \"m0\"\n"
operator|+
literal|"from \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\", \"customer\" as \"customer\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"and \"product_class\".\"product_family\" = 'Drink'\n"
operator|+
literal|"and \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"and \"customer\".\"country\" = 'USA'\n"
operator|+
literal|"and \"customer\".\"state_province\" = 'WA'\n"
operator|+
literal|"and \"customer\".\"city\" in ('Anacortes', 'Ballard', 'Bellingham', 'Bremerton', 'Burien', 'Edmonds', 'Everett', 'Issaquah', 'Kirkland', 'Lynnwood', 'Marysville', 'Olympia', 'Port Orchard', 'Puyallup', 'Redmond', 'Renton', 'Seattle', 'Sedro Woolley', 'Spokane', 'Tacoma', 'Walla Walla', 'Yakima')\n"
operator|+
literal|"group by \"time_by_day\".\"the_year\",\n"
operator|+
literal|" \"product_class\".\"product_family\",\n"
operator|+
literal|" \"customer\".\"country\",\n"
operator|+
literal|" \"customer\".\"state_province\",\n"
operator|+
literal|" \"customer\".\"city\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=1997; c1=Drink; c2=USA; c3=WA; c4=Sedro Woolley; m0=58.0000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Query that uses parenthesized JOIN. */
annotation|@
name|Test
specifier|public
name|void
name|testSql92JoinParenthesized
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
return|return;
block|}
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|"   \"product_class\".\"product_family\" as \"c0\",\n"
operator|+
literal|"   \"product_class\".\"product_department\" as \"c1\",\n"
operator|+
literal|"   \"customer\".\"country\" as \"c2\",\n"
operator|+
literal|"   \"customer\".\"state_province\" as \"c3\",\n"
operator|+
literal|"   \"customer\".\"city\" as \"c4\"\n"
operator|+
literal|"from\n"
operator|+
literal|"   \"sales_fact_1997\" as \"sales_fact_1997\"\n"
operator|+
literal|"join (\"product\" as \"product\"\n"
operator|+
literal|"     join \"product_class\" as \"product_class\"\n"
operator|+
literal|"     on \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\")\n"
operator|+
literal|"on  \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"join \"customer\" as \"customer\"\n"
operator|+
literal|"on  \"sales_fact_1997\".\"customer_id\" = \"customer\".\"customer_id\"\n"
operator|+
literal|"join \"promotion\" as \"promotion\"\n"
operator|+
literal|"on \"sales_fact_1997\".\"promotion_id\" = \"promotion\".\"promotion_id\"\n"
operator|+
literal|"where (\"promotion\".\"media_type\" = 'Radio'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'TV'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Sunday Paper'\n"
operator|+
literal|" or \"promotion\".\"media_type\" = 'Street Handout')\n"
operator|+
literal|" and (\"product_class\".\"product_family\" = 'Drink')\n"
operator|+
literal|" and (\"customer\".\"country\" = 'USA' and \"customer\".\"state_province\""
operator|+
literal|" = 'WA' and \"customer\".\"city\" = 'Bellingham')\n"
operator|+
literal|"group by \"product_class\".\"product_family\",\n"
operator|+
literal|"   \"product_class\".\"product_department\",\n"
operator|+
literal|"   \"customer\".\"country\",\n"
operator|+
literal|"   \"customer\".\"state_province\",\n"
operator|+
literal|"   \"customer\".\"city\"\n"
operator|+
literal|"order by \"product_class\".\"product_family\" ASC,\n"
operator|+
literal|"   \"product_class\".\"product_department\" ASC,\n"
operator|+
literal|"   \"customer\".\"country\" ASC,\n"
operator|+
literal|"   \"customer\".\"state_province\" ASC,\n"
operator|+
literal|"   \"customer\".\"city\" ASC"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| c0    | c1                  | c2  | c3   | c4         |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
operator|+
literal|"| Drink | Alcoholic Beverages | USA | WA   | Bellingham |\n"
operator|+
literal|"| Drink | Dairy               | USA | WA   | Bellingham |\n"
operator|+
literal|"+-------+---------------------+-----+------+------------+\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY with no options. Nulls come last.    *    * @see org.apache.calcite.avatica.AvaticaDatabaseMetaData#nullsAreSortedAtEnd()    */
annotation|@
name|Test
specifier|public
name|void
name|testOrderBy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=0; grocery_sqft=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... DESC. Nulls come first (they come last for ASC). */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByDesc
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by an expression not in the select clause. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByExpr
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"name\", \"empid\" from \"hr\".\"emps\"\n"
operator|+
literal|"order by - \"empid\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"name=Eric; empid=200\n"
operator|+
literal|"name=Sebastian; empid=150\n"
operator|+
literal|"name=Theodore; empid=110\n"
operator|+
literal|"name=Bill; empid=100\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by an expression not in the '*' select clause. Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-176">[CALCITE-176]    * ORDER BY expression doesn't work with SELECT *</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderStarByExpr
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"order by - \"empid\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSort(sort0=[$5], dir0=[ASC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[-($t0)], proj#0..5=[{exprs}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderUnionStarByExpr
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\" where \"empid\"< 150\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"hr\".\"emps\" where \"empid\"> 150\n"
operator|+
literal|"order by - \"empid\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by a CAST expression not in the select clause. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByCast
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"customer_id\", \"postal_code\" from \"customer\"\n"
operator|+
literal|"where \"customer_id\"< 5\n"
operator|+
literal|"order by cast(substring(\"postal_code\" from 3) as integer) desc"
argument_list|)
comment|// ordered by last 3 digits (980, 674, 172, 057)
operator|.
name|returns
argument_list|(
literal|"customer_id=3; postal_code=73980\n"
operator|+
literal|"customer_id=4; postal_code=74674\n"
operator|+
literal|"customer_id=2; postal_code=17172\n"
operator|+
literal|"customer_id=1; postal_code=15057\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY with all combinations of ASC, DESC, NULLS FIRST,    * NULLS LAST. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByNulls
parameter_list|()
block|{
name|checkOrderByNulls
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
expr_stmt|;
name|checkOrderByNulls
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkOrderByNulls
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|clone
parameter_list|)
block|{
name|checkOrderByDescNullsFirst
argument_list|(
name|clone
argument_list|)
expr_stmt|;
name|checkOrderByNullsFirst
argument_list|(
name|clone
argument_list|)
expr_stmt|;
name|checkOrderByDescNullsLast
argument_list|(
name|clone
argument_list|)
expr_stmt|;
name|checkOrderByNullsLast
argument_list|(
name|clone
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... DESC NULLS FIRST. */
specifier|private
name|void
name|checkOrderByDescNullsFirst
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|config
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|config
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\"\n"
operator|+
literal|"from \"foodmart\".\"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 desc nulls first"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... NULLS FIRST. */
specifier|private
name|void
name|checkOrderByNullsFirst
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|config
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|config
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\"\n"
operator|+
literal|"from \"foodmart\".\"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 nulls first"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... DESC NULLS LAST. */
specifier|private
name|void
name|checkOrderByDescNullsLast
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|config
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|config
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\"\n"
operator|+
literal|"from \"foodmart\".\"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 desc nulls last"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=0; grocery_sqft=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... NULLS LAST. */
specifier|private
name|void
name|checkOrderByNullsLast
parameter_list|(
name|CalciteAssert
operator|.
name|Config
name|config
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|config
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\"\n"
operator|+
literal|"from \"foodmart\".\"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 nulls last"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=0; grocery_sqft=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ...  with various values of    * {@link CalciteConnectionConfig#defaultNullCollation()}. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByVarious
parameter_list|()
block|{
specifier|final
name|boolean
index|[]
name|booleans
init|=
block|{
literal|false
block|,
literal|true
block|}
decl_stmt|;
for|for
control|(
name|NullCollation
name|nullCollation
range|:
name|NullCollation
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|boolean
name|asc
range|:
name|booleans
control|)
block|{
name|checkOrderBy
argument_list|(
name|asc
argument_list|,
name|nullCollation
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|checkOrderBy
parameter_list|(
specifier|final
name|boolean
name|desc
parameter_list|,
specifier|final
name|NullCollation
name|nullCollation
parameter_list|)
block|{
specifier|final
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|checker
init|=
name|resultSet
lambda|->
block|{
specifier|final
name|String
name|msg
init|=
operator|(
name|desc
condition|?
literal|"DESC"
else|:
literal|"ASC"
operator|)
operator|+
literal|":"
operator|+
name|nullCollation
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|numbers
operator|.
name|add
argument_list|(
operator|(
name|Number
operator|)
name|resultSet
operator|.
name|getObject
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|assertThat
argument_list|(
name|msg
argument_list|,
name|numbers
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|msg
argument_list|,
name|numbers
operator|.
name|get
argument_list|(
name|nullCollation
operator|.
name|last
argument_list|(
name|desc
argument_list|)
condition|?
literal|2
else|:
literal|0
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|DEFAULT_NULL_COLLATION
argument_list|,
name|nullCollation
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 "
operator|+
operator|(
name|desc
condition|?
literal|" DESC"
else|:
literal|""
operator|)
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by \"florist\", 2 "
operator|+
operator|(
name|desc
condition|?
literal|" DESC"
else|:
literal|""
operator|)
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 3 order by 2 "
operator|+
operator|(
name|desc
condition|?
literal|" DESC"
else|:
literal|""
operator|)
operator|+
literal|", 1"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|checker
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql1
argument_list|)
operator|.
name|returns
argument_list|(
name|checker
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
name|sql2
argument_list|)
operator|.
name|returns
argument_list|(
name|checker
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... FETCH. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByFetch
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 10\n"
operator|+
literal|"order by 1 fetch first 5 rows only"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..23=[{inputs}], store_id=[$t0], grocery_sqft=[$t16])\n"
operator|+
literal|"  EnumerableLimit(fetch=[5])\n"
operator|+
literal|"    EnumerableCalc(expr#0..23=[{inputs}], expr#24=[10], expr#25=[<($t0, $t24)], proj#0..23=[{exprs}], $condition=[$t25])\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, store]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0; grocery_sqft=null\n"
operator|+
literal|"store_id=1; grocery_sqft=17475\n"
operator|+
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=3; grocery_sqft=24390\n"
operator|+
literal|"store_id=4; grocery_sqft=16844\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests ORDER BY ... OFFSET ... FETCH. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOffsetFetch
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\", \"grocery_sqft\" from \"store\"\n"
operator|+
literal|"where \"store_id\"< 10\n"
operator|+
literal|"order by 1 offset 2 rows fetch next 5 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=2; grocery_sqft=22271\n"
operator|+
literal|"store_id=3; grocery_sqft=24390\n"
operator|+
literal|"store_id=4; grocery_sqft=16844\n"
operator|+
literal|"store_id=5; grocery_sqft=15012\n"
operator|+
literal|"store_id=6; grocery_sqft=15337\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests FETCH with no ORDER BY. */
annotation|@
name|Test
specifier|public
name|void
name|testFetch
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"empid\" from \"hr\".\"emps\"\n"
operator|+
literal|"fetch first 2 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100\n"
operator|+
literal|"empid=200\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFetchStar
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"fetch first 2 rows only"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
argument_list|)
expr_stmt|;
block|}
comment|/** "SELECT ... LIMIT 0" is executed differently. A planner rule converts the    * whole query to an empty rel. */
annotation|@
name|Test
specifier|public
name|void
name|testLimitZero
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"limit 0"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return org.apache.calcite.linq4j.Linq4j.asEnumerable(new Object[] {})"
argument_list|)
expr_stmt|;
block|}
comment|/** Alternative formulation for {@link #testFetchStar()}. */
annotation|@
name|Test
specifier|public
name|void
name|testLimitStar
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Limit implemented using {@link Queryable#take}. Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-96">[CALCITE-96]    * LIMIT against a table in a clone schema causes    * UnsupportedOperationException</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testLimitOnQueryableTable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"days\"\n"
operator|+
literal|"limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1; week_day=Sunday\n"
operator|+
literal|"day=2; week_day=Monday\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Limit implemented using {@link Queryable#take}. Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-70">[CALCITE-70]    * Joins seem to be very expensive in memory</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelfJoinCount
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"store\" as p1 join \"foodmart\".\"store\" as p2 using (\"store_id\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=25\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcAggregate(group=[{}], C=[COUNT()])\n"
operator|+
literal|"    JdbcJoin(condition=[=($0, $1)], joinType=[inner])\n"
operator|+
literal|"      JdbcProject(store_id=[$0])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, store]])\n"
operator|+
literal|"      JdbcProject(store_id=[$0])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, store]])\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests composite GROUP BY where one of the columns has NULL values. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByNull
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", \"commission\", sum(\"salary\") s\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\", \"commission\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; commission=null; S=7000.0"
argument_list|,
literal|"deptno=20; commission=500; S=8000.0"
argument_list|,
literal|"deptno=10; commission=1000; S=10000.0"
argument_list|,
literal|"deptno=10; commission=250; S=11500.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupingSets
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by grouping sets((\"deptno\"), ())"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=null; C=4; S=36500.0"
argument_list|,
literal|"deptno=10; C=3; S=28500.0"
argument_list|,
literal|"deptno=20; C=1; S=8000.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollup
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by rollup(\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=null; C=4; S=36500.0"
argument_list|,
literal|"deptno=10; C=3; S=28500.0"
argument_list|,
literal|"deptno=20; C=1; S=8000.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select distinct \"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10"
argument_list|,
literal|"deptno=20"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-397">[CALCITE-397]    * "SELECT DISTINCT *" on reflective schema gives ClassCastException at    * runtime</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinctStar
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select distinct *\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|4
argument_list|)
operator|.
name|planContains
argument_list|(
literal|".distinct("
argument_list|)
expr_stmt|;
block|}
comment|/** Select distinct on composite key, one column of which is boolean to    * boot. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinctComposite
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select distinct \"empid\"> 140 as c, \"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=false; deptno=10"
argument_list|,
literal|"C=true; deptno=10"
argument_list|,
literal|"C=true; deptno=20"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|".distinct("
argument_list|)
expr_stmt|;
block|}
comment|/** Same result (and plan) as {@link #testSelectDistinct}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByNoAggregates
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10"
argument_list|,
literal|"deptno=20"
argument_list|)
expr_stmt|;
block|}
comment|/** Same result (and plan) as {@link #testSelectDistinct}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByNoAggregatesAllColumns
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\", \"name\", \"salary\", \"commission\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|4
argument_list|)
operator|.
name|planContains
argument_list|(
literal|".distinct("
argument_list|)
expr_stmt|;
block|}
comment|/** Same result (and plan) as {@link #testSelectDistinct}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMax1IsNull
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from (\n"
operator|+
literal|"select max(1) max_id\n"
operator|+
literal|"from \"hr\".\"emps\" where 1=2\n"
operator|+
literal|") where max_id is null"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"MAX_ID=null"
argument_list|)
expr_stmt|;
block|}
comment|/** Same result (and plan) as {@link #testSelectDistinct}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy1Max1
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from (\n"
operator|+
literal|"select max(u) max_id\n"
operator|+
literal|"from (select \"empid\"+\"deptno\" u, 1 cnst\n"
operator|+
literal|"from \"hr\".\"emps\" a) where 1=2\n"
operator|+
literal|"group by cnst\n"
operator|+
literal|") where max_id is null"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-403">[CALCITE-403]    * Enumerable gives NullPointerException with NOT on nullable    * expression</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testHavingNot
parameter_list|()
throws|throws
name|IOException
block|{
name|withFoodMartQuery
argument_list|(
literal|6597
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Minimal case of {@link #testHavingNot()}. */
annotation|@
name|Test
specifier|public
name|void
name|testHavingNot2
parameter_list|()
throws|throws
name|IOException
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select 1\n"
operator|+
literal|"from \"store\"\n"
operator|+
literal|"group by \"store\".\"store_street_address\"\n"
operator|+
literal|"having NOT (sum(\"store\".\"grocery_sqft\")< 20000)"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/** ORDER BY on a sort-key does not require a sort. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderOnSortedTable
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The ArrayTable "store" is sorted by "store_id".
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"day\"\n"
operator|+
literal|"from \"days\"\n"
operator|+
literal|"order by \"day\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1\n"
operator|+
literal|"day=2\n"
operator|+
literal|"day=3\n"
operator|+
literal|"day=4\n"
operator|+
literal|"day=5\n"
operator|+
literal|"day=6\n"
operator|+
literal|"day=7\n"
argument_list|)
expr_stmt|;
block|}
comment|/** ORDER BY on a sort-key does not require a sort. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderSorted
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The ArrayTable "store" is sorted by "store_id".
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store_id\"\n"
operator|+
literal|"from \"store\"\n"
operator|+
literal|"order by \"store_id\" limit 3"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"store_id=0\n"
operator|+
literal|"store_id=1\n"
operator|+
literal|"store_id=2\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereNot
parameter_list|()
throws|throws
name|IOException
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select 1\n"
operator|+
literal|"from \"store\"\n"
operator|+
literal|"where NOT (\"store\".\"grocery_sqft\"< 22000)\n"
operator|+
literal|"group by \"store\".\"store_street_address\"\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
comment|/** Query that reads no columns from either underlying table. */
annotation|@
name|Test
specifier|public
name|void
name|testCountStar
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) c from \"hr\".\"emps\", \"hr\".\"depts\""
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalAggregate(group=[{}], C=[COUNT()])\n"
operator|+
literal|"  LogicalProject(DUMMY=[0])\n"
operator|+
literal|"    LogicalJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"      LogicalProject(DUMMY=[0])\n"
operator|+
literal|"        EnumerableTableScan(table=[[hr, emps]])\n"
operator|+
literal|"      LogicalProject(DUMMY=[0])\n"
operator|+
literal|"        EnumerableTableScan(table=[[hr, depts]])"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Same result (and plan) as {@link #testSelectDistinct}. */
annotation|@
name|Test
specifier|public
name|void
name|testCountUnionAll
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) c from (\n"
operator|+
literal|"select * from \"hr\".\"emps\" where 1=2\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"hr\".\"emps\" where 3=4\n"
operator|+
literal|")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionAll
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"deptno\"=10\n"
operator|+
literal|"union all\n"
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"empid\">=150"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableUnion(all=[true])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill"
argument_list|,
literal|"empid=110; name=Theodore"
argument_list|,
literal|"empid=150; name=Sebastian"
argument_list|,
literal|"empid=150; name=Sebastian"
argument_list|,
literal|"empid=200; name=Eric"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"deptno\"=10\n"
operator|+
literal|"union\n"
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"empid\">=150"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableUnion(all=[false])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill"
argument_list|,
literal|"empid=110; name=Theodore"
argument_list|,
literal|"empid=150; name=Sebastian"
argument_list|,
literal|"empid=200; name=Eric"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"deptno\"=10\n"
operator|+
literal|"intersect\n"
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"empid\">=150"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PLANNER
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelOptPlanner
argument_list|>
operator|)
name|planner
lambda|->
name|planner
operator|.
name|removeRule
argument_list|(
name|IntersectToDistinctRule
operator|.
name|INSTANCE
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableIntersect(all=[false])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=150; name=Sebastian"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcept
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"deptno\"=10\n"
operator|+
literal|"except\n"
operator|+
literal|"select \"empid\", \"name\" from \"hr\".\"emps\" where \"empid\">=150"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableMinus(all=[false])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill"
argument_list|,
literal|"empid=110; name=Theodore"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that SUM and AVG over empty set return null. COUNT returns 0. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateEmpty
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|" count(*) as cs,\n"
operator|+
literal|" count(\"deptno\") as c,\n"
operator|+
literal|" sum(\"deptno\") as s,\n"
operator|+
literal|" avg(\"deptno\") as a\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"where \"deptno\"< 0"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableCalc(expr#0..1=[{inputs}], expr#2=[0], expr#3=[=($t0, $t2)], expr#4=[null:JavaType(class java.lang.Integer)], expr#5=[CASE($t3, $t4, $t1)], expr#6=[/($t5, $t0)], expr#7=[CAST($t6):JavaType(class java.lang.Integer)], CS=[$t0], C=[$t0], S=[$t5], A=[$t7])\n"
operator|+
literal|"  EnumerableAggregate(group=[{}], CS=[COUNT()], S=[$SUM0($1)])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[0], expr#6=[<($t1, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"CS=0; C=0; S=null; A=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that count(deptno) is reduced to count(). */
annotation|@
name|Test
specifier|public
name|void
name|testReduceCountNotNullable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|" count(\"deptno\") as cs,\n"
operator|+
literal|" count(*) as cs2\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"where \"deptno\"< 0"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableCalc(expr#0=[{inputs}], CS=[$t0], CS2=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{}], CS=[COUNT()])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[0], expr#6=[<($t1, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"CS=0; CS2=0\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that {@code count(deptno, commission, commission + 1)} is reduced to    * {@code count(commission, commission + 1)}, because deptno is NOT NULL. */
annotation|@
name|Test
specifier|public
name|void
name|testReduceCompositeCountNotNullable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select\n"
operator|+
literal|" count(\"deptno\", \"commission\", \"commission\" + 1) as cs\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{}], CS=[COUNT($0, $1)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[1], expr#6=[+($t4, $t5)], commission=[$t4], $f2=[$t6])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"CS=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by a column that is already sorted. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOnSortedTable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"time_by_day\"\n"
operator|+
literal|"order by \"time_id\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests sorting by a column that is already sorted. */
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOnSortedTable2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"time_id\", \"the_date\" from \"time_by_day\"\n"
operator|+
literal|"where \"time_id\"< 370\n"
operator|+
literal|"order by \"time_id\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"time_id=367; the_date=1997-01-01 00:00:00\n"
operator|+
literal|"time_id=368; the_date=1997-01-02 00:00:00\n"
operator|+
literal|"time_id=369; the_date=1997-01-03 00:00:00\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"PLAN=EnumerableCalc(expr#0..9=[{inputs}], expr#10=[370], expr#11=[<($t0, $t10)], proj#0..1=[{exprs}], $condition=[$t11])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart2, time_by_day]])\n\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithInsideWhereExists
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\" from \"hr\".\"emps\"\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from \"hr\".\"depts\" where \"depts\".\"deptno\">= \"emps\".\"deptno\")\n"
operator|+
literal|"  select 1 from dept2 where \"deptno\"<= \"emps\".\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10"
argument_list|,
literal|"deptno=10"
argument_list|,
literal|"deptno=10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithOrderBy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"with emp2 as (select * from \"hr\".\"emps\")\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"order by \"deptno\" desc, \"empid\" desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests windowed aggregation. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAgg
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select"
operator|+
literal|" \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|"sum(\"salary\" + \"empid\") over w as s,\n"
operator|+
literal|" 5 as five,\n"
operator|+
literal|" min(\"salary\") over w as m,\n"
operator|+
literal|" count(*) over w as c\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (partition by \"deptno\" order by \"empid\" rows 1 preceding)"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, S REAL, FIVE INTEGER NOT NULL, M REAL, C BIGINT NOT NULL]"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..7=[{inputs}], expr#8=[0:BIGINT], expr#9=[>($t4, $t8)], expr#10=[null:JavaType(class java.lang.Float)], expr#11=[CASE($t9, $t5, $t10)], expr#12=[5], deptno=[$t1], empid=[$t0], S=[$t11], FIVE=[$t12], M=[$t6], C=[$t7])\n"
operator|+
literal|"  EnumerableWindow(window#0=[window(partition {1} order by [0] rows between $4 PRECEDING and CURRENT ROW aggs [COUNT($3), $SUM0($3), MIN($2), COUNT()])])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[+($t3, $t0)], proj#0..1=[{exprs}], salary=[$t3], $3=[$t5])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; S=10100.0; FIVE=5; M=10000.0; C=1"
argument_list|,
literal|"deptno=10; empid=110; S=21710.0; FIVE=5; M=10000.0; C=2"
argument_list|,
literal|"deptno=10; empid=150; S=18760.0; FIVE=5; M=7000.0; C=2"
argument_list|,
literal|"deptno=20; empid=200; S=8200.0; FIVE=5; M=8000.0; C=1"
argument_list|)
operator|.
name|planContains
argument_list|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|?
literal|"_list.add(new Object[] {\n"
operator|+
literal|"        row[0],\n"
comment|// box-unbox is optimized
operator|+
literal|"        row[1],\n"
operator|+
literal|"        row[2],\n"
operator|+
literal|"        row[3],\n"
operator|+
literal|"        COUNTa0w0,\n"
operator|+
literal|"        $SUM0a1w0,\n"
operator|+
literal|"        MINa2w0,\n"
operator|+
literal|"        COUNTa3w0});"
else|:
literal|"_list.add(new Object[] {\n"
operator|+
literal|"        row[0],\n"
comment|// box-unbox is optimized
operator|+
literal|"        row[1],\n"
operator|+
literal|"        row[2],\n"
operator|+
literal|"        row[3],\n"
operator|+
literal|"        a0w0,\n"
operator|+
literal|"        a1w0,\n"
operator|+
literal|"        a2w0,\n"
operator|+
literal|"        a3w0});"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"return new Object[] {\n"
operator|+
literal|"                  current[1],\n"
operator|+
literal|"                  current[0],\n"
comment|// Float.valueOf(SqlFunctions.toFloat(current[5])) comes from SUM0
operator|+
literal|"                  org.apache.calcite.runtime.SqlFunctions.toLong(current[4])> 0L ? Float.valueOf(org.apache.calcite.runtime.SqlFunctions.toFloat(current[5])) : (Float) null,\n"
operator|+
literal|"                  5,\n"
operator|+
literal|"                  current[6],\n"
operator|+
literal|"                  current[7]};\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests windowed aggregation with multiple windows.    * One window straddles the current row.    * Some windows have no PARTITION BY clause. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAgg2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select"
operator|+
literal|" \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|"sum(\"salary\" + \"empid\") over w as s,\n"
operator|+
literal|" 5 as five,\n"
operator|+
literal|" min(\"salary\") over w as m,\n"
operator|+
literal|" count(*) over w as c,\n"
operator|+
literal|" count(*) over w2 as c2,\n"
operator|+
literal|" count(*) over w11 as c11,\n"
operator|+
literal|" count(*) over w11dept as c11dept\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (order by \"empid\" rows 1 preceding),\n"
operator|+
literal|" w2 as (order by \"empid\" rows 2 preceding),\n"
operator|+
literal|" w11 as (order by \"empid\" rows between 1 preceding and 1 following),\n"
operator|+
literal|" w11dept as (partition by \"deptno\" order by \"empid\" rows between 1 preceding and 1 following)"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, S REAL, FIVE INTEGER NOT NULL, M REAL, C BIGINT NOT NULL, C2 BIGINT NOT NULL, C11 BIGINT NOT NULL, C11DEPT BIGINT NOT NULL]"
argument_list|)
comment|// Check that optimizes for window whose PARTITION KEY is empty
operator|.
name|planContains
argument_list|(
literal|"tempList.size()"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=20; empid=200; S=15350.0; FIVE=5; M=7000.0; C=2; C2=3; C11=2; C11DEPT=1"
argument_list|,
literal|"deptno=10; empid=100; S=10100.0; FIVE=5; M=10000.0; C=1; C2=1; C11=2; C11DEPT=2"
argument_list|,
literal|"deptno=10; empid=110; S=21710.0; FIVE=5; M=10000.0; C=2; C2=2; C11=3; C11DEPT=3"
argument_list|,
literal|"deptno=10; empid=150; S=18760.0; FIVE=5; M=7000.0; C=2; C2=3; C11=3; C11DEPT=2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that window aggregates work when computed over non-nullable    * {@link org.apache.calcite.adapter.enumerable.JavaRowFormat#SCALAR} inputs.    * Window aggregates use temporary buffers, thus need to check if    * primitives are properly boxed and un-boxed.    */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggScalarNonNullPhysType
parameter_list|()
block|{
name|String
name|planLine
init|=
literal|"a0s0w0 = org.apache.calcite.runtime.SqlFunctions.lesser(a0s0w0, org.apache.calcite.runtime.SqlFunctions.toFloat(_rows[j]));"
decl_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|planLine
operator|=
name|planLine
operator|.
name|replaceAll
argument_list|(
literal|"a0s0w0"
argument_list|,
literal|"MINa0s0w0"
argument_list|)
expr_stmt|;
block|}
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select min(\"salary\"+1) over w as m\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (order by \"salary\"+1 rows 1 preceding)\n"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[M REAL]"
argument_list|)
operator|.
name|planContains
argument_list|(
name|planLine
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"M=7001.0"
argument_list|,
literal|"M=7001.0"
argument_list|,
literal|"M=8001.0"
argument_list|,
literal|"M=10001.0"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that {@link org.apache.calcite.rel.logical.LogicalCalc} is    * implemented properly when input is    * {@link org.apache.calcite.rel.logical.LogicalWindow} and literal.    */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggScalarNonNullPhysTypePlusOne
parameter_list|()
block|{
name|String
name|planLine
init|=
literal|"a0s0w0 = org.apache.calcite.runtime.SqlFunctions.lesser(a0s0w0, org.apache.calcite.runtime.SqlFunctions.toFloat(_rows[j]));"
decl_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|planLine
operator|=
name|planLine
operator|.
name|replaceAll
argument_list|(
literal|"a0s0w0"
argument_list|,
literal|"MINa0s0w0"
argument_list|)
expr_stmt|;
block|}
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select 1+min(\"salary\"+1) over w as m\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"window w as (order by \"salary\"+1 rows 1 preceding)\n"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[M REAL]"
argument_list|)
operator|.
name|planContains
argument_list|(
name|planLine
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"M=7002.0"
argument_list|,
literal|"M=7002.0"
argument_list|,
literal|"M=8002.0"
argument_list|,
literal|"M=10002.0"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for RANK and ORDER BY ... DESCENDING, NULLS FIRST, NULLS LAST. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggRank
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"commission\" desc nulls first) as rcnf,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"commission\" desc nulls last) as rcnl,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"empid\") as r,\n"
operator|+
literal|" rank() over (partition by \"deptno\" order by \"empid\" desc) as rd\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, RCNF BIGINT NOT NULL, RCNL BIGINT NOT NULL, R BIGINT NOT NULL, RD BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; commission=1000; RCNF=2; RCNL=1; R=1; RD=3"
argument_list|,
literal|"deptno=10; empid=110; commission=250; RCNF=3; RCNL=2; R=2; RD=2"
argument_list|,
literal|"deptno=10; empid=150; commission=null; RCNF=1; RCNL=3; R=3; RD=1"
argument_list|,
literal|"deptno=20; empid=200; commission=500; RCNF=1; RCNL=1; R=1; RD=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for RANK with same values */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggRankValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" rank() over (order by \"deptno\") as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, R BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=20; R=4"
argument_list|)
expr_stmt|;
comment|// 4 for rank and 2 for dense_rank
block|}
comment|/** Tests for RANK with same values */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggRankValuesDesc
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" rank() over (order by \"deptno\" desc) as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, R BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=20; R=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for DENSE_RANK with same values */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggDenseRankValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" dense_rank() over (order by \"deptno\") as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, R BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=10; R=1"
argument_list|,
literal|"deptno=20; R=2"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for DENSE_RANK with same values */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggDenseRankValuesDesc
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" dense_rank() over (order by \"deptno\" desc) as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, R BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=10; R=2"
argument_list|,
literal|"deptno=20; R=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for DATE +- INTERVAL window frame */
annotation|@
name|Test
specifier|public
name|void
name|testWinIntervalFrame
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"hire_date\",\n"
operator|+
literal|" count(*) over (partition by \"deptno\" order by \"hire_date\""
operator|+
literal|" range between interval '1' year preceding and interval '1' year following) as r\n"
operator|+
literal|"from (select \"empid\", \"deptno\",\n"
operator|+
literal|"  DATE '2014-06-12' + \"empid\"*interval '0' day \"hire_date\"\n"
operator|+
literal|"  from \"hr\".\"emps\")"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, hire_date DATE NOT NULL, R BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; hire_date=2014-06-12; R=3"
argument_list|,
literal|"deptno=10; empid=110; hire_date=2014-06-12; R=3"
argument_list|,
literal|"deptno=10; empid=150; hire_date=2014-06-12; R=3"
argument_list|,
literal|"deptno=20; empid=200; hire_date=2014-06-12; R=1"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startOfGroupStep1
parameter_list|(
name|String
name|startOfGroup
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*\n"
operator|+
literal|"  from (\n"
operator|+
literal|"       select  t.*,\n"
operator|+
literal|"               case when "
operator|+
name|startOfGroup
operator|+
literal|" then 0 else 1 end start_of_group\n"
operator|+
literal|"         from "
operator|+
name|START_OF_GROUP_DATA
operator|+
literal|") t\n"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, START_OF_GROUP INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; VAL=0; EXPECTED=1; START_OF_GROUP=1"
argument_list|,
literal|"RN=2; VAL=0; EXPECTED=1; START_OF_GROUP=0"
argument_list|,
literal|"RN=3; VAL=1; EXPECTED=2; START_OF_GROUP=1"
argument_list|,
literal|"RN=4; VAL=0; EXPECTED=3; START_OF_GROUP=1"
argument_list|,
literal|"RN=5; VAL=0; EXPECTED=3; START_OF_GROUP=0"
argument_list|,
literal|"RN=6; VAL=0; EXPECTED=3; START_OF_GROUP=0"
argument_list|,
literal|"RN=7; VAL=1; EXPECTED=4; START_OF_GROUP=1"
argument_list|,
literal|"RN=8; VAL=1; EXPECTED=4; START_OF_GROUP=0"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startOfGroupStep2
parameter_list|(
name|String
name|startOfGroup
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*\n"
comment|// current row is assumed, group_id should be NOT NULL
operator|+
literal|"       ,sum(start_of_group) over (order by rn rows unbounded preceding) group_id\n"
operator|+
literal|"  from (\n"
operator|+
literal|"       select  t.*,\n"
operator|+
literal|"               case when "
operator|+
name|startOfGroup
operator|+
literal|" then 0 else 1 end start_of_group\n"
operator|+
literal|"         from "
operator|+
name|START_OF_GROUP_DATA
operator|+
literal|") t\n"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, START_OF_GROUP INTEGER NOT NULL, GROUP_ID INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; VAL=0; EXPECTED=1; START_OF_GROUP=1; GROUP_ID=1"
argument_list|,
literal|"RN=2; VAL=0; EXPECTED=1; START_OF_GROUP=0; GROUP_ID=1"
argument_list|,
literal|"RN=3; VAL=1; EXPECTED=2; START_OF_GROUP=1; GROUP_ID=2"
argument_list|,
literal|"RN=4; VAL=0; EXPECTED=3; START_OF_GROUP=1; GROUP_ID=3"
argument_list|,
literal|"RN=5; VAL=0; EXPECTED=3; START_OF_GROUP=0; GROUP_ID=3"
argument_list|,
literal|"RN=6; VAL=0; EXPECTED=3; START_OF_GROUP=0; GROUP_ID=3"
argument_list|,
literal|"RN=7; VAL=1; EXPECTED=4; START_OF_GROUP=1; GROUP_ID=4"
argument_list|,
literal|"RN=8; VAL=1; EXPECTED=4; START_OF_GROUP=0; GROUP_ID=4"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startOfGroupStep3
parameter_list|(
name|String
name|startOfGroup
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select group_id, min(rn) min_rn, max(rn) max_rn,\n"
operator|+
literal|"  count(rn) cnt_rn, avg(val) avg_val"
operator|+
literal|" from (\n"
operator|+
literal|"select t.*\n"
comment|// current row is assumed, group_id should be NOT NULL
operator|+
literal|"       ,sum(start_of_group) over (order by rn rows unbounded preceding) group_id\n"
operator|+
literal|"  from (\n"
operator|+
literal|"       select  t.*,\n"
operator|+
literal|"               case when "
operator|+
name|startOfGroup
operator|+
literal|" then 0 else 1 end start_of_group\n"
operator|+
literal|"         from "
operator|+
name|START_OF_GROUP_DATA
operator|+
literal|") t\n"
operator|+
literal|") group by group_id\n"
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[GROUP_ID INTEGER NOT NULL, MIN_RN INTEGER NOT NULL, MAX_RN INTEGER NOT NULL, CNT_RN BIGINT NOT NULL, AVG_VAL INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"GROUP_ID=1; MIN_RN=1; MAX_RN=2; CNT_RN=2; AVG_VAL=0"
argument_list|,
literal|"GROUP_ID=2; MIN_RN=3; MAX_RN=3; CNT_RN=1; AVG_VAL=1"
argument_list|,
literal|"GROUP_ID=3; MIN_RN=4; MAX_RN=6; CNT_RN=3; AVG_VAL=0"
argument_list|,
literal|"GROUP_ID=4; MIN_RN=7; MAX_RN=8; CNT_RN=2; AVG_VAL=1"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step1, implemented as last_value.    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLastValueStep1
parameter_list|()
block|{
name|startOfGroupStep1
argument_list|(
literal|"val = last_value(val) over (order by rn rows between 1 preceding and 1 preceding)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step2, that gets the final group numbers    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLastValueStep2
parameter_list|()
block|{
name|startOfGroupStep2
argument_list|(
literal|"val = last_value(val) over (order by rn rows between 1 preceding and 1 preceding)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step3, that aggregates the computed groups    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLastValueStep3
parameter_list|()
block|{
name|startOfGroupStep3
argument_list|(
literal|"val = last_value(val) over (order by rn rows between 1 preceding and 1 preceding)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step1, implemented as last_value.    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLagStep1
parameter_list|()
block|{
name|startOfGroupStep1
argument_list|(
literal|"val = lag(val) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step2, that gets the final group numbers    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLagValueStep2
parameter_list|()
block|{
name|startOfGroupStep2
argument_list|(
literal|"val = lag(val) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step3, that aggregates the computed groups    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLagStep3
parameter_list|()
block|{
name|startOfGroupStep3
argument_list|(
literal|"val = lag(val) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step1, implemented as last_value.    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLeadStep1
parameter_list|()
block|{
name|startOfGroupStep1
argument_list|(
literal|"val = lead(val, -1) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step2, that gets the final group numbers    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLeadValueStep2
parameter_list|()
block|{
name|startOfGroupStep2
argument_list|(
literal|"val = lead(val, -1) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests start_of_group approach for grouping of adjacent intervals.    * This is a step3, that aggregates the computed groups    * http://timurakhmadeev.wordpress.com/2013/07/21/start_of_group/    */
annotation|@
name|Test
specifier|public
name|void
name|testStartOfGroupLeadStep3
parameter_list|()
block|{
name|startOfGroupStep3
argument_list|(
literal|"val = lead(val, -1) over (order by rn)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests default value of LAG function.    */
annotation|@
name|Test
specifier|public
name|void
name|testLagDefaultValue
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*, lag(rn+expected,1,42) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, L INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; VAL=0; EXPECTED=1; L=42"
argument_list|,
literal|"RN=2; VAL=0; EXPECTED=1; L=2"
argument_list|,
literal|"RN=3; VAL=1; EXPECTED=2; L=3"
argument_list|,
literal|"RN=4; VAL=0; EXPECTED=3; L=5"
argument_list|,
literal|"RN=5; VAL=0; EXPECTED=3; L=7"
argument_list|,
literal|"RN=6; VAL=0; EXPECTED=3; L=8"
argument_list|,
literal|"RN=7; VAL=1; EXPECTED=4; L=9"
argument_list|,
literal|"RN=8; VAL=1; EXPECTED=4; L=11"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests default value of LEAD function.    */
annotation|@
name|Test
specifier|public
name|void
name|testLeadDefaultValue
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*, lead(rn+expected,1,42) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, L INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; VAL=0; EXPECTED=1; L=3"
argument_list|,
literal|"RN=2; VAL=0; EXPECTED=1; L=5"
argument_list|,
literal|"RN=3; VAL=1; EXPECTED=2; L=7"
argument_list|,
literal|"RN=4; VAL=0; EXPECTED=3; L=8"
argument_list|,
literal|"RN=5; VAL=0; EXPECTED=3; L=9"
argument_list|,
literal|"RN=6; VAL=0; EXPECTED=3; L=11"
argument_list|,
literal|"RN=7; VAL=1; EXPECTED=4; L=12"
argument_list|,
literal|"RN=8; VAL=1; EXPECTED=4; L=42"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests expression in offset value of LAG function.    */
annotation|@
name|Test
specifier|public
name|void
name|testLagExpressionOffset
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*, lag(rn, expected, 42) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, L INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; VAL=0; EXPECTED=1; L=42"
argument_list|,
literal|"RN=2; VAL=0; EXPECTED=1; L=1"
argument_list|,
literal|"RN=3; VAL=1; EXPECTED=2; L=1"
argument_list|,
literal|"RN=4; VAL=0; EXPECTED=3; L=1"
argument_list|,
literal|"RN=5; VAL=0; EXPECTED=3; L=2"
argument_list|,
literal|"RN=6; VAL=0; EXPECTED=3; L=3"
argument_list|,
literal|"RN=7; VAL=1; EXPECTED=4; L=3"
argument_list|,
literal|"RN=8; VAL=1; EXPECTED=4; L=4"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests DATE as offset argument of LAG function.    */
annotation|@
name|Test
specifier|public
name|void
name|testLagInvalidOffsetArgument
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.*,\n"
operator|+
literal|"  lag(rn, DATE '2014-06-20', 42) over (order by rn) l\n"
operator|+
literal|"from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot apply 'LAG' to arguments of type 'LAG(<INTEGER>,<DATE>,<INTEGER>)'"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests NTILE(2).    */
annotation|@
name|Test
specifier|public
name|void
name|testNtile1
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select rn, ntile(1) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, L BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; L=1"
argument_list|,
literal|"RN=2; L=1"
argument_list|,
literal|"RN=3; L=1"
argument_list|,
literal|"RN=4; L=1"
argument_list|,
literal|"RN=5; L=1"
argument_list|,
literal|"RN=6; L=1"
argument_list|,
literal|"RN=7; L=1"
argument_list|,
literal|"RN=8; L=1"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests NTILE(2).    */
annotation|@
name|Test
specifier|public
name|void
name|testNtile2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select rn, ntile(2) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, L BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; L=1"
argument_list|,
literal|"RN=2; L=1"
argument_list|,
literal|"RN=3; L=1"
argument_list|,
literal|"RN=4; L=1"
argument_list|,
literal|"RN=5; L=2"
argument_list|,
literal|"RN=6; L=2"
argument_list|,
literal|"RN=7; L=2"
argument_list|,
literal|"RN=8; L=2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests expression in offset value of LAG function.    */
annotation|@
name|Ignore
argument_list|(
literal|"Have no idea how to validate that expression is constant"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testNtileConstantArgs
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select rn, ntile(1+1) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[RN INTEGER NOT NULL, VAL INTEGER NOT NULL, EXPECTED INTEGER NOT NULL, L INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"RN=1; L=1"
argument_list|,
literal|"RN=2; L=1"
argument_list|,
literal|"RN=3; L=1"
argument_list|,
literal|"RN=4; L=1"
argument_list|,
literal|"RN=5; L=2"
argument_list|,
literal|"RN=6; L=2"
argument_list|,
literal|"RN=7; L=2"
argument_list|,
literal|"RN=8; L=2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests expression in offset value of LAG function.    */
annotation|@
name|Test
specifier|public
name|void
name|testNtileNegativeArg
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select rn, ntile(-1) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Argument to function 'NTILE' must be a positive integer literal"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests expression in offset value of LAG function.    */
annotation|@
name|Test
specifier|public
name|void
name|testNtileDecimalArg
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select rn, ntile(3.141592653) over (order by rn) l\n"
operator|+
literal|" from "
operator|+
name|START_OF_GROUP_DATA
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot apply 'NTILE' to arguments of type 'NTILE(<DECIMAL(10, 9)>)'"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for FIRST_VALUE */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggFirstValue
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" first_value(\"commission\") over (partition by \"deptno\" order by \"empid\") as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, R INTEGER]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; commission=1000; R=1000"
argument_list|,
literal|"deptno=10; empid=110; commission=250; R=1000"
argument_list|,
literal|"deptno=10; empid=150; commission=null; R=1000"
argument_list|,
literal|"deptno=20; empid=200; commission=500; R=500"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for FIRST_VALUE desc */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggFirstValueDesc
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select  \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" first_value(\"commission\") over (partition by \"deptno\" order by \"empid\" desc) as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, R INTEGER]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; commission=1000; R=null"
argument_list|,
literal|"deptno=10; empid=110; commission=250; R=null"
argument_list|,
literal|"deptno=10; empid=150; commission=null; R=null"
argument_list|,
literal|"deptno=20; empid=200; commission=500; R=500"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for FIRST_VALUE empty window */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggFirstValueEmptyWindow
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" first_value(\"commission\") over (partition by \"deptno\" order by \"empid\" desc range between 1000 preceding and 999 preceding) as r\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, R INTEGER]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; commission=1000; R=null"
argument_list|,
literal|"deptno=10; empid=110; commission=250; R=null"
argument_list|,
literal|"deptno=10; empid=150; commission=null; R=null"
argument_list|,
literal|"deptno=20; empid=200; commission=500; R=null"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests for ROW_NUMBER */
annotation|@
name|Test
specifier|public
name|void
name|testWinRowNumber
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"deptno\",\n"
operator|+
literal|" \"empid\",\n"
operator|+
literal|" \"commission\",\n"
operator|+
literal|" row_number() over (partition by \"deptno\") as r,\n"
operator|+
literal|" row_number() over (partition by \"deptno\" order by \"commission\" desc nulls first) as rcnf,\n"
operator|+
literal|" row_number() over (partition by \"deptno\" order by \"commission\" desc nulls last) as rcnl,\n"
operator|+
literal|" row_number() over (partition by \"deptno\" order by \"empid\") as r,\n"
operator|+
literal|" row_number() over (partition by \"deptno\" order by \"empid\" desc) as rd\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[deptno INTEGER NOT NULL, empid INTEGER NOT NULL, commission INTEGER, R BIGINT NOT NULL, RCNF BIGINT NOT NULL, RCNL BIGINT NOT NULL, R BIGINT NOT NULL, RD BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; empid=100; commission=1000; R=1; RCNF=2; RCNL=1; R=1; RD=3"
argument_list|,
literal|"deptno=10; empid=110; commission=250; R=3; RCNF=3; RCNL=2; R=2; RD=2"
argument_list|,
literal|"deptno=10; empid=150; commission=null; R=2; RCNF=1; RCNL=3; R=3; RD=1"
argument_list|,
literal|"deptno=20; empid=200; commission=500; R=1; RCNF=1; RCNL=1; R=1; RD=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests UNBOUNDED PRECEDING clause. */
annotation|@
name|Test
specifier|public
name|void
name|testOverUnboundedPreceding
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"empid\",\n"
operator|+
literal|"  \"commission\",\n"
operator|+
literal|"  count(\"empid\") over (partition by 42\n"
operator|+
literal|"    order by \"commission\" nulls first\n"
operator|+
literal|"    rows between UNBOUNDED PRECEDING and current row) as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[empid INTEGER NOT NULL, commission INTEGER, M BIGINT NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; commission=1000; M=4"
argument_list|,
literal|"empid=200; commission=500; M=3"
argument_list|,
literal|"empid=150; commission=null; M=1"
argument_list|,
literal|"empid=110; commission=250; M=2"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests UNBOUNDED PRECEDING clause. */
annotation|@
name|Test
specifier|public
name|void
name|testSumOverUnboundedPreceding
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\",\n"
operator|+
literal|"  \"commission\",\n"
operator|+
literal|"  sum(\"empid\") over (partition by 42\n"
operator|+
literal|"    order by \"commission\" nulls first\n"
operator|+
literal|"    rows between UNBOUNDED PRECEDING and current row) as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[empid INTEGER NOT NULL, commission INTEGER, M INTEGER NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; commission=1000; M=560"
argument_list|,
literal|"empid=110; commission=250; M=260"
argument_list|,
literal|"empid=150; commission=null; M=150"
argument_list|,
literal|"empid=200; commission=500; M=460"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that sum over possibly empty window is nullable. */
annotation|@
name|Test
specifier|public
name|void
name|testSumOverPossiblyEmptyWindow
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\",\n"
operator|+
literal|"  \"commission\",\n"
operator|+
literal|"  sum(\"empid\") over (partition by 42\n"
operator|+
literal|"    order by \"commission\" nulls first\n"
operator|+
literal|"    rows between UNBOUNDED PRECEDING and 1 preceding) as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[empid INTEGER NOT NULL, commission INTEGER, M INTEGER]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; commission=1000; M=460"
argument_list|,
literal|"empid=110; commission=250; M=150"
argument_list|,
literal|"empid=150; commission=null; M=null"
argument_list|,
literal|"empid=200; commission=500; M=260"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests windowed aggregation with no ORDER BY clause.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-285">[CALCITE-285]    * Window functions throw exception without ORDER BY</a>.    *    *<p>Note:</p>    *    *<ul>    *<li>With no ORDER BY, the window is over all rows in the partition.    *<li>With an ORDER BY, the implicit frame is 'RANGE BETWEEN    *     UNBOUNDED PRECEDING AND CURRENT ROW'.    *<li>With no ORDER BY or PARTITION BY, the window contains all rows in the    *     table.    *</ul>    */
annotation|@
name|Test
specifier|public
name|void
name|testOverNoOrder
parameter_list|()
block|{
comment|// If no range is specified, default is "RANGE BETWEEN UNBOUNDED PRECEDING
comment|// AND CURRENT ROW".
comment|// The aggregate function is within the current partition;
comment|// if there is no partition, that means the whole table.
comment|// Rows are deemed "equal to" the current row per the ORDER BY clause.
comment|// If there is no ORDER BY clause, CURRENT ROW has the same effect as
comment|// UNBOUNDED FOLLOWING; that is, no filtering effect at all.
specifier|final
name|String
name|sql
init|=
literal|"select *,\n"
operator|+
literal|" count(*) over (partition by deptno) as m1,\n"
operator|+
literal|" count(*) over (partition by deptno order by ename) as m2,\n"
operator|+
literal|" count(*) over () as m3\n"
operator|+
literal|"from emp"
decl_stmt|;
name|withEmpDept
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"ENAME=Adam ; DEPTNO=50; GENDER=M; M1=2; M2=1; M3=9"
argument_list|,
literal|"ENAME=Alice; DEPTNO=30; GENDER=F; M1=2; M2=1; M3=9"
argument_list|,
literal|"ENAME=Bob  ; DEPTNO=10; GENDER=M; M1=2; M2=1; M3=9"
argument_list|,
literal|"ENAME=Eric ; DEPTNO=20; GENDER=M; M1=1; M2=1; M3=9"
argument_list|,
literal|"ENAME=Eve  ; DEPTNO=50; GENDER=F; M1=2; M2=2; M3=9"
argument_list|,
literal|"ENAME=Grace; DEPTNO=60; GENDER=F; M1=1; M2=1; M3=9"
argument_list|,
literal|"ENAME=Jane ; DEPTNO=10; GENDER=F; M1=2; M2=2; M3=9"
argument_list|,
literal|"ENAME=Susan; DEPTNO=30; GENDER=F; M1=2; M2=2; M3=9"
argument_list|,
literal|"ENAME=Wilma; DEPTNO=null; GENDER=F; M1=1; M2=1; M3=9"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that field-trimming creates a project near the table scan. */
annotation|@
name|Test
specifier|public
name|void
name|testTrimFields
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"name\", count(\"commission\") + 1\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\", \"name\""
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalProject(name=[$1], EXPR$1=[+($2, 1)])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], agg#0=[COUNT($2)])\n"
operator|+
literal|"    LogicalProject(deptno=[$1], name=[$2], commission=[$4])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests that field-trimming creates a project near the table scan, in a    * query with windowed-aggregation. */
annotation|@
name|Test
specifier|public
name|void
name|testTrimFieldsOver
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
comment|// The correct plan has a project on a filter on a project on a scan.
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"name\",\n"
operator|+
literal|"  count(\"commission\") over (partition by \"deptno\") + 1\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"where \"empid\"> 10"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|""
operator|+
literal|"LogicalProject(name=[$2], EXPR$1=[+(COUNT($3) OVER (PARTITION BY $1 RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING), 1)])\n"
operator|+
literal|"  LogicalFilter(condition=[>($0, 10)])\n"
operator|+
literal|"    LogicalProject(empid=[$0], deptno=[$1], name=[$2], commission=[$4])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, emps]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests window aggregate whose argument is a constant. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggConstant
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select max(1) over (partition by \"deptno\"\n"
operator|+
literal|"  order by \"empid\") as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"M=1"
argument_list|,
literal|"M=1"
argument_list|,
literal|"M=1"
argument_list|,
literal|"M=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests multiple window aggregates over constants.    * This tests that EnumerableWindowRel is able to reference the right slot    * when accessing constant for aggregation argument. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggConstantMultipleConstants
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"deptno\", sum(1) over (partition by \"deptno\"\n"
operator|+
literal|"  order by \"empid\" rows between unbounded preceding and current row) as a,\n"
operator|+
literal|" sum(-1) over (partition by \"deptno\"\n"
operator|+
literal|"  order by \"empid\" rows between unbounded preceding and current row) as b\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; A=1; B=-1"
argument_list|,
literal|"deptno=10; A=2; B=-2"
argument_list|,
literal|"deptno=10; A=3; B=-3"
argument_list|,
literal|"deptno=20; A=1; B=-1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests window aggregate PARTITION BY constant. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggPartitionByConstant
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|""
comment|// *0 is used to make results predictable.
comment|// If using just max(empid) calcite cannot compute the result
comment|// properly since it does not support range windows yet :(
operator|+
literal|"select max(\"empid\"*0) over (partition by 42\n"
operator|+
literal|"  order by \"empid\") as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests window aggregate ORDER BY constant. Unlike in SELECT ... ORDER BY,    * the constant does not mean a column. It means a constant, therefore the    * order of the rows is not changed. */
annotation|@
name|Test
specifier|public
name|void
name|testWinAggOrderByConstant
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|""
comment|// *0 is used to make results predictable.
comment|// If using just max(empid) calcite cannot compute the result
comment|// properly since it does not support range windows yet :(
operator|+
literal|"select max(\"empid\"*0) over (partition by \"deptno\"\n"
operator|+
literal|"  order by 42) as m\n"
operator|+
literal|"from \"hr\".\"emps\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|,
literal|"M=0"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests WHERE comparing a nullable integer with an integer literal. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereNullable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where \"commission\"> 800"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests CALCITE-980: Not (C='a' or C='b') causes NPE */
annotation|@
name|Test
specifier|public
name|void
name|testWhereOrAndNullable
parameter_list|()
block|{
comment|/* Generates the following code:        public boolean moveNext() {          while (inputEnumerator.moveNext()) {            final Object[] current = (Object[]) inputEnumerator.current();            final String inp0_ = current[0] == null ? (String) null : current[0].toString();            final String inp1_ = current[1] == null ? (String) null : current[1].toString();            if (inp0_ != null&& org.apache.calcite.runtime.SqlFunctions.eq(inp0_, "a")&& (inp1_ != null&& org.apache.calcite.runtime.SqlFunctions.eq(inp1_, "b"))                || inp0_ != null&& org.apache.calcite.runtime.SqlFunctions.eq(inp0_, "b")&& (inp1_ != null&& org.apache.calcite.runtime.SqlFunctions.eq(inp1_, "c"))) {              return true;            }          }          return false;        }      */
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"with tst(c) as (values('a'),('b'),('c'),(cast(null as varchar)))"
operator|+
literal|" select u.c u, v.c v from tst u, tst v where ((u.c = 'a' and v.c = 'b') or (u.c = 'b' and v.c = 'c'))"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"U=a; V=b"
argument_list|,
literal|"U=b; V=c"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-980">[CALCITE-980]    * different flavors of boolean logic</a>.    *    * @see QuidemTest sql/conditions.iq */
annotation|@
name|Ignore
argument_list|(
literal|"Fails with org.codehaus.commons.compiler.CompileException: Line 16, Column 112:"
operator|+
literal|" Cannot compare types \"int\" and \"java.lang.String\"\n"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testComparingIntAndString
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if (((...test.ReflectiveSchemaTest.IntAndString) inputEnumerator.current()).id == "T")
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|ReflectiveSchemaTest
operator|.
name|CatchallSchema
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
literal|"select a.\"value\", b.\"value\"\n"
operator|+
literal|"  from \"bools\" a\n"
operator|+
literal|"     , \"bools\" b\n"
operator|+
literal|" where b.\"value\" = 'T'\n"
operator|+
literal|" order by 1, 2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"should fail with 'not a number' sql error while converting text to number"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1015">[CALCITE-1015]    * OFFSET 0 causes AssertionError</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTrivialSort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select a.\"value\", b.\"value\"\n"
operator|+
literal|"  from \"bools\" a\n"
operator|+
literal|"     , \"bools\" b\n"
operator|+
literal|" offset 0"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|ReflectiveSchemaTest
operator|.
name|CatchallSchema
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"value=T; value=T"
argument_list|,
literal|"value=T; value=F"
argument_list|,
literal|"value=T; value=null"
argument_list|,
literal|"value=F; value=T"
argument_list|,
literal|"value=F; value=F"
argument_list|,
literal|"value=F; value=null"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the LIKE operator. */
annotation|@
name|Test
specifier|public
name|void
name|testLike
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where \"name\" like '%i__'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests array index. */
annotation|@
name|Test
specifier|public
name|void
name|testArrayIndexing
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"deptno\", \"employees\"[1] as e from \"hr\".\"depts\"\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; E={100, 10, Bill, 10000.0, 1000}"
argument_list|,
literal|"deptno=30; E=null"
argument_list|,
literal|"deptno=40; E={200, 20, Eric, 8000.0, 500}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVarcharEquals
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"lname\" from \"customer\" where \"lname\" = 'Nowmer'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lname=Nowmer\n"
argument_list|)
expr_stmt|;
comment|// lname is declared as VARCHAR(30), comparing it with a string longer
comment|// than 30 characters would introduce a cast to the least restrictive
comment|// type, thus lname would be cast to a varchar(40) in this case.
comment|// These sorts of casts are removed though when constructing the jdbc
comment|// sql, since e.g. HSQLDB does not support them.
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"customer\" "
operator|+
literal|"where \"lname\" = 'this string is longer than 30 characters'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=0\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"customer\" "
operator|+
literal|"where cast(\"customer_id\" as char(20)) = 'this string is longer than 30 characters'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=0\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1153">[CALCITE-1153]    * Invalid CAST when push JOIN down to Oracle</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinMismatchedVarchar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c\n"
operator|+
literal|"from \"customer\" as c\n"
operator|+
literal|"join \"product\" as p on c.\"lname\" = p.\"brand_name\""
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=607\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntersectMismatchedVarchar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from (\n"
operator|+
literal|"  select \"lname\" from \"customer\" as c\n"
operator|+
literal|"  intersect\n"
operator|+
literal|"  select \"brand_name\" from \"product\" as p)"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=12\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the NOT IN operator. Problems arose in code-generation because    * the column allows nulls. */
annotation|@
name|Test
specifier|public
name|void
name|testNotIn
parameter_list|()
block|{
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b') or \"name\" is null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
comment|// And some similar combinations...
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b') or \"name\" is null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b', null) or \"name\" is null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b') or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" in ('a', 'b', null) or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b', null) or \"name\" is not null"
argument_list|)
expr_stmt|;
name|predicate
argument_list|(
literal|"\"name\" not in ('a', 'b', null) and \"name\" is not null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInEmptyQuery
parameter_list|()
block|{
comment|// RHS is empty, therefore returns all rows from emp, including the one
comment|// with deptno = NULL.
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp where deptno not in (\n"
operator|+
literal|"select deptno from dept where deptno = -1)"
decl_stmt|;
name|withEmpDept
argument_list|(
name|sql
argument_list|)
comment|//        .explainContains("EnumerableCalc(expr#0..2=[{inputs}], "
comment|//            + "expr#3=[IS NOT NULL($t2)], expr#4=[true], "
comment|//            + "expr#5=[IS NULL($t0)], expr#6=[null], expr#7=[false], "
comment|//            + "expr#8=[CASE($t3, $t4, $t5, $t6, $t7)], expr#9=[NOT($t8)], "
comment|//            + "EXPR$1=[$t0], $condition=[$t9])")
operator|.
name|returnsUnordered
argument_list|(
literal|"DEPTNO=null"
argument_list|,
literal|"DEPTNO=10"
argument_list|,
literal|"DEPTNO=10"
argument_list|,
literal|"DEPTNO=20"
argument_list|,
literal|"DEPTNO=30"
argument_list|,
literal|"DEPTNO=30"
argument_list|,
literal|"DEPTNO=50"
argument_list|,
literal|"DEPTNO=50"
argument_list|,
literal|"DEPTNO=60"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInQuery
parameter_list|()
block|{
comment|// None of the rows from RHS is NULL.
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp where deptno not in (\n"
operator|+
literal|"select deptno from dept)"
decl_stmt|;
name|withEmpDept
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DEPTNO=50"
argument_list|,
literal|"DEPTNO=50"
argument_list|,
literal|"DEPTNO=60"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotInQueryWithNull
parameter_list|()
block|{
comment|// There is a NULL on the RHS, and '10 not in (20, null)' yields unknown
comment|// (similarly for every other value of deptno), so no rows are returned.
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp where deptno not in (\n"
operator|+
literal|"select deptno from emp)"
decl_stmt|;
name|withEmpDept
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTrim
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select trim(\"lname\") as \"lname\" "
operator|+
literal|"from \"customer\" where \"lname\" = 'Nowmer'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lname=Nowmer\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select trim(leading 'N' from \"lname\") as \"lname\" "
operator|+
literal|"from \"customer\" where \"lname\" = 'Nowmer'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lname=owmer\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|predicate
parameter_list|(
name|String
name|foo
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"hr\".\"emps\"\n"
operator|+
literal|"where "
operator|+
name|foo
argument_list|)
operator|.
name|runs
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select*from \"hr\".\"emps\" where exists (\n"
operator|+
literal|" select 1 from \"hr\".\"depts\"\n"
operator|+
literal|" where \"emps\".\"deptno\"=\"depts\".\"deptno\")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])\n"
operator|+
literal|"  LogicalFilter(condition=[EXISTS({\n"
operator|+
literal|"LogicalFilter(condition=[=($cor0.deptno, $0)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"})], variablesSet=[[$cor0]])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|convertContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000"
argument_list|,
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null"
argument_list|,
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotExistsCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..5=[{inputs}], expr#6=[IS NULL($t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"  EnumerableCorrelate(correlation=[$cor0], joinType=[left], requiredColumns=[{1}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"      EnumerableCalc(expr#0..3=[{inputs}], expr#4=[true], expr#5=[$cor0], expr#6=[$t5.deptno], expr#7=[=($t6, $t0)], i=[$t4], $condition=[$t7])\n"
operator|+
literal|"        EnumerableTableScan(table=[[hr, depts]])\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from \"hr\".\"emps\" where not exists (\n"
operator|+
literal|" select 1 from \"hr\".\"depts\"\n"
operator|+
literal|" where \"emps\".\"deptno\"=\"depts\".\"deptno\")"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FORCE_DECORRELATE
argument_list|,
literal|false
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500"
argument_list|)
expr_stmt|;
block|}
comment|/** Manual expansion of EXISTS in {@link #testNotExistsCorrelated()}. */
annotation|@
name|Test
specifier|public
name|void
name|testNotExistsCorrelated2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from \"hr\".\"emps\" as e left join lateral (\n"
operator|+
literal|" select distinct true as i\n"
operator|+
literal|" from \"hr\".\"depts\"\n"
operator|+
literal|" where e.\"deptno\"=\"depts\".\"deptno\") on true"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|""
operator|+
literal|"EnumerableCalc(expr#0..6=[{inputs}], proj#0..4=[{exprs}], I=[$t6])\n"
operator|+
literal|"  EnumerableJoin(condition=[=($1, $5)], joinType=[left])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0=[{inputs}], expr#1=[true], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"        EnumerableTableScan(table=[[hr, depts]])"
decl_stmt|;
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000; I=true"
argument_list|,
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250; I=true"
argument_list|,
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null; I=true"
argument_list|,
literal|"empid=200; deptno=20; name=Eric; salary=8000.0; commission=500; I=null"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-313">[CALCITE-313]    * Query decorrelation fails</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinInCorrelatedSubQuery
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from \"hr\".\"depts\" as d\n"
operator|+
literal|"where \"deptno\" in (\n"
operator|+
literal|"  select d2.\"deptno\"\n"
operator|+
literal|"  from \"hr\".\"depts\" as d2\n"
operator|+
literal|"  join \"hr\".\"emps\" as e2 using (\"deptno\")\n"
operator|+
literal|"where d.\"deptno\" = d2.\"deptno\")"
argument_list|)
operator|.
name|convertMatches
argument_list|(
name|relNode
lambda|->
block|{
name|String
name|s
init|=
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|relNode
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"Correlate"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a correlated scalar sub-query in the SELECT clause.    *    *<p>Note that there should be an extra row "empid=200; deptno=20;    * DNAME=null" but left join doesn't work.</p> */
annotation|@
name|Test
specifier|public
name|void
name|testScalarSubQuery
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"empid\", \"deptno\",\n"
operator|+
literal|" (select \"name\" from \"hr\".\"depts\"\n"
operator|+
literal|"  where \"deptno\" = e.\"deptno\") as dname\n"
operator|+
literal|"from \"hr\".\"emps\" as e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; DNAME=Sales"
argument_list|,
literal|"empid=110; deptno=10; DNAME=Sales"
argument_list|,
literal|"empid=150; deptno=10; DNAME=Sales"
argument_list|,
literal|"empid=200; deptno=20; DNAME=null"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-559">[CALCITE-559]    * Correlated scalar sub-query in WHERE gives error</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinCorrelatedScalarSubQuery
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select e.employee_id, d.department_id "
operator|+
literal|" from employee e, department d "
operator|+
literal|" where e.department_id = d.department_id "
operator|+
literal|" and e.salary> (select avg(e2.salary) "
operator|+
literal|"                 from employee e2 "
operator|+
literal|"                 where e2.store_id = e.store_id)"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|599
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-685">[CALCITE-685]    * Correlated scalar sub-query in SELECT clause throws</a>. */
annotation|@
name|Ignore
argument_list|(
literal|"[CALCITE-685]"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCorrelatedScalarSubQuery
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select e.department_id, sum(e.employee_id),\n"
operator|+
literal|"       ( select sum(e2.employee_id)\n"
operator|+
literal|"         from  employee e2\n"
operator|+
literal|"         where e.department_id = e2.department_id\n"
operator|+
literal|"       )\n"
operator|+
literal|"from employee e\n"
operator|+
literal|"group by e.department_id\n"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"EnumerableJoin(condition=[true], joinType=[left])\n"
operator|+
literal|"  EnumerableAggregate(group=[{7}], EXPR$1=[$SUM0($0)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[foodmart2, employee]])\n"
operator|+
literal|"  EnumerableAggregate(group=[{}], EXPR$0=[SUM($0)])\n"
operator|+
literal|"    EnumerableCalc(expr#0..16=[{inputs}], expr#17=[$cor0], expr#18=[$t17.department_id], expr#19=[=($t18, $t7)], employee_id=[$t0], department_id=[$t7], $condition=[$t19])\n"
operator|+
literal|"      EnumerableTableScan(table=[[foodmart2, employee]])\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLeftJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"deptno\", d.\"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"  left join \"hr\".\"depts\" as d using (\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=20; deptno=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFullJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"deptno\", d.\"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"  full join \"hr\".\"depts\" as d using (\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=20; deptno=null"
argument_list|,
literal|"deptno=null; deptno=30"
argument_list|,
literal|"deptno=null; deptno=40"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRightJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"deptno\", d.\"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"  right join \"hr\".\"depts\" as d using (\"deptno\")"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=null; deptno=30"
argument_list|,
literal|"deptno=null; deptno=40"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2464">[CALCITE-2464]    * Allow to set nullability for columns of structured types</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testLeftJoinWhereStructIsNotNull
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"deptno\", d.\"deptno\"\n"
operator|+
literal|"from \"hr\".\"emps\" as e\n"
operator|+
literal|"  left join \"hr\".\"depts\" as d using (\"deptno\")"
operator|+
literal|"where d.\"location\" is not null"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|,
literal|"deptno=10; deptno=10"
argument_list|)
expr_stmt|;
block|}
comment|/** Various queries against EMP and DEPT, in particular involving composite    * join conditions in various flavors of outer join. Results are verified    * against MySQL (except full join, which MySQL does not support). */
annotation|@
name|Test
specifier|public
name|void
name|testVariousOuter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp join dept on emp.deptno = dept.deptno"
decl_stmt|;
name|withEmpDept
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"ENAME=Alice; DEPTNO=30; GENDER=F; DEPTNO0=30; DNAME=Engineering"
argument_list|,
literal|"ENAME=Bob  ; DEPTNO=10; GENDER=M; DEPTNO0=10; DNAME=Sales      "
argument_list|,
literal|"ENAME=Eric ; DEPTNO=20; GENDER=M; DEPTNO0=20; DNAME=Marketing  "
argument_list|,
literal|"ENAME=Jane ; DEPTNO=10; GENDER=F; DEPTNO0=10; DNAME=Sales      "
argument_list|,
literal|"ENAME=Susan; DEPTNO=30; GENDER=F; DEPTNO0=30; DNAME=Engineering"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|withEmpDept
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
comment|// Append a 'WITH' clause that supplies EMP and DEPT tables like this:
comment|//
comment|// drop table emp;
comment|// drop table dept;
comment|// create table emp(ename varchar(10), deptno int, gender varchar(1));
comment|// insert into emp values ('Jane', 10, 'F');
comment|// insert into emp values ('Bob', 10, 'M');
comment|// insert into emp values ('Eric', 20, 'M');
comment|// insert into emp values ('Susan', 30, 'F');
comment|// insert into emp values ('Alice', 30, 'F');
comment|// insert into emp values ('Adam', 50, 'M');
comment|// insert into emp values ('Eve', 50, 'F');
comment|// insert into emp values ('Grace', 60, 'F');
comment|// insert into emp values ('Wilma', null, 'F');
comment|// create table dept (deptno int, dname varchar(12));
comment|// insert into dept values (10, 'Sales');
comment|// insert into dept values (20, 'Marketing');
comment|// insert into dept values (30, 'Engineering');
comment|// insert into dept values (40, 'Empty');
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"with\n"
operator|+
literal|"  emp(ename, deptno, gender) as (values\n"
operator|+
literal|"    ('Jane', 10, 'F'),\n"
operator|+
literal|"    ('Bob', 10, 'M'),\n"
operator|+
literal|"    ('Eric', 20, 'M'),\n"
operator|+
literal|"    ('Susan', 30, 'F'),\n"
operator|+
literal|"    ('Alice', 30, 'F'),\n"
operator|+
literal|"    ('Adam', 50, 'M'),\n"
operator|+
literal|"    ('Eve', 50, 'F'),\n"
operator|+
literal|"    ('Grace', 60, 'F'),\n"
operator|+
literal|"    ('Wilma', cast(null as integer), 'F')),\n"
operator|+
literal|"  dept(deptno, dname) as (values\n"
operator|+
literal|"    (10, 'Sales'),\n"
operator|+
literal|"    (20, 'Marketing'),\n"
operator|+
literal|"    (30, 'Engineering'),\n"
operator|+
literal|"    (40, 'Empty'))\n"
operator|+
name|sql
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScalarSubQueryUncorrelated
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select \"empid\", \"deptno\",\n"
operator|+
literal|" (select \"name\" from \"hr\".\"depts\"\n"
operator|+
literal|"  where \"deptno\" = 30) as dname\n"
operator|+
literal|"from \"hr\".\"emps\" as e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; deptno=10; DNAME=Marketing"
argument_list|,
literal|"empid=110; deptno=10; DNAME=Marketing"
argument_list|,
literal|"empid=150; deptno=10; DNAME=Marketing"
argument_list|,
literal|"empid=200; deptno=20; DNAME=Marketing"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScalarSubQueryInCase
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"name\",\n"
operator|+
literal|" (CASE e.\"deptno\"\n"
operator|+
literal|"  WHEN (Select \"deptno\" from \"hr\".\"depts\" d\n"
operator|+
literal|"        where d.\"deptno\" = e.\"deptno\")\n"
operator|+
literal|"  THEN (Select d.\"name\" from \"hr\".\"depts\" d\n"
operator|+
literal|"        where d.\"deptno\" = e.\"deptno\")\n"
operator|+
literal|"  ELSE 'DepartmentNotFound'  END) AS DEPTNAME\n"
operator|+
literal|"from \"hr\".\"emps\" e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; DEPTNAME=Sales"
argument_list|,
literal|"name=Eric; DEPTNAME=DepartmentNotFound"
argument_list|,
literal|"name=Sebastian; DEPTNAME=Sales"
argument_list|,
literal|"name=Theodore; DEPTNAME=Sales"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScalarSubQueryInCase2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|query
argument_list|(
literal|"select e.\"name\",\n"
operator|+
literal|" (CASE WHEN e.\"deptno\" = (\n"
operator|+
literal|"    Select \"deptno\" from \"hr\".\"depts\" d\n"
operator|+
literal|"    where d.\"name\" = 'Sales')\n"
operator|+
literal|"  THEN 'Sales'\n"
operator|+
literal|"  ELSE 'Not Matched'  END) AS DEPTNAME\n"
operator|+
literal|"from \"hr\".\"emps\" e"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; DEPTNAME=Sales      "
argument_list|,
literal|"name=Eric; DEPTNAME=Not Matched"
argument_list|,
literal|"name=Sebastian; DEPTNAME=Sales      "
argument_list|,
literal|"name=Theodore; DEPTNAME=Sales      "
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the TABLES table in the information schema. */
annotation|@
name|Test
specifier|public
name|void
name|testMetaTables
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR_PLUS_METADATA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"metadata\".TABLES"
argument_list|)
operator|.
name|returns
argument_list|(
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"tableSchem=metadata; tableName=COLUMNS; tableType=SYSTEM TABLE; "
argument_list|)
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR_PLUS_METADATA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(distinct \"tableSchem\") as c\n"
operator|+
literal|"from \"metadata\".TABLES"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=3\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that {@link java.sql.Statement#setMaxRows(int)} is honored. */
annotation|@
name|Test
specifier|public
name|void
name|testSetMaxRows
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|statement
operator|.
name|setMaxRows
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"illegal maxRows value: -1"
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|setMaxRows
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|statement
operator|.
name|getMaxRows
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"hr\".\"emps\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a {@link PreparedStatement} with parameters. */
annotation|@
name|Test
specifier|public
name|void
name|testPreparedStatement
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|PreparedStatement
name|preparedStatement
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select \"deptno\", \"name\" "
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"where \"deptno\"< ? and \"name\" like ?"
argument_list|)
decl_stmt|;
comment|// execute with vars unbound - gives error
name|ResultSet
name|resultSet
decl_stmt|;
try|try
block|{
name|resultSet
operator|=
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|resultSet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
name|containsString
argument_list|(
literal|"exception while executing query: unbound parameter"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// execute with both vars null - no results
name|preparedStatement
operator|.
name|setNull
argument_list|(
literal|1
argument_list|,
name|Types
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|preparedStatement
operator|.
name|setNull
argument_list|(
literal|2
argument_list|,
name|Types
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|resultSet
operator|=
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// execute with ?0=15, ?1='%' - 3 rows
name|preparedStatement
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|preparedStatement
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"%"
argument_list|)
expr_stmt|;
name|resultSet
operator|=
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deptno=10; name=Bill\n"
operator|+
literal|"deptno=10; name=Sebastian\n"
operator|+
literal|"deptno=10; name=Theodore\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
comment|// execute with ?0=15 (from last bind), ?1='%r%' - 1 row
name|preparedStatement
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
literal|"%r%"
argument_list|)
expr_stmt|;
name|resultSet
operator|=
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deptno=10; name=Theodore\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now BETWEEN, with 3 arguments, 2 of which are parameters
specifier|final
name|String
name|sql2
init|=
literal|"select \"deptno\", \"name\" "
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"where \"deptno\" between symmetric ? and ?\n"
operator|+
literal|"order by 2"
decl_stmt|;
specifier|final
name|PreparedStatement
name|preparedStatement2
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql2
argument_list|)
decl_stmt|;
name|preparedStatement2
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|preparedStatement2
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|resultSet
operator|=
name|preparedStatement2
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"deptno=10; name=Bill\n"
operator|+
literal|"deptno=10; name=Sebastian\n"
operator|+
literal|"deptno=10; name=Theodore\n"
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|preparedStatement2
operator|.
name|close
argument_list|()
expr_stmt|;
name|preparedStatement
operator|.
name|close
argument_list|()
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2061">[CALCITE-2061]    * Dynamic parameters in offset/fetch</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testPreparedOffsetFetch
parameter_list|()
throws|throws
name|Exception
block|{
name|checkPreparedOffsetFetch
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Matchers
operator|.
name|returnsUnordered
argument_list|()
argument_list|)
expr_stmt|;
name|checkPreparedOffsetFetch
argument_list|(
literal|100
argument_list|,
literal|4
argument_list|,
name|Matchers
operator|.
name|returnsUnordered
argument_list|()
argument_list|)
expr_stmt|;
name|checkPreparedOffsetFetch
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
name|Matchers
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Eric"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkPreparedOffsetFetch
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|fetch
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|?
super|super
name|ResultSet
argument_list|>
name|matcher
parameter_list|)
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|hr
argument_list|()
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"name\"\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"order by \"empid\" offset ? fetch next ? rows only"
decl_stmt|;
try|try
init|(
name|PreparedStatement
name|p
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
init|)
block|{
specifier|final
name|ParameterMetaData
name|pmd
init|=
name|p
operator|.
name|getParameterMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterType
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterType
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|p
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
try|try
init|(
name|ResultSet
name|r
init|=
name|p
operator|.
name|executeQuery
argument_list|()
init|)
block|{
name|assertThat
argument_list|(
name|r
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model (a single schema based on    * a JDBC database). */
annotation|@
name|Test
specifier|public
name|void
name|testModel
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"time_by_day\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=730\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JSON model with a comment. Not standard JSON, but harmless to    * allow Jackson's comments extension.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-160">[CALCITE-160]    * Allow comments in schema definitions</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testModelWithComment
parameter_list|()
block|{
specifier|final
name|String
name|model
init|=
name|FOODMART_MODEL
operator|.
name|replace
argument_list|(
literal|"schemas:"
argument_list|,
literal|"/* comment */ schemas:"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|model
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|FOODMART_MODEL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"time_by_day\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=730\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Defines a materialized view and tests that the query is rewritten to use    * it, and that the query produces the same result with and without it. There    * are more comprehensive tests in {@link MaterializationTest}. */
annotation|@
name|Ignore
argument_list|(
literal|"until JdbcSchema can define materialized views"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testModelWithMaterializedView
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|FOODMART_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
literal|false
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" join \"foodmart\".\"time_by_day\" using (\"time_id\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=86837\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|FOODMART_MODEL
argument_list|,
literal|"agg_c_10_sales_fact_1997"
argument_list|,
literal|"select t.`month_of_year`, t.`quarter`, t.`the_year`, sum(s.`store_sales`) as `store_sales`, sum(s.`store_cost`), sum(s.`unit_sales`), count(distinct s.`customer_id`), count(*) as `fact_count` from `time_by_day` as t join `sales_fact_1997` as s using (`time_id`) group by t.`month_of_year`, t.`quarter`, t.`the_year`"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select t.\"month_of_year\", t.\"quarter\", t.\"the_year\", sum(s.\"store_sales\") as \"store_sales\", sum(s.\"store_cost\"), sum(s.\"unit_sales\"), count(distinct s.\"customer_id\"), count(*) as \"fact_count\" from \"time_by_day\" as t join \"sales_fact_1997\" as s using (\"time_id\") group by t.\"month_of_year\", t.\"quarter\", t.\"the_year\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"JdbcTableScan(table=[[foodmart, agg_c_10_sales_fact_1997]])"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|false
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"JdbcTableScan(table=[[foodmart, sales_fact_1997]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTable
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': 1, 'bar': [345, 357] }\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
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
literal|"select * from \"adhoc\".EMPLOYEES where \"deptno\" = 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTable2
parameter_list|()
block|{
name|testRangeTable
argument_list|(
literal|"object"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTableArrayRowSingleColumn
parameter_list|()
block|{
name|testRangeTable
argument_list|(
literal|"array"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains custom    * tables. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomTableIntegerRowSingleColumn
parameter_list|()
block|{
name|testRangeTable
argument_list|(
literal|"integer"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testRangeTable
parameter_list|(
name|String
name|elementType
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'MATH',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'INTEGERS',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|RangeTable
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'column': 'N', 'start': 3, 'end': 7, "
operator|+
literal|" 'elementType': '"
operator|+
name|elementType
operator|+
literal|"'}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
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
literal|"select * from math.integers"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"N=3\n"
operator|+
literal|"N=4\n"
operator|+
literal|"N=5\n"
operator|+
literal|"N=6\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a custom    * schema. */
annotation|@
name|Test
specifier|public
name|void
name|testModelCustomSchema
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|that
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'empty'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'adhoc',\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      factory: '"
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"      operand: {'tableName': 'ELVIS'}\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
decl_stmt|;
comment|// check that the specified 'defaultSchema' was used
name|that
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|"adhoc"
argument_list|,
name|connection
operator|.
name|getSchema
argument_list|()
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".ELVIS where \"deptno\" = 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
argument_list|)
expr_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".EMPLOYEES"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'EMPLOYEES' not found within 'adhoc'"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1360">[CALCITE-1360]    * Custom schema in file in current directory</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCustomSchemaInFileInPwd
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkCustomSchemaInFileInPwd
argument_list|(
literal|"custom-schema-model.json"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|File
operator|.
name|pathSeparatorChar
condition|)
block|{
case|case
literal|'/'
case|:
comment|// Skip this test on Windows; the mapping from file names to URLs is too
comment|// weird.
name|checkCustomSchemaInFileInPwd
argument_list|(
literal|"."
operator|+
name|File
operator|.
name|pathSeparatorChar
operator|+
literal|"custom-schema-model2.json"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkCustomSchemaInFileInPwd
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
try|try
init|(
name|PrintWriter
name|pw
init|=
name|Util
operator|.
name|printWriter
argument_list|(
name|file
argument_list|)
init|)
block|{
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'empty'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'adhoc',\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      factory: '"
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"      operand: {'tableName': 'ELVIS'}\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|url
init|=
literal|"jdbc:calcite:model="
operator|+
name|file
decl_stmt|;
try|try
init|(
name|Connection
name|c
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
init|;
name|Statement
name|s
init|=
name|c
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|r
init|=
name|s
operator|.
name|executeQuery
argument_list|(
literal|"values 1"
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|r
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
block|}
comment|//noinspection ResultOfMethodCallIgnored
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// current directory is not writable; an environment issue, not
comment|// necessarily a bug
block|}
block|}
comment|/** Connects to a custom schema without writing a model.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1259">[CALCITE-1259]    * Allow connecting to a single schema without writing a model</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCustomSchemaDirectConnection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|url
init|=
literal|"jdbc:calcite:"
operator|+
literal|"schemaFactory="
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"; schema.tableName=ELVIS"
decl_stmt|;
name|checkCustomSchema
argument_list|(
name|url
argument_list|,
literal|"adhoc"
argument_list|)
expr_stmt|;
comment|// implicit schema is called 'adhoc'
name|checkCustomSchema
argument_list|(
name|url
operator|+
literal|"; schema=xyz"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
comment|// explicit schema
block|}
specifier|private
name|void
name|checkCustomSchema
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|schemaName
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|connection
operator|.
name|getSchema
argument_list|()
argument_list|,
name|is
argument_list|(
name|schemaName
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from \""
operator|+
name|schemaName
operator|+
literal|"\".ELVIS where \"deptno\" = 10"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select * from ELVIS where \"deptno\" = 10"
decl_stmt|;
name|String
name|expected
init|=
literal|""
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
decl_stmt|;
try|try
init|(
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|)
block|{
try|try
init|(
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql2
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Connects to a JDBC schema without writing a model. */
annotation|@
name|Test
specifier|public
name|void
name|testJdbcSchemaDirectConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|checkJdbcSchemaDirectConnection
argument_list|(
literal|"schemaFactory=org.apache.calcite.adapter.jdbc.JdbcSchema$Factory"
argument_list|)
expr_stmt|;
name|checkJdbcSchemaDirectConnection
argument_list|(
literal|"schemaType=JDBC"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkJdbcSchemaDirectConnection
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcUser"
argument_list|,
name|SCOTT
operator|.
name|username
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcPassword"
argument_list|,
name|SCOTT
operator|.
name|password
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcUrl"
argument_list|,
name|SCOTT
operator|.
name|url
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcCatalog"
argument_list|,
name|SCOTT
operator|.
name|catalog
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcDriver"
argument_list|,
name|SCOTT
operator|.
name|driver
argument_list|)
expr_stmt|;
name|pv
argument_list|(
name|b
argument_list|,
literal|"schema.jdbcSchema"
argument_list|,
name|SCOTT
operator|.
name|schema
argument_list|)
expr_stmt|;
specifier|final
name|String
name|url
init|=
name|b
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|connection
operator|.
name|getSchema
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"adhoc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"C=14\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from emp"
decl_stmt|;
try|try
init|(
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|pv
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|p
parameter_list|,
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Connects to a map schema without writing a model. */
annotation|@
name|Test
specifier|public
name|void
name|testMapSchemaDirectConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMapSchemaDirectConnection
argument_list|(
literal|"schemaType=MAP"
argument_list|)
expr_stmt|;
name|checkMapSchemaDirectConnection
argument_list|(
literal|"schemaFactory=org.apache.calcite.schema.impl.AbstractSchema$Factory"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkMapSchemaDirectConnection
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|url
init|=
literal|"jdbc:calcite:"
operator|+
name|s
decl_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|connection
operator|.
name|getSchema
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"adhoc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"EXPR$0=1\n"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"values 1"
decl_stmt|;
try|try
init|(
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests that an immutable schema in a model cannot contain a view. */
annotation|@
name|Test
specifier|public
name|void
name|testModelImmutableSchemaCannotContainView
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'empty'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    {\n"
operator|+
literal|"      name: 'adhoc',\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      tables: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          name: 'v',\n"
operator|+
literal|"          type: 'view',\n"
operator|+
literal|"          sql: 'values (1)'\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      factory: '"
operator|+
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"      operand: {\n"
operator|+
literal|"           'tableName': 'ELVIS',\n"
operator|+
literal|"           'mutable': false\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Cannot define view; parent schema 'adhoc' is not mutable"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|modelWithView
parameter_list|(
name|String
name|view
parameter_list|,
name|Boolean
name|modifiable
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|EmpDeptTableFactory
argument_list|>
name|clazz
init|=
name|EmpDeptTableFactory
operator|.
name|class
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MUTABLE_EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': false}\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'V',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
operator|(
name|modifiable
operator|==
literal|null
condition|?
literal|""
else|:
literal|" modifiable: "
operator|+
name|modifiable
operator|+
literal|",\n"
operator|)
operator|+
literal|"           sql: "
operator|+
operator|new
name|JsonBuilder
argument_list|()
operator|.
name|toJsonString
argument_list|(
name|view
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
return|;
block|}
comment|/** Tests a JDBC connection that provides a model that contains a view. */
annotation|@
name|Test
specifier|public
name|void
name|testModelView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|modelWithView
argument_list|(
literal|"select * from \"EMPLOYEES\" where \"deptno\" = 10"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"adhoc\".V order by \"name\" desc"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
operator|+
literal|"empid=110; deptno=10; name=Theodore; salary=11500.0; commission=250\n"
operator|+
literal|"empid=150; deptno=10; name=Sebastian; salary=7000.0; commission=null\n"
operator|+
literal|"empid=100; deptno=10; name=Bill; salary=10000.0; commission=1000\n"
argument_list|)
expr_stmt|;
comment|// Make sure that views appear in metadata.
name|with
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
comment|// all table types
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=EMPLOYEES; TABLE_TYPE=TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=MUTABLE_EMPLOYEES; TABLE_TYPE=TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; TABLE_TYPE=VIEW; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// including system tables; note that table type is "SYSTEM TABLE"
comment|// not "SYSTEM_TABLE"
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=EMPLOYEES; TABLE_TYPE=TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=MUTABLE_EMPLOYEES; TABLE_TYPE=TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; TABLE_TYPE=VIEW; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=metadata; TABLE_NAME=COLUMNS; TABLE_TYPE=SYSTEM TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=metadata; TABLE_NAME=TABLES; TABLE_TYPE=SYSTEM TABLE; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// views only
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
name|Schema
operator|.
name|TableType
operator|.
name|VIEW
operator|.
name|jdbcName
block|}
init|)
argument_list|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; TABLE_TYPE=VIEW; REMARKS=null; TYPE_CAT=null; TYPE_SCHEM=null; TYPE_NAME=null; SELF_REFERENCING_COL_NAME=null; REF_GENERATION=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
block|;         }
comment|// columns
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|,
literal|"V"
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=empid; DATA_TYPE=4; TYPE_NAME=JavaType(int) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=1; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_SCHEMA=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=; IS_GENERATEDCOLUMN=\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=deptno; DATA_TYPE=4; TYPE_NAME=JavaType(int) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=2; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_SCHEMA=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=; IS_GENERATEDCOLUMN=\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=name; DATA_TYPE=12; TYPE_NAME=JavaType(class java.lang.String); COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=1; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=3; IS_NULLABLE=YES; SCOPE_CATALOG=null; SCOPE_SCHEMA=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=; IS_GENERATEDCOLUMN=\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=salary; DATA_TYPE=7; TYPE_NAME=JavaType(float) NOT NULL; COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=0; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=4; IS_NULLABLE=NO; SCOPE_CATALOG=null; SCOPE_SCHEMA=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=; IS_GENERATEDCOLUMN=\n"
operator|+
literal|"TABLE_CAT=null; TABLE_SCHEM=adhoc; TABLE_NAME=V; COLUMN_NAME=commission; DATA_TYPE=4; TYPE_NAME=JavaType(class java.lang.Integer); COLUMN_SIZE=-1; BUFFER_LENGTH=null; DECIMAL_DIGITS=null; NUM_PREC_RADIX=10; NULLABLE=1; REMARKS=null; COLUMN_DEF=null; SQL_DATA_TYPE=null; SQL_DATETIME_SUB=null; CHAR_OCTET_LENGTH=-1; ORDINAL_POSITION=5; IS_NULLABLE=YES; SCOPE_CATALOG=null; SCOPE_SCHEMA=null; SCOPE_TABLE=null; SOURCE_DATA_TYPE=null; IS_AUTOINCREMENT=; IS_GENERATEDCOLUMN=\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// catalog
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getCatalogs
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_CAT=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// schemas
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getSchemas
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_SCHEM=adhoc; TABLE_CATALOG=null\n"
operator|+
literal|"TABLE_SCHEM=metadata; TABLE_CATALOG=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// schemas (qualified)
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getSchemas
argument_list|(
literal|null
argument_list|,
literal|"adhoc"
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_SCHEM=adhoc; TABLE_CATALOG=null\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// table types
try|try
init|(
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getTableTypes
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
literal|"TABLE_TYPE=TABLE\n"
operator|+
literal|"TABLE_TYPE=VIEW\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_comment
unit|}
comment|/** Tests a view with ORDER BY and LIMIT clauses. */
end_comment

begin_function
unit|@
name|Test
specifier|public
name|void
name|testOrderByView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|modelWithView
argument_list|(
literal|"select * from \"EMPLOYEES\" where \"deptno\" = 10 "
operator|+
literal|"order by \"empid\" limit 2"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V order by \"name\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"name=Bill\n"
operator|+
literal|"name=Theodore\n"
argument_list|)
expr_stmt|;
comment|// Now a sub-query with ORDER BY and LIMIT clauses. (Same net effect, but
comment|// ORDER BY and LIMIT in sub-query were not standard SQL until SQL:2008.)
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" from (\n"
operator|+
literal|"select * from \"adhoc\".\"EMPLOYEES\" where \"deptno\" = 10\n"
operator|+
literal|"order by \"empid\" limit 2)\n"
operator|+
literal|"order by \"name\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"name=Bill\n"
operator|+
literal|"name=Theodore\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1900">[CALCITE-1900]    * Improve error message for cyclic views</a>.    * Previously got a {@link StackOverflowError}. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testSelfReferentialView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|modelWithView
argument_list|(
literal|"select * from \"V\""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" from \"adhoc\".V"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'adhoc.V'; it references view 'adhoc.V', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testSelfReferentialView2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|model
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'adhoc',\n"
operator|+
literal|"  schemas: [ {\n"
operator|+
literal|"    name: 'adhoc',\n"
operator|+
literal|"    tables: [ {\n"
operator|+
literal|"      name: 'A',\n"
operator|+
literal|"      type: 'view',\n"
operator|+
literal|"      sql: "
operator|+
operator|new
name|JsonBuilder
argument_list|()
operator|.
name|toJsonString
argument_list|(
literal|"select * from B"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      name: 'B',\n"
operator|+
literal|"      type: 'view',\n"
operator|+
literal|"      sql: "
operator|+
operator|new
name|JsonBuilder
argument_list|()
operator|.
name|toJsonString
argument_list|(
literal|"select * from C"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      name: 'C',\n"
operator|+
literal|"      type: 'view',\n"
operator|+
literal|"      sql: "
operator|+
operator|new
name|JsonBuilder
argument_list|()
operator|.
name|toJsonString
argument_list|(
literal|"select * from D, B"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      name: 'D',\n"
operator|+
literal|"      type: 'view',\n"
operator|+
literal|"      sql: "
operator|+
operator|new
name|JsonBuilder
argument_list|()
operator|.
name|toJsonString
argument_list|(
literal|"select * from (values (1, 'a')) as t(x, y)"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"    } ]\n"
operator|+
literal|"  } ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
decl_stmt|;
comment|//
comment|//       +-----+
comment|//       V     |
comment|// A --> B --> C --> D
comment|//
comment|// A is not in a cycle, but depends on cyclic views
comment|// B is cyclic
comment|// C is cyclic
comment|// D is not cyclic
name|with
operator|.
name|query
argument_list|(
literal|"select x from \"adhoc\".a"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'adhoc.A'; it references view 'adhoc.B', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x from \"adhoc\".b"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'adhoc.B'; it references view 'adhoc.B', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
comment|// as previous, but implicit schema
name|with
operator|.
name|query
argument_list|(
literal|"select x from b"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'B'; it references view 'adhoc.B', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x from \"adhoc\".c"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'adhoc.C'; it references view 'adhoc.C', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x from \"adhoc\".d"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"X=1\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x from \"adhoc\".d except select x from \"adhoc\".a"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot resolve 'adhoc.A'; it references view 'adhoc.B', "
operator|+
literal|"whose definition is cyclic"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests saving query results into temporary tables, per    * {@link org.apache.calcite.avatica.Handler.ResultSink}. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testAutomaticTemporaryTable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|objects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|CalciteConnection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
block|{
name|CalciteConnection
name|connection
init|=
operator|(
name|CalciteConnection
operator|)
operator|new
name|AutoTempDriver
argument_list|(
name|objects
argument_list|)
operator|.
name|connect
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from \"hr\".\"emps\" "
operator|+
literal|"where \"deptno\" = 10"
decl_stmt|;
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|objects
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testExplain
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableValues(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedXml
init|=
literal|"PLAN=<RelNode type=\"EnumerableValues\">\n"
operator|+
literal|"\t<Property name=\"tuples\">\n"
operator|+
literal|"\t\t[{ 1,&#39;ab&#39; }]\t</Property>\n"
operator|+
literal|"\t<Inputs/>\n"
operator|+
literal|"</RelNode>\n"
operator|+
literal|"\n"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan as xml for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedXml
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedJson
init|=
literal|"PLAN={\n"
operator|+
literal|"  \"rels\": [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"id\": \"0\",\n"
operator|+
literal|"      \"relOp\": \"org.apache.calcite.adapter.enumerable.EnumerableValues\",\n"
operator|+
literal|"      \"type\": [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"type\": \"INTEGER\",\n"
operator|+
literal|"          \"nullable\": false,\n"
operator|+
literal|"          \"name\": \"EXPR$0\"\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"type\": \"CHAR\",\n"
operator|+
literal|"          \"nullable\": false,\n"
operator|+
literal|"          \"precision\": 2,\n"
operator|+
literal|"          \"name\": \"EXPR$1\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      \"tuples\": [\n"
operator|+
literal|"        [\n"
operator|+
literal|"          1,\n"
operator|+
literal|"          \"ab\"\n"
operator|+
literal|"        ]\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      \"inputs\": []\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}\n"
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan as json for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedJson
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan with implementation for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EnumerableValues(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan without implementation for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=LogicalValues(tuples=[[{ 1, 'ab' }]])\n\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"explain plan with type for values (1, 'ab')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"PLAN=EXPR$0 INTEGER NOT NULL,\n"
operator|+
literal|"EXPR$1 CHAR(2) NOT NULL\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for bug where if two tables have different element classes    * but those classes have identical fields, Calcite would generate code to use    * the wrong element class; a {@link ClassCastException} would ensue. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testDifferentTypesSameFields
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"TEST"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|MySchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
name|calciteConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT \"myvalue\" from TEST.\"mytable2\""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"myvalue=2\n"
argument_list|,
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that CURRENT_TIMESTAMP gives different values each time a statement    * is executed. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testCurrentTimestamp
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TIME_ZONE
argument_list|,
literal|"GMT+1:00"
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
specifier|final
name|PreparedStatement
name|statement
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"VALUES CURRENT_TIMESTAMP"
argument_list|)
decl_stmt|;
name|ResultSet
name|resultSet
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|s0
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|s1
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\n"
operator|+
literal|"s0="
operator|+
name|s0
operator|+
literal|"\n"
operator|+
literal|"s1="
operator|+
name|s1
operator|+
literal|"\n"
argument_list|,
name|s0
operator|.
name|compareTo
argument_list|(
name|s1
argument_list|)
operator|<
literal|0
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test for timestamps and time zones, based on pgsql TimezoneTest. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testGetTimestamp
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TIME_ZONE
argument_list|,
literal|"GMT+1:00"
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|checkGetTimestamp
argument_list|(
name|connection
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|void
name|checkGetTimestamp
parameter_list|(
name|Connection
name|con
parameter_list|)
throws|throws
name|SQLException
block|{
name|Statement
name|statement
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// Not supported yet. We set timezone using connect-string parameters.
comment|//statement.executeUpdate("alter session set timezone = 'gmt-3'");
name|ResultSet
name|rs
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM (VALUES(\n"
operator|+
literal|" TIMESTAMP '1970-01-01 00:00:00',\n"
operator|+
literal|" /* TIMESTAMP '2005-01-01 15:00:00 +0300', */\n"
operator|+
literal|" TIMESTAMP '2005-01-01 15:00:00',\n"
operator|+
literal|" TIME '15:00:00',\n"
operator|+
literal|" /* TIME '15:00:00 +0300', */\n"
operator|+
literal|" DATE '2005-01-01'\n"
operator|+
literal|")) AS t(ts0, /* tstz, */ ts, t, /* tz, */ d)"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|TimeZone
name|tzUtc
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
comment|// +0000 always
name|TimeZone
name|tzGmt03
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+03"
argument_list|)
decl_stmt|;
comment|// +0300 always
name|TimeZone
name|tzGmt05
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-05"
argument_list|)
decl_stmt|;
comment|// -0500 always
name|TimeZone
name|tzGmt13
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+13"
argument_list|)
decl_stmt|;
comment|// +1000 always
name|Calendar
name|cUtc
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tzUtc
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Calendar
name|cGmt03
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tzGmt03
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Calendar
name|cGmt05
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tzGmt05
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Calendar
name|cGmt13
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tzGmt13
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|Timestamp
name|ts
decl_stmt|;
name|String
name|s
decl_stmt|;
name|int
name|c
init|=
literal|1
decl_stmt|;
comment|// timestamp: 1970-01-01 00:00:00
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +0100
name|assertEquals
argument_list|(
operator|-
literal|3600000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00 +0100
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
comment|// Convert timestamp to UTC
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00 +0000
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +0300
name|assertEquals
argument_list|(
operator|-
literal|10800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00 +0300
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
comment|// Convert timestamp to -0500
name|assertEquals
argument_list|(
literal|18000000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00 -0500
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +1300
name|assertEquals
argument_list|(
operator|-
literal|46800000
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 00:00:00 +1300
name|s
operator|=
name|rs
operator|.
name|getString
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01 00:00:00"
argument_list|,
name|s
argument_list|)
expr_stmt|;
operator|++
name|c
expr_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
comment|// timestamptz: 2005-01-01 15:00:00+03
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// Represents an instant in
comment|// time, TZ is irrelevant.
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 12:00:00 UTC
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
comment|// TZ irrelevant, as above
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 12:00:00 UTC
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
comment|// TZ irrelevant, as above
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 12:00:00 UTC
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
comment|// TZ irrelevant, as above
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 12:00:00 UTC
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
comment|// TZ irrelevant, as above
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 12:00:00 UTC
operator|++
name|c
expr_stmt|;
block|}
comment|// timestamp: 2005-01-01 15:00:00
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +0100
name|assertEquals
argument_list|(
literal|1104588000000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 15:00:00 +0100
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
comment|// Convert timestamp to UTC
name|assertEquals
argument_list|(
literal|1104591600000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 15:00:00 +0000
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +0300
name|assertEquals
argument_list|(
literal|1104580800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 15:00:00 +0300
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
comment|// Convert timestamp to -0500
name|assertEquals
argument_list|(
literal|1104609600000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 15:00:00 -0500
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
comment|// Convert timestamp to +1300
name|assertEquals
argument_list|(
literal|1104544800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 15:00:00 +1300
name|s
operator|=
name|rs
operator|.
name|getString
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2005-01-01 15:00:00"
argument_list|,
name|s
argument_list|)
expr_stmt|;
operator|++
name|c
expr_stmt|;
comment|// time: 15:00:00
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50400000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0100
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|54000000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0000
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|72000000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 -0500
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +1300
name|s
operator|=
name|rs
operator|.
name|getString
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"15:00:00"
argument_list|,
name|s
argument_list|)
expr_stmt|;
operator|++
name|c
expr_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
comment|// timetz: 15:00:00+03
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300 ->
comment|// 1970-01-01 13:00:00 +0100
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300 ->
comment|// 1970-01-01 12:00:00 +0000
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300 ->
comment|// 1970-01-01 15:00:00 +0300
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300 ->
comment|// 1970-01-01 07:00:00 -0500
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43200000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1970-01-01 15:00:00 +0300 ->
comment|// 1970-01-02 01:00:00 +1300
operator|++
name|c
expr_stmt|;
block|}
comment|// date: 2005-01-01
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1104534000000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 +0100
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cUtc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1104537600000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 +0000
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt03
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1104526800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 +0300
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1104555600000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 -0500
name|ts
operator|=
name|rs
operator|.
name|getTimestamp
argument_list|(
name|c
argument_list|,
name|cGmt13
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1104490800000L
argument_list|,
name|ts
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 +1300
name|s
operator|=
name|rs
operator|.
name|getString
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2005-01-01"
argument_list|,
name|s
argument_list|)
expr_stmt|;
comment|// 2005-01-01 00:00:00 +0100
operator|++
name|c
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests accessing a column in a JDBC source whose type is DATE. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testGetDate
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|Statement
name|stmt
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select min(\"date\") mindate from \"foodmart\".\"currency\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Date
operator|.
name|valueOf
argument_list|(
literal|"1997-01-01"
argument_list|)
argument_list|,
name|rs
operator|.
name|getDate
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|next
argument_list|()
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests accessing a date as a string in a JDBC source whose type is DATE. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testGetDateAsString
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select min(\"date\") mindate from \"foodmart\".\"currency\""
argument_list|)
operator|.
name|returns2
argument_list|(
literal|"MINDATE=1997-01-01\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testGetTimestampObject
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|Statement
name|stmt
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select \"hire_date\" from \"foodmart\".\"employee\" where \"employee_id\" = 1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Timestamp
operator|.
name|valueOf
argument_list|(
literal|"1994-12-01 00:00:00"
argument_list|)
argument_list|,
name|rs
operator|.
name|getTimestamp
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|next
argument_list|()
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testRowComparison
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_SCOTT
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT empno FROM JDBC_SCOTT.emp WHERE (ename, job)< ('Blake', 'Manager')"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EMPNO=7876"
argument_list|,
literal|"EMPNO=7499"
argument_list|,
literal|"EMPNO=7698"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testUnicode
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
decl_stmt|;
comment|// Note that \u82f1 in a Java string is a Java unicode escape;
comment|// But \\82f1 in a SQL string is a SQL unicode escape.
comment|// various ways to create a unicode string literal
name|with
operator|.
name|query
argument_list|(
literal|"values _UTF16'\u82f1\u56fd'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=\u82f1\u56fd\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values U&'\\82F1\\56FD'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=\u82f1\u56fd\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values u&'\\82f1\\56fd'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=\u82f1\u56fd\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values '\u82f1\u56fd'"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Failed to encode '\u82f1\u56fd' in character set 'ISO-8859-1'"
argument_list|)
expr_stmt|;
comment|// comparing a unicode string literal with a regular string literal
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"employee\" where \"full_name\" = '\u82f1\u56fd'"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Failed to encode '\u82f1\u56fd' in character set 'ISO-8859-1'"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"employee\" where \"full_name\" = _UTF16'\u82f1\u56fd'"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot apply = to the two different charsets ISO-8859-1 and UTF-16LE"
argument_list|)
expr_stmt|;
comment|// The CONVERT function (what SQL:2011 calls "character transliteration") is
comment|// not implemented yet. See
comment|// https://issues.apache.org/jira/browse/CALCITE-111.
name|with
operator|.
name|query
argument_list|(
literal|"select * from \"employee\"\n"
operator|+
literal|"where convert(\"full_name\" using UTF16) = _UTF16'\u82f1\u56fd'"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Column 'UTF16' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for the MySQL lexical scheme. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexMySQL
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"`"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for the MySQL ANSI lexical scheme. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexMySQLANSI
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL_ANSI
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for different the "SQL_SERVER" lexical scheme. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexSqlServer
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"["
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for the ORACLE (and default) lexical scheme. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexOracle
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Oracle JDBC 12.1.0.1.0 returns true here, however it is
comment|// not clear if the bug is in JDBC specification or Oracle
comment|// driver
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for the JAVA lexical scheme. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexJava
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"`"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests metadata for the ORACLE lexical scheme overridden like JAVA. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexOracleAsJava
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|QUOTING
argument_list|,
name|Quoting
operator|.
name|BACK_TICK
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|UNQUOTED_CASING
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|QUOTED_CASING
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CASE_SENSITIVE
argument_list|,
literal|true
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|connection
lambda|->
block|{
try|try
block|{
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|getIdentifierQuoteString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"`"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesUpperCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests case-insensitive resolution of schema and table names. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexCaseInsensitive
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from metaData.tAbles"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c=2\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`tAbles`"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c=2\n"
argument_list|)
expr_stmt|;
comment|// case-sensitive gives error
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with2
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
decl_stmt|;
name|with2
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`tAbles`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'metaData' not found; did you mean 'metadata'?"
argument_list|)
expr_stmt|;
name|with2
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`TABLES`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'metaData' not found; did you mean 'metadata'?"
argument_list|)
expr_stmt|;
name|with2
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`tables`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'metaData' not found; did you mean 'metadata'?"
argument_list|)
expr_stmt|;
name|with2
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`nonExistent`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'metaData' not found; did you mean 'metadata'?"
argument_list|)
expr_stmt|;
name|with2
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metadata`.`tAbles`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'tAbles' not found within 'metadata'; did you mean 'TABLES'?"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1563">[CALCITE-1563]    * In case-insensitive connection, non-existent tables use alphabetically    * preceding table</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexCaseInsensitiveFindsNonexistentTable
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
comment|// With [CALCITE-1563], the following query succeeded; it queried
comment|// metadata.tables.
name|with
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`zoo`"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Object 'zoo' not found within 'metadata'"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select COUNT(*) as c from `metaData`.`tAbLes`"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c=2\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests case-insensitive resolution of sub-query columns.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-550">[CALCITE-550]    * Case-insensitive matching of sub-query columns fails</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexCaseInsensitiveSubQueryField
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select DID\n"
operator|+
literal|"from (select deptid as did\n"
operator|+
literal|"         FROM\n"
operator|+
literal|"            ( values (1), (2) ) as T1(deptid)\n"
operator|+
literal|"         ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLexCaseInsensitiveTableAlias
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select e.empno\n"
operator|+
literal|"from (values (1, 2)) as E (empno, deptno),\n"
operator|+
literal|"  (values (3, 4)) as d (deptno, name)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empno=1"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testFunOracle
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FUN
argument_list|,
literal|"oracle"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select nvl(\"commission\", -99) as c from \"hr\".\"emps\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=-99"
argument_list|,
literal|"C=1000"
argument_list|,
literal|"C=250"
argument_list|,
literal|"C=500"
argument_list|)
expr_stmt|;
comment|// NVL is not present in the default operator table
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select nvl(\"commission\", -99) as c from \"hr\".\"emps\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature NVL(<NUMERIC>,<NUMERIC>)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2072">[CALCITE-2072]    * Enable spatial operator table by adding 'fun=spatial'to JDBC URL</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testFunSpatial
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct\n"
operator|+
literal|"  ST_PointFromText('POINT(-71.0642.28)') as c\n"
operator|+
literal|"from \"hr\".\"emps\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FUN
argument_list|,
literal|"spatial"
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C={\"x\":-71.0642,\"y\":0.28}"
argument_list|)
expr_stmt|;
comment|// NVL is present in the Oracle operator table, but not spatial or core
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
literal|"select nvl(\"commission\", -99) as c from \"hr\".\"emps\""
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature NVL(<NUMERIC>,<NUMERIC>)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Unit test for LATERAL CROSS JOIN to table function. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testLateralJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM AUX.SIMPLETABLE ST\n"
operator|+
literal|"CROSS JOIN LATERAL TABLE(AUX.TBLFUN(ST.INTCOL))"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|AUX
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"STRCOL=ABC; INTCOL=1; n=0; s="
argument_list|,
literal|"STRCOL=DEF; INTCOL=2; n=0; s="
argument_list|,
literal|"STRCOL=DEF; INTCOL=2; n=1; s=a"
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=0; s="
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=1; s=a"
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=2; s=ab"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Unit test for view expansion with lateral join. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testExpandViewWithLateralJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM AUX.VIEWLATERAL"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|AUX
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"STRCOL=ABC; INTCOL=1; n=0; s="
argument_list|,
literal|"STRCOL=DEF; INTCOL=2; n=0; s="
argument_list|,
literal|"STRCOL=DEF; INTCOL=2; n=1; s=a"
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=0; s="
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=1; s=a"
argument_list|,
literal|"STRCOL=GHI; INTCOL=3; n=2; s=ab"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that {@link Hook#PARSE_TREE} works. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testHook
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|callCount
init|=
block|{
literal|0
block|}
decl_stmt|;
try|try
init|(
name|Hook
operator|.
name|Closeable
name|ignored
init|=
name|Hook
operator|.
name|PARSE_TREE
operator|.
expr|<
name|Object
index|[]
operator|>
name|addThread
argument_list|(
name|args
lambda|->
block|{
name|assertThat
argument_list|(
name|args
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"select \"deptno\", \"commission\", sum(\"salary\") s\n"
operator|+
literal|"from \"hr\".\"emps\"\n"
operator|+
literal|"group by \"deptno\", \"commission\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|,
name|instanceOf
argument_list|(
name|SqlSelect
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|callCount
index|[
literal|0
index|]
expr_stmt|;
block|}
argument_list|)
init|)
block|{
comment|// Simple query does not run the hook.
name|testSimple
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|callCount
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Non-trivial query runs hook once.
name|testGroupByNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|callCount
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/** Tests {@link SqlDialect}. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testDialect
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|sqls
init|=
block|{
literal|null
block|}
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"employee\" as e1\n"
operator|+
literal|"  where \"first_name\" = 'abcde'\n"
operator|+
literal|"  and \"gender\" = 'F'"
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|QUERY_PLAN
argument_list|,
operator|(
name|Consumer
argument_list|<
name|String
argument_list|>
operator|)
name|sql
lambda|->
name|sqls
index|[
literal|0
index|]
operator|=
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=0\n"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|CalciteAssert
operator|.
name|DB
condition|)
block|{
case|case
name|HSQLDB
case|:
name|assertThat
argument_list|(
name|sqls
index|[
literal|0
index|]
argument_list|,
name|isLinux
argument_list|(
literal|"SELECT COUNT(*) AS \"C\"\n"
operator|+
literal|"FROM \"foodmart\".\"employee\"\n"
operator|+
literal|"WHERE \"first_name\" = 'abcde' AND \"gender\" = 'F'"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testExplicitImplicitSchemaSameName
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|)
operator|.
name|plus
argument_list|()
decl_stmt|;
comment|// create schema "/a"
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|aSubSchemaMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|aSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|aSubSchemaMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// add explicit schema "/a/b".
name|aSchema
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// add implicit schema "/a/b"
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|aSchema
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// explicit should win implicit.
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testSimpleCalciteSchema
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|plus
argument_list|()
decl_stmt|;
comment|// create schema "/a"
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|aSubSchemaMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|aSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|aSubSchemaMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// add explicit schema "/a/b".
name|aSchema
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// add implicit schema "/a/c"
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// add implicit schema "/a/b"
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// explicit should win implicit.
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testSimpleCalciteSchemaWithView
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|plus
argument_list|()
decl_stmt|;
specifier|final
name|Multimap
argument_list|<
name|String
argument_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|Function
argument_list|>
name|functionMap
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// create schema "/a"
specifier|final
name|SchemaPlus
name|aSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Multimap
argument_list|<
name|String
argument_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|Function
argument_list|>
name|getFunctionMultimap
parameter_list|()
block|{
return|return
name|functionMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// add view definition
specifier|final
name|String
name|viewName
init|=
literal|"V"
decl_stmt|;
specifier|final
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|Function
name|view
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|rootSchema
operator|.
name|getSubSchema
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"values('1', '2')"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|functionMap
operator|.
name|put
argument_list|(
name|viewName
argument_list|,
name|view
argument_list|)
expr_stmt|;
specifier|final
name|CalciteSchema
name|calciteSchema
init|=
name|CalciteSchema
operator|.
name|from
argument_list|(
name|aSchema
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
name|viewName
argument_list|,
literal|true
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
name|viewName
argument_list|,
literal|false
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
literal|"V1"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
literal|"V1"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getFunctions
argument_list|(
name|viewName
argument_list|,
literal|true
argument_list|)
argument_list|,
name|hasItem
argument_list|(
name|view
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getFunctions
argument_list|(
name|viewName
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hasItem
argument_list|(
name|view
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getFunctions
argument_list|(
literal|"V1"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|not
argument_list|(
name|hasItem
argument_list|(
name|view
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|calciteSchema
operator|.
name|getFunctions
argument_list|(
literal|"V1"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|not
argument_list|(
name|hasItem
argument_list|(
name|view
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testSchemaCaching
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|connection
init|=
name|CalciteAssert
operator|.
name|that
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|connect
argument_list|()
decl_stmt|;
specifier|final
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
comment|// create schema "/a"
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|aSubSchemaMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|aSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|aSubSchemaMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|aSchema
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// first call, to populate the cache
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// create schema "/a/b1". Appears only when we disable caching.
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"b1"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
literal|"b1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|aSchema
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
literal|"b1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// create schema "/a/b2". Appears immediately, because caching is disabled.
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"b2"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// an explicit sub-schema appears immediately, even if caching is enabled
name|aSchema
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|aSchema
operator|.
name|add
argument_list|(
literal|"b3"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// explicit
name|aSubSchemaMap
operator|.
name|put
argument_list|(
literal|"b4"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// implicit
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|aSchema
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create schema "/a2"
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|a2SubSchemaMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|a2Schema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|a2SubSchemaMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|a2Schema
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// create schema "/a2/b3". Change not visible since caching is enabled.
name|a2SubSchemaMap
operator|.
name|put
argument_list|(
literal|"b3"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Change visible after we turn off caching.
name|a2Schema
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|a2SubSchemaMap
operator|.
name|put
argument_list|(
literal|"b4"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|aSchema
operator|.
name|getSubSchemaNames
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|aSchema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add tables and retrieve with various case sensitivities
specifier|final
name|TableInRootSchemaTest
operator|.
name|SimpleTable
name|table
init|=
operator|new
name|TableInRootSchemaTest
operator|.
name|SimpleTable
argument_list|()
decl_stmt|;
name|a2Schema
operator|.
name|add
argument_list|(
literal|"table1"
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|a2Schema
operator|.
name|add
argument_list|(
literal|"TABLE1"
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|a2Schema
operator|.
name|add
argument_list|(
literal|"tabLe1"
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|a2Schema
operator|.
name|add
argument_list|(
literal|"tabLe2"
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2Schema
operator|.
name|getTableNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CalciteSchema
name|a2CalciteSchema
init|=
name|CalciteSchema
operator|.
name|from
argument_list|(
name|a2Schema
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|a2CalciteSchema
operator|.
name|getTable
argument_list|(
literal|"table1"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2CalciteSchema
operator|.
name|getTable
argument_list|(
literal|"table1"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2CalciteSchema
operator|.
name|getTable
argument_list|(
literal|"taBle1"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a2CalciteSchema
operator|.
name|getTable
argument_list|(
literal|"taBle1"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|TableMacro
name|function
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|a2Schema
argument_list|,
literal|"values 1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|function
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testCaseSensitiveSubQueryOracle
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select DID from (select DEPTID as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x.DID from (select DEPTID as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testUnquotedCaseSensitiveSubQueryMySql
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select DID from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select x.DID from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select X.DID from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select X.DID2 from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X (DID2)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID2=1"
argument_list|,
literal|"DID2=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select X.DID2 from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X (DID2)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID2=1"
argument_list|,
literal|"DID2=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testQuotedCaseSensitiveSubQueryMySql
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select `DID` from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select `x`.`DID` from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select `X`.`DID` from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select `X`.`DID2` from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X (DID2)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID2=1"
argument_list|,
literal|"DID2=2"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select `X`.`DID2` from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) X (DID2)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID2=1"
argument_list|,
literal|"DID2=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testUnquotedCaseSensitiveSubQuerySqlServer
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|)
operator|.
name|query
argument_list|(
literal|"select DID from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1(deptid) ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testQuotedCaseSensitiveSubQuerySqlServer
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|)
operator|.
name|query
argument_list|(
literal|"select [DID] from (select deptid as did FROM\n"
operator|+
literal|"     ( values (1), (2) ) as T1([deptid]) ) "
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"DID=1"
argument_list|,
literal|"DID=2"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-596">[CALCITE-596]    * JDBC adapter incorrectly reads null values as 0</a>.    */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testPrimitiveColumnsWithNullValues
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hsqldbMemUrl
init|=
literal|"jdbc:hsqldb:mem:."
decl_stmt|;
name|Connection
name|baseConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|hsqldbMemUrl
argument_list|)
decl_stmt|;
name|Statement
name|baseStmt
init|=
name|baseConnection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE T1 (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS DOUBLE)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T1 VALUES (1, 1.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T1 VALUES (2, null)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T1 VALUES (null, 2.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|baseConnection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'BASEJDBC',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'BASEJDBC',\n"
operator|+
literal|"       jdbcDriver: '"
operator|+
name|jdbcDriver
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       jdbcUrl: '"
operator|+
name|hsqldbMemUrl
operator|+
literal|"',\n"
operator|+
literal|"       jdbcCatalog: null,\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|Connection
name|calciteConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|ResultSet
name|rs
init|=
name|calciteConnection
operator|.
name|prepareStatement
argument_list|(
literal|"select * from t1"
argument_list|)
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|rs
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
operator|(
name|Integer
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
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
operator|(
name|Integer
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
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
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|calciteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2054">[CALCITE-2054]    * Error while validating UPDATE with dynamic parameter in SET clause</a>.    */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testUpdateBind
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hsqldbMemUrl
init|=
literal|"jdbc:hsqldb:mem:."
decl_stmt|;
try|try
init|(
name|Connection
name|baseConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|hsqldbMemUrl
argument_list|)
init|;
name|Statement
name|baseStmt
init|=
name|baseConnection
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE T2 (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS DOUBLE)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T2 VALUES (1, 1.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T2 VALUES (2, null)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T2 VALUES (null, 2.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|baseConnection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'BASEJDBC',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'BASEJDBC',\n"
operator|+
literal|"       jdbcDriver: '"
operator|+
name|jdbcDriver
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       jdbcUrl: '"
operator|+
name|hsqldbMemUrl
operator|+
literal|"',\n"
operator|+
literal|"       jdbcCatalog: null,\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
name|model
argument_list|)
expr_stmt|;
name|Connection
name|calciteConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|ResultSet
name|rs
init|=
name|calciteConnection
operator|.
name|prepareStatement
argument_list|(
literal|"select * from t2"
argument_list|)
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|rs
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
operator|(
name|Integer
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
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
operator|(
name|Integer
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
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
name|rs
operator|.
name|getObject
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|Double
operator|)
name|rs
operator|.
name|getObject
argument_list|(
literal|"VALS"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|2.0
argument_list|)
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"update t2 set vals=? where id=?"
decl_stmt|;
try|try
init|(
name|PreparedStatement
name|ps
init|=
name|calciteConnection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|ParameterMetaData
name|pmd
init|=
name|ps
operator|.
name|getParameterMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterType
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterType
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
argument_list|)
expr_stmt|;
name|ps
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|calciteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-730">[CALCITE-730]    * ClassCastException in table from CloneSchema</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testNullableNumericColumnInCloneSchema
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'SCOTT_CLONE',\n"
operator|+
literal|"  schemas: [ {\n"
operator|+
literal|"    name: 'SCOTT_CLONE',\n"
operator|+
literal|"    type: 'custom',\n"
operator|+
literal|"    factory: 'org.apache.calcite.adapter.clone.CloneSchema$Factory',\n"
operator|+
literal|"    operand: {\n"
operator|+
literal|"      jdbcDriver: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|driver
operator|+
literal|"',\n"
operator|+
literal|"      jdbcUser: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|username
operator|+
literal|"',\n"
operator|+
literal|"      jdbcPassword: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|password
operator|+
literal|"',\n"
operator|+
literal|"      jdbcUrl: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|url
operator|+
literal|"',\n"
operator|+
literal|"      jdbcSchema: 'SCOTT'\n"
operator|+
literal|"   } } ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from emp"
argument_list|)
operator|.
name|returns
argument_list|(
name|input
lambda|->
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|columnCount
init|=
name|input
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
while|while
condition|(
name|input
operator|.
name|next
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|input
operator|.
name|getObject
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1097">[CALCITE-1097]    * Exception when executing query with too many aggregation columns</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testAggMultipleMeasures
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|Driver
name|driver
init|=
operator|new
name|Driver
argument_list|()
decl_stmt|;
name|CalciteConnection
name|connection
init|=
operator|(
name|CalciteConnection
operator|)
name|driver
operator|.
name|connect
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|connection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"sale"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|Smalls
operator|.
name|WideSaleSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setSchema
argument_list|(
literal|"sale"
argument_list|)
expr_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// 200 columns: sum(sale0) + ... sum(sale199)
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select s.\"prodId\""
operator|+
name|sums
argument_list|(
literal|200
argument_list|,
literal|true
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"from \"sale\".\"prod\" as s group by s.\"prodId\"\n"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getInt
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getInt
argument_list|(
literal|200
argument_list|)
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// 800 columns:
comment|//   sum(sale0 + 0) + ... + sum(sale0 + 100) + ... sum(sale99 + 799)
specifier|final
name|int
name|n
init|=
literal|800
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select s.\"prodId\""
operator|+
name|sums
argument_list|(
name|n
argument_list|,
literal|false
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"from \"sale\".\"prod\" as s group by s.\"prodId\"\n"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
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
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getInt
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getInt
argument_list|(
name|n
argument_list|)
argument_list|,
name|is
argument_list|(
name|n
operator|+
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2224">[CALCITE-2224]    * WITHIN GROUP clause for aggregate functions</a>. */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select X,\n"
operator|+
literal|" collect(Y) within group (order by Y desc) as \"SET\"\n"
operator|+
literal|"from (values (1, 'a'), (1, 'b'),\n"
operator|+
literal|"             (3, 'c'), (3, 'd')) AS t(X, Y)\n"
operator|+
literal|"group by X\n"
operator|+
literal|"limit 10"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=1; SET=[b, a]"
argument_list|,
literal|"X=3; SET=[d, c]"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select X,\n"
operator|+
literal|" collect(Y) within group (order by Y desc) as SET_1,\n"
operator|+
literal|" collect(Y) within group (order by Y asc) as SET_2\n"
operator|+
literal|"from (values (1, 'a'), (1, 'b'), (3, 'c'), (3, 'd')) AS t(X, Y)\n"
operator|+
literal|"group by X\n"
operator|+
literal|"limit 10"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"X=1; SET_1=[b, a]; SET_2=[a, b]"
argument_list|,
literal|"X=3; SET_1=[d, c]; SET_2=[c, d]"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select"
operator|+
literal|" collect(Y) within group (order by Y desc) as SET_1,\n"
operator|+
literal|" collect(Y) within group (order by Y asc) as SET_2\n"
operator|+
literal|"from (values (1, 'a'), (1, 'b'), (3, 'c'), (3, 'd')) AS t(X, Y)\n"
operator|+
literal|"limit 10"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"SET_1=[d, c, b, a]; SET_2=[a, b, c, d]\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select"
operator|+
literal|" collect(Y) within group (order by Y desc) as SET_1,\n"
operator|+
literal|" collect(Y) within group (order by Y asc) as SET_2\n"
operator|+
literal|"from (values (1, 'a'), (1, 'b'), (3, 'c'), (3, 'd')) AS t(X, Y)\n"
operator|+
literal|"group by X\n"
operator|+
literal|"limit 10"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"SET_1=[b, a]; SET_2=[a, b]"
argument_list|,
literal|"SET_1=[d, c]; SET_2=[c, d]"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause5
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|query
argument_list|(
literal|"select collect(array[X, Y])\n"
operator|+
literal|" within group (order by Y desc) as \"SET\"\n"
operator|+
literal|"from (values ('b', 'a'), ('a', 'b'), ('a', 'c'),\n"
operator|+
literal|"             ('a', 'd')) AS t(X, Y)\n"
operator|+
literal|"limit 10"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"SET=[[a, d], [a, c], [a, b], [b, a]]\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testWithinGroupClause6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select collect(\"commission\")"
operator|+
literal|" within group (order by \"commission\")\n"
operator|+
literal|"from \"hr\".\"emps\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|REGULAR
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableAggregate(group=[{}], "
operator|+
literal|"EXPR$0=[COLLECT($4) WITHIN GROUP ([4])])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=[250, 500, 1000]\n"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2609">[CALCITE-2609]    * Dynamic parameters ("?") pushed to underlying JDBC schema, causing    * error</a>.    */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testQueryWithParameter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hsqldbMemUrl
init|=
literal|"jdbc:hsqldb:mem:."
decl_stmt|;
try|try
init|(
name|Connection
name|baseConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|hsqldbMemUrl
argument_list|)
init|;
name|Statement
name|baseStmt
init|=
name|baseConnection
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE T3 (\n"
operator|+
literal|"ID INTEGER,\n"
operator|+
literal|"VALS DOUBLE)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T3 VALUES (1, 1.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T3 VALUES (2, null)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|execute
argument_list|(
literal|"INSERT INTO T3 VALUES (null, 2.0)"
argument_list|)
expr_stmt|;
name|baseStmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|baseConnection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'BASEJDBC',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'BASEJDBC',\n"
operator|+
literal|"       jdbcDriver: '"
operator|+
name|jdbcDriver
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       jdbcUrl: '"
operator|+
name|hsqldbMemUrl
operator|+
literal|"',\n"
operator|+
literal|"       jdbcCatalog: null,\n"
operator|+
literal|"       jdbcSchema: null\n"
operator|+
literal|"     }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
name|model
argument_list|)
expr_stmt|;
name|Connection
name|calciteConnection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from t3 where vals = ?"
decl_stmt|;
try|try
init|(
name|PreparedStatement
name|ps
init|=
name|calciteConnection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|ParameterMetaData
name|pmd
init|=
name|ps
operator|.
name|getParameterMetaData
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pmd
operator|.
name|getParameterType
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|Types
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setDouble
argument_list|(
literal|1
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|ps
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
block|}
name|calciteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_function
specifier|private
specifier|static
name|String
name|sums
parameter_list|(
name|int
name|n
parameter_list|,
name|boolean
name|c
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|c
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", sum(s.\"sale"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|", sum(s.\"sale"
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|%
literal|100
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
operator|.
name|append
argument_list|(
literal|" + "
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
end_function

begin_comment
comment|// Disable checkstyle, so it doesn't complain about fields like "customer_id".
end_comment

begin_comment
comment|//CHECKSTYLE: OFF
end_comment

begin_class
specifier|public
specifier|static
class|class
name|HrSchema
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HrSchema"
return|;
block|}
specifier|public
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|,
literal|"Bill"
argument_list|,
literal|10000
argument_list|,
literal|1000
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|200
argument_list|,
literal|20
argument_list|,
literal|"Eric"
argument_list|,
literal|8000
argument_list|,
literal|500
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|150
argument_list|,
literal|10
argument_list|,
literal|"Sebastian"
argument_list|,
literal|7000
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|10
argument_list|,
literal|"Theodore"
argument_list|,
literal|11500
argument_list|,
literal|250
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Department
index|[]
name|depts
init|=
block|{
operator|new
name|Department
argument_list|(
literal|10
argument_list|,
literal|"Sales"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|0
index|]
argument_list|,
name|emps
index|[
literal|2
index|]
argument_list|)
argument_list|,
operator|new
name|Location
argument_list|(
operator|-
literal|122
argument_list|,
literal|38
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|30
argument_list|,
literal|"Marketing"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|Location
argument_list|(
literal|0
argument_list|,
literal|52
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|40
argument_list|,
literal|"HR"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|emps
index|[
literal|1
index|]
argument_list|)
argument_list|,
literal|null
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Dependent
index|[]
name|dependents
init|=
block|{
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"Michael"
argument_list|)
block|,
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"Jane"
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Dependent
index|[]
name|locations
init|=
block|{
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"San Francisco"
argument_list|)
block|,
operator|new
name|Dependent
argument_list|(
literal|20
argument_list|,
literal|"San Diego"
argument_list|)
block|,     }
decl_stmt|;
specifier|public
name|QueryableTable
name|foo
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|Smalls
operator|.
name|generateStrings
argument_list|(
name|count
argument_list|)
return|;
block|}
specifier|public
name|TranslatableTable
name|view
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|Smalls
operator|.
name|view
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Employee
block|{
specifier|public
specifier|final
name|int
name|empid
decl_stmt|;
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|float
name|salary
decl_stmt|;
specifier|public
specifier|final
name|Integer
name|commission
decl_stmt|;
specifier|public
name|Employee
parameter_list|(
name|int
name|empid
parameter_list|,
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|,
name|float
name|salary
parameter_list|,
name|Integer
name|commission
parameter_list|)
block|{
name|this
operator|.
name|empid
operator|=
name|empid
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|salary
operator|=
name|salary
expr_stmt|;
name|this
operator|.
name|commission
operator|=
name|commission
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Employee [empid: "
operator|+
name|empid
operator|+
literal|", deptno: "
operator|+
name|deptno
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Employee
operator|&&
name|empid
operator|==
operator|(
operator|(
name|Employee
operator|)
name|obj
operator|)
operator|.
name|empid
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Department
block|{
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|java
operator|.
name|Array
argument_list|(
name|component
operator|=
name|Employee
operator|.
name|class
argument_list|)
specifier|public
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
decl_stmt|;
specifier|public
specifier|final
name|Location
name|location
decl_stmt|;
specifier|public
name|Department
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|employees
operator|=
name|employees
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Department [deptno: "
operator|+
name|deptno
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|", employees: "
operator|+
name|employees
operator|+
literal|", location: "
operator|+
name|location
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Department
operator|&&
name|deptno
operator|==
operator|(
operator|(
name|Department
operator|)
name|obj
operator|)
operator|.
name|deptno
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Location
block|{
specifier|public
specifier|final
name|int
name|x
decl_stmt|;
specifier|public
specifier|final
name|int
name|y
decl_stmt|;
specifier|public
name|Location
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Location [x: "
operator|+
name|x
operator|+
literal|", y: "
operator|+
name|y
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Location
operator|&&
name|x
operator|==
operator|(
operator|(
name|Location
operator|)
name|obj
operator|)
operator|.
name|x
operator|&&
name|y
operator|==
operator|(
operator|(
name|Location
operator|)
name|obj
operator|)
operator|.
name|y
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Dependent
block|{
specifier|public
specifier|final
name|int
name|empid
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|Dependent
parameter_list|(
name|int
name|empid
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|empid
operator|=
name|empid
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Dependent [empid: "
operator|+
name|empid
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Dependent
operator|&&
name|empid
operator|==
operator|(
operator|(
name|Dependent
operator|)
name|obj
operator|)
operator|.
name|empid
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Dependent
operator|)
name|obj
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Event
block|{
specifier|public
specifier|final
name|int
name|eventid
decl_stmt|;
specifier|public
specifier|final
name|Timestamp
name|ts
decl_stmt|;
specifier|public
name|Event
parameter_list|(
name|int
name|eventid
parameter_list|,
name|Timestamp
name|ts
parameter_list|)
block|{
name|this
operator|.
name|eventid
operator|=
name|eventid
expr_stmt|;
name|this
operator|.
name|ts
operator|=
name|ts
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Event [eventid: "
operator|+
name|eventid
operator|+
literal|", ts: "
operator|+
name|ts
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Event
operator|&&
name|eventid
operator|==
operator|(
operator|(
name|Event
operator|)
name|obj
operator|)
operator|.
name|eventid
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|FoodmartSchema
block|{
specifier|public
specifier|final
name|SalesFact
index|[]
name|sales_fact_1997
init|=
block|{
operator|new
name|SalesFact
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|SalesFact
argument_list|(
literal|150
argument_list|,
literal|20
argument_list|)
block|,     }
decl_stmt|;
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|LingualSchema
block|{
specifier|public
specifier|final
name|LingualEmp
index|[]
name|EMPS
init|=
block|{
operator|new
name|LingualEmp
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|LingualEmp
argument_list|(
literal|2
argument_list|,
literal|30
argument_list|)
block|}
decl_stmt|;
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|LingualEmp
block|{
specifier|public
specifier|final
name|int
name|EMPNO
decl_stmt|;
specifier|public
specifier|final
name|int
name|DEPTNO
decl_stmt|;
specifier|public
name|LingualEmp
parameter_list|(
name|int
name|EMPNO
parameter_list|,
name|int
name|DEPTNO
parameter_list|)
block|{
name|this
operator|.
name|EMPNO
operator|=
name|EMPNO
expr_stmt|;
name|this
operator|.
name|DEPTNO
operator|=
name|DEPTNO
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|LingualEmp
operator|&&
name|EMPNO
operator|==
operator|(
operator|(
name|LingualEmp
operator|)
name|obj
operator|)
operator|.
name|EMPNO
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|FoodmartJdbcSchema
extends|extends
name|JdbcSchema
block|{
specifier|public
name|FoodmartJdbcSchema
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|JdbcConvention
name|convention
parameter_list|,
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|dataSource
argument_list|,
name|dialect
argument_list|,
name|convention
argument_list|,
name|catalog
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|Table
name|customer
init|=
name|getTable
argument_list|(
literal|"customer"
argument_list|)
decl_stmt|;
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|Customer
block|{
specifier|public
specifier|final
name|int
name|customer_id
decl_stmt|;
specifier|public
name|Customer
parameter_list|(
name|int
name|customer_id
parameter_list|)
block|{
name|this
operator|.
name|customer_id
operator|=
name|customer_id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Customer
operator|&&
name|customer_id
operator|==
operator|(
operator|(
name|Customer
operator|)
name|obj
operator|)
operator|.
name|customer_id
return|;
block|}
block|}
end_class

begin_class
specifier|public
specifier|static
class|class
name|SalesFact
block|{
specifier|public
specifier|final
name|int
name|cust_id
decl_stmt|;
specifier|public
specifier|final
name|int
name|prod_id
decl_stmt|;
specifier|public
name|SalesFact
parameter_list|(
name|int
name|cust_id
parameter_list|,
name|int
name|prod_id
parameter_list|)
block|{
name|this
operator|.
name|cust_id
operator|=
name|cust_id
expr_stmt|;
name|this
operator|.
name|prod_id
operator|=
name|prod_id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|SalesFact
operator|&&
name|cust_id
operator|==
operator|(
operator|(
name|SalesFact
operator|)
name|obj
operator|)
operator|.
name|cust_id
operator|&&
name|prod_id
operator|==
operator|(
operator|(
name|SalesFact
operator|)
name|obj
operator|)
operator|.
name|prod_id
return|;
block|}
block|}
end_class

begin_comment
comment|//CHECKSTYLE: ON
end_comment

begin_comment
comment|/** Abstract base class for implementations of {@link ModifiableTable}. */
end_comment

begin_class
specifier|public
specifier|abstract
specifier|static
class|class
name|AbstractModifiableTable
extends|extends
name|AbstractTable
implements|implements
name|ModifiableTable
block|{
specifier|protected
name|AbstractModifiableTable
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TableModify
name|toModificationRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|TableModify
operator|.
name|Operation
name|operation
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|sourceExpressionList
parameter_list|,
name|boolean
name|flattened
parameter_list|)
block|{
return|return
name|LogicalTableModify
operator|.
name|create
argument_list|(
name|table
argument_list|,
name|catalogReader
argument_list|,
name|child
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|sourceExpressionList
argument_list|,
name|flattened
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/** Abstract base class for implementations of {@link ModifiableView}. */
end_comment

begin_class
specifier|public
specifier|abstract
specifier|static
class|class
name|AbstractModifiableView
extends|extends
name|AbstractTable
implements|implements
name|ModifiableView
block|{
specifier|protected
name|AbstractModifiableView
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/** Factory for EMP and DEPT tables. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|EmpDeptTableFactory
implements|implements
name|TableFactory
argument_list|<
name|Table
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|TryThreadLocal
argument_list|<
name|List
argument_list|<
name|Employee
argument_list|>
argument_list|>
name|THREAD_COLLECTION
init|=
name|TryThreadLocal
operator|.
name|of
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|public
name|Table
name|create
parameter_list|(
name|SchemaPlus
name|schema
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
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|final
name|Object
index|[]
name|array
decl_stmt|;
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"EMPLOYEES"
case|:
name|clazz
operator|=
name|Employee
operator|.
name|class
expr_stmt|;
name|array
operator|=
operator|new
name|HrSchema
argument_list|()
operator|.
name|emps
expr_stmt|;
break|break;
case|case
literal|"MUTABLE_EMPLOYEES"
case|:
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
init|=
name|THREAD_COLLECTION
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|employees
operator|==
literal|null
condition|)
block|{
name|employees
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
return|return
name|JdbcFrontLinqBackTest
operator|.
name|mutable
argument_list|(
name|name
argument_list|,
name|employees
argument_list|)
return|;
case|case
literal|"DEPARTMENTS"
case|:
name|clazz
operator|=
name|Department
operator|.
name|class
expr_stmt|;
name|array
operator|=
operator|new
name|HrSchema
argument_list|()
operator|.
name|depts
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
operator|new
name|AbstractQueryableTable
argument_list|(
name|clazz
argument_list|)
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|createType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|(
name|List
operator|)
name|Arrays
operator|.
name|asList
argument_list|(
name|array
argument_list|)
decl_stmt|;
return|return
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|/** Schema factory that creates {@link MySchema} objects. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|MySchemaFactory
implements|implements
name|SchemaFactory
block|{
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
specifier|final
name|boolean
name|mutable
init|=
name|SqlFunctions
operator|.
name|isNotFalse
argument_list|(
operator|(
name|Boolean
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"mutable"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|HrSchema
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
comment|// Mine the EMPS table and add it under another name e.g. ELVIS
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tableMap
init|=
name|super
operator|.
name|getTableMap
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|tableMap
operator|.
name|get
argument_list|(
literal|"emps"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tableName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"tableName"
argument_list|)
decl_stmt|;
return|return
name|FlatLists
operator|.
name|append
argument_list|(
name|tableMap
argument_list|,
name|tableName
argument_list|,
name|table
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|mutable
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|/** Mock driver that has a handler that stores the results of each query in    * a temporary table. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|AutoTempDriver
extends|extends
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|Driver
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|results
decl_stmt|;
name|AutoTempDriver
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|results
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Handler
name|createHandler
parameter_list|()
block|{
return|return
operator|new
name|HandlerImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onStatementExecute
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|ResultSink
name|resultSink
parameter_list|)
block|{
name|super
operator|.
name|onStatementExecute
argument_list|(
name|statement
argument_list|,
name|resultSink
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|resultSink
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|/** Mock driver that a given {@link Handler}. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|HandlerDriver
extends|extends
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|Driver
block|{
specifier|private
specifier|static
specifier|final
name|TryThreadLocal
argument_list|<
name|Handler
argument_list|>
name|HANDLERS
init|=
name|TryThreadLocal
operator|.
name|of
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|public
name|HandlerDriver
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|protected
name|Handler
name|createHandler
parameter_list|()
block|{
return|return
name|HANDLERS
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|/** Mock driver that can execute a trivial DDL statement. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|MockDdlDriver
extends|extends
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|Driver
block|{
specifier|public
name|int
name|counter
decl_stmt|;
specifier|public
name|MockDdlDriver
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|protected
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
name|createPrepareFactory
parameter_list|()
block|{
return|return
operator|new
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CalcitePrepare
name|apply
parameter_list|()
block|{
return|return
operator|new
name|CalcitePrepareImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SqlParser
operator|.
name|ConfigBuilder
name|createParserConfig
parameter_list|()
block|{
return|return
name|super
operator|.
name|createParserConfig
argument_list|()
operator|.
name|setParserFactory
argument_list|(
name|stream
lambda|->
operator|new
name|SqlParserImpl
argument_list|(
name|stream
argument_list|)
block|{
block_content|@Override public SqlNode parseSqlStmtEof(
argument_list|)
block|{
return|return
operator|new
name|SqlCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"COMMIT"
argument_list|,
name|SqlKind
operator|.
name|COMMIT
argument_list|)
return|;
block|}
expr|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
argument_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
empty_stmt|;
block|}
block|}
block|)
empty_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|executeDdl
parameter_list|(
name|Context
name|context
parameter_list|,
name|SqlNode
name|node
parameter_list|)
block|{
operator|++
name|counter
expr_stmt|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_empty_stmt
unit|}       }
empty_stmt|;
end_empty_stmt

begin_comment
unit|}   }
comment|/** Dummy table. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|MyTable
block|{
specifier|public
name|String
name|mykey
init|=
literal|"foo"
decl_stmt|;
specifier|public
name|Integer
name|myvalue
init|=
literal|1
decl_stmt|;
block|}
end_class

begin_comment
comment|/** Another dummy table. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|MyTable2
block|{
specifier|public
name|String
name|mykey
init|=
literal|"foo"
decl_stmt|;
specifier|public
name|Integer
name|myvalue
init|=
literal|2
decl_stmt|;
block|}
end_class

begin_comment
comment|/** Schema containing dummy tables. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|MySchema
block|{
specifier|public
name|MyTable
index|[]
name|mytable
init|=
block|{
operator|new
name|MyTable
argument_list|()
block|}
decl_stmt|;
specifier|public
name|MyTable2
index|[]
name|mytable2
init|=
block|{
operator|new
name|MyTable2
argument_list|()
block|}
decl_stmt|;
block|}
end_class

begin_comment
unit|}
comment|// End JdbcTest.java
end_comment

end_unit

