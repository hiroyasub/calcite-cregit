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
name|reltype
operator|.
name|RelDataType
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
name|sql
operator|.
name|SqlAggFunction
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
name|ImmutableIntList
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
name|Util
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
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

begin_comment
comment|/**  * A relational expression representing a set of window aggregates.  *  *<p>A window rel can handle several window aggregate functions, over several  * partitions, with pre- and post-expressions, and an optional post-filter.  * Each of the partitions is defined by a partition key (zero or more columns)  * and a range (logical or physical). The partitions expect the data to be  * sorted correctly on input to the relational expression.  *  *<p>Each {@link org.eigenbase.rel.WindowRelBase.Window} has a set of  * {@link org.eigenbase.rex.RexOver} objects.  *  *<p>Created by {@link org.eigenbase.rel.rules.WindowedAggSplitterRule}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WindowRelBase
extends|extends
name|SingleRel
block|{
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Window
argument_list|>
name|windows
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|constants
decl_stmt|;
comment|/**    * Creates a window relational expression.    *    * @param cluster Cluster    * @param child   Input relational expression    * @param constants List of constants that are additional inputs    * @param rowType Output row type    * @param windows Windows    */
specifier|public
name|WindowRelBase
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
name|Window
argument_list|>
name|windows
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
name|windows
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|windows
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
comment|// In the window specifications, an aggregate call such as
comment|// 'SUM(RexInputRef #10)' refers to expression #10 of inputProgram.
comment|// (Not its projections.)
specifier|final
name|RelDataType
name|childRowType
init|=
name|getChild
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
name|fail
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Window
name|window
range|:
name|windows
control|)
block|{
for|for
control|(
name|RexWinAggCall
name|over
range|:
name|window
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
literal|false
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
assert|assert
operator|!
name|fail
operator|:
literal|"empty"
assert|;
return|return
literal|false
return|;
block|}
return|return
literal|true
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
name|Window
argument_list|>
name|window
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|windows
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
name|RelCollationImpl
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
comment|/**    * A Window is a range of input rows, defined by an upper and lower bound.    * It also has zero or more partitioning columns.    *    *<p>A window is either logical or physical. A physical window is measured    * in terms of row count. A logical window is measured in terms of rows    * within a certain distance from the current sort key.    *    *<p>For example:    *    *<ul>    *<li><code>ROWS BETWEEN 10 PRECEDING and 5 FOLLOWING</code> is a physical    * window with an upper and lower bound;    *<li><code>RANGE BETWEEN INTERVAL '1' HOUR PRECEDING AND UNBOUNDED    * FOLLOWING</code> is a logical window with only a lower bound;    *<li><code>RANGE INTERVAL '10' MINUTES PRECEDING</code> (which is    * equivalent to<code>RANGE BETWEEN INTERVAL '10' MINUTES PRECEDING AND    * CURRENT ROW</code>) is a logical window with an upper and lower bound.    *</ul>    */
specifier|public
specifier|static
class|class
name|Window
block|{
specifier|public
specifier|final
name|BitSet
name|groupSet
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
comment|/**      * List of {@link org.eigenbase.rel.WindowRelBase.RexWinAggCall}      * objects, each of which is a call to a      * {@link org.eigenbase.sql.SqlAggFunction}.      */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexWinAggCall
argument_list|>
name|aggCalls
decl_stmt|;
specifier|public
name|Window
parameter_list|(
name|BitSet
name|groupSet
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
assert|assert
name|orderKeys
operator|!=
literal|null
operator|:
literal|"precondition: ordinals != null"
assert|;
assert|assert
name|groupSet
operator|!=
literal|null
assert|;
name|this
operator|.
name|groupSet
operator|=
name|groupSet
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
name|lowerBound
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|upperBound
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|orderKeys
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
specifier|private
name|String
name|computeString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"window(partition "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|groupSet
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|orderKeys
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|isRows
condition|?
literal|" rows "
else|:
literal|" range "
argument_list|)
expr_stmt|;
if|if
condition|(
name|lowerBound
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|upperBound
operator|!=
literal|null
condition|)
block|{
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
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|lowerBound
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|upperBound
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|upperBound
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|" aggs "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|aggCalls
argument_list|)
expr_stmt|;
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
name|Window
operator|&&
name|this
operator|.
name|digest
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Window
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
comment|/**      * Returns if the window is guaranteed to have rows.      * This is useful to refine data type of window aggregates.      * For instance sum(non-nullable) over (empty window) is NULL.      * @return true when the window is non-empty      * @see org.eigenbase.sql.SqlWindow#isAlwaysNonEmpty()      * @see org.eigenbase.sql.SqlOperatorBinding#getGroupCount()      * @see org.eigenbase.sql.validate.SqlValidatorImpl#resolveWindow(org.eigenbase.sql.SqlNode, org.eigenbase.sql.validate.SqlValidatorScope, boolean)      */
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
name|WindowRelBase
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
name|getChild
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
return|return
operator|new
name|AggregateCall
argument_list|(
operator|(
name|Aggregation
operator|)
name|aggCall
operator|.
name|getOperator
argument_list|()
argument_list|,
literal|false
argument_list|,
name|getProjectOrdinals
argument_list|(
name|aggCall
operator|.
name|getOperands
argument_list|()
argument_list|)
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
comment|/**    * A call to a windowed aggregate function.    *    *<p>Belongs to a {@link org.eigenbase.rel.WindowRelBase.Window}.    *    *<p>It's a bastard son of a {@link org.eigenbase.rex.RexCall}; similar    * enough that it gets visited by a {@link org.eigenbase.rex.RexVisitor},    * but it also has some extra data members.    */
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
comment|/**      * Creates a RexWinAggCall.      *      * @param aggFun   Aggregate function      * @param type     Result type      * @param operands Operands to call      * @param ordinal  Ordinal within its partition      */
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End WindowRelBase.java
end_comment

end_unit

