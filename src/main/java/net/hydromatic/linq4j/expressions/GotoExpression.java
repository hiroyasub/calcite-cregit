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

begin_comment
comment|/**  * Represents an unconditional jump. This includes return statements, break and  * continue statements, and other jumps.  */
end_comment

begin_class
specifier|public
class|class
name|GotoExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|GotoExpressionKind
name|kind
decl_stmt|;
specifier|private
specifier|final
name|LabelTarget
name|labelTarget
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
name|GotoExpression
parameter_list|(
name|GotoExpressionKind
name|kind
parameter_list|,
name|LabelTarget
name|labelTarget
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Goto
argument_list|,
name|expression
operator|==
literal|null
condition|?
name|Void
operator|.
name|TYPE
else|:
name|expression
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
name|this
operator|.
name|labelTarget
operator|=
name|labelTarget
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|Break
case|:
case|case
name|Continue
case|:
assert|assert
name|expression
operator|==
literal|null
assert|;
break|break;
case|case
name|Goto
case|:
assert|assert
name|expression
operator|==
literal|null
assert|;
assert|assert
name|labelTarget
operator|!=
literal|null
assert|;
break|break;
case|case
name|Return
case|:
assert|assert
name|labelTarget
operator|==
literal|null
assert|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected: "
operator|+
name|kind
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
name|writer
operator|.
name|append
argument_list|(
name|kind
operator|.
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|labelTarget
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|labelTarget
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writer
operator|.
name|begin
argument_list|()
expr_stmt|;
name|expression
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End GotoExpression.java
end_comment

end_unit

