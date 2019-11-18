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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|linq4j
operator|.
name|tree
operator|.
name|FunctionExpression
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactoryImpl
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
name|SqlLiteral
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
name|SqlNode
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
name|SqlUtil
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
name|ImmutableNullableList
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
name|NlsString
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
name|Pair
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Objects
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
block|{
specifier|private
specifier|final
name|TableMacro
name|tableMacro
decl_stmt|;
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|paramTypes
argument_list|)
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
name|List
argument_list|<
name|String
argument_list|>
name|getParamNames
parameter_list|()
block|{
return|return
name|Lists
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
name|convertArguments
argument_list|(
name|typeFactory
argument_list|,
name|operandList
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
comment|/**    * Converts arguments from {@link org.apache.calcite.sql.SqlNode} to    * java object format.    *    * @param typeFactory type factory used to convert the arguments    * @param operandList input arguments    * @param function target function to get parameter types from    * @param opName name of the operator to use in error message    * @param failOnNonLiteral true when conversion should fail on non-literal    * @return converted list of arguments    */
specifier|public
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|convertArguments
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
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
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|operandList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// Construct a list of arguments, if they are all constants.
for|for
control|(
name|Pair
argument_list|<
name|FunctionParameter
argument_list|,
name|SqlNode
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|function
operator|.
name|getParameters
argument_list|()
argument_list|,
name|operandList
argument_list|)
control|)
block|{
try|try
block|{
specifier|final
name|Object
name|o
init|=
name|getValue
argument_list|(
name|pair
operator|.
name|right
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|o2
init|=
name|coerce
argument_list|(
name|o
argument_list|,
name|pair
operator|.
name|left
operator|.
name|getType
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
decl_stmt|;
name|arguments
operator|.
name|add
argument_list|(
name|o2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NonLiteralException
name|e
parameter_list|)
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
name|pair
operator|.
name|left
operator|.
name|getOrdinal
argument_list|()
operator|+
literal|" ("
operator|+
name|pair
operator|.
name|left
operator|.
name|getName
argument_list|()
operator|+
literal|") is not literal: "
operator|+
name|pair
operator|.
name|right
argument_list|)
throw|;
block|}
specifier|final
name|RelDataType
name|type
init|=
name|pair
operator|.
name|left
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
name|arguments
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|arguments
return|;
block|}
specifier|private
specifier|static
name|Object
name|getValue
parameter_list|(
name|SqlNode
name|right
parameter_list|)
throws|throws
name|NonLiteralException
block|{
switch|switch
condition|(
name|right
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|ARRAY_VALUE_CONSTRUCTOR
case|:
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlNode
name|o
range|:
operator|(
operator|(
name|SqlCall
operator|)
name|right
operator|)
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getValue
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableNullableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
return|;
case|case
name|MAP_VALUE_CONSTRUCTOR
case|:
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|builder2
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|SqlCall
operator|)
name|right
operator|)
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operands
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
specifier|final
name|SqlNode
name|key
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|value
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|builder2
operator|.
name|put
argument_list|(
name|getValue
argument_list|(
name|key
argument_list|)
argument_list|,
name|getValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder2
operator|.
name|build
argument_list|()
return|;
case|case
name|CAST
case|:
return|return
name|getValue
argument_list|(
operator|(
operator|(
name|SqlCall
operator|)
name|right
operator|)
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
default|default:
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|right
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|SqlUtil
operator|.
name|isLiteral
argument_list|(
name|right
argument_list|)
condition|)
block|{
return|return
operator|(
operator|(
name|SqlLiteral
operator|)
name|right
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
if|if
condition|(
name|right
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|DEFAULT
condition|)
block|{
return|return
literal|null
return|;
comment|// currently NULL is the only default value
block|}
throw|throw
operator|new
name|NonLiteralException
argument_list|()
throw|;
block|}
block|}
specifier|private
specifier|static
name|Object
name|coerce
parameter_list|(
name|Object
name|o
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
name|javaType
init|=
operator|(
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
name|type
decl_stmt|;
specifier|final
name|Class
name|clazz
init|=
name|javaType
operator|.
name|getJavaClass
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
if|if
condition|(
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|o
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|NlsString
condition|)
block|{
return|return
name|coerce
argument_list|(
operator|(
operator|(
name|NlsString
operator|)
name|o
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|// We need optimization here for constant folding.
comment|// Not all the expressions can be interpreted (e.g. ternary), so
comment|// we rely on optimization capabilities to fold non-interpretable
comment|// expressions.
name|BlockBuilder
name|bb
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Expression
name|expr
init|=
name|EnumUtils
operator|.
name|convert
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|o
argument_list|)
argument_list|,
name|clazz
argument_list|)
decl_stmt|;
name|bb
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|expr
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FunctionExpression
name|convert
init|=
name|Expressions
operator|.
name|lambda
argument_list|(
name|bb
operator|.
name|toBlock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|convert
operator|.
name|compile
argument_list|()
operator|.
name|dynamicInvoke
argument_list|()
return|;
block|}
comment|/** Thrown when a non-literal occurs in an argument to a user-defined    * table macro. */
specifier|private
specifier|static
class|class
name|NonLiteralException
extends|extends
name|Exception
block|{   }
block|}
end_class

begin_comment
comment|// End SqlUserDefinedTableMacro.java
end_comment

end_unit

