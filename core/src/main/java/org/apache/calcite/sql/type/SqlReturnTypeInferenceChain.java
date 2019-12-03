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
name|SqlOperatorBinding
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
comment|/**  * Strategy to infer the type of an operator call from the type of the operands  * by using a series of {@link SqlReturnTypeInference} rules in a given order.  * If a rule fails to find a return type (by returning NULL), next rule is tried  * until there are no more rules in which case NULL will be returned.  */
end_comment

begin_class
specifier|public
class|class
name|SqlReturnTypeInferenceChain
implements|implements
name|SqlReturnTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|SqlReturnTypeInference
argument_list|>
name|rules
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlReturnTypeInferenceChain from an array of rules.    *    *<p>Package-protected.    * Use {@link org.apache.calcite.sql.type.ReturnTypes#chain}.</p>    */
name|SqlReturnTypeInferenceChain
parameter_list|(
name|SqlReturnTypeInference
modifier|...
name|rules
parameter_list|)
block|{
assert|assert
name|rules
operator|!=
literal|null
assert|;
assert|assert
name|rules
operator|.
name|length
operator|>
literal|1
assert|;
for|for
control|(
name|SqlReturnTypeInference
name|rule
range|:
name|rules
control|)
block|{
assert|assert
name|rule
operator|!=
literal|null
assert|;
block|}
name|this
operator|.
name|rules
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rules
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
for|for
control|(
name|SqlReturnTypeInference
name|rule
range|:
name|rules
control|)
block|{
name|RelDataType
name|ret
init|=
name|rule
operator|.
name|inferReturnType
argument_list|(
name|opBinding
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
return|return
name|ret
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

