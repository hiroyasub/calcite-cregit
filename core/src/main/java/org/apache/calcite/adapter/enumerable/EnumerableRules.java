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
name|adapter
operator|.
name|enumerable
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
name|rel
operator|.
name|core
operator|.
name|RelFactories
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
name|trace
operator|.
name|CalciteTrace
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
name|slf4j
operator|.
name|Logger
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
comment|/**  * Rules and relational operators for the  * {@link EnumerableConvention enumerable calling convention}.  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableRules
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|BRIDGE_METHODS
init|=
literal|true
decl_stmt|;
specifier|private
name|EnumerableRules
parameter_list|()
block|{
block|}
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|ENUMERABLE_JOIN_RULE
init|=
operator|new
name|EnumerableJoinRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|ENUMERABLE_MERGE_JOIN_RULE
init|=
operator|new
name|EnumerableMergeJoinRule
argument_list|()
decl_stmt|;
comment|/** @deprecated To be removed along with {@link SemiJoin};    * use {@link #ENUMERABLE_JOIN_RULE} */
annotation|@
name|Deprecated
comment|// to be removed before 1.21
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|ENUMERABLE_SEMI_JOIN_RULE
init|=
operator|new
name|EnumerableSemiJoinRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|ENUMERABLE_CORRELATE_RULE
init|=
operator|new
name|EnumerableCorrelateRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableProjectRule
name|ENUMERABLE_PROJECT_RULE
init|=
operator|new
name|EnumerableProjectRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableFilterRule
name|ENUMERABLE_FILTER_RULE
init|=
operator|new
name|EnumerableFilterRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableCalcRule
name|ENUMERABLE_CALC_RULE
init|=
operator|new
name|EnumerableCalcRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableAggregateRule
name|ENUMERABLE_AGGREGATE_RULE
init|=
operator|new
name|EnumerableAggregateRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableSortRule
name|ENUMERABLE_SORT_RULE
init|=
operator|new
name|EnumerableSortRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableLimitRule
name|ENUMERABLE_LIMIT_RULE
init|=
operator|new
name|EnumerableLimitRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableUnionRule
name|ENUMERABLE_UNION_RULE
init|=
operator|new
name|EnumerableUnionRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableRepeatUnionRule
name|ENUMERABLE_REPEAT_UNION_RULE
init|=
operator|new
name|EnumerableRepeatUnionRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableTableSpoolRule
name|ENUMERABLE_TABLE_SPOOL_RULE
init|=
operator|new
name|EnumerableTableSpoolRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableIntersectRule
name|ENUMERABLE_INTERSECT_RULE
init|=
operator|new
name|EnumerableIntersectRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableMinusRule
name|ENUMERABLE_MINUS_RULE
init|=
operator|new
name|EnumerableMinusRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableTableModifyRule
name|ENUMERABLE_TABLE_MODIFICATION_RULE
init|=
operator|new
name|EnumerableTableModifyRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableValuesRule
name|ENUMERABLE_VALUES_RULE
init|=
operator|new
name|EnumerableValuesRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableWindowRule
name|ENUMERABLE_WINDOW_RULE
init|=
operator|new
name|EnumerableWindowRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableCollectRule
name|ENUMERABLE_COLLECT_RULE
init|=
operator|new
name|EnumerableCollectRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableUncollectRule
name|ENUMERABLE_UNCOLLECT_RULE
init|=
operator|new
name|EnumerableUncollectRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableFilterToCalcRule
name|ENUMERABLE_FILTER_TO_CALC_RULE
init|=
operator|new
name|EnumerableFilterToCalcRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableProjectToCalcRule
name|ENUMERABLE_PROJECT_TO_CALC_RULE
init|=
operator|new
name|EnumerableProjectToCalcRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableTableScanRule
name|ENUMERABLE_TABLE_SCAN_RULE
init|=
operator|new
name|EnumerableTableScanRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableTableFunctionScanRule
name|ENUMERABLE_TABLE_FUNCTION_SCAN_RULE
init|=
operator|new
name|EnumerableTableFunctionScanRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|ENUMERABLE_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_MERGE_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SEMI_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_CORRELATE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_LIMIT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_COLLECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_UNCOLLECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_UNION_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_REPEAT_UNION_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SPOOL_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_INTERSECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_MINUS_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_MODIFICATION_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_WINDOW_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_FUNCTION_SCAN_RULE
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|()
block|{
return|return
name|ENUMERABLE_RULES
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableRules.java
end_comment

end_unit

