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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|SqlIdentifier
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
name|SqlJsonValueEmptyOrErrorBehavior
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
name|SqlTypeName
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * The<code>JSON_VALUE</code> function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJsonValueFunction
extends|extends
name|SqlFunction
block|{
specifier|private
specifier|final
name|boolean
name|returnAny
decl_stmt|;
specifier|public
name|SqlJsonValueFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|returnAny
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
parameter_list|(
name|callBinding
parameter_list|,
name|returnType
parameter_list|,
name|operandTypes
parameter_list|)
lambda|->
block|{
name|RelDataTypeFactory
name|typeFactory
init|=
name|callBinding
operator|.
name|getTypeFactory
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
name|operandTypes
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|operandTypes
index|[
name|i
index|]
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
name|this
operator|.
name|returnAny
operator|=
name|returnAny
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
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|operands
index|[
literal|1
index|]
operator|==
literal|null
condition|)
block|{
name|operandList
operator|.
name|add
argument_list|(
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|SqlJsonValueEmptyOrErrorBehavior
operator|.
name|NULL
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operands
index|[
literal|3
index|]
operator|==
literal|null
condition|)
block|{
name|operandList
operator|.
name|add
argument_list|(
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|SqlJsonValueEmptyOrErrorBehavior
operator|.
name|NULL
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operands
operator|.
name|length
operator|==
literal|6
operator|&&
name|operands
index|[
literal|5
index|]
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|returnAny
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal returning clause in json_value_any function"
argument_list|)
throw|;
block|}
name|operandList
operator|.
name|add
argument_list|(
name|operands
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|returnAny
condition|)
block|{
name|SqlDataTypeSpec
name|defaultTypeSpec
init|=
operator|new
name|SqlDataTypeSpec
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
literal|"VARCHAR"
argument_list|,
name|pos
argument_list|)
argument_list|,
literal|2000
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|operandList
operator|.
name|add
argument_list|(
name|defaultTypeSpec
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|operandList
operator|.
name|toArray
argument_list|(
name|SqlNode
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
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
literal|5
argument_list|,
literal|6
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
specifier|final
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|RelDataType
name|defaultValueOnEmptyType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|callBinding
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|defaultValueOnErrorType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|callBinding
operator|.
name|operand
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|returnType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|callBinding
operator|.
name|operand
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|canCastFrom
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|,
name|defaultValueOnEmptyType
argument_list|,
name|returnType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|canCastFrom
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|,
name|defaultValueOnErrorType
argument_list|,
name|returnType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
assert|assert
name|opBinding
operator|.
name|getOperandCount
argument_list|()
operator|==
literal|5
operator|||
name|opBinding
operator|.
name|getOperandCount
argument_list|()
operator|==
literal|6
assert|;
name|RelDataType
name|ret
decl_stmt|;
if|if
condition|(
name|opBinding
operator|.
name|getOperandCount
argument_list|()
operator|==
literal|6
condition|)
block|{
name|ret
operator|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
expr_stmt|;
block|}
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|ret
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
name|int
name|operandsCount
parameter_list|)
block|{
assert|assert
name|operandsCount
operator|==
literal|5
operator|||
name|operandsCount
operator|==
literal|6
assert|;
if|if
condition|(
name|operandsCount
operator|==
literal|6
condition|)
block|{
return|return
literal|"{0}({1} RETURNING {6} {2} {3} ON EMPTY {4} {5} ON ERROR)"
return|;
block|}
return|return
literal|"{0}({1} {2} {3} ON EMPTY {4} {5} ON ERROR)"
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
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|5
operator|||
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|6
assert|;
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
if|if
condition|(
operator|!
name|returnAny
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"RETURNING"
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|5
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
name|unparseEnum
argument_list|(
name|writer
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDefaultLiteral
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|call
operator|.
name|operand
argument_list|(
literal|2
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
name|keyword
argument_list|(
literal|"ON"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"EMPTY"
argument_list|)
expr_stmt|;
name|unparseEnum
argument_list|(
name|writer
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDefaultLiteral
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|3
argument_list|)
argument_list|)
condition|)
block|{
name|call
operator|.
name|operand
argument_list|(
literal|4
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
name|keyword
argument_list|(
literal|"ON"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"ERROR"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|unparseEnum
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlLiteral
name|literal
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
operator|(
operator|(
name|Enum
operator|)
name|literal
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isDefaultLiteral
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
return|return
name|literal
operator|.
name|getValueAs
argument_list|(
name|SqlJsonValueEmptyOrErrorBehavior
operator|.
name|class
argument_list|)
operator|==
name|SqlJsonValueEmptyOrErrorBehavior
operator|.
name|DEFAULT
return|;
block|}
specifier|private
name|boolean
name|canCastFrom
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|,
name|RelDataType
name|inType
parameter_list|,
name|RelDataType
name|outType
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|outType
argument_list|,
name|inType
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|RESOURCE
operator|.
name|cannotCastValue
argument_list|(
name|inType
operator|.
name|toString
argument_list|()
argument_list|,
name|outType
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlJsonValueFunction.java
end_comment

end_unit

