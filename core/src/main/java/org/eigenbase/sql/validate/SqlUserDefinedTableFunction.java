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
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|reltype
operator|.
name|RelDataTypeFactory
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
name|SqlIdentifier
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
name|SqlNode
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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|TableFunction
import|;
end_import

begin_comment
comment|/**  * User-defined table function.  *<p>  * Created by the validator, after resolving a function call to a function  * defined in an Optiq schema. */
end_comment

begin_class
specifier|public
class|class
name|SqlUserDefinedTableFunction
extends|extends
name|SqlUserDefinedFunction
block|{
specifier|public
name|SqlUserDefinedTableFunction
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
name|TableFunction
name|function
parameter_list|)
block|{
name|super
argument_list|(
name|opName
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|paramTypes
argument_list|,
name|function
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns function that implements given operator call.    * @return function that implements given operator call    */
specifier|public
name|TableFunction
name|getFunction
parameter_list|()
block|{
return|return
operator|(
name|TableFunction
operator|)
name|super
operator|.
name|getFunction
argument_list|()
return|;
block|}
comment|/**    * Returns the record type of the table yielded by this function when    * applied to given arguments. Only literal arguments are passed,    * non-literal are replaced with default values (null, 0, false, etc).    *    * @param typeFactory Type factory    * @param operandList arguments of a function call (only literal arguments    *                    are passed, nulls for non-literal ones)    * @return row type of the table    */
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
init|=
name|SqlUserDefinedTableMacro
operator|.
name|convertArguments
argument_list|(
name|typeFactory
argument_list|,
name|operandList
argument_list|,
name|function
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|getFunction
argument_list|()
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|,
name|arguments
argument_list|)
return|;
block|}
comment|/**    * Returns the row type of the table yielded by this function when    * applied to given arguments. Only literal arguments are passed,    * non-literal are replaced with default values (null, 0, false, etc).    *    * @param operandList arguments of a function call (only literal arguments    *                    are passed, nulls for non-literal ones)    * @return element type of the table (e.g. {@code Object[].class})    */
specifier|public
name|Type
name|getElementType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
init|=
name|SqlUserDefinedTableMacro
operator|.
name|convertArguments
argument_list|(
name|typeFactory
argument_list|,
name|operandList
argument_list|,
name|function
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|getFunction
argument_list|()
operator|.
name|getElementType
argument_list|(
name|arguments
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUserDefinedTableFunction.java
end_comment

end_unit

