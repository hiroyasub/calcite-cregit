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
name|BlockBuilder
import|;
end_import

begin_comment
comment|/**  * Allows to build nested code blocks with tracking of current context.  *  * @see org.apache.calcite.adapter.enumerable.StrictAggImplementor#implementAdd(AggContext, AggAddContext)  */
end_comment

begin_interface
specifier|public
interface|interface
name|NestedBlockBuilder
block|{
comment|/**    * Starts nested code block. The resulting block can optimize expressions    * and reuse already calculated values from the parent blocks.    * @return new code block that can optimize expressions and reuse already    * calculated values from the parent blocks.    */
name|BlockBuilder
name|nestBlock
parameter_list|()
function_decl|;
comment|/**    * Uses given block as the new code context.    * The current block will be restored after {@link #exitBlock()} call.    * @param block new code block    * @see #exitBlock()    */
name|void
name|nestBlock
parameter_list|(
name|BlockBuilder
name|block
parameter_list|)
function_decl|;
comment|/**    * Returns the current code block.    */
name|BlockBuilder
name|currentBlock
parameter_list|()
function_decl|;
comment|/**    * Leaves the current code block.    * @see #nestBlock()    */
name|void
name|exitBlock
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

