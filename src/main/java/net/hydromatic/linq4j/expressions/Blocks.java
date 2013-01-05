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
comment|/**  *<p>Helper methods concerning {@link BlockExpression}s.</p>  *  * @see BlockBuilder  *  * @author jhyde  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Blocks
block|{
specifier|private
name|Blocks
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"no blocks for you!"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|BlockExpression
name|toFunctionBlock
parameter_list|(
name|Node
name|body
parameter_list|,
name|boolean
name|function
parameter_list|)
block|{
if|if
condition|(
name|body
operator|instanceof
name|BlockExpression
condition|)
block|{
return|return
operator|(
name|BlockExpression
operator|)
name|body
return|;
block|}
name|Statement
name|statement
decl_stmt|;
if|if
condition|(
name|body
operator|instanceof
name|Statement
condition|)
block|{
name|statement
operator|=
operator|(
name|Statement
operator|)
name|body
expr_stmt|;
block|}
if|else if
condition|(
name|body
operator|instanceof
name|Expression
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|Expression
operator|)
name|body
operator|)
operator|.
name|getType
argument_list|()
operator|==
name|Void
operator|.
name|TYPE
operator|&&
name|function
condition|)
block|{
name|statement
operator|=
name|Expressions
operator|.
name|statement
argument_list|(
operator|(
name|Expression
operator|)
name|body
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statement
operator|=
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
operator|(
name|Expression
operator|)
name|body
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"block cannot contain node that is neither statement nor "
operator|+
literal|"expression: "
operator|+
name|body
argument_list|)
throw|;
block|}
return|return
name|Expressions
operator|.
name|block
argument_list|(
name|statement
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BlockExpression
name|toFunctionBlock
parameter_list|(
name|Node
name|body
parameter_list|)
block|{
return|return
name|toFunctionBlock
argument_list|(
name|body
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BlockExpression
name|toBlock
parameter_list|(
name|Node
name|body
parameter_list|)
block|{
return|return
name|toFunctionBlock
argument_list|(
name|body
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Prepends a statement to a block. */
specifier|public
specifier|static
name|BlockExpression
name|create
parameter_list|(
name|Statement
name|statement
parameter_list|,
name|BlockExpression
name|block
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|list
argument_list|(
name|statement
argument_list|)
operator|.
name|appendAll
argument_list|(
name|block
operator|.
name|statements
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts a simple "{ return expr; }" block into "expr"; otherwise      * throws. */
specifier|public
specifier|static
name|Expression
name|simple
parameter_list|(
name|BlockExpression
name|block
parameter_list|)
block|{
if|if
condition|(
name|block
operator|.
name|statements
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Statement
name|statement
init|=
name|block
operator|.
name|statements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|statement
operator|instanceof
name|GotoExpression
condition|)
block|{
return|return
operator|(
operator|(
name|GotoExpression
operator|)
name|statement
operator|)
operator|.
name|expression
return|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"not a simple block: "
operator|+
name|block
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|// End Blocks.java
end_comment

end_unit

