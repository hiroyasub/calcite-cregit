begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Pair
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
specifier|public
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"MERGE"
argument_list|,
name|SqlKind
operator|.
name|MERGE
argument_list|)
decl_stmt|;
name|SqlNode
name|targetTable
decl_stmt|;
name|SqlNode
name|condition
decl_stmt|;
name|SqlNode
name|source
decl_stmt|;
name|SqlUpdate
name|updateCall
decl_stmt|;
name|SqlInsert
name|insertCall
decl_stmt|;
name|SqlSelect
name|sourceSelect
decl_stmt|;
name|SqlIdentifier
name|alias
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMerge
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|targetTable
parameter_list|,
name|SqlNode
name|condition
parameter_list|,
name|SqlNode
name|source
parameter_list|,
name|SqlUpdate
name|updateCall
parameter_list|,
name|SqlInsert
name|insertCall
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
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|updateCall
operator|=
name|updateCall
expr_stmt|;
name|this
operator|.
name|insertCall
operator|=
name|insertCall
expr_stmt|;
name|this
operator|.
name|sourceSelect
operator|=
name|sourceSelect
expr_stmt|;
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
return|;
block|}
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
name|MERGE
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
name|condition
argument_list|,
name|source
argument_list|,
name|updateCall
argument_list|,
name|insertCall
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
assert|assert
name|operand
operator|instanceof
name|SqlIdentifier
assert|;
name|targetTable
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|condition
operator|=
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
name|updateCall
operator|=
operator|(
name|SqlUpdate
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|insertCall
operator|=
operator|(
name|SqlInsert
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|sourceSelect
operator|=
operator|(
name|SqlSelect
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|6
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
comment|/**    * @return the identifier for the target table of the merge    */
specifier|public
name|SqlNode
name|getTargetTable
parameter_list|()
block|{
return|return
name|targetTable
return|;
block|}
comment|/**    * @return the alias for the target table of the merge    */
specifier|public
name|SqlIdentifier
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
comment|/**    * @return the source for the merge    */
specifier|public
name|SqlNode
name|getSourceTableRef
parameter_list|()
block|{
return|return
name|source
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
name|this
operator|.
name|source
operator|=
name|tableRef
expr_stmt|;
block|}
comment|/**    * @return the update statement for the merge    */
specifier|public
name|SqlUpdate
name|getUpdateCall
parameter_list|()
block|{
return|return
name|updateCall
return|;
block|}
comment|/**    * @return the insert statement for the merge    */
specifier|public
name|SqlInsert
name|getInsertCall
parameter_list|()
block|{
return|return
name|insertCall
return|;
block|}
comment|/**    * @return the condition expression to determine whether to update or insert    */
specifier|public
name|SqlNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
comment|/**    * Gets the source SELECT expression for the data to be updated/inserted.    * Returns null before the statement has been expanded by    * {@link SqlValidatorImpl#performUnconditionalRewrites(SqlNode, boolean)}.    *    * @return the source SELECT for the data to be updated    */
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
literal|"MERGE INTO"
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
name|updateCall
operator|.
name|targetColumnList
argument_list|,
name|updateCall
operator|.
name|sourceExpressionList
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
block|}
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
name|opLeft
argument_list|,
name|opRight
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
name|opLeft
argument_list|,
name|opRight
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

end_unit

