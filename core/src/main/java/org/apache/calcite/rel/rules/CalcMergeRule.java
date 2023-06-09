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
name|RelRule
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

begin_import
import|import
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
import|;
end_import

begin_comment
comment|/**  * Planner rule that merges a  * {@link org.apache.calcite.rel.core.Calc} onto a  * {@link org.apache.calcite.rel.core.Calc}.  *  *<p>The resulting {@link org.apache.calcite.rel.core.Calc} has the  * same project list as the upper  * {@link org.apache.calcite.rel.core.Calc}, but expressed in terms of  * the lower {@link org.apache.calcite.rel.core.Calc}'s inputs.  *  * @see CoreRules#CALC_MERGE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|CalcMergeRule
extends|extends
name|RelRule
argument_list|<
name|CalcMergeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a CalcMergeRule. */
specifier|protected
name|CalcMergeRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|CalcMergeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
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
name|prune
argument_list|(
name|topCalc
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
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableCalcMergeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|Calc
operator|.
name|class
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|Calc
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|CalcMergeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|CalcMergeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

