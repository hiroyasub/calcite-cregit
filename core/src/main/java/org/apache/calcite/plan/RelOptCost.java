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
name|plan
package|;
end_package

begin_comment
comment|/**  * RelOptCost defines an interface for optimizer cost in terms of number of rows  * processed, CPU cost, and I/O cost. Optimizer implementations may use all of  * this information, or selectively ignore portions of it. The specific units  * for all of these quantities are rather vague; most relational expressions  * provide a default cost calculation, but optimizers can override this by  * plugging in their own cost models with well-defined meanings for each unit.  * Optimizers which supply their own cost models may also extend this interface  * with additional cost metrics such as memory usage.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptCost
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return number of rows processed; this should not be confused with the    * row count produced by a relational expression    * ({@link org.apache.calcite.rel.RelNode#estimateRowCount})    */
name|double
name|getRows
parameter_list|()
function_decl|;
comment|/**    * @return usage of CPU resources    */
name|double
name|getCpu
parameter_list|()
function_decl|;
comment|/**    * @return usage of I/O resources    */
name|double
name|getIo
parameter_list|()
function_decl|;
comment|/**    * @return true iff this cost represents an expression that hasn't actually    * been implemented (e.g. a pure relational algebra expression) or can't    * actually be implemented, e.g. a transfer of data between two disconnected    * sites    */
name|boolean
name|isInfinite
parameter_list|()
function_decl|;
comment|// REVIEW jvs 3-Apr-2006:  we should standardize this
comment|// to Comparator/equals/hashCode
comment|/**    * Compares this to another cost.    *    * @param cost another cost    * @return true iff this is exactly equal to other cost    */
name|boolean
name|equals
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Compares this to another cost, allowing for slight roundoff errors.    *    * @param cost another cost    * @return true iff this is the same as the other cost within a roundoff    * margin of error    */
name|boolean
name|isEqWithEpsilon
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Compares this to another cost.    *    * @param cost another cost    * @return true iff this is less than or equal to other cost    */
name|boolean
name|isLe
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Compares this to another cost.    *    * @param cost another cost    * @return true iff this is strictly less than other cost    */
name|boolean
name|isLt
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Adds another cost to this.    *    * @param cost another cost    * @return sum of this and other cost    */
name|RelOptCost
name|plus
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Subtracts another cost from this.    *    * @param cost another cost    * @return difference between this and other cost    */
name|RelOptCost
name|minus
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Multiplies this cost by a scalar factor.    *    * @param factor scalar factor    * @return scalar product of this and factor    */
name|RelOptCost
name|multiplyBy
parameter_list|(
name|double
name|factor
parameter_list|)
function_decl|;
comment|/**    * Computes the ratio between this cost and another cost.    *    *<p>divideBy is the inverse of {@link #multiplyBy(double)}. For any    * finite, non-zero cost and factor f,<code>    * cost.divideBy(cost.multiplyBy(f))</code> yields<code>1 / f</code>.    *    * @param cost Other cost    * @return Ratio between costs    */
name|double
name|divideBy
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
function_decl|;
comment|/**    * Forces implementations to override {@link Object#toString} and provide a    * good cost rendering to use during tracing.    */
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptCost.java
end_comment

end_unit

