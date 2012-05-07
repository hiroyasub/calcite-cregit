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
comment|/**  * Parameter type-checking strategy type must be a literal (whether null is  * allowede is determined by the constructor).<code>CAST(NULL as ...)</code> is  * considered to be a NULL literal but not<code>CAST(CAST(NULL as ...) AS  * ...)</code>  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|LiteralOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|boolean
name|allowNull
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|LiteralOperandTypeChecker
parameter_list|(
name|boolean
name|allowNull
parameter_list|)
block|{
name|this
operator|.
name|allowNull
operator|=
name|allowNull
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|Util
operator|.
name|discard
argument_list|(
name|iFormalOperand
argument_list|)
expr_stmt|;
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|node
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|allowNull
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|ArgumentMustNotBeNull
operator|.
name|ex
argument_list|(
name|callBinding
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isLiteral
argument_list|(
name|node
argument_list|)
operator|&&
operator|!
name|SqlUtil
operator|.
name|isLiteralChain
argument_list|(
name|node
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
name|newError
argument_list|(
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|ArgumentMustBeLiteral
operator|.
name|ex
argument_list|(
name|callBinding
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
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
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRange
operator|.
name|One
return|;
block|}
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
literal|"<LITERAL>"
return|;
block|}
block|}
end_class

begin_comment
comment|// End LiteralOperandTypeChecker.java
end_comment

end_unit

