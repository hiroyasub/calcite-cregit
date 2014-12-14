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

begin_comment
comment|/**  * Planner rule that merges a  * {@link org.apache.calcite.rel.logical.LogicalFilter} and a  * {@link org.apache.calcite.rel.logical.LogicalCalc}. The  * result is a {@link org.apache.calcite.rel.logical.LogicalCalc}  * whose filter condition is the logical AND of the two.  *  * @see FilterMergeRule  */
end_comment

begin_class
specifier|public
class|class
name|FilterCalcMergeRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|FilterCalcMergeRule
name|INSTANCE
init|=
operator|new
name|FilterCalcMergeRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|FilterCalcMergeRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Filter
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
name|LogicalFilter
name|filter
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
comment|// Don't merge a filter onto a calc which contains windowed aggregates.
comment|// That would effectively be pushing a multiset down through a filter.
comment|// We'll have chance to merge later, when the over is expanded.
if|if
condition|(
name|calc
operator|.
name|getProgram
argument_list|()
operator|.
name|containsAggs
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Create a program containing the filter.
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|filter
operator|.
name|getCluster
argument_list|()
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
name|progBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
name|progBuilder
operator|.
name|addCondition
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
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
name|LogicalCalc
operator|.
name|create
argument_list|(
name|calc
operator|.
name|getInput
argument_list|()
argument_list|,
name|mergedProgram
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
comment|// End FilterCalcMergeRule.java
end_comment

end_unit

