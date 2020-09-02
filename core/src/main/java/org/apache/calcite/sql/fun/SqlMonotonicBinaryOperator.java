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
name|sql
operator|.
name|SqlBinaryOperator
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
name|validate
operator|.
name|SqlMonotonicity
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

begin_comment
comment|/**  * Base class for binary operators such as addition, subtraction, and  * multiplication which are monotonic for the patterns<code>m op c</code> and  *<code>c op m</code> where m is any monotonic expression and c is a constant.  */
end_comment

begin_class
specifier|public
class|class
name|SqlMonotonicBinaryOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMonotonicBinaryOperator
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
name|isLeftAssoc
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
name|prec
argument_list|,
name|isLeftAssoc
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
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlOperatorBinding
name|call
parameter_list|)
block|{
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
comment|// unknown<op> unknown --> unknown
if|if
condition|(
name|mono0
operator|==
literal|null
operator|||
name|mono1
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// constant<op> constant --> constant
if|if
condition|(
operator|(
name|mono1
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
operator|)
operator|&&
operator|(
name|mono0
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
operator|)
condition|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
block|}
comment|// monotonic<op> constant
if|if
condition|(
name|mono1
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
condition|)
block|{
comment|// mono0 + constant --> mono0
comment|// mono0 - constant --> mono0
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
operator|||
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
return|return
name|mono0
return|;
block|}
assert|assert
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
assert|;
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
switch|switch
condition|(
name|value
operator|==
literal|null
condition|?
literal|1
else|:
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
comment|// mono0 * negative constant --> reverse mono0
return|return
name|mono0
operator|.
name|reverse
argument_list|()
return|;
case|case
literal|0
case|:
comment|// mono0 * 0 --> constant (zero)
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
default|default:
comment|// mono0 * positive constant --> mono0
return|return
name|mono0
return|;
block|}
block|}
comment|// constant<op> mono
if|if
condition|(
name|mono0
operator|==
name|SqlMonotonicity
operator|.
name|CONSTANT
condition|)
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
comment|// constant - mono1 --> reverse mono1
return|return
name|mono1
operator|.
name|reverse
argument_list|()
return|;
block|}
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
comment|// constant + mono1 --> mono1
return|return
name|mono1
return|;
block|}
assert|assert
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
assert|;
if|if
condition|(
operator|!
name|call
operator|.
name|isOperandNull
argument_list|(
literal|0
argument_list|,
literal|true
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
literal|0
argument_list|,
name|BigDecimal
operator|.
name|class
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|value
operator|==
literal|null
condition|?
literal|1
else|:
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
comment|// negative constant * mono1 --> reverse mono1
return|return
name|mono1
operator|.
name|reverse
argument_list|()
return|;
case|case
literal|0
case|:
comment|// 0 * mono1 --> constant (zero)
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
default|default:
comment|// positive constant * mono1 --> mono1
return|return
name|mono1
return|;
block|}
block|}
block|}
comment|// strictly asc + strictly asc --> strictly asc
comment|//   e.g. 2 * orderid + 3 * orderid
comment|//     is strictly increasing if orderid is strictly increasing
comment|// asc + asc --> asc
comment|//   e.g. 2 * orderid + 3 * orderid
comment|//     is increasing if orderid is increasing
comment|// asc + desc --> not monotonic
comment|//   e.g. 2 * orderid + (-3 * orderid) is not monotonic
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
if|if
condition|(
name|mono0
operator|==
name|mono1
condition|)
block|{
return|return
name|mono0
return|;
block|}
if|else if
condition|(
name|mono0
operator|.
name|unstrict
argument_list|()
operator|==
name|mono1
operator|.
name|unstrict
argument_list|()
condition|)
block|{
return|return
name|mono0
operator|.
name|unstrict
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
block|}
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
if|if
condition|(
name|mono0
operator|==
name|mono1
operator|.
name|reverse
argument_list|()
condition|)
block|{
return|return
name|mono0
return|;
block|}
if|else if
condition|(
name|mono0
operator|.
name|unstrict
argument_list|()
operator|==
name|mono1
operator|.
name|reverse
argument_list|()
operator|.
name|unstrict
argument_list|()
condition|)
block|{
return|return
name|mono0
operator|.
name|unstrict
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
block|}
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
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
block|}
end_class

end_unit

