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
comment|/**  * A function to customize whether a relational expression should match a hint.  *  *<p>Usually you may not need to implement this function,  * {@link NodeTypeHintStrategy} is enough for most of the {@link RelHint}s.  *  *<p>Some of the hints can only be matched to the relational  * expression with special match conditions(not only the relational expression type).  * i.e. "hash_join(r, st)", this hint can only be applied to JOIN expression that  * has "r" and "st" as the input table names. To implement this, you may need to customize an  * {@link ExplicitHintStrategy} with the {@link ExplicitHintMatcher}.  *  * @param<R> Relational expression to test if the hint matches  *  * @see ExplicitHintStrategy  * @see HintStrategies  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|ExplicitHintMatcher
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
block|{
name|boolean
name|apply
parameter_list|(
name|RelHint
name|hint
parameter_list|,
name|R
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

