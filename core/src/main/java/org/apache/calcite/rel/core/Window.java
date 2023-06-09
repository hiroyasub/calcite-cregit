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
name|RelCollations
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
name|hint
operator|.
name|Hintable
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
name|hint
operator|.
name|RelHint
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
name|rex
operator|.
name|RexCall
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
name|RexChecker
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
name|RexFieldCollation
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
name|RexLocalRef
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
name|RexSlot
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
name|RexWindowBound
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
name|initialization
operator|.
name|qual
operator|.
name|UnderInitialization
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|RequiresNonNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * A relational expression representing a set of window aggregates.  *  *<p>A Window can handle several window aggregate functions, over several  * partitions, with pre- and post-expressions, and an optional post-filter.  * Each of the partitions is defined by a partition key (zero or more columns)  * and a range (logical or physical). The partitions expect the data to be  * sorted correctly on input to the relational expression.  *  *<p>Each {@link Window.Group} has a set of  * {@link org.apache.calcite.rex.RexOver} objects.  *  *<p>Created by {@link org.apache.calcite.rel.rules.ProjectToWindowRule}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Window
extends|extends
name|SingleRel
implements|implements
name|Hintable
block|{
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Group
argument_list|>
name|groups
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|constants
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|hints
decl_stmt|;
comment|/**    * Creates a window relational expression.    *    * @param cluster Cluster    * @param traitSet Trait set    * @param hints   Hints for this node    * @param input   Input relational expression    * @param constants List of constants that are additional inputs    * @param rowType Output row type    * @param groups Windows    */
specifier|protected
name|Window
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|constants
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|constants
argument_list|)
expr_stmt|;
assert|assert
name|rowType
operator|!=
literal|null
assert|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|this
operator|.
name|hints
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|hints
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a window relational expression.    *    * @param cluster Cluster    * @param traitSet Trait set    * @param input   Input relational expression    * @param constants List of constants that are additional inputs    * @param rowType Output row type    * @param groups Windows    */
specifier|public
name|Window
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|input
argument_list|,
name|constants
argument_list|,
name|rowType
argument_list|,
name|groups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|,
annotation|@
name|Nullable
name|Context
name|context
parameter_list|)
block|{
comment|// In the window specifications, an aggregate call such as
comment|// 'SUM(RexInputRef #10)' refers to expression #10 of inputProgram.
comment|// (Not its projections.)
specifier|final
name|RelDataType
name|childRowType
init|=
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|int
name|childFieldCount
init|=
name|childRowType
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|inputSize
init|=
name|childFieldCount
operator|+
name|constants
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|inputTypes
init|=
operator|new
name|AbstractList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelDataType
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|index
operator|<
name|childFieldCount
condition|?
name|childRowType
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
else|:
name|constants
operator|.
name|get
argument_list|(
name|index
operator|-
name|childFieldCount
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|inputSize
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RexChecker
name|checker
init|=
operator|new
name|RexChecker
argument_list|(
name|inputTypes
argument_list|,
name|context
argument_list|,
name|litmus
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Group
name|group
range|:
name|groups
control|)
block|{
for|for
control|(
name|RexWinAggCall
name|over
range|:
name|group
operator|.
name|aggCalls
control|)
block|{
operator|++
name|count
expr_stmt|;
if|if
condition|(
operator|!
name|checker
operator|.
name|isValid
argument_list|(
name|over
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|null
argument_list|)
return|;
block|}
block|}
block|}
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"empty"
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
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
for|for
control|(
name|Ord
argument_list|<
name|Group
argument_list|>
name|window
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|groups
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"window#"
operator|+
name|window
operator|.
name|i
argument_list|,
name|window
operator|.
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
return|;
block|}
specifier|public
specifier|static
name|ImmutableIntList
name|getProjectOrdinals
parameter_list|(
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
parameter_list|)
block|{
return|return
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
operator|(
name|RexSlot
operator|)
name|exprs
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|)
operator|.
name|getIndex
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|exprs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RelCollation
name|getCollation
parameter_list|(
specifier|final
name|List
argument_list|<
name|RexFieldCollation
argument_list|>
name|collations
parameter_list|)
block|{
return|return
name|RelCollations
operator|.
name|of
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|RelFieldCollation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelFieldCollation
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|RexFieldCollation
name|collation
init|=
name|collations
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelFieldCollation
argument_list|(
operator|(
operator|(
name|RexLocalRef
operator|)
name|collation
operator|.
name|left
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|,
name|collation
operator|.
name|getDirection
argument_list|()
argument_list|,
name|collation
operator|.
name|getNullDirection
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|collations
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Returns constants that are additional inputs of current relation.    * @return constants that are additional inputs of current relation    */
specifier|public
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|getConstants
parameter_list|()
block|{
return|return
name|constants
return|;
block|}
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
comment|// Cost is proportional to the number of rows and the number of
comment|// components (groups and aggregate functions). There is
comment|// no I/O cost.
comment|//
comment|// TODO #1. Add memory cost.
comment|// TODO #2. MIN and MAX have higher CPU cost than SUM and COUNT.
specifier|final
name|double
name|rowsIn
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|groups
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Group
name|group
range|:
name|groups
control|)
block|{
name|count
operator|+=
name|group
operator|.
name|aggCalls
operator|.
name|size
argument_list|()
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
name|rowsIn
argument_list|,
name|rowsIn
operator|*
name|count
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Group of windowed aggregate calls that have the same window specification.    *    *<p>The specification is defined by an upper and lower bound, and    * also has zero or more partitioning columns.    *    *<p>A window is either logical or physical. A physical window is measured    * in terms of row count. A logical window is measured in terms of rows    * within a certain distance from the current sort key.    *    *<p>For example:    *    *<ul>    *<li><code>ROWS BETWEEN 10 PRECEDING and 5 FOLLOWING</code> is a physical    * window with an upper and lower bound;    *<li><code>RANGE BETWEEN INTERVAL '1' HOUR PRECEDING AND UNBOUNDED    * FOLLOWING</code> is a logical window with only a lower bound;    *<li><code>RANGE INTERVAL '10' MINUTES PRECEDING</code> (which is    * equivalent to<code>RANGE BETWEEN INTERVAL '10' MINUTES PRECEDING AND    * CURRENT ROW</code>) is a logical window with an upper and lower bound.    *</ul>    */
specifier|public
specifier|static
class|class
name|Group
block|{
specifier|public
specifier|final
name|ImmutableBitSet
name|keys
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|isRows
decl_stmt|;
specifier|public
specifier|final
name|RexWindowBound
name|lowerBound
decl_stmt|;
specifier|public
specifier|final
name|RexWindowBound
name|upperBound
decl_stmt|;
specifier|public
specifier|final
name|RelCollation
name|orderKeys
decl_stmt|;
specifier|private
specifier|final
name|String
name|digest
decl_stmt|;
comment|/**      * List of {@link Window.RexWinAggCall}      * objects, each of which is a call to a      * {@link org.apache.calcite.sql.SqlAggFunction}.      */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexWinAggCall
argument_list|>
name|aggCalls
decl_stmt|;
specifier|public
name|Group
parameter_list|(
name|ImmutableBitSet
name|keys
parameter_list|,
name|boolean
name|isRows
parameter_list|,
name|RexWindowBound
name|lowerBound
parameter_list|,
name|RexWindowBound
name|upperBound
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|List
argument_list|<
name|RexWinAggCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|this
operator|.
name|keys
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|keys
argument_list|,
literal|"keys"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isRows
operator|=
name|isRows
expr_stmt|;
name|this
operator|.
name|lowerBound
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|lowerBound
argument_list|,
literal|"lowerBound"
argument_list|)
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|upperBound
argument_list|,
literal|"upperBound"
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|orderKeys
argument_list|,
literal|"orderKeys"
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
name|digest
operator|=
name|computeString
argument_list|()
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
name|digest
return|;
block|}
annotation|@
name|RequiresNonNull
argument_list|(
block|{
literal|"keys"
block|,
literal|"orderKeys"
block|,
literal|"lowerBound"
block|,
literal|"upperBound"
block|,
literal|"aggCalls"
block|}
argument_list|)
specifier|private
name|String
name|computeString
parameter_list|(
annotation|@
name|UnderInitialization
name|Group
name|this
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"window("
argument_list|)
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|buf
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"partition "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|keys
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|orderKeys
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
name|i
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"order by "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|orderKeys
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|orderKeys
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isUnbounded
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isPreceding
argument_list|()
operator|&&
name|upperBound
operator|.
name|isUnbounded
argument_list|()
operator|&&
name|upperBound
operator|.
name|isFollowing
argument_list|()
condition|)
block|{
comment|// skip bracket if no ORDER BY, and if bracket is the default,
comment|// "RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING",
comment|// which is equivalent to
comment|// "ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING"
block|}
if|else if
condition|(
operator|!
name|orderKeys
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isUnbounded
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isPreceding
argument_list|()
operator|&&
name|upperBound
operator|.
name|isCurrentRow
argument_list|()
operator|&&
operator|!
name|isRows
condition|)
block|{
comment|// skip bracket if there is ORDER BY, and if bracket is the default,
comment|// "RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW",
comment|// which is NOT equivalent to
comment|// "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW"
block|}
else|else
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
name|i
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|isRows
condition|?
literal|"rows "
else|:
literal|"range "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"between "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|lowerBound
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" and "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|upperBound
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|aggCalls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
name|i
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"aggs "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|aggCalls
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|Group
operator|&&
name|this
operator|.
name|digest
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Group
operator|)
name|obj
operator|)
operator|.
name|digest
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|digest
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|RelCollation
name|collation
parameter_list|()
block|{
return|return
name|orderKeys
return|;
block|}
comment|/**      * Returns if the window is guaranteed to have rows.      * This is useful to refine data type of window aggregates.      * For instance sum(non-nullable) over (empty window) is NULL.      * @return true when the window is non-empty      * @see org.apache.calcite.sql.SqlWindow#isAlwaysNonEmpty()      * @see org.apache.calcite.sql.SqlOperatorBinding#getGroupCount()      * @see org.apache.calcite.sql.validate.SqlValidatorImpl#resolveWindow(org.apache.calcite.sql.SqlNode, org.apache.calcite.sql.validate.SqlValidatorScope)      */
specifier|public
name|boolean
name|isAlwaysNonEmpty
parameter_list|()
block|{
name|int
name|lowerKey
init|=
name|lowerBound
operator|.
name|getOrderKey
argument_list|()
decl_stmt|;
name|int
name|upperKey
init|=
name|upperBound
operator|.
name|getOrderKey
argument_list|()
decl_stmt|;
return|return
name|lowerKey
operator|>
operator|-
literal|1
operator|&&
name|lowerKey
operator|<=
name|upperKey
return|;
block|}
comment|/**      * Presents a view of the {@link RexWinAggCall} list as a list of      * {@link AggregateCall}.      */
specifier|public
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|getAggregateCalls
parameter_list|(
name|Window
name|windowRel
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|Util
operator|.
name|skip
argument_list|(
name|windowRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|windowRel
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
return|return
operator|new
name|AbstractList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|aggCalls
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|AggregateCall
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|RexWinAggCall
name|aggCall
init|=
name|aggCalls
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|SqlAggFunction
name|op
init|=
operator|(
name|SqlAggFunction
operator|)
name|aggCall
operator|.
name|getOperator
argument_list|()
decl_stmt|;
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|op
argument_list|,
name|aggCall
operator|.
name|distinct
argument_list|,
literal|false
argument_list|,
name|aggCall
operator|.
name|ignoreNulls
argument_list|,
name|getProjectOrdinals
argument_list|(
name|aggCall
operator|.
name|getOperands
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
name|RelCollations
operator|.
name|EMPTY
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|fieldNames
operator|.
name|get
argument_list|(
name|aggCall
operator|.
name|ordinal
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * A call to a windowed aggregate function.    *    *<p>Belongs to a {@link Window.Group}.    *    *<p>It's a bastard son of a {@link org.apache.calcite.rex.RexCall}; similar    * enough that it gets visited by a {@link org.apache.calcite.rex.RexVisitor},    * but it also has some extra data members.    */
specifier|public
specifier|static
class|class
name|RexWinAggCall
extends|extends
name|RexCall
block|{
comment|/**      * Ordinal of this aggregate within its partition.      */
specifier|public
specifier|final
name|int
name|ordinal
decl_stmt|;
comment|/** Whether to eliminate duplicates before applying aggregate function. */
specifier|public
specifier|final
name|boolean
name|distinct
decl_stmt|;
comment|/** Whether to ignore nulls. */
specifier|public
specifier|final
name|boolean
name|ignoreNulls
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RexWinAggCall
parameter_list|(
name|SqlAggFunction
name|aggFun
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|boolean
name|distinct
parameter_list|)
block|{
name|this
argument_list|(
name|aggFun
argument_list|,
name|type
argument_list|,
name|operands
argument_list|,
name|ordinal
argument_list|,
name|distinct
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a RexWinAggCall.      *      * @param aggFun   Aggregate function      * @param type     Result type      * @param operands Operands to call      * @param ordinal  Ordinal within its partition      * @param distinct Eliminate duplicates before applying aggregate function      */
specifier|public
name|RexWinAggCall
parameter_list|(
name|SqlAggFunction
name|aggFun
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|aggFun
argument_list|,
name|operands
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
name|this
operator|.
name|ignoreNulls
operator|=
name|ignoreNulls
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexWinAggCall
name|that
init|=
operator|(
name|RexWinAggCall
operator|)
name|o
decl_stmt|;
return|return
name|ordinal
operator|==
name|that
operator|.
name|ordinal
operator|&&
name|distinct
operator|==
name|that
operator|.
name|distinct
operator|&&
name|ignoreNulls
operator|==
name|that
operator|.
name|ignoreNulls
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ordinal
argument_list|,
name|distinct
argument_list|,
name|ignoreNulls
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexCall
name|clone
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
return|return
name|super
operator|.
name|clone
argument_list|(
name|type
argument_list|,
name|operands
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|getHints
parameter_list|()
block|{
return|return
name|hints
return|;
block|}
block|}
end_class

end_unit

