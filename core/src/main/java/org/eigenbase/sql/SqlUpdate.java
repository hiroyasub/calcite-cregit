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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|ImmutableNullableList
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
name|Pair
import|;
end_import

begin_comment
comment|/**  * A<code>SqlUpdate</code> is a node of a parse tree which represents an UPDATE  * statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUpdate
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
literal|"UPDATE"
argument_list|,
name|SqlKind
operator|.
name|UPDATE
argument_list|)
decl_stmt|;
name|SqlIdentifier
name|targetTable
decl_stmt|;
name|SqlNodeList
name|targetColumnList
decl_stmt|;
name|SqlNodeList
name|sourceExpressionList
decl_stmt|;
name|SqlNode
name|condition
decl_stmt|;
name|SqlSelect
name|sourceSelect
decl_stmt|;
name|SqlIdentifier
name|alias
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlUpdate
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlIdentifier
name|targetTable
parameter_list|,
name|SqlNodeList
name|targetColumnList
parameter_list|,
name|SqlNodeList
name|sourceExpressionList
parameter_list|,
name|SqlNode
name|condition
parameter_list|,
name|SqlSelect
name|sourceSelect
parameter_list|,
name|SqlIdentifier
name|alias
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetTable
operator|=
name|targetTable
expr_stmt|;
name|this
operator|.
name|targetColumnList
operator|=
name|targetColumnList
expr_stmt|;
name|this
operator|.
name|sourceExpressionList
operator|=
name|sourceExpressionList
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|sourceSelect
operator|=
name|sourceSelect
expr_stmt|;
assert|assert
name|sourceExpressionList
operator|.
name|size
argument_list|()
operator|==
name|targetColumnList
operator|.
name|size
argument_list|()
assert|;
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
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
name|UPDATE
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
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|targetTable
argument_list|,
name|targetColumnList
argument_list|,
name|sourceExpressionList
argument_list|,
name|condition
argument_list|,
name|sourceSelect
argument_list|,
name|alias
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
name|targetTable
operator|=
operator|(
name|SqlIdentifier
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|targetColumnList
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
name|sourceExpressionList
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
name|condition
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|sourceExpressionList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|alias
operator|=
operator|(
name|SqlIdentifier
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
comment|/**    * @return the identifier for the target table of the update    */
specifier|public
name|SqlIdentifier
name|getTargetTable
parameter_list|()
block|{
return|return
name|targetTable
return|;
block|}
comment|/**    * @return the alias for the target table of the update    */
specifier|public
name|SqlIdentifier
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
specifier|public
name|void
name|setAlias
parameter_list|(
name|SqlIdentifier
name|alias
parameter_list|)
block|{
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
block|}
comment|/**    * @return the list of target column names    */
specifier|public
name|SqlNodeList
name|getTargetColumnList
parameter_list|()
block|{
return|return
name|targetColumnList
return|;
block|}
comment|/**    * @return the list of source expressions    */
specifier|public
name|SqlNodeList
name|getSourceExpressionList
parameter_list|()
block|{
return|return
name|sourceExpressionList
return|;
block|}
comment|/**    * Gets the filter condition for rows to be updated.    *    * @return the condition expression for the data to be updated, or null for    * all rows in the table    */
specifier|public
name|SqlNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
comment|/**    * Gets the source SELECT expression for the data to be updated. Returns    * null before the statement has been expanded by    * {@link SqlValidatorImpl#performUnconditionalRewrites(SqlNode, boolean)}.    *    * @return the source SELECT for the data to be updated    */
specifier|public
name|SqlSelect
name|getSourceSelect
parameter_list|()
block|{
return|return
name|sourceSelect
return|;
block|}
specifier|public
name|void
name|setSourceSelect
parameter_list|(
name|SqlSelect
name|sourceSelect
parameter_list|)
block|{
name|this
operator|.
name|sourceSelect
operator|=
name|sourceSelect
expr_stmt|;
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
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SELECT
argument_list|,
literal|"UPDATE"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
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
name|targetColumnList
operator|!=
literal|null
condition|)
block|{
name|targetColumnList
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
if|if
condition|(
name|alias
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|alias
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
specifier|final
name|SqlWriter
operator|.
name|Frame
name|setFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|UPDATE_SET_LIST
argument_list|,
literal|"SET"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlNode
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|getTargetColumnList
argument_list|()
argument_list|,
name|getSourceExpressionList
argument_list|()
argument_list|)
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|SqlIdentifier
name|id
init|=
operator|(
name|SqlIdentifier
operator|)
name|pair
operator|.
name|left
decl_stmt|;
name|id
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
name|writer
operator|.
name|keyword
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|SqlNode
name|sourceExp
init|=
name|pair
operator|.
name|right
decl_stmt|;
name|sourceExp
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
name|endList
argument_list|(
name|setFrame
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"WHERE"
argument_list|)
expr_stmt|;
name|condition
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
name|endList
argument_list|(
name|frame
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
name|validateUpdate
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUpdate.java
end_comment

end_unit

