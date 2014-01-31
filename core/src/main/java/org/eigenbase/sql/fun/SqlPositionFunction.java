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
name|fun
package|;
end_package

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
specifier|public
name|SqlPositionFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"POSITION"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiNullableInteger
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcStringSameX2
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
index|[]
name|operands
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
name|operands
index|[
literal|0
index|]
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
name|operands
index|[
literal|1
index|]
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
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
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
literal|"{0}({1} IN {2})"
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
comment|// check that the two operands are of same type.
return|return
name|SqlTypeStrategies
operator|.
name|otcSameX2
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
block|}
block|}
end_class

begin_comment
comment|// End SqlPositionFunction.java
end_comment

end_unit

