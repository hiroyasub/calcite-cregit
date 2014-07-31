begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|Resources
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
name|fun
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
comment|/**  *<code>SqlCallBinding</code> implements {@link SqlOperatorBinding} by  * analyzing to the operands of a {@link SqlCall} with a {@link SqlValidator}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCallBinding
extends|extends
name|SqlOperatorBinding
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlValidator
name|validator
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
specifier|private
specifier|final
name|SqlCall
name|call
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a call binding.    *    * @param validator Validator    * @param scope     Scope of call    * @param call      Call node    */
specifier|public
name|SqlCallBinding
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|super
argument_list|(
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
specifier|final
name|SelectScope
name|selectScope
init|=
name|SqlValidatorUtil
operator|.
name|getEnclosingSelectScope
argument_list|(
name|scope
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectScope
operator|==
literal|null
condition|)
block|{
comment|// Probably "VALUES expr". Treat same as "SELECT expr GROUP BY ()"
return|return
literal|0
return|;
block|}
specifier|final
name|SqlSelect
name|select
init|=
name|selectScope
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|select
operator|.
name|getGroup
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|select
operator|.
name|getGroup
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
return|return
name|validator
operator|.
name|isAggregate
argument_list|(
name|select
argument_list|)
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
comment|/**    * Returns the validator.    */
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|validator
return|;
block|}
comment|/**    * Returns the scope of the call.    */
specifier|public
name|SqlValidatorScope
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
comment|/**    * Returns the call node.    */
specifier|public
name|SqlCall
name|getCall
parameter_list|()
block|{
return|return
name|call
return|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|String
name|getStringLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|SqlNode
name|node
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
return|return
name|SqlLiteral
operator|.
name|stringValue
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|int
name|getIntLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
comment|// todo: move this to SqlTypeUtil
name|SqlNode
name|node
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|SqlLiteral
condition|)
block|{
name|SqlLiteral
name|sqlLiteral
init|=
operator|(
name|SqlLiteral
operator|)
name|node
decl_stmt|;
return|return
name|sqlLiteral
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
return|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|SqlCall
condition|)
block|{
specifier|final
name|SqlCall
name|c
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|MINUS_PREFIX
condition|)
block|{
name|SqlNode
name|child
init|=
name|c
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|SqlLiteral
condition|)
block|{
return|return
operator|-
operator|(
operator|(
name|SqlLiteral
operator|)
name|child
operator|)
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
block|}
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"should never come here"
argument_list|)
throw|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|boolean
name|isOperandNull
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|allowCast
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|allowCast
argument_list|)
return|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|int
name|getOperandCount
parameter_list|()
block|{
return|return
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|RelDataType
name|getOperandType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operand
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|namespace
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
return|return
name|namespace
operator|.
name|getType
argument_list|()
return|;
block|}
return|return
name|type
return|;
block|}
specifier|public
name|RelDataType
name|getCursorOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isCallTo
argument_list|(
name|operand
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CURSOR
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SqlCall
name|cursorCall
init|=
operator|(
name|SqlCall
operator|)
name|operand
decl_stmt|;
specifier|final
name|SqlNode
name|query
init|=
name|cursorCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|// implement SqlOperatorBinding
specifier|public
name|String
name|getColumnListParamInfo
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|String
name|paramName
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|columnList
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operand
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isCallTo
argument_list|(
name|operand
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|SqlNode
name|id
range|:
operator|(
operator|(
name|SqlCall
operator|)
name|operand
operator|)
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|columnList
operator|.
name|add
argument_list|(
operator|(
operator|(
name|SqlIdentifier
operator|)
name|id
operator|)
operator|.
name|getSimple
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|validator
operator|.
name|getParentCursor
argument_list|(
name|paramName
argument_list|)
return|;
block|}
specifier|public
name|EigenbaseException
name|newError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/**    * Constructs a new validation signature error for the call.    *    * @return signature exception    */
specifier|public
name|EigenbaseException
name|newValidationSignatureError
parameter_list|()
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|RESOURCE
operator|.
name|canNotApplyOp2Type
argument_list|(
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|call
operator|.
name|getCallSignature
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getAllowedSignatures
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Constructs a new validation error for the call. (Do not use this to    * construct a validation error for other nodes such as an operands.)    *    * @param ex underlying exception    * @return wrapped exception    */
specifier|public
name|EigenbaseException
name|newValidationError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|ex
parameter_list|)
block|{
return|return
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|ex
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCallBinding.java
end_comment

end_unit

