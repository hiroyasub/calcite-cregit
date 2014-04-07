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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|RelDataType
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
name|SqlOperandTypeChecker
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
name|SqlOperandTypeInference
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
name|SqlReturnTypeInference
import|;
end_import

begin_comment
comment|/**  * Placeholder for an unresolved function.  *  *<p>Created by the parser, then it is rewritten to proper SqlFunction by  * the validator to a function defined in an Optiq schema.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlUnresolvedFunction
extends|extends
name|SqlFunction
block|{
comment|/**    * Creates a placeholder SqlUnresolvedFunction for an invocation of a function    * with a possibly qualified name. This name must be resolved into either    * a builtin function or a user-defined function.    *    * @param sqlIdentifier        possibly qualified identifier for function    * @param returnTypeInference  strategy to use for return type inference    * @param operandTypeInference strategy to use for parameter type inference    * @param operandTypeChecker   strategy to use for parameter type checking    * @param paramTypes           array of parameter types    * @param funcType             function category    */
specifier|public
name|SqlUnresolvedFunction
parameter_list|(
name|SqlIdentifier
name|sqlIdentifier
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
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|super
argument_list|(
name|sqlIdentifier
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|paramTypes
argument_list|,
name|funcType
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUnresolvedFunction.java
end_comment

end_unit

