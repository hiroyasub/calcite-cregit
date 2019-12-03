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
name|core
operator|.
name|Calc
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * Planner rule that merges a  * {@link org.apache.calcite.rel.logical.LogicalCalc} onto a  * {@link org.apache.calcite.rel.logical.LogicalCalc}.  *  *<p>The resulting {@link org.apache.calcite.rel.logical.LogicalCalc} has the  * same project list as the upper  * {@link org.apache.calcite.rel.logical.LogicalCalc}, but expressed in terms of  * the lower {@link org.apache.calcite.rel.logical.LogicalCalc}'s inputs.  */
end_comment

begin_class
specifier|public
class|class
name|CalcMergeRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|CalcMergeRule
name|INSTANCE
init|=
operator|new
name|CalcMergeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a CalcMergeRule.    *    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|CalcMergeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Calc
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Calc
operator|.
name|class
argument_list|,
name|any
argument_list|()
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
specifier|final
name|Calc
name|topCalc
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Calc
name|bottomCalc
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Don't merge a calc which contains windowed aggregates onto a
comment|// calc. That would effectively be pushing a windowed aggregate down
comment|// through a filter.
name|RexProgram
name|topProgram
init|=
name|topCalc
operator|.
name|getProgram
argument_list|()
decl_stmt|;
if|if
condition|(
name|RexOver
operator|.
name|containsOver
argument_list|(
name|topProgram
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Merge the programs together.
name|RexProgram
name|mergedProgram
init|=
name|RexProgramBuilder
operator|.
name|mergePrograms
argument_list|(
name|topCalc
operator|.
name|getProgram
argument_list|()
argument_list|,
name|bottomCalc
operator|.
name|getProgram
argument_list|()
argument_list|,
name|topCalc
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|mergedProgram
operator|.
name|getOutputRowType
argument_list|()
operator|==
name|topProgram
operator|.
name|getOutputRowType
argument_list|()
assert|;
specifier|final
name|Calc
name|newCalc
init|=
name|topCalc
operator|.
name|copy
argument_list|(
name|topCalc
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|bottomCalc
operator|.
name|getInput
argument_list|()
argument_list|,
name|mergedProgram
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCalc
operator|.
name|getDigest
argument_list|()
operator|.
name|equals
argument_list|(
name|bottomCalc
operator|.
name|getDigest
argument_list|()
argument_list|)
operator|&&
name|newCalc
operator|.
name|getRowType
argument_list|()
operator|.
name|equals
argument_list|(
name|bottomCalc
operator|.
name|getRowType
argument_list|()
argument_list|)
condition|)
block|{
comment|// newCalc is equivalent to bottomCalc, which means that topCalc
comment|// must be trivial. Take it out of the game.
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|setImportance
argument_list|(
name|topCalc
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
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

end_unit

