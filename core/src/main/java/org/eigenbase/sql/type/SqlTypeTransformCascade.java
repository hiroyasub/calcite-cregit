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
comment|/**  * Strategy to infer the type of an operator call from the type of the operands  * by using one {@link SqlReturnTypeInference} rule and a combination of {@link  * SqlTypeTransform}s  */
end_comment

begin_class
specifier|public
class|class
name|SqlTypeTransformCascade
implements|implements
name|SqlReturnTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlReturnTypeInference
name|rule
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|SqlTypeTransform
argument_list|>
name|transforms
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlTypeTransformCascade from a rule and an array of one or more    * transforms.    */
specifier|public
name|SqlTypeTransformCascade
parameter_list|(
name|SqlReturnTypeInference
name|rule
parameter_list|,
name|SqlTypeTransform
modifier|...
name|transforms
parameter_list|)
block|{
assert|assert
name|rule
operator|!=
literal|null
assert|;
assert|assert
name|transforms
operator|.
name|length
operator|>
literal|0
assert|;
name|this
operator|.
name|rule
operator|=
name|rule
expr_stmt|;
name|this
operator|.
name|transforms
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|transforms
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
operator|==
literal|null
condition|)
block|{
comment|// inferReturnType may return null; transformType does not accept or
comment|// return null types
return|return
literal|null
return|;
block|}
for|for
control|(
name|SqlTypeTransform
name|transform
range|:
name|transforms
control|)
block|{
name|ret
operator|=
name|transform
operator|.
name|transformType
argument_list|(
name|opBinding
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTypeTransformCascade.java
end_comment

end_unit

