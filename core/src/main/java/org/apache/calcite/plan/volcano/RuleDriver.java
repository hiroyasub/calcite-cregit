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
operator|.
name|volcano
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
name|RelNode
import|;
end_import

begin_comment
comment|/**  * A rule driver applies rules with designed algorithms.  */
end_comment

begin_interface
interface|interface
name|RuleDriver
block|{
comment|/**    * Gets the rule queue.    */
name|RuleQueue
name|getRuleQueue
parameter_list|()
function_decl|;
comment|/**    * Apply rules.    */
name|void
name|drive
parameter_list|()
function_decl|;
comment|/**    * Callback when new RelNodes are added into RelSet.    * @param rel the new RelNode    * @param subset subset to add    */
name|void
name|onProduce
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelSubset
name|subset
parameter_list|)
function_decl|;
comment|/**    * Callback when RelSets are merged.    * @param set the merged result set    */
name|void
name|onSetMerged
parameter_list|(
name|RelSet
name|set
parameter_list|)
function_decl|;
comment|/**    * Clear this RuleDriver.    */
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

