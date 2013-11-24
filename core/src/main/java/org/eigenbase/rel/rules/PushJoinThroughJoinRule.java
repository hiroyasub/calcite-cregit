begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
operator|.
name|BitSets
import|;
end_import

begin_comment
comment|/**  * Rule that pushes the right input of a join into through the left input of  * the join, provided that the left input is also a join.  *  *<p>Thus, {@code (A join B) join C} becomes {@code (A join C) join B}. The  * advantage of applying this rule is that it may be possible to apply  * conditions earlier. For instance,</p>  *  *<pre>{@code  * (sales as s join product_class as pc on true)  * join product as p  * on s.product_id = p.product_id  * and p.product_class_id = pc.product_class_id}</pre>  *  * becomes  *  *<pre>{@code (sales as s join product as p on s.product_id = p.product_id)  * join product_class as pc  * on p.product_class_id = pc.product_class_id}</pre>  *  *<p>Before the rule, one join has two conditions and the other has none  * ({@code ON TRUE}). After the rule, each join has one condition.</p>  */
end_comment

begin_class
specifier|public
class|class
name|PushJoinThroughJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|RIGHT
init|=
operator|new
name|PushJoinThroughJoinRule
argument_list|(
literal|"PushJoinThroughJoinRule:right"
argument_list|,
literal|true
argument_list|,
name|JoinRel
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|LEFT
init|=
operator|new
name|PushJoinThroughJoinRule
argument_list|(
literal|"PushJoinThroughJoinRule:left"
argument_list|,
literal|false
argument_list|,
name|JoinRel
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|right
decl_stmt|;
specifier|public
name|PushJoinThroughJoinRule
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
name|JoinRelBase
argument_list|>
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|some
argument_list|(
name|clazz
argument_list|,
name|any
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|any
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
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
name|right
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
name|void
name|onMatchRight
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|JoinRelBase
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
name|JoinRelBase
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
name|BitSet
name|bBitSet
init|=
name|BitSets
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|newBottomList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
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
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|bottomBottomMapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|aCount
operator|+
name|bCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|aCount
argument_list|)
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomBottomMapping
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|JoinRelBase
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|newTopList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SuspiciousNameCombination"
argument_list|)
specifier|final
name|JoinRelBase
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
name|RelNode
name|newProject
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newTopJoin
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|topMapping
argument_list|)
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
comment|/** Similar to {@link #onMatch}, but swaps the upper sibling with the left      * of the two lower siblings, rather than the right. */
specifier|private
name|void
name|onMatchLeft
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|JoinRelBase
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
name|JoinRelBase
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
name|BitSet
name|aBitSet
init|=
name|BitSets
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
argument_list|<
name|RexNode
argument_list|>
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|newBottomList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
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
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|bottomBottomMapping
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
name|aCount
operator|+
name|bCount
argument_list|,
name|cCount
argument_list|,
name|cCount
argument_list|,
name|aCount
argument_list|,
name|bCount
argument_list|)
decl_stmt|;
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|bottomBottomMapping
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|JoinRelBase
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|newTopList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
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
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SuspiciousNameCombination"
argument_list|)
specifier|final
name|JoinRelBase
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
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|newProject
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newTopJoin
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|topMapping
argument_list|)
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
comment|/** Splits a condition into conjunctions that do or do not intersect with      * a given bit set. */
specifier|static
name|void
name|split
parameter_list|(
name|RexNode
name|condition
parameter_list|,
name|BitSet
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
name|BitSet
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
block|}
end_class

begin_comment
comment|// End PushJoinThroughJoinRule.java
end_comment

end_unit

