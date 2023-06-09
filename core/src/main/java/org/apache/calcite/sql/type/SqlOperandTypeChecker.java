begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlCallBinding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlOperandCountRange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlOperator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_comment
comment|/**  * Strategy interface to check for allowed operand types of an operator call.  *  *<p>This interface is an example of the  * {@link org.apache.calcite.util.Glossary#STRATEGY_PATTERN strategy pattern}.  *  * @see OperandTypes  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Checks the types of all operands to an operator call.    *    * @param callBinding    description of the call to be checked    * @param throwOnFailure whether to throw an exception if check fails    *                       (otherwise returns false in that case)    * @return whether check succeeded    */
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
comment|/** Returns the range of operand counts allowed in a call. */
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
function_decl|;
comment|/**    * Returns a string describing the allowed formal signatures of a call, e.g.    * "SUBSTR(VARCHAR, INTEGER, INTEGER)".    *    * @param op     the operator being checked    * @param opName name to use for the operator in case of aliasing    * @return generated string    */
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
comment|/** Returns the strategy for making the arguments have consistency types. */
specifier|default
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|Consistency
operator|.
name|NONE
return|;
block|}
comment|/** Returns a copy of this checker with the given signature generator. */
specifier|default
name|CompositeOperandTypeChecker
name|withGenerator
parameter_list|(
name|BiFunction
argument_list|<
name|SqlOperator
argument_list|,
name|String
argument_list|,
name|String
argument_list|>
name|signatureGenerator
parameter_list|)
block|{
comment|// We should support for all subclasses but don't yet.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"withGenerator"
argument_list|)
throw|;
block|}
comment|/** Returns whether the {@code i}th operand is optional. */
specifier|default
name|boolean
name|isOptional
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns whether the list of parameters is fixed-length. In standard SQL,    * user-defined functions are fixed-length.    *    *<p>If true, the validator should expand calls, supplying a {@code DEFAULT}    * value for each parameter for which an argument is not supplied. */
specifier|default
name|boolean
name|isFixedParameters
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Converts this type checker to a type inference; returns null if not    * possible. */
specifier|default
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|typeInference
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Composes this with another checker using AND. */
specifier|default
name|SqlOperandTypeChecker
name|and
parameter_list|(
name|SqlOperandTypeChecker
name|checker
parameter_list|)
block|{
return|return
name|OperandTypes
operator|.
name|and
argument_list|(
name|this
argument_list|,
name|checker
argument_list|)
return|;
block|}
comment|/** Composes this with another checker using OR. */
specifier|default
name|SqlOperandTypeChecker
name|or
parameter_list|(
name|SqlOperandTypeChecker
name|checker
parameter_list|)
block|{
return|return
name|OperandTypes
operator|.
name|or
argument_list|(
name|this
argument_list|,
name|checker
argument_list|)
return|;
block|}
comment|/** Strategy used to make arguments consistent. */
enum|enum
name|Consistency
block|{
comment|/** Do not try to make arguments consistent. */
name|NONE
block|,
comment|/** Make arguments of consistent type using comparison semantics.      * Character values are implicitly converted to numeric, date-time, interval      * or boolean. */
name|COMPARE
block|,
comment|/** Convert all arguments to the least restrictive type. */
name|LEAST_RESTRICTIVE
block|}
block|}
end_interface

end_unit

