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
name|type
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
name|RelDataTypeComparability
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
name|validate
operator|.
name|implicit
operator|.
name|TypeCoercion
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Type checking strategy which verifies that types have the required attributes  * to be used as arguments to comparison operators.  */
end_comment

begin_class
specifier|public
class|class
name|ComparableOperandTypeChecker
extends|extends
name|SameOperandTypeChecker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataTypeComparability
name|requiredComparability
decl_stmt|;
specifier|private
specifier|final
name|Consistency
name|consistency
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ComparableOperandTypeChecker
parameter_list|(
name|int
name|nOperands
parameter_list|,
name|RelDataTypeComparability
name|requiredComparability
parameter_list|)
block|{
name|this
argument_list|(
name|nOperands
argument_list|,
name|requiredComparability
argument_list|,
name|Consistency
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ComparableOperandTypeChecker
parameter_list|(
name|int
name|nOperands
parameter_list|,
name|RelDataTypeComparability
name|requiredComparability
parameter_list|,
name|Consistency
name|consistency
parameter_list|)
block|{
name|super
argument_list|(
name|nOperands
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredComparability
operator|=
name|requiredComparability
expr_stmt|;
name|this
operator|.
name|consistency
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|consistency
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|boolean
name|b
init|=
literal|true
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
name|nOperands
condition|;
operator|++
name|i
control|)
block|{
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkType
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|,
name|type
argument_list|)
condition|)
block|{
name|b
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|b
condition|)
block|{
comment|// Coerce type first.
if|if
condition|(
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
name|TypeCoercion
name|typeCoercion
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|getTypeCoercion
argument_list|()
decl_stmt|;
comment|// For comparison operators, i.e.>,<, =,>=,<=.
name|typeCoercion
operator|.
name|binaryComparisonCoercion
argument_list|(
name|callBinding
argument_list|)
expr_stmt|;
block|}
name|b
operator|=
name|super
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|b
operator|&&
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
return|return
name|b
return|;
block|}
specifier|private
name|boolean
name|checkType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|getComparability
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|<
name|requiredComparability
operator|.
name|ordinal
argument_list|()
condition|)
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
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Similar functionality to    * {@link #checkOperandTypes(SqlCallBinding, boolean)}, but not part of the    * interface, and cannot throw an error.    */
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlOperatorBinding
name|operatorBinding
parameter_list|,
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
name|boolean
name|b
init|=
literal|true
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
name|nOperands
condition|;
operator|++
name|i
control|)
block|{
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getComparability
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|<
name|requiredComparability
operator|.
name|ordinal
argument_list|()
condition|)
block|{
name|b
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|b
condition|)
block|{
name|b
operator|=
name|super
operator|.
name|checkOperandTypes
argument_list|(
name|operatorBinding
argument_list|,
name|callBinding
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
literal|"COMPARABLE_TYPE"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|consistency
return|;
block|}
block|}
end_class

begin_comment
comment|// End ComparableOperandTypeChecker.java
end_comment

end_unit

