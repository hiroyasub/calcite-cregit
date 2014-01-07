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
name|relopt
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Callback used to hold state while converting a tree of {@link RelNode  * relational expressions} into a plan. Calling conventions typically have their  * own protocol for walking over a tree, and correspondingly have their own  * implementors, which are subclasses of<code>RelImplementor</code>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelImplementor
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Implements a relational expression according to a calling convention.    *    * @param parent  Parent relational expression    * @param ordinal Ordinal of child within its parent    * @param child   Child relational expression    * @return Interpretation of the return value is left to the implementor    */
name|Object
name|visitChild
parameter_list|(
name|RelNode
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|child
parameter_list|)
function_decl|;
comment|/**    * Called from {@link #visitChild} after the frame has been set up. Specific    * implementors should override this method.    *    * @param child   Child relational expression    * @param ordinal Ordinal of child within its parent    * @param arg     Additional parameter; type depends on implementor    * @return Interpretation of the return value is left to the implementor    */
name|Object
name|visitChildInternal
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|Object
name|arg
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelImplementor.java
end_comment

end_unit

