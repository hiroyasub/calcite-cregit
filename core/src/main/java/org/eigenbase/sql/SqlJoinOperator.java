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
name|type
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
comment|/**  *<code>SqlJoinOperator</code> describes the syntax of the SQL<code>  * JOIN</code> operator. Since there is only one such operator, this class is  * almost certainly a singleton.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJoinOperator
extends|extends
name|SqlOperator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|SqlWriter
operator|.
name|FrameType
name|UsingFrameType
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
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Enumerates the types of condition in a join expression.    */
specifier|public
enum|enum
name|ConditionType
implements|implements
name|SqlLiteral
operator|.
name|SqlSymbol
block|{
comment|/**      * Join clause has no condition, for example "FROM EMP, DEPT"      */
name|None
block|,
comment|/**      * Join clause has an ON condition, for example "FROM EMP JOIN DEPT ON      * EMP.DEPTNO = DEPT.DEPTNO"      */
name|On
block|,
comment|/**      * Join clause has a USING condition, for example "FROM EMP JOIN DEPT      * USING (DEPTNO)"      */
name|Using
block|;
comment|/**      * Creates a parse-tree node representing an occurrence of this join      * type at a particular position in the parsed text.      */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|/**    * Enumerates the types of join.    */
specifier|public
enum|enum
name|JoinType
implements|implements
name|SqlLiteral
operator|.
name|SqlSymbol
block|{
comment|/**      * Inner join.      */
name|Inner
block|,
comment|/**      * Full outer join.      */
name|Full
block|,
comment|/**      * Cross join (also known as Cartesian product).      */
name|Cross
block|,
comment|/**      * Left outer join.      */
name|Left
block|,
comment|/**      * Right outer join.      */
name|Right
block|,
comment|/**      * Comma join: the good old-fashioned SQL<code>FROM</code> clause,      * where table expressions are specified with commas between them, and      * join conditions are specified in the<code>WHERE</code> clause.      */
name|Comma
block|;
comment|/**      * Creates a parse-tree node representing an occurrence of this      * condition type keyword at a particular position in the parsed      * text.      */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|//~ Constructors -----------------------------------------------------------
specifier|public
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
name|Special
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
assert|assert
operator|(
name|operands
index|[
name|SqlJoin
operator|.
name|IS_NATURAL_OPERAND
index|]
operator|instanceof
name|SqlLiteral
operator|)
assert|;
specifier|final
name|SqlLiteral
name|isNatural
init|=
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
name|SqlJoin
operator|.
name|IS_NATURAL_OPERAND
index|]
decl_stmt|;
assert|assert
operator|(
name|isNatural
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
operator|)
assert|;
assert|assert
name|operands
index|[
name|SqlJoin
operator|.
name|CONDITION_TYPE_OPERAND
index|]
operator|!=
literal|null
operator|:
literal|"precondition: operands[CONDITION_TYPE_OPERAND] != null"
assert|;
assert|assert
operator|(
name|operands
index|[
name|SqlJoin
operator|.
name|CONDITION_TYPE_OPERAND
index|]
operator|instanceof
name|SqlLiteral
operator|)
operator|&&
operator|(
name|SqlLiteral
operator|.
name|symbolValue
argument_list|(
name|operands
index|[
name|SqlJoin
operator|.
name|CONDITION_TYPE_OPERAND
index|]
argument_list|)
operator|instanceof
name|ConditionType
operator|)
assert|;
assert|assert
name|operands
index|[
name|SqlJoin
operator|.
name|TYPE_OPERAND
index|]
operator|!=
literal|null
operator|:
literal|"precondition: operands[TYPE_OPERAND] != null"
assert|;
assert|assert
operator|(
name|operands
index|[
name|SqlJoin
operator|.
name|TYPE_OPERAND
index|]
operator|instanceof
name|SqlLiteral
operator|)
operator|&&
operator|(
name|SqlLiteral
operator|.
name|symbolValue
argument_list|(
name|operands
index|[
name|SqlJoin
operator|.
name|TYPE_OPERAND
index|]
argument_list|)
operator|instanceof
name|JoinType
operator|)
assert|;
return|return
operator|new
name|SqlJoin
argument_list|(
name|this
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlNode
name|left
parameter_list|,
name|SqlLiteral
name|isNatural
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
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|createCall
argument_list|(
name|pos
argument_list|,
name|left
argument_list|,
name|isNatural
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
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlNode
name|left
init|=
name|operands
index|[
name|SqlJoin
operator|.
name|LEFT_OPERAND
index|]
decl_stmt|;
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
name|SqlLiteral
operator|.
name|booleanValue
argument_list|(
name|operands
index|[
name|SqlJoin
operator|.
name|IS_NATURAL_OPERAND
index|]
argument_list|)
condition|)
block|{
name|natural
operator|=
literal|"NATURAL "
expr_stmt|;
block|}
specifier|final
name|SqlJoinOperator
operator|.
name|JoinType
name|joinType
init|=
operator|(
name|JoinType
operator|)
name|SqlLiteral
operator|.
name|symbolValue
argument_list|(
name|operands
index|[
name|SqlJoin
operator|.
name|TYPE_OPERAND
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|Comma
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
name|Cross
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
name|Full
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
name|Inner
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
name|Left
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
name|Right
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
name|joinType
argument_list|)
throw|;
block|}
specifier|final
name|SqlNode
name|right
init|=
name|operands
index|[
name|SqlJoin
operator|.
name|RIGHT_OPERAND
index|]
decl_stmt|;
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
specifier|final
name|SqlNode
name|condition
init|=
name|operands
index|[
name|SqlJoin
operator|.
name|CONDITION_OPERAND
index|]
decl_stmt|;
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SqlJoinOperator
operator|.
name|ConditionType
name|conditionType
init|=
operator|(
name|ConditionType
operator|)
name|SqlLiteral
operator|.
name|symbolValue
argument_list|(
name|operands
index|[
name|SqlJoin
operator|.
name|CONDITION_TYPE_OPERAND
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|conditionType
condition|)
block|{
case|case
name|Using
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
name|UsingFrameType
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
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
name|On
case|:
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
name|conditionType
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlJoinOperator.java
end_comment

end_unit

