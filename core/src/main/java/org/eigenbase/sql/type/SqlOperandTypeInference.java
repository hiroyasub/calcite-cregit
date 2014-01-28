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
comment|/**  * Strategy to infer unknown types of the operands of an operator call.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperandTypeInference
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Infers any unknown operand types.    *    * @param callBinding  description of the call being analyzed    * @param returnType   the type known or inferred for the result of the call    * @param operandTypes receives the inferred types for all operands    */
name|void
name|inferOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|RelDataType
name|returnType
parameter_list|,
name|RelDataType
index|[]
name|operandTypes
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlOperandTypeInference.java
end_comment

end_unit

