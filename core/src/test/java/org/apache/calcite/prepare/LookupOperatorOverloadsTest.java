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
name|prepare
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
name|apache
operator|.
name|calcite
operator|.
name|server
operator|.
name|CalciteServerStatement
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
name|SqlFunctionCategory
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
name|SqlIdentifier
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
name|SqlSyntax
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
name|validate
operator|.
name|SqlUserDefinedTableFunction
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
name|Lists
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
name|List
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|MATCH_RECOGNIZE
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_CONSTRUCTOR
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_PROCEDURE
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_SPECIFIC_FUNCTION
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
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
name|sql
operator|.
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_SPECIFIC_FUNCTION
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

begin_comment
comment|/**  * Test for lookupOperatorOverloads() in {@link CalciteCatalogReader}.  */
end_comment

begin_class
specifier|public
class|class
name|LookupOperatorOverloadsTest
block|{
specifier|private
name|void
name|checkFunctionType
parameter_list|(
name|int
name|size
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|)
block|{
name|assertThat
argument_list|(
name|size
argument_list|,
name|is
argument_list|(
name|operatorList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlOperator
name|op
range|:
name|operatorList
control|)
block|{
name|assertThat
argument_list|(
name|op
argument_list|,
name|instanceOf
argument_list|(
name|SqlUserDefinedTableFunction
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|name
argument_list|,
name|is
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|check
parameter_list|(
name|List
argument_list|<
name|SqlFunctionCategory
argument_list|>
name|actuals
parameter_list|,
name|SqlFunctionCategory
modifier|...
name|expecteds
parameter_list|)
block|{
name|assertThat
argument_list|(
name|actuals
argument_list|,
name|is
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expecteds
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsUserDefined
parameter_list|()
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|SqlFunctionCategory
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlFunctionCategory
name|c
range|:
name|SqlFunctionCategory
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|isUserDefined
argument_list|()
condition|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|check
argument_list|(
name|cats
argument_list|,
name|USER_DEFINED_FUNCTION
argument_list|,
name|USER_DEFINED_PROCEDURE
argument_list|,
name|USER_DEFINED_CONSTRUCTOR
argument_list|,
name|USER_DEFINED_SPECIFIC_FUNCTION
argument_list|,
name|USER_DEFINED_TABLE_FUNCTION
argument_list|,
name|USER_DEFINED_TABLE_SPECIFIC_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsTableFunction
parameter_list|()
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|SqlFunctionCategory
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlFunctionCategory
name|c
range|:
name|SqlFunctionCategory
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|isTableFunction
argument_list|()
condition|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|check
argument_list|(
name|cats
argument_list|,
name|USER_DEFINED_TABLE_FUNCTION
argument_list|,
name|USER_DEFINED_TABLE_SPECIFIC_FUNCTION
argument_list|,
name|MATCH_RECOGNIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSpecific
parameter_list|()
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|SqlFunctionCategory
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlFunctionCategory
name|c
range|:
name|SqlFunctionCategory
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|isSpecific
argument_list|()
condition|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|check
argument_list|(
name|cats
argument_list|,
name|USER_DEFINED_SPECIFIC_FUNCTION
argument_list|,
name|USER_DEFINED_TABLE_SPECIFIC_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsUserDefinedNotSpecificFunction
parameter_list|()
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|SqlFunctionCategory
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlFunctionCategory
name|sqlFunctionCategory
range|:
name|SqlFunctionCategory
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|sqlFunctionCategory
operator|.
name|isUserDefinedNotSpecificFunction
argument_list|()
condition|)
block|{
name|cats
operator|.
name|add
argument_list|(
name|sqlFunctionCategory
argument_list|)
expr_stmt|;
block|}
block|}
name|check
argument_list|(
name|cats
argument_list|,
name|USER_DEFINED_FUNCTION
argument_list|,
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|schemaName
init|=
literal|"MySchema"
decl_stmt|;
specifier|final
name|String
name|funcName
init|=
literal|"MyFUNC"
decl_stmt|;
specifier|final
name|String
name|anotherName
init|=
literal|"AnotherFunc"
decl_stmt|;
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
name|schemaName
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
name|funcName
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|anotherName
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
name|Smalls
operator|.
name|MAZE3_METHOD
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|funcName
argument_list|,
name|table2
argument_list|)
expr_stmt|;
specifier|final
name|CalciteServerStatement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|unwrap
argument_list|(
name|CalciteServerStatement
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|CalcitePrepare
operator|.
name|Context
name|prepareContext
init|=
name|statement
operator|.
name|createPrepareContext
argument_list|()
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
name|prepareContext
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|CalciteCatalogReader
name|reader
init|=
operator|new
name|CalciteCatalogReader
argument_list|(
name|prepareContext
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
name|typeFactory
argument_list|,
name|prepareContext
operator|.
name|config
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|SqlIdentifier
name|myFuncIdentifier
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|schemaName
argument_list|,
name|funcName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|reader
operator|.
name|lookupOperatorOverloads
argument_list|(
name|myFuncIdentifier
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
name|checkFunctionType
argument_list|(
literal|2
argument_list|,
name|funcName
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
name|operatorList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reader
operator|.
name|lookupOperatorOverloads
argument_list|(
name|myFuncIdentifier
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
name|checkFunctionType
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
name|operatorList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|SqlIdentifier
name|anotherFuncIdentifier
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|schemaName
argument_list|,
name|anotherName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|reader
operator|.
name|lookupOperatorOverloads
argument_list|(
name|anotherFuncIdentifier
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
name|checkFunctionType
argument_list|(
literal|1
argument_list|,
name|anotherName
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End LookupOperatorOverloadsTest.java
end_comment

end_unit

