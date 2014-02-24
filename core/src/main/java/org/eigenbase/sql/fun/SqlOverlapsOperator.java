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
name|fun
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

begin_comment
comment|/**  * SqlOverlapsOperator represents the SQL:1999 standard {@code OVERLAPS}  * function. Determines whether two anchored time intervals overlap.  */
end_comment

begin_class
specifier|public
class|class
name|SqlOverlapsOperator
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|SqlWriter
operator|.
name|FrameType
name|FRAME_TYPE
init|=
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|create
argument_list|(
literal|"OVERLAPS"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlOverlapsOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"OVERLAPS"
argument_list|,
name|SqlKind
operator|.
name|OVERLAPS
argument_list|,
literal|30
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|BOOLEAN_NULLABLE
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|FRAME_TYPE
argument_list|,
literal|"("
argument_list|,
literal|")"
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
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|")"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"("
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|3
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
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
literal|4
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
name|int
name|operandsCount
parameter_list|)
block|{
assert|assert
literal|4
operator|==
name|operandsCount
assert|;
return|return
literal|"({1}, {2}) {0} ({3}, {4})"
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
specifier|final
name|String
name|d
init|=
literal|"DATETIME"
decl_stmt|;
specifier|final
name|String
name|i
init|=
literal|"INTERVAL"
decl_stmt|;
name|String
index|[]
name|typeNames
init|=
block|{
name|d
block|,
name|d
block|,
name|d
block|,
name|i
block|,
name|i
block|,
name|d
block|,
name|i
block|,
name|i
block|}
decl_stmt|;
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|typeNames
operator|.
name|length
condition|;
name|y
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|y
operator|>
literal|0
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|this
argument_list|,
name|opName
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|d
argument_list|,
name|typeNames
index|[
name|y
index|]
argument_list|,
name|d
argument_list|,
name|typeNames
index|[
name|y
operator|+
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|SqlCall
name|call
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|SqlValidatorScope
name|scope
init|=
name|callBinding
operator|.
name|getScope
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|DATETIME
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
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
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|DATETIME
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
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
name|RelDataType
name|t0
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|t1
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|t2
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|t3
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
comment|// t0 must be comparable with t2
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|sameNamedType
argument_list|(
name|t0
argument_list|,
name|t2
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
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|t1
argument_list|)
condition|)
block|{
comment|// if t1 is of DATETIME,
comment|// then t1 must be comparable with t0
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|sameNamedType
argument_list|(
name|t0
argument_list|,
name|t1
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
block|}
if|else if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|t1
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
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|t3
argument_list|)
condition|)
block|{
comment|// if t3 is of DATETIME,
comment|// then t3 must be comparable with t2
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|sameNamedType
argument_list|(
name|t2
argument_list|,
name|t3
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
block|}
if|else if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|t3
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
block|}
end_class

begin_comment
comment|// End SqlOverlapsOperator.java
end_comment

end_unit

