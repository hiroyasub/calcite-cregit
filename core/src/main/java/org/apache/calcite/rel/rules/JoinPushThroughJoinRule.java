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
name|core
operator|.
name|RelFactories
operator|.
name|ProjectFactory
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
name|RexPermuteInputsShuttle
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
name|ImmutableBeans
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
comment|/**  * Rule that pushes the right input of a join into through the left input of  * the join, provided that the left input is also a join.  *  *<p>Thus, {@code (A join B) join C} becomes {@code (A join C) join B}. The  * advantage of applying this rule is that it may be possible to apply  * conditions earlier. For instance,  *  *<blockquote>  *<pre>(sales as s join product_class as pc on true)  * join product as p  * on s.product_id = p.product_id  * and p.product_class_id = pc.product_class_id</pre></blockquote>  *  *<p>becomes  *  *<blockquote>  *<pre>(sales as s join product as p on s.product_id = p.product_id)  * join product_class as pc  * on p.product_class_id = pc.product_class_id</pre></blockquote>  *  *<p>Before the rule, one join has two conditions and the other has none  * ({@code ON TRUE}). After the rule, each join has one condition.</p>  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|JoinPushThroughJoinRule
extends|extends
name|RelRule
argument_list|<
name|JoinPushThroughJoinRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Instance of the rule that works on logical joins only, and pushes to the    * right. */
specifier|public
specifier|static
specifier|final
name|JoinPushThroughJoinRule
name|RIGHT
init|=
name|Config
operator|.
name|RIGHT
operator|.
name|toRule
argument_list|()
decl_stmt|;
comment|/** Instance of the rule that works on logical joins only, and pushes to the    * left. */
specifier|public
specifier|static
specifier|final
name|JoinPushThroughJoinRule
name|LEFT
init|=
name|Config
operator|.
name|LEFT
operator|.
name|toRule
argument_list|()
decl_stmt|;
comment|/** Creates a JoinPushThroughJoinRule. */
specifier|protected
name|JoinPushThroughJoinRule
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
name|JoinPushThroughJoinRule
parameter_list|(
name|String
name|description
parameter_list|,
name|boolean
name|right
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
name|LEFT
operator|.
name|withDescription
argument_list|(
name|description
argument_list|)
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
name|joinClass
argument_list|)
operator|.
name|withRight
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinPushThroughJoinRule
parameter_list|(
name|String
name|description
parameter_list|,
name|boolean
name|right
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|LEFT
operator|.
name|withDescription
argument_list|(
name|description
argument_list|)
operator|.
name|withRelBuilderFactory
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|projectFactory
argument_list|)
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
name|joinClass
argument_list|)
operator|.
name|withRight
argument_list|(
name|right
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
if|if
condition|(
name|config
operator|.
name|isRight
argument_list|()
condition|)
block|{
name|onMatchRight
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onMatchLeft
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|onMatchRight
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Join
name|topJoin
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
name|bottomJoin
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
name|relC
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
name|relA
init|=
name|bottomJoin
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|relB
init|=
name|bottomJoin
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|topJoin
operator|.
name|getCluster
argument_list|()
decl_stmt|;
comment|//        topJoin
comment|//        /     \
comment|//   bottomJoin  C
comment|//    /    \
comment|//   A      B
specifier|final
name|int
name|aCount
init|=
name|relA
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bCount
init|=
name|relB
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|cCount
init|=
name|relC
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|bBitSet
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|aCount
argument_list|,
name|aCount
operator|+
name|bCount
argument_list|)
decl_stmt|;
comment|// becomes
comment|//
comment|//        newTopJoin
comment|//        /        \
comment|//   newBottomJoin  B
comment|//    /    \
comment|//   A      C
comment|// If either join is not inner, we cannot proceed.
comment|// (Is this too strict?)
if|if
condition|(
name|topJoin
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
operator|||
name|bottomJoin
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
return|return;
block|}
comment|// Split the condition of topJoin into a conjunction. Each of the
comment|// parts that does not use columns from B can be pushed down.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|intersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|nonIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|split
argument_list|(
name|topJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|bBitSet
argument_list|,
name|intersecting
argument_list|,
name|nonIntersecting
argument_list|)
expr_stmt|;
comment|// If there's nothing to push down, it's not worth proceeding.
if|if
condition|(
name|nonIntersecting
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Split the condition of bottomJoin into a conjunction. Each of the
comment|// parts that use columns from B will need to be pulled up.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|bottomIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|bottomNonIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|split
argument_list|(
name|bottomJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|bBitSet
argument_list|,
name|bottomIntersecting
argument_list|,
name|bottomNonIntersecting
argument_list|)
expr_stmt|;
comment|// target: | A       | C      |
comment|// source: | A       | B | C      |
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|bottomMapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|aCount
operator|+
name|bCount
operator|+
name|cCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|aCount
argument_list|,
name|aCount
argument_list|,
name|aCount
operator|+
name|bCount
argument_list|,
name|cCount
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newBottomList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomMapping
argument_list|,
name|relA
argument_list|,
name|relC
argument_list|)
operator|.
name|visitList
argument_list|(
name|nonIntersecting
argument_list|,
name|newBottomList
argument_list|)
expr_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomMapping
argument_list|,
name|relA
argument_list|,
name|relC
argument_list|)
operator|.
name|visitList
argument_list|(
name|bottomNonIntersecting
argument_list|,
name|newBottomList
argument_list|)
expr_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexNode
name|newBottomCondition
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newBottomList
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|newBottomJoin
init|=
name|bottomJoin
operator|.
name|copy
argument_list|(
name|bottomJoin
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newBottomCondition
argument_list|,
name|relA
argument_list|,
name|relC
argument_list|,
name|bottomJoin
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|bottomJoin
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
comment|// target: | A       | C      | B |
comment|// source: | A       | B | C      |
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|topMapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|aCount
operator|+
name|bCount
operator|+
name|cCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|aCount
argument_list|,
name|aCount
operator|+
name|cCount
argument_list|,
name|aCount
argument_list|,
name|bCount
argument_list|,
name|aCount
argument_list|,
name|aCount
operator|+
name|bCount
argument_list|,
name|cCount
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newTopList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|topMapping
argument_list|,
name|newBottomJoin
argument_list|,
name|relB
argument_list|)
operator|.
name|visitList
argument_list|(
name|intersecting
argument_list|,
name|newTopList
argument_list|)
expr_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|topMapping
argument_list|,
name|newBottomJoin
argument_list|,
name|relB
argument_list|)
operator|.
name|visitList
argument_list|(
name|bottomIntersecting
argument_list|,
name|newTopList
argument_list|)
expr_stmt|;
name|RexNode
name|newTopCondition
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newTopList
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SuspiciousNameCombination"
argument_list|)
specifier|final
name|Join
name|newTopJoin
init|=
name|topJoin
operator|.
name|copy
argument_list|(
name|topJoin
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newTopCondition
argument_list|,
name|newBottomJoin
argument_list|,
name|relB
argument_list|,
name|topJoin
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|topJoin
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|Mappings
operator|.
name|isIdentity
argument_list|(
name|topMapping
argument_list|)
assert|;
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
name|newTopJoin
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|topMapping
argument_list|)
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
comment|/**    * Similar to {@link #onMatch}, but swaps the upper sibling with the left    * of the two lower siblings, rather than the right.    */
specifier|private
specifier|static
name|void
name|onMatchLeft
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Join
name|topJoin
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
name|bottomJoin
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
name|relC
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
name|relA
init|=
name|bottomJoin
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|relB
init|=
name|bottomJoin
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|topJoin
operator|.
name|getCluster
argument_list|()
decl_stmt|;
comment|//        topJoin
comment|//        /     \
comment|//   bottomJoin  C
comment|//    /    \
comment|//   A      B
specifier|final
name|int
name|aCount
init|=
name|relA
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bCount
init|=
name|relB
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|cCount
init|=
name|relC
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|aBitSet
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|aCount
argument_list|)
decl_stmt|;
comment|// becomes
comment|//
comment|//        newTopJoin
comment|//        /        \
comment|//   newBottomJoin  A
comment|//    /    \
comment|//   C      B
comment|// If either join is not inner, we cannot proceed.
comment|// (Is this too strict?)
if|if
condition|(
name|topJoin
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
operator|||
name|bottomJoin
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
return|return;
block|}
comment|// Split the condition of topJoin into a conjunction. Each of the
comment|// parts that does not use columns from A can be pushed down.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|intersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|nonIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|split
argument_list|(
name|topJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|aBitSet
argument_list|,
name|intersecting
argument_list|,
name|nonIntersecting
argument_list|)
expr_stmt|;
comment|// If there's nothing to push down, it's not worth proceeding.
if|if
condition|(
name|nonIntersecting
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Split the condition of bottomJoin into a conjunction. Each of the
comment|// parts that use columns from A will need to be pulled up.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|bottomIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|bottomNonIntersecting
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|split
argument_list|(
name|bottomJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|aBitSet
argument_list|,
name|bottomIntersecting
argument_list|,
name|bottomNonIntersecting
argument_list|)
expr_stmt|;
comment|// target: | C      | B |
comment|// source: | A       | B | C      |
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|bottomMapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|aCount
operator|+
name|bCount
operator|+
name|cCount
argument_list|,
name|cCount
argument_list|,
name|aCount
argument_list|,
name|bCount
argument_list|,
literal|0
argument_list|,
name|aCount
operator|+
name|bCount
argument_list|,
name|cCount
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newBottomList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomMapping
argument_list|,
name|relC
argument_list|,
name|relB
argument_list|)
operator|.
name|visitList
argument_list|(
name|nonIntersecting
argument_list|,
name|newBottomList
argument_list|)
expr_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomMapping
argument_list|,
name|relC
argument_list|,
name|relB
argument_list|)
operator|.
name|visitList
argument_list|(
name|bottomNonIntersecting
argument_list|,
name|newBottomList
argument_list|)
expr_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexNode
name|newBottomCondition
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newBottomList
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|newBottomJoin
init|=
name|bottomJoin
operator|.
name|copy
argument_list|(
name|bottomJoin
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newBottomCondition
argument_list|,
name|relC
argument_list|,
name|relB
argument_list|,
name|bottomJoin
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|bottomJoin
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
comment|// target: | C      | B | A       |
comment|// source: | A       | B | C      |
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|topMapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|aCount
operator|+
name|bCount
operator|+
name|cCount
argument_list|,
name|cCount
operator|+
name|bCount
argument_list|,
literal|0
argument_list|,
name|aCount
argument_list|,
name|cCount
argument_list|,
name|aCount
argument_list|,
name|bCount
argument_list|,
literal|0
argument_list|,
name|aCount
operator|+
name|bCount
argument_list|,
name|cCount
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newTopList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|topMapping
argument_list|,
name|newBottomJoin
argument_list|,
name|relA
argument_list|)
operator|.
name|visitList
argument_list|(
name|intersecting
argument_list|,
name|newTopList
argument_list|)
expr_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|topMapping
argument_list|,
name|newBottomJoin
argument_list|,
name|relA
argument_list|)
operator|.
name|visitList
argument_list|(
name|bottomIntersecting
argument_list|,
name|newTopList
argument_list|)
expr_stmt|;
name|RexNode
name|newTopCondition
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newTopList
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SuspiciousNameCombination"
argument_list|)
specifier|final
name|Join
name|newTopJoin
init|=
name|topJoin
operator|.
name|copy
argument_list|(
name|topJoin
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newTopCondition
argument_list|,
name|newBottomJoin
argument_list|,
name|relA
argument_list|,
name|topJoin
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|topJoin
operator|.
name|isSemiJoinDone
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
name|newTopJoin
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|topMapping
argument_list|)
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
comment|/**    * Splits a condition into conjunctions that do or do not intersect with    * a given bit set.    */
specifier|static
name|void
name|split
parameter_list|(
name|RexNode
name|condition
parameter_list|,
name|ImmutableBitSet
name|bitSet
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|intersecting
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|nonIntersecting
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|node
range|:
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|condition
argument_list|)
control|)
block|{
name|ImmutableBitSet
name|inputBitSet
init|=
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitSet
operator|.
name|intersects
argument_list|(
name|inputBitSet
argument_list|)
condition|)
block|{
name|intersecting
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nonIntersecting
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
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
name|RIGHT
init|=
name|ImmutableJoinPushThroughJoinRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"JoinPushThroughJoinRule:right"
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|withRight
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Config
name|LEFT
init|=
name|ImmutableJoinPushThroughJoinRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"JoinPushThroughJoinRule:left"
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|)
operator|.
name|withRight
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|JoinPushThroughJoinRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|JoinPushThroughJoinRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Whether to push on the right. If false, push to the left. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|false
argument_list|)
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|isRight
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Sets {@link #isRight()}. */
name|Config
name|withRight
parameter_list|(
name|boolean
name|right
parameter_list|)
function_decl|;
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
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
name|joinClass
argument_list|)
operator|.
name|inputs
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
name|anyInputs
argument_list|()
argument_list|,
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
name|predicate
argument_list|(
name|n
lambda|->
operator|!
name|n
operator|.
name|isEnforcer
argument_list|()
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

