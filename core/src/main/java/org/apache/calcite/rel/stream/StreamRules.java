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
name|stream
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
name|plan
operator|.
name|RelOptTable
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
name|prepare
operator|.
name|RelOptTableImpl
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
name|core
operator|.
name|Sort
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
name|TableScan
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
name|Union
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
name|Values
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
name|LogicalAggregate
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
name|logical
operator|.
name|LogicalSort
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
name|LogicalTableScan
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
name|LogicalUnion
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
name|schema
operator|.
name|StreamableTable
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
name|schema
operator|.
name|Table
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
name|Util
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
comment|/**  * Rules and relational operators for streaming relational expressions.  */
end_comment

begin_class
specifier|public
class|class
name|StreamRules
block|{
specifier|private
name|StreamRules
parameter_list|()
block|{
block|}
specifier|public
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRule
argument_list|>
name|RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|DeltaProjectTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaFilterTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaAggregateTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaSortTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaUnionTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaJoinTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaTableScanRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|,
operator|new
name|DeltaTableScanToEmptyRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Planner rule that pushes a {@link Delta} through a {@link Project}. */
specifier|public
specifier|static
class|class
name|DeltaProjectTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaProjectTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaProjectTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Project
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
specifier|final
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LogicalDelta
name|newDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalProject
name|newProject
init|=
name|LogicalProject
operator|.
name|create
argument_list|(
name|newDelta
argument_list|,
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
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newProject
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Planner rule that pushes a {@link Delta} through a {@link Filter}. */
specifier|public
specifier|static
class|class
name|DeltaFilterTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaFilterTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaFilterTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
specifier|final
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LogicalDelta
name|newDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalFilter
name|newFilter
init|=
name|LogicalFilter
operator|.
name|create
argument_list|(
name|newDelta
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Planner rule that pushes a {@link Delta} through an {@link Aggregate}. */
specifier|public
specifier|static
class|class
name|DeltaAggregateTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaAggregateTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaAggregateTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operandJ
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|::
name|isSimple
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
specifier|final
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LogicalDelta
name|newDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|aggregate
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalAggregate
name|newAggregate
init|=
name|LogicalAggregate
operator|.
name|create
argument_list|(
name|newDelta
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggregate
operator|.
name|groupSets
argument_list|,
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newAggregate
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Planner rule that pushes a {@link Delta} through an {@link Sort}. */
specifier|public
specifier|static
class|class
name|DeltaSortTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaSortTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaSortTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Sort
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
specifier|final
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LogicalDelta
name|newDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalSort
name|newSort
init|=
name|LogicalSort
operator|.
name|create
argument_list|(
name|newDelta
argument_list|,
name|sort
operator|.
name|collation
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newSort
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Planner rule that pushes a {@link Delta} through an {@link Union}. */
specifier|public
specifier|static
class|class
name|DeltaUnionTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaUnionTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaUnionTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Union
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
specifier|final
name|Union
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|union
operator|.
name|getInputs
argument_list|()
control|)
block|{
specifier|final
name|LogicalDelta
name|newDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|newInputs
operator|.
name|add
argument_list|(
name|newDelta
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LogicalUnion
name|newUnion
init|=
name|LogicalUnion
operator|.
name|create
argument_list|(
name|newInputs
argument_list|,
name|union
operator|.
name|all
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newUnion
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Planner rule that pushes a {@link Delta} into a {@link TableScan} of a    * {@link org.apache.calcite.schema.StreamableTable}.    *    *<p>Very likely, the stream was only represented as a table for uniformity    * with the other relations in the system. The Delta disappears and the stream    * can be implemented directly. */
specifier|public
specifier|static
class|class
name|DeltaTableScanRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaTableScanRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaTableScanRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|TableScan
operator|.
name|class
argument_list|,
name|none
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|delta
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|relOptTable
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|StreamableTable
name|streamableTable
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|StreamableTable
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|streamableTable
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Table
name|table1
init|=
name|streamableTable
operator|.
name|stream
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|relOptTable2
init|=
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|relOptTable
operator|.
name|getRelOptSchema
argument_list|()
argument_list|,
name|relOptTable
operator|.
name|getRowType
argument_list|()
argument_list|,
name|table1
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|relOptTable
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"(STREAM)"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalTableScan
name|newScan
init|=
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptTable2
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newScan
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Planner rule that converts {@link Delta} over a {@link TableScan} of    * a table other than {@link org.apache.calcite.schema.StreamableTable} to    * an empty {@link Values}.    */
specifier|public
specifier|static
class|class
name|DeltaTableScanToEmptyRule
extends|extends
name|RelOptRule
block|{
comment|/**      * Creates a DeltaTableScanToEmptyRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaTableScanToEmptyRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|TableScan
operator|.
name|class
argument_list|,
name|none
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
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelOptTable
name|relOptTable
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|StreamableTable
name|streamableTable
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|StreamableTable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamableTable
operator|==
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|values
argument_list|(
name|delta
operator|.
name|getRowType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Planner rule that pushes a {@link Delta} through a {@link Join}.    *    *<p>We apply something analogous to the    *<a href="https://en.wikipedia.org/wiki/Product_rule">product rule of    * differential calculus</a> to implement the transpose:    *    *<blockquote><code>stream(x join y)&rarr;    * x join stream(y) union all stream(x) join y</code></blockquote>    */
specifier|public
specifier|static
class|class
name|DeltaJoinTransposeRule
extends|extends
name|RelOptRule
block|{
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|DeltaJoinTransposeRule
parameter_list|()
block|{
name|this
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a DeltaJoinTransposeRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|DeltaJoinTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Delta
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|Join
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
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Delta
name|delta
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|delta
argument_list|)
expr_stmt|;
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
specifier|final
name|RelNode
name|left
init|=
name|join
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|right
init|=
name|join
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
name|LogicalDelta
name|rightWithDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|right
argument_list|)
decl_stmt|;
specifier|final
name|LogicalJoin
name|joinL
init|=
name|LogicalJoin
operator|.
name|create
argument_list|(
name|left
argument_list|,
name|rightWithDelta
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|join
operator|.
name|getVariablesSet
argument_list|()
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
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|join
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|LogicalDelta
name|leftWithDelta
init|=
name|LogicalDelta
operator|.
name|create
argument_list|(
name|left
argument_list|)
decl_stmt|;
specifier|final
name|LogicalJoin
name|joinR
init|=
name|LogicalJoin
operator|.
name|create
argument_list|(
name|leftWithDelta
argument_list|,
name|right
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|join
operator|.
name|getVariablesSet
argument_list|()
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
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|join
operator|.
name|getSystemFieldList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputsToUnion
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|inputsToUnion
operator|.
name|add
argument_list|(
name|joinL
argument_list|)
expr_stmt|;
name|inputsToUnion
operator|.
name|add
argument_list|(
name|joinR
argument_list|)
expr_stmt|;
specifier|final
name|LogicalUnion
name|newNode
init|=
name|LogicalUnion
operator|.
name|create
argument_list|(
name|inputsToUnion
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

