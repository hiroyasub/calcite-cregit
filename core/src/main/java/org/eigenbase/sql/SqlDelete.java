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
comment|/**  * A<code>SqlDelete</code> is a node of a parse tree which represents a DELETE  * statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDelete
extends|extends
name|SqlBasicCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// constants representing operand positions
specifier|public
specifier|static
specifier|final
name|int
name|TARGET_TABLE_OPERAND
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CONDITION_OPERAND
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|SOURCE_SELECT_OPERAND
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ALIAS_OPERAND
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_COUNT
init|=
literal|4
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlDelete
parameter_list|(
name|SqlSpecialOperator
name|operator
parameter_list|,
name|SqlIdentifier
name|targetTable
parameter_list|,
name|SqlNode
name|condition
parameter_list|,
name|SqlIdentifier
name|alias
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|operator
argument_list|,
operator|new
name|SqlNode
index|[
name|OPERAND_COUNT
index|]
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|operands
index|[
name|TARGET_TABLE_OPERAND
index|]
operator|=
name|targetTable
expr_stmt|;
name|operands
index|[
name|CONDITION_OPERAND
index|]
operator|=
name|condition
expr_stmt|;
name|operands
index|[
name|ALIAS_OPERAND
index|]
operator|=
name|alias
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return the identifier for the target table of the deletion    */
specifier|public
name|SqlIdentifier
name|getTargetTable
parameter_list|()
block|{
return|return
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|TARGET_TABLE_OPERAND
index|]
return|;
block|}
comment|/**    * @return the alias for the target table of the deletion    */
specifier|public
name|SqlIdentifier
name|getAlias
parameter_list|()
block|{
return|return
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|ALIAS_OPERAND
index|]
return|;
block|}
comment|/**    * Gets the filter condition for rows to be deleted.    *    * @return the condition expression for the data to be deleted, or null for    * all rows in the table    */
specifier|public
name|SqlNode
name|getCondition
parameter_list|()
block|{
return|return
name|operands
index|[
name|CONDITION_OPERAND
index|]
return|;
block|}
comment|/**    * Gets the source SELECT expression for the data to be deleted. This    * returns null before the condition has been expanded by    * SqlValidator.performUnconditionRewrites.    *    * @return the source SELECT for the data to be inserted    */
specifier|public
name|SqlSelect
name|getSourceSelect
parameter_list|()
block|{
return|return
operator|(
name|SqlSelect
operator|)
name|operands
index|[
name|SOURCE_SELECT_OPERAND
index|]
return|;
block|}
comment|// implement SqlNode
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
literal|"DELETE FROM"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|getTargetTable
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getAlias
argument_list|()
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
name|getAlias
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getCondition
argument_list|()
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
name|getCondition
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getRightPrec
argument_list|()
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
name|validateDelete
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlDelete.java
end_comment

end_unit

