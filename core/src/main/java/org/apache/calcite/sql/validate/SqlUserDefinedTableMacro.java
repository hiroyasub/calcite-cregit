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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|Function
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
name|FunctionParameter
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
name|TableMacro
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
name|TranslatableTable
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * User-defined table macro.  *  *<p>Created by the validator, after resolving a function call to a function  * defined in a Calcite schema. */
end_comment

begin_class
specifier|public
class|class
name|SqlUserDefinedTableMacro
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
specifier|private
specifier|final
name|TableMacro
name|tableMacro
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SqlUserDefinedTableMacro
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
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|,
name|TableMacro
name|tableMacro
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
name|tableMacro
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|paramTypes
argument_list|)
expr_stmt|;
comment|// no longer used
block|}
comment|/** Creates a user-defined table macro. */
specifier|public
name|SqlUserDefinedTableMacro
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
annotation|@
name|Nullable
name|SqlOperandMetadata
name|operandMetadata
parameter_list|,
name|TableMacro
name|tableMacro
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
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandMetadata
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableMacro
operator|=
name|tableMacro
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlOperandMetadata
name|getOperandTypeChecker
parameter_list|()
block|{
return|return
operator|(
expr|@
name|Nullable
name|SqlOperandMetadata
operator|)
name|super
operator|.
name|getOperandTypeChecker
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getParamNames
parameter_list|()
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|tableMacro
operator|.
name|getParameters
argument_list|()
argument_list|,
name|FunctionParameter
operator|::
name|getName
argument_list|)
return|;
block|}
comment|/** Returns the table in this UDF, or null if there is no table. */
specifier|public
name|TranslatableTable
name|getTable
parameter_list|(
name|SqlOperatorBinding
name|callBinding
parameter_list|)
block|{
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|arguments
init|=
name|convertArguments
argument_list|(
name|callBinding
argument_list|,
name|tableMacro
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|tableMacro
operator|.
name|apply
argument_list|(
name|arguments
argument_list|)
return|;
block|}
comment|/**    * Converts arguments from {@link org.apache.calcite.sql.SqlNode} to    * java object format.    *    * @param callBinding Operator bound to arguments    * @param function target function to get parameter types from    * @param opName name of the operator to use in error message    * @param failOnNonLiteral true when conversion should fail on non-literal    * @return converted list of arguments    */
specifier|static
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|convertArguments
parameter_list|(
name|SqlOperatorBinding
name|callBinding
parameter_list|,
name|Function
name|function
parameter_list|,
name|SqlIdentifier
name|opName
parameter_list|,
name|boolean
name|failOnNonLiteral
parameter_list|)
block|{
name|RelDataTypeFactory
name|typeFactory
init|=
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|arguments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
decl_stmt|;
name|Ord
operator|.
name|forEach
argument_list|(
name|function
operator|.
name|getParameters
argument_list|()
argument_list|,
parameter_list|(
name|parameter
parameter_list|,
name|i
parameter_list|)
lambda|->
block|{
specifier|final
name|RelDataType
name|type
init|=
name|parameter
operator|.
name|getType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|value
decl_stmt|;
if|if
condition|(
name|callBinding
operator|.
name|isOperandLiteral
argument_list|(
name|i
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|value
operator|=
name|callBinding
operator|.
name|getOperandLiteralValue
argument_list|(
name|i
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|failOnNonLiteral
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All arguments of call to macro "
operator|+
name|opName
operator|+
literal|" should be literal. Actual argument #"
operator|+
name|parameter
operator|.
name|getOrdinal
argument_list|()
operator|+
literal|" ("
operator|+
name|parameter
operator|.
name|getName
argument_list|()
operator|+
literal|") is not literal"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|0L
expr_stmt|;
block|}
block|}
name|arguments
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|arguments
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
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|TranslatableTable
name|table
init|=
name|getTable
argument_list|(
name|callBinding
argument_list|)
decl_stmt|;
return|return
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
block|}
end_class

end_unit

