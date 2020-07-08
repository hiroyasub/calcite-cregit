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
name|JoinInfo
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
name|ImmutableIntList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Planner rule that creates a {@code SemiJoin} from a  * {@link org.apache.calcite.rel.core.Join} on top of a  * {@link org.apache.calcite.rel.logical.LogicalAggregate}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SemiJoinRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|Join
argument_list|>
name|NOT_GENERATE_NULLS_ON_LEFT
init|=
name|join
lambda|->
operator|!
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
decl_stmt|;
comment|/* Tests if an Aggregate always produces 1 row and 0 columns. */
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|Aggregate
argument_list|>
name|IS_EMPTY_AGGREGATE
init|=
name|aggregate
lambda|->
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|0
decl_stmt|;
comment|/** @deprecated Use {@link CoreRules#PROJECT_TO_SEMI_JOIN}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticInitializerReferencesSubClass"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 1.25
specifier|public
specifier|static
specifier|final
name|SemiJoinRule
name|PROJECT
init|=
name|CoreRules
operator|.
name|PROJECT_TO_SEMI_JOIN
decl_stmt|;
comment|/** @deprecated Use {@link CoreRules#JOIN_TO_SEMI_JOIN}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticInitializerReferencesSubClass"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 1.25
specifier|public
specifier|static
specifier|final
name|SemiJoinRule
name|JOIN
init|=
name|CoreRules
operator|.
name|JOIN_TO_SEMI_JOIN
decl_stmt|;
specifier|protected
name|SemiJoinRule
parameter_list|(
name|Class
argument_list|<
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|projectClass
argument_list|,
name|some
argument_list|(
name|operandJ
argument_list|(
name|joinClass
argument_list|,
literal|null
argument_list|,
name|NOT_GENERATE_NULLS_ON_LEFT
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|aggregateClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SemiJoinRule
parameter_list|(
name|Class
argument_list|<
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|joinClass
argument_list|,
literal|null
argument_list|,
name|NOT_GENERATE_NULLS_ON_LEFT
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|aggregateClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|perform
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|Project
name|project
parameter_list|,
name|Join
name|join
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|Aggregate
name|aggregate
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|join
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|project
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ImmutableBitSet
name|bits
init|=
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|rightBits
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|.
name|intersects
argument_list|(
name|rightBits
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|projectsRight
argument_list|()
operator|&&
operator|!
name|IS_EMPTY_AGGREGATE
operator|.
name|test
argument_list|(
name|aggregate
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|join
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|joinInfo
operator|.
name|rightSet
argument_list|()
operator|.
name|equals
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|aggregate
operator|.
name|getGroupCount
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
comment|// Rule requires that aggregate key to be the same as the join key.
comment|// By the way, neither a super-set nor a sub-set would work.
return|return;
block|}
if|if
condition|(
operator|!
name|joinInfo
operator|.
name|isEqui
argument_list|()
condition|)
block|{
return|return;
block|}
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
name|left
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|SEMI
case|:
case|case
name|INNER
case|:
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newRightKeyBuilder
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|aggregateKeys
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|joinInfo
operator|.
name|rightKeys
control|)
block|{
name|newRightKeyBuilder
operator|.
name|add
argument_list|(
name|aggregateKeys
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ImmutableIntList
name|newRightKeys
init|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|newRightKeyBuilder
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|aggregate
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|newCondition
init|=
name|RelOptUtil
operator|.
name|createEquiJoinCondition
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
name|joinInfo
operator|.
name|leftKeys
argument_list|,
name|relBuilder
operator|.
name|peek
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|newRightKeys
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|semiJoin
argument_list|(
name|newCondition
argument_list|)
expr_stmt|;
break|break;
case|case
name|LEFT
case|:
comment|// The right-hand side produces no more than 1 row (because of the
comment|// Aggregate) and no fewer than 1 row (because of LEFT), and therefore
comment|// we can eliminate the semi-join.
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|project
operator|!=
literal|null
condition|)
block|{
name|relBuilder
operator|.
name|project
argument_list|(
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
expr_stmt|;
block|}
specifier|final
name|RelNode
name|relNode
init|=
name|relBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relNode
argument_list|)
expr_stmt|;
block|}
comment|/** SemiJoinRule that matches a Project on top of a Join with an Aggregate    * as its right child. */
specifier|public
specifier|static
class|class
name|ProjectToSemiJoinRule
extends|extends
name|SemiJoinRule
block|{
comment|/** Creates a ProjectToSemiJoinRule. */
specifier|public
name|ProjectToSemiJoinRule
parameter_list|(
name|Class
argument_list|<
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|projectClass
argument_list|,
name|joinClass
argument_list|,
name|aggregateClass
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
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
name|Project
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
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|perform
argument_list|(
name|call
argument_list|,
name|project
argument_list|,
name|join
argument_list|,
name|left
argument_list|,
name|aggregate
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SemiJoinRule that matches a Join with an empty Aggregate as its right    * child. */
specifier|public
specifier|static
class|class
name|JoinToSemiJoinRule
extends|extends
name|SemiJoinRule
block|{
comment|/** Creates a JoinToSemiJoinRule. */
specifier|public
name|JoinToSemiJoinRule
parameter_list|(
name|Class
argument_list|<
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|joinClass
argument_list|,
name|aggregateClass
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
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
name|Join
name|join
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
name|left
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|perform
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|join
argument_list|,
name|left
argument_list|,
name|aggregate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

