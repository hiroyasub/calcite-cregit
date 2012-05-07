begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|resource
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
comment|/**  * Operand type-checking strategy which checks operands for inclusion in type  * families.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|FamilyOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|SqlTypeFamily
index|[]
name|families
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|FamilyOperandTypeChecker
parameter_list|(
name|SqlTypeFamily
modifier|...
name|families
parameter_list|)
block|{
name|this
operator|.
name|families
operator|=
name|families
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement SqlSingleOperandTypeChecker
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
name|SqlTypeFamily
name|family
init|=
name|families
index|[
name|iFormalOperand
index|]
decl_stmt|;
if|if
condition|(
name|family
operator|==
name|SqlTypeFamily
operator|.
name|ANY
condition|)
block|{
comment|// no need to check
return|return
literal|true
return|;
block|}
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|node
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
name|node
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|NullIllegal
operator|.
name|ex
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
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|family
operator|.
name|getTypeNames
argument_list|()
operator|.
name|contains
argument_list|(
name|typeName
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
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|families
operator|.
name|length
operator|!=
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|)
block|{
comment|// assume this is an inapplicable sub-rule of a composite rule;
comment|// don't throw
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|operand
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operand
argument_list|,
name|i
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// implement SqlOperandTypeChecker
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
operator|new
name|SqlOperandCountRange
argument_list|(
name|families
operator|.
name|length
argument_list|)
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
name|families
argument_list|)
argument_list|)
return|;
block|}
comment|// hack for FarragoCalcSystemTest
specifier|public
name|SqlTypeFamily
index|[]
name|getFamilies
parameter_list|()
block|{
return|return
name|families
return|;
block|}
block|}
end_class

begin_comment
comment|// End FamilyOperandTypeChecker.java
end_comment

end_unit

