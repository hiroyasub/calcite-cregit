begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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

begin_comment
comment|/**  * Base class for unary operators such as FLOOR/CEIL which are monotonic for  * monotonic inputs.  */
end_comment

begin_class
specifier|public
class|class
name|SqlMonotonicUnaryFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlMonotonicUnaryFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|funcType
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
return|return
name|scope
operator|.
name|getMonotonicity
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|unstrict
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlMonotonicUnaryFunction.java
end_comment

end_unit

