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
name|Field
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

begin_comment
comment|/**  * Represents accessing a field or property.  */
end_comment

begin_class
specifier|public
class|class
name|MemberExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|public
specifier|final
name|PseudoField
name|field
decl_stmt|;
specifier|public
name|MemberExpression
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Field
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|expression
argument_list|,
name|Types
operator|.
name|field
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemberExpression
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|PseudoField
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|MemberAccess
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|field
operator|!=
literal|null
operator|:
literal|"field should not be null"
assert|;
assert|assert
name|expression
operator|!=
literal|null
operator|||
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|:
literal|"must specify expression if field is not static"
assert|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
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
name|Expression
name|expression1
init|=
name|expression
operator|==
literal|null
condition|?
literal|null
else|:
name|expression
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
name|expression1
argument_list|)
return|;
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
specifier|final
name|Object
name|o
init|=
name|expression
operator|==
literal|null
condition|?
literal|null
else|:
name|expression
operator|.
name|evaluate
argument_list|(
name|evaluator
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|field
operator|.
name|get
argument_list|(
name|o
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"error while evaluating "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|expression
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
block|}
else|else
block|{
assert|assert
operator|(
name|field
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|!=
literal|0
assert|;
name|writer
operator|.
name|append
argument_list|(
name|field
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
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
name|MemberExpression
name|that
init|=
operator|(
name|MemberExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|expression
operator|!=
literal|null
condition|?
operator|!
name|expression
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expression
argument_list|)
else|:
name|that
operator|.
name|expression
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
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
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
operator|(
name|expression
operator|!=
literal|null
condition|?
name|expression
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
name|field
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
comment|// End MemberExpression.java
end_comment

end_unit

