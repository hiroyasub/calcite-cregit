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
name|plan
operator|.
name|RelTraitSet
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
name|RelCollation
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
name|RelCollationTraitDef
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
name|RelCollations
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
name|RelFieldCollation
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
name|Join
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
name|RexCall
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
name|rex
operator|.
name|RexShuttle
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
name|RexUtil
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|mapping
operator|.
name|Mappings
import|;
end_import

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

begin_comment
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Project}  * past a {@link org.apache.calcite.rel.core.Join}  * by splitting the projection into a projection on top of each child of  * the join.  */
end_comment

begin_class
specifier|public
class|class
name|ProjectJoinTransposeRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
comment|/**    * A instance for ProjectJoinTransposeRule that pushes a    * {@link org.apache.calcite.rel.logical.LogicalProject}    * past a {@link org.apache.calcite.rel.logical.LogicalJoin}    * by splitting the projection into a projection on top of each child of    * the join.    */
specifier|public
specifier|static
specifier|final
name|ProjectJoinTransposeRule
name|INSTANCE
init|=
operator|new
name|ProjectJoinTransposeRule
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|,
name|expr
lambda|->
operator|!
operator|(
name|expr
operator|instanceof
name|RexOver
operator|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Condition for expressions that should be preserved in the projection.    */
specifier|private
specifier|final
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ProjectJoinTransposeRule with an explicit condition.    *    * @param preserveExprCondition Condition for expressions that should be    *                             preserved in the projection    */
specifier|public
name|ProjectJoinTransposeRule
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
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|PushProjector
operator|.
name|ExprCondition
name|preserveExprCondition
parameter_list|,
name|RelBuilderFactory
name|relFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|projectClass
argument_list|,
name|operand
argument_list|(
name|joinClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relFactory
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
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
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
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|projectsRight
argument_list|()
condition|)
block|{
return|return;
comment|// TODO: support SemiJoin / AntiJoin
block|}
comment|// Normalize the join condition so we don't end up misidentified expanded
comment|// form of IS NOT DISTINCT FROM as PushProject also visit the filter condition
comment|// and push down expressions.
name|RexNode
name|joinFilter
init|=
name|join
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitCall
parameter_list|(
name|RexCall
name|rexCall
parameter_list|)
block|{
specifier|final
name|RexNode
name|node
init|=
name|super
operator|.
name|visitCall
argument_list|(
name|rexCall
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|RexCall
operator|)
condition|)
block|{
return|return
name|node
return|;
block|}
return|return
name|RelOptUtil
operator|.
name|collapseExpandedIsNotDistinctFromExpr
argument_list|(
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|call
operator|.
name|builder
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// locate all fields referenced in the projection and join condition;
comment|// determine which inputs are referenced in the projection and
comment|// join condition; if all fields are being referenced and there are no
comment|// special expressions, no point in proceeding any further
name|PushProjector
name|pushProject
init|=
operator|new
name|PushProjector
argument_list|(
name|origProj
argument_list|,
name|joinFilter
argument_list|,
name|join
argument_list|,
name|preserveExprCondition
argument_list|,
name|call
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pushProject
operator|.
name|locateAllRefs
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// create left and right projections, projecting only those
comment|// fields referenced on each side
name|RelNode
name|leftProjRel
init|=
name|pushProject
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RelNode
name|rightProjRel
init|=
name|pushProject
operator|.
name|createProjectRefsAndExprs
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// convert the join condition to reference the projected columns
name|RexNode
name|newJoinFilter
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
name|pushProject
operator|.
name|getAdjustments
argument_list|()
decl_stmt|;
if|if
condition|(
name|joinFilter
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|projJoinFieldList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|join
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|leftProjRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|projJoinFieldList
operator|.
name|addAll
argument_list|(
name|rightProjRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|newJoinFilter
operator|=
name|pushProject
operator|.
name|convertRefsAndExprs
argument_list|(
name|joinFilter
argument_list|,
name|projJoinFieldList
argument_list|,
name|adjustments
argument_list|)
expr_stmt|;
block|}
name|RelTraitSet
name|traits
init|=
name|join
operator|.
name|getTraitSet
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|originCollations
init|=
name|traits
operator|.
name|getTraits
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|originCollations
operator|!=
literal|null
operator|&&
operator|!
name|originCollations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|RelCollation
argument_list|>
name|newCollations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|originLeftCnt
init|=
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|leftMapping
init|=
name|RelOptUtil
operator|.
name|permutationPushDownProject
argument_list|(
operator|(
operator|(
name|Project
operator|)
name|leftProjRel
operator|)
operator|.
name|getProjects
argument_list|()
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|rightMapping
init|=
name|RelOptUtil
operator|.
name|permutationPushDownProject
argument_list|(
operator|(
operator|(
name|Project
operator|)
name|rightProjRel
operator|)
operator|.
name|getProjects
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|originLeftCnt
argument_list|,
name|leftProjRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelCollation
name|collation
range|:
name|originCollations
control|)
block|{
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fc
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
init|=
name|collation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|relFieldCollation
range|:
name|fieldCollations
control|)
block|{
specifier|final
name|int
name|fieldIndex
init|=
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|fieldIndex
operator|<
name|originLeftCnt
condition|?
name|leftMapping
else|:
name|rightMapping
decl_stmt|;
name|RelFieldCollation
name|newFieldCollation
init|=
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|relFieldCollation
argument_list|)
decl_stmt|;
if|if
condition|(
name|newFieldCollation
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|fc
operator|.
name|add
argument_list|(
name|newFieldCollation
argument_list|)
expr_stmt|;
block|}
name|newCollations
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|fc
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|newCollations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|traits
operator|=
name|traits
operator|.
name|replace
argument_list|(
name|newCollations
argument_list|)
expr_stmt|;
block|}
block|}
comment|// create a new join with the projected children
name|Join
name|newJoinRel
init|=
name|join
operator|.
name|copy
argument_list|(
name|traits
argument_list|,
name|newJoinFilter
argument_list|,
name|leftProjRel
argument_list|,
name|rightProjRel
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
comment|// put the original project on top of the join, converting it to
comment|// reference the modified projection list
name|RelNode
name|topProject
init|=
name|pushProject
operator|.
name|createNewProject
argument_list|(
name|newJoinRel
argument_list|,
name|adjustments
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|topProject
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

