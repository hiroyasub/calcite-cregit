begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * PushProjectPastFilterRule implements the rule for pushing a projection past a  * filter.  */
end_comment

begin_class
specifier|public
class|class
name|PushProjectPastFilterRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushProjectPastFilterRule
name|INSTANCE
init|=
operator|new
name|PushProjectPastFilterRule
argument_list|(
name|PushProjector
operator|.
name|ExprCondition
operator|.
name|FALSE
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Expressions that should be preserved in the projection    */
specifier|private
specifier|final
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushProjectPastFilterRule.    *    * @param preserveExprCondition Condition for expressions that should be    *                              preserved in the projection    */
specifier|private
name|PushProjectPastFilterRule
parameter_list|(
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
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
expr_stmt|;
name|this
operator|.
name|preserveExprCondition
operator|=
name|preserveExprCondition
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
name|ProjectRel
name|origProj
decl_stmt|;
name|FilterRel
name|filterRel
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|origProj
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|filterRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|origProj
operator|=
literal|null
expr_stmt|;
name|filterRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|rel
init|=
name|filterRel
operator|.
name|getChild
argument_list|()
decl_stmt|;
name|RexNode
name|origFilter
init|=
name|filterRel
operator|.
name|getCondition
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|origProj
operator|!=
literal|null
operator|)
operator|&&
name|RexOver
operator|.
name|containsOver
argument_list|(
name|origProj
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|)
condition|)
block|{
comment|// Cannot push project through filter if project contains a windowed
comment|// aggregate -- it will affect row counts. Abort this rule
comment|// invocation; pushdown will be considered after the windowed
comment|// aggregate has been implemented. It's OK if the filter contains a
comment|// windowed aggregate.
return|return;
block|}
name|PushProjector
name|pushProjector
init|=
operator|new
name|PushProjector
argument_list|(
name|origProj
argument_list|,
name|origFilter
argument_list|,
name|rel
argument_list|,
name|preserveExprCondition
argument_list|)
decl_stmt|;
name|ProjectRel
name|topProject
init|=
name|pushProjector
operator|.
name|convertProject
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|topProject
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|topProject
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End PushProjectPastFilterRule.java
end_comment

end_unit

