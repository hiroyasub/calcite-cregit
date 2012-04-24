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
name|expressions
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
name|function
operator|.
name|DynamicFunction
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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Expression compiler.  */
end_comment

begin_class
class|class
name|ExpressionCompiler
implements|implements
name|ExpressionVisitor
block|{
specifier|final
name|DynamicFunction
name|function
init|=
literal|null
decl_stmt|;
comment|/**      * Returns an invokable function that is the result of compilation.      *      * @return Invokable function      */
specifier|public
name|DynamicFunction
name|function
parameter_list|()
block|{
return|return
name|function
return|;
block|}
specifier|public
parameter_list|<
name|T
extends|extends
name|Function
argument_list|<
name|?
argument_list|>
parameter_list|>
name|void
name|visitLambda
parameter_list|(
name|FunctionExpression
argument_list|<
name|T
argument_list|>
name|expression
parameter_list|)
block|{
block|}
block|}
end_class

begin_comment
comment|// End ExpressionCompiler.java
end_comment

end_unit

