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
comment|/**  * Cost model for query planning.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptCostFactory
block|{
comment|/**    * Creates a cost object.    */
name|RelOptCost
name|makeCost
parameter_list|(
name|double
name|rowCount
parameter_list|,
name|double
name|cpu
parameter_list|,
name|double
name|io
parameter_list|)
function_decl|;
comment|/**    * Creates a cost object representing an enormous non-infinite cost.    */
name|RelOptCost
name|makeHugeCost
parameter_list|()
function_decl|;
comment|/**    * Creates a cost object representing infinite cost.    */
name|RelOptCost
name|makeInfiniteCost
parameter_list|()
function_decl|;
comment|/**    * Creates a cost object representing a small positive cost.    */
name|RelOptCost
name|makeTinyCost
parameter_list|()
function_decl|;
comment|/**    * Creates a cost object representing zero cost.    */
name|RelOptCost
name|makeZeroCost
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

