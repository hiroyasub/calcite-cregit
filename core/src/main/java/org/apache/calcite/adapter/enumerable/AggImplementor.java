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
name|adapter
operator|.
name|enumerable
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
name|tree
operator|.
name|Expression
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
comment|/**  * Implements an aggregate function by generating expressions to  * initialize, add to, and get a result from, an accumulator.  *  * @see org.apache.calcite.adapter.enumerable.StrictAggImplementor  * @see org.apache.calcite.adapter.enumerable.StrictWinAggImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.CountImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.SumImplementor  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggImplementor
block|{
comment|/**    * Returns the types of the intermediate variables used by the aggregate    * implementation.    *    *<p>For instance, for "concatenate to string" this can be    * {@link java.lang.StringBuilder}.    * Calcite calls this method before all other {@code implement*} methods.    *    * @param info Aggregate context    * @return Types of the intermediate variables used by the aggregate    *   implementation    */
name|List
argument_list|<
name|Type
argument_list|>
name|getStateType
parameter_list|(
name|AggContext
name|info
parameter_list|)
function_decl|;
comment|/**    * Implements reset of the intermediate variables to the initial state.    * {@link AggResetContext#accumulator()} should be used to reference    * the state variables.    * For instance, to zero the count, use the following code:    *    *<blockquote><code>reset.currentBlock().add(<br>    *   Expressions.statement(<br>    *     Expressions.assign(reset.accumulator().get(0),<br>    *       Expressions.constant(0)));</code></blockquote>    *    * @param info Aggregate context    * @param reset Reset context    */
name|void
name|implementReset
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResetContext
name|reset
parameter_list|)
function_decl|;
comment|/**    * Updates intermediate values to account for the newly added value.    * {@link AggResetContext#accumulator()} should be used to reference    * the state variables.    *    * @param info Aggregate context    * @param add Add context    */
name|void
name|implementAdd
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggAddContext
name|add
parameter_list|)
function_decl|;
comment|/**    * Calculates the resulting value based on the intermediate variables.    * Note: this method must NOT destroy the intermediate variables as    * calcite might reuse the state when calculating sliding aggregates.    * {@link AggResetContext#accumulator()} should be used to reference    * the state variables.    *    * @param info Aggregate context    * @param result Result context    * @return Expression that is a result of calculating final value of    *   the aggregate being implemented    */
name|Expression
name|implementResult
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResultContext
name|result
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

