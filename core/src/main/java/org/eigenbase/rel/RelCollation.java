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
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelTrait
import|;
end_import

begin_comment
comment|/**  * Description of the physical ordering of a relational expression.  *  *<p>An ordering consists of a list of one or more column ordinals and the  * direction of the ordering.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelCollation
extends|extends
name|RelTrait
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the ordinals and directions of the columns in this ordering.    */
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|getFieldCollations
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelCollation.java
end_comment

end_unit

