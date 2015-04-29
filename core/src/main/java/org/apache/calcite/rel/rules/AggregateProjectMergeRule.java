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
name|util
operator|.
name|ImmutableBitSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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

begin_comment
comment|/**  * Planner rule that recognizes a {@link org.apache.calcite.rel.core.Aggregate}  * on top of a {@link org.apache.calcite.rel.core.Project} and if possible  * aggregate through the project or removes the project.  *  *<p>This is only possible when the grouping expressions and arguments to  * the aggregate functions are field references (i.e. not expressions).  *  *<p>In some cases, this rule has the effect of trimming: the aggregate will  * use fewer columns than the project did.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateProjectMergeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateProjectMergeRule
name|INSTANCE
init|=
operator|new
name|AggregateProjectMergeRule
argument_list|()
decl_stmt|;
comment|/** Private constructor. */
specifier|private
name|AggregateProjectMergeRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Aggregate
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
name|RelNode
name|apply
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|,
name|Project
name|project
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newKeys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|key
range|:
name|aggregate
operator|.
name|getGroupSet
argument_list|()
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
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|rex
operator|instanceof
name|RexInputRef
condition|)
block|{
specifier|final
name|int
name|newKey
init|=
operator|(
operator|(
name|RexInputRef
operator|)
name|rex
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|newKeys
operator|.
name|add
argument_list|(
name|newKey
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|newKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Cannot handle "GROUP BY expression"
return|return
literal|null
return|;
block|}
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
name|indicator
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
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Integer
argument_list|>
name|newArgs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|arg
range|:
name|aggregateCall
operator|.
name|getArgList
argument_list|()
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
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
name|rex
operator|instanceof
name|RexInputRef
condition|)
block|{
name|newArgs
operator|.
name|add
argument_list|(
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
else|else
block|{
comment|// Cannot handle "AGG(expression)"
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|int
name|newFilterArg
decl_stmt|;
if|if
condition|(
name|aggregateCall
operator|.
name|filterArg
operator|>=
literal|0
condition|)
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
name|aggregateCall
operator|.
name|filterArg
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
name|newFilterArg
operator|=
operator|(
operator|(
name|RexInputRef
operator|)
name|rex
operator|)
operator|.
name|getIndex
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|newFilterArg
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|aggCalls
operator|.
name|add
argument_list|(
name|aggregateCall
operator|.
name|copy
argument_list|(
name|newArgs
operator|.
name|build
argument_list|()
argument_list|,
name|newFilterArg
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
name|aggregate
operator|.
name|indicator
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
name|RelNode
name|rel
init|=
name|newAggregate
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
name|Lists
operator|.
name|newArrayList
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
if|if
condition|(
name|aggregate
operator|.
name|indicator
condition|)
block|{
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
name|aggregate
operator|.
name|getGroupCount
argument_list|()
operator|+
name|newGroupSet
operator|.
name|indexOf
argument_list|(
name|newKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|+
name|newAggregate
operator|.
name|getIndicatorCount
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
name|rel
operator|=
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|RelFactories
operator|.
name|DEFAULT_PROJECT_FACTORY
argument_list|,
name|rel
argument_list|,
name|posList
argument_list|)
expr_stmt|;
block|}
return|return
name|rel
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateProjectMergeRule.java
end_comment

end_unit

