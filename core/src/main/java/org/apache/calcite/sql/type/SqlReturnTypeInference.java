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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlOperatorBinding
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

begin_comment
comment|/**  * Strategy interface to infer the type of an operator call from the type of the  * operands.  *  *<p>This interface is an example of the  * {@link org.apache.calcite.util.Glossary#STRATEGY_PATTERN strategy pattern}.  * This makes  * sense because many operators have similar, straightforward strategies, such  * as to take the type of the first operand.  *  * @see ReturnTypes  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|SqlReturnTypeInference
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Infers the return type of a call to an {@link SqlOperator}.    *    * @param opBinding description of operator binding    * @return inferred type; may be null    */
annotation|@
name|Nullable
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
function_decl|;
comment|/** Returns a return-type inference that applies this rule then a    * transform. */
specifier|default
name|SqlReturnTypeInference
name|andThen
parameter_list|(
name|SqlTypeTransform
name|transform
parameter_list|)
block|{
return|return
name|ReturnTypes
operator|.
name|cascade
argument_list|(
name|this
argument_list|,
name|transform
argument_list|)
return|;
block|}
comment|/** Returns a return-type inference that applies this rule then another    * rule, until one of them returns a not-null result. */
specifier|default
name|SqlReturnTypeInference
name|orElse
parameter_list|(
name|SqlReturnTypeInference
name|transform
parameter_list|)
block|{
return|return
name|ReturnTypes
operator|.
name|chain
argument_list|(
name|this
argument_list|,
name|transform
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

