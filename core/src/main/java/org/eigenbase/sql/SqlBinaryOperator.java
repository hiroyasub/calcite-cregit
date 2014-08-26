begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|*
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
name|*
import|;
end_import

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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
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
name|validate
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
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
specifier|public
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
comment|/**    * Returns whether this operator should be surrounded by space when    * unparsed.    *    *<p>Returns true for most operators but false for the '.' operator;    * consider    *    *<blockquote>    *<pre>x.y + 5 * 6</pre>    *</blockquote>    *    * @return whether this operator should be surrounded by space    */
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
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|operandType2
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
name|operandType1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType2
argument_list|)
condition|)
block|{
name|Charset
name|cs1
init|=
name|operandType1
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|Charset
name|cs2
init|=
name|operandType2
operator|.
name|getCharset
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|cs1
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|cs2
operator|)
operator|:
literal|"An implicit or explicit charset should have been set"
assert|;
if|if
condition|(
operator|!
name|cs1
operator|.
name|equals
argument_list|(
name|cs2
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
name|cs1
operator|.
name|name
argument_list|()
argument_list|,
name|cs2
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|SqlCollation
name|col1
init|=
name|operandType1
operator|.
name|getCollation
argument_list|()
decl_stmt|;
name|SqlCollation
name|col2
init|=
name|operandType2
operator|.
name|getCollation
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|col1
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|col2
operator|)
operator|:
literal|"An implicit or explicit collation should have been set"
assert|;
comment|// validation will occur inside getCoercibilityDyadicOperator...
name|SqlCollation
name|resultCol
init|=
name|SqlCollation
operator|.
name|getCoercibilityDyadicOperator
argument_list|(
name|col1
argument_list|,
name|col2
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
name|resultCol
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
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
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|operandType2
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
name|operandType1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|operandType2
argument_list|)
condition|)
block|{
name|Charset
name|cs1
init|=
name|operandType1
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|Charset
name|cs2
init|=
name|operandType2
operator|.
name|getCharset
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|cs1
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|cs2
operator|)
operator|:
literal|"An implicit or explicit charset should have been set"
assert|;
if|if
condition|(
operator|!
name|cs1
operator|.
name|equals
argument_list|(
name|cs2
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
name|cs1
operator|.
name|name
argument_list|()
argument_list|,
name|cs2
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|SqlCollation
name|col1
init|=
name|operandType1
operator|.
name|getCollation
argument_list|()
decl_stmt|;
name|SqlCollation
name|col2
init|=
name|operandType2
operator|.
name|getCollation
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|null
operator|!=
name|col1
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|col2
operator|)
operator|:
literal|"An implicit or explicit collation should have been set"
assert|;
comment|// validation will occur inside getCoercibilityDyadicOperator...
name|SqlCollation
name|resultCol
init|=
name|SqlCollation
operator|.
name|getCoercibilityDyadicOperator
argument_list|(
name|col1
argument_list|,
name|col2
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
name|resultCol
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidatorScope
name|scope
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
specifier|final
name|SqlNode
name|operand0
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|operand1
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|SqlMonotonicity
name|mono0
init|=
name|operand0
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
decl_stmt|;
specifier|final
name|SqlMonotonicity
name|mono1
init|=
name|operand1
operator|.
name|getMonotonicity
argument_list|(
name|scope
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
name|operand1
operator|instanceof
name|SqlLiteral
condition|)
block|{
name|SqlLiteral
name|literal
init|=
operator|(
name|SqlLiteral
operator|)
name|operand1
decl_stmt|;
switch|switch
condition|(
name|literal
operator|.
name|bigDecimalValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
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
argument_list|,
name|scope
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
name|boolean
name|fail
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
assert|assert
operator|!
name|fail
operator|:
literal|"wrong operand count "
operator|+
name|count
operator|+
literal|" for "
operator|+
name|this
assert|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlBinaryOperator.java
end_comment

end_unit

