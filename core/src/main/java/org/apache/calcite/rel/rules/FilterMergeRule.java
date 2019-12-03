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
name|Contexts
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

begin_comment
comment|/**  * Planner rule that combines two  * {@link org.apache.calcite.rel.logical.LogicalFilter}s.  */
end_comment

begin_class
specifier|public
class|class
name|FilterMergeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|FilterMergeRule
name|INSTANCE
init|=
operator|new
name|FilterMergeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a FilterMergeRule.    */
specifier|public
name|FilterMergeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
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
name|Filter
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|FilterMergeRule
parameter_list|(
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|)
block|{
name|this
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|filterFactory
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
name|Filter
name|topFilter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|bottomFilter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// use RexPrograms to merge the two FilterRels into a single program
comment|// so we can convert the two LogicalFilter conditions to directly
comment|// reference the bottom LogicalFilter's child
name|RexBuilder
name|rexBuilder
init|=
name|topFilter
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexProgram
name|bottomProgram
init|=
name|createProgram
argument_list|(
name|bottomFilter
argument_list|)
decl_stmt|;
name|RexProgram
name|topProgram
init|=
name|createProgram
argument_list|(
name|topFilter
argument_list|)
decl_stmt|;
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
name|RexNode
name|newCondition
init|=
name|mergedProgram
operator|.
name|expandLocalRef
argument_list|(
name|mergedProgram
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
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
name|bottomFilter
operator|.
name|getInput
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|newCondition
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
comment|/**    * Creates a RexProgram corresponding to a LogicalFilter    *    * @param filterRel the LogicalFilter    * @return created RexProgram    */
specifier|private
name|RexProgram
name|createProgram
parameter_list|(
name|Filter
name|filterRel
parameter_list|)
block|{
name|RexProgramBuilder
name|programBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|filterRel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|filterRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|programBuilder
operator|.
name|addIdentity
argument_list|()
expr_stmt|;
name|programBuilder
operator|.
name|addCondition
argument_list|(
name|filterRel
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|programBuilder
operator|.
name|getProgram
argument_list|()
return|;
block|}
block|}
end_class

end_unit

