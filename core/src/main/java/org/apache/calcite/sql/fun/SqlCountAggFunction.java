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
name|fun
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
name|config
operator|.
name|CalciteSystemProperty
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
name|sql
operator|.
name|SqlAggFunction
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
name|SqlSplittableAggFunction
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
name|SqlSyntax
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
name|OperandTypes
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
name|ReturnTypes
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
name|SqlTypeName
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
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorScope
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
name|Optionality
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
name|ImmutableList
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
name|List
import|;
end_import

begin_comment
comment|/**  * Definition of the SQL<code>COUNT</code> aggregation function.  *  *<p><code>COUNT</code> is an aggregator which returns the number of rows which  * have gone into it. With one argument (or more), it returns the number of rows  * for which that argument (or all) is not<code>null</code>.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCountAggFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCountAggFunction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|CalciteSystemProperty
operator|.
name|STRICT
operator|.
name|value
argument_list|()
condition|?
name|OperandTypes
operator|.
name|ANY
else|:
name|OperandTypes
operator|.
name|ONE_OR_MORE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlCountAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlOperandTypeChecker
name|sqlOperandTypeChecker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|SqlKind
operator|.
name|COUNT
argument_list|,
name|ReturnTypes
operator|.
name|BIGINT
argument_list|,
literal|null
argument_list|,
name|sqlOperandTypeChecker
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|FUNCTION_STAR
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
name|RelDataType
argument_list|>
name|getParameterTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
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
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Check for COUNT(*) function.  If it is we don't
comment|// want to try and derive the "*"
if|if
condition|(
name|call
operator|.
name|isCountStar
argument_list|()
condition|)
block|{
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
annotation|@
name|Nullable
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
name|SqlSplittableAggFunction
operator|.
name|class
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|CountSplitter
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|unwrap
argument_list|(
name|clazz
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlAggFunction
name|getRollup
parameter_list|()
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|SUM0
return|;
block|}
block|}
end_class

end_unit

