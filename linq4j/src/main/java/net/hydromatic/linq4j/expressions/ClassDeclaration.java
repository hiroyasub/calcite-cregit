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
name|List
import|;
end_import

begin_comment
comment|/**  * Declaration of a class.  */
end_comment

begin_class
specifier|public
class|class
name|ClassDeclaration
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
name|classClass
init|=
literal|"class"
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
decl_stmt|;
specifier|public
specifier|final
name|Type
name|extended
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|implemented
decl_stmt|;
specifier|public
name|ClassDeclaration
parameter_list|(
name|int
name|modifier
parameter_list|,
name|String
name|name
parameter_list|,
name|Type
name|extended
parameter_list|,
name|List
argument_list|<
name|Type
argument_list|>
name|implemented
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
assert|assert
name|name
operator|!=
literal|null
operator|:
literal|"name should not be null"
assert|;
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
name|memberDeclarations
operator|=
name|memberDeclarations
expr_stmt|;
name|this
operator|.
name|extended
operator|=
name|extended
expr_stmt|;
name|this
operator|.
name|implemented
operator|=
name|implemented
expr_stmt|;
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
name|classClass
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
expr_stmt|;
if|if
condition|(
name|extended
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|" extends "
argument_list|)
operator|.
name|append
argument_list|(
name|extended
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|implemented
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|.
name|list
argument_list|(
literal|" implements "
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|,
name|implemented
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|list
argument_list|(
literal|" {\n"
argument_list|,
literal|""
argument_list|,
literal|"}"
argument_list|,
name|memberDeclarations
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ClassDeclaration
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
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|members1
init|=
name|Expressions
operator|.
name|acceptMemberDeclarations
argument_list|(
name|memberDeclarations
argument_list|,
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
name|members1
argument_list|)
return|;
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
name|ClassDeclaration
name|that
init|=
operator|(
name|ClassDeclaration
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
operator|!
name|classClass
operator|.
name|equals
argument_list|(
name|that
operator|.
name|classClass
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|extended
operator|!=
literal|null
condition|?
operator|!
name|extended
operator|.
name|equals
argument_list|(
name|that
operator|.
name|extended
argument_list|)
else|:
name|that
operator|.
name|extended
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
name|implemented
operator|!=
literal|null
condition|?
operator|!
name|implemented
operator|.
name|equals
argument_list|(
name|that
operator|.
name|implemented
argument_list|)
else|:
name|that
operator|.
name|implemented
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
name|memberDeclarations
operator|!=
literal|null
condition|?
operator|!
name|memberDeclarations
operator|.
name|equals
argument_list|(
name|that
operator|.
name|memberDeclarations
argument_list|)
else|:
name|that
operator|.
name|memberDeclarations
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
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
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
name|classClass
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
name|name
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
name|memberDeclarations
operator|!=
literal|null
condition|?
name|memberDeclarations
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|extended
operator|!=
literal|null
condition|?
name|extended
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|implemented
operator|!=
literal|null
condition|?
name|implemented
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
comment|// End ClassDeclaration.java
end_comment

end_unit

