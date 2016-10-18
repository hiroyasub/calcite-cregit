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
name|DataContext
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|Enumerable
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
name|runtime
operator|.
name|CalciteContextException
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
name|ScannableTable
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
name|Statistic
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
name|Statistics
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
name|junit
operator|.
name|Test
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
name|ResultSet
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
name|HashMap
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test cases for  *<a href="https://issues.apache.org/jira/browse/CALCITE-1386">[CALCITE-1386]  * ITEM operator seems to ignore the value type of collection and assign the value to Object</a>.  */
end_comment

begin_class
specifier|public
class|class
name|CollectionTypeTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAccessNestedMap
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
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
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\"['c'] AS \"MAPFIELD_C\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"NESTEDMAPFIELD\"['a']['b'] = 2 AND \"ARRAYFIELD\"[2] = 200"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
comment|// JDBC doesn't support Map / Nested Map so just relying on string representation
name|String
name|expectedRow
init|=
literal|"ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; "
operator|+
literal|"ARRAYFIELD=[100, 200, 300]"
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNonExistKeyFromMap
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// this shouldn't throw any Exceptions on runtime, just don't return any rows.
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\","
operator|+
literal|" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"MAPFIELD\"['a'] = 2"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNonExistKeyFromNestedMap
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// this shouldn't throw any Exceptions on runtime, just don't return any rows.
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"NESTEDMAPFIELD\"['b']['c'] = 4"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidAccessUseStringForIndexOnArray
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
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
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\","
operator|+
literal|" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"ARRAYFIELD\"['a'] = 200"
decl_stmt|;
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This query shouldn't be evaluated properly"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|Throwable
name|e2
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|e2
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|CalciteContextException
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedArrayOutOfBoundAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
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
name|String
name|sql
init|=
literal|"select \"ID\","
operator|+
literal|" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"ARRAYFIELD\"[10] = 200"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
comment|// this is against SQL standard definition...
comment|// SQL standard states that data exception should be occurred
comment|// when accessing array with out of bound index.
comment|// but PostgreSQL breaks it, and this is more convenient since it guarantees runtime safety.
name|assertThat
argument_list|(
name|resultStrings
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNestedMapWithAnyType
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedAnyTypeTable
argument_list|()
decl_stmt|;
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
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\"['c'] AS \"MAPFIELD_C\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where CAST(\"NESTEDMAPFIELD\"['a']['b'] AS INTEGER) = 2"
operator|+
literal|" AND CAST(\"ARRAYFIELD\"[2] AS INTEGER) = 200"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
comment|// JDBC doesn't support Map / Nested Map so just relying on string representation
name|String
name|expectedRow
init|=
literal|"ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; "
operator|+
literal|"ARRAYFIELD=[100, 200, 300]"
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNestedMapWithAnyTypeWithoutCast
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedAnyTypeTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// placing literal earlier than ANY type is intended: do not modify
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\"['c'] AS \"MAPFIELD_C\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"NESTEDMAPFIELD\"['a']['b'] = 2 AND 200.0 = \"ARRAYFIELD\"[2]"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
comment|// JDBC doesn't support Map / Nested Map so just relying on string representation
name|String
name|expectedRow
init|=
literal|"ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; "
operator|+
literal|"ARRAYFIELD=[100, 200, 300]"
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArithmeticToAnyTypeWithoutCast
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedAnyTypeTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// placing literal earlier than ANY type is intended: do not modify
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\"['c'] AS \"MAPFIELD_C\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where \"NESTEDMAPFIELD\"['a']['b'] + 1.0 = 3 "
operator|+
literal|"AND \"NESTEDMAPFIELD\"['a']['b'] * 2.0 = 4 "
operator|+
literal|"AND \"NESTEDMAPFIELD\"['a']['b']> 1"
operator|+
literal|"AND \"NESTEDMAPFIELD\"['a']['b']>= 2"
operator|+
literal|"AND 100.1<> \"ARRAYFIELD\"[2] - 100.0"
operator|+
literal|"AND 100.0 = \"ARRAYFIELD\"[2] / 2"
operator|+
literal|"AND 99.9< \"ARRAYFIELD\"[2] / 2"
operator|+
literal|"AND 100.0<= \"ARRAYFIELD\"[2] / 2"
operator|+
literal|"AND '200'<> \"STRINGARRAYFIELD\"[1]"
operator|+
literal|"AND '200' = \"STRINGARRAYFIELD\"[2]"
operator|+
literal|"AND '100'< \"STRINGARRAYFIELD\"[2]"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
comment|// JDBC doesn't support Map / Nested Map so just relying on string representation
name|String
name|expectedRow
init|=
literal|"ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; "
operator|+
literal|"ARRAYFIELD=[100, 200, 300]"
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNonExistKeyFromMapWithAnyType
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// this shouldn't throw any Exceptions on runtime, just don't return any rows.
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\", "
operator|+
literal|"\"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where CAST(\"MAPFIELD\"['a'] AS INTEGER) = 2"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessNonExistKeyFromNestedMapWithAnyType
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
comment|// this shouldn't throw any Exceptions on runtime, just don't return any rows.
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where CAST(\"NESTEDMAPFIELD\"['b']['c'] AS INTEGER) = 4"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultStrings
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidAccessUseStringForIndexOnArrayWithAnyType
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
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
specifier|final
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where CAST(\"ARRAYFIELD\"['a'] AS INTEGER) = 200"
decl_stmt|;
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This query shouldn't be evaluated properly"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|Throwable
name|e2
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|e2
argument_list|,
name|is
argument_list|(
name|instanceOf
argument_list|(
name|CalciteContextException
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedArrayOutOfBoundAccessWithAnyType
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|setupConnectionWithNestedTable
argument_list|()
decl_stmt|;
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
name|String
name|sql
init|=
literal|"select \"ID\", \"MAPFIELD\","
operator|+
literal|" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" "
operator|+
literal|"from \"s\".\"nested\" "
operator|+
literal|"where CAST(\"ARRAYFIELD\"[10] AS INTEGER) = 200"
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|resultStrings
init|=
name|CalciteAssert
operator|.
name|toList
argument_list|(
name|resultSet
argument_list|)
decl_stmt|;
comment|// this is against SQL standard definition...
comment|// SQL standard states that data exception should be occurred
comment|// when accessing array with out of bound index.
comment|// but PostgreSQL breaks it, and this is more convenient since it guarantees runtime safety.
name|assertThat
argument_list|(
name|resultStrings
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
block|}
specifier|private
name|Connection
name|setupConnectionWithNestedTable
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
name|schema
operator|.
name|add
argument_list|(
literal|"nested"
argument_list|,
operator|new
name|NestedCollectionTable
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|private
name|Connection
name|setupConnectionWithNestedAnyTypeTable
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
name|schema
operator|.
name|add
argument_list|(
literal|"nested"
argument_list|,
operator|new
name|NestedCollectionWithAnyTypeTable
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|public
specifier|static
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|nestedRecordsEnumerator
parameter_list|()
block|{
specifier|final
name|Object
index|[]
index|[]
name|records
init|=
name|setupNestedRecords
argument_list|()
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
name|int
name|row
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|returnCount
init|=
literal|0
decl_stmt|;
name|Object
index|[]
name|current
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
while|while
condition|(
operator|++
name|row
operator|<
literal|5
condition|)
block|{
name|this
operator|.
name|current
operator|=
name|records
index|[
name|row
index|]
expr_stmt|;
operator|++
name|returnCount
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|row
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Object
index|[]
index|[]
name|setupNestedRecords
parameter_list|()
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|ints
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|,
literal|300
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"100"
argument_list|,
literal|"200"
argument_list|,
literal|"300"
argument_list|)
decl_stmt|;
name|Object
index|[]
index|[]
name|records
init|=
operator|new
name|Object
index|[
literal|5
index|]
index|[]
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
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
name|i
operator|*
name|i
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|mm
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mm
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|records
index|[
name|i
index|]
operator|=
operator|new
name|Object
index|[]
block|{
name|i
block|,
name|map
block|,
name|mm
block|,
name|ints
block|,
name|strings
block|}
expr_stmt|;
block|}
return|return
name|records
return|;
block|}
comment|/** Table that returns columns which include complicated collection type via the ScannableTable    * interface. */
specifier|public
specifier|static
class|class
name|NestedCollectionTable
implements|implements
name|ScannableTable
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|RelDataType
name|nullableVarcharType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RelDataType
name|nullableIntegerType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RelDataType
name|nullableMapType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|nullableVarcharType
argument_list|,
name|nullableIntegerType
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"ID"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|add
argument_list|(
literal|"MAPFIELD"
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|nullableVarcharType
argument_list|,
name|nullableIntegerType
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"NESTEDMAPFIELD"
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|nullableVarcharType
argument_list|,
name|nullableMapType
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"ARRAYFIELD"
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|nullableIntegerType
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"STRINGARRAYFIELD"
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|nullableVarcharType
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|nestedRecordsEnumerator
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Table that returns columns which include complicated collection type via the ScannableTable    * interface. */
specifier|public
specifier|static
class|class
name|NestedCollectionWithAnyTypeTable
implements|implements
name|ScannableTable
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
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"ID"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|add
argument_list|(
literal|"MAPFIELD"
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|add
argument_list|(
literal|"NESTEDMAPFIELD"
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|add
argument_list|(
literal|"ARRAYFIELD"
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|add
argument_list|(
literal|"STRINGARRAYFIELD"
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|nestedRecordsEnumerator
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CollectionTypeTest.java
end_comment

end_unit

