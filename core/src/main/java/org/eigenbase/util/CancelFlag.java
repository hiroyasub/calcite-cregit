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
comment|/**  * CancelFlag is used to post and check cancellation requests.  */
end_comment

begin_class
specifier|public
class|class
name|CancelFlag
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|boolean
name|cancelRequested
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return whether a cancellation has been requested    */
specifier|public
name|boolean
name|isCancelRequested
parameter_list|()
block|{
return|return
name|cancelRequested
return|;
block|}
comment|/**    * Requests a cancellation.    */
specifier|public
name|void
name|requestCancel
parameter_list|()
block|{
name|cancelRequested
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Clears any pending cancellation request.    */
specifier|public
name|void
name|clearCancel
parameter_list|()
block|{
name|cancelRequested
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CancelFlag.java
end_comment

end_unit

