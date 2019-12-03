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
name|fun
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFamily
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
name|SqlCall
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
name|SqlDynamicParam
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
name|SqlFunction
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
name|SqlFunctionCategory
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
name|SqlIntervalQualifier
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
name|SqlKind
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
name|SqlLiteral
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
name|SqlOperatorBinding
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
name|SqlSyntax
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
name|SqlUtil
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
name|SqlWriter
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
name|InferTypes
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
name|SqlOperandCountRanges
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
name|SqlTypeFamily
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
name|SqlValidatorImpl
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
name|ImmutableSetMultimap
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
name|SetMultimap
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
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * SqlCastFunction. Note that the std functions are really singleton objects,  * because they always get fetched via the StdOperatorTable. So you can't store  * any local info in the class and hence the return type data is maintained in  * operand[1] through the validation phase.  *  *<p>Can be used for both {@link SqlCall} and  * {@link org.apache.calcite.rex.RexCall}.  * Note that the {@code SqlCall} has two operands (expression and type),  * while the {@code RexCall} has one operand (expression) and the type is  * obtained from {@link org.apache.calcite.rex.RexNode#getType()}.  *  * @see SqlCastOperator  */
end_comment

begin_class
specifier|public
class|class
name|SqlCastFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/** Map of all casts that do not preserve monotonicity. */
specifier|private
specifier|final
name|SetMultimap
argument_list|<
name|SqlTypeFamily
argument_list|,
name|SqlTypeFamily
argument_list|>
name|nonMonotonicCasts
init|=
name|ImmutableSetMultimap
operator|.
expr|<
name|SqlTypeFamily
decl_stmt|,
name|SqlTypeFamily
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|APPROXIMATE_NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|,
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|,
name|SqlTypeFamily
operator|.
name|EXACT_NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|,
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|,
name|SqlTypeFamily
operator|.
name|APPROXIMATE_NUMERIC
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME_INTERVAL
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|,
name|SqlTypeFamily
operator|.
name|TIME
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|TIMESTAMP
argument_list|,
name|SqlTypeFamily
operator|.
name|TIME
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|TIME
argument_list|,
name|SqlTypeFamily
operator|.
name|DATETIME
argument_list|)
decl|.
name|put
argument_list|(
name|SqlTypeFamily
operator|.
name|TIME
argument_list|,
name|SqlTypeFamily
operator|.
name|TIMESTAMP
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCastFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"CAST"
argument_list|,
name|SqlKind
operator|.
name|CAST
argument_list|,
literal|null
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
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
assert|assert
name|opBinding
operator|.
name|getOperandCount
argument_list|()
operator|==
literal|2
assert|;
name|RelDataType
name|ret
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelDataType
name|firstType
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ret
operator|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|ret
argument_list|,
name|firstType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|opBinding
operator|instanceof
name|SqlCallBinding
condition|)
block|{
name|SqlCallBinding
name|callBinding
init|=
operator|(
name|SqlCallBinding
operator|)
name|opBinding
decl_stmt|;
name|SqlNode
name|operand0
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// dynamic parameters and null constants need their types assigned
comment|// to them using the type they are casted to.
if|if
condition|(
operator|(
operator|(
name|operand0
operator|instanceof
name|SqlLiteral
operator|)
operator|&&
operator|(
operator|(
operator|(
name|SqlLiteral
operator|)
name|operand0
operator|)
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|)
operator|)
operator|||
operator|(
name|operand0
operator|instanceof
name|SqlDynamicParam
operator|)
condition|)
block|{
specifier|final
name|SqlValidatorImpl
name|validator
init|=
operator|(
name|SqlValidatorImpl
operator|)
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|operand0
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
assert|assert
name|operandsCount
operator|==
literal|2
assert|;
return|return
literal|"{0}({1} AS {2})"
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
literal|2
argument_list|)
return|;
block|}
comment|/**    * Makes sure that the number and types of arguments are allowable.    * Operators (such as "ROW" and "AS") which do not check their arguments can    * override this method.    */
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
specifier|final
name|SqlNode
name|left
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|right
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|left
argument_list|,
literal|false
argument_list|)
operator|||
name|left
operator|instanceof
name|SqlDynamicParam
condition|)
block|{
return|return
literal|true
return|;
block|}
name|RelDataType
name|validatedNodeType
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|getValidatedNodeType
argument_list|(
name|left
argument_list|)
decl_stmt|;
name|RelDataType
name|returnType
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
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|returnType
argument_list|,
name|validatedNodeType
argument_list|,
literal|true
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
name|newError
argument_list|(
name|RESOURCE
operator|.
name|cannotCastValue
argument_list|(
name|validatedNodeType
operator|.
name|toString
argument_list|()
argument_list|,
name|returnType
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|areCharacterSetsMismatched
argument_list|(
name|validatedNodeType
argument_list|,
name|returnType
argument_list|)
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
comment|// Include full type string to indicate character
comment|// set mismatch.
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|RESOURCE
operator|.
name|cannotCastValue
argument_list|(
name|validatedNodeType
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|returnType
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|SPECIAL
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|2
assert|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
if|if
condition|(
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|SqlIntervalQualifier
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"INTERVAL"
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
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
name|RelDataTypeFamily
name|castFrom
init|=
name|call
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
operator|.
name|getFamily
argument_list|()
decl_stmt|;
name|RelDataTypeFamily
name|castTo
init|=
name|call
operator|.
name|getOperandType
argument_list|(
literal|1
argument_list|)
operator|.
name|getFamily
argument_list|()
decl_stmt|;
if|if
condition|(
name|castFrom
operator|instanceof
name|SqlTypeFamily
operator|&&
name|castTo
operator|instanceof
name|SqlTypeFamily
operator|&&
name|nonMonotonicCasts
operator|.
name|containsEntry
argument_list|(
name|castFrom
argument_list|,
name|castTo
argument_list|)
condition|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
else|else
block|{
return|return
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

