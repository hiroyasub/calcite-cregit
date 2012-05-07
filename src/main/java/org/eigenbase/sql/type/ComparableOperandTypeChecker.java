begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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

begin_comment
comment|/**  * Type checking strategy which verifies that types have the required attributes  * to be used as arguments to comparison operators.  *  * @author John V. Sichi  * @version $Id$  */
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
comment|//~ Constructors -----------------------------------------------------------
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
name|callBinding
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
comment|/**      * Similar functionality to {@link #checkOperandTypes(SqlCallBinding,      * boolean)}, but not part of the interface, and cannot throw an error.      */
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlOperatorBinding
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
name|boolean
name|result
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
name|result
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|result
condition|)
block|{
name|b
operator|=
literal|false
expr_stmt|;
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
name|callBinding
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
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
name|String
index|[]
name|array
init|=
operator|new
name|String
index|[
name|nOperands
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|array
argument_list|,
literal|"COMPARABLE_TYPE"
argument_list|)
expr_stmt|;
return|return
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|op
argument_list|,
name|opName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|array
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ComparableOperandTypeChecker.java
end_comment

end_unit

