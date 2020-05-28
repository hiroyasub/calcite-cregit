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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|AggregateCall
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

begin_comment
comment|/**  * Information for a call to  * {@link AggImplementor#implementResult(AggContext, AggResultContext)}  *  *<p>Typically, the aggregation implementation will convert  * {@link #accumulator()} to the resulting value of the aggregation.  The  * implementation MUST NOT destroy the contents of {@link #accumulator()}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggResultContext
extends|extends
name|NestedBlockBuilder
extends|,
name|AggResetContext
block|{
comment|/** Expression by which to reference the key upon which the values in the    * accumulator were aggregated. Most aggregate functions depend on only the    * accumulator, but quasi-aggregate functions such as GROUPING access at the    * key. */
annotation|@
name|Nullable
name|Expression
name|key
parameter_list|()
function_decl|;
comment|/** Returns an expression that references the {@code i}th field of the key,    * cast to the appropriate type. */
name|Expression
name|keyField
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
name|AggregateCall
name|call
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

