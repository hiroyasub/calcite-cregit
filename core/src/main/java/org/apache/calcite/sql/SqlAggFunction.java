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
name|plan
operator|.
name|Context
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
name|fun
operator|.
name|SqlBasicAggFunction
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
comment|/**  * Abstract base class for the definition of an aggregate function: an operator  * which aggregates sets of values into a result.  *  * @see SqlBasicAggFunction  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlAggFunction
extends|extends
name|SqlFunction
implements|implements
name|Context
block|{
specifier|private
specifier|final
name|boolean
name|requiresOrder
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|requiresOver
decl_stmt|;
specifier|private
specifier|final
name|Optionality
name|requiresGroupOrder
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a built-in SqlAggFunction. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|SqlAggFunction
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
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
comment|// We leave sqlIdentifier as null to indicate that this is a builtin.
name|this
argument_list|(
name|name
argument_list|,
literal|null
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
comment|/** Creates a user-defined SqlAggFunction. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|SqlAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|sqlIdentifier
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|SqlAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
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
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|sqlIdentifier
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
argument_list|,
name|requiresOrder
argument_list|,
name|requiresOver
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a built-in or user-defined SqlAggFunction or window function.    *    *<p>A user-defined function will have a value for {@code sqlIdentifier}; for    * a built-in function it will be null. */
specifier|protected
name|SqlAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
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
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|funcType
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiresOrder
operator|=
name|requiresOrder
expr_stmt|;
name|this
operator|.
name|requiresOver
operator|=
name|requiresOver
expr_stmt|;
name|this
operator|.
name|requiresGroupOrder
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|requiresGroupOrder
argument_list|,
literal|"requiresGroupOrder"
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|?
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAggregator
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQuantifierAllowed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
name|super
operator|.
name|validateCall
argument_list|(
name|call
argument_list|,
name|validator
argument_list|,
name|scope
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
name|validator
operator|.
name|validateAggregateParams
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|requiresOrder
parameter_list|()
block|{
return|return
name|requiresOrder
return|;
block|}
comment|/** Returns whether this aggregate function must, may, or must not contain a    * {@code WITHIN GROUP (ORDER ...)} clause.    *    *<p>Cases:<ul>    *    *<li>If {@link Optionality#MANDATORY},    * then {@code AGG(x) WITHIN GROUP (ORDER BY 1)} is valid,    * and {@code AGG(x)} is invalid.    *    *<li>If {@link Optionality#OPTIONAL},    * then {@code AGG(x) WITHIN GROUP (ORDER BY 1)}    * and {@code AGG(x)} are both valid.    *    *<li>If {@link Optionality#IGNORED},    * then {@code AGG(x)} is valid,    * and {@code AGG(x) WITHIN GROUP (ORDER BY 1)} is valid but is    * treated the same as {@code AGG(x)}.    *    *<li>If {@link Optionality#FORBIDDEN},    * then {@code AGG(x) WITHIN GROUP (ORDER BY 1)} is invalid,    * and {@code AGG(x)} is valid.    *</ul>    */
specifier|public
name|Optionality
name|requiresGroupOrder
parameter_list|()
block|{
return|return
name|requiresGroupOrder
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|requiresOver
parameter_list|()
block|{
return|return
name|requiresOver
return|;
block|}
comment|/** Returns whether this aggregate function allows the {@code DISTINCT}    * keyword.    *    *<p>The default implementation returns {@link Optionality#OPTIONAL},    * which is appropriate for most aggregate functions, including {@code SUM}    * and {@code COUNT}.    *    *<p>Some aggregate functions, for example {@code MIN}, produce the same    * result with or without {@code DISTINCT}, and therefore return    * {@link Optionality#IGNORED} to indicate this. For such functions,    * Calcite will probably remove {@code DISTINCT} while optimizing the query.    */
specifier|public
name|Optionality
name|getDistinctOptionality
parameter_list|()
block|{
return|return
name|Optionality
operator|.
name|OPTIONAL
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove before calcite-2.0"
argument_list|)
throw|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove before calcite-2.0"
argument_list|)
throw|;
block|}
comment|/** Whether this aggregate function allows a {@code FILTER (WHERE ...)}    * clause. */
specifier|public
name|boolean
name|allowsFilter
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Returns whether this aggregate function allows specifying null treatment    * ({@code RESPECT NULLS} or {@code IGNORE NULLS}). */
specifier|public
name|boolean
name|allowsNullTreatment
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Gets rollup aggregation function.    */
specifier|public
annotation|@
name|Nullable
name|SqlAggFunction
name|getRollup
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

