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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
comment|/**  * Declaration of a constructor.  */
end_comment

begin_class
specifier|public
class|class
name|ConstructorDeclaration
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
comment|/** Cached hash code for the expression. */
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|ConstructorDeclaration
parameter_list|(
name|int
name|modifier
parameter_list|,
name|Type
name|declaredAgainst
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
assert|assert
name|parameters
operator|!=
literal|null
operator|:
literal|"parameters should not be null"
assert|;
assert|assert
name|body
operator|!=
literal|null
operator|:
literal|"body should not be null"
assert|;
assert|assert
name|declaredAgainst
operator|!=
literal|null
operator|:
literal|"declaredAgainst should not be null"
assert|;
name|this
operator|.
name|modifier
operator|=
name|modifier
expr_stmt|;
name|this
operator|.
name|resultType
operator|=
name|declaredAgainst
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
name|body
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
name|list
argument_list|(
literal|"("
argument_list|,
literal|", "
argument_list|,
literal|")"
argument_list|,
name|Lists
operator|.
name|transform
argument_list|(
name|parameters
argument_list|,
name|parameter
lambda|->
block|{
specifier|final
name|String
name|modifiers1
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
name|modifiers1
operator|+
operator|(
name|modifiers1
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
argument_list|)
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
name|ConstructorDeclaration
name|that
init|=
operator|(
name|ConstructorDeclaration
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
name|body
operator|.
name|equals
argument_list|(
name|that
operator|.
name|body
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|parameters
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parameters
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|resultType
operator|.
name|equals
argument_list|(
name|that
operator|.
name|resultType
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
name|modifier
argument_list|,
name|resultType
argument_list|,
name|parameters
argument_list|,
name|body
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

