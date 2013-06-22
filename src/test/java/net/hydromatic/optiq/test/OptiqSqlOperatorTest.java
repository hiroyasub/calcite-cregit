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
name|SqlOperatorBaseTest
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
name|SqlTester
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
name|getHrTester
parameter_list|()
block|{
try|try
block|{
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
return|return
name|tester
argument_list|(
name|connection
argument_list|)
return|;
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
block|}
specifier|public
name|OptiqSqlOperatorTest
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|,
name|getHrTester
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End OptiqSqlOperatorTest.java
end_comment

end_unit

