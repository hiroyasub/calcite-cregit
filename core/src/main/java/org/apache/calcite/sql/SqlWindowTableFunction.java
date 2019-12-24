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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFieldImpl
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
name|RelRecordType
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
name|SqlReturnTypeInference
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
comment|/**  * Base class for table-valued function windowing operator (TUMBLE, HOP and SESSION).  */
end_comment

begin_class
specifier|public
class|class
name|SqlWindowTableFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|SqlWindowTableFunction
parameter_list|(
name|String
name|name
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
name|ARG0_TABLE_FUNCTION_WINDOWING
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
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
literal|3
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
comment|// There should only be three operands, and number of operands are checked before
comment|// this call.
specifier|final
name|SqlNode
name|operand0
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|operand0
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|SqlTypeName
operator|.
name|ROW
condition|)
block|{
return|return
name|throwValidationSignatureErrorOrReturnFalse
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|final
name|SqlNode
name|operand1
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
name|operand1
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|DESCRIPTOR
condition|)
block|{
return|return
name|throwValidationSignatureErrorOrReturnFalse
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
for|for
control|(
name|SqlNode
name|descOperand
range|:
operator|(
operator|(
name|SqlCall
operator|)
name|operand1
operator|)
operator|.
name|getOperandList
argument_list|()
control|)
block|{
specifier|final
name|String
name|colName
init|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|descOperand
operator|)
operator|.
name|getSimple
argument_list|()
decl_stmt|;
name|boolean
name|matches
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|type
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
operator|.
name|matches
argument_list|(
name|field
argument_list|,
name|colName
argument_list|)
condition|)
block|{
name|matches
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|matches
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|descOperand
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|unknownIdentifier
argument_list|(
name|colName
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|final
name|RelDataType
name|type2
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
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
name|throwValidationSignatureErrorOrReturnFalse
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|throwValidationSignatureErrorOrReturnFalse
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|opNameToUse
parameter_list|)
block|{
return|return
name|getName
argument_list|()
operator|+
literal|"(TABLE table_name, DESCRIPTOR(col1, col2 ...), datetime interval)"
return|;
block|}
comment|/**    * The first parameter of table-value function windowing is a TABLE parameter,    * which is not scalar. So need to override SqlOperator.argumentMustBeScalar.    */
annotation|@
name|Override
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|ordinal
operator|!=
literal|0
return|;
block|}
comment|/**    * Type-inference strategy whereby the result type of a table function call is a ROW,    * which is combined from the operand #0(TABLE parameter)'s schema and two    * additional fields:    *    *<ol>    *<li>window_start. TIMESTAMP type to indicate a window's start.</li>    *<li>window_end. TIMESTAMP type to indicate a window's end.</li>    *</ol>    */
specifier|public
specifier|static
specifier|final
name|SqlReturnTypeInference
name|ARG0_TABLE_FUNCTION_WINDOWING
init|=
name|opBinding
lambda|->
block|{
name|RelDataType
name|inputRowType
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|inputRowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
decl_stmt|;
name|RelDataType
name|timestampType
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
decl_stmt|;
name|RelDataTypeField
name|windowStartField
init|=
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"window_start"
argument_list|,
name|newFields
operator|.
name|size
argument_list|()
argument_list|,
name|timestampType
argument_list|)
decl_stmt|;
name|newFields
operator|.
name|add
argument_list|(
name|windowStartField
argument_list|)
expr_stmt|;
name|RelDataTypeField
name|windowEndField
init|=
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"window_end"
argument_list|,
name|newFields
operator|.
name|size
argument_list|()
argument_list|,
name|timestampType
argument_list|)
decl_stmt|;
name|newFields
operator|.
name|add
argument_list|(
name|windowEndField
argument_list|)
expr_stmt|;
return|return
operator|new
name|RelRecordType
argument_list|(
name|inputRowType
operator|.
name|getStructKind
argument_list|()
argument_list|,
name|newFields
argument_list|)
return|;
block|}
decl_stmt|;
block|}
end_class

end_unit

