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
name|SqlMonotonicity
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
name|base
operator|.
name|Preconditions
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * SQL function that computes keys by which rows can be partitioned and  * aggregated.  *  *<p>Grouped window functions always occur in the GROUP BY clause. They often  * have auxiliary functions that access information about the group. For  * example, {@code HOP} is a group function, and its auxiliary functions are  * {@code HOP_START} and {@code HOP_END}. Here they are used in a streaming  * query:  *  *<blockquote><pre>  * SELECT STREAM HOP_START(rowtime, INTERVAL '1' HOUR),  *   HOP_END(rowtime, INTERVAL '1' HOUR),  *   MIN(unitPrice)  * FROM Orders  * GROUP BY HOP(rowtime, INTERVAL '1' HOUR), productId  *</pre></blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|SqlGroupedWindowFunction
extends|extends
name|SqlFunction
block|{
comment|/** The grouped function, if this an auxiliary function; null otherwise. */
specifier|public
specifier|final
name|SqlGroupedWindowFunction
name|groupFunction
decl_stmt|;
comment|/** Creates a SqlGroupedWindowFunction.    *    * @param name Function name    * @param kind Kind    * @param groupFunction Group function, if this is an auxiliary;    *                      null, if this is a group function    * @param returnTypeInference  Strategy to use for return type inference    * @param operandTypeInference Strategy to use for parameter type inference    * @param operandTypeChecker   Strategy to use for parameter type checking    * @param category             Categorization for function    */
specifier|public
name|SqlGroupedWindowFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlGroupedWindowFunction
name|groupFunction
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
name|category
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|category
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupFunction
operator|=
name|groupFunction
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|groupFunction
operator|==
literal|null
operator|||
name|groupFunction
operator|.
name|groupFunction
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SqlGroupedWindowFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlGroupedWindowFunction
name|groupFunction
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|groupFunction
argument_list|,
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SqlGroupedWindowFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|SqlGroupedWindowFunction
name|groupFunction
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|this
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|,
name|groupFunction
argument_list|,
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an auxiliary function from this grouped window function.    *    * @param kind Kind; also determines function name    */
specifier|public
name|SqlGroupedWindowFunction
name|auxiliary
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
name|auxiliary
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|)
return|;
block|}
comment|/** Creates an auxiliary function from this grouped window function.    *    * @param name Function name    * @param kind Kind    */
specifier|public
name|SqlGroupedWindowFunction
name|auxiliary
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
operator|new
name|SqlGroupedWindowFunction
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|this
argument_list|,
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
literal|null
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
return|;
block|}
comment|/** Returns a list of this grouped window function's auxiliary functions. */
specifier|public
name|List
argument_list|<
name|SqlGroupedWindowFunction
argument_list|>
name|getAuxiliaryFunctions
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
comment|// Auxiliary functions are not group functions
return|return
name|groupFunction
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroupAuxiliary
parameter_list|()
block|{
return|return
name|groupFunction
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlOperatorBinding
name|call
parameter_list|)
block|{
comment|// Monotonic iff its first argument is, but not strict.
comment|//
comment|// Note: This strategy happens to works for all current group functions
comment|// (HOP, TUMBLE, SESSION). When there are exceptions to this rule, we'll
comment|// make the method abstract.
return|return
name|call
operator|.
name|getOperandMonotonicity
argument_list|(
literal|0
argument_list|)
operator|.
name|unstrict
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
comment|// Always rename the name of the SqlKind. The tumble operator is called
comment|// "$TUMBLE", so that it does not clash with a user-defined table function
comment|// "TUMBLE", but we want the name to be "TUMBLE".
return|return
name|getKind
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
block|}
end_class

end_unit

