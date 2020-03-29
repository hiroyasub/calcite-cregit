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
operator|.
name|materialize
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
name|plan
operator|.
name|hep
operator|.
name|HepProgram
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
name|Aggregate
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/** Rule that matches Aggregate. */
end_comment

begin_class
specifier|public
class|class
name|MaterializedViewOnlyAggregateRule
extends|extends
name|MaterializedViewAggregateRule
block|{
specifier|public
specifier|static
specifier|final
name|MaterializedViewOnlyAggregateRule
name|INSTANCE
init|=
operator|new
name|MaterializedViewOnlyAggregateRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
name|MaterializedViewOnlyAggregateRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|boolean
name|generateUnionRewriting
parameter_list|,
name|HepProgram
name|unionRewritingPullProgram
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"MaterializedViewAggregateRule(Aggregate)"
argument_list|,
name|generateUnionRewriting
argument_list|,
name|unionRewritingPullProgram
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MaterializedViewOnlyAggregateRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|,
name|boolean
name|generateUnionRewriting
parameter_list|,
name|HepProgram
name|unionRewritingPullProgram
parameter_list|,
name|RelOptRule
name|filterProjectTransposeRule
parameter_list|,
name|RelOptRule
name|filterAggregateTransposeRule
parameter_list|,
name|RelOptRule
name|aggregateProjectPullUpConstantsRule
parameter_list|,
name|RelOptRule
name|projectMergeRule
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"MaterializedViewAggregateRule(Aggregate)"
argument_list|,
name|generateUnionRewriting
argument_list|,
name|unionRewritingPullProgram
argument_list|,
name|filterProjectTransposeRule
argument_list|,
name|filterAggregateTransposeRule
argument_list|,
name|aggregateProjectPullUpConstantsRule
argument_list|,
name|projectMergeRule
argument_list|)
expr_stmt|;
block|}
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
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|perform
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|aggregate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
