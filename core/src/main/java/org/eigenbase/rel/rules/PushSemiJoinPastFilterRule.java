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
comment|/**  * PushSemiJoinPastFilterRule implements the rule for pushing semijoins down in  * a tree past a filter in order to trigger other rules that will convert  * semijoins. SemiJoinRel(FilterRel(X), Y) --> FilterRel(SemiJoinRel(X, Y))  */
end_comment

begin_class
specifier|public
class|class
name|PushSemiJoinPastFilterRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushSemiJoinPastFilterRule
name|instance
init|=
operator|new
name|PushSemiJoinPastFilterRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushSemiJoinPastFilterRule.    */
specifier|private
name|PushSemiJoinPastFilterRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|SemiJoinRel
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|FilterRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|SemiJoinRel
name|semiJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FilterRel
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelNode
name|newSemiJoin
init|=
operator|new
name|SemiJoinRel
argument_list|(
name|semiJoin
operator|.
name|getCluster
argument_list|()
argument_list|,
name|filter
operator|.
name|getChild
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getRight
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getLeftKeys
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getRightKeys
argument_list|()
argument_list|)
decl_stmt|;
name|RelNode
name|newFilter
init|=
name|CalcRel
operator|.
name|createFilter
argument_list|(
name|newSemiJoin
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PushSemiJoinPastFilterRule.java
end_comment

end_unit

