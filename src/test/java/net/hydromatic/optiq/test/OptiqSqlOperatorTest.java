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
name|jdbc
operator|.
name|OptiqConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|test
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|SqlValidatorTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Embodiment of {@link org.eigenbase.sql.test.SqlOperatorBaseTest}  * that generates SQL statements and executes them using Optiq.  */
end_comment

begin_class
specifier|public
class|class
name|OptiqSqlOperatorTest
extends|extends
name|SqlOperatorBaseTest
block|{
specifier|private
specifier|static
name|SqlTester
name|STATIC_TESTER
decl_stmt|;
specifier|public
name|OptiqSqlOperatorTest
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|STATIC_TESTER
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|OptiqConnection
name|connection
init|=
name|JdbcTest
operator|.
name|getConnection
argument_list|(
literal|"hr"
argument_list|)
decl_stmt|;
name|STATIC_TESTER
operator|=
operator|new
name|SqlValidatorTestCase
operator|.
name|TesterImpl
argument_list|(
name|SqlConformance
operator|.
name|Default
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|check
parameter_list|(
name|String
name|query
parameter_list|,
name|TypeChecker
name|typeChecker
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|super
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|typeChecker
argument_list|,
name|resultChecker
argument_list|)
expr_stmt|;
name|Statement
name|statement
init|=
literal|null
decl_stmt|;
try|try
block|{
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|resultChecker
operator|.
name|checkResult
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
finally|finally
block|{
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
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
comment|// ignore
block|}
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SqlTester
name|getTester
parameter_list|()
block|{
return|return
name|STATIC_TESTER
return|;
block|}
block|}
end_class

begin_comment
comment|// End OptiqSqlOperatorTest.java
end_comment

end_unit

