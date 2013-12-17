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
name|optiq
operator|.
name|model
operator|.
name|*
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
name|util
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for data models.  */
end_comment

begin_class
specifier|public
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
specifier|public
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
specifier|public
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
specifier|public
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
literal|"       operand: {a: 'foo', b: [1, 3.5] }\n"
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
block|}
comment|/** Tests that an immutable schema in a model cannot contain a    * materialization. */
annotation|@
name|Test
specifier|public
name|void
name|testModelImmutableSchemaCannotContainMaterialization
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OptiqAssert
operator|.
name|AssertThat
name|that
init|=
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
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
decl_stmt|;
name|that
operator|.
name|connectThrows
argument_list|(
literal|"Cannot define materialization; parent schema 'adhoc' is not a "
operator|+
literal|"SemiMutableSchema"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ModelTest.java
end_comment

end_unit

