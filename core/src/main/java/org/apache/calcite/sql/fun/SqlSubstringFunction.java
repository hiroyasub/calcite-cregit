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
name|OperandTypes
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
name|ReturnTypes
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
name|SqlTypeName
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
name|math
operator|.
name|BigDecimal
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
comment|/**  * Definition of the "SUBSTRING" builtin SQL function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlSubstringFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates the SqlSubstringFunction.    */
name|SqlSubstringFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"SUBSTRING"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|ARG0_NULLABLE_VARYING
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
switch|switch
condition|(
name|operandsCount
condition|)
block|{
case|case
literal|2
case|:
return|return
literal|"{0}({1} FROM {2})"
return|;
case|case
literal|3
case|:
return|return
literal|"{0}({1} FROM {2} FOR {3})"
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|SqlTypeName
argument_list|>
name|typeName
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|SqlTypeName
operator|.
name|STRING_TYPES
argument_list|)
control|)
block|{
if|if
condition|(
name|typeName
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|this
argument_list|,
name|opName
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|typeName
operator|.
name|e
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|this
argument_list|,
name|opName
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|typeName
operator|.
name|e
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
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
name|SqlCall
name|call
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|SqlValidatorScope
name|scope
init|=
name|callBinding
operator|.
name|getScope
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|operands
operator|.
name|size
argument_list|()
decl_stmt|;
assert|assert
operator|(
literal|3
operator|==
name|n
operator|)
operator|||
operator|(
literal|2
operator|==
name|n
operator|)
assert|;
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|STRING
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|2
operator|==
name|n
condition|)
block|{
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|NUMERIC
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|RelDataType
name|t1
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|t2
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|t1
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|STRING
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|STRING
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isCharTypeComparable
argument_list|(
name|callBinding
argument_list|,
name|operands
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|NUMERIC
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|NUMERIC
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|inSameFamily
argument_list|(
name|t1
argument_list|,
name|t2
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
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
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
name|between
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
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
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"FROM"
argument_list|)
expr_stmt|;
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
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
if|if
condition|(
literal|3
operator|==
name|call
operator|.
name|operandCount
argument_list|()
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"FOR"
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|2
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
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
comment|// SUBSTRING(x FROM 0 FOR constant) has same monotonicity as x
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
specifier|final
name|SqlNode
name|op0
init|=
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|op1
init|=
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|op2
init|=
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SqlMonotonicity
name|mono0
init|=
name|op0
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|mono0
operator|!=
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
operator|)
operator|&&
name|op1
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
operator|&&
name|op1
operator|instanceof
name|SqlLiteral
operator|&&
operator|(
operator|(
name|SqlLiteral
operator|)
name|op1
operator|)
operator|.
name|bigDecimalValue
argument_list|()
operator|.
name|equals
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
operator|&&
name|op2
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
condition|)
block|{
return|return
name|mono0
operator|.
name|unstrict
argument_list|()
return|;
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
block|}
end_class

begin_comment
comment|// End SqlSubstringFunction.java
end_comment

end_unit

