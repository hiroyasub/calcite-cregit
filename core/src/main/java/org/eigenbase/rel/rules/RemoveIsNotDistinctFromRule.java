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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Rule to replace isNotDistinctFromOperator with logical equivalent conditions  * in a {@link FilterRel}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RemoveIsNotDistinctFromRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * The singleton.    */
specifier|public
specifier|static
specifier|final
name|RemoveIsNotDistinctFromRule
name|instance
init|=
operator|new
name|RemoveIsNotDistinctFromRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RemoveIsNotDistinctFromRule
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
name|FilterRel
name|oldFilterRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RexNode
name|oldFilterCond
init|=
name|oldFilterRel
operator|.
name|getCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|RexUtil
operator|.
name|findOperatorCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|isNotDistinctFromOperator
argument_list|,
name|oldFilterCond
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// no longer contains isNotDistinctFromOperator
return|return;
block|}
comment|// Now replace all the "a isNotDistinctFrom b"
comment|// with the RexNode given by RelOptUtil.isDistinctFrom() method
name|RemoveIsNotDistinctFromRexShuttle
name|rewriteShuttle
init|=
operator|new
name|RemoveIsNotDistinctFromRexShuttle
argument_list|(
name|oldFilterRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|RelNode
name|newFilterRel
init|=
name|CalcRel
operator|.
name|createFilter
argument_list|(
name|oldFilterRel
operator|.
name|getChild
argument_list|()
argument_list|,
name|oldFilterCond
operator|.
name|accept
argument_list|(
name|rewriteShuttle
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newFilterRel
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
class|class
name|RemoveIsNotDistinctFromRexShuttle
extends|extends
name|RexShuttle
block|{
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|public
name|RemoveIsNotDistinctFromRexShuttle
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
block|}
comment|// override RexShuttle
specifier|public
name|RexNode
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
name|RexNode
name|newCall
init|=
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|isNotDistinctFromOperator
condition|)
block|{
name|RexCall
name|tmpCall
init|=
operator|(
name|RexCall
operator|)
name|newCall
decl_stmt|;
name|newCall
operator|=
name|RelOptUtil
operator|.
name|isDistinctFrom
argument_list|(
name|rexBuilder
argument_list|,
name|tmpCall
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tmpCall
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|newCall
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RemoveIsNotDistinctFromRule.java
end_comment

end_unit

