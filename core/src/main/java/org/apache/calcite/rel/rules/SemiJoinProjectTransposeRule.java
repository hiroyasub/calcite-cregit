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
name|JoinRelType
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
name|RelDataType
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
name|RelDataTypeFactory
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
name|RexBuilder
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
name|RexProgram
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
name|RexProgramBuilder
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|RelBuilder
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
name|Pair
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
name|ImmutableSet
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
comment|/**  * Planner rule that pushes  * a {@code SemiJoin} down in a tree past  * a {@link org.apache.calcite.rel.core.Project}.  *  *<p>The intention is to trigger other rules that will convert  * {@code SemiJoin}s.  *  *<p>SemiJoin(LogicalProject(X), Y)&rarr; LogicalProject(SemiJoin(X, Y))  *  * @see org.apache.calcite.rel.rules.SemiJoinFilterTransposeRule  */
end_comment

begin_class
specifier|public
class|class
name|SemiJoinProjectTransposeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|SemiJoinProjectTransposeRule
name|INSTANCE
init|=
operator|new
name|SemiJoinProjectTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SemiJoinProjectTransposeRule.    */
specifier|private
name|SemiJoinProjectTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Join
operator|::
name|isSemiJoin
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
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
name|LogicalJoin
name|semiJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Convert the LHS semi-join keys to reference the child projection
comment|// expression; all projection expressions must be RexInputRefs,
comment|// otherwise, we wouldn't have created this semi-join.
comment|// convert the semijoin condition to reflect the LHS with the project
comment|// pulled up
name|RexNode
name|newCondition
init|=
name|adjustCondition
argument_list|(
name|project
argument_list|,
name|semiJoin
argument_list|)
decl_stmt|;
name|LogicalJoin
name|newSemiJoin
init|=
name|LogicalJoin
operator|.
name|create
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getRight
argument_list|()
argument_list|,
comment|// No need to copy the hints, the framework would try to do that.
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|newCondition
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|SEMI
argument_list|)
decl_stmt|;
comment|// Create the new projection.  Note that the projection expressions
comment|// are the same as the original because they only reference the LHS
comment|// of the semijoin and the semijoin only projects out the LHS
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|newSemiJoin
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pulls the project above the semijoin and returns the resulting semijoin    * condition. As a result, the semijoin condition should be modified such    * that references to the LHS of a semijoin should now reference the    * children of the project that's on the LHS.    *    * @param project  LogicalProject on the LHS of the semijoin    * @param semiJoin the semijoin    * @return the modified semijoin condition    */
specifier|private
name|RexNode
name|adjustCondition
parameter_list|(
name|LogicalProject
name|project
parameter_list|,
name|LogicalJoin
name|semiJoin
parameter_list|)
block|{
comment|// create two RexPrograms -- the bottom one representing a
comment|// concatenation of the project and the RHS of the semijoin and the
comment|// top one representing the semijoin condition
name|RexBuilder
name|rexBuilder
init|=
name|project
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelNode
name|rightChild
init|=
name|semiJoin
operator|.
name|getRight
argument_list|()
decl_stmt|;
comment|// for the bottom RexProgram, the input is a concatenation of the
comment|// child of the project and the RHS of the semijoin
name|RelDataType
name|bottomInputRowType
init|=
name|SqlValidatorUtil
operator|.
name|deriveJoinRowType
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rightChild
operator|.
name|getRowType
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|typeFactory
argument_list|,
literal|null
argument_list|,
name|semiJoin
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
decl_stmt|;
name|RexProgramBuilder
name|bottomProgramBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|bottomInputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
comment|// add the project expressions, then add input references for the RHS
comment|// of the semijoin
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|project
operator|.
name|getNamedProjects
argument_list|()
control|)
block|{
name|bottomProgramBuilder
operator|.
name|addProject
argument_list|(
name|pair
operator|.
name|left
argument_list|,
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
name|int
name|nLeftFields
init|=
name|project
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|rightFields
init|=
name|rightChild
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|int
name|nRightFields
init|=
name|rightFields
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nRightFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|rightFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RexNode
name|inputRef
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|i
operator|+
name|nLeftFields
argument_list|)
decl_stmt|;
name|bottomProgramBuilder
operator|.
name|addProject
argument_list|(
name|inputRef
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RexProgram
name|bottomProgram
init|=
name|bottomProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
comment|// input rowtype into the top program is the concatenation of the
comment|// project and the RHS of the semijoin
name|RelDataType
name|topInputRowType
init|=
name|SqlValidatorUtil
operator|.
name|deriveJoinRowType
argument_list|(
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rightChild
operator|.
name|getRowType
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|typeFactory
argument_list|,
literal|null
argument_list|,
name|semiJoin
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
decl_stmt|;
name|RexProgramBuilder
name|topProgramBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|topInputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|topProgramBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
name|topProgramBuilder
operator|.
name|addCondition
argument_list|(
name|semiJoin
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
name|RexProgram
name|topProgram
init|=
name|topProgramBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
comment|// merge the programs and expand out the local references to form
comment|// the new semijoin condition; it now references a concatenation of
comment|// the project's child and the RHS of the semijoin
name|RexProgram
name|mergedProgram
init|=
name|RexProgramBuilder
operator|.
name|mergePrograms
argument_list|(
name|topProgram
argument_list|,
name|bottomProgram
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
return|return
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|mergedProgram
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

