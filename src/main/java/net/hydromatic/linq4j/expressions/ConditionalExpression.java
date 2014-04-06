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
comment|/**  * Represents an expression that has a conditional operator.  *  *<p>With an odd number of expressions  * {c0, e0, c1, e1, ..., c<sub>n-1</sub>, e<sub>n-1</sub>, e<sub>n</sub>}  * represents "if (c0) e0 else if (c1) e1 ... else e<sub>n</sub>";  * with an even number of expressions  * {c0, e0, c1, e1, ..., c<sub>n-1</sub>, e<sub>n-1</sub>}  * represents  * "if (c0) e0 else if (c1) e1 ... else if (c<sub>n-1</sub>) e<sub>n-1</sub>".  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|ConditionalExpression
extends|extends
name|AbstractNode
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|expressionList
decl_stmt|;
specifier|public
name|ConditionalExpression
parameter_list|(
name|List
argument_list|<
name|Node
argument_list|>
name|expressionList
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Conditional
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|expressionList
operator|!=
literal|null
operator|:
literal|"expressionList should not be null"
assert|;
name|this
operator|.
name|expressionList
operator|=
name|expressionList
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expressionList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|i
operator|>
literal|0
condition|?
literal|" else if ("
else|:
literal|"if ("
argument_list|)
operator|.
name|append
argument_list|(
name|expressionList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|expressionList
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expressionList
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|" else "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|expressionList
operator|.
name|get
argument_list|(
name|expressionList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
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
name|ConditionalExpression
name|that
init|=
operator|(
name|ConditionalExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|expressionList
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expressionList
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|expressionList
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConditionalExpression.java
end_comment

end_unit

