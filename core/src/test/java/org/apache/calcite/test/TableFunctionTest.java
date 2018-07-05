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
name|TableFunction
import|;
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
name|TableFunctionImpl
import|;
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
name|SqlConformanceEnum
import|;
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
name|ArrayList
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
comment|/**  * Tests for user-defined table functions.  *  * @see UdfTest  * @see Smalls  */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionTest
block|{
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|with
parameter_list|()
block|{
specifier|final
name|String
name|c
init|=
name|Smalls
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|m
init|=
name|Smalls
operator|.
name|MULTIPLICATION_TABLE_METHOD
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|m2
init|=
name|Smalls
operator|.
name|FIBONACCI_TABLE_METHOD
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|m3
init|=
name|Smalls
operator|.
name|FIBONACCI2_TABLE_METHOD
operator|.
name|getName
argument_list|()
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
literal|"       name: 's',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'multiplication',\n"
operator|+
literal|"           className: '"
operator|+
name|c
operator|+
literal|"',\n"
operator|+
literal|"           methodName: '"
operator|+
name|m
operator|+
literal|"'\n"
operator|+
literal|"         }, {\n"
operator|+
literal|"           name: 'fibonacci',\n"
operator|+
literal|"           className: '"
operator|+
name|c
operator|+
literal|"',\n"
operator|+
literal|"           methodName: '"
operator|+
name|m2
operator|+
literal|"'\n"
operator|+
literal|"         }, {\n"
operator|+
literal|"           name: 'fibonacci2',\n"
operator|+
literal|"           className: '"
operator|+
name|c
operator|+
literal|"',\n"
operator|+
literal|"           methodName: '"
operator|+
name|m3
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
operator|.
name|withDefaultSchema
argument_list|(
literal|"s"
argument_list|)
return|;
block|}
comment|/**    * Tests a table function with literal arguments.    */
annotation|@
name|Test
specifier|public
name|void
name|testTableFunction
parameter_list|()
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
literal|"jdbc:calcite:"
argument_list|)
init|)
block|{
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|GENERATE_STRINGS_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|table
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"GenerateStrings\"(5)) as t(n, c)\n"
operator|+
literal|"where char_length(c)> 3"
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
literal|"N=4; C=abcd\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests a table function that implements {@link ScannableTable} and returns    * a single column.    */
annotation|@
name|Test
specifier|public
name|void
name|testScannableTableFunction
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MAZE_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Maze"
argument_list|,
name|table
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(5, 3, 1))"
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
name|result
init|=
literal|"S=abcde\n"
operator|+
literal|"S=xyz\n"
operator|+
literal|"S=generate(w=5, h=3, s=1)\n"
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
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testScannableTableFunction()} but with named parameters. */
annotation|@
name|Test
specifier|public
name|void
name|testScannableTableFunctionWithNamedParameters
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MAZE2_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Maze"
argument_list|,
name|table
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(5, 3, 1))"
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
name|String
name|result
init|=
literal|"S=abcde\n"
operator|+
literal|"S=xyz\n"
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
name|result
operator|+
literal|"S=generate2(w=5, h=3, s=1)\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(WIDTH => 5, HEIGHT => 3, SEED => 1))"
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql2
argument_list|)
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
name|result
operator|+
literal|"S=generate2(w=5, h=3, s=1)\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(HEIGHT => 3, WIDTH => 5))"
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql3
argument_list|)
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
name|result
operator|+
literal|"S=generate2(w=5, h=3, s=null)\n"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testScannableTableFunction()} but with named parameters. */
annotation|@
name|Test
specifier|public
name|void
name|testMultipleScannableTableFunctionWithNamedParameters
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
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
literal|"jdbc:calcite:"
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
name|TableFunction
name|table1
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MAZE_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Maze"
argument_list|,
name|table1
argument_list|)
expr_stmt|;
specifier|final
name|TableFunction
name|table2
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MAZE2_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Maze"
argument_list|,
name|table2
argument_list|)
expr_stmt|;
specifier|final
name|TableFunction
name|table3
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MAZE3_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Maze"
argument_list|,
name|table3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(5, 3, 1))"
decl_stmt|;
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
name|String
name|result
init|=
literal|"S=abcde\n"
operator|+
literal|"S=xyz\n"
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
name|result
operator|+
literal|"S=generate(w=5, h=3, s=1)\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(WIDTH => 5, HEIGHT => 3, SEED => 1))"
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql2
argument_list|)
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
name|result
operator|+
literal|"S=generate2(w=5, h=3, s=1)\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(HEIGHT => 3, WIDTH => 5))"
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql3
argument_list|)
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
name|result
operator|+
literal|"S=generate2(w=5, h=3, s=null)\n"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(FOO => 'a'))"
decl_stmt|;
name|resultSet
operator|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql4
argument_list|)
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
name|result
operator|+
literal|"S=generate3(foo=a)\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests a table function that returns different row type based on    * actual call arguments.    */
annotation|@
name|Test
specifier|public
name|void
name|testTableFunctionDynamicStructure
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Connection
name|connection
init|=
name|getConnectionWithMultiplyFunction
argument_list|()
decl_stmt|;
specifier|final
name|PreparedStatement
name|ps
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(4, 3, ?))\n"
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|ps
operator|.
name|executeQuery
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
name|equalTo
argument_list|(
literal|"row_name=row 0; c1=101; c2=102; c3=103; c4=104\n"
operator|+
literal|"row_name=row 1; c1=102; c2=104; c3=106; c4=108\n"
operator|+
literal|"row_name=row 2; c1=103; c2=106; c3=109; c4=112\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that non-nullable arguments of a table function must be provided    * as literals.    */
annotation|@
name|Ignore
argument_list|(
literal|"SQLException does not include message from nested exception"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTableFunctionNonNullableMustBeLiterals
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
name|Connection
name|connection
init|=
name|getConnectionWithMultiplyFunction
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|PreparedStatement
name|ps
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(?, 3, 100))\n"
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|ps
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"Should fail, got "
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
literal|"Wrong arguments for table function 'public static "
operator|+
literal|"org.apache.calcite.schema.QueryableTable "
operator|+
literal|"org.apache.calcite.test.JdbcTest"
operator|+
literal|".multiplicationTable(int,int,java.lang.Integer)'"
operator|+
literal|" call. Expected '[int, int, class"
operator|+
literal|"java.lang.Integer]', actual '[null, 3, 100]'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Connection
name|getConnectionWithMultiplyFunction
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MULTIPLICATION_TABLE_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"multiplication"
argument_list|,
name|table
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**    * Tests a table function that takes cursor input.    */
annotation|@
name|Ignore
argument_list|(
literal|"CannotPlanException: Node [rel#18:Subset#4.ENUMERABLE.[]] "
operator|+
literal|"could not be implemented"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTableFunctionCursorInputs
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
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
literal|"jdbc:calcite:"
argument_list|)
init|)
block|{
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|GENERATE_STRINGS_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|table
argument_list|)
expr_stmt|;
specifier|final
name|TableFunction
name|add
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|PROCESS_CURSOR_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"process"
argument_list|,
name|add
argument_list|)
expr_stmt|;
specifier|final
name|PreparedStatement
name|ps
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"process\"(2,\n"
operator|+
literal|"cursor(select * from table(\"s\".\"GenerateStrings\"(?)))\n"
operator|+
literal|")) as t(u)\n"
operator|+
literal|"where u> 3"
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|ps
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
comment|// GenerateStrings returns 0..4, then 2 is added (process function),
comment|// thus 2..6, finally where u> 3 leaves just 4..6
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
literal|"u=4\n"
operator|+
literal|"u=5\n"
operator|+
literal|"u=6\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests a table function that takes multiple cursor inputs.    */
annotation|@
name|Ignore
argument_list|(
literal|"CannotPlanException: Node [rel#24:Subset#6.ENUMERABLE.[]] "
operator|+
literal|"could not be implemented"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testTableFunctionCursorsInputs
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
try|try
init|(
name|Connection
name|connection
init|=
name|getConnectionWithMultiplyFunction
argument_list|()
init|)
block|{
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
name|getSubSchema
argument_list|(
literal|"s"
argument_list|)
decl_stmt|;
specifier|final
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|GENERATE_STRINGS_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|table
argument_list|)
expr_stmt|;
specifier|final
name|TableFunction
name|add
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|PROCESS_CURSORS_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"process"
argument_list|,
name|add
argument_list|)
expr_stmt|;
specifier|final
name|PreparedStatement
name|ps
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"process\"(2,\n"
operator|+
literal|"cursor(select * from table(\"s\".\"multiplication\"(5,5,0))),\n"
operator|+
literal|"cursor(select * from table(\"s\".\"GenerateStrings\"(?)))\n"
operator|+
literal|")) as t(u)\n"
operator|+
literal|"where u> 3"
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|ResultSet
name|resultSet
init|=
name|ps
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
comment|// GenerateStrings produce 0..4
comment|// multiplication produce 1..5
comment|// process sums and adds 2
comment|// sum is 2 + 1..9 == 3..9
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
literal|"u=4\n"
operator|+
literal|"u=5\n"
operator|+
literal|"u=6\n"
operator|+
literal|"u=7\n"
operator|+
literal|"u=8\n"
operator|+
literal|"u=9\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(2, 3, 100))\n"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"row_name=row 0; c1=101; c2=102"
argument_list|,
literal|"row_name=row 1; c1=102; c2=104"
argument_list|,
literal|"row_name=row 2; c1=103; c2=106"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction2
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select c1\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(2, 3, 100))\n"
operator|+
literal|"where c1 + 2< c2"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Column 'C1' not found in any table; did you mean 'c1'?"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction3
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select \"c1\"\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(2, 3, 100))\n"
operator|+
literal|"where \"c1\" + 2< \"c2\""
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"c1=103"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction4
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"multiplication\"('2', 3, 100))\n"
operator|+
literal|"where c1 + 2< c2"
decl_stmt|;
specifier|final
name|String
name|e
init|=
literal|"No match found for function signature "
operator|+
literal|"multiplication(<CHARACTER>,<NUMERIC>,<NUMERIC>)"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|throws_
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction5
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"multiplication\"(3, 100))\n"
operator|+
literal|"where c1 + 2< c2"
decl_stmt|;
specifier|final
name|String
name|e
init|=
literal|"No match found for function signature "
operator|+
literal|"multiplication(<NUMERIC>,<NUMERIC>)"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|throws_
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction6
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"fibonacci\"())"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|numbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
operator|&&
name|numbers
operator|.
name|size
argument_list|()
operator|<
literal|13
condition|)
block|{
name|numbers
operator|.
name|add
argument_list|(
name|r
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|numbers
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233]"
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
name|testUserDefinedTableFunction7
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"fibonacci2\"(20))\n"
operator|+
literal|"where n> 7"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"N=13"
argument_list|,
literal|"N=8"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedTableFunction8
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select count(*) as c\n"
operator|+
literal|"from table(\"s\".\"fibonacci2\"(20))"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=7"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCrossApply
parameter_list|()
block|{
specifier|final
name|String
name|q1
init|=
literal|"select *\n"
operator|+
literal|"from (values 2, 5) as t (c)\n"
operator|+
literal|"cross apply table(\"s\".\"fibonacci2\"(c))"
decl_stmt|;
specifier|final
name|String
name|q2
init|=
literal|"select *\n"
operator|+
literal|"from (values 2, 5) as t (c)\n"
operator|+
literal|"cross apply table(\"s\".\"fibonacci2\"(t.c))"
decl_stmt|;
for|for
control|(
name|String
name|q
range|:
operator|new
name|String
index|[]
block|{
name|q1
block|,
name|q2
block|}
control|)
block|{
name|with
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=2; N=1"
argument_list|,
literal|"C=2; N=1"
argument_list|,
literal|"C=2; N=2"
argument_list|,
literal|"C=5; N=1"
argument_list|,
literal|"C=5; N=1"
argument_list|,
literal|"C=5; N=2"
argument_list|,
literal|"C=5; N=3"
argument_list|,
literal|"C=5; N=5"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2382">[CALCITE-2382]    * Sub-query lateral joined to table function</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testInlineViewLateralTableFunction
parameter_list|()
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
literal|"jdbc:calcite:"
argument_list|)
init|)
block|{
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
name|TableFunction
name|table
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|GENERATE_STRINGS_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"GenerateStrings"
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|Table
name|tbl
init|=
operator|new
name|ScannableTableTest
operator|.
name|SimpleTable
argument_list|()
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"t"
argument_list|,
name|tbl
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select 5 as f0 from \"s\".\"t\") \"a\",\n"
operator|+
literal|"  lateral table(\"s\".\"GenerateStrings\"(f0)) as t(n, c)\n"
operator|+
literal|"where char_length(c)> 3"
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
literal|"F0=5; N=4; C=abcd\n"
operator|+
literal|"F0=5; N=4; C=abcd\n"
operator|+
literal|"F0=5; N=4; C=abcd\n"
operator|+
literal|"F0=5; N=4; C=abcd\n"
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
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionTest.java
end_comment

end_unit

