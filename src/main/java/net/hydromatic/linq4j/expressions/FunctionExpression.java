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

begin_comment
comment|/**  * Represents a strongly typed lambda expression as a data structure in the form  * of an expression tree. This class cannot be inherited.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FunctionExpression
parameter_list|<
name|T
parameter_list|>
extends|extends
name|LambdaExpression
block|{
specifier|public
name|FunctionExpression
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FunctionExpression.java
end_comment

end_unit

