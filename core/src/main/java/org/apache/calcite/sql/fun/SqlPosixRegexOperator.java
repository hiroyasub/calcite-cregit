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
name|SqlBinaryOperator
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
name|SqlCallBinding
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
name|SqlOperandCountRange
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
name|InferTypes
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
name|type
operator|.
name|SqlOperandCountRanges
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * An operator describing the<code>~</code> operator.  *  *<p> Syntax:<code>src-value [!] ~ [*] pattern-value</code>  */
end_comment

begin_class
specifier|public
class|class
name|SqlPosixRegexOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|// ~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|negated
decl_stmt|;
comment|// ~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlPosixRegexOperator.    *    * @param name    Operator name    * @param kind    Kind    * @param negated Whether this is '!~' or '!~*'    */
name|SqlPosixRegexOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|negated
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
literal|32
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|BOOLEAN_NULLABLE
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
name|OperandTypes
operator|.
name|STRING_SAME_SAME_SAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
name|this
operator|.
name|negated
operator|=
name|negated
expr_stmt|;
block|}
comment|// ~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|between
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
return|;
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
name|pos
operator|=
name|pos
operator|.
name|plusAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|operands
argument_list|)
argument_list|)
expr_stmt|;
name|operands
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|operands
argument_list|,
name|operands
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|operands
index|[
name|operands
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
name|caseSensitive
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlBasicCall
argument_list|(
name|this
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|,
literal|false
argument_list|,
name|functionQualifier
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
name|int
name|operandCount
init|=
name|callBinding
operator|.
name|getOperandCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|operandCount
operator|!=
literal|2
operator|&&
name|operandCount
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unexpected number of args to "
operator|+
name|callBinding
operator|.
name|getCall
argument_list|()
operator|+
literal|": "
operator|+
name|operandCount
argument_list|)
throw|;
block|}
name|RelDataType
name|op1Type
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelDataType
name|op2Type
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isComparable
argument_list|(
name|op1Type
argument_list|,
name|op2Type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Incompatible first two operand types "
operator|+
name|op1Type
operator|+
literal|" and "
operator|+
name|op2Type
argument_list|)
throw|;
block|}
return|return
name|SqlTypeUtil
operator|.
name|isCharTypeComparable
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|operands
argument_list|()
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|throwOnFailure
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
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
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
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|negated
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"!"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|caseSensitive
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|" "
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
name|getLeftPrec
argument_list|()
argument_list|,
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
end_class

end_unit

