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
name|RexUtil
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

begin_comment
comment|/**  * Planner rule that matches an {@link org.apache.calcite.rel.core.Aggregate}  * on a {@link org.apache.calcite.rel.core.Join} and removes the left input  * of the join provided that the left input is also a left join if possible.  *  *<p>For instance,  *  *<blockquote>  *<pre>select distinct s.product_id, pc.product_id  * from sales as s  * left join product as p  *   on s.product_id = p.product_id  * left join product_class pc  *   on s.product_id = pc.product_id</pre></blockquote>  *  *<p>becomes  *  *<blockquote>  *<pre>select distinct s.product_id, pc.product_id  * from sales as s  * left join product_class pc  *   on s.product_id = pc.product_id</pre></blockquote>  *  * @see CoreRules#AGGREGATE_JOIN_JOIN_REMOVE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|AggregateJoinJoinRemoveRule
extends|extends
name|RelRule
argument_list|<
name|AggregateJoinJoinRemoveRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates an AggregateJoinJoinRemoveRule. */
specifier|protected
name|AggregateJoinJoinRemoveRule
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
name|AggregateJoinJoinRemoveRule
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
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
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
name|joinClass
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
name|Join
name|topJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|bottomJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|int
name|leftBottomChildSize
init|=
name|bottomJoin
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
comment|// Check whether the aggregate uses columns in the right input of
comment|// bottom join.
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|allFields
init|=
name|RelOptUtil
operator|.
name|getAllFields
argument_list|(
name|aggregate
argument_list|)
decl_stmt|;
if|if
condition|(
name|allFields
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|i
lambda|->
name|i
operator|>=
name|leftBottomChildSize
operator|&&
name|i
operator|<
name|bottomJoin
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|aggregateCall
lambda|->
operator|!
name|aggregateCall
operator|.
name|isDistinct
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Check whether the top join uses columns in the right input of bottom join.
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|topJoin
operator|.
name|getLeft
argument_list|()
argument_list|,
name|topJoin
operator|.
name|getRight
argument_list|()
argument_list|,
name|topJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftKeys
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|leftKeys
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|s
lambda|->
name|s
operator|>=
name|leftBottomChildSize
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Check whether left join keys in top join and bottom join are equal.
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftChildKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|bottomJoin
operator|.
name|getLeft
argument_list|()
argument_list|,
name|bottomJoin
operator|.
name|getRight
argument_list|()
argument_list|,
name|bottomJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftChildKeys
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|leftKeys
operator|.
name|equals
argument_list|(
name|leftChildKeys
argument_list|)
condition|)
block|{
return|return;
block|}
name|int
name|offset
init|=
name|bottomJoin
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
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
name|RexNode
name|condition
init|=
name|RexUtil
operator|.
name|shift
argument_list|(
name|topJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftBottomChildSize
argument_list|,
operator|-
name|offset
argument_list|)
decl_stmt|;
name|RelNode
name|join
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|bottomJoin
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|.
name|push
argument_list|(
name|topJoin
operator|.
name|getRight
argument_list|()
argument_list|)
operator|.
name|join
argument_list|(
name|topJoin
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|condition
argument_list|)
operator|.
name|build
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
name|allFields
operator|.
name|forEach
argument_list|(
name|index
lambda|->
name|map
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|index
operator|<
name|leftBottomChildSize
condition|?
name|index
else|:
name|index
operator|-
name|offset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableBitSet
name|groupSet
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
name|sourceCount
argument_list|)
decl_stmt|;
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|forEach
argument_list|(
name|aggregateCall
lambda|->
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
argument_list|)
expr_stmt|;
name|RelNode
name|newAggregate
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|join
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|groupSet
argument_list|)
argument_list|,
name|aggCalls
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newAggregate
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
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableAggregateJoinJoinRemoveRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateJoinJoinRemoveRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateJoinJoinRemoveRule
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
name|Join
argument_list|>
name|joinClass
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
name|joinClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|join
lambda|->
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
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
name|joinClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|join
lambda|->
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
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

