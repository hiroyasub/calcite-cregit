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
name|validate
operator|.
name|implicit
operator|.
name|TypeCoercion
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An operand type checker that supports implicit type cast, see  * {@link TypeCoercion#builtinFunctionCoercion(SqlCallBinding, List, List)}  * for details.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ImplicitCastOperandTypeChecker
block|{
comment|/**    * Checks the types of an operator's all operands, but without type coercion.    * This is mainly used as a pre-check when this checker is included as one of the rules in    * {@link CompositeOperandTypeChecker} and the composite predicate is `OR`.    *    * @param callBinding    description of the call to be checked    * @param throwOnFailure whether to throw an exception if check fails    *                       (otherwise returns false in that case)    * @return whether check succeeded    */
name|boolean
name|checkOperandTypesWithoutTypeCoercion
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
function_decl|;
comment|/**    * Get the operand SqlTypeFamily of formal index {@code iFormalOperand}.    * This is mainly used to get the operand SqlTypeFamily when this checker is included as    * one of the rules in {@link CompositeOperandTypeChecker} and the    * composite predicate is `SEQUENCE`.    *    * @param iFormalOperand the formal operand index.    * @return SqlTypeFamily of the operand.    */
name|SqlTypeFamily
name|getOperandSqlTypeFamily
parameter_list|(
name|int
name|iFormalOperand
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

