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
name|example
operator|.
name|maze
operator|.
name|MazeTable
import|;
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
name|tree
operator|.
name|Types
import|;
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
comment|/**  * Unit tests for example user-defined functions.  */
end_comment

begin_class
class|class
name|ExampleFunctionTest
block|{
specifier|public
specifier|static
specifier|final
name|Method
name|MAZE_METHOD
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|MazeTable
operator|.
name|class
argument_list|,
literal|"generate"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Method
name|SOLVE_METHOD
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|MazeTable
operator|.
name|class
argument_list|,
literal|"solve"
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Unit test for {@link MazeTable}. */
annotation|@
name|Test
name|void
name|testMazeTableFunction
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
specifier|final
name|String
name|maze
init|=
literal|""
operator|+
literal|"+--+--+--+--+--+\n"
operator|+
literal|"|        |     |\n"
operator|+
literal|"+--+  +--+--+  +\n"
operator|+
literal|"|     |  |     |\n"
operator|+
literal|"+  +--+  +--+  +\n"
operator|+
literal|"|              |\n"
operator|+
literal|"+--+--+--+--+--+\n"
decl_stmt|;
name|checkMazeTableFunction
argument_list|(
literal|false
argument_list|,
name|maze
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link MazeTable}. */
annotation|@
name|Test
name|void
name|testMazeTableFunctionWithSolution
parameter_list|()
throws|throws
name|SQLException
throws|,
name|ClassNotFoundException
block|{
specifier|final
name|String
name|maze
init|=
literal|""
operator|+
literal|"+--+--+--+--+--+\n"
operator|+
literal|"|*  *    |     |\n"
operator|+
literal|"+--+  +--+--+  +\n"
operator|+
literal|"|*  * |  |     |\n"
operator|+
literal|"+  +--+  +--+  +\n"
operator|+
literal|"|*  *  *  *  * |\n"
operator|+
literal|"+--+--+--+--+--+\n"
decl_stmt|;
name|checkMazeTableFunction
argument_list|(
literal|true
argument_list|,
name|maze
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkMazeTableFunction
parameter_list|(
name|Boolean
name|solution
parameter_list|,
name|String
name|maze
parameter_list|)
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
name|TableFunction
name|table2
init|=
name|TableFunctionImpl
operator|.
name|create
argument_list|(
name|SOLVE_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
literal|"Solve"
argument_list|,
name|table2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
decl_stmt|;
if|if
condition|(
name|solution
condition|)
block|{
name|sql
operator|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Solve\"(5, 3, 1)) as t(s)"
expr_stmt|;
block|}
else|else
block|{
name|sql
operator|=
literal|"select *\n"
operator|+
literal|"from table(\"s\".\"Maze\"(5, 3, 1)) as t(s)"
expr_stmt|;
block|}
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
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getString
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
name|assertThat
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|maze
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

