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
name|JoinRelType
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
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
comment|/**  * Planner rule that creates a {@code SemiJoin} from a  * {@link org.apache.calcite.rel.core.Join} on top of a  * {@link org.apache.calcite.rel.logical.LogicalAggregate} or  * on a {@link org.apache.calcite.rel.RelNode} which is  * unique for join's right keys.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SemiJoinRule
extends|extends
name|RelRule
argument_list|<
name|SemiJoinRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
specifier|private
specifier|static
name|boolean
name|isJoinTypeSupported
parameter_list|(
name|Join
name|join
parameter_list|)
block|{
specifier|final
name|JoinRelType
name|type
init|=
name|join
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
return|return
name|type
operator|==
name|JoinRelType
operator|.
name|INNER
operator|||
name|type
operator|==
name|JoinRelType
operator|.
name|LEFT
return|;
block|}
comment|/**    * Tests if an Aggregate always produces 1 row and 0 columns.    */
specifier|private
specifier|static
name|boolean
name|isEmptyAggregate
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
return|return
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/** Creates a SemiJoinRule. */
specifier|protected
name|SemiJoinRule
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
specifier|protected
name|void
name|perform
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
annotation|@
name|Nullable
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
name|isEmptyAggregate
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
operator|.
name|hints
argument_list|(
name|join
operator|.
name|getHints
argument_list|()
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
comment|/** SemiJoinRule that matches a Project on top of a Join with an Aggregate    * as its right child.    *    * @see CoreRules#PROJECT_TO_SEMI_JOIN */
specifier|public
specifier|static
class|class
name|ProjectToSemiJoinRule
extends|extends
name|SemiJoinRule
block|{
comment|/** Creates a ProjectToSemiJoinRule. */
specifier|protected
name|ProjectToSemiJoinRule
parameter_list|(
name|ProjectToSemiJoinRuleConfig
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
name|this
argument_list|(
name|ProjectToSemiJoinRuleConfig
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|withDescription
argument_list|(
name|description
argument_list|)
operator|.
name|as
argument_list|(
name|ProjectToSemiJoinRuleConfig
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|projectClass
argument_list|,
name|joinClass
argument_list|,
name|aggregateClass
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
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|ProjectToSemiJoinRuleConfig
extends|extends
name|SemiJoinRule
operator|.
name|Config
block|{
name|ProjectToSemiJoinRuleConfig
name|DEFAULT
init|=
name|ImmutableProjectToSemiJoinRuleConfig
operator|.
name|of
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"SemiJoinRule:project"
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|Project
operator|.
name|class
argument_list|,
name|Join
operator|.
name|class
argument_list|,
name|Aggregate
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectToSemiJoinRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectToSemiJoinRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|ProjectToSemiJoinRuleConfig
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|projectClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|joinClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|SemiJoinRule
operator|::
name|isJoinTypeSupported
argument_list|)
operator|.
name|inputs
argument_list|(
name|b3
lambda|->
name|b3
operator|.
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b4
lambda|->
name|b4
operator|.
name|operand
argument_list|(
name|aggregateClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|ProjectToSemiJoinRuleConfig
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
comment|/** SemiJoinRule that matches a Join with an empty Aggregate as its right    * input.    *    * @see CoreRules#JOIN_TO_SEMI_JOIN */
specifier|public
specifier|static
class|class
name|JoinToSemiJoinRule
extends|extends
name|SemiJoinRule
block|{
comment|/** Creates a JoinToSemiJoinRule. */
specifier|protected
name|JoinToSemiJoinRule
parameter_list|(
name|JoinToSemiJoinRuleConfig
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
name|this
argument_list|(
name|JoinToSemiJoinRuleConfig
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|withDescription
argument_list|(
name|description
argument_list|)
operator|.
name|as
argument_list|(
name|JoinToSemiJoinRuleConfig
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|joinClass
argument_list|,
name|aggregateClass
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
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|JoinToSemiJoinRuleConfig
extends|extends
name|SemiJoinRule
operator|.
name|Config
block|{
name|JoinToSemiJoinRuleConfig
name|DEFAULT
init|=
name|ImmutableJoinToSemiJoinRuleConfig
operator|.
name|of
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"SemiJoinRule:join"
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|Join
operator|.
name|class
argument_list|,
name|Aggregate
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|JoinToSemiJoinRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|JoinToSemiJoinRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|JoinToSemiJoinRuleConfig
name|withOperandFor
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
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|joinClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|SemiJoinRule
operator|::
name|isJoinTypeSupported
argument_list|)
operator|.
name|inputs
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b3
lambda|->
name|b3
operator|.
name|operand
argument_list|(
name|aggregateClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|JoinToSemiJoinRuleConfig
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * SemiJoinRule that matches a Project on top of a Join with a RelNode    * which is unique for Join's right keys.    *    * @see CoreRules#JOIN_ON_UNIQUE_TO_SEMI_JOIN */
specifier|public
specifier|static
class|class
name|JoinOnUniqueToSemiJoinRule
extends|extends
name|SemiJoinRule
block|{
comment|/** Creates a JoinOnUniqueToSemiJoinRule. */
specifier|protected
name|JoinOnUniqueToSemiJoinRule
parameter_list|(
name|JoinOnUniqueToSemiJoinRuleConfig
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
name|Override
specifier|public
name|boolean
name|matches
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
return|return
operator|!
name|bits
operator|.
name|intersects
argument_list|(
name|rightBits
argument_list|)
return|;
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
name|RelNode
name|right
init|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|join
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
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
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|Boolean
name|unique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|right
argument_list|,
name|joinInfo
operator|.
name|rightSet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
operator|!=
literal|null
operator|&&
name|unique
condition|)
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|INNER
case|:
name|builder
operator|.
name|push
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|SEMI
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LEFT
case|:
name|builder
operator|.
name|push
argument_list|(
name|left
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|project
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Rule configuration.      */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|JoinOnUniqueToSemiJoinRuleConfig
extends|extends
name|SemiJoinRule
operator|.
name|Config
block|{
name|JoinOnUniqueToSemiJoinRuleConfig
name|DEFAULT
init|=
name|ImmutableJoinOnUniqueToSemiJoinRuleConfig
operator|.
name|of
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"SemiJoinRule:unique"
argument_list|)
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|Project
operator|.
name|class
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|Join
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|SemiJoinRule
operator|::
name|isJoinTypeSupported
argument_list|)
operator|.
name|inputs
argument_list|(
name|b3
lambda|->
name|b3
operator|.
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b4
lambda|->
name|b4
operator|.
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
comment|// If RHS is Aggregate, it will be covered by ProjectToSemiJoinRule
operator|.
name|predicate
argument_list|(
name|n
lambda|->
operator|!
operator|(
name|n
operator|instanceof
name|Aggregate
operator|)
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|JoinOnUniqueToSemiJoinRuleConfig
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|JoinOnUniqueToSemiJoinRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|JoinOnUniqueToSemiJoinRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Rule configuration.    */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
annotation|@
name|Override
name|SemiJoinRule
name|toRule
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

