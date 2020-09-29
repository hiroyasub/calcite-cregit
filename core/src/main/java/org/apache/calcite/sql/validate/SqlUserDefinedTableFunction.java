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
name|validate
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
name|schema
operator|.
name|TableFunction
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
name|SqlIdentifier
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
name|SqlTableFunction
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
name|SqlOperandMetadata
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
name|util
operator|.
name|Util
import|;
end_import

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

begin_comment
comment|/**  * User-defined table function.  *  *<p>Created by the validator, after resolving a function call to a function  * defined in a Calcite schema. */
end_comment

begin_class
specifier|public
class|class
name|SqlUserDefinedTableFunction
extends|extends
name|SqlUserDefinedFunction
implements|implements
name|SqlTableFunction
block|{
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
comment|// no longer used
name|TableFunction
name|function
parameter_list|)
block|{
name|this
argument_list|(
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
operator|instanceof
name|SqlOperandMetadata
condition|?
operator|(
name|SqlOperandMetadata
operator|)
name|operandTypeChecker
else|:
literal|null
argument_list|,
name|function
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|paramTypes
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a user-defined table function. */
specifier|public
name|SqlUserDefinedTableFunction
parameter_list|(
name|SqlIdentifier
name|opName
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
name|SqlOperandMetadata
name|operandMetadata
parameter_list|,
name|TableFunction
name|function
parameter_list|)
block|{
name|super
argument_list|(
name|opName
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandMetadata
argument_list|,
name|function
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns function that implements given operator call.    * @return function that implements given operator call    */
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|this
operator|::
name|inferRowType
return|;
block|}
specifier|private
name|RelDataType
name|inferRowType
parameter_list|(
name|SqlOperatorBinding
name|callBinding
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
name|callBinding
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
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|arguments
argument_list|)
return|;
block|}
comment|/**    * Returns the row type of the table yielded by this function when    * applied to given arguments. Only literal arguments are passed,    * non-literal are replaced with default values (null, 0, false, etc).    *    * @param callBinding Operand bound to arguments    * @return element type of the table (e.g. {@code Object[].class})    */
specifier|public
name|Type
name|getElementType
parameter_list|(
name|SqlOperatorBinding
name|callBinding
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
name|callBinding
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

end_unit

