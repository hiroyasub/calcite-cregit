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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Concrete implementation of {@link SqlAggFunction}.  *  *<p>The class is final, and instances are immutable.  *  *<p>Instances are created only by {@link SqlBasicAggFunction#create} and are  * "modified" by "wither" methods such as {@link #withDistinct} to create a new  * instance with one property changed. Since the class is final, you can modify  * behavior only by providing strategy objects, not by overriding methods in a  * sub-class.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SqlBasicAggFunction
extends|extends
name|SqlAggFunction
block|{
specifier|private
specifier|final
name|Optionality
name|distinctOptionality
decl_stmt|;
specifier|private
specifier|final
name|SqlSyntax
name|syntax
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|allowsNullTreatment
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|SqlBasicAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlIdentifier
name|sqlIdentifier
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
parameter_list|,
name|boolean
name|requiresOrder
parameter_list|,
name|boolean
name|requiresOver
parameter_list|,
name|Optionality
name|requiresGroupOrder
parameter_list|,
name|Optionality
name|distinctOptionality
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|boolean
name|allowsNullTreatment
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|sqlIdentifier
argument_list|,
name|kind
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|returnTypeInference
argument_list|)
argument_list|,
name|operandTypeInference
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|operandTypeChecker
argument_list|)
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|funcType
argument_list|)
argument_list|,
name|requiresOrder
argument_list|,
name|requiresOver
argument_list|,
name|requiresGroupOrder
argument_list|)
expr_stmt|;
name|this
operator|.
name|distinctOptionality
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|distinctOptionality
argument_list|)
expr_stmt|;
name|this
operator|.
name|syntax
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|syntax
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowsNullTreatment
operator|=
name|allowsNullTreatment
expr_stmt|;
block|}
comment|/** Creates a SqlBasicAggFunction whose name is the same as its kind. */
specifier|public
specifier|static
name|SqlBasicAggFunction
name|create
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
return|;
block|}
comment|/** Creates a SqlBasicAggFunction. */
specifier|public
specifier|static
name|SqlBasicAggFunction
name|create
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
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
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
argument_list|,
name|Optionality
operator|.
name|OPTIONAL
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
if|if
condition|(
name|syntax
operator|==
name|SqlSyntax
operator|.
name|ORDERED_FUNCTION
condition|)
block|{
name|call
operator|=
name|ReturnTypes
operator|.
name|stripOrderBy
argument_list|(
name|call
argument_list|)
expr_stmt|;
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
annotation|@
name|Nonnull
specifier|public
name|Optionality
name|getDistinctOptionality
parameter_list|()
block|{
return|return
name|distinctOptionality
return|;
block|}
comment|/** Sets {@link #getDistinctOptionality()}. */
name|SqlBasicAggFunction
name|withDistinct
parameter_list|(
annotation|@
name|Nonnull
name|Optionality
name|distinctOptionality
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getSqlIdentifier
argument_list|()
argument_list|,
name|kind
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|requiresOrder
argument_list|()
argument_list|,
name|requiresOver
argument_list|()
argument_list|,
name|requiresGroupOrder
argument_list|()
argument_list|,
name|distinctOptionality
argument_list|,
name|syntax
argument_list|,
name|allowsNullTreatment
argument_list|)
return|;
block|}
comment|/** Sets {@link #getFunctionType()}. */
specifier|public
name|SqlBasicAggFunction
name|withFunctionType
parameter_list|(
name|SqlFunctionCategory
name|category
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getSqlIdentifier
argument_list|()
argument_list|,
name|kind
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|category
argument_list|,
name|requiresOrder
argument_list|()
argument_list|,
name|requiresOver
argument_list|()
argument_list|,
name|requiresGroupOrder
argument_list|()
argument_list|,
name|distinctOptionality
argument_list|,
name|syntax
argument_list|,
name|allowsNullTreatment
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|syntax
return|;
block|}
comment|/** Sets {@link #getSyntax()}. */
specifier|public
name|SqlBasicAggFunction
name|withSyntax
parameter_list|(
name|SqlSyntax
name|syntax
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getSqlIdentifier
argument_list|()
argument_list|,
name|kind
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|requiresOrder
argument_list|()
argument_list|,
name|requiresOver
argument_list|()
argument_list|,
name|requiresGroupOrder
argument_list|()
argument_list|,
name|distinctOptionality
argument_list|,
name|syntax
argument_list|,
name|allowsNullTreatment
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowsNullTreatment
parameter_list|()
block|{
return|return
name|allowsNullTreatment
return|;
block|}
comment|/** Sets {@link #allowsNullTreatment()}. */
specifier|public
name|SqlBasicAggFunction
name|withAllowsNullTreatment
parameter_list|(
name|boolean
name|allowsNullTreatment
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getSqlIdentifier
argument_list|()
argument_list|,
name|kind
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|requiresOrder
argument_list|()
argument_list|,
name|requiresOver
argument_list|()
argument_list|,
name|requiresGroupOrder
argument_list|()
argument_list|,
name|distinctOptionality
argument_list|,
name|syntax
argument_list|,
name|allowsNullTreatment
argument_list|)
return|;
block|}
comment|/** Sets {@link #requiresGroupOrder()}. */
specifier|public
name|SqlBasicAggFunction
name|withGroupOrder
parameter_list|(
name|Optionality
name|groupOrder
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicAggFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getSqlIdentifier
argument_list|()
argument_list|,
name|kind
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|requiresOrder
argument_list|()
argument_list|,
name|requiresOver
argument_list|()
argument_list|,
name|groupOrder
argument_list|,
name|distinctOptionality
argument_list|,
name|syntax
argument_list|,
name|allowsNullTreatment
argument_list|)
return|;
block|}
block|}
end_class

end_unit

