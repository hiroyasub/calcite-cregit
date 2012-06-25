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
name|lamdba
operator|.
name|functions
package|;
end_package

begin_comment
comment|/**  * Performs operations on a pair of values from a BiValue.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BiBlock
parameter_list|<
name|L
parameter_list|,
name|R
parameter_list|>
block|{
name|void
name|apply
parameter_list|(
name|L
name|l
parameter_list|,
name|R
name|r
parameter_list|)
function_decl|;
name|BiBlock
argument_list|<
name|L
argument_list|,
name|R
argument_list|>
name|chain
parameter_list|(
name|BiBlock
argument_list|<
name|?
super|super
name|L
argument_list|,
name|?
super|super
name|R
argument_list|>
name|second
parameter_list|)
function_decl|;
comment|// default:
comment|// throw new UnsupportedOperationException("Not yet implemented");
block|}
end_interface

begin_comment
comment|// End BiBlock.java
end_comment

end_unit

