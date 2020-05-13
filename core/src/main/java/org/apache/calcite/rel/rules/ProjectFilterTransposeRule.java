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
name|RelOptRule
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
name|Filter
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
name|LogicalFilter
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexOver
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
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Project}  * past a {@link org.apache.calcite.rel.core.Filter}.  */
end_comment

begin_class
specifier|public
class|class
name|ProjectFilterTransposeRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
specifier|public
specifier|static
specifier|final
name|ProjectFilterTransposeRule
name|INSTANCE
init|=
operator|new
name|ProjectFilterTransposeRule
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|LogicalFilter
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|expr
lambda|->
literal|false
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
comment|/**    * Creates a ProjectFilterTransposeRule.    *    * @param preserveExprCondition Condition for expressions that should be    *                              preserved in the projection    */
specifier|public
name|ProjectFilterTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|(
name|projectClass
argument_list|,
name|operand
argument_list|(
name|filterClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|preserveExprCondition
argument_list|,
name|relBuilderFactory
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ProjectFilterTransposeRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
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
name|Project
name|origProj
decl_stmt|;
name|Filter
name|filter
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rels
operator|.
name|length
operator|>=
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
name|filter
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
name|filter
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
name|filter
operator|.
name|getInput
argument_list|()
decl_stmt|;
name|RexNode
name|origFilter
init|=
name|filter
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
if|if
condition|(
operator|(
name|origProj
operator|!=
literal|null
operator|)
operator|&&
name|origProj
operator|.
name|getRowType
argument_list|()
operator|.
name|isStruct
argument_list|()
operator|&&
name|origProj
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|RelDataTypeField
operator|::
name|isDynamicStar
argument_list|)
condition|)
block|{
comment|// The PushProjector would change the plan:
comment|//
comment|//    prj(**=[$0])
comment|//    : - filter
comment|//        : - scan
comment|//
comment|// to form like:
comment|//
comment|//    prj(**=[$0])                    (1)
comment|//    : - filter                      (2)
comment|//        : - prj(**=[$0], ITEM= ...) (3)
comment|//            :  - scan
comment|// This new plan has more cost that the old one, because of the new
comment|// redundant project (3), if we also have FilterProjectTransposeRule in
comment|// the rule set, it will also trigger infinite match of the ProjectMergeRule
comment|// for project (1) and (3).
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
argument_list|,
name|call
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|RelNode
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

end_unit

