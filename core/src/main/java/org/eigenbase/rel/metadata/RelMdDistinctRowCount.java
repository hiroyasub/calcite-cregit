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
name|metadata
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
name|rel
operator|.
name|rules
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
name|sql
operator|.
name|fun
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
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util14
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * RelMdDistinctRowCount supplies a default implementation of {@link  * RelMetadataQuery#getDistinctRowCount} for the standard logical algebra.  *  * @author Zelaine Fong  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RelMdDistinctRowCount
extends|extends
name|ReflectiveRelMetadataProvider
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelMdDistinctRowCount
parameter_list|()
block|{
comment|// Tell superclass reflection about parameter types expected
comment|// for various metadata queries.
comment|// This corresponds to getDistinctRowCount(rel, RexNode predicate);
comment|// note that we don't specify the rel type because we always overload
comment|// on that.
name|List
argument_list|<
name|Class
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
operator|(
name|Class
operator|)
name|BitSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
operator|(
name|Class
operator|)
name|RexNode
operator|.
name|class
argument_list|)
expr_stmt|;
name|mapParameterTypes
argument_list|(
literal|"getDistinctRowCount"
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|UnionRelBase
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
name|Double
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
name|RelMetadataQuery
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
name|SortRel
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getChild
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
name|FilterRelBase
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
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
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getChild
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
name|JoinRelBase
name|rel
parameter_list|,
name|BitSet
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
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|SemiJoinRel
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
comment|// create a RexNode representing the selectivity of the
comment|// semijoin filter and pass it to getDistinctRowCount
name|RexNode
name|newPred
init|=
name|RelMdUtil
operator|.
name|makeSemiJoinSelectivityRexNode
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicate
operator|!=
literal|null
condition|)
block|{
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
name|newPred
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|newPred
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
return|return
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|groupKey
argument_list|,
name|newPred
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|AggregateRelBase
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
comment|// determine which predicates can be applied on the child of the
comment|// aggregate
name|List
argument_list|<
name|RexNode
argument_list|>
name|notPushable
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushable
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitFilters
argument_list|(
name|rel
operator|.
name|getGroupCount
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
name|BitSet
name|childKey
init|=
operator|new
name|BitSet
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
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|childKey
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
name|ValuesRelBase
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
name|Double
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
name|Double
name|nRows
init|=
name|rel
operator|.
name|getRows
argument_list|()
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
name|ProjectRelBase
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
name|BitSet
name|baseCols
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|BitSet
name|projCols
init|=
operator|new
name|BitSet
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|notPushable
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushable
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|splitFilters
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
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
name|pushFilterPastProject
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
name|RelMetadataQuery
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|baseCols
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
name|Util
operator|.
name|toIter
argument_list|(
name|projCols
argument_list|)
control|)
block|{
name|Double
name|subRowCount
init|=
name|RelMdUtil
operator|.
name|cardOfProjExpr
argument_list|(
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
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|)
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Double
name|getDistinctRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|BitSet
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
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|,
name|RelMetadataQuery
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
block|}
end_class

begin_comment
comment|// End RelMdDistinctRowCount.java
end_comment

end_unit

