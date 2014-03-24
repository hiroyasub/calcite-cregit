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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>SqlInsert</code> is a node of a parse tree which represents an INSERT  * statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlInsert
extends|extends
name|SqlCall
block|{
specifier|public
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"INSERT"
argument_list|,
name|SqlKind
operator|.
name|INSERT
argument_list|)
decl_stmt|;
name|SqlNodeList
name|keywords
decl_stmt|;
name|SqlIdentifier
name|targetTable
decl_stmt|;
name|SqlNode
name|source
decl_stmt|;
name|SqlNodeList
name|columnList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlInsert
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNodeList
name|keywords
parameter_list|,
name|SqlIdentifier
name|targetTable
parameter_list|,
name|SqlNode
name|source
parameter_list|,
name|SqlNodeList
name|columnList
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|keywords
operator|=
name|keywords
expr_stmt|;
name|this
operator|.
name|targetTable
operator|=
name|targetTable
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
assert|assert
name|keywords
operator|!=
literal|null
assert|;
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
name|INSERT
return|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
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
name|Arrays
operator|.
name|asList
argument_list|(
name|keywords
argument_list|,
name|targetTable
argument_list|,
name|source
argument_list|,
name|columnList
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
name|keywords
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|targetTable
operator|=
operator|(
name|SqlIdentifier
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|source
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|columnList
operator|=
operator|(
name|SqlNodeList
operator|)
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
comment|/**    * @return the identifier for the target table of the insertion    */
specifier|public
name|SqlIdentifier
name|getTargetTable
parameter_list|()
block|{
return|return
name|targetTable
return|;
block|}
comment|/**    * @return the source expression for the data to be inserted    */
specifier|public
name|SqlNode
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
specifier|public
name|void
name|setSource
parameter_list|(
name|SqlSelect
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
comment|/**    * @return the list of target column names, or null for all columns in the    * target table    */
specifier|public
name|SqlNodeList
name|getTargetColumnList
parameter_list|()
block|{
return|return
name|columnList
return|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getModifierNode
parameter_list|(
name|SqlInsertKeyword
name|modifier
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|keyword
range|:
name|keywords
control|)
block|{
name|SqlInsertKeyword
name|keyword2
init|=
operator|(
operator|(
name|SqlLiteral
operator|)
name|keyword
operator|)
operator|.
name|symbolValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyword2
operator|==
name|modifier
condition|)
block|{
return|return
name|keyword
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SELECT
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"INSERT INTO"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|opLeft
init|=
name|getOperator
argument_list|()
operator|.
name|getLeftPrec
argument_list|()
decl_stmt|;
specifier|final
name|int
name|opRight
init|=
name|getOperator
argument_list|()
operator|.
name|getRightPrec
argument_list|()
decl_stmt|;
name|targetTable
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|opLeft
argument_list|,
name|opRight
argument_list|)
expr_stmt|;
if|if
condition|(
name|columnList
operator|!=
literal|null
condition|)
block|{
name|columnList
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|opLeft
argument_list|,
name|opRight
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|source
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|opLeft
argument_list|,
name|opRight
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateInsert
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlInsert.java
end_comment

end_unit

