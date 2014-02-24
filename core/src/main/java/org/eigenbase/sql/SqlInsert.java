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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
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
name|SqlBasicCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// constants representing operand positions
specifier|public
specifier|static
specifier|final
name|int
name|KEYWORDS_OPERAND
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TARGET_TABLE_OPERAND
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|SOURCE_OPERAND
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TARGET_COLUMN_LIST_OPERAND
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
name|SqlInsert
parameter_list|(
name|SqlSpecialOperator
name|operator
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
name|Util
operator|.
name|pre
argument_list|(
name|keywords
operator|!=
literal|null
argument_list|,
literal|"keywords != null"
argument_list|)
expr_stmt|;
name|operands
index|[
name|KEYWORDS_OPERAND
index|]
operator|=
name|keywords
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
name|SOURCE_OPERAND
index|]
operator|=
name|source
expr_stmt|;
name|operands
index|[
name|TARGET_COLUMN_LIST_OPERAND
index|]
operator|=
name|columnList
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return the identifier for the target table of the insertion    */
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
comment|/**    * @return the source expression for the data to be inserted    */
specifier|public
name|SqlNode
name|getSource
parameter_list|()
block|{
return|return
name|operands
index|[
name|SOURCE_OPERAND
index|]
return|;
block|}
comment|/**    * @return the list of target column names, or null for all columns in the    * target table    */
specifier|public
name|SqlNodeList
name|getTargetColumnList
parameter_list|()
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|TARGET_COLUMN_LIST_OPERAND
index|]
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
specifier|final
name|SqlNodeList
name|keywords
init|=
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|KEYWORDS_OPERAND
index|]
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
name|keywords
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlInsertKeyword
name|keyword
init|=
operator|(
name|SqlInsertKeyword
operator|)
name|SqlLiteral
operator|.
name|symbolValue
argument_list|(
name|keywords
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyword
operator|==
name|modifier
condition|)
block|{
return|return
name|keywords
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|null
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
name|getTargetColumnList
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTargetColumnList
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
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|getSource
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

