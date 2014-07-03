begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|AggImplementor
import|;
end_import

begin_comment
comment|/**  * Function that can be translated to java code.  *  * @see net.hydromatic.optiq.rules.java.AggImplementor  * @see net.hydromatic.optiq.rules.java.WinAggImplementor  * @see net.hydromatic.optiq.rules.java.StrictAggImplementor  * @see net.hydromatic.optiq.rules.java.StrictWinAggImplementor  */
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

