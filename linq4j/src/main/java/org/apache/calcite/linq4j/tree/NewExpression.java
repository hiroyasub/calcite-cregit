begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represents a constructor call.  *  *<p>If {@link #memberDeclarations} is not null (even if empty) represents  * an anonymous class.</p>  */
end_comment

begin_class
specifier|public
class|class
name|NewExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
decl_stmt|;
comment|/**    * Cache the hash code for the expression    */
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|NewExpression
parameter_list|(
name|Type
name|type
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|New
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|arguments
expr_stmt|;
name|this
operator|.
name|memberDeclarations
operator|=
name|memberDeclarations
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|accept
parameter_list|(
name|Shuttle
name|shuttle
parameter_list|)
block|{
name|shuttle
operator|=
name|shuttle
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
init|=
name|Expressions
operator|.
name|acceptExpressions
argument_list|(
name|this
operator|.
name|arguments
argument_list|,
name|shuttle
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
init|=
name|Expressions
operator|.
name|acceptMemberDeclarations
argument_list|(
name|this
operator|.
name|memberDeclarations
argument_list|,
name|shuttle
argument_list|)
decl_stmt|;
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|Visitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"new "
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|list
argument_list|(
literal|"(\n"
argument_list|,
literal|",\n"
argument_list|,
literal|")"
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
if|if
condition|(
name|memberDeclarations
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|list
argument_list|(
literal|"{\n"
argument_list|,
literal|""
argument_list|,
literal|"}"
argument_list|,
name|memberDeclarations
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NewExpression
name|that
init|=
operator|(
name|NewExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|arguments
operator|!=
literal|null
condition|?
operator|!
name|arguments
operator|.
name|equals
argument_list|(
name|that
operator|.
name|arguments
argument_list|)
else|:
name|that
operator|.
name|arguments
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
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
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
name|hash
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|super
operator|.
name|type
argument_list|,
name|type
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
literal|1
expr_stmt|;
block|}
name|hash
operator|=
name|result
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

