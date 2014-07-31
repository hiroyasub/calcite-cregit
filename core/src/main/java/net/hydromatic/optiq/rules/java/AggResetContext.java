begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|Expression
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Information for a call to {@link AggImplementor#implementReset(AggContext, AggResetContext)}.  * {@link AggResetContext} provides access to the accumulator variables  * that should be reset.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggResetContext
extends|extends
name|NestedBlockBuilder
block|{
comment|/**    * Returns accumulator variables that should be reset.    * There MUST be an assignment even if you just assign the default value.    * @return accumulator variables that should be reset or empty list when no    *   accumulator variables are used by the aggregate implementation.    * @see AggImplementor#getStateType(net.hydromatic.optiq.rules.java.AggContext)    */
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End AggResetContext.java
end_comment

end_unit

