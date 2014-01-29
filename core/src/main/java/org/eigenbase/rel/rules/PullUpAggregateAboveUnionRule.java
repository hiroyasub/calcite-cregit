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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * PullUpAggregateAboveUnionRule implements the rule for pulling {@link  * AggregateRel}s beneath a {@link UnionRel} so two {@link AggregateRel}s that  * are used to remove duplicates can be combined into a single {@link  * AggregateRel}.  *  *<p>This rule only handles cases where the {@link UnionRel}s still have only  * two inputs.  */
end_comment

begin_class
specifier|public
class|class
name|PullUpAggregateAboveUnionRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PullUpAggregateAboveUnionRule
name|INSTANCE
init|=
operator|new
name|PullUpAggregateAboveUnionRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PullUpAggregateAboveUnionRule.    */
specifier|private
name|PullUpAggregateAboveUnionRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|AggregateRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|RelNode
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
name|UnionRel
name|unionRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// If distincts haven't been removed yet, defer invoking this rule
if|if
condition|(
operator|!
name|unionRel
operator|.
name|all
condition|)
block|{
return|return;
block|}
name|AggregateRel
name|topAggRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|AggregateRel
name|bottomAggRel
decl_stmt|;
comment|// We want to apply this rule on the pattern where the AggregateRel
comment|// is the second input into the UnionRel first.  Hence, that's why the
comment|// rule pattern matches on generic RelNodes rather than explicit
comment|// UnionRels.  By doing so, and firing this rule in a bottom-up order,
comment|// it allows us to only specify a single pattern for this rule.
name|List
argument_list|<
name|RelNode
argument_list|>
name|unionInputs
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
operator|instanceof
name|AggregateRel
condition|)
block|{
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|unionInputs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|,
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
operator|instanceof
name|AggregateRel
condition|)
block|{
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|unionInputs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
comment|// Only pull up aggregates if they are there just to remove distincts
if|if
condition|(
operator|!
name|topAggRel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|bottomAggRel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|UnionRel
name|newUnionRel
init|=
operator|new
name|UnionRel
argument_list|(
name|unionRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|unionInputs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|AggregateRel
name|newAggRel
init|=
operator|new
name|AggregateRel
argument_list|(
name|topAggRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newUnionRel
argument_list|,
name|topAggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|topAggRel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newAggRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PullUpAggregateAboveUnionRule.java
end_comment

end_unit

