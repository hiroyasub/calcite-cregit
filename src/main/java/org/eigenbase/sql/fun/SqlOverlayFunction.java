begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
comment|/**  * The<code>OVERLAY</code> function.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlOverlayFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|otcCustom
init|=
operator|new
name|CompositeOperandTypeChecker
argument_list|(
name|CompositeOperandTypeChecker
operator|.
name|Composition
operator|.
name|OR
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcStringX2Int
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcStringX2IntX2
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlOverlayFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"OVERLAY"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiNullableVaryingDyadicStringSumPrecision
argument_list|,
literal|null
argument_list|,
name|otcCustom
argument_list|,
name|SqlFunctionCategory
operator|.
name|String
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
literal|"PLACING"
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
name|sep
argument_list|(
literal|"FROM"
argument_list|)
expr_stmt|;
name|operands
index|[
literal|2
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
if|if
condition|(
literal|4
operator|==
name|operands
operator|.
name|length
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"FOR"
argument_list|)
expr_stmt|;
name|operands
index|[
literal|3
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
literal|3
case|:
return|return
literal|"{0}({1} PLACING {2} FROM {3})"
return|;
case|case
literal|4
case|:
return|return
literal|"{0}({1} PLACING {2} FROM {3} FOR {4})"
return|;
block|}
assert|assert
operator|(
literal|false
operator|)
assert|;
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlOverlayFunction.java
end_comment

end_unit

