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
comment|/**  * Represents creating a new array and possibly initializing the elements of the  * new array.  */
end_comment

begin_class
specifier|public
class|class
name|NewArrayExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|int
name|dimension
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|bound
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
decl_stmt|;
comment|/**    * Cache the hash code for the expression    */
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|NewArrayExpression
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|dimension
parameter_list|,
name|Expression
name|bound
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|NewArrayInit
argument_list|,
name|Types
operator|.
name|arrayType
argument_list|(
name|type
argument_list|,
name|dimension
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|dimension
operator|=
name|dimension
expr_stmt|;
name|this
operator|.
name|bound
operator|=
name|bound
expr_stmt|;
name|this
operator|.
name|expressions
operator|=
name|expressions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
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
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
name|this
operator|.
name|expressions
operator|==
literal|null
condition|?
literal|null
else|:
name|Expressions
operator|.
name|acceptExpressions
argument_list|(
name|this
operator|.
name|expressions
argument_list|,
name|visitor
argument_list|)
decl_stmt|;
name|Expression
name|bound
init|=
name|Expressions
operator|.
name|accept
argument_list|(
name|this
operator|.
name|bound
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
name|dimension
argument_list|,
name|bound
argument_list|,
name|expressions
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
name|Types
operator|.
name|getComponentTypeN
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
operator|&&
name|bound
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|bound
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"[]"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|expressions
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|list
argument_list|(
literal|" {\n"
argument_list|,
literal|",\n"
argument_list|,
literal|"}"
argument_list|,
name|expressions
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
name|NewArrayExpression
name|that
init|=
operator|(
name|NewArrayExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|dimension
operator|!=
name|that
operator|.
name|dimension
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|bound
operator|!=
literal|null
condition|?
operator|!
name|bound
operator|.
name|equals
argument_list|(
name|that
operator|.
name|bound
argument_list|)
else|:
name|that
operator|.
name|bound
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
name|expressions
operator|!=
literal|null
condition|?
operator|!
name|expressions
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expressions
argument_list|)
else|:
name|that
operator|.
name|expressions
operator|!=
literal|null
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
name|type
argument_list|,
name|dimension
argument_list|,
name|bound
argument_list|,
name|expressions
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

begin_comment
comment|// End NewArrayExpression.java
end_comment

end_unit

