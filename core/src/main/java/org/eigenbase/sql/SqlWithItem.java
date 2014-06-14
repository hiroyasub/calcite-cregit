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
name|SqlParserPos
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

begin_comment
comment|/**  * An item in a WITH clause of a query.  * It has a name, an optional column list, and a query.  */
end_comment

begin_class
specifier|public
class|class
name|SqlWithItem
extends|extends
name|SqlCall
block|{
specifier|public
name|SqlIdentifier
name|name
decl_stmt|;
specifier|public
name|SqlNodeList
name|columnList
decl_stmt|;
comment|// may be null
specifier|public
name|SqlNode
name|query
decl_stmt|;
specifier|public
name|SqlWithItem
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
name|SqlNodeList
name|columnList
parameter_list|,
name|SqlNode
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
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
name|WITH_ITEM
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
name|name
argument_list|,
name|columnList
argument_list|,
name|query
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
name|name
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
name|columnList
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
name|query
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
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|SqlWithItemOperator
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * SqlWithItemOperator is used to represent an item in a WITH clause of a    * query. It has a name, an optional column list, and a query.    */
specifier|private
specifier|static
class|class
name|SqlWithItemOperator
extends|extends
name|SqlSpecialOperator
block|{
specifier|private
specifier|static
specifier|final
name|SqlWithItemOperator
name|INSTANCE
init|=
operator|new
name|SqlWithItemOperator
argument_list|()
decl_stmt|;
specifier|public
name|SqlWithItemOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"WITH_ITEM"
argument_list|,
name|SqlKind
operator|.
name|WITH_ITEM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWithItem
name|withItem
init|=
operator|(
name|SqlWithItem
operator|)
name|call
decl_stmt|;
name|withItem
operator|.
name|name
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|withItem
operator|.
name|columnList
operator|!=
literal|null
condition|)
block|{
name|withItem
operator|.
name|columnList
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|withItem
operator|.
name|query
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
assert|assert
name|functionQualifier
operator|==
literal|null
assert|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|3
assert|;
return|return
operator|new
name|SqlWithItem
argument_list|(
name|pos
argument_list|,
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
literal|0
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|1
index|]
argument_list|,
name|operands
index|[
literal|2
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlWithItemOperator.java
end_comment

end_unit

