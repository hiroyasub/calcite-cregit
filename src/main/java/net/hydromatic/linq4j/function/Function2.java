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
name|linq4j
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * Function with two parameters.  *  * @param<R> Result type  * @param<T0> Type of argument #0  * @param<T1> Type of argument #1  */
end_comment

begin_interface
specifier|public
interface|interface
name|Function2
parameter_list|<
name|T0
parameter_list|,
name|T1
parameter_list|,
name|R
parameter_list|>
extends|extends
name|Function
argument_list|<
name|R
argument_list|>
block|{
name|R
name|apply
parameter_list|(
name|T0
name|v0
parameter_list|,
name|T1
name|v1
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Function2.java
end_comment

end_unit

