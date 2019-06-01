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
name|metadata
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
name|volcano
operator|.
name|RelSubset
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
name|Exchange
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
name|Filter
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
name|Project
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
name|Sort
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
name|Union
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
name|Values
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
name|util
operator|.
name|Bug
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
name|BuiltInMethod
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
name|NumberUtil
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
comment|/**  * RelMdDistinctRowCount supplies a default implementation of  * {@link RelMetadataQuery#getDistinctRowCount} for the standard logical  * algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdDistinctRowCount
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|DistinctRowCount
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
name|BuiltInMethod
operator|.
name|DISTINCT_ROW_COUNT
operator|.
name|method
argument_list|,
operator|new
name|RelMdDistinctRowCount
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelMdDistinctRowCount
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|DistinctRowCount
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|DistinctRowCount
operator|.
name|DEF
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.DistinctRowCount#getDistinctRowCount(ImmutableBitSet, RexNode)},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getDistinctRowCount(RelNode, ImmutableBitSet, RexNode)    */
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
comment|// REVIEW zfong 4/19/06 - Broadbase code does not take into
comment|// consideration selectivity of predicates passed in.  Also, they
comment|// assume the rows are unique even if the table is not
name|boolean
name|uniq
init|=
name|RelMdUtil
operator|.
name|areColumnsDefinitelyUnique
argument_list|(
name|mq
argument_list|,
name|rel
argument_list|,
name|groupKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniq
condition|)
block|{
return|return
name|NumberUtil
operator|.
name|multiply
argument_list|(
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|,
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
argument_list|,
name|predicate
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
name|double
name|rowCount
init|=
literal|0.0
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
index|]
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
comment|// convert the predicate to reference the types of the union child
name|RexNode
name|modifiedPred
decl_stmt|;
if|if
condition|(
name|predicate
operator|==
literal|null
condition|)
block|{
name|modifiedPred
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|modifiedPred
operator|=
name|predicate
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
literal|null
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Double
name|partialRowCount
init|=
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|input
argument_list|,
name|groupKey
argument_list|,
name|modifiedPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|rowCount
operator|+=
name|partialRowCount
expr_stmt|;
block|}
return|return
name|rowCount
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
operator|||
name|predicate
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
if|if
condition|(
name|groupKey
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|1D
return|;
block|}
block|}
comment|// REVIEW zfong 4/18/06 - In the Broadbase code, duplicates are not
comment|// removed from the two filter lists.  However, the code below is
comment|// doing so.
name|RexNode
name|unionPreds
init|=
name|RelMdUtil
operator|.
name|unionPreds
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|predicate
argument_list|,
name|rel
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|,
name|unionPreds
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|getJoinDistinctRowCount
argument_list|(
name|mq
argument_list|,
name|rel
argument_list|,
name|rel
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 1.21
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
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
name|SemiJoin
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|getSemiJoinDistinctRowCount
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
operator|||
name|predicate
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
if|if
condition|(
name|groupKey
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|1D
return|;
block|}
block|}
comment|// determine which predicates can be applied on the child of the
comment|// aggregate
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|notPushable
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
name|pushable
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitFilters
argument_list|(
name|rel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|predicate
argument_list|,
name|pushable
argument_list|,
name|notPushable
argument_list|)
expr_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexNode
name|childPreds
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|pushable
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// set the bits as they correspond to the child input
name|ImmutableBitSet
operator|.
name|Builder
name|childKey
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RelMdUtil
operator|.
name|setAggChildKeys
argument_list|(
name|groupKey
argument_list|,
name|rel
argument_list|,
name|childKey
argument_list|)
expr_stmt|;
name|Double
name|distinctRowCount
init|=
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|childKey
operator|.
name|build
argument_list|()
argument_list|,
name|childPreds
argument_list|)
decl_stmt|;
if|if
condition|(
name|distinctRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|notPushable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|distinctRowCount
return|;
block|}
else|else
block|{
name|RexNode
name|preds
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|notPushable
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|distinctRowCount
operator|*
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|preds
argument_list|)
return|;
block|}
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Values
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
operator|||
name|predicate
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
if|if
condition|(
name|groupKey
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|1D
return|;
block|}
block|}
name|double
name|selectivity
init|=
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|predicate
argument_list|)
decl_stmt|;
comment|// assume half the rows are duplicates
name|double
name|nRows
init|=
name|rel
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
operator|/
literal|2
decl_stmt|;
return|return
name|RelMdUtil
operator|.
name|numDistinctVals
argument_list|(
name|nRows
argument_list|,
name|nRows
operator|*
name|selectivity
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
if|if
condition|(
name|predicate
operator|==
literal|null
operator|||
name|predicate
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
if|if
condition|(
name|groupKey
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|1D
return|;
block|}
block|}
name|ImmutableBitSet
operator|.
name|Builder
name|baseCols
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableBitSet
operator|.
name|Builder
name|projCols
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|projExprs
init|=
name|rel
operator|.
name|getProjects
argument_list|()
decl_stmt|;
name|RelMdUtil
operator|.
name|splitCols
argument_list|(
name|projExprs
argument_list|,
name|groupKey
argument_list|,
name|baseCols
argument_list|,
name|projCols
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|notPushable
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
name|pushable
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitFilters
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|,
name|predicate
argument_list|,
name|pushable
argument_list|,
name|notPushable
argument_list|)
expr_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
comment|// get the distinct row count of the child input, passing in the
comment|// columns and filters that only reference the child; convert the
comment|// filter to reference the children projection expressions
name|RexNode
name|childPred
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|pushable
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RexNode
name|modifiedPred
decl_stmt|;
if|if
condition|(
name|childPred
operator|==
literal|null
condition|)
block|{
name|modifiedPred
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|modifiedPred
operator|=
name|RelOptUtil
operator|.
name|pushPastProject
argument_list|(
name|childPred
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
name|Double
name|distinctRowCount
init|=
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|baseCols
operator|.
name|build
argument_list|()
argument_list|,
name|modifiedPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|distinctRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
operator|!
name|notPushable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|RexNode
name|preds
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|notPushable
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|distinctRowCount
operator|*=
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|preds
argument_list|)
expr_stmt|;
block|}
comment|// No further computation required if the projection expressions
comment|// are all column references
if|if
condition|(
name|projCols
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|distinctRowCount
return|;
block|}
comment|// multiply by the cardinality of the non-child projection expressions
for|for
control|(
name|int
name|bit
range|:
name|projCols
operator|.
name|build
argument_list|()
control|)
block|{
name|Double
name|subRowCount
init|=
name|RelMdUtil
operator|.
name|cardOfProjExpr
argument_list|(
name|mq
argument_list|,
name|rel
argument_list|,
name|projExprs
operator|.
name|get
argument_list|(
name|bit
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|subRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|distinctRowCount
operator|*=
name|subRowCount
expr_stmt|;
block|}
return|return
name|RelMdUtil
operator|.
name|numDistinctVals
argument_list|(
name|distinctRowCount
argument_list|,
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
specifier|final
name|RelNode
name|best
init|=
name|rel
operator|.
name|getBest
argument_list|()
decl_stmt|;
if|if
condition|(
name|best
operator|!=
literal|null
condition|)
block|{
return|return
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|best
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_1048_FIXED
condition|)
block|{
return|return
name|getDistinctRowCount
argument_list|(
operator|(
name|RelNode
operator|)
name|rel
argument_list|,
name|mq
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
return|;
block|}
name|Double
name|d
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RelNode
name|r2
range|:
name|rel
operator|.
name|getRels
argument_list|()
control|)
block|{
try|try
block|{
name|Double
name|d2
init|=
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|r2
argument_list|,
name|groupKey
argument_list|,
name|predicate
argument_list|)
decl_stmt|;
name|d
operator|=
name|NumberUtil
operator|.
name|min
argument_list|(
name|d
argument_list|,
name|d2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CyclicMetadataException
name|e
parameter_list|)
block|{
comment|// Ignore this relational expression; there will be non-cyclic ones
comment|// in this set.
block|}
block|}
return|return
name|d
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdDistinctRowCount.java
end_comment

end_unit

