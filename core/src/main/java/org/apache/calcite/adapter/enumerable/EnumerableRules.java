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
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|ENUMERABLE_SEMI_JOIN_RULE
init|=
operator|new
name|EnumerableSemiJoinRule
argument_list|()
decl_stmt|;
specifier|private
name|EnumerableRules
parameter_list|()
block|{
block|}
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
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableValuesRule
name|ENUMERABLE_VALUES_RULE
init|=
operator|new
name|EnumerableValuesRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableOneRowRule
name|ENUMERABLE_ONE_ROW_RULE
init|=
operator|new
name|EnumerableOneRowRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableEmptyRule
name|ENUMERABLE_EMPTY_RULE
init|=
operator|new
name|EnumerableEmptyRule
argument_list|()
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
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableProjectToCalcRule
name|ENUMERABLE_PROJECT_TO_CALC_RULE
init|=
operator|new
name|EnumerableProjectToCalcRule
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumerableTableFunctionScanRule
name|ENUMERABLE_TABLE_FUNCTION_SCAN_RULE
init|=
operator|new
name|EnumerableTableFunctionScanRule
argument_list|()
decl_stmt|;
block|}
end_class

begin_comment
comment|// End EnumerableRules.java
end_comment

end_unit

