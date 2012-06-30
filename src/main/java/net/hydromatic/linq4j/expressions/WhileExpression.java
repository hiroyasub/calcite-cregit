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
comment|/**  * Represents a "while" statement.  */
end_comment

begin_class
specifier|public
class|class
name|WhileExpression
extends|extends
name|Statement
block|{
specifier|public
specifier|final
name|Expression
name|condition
decl_stmt|;
specifier|public
specifier|final
name|Statement
name|body
decl_stmt|;
specifier|public
name|WhileExpression
parameter_list|(
name|Expression
name|condition
parameter_list|,
name|Statement
name|body
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|While
argument_list|,
name|Void
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
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
name|void
name|accept0
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"while ("
argument_list|)
operator|.
name|append
argument_list|(
name|condition
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
name|body
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End WhileExpression.java
end_comment

end_unit

