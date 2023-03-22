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
name|SqlOperandTypeChecker
import|;
end_import

begin_comment
comment|/**  * The<code>POSITION</code> function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlPositionFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|// FIXME jvs 25-Jan-2009:  POSITION should verify that
comment|// params are all same character set, like OVERLAY does implicitly
comment|// as part of rtiDyadicStringSumPrecision
specifier|private
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|OTC_CUSTOM
init|=
name|OperandTypes
operator|.
name|STRING_SAME_SAME
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|STRING_SAME_SAME_INTEGER
argument_list|)
decl_stmt|;
specifier|public
name|SqlPositionFunction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|POSITION
argument_list|,
name|ReturnTypes
operator|.
name|INTEGER_NULLABLE
argument_list|,
literal|null
argument_list|,
name|OTC_CUSTOM
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
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
literal|"IN"
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
literal|"FROM"
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
annotation|@
name|Override
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
literal|"{0}({1} IN {2})"
return|;
case|case
literal|3
case|:
return|return
literal|"{0}({1} IN {2} FROM {3})"
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
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
comment|// check that the two operands are of same type.
switch|switch
condition|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|)
block|{
case|case
literal|2
case|:
return|return
name|OperandTypes
operator|.
name|SAME_SAME
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
operator|&&
name|super
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|OperandTypes
operator|.
name|SAME_SAME_INTEGER
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
operator|&&
name|super
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

