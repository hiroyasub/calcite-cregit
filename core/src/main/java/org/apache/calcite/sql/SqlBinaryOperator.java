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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorScope
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
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  *<code>SqlBinaryOperator</code> is a binary operator.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBinaryOperator
extends|extends
name|SqlOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlBinaryOperator.    *    * @param name                 Name of operator    * @param kind                 Kind    * @param prec                 Precedence    * @param leftAssoc            Left-associativity    * @param returnTypeInference  Strategy to infer return type    * @param operandTypeInference Strategy to infer operand types    * @param operandTypeChecker   Validator for operand types    */
specifier|public
name|SqlBinaryOperator
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
name|boolean
name|leftAssoc
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
name|leftAssoc
argument_list|)
argument_list|,
name|rightPrec
argument_list|(
name|prec
argument_list|,
name|leftAssoc
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
name|BINARY
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
comment|// op0 opname op1
return|return
literal|"{1} {0} {2}"
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>Returns true for most operators but false for the '.' operator;    * consider    *    *<blockquote>    *<pre>x.y + 5 * 6</pre>    *</blockquote>    */
annotation|@
name|Override
name|boolean
name|needsSpace
parameter_list|()
block|{
return|return
operator|!
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"."
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlOperator
name|reverse
parameter_list|()
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|EQUALS
case|:
case|case
name|NOT_EQUALS
case|:
case|case
name|IS_DISTINCT_FROM
case|:
case|case
name|IS_NOT_DISTINCT_FROM
case|:
case|case
name|OR
case|:
case|case
name|AND
case|:
case|case
name|PLUS
case|:
case|case
name|TIMES
case|:
return|return
name|this
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
return|;
case|case
name|LESS_THAN
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
return|;
default|default:
return|return
literal|null
return|;
block|}
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
specifier|final
name|SqlCall
name|call
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|convertType
argument_list|(
name|validator
argument_list|,
name|call
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|convertType
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
name|RelDataType
name|operandType0
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
name|RelDataType
name|operandType1
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType0
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType1
argument_list|)
condition|)
block|{
name|Charset
name|cs0
init|=
name|operandType0
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|Charset
name|cs1
init|=
name|operandType1
operator|.
name|getCharset
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|cs0
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|cs1
operator|)
operator|:
literal|"An implicit or explicit charset should have been set"
assert|;
if|if
condition|(
operator|!
name|cs0
operator|.
name|equals
argument_list|(
name|cs1
argument_list|)
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|RESOURCE
operator|.
name|incompatibleCharset
argument_list|(
name|getName
argument_list|()
argument_list|,
name|cs0
operator|.
name|name
argument_list|()
argument_list|,
name|cs1
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|SqlCollation
name|collation0
init|=
name|operandType0
operator|.
name|getCollation
argument_list|()
decl_stmt|;
name|SqlCollation
name|collation1
init|=
name|operandType1
operator|.
name|getCollation
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|collation0
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|collation1
operator|)
operator|:
literal|"An implicit or explicit collation should have been set"
assert|;
comment|// Validation will occur inside getCoercibilityDyadicOperator...
name|SqlCollation
name|resultCol
init|=
name|SqlCollation
operator|.
name|getCoercibilityDyadicOperator
argument_list|(
name|collation0
argument_list|,
name|collation1
argument_list|)
decl_stmt|;
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
name|type
operator|.
name|getCharset
argument_list|()
argument_list|,
name|requireNonNull
argument_list|(
name|resultCol
argument_list|,
literal|"resultCol"
argument_list|)
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
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|super
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
return|return
name|convertType
argument_list|(
name|validator
argument_list|,
name|call
argument_list|,
name|type
argument_list|)
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
literal|"/"
argument_list|)
condition|)
block|{
if|if
condition|(
name|call
operator|.
name|isOperandNull
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
operator|||
name|call
operator|.
name|isOperandNull
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// null result => CONSTANT monotonicity
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
block|}
specifier|final
name|SqlMonotonicity
name|mono0
init|=
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlMonotonicity
name|mono1
init|=
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|mono1
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
condition|)
block|{
if|if
condition|(
name|call
operator|.
name|isOperandLiteral
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|BigDecimal
name|value
init|=
name|call
operator|.
name|getOperandLiteralValue
argument_list|(
literal|1
argument_list|,
name|BigDecimal
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
block|}
switch|switch
condition|(
name|value
operator|.
name|signum
argument_list|()
condition|)
block|{
case|case
operator|-
literal|1
case|:
comment|// mono / -ve constant --> reverse mono, unstrict
return|return
name|mono0
operator|.
name|reverse
argument_list|()
operator|.
name|unstrict
argument_list|()
return|;
case|case
literal|0
case|:
comment|// mono / zero --> constant (infinity!)
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
default|default:
comment|// mono / +ve constant * mono1 --> mono, unstrict
return|return
name|mono0
operator|.
name|unstrict
argument_list|()
return|;
block|}
block|}
block|}
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
literal|2
condition|)
block|{
comment|// Special exception for AND and OR.
if|if
condition|(
operator|(
name|this
operator|==
name|SqlStdOperatorTable
operator|.
name|AND
operator|||
name|this
operator|==
name|SqlStdOperatorTable
operator|.
name|OR
operator|)
operator|&&
name|count
operator|>
literal|2
condition|)
block|{
return|return
literal|true
return|;
block|}
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

