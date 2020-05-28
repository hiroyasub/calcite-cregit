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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represents an expression that has a ternary operator.  */
end_comment

begin_class
specifier|public
class|class
name|TernaryExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Expression
name|expression0
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|expression1
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|expression2
decl_stmt|;
name|TernaryExpression
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Type
name|type
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|,
name|Expression
name|expression2
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|expression0
operator|!=
literal|null
operator|:
literal|"expression0 should not be null"
assert|;
assert|assert
name|expression1
operator|!=
literal|null
operator|:
literal|"expression1 should not be null"
assert|;
assert|assert
name|expression2
operator|!=
literal|null
operator|:
literal|"expression2 should not be null"
assert|;
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
name|this
operator|.
name|expression2
operator|=
name|expression2
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
name|Expression
name|expression0
init|=
name|this
operator|.
name|expression0
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|Expression
name|expression1
init|=
name|this
operator|.
name|expression1
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|Expression
name|expression2
init|=
name|this
operator|.
name|expression2
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
name|expression0
argument_list|,
name|expression1
argument_list|,
name|expression2
argument_list|)
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|writer
operator|.
name|requireParentheses
argument_list|(
name|this
argument_list|,
name|lprec
argument_list|,
name|rprec
argument_list|)
condition|)
block|{
return|return;
block|}
name|expression0
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
name|lprec
argument_list|,
name|nodeType
operator|.
name|lprec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|nodeType
operator|.
name|op
argument_list|)
expr_stmt|;
name|expression1
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
name|nodeType
operator|.
name|rprec
argument_list|,
name|nodeType
operator|.
name|lprec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|nodeType
operator|.
name|op2
argument_list|)
expr_stmt|;
name|expression2
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
name|nodeType
operator|.
name|rprec
argument_list|,
name|rprec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
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
name|TernaryExpression
name|that
init|=
operator|(
name|TernaryExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|expression0
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expression0
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
name|expression1
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expression1
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
name|expression2
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expression2
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|,
name|expression2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

