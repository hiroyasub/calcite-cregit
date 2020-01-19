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
name|rel
operator|.
name|hint
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
comment|/**  * A {@code HintStrategy} indicates whether a {@link org.apache.calcite.rel.RelNode}  * can apply the specified hint.  *  *<p>Typically, every supported hint should register a {@code HintStrategy}  * into the {@link HintStrategyTable}. For example, {@link HintStrategies#JOIN} implies  * that this hint would be propagated and applied to the {@link org.apache.calcite.rel.core.Join}  * relational expressions.  *  *<p>In {@link HintStrategyTable} the strategy is used for  * hints registration.  *  * @see HintStrategyTable  */
end_comment

begin_interface
specifier|public
interface|interface
name|HintStrategy
block|{
comment|/**    * Decides if the given {@code hint} can be applied to    * the relational expression {@code rel}.    *    * @param hint The hint    * @param rel  The relational expression    * @return True if the {@code hint} can be applied to the {@code rel}    */
name|boolean
name|canApply
parameter_list|(
name|RelHint
name|hint
parameter_list|,
name|RelNode
name|rel
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

