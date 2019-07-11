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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|SqlSpecialOperator
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
name|SqlSingleOperandTypeChecker
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
name|SqlTypeFamily
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * The item operator {@code [ ... ]}, used to access a given element of an  * array or map. For example, {@code myArray[3]} or {@code "myMap['foo']"}.  */
end_comment

begin_class
class|class
name|SqlItemOperator
extends|extends
name|SqlSpecialOperator
block|{
specifier|private
specifier|static
specifier|final
name|SqlSingleOperandTypeChecker
name|ARRAY_OR_MAP
init|=
name|OperandTypes
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ARRAY
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|MAP
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
argument_list|)
decl_stmt|;
name|SqlItemOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"ITEM"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|100
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
annotation|@
name|Override
specifier|public
name|ReduceResult
name|reduceExpr
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|TokenSequence
name|list
parameter_list|)
block|{
name|SqlNode
name|left
init|=
name|list
operator|.
name|node
argument_list|(
name|ordinal
operator|-
literal|1
argument_list|)
decl_stmt|;
name|SqlNode
name|right
init|=
name|list
operator|.
name|node
argument_list|(
name|ordinal
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReduceResult
argument_list|(
name|ordinal
operator|-
literal|1
argument_list|,
name|ordinal
operator|+
literal|2
argument_list|,
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|sum
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|left
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|right
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|list
operator|.
name|pos
argument_list|(
name|ordinal
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
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
name|leftPrec
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
literal|"["
argument_list|,
literal|"]"
argument_list|)
decl_stmt|;
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
name|of
argument_list|(
literal|2
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
name|SqlNode
name|left
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|right
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ARRAY_OR_MAP
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|left
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|SqlSingleOperandTypeChecker
name|checker
init|=
name|getChecker
argument_list|(
name|callBinding
argument_list|)
decl_stmt|;
return|return
name|checker
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|right
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|private
name|SqlSingleOperandTypeChecker
name|getChecker
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
specifier|final
name|RelDataType
name|operandType
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operandType
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
return|return
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|)
return|;
case|case
name|MAP
case|:
return|return
name|OperandTypes
operator|.
name|family
argument_list|(
name|operandType
operator|.
name|getKeyType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getFamily
argument_list|()
argument_list|)
return|;
case|case
name|ROW
case|:
return|return
name|OperandTypes
operator|.
name|CHARACTER
return|;
case|case
name|ANY
case|:
case|case
name|DYNAMIC_STAR
case|:
return|return
name|OperandTypes
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|INTEGER
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|"<ARRAY>[<INTEGER>]\n"
operator|+
literal|"<MAP>[<VALUE>]"
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
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|operandType
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operandType
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|operandType
operator|.
name|getComponentType
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
case|case
name|MAP
case|:
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|operandType
operator|.
name|getValueType
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
case|case
name|ROW
case|:
name|String
name|fieldName
init|=
name|opBinding
operator|.
name|getOperandLiteralValue
argument_list|(
literal|1
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|RelDataTypeField
name|field
init|=
name|operandType
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Cannot infer type of field '"
operator|+
name|fieldName
operator|+
literal|"' within ROW type: "
operator|+
name|operandType
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|field
operator|.
name|getType
argument_list|()
return|;
block|}
case|case
name|ANY
case|:
case|case
name|DYNAMIC_STAR
case|:
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlItemOperator.java
end_comment

end_unit

