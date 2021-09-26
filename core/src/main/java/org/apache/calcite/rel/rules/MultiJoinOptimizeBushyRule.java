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
name|config
operator|.
name|CalciteSystemProperty
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
name|RelMdUtil
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
name|rex
operator|.
name|RexVisitor
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
name|Pair
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
name|io
operator|.
name|PrintWriter
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Objects
import|;
end_import

begin_import
import|import static
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
name|LoptMultiJoin
operator|.
name|Edge
import|;
end_import

begin_import
import|import static
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
operator|.
name|TargetMapping
import|;
end_import

begin_comment
comment|/**  * Planner rule that finds an approximately optimal ordering for join operators  * using a heuristic algorithm.  *  *<p>It is triggered by the pattern  * {@link org.apache.calcite.rel.logical.LogicalProject} ({@link MultiJoin}).  *  *<p>It is similar to  * {@link org.apache.calcite.rel.rules.LoptOptimizeJoinRule}  * ({@link CoreRules#MULTI_JOIN_OPTIMIZE}).  * {@code LoptOptimizeJoinRule} is only capable of producing left-deep joins;  * this rule is capable of producing bushy joins.  *  *<p>TODO:  *<ol>  *<li>Join conditions that touch 1 factor.  *<li>Join conditions that touch 3 factors.  *<li>More than 1 join conditions that touch the same pair of factors,  *       e.g. {@code t0.c1 = t1.c1 and t1.c2 = t0.c3}  *</ol>  *  * @see CoreRules#MULTI_JOIN_OPTIMIZE_BUSHY  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|MultiJoinOptimizeBushyRule
extends|extends
name|RelRule
argument_list|<
name|MultiJoinOptimizeBushyRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
specifier|private
specifier|final
annotation|@
name|Nullable
name|PrintWriter
name|pw
init|=
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|?
name|Util
operator|.
name|printWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
else|:
literal|null
decl_stmt|;
comment|/** Creates a MultiJoinOptimizeBushyRule. */
specifier|protected
name|MultiJoinOptimizeBushyRule
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
name|MultiJoinOptimizeBushyRule
parameter_list|(
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|MultiJoinOptimizeBushyRule
parameter_list|(
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|,
name|RelFactories
operator|.
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|joinFactory
argument_list|,
name|projectFactory
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
name|MultiJoin
name|multiJoinRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|multiJoinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
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
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|LoptMultiJoin
name|multiJoin
init|=
operator|new
name|LoptMultiJoin
argument_list|(
name|multiJoinRel
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Vertex
argument_list|>
name|vertexes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|x
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|multiJoin
operator|.
name|getNumJoinFactors
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelNode
name|rel
init|=
name|multiJoin
operator|.
name|getJoinFactor
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|double
name|cost
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|vertexes
operator|.
name|add
argument_list|(
operator|new
name|LeafVertex
argument_list|(
name|i
argument_list|,
name|rel
argument_list|,
name|cost
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|+=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
block|}
assert|assert
name|x
operator|==
name|multiJoin
operator|.
name|getNumTotalFields
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|Edge
argument_list|>
name|unusedEdges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|multiJoin
operator|.
name|getJoinFilters
argument_list|()
control|)
block|{
name|unusedEdges
operator|.
name|add
argument_list|(
name|multiJoin
operator|.
name|createEdge
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Comparator that chooses the best edge. A "good edge" is one that has
comment|// a large difference in the number of rows on LHS and RHS.
specifier|final
name|Comparator
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|edgeComparator
init|=
operator|new
name|Comparator
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LoptMultiJoin
operator|.
name|Edge
name|e0
parameter_list|,
name|LoptMultiJoin
operator|.
name|Edge
name|e1
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|rowCountDiff
argument_list|(
name|e0
argument_list|)
argument_list|,
name|rowCountDiff
argument_list|(
name|e1
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|double
name|rowCountDiff
parameter_list|(
name|LoptMultiJoin
operator|.
name|Edge
name|edge
parameter_list|)
block|{
assert|assert
name|edge
operator|.
name|factors
operator|.
name|cardinality
argument_list|()
operator|==
literal|2
operator|:
name|edge
operator|.
name|factors
assert|;
specifier|final
name|int
name|factor0
init|=
name|edge
operator|.
name|factors
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|factor1
init|=
name|edge
operator|.
name|factors
operator|.
name|nextSetBit
argument_list|(
name|factor0
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|vertexes
operator|.
name|get
argument_list|(
name|factor0
argument_list|)
operator|.
name|cost
operator|-
name|vertexes
operator|.
name|get
argument_list|(
name|factor1
argument_list|)
operator|.
name|cost
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Edge
argument_list|>
name|usedEdges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|edgeOrdinal
init|=
name|chooseBestEdge
argument_list|(
name|unusedEdges
argument_list|,
name|edgeComparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|trace
argument_list|(
name|vertexes
argument_list|,
name|unusedEdges
argument_list|,
name|usedEdges
argument_list|,
name|edgeOrdinal
argument_list|,
name|pw
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|factors
decl_stmt|;
if|if
condition|(
name|edgeOrdinal
operator|==
operator|-
literal|1
condition|)
block|{
comment|// No more edges. Are there any un-joined vertexes?
specifier|final
name|Vertex
name|lastVertex
init|=
name|Util
operator|.
name|last
argument_list|(
name|vertexes
argument_list|)
decl_stmt|;
specifier|final
name|int
name|z
init|=
name|lastVertex
operator|.
name|factors
operator|.
name|previousClearBit
argument_list|(
name|lastVertex
operator|.
name|id
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|z
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|factors
operator|=
operator|new
name|int
index|[]
block|{
name|z
block|,
name|lastVertex
operator|.
name|id
block|}
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|LoptMultiJoin
operator|.
name|Edge
name|bestEdge
init|=
name|unusedEdges
operator|.
name|get
argument_list|(
name|edgeOrdinal
argument_list|)
decl_stmt|;
comment|// For now, assume that the edge is between precisely two factors.
comment|// 1-factor conditions have probably been pushed down,
comment|// and 3-or-more-factor conditions are advanced. (TODO:)
comment|// Therefore, for now, the factors that are merged are exactly the
comment|// factors on this edge.
assert|assert
name|bestEdge
operator|.
name|factors
operator|.
name|cardinality
argument_list|()
operator|==
literal|2
assert|;
name|factors
operator|=
name|bestEdge
operator|.
name|factors
operator|.
name|toArray
argument_list|()
expr_stmt|;
block|}
comment|// Determine which factor is to be on the LHS of the join.
specifier|final
name|int
name|majorFactor
decl_stmt|;
specifier|final
name|int
name|minorFactor
decl_stmt|;
if|if
condition|(
name|vertexes
operator|.
name|get
argument_list|(
name|factors
index|[
literal|0
index|]
argument_list|)
operator|.
name|cost
operator|<=
name|vertexes
operator|.
name|get
argument_list|(
name|factors
index|[
literal|1
index|]
argument_list|)
operator|.
name|cost
condition|)
block|{
name|majorFactor
operator|=
name|factors
index|[
literal|0
index|]
expr_stmt|;
name|minorFactor
operator|=
name|factors
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|majorFactor
operator|=
name|factors
index|[
literal|1
index|]
expr_stmt|;
name|minorFactor
operator|=
name|factors
index|[
literal|0
index|]
expr_stmt|;
block|}
specifier|final
name|Vertex
name|majorVertex
init|=
name|vertexes
operator|.
name|get
argument_list|(
name|majorFactor
argument_list|)
decl_stmt|;
specifier|final
name|Vertex
name|minorVertex
init|=
name|vertexes
operator|.
name|get
argument_list|(
name|minorFactor
argument_list|)
decl_stmt|;
comment|// Find the join conditions. All conditions whose factors are now all in
comment|// the join can now be used.
specifier|final
name|int
name|v
init|=
name|vertexes
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|newFactors
init|=
name|majorVertex
operator|.
name|factors
operator|.
name|rebuild
argument_list|()
operator|.
name|addAll
argument_list|(
name|minorVertex
operator|.
name|factors
argument_list|)
operator|.
name|set
argument_list|(
name|v
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|edgeIterator
init|=
name|unusedEdges
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|edgeIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LoptMultiJoin
operator|.
name|Edge
name|edge
init|=
name|edgeIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|newFactors
operator|.
name|contains
argument_list|(
name|edge
operator|.
name|factors
argument_list|)
condition|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|edge
operator|.
name|condition
argument_list|)
expr_stmt|;
name|edgeIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|usedEdges
operator|.
name|add
argument_list|(
name|edge
argument_list|)
expr_stmt|;
block|}
block|}
name|double
name|cost
init|=
name|majorVertex
operator|.
name|cost
operator|*
name|minorVertex
operator|.
name|cost
operator|*
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|conditions
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Vertex
name|newVertex
init|=
operator|new
name|JoinVertex
argument_list|(
name|v
argument_list|,
name|majorFactor
argument_list|,
name|minorFactor
argument_list|,
name|newFactors
argument_list|,
name|cost
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|conditions
argument_list|)
argument_list|)
decl_stmt|;
name|vertexes
operator|.
name|add
argument_list|(
name|newVertex
argument_list|)
expr_stmt|;
comment|// Re-compute selectivity of edges above the one just chosen.
comment|// Suppose that we just chose the edge between "product" (10k rows) and
comment|// "product_class" (10 rows).
comment|// Both of those vertices are now replaced by a new vertex "P-PC".
comment|// This vertex has fewer rows (1k rows) -- a fact that is critical to
comment|// decisions made later. (Hence "greedy" algorithm not "simple".)
comment|// The adjacent edges are modified.
specifier|final
name|ImmutableBitSet
name|merged
init|=
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|minorFactor
argument_list|,
name|majorFactor
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|unusedEdges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|LoptMultiJoin
operator|.
name|Edge
name|edge
init|=
name|unusedEdges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|edge
operator|.
name|factors
operator|.
name|intersects
argument_list|(
name|merged
argument_list|)
condition|)
block|{
name|ImmutableBitSet
name|newEdgeFactors
init|=
name|edge
operator|.
name|factors
operator|.
name|rebuild
argument_list|()
operator|.
name|removeAll
argument_list|(
name|newFactors
argument_list|)
operator|.
name|set
argument_list|(
name|v
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
assert|assert
name|newEdgeFactors
operator|.
name|cardinality
argument_list|()
operator|==
literal|2
assert|;
specifier|final
name|LoptMultiJoin
operator|.
name|Edge
name|newEdge
init|=
operator|new
name|LoptMultiJoin
operator|.
name|Edge
argument_list|(
name|edge
operator|.
name|condition
argument_list|,
name|newEdgeFactors
argument_list|,
name|edge
operator|.
name|columns
argument_list|)
decl_stmt|;
name|unusedEdges
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|newEdge
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// We have a winner!
name|List
argument_list|<
name|Pair
argument_list|<
name|RelNode
argument_list|,
name|TargetMapping
argument_list|>
argument_list|>
name|relNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Vertex
name|vertex
range|:
name|vertexes
control|)
block|{
if|if
condition|(
name|vertex
operator|instanceof
name|LeafVertex
condition|)
block|{
name|LeafVertex
name|leafVertex
init|=
operator|(
name|LeafVertex
operator|)
name|vertex
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|offsetSource
argument_list|(
name|Mappings
operator|.
name|createIdentity
argument_list|(
name|leafVertex
operator|.
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|,
name|leafVertex
operator|.
name|fieldOffset
argument_list|,
name|multiJoin
operator|.
name|getNumTotalFields
argument_list|()
argument_list|)
decl_stmt|;
name|relNodes
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|leafVertex
operator|.
name|rel
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JoinVertex
name|joinVertex
init|=
operator|(
name|JoinVertex
operator|)
name|vertex
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|RelNode
argument_list|,
name|Mappings
operator|.
name|TargetMapping
argument_list|>
name|leftPair
init|=
name|relNodes
operator|.
name|get
argument_list|(
name|joinVertex
operator|.
name|leftFactor
argument_list|)
decl_stmt|;
name|RelNode
name|left
init|=
name|leftPair
operator|.
name|left
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|leftMapping
init|=
name|leftPair
operator|.
name|right
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|RelNode
argument_list|,
name|Mappings
operator|.
name|TargetMapping
argument_list|>
name|rightPair
init|=
name|relNodes
operator|.
name|get
argument_list|(
name|joinVertex
operator|.
name|rightFactor
argument_list|)
decl_stmt|;
name|RelNode
name|right
init|=
name|rightPair
operator|.
name|left
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|rightMapping
init|=
name|rightPair
operator|.
name|right
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|merge
argument_list|(
name|leftMapping
argument_list|,
name|Mappings
operator|.
name|offsetTarget
argument_list|(
name|rightMapping
argument_list|,
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"left: "
operator|+
name|leftMapping
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"right: "
operator|+
name|rightMapping
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"combined: "
operator|+
name|mapping
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RexVisitor
argument_list|<
name|RexNode
argument_list|>
name|shuttle
init|=
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|mapping
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|condition
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|joinVertex
operator|.
name|conditions
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|join
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|left
argument_list|)
operator|.
name|push
argument_list|(
name|right
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|condition
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|relNodes
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|join
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|relNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Pair
argument_list|<
name|RelNode
argument_list|,
name|Mappings
operator|.
name|TargetMapping
argument_list|>
name|top
init|=
name|Util
operator|.
name|last
argument_list|(
name|relNodes
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|top
operator|.
name|left
argument_list|)
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|top
operator|.
name|right
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
specifier|private
specifier|static
name|void
name|trace
parameter_list|(
name|List
argument_list|<
name|Vertex
argument_list|>
name|vertexes
parameter_list|,
name|List
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|unusedEdges
parameter_list|,
name|List
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|usedEdges
parameter_list|,
name|int
name|edgeOrdinal
parameter_list|,
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"bestEdge: "
operator|+
name|edgeOrdinal
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"vertexes:"
argument_list|)
expr_stmt|;
for|for
control|(
name|Vertex
name|vertex
range|:
name|vertexes
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|vertex
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|(
literal|"unused edges:"
argument_list|)
expr_stmt|;
for|for
control|(
name|LoptMultiJoin
operator|.
name|Edge
name|edge
range|:
name|unusedEdges
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|edge
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|(
literal|"edges:"
argument_list|)
expr_stmt|;
for|for
control|(
name|LoptMultiJoin
operator|.
name|Edge
name|edge
range|:
name|usedEdges
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|edge
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|int
name|chooseBestEdge
parameter_list|(
name|List
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|edges
parameter_list|,
name|Comparator
argument_list|<
name|LoptMultiJoin
operator|.
name|Edge
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|minPos
argument_list|(
name|edges
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/** Returns the index within a list at which compares least according to a    * comparator.    *    *<p>In the case of a tie, returns the earliest such element.</p>    *    *<p>If the list is empty, returns -1.</p>    */
specifier|static
parameter_list|<
name|E
parameter_list|>
name|int
name|minPos
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|,
name|Comparator
argument_list|<
name|E
argument_list|>
name|fn
parameter_list|)
block|{
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|E
name|eBest
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|iBest
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|E
name|e
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fn
operator|.
name|compare
argument_list|(
name|e
argument_list|,
name|eBest
argument_list|)
operator|<
literal|0
condition|)
block|{
name|eBest
operator|=
name|e
expr_stmt|;
name|iBest
operator|=
name|i
expr_stmt|;
block|}
block|}
return|return
name|iBest
return|;
block|}
comment|/** Participant in a join (relation or join). */
specifier|abstract
specifier|static
class|class
name|Vertex
block|{
specifier|final
name|int
name|id
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableBitSet
name|factors
decl_stmt|;
specifier|final
name|double
name|cost
decl_stmt|;
name|Vertex
parameter_list|(
name|int
name|id
parameter_list|,
name|ImmutableBitSet
name|factors
parameter_list|,
name|double
name|cost
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|factors
operator|=
name|factors
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
block|}
comment|/** Relation participating in a join. */
specifier|static
class|class
name|LeafVertex
extends|extends
name|Vertex
block|{
specifier|private
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|final
name|int
name|fieldOffset
decl_stmt|;
name|LeafVertex
parameter_list|(
name|int
name|id
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|double
name|cost
parameter_list|,
name|int
name|fieldOffset
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|id
argument_list|)
argument_list|,
name|cost
argument_list|)
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|fieldOffset
operator|=
name|fieldOffset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LeafVertex(id: "
operator|+
name|id
operator|+
literal|", cost: "
operator|+
name|Util
operator|.
name|human
argument_list|(
name|cost
argument_list|)
operator|+
literal|", factors: "
operator|+
name|factors
operator|+
literal|", fieldOffset: "
operator|+
name|fieldOffset
operator|+
literal|")"
return|;
block|}
block|}
comment|/** Participant in a join which is itself a join. */
specifier|static
class|class
name|JoinVertex
extends|extends
name|Vertex
block|{
specifier|private
specifier|final
name|int
name|leftFactor
decl_stmt|;
specifier|private
specifier|final
name|int
name|rightFactor
decl_stmt|;
comment|/** Zero or more join conditions. All are in terms of the original input      * columns (not in terms of the outputs of left and right input factors). */
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|conditions
decl_stmt|;
name|JoinVertex
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|leftFactor
parameter_list|,
name|int
name|rightFactor
parameter_list|,
name|ImmutableBitSet
name|factors
parameter_list|,
name|double
name|cost
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|conditions
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|factors
argument_list|,
name|cost
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftFactor
operator|=
name|leftFactor
expr_stmt|;
name|this
operator|.
name|rightFactor
operator|=
name|rightFactor
expr_stmt|;
name|this
operator|.
name|conditions
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|conditions
argument_list|,
literal|"conditions"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JoinVertex(id: "
operator|+
name|id
operator|+
literal|", cost: "
operator|+
name|Util
operator|.
name|human
argument_list|(
name|cost
argument_list|)
operator|+
literal|", factors: "
operator|+
name|factors
operator|+
literal|", leftFactor: "
operator|+
name|leftFactor
operator|+
literal|", rightFactor: "
operator|+
name|rightFactor
operator|+
literal|")"
return|;
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
name|DEFAULT
init|=
name|ImmutableMultiJoinOptimizeBushyRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|MultiJoin
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|MultiJoinOptimizeBushyRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|MultiJoinOptimizeBushyRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

