begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_comment
comment|/**  * A class that describes how many operands an operator can take.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperandCountRange
block|{
comment|/**    * Returns whether {@code count} is a valid number of operands.    */
name|boolean
name|isValidCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Returns an lower bound. -1 if there is no lower bound.    */
name|int
name|getMin
parameter_list|()
function_decl|;
comment|/**    * Returns an upper bound. -1 if there is no upper bound.    */
name|int
name|getMax
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlOperandCountRange.java
end_comment

end_unit

