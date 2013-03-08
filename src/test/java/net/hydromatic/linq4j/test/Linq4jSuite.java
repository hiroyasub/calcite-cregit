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
name|linq4j
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
name|linq4j
operator|.
name|expressions
operator|.
name|TypeTest
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * Suite of all Linq4j tests.  */
end_comment

begin_class
specifier|public
class|class
name|Linq4jSuite
block|{
comment|/** Returns a {@link TestSuite junit suite} of all Linq4j tests. */
specifier|public
specifier|static
name|TestSuite
name|suite
parameter_list|()
block|{
name|TestSuite
name|testSuite
init|=
operator|new
name|TestSuite
argument_list|()
decl_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|PrimitiveTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|Linq4jTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|ExpressionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|testSuite
operator|.
name|addTestSuite
argument_list|(
name|TypeTest
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|testSuite
return|;
block|}
block|}
end_class

begin_comment
comment|// End Linq4jSuite.java
end_comment

end_unit

