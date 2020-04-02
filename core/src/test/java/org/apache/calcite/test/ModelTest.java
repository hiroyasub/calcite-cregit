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
name|model
operator|.
name|JsonColumn
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
name|model
operator|.
name|JsonCustomSchema
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
name|model
operator|.
name|JsonCustomTable
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
name|model
operator|.
name|JsonJdbcSchema
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
name|model
operator|.
name|JsonLattice
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
name|model
operator|.
name|JsonMapSchema
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
name|model
operator|.
name|JsonRoot
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
name|model
operator|.
name|JsonTable
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
name|model
operator|.
name|JsonTypeAttribute
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
name|model
operator|.
name|JsonView
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
name|core
operator|.
name|JsonParser
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
name|ObjectMapper
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|assertEquals
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
name|assertNull
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit test for data models.  */
end_comment

begin_class
class|class
name|ModelTest
block|{
specifier|private
name|ObjectMapper
name|mapper
parameter_list|()
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|mapper
return|;
block|}
comment|/** Reads a simple schema from a string into objects. */
annotation|@
name|Test
name|void
name|testRead
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
name|mapper
argument_list|()
decl_stmt|;
name|JsonRoot
name|root
init|=
name|mapper
operator|.
name|readValue
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'FoodMart',\n"
operator|+
literal|"       types: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'mytype1',\n"
operator|+
literal|"           attributes: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'f1',\n"
operator|+
literal|"               type: 'BIGINT'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'time_by_day',\n"
operator|+
literal|"           columns: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'time_id'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'sales_fact_1997',\n"
operator|+
literal|"           columns: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'time_id'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
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
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|root
operator|.
name|version
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|root
operator|.
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonMapSchema
name|schema
init|=
operator|(
name|JsonMapSchema
operator|)
name|root
operator|.
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FoodMart"
argument_list|,
name|schema
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|schema
operator|.
name|types
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|JsonTypeAttribute
argument_list|>
name|attributes
init|=
name|schema
operator|.
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|attributes
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f1"
argument_list|,
name|attributes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BIGINT"
argument_list|,
name|attributes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|schema
operator|.
name|tables
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonTable
name|table0
init|=
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"time_by_day"
argument_list|,
name|table0
operator|.
name|name
argument_list|)
expr_stmt|;
specifier|final
name|JsonTable
name|table1
init|=
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sales_fact_1997"
argument_list|,
name|table1
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|table0
operator|.
name|columns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonColumn
name|column
init|=
name|table0
operator|.
name|columns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"time_id"
argument_list|,
name|column
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Reads a simple schema containing JdbcSchema, a sub-type of Schema. */
annotation|@
name|Test
name|void
name|testSubtype
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
name|mapper
argument_list|()
decl_stmt|;
name|JsonRoot
name|root
init|=
name|mapper
operator|.
name|readValue
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'jdbc',\n"
operator|+
literal|"       name: 'FoodMart',\n"
operator|+
literal|"       jdbcUser: 'u_baz',\n"
operator|+
literal|"       jdbcPassword: 'p_baz',\n"
operator|+
literal|"       jdbcUrl: 'jdbc:baz',\n"
operator|+
literal|"       jdbcCatalog: 'cat_baz',\n"
operator|+
literal|"       jdbcSchema: ''\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|root
operator|.
name|version
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|root
operator|.
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonJdbcSchema
name|schema
init|=
operator|(
name|JsonJdbcSchema
operator|)
name|root
operator|.
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FoodMart"
argument_list|,
name|schema
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Reads a custom schema. */
annotation|@
name|Test
name|void
name|testCustomSchema
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
name|mapper
argument_list|()
decl_stmt|;
name|JsonRoot
name|root
init|=
name|mapper
operator|.
name|readValue
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'My Custom Schema',\n"
operator|+
literal|"       factory: 'com.acme.MySchemaFactory',\n"
operator|+
literal|"       operand: {a: 'foo', b: [1, 3.5] },\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         { type: 'custom', name: 'T1' },\n"
operator|+
literal|"         { type: 'custom', name: 'T2', operand: {} },\n"
operator|+
literal|"         { type: 'custom', name: 'T3', operand: {a: 'foo'} }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     },\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'has-no-operand'\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|root
operator|.
name|version
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|root
operator|.
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonCustomSchema
name|schema
init|=
operator|(
name|JsonCustomSchema
operator|)
name|root
operator|.
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"My Custom Schema"
argument_list|,
name|schema
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"com.acme.MySchemaFactory"
argument_list|,
name|schema
operator|.
name|factory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|schema
operator|.
name|operand
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|schema
operator|.
name|operand
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|operand
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
operator|instanceof
name|List
argument_list|)
expr_stmt|;
specifier|final
name|List
name|list
init|=
operator|(
name|List
operator|)
name|schema
operator|.
name|operand
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.5
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schema
operator|.
name|tables
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
operator|(
operator|(
name|JsonCustomTable
operator|)
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|operand
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|JsonCustomTable
operator|)
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|operand
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that an immutable schema in a model cannot contain a    * materialization. */
annotation|@
name|Test
name|void
name|testModelImmutableSchemaCannotContainMaterialization
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
literal|"      factory: '"
operator|+
name|JdbcTest
operator|.
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
literal|"      },\n"
operator|+
literal|"      materializations: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          table: 'v',\n"
operator|+
literal|"          sql: 'values (1)'\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ]\n"
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
literal|"Cannot define materialization; parent schema 'adhoc' "
operator|+
literal|"is not a SemiMutableSchema"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1899">[CALCITE-1899]    * When reading model, give error if mandatory JSON attributes are    * missing</a>.    *    *<p>Schema without name should give useful error, not    * NullPointerException. */
annotation|@
name|Test
name|void
name|testSchemaWithoutName
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
literal|"  } ]\n"
operator|+
literal|"}"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Field 'name' is required in JsonMapSchema"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCustomSchemaWithoutFactory
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
literal|"    type: 'custom',\n"
operator|+
literal|"    name: 'my_custom_schema'\n"
operator|+
literal|"  } ]\n"
operator|+
literal|"}"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Field 'factory' is required in JsonCustomSchema"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a model containing a lattice and some views. */
annotation|@
name|Test
name|void
name|testReadLattice
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
name|mapper
argument_list|()
decl_stmt|;
name|JsonRoot
name|root
init|=
name|mapper
operator|.
name|readValue
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'FoodMart',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'time_by_day',\n"
operator|+
literal|"           columns: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'time_id'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'sales_fact_1997',\n"
operator|+
literal|"           columns: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'time_id'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'V',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: 'values (1)'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'V2',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: [ 'values (1)', '(2)' ]\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       lattices: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'SalesStar',\n"
operator|+
literal|"           sql: 'select * from sales_fact_1997'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'SalesStar2',\n"
operator|+
literal|"           sql: [ 'select *', 'from sales_fact_1997' ]\n"
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
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|root
operator|.
name|version
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|root
operator|.
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonMapSchema
name|schema
init|=
operator|(
name|JsonMapSchema
operator|)
name|root
operator|.
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FoodMart"
argument_list|,
name|schema
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|schema
operator|.
name|lattices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonLattice
name|lattice0
init|=
name|schema
operator|.
name|lattices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SalesStar"
argument_list|,
name|lattice0
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"select * from sales_fact_1997"
argument_list|,
name|lattice0
operator|.
name|getSql
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonLattice
name|lattice1
init|=
name|schema
operator|.
name|lattices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SalesStar2"
argument_list|,
name|lattice1
operator|.
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"select *\nfrom sales_fact_1997\n"
argument_list|,
name|lattice1
operator|.
name|getSql
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|schema
operator|.
name|tables
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonTable
name|table1
init|=
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
operator|(
name|table1
operator|instanceof
name|JsonView
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|JsonTable
name|table2
init|=
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|table2
operator|instanceof
name|JsonView
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|JsonView
operator|)
name|table2
operator|)
operator|.
name|getSql
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"values (1)"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|JsonTable
name|table3
init|=
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|table3
operator|instanceof
name|JsonView
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|JsonView
operator|)
name|table3
operator|)
operator|.
name|getSql
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"values (1)\n(2)\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a model with bad multi-line SQL. */
annotation|@
name|Test
name|void
name|testReadBadMultiLineSql
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ObjectMapper
name|mapper
init|=
name|mapper
argument_list|()
decl_stmt|;
name|JsonRoot
name|root
init|=
name|mapper
operator|.
name|readValue
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'FoodMart',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'V',\n"
operator|+
literal|"           type: 'view',\n"
operator|+
literal|"           sql: [ 'values (1)', 2 ]\n"
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
argument_list|,
name|JsonRoot
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|root
operator|.
name|schemas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonMapSchema
name|schema
init|=
operator|(
name|JsonMapSchema
operator|)
name|root
operator|.
name|schemas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|schema
operator|.
name|tables
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|JsonView
name|table1
init|=
operator|(
name|JsonView
operator|)
name|schema
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|s
init|=
name|table1
operator|.
name|getSql
argument_list|()
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
name|RuntimeException
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
literal|"each element of a string list must be a string; found: 2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testYamlInlineDetection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// yaml model with different line endings
specifier|final
name|String
name|yamlModel
init|=
literal|"version: 1.0\r\n"
operator|+
literal|"schemas:\n"
operator|+
literal|"- type: custom\r\n"
operator|+
literal|"  name: 'MyCustomSchema'\n"
operator|+
literal|"  factory: "
operator|+
name|JdbcTest
operator|.
name|MySchemaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"\r\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|yamlModel
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|calciteConnection
lambda|->
literal|null
argument_list|)
expr_stmt|;
comment|// with a comment
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"\n  \r\n# comment\n "
operator|+
name|yamlModel
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|calciteConnection
lambda|->
literal|null
argument_list|)
expr_stmt|;
comment|// if starts with { => treated as json
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"  { "
operator|+
name|yamlModel
operator|+
literal|" }"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Unexpected character ('s' (code 115)): "
operator|+
literal|"was expecting comma to separate Object entries"
argument_list|)
expr_stmt|;
comment|// if starts with /* => treated as json
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"  /* "
operator|+
name|yamlModel
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Unexpected end-of-input in a comment"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testYamlFileDetection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|URL
name|inUrl
init|=
name|ModelTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/empty-model.yaml"
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|inUrl
argument_list|)
operator|.
name|doWithConnection
argument_list|(
name|calciteConnection
lambda|->
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

