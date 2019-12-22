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
name|adapter
operator|.
name|elasticsearch
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
name|rex
operator|.
name|RexBuilder
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
name|schema
operator|.
name|impl
operator|.
name|ViewTableMacro
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
name|SqlCollation
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
name|RelRunner
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
name|NlsString
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ObjectNode
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
name|io
operator|.
name|LineProcessor
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
name|io
operator|.
name|Resources
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
name|BeforeAll
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
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|RegisterExtension
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|DriverManager
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
name|SQLException
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|hasTree
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
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Testing Elasticsearch match query.  */
end_comment

begin_class
specifier|public
class|class
name|MatchTest
block|{
annotation|@
name|RegisterExtension
comment|//init once for all tests
specifier|public
specifier|static
specifier|final
name|EmbeddedElasticsearchPolicy
name|NODE
init|=
name|EmbeddedElasticsearchPolicy
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/** Default index/type name */
specifier|private
specifier|static
specifier|final
name|String
name|ZIPS
init|=
literal|"zips"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ZIPS_SIZE
init|=
literal|149
decl_stmt|;
comment|/**    * Used to create {@code zips} index and insert zip data in bulk.    * @throws Exception when instance setup failed    */
annotation|@
name|BeforeAll
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"city"
argument_list|,
literal|"text"
argument_list|,
literal|"state"
argument_list|,
literal|"keyword"
argument_list|,
literal|"pop"
argument_list|,
literal|"long"
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|createIndex
argument_list|(
name|ZIPS
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
comment|// load records from file
specifier|final
name|List
argument_list|<
name|ObjectNode
argument_list|>
name|bulk
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Resources
operator|.
name|readLines
argument_list|(
name|ElasticSearchAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/zips-mini.json"
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|,
operator|new
name|LineProcessor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|processLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
block|{
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"_id"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
comment|// _id is a reserved attribute in ES
name|bulk
operator|.
name|add
argument_list|(
operator|(
name|ObjectNode
operator|)
name|NODE
operator|.
name|mapper
argument_list|()
operator|.
name|readTree
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|getResult
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|bulk
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No records to index. Empty file ?"
argument_list|)
throw|;
block|}
name|NODE
operator|.
name|insertBulk
argument_list|(
name|ZIPS
argument_list|,
name|bulk
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|ConnectionFactory
name|newConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:lex=JAVA"
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|root
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"elastic"
argument_list|,
operator|new
name|ElasticsearchSchema
argument_list|(
name|NODE
operator|.
name|restClient
argument_list|()
argument_list|,
name|NODE
operator|.
name|mapper
argument_list|()
argument_list|,
name|ZIPS
argument_list|)
argument_list|)
expr_stmt|;
comment|// add calcite view programmatically
specifier|final
name|String
name|viewSql
init|=
literal|"select cast(_MAP['city'] AS varchar(20)) AS \"city\", "
operator|+
literal|" cast(_MAP['loc'][0] AS float) AS \"longitude\",\n"
operator|+
literal|" cast(_MAP['loc'][1] AS float) AS \"latitude\",\n"
operator|+
literal|" cast(_MAP['pop'] AS integer) AS \"pop\", "
operator|+
literal|" cast(_MAP['state'] AS varchar(2)) AS \"state\", "
operator|+
literal|" cast(_MAP['id'] AS varchar(5)) AS \"id\" "
operator|+
literal|"from \"elastic\".\"zips\""
decl_stmt|;
name|ViewTableMacro
name|macro
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|root
argument_list|,
name|viewSql
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"elastic"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"elastic"
argument_list|,
literal|"view"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"zips"
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
comment|/**    * Test the ElasticSearch match query. The match query is translated from CONTAINS query which    * is build using RelBuilder, RexBuilder because the normal sql query assumes CONTAINS query    * is for date/period range    *    * Equivalent SQL query: select * from zips where city contains 'waltham'    *    * ElasticSearch query for it:    * {"query":{"constant_score":{"filter":{"match":{"city":"waltham"}}}}}    *    * @throws Exception    */
annotation|@
name|Test
specifier|public
name|void
name|testMatchQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteConnection
name|con
init|=
operator|(
name|CalciteConnection
operator|)
name|newConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|SchemaPlus
name|postschema
init|=
name|con
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getSubSchema
argument_list|(
literal|"elastic"
argument_list|)
decl_stmt|;
name|FrameworkConfig
name|postConfig
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|postschema
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|postConfig
argument_list|)
decl_stmt|;
name|builder
operator|.
name|scan
argument_list|(
name|ZIPS
argument_list|)
expr_stmt|;
specifier|final
name|RelDataTypeFactory
name|relDataTypeFactory
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
name|RexBuilder
name|rexbuilder
init|=
operator|new
name|RexBuilder
argument_list|(
name|relDataTypeFactory
argument_list|)
decl_stmt|;
name|RexNode
name|nameRexNode
init|=
name|rexbuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ITEM
argument_list|,
name|rexbuilder
operator|.
name|makeInputRef
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
name|rexbuilder
operator|.
name|makeCharLiteral
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|"city"
argument_list|,
name|rexbuilder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|SqlCollation
operator|.
name|COERCIBLE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|mapType
init|=
name|relDataTypeFactory
operator|.
name|createMapType
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|relDataTypeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
name|namedList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|namedList
operator|.
name|add
argument_list|(
name|rexbuilder
operator|.
name|makeInputRef
argument_list|(
name|mapType
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|namedList
operator|.
name|add
argument_list|(
name|nameRexNode
argument_list|)
expr_stmt|;
comment|//Add fields in builder stack so it is accessible while filter preparation
name|builder
operator|.
name|projectNamed
argument_list|(
name|namedList
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"_MAP"
argument_list|,
literal|"city"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RexNode
name|filterRexNode
init|=
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONTAINS
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"city"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"waltham"
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|filter
argument_list|(
name|filterRexNode
argument_list|)
expr_stmt|;
name|String
name|builderExpected
init|=
literal|""
operator|+
literal|"LogicalFilter(condition=[CONTAINS($1, 'waltham')])\n"
operator|+
literal|"  LogicalProject(_MAP=[$0], city=[ITEM($0, 'city')])\n"
operator|+
literal|"    LogicalTableScan(table=[[elastic, zips]])\n"
decl_stmt|;
name|RelNode
name|root
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelRunner
name|ru
init|=
operator|(
name|RelRunner
operator|)
name|con
operator|.
name|unwrap
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.calcite.tools.RelRunner"
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|PreparedStatement
name|preparedStatement
init|=
name|ru
operator|.
name|prepare
argument_list|(
name|root
argument_list|)
init|)
block|{
name|String
name|s
init|=
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"_MAP={id=02154, city=NORTH WALTHAM, loc=[-71.236497, 42.382492], pop=57871, state=MA}; city=NORTH WALTHAM\n"
decl_stmt|;
comment|//Validate query prepared
name|assertThat
argument_list|(
name|root
argument_list|,
name|hasTree
argument_list|(
name|builderExpected
argument_list|)
argument_list|)
expr_stmt|;
comment|//Validate result returned from ES
name|assertThat
argument_list|(
name|s
argument_list|,
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

