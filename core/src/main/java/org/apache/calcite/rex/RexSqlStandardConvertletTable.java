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
name|rex
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
name|SqlBasicCall
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
name|SqlCall
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
name|SqlDataTypeSpec
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
name|SqlNode
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
name|SqlNodeList
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
name|OracleSqlOperatorTable
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
name|SqlCaseOperator
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
name|type
operator|.
name|SqlTypeUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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

begin_comment
comment|/**  * Standard implementation of {@link RexSqlConvertletTable}.  */
end_comment

begin_class
specifier|public
class|class
name|RexSqlStandardConvertletTable
extends|extends
name|RexSqlReflectiveConvertletTable
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexSqlStandardConvertletTable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Register convertlets
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_IN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_LIKE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIMILAR_TO
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_SIMILAR_TO
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DIVIDE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_UNKNOWN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_UNKNOWN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_PLUS
argument_list|)
expr_stmt|;
name|registerCaseOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BETWEEN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SYMMETRIC_BETWEEN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_BETWEEN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SYMMETRIC_NOT_BETWEEN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_DISTINCT_FROM
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS_DATE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONVERT
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRANSLATE
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVERLAY
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|OracleSqlOperatorTable
operator|.
name|TRANSLATE3
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POSITION
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHAR_LENGTH
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHARACTER_LENGTH
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UPPER
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOWER
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|INITCAP
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POWER
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SQRT
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MOD
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LN
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOG10
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXP
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|FLOOR
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CEIL
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NULLIF
argument_list|)
expr_stmt|;
name|registerEquivOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COALESCE
argument_list|)
expr_stmt|;
name|registerTypeAppendOp
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a call to an operator into a {@link SqlCall} to the same    * operator.    *    *<p>Called automatically via reflection.    *    * @param converter Converter    * @param call      Call    * @return Sql call    */
specifier|public
name|SqlNode
name|convertCall
parameter_list|(
name|RexToSqlNodeConverter
name|converter
parameter_list|,
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|get
argument_list|(
name|call
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SqlOperator
name|op
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|SqlNode
index|[]
name|exprs
init|=
name|convertExpressionList
argument_list|(
name|converter
argument_list|,
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|exprs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SqlBasicCall
argument_list|(
name|op
argument_list|,
name|exprs
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
specifier|private
name|SqlNode
index|[]
name|convertExpressionList
parameter_list|(
name|RexToSqlNodeConverter
name|converter
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|)
block|{
specifier|final
name|SqlNode
index|[]
name|exprs
init|=
operator|new
name|SqlNode
index|[
name|nodes
operator|.
name|size
argument_list|()
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
name|nodes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|exprs
index|[
name|i
index|]
operator|=
name|converter
operator|.
name|convertNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|exprs
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|exprs
return|;
block|}
comment|/**    * Creates and registers a convertlet for an operator in which    * the SQL and Rex representations are structurally equivalent.    *    * @param op operator instance    */
specifier|protected
name|void
name|registerEquivOp
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|registerOp
argument_list|(
name|op
argument_list|,
operator|new
name|EquivConvertlet
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates and registers a convertlet for an operator in which    * the SQL representation needs the result type appended    * as an extra argument (e.g. CAST).    *    * @param op operator instance    */
specifier|private
name|void
name|registerTypeAppendOp
parameter_list|(
specifier|final
name|SqlOperator
name|op
parameter_list|)
block|{
name|registerOp
argument_list|(
name|op
argument_list|,
parameter_list|(
name|converter
parameter_list|,
name|call
parameter_list|)
lambda|->
block|{
name|SqlNode
index|[]
name|operands
init|=
name|convertExpressionList
argument_list|(
name|converter
argument_list|,
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|operands
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|operands
argument_list|)
argument_list|)
decl_stmt|;
name|SqlDataTypeSpec
name|typeSpec
init|=
name|SqlTypeUtil
operator|.
name|convertTypeToSpec
argument_list|(
name|call
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|typeSpec
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlBasicCall
argument_list|(
name|op
argument_list|,
name|operandList
operator|.
name|toArray
argument_list|(
operator|new
name|SqlNode
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates and registers a convertlet for the CASE operator,    * which takes different forms for SQL vs Rex.    *    * @param op instance of CASE operator    */
specifier|private
name|void
name|registerCaseOp
parameter_list|(
specifier|final
name|SqlOperator
name|op
parameter_list|)
block|{
name|registerOp
argument_list|(
name|op
argument_list|,
parameter_list|(
name|converter
parameter_list|,
name|call
parameter_list|)
lambda|->
block|{
assert|assert
name|op
operator|instanceof
name|SqlCaseOperator
assert|;
name|SqlNode
index|[]
name|operands
init|=
name|convertExpressionList
argument_list|(
name|converter
argument_list|,
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|operands
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SqlNodeList
name|whenList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|SqlNodeList
name|thenList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|operands
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|whenList
operator|.
name|add
argument_list|(
name|operands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
name|thenList
operator|.
name|add
argument_list|(
name|operands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
name|SqlNode
name|elseExpr
init|=
name|operands
index|[
name|i
index|]
decl_stmt|;
return|return
name|op
operator|.
name|createCall
argument_list|(
literal|null
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
literal|null
argument_list|,
name|whenList
argument_list|,
name|thenList
argument_list|,
name|elseExpr
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Convertlet that converts a {@link SqlCall} to a {@link RexCall} of the    * same operator. */
specifier|private
class|class
name|EquivConvertlet
implements|implements
name|RexSqlConvertlet
block|{
specifier|private
specifier|final
name|SqlOperator
name|op
decl_stmt|;
name|EquivConvertlet
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|convertCall
parameter_list|(
name|RexToSqlNodeConverter
name|converter
parameter_list|,
name|RexCall
name|call
parameter_list|)
block|{
name|SqlNode
index|[]
name|operands
init|=
name|convertExpressionList
argument_list|(
name|converter
argument_list|,
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|operands
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SqlBasicCall
argument_list|(
name|op
argument_list|,
name|operands
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexSqlStandardConvertletTable.java
end_comment

end_unit

