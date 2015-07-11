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
name|SemiJoin
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
comment|/**  * RelMdSelectivity supplies a default implementation of  * {@link RelMetadataQuery#getSelectivity} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdSelectivity
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
name|SELECTIVITY
operator|.
name|method
argument_list|,
operator|new
name|RelMdSelectivity
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelMdSelectivity
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
if|if
condition|(
operator|(
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
operator|||
operator|(
name|predicate
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|1.0
return|;
block|}
name|double
name|sumRows
init|=
literal|0.0
decl_stmt|;
name|double
name|sumSelectedRows
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
name|Double
name|nRows
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|nRows
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// convert the predicate to reference the types of the union child
name|RexNode
name|modifiedPred
init|=
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
decl_stmt|;
name|double
name|sel
init|=
name|mq
operator|.
name|getSelectivity
argument_list|(
name|input
argument_list|,
name|modifiedPred
argument_list|)
decl_stmt|;
name|sumRows
operator|+=
name|nRows
expr_stmt|;
name|sumSelectedRows
operator|+=
name|nRows
operator|*
name|sel
expr_stmt|;
block|}
if|if
condition|(
name|sumRows
operator|<
literal|1.0
condition|)
block|{
name|sumRows
operator|=
literal|1.0
expr_stmt|;
block|}
return|return
name|sumSelectedRows
operator|/
name|sumRows
return|;
block|}
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|predicate
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
comment|// Take the difference between the predicate passed in and the
comment|// predicate in the filter's condition, so we don't apply the
comment|// selectivity of the filter twice.  If no predicate is passed in,
comment|// use the filter's condition.
if|if
condition|(
name|predicate
operator|!=
literal|null
condition|)
block|{
return|return
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|RelMdUtil
operator|.
name|minusPreds
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
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|SemiJoin
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
comment|// create a RexNode representing the selectivity of the
comment|// semijoin filter and pass it to getSelectivity
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
name|newPred
init|=
name|RelMdUtil
operator|.
name|makeSemiJoinSelectivityRexNode
argument_list|(
name|mq
argument_list|,
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
name|newPred
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|newPred
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
return|return
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|newPred
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
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
name|Double
name|selectivity
init|=
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|childPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectivity
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|RexNode
name|pred
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
name|selectivity
operator|*
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|pred
argument_list|)
return|;
block|}
block|}
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
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
name|selectivity
init|=
name|mq
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|modifiedPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectivity
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|RexNode
name|pred
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
name|selectivity
operator|*
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|pred
argument_list|)
return|;
block|}
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Double
name|getSelectivity
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|guessSelectivity
argument_list|(
name|predicate
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdSelectivity.java
end_comment

end_unit

