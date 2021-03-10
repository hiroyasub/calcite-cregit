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
name|core
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
name|linq4j
operator|.
name|Ord
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelTraitSet
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
name|RelCollation
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
name|RelCollationTraitDef
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
name|RelFieldCollation
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
name|RelInput
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
name|RelWriter
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
name|SingleRel
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
name|RexLiteral
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
name|RexShuttle
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

begin_comment
comment|/**  * Relational expression that imposes a particular sort order on its input  * without otherwise changing its content.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Sort
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|RelCollation
name|collation
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|RexNode
name|offset
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|RexNode
name|fetch
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Sort.    *    * @param cluster   Cluster this relational expression belongs to    * @param traits    Traits    * @param child     input relational expression    * @param collation array of sort specifications    */
specifier|protected
name|Sort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelCollation
name|collation
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|collation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a Sort.    *    * @param cluster   Cluster this relational expression belongs to    * @param traits    Traits    * @param child     input relational expression    * @param collation array of sort specifications    * @param offset    Expression for number of rows to discard before returning    *                  first row    * @param fetch     Expression for number of rows to fetch    */
specifier|protected
name|Sort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelCollation
name|collation
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|offset
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|fetch
operator|=
name|fetch
expr_stmt|;
assert|assert
name|traits
operator|.
name|containsIfApplicable
argument_list|(
name|collation
argument_list|)
operator|:
literal|"traits="
operator|+
name|traits
operator|+
literal|", collation="
operator|+
name|collation
assert|;
assert|assert
operator|!
operator|(
name|fetch
operator|==
literal|null
operator|&&
name|offset
operator|==
literal|null
operator|&&
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
operator|:
literal|"trivial sort"
assert|;
block|}
comment|/**    * Creates a Sort by parsing serialized output.    */
specifier|protected
name|Sort
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|plus
argument_list|(
name|input
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|input
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|,
name|input
operator|.
name|getExpression
argument_list|(
literal|"offset"
argument_list|)
argument_list|,
name|input
operator|.
name|getExpression
argument_list|(
literal|"fetch"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newCollation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|offset
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|fetch
parameter_list|)
function_decl|;
comment|/** {@inheritDoc}    *    *<p>The CPU cost of a Sort has three main cases:    *    *<ul>    *<li>If {@code fetch} is zero, CPU cost is zero; otherwise,    *    *<li>if the sort keys are empty, we don't need to sort, only step over    * the rows, and therefore the CPU cost is    * {@code min(fetch + offset, inputRowCount) * bytesPerRow}; otherwise    *    *<li>we need to read and sort {@code inputRowCount} rows, with at most    * {@code min(fetch + offset, inputRowCount)} of them in the sort data    * structure at a time, giving a CPU cost of {@code inputRowCount *    * log(min(fetch + offset, inputRowCount)) * bytesPerRow}.    *</ul>    *    *<p>The cost model factors in row width via {@code bytesPerRow}, because    * sorts need to move rows around, not just compare them; by making the cost    * higher if rows are wider, we discourage pushing a Project through a Sort.    * We assume that each field is 4 bytes, and we add 3 'virtual fields' to    * represent the per-row overhead. Thus a 1-field row is (3 + 1) * 4 = 16    * bytes; a 5-field row is (3 + 5) * 4 = 32 bytes.    *    *<p>The cost model does not consider a 5-field sort to be more expensive    * than, say, a 2-field sort, because both sorts will compare just one field    * most of the time. */
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|double
name|offsetValue
init|=
name|Util
operator|.
name|first
argument_list|(
name|doubleValue
argument_list|(
name|offset
argument_list|)
argument_list|,
literal|0d
argument_list|)
decl_stmt|;
assert|assert
name|offsetValue
operator|>=
literal|0
operator|:
literal|"offset should not be negative:"
operator|+
name|offsetValue
assert|;
specifier|final
name|double
name|inCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
annotation|@
name|Nullable
name|Double
name|fetchValue
init|=
name|doubleValue
argument_list|(
name|fetch
argument_list|)
decl_stmt|;
specifier|final
name|double
name|readCount
decl_stmt|;
if|if
condition|(
name|fetchValue
operator|==
literal|null
condition|)
block|{
name|readCount
operator|=
name|inCount
expr_stmt|;
block|}
if|else if
condition|(
name|fetchValue
operator|<=
literal|0
condition|)
block|{
comment|// Case 1. Read zero rows from input, therefore CPU cost is zero.
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|inCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
else|else
block|{
name|readCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|inCount
argument_list|,
name|offsetValue
operator|+
name|fetchValue
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|bytesPerRow
init|=
operator|(
literal|3
operator|+
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|)
operator|*
literal|4
decl_stmt|;
specifier|final
name|double
name|cpu
decl_stmt|;
if|if
condition|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Case 2. If sort keys are empty, CPU cost is cheaper because we are just
comment|// stepping over the first "readCount" rows, rather than sorting all
comment|// "inCount" them. (Presumably we are applying FETCH and/or OFFSET,
comment|// otherwise this Sort is a no-op.)
name|cpu
operator|=
name|readCount
operator|*
name|bytesPerRow
expr_stmt|;
block|}
else|else
block|{
comment|// Case 3. Read and sort all "inCount" rows, keeping "readCount" in the
comment|// sort data structure at a time.
name|cpu
operator|=
name|Util
operator|.
name|nLogM
argument_list|(
name|inCount
argument_list|,
name|readCount
argument_list|)
operator|*
name|bytesPerRow
expr_stmt|;
block|}
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|readCount
argument_list|,
name|cpu
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|RexNode
name|offset
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|offset
argument_list|)
decl_stmt|;
name|RexNode
name|fetch
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|fetch
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|originalSortExps
init|=
name|getSortExps
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|sortExps
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|originalSortExps
argument_list|)
decl_stmt|;
assert|assert
name|sortExps
operator|==
name|originalSortExps
operator|:
literal|"Sort node does not support modification of input field expressions."
operator|+
literal|" Old expressions: "
operator|+
name|originalSortExps
operator|+
literal|", new ones: "
operator|+
name|sortExps
assert|;
if|if
condition|(
name|offset
operator|==
name|this
operator|.
name|offset
operator|&&
name|fetch
operator|==
name|this
operator|.
name|fetch
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|getInput
argument_list|()
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnforcer
parameter_list|()
block|{
return|return
name|offset
operator|==
literal|null
operator|&&
name|fetch
operator|==
literal|null
operator|&&
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Returns the array of {@link RelFieldCollation}s asked for by the sort    * specification, from most significant to least significant.    *    *<p>See also {@link RelMetadataQuery#collations(RelNode)},    * which lists all known collations. For example,    *<code>ORDER BY time_id</code> might also be sorted by    *<code>the_year, the_month</code> because of a known monotonicity    * constraint among the columns. {@code getCollation} would return    *<code>[time_id]</code> and {@code collations} would return    *<code>[ [time_id], [the_year, the_month] ]</code>.</p>    */
specifier|public
name|RelCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
comment|/** Returns the sort expressions. */
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getSortExps
parameter_list|()
block|{
comment|//noinspection StaticPseudoFunctionalStyleMethod
return|return
name|Util
operator|.
name|transform
argument_list|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|,
name|field
lambda|->
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeInputRef
argument_list|(
name|input
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|,
literal|"field"
argument_list|)
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|pw
operator|.
name|nest
argument_list|()
condition|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"collation"
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|getSortExps
argument_list|()
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"sort"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Ord
argument_list|<
name|RelFieldCollation
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"dir"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
operator|.
name|shortString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|pw
operator|.
name|itemIf
argument_list|(
literal|"offset"
argument_list|,
name|offset
argument_list|,
name|offset
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|pw
operator|.
name|itemIf
argument_list|(
literal|"fetch"
argument_list|,
name|fetch
argument_list|,
name|fetch
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|pw
return|;
block|}
comment|/** Returns the double value of a node if it is a literal, otherwise null. */
specifier|private
specifier|static
annotation|@
name|Nullable
name|Double
name|doubleValue
parameter_list|(
annotation|@
name|Nullable
name|RexNode
name|r
parameter_list|)
block|{
return|return
name|r
operator|instanceof
name|RexLiteral
condition|?
operator|(
operator|(
name|RexLiteral
operator|)
name|r
operator|)
operator|.
name|getValueAs
argument_list|(
name|Double
operator|.
name|class
argument_list|)
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

