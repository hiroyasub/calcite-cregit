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
name|validate
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Function
import|;
end_import

begin_comment
comment|/** * User-defined scalar function.  *  *<p>Created by the validator, after resolving a function call to a function  * defined in an Optiq schema.</p> */
end_comment

begin_class
specifier|public
class|class
name|SqlUserDefinedFunction
extends|extends
name|SqlFunction
block|{
specifier|public
specifier|final
name|Function
name|function
decl_stmt|;
specifier|public
name|SqlUserDefinedFunction
parameter_list|(
name|SqlIdentifier
name|opName
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
name|Function
name|function
parameter_list|)
block|{
name|super
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|opName
operator|.
name|names
argument_list|)
argument_list|,
name|opName
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|paramTypes
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
comment|/**    * Returns function that implements given operator call.    * @return function that implements given operator call    */
specifier|public
name|Function
name|getFunction
parameter_list|()
block|{
return|return
name|function
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUserDefinedFunction.java
end_comment

end_unit

