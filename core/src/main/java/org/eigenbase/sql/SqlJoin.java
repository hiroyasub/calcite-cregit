begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|type
operator|.
name|SqlTypeName
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
name|Util
import|;
end_import

begin_comment
comment|/**  * Parse tree node representing a {@code JOIN} clause.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJoin
extends|extends
name|SqlCall
block|{
specifier|public
specifier|static
specifier|final
name|SqlJoinOperator
name|OPERATOR
init|=
operator|new
name|SqlJoinOperator
argument_list|()
decl_stmt|;
name|SqlNode
name|left
decl_stmt|;
comment|/**    * Operand says whether this is a natural join. Must be constant TRUE or    * FALSE.    */
name|SqlLiteral
name|natural
decl_stmt|;
comment|/**    * Value must be a {@link SqlLiteral}, one of the integer codes for    * {@link JoinType}.    */
name|SqlLiteral
name|joinType
decl_stmt|;
name|SqlNode
name|right
decl_stmt|;
comment|/**    * Value must be a {@link SqlLiteral}, one of the integer codes for    * {@link JoinConditionType}.    */
name|SqlLiteral
name|conditionType
decl_stmt|;
name|SqlNode
name|condition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlJoin
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|left
parameter_list|,
name|SqlLiteral
name|natural
parameter_list|,
name|SqlLiteral
name|joinType
parameter_list|,
name|SqlNode
name|right
parameter_list|,
name|SqlLiteral
name|conditionType
parameter_list|,
name|SqlNode
name|condition
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|natural
operator|=
name|natural
expr_stmt|;
name|this
operator|.
name|joinType
operator|=
name|joinType
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
name|this
operator|.
name|conditionType
operator|=
name|conditionType
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
assert|assert
name|natural
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
assert|;
assert|assert
name|conditionType
operator|!=
literal|null
assert|;
assert|assert
name|conditionType
operator|.
name|symbolValue
argument_list|()
operator|instanceof
name|JoinConditionType
assert|;
assert|assert
name|joinType
operator|!=
literal|null
assert|;
assert|assert
name|joinType
operator|.
name|symbolValue
argument_list|()
operator|instanceof
name|JoinType
assert|;
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
name|JOIN
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
name|left
argument_list|,
name|natural
argument_list|,
name|joinType
argument_list|,
name|right
argument_list|,
name|conditionType
argument_list|,
name|condition
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
name|left
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|natural
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|joinType
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|right
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|conditionType
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|condition
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
specifier|final
name|SqlNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
comment|/** Returns a {@link JoinConditionType}, never null. */
specifier|public
specifier|final
name|JoinConditionType
name|getConditionType
parameter_list|()
block|{
return|return
name|conditionType
operator|.
name|symbolValue
argument_list|()
return|;
block|}
specifier|public
name|SqlLiteral
name|getConditionTypeNode
parameter_list|()
block|{
return|return
name|conditionType
return|;
block|}
comment|/** Returns a {@link JoinType}, never null. */
specifier|public
specifier|final
name|JoinType
name|getJoinType
parameter_list|()
block|{
return|return
name|joinType
operator|.
name|symbolValue
argument_list|()
return|;
block|}
specifier|public
name|SqlLiteral
name|getJoinTypeNode
parameter_list|()
block|{
return|return
name|joinType
return|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|void
name|setLeft
parameter_list|(
name|SqlNode
name|left
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
block|}
specifier|public
specifier|final
name|boolean
name|isNatural
parameter_list|()
block|{
return|return
name|natural
operator|.
name|booleanValue
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|SqlLiteral
name|isNaturalNode
parameter_list|()
block|{
return|return
name|natural
return|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
specifier|public
name|void
name|setRight
parameter_list|(
name|SqlNode
name|right
parameter_list|)
block|{
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
comment|/**    *<code>SqlJoinOperator</code> describes the syntax of the SQL<code>    * JOIN</code> operator. Since there is only one such operator, this class is    * almost certainly a singleton.    */
specifier|public
specifier|static
class|class
name|SqlJoinOperator
extends|extends
name|SqlOperator
block|{
specifier|private
specifier|static
specifier|final
name|SqlWriter
operator|.
name|FrameType
name|FRAME_TYPE
init|=
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|create
argument_list|(
literal|"USING"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|SqlJoinOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"JOIN"
argument_list|,
name|SqlKind
operator|.
name|JOIN
argument_list|,
literal|16
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|SPECIAL
return|;
block|}
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
return|return
operator|new
name|SqlJoin
argument_list|(
name|pos
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|1
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|2
index|]
argument_list|,
name|operands
index|[
literal|3
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|4
index|]
argument_list|,
name|operands
index|[
literal|5
index|]
argument_list|)
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
name|SqlJoin
name|join
init|=
operator|(
name|SqlJoin
operator|)
name|call
decl_stmt|;
name|join
operator|.
name|left
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|natural
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|join
operator|.
name|isNatural
argument_list|()
condition|)
block|{
name|natural
operator|=
literal|"NATURAL "
expr_stmt|;
block|}
switch|switch
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|COMMA
case|:
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|CROSS
case|:
name|writer
operator|.
name|sep
argument_list|(
name|natural
operator|+
literal|"CROSS JOIN"
argument_list|)
expr_stmt|;
break|break;
case|case
name|FULL
case|:
name|writer
operator|.
name|sep
argument_list|(
name|natural
operator|+
literal|"FULL JOIN"
argument_list|)
expr_stmt|;
break|break;
case|case
name|INNER
case|:
name|writer
operator|.
name|sep
argument_list|(
name|natural
operator|+
literal|"INNER JOIN"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LEFT
case|:
name|writer
operator|.
name|sep
argument_list|(
name|natural
operator|+
literal|"LEFT JOIN"
argument_list|)
expr_stmt|;
break|break;
case|case
name|RIGHT
case|:
name|writer
operator|.
name|sep
argument_list|(
name|natural
operator|+
literal|"RIGHT JOIN"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
throw|;
block|}
name|join
operator|.
name|right
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
if|if
condition|(
name|join
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|join
operator|.
name|getConditionType
argument_list|()
condition|)
block|{
case|case
name|USING
case|:
comment|// No need for an extra pair of parens -- the condition is a
comment|// list. The result is something like "USING (deptno, gender)".
name|writer
operator|.
name|keyword
argument_list|(
literal|"USING"
argument_list|)
expr_stmt|;
assert|assert
name|join
operator|.
name|condition
operator|instanceof
name|SqlNodeList
assert|;
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
name|FRAME_TYPE
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|join
operator|.
name|condition
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
break|break;
case|case
name|ON
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"ON"
argument_list|)
expr_stmt|;
name|join
operator|.
name|condition
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|join
operator|.
name|getConditionType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlJoin.java
end_comment

end_unit

