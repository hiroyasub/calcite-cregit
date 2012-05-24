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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Expression that declares and optionally initializes a variable.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|DeclarationExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|int
name|modifiers
decl_stmt|;
specifier|public
specifier|final
name|ParameterExpression
name|parameter
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|initializer
decl_stmt|;
specifier|public
name|DeclarationExpression
parameter_list|(
name|int
name|modifiers
parameter_list|,
name|ParameterExpression
name|parameter
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Declaration
argument_list|,
name|Void
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|modifiers
operator|=
name|modifiers
expr_stmt|;
name|this
operator|.
name|parameter
operator|=
name|parameter
expr_stmt|;
name|this
operator|.
name|initializer
operator|=
name|initializer
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DeclarationExpression.java
end_comment

end_unit

