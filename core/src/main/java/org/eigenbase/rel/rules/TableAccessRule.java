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
operator|.
name|rules
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Planner rule that converts a {@link TableAccessRel} to the result of calling  * {@link RelOptTable#toRel}.  */
end_comment

begin_class
specifier|public
class|class
name|TableAccessRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|TableAccessRule
name|INSTANCE
init|=
operator|new
name|TableAccessRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|TableAccessRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|TableAccessRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|TableAccessRel
name|oldRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|newRel
init|=
name|oldRel
operator|.
name|getTable
argument_list|()
operator|.
name|toRel
argument_list|(
name|RelOptUtil
operator|.
name|getContext
argument_list|(
name|oldRel
operator|.
name|getCluster
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TableAccessRule.java
end_comment

end_unit

