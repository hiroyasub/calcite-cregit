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
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
comment|/**  * Test cases to check that necessary information from underlying exceptions  * is correctly propagated via {@link SQLException}s.  */
end_comment

begin_class
specifier|public
class|class
name|ExceptionMessageTest
block|{
specifier|private
name|Connection
name|conn
decl_stmt|;
comment|/**    * Simple reflective schema that provides valid and invalid entries.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
specifier|static
class|class
name|TestSchema
block|{
specifier|public
name|Entry
index|[]
name|entries
init|=
block|{
operator|new
name|Entry
argument_list|(
literal|1
argument_list|,
literal|"name1"
argument_list|)
block|,
operator|new
name|Entry
argument_list|(
literal|2
argument_list|,
literal|"name2"
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Iterable
argument_list|<
name|Entry
argument_list|>
name|badEntries
init|=
parameter_list|()
lambda|->
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't iterate over badEntries"
argument_list|)
throw|;
block|}
decl_stmt|;
block|}
comment|/**    * Entries made available in the reflective TestSchema.    */
specifier|public
specifier|static
class|class
name|Entry
block|{
specifier|public
name|int
name|id
decl_stmt|;
specifier|public
name|String
name|name
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
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
name|rootSchema
operator|.
name|add
argument_list|(
literal|"test"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|TestSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|calciteConnection
operator|.
name|setSchema
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conn
operator|=
name|calciteConnection
expr_stmt|;
block|}
specifier|private
name|void
name|runQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// We catch a possible exception on close so that we know we're not
comment|// masking the query exception with the close exception
name|fail
argument_list|(
literal|"Error on close"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidQuery
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Just ensure that we're actually dealing with a valid connection
comment|// to be sure that the results of the other tests can be trusted
name|runQuery
argument_list|(
literal|"select * from \"entries\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonSqlException
parameter_list|()
throws|throws
name|SQLException
block|{
try|try
block|{
name|runQuery
argument_list|(
literal|"select * from \"badEntries\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Query badEntries should result in an exception"
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
literal|"Error while executing SQL \"select * from \"badEntries\"\": "
operator|+
literal|"Can't iterate over badEntries"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSyntaxError
parameter_list|()
block|{
try|try
block|{
name|runQuery
argument_list|(
literal|"invalid sql"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Query should fail"
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
literal|"Error while executing SQL \"invalid sql\": parse failed: "
operator|+
literal|"Non-query expression encountered in illegal context"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSemanticError
parameter_list|()
block|{
try|try
block|{
name|runQuery
argument_list|(
literal|"select \"name\" - \"id\" from \"entries\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Query with semantic error should fail"
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
literal|"Cannot apply '-' to arguments"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonexistentTable
parameter_list|()
block|{
try|try
block|{
name|runQuery
argument_list|(
literal|"select name from \"nonexistentTable\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Query should fail"
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
literal|"Object 'nonexistentTable' not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ExceptionMessageTest.java
end_comment

end_unit

