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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Declaration of a method.  */
end_comment

begin_class
specifier|public
class|class
name|MethodDeclaration
extends|extends
name|MemberDeclaration
block|{
specifier|public
specifier|final
name|int
name|modifier
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|Type
name|resultType
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|body
decl_stmt|;
specifier|public
name|MethodDeclaration
parameter_list|(
name|int
name|modifier
parameter_list|,
name|String
name|name
parameter_list|,
name|Type
name|resultType
parameter_list|,
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
parameter_list|,
name|Expression
name|body
parameter_list|)
block|{
name|this
operator|.
name|modifier
operator|=
name|modifier
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|resultType
operator|=
name|resultType
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MethodDeclaration.java
end_comment

end_unit

