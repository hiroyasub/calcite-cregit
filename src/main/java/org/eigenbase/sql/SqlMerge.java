begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>SqlMerge</code> is a node of a parse tree which represents a MERGE  * statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlMerge
extends|extends
name|SqlCall
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
name|SOURCE_TABLEREF_OPERAND
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|CONDITION_OPERAND
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|UPDATE_OPERAND
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|INSERT_OPERAND
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|SOURCE_SELECT_OPERAND
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ALIAS_OPERAND
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_COUNT
init|=
literal|7
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMerge
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
name|SqlNode
name|source
parameter_list|,
name|SqlNode
name|updateCall
parameter_list|,
name|SqlNode
name|insertCall
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
name|SOURCE_TABLEREF_OPERAND
index|]
operator|=
name|source
expr_stmt|;
name|operands
index|[
name|UPDATE_OPERAND
index|]
operator|=
name|updateCall
expr_stmt|;
name|operands
index|[
name|INSERT_OPERAND
index|]
operator|=
name|insertCall
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
comment|/**      * @return the identifier for the target table of the merge      */
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
comment|/**      * @return the alias for the target table of the merge      */
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
comment|/**      * @return the source for the merge      */
specifier|public
name|SqlNode
name|getSourceTableRef
parameter_list|()
block|{
return|return
operator|(
name|SqlNode
operator|)
name|operands
index|[
name|SOURCE_TABLEREF_OPERAND
index|]
return|;
block|}
specifier|public
name|void
name|setSourceTableRef
parameter_list|(
name|SqlNode
name|tableRef
parameter_list|)
block|{
name|operands
index|[
name|SOURCE_TABLEREF_OPERAND
index|]
operator|=
name|tableRef
expr_stmt|;
block|}
comment|/**      * @return the update statement for the merge      */
specifier|public
name|SqlUpdate
name|getUpdateCall
parameter_list|()
block|{
return|return
operator|(
name|SqlUpdate
operator|)
name|operands
index|[
name|UPDATE_OPERAND
index|]
return|;
block|}
comment|/**      * @return the insert statement for the merge      */
specifier|public
name|SqlInsert
name|getInsertCall
parameter_list|()
block|{
return|return
operator|(
name|SqlInsert
operator|)
name|operands
index|[
name|INSERT_OPERAND
index|]
return|;
block|}
comment|/**      * @return the condition expression to determine whether to update or insert      */
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
comment|/**      * Gets the source SELECT expression for the data to be updated/inserted.      * Returns null before the statement has been expanded by      * SqlValidator.performUnconditionalRewrites.      *      * @return the source SELECT for the data to be updated      */
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
name|Select
argument_list|,
literal|"MERGE INTO"
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
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"USING"
argument_list|)
expr_stmt|;
name|getSourceTableRef
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
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"ON"
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
name|SqlUpdate
name|updateCall
init|=
operator|(
name|SqlUpdate
operator|)
name|getUpdateCall
argument_list|()
decl_stmt|;
if|if
condition|(
name|updateCall
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"WHEN MATCHED THEN UPDATE"
argument_list|)
expr_stmt|;
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
name|UpdateSetList
argument_list|,
literal|"SET"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Iterator
name|targetColumnIter
init|=
name|updateCall
operator|.
name|getTargetColumnList
argument_list|()
operator|.
name|getList
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
name|sourceExpressionIter
init|=
name|updateCall
operator|.
name|getSourceExpressionList
argument_list|()
operator|.
name|getList
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|targetColumnIter
operator|.
name|hasNext
argument_list|()
condition|)
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
name|targetColumnIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|id
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
operator|(
name|SqlNode
operator|)
name|sourceExpressionIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|sourceExp
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
name|setFrame
argument_list|)
expr_stmt|;
block|}
name|SqlInsert
name|insertCall
init|=
operator|(
name|SqlInsert
operator|)
name|getInsertCall
argument_list|()
decl_stmt|;
if|if
condition|(
name|insertCall
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"WHEN NOT MATCHED THEN INSERT"
argument_list|)
expr_stmt|;
if|if
condition|(
name|insertCall
operator|.
name|getTargetColumnList
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|insertCall
operator|.
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
name|insertCall
operator|.
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
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
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
name|validateMerge
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlMerge.java
end_comment

end_unit

