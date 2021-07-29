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
name|ImmutableSet
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
name|function
operator|.
name|UnaryOperator
import|;
end_import

begin_comment
comment|/**  * Tests for {@link RelToSqlConverter} on a schema that has nested structures of multiple  * levels.  */
end_comment

begin_class
class|class
name|RelToSqlConverterStructsTest
block|{
specifier|private
name|RelToSqlConverterTest
operator|.
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|RelToSqlConverterTest
operator|.
name|Sql
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|MY_DB
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
argument_list|,
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
annotation|@
name|Test
name|void
name|testNestedSchemaSelectStar
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT * FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"ROW(ROW(\"n1\".\"n11\".\"b\"), ROW(\"n1\".\"n12\".\"c\")) AS \"n1\", "
operator|+
literal|"ROW(\"n2\".\"d\") AS \"n2\", "
operator|+
literal|"\"e\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
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
name|testNestedSchemaRootColumns
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT \"a\", \"e\" FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"\"e\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
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
name|testNestedSchemaNestedColumns
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT \"a\", \"e\", "
operator|+
literal|"\"myTable\".\"n1\".\"n11\".\"b\", "
operator|+
literal|"\"myTable\".\"n2\".\"d\" "
operator|+
literal|"FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"\"e\", "
operator|+
literal|"\"n1\".\"n11\".\"b\", "
operator|+
literal|"\"n2\".\"d\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
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
block|}
end_class

end_unit

