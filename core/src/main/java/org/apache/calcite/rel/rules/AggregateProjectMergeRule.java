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
name|RelOptUtil
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
name|Aggregate
operator|.
name|Group
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
name|AggregateCall
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
name|rex
operator|.
name|RexInputRef
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
name|ImmutableBitSet
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|mapping
operator|.
name|Mappings
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Planner rule that recognizes a {@link org.apache.calcite.rel.core.Aggregate}  * on top of a {@link org.apache.calcite.rel.core.Project} and if possible  * aggregate through the project or removes the project.  *  *<p>This is only possible when the grouping expressions and arguments to  * the aggregate functions are field references (i.e. not expressions).  *  *<p>In some cases, this rule has the effect of trimming: the aggregate will  * use fewer columns than the project did.  *  * @see CoreRules#AGGREGATE_PROJECT_MERGE  */
end_comment

begin_class
specifier|public
class|class
name|AggregateProjectMergeRule
extends|extends
name|RelRule
argument_list|<
name|AggregateProjectMergeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates an AggregateProjectMergeRule. */
specifier|protected
name|AggregateProjectMergeRule
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
name|AggregateProjectMergeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|CoreRules
operator|.
name|AGGREGATE_PROJECT_MERGE
operator|.
name|config
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
operator|.
name|withOperandFor
argument_list|(
name|aggregateClass
argument_list|,
name|projectClass
argument_list|)
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
name|RelNode
name|x
init|=
name|apply
argument_list|(
name|call
argument_list|,
name|aggregate
argument_list|,
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
annotation|@
name|Nullable
name|RelNode
name|apply
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Aggregate
name|aggregate
parameter_list|,
name|Project
name|project
parameter_list|)
block|{
comment|// Find all fields which we need to be straightforward field projections.
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|interestingFields
init|=
name|RelOptUtil
operator|.
name|getAllFields
argument_list|(
name|aggregate
argument_list|)
decl_stmt|;
comment|// Build the map from old to new; abort if any entry is not a
comment|// straightforward field projection.
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|source
range|:
name|interestingFields
control|)
block|{
specifier|final
name|RexNode
name|rex
init|=
name|project
operator|.
name|getProjects
argument_list|()
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|rex
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|map
operator|.
name|put
argument_list|(
name|source
argument_list|,
operator|(
operator|(
name|RexInputRef
operator|)
name|rex
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ImmutableBitSet
name|newGroupSet
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|permute
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|newGroupingSets
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|!=
name|Group
operator|.
name|SIMPLE
condition|)
block|{
name|newGroupingSets
operator|=
name|ImmutableBitSet
operator|.
name|ORDERING
operator|.
name|immutableSortedCopy
argument_list|(
name|ImmutableBitSet
operator|.
name|permute
argument_list|(
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|,
name|map
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|sourceCount
init|=
name|aggregate
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
specifier|final
name|int
name|targetCount
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
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|targetMapping
init|=
name|Mappings
operator|.
name|target
argument_list|(
name|map
argument_list|,
name|sourceCount
argument_list|,
name|targetCount
argument_list|)
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggregateCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
name|aggCalls
operator|.
name|add
argument_list|(
name|aggregateCall
operator|.
name|transform
argument_list|(
name|targetMapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Aggregate
name|newAggregate
init|=
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|newGroupSet
argument_list|,
name|newGroupingSets
argument_list|,
name|aggCalls
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add a project if the group set is not in the same order or
comment|// contains duplicates.
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
name|newAggregate
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newKeys
init|=
name|Util
operator|.
name|transform
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asList
argument_list|()
argument_list|,
name|key
lambda|->
name|requireNonNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"no value found for key "
operator|+
name|key
operator|+
literal|" in "
operator|+
name|map
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newKeys
operator|.
name|equals
argument_list|(
name|newGroupSet
operator|.
name|asList
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|posList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|newKey
range|:
name|newKeys
control|)
block|{
name|posList
operator|.
name|add
argument_list|(
name|newGroupSet
operator|.
name|indexOf
argument_list|(
name|newKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|newAggregate
operator|.
name|getGroupCount
argument_list|()
init|;
name|i
operator|<
name|newAggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|posList
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|posList
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Rule configuration. */
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
name|EMPTY
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|,
name|Project
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateProjectMergeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateProjectMergeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|aggregateClass
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
name|projectClass
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
return|;
block|}
block|}
block|}
end_class

end_unit

