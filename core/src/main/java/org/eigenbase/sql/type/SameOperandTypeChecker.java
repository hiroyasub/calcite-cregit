begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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
comment|// implement SqlOperandTypeChecker
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
specifier|private
name|boolean
name|checkOperandTypesImpl
parameter_list|(
name|SqlOperatorBinding
name|operatorBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|,
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
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
assert|assert
operator|!
operator|(
name|throwOnFailure
operator|&&
operator|(
name|callBinding
operator|==
literal|null
operator|)
operator|)
assert|;
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
if|if
condition|(
name|operatorBinding
operator|.
name|isOperandNull
argument_list|(
name|i
argument_list|,
literal|false
argument_list|)
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
name|getValidator
argument_list|()
operator|.
name|newValidationError
argument_list|(
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operand
argument_list|(
name|i
argument_list|)
argument_list|,
name|RESOURCE
operator|.
name|nullIllegal
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
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
name|callBinding
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
comment|/**    * Similar functionality to {@link #checkOperandTypes(SqlCallBinding,    * boolean)}, but not part of the interface, and cannot throw an error.    */
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlOperatorBinding
name|operatorBinding
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
comment|// implement SqlOperandTypeChecker
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
literal|"EQUIVALENT_TYPE"
argument_list|,
literal|"EQUIVALENT_TYPE"
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
literal|"EQUIVALENT_TYPE"
argument_list|)
argument_list|)
return|;
block|}
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

begin_comment
comment|// End SameOperandTypeChecker.java
end_comment

end_unit

