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
comment|/**  * Rule to remove an {@link AggregateRel} implementing DISTINCT if the  * underlying relational expression is already distinct.  */
end_comment

begin_class
specifier|public
class|class
name|RemoveDistinctRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|RemoveDistinctRule
name|INSTANCE
init|=
operator|new
name|RemoveDistinctRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RemoveDistinctRule.    */
specifier|private
name|RemoveDistinctRule
parameter_list|()
block|{
comment|// REVIEW jvs 14-Mar-2006: We have to explicitly mention the child here
comment|// to make sure the rule re-fires after the child changes (e.g. via
comment|// RemoveTrivialProjectRule), since that may change our information
comment|// about whether the child is distinct.  If we clean up the inference of
comment|// distinct to make it correct up-front, we can get rid of the reference
comment|// to the child here.
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
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
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
name|AggregateRel
name|distinct
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|child
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|distinct
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|child
operator|.
name|isKey
argument_list|(
name|distinct
operator|.
name|getGroupSet
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Distinct is "GROUP BY c1, c2" (where c1, c2 are a set of columns on
comment|// which the input is unique, i.e. contain a key) and has no aggregate
comment|// functions. It can be removed.
name|child
operator|=
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|register
argument_list|(
name|child
argument_list|,
name|distinct
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|convert
argument_list|(
name|child
argument_list|,
name|distinct
operator|.
name|getTraitSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RemoveDistinctRule.java
end_comment

end_unit

