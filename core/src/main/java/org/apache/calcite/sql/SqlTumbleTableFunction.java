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

begin_comment
comment|/**  * SqlTumbleTableFunction implements an operator for tumbling. It allows three parameters:  * 1. a table.  * 2. a descriptor to provide a watermarked column name from the input table.  * 3. an interval parameter to specify the length of window size.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTumbleTableFunction
extends|extends
name|SqlWindowTableFunction
block|{
specifier|public
name|SqlTumbleTableFunction
parameter_list|()
block|{
name|super
argument_list|(
name|SqlKind
operator|.
name|TUMBLE
operator|.
name|name
argument_list|()
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
name|validateColumnNames
argument_list|(
name|validator
argument_list|,
name|type
operator|.
name|getFieldNames
argument_list|()
argument_list|,
operator|(
operator|(
name|SqlCall
operator|)
name|operand1
operator|)
operator|.
name|getOperandList
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit
