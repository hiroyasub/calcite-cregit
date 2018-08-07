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
name|test
operator|.
name|fuzzer
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexFieldAccess
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexVisitorImpl
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
name|SqlOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Converts {@link RexNode} into a string form usable for inclusion into  * {@link RexProgramFuzzyTest}.  * For instance, it converts {@code AND(=(?0.bool0, true), =(?0.bool1, true))} to  * {@code isTrue(and(eq(vBool(0), trueLiteral), eq(vBool(1), trueLiteral)))}.  */
end_comment

begin_class
specifier|public
class|class
name|RexToTestCodeShuttle
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|SqlOperator
argument_list|,
name|String
argument_list|>
name|OP_METHODS
init|=
name|ImmutableMap
operator|.
expr|<
name|SqlOperator
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
literal|"and"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
literal|"or"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COALESCE
argument_list|,
literal|"coalesce"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|,
literal|"isNull"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|,
literal|"isNotNull"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_UNKNOWN
argument_list|,
literal|"isUnknown"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
argument_list|,
literal|"isTrue"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
argument_list|,
literal|"isNotTrue"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
argument_list|,
literal|"isFalse"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
argument_list|,
literal|"isNotFalse"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_DISTINCT_FROM
argument_list|,
literal|"isDistinctFrom"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
literal|"isNotDistinctFrom"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NULLIF
argument_list|,
literal|"nullIf"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
literal|"not"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
literal|"gt"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
literal|"ge"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
literal|"lt"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
literal|"le"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
literal|"eq"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
literal|"ne"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
literal|"plus"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_PLUS
argument_list|,
literal|"unaryPlus"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|,
literal|"sub"
argument_list|)
decl|.
name|put
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
argument_list|,
literal|"unaryMinus"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|protected
name|RexToTestCodeShuttle
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
name|SqlOperator
name|operator
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
name|String
name|method
init|=
name|OP_METHODS
operator|.
name|get
argument_list|(
name|operator
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"rexBuilder.makeCall("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"SqlStdOperatorTable."
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|operator
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperands
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
name|operands
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|literal
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
if|if
condition|(
name|literal
operator|.
name|isNull
argument_list|()
condition|)
block|{
return|return
literal|"nullBool"
return|;
block|}
return|return
name|literal
operator|.
name|toString
argument_list|()
operator|+
literal|"Literal"
return|;
block|}
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|INTEGER
condition|)
block|{
if|if
condition|(
name|literal
operator|.
name|isNull
argument_list|()
condition|)
block|{
return|return
literal|"nullInt"
return|;
block|}
return|return
literal|"literal("
operator|+
name|literal
operator|.
name|getValue
argument_list|()
operator|+
literal|")"
return|;
block|}
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|VARCHAR
condition|)
block|{
if|if
condition|(
name|literal
operator|.
name|isNull
argument_list|()
condition|)
block|{
return|return
literal|"nullVarchar"
return|;
block|}
block|}
return|return
literal|"/*"
operator|+
name|literal
operator|.
name|getTypeName
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"*/"
operator|+
name|literal
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"v"
argument_list|)
expr_stmt|;
name|RelDataType
name|type
init|=
name|fieldAccess
operator|.
name|getType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
name|sb
operator|.
name|append
argument_list|(
literal|"Bool"
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|sb
operator|.
name|append
argument_list|(
literal|"Int"
argument_list|)
expr_stmt|;
break|break;
case|case
name|VARCHAR
case|:
name|sb
operator|.
name|append
argument_list|(
literal|"Varchar"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"NotNull"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fieldAccess
operator|.
name|getField
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|%
literal|10
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexToTestCodeShuttle.java
end_comment

end_unit

