begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRuleCall
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptRuleOperand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|RelFactories
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|logical
operator|.
name|LogicalJoin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|logical
operator|.
name|LogicalProject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * MultiJoinProjectTransposeRule implements the rule for pulling  * {@link org.apache.calcite.rel.logical.LogicalProject}s that are on top of a  * {@link MultiJoin} and beneath a  * {@link org.apache.calcite.rel.logical.LogicalJoin} so the  * {@link org.apache.calcite.rel.logical.LogicalProject} appears above the  * {@link org.apache.calcite.rel.logical.LogicalJoin}.  *  *<p>In the process of doing  * so, also save away information about the respective fields that are  * referenced in the expressions in the  * {@link org.apache.calcite.rel.logical.LogicalProject} we're pulling up, as  * well as the join condition, in the resultant {@link MultiJoin}s  *  *<p>For example, if we have the following sub-query:  *  *<blockquote><pre>  * (select X.x1, Y.y1 from X, Y  *  where X.x2 = Y.y2 and X.x3 = 1 and Y.y3 = 2)</pre></blockquote>  *  *<p>The {@link MultiJoin} associated with (X, Y) associates x1 with X and  * y1 with Y. Although x3 and y3 need to be read due to the filters, they are  * not required after the row scan has completed and therefore are not saved.  * The join fields, x2 and y2, are also tracked separately.  *  *<p>Note that by only pulling up projects that are on top of  * {@link MultiJoin}s, we preserve projections on top of row scans.  *  *<p>See the superclass for details on restrictions regarding which  * {@link org.apache.calcite.rel.logical.LogicalProject}s cannot be pulled.  */
end_comment

begin_class
specifier|public
class|class
name|MultiJoinProjectTransposeRule
extends|extends
name|JoinProjectTransposeRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|MultiJoinProjectTransposeRule
name|MULTI_BOTH_PROJECT
init|=
operator|new
name|MultiJoinProjectTransposeRule
argument_list|(
name|operand
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoin
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
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoin
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"MultiJoinProjectTransposeRule: with two LogicalProject children"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MultiJoinProjectTransposeRule
name|MULTI_LEFT_PROJECT
init|=
operator|new
name|MultiJoinProjectTransposeRule
argument_list|(
name|operand
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoin
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
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"MultiJoinProjectTransposeRule: with LogicalProject on left"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MultiJoinProjectTransposeRule
name|MULTI_RIGHT_PROJECT
init|=
operator|new
name|MultiJoinProjectTransposeRule
argument_list|(
name|operand
argument_list|(
name|LogicalJoin
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
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|MultiJoin
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"MultiJoinProjectTransposeRule: with LogicalProject on right"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|MultiJoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a MultiJoinProjectTransposeRule. */
specifier|public
name|MultiJoinProjectTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
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
argument_list|,
literal|false
argument_list|,
name|relBuilderFactory
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// override JoinProjectTransposeRule
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
comment|// override JoinProjectTransposeRule
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
comment|// override JoinProjectTransposeRule
specifier|protected
name|Project
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
comment|// override JoinProjectTransposeRule
specifier|protected
name|RelNode
name|getProjectChild
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Project
name|project
parameter_list|,
name|boolean
name|leftChild
parameter_list|)
block|{
comment|// locate the appropriate MultiJoin based on which rule was fired
comment|// and which projection we're dealing with
name|MultiJoin
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
comment|// create a new MultiJoin that reflects the columns in the projection
comment|// above the MultiJoin
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
comment|// End MultiJoinProjectTransposeRule.java
end_comment

end_unit

