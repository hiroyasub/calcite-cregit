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
name|adapter
operator|.
name|enumerable
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/** Variant of {@link org.apache.calcite.rel.rules.ProjectToCalcRule} for  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableProjectToCalcRule
extends|extends
name|RelOptRule
block|{
comment|/**    * Creates an EnumerableProjectToCalcRule.    *    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|EnumerableProjectToCalcRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|EnumerableProject
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|EnumerableProject
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
name|RelNode
name|input
init|=
name|project
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|input
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
name|project
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|EnumerableCalc
name|calc
init|=
name|EnumerableCalc
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|program
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|calc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

