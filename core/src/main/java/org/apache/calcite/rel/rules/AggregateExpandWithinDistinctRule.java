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
name|sql
operator|.
name|SqlKind
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
name|sql
operator|.
name|fun
operator|.
name|SqlInternalOperators
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|ImmutableIntList
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
name|IntPair
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
name|ArrayListMultimap
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
name|Multimap
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Objects
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
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|IntPredicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|AggregateExpandDistinctAggregatesRule
operator|.
name|groupValue
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
name|AggregateExpandDistinctAggregatesRule
operator|.
name|remap
import|;
end_import

begin_comment
comment|/**  * Planner rule that rewrites an {@link Aggregate} that contains  * {@code WITHIN DISTINCT} aggregate functions.  *  *<p>For example,  *<blockquote>  *<pre>SELECT o.paymentType,  *     COUNT(*) as "count",  *     COUNT(*) WITHIN DISTINCT (o.orderId) AS orderCount,  *     SUM(o.shipping) WITHIN DISTINCT (o.orderId) as sumShipping,  *     SUM(i.units) as sumUnits  *   FROM Orders AS o  *   JOIN OrderItems AS i USING (orderId)  *   GROUP BY o.paymentType</pre>  *</blockquote>  *  *<p>becomes  *  *<blockquote>  *<pre>  *     SELECT paymentType,  *       COUNT(*) as "count",  *       COUNT(*) FILTER (WHERE g = 0) AS orderCount,  *       SUM(minShipping) FILTER (WHERE g = 0) AS sumShipping,  *       SUM(sumUnits) FILTER (WHERE g = 1) as sumUnits  *    FROM (  *      SELECT o.paymentType,  *        GROUPING(o.orderId) AS g,  *        SUM(o.shipping) AS sumShipping,  *        MIN(o.shipping) AS minShipping,  *        SUM(i.units) AS sumUnits  *        FROM Orders AS o  *        JOIN OrderItems ON o.orderId = i.orderId  *        GROUP BY GROUPING SETS ((o.paymentType), (o.paymentType, o.orderId)))  *     GROUP BY o.paymentType</pre>  *</blockquote>  *  *<p>By the way, note that {@code COUNT(*) WITHIN DISTINCT (o.orderId)}  * is identical to {@code COUNT(DISTINCT o.orderId)}.  * {@code WITHIN DISTINCT} is a generalization of aggregate(DISTINCT).  * So, it is perhaps not surprising that the rewrite to {@code GROUPING SETS}  * is similar.  *  *<p>If there are multiple arguments  * (e.g. {@code SUM(a) WITHIN DISTINCT (x), SUM(a) WITHIN DISTINCT (y)})  * the rule creates separate {@code GROUPING SET}s.  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|AggregateExpandWithinDistinctRule
extends|extends
name|RelRule
argument_list|<
name|AggregateExpandWithinDistinctRule
operator|.
name|Config
argument_list|>
block|{
comment|/** Creates an AggregateExpandWithinDistinctRule. */
specifier|protected
name|AggregateExpandWithinDistinctRule
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
specifier|private
specifier|static
name|boolean
name|hasWithinDistinct
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
return|return
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
name|c
lambda|->
name|c
operator|.
name|distinctKeys
operator|!=
literal|null
argument_list|)
comment|// Wait until AggregateReduceFunctionsRule has dealt with AVG etc.
operator|&&
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|noneMatch
argument_list|(
name|CoreRules
operator|.
name|AGGREGATE_REDUCE_FUNCTIONS
operator|::
name|canReduce
argument_list|)
comment|// Don't think we can handle GROUPING SETS yet
operator|&&
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|==
name|Aggregate
operator|.
name|Group
operator|.
name|SIMPLE
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
comment|// Throughout this method, assume we are working on the following SQL:
comment|//
comment|//   SELECT deptno, SUM(sal), SUM(sal) WITHIN DISTINCT (job)
comment|//   FROM emp
comment|//   GROUP BY deptno
comment|//
comment|// or in algebra,
comment|//
comment|//   Aggregate($0, SUM($2), SUM($2) WITHIN DISTINCT ($4))
comment|//     Scan(emp)
comment|//
comment|// We plan to generate the following:
comment|//
comment|//   SELECT deptno, SUM(sal), SUM(sal) WITHIN DISTINCT (job)
comment|//   FROM (
comment|//     SELECT deptno, GROUPING(deptno, job), SUM(sal), MIN(sal)
comment|//     FROM emp
comment|//     GROUP BY GROUPING SETS ((deptno), (deptno, job)))
comment|//   GROUP BY deptno
comment|//
comment|// This rewrite also handles DISTINCT aggregates. We treat
comment|//
comment|//   SUM(DISTINCT sal)
comment|//   SUM(DISTINCT sal) WITHIN DISTINCT (job)
comment|//
comment|// as if the user had written
comment|//
comment|//   SUM(sal) WITHIN DISTINCT (sal)
comment|//
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCallList
init|=
name|aggregate
operator|.
name|getAggCallList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|c
lambda|->
name|unDistinct
argument_list|(
name|c
argument_list|,
name|aggregate
operator|.
name|getInput
argument_list|()
operator|::
name|fieldIsNullable
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Util
operator|.
name|toImmutableList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Find all within-distinct expressions.
specifier|final
name|Multimap
argument_list|<
name|ImmutableBitSet
argument_list|,
name|AggregateCall
argument_list|>
name|argLists
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// A bit set that represents an aggregate with no WITHIN DISTINCT.
comment|// Different from "WITHIN DISTINCT ()".
specifier|final
name|ImmutableBitSet
name|notDistinct
init|=
name|ImmutableBitSet
operator|.
name|of
argument_list|(
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
argument_list|)
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCallList
control|)
block|{
name|ImmutableBitSet
name|distinctKeys
init|=
name|aggCall
operator|.
name|distinctKeys
decl_stmt|;
if|if
condition|(
name|distinctKeys
operator|==
literal|null
condition|)
block|{
name|distinctKeys
operator|=
name|notDistinct
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|distinctKeys
operator|.
name|intersects
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
condition|)
block|{
comment|// Remove group keys. E.g.
comment|//   sum(x) within distinct (y, z) ... group by y
comment|// can be simplified to
comment|//   sum(x) within distinct (z) ... group by y
comment|// Note that this assumes a single grouping set for the original agg.
name|distinctKeys
operator|=
name|distinctKeys
operator|.
name|rebuild
argument_list|()
operator|.
name|removeAll
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
name|argLists
operator|.
name|put
argument_list|(
name|distinctKeys
argument_list|,
name|aggCall
argument_list|)
expr_stmt|;
block|}
comment|// Compute the set of all grouping sets that will be used in the output
comment|// query. For each WITHIN DISTINCT aggregate call, we will need a grouping
comment|// set that is the union of the aggregate call's unique keys and the input
comment|// query's overall grouping. Redundant grouping sets can be reused for
comment|// multiple aggregate calls.
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSetTreeSet
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|ImmutableBitSet
operator|.
name|ORDERING
argument_list|)
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|key
range|:
name|argLists
operator|.
name|keySet
argument_list|()
control|)
block|{
name|groupSetTreeSet
operator|.
name|add
argument_list|(
operator|(
name|key
operator|==
name|notDistinct
operator|)
condition|?
name|aggregate
operator|.
name|getGroupSet
argument_list|()
else|:
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|key
argument_list|)
operator|.
name|union
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupSetTreeSet
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|hasMultipleGroupSets
init|=
name|groupSets
operator|.
name|size
argument_list|()
operator|>
literal|1
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|fullGroupSet
init|=
name|ImmutableBitSet
operator|.
name|union
argument_list|(
name|groupSets
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|fullGroupOrderedSet
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|fullGroupOrderedSet
operator|.
name|addAll
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asSet
argument_list|()
argument_list|)
expr_stmt|;
name|fullGroupOrderedSet
operator|.
name|addAll
argument_list|(
name|fullGroupSet
operator|.
name|asSet
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableIntList
name|fullGroupList
init|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|fullGroupOrderedSet
argument_list|)
decl_stmt|;
comment|// Build the inner query
comment|//
comment|//   SELECT deptno, SUM(sal) AS sum_sal, MIN(sal) AS min_sal,
comment|//       MAX(sal) AS max_sal, GROUPING(deptno, job) AS g
comment|//   FROM emp
comment|//   GROUP BY GROUPING SETS ((deptno), (deptno, job))
comment|//
comment|// or in algebra,
comment|//
comment|//   Aggregate([($0), ($0, $4)], SUM($2), MIN($2), MAX($2), GROUPING($0, $4))
comment|//     Scan(emp)
specifier|final
name|RelBuilder
name|b
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
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
name|List
argument_list|<
name|RelBuilder
operator|.
name|AggCall
argument_list|>
name|aggCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Helper class for building the inner query.
comment|// CHECKSTYLE: IGNORE 1
class|class
name|Registrar
block|{
specifier|final
name|int
name|g
init|=
name|fullGroupSet
operator|.
name|cardinality
argument_list|()
decl_stmt|;
comment|/** Map of input fields (below the original aggregation) and filter args        * to inner query aggregate calls. */
specifier|final
name|Map
argument_list|<
name|IntPair
argument_list|,
name|Integer
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Map of aggregate calls from the original aggregation to inner query        * aggregate calls. */
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|aggs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Map of aggregate calls from the original aggregation to inner-query        * {@code COUNT(*)} calls, which are only needed for filters in the outer        * aggregate when the original aggregate call does not ignore null        * inputs. */
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|int
name|filterArg
parameter_list|)
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|fields
argument_list|,
name|f
lambda|->
name|this
operator|.
name|field
argument_list|(
name|f
argument_list|,
name|filterArg
argument_list|)
argument_list|)
return|;
block|}
name|int
name|field
parameter_list|(
name|int
name|field
parameter_list|,
name|int
name|filterArg
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|IntPair
operator|.
name|of
argument_list|(
name|field
argument_list|,
name|filterArg
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** Computes an aggregate call argument's values for a        * {@code WITHIN DISTINCT} aggregate call.        *        *<p>For example, to compute        * {@code SUM(x) WITHIN DISTINCT (y) GROUP BY (z)},        * the inner aggregate must first group {@code x} by {@code (y, z)}        *&mdash; using {@code MIN} to select the (hopefully) unique value of        * {@code x} for each {@code (y, z)} group. Actually summing over the        * grouped {@code x} values must occur in an outer aggregate.        *        * @param field Index of an input field that's used in a        *         {@code WITHIN DISTINCT} aggregate call        * @param filterArg Filter arg used in the original aggregate call, or        *         {@code -1} if there is no filter. We use the same filter in        *         the inner query.        * @return Index of the inner query aggregate call representing the        *         grouped field, which can be referenced in the outer query        *         aggregate call        */
name|int
name|register
parameter_list|(
name|int
name|field
parameter_list|,
name|int
name|filterArg
parameter_list|)
block|{
return|return
name|args
operator|.
name|computeIfAbsent
argument_list|(
name|IntPair
operator|.
name|of
argument_list|(
name|field
argument_list|,
name|filterArg
argument_list|)
argument_list|,
name|j
lambda|->
block|{
specifier|final
name|int
name|ordinal
init|=
name|g
operator|+
name|aggCalls
operator|.
name|size
argument_list|()
decl_stmt|;
name|RelBuilder
operator|.
name|AggCall
name|groupedField
init|=
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|,
name|b
operator|.
name|field
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|aggCalls
operator|.
name|add
argument_list|(
name|filterArg
operator|<
literal|0
condition|?
name|groupedField
else|:
name|groupedField
operator|.
name|filter
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|filterArg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|throwIfNotUnique
argument_list|()
condition|)
block|{
name|groupedField
operator|=
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|,
name|b
operator|.
name|field
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|aggCalls
operator|.
name|add
argument_list|(
name|filterArg
operator|<
literal|0
condition|?
name|groupedField
else|:
name|groupedField
operator|.
name|filter
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|filterArg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ordinal
return|;
block|}
argument_list|)
return|;
block|}
comment|/** Registers an aggregate call that is<em>not</em> a        * {@code WITHIN DISTINCT} call.        *        *<p>Unlike the case handled by {@link #register(int, int)} above,        * aggregate calls without any distinct keys do not need a second round        * of aggregation in the outer query, so they can be computed "as-is" in        * the inner query.        *        * @param i Index of the aggregate call in the original aggregation        * @param aggregateCall Original aggregate call        * @return Index of the aggregate call in the computed inner query        */
name|int
name|registerAgg
parameter_list|(
name|int
name|i
parameter_list|,
name|RelBuilder
operator|.
name|AggCall
name|aggregateCall
parameter_list|)
block|{
specifier|final
name|int
name|ordinal
init|=
name|g
operator|+
name|aggCalls
operator|.
name|size
argument_list|()
decl_stmt|;
name|aggs
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|ordinal
argument_list|)
expr_stmt|;
name|aggCalls
operator|.
name|add
argument_list|(
name|aggregateCall
argument_list|)
expr_stmt|;
return|return
name|ordinal
return|;
block|}
name|int
name|getAgg
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|aggs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
return|;
block|}
comment|/** Registers an extra {@code COUNT} aggregate call when it's needed to        * filter out null inputs in the outer aggregate.        *        *<p>This should only be called for aggregate calls with filters. It's        * possible that the filter would eliminate all input rows to the        * {@code MIN} call in the inner query, so calls in the outer        * aggregate may need to be aware of this. See usage of        * {@link AggregateExpandWithinDistinctRule#mustBeCounted(AggregateCall)}.        *        * @param filterArg The original aggregate call's filter; must be        *                 non-negative        * @return Index of the {@code COUNT} call in the computed inner query        */
name|int
name|registerCount
parameter_list|(
name|int
name|filterArg
parameter_list|)
block|{
assert|assert
name|filterArg
operator|>=
literal|0
assert|;
return|return
name|counts
operator|.
name|computeIfAbsent
argument_list|(
name|filterArg
argument_list|,
name|i
lambda|->
block|{
specifier|final
name|int
name|ordinal
init|=
name|g
operator|+
name|aggCalls
operator|.
name|size
argument_list|()
decl_stmt|;
name|aggCalls
operator|.
name|add
argument_list|(
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|filterArg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ordinal
return|;
block|}
argument_list|)
return|;
block|}
name|int
name|getCount
parameter_list|(
name|int
name|filterArg
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|counts
operator|.
name|get
argument_list|(
name|filterArg
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|final
name|Registrar
name|registrar
init|=
operator|new
name|Registrar
argument_list|()
decl_stmt|;
name|Ord
operator|.
name|forEach
argument_list|(
name|aggCallList
argument_list|,
parameter_list|(
name|c
parameter_list|,
name|i
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|c
operator|.
name|distinctKeys
operator|==
literal|null
condition|)
block|{
name|registrar
operator|.
name|registerAgg
argument_list|(
name|i
argument_list|,
name|b
operator|.
name|aggregateCall
argument_list|(
name|c
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|b
operator|.
name|fields
argument_list|(
name|c
operator|.
name|getArgList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|inputIdx
range|:
name|c
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|registrar
operator|.
name|register
argument_list|(
name|inputIdx
argument_list|,
name|c
operator|.
name|filterArg
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mustBeCounted
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|registrar
operator|.
name|registerCount
argument_list|(
name|c
operator|.
name|filterArg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Add an additional GROUPING() aggregate call so we can select only the
comment|// relevant inner-aggregate rows from the outer aggregate. If there is only
comment|// 1 grouping set (i.e. every aggregate call has the same distinct keys),
comment|// no GROUPING() call is necessary.
specifier|final
name|int
name|grouping
init|=
name|hasMultipleGroupSets
condition|?
name|registrar
operator|.
name|registerAgg
argument_list|(
operator|-
literal|1
argument_list|,
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GROUPING
argument_list|,
name|b
operator|.
name|fields
argument_list|(
name|fullGroupList
argument_list|)
argument_list|)
argument_list|)
else|:
operator|-
literal|1
decl_stmt|;
name|b
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|(
name|fullGroupSet
argument_list|,
operator|(
name|Iterable
argument_list|<
name|ImmutableBitSet
argument_list|>
operator|)
name|groupSets
argument_list|)
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
comment|// Build the outer query
comment|//
comment|//   SELECT deptno,
comment|//     MIN(sum_sal) FILTER (g = 0),
comment|//     SUM(min_sal) FILTER (g = 1)
comment|//   FROM ( ... )
comment|//   GROUP BY deptno
comment|//
comment|// or in algebra,
comment|//
comment|//   Aggregate($0, SUM($2 WHERE $4 = 0), SUM($3 WHERE $4 = 1))
comment|//     Aggregate([($0), ($0, $2)], SUM($2), MIN($2), GROUPING($0, $4))
comment|//       Scan(emp)
comment|//
comment|// If throwIfNotUnique, the "SUM(min_sal) FILTER (g = 1)" term above becomes
comment|//
comment|//     SUM(min_sal) FILTER (
comment|//         $THROW_UNLESS(g<> 1 OR min_sal IS NOT DISTINCT FROM max_sal,
comment|//             'more than one distinct value in agg UNIQUE_VALUE')
comment|//         AND g = 1)
name|aggCalls
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Ord
operator|.
name|forEach
argument_list|(
name|aggCallList
argument_list|,
parameter_list|(
name|c
parameter_list|,
name|i
parameter_list|)
lambda|->
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RexNode
name|groupFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasMultipleGroupSets
condition|)
block|{
name|groupFilter
operator|=
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|grouping
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
name|groupValue
argument_list|(
name|fullGroupList
argument_list|,
name|union
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|c
operator|.
name|distinctKeys
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|groupFilter
argument_list|)
expr_stmt|;
block|}
name|RelBuilder
operator|.
name|AggCall
name|aggCall
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|distinctKeys
operator|==
literal|null
condition|)
block|{
name|aggCall
operator|=
name|b
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|,
name|b
operator|.
name|field
argument_list|(
name|registrar
operator|.
name|getAgg
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The inputs to this aggregate are outputs from MIN() calls from the
comment|// inner agg, and MIN() returns null iff it has no non-null inputs,
comment|// which can only happen if an original aggregate's filter causes all
comment|// non-null input rows to be discarded for a particular group in the
comment|// inner aggregate. In this case, it should be ignored by the outer
comment|// aggregate as well. In case the aggregate call does not naturally
comment|// ignore null inputs, we add a filter based on a COUNT() in the inner
comment|// aggregate.
name|aggCall
operator|=
name|b
operator|.
name|aggregateCall
argument_list|(
name|c
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|b
operator|.
name|fields
argument_list|(
name|registrar
operator|.
name|fields
argument_list|(
name|c
operator|.
name|getArgList
argument_list|()
argument_list|,
name|c
operator|.
name|filterArg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|mustBeCounted
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|b
operator|.
name|greaterThan
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|registrar
operator|.
name|getCount
argument_list|(
name|c
operator|.
name|filterArg
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|throwIfNotUnique
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|j
range|:
name|c
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|RexNode
name|isUniqueCondition
init|=
name|b
operator|.
name|isNotDistinctFrom
argument_list|(
name|b
operator|.
name|field
argument_list|(
name|registrar
operator|.
name|field
argument_list|(
name|j
argument_list|,
name|c
operator|.
name|filterArg
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|field
argument_list|(
name|registrar
operator|.
name|field
argument_list|(
name|j
argument_list|,
name|c
operator|.
name|filterArg
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupFilter
operator|!=
literal|null
condition|)
block|{
name|isUniqueCondition
operator|=
name|b
operator|.
name|or
argument_list|(
name|b
operator|.
name|not
argument_list|(
name|groupFilter
argument_list|)
argument_list|,
name|isUniqueCondition
argument_list|)
expr_stmt|;
block|}
name|String
name|message
init|=
literal|"more than one distinct value in agg UNIQUE_VALUE"
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlInternalOperators
operator|.
name|THROW_UNLESS
argument_list|,
name|isUniqueCondition
argument_list|,
name|b
operator|.
name|literal
argument_list|(
name|message
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|filters
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|aggCall
operator|=
name|aggCall
operator|.
name|filter
argument_list|(
name|b
operator|.
name|and
argument_list|(
name|filters
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aggCalls
operator|.
name|add
argument_list|(
name|aggCall
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|b
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|(
name|remap
argument_list|(
name|fullGroupSet
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|,
operator|(
name|Iterable
argument_list|<
name|ImmutableBitSet
argument_list|>
operator|)
name|remap
argument_list|(
name|fullGroupSet
argument_list|,
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
name|b
operator|.
name|convert
argument_list|(
name|aggregate
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|mustBeCounted
parameter_list|(
name|AggregateCall
name|aggCall
parameter_list|)
block|{
comment|// Always count filtered inner aggregates to be safe.
comment|//
comment|// It's possible that, for some aggregate calls (namely, those that
comment|// completely ignore null inputs), we could neglect counting the
comment|// grouped-and-filtered rows of the inner aggregate and filtering the empty
comment|// ones out from the outer aggregate, since those empty groups would produce
comment|// null values as the result of MIN and thus be ignored by the outer
comment|// aggregate anyway.
comment|//
comment|// Note that using "aggCall.ignoreNulls()" is not sufficient to determine
comment|// when it's safe to do this, since for COUNT the value of ignoreNulls()
comment|// should generally be true even though COUNT(*) will never ignore anything.
return|return
name|aggCall
operator|.
name|hasFilter
argument_list|()
return|;
block|}
comment|/** Converts a {@code DISTINCT} aggregate call into an equivalent one with    * {@code WITHIN DISTINCT}.    *    *<p>Examples:    *<ul>    *<li>{@code SUM(DISTINCT x)}&rarr;    *     {@code SUM(x) WITHIN DISTINCT (x)} has distinct key (x);    *<li>{@code SUM(DISTINCT x)} WITHIN DISTINCT (y)&rarr;    *     {@code SUM(x) WITHIN DISTINCT (x)} has distinct key (x);    *<li>{@code SUM(x)} WITHIN DISTINCT (y, z) has distinct key (y, z);    *<li>{@code SUM(x)} has no distinct key.    *</ul>    */
specifier|private
specifier|static
name|AggregateCall
name|unDistinct
parameter_list|(
name|AggregateCall
name|aggregateCall
parameter_list|,
name|IntPredicate
name|isNullable
parameter_list|)
block|{
if|if
condition|(
name|aggregateCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newArgList
init|=
name|aggregateCall
operator|.
name|getArgList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|COUNT
operator|||
name|aggregateCall
operator|.
name|hasFilter
argument_list|()
operator|||
name|isNullable
operator|.
name|test
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|aggregateCall
operator|.
name|withDistinct
argument_list|(
literal|false
argument_list|)
operator|.
name|withDistinctKeys
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|aggregateCall
operator|.
name|getArgList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withArgList
argument_list|(
name|newArgList
argument_list|)
return|;
block|}
return|return
name|aggregateCall
return|;
block|}
specifier|private
specifier|static
name|ImmutableBitSet
name|union
parameter_list|(
name|ImmutableBitSet
name|s0
parameter_list|,
annotation|@
name|Nullable
name|ImmutableBitSet
name|s1
parameter_list|)
block|{
return|return
name|s1
operator|==
literal|null
condition|?
name|s0
else|:
name|s0
operator|.
name|union
argument_list|(
name|s1
argument_list|)
return|;
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
name|ImmutableAggregateExpandWithinDistinctRule
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
name|LogicalAggregate
operator|.
name|class
argument_list|)
operator|.
name|predicate
argument_list|(
name|AggregateExpandWithinDistinctRule
operator|::
name|hasWithinDistinct
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateExpandWithinDistinctRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateExpandWithinDistinctRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Whether the code generated by the rule should throw if the arguments      * are not functionally dependent.      *      *<p>For example, if implementing {@code SUM(sal) WITHIN DISTINCT job)} ...      * {@code GROUP BY deptno},      * suppose that within department 10, (job, sal) has the values      * ('CLERK', 100), ('CLERK', 120), ('MANAGER', 150), ('MANAGER', 150). If      * {@code throwIfNotUnique} is true, the query would throw because of the      * values [100, 120]; if false, the query would sum the distinct values      * [100, 120, 150]. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|throwIfNotUnique
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Sets {@link #throwIfNotUnique()}. */
name|Config
name|withThrowIfNotUnique
parameter_list|(
name|boolean
name|throwIfNotUnique
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

