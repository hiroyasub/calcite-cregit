begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
package|;
end_package

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
name|BlockBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|RexNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Allows to build nested code blocks with tracking of current context and  * the nullability of particular {@link org.eigenbase.rex.RexNode} expressions.  * @see net.hydromatic.optiq.rules.java.StrictAggImplementor#implementAdd(AggContext, AggAddContext)  */
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
comment|/**    * Uses given block as the new code context and the map of nullability.    * The current block will be restored after {@link #exitBlock()} call.    * @param block new code block    * @param nullables map of expression to its nullability state    * @see #exitBlock()    */
name|void
name|nestBlock
parameter_list|(
name|BlockBuilder
name|block
parameter_list|,
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Boolean
argument_list|>
name|nullables
parameter_list|)
function_decl|;
comment|/**    * Returns the current code block    * @return current code block    */
name|BlockBuilder
name|currentBlock
parameter_list|()
function_decl|;
comment|/**    * Returns the current nullability state of rex nodes.    * The resulting value is the summary of all the maps in the block hierarchy.    * @return current nullability state of rex nodes    */
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Boolean
argument_list|>
name|currentNullables
parameter_list|()
function_decl|;
comment|/**    * Leaves the current code block.    * @see #nestBlock()    */
name|void
name|exitBlock
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End NestedBlockBuilder.java
end_comment

end_unit

