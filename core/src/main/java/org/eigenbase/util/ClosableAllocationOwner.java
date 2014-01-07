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
name|util
package|;
end_package

begin_comment
comment|/**  * ClosableAllocationOwner represents an object which can take ownership of  * ClosableAllocations and guarantee that they will be cleaned up correctly when  * its own closeAllocation() is called.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ClosableAllocationOwner
extends|extends
name|ClosableAllocation
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Assigns ownership of a ClosableAllocation to this owner.      *      * @param allocation the ClosableAllocation to take over      */
specifier|public
name|void
name|addAllocation
parameter_list|(
name|ClosableAllocation
name|allocation
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End ClosableAllocationOwner.java
end_comment

end_unit

