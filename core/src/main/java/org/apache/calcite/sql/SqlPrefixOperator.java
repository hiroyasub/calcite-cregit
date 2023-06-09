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
name|type
operator|.
name|SqlOperandTypeChecker
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
name|type
operator|.
name|SqlOperandTypeInference
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
name|type
operator|.
name|SqlReturnTypeInference
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
name|type
operator|.
name|SqlTypeUtil
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
name|SqlMonotonicity
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
name|SqlValidator
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
name|Litmus
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
name|Util
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|NonNullableAccessors
operator|.
name|getCharset
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|NonNullableAccessors
operator|.
name|getCollation
import|;
end_import

begin_comment
comment|/**  * A unary operator.  */
end_comment

begin_class
specifier|public
class|class
name|SqlPrefixOperator
extends|extends
name|SqlOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlPrefixOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
annotation|@
name|Nullable
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|leftPrec
argument_list|(
name|prec
argument_list|,
literal|true
argument_list|)
argument_list|,
name|rightPrec
argument_list|(
name|prec
argument_list|,
literal|true
argument_list|)
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|operandsCount
argument_list|)
expr_stmt|;
return|return
literal|"{0}{1}"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|adjustType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// Determine coercibility and resulting collation name of
comment|// unary operator if needed.
name|RelDataType
name|operandType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|operandType
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"operand's type should have been derived"
argument_list|)
throw|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType
argument_list|)
condition|)
block|{
name|SqlCollation
name|collation
init|=
name|getCollation
argument_list|(
name|operandType
argument_list|)
decl_stmt|;
name|type
operator|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithCharsetAndCollation
argument_list|(
name|type
argument_list|,
name|getCharset
argument_list|(
name|type
argument_list|)
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlOperatorBinding
name|call
parameter_list|)
block|{
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
return|return
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|0
argument_list|)
operator|.
name|reverse
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|getMonotonicity
argument_list|(
name|call
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validRexOperands
parameter_list|(
name|int
name|count
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
name|count
operator|!=
literal|1
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"wrong operand count {} for {}"
argument_list|,
name|count
argument_list|,
name|this
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
block|}
end_class

end_unit

