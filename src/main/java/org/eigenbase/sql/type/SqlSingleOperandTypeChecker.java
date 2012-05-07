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

begin_comment
comment|/**  * SqlSingleOperandTypeChecker is an extension of {@link SqlOperandTypeChecker}  * for implementations which are cabable of checking the type of a single  * operand in isolation. This isn't meaningful for all type-checking rules (e.g.  * SameOperandTypeChecker requires two operands to have matching types, so  * checking one in isolation is meaningless).  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlSingleOperandTypeChecker
extends|extends
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Checks the type of a single operand against a particular ordinal position      * within a formal operator signature. Note that the actual ordinal position      * of the operand being checked may be<em>different</em> from the position      * of the formal operand.      *      *<p>For example, when validating the actual call      *      *<blockquote>      *<pre>C(X, Y, Z)</pre>      *</blockquote>      *      * the strategy for validating the operand Z might involve checking its type      * against the formal signature OP(W). In this case,<code>      * iFormalOperand</code> would be zero, even though the position of Z within      * call C is two.      *      * @param callBinding description of the call being checked; this is only      * provided for context when throwing an exception; the implementation      * should<em>NOT</em> examine the operands of the call as part of the check      * @param operand the actual operand to be checked      * @param iFormalOperand the 0-based formal operand ordinal      * @param throwOnFailure whether to throw an exception if check fails      * (otherwise returns false in that case)      *      * @return whether check succeeded      */
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
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlSingleOperandTypeChecker.java
end_comment

end_unit

