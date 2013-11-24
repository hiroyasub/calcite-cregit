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
name|rel
operator|.
name|metadata
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
name|parser
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
name|validate
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
name|*
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
comment|/**  *<code>AggregateRelBase</code> is an abstract base class for implementations  * of {@link AggregateRel}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AggregateRelBase
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
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
name|BitSet
name|groupSet
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an AggregateRelBase.      *      * @param cluster Cluster      * @param traits Traits      * @param child Child      * @param groupSet Bitset of grouping fields      * @param aggCalls Collection of calls to aggregate functions      */
specifier|protected
name|AggregateRelBase
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
name|BitSet
name|groupSet
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
name|Util
operator|.
name|pre
argument_list|(
name|aggCalls
operator|!=
literal|null
argument_list|,
literal|"aggCalls != null"
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
name|groupSet
expr_stmt|;
assert|assert
name|groupSet
operator|!=
literal|null
assert|;
assert|assert
name|groupSet
operator|.
name|isEmpty
argument_list|()
operator|==
operator|(
name|groupSet
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
operator|)
operator|:
literal|"See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6222207"
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelNode
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
comment|// we never return duplicate rows
return|return
literal|true
return|;
block|}
comment|/**      * Returns a list of calls to aggregate functions.      *      * @return list of calls to aggregate functions      */
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
comment|/**      * Returns the number of grouping fields.      * These grouping fields are the leading fields in both the input and output      * records.      *      *<p>NOTE: The {@link #getGroupSet()} data structure allows for the      * grouping fields to not be on the leading edge. New code should, if      * possible, assume that grouping fields are in arbitrary positions in the      * input relational expression.      *      * @return number of grouping fields      */
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
comment|/**      * Returns a bitmap of the grouping fields.      *      * @return bitset of ordinals of grouping fields      */
specifier|public
name|BitSet
name|getGroupSet
parameter_list|()
block|{
return|return
name|groupSet
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
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
expr_stmt|;
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
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|ord
operator|.
name|e
operator|.
name|name
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|ord
operator|.
name|e
operator|.
name|name
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"agg#"
operator|+
name|ord
operator|.
name|i
expr_stmt|;
block|}
name|pw
operator|.
name|item
argument_list|(
name|name
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
return|;
block|}
comment|// implement RelNode
specifier|public
name|double
name|getRows
parameter_list|()
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
name|getRows
argument_list|()
decl_stmt|;
name|rowCount
operator|*=
operator|(
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
operator|)
expr_stmt|;
return|return
name|rowCount
return|;
block|}
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// REVIEW jvs 24-Aug-2008:  This is bogus, but no more bogus
comment|// than what's currently in JoinRelBase.
name|double
name|rowCount
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|makeCost
argument_list|(
name|rowCount
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
comment|//noinspection unchecked
return|return
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|CompositeList
operator|.
name|of
argument_list|(
comment|// fields derived from grouping columns
operator|new
name|AbstractList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|groupSet
operator|.
name|cardinality
argument_list|()
return|;
block|}
specifier|public
name|RelDataTypeField
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|BitSets
operator|.
name|toList
argument_list|(
name|groupSet
argument_list|)
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|,
comment|// fields derived from aggregate calls
operator|new
name|AbstractList
argument_list|<
name|RelDataTypeField
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
name|RelDataTypeField
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|AggregateCall
name|aggCall
init|=
name|aggCalls
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|String
name|name
decl_stmt|;
if|if
condition|(
name|aggCall
operator|.
name|name
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|aggCall
operator|.
name|name
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"$f"
operator|+
operator|(
name|groupSet
operator|.
name|cardinality
argument_list|()
operator|+
name|index
operator|)
expr_stmt|;
block|}
assert|assert
name|typeMatchesInferred
argument_list|(
name|aggCall
argument_list|,
literal|true
argument_list|)
assert|;
return|return
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|name
argument_list|,
name|index
argument_list|,
name|aggCall
operator|.
name|type
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns whether the inferred type of an {@link AggregateCall} matches the      * type it was given when it was created.      *      * @param aggCall Aggregate call      * @param fail Whether to fail if the types do not match      *      * @return Whether the inferred and declared types match      */
specifier|private
name|boolean
name|typeMatchesInferred
parameter_list|(
specifier|final
name|AggregateCall
name|aggCall
parameter_list|,
specifier|final
name|boolean
name|fail
parameter_list|)
block|{
name|SqlAggFunction
name|aggFunction
init|=
operator|(
name|SqlAggFunction
operator|)
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
name|fail
argument_list|)
return|;
block|}
comment|/**      * Returns whether any of the aggregates are DISTINCT.      */
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
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Implementation of the {@link SqlOperatorBinding} interface for an {@link      * AggregateCall aggregate call} applied to a set of operands in the context      * of a {@link AggregateRel}.      */
specifier|public
specifier|static
class|class
name|AggCallBinding
extends|extends
name|SqlOperatorBinding
block|{
specifier|private
specifier|final
name|AggregateRelBase
name|aggregateRel
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|operands
decl_stmt|;
comment|/**          * Creates an AggCallBinding          *          * @param typeFactory Type factory          * @param aggFunction Aggregation function          * @param aggregateRel Relational expression which is context          * @param operands Operand ordinals          */
name|AggCallBinding
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|AggregateRelBase
name|aggregateRel
parameter_list|,
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|operands
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
name|aggregateRel
operator|=
name|aggregateRel
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|operands
expr_stmt|;
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
specifier|final
name|RelDataType
name|childType
init|=
name|aggregateRel
operator|.
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
name|int
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
return|return
name|childType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|operand
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|EigenbaseException
name|newError
parameter_list|(
name|SqlValidatorException
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
comment|// End AggregateRelBase.java
end_comment

end_unit

