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
name|RelOptRule
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
name|rel
operator|.
name|logical
operator|.
name|LogicalUnion
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
name|fun
operator|.
name|SqlCountAggFunction
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
name|SqlMinMaxAggFunction
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
name|sql
operator|.
name|fun
operator|.
name|SqlSumAggFunction
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
name|SqlSumEmptyIsZeroAggFunction
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
name|Lists
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
name|IdentityHashMap
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

begin_comment
comment|/**  * Planner rule that pushes an  * {@link org.apache.calcite.rel.logical.LogicalAggregate}  * past a non-distinct {@link org.apache.calcite.rel.logical.LogicalUnion}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateUnionTransposeRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateUnionTransposeRule
name|INSTANCE
init|=
operator|new
name|AggregateUnionTransposeRule
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|Boolean
argument_list|>
name|SUPPORTED_AGGREGATES
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|SUPPORTED_AGGREGATES
operator|.
name|put
argument_list|(
name|SqlMinMaxAggFunction
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SUPPORTED_AGGREGATES
operator|.
name|put
argument_list|(
name|SqlCountAggFunction
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SUPPORTED_AGGREGATES
operator|.
name|put
argument_list|(
name|SqlSumAggFunction
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SUPPORTED_AGGREGATES
operator|.
name|put
argument_list|(
name|SqlSumEmptyIsZeroAggFunction
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Private constructor.    */
specifier|private
name|AggregateUnionTransposeRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|LogicalAggregate
name|aggRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LogicalUnion
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|union
operator|.
name|all
condition|)
block|{
comment|// This transformation is only valid for UNION ALL.
comment|// Consider t1(i) with rows (5), (5) and t2(i) with
comment|// rows (5), (10), and the query
comment|// select sum(i) from (select i from t1) union (select i from t2).
comment|// The correct answer is 15.  If we apply the transformation,
comment|// we get
comment|// select sum(i) from
comment|// (select sum(i) as i from t1) union (select sum(i) as i from t2)
comment|// which yields 25 (incorrect).
return|return;
block|}
name|RelOptCluster
name|cluster
init|=
name|union
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|transformedAggCalls
init|=
name|transformAggCalls
argument_list|(
name|aggRel
argument_list|,
name|aggRel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|cardinality
argument_list|()
argument_list|,
name|aggRel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transformedAggCalls
operator|==
literal|null
condition|)
block|{
comment|// we've detected the presence of something like AVG,
comment|// which we can't handle
return|return;
block|}
name|boolean
name|anyTransformed
init|=
literal|false
decl_stmt|;
comment|// create corresponding aggs on top of each union child
name|List
argument_list|<
name|RelNode
argument_list|>
name|newUnionInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|union
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|boolean
name|alreadyUnique
init|=
name|RelMdUtil
operator|.
name|areColumnsDefinitelyUnique
argument_list|(
name|input
argument_list|,
name|aggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|alreadyUnique
condition|)
block|{
name|newUnionInputs
operator|.
name|add
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|anyTransformed
operator|=
literal|true
expr_stmt|;
name|newUnionInputs
operator|.
name|add
argument_list|(
operator|new
name|LogicalAggregate
argument_list|(
name|cluster
argument_list|,
name|input
argument_list|,
name|aggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggRel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|anyTransformed
condition|)
block|{
comment|// none of the children could benefit from the pushdown,
comment|// so bail out (preventing the infinite loop to which most
comment|// planners would succumb)
return|return;
block|}
comment|// create a new union whose children are the aggs created above
name|LogicalUnion
name|newUnion
init|=
operator|new
name|LogicalUnion
argument_list|(
name|cluster
argument_list|,
name|newUnionInputs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LogicalAggregate
name|newTopAggRel
init|=
operator|new
name|LogicalAggregate
argument_list|(
name|cluster
argument_list|,
name|newUnion
argument_list|,
name|aggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|transformedAggCalls
argument_list|)
decl_stmt|;
comment|// In case we transformed any COUNT (which is always NOT NULL)
comment|// to SUM (which is always NULLABLE), cast back to keep the
comment|// planner happy.
name|RelNode
name|castRel
init|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|newTopAggRel
argument_list|,
name|aggRel
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|castRel
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|transformAggCalls
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|int
name|groupCount
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|origCalls
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|origCalls
argument_list|)
control|)
block|{
specifier|final
name|AggregateCall
name|origCall
init|=
name|ord
operator|.
name|e
decl_stmt|;
if|if
condition|(
name|origCall
operator|.
name|isDistinct
argument_list|()
operator|||
operator|!
name|SUPPORTED_AGGREGATES
operator|.
name|containsKey
argument_list|(
name|origCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|SqlAggFunction
name|aggFun
decl_stmt|;
specifier|final
name|RelDataType
name|aggType
decl_stmt|;
if|if
condition|(
name|origCall
operator|.
name|getAggregation
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|COUNT
condition|)
block|{
name|aggFun
operator|=
name|SqlStdOperatorTable
operator|.
name|SUM0
expr_stmt|;
comment|// count(any) is always not null, however nullability of sum might
comment|// depend on the number of columns in GROUP BY.
comment|// Here we use SUM0 since we are sure we will not face nullable
comment|// inputs nor we'll face empty set.
name|aggType
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|aggFun
operator|=
operator|(
name|SqlAggFunction
operator|)
name|origCall
operator|.
name|getAggregation
argument_list|()
expr_stmt|;
name|aggType
operator|=
name|origCall
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|AggregateCall
name|newCall
init|=
name|AggregateCall
operator|.
name|create
argument_list|(
name|aggFun
argument_list|,
name|origCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|groupCount
operator|+
name|ord
operator|.
name|i
argument_list|)
argument_list|,
name|groupCount
argument_list|,
name|input
argument_list|,
name|aggType
argument_list|,
name|origCall
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newCalls
operator|.
name|add
argument_list|(
name|newCall
argument_list|)
expr_stmt|;
block|}
return|return
name|newCalls
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateUnionTransposeRule.java
end_comment

end_unit

