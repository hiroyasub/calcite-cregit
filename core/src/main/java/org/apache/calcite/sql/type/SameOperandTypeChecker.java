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
name|util
operator|.
name|Util
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Parameter type-checking strategy where all operand types must be the same.  */
end_comment

begin_class
specifier|public
class|class
name|SameOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|int
name|nOperands
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SameOperandTypeChecker
parameter_list|(
name|int
name|nOperands
parameter_list|)
block|{
name|this
operator|.
name|nOperands
operator|=
name|nOperands
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|Consistency
operator|.
name|NONE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOptional
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|false
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
return|return
name|checkOperandTypesImpl
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|,
name|callBinding
argument_list|)
return|;
block|}
specifier|protected
name|List
argument_list|<
name|Integer
argument_list|>
name|getOperandList
parameter_list|(
name|int
name|operandCount
parameter_list|)
block|{
return|return
name|nOperands
operator|==
operator|-
literal|1
condition|?
name|Util
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|operandCount
argument_list|)
else|:
name|Util
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|nOperands
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|checkOperandTypesImpl
parameter_list|(
name|SqlOperatorBinding
name|operatorBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|,
annotation|@
name|Nullable
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
if|if
condition|(
name|throwOnFailure
operator|&&
name|callBinding
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"callBinding must be non-null in case throwOnFailure=true"
argument_list|)
throw|;
block|}
name|int
name|nOperandsActual
init|=
name|nOperands
decl_stmt|;
if|if
condition|(
name|nOperandsActual
operator|==
operator|-
literal|1
condition|)
block|{
name|nOperandsActual
operator|=
name|operatorBinding
operator|.
name|getOperandCount
argument_list|()
expr_stmt|;
block|}
name|RelDataType
index|[]
name|types
init|=
operator|new
name|RelDataType
index|[
name|nOperandsActual
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|operandList
init|=
name|getOperandList
argument_list|(
name|operatorBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|operandList
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|operatorBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|prev
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|operandList
control|)
block|{
if|if
condition|(
name|prev
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isComparable
argument_list|(
name|types
index|[
name|i
index|]
argument_list|,
name|types
index|[
name|prev
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|throwOnFailure
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// REVIEW jvs 5-June-2005: Why don't we use
comment|// newValidationSignatureError() here?  It gives more
comment|// specific diagnostics.
throw|throw
name|requireNonNull
argument_list|(
name|callBinding
argument_list|,
literal|"callBinding"
argument_list|)
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|needSameTypeParameter
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|prev
operator|=
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
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
return|return
name|checkOperandTypesImpl
argument_list|(
name|operatorBinding
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|// implement SqlOperandTypeChecker
annotation|@
name|Override
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
if|if
condition|(
name|nOperands
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|any
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
name|nOperands
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
specifier|final
name|String
name|typeName
init|=
name|getTypeName
argument_list|()
decl_stmt|;
return|return
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|op
argument_list|,
name|opName
argument_list|,
name|nOperands
operator|==
operator|-
literal|1
condition|?
name|ImmutableList
operator|.
name|of
argument_list|(
name|typeName
argument_list|,
name|typeName
argument_list|,
literal|"..."
argument_list|)
else|:
name|Collections
operator|.
name|nCopies
argument_list|(
name|nOperands
argument_list|,
name|typeName
argument_list|)
argument_list|)
return|;
block|}
comment|/** Override to change the behavior of    * {@link #getAllowedSignatures(SqlOperator, String)}. */
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
literal|"EQUIVALENT_TYPE"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|operand
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO:
block|}
block|}
end_class

end_unit

