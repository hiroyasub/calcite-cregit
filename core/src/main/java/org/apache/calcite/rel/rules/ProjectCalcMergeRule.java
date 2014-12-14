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
name|RelOptCluster
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
name|logical
operator|.
name|LogicalCalc
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
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * Planner rule which merges a  * {@link org.apache.calcite.rel.logical.LogicalProject} and a  * {@link org.apache.calcite.rel.logical.LogicalCalc}.  *  *<p>The resulting {@link org.apache.calcite.rel.logical.LogicalCalc} has the  * same project list as the original  * {@link org.apache.calcite.rel.logical.LogicalProject}, but expressed in terms  * of the original {@link org.apache.calcite.rel.logical.LogicalCalc}'s inputs.  *  * @see FilterCalcMergeRule  */
end_comment

begin_class
specifier|public
class|class
name|ProjectCalcMergeRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|ProjectCalcMergeRule
name|INSTANCE
init|=
operator|new
name|ProjectCalcMergeRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|ProjectCalcMergeRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalCalc
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
specifier|final
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|LogicalCalc
name|calc
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Don't merge a project which contains windowed aggregates onto a
comment|// calc. That would effectively be pushing a windowed aggregate down
comment|// through a filter. Transform the project into an identical calc,
comment|// which we'll have chance to merge later, after the over is
comment|// expanded.
specifier|final
name|RelOptCluster
name|cluster
init|=
name|project
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|RexProgram
name|program
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|calc
operator|.
name|getRowType
argument_list|()
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|RexOver
operator|.
name|containsOver
argument_list|(
name|program
argument_list|)
condition|)
block|{
name|LogicalCalc
name|projectAsCalc
init|=
operator|new
name|LogicalCalc
argument_list|(
name|cluster
argument_list|,
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|calc
argument_list|,
name|program
argument_list|,
name|Collections
operator|.
expr|<
name|RelCollation
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|projectAsCalc
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Create a program containing the project node's expressions.
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RexProgramBuilder
name|progBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|calc
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|field
range|:
name|project
operator|.
name|getNamedProjects
argument_list|()
control|)
block|{
name|progBuilder
operator|.
name|addProject
argument_list|(
name|field
operator|.
name|left
argument_list|,
name|field
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
name|RexProgram
name|topProgram
init|=
name|progBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
name|RexProgram
name|bottomProgram
init|=
name|calc
operator|.
name|getProgram
argument_list|()
decl_stmt|;
comment|// Merge the programs together.
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
specifier|final
name|LogicalCalc
name|newCalc
init|=
operator|new
name|LogicalCalc
argument_list|(
name|cluster
argument_list|,
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|calc
operator|.
name|getInput
argument_list|()
argument_list|,
name|mergedProgram
argument_list|,
name|Collections
operator|.
expr|<
name|RelCollation
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newCalc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ProjectCalcMergeRule.java
end_comment

end_unit

