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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|*
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

begin_comment
comment|/**  * Unit test for {@link net.hydromatic.linq4j.expressions.Expression}  * and subclasses.  */
end_comment

begin_class
specifier|public
class|class
name|ExpressionTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testLambda
parameter_list|()
block|{
comment|// A parameter for the lambda expression.
name|ParameterExpression
name|paramExpr
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Integer
operator|.
name|TYPE
argument_list|,
literal|"arg"
argument_list|)
decl_stmt|;
comment|// This expression represents a lambda expression
comment|// that adds 1 to the parameter value.
name|LambdaExpression
name|lambdaExpr
init|=
name|Expressions
operator|.
name|lambda
argument_list|(
name|Expressions
operator|.
name|add
argument_list|(
name|paramExpr
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|paramExpr
argument_list|)
argument_list|)
decl_stmt|;
comment|// Print out the expression.
name|String
name|s
init|=
name|lambdaExpr
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Compile and run the lambda expression.
comment|// The value of the parameter is 1.
name|int
name|n
init|=
operator|(
name|Integer
operator|)
name|lambdaExpr
operator|.
name|compile
argument_list|()
operator|.
name|dynamicInvoke
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// This code example produces the following output:
comment|//
comment|// arg => (arg +1)
comment|// 2
block|}
block|}
end_class

begin_comment
comment|// End ExpressionTest.java
end_comment

end_unit

