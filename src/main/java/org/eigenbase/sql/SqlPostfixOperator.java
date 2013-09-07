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
package|;
end_package

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
name|type
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
name|validate
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
name|*
import|;
end_import

begin_comment
comment|/**  * A postfix unary operator.  */
end_comment

begin_class
specifier|public
class|class
name|SqlPostfixOperator
extends|extends
name|SqlOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlPostfixOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|leftPrec
argument_list|(
name|prec
argument_list|,
literal|true
argument_list|)
argument_list|,
name|rightPrec
argument_list|(
name|prec
argument_list|,
literal|true
argument_list|)
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|Postfix
return|;
block|}
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|operandsCount
argument_list|)
expr_stmt|;
return|return
literal|"{1} {0}"
return|;
block|}
specifier|protected
name|RelDataType
name|adjustType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// Determine coercibility and resulting collation name of
comment|// unary operator if needed.
name|RelDataType
name|operandType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|call
operator|.
name|operands
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|operandType
condition|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"operand's type should have been derived"
argument_list|)
throw|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType
argument_list|)
condition|)
block|{
name|SqlCollation
name|collation
init|=
name|operandType
operator|.
name|getCollation
argument_list|()
decl_stmt|;
assert|assert
literal|null
operator|!=
name|collation
operator|:
literal|"An implicit or explicit collation should have been set"
assert|;
name|type
operator|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithCharsetAndCollation
argument_list|(
name|type
argument_list|,
name|type
operator|.
name|getCharset
argument_list|()
argument_list|,
operator|new
name|SqlCollation
argument_list|(
name|collation
operator|.
name|getCollationName
argument_list|()
argument_list|,
name|collation
operator|.
name|getCoercibility
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validRexOperands
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|count
operator|!=
literal|1
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"wrong operand count "
operator|+
name|count
operator|+
literal|" for "
operator|+
name|this
assert|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlPostfixOperator.java
end_comment

end_unit

