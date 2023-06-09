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
name|plan
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRules
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
name|config
operator|.
name|CalciteSystemProperty
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
name|interpreter
operator|.
name|Bindables
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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
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
name|AbstractConverter
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
name|rules
operator|.
name|CoreRules
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
name|rules
operator|.
name|DateRangeRules
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
name|rules
operator|.
name|JoinPushThroughJoinRule
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
name|rules
operator|.
name|PruneEmptyRules
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
name|rules
operator|.
name|materialize
operator|.
name|MaterializedViewRules
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A utility class for organizing built-in rules and rule related  * methods. Currently some rule sets are package private for serving core Calcite.  *  * @see RelOptRule  * @see RelOptUtil  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
class|class
name|RelOptRules
block|{
specifier|private
name|RelOptRules
parameter_list|()
block|{
block|}
comment|/** Calc rule set; public so that {@link org.apache.calcite.tools.Programs} can    * use it. */
specifier|public
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRule
argument_list|>
name|CALC_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Bindables
operator|.
name|FROM_NONE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_CALC_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_TO_CALC_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_TO_CALC_RULE
argument_list|,
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|,
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|,
name|CoreRules
operator|.
name|CALC_MERGE
argument_list|,
comment|// REVIEW jvs 9-Apr-2006: Do we still need these two?  Doesn't the
comment|// combination of CalcMergeRule, FilterToCalcRule, and
comment|// ProjectToCalcRule have the same effect?
name|CoreRules
operator|.
name|FILTER_CALC_MERGE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_CALC_MERGE
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|BASE_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|AGGREGATE_STAR_TABLE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_PROJECT_STAR_TABLE
argument_list|,
name|CalciteSystemProperty
operator|.
name|COMMUTE
operator|.
name|value
argument_list|()
condition|?
name|CoreRules
operator|.
name|JOIN_ASSOCIATE
else|:
name|CoreRules
operator|.
name|PROJECT_MERGE
argument_list|,
name|CoreRules
operator|.
name|FILTER_SCAN
argument_list|,
name|CoreRules
operator|.
name|PROJECT_FILTER_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|FILTER_PROJECT_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|FILTER_INTO_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_PUSH_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_EXPAND_DISTINCT_AGGREGATES
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_EXPAND_WITHIN_DISTINCT
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_CASE_TO_FILTER
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_REDUCE_FUNCTIONS
argument_list|,
name|CoreRules
operator|.
name|FILTER_AGGREGATE_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_WINDOW_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|MATCH
argument_list|,
name|CoreRules
operator|.
name|JOIN_COMMUTE
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|RIGHT
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|LEFT
argument_list|,
name|CoreRules
operator|.
name|SORT_PROJECT_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|SORT_JOIN_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|SORT_REMOVE_CONSTANT_KEYS
argument_list|,
name|CoreRules
operator|.
name|SORT_UNION_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|EXCHANGE_REMOVE_CONSTANT_KEYS
argument_list|,
name|CoreRules
operator|.
name|SORT_EXCHANGE_REMOVE_CONSTANT_KEYS
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|ABSTRACT_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|AGGREGATE_ANY_PULL_UP_CONSTANTS
argument_list|,
name|CoreRules
operator|.
name|UNION_PULL_UP_CONSTANTS
argument_list|,
name|PruneEmptyRules
operator|.
name|UNION_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|INTERSECT_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|MINUS_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|PROJECT_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|FILTER_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|SORT_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|AGGREGATE_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|JOIN_LEFT_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|JOIN_RIGHT_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|SORT_FETCH_ZERO_INSTANCE
argument_list|,
name|PruneEmptyRules
operator|.
name|EMPTY_TABLE_INSTANCE
argument_list|,
name|CoreRules
operator|.
name|UNION_MERGE
argument_list|,
name|CoreRules
operator|.
name|INTERSECT_MERGE
argument_list|,
name|CoreRules
operator|.
name|MINUS_MERGE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_TO_LOGICAL_PROJECT_AND_WINDOW
argument_list|,
name|CoreRules
operator|.
name|FILTER_MERGE
argument_list|,
name|DateRangeRules
operator|.
name|FILTER_INSTANCE
argument_list|,
name|CoreRules
operator|.
name|INTERSECT_TO_DISTINCT
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|ABSTRACT_RELATIONAL_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|FILTER_INTO_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_CONDITION_PUSH
argument_list|,
name|AbstractConverter
operator|.
name|ExpandConversionRule
operator|.
name|INSTANCE
argument_list|,
name|CoreRules
operator|.
name|JOIN_COMMUTE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_TO_SEMI_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_ON_UNIQUE_TO_SEMI_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_TO_SEMI_JOIN
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_REMOVE
argument_list|,
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|,
name|CoreRules
operator|.
name|PROJECT_REMOVE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_AGGREGATE_MERGE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_JOIN_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_MERGE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_PROJECT_MERGE
argument_list|,
name|CoreRules
operator|.
name|CALC_REMOVE
argument_list|,
name|CoreRules
operator|.
name|SORT_REMOVE
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|CONSTANT_REDUCTION_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|PROJECT_REDUCE_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|FILTER_REDUCE_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|CALC_REDUCE_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|WINDOW_REDUCE_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|JOIN_REDUCE_EXPRESSIONS
argument_list|,
name|CoreRules
operator|.
name|FILTER_VALUES_MERGE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_FILTER_VALUES_MERGE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_VALUES_MERGE
argument_list|,
name|CoreRules
operator|.
name|AGGREGATE_VALUES
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|MATERIALIZATION_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|MaterializedViewRules
operator|.
name|FILTER_SCAN
argument_list|,
name|MaterializedViewRules
operator|.
name|PROJECT_FILTER
argument_list|,
name|MaterializedViewRules
operator|.
name|FILTER
argument_list|,
name|MaterializedViewRules
operator|.
name|PROJECT_JOIN
argument_list|,
name|MaterializedViewRules
operator|.
name|JOIN
argument_list|,
name|MaterializedViewRules
operator|.
name|PROJECT_AGGREGATE
argument_list|,
name|MaterializedViewRules
operator|.
name|AGGREGATE
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

