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
name|rex
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
name|parser
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
comment|/**  *<code>RexCallBinding</code> implements {@link SqlOperatorBinding} by  * referring to an underlying collection of {@link RexNode} operands.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RexCallBinding
extends|extends
name|SqlOperatorBinding
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexNode
index|[]
name|operands
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexCallBinding
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlOperator
name|sqlOperator
parameter_list|,
name|RexNode
index|[]
name|operands
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
name|sqlOperator
argument_list|)
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|operands
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement SqlOperatorBinding
specifier|public
name|String
name|getStringLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|RexLiteral
operator|.
name|stringValue
argument_list|(
name|operands
index|[
name|ordinal
index|]
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
return|return
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|operands
index|[
name|ordinal
index|]
argument_list|)
return|;
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
name|RexUtil
operator|.
name|isNullLiteral
argument_list|(
name|operands
index|[
name|ordinal
index|]
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
name|operands
operator|.
name|length
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
return|return
name|operands
index|[
name|ordinal
index|]
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|EigenbaseException
name|newError
parameter_list|(
name|SqlValidatorException
name|e
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexCallBinding.java
end_comment

end_unit

