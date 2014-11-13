begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Declaration of a field.  */
end_comment

begin_class
specifier|public
class|class
name|FieldDeclaration
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
name|ParameterExpression
name|parameter
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|initializer
decl_stmt|;
specifier|public
name|FieldDeclaration
parameter_list|(
name|int
name|modifier
parameter_list|,
name|ParameterExpression
name|parameter
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
assert|assert
name|parameter
operator|!=
literal|null
operator|:
literal|"parameter should not be null"
assert|;
name|this
operator|.
name|modifier
operator|=
name|modifier
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
name|visitor
operator|=
name|visitor
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// do not visit parameter - visit may not return a ParameterExpression
specifier|final
name|Expression
name|initializer
init|=
name|this
operator|.
name|initializer
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|initializer
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
name|initializer
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
name|parameter
operator|.
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|parameter
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|initializer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
operator|.
name|append
argument_list|(
name|initializer
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FieldDeclaration
name|that
init|=
operator|(
name|FieldDeclaration
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|modifier
operator|!=
name|that
operator|.
name|modifier
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|initializer
operator|!=
literal|null
condition|?
operator|!
name|initializer
operator|.
name|equals
argument_list|(
name|that
operator|.
name|initializer
argument_list|)
else|:
name|that
operator|.
name|initializer
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|parameter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parameter
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|modifier
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|parameter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|initializer
operator|!=
literal|null
condition|?
name|initializer
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End FieldDeclaration.java
end_comment

end_unit

