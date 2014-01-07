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
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Strategy interface to check for allowed operand types of an operator call.  *  *<p>This interface is an example of the {@link  * org.eigenbase.util.Glossary#StrategyPattern strategy pattern}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Checks the types of all operands to an operator call.    *    * @param callBinding    description of the call to be checked    * @param throwOnFailure whether to throw an exception if check fails    *                       (otherwise returns false in that case)    * @return whether check succeeded    */
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
function_decl|;
comment|/**    * @return range of operand counts allowed in a call    */
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
function_decl|;
comment|/**    * Returns a string describing the allowed formal signatures of a call, e.g.    * "SUBSTR(VARCHAR, INTEGER, INTEGER)".    *    * @param op     the operator being checked    * @param opName name to use for the operator in case of aliasing    * @return generated string    */
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
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlOperandTypeChecker.java
end_comment

end_unit

