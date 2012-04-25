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
comment|/**  * Represents an expression that has a binary operator.  */
end_comment

begin_class
specifier|public
class|class
name|BinaryExpression
extends|extends
name|Expression
block|{
specifier|final
name|Expression
name|expression0
decl_stmt|;
specifier|final
name|Expression
name|expression1
decl_stmt|;
name|BinaryExpression
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Class
name|type
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression0
operator|=
name|expression0
expr_stmt|;
name|this
operator|.
name|expression1
operator|=
name|expression1
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End BinaryExpression.java
end_comment

end_unit

