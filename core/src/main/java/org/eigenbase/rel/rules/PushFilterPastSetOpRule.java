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
name|ArrayList
import|;
end_import

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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * PushFilterPastSetOpRule implements the rule for pushing a {@link FilterRel}  * past a {@link SetOpRel}.  */
end_comment

begin_class
specifier|public
class|class
name|PushFilterPastSetOpRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushFilterPastSetOpRule
name|instance
init|=
operator|new
name|PushFilterPastSetOpRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushFilterPastSetOpRule.    */
specifier|private
name|PushFilterPastSetOpRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|FilterRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SetOpRel
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
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|FilterRel
name|filterRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SetOpRel
name|setOpRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelOptCluster
name|cluster
init|=
name|setOpRel
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|RexNode
name|condition
init|=
name|filterRel
operator|.
name|getCondition
argument_list|()
decl_stmt|;
comment|// create filters on top of each setop child, modifying the filter
comment|// condition to reference each setop child
name|RexBuilder
name|rexBuilder
init|=
name|filterRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|origFields
init|=
name|setOpRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|origFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newSetOpInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|setOpRel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|RexNode
name|newCondition
init|=
name|condition
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
name|origFields
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|adjustments
argument_list|)
argument_list|)
decl_stmt|;
name|newSetOpInputs
operator|.
name|add
argument_list|(
operator|new
name|FilterRel
argument_list|(
name|cluster
argument_list|,
name|input
argument_list|,
name|newCondition
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create a new setop whose children are the filters created above
name|SetOpRel
name|newSetOpRel
init|=
name|setOpRel
operator|.
name|copy
argument_list|(
name|setOpRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newSetOpInputs
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newSetOpRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PushFilterPastSetOpRule.java
end_comment

end_unit

