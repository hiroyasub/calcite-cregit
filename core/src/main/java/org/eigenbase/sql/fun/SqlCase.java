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
name|fun
package|;
end_package

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
name|org
operator|.
name|eigenbase
operator|.
name|sql
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
name|parser
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
name|util
operator|.
name|UnmodifiableArrayList
import|;
end_import

begin_comment
comment|/**  * A<code>SqlCase</code> is a node of a parse tree which represents a case  * statement. It warrants its own node type just because we have a lot of  * methods to put somewhere.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCase
extends|extends
name|SqlCall
block|{
name|SqlNode
name|value
decl_stmt|;
name|SqlNodeList
name|whenList
decl_stmt|;
name|SqlNodeList
name|thenList
decl_stmt|;
name|SqlNode
name|elseExpr
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlCase expression.    *    * @param pos Parser position    * @param value The value (null for boolean case)    * @param whenList List of all WHEN expressions    * @param thenList List of all THEN expressions    * @param elseExpr The implicit or explicit ELSE expression    */
specifier|public
name|SqlCase
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|value
parameter_list|,
name|SqlNodeList
name|whenList
parameter_list|,
name|SqlNodeList
name|thenList
parameter_list|,
name|SqlNode
name|elseExpr
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|whenList
operator|=
name|whenList
expr_stmt|;
name|this
operator|.
name|thenList
operator|=
name|thenList
expr_stmt|;
name|this
operator|.
name|elseExpr
operator|=
name|elseExpr
expr_stmt|;
block|}
comment|/**    * Creates a call to the switched form of the case operator, viz:    *    *<blockquote><code>CASE value<br>    * WHEN whenList[0] THEN thenList[0]<br>    * WHEN whenList[1] THEN thenList[1]<br>    * ...<br>    * ELSE elseClause<br>    * END</code></blockquote>    */
specifier|public
specifier|static
name|SqlCase
name|createSwitched
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|value
parameter_list|,
name|SqlNodeList
name|whenList
parameter_list|,
name|SqlNodeList
name|thenList
parameter_list|,
name|SqlNode
name|elseClause
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|!=
name|value
condition|)
block|{
name|List
argument_list|<
name|SqlNode
argument_list|>
name|list
init|=
name|whenList
operator|.
name|getList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|e
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlCall
name|call
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SqlNodeList
condition|)
block|{
name|call
operator|=
name|SqlStdOperatorTable
operator|.
name|IN
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|value
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|=
name|SqlStdOperatorTable
operator|.
name|EQUALS
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|value
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|elseClause
condition|)
block|{
name|elseClause
operator|=
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SqlCase
argument_list|(
name|pos
argument_list|,
literal|null
argument_list|,
name|whenList
argument_list|,
name|thenList
argument_list|,
name|elseClause
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|CASE
return|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|CASE
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|value
argument_list|,
name|whenList
argument_list|,
name|thenList
argument_list|,
name|elseExpr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|value
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|whenList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|thenList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|elseExpr
operator|=
name|operand
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|i
argument_list|)
throw|;
block|}
block|}
specifier|public
name|SqlNode
name|getValueOperand
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|SqlNodeList
name|getWhenOperands
parameter_list|()
block|{
return|return
name|whenList
return|;
block|}
specifier|public
name|SqlNodeList
name|getThenOperands
parameter_list|()
block|{
return|return
name|thenList
return|;
block|}
specifier|public
name|SqlNode
name|getElseOperand
parameter_list|()
block|{
return|return
name|elseExpr
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCase.java
end_comment

end_unit

