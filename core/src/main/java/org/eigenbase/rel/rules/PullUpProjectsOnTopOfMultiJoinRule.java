begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * PullUpProjectsOnTopOfMultiJoinRule implements the rule for pulling {@link  * ProjectRel}s that are on top of a {@link MultiJoinRel} and beneath a {@link  * JoinRel} so the {@link ProjectRel} appears above the {@link JoinRel}. In the  * process of doing so, also save away information about the respective fields  * that are referenced in the expressions in the {@link ProjectRel} we're  * pulling up, as well as the join condition, in the resultant {@link  * MultiJoinRel}s  *  *<p>For example, if we have the following subselect:  *  *<pre>  *      (select X.x1, Y.y1 from X, Y  *          where X.x2 = Y.y2 and X.x3 = 1 and Y.y3 = 2)</pre>  *  *<p>The {@link MultiJoinRel} associated with (X, Y) associates x1 with X and  * y1 with Y. Although x3 and y3 need to be read due to the filters, they are  * not required after the row scan has completed and therefore are not saved.  * The join fields, x2 and y2, are also tracked separately.  *  *<p>Note that by only pulling up projects that are on top of {@link  * MultiJoinRel}s, we preserve projections on top of row scans.  *  *<p>See the superclass for details on restrictions regarding which {@link  * ProjectRel}s cannot be pulled.  */
end_comment

begin_class
specifier|public
class|class
name|PullUpProjectsOnTopOfMultiJoinRule
extends|extends
name|PullUpProjectsAboveJoinRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|PullUpProjectsOnTopOfMultiJoinRule
name|MULTI_BOTH_PROJECT
init|=
operator|new
name|PullUpProjectsOnTopOfMultiJoinRule
argument_list|(
name|operand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoinRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|operand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoinRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsOnTopOfMultiJoinRule: with two ProjectRel children"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PullUpProjectsOnTopOfMultiJoinRule
name|MULTI_LEFT_PROJECT
init|=
operator|new
name|PullUpProjectsOnTopOfMultiJoinRule
argument_list|(
name|operand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoinRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsOnTopOfMultiJoinRule: with ProjectRel on left"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PullUpProjectsOnTopOfMultiJoinRule
name|MULTI_RIGHT_PROJECT
init|=
operator|new
name|PullUpProjectsOnTopOfMultiJoinRule
argument_list|(
name|operand
argument_list|(
name|JoinRel
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
name|ProjectRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoinRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|"PullUpProjectsOnTopOfMultiJoinRule: with ProjectRel on right"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|PullUpProjectsOnTopOfMultiJoinRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// override PullUpProjectsAboveJoinRule
specifier|protected
name|boolean
name|hasLeftChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|rels
operator|.
name|length
operator|!=
literal|4
return|;
block|}
comment|// override PullUpProjectsAboveJoinRule
specifier|protected
name|boolean
name|hasRightChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|rels
operator|.
name|length
operator|>
literal|3
return|;
block|}
comment|// override PullUpProjectsAboveJoinRule
specifier|protected
name|ProjectRel
name|getRightChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|==
literal|4
condition|)
block|{
return|return
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
return|;
block|}
block|}
comment|// override PullUpProjectsAboveJoinRule
specifier|protected
name|RelNode
name|getProjectChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|ProjectRel
name|project
parameter_list|,
name|boolean
name|leftChild
parameter_list|)
block|{
comment|// locate the appropriate MultiJoinRel based on which rule was fired
comment|// and which projection we're dealing with
name|MultiJoinRel
name|multiJoin
decl_stmt|;
if|if
condition|(
name|leftChild
condition|)
block|{
name|multiJoin
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|multiJoin
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|multiJoin
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|// create a new MultiJoinRel that reflects the columns in the projection
comment|// above the MultiJoinRel
return|return
name|RelOptUtil
operator|.
name|projectMultiJoin
argument_list|(
name|multiJoin
argument_list|,
name|project
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End PullUpProjectsOnTopOfMultiJoinRule.java
end_comment

end_unit

