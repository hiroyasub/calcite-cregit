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
name|Modifier
import|;
end_import

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
name|AbstractList
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
name|BlockStatement
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
name|BlockStatement
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
annotation|@
name|Override
specifier|public
name|MemberDeclaration
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
comment|// do not visit parameters
specifier|final
name|BlockStatement
name|body
init|=
name|this
operator|.
name|body
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
decl_stmt|;
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|parameters
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
block|{
name|String
name|modifiers
init|=
name|Modifier
operator|.
name|toString
argument_list|(
name|modifier
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|modifiers
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|modifiers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
name|resultType
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|list
argument_list|(
literal|"("
argument_list|,
literal|", "
argument_list|,
literal|")"
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|ParameterExpression
name|parameter
init|=
name|parameters
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|String
name|modifiers
init|=
name|Modifier
operator|.
name|toString
argument_list|(
name|parameter
operator|.
name|modifier
argument_list|)
decl_stmt|;
return|return
name|modifiers
operator|+
operator|(
name|modifiers
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|" "
operator|)
operator|+
name|Types
operator|.
name|className
argument_list|(
name|parameter
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" "
operator|+
name|parameter
operator|.
name|name
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|parameters
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MethodDeclaration.java
end_comment

end_unit

