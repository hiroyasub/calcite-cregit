begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
name|java
operator|.
name|util
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
comment|/**  * Planner rule which merges a {@link CalcRel} onto a {@link CalcRel}. The  * resulting {@link CalcRel} has the same project list as the upper {@link  * CalcRel}, but expressed in terms of the lower {@link CalcRel}'s inputs.  *  * @author jhyde  * @version $Id$  * @since Mar 7, 2004  */
end_comment

begin_class
specifier|public
class|class
name|MergeCalcRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|MergeCalcRule
name|instance
init|=
operator|new
name|MergeCalcRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|MergeCalcRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|CalcRel
operator|.
name|class
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|CalcRel
operator|.
name|class
argument_list|,
name|ANY
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
name|CalcRel
name|topCalc
init|=
operator|(
name|CalcRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|CalcRel
name|bottomCalc
init|=
operator|(
name|CalcRel
operator|)
name|call
operator|.
name|rels
index|[
literal|1
index|]
decl_stmt|;
comment|// Don't merge a calc which contains windowed aggregates onto a
comment|// calc. That would effectively be pushing a windowed aggregate down
comment|// through a filter.
name|RexProgram
name|program
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
name|program
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
specifier|final
name|CalcRel
name|newCalc
init|=
operator|new
name|CalcRel
argument_list|(
name|bottomCalc
operator|.
name|getCluster
argument_list|()
argument_list|,
name|bottomCalc
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|bottomCalc
operator|.
name|getChild
argument_list|()
argument_list|,
name|topCalc
operator|.
name|getRowType
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

begin_comment
comment|// End MergeCalcRule.java
end_comment

end_unit

