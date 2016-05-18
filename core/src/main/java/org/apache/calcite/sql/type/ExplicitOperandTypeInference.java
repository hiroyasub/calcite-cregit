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
name|SqlCallBinding
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
comment|/**  * ExplicitOperandTypeInferences implements {@link SqlOperandTypeInference} by  * explicitly supplying a type for each parameter.  */
end_comment

begin_class
specifier|public
class|class
name|ExplicitOperandTypeInference
implements|implements
name|SqlOperandTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Use    * {@link org.apache.calcite.sql.type.InferTypes#explicit(java.util.List)}. */
name|ExplicitOperandTypeInference
parameter_list|(
name|ImmutableList
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|)
block|{
name|this
operator|.
name|paramTypes
operator|=
name|paramTypes
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
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
block|{
if|if
condition|(
name|operandTypes
operator|.
name|length
operator|!=
name|paramTypes
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// This call does not match the inference strategy.
comment|// It's likely that we're just about to give a validation error.
comment|// Don't make a fuss, just give up.
return|return;
block|}
name|paramTypes
operator|.
name|toArray
argument_list|(
name|operandTypes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ExplicitOperandTypeInference.java
end_comment

end_unit

