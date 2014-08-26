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
name|reltype
operator|.
name|RelDataTypeFactoryImpl
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
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|linq4j
operator|.
name|expressions
operator|.
name|*
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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|FunctionParameter
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
name|TableMacro
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
name|TranslatableTable
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
name|rules
operator|.
name|java
operator|.
name|RexToLixTranslator
import|;
end_import

begin_comment
comment|/**  * User-defined table macro.  *<p>  * Created by the validator, after resolving a function call to a function  * defined in an Optiq schema. */
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
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableMacro
operator|=
name|tableMacro
expr_stmt|;
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
comment|/**    * Converts arguments from {@link org.eigenbase.sql.SqlNode} to java object    * format.    * @param typeFactory type factory used to convert the arguments    * @param operandList input arguments    * @param function target function to get parameter types from    * @param opName name of the operator to use in error message    * @param failOnNonLiteral true when conversion should fail on non-literal    * @return converted list of arguments    */
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
argument_list|<
name|Object
argument_list|>
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
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|pair
operator|.
name|right
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|arguments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|SqlUtil
operator|.
name|isLiteral
argument_list|(
name|pair
operator|.
name|right
argument_list|)
condition|)
block|{
specifier|final
name|Object
name|o
init|=
operator|(
operator|(
name|SqlLiteral
operator|)
name|pair
operator|.
name|right
operator|)
operator|.
name|getValue
argument_list|()
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
else|else
block|{
name|arguments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
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
literal|" should be "
operator|+
literal|"literal. Actual argument #"
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
block|}
block|}
return|return
name|arguments
return|;
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
name|clazz
operator|==
name|String
operator|.
name|class
operator|&&
name|o
operator|instanceof
name|NlsString
condition|)
block|{
return|return
operator|(
operator|(
name|NlsString
operator|)
name|o
operator|)
operator|.
name|getValue
argument_list|()
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
name|RexToLixTranslator
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
expr|<
name|ParameterExpression
operator|>
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
block|}
end_class

begin_comment
comment|// End SqlUserDefinedTableMacro.java
end_comment

end_unit

