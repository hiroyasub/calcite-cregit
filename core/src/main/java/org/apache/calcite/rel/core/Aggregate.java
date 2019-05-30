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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|RelDataTypeField
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
name|runtime
operator|.
name|CalciteException
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
name|runtime
operator|.
name|Resources
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
name|SqlAggFunction
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
name|SqlOperatorBinding
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
name|SqlUtil
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
name|parser
operator|.
name|SqlParserPos
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
name|type
operator|.
name|SqlTypeName
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
name|validate
operator|.
name|SqlValidatorException
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
name|Litmus
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|math
operator|.
name|IntMath
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/**  * Relational operator that eliminates  * duplicates and computes totals.  *  *<p>It corresponds to the {@code GROUP BY} operator in a SQL query  * statement, together with the aggregate functions in the {@code SELECT}  * clause.  *  *<p>Rules:  *  *<ul>  *<li>{@link org.apache.calcite.rel.rules.AggregateProjectPullUpConstantsRule}  *<li>{@link org.apache.calcite.rel.rules.AggregateExpandDistinctAggregatesRule}  *<li>{@link org.apache.calcite.rel.rules.AggregateReduceFunctionsRule}.  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Aggregate
extends|extends
name|SingleRel
block|{
specifier|public
specifier|static
name|boolean
name|isSimple
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
return|return
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|==
name|Group
operator|.
name|SIMPLE
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be converted to Java Predicate before 2.0
specifier|public
specifier|static
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|Aggregate
argument_list|>
name|IS_SIMPLE
init|=
name|Aggregate
operator|::
name|isSimple
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be converted to Java Predicate before 2.0
specifier|public
specifier|static
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|Aggregate
argument_list|>
name|NO_INDICATOR
init|=
name|Aggregate
operator|::
name|noIndicator
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be converted to Java Predicate before 2.0
specifier|public
specifier|static
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|Aggregate
argument_list|>
name|IS_NOT_GRAND_TOTAL
init|=
name|Aggregate
operator|::
name|isNotGrandTotal
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
annotation|@
name|Deprecated
comment|// unused field, to be removed before 2.0
specifier|public
specifier|final
name|boolean
name|indicator
init|=
literal|false
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableBitSet
name|groupSet
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an Aggregate.    *    *<p>All members of {@code groupSets} must be sub-sets of {@code groupSet}.    * For a simple {@code GROUP BY}, {@code groupSets} is a singleton list    * containing {@code groupSet}.    *    *<p>If {@code GROUP BY} is not specified,    * or equivalently if {@code GROUP BY ()} is specified,    * {@code groupSet} will be the empty set,    * and {@code groupSets} will have one element, that empty set.    *    *<p>If {@code CUBE}, {@code ROLLUP} or {@code GROUPING SETS} are    * specified, {@code groupSets} will have additional elements,    * but they must each be a subset of {@code groupSet},    * and they must be sorted by inclusion:    * {@code (0, 1, 2), (1), (0, 2), (0), ()}.    *    * @param cluster  Cluster    * @param traits   Traits    * @param child    Child    * @param groupSet Bit set of grouping fields    * @param groupSets List of all grouping sets; null for just {@code groupSet}    * @param aggCalls Collection of calls to aggregate functions    */
specifier|protected
name|Aggregate
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
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
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
name|aggCalls
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|aggCalls
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSet
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|groupSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupSets
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|groupSets
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|groupSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|groupSets
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupSets
argument_list|)
expr_stmt|;
assert|assert
name|ImmutableBitSet
operator|.
name|ORDERING
operator|.
name|isStrictlyOrdered
argument_list|(
name|groupSets
argument_list|)
operator|:
name|groupSets
assert|;
for|for
control|(
name|ImmutableBitSet
name|set
range|:
name|groupSets
control|)
block|{
assert|assert
name|groupSet
operator|.
name|contains
argument_list|(
name|set
argument_list|)
assert|;
block|}
block|}
assert|assert
name|groupSet
operator|.
name|length
argument_list|()
operator|<=
name|child
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
assert|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
assert|assert
name|typeMatchesInferred
argument_list|(
name|aggCall
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
assert|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|aggCall
operator|.
name|filterArg
operator|<
literal|0
operator|||
name|isPredicate
argument_list|(
name|child
argument_list|,
name|aggCall
operator|.
name|filterArg
argument_list|)
argument_list|,
literal|"filter must be BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates an Aggregate.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|Aggregate
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
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
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
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|indicator
argument_list|,
literal|"indicator is not supported, use GROUPING function instead"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isNotGrandTotal
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
return|return
name|aggregate
operator|.
name|getGroupCount
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|boolean
name|noIndicator
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|isPredicate
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|int
name|index
parameter_list|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
operator|&&
operator|!
name|type
operator|.
name|isNullable
argument_list|()
return|;
block|}
comment|/**    * Creates an Aggregate by parsing serialized output.    */
specifier|protected
name|Aggregate
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
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|input
operator|.
name|getBitSet
argument_list|(
literal|"group"
argument_list|)
argument_list|,
name|input
operator|.
name|getBitSetList
argument_list|(
literal|"groups"
argument_list|)
argument_list|,
name|input
operator|.
name|getAggregateCalls
argument_list|(
literal|"aggs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|RelNode
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
literal|false
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
comment|/** Creates a copy of this aggregate.    *    * @param traitSet Traits    * @param input Input    * @param indicator Deprecated, always false    * @param groupSet Bit set of grouping fields    * @param groupSets List of all grouping sets; null for just {@code groupSet}    * @param aggCalls Collection of calls to aggregate functions    * @return New {@code Aggregate} if any parameter differs from the value of    *   this {@code Aggregate}, or just {@code this} if all the parameters are    *   the same    *    * @see #copy(org.apache.calcite.plan.RelTraitSet, java.util.List)    */
specifier|public
specifier|abstract
name|Aggregate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
function_decl|;
comment|/**    * Returns a list of calls to aggregate functions.    *    * @return list of calls to aggregate functions    */
specifier|public
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|getAggCallList
parameter_list|()
block|{
return|return
name|aggCalls
return|;
block|}
comment|/**    * Returns a list of calls to aggregate functions together with their output    * field names.    *    * @return list of calls to aggregate functions and their output field names    */
specifier|public
name|List
argument_list|<
name|Pair
argument_list|<
name|AggregateCall
argument_list|,
name|String
argument_list|>
argument_list|>
name|getNamedAggCalls
parameter_list|()
block|{
specifier|final
name|int
name|offset
init|=
name|getGroupCount
argument_list|()
decl_stmt|;
return|return
name|Pair
operator|.
name|zip
argument_list|(
name|aggCalls
argument_list|,
name|Util
operator|.
name|skip
argument_list|(
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the number of grouping fields.    * These grouping fields are the leading fields in both the input and output    * records.    *    *<p>NOTE: The {@link #getGroupSet()} data structure allows for the    * grouping fields to not be on the leading edge. New code should, if    * possible, assume that grouping fields are in arbitrary positions in the    * input relational expression.    *    * @return number of grouping fields    */
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
return|return
name|groupSet
operator|.
name|cardinality
argument_list|()
return|;
block|}
comment|/**    * Returns the number of indicator fields.    *    *<p>Always zero.    *    * @return number of indicator fields, always zero    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|int
name|getIndicatorCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Returns a bit set of the grouping fields.    *    * @return bit set of ordinals of grouping fields    */
specifier|public
name|ImmutableBitSet
name|getGroupSet
parameter_list|()
block|{
return|return
name|groupSet
return|;
block|}
comment|/**    * Returns the list of grouping sets computed by this Aggregate.    *    * @return List of all grouping sets; null for just {@code groupSet}    */
specifier|public
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getGroupSets
parameter_list|()
block|{
return|return
name|groupSets
return|;
block|}
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
comment|// We skip the "groups" element if it is a singleton of "group".
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"group"
argument_list|,
name|groupSet
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"groups"
argument_list|,
name|groupSets
argument_list|,
name|getGroupType
argument_list|()
operator|!=
name|Group
operator|.
name|SIMPLE
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"aggs"
argument_list|,
name|aggCalls
argument_list|,
name|pw
operator|.
name|nest
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pw
operator|.
name|nest
argument_list|()
condition|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|AggregateCall
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|aggCalls
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|ord
operator|.
name|e
operator|.
name|name
argument_list|,
literal|"agg#"
operator|+
name|ord
operator|.
name|i
argument_list|)
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pw
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// Assume that each sort column has 50% of the value count.
comment|// Therefore one sort column has .5 * rowCount,
comment|// 2 sort columns give .75 * rowCount.
comment|// Zero sort columns yields 1 row (or 0 if the input is empty).
specifier|final
name|int
name|groupCount
init|=
name|groupSet
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupCount
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
name|double
name|rowCount
init|=
name|super
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
decl_stmt|;
name|rowCount
operator|*=
literal|1.0
operator|-
name|Math
operator|.
name|pow
argument_list|(
literal|.5
argument_list|,
name|groupCount
argument_list|)
expr_stmt|;
return|return
name|rowCount
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
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
comment|// REVIEW jvs 24-Aug-2008:  This is bogus, but no more bogus
comment|// than what's currently in Join.
name|double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// Aggregates with more aggregate functions cost a bit more
name|float
name|multiplier
init|=
literal|1f
operator|+
operator|(
name|float
operator|)
name|aggCalls
operator|.
name|size
argument_list|()
operator|*
literal|0.125f
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
if|if
condition|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SUM"
argument_list|)
condition|)
block|{
comment|// Pretend that SUM costs a little bit more than $SUM0,
comment|// to make things deterministic.
name|multiplier
operator|+=
literal|0.0125f
expr_stmt|;
block|}
block|}
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|rowCount
operator|*
name|multiplier
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|deriveRowType
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
comment|/**    * Computes the row type of an {@code Aggregate} before it exists.    *    * @param typeFactory Type factory    * @param inputRowType Input row type    * @param indicator Deprecated, always false    * @param groupSet Bit set of grouping fields    * @param groupSets List of all grouping sets; null for just {@code groupSet}    * @param aggCalls Collection of calls to aggregate functions    * @return Row type of the aggregate    */
specifier|public
specifier|static
name|RelDataType
name|deriveRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|RelDataType
name|inputRowType
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|groupList
init|=
name|groupSet
operator|.
name|asList
argument_list|()
decl_stmt|;
assert|assert
name|groupList
operator|.
name|size
argument_list|()
operator|==
name|groupSet
operator|.
name|cardinality
argument_list|()
assert|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|containedNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|groupKey
range|:
name|groupList
control|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|fieldList
operator|.
name|get
argument_list|(
name|groupKey
argument_list|)
decl_stmt|;
name|containedNames
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupSets
operator|!=
literal|null
operator|&&
operator|!
name|allContain
argument_list|(
name|groupSets
argument_list|,
name|groupKey
argument_list|)
condition|)
block|{
name|builder
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|indicator
argument_list|,
literal|"indicator is not supported, use GROUPING function instead"
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|AggregateCall
argument_list|>
name|aggCall
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|aggCalls
argument_list|)
control|)
block|{
specifier|final
name|String
name|base
decl_stmt|;
if|if
condition|(
name|aggCall
operator|.
name|e
operator|.
name|name
operator|!=
literal|null
condition|)
block|{
name|base
operator|=
name|aggCall
operator|.
name|e
operator|.
name|name
expr_stmt|;
block|}
else|else
block|{
name|base
operator|=
literal|"$f"
operator|+
operator|(
name|groupList
operator|.
name|size
argument_list|()
operator|+
name|aggCall
operator|.
name|i
operator|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|base
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|containedNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|base
operator|+
literal|"_"
operator|+
name|i
operator|++
expr_stmt|;
block|}
name|containedNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|aggCall
operator|.
name|e
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|allContain
parameter_list|(
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|int
name|groupKey
parameter_list|)
block|{
for|for
control|(
name|ImmutableBitSet
name|groupSet
range|:
name|groupSets
control|)
block|{
if|if
condition|(
operator|!
name|groupSet
operator|.
name|get
argument_list|(
name|groupKey
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
return|return
name|super
operator|.
name|isValid
argument_list|(
name|litmus
argument_list|,
name|context
argument_list|)
operator|&&
name|litmus
operator|.
name|check
argument_list|(
name|Util
operator|.
name|isDistinct
argument_list|(
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
argument_list|,
literal|"distinct field names: {}"
argument_list|,
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether the inferred type of an {@link AggregateCall} matches the    * type it was given when it was created.    *    * @param aggCall Aggregate call    * @param litmus What to do if an error is detected (types do not match)    * @return Whether the inferred and declared types match    */
specifier|private
name|boolean
name|typeMatchesInferred
parameter_list|(
specifier|final
name|AggregateCall
name|aggCall
parameter_list|,
specifier|final
name|Litmus
name|litmus
parameter_list|)
block|{
name|SqlAggFunction
name|aggFunction
init|=
name|aggCall
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
name|AggCallBinding
name|callBinding
init|=
name|aggCall
operator|.
name|createBinding
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|RelDataType
name|type
init|=
name|aggFunction
operator|.
name|inferReturnType
argument_list|(
name|callBinding
argument_list|)
decl_stmt|;
name|RelDataType
name|expectedType
init|=
name|aggCall
operator|.
name|type
decl_stmt|;
return|return
name|RelOptUtil
operator|.
name|eq
argument_list|(
literal|"aggCall type"
argument_list|,
name|expectedType
argument_list|,
literal|"inferred type"
argument_list|,
name|type
argument_list|,
name|litmus
argument_list|)
return|;
block|}
comment|/**    * Returns whether any of the aggregates are DISTINCT.    *    * @return Whether any of the aggregates are DISTINCT    */
specifier|public
name|boolean
name|containsDistinctCall
parameter_list|()
block|{
for|for
control|(
name|AggregateCall
name|call
range|:
name|aggCalls
control|)
block|{
if|if
condition|(
name|call
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns the type of roll-up.    *    * @return Type of roll-up    */
specifier|public
name|Group
name|getGroupType
parameter_list|()
block|{
return|return
name|Group
operator|.
name|induce
argument_list|(
name|groupSet
argument_list|,
name|groupSets
argument_list|)
return|;
block|}
comment|/** What kind of roll-up is it? */
specifier|public
enum|enum
name|Group
block|{
name|SIMPLE
block|,
name|ROLLUP
block|,
name|CUBE
block|,
name|OTHER
block|;
specifier|public
specifier|static
name|Group
name|induce
parameter_list|(
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ImmutableBitSet
operator|.
name|ORDERING
operator|.
name|isStrictlyOrdered
argument_list|(
name|groupSets
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must be sorted: "
operator|+
name|groupSets
argument_list|)
throw|;
block|}
if|if
condition|(
name|groupSets
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|groupSets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|groupSet
argument_list|)
condition|)
block|{
return|return
name|SIMPLE
return|;
block|}
if|if
condition|(
name|groupSets
operator|.
name|size
argument_list|()
operator|==
name|IntMath
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|groupSet
operator|.
name|cardinality
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|CUBE
return|;
block|}
if|if
condition|(
name|isRollup
argument_list|(
name|groupSet
argument_list|,
name|groupSets
argument_list|)
condition|)
block|{
return|return
name|ROLLUP
return|;
block|}
return|return
name|OTHER
return|;
block|}
comment|/** Returns whether a list of sets is a rollup.      *      *<p>For example, if {@code groupSet} is<code>{2, 4, 5}</code>, then      *<code>[{2, 4, 5], {2, 5}, {5}, {}]</code> is a rollup. The first item is      * equal to {@code groupSet}, and each subsequent item is a subset with one      * fewer bit than the previous.      *      * @see #getRollup(List) */
specifier|public
specifier|static
name|boolean
name|isRollup
parameter_list|(
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|)
block|{
if|if
condition|(
name|groupSets
operator|.
name|size
argument_list|()
operator|!=
name|groupSet
operator|.
name|cardinality
argument_list|()
operator|+
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ImmutableBitSet
name|g
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|bitSet
range|:
name|groupSets
control|)
block|{
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
comment|// First item must equal groupSet
if|if
condition|(
operator|!
name|bitSet
operator|.
name|equals
argument_list|(
name|groupSet
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// Each subsequent items must be a subset with one fewer bit than the
comment|// previous item
if|if
condition|(
operator|!
name|g
operator|.
name|contains
argument_list|(
name|bitSet
argument_list|)
operator|||
name|g
operator|.
name|except
argument_list|(
name|bitSet
argument_list|)
operator|.
name|cardinality
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|g
operator|=
name|bitSet
expr_stmt|;
block|}
assert|assert
name|g
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
literal|true
return|;
block|}
comment|/** Returns the ordered list of bits in a rollup.      *      *<p>For example, given a {@code groupSets} value      *<code>[{2, 4, 5], {2, 5}, {5}, {}]</code>, returns the list      * {@code [5, 2, 4]}, which are the succession of bits      * added to each of the sets starting with the empty set.      *      * @see #isRollup(ImmutableBitSet, List) */
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|getRollup
parameter_list|(
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ImmutableBitSet
name|g
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|bitSet
range|:
name|groupSets
control|)
block|{
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
comment|// First item must equal groupSet
block|}
else|else
block|{
comment|// Each subsequent items must be a subset with one fewer bit than the
comment|// previous item
name|set
operator|.
name|addAll
argument_list|(
name|g
operator|.
name|except
argument_list|(
name|bitSet
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|g
operator|=
name|bitSet
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|set
argument_list|)
operator|.
name|reverse
argument_list|()
return|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Implementation of the {@link SqlOperatorBinding} interface for an    * {@link AggregateCall aggregate call} applied to a set of operands in the    * context of a {@link org.apache.calcite.rel.logical.LogicalAggregate}.    */
specifier|public
specifier|static
class|class
name|AggCallBinding
extends|extends
name|SqlOperatorBinding
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|operands
decl_stmt|;
specifier|private
specifier|final
name|int
name|groupCount
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|filter
decl_stmt|;
comment|/**      * Creates an AggCallBinding      *      * @param typeFactory  Type factory      * @param aggFunction  Aggregate function      * @param operands     Data types of operands      * @param groupCount   Number of columns in the GROUP BY clause      * @param filter       Whether the aggregate function has a FILTER clause      */
specifier|public
name|AggCallBinding
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|operands
parameter_list|,
name|int
name|groupCount
parameter_list|,
name|boolean
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
name|aggFunction
argument_list|)
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|operands
expr_stmt|;
name|this
operator|.
name|groupCount
operator|=
name|groupCount
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
assert|assert
name|operands
operator|!=
literal|null
operator|:
literal|"operands of aggregate call should not be null"
assert|;
assert|assert
name|groupCount
operator|>=
literal|0
operator|:
literal|"number of group by columns should be greater than zero in "
operator|+
literal|"aggregate call. Got "
operator|+
name|groupCount
assert|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getGroupCount
parameter_list|()
block|{
return|return
name|groupCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
specifier|public
name|int
name|getOperandCount
parameter_list|()
block|{
return|return
name|operands
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getOperandType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
specifier|public
name|CalciteException
name|newError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Aggregate.java
end_comment

end_unit

