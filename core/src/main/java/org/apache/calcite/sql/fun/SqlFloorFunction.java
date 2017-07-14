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
operator|.
name|fun
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
name|SqlFunction
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
name|SqlFunctionCategory
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
name|SqlKind
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
name|SqlLiteral
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
name|SqlOperatorBinding
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
name|SqlUtil
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
name|SqlWriter
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
name|OperandTypes
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
name|ReturnTypes
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
name|SqlMonotonicity
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Definition of the "FLOOR" and "CEIL" built-in SQL functions.  */
end_comment

begin_class
specifier|public
class|class
name|SqlFloorFunction
extends|extends
name|SqlMonotonicUnaryFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlFloorFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_OR_EXACT_NO_SCALE
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|NUMERIC_OR_INTERVAL
argument_list|,
name|OperandTypes
operator|.
name|sequence
argument_list|(
literal|"'"
operator|+
name|kind
operator|+
literal|"(<DATE> TO<TIME_UNIT>)'\n"
operator|+
literal|"'"
operator|+
name|kind
operator|+
literal|"(<TIME> TO<TIME_UNIT>)'\n"
operator|+
literal|"'"
operator|+
name|kind
operator|+
literal|"(<TIMESTAMP> TO<TIME_UNIT>)'"
argument_list|,
name|OperandTypes
operator|.
name|DATETIME
argument_list|,
name|OperandTypes
operator|.
name|ANY
argument_list|)
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|kind
operator|==
name|SqlKind
operator|.
name|FLOOR
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|CEIL
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlOperatorBinding
name|call
parameter_list|)
block|{
comment|// Monotonic iff its first argument is, but not strict.
return|return
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|0
argument_list|)
operator|.
name|unstrict
argument_list|()
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
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"TO"
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
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
block|}
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copies a {@link SqlCall}, replacing the time unit operand with the given    * literal.    *    * @param call Call    * @param literal Literal to replace time unit with    * @param pos Parser position    * @return Modified call    */
specifier|public
specifier|static
name|SqlCall
name|replaceTimeUnitOperand
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|String
name|literal
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|SqlLiteral
name|literalNode
init|=
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
name|literal
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
argument_list|,
name|pos
argument_list|,
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|literalNode
argument_list|)
return|;
block|}
comment|/**    * Most dialects that natively support datetime floor will use this.    * In those cases the call will look like TRUNC(datetime, 'year').    *    * @param writer SqlWriter    * @param call SqlCall    * @param funName Name of the sql function to call    * @param datetimeFirst Specify the order of the datetime&amp; timeUnit    * arguments    */
specifier|public
specifier|static
name|void
name|unparseDatetimeFunction
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|String
name|funName
parameter_list|,
name|Boolean
name|datetimeFirst
parameter_list|)
block|{
name|SqlFunction
name|func
init|=
operator|new
name|SqlFunction
argument_list|(
name|funName
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|SqlCall
name|call1
decl_stmt|;
if|if
condition|(
name|datetimeFirst
condition|)
block|{
name|call1
operator|=
name|call
expr_stmt|;
block|}
else|else
block|{
comment|// switch order of operands
name|SqlNode
name|op1
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SqlNode
name|op2
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|call1
operator|=
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|op2
argument_list|,
name|op1
argument_list|)
expr_stmt|;
block|}
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|func
argument_list|,
name|writer
argument_list|,
name|call1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlFloorFunction.java
end_comment

end_unit

