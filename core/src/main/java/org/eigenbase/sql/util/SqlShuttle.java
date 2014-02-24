begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Basic implementation of {@link SqlVisitor} which returns each leaf node  * unchanged.  *  *<p>This class is useful as a base class for classes which implement the  * {@link SqlVisitor} interface and have {@link SqlNode} as the return type. The  * derived class can override whichever methods it chooses.  */
end_comment

begin_class
specifier|public
class|class
name|SqlShuttle
extends|extends
name|SqlBasicVisitor
argument_list|<
name|SqlNode
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
return|return
name|literal
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
return|return
name|id
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlDataTypeSpec
name|type
parameter_list|)
block|{
return|return
name|type
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlDynamicParam
name|param
parameter_list|)
block|{
return|return
name|param
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
block|{
return|return
name|intervalQualifier
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
specifier|final
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Handler creates a new copy of 'call' only if one or more operands
comment|// change.
name|ArgHandler
argument_list|<
name|SqlNode
argument_list|>
name|argHandler
init|=
operator|new
name|CallCopyingArgHandler
argument_list|(
name|call
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|acceptCall
argument_list|(
name|this
argument_list|,
name|call
argument_list|,
literal|false
argument_list|,
name|argHandler
argument_list|)
expr_stmt|;
return|return
name|argHandler
operator|.
name|result
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
name|boolean
name|update
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|SqlNode
argument_list|>
name|exprs
init|=
name|nodeList
operator|.
name|getList
argument_list|()
decl_stmt|;
name|int
name|exprCount
init|=
name|exprs
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SqlNode
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlNode
argument_list|>
argument_list|(
name|exprCount
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|exprs
control|)
block|{
name|SqlNode
name|clonedOperand
decl_stmt|;
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
name|clonedOperand
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|clonedOperand
operator|=
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|clonedOperand
operator|!=
name|operand
condition|)
block|{
name|update
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|newList
operator|.
name|add
argument_list|(
name|clonedOperand
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|update
condition|)
block|{
return|return
operator|new
name|SqlNodeList
argument_list|(
name|newList
argument_list|,
name|nodeList
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|nodeList
return|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Implementation of {@link ArgHandler} which deep-copies {@link SqlCall}s    * and their operands.    */
specifier|protected
class|class
name|CallCopyingArgHandler
implements|implements
name|ArgHandler
argument_list|<
name|SqlNode
argument_list|>
block|{
name|boolean
name|update
decl_stmt|;
name|SqlNode
index|[]
name|clonedOperands
decl_stmt|;
specifier|private
specifier|final
name|SqlCall
name|call
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|alwaysCopy
decl_stmt|;
specifier|public
name|CallCopyingArgHandler
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|boolean
name|alwaysCopy
parameter_list|)
block|{
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
name|this
operator|.
name|update
operator|=
literal|false
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
name|this
operator|.
name|clonedOperands
operator|=
name|operands
operator|.
name|toArray
argument_list|(
operator|new
name|SqlNode
index|[
name|operands
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|alwaysCopy
operator|=
name|alwaysCopy
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|result
parameter_list|()
block|{
if|if
condition|(
name|update
operator|||
name|alwaysCopy
condition|)
block|{
return|return
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
argument_list|,
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|clonedOperands
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|call
return|;
block|}
block|}
specifier|public
name|SqlNode
name|visitChild
parameter_list|(
name|SqlVisitor
argument_list|<
name|SqlNode
argument_list|>
name|visitor
parameter_list|,
name|SqlNode
name|expr
parameter_list|,
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SqlNode
name|newOperand
init|=
name|operand
operator|.
name|accept
argument_list|(
name|SqlShuttle
operator|.
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|newOperand
operator|!=
name|operand
condition|)
block|{
name|update
operator|=
literal|true
expr_stmt|;
block|}
name|clonedOperands
index|[
name|i
index|]
operator|=
name|newOperand
expr_stmt|;
return|return
name|newOperand
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlShuttle.java
end_comment

end_unit

