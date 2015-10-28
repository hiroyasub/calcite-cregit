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
name|linq4j
operator|.
name|Ord
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Pair
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
comment|/**  * AssignableOperandTypeChecker implements {@link SqlOperandTypeChecker} by  * verifying that the type of each argument is assignable to a predefined set of  * parameter types (under the SQL definition of "assignable").  */
end_comment

begin_class
specifier|public
class|class
name|AssignableOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|paramNames
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Instantiates this strategy with a specific set of parameter types.    *    * @param paramTypes parameter types for operands; index in this array    *                   corresponds to operand number    * @param paramNames parameter names, or null    */
specifier|public
name|AssignableOperandTypeChecker
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
parameter_list|)
block|{
name|this
operator|.
name|paramTypes
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paramTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|paramNames
operator|=
name|paramNames
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paramNames
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
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
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
name|paramTypes
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
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
block|{
comment|// Do not use callBinding.operands(). We have not resolved to a function
comment|// yet, therefore we do not know the ordered parameter names.
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RelDataType
argument_list|,
name|SqlNode
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|paramTypes
argument_list|,
name|operands
argument_list|)
control|)
block|{
name|RelDataType
name|argType
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|pair
operator|.
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|pair
operator|.
name|left
argument_list|,
name|argType
argument_list|)
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
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
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|opName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelDataType
argument_list|>
name|paramType
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|paramTypes
argument_list|)
control|)
block|{
if|if
condition|(
name|paramType
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|paramNames
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|paramNames
operator|.
name|get
argument_list|(
name|paramType
operator|.
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" => "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|paramType
operator|.
name|e
operator|.
name|getFamily
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
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
block|}
end_class

begin_comment
comment|// End AssignableOperandTypeChecker.java
end_comment

end_unit

