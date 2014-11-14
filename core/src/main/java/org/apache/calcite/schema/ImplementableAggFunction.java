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
name|schema
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
name|adapter
operator|.
name|enumerable
operator|.
name|AggImplementor
import|;
end_import

begin_comment
comment|/**  * Function that can be translated to java code.  *  * @see org.apache.calcite.adapter.enumerable.AggImplementor  * @see org.apache.calcite.adapter.enumerable.WinAggImplementor  * @see org.apache.calcite.adapter.enumerable.StrictAggImplementor  * @see org.apache.calcite.adapter.enumerable.StrictWinAggImplementor  */
end_comment

begin_interface
specifier|public
interface|interface
name|ImplementableAggFunction
extends|extends
name|AggregateFunction
block|{
comment|/**    * Returns implementor that translates the function to linq4j expression.    *    * @param windowContext true when aggregate is used in window context    * @return implementor that translates the function to linq4j expression.    */
name|AggImplementor
name|getImplementor
parameter_list|(
name|boolean
name|windowContext
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End ImplementableAggFunction.java
end_comment

end_unit

