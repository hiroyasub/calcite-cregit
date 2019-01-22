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
name|tools
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
name|CalciteConnectionConfig
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
name|NoneToBindableConverterRule
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
name|RelOptCostImpl
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
name|RelOptLattice
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
name|RelOptMaterialization
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
name|plan
operator|.
name|hep
operator|.
name|HepMatchOrder
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|hep
operator|.
name|HepProgramBuilder
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
name|Calc
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
name|rel
operator|.
name|metadata
operator|.
name|ChainedRelMetadataProvider
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
name|DefaultRelMetadataProvider
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
name|RelMetadataProvider
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
name|AggregateExpandDistinctAggregatesRule
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
name|AggregateReduceFunctionsRule
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
name|AggregateStarTableRule
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
name|CalcMergeRule
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
name|FilterAggregateTransposeRule
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
name|FilterCalcMergeRule
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
name|FilterJoinRule
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
name|FilterProjectTransposeRule
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
name|FilterTableScanRule
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
name|FilterToCalcRule
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
name|JoinAssociateRule
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
name|JoinCommuteRule
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
name|JoinToMultiJoinRule
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
name|LoptOptimizeJoinRule
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
name|MultiJoinOptimizeBushyRule
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
name|ProjectCalcMergeRule
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
name|ProjectMergeRule
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
name|ProjectToCalcRule
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
name|SemiJoinRule
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
name|SortProjectTransposeRule
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
name|SubQueryRemoveRule
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
name|TableScanRule
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
name|sql2rel
operator|.
name|RelDecorrelator
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
name|sql2rel
operator|.
name|RelFieldTrimmer
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|ImmutableSet
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
name|Arrays
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
comment|/**  * Utilities for creating {@link Program}s.  */
end_comment

begin_class
specifier|public
class|class
name|Programs
block|{
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
name|NoneToBindableConverterRule
operator|.
name|INSTANCE
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
name|CalcMergeRule
operator|.
name|INSTANCE
argument_list|,
name|FilterCalcMergeRule
operator|.
name|INSTANCE
argument_list|,
name|ProjectCalcMergeRule
operator|.
name|INSTANCE
argument_list|,
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|,
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|,
name|CalcMergeRule
operator|.
name|INSTANCE
argument_list|,
comment|// REVIEW jvs 9-Apr-2006: Do we still need these two?  Doesn't the
comment|// combination of CalcMergeRule, FilterToCalcRule, and
comment|// ProjectToCalcRule have the same effect?
name|FilterCalcMergeRule
operator|.
name|INSTANCE
argument_list|,
name|ProjectCalcMergeRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|/** Program that converts filters and projects to {@link Calc}s. */
specifier|public
specifier|static
specifier|final
name|Program
name|CALC_PROGRAM
init|=
name|calc
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|/** Program that expands sub-queries. */
specifier|public
specifier|static
specifier|final
name|Program
name|SUB_QUERY_PROGRAM
init|=
name|subQuery
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|RelOptRule
argument_list|>
name|RULE_SET
init|=
name|ImmutableSet
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
name|ENUMERABLE_UNION_RULE
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
name|SemiJoinRule
operator|.
name|PROJECT
argument_list|,
name|SemiJoinRule
operator|.
name|JOIN
argument_list|,
name|TableScanRule
operator|.
name|INSTANCE
argument_list|,
name|CalciteSystemProperty
operator|.
name|COMMUTE
operator|.
name|value
argument_list|()
condition|?
name|JoinAssociateRule
operator|.
name|INSTANCE
else|:
name|ProjectMergeRule
operator|.
name|INSTANCE
argument_list|,
name|AggregateStarTableRule
operator|.
name|INSTANCE
argument_list|,
name|AggregateStarTableRule
operator|.
name|INSTANCE2
argument_list|,
name|FilterTableScanRule
operator|.
name|INSTANCE
argument_list|,
name|FilterProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|FilterJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|,
name|AggregateExpandDistinctAggregatesRule
operator|.
name|INSTANCE
argument_list|,
name|AggregateReduceFunctionsRule
operator|.
name|INSTANCE
argument_list|,
name|FilterAggregateTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|JoinCommuteRule
operator|.
name|INSTANCE
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|RIGHT
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|LEFT
argument_list|,
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|// private constructor for utility class
specifier|private
name|Programs
parameter_list|()
block|{
block|}
comment|/** Creates a program that executes a rule set. */
specifier|public
specifier|static
name|Program
name|of
parameter_list|(
name|RuleSet
name|ruleSet
parameter_list|)
block|{
return|return
operator|new
name|RuleSetProgram
argument_list|(
name|ruleSet
argument_list|)
return|;
block|}
comment|/** Creates a list of programs based on an array of rule sets. */
specifier|public
specifier|static
name|List
argument_list|<
name|Program
argument_list|>
name|listOf
parameter_list|(
name|RuleSet
modifier|...
name|ruleSets
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ruleSets
argument_list|)
argument_list|,
name|Programs
operator|::
name|of
argument_list|)
return|;
block|}
comment|/** Creates a list of programs based on a list of rule sets. */
specifier|public
specifier|static
name|List
argument_list|<
name|Program
argument_list|>
name|listOf
parameter_list|(
name|List
argument_list|<
name|RuleSet
argument_list|>
name|ruleSets
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|ruleSets
argument_list|,
name|Programs
operator|::
name|of
argument_list|)
return|;
block|}
comment|/** Creates a program from a list of rules. */
specifier|public
specifier|static
name|Program
name|ofRules
parameter_list|(
name|RelOptRule
modifier|...
name|rules
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|RuleSets
operator|.
name|ofList
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a program from a list of rules. */
specifier|public
specifier|static
name|Program
name|ofRules
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|RuleSets
operator|.
name|ofList
argument_list|(
name|rules
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a program that executes a sequence of programs. */
specifier|public
specifier|static
name|Program
name|sequence
parameter_list|(
name|Program
modifier|...
name|programs
parameter_list|)
block|{
return|return
operator|new
name|SequenceProgram
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|programs
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a program that executes a list of rules in a HEP planner. */
specifier|public
specifier|static
name|Program
name|hep
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RelOptRule
argument_list|>
name|rules
parameter_list|,
name|boolean
name|noDag
parameter_list|,
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
specifier|final
name|HepProgramBuilder
name|builder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|rules
control|)
block|{
name|builder
operator|.
name|addRuleInstance
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
return|return
name|of
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|noDag
argument_list|,
name|metadataProvider
argument_list|)
return|;
block|}
comment|/** Creates a program that executes a {@link HepProgram}. */
specifier|public
specifier|static
name|Program
name|of
parameter_list|(
specifier|final
name|HepProgram
name|hepProgram
parameter_list|,
specifier|final
name|boolean
name|noDag
parameter_list|,
specifier|final
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
return|return
parameter_list|(
name|planner
parameter_list|,
name|rel
parameter_list|,
name|requiredOutputTraits
parameter_list|,
name|materializations
parameter_list|,
name|lattices
parameter_list|)
lambda|->
block|{
specifier|final
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|hepProgram
argument_list|,
literal|null
argument_list|,
name|noDag
argument_list|,
literal|null
argument_list|,
name|RelOptCostImpl
operator|.
name|FACTORY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelMetadataProvider
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|metadataProvider
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|metadataProvider
argument_list|)
expr_stmt|;
block|}
name|hepPlanner
operator|.
name|registerMetadataProviders
argument_list|(
name|list
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptMaterialization
name|materialization
range|:
name|materializations
control|)
block|{
name|hepPlanner
operator|.
name|addMaterialization
argument_list|(
name|materialization
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelOptLattice
name|lattice
range|:
name|lattices
control|)
block|{
name|hepPlanner
operator|.
name|addLattice
argument_list|(
name|lattice
argument_list|)
expr_stmt|;
block|}
name|RelMetadataProvider
name|plannerChain
init|=
name|ChainedRelMetadataProvider
operator|.
name|of
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|setMetadataProvider
argument_list|(
name|plannerChain
argument_list|)
expr_stmt|;
name|hepPlanner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
return|return
name|hepPlanner
operator|.
name|findBestExp
argument_list|()
return|;
block|}
return|;
block|}
comment|/** Creates a program that invokes heuristic join-order optimization    * (via {@link org.apache.calcite.rel.rules.JoinToMultiJoinRule},    * {@link org.apache.calcite.rel.rules.MultiJoin} and    * {@link org.apache.calcite.rel.rules.LoptOptimizeJoinRule})    * if there are 6 or more joins (7 or more relations). */
specifier|public
specifier|static
name|Program
name|heuristicJoinOrder
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|RelOptRule
argument_list|>
name|rules
parameter_list|,
specifier|final
name|boolean
name|bushy
parameter_list|,
specifier|final
name|int
name|minJoinCount
parameter_list|)
block|{
return|return
parameter_list|(
name|planner
parameter_list|,
name|rel
parameter_list|,
name|requiredOutputTraits
parameter_list|,
name|materializations
parameter_list|,
name|lattices
parameter_list|)
lambda|->
block|{
specifier|final
name|int
name|joinCount
init|=
name|RelOptUtil
operator|.
name|countJoins
argument_list|(
name|rel
argument_list|)
decl_stmt|;
specifier|final
name|Program
name|program
decl_stmt|;
if|if
condition|(
name|joinCount
operator|<
name|minJoinCount
condition|)
block|{
name|program
operator|=
name|ofRules
argument_list|(
name|rules
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Create a program that gathers together joins as a MultiJoin.
specifier|final
name|HepProgram
name|hep
init|=
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleInstance
argument_list|(
name|FilterJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|)
operator|.
name|addMatchOrder
argument_list|(
name|HepMatchOrder
operator|.
name|BOTTOM_UP
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|JoinToMultiJoinRule
operator|.
name|INSTANCE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Program
name|program1
init|=
name|of
argument_list|(
name|hep
argument_list|,
literal|false
argument_list|,
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|// Create a program that contains a rule to expand a MultiJoin
comment|// into heuristically ordered joins.
comment|// We use the rule set passed in, but remove JoinCommuteRule and
comment|// JoinPushThroughJoinRule, because they cause exhaustive search.
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|rules
argument_list|)
decl_stmt|;
name|list
operator|.
name|removeAll
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|JoinCommuteRule
operator|.
name|INSTANCE
argument_list|,
name|JoinAssociateRule
operator|.
name|INSTANCE
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|LEFT
argument_list|,
name|JoinPushThroughJoinRule
operator|.
name|RIGHT
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|bushy
condition|?
name|MultiJoinOptimizeBushyRule
operator|.
name|INSTANCE
else|:
name|LoptOptimizeJoinRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
specifier|final
name|Program
name|program2
init|=
name|ofRules
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|program
operator|=
name|sequence
argument_list|(
name|program1
argument_list|,
name|program2
argument_list|)
expr_stmt|;
block|}
return|return
name|program
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|,
name|materializations
argument_list|,
name|lattices
argument_list|)
return|;
block|}
return|;
block|}
specifier|public
specifier|static
name|Program
name|calc
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
return|return
name|hep
argument_list|(
name|CALC_RULES
argument_list|,
literal|true
argument_list|,
name|metadataProvider
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|Program
name|subquery
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
return|return
name|subQuery
argument_list|(
name|metadataProvider
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Program
name|subQuery
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
specifier|final
name|HepProgramBuilder
name|builder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addRuleCollection
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|RelOptRule
operator|)
name|SubQueryRemoveRule
operator|.
name|FILTER
argument_list|,
name|SubQueryRemoveRule
operator|.
name|PROJECT
argument_list|,
name|SubQueryRemoveRule
operator|.
name|JOIN
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|of
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|,
name|metadataProvider
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Program
name|getProgram
parameter_list|()
block|{
return|return
parameter_list|(
name|planner
parameter_list|,
name|rel
parameter_list|,
name|requiredOutputTraits
parameter_list|,
name|materializations
parameter_list|,
name|lattices
parameter_list|)
lambda|->
literal|null
return|;
block|}
comment|/** Returns the standard program used by Prepare. */
specifier|public
specifier|static
name|Program
name|standard
parameter_list|()
block|{
return|return
name|standard
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/** Returns the standard program with user metadata provider. */
specifier|public
specifier|static
name|Program
name|standard
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
specifier|final
name|Program
name|program1
init|=
parameter_list|(
name|planner
parameter_list|,
name|rel
parameter_list|,
name|requiredOutputTraits
parameter_list|,
name|materializations
parameter_list|,
name|lattices
parameter_list|)
lambda|->
block|{
name|planner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptMaterialization
name|materialization
range|:
name|materializations
control|)
block|{
name|planner
operator|.
name|addMaterialization
argument_list|(
name|materialization
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelOptLattice
name|lattice
range|:
name|lattices
control|)
block|{
name|planner
operator|.
name|addLattice
argument_list|(
name|lattice
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|rootRel2
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|requiredOutputTraits
argument_list|)
condition|?
name|rel
else|:
name|planner
operator|.
name|changeTraits
argument_list|(
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|)
decl_stmt|;
assert|assert
name|rootRel2
operator|!=
literal|null
assert|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rootRel2
argument_list|)
expr_stmt|;
specifier|final
name|RelOptPlanner
name|planner2
init|=
name|planner
operator|.
name|chooseDelegate
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rootRel3
init|=
name|planner2
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
assert|assert
name|rootRel3
operator|!=
literal|null
operator|:
literal|"could not implement exp"
assert|;
return|return
name|rootRel3
return|;
block|}
decl_stmt|;
return|return
name|sequence
argument_list|(
name|subQuery
argument_list|(
name|metadataProvider
argument_list|)
argument_list|,
operator|new
name|DecorrelateProgram
argument_list|()
argument_list|,
operator|new
name|TrimFieldsProgram
argument_list|()
argument_list|,
name|program1
argument_list|,
comment|// Second planner pass to do physical "tweaks". This the first time
comment|// that EnumerableCalcRel is introduced.
name|calc
argument_list|(
name|metadataProvider
argument_list|)
argument_list|)
return|;
block|}
comment|/** Program backed by a {@link RuleSet}. */
specifier|static
class|class
name|RuleSetProgram
implements|implements
name|Program
block|{
specifier|final
name|RuleSet
name|ruleSet
decl_stmt|;
specifier|private
name|RuleSetProgram
parameter_list|(
name|RuleSet
name|ruleSet
parameter_list|)
block|{
name|this
operator|.
name|ruleSet
operator|=
name|ruleSet
expr_stmt|;
block|}
specifier|public
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
parameter_list|,
name|List
argument_list|<
name|RelOptLattice
argument_list|>
name|lattices
parameter_list|)
block|{
name|planner
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|ruleSet
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelOptMaterialization
name|materialization
range|:
name|materializations
control|)
block|{
name|planner
operator|.
name|addMaterialization
argument_list|(
name|materialization
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelOptLattice
name|lattice
range|:
name|lattices
control|)
block|{
name|planner
operator|.
name|addLattice
argument_list|(
name|lattice
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|requiredOutputTraits
argument_list|)
condition|)
block|{
name|rel
operator|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|)
expr_stmt|;
block|}
name|planner
operator|.
name|setRoot
argument_list|(
name|rel
argument_list|)
expr_stmt|;
return|return
name|planner
operator|.
name|findBestExp
argument_list|()
return|;
block|}
block|}
comment|/** Program that runs sub-programs, sending the output of the previous as    * input to the next. */
specifier|private
specifier|static
class|class
name|SequenceProgram
implements|implements
name|Program
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|programs
decl_stmt|;
name|SequenceProgram
parameter_list|(
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|programs
parameter_list|)
block|{
name|this
operator|.
name|programs
operator|=
name|programs
expr_stmt|;
block|}
specifier|public
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
parameter_list|,
name|List
argument_list|<
name|RelOptLattice
argument_list|>
name|lattices
parameter_list|)
block|{
for|for
control|(
name|Program
name|program
range|:
name|programs
control|)
block|{
name|rel
operator|=
name|program
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|,
name|materializations
argument_list|,
name|lattices
argument_list|)
expr_stmt|;
block|}
return|return
name|rel
return|;
block|}
block|}
comment|/** Program that de-correlates a query.    *    *<p>To work around    *<a href="https://issues.apache.org/jira/browse/CALCITE-842">[CALCITE-842]    * Decorrelator gets field offsets confused if fields have been trimmed</a>,    * disable field-trimming in {@link SqlToRelConverter}, and run    * {@link TrimFieldsProgram} after this program. */
specifier|private
specifier|static
class|class
name|DecorrelateProgram
implements|implements
name|Program
block|{
specifier|public
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
parameter_list|,
name|List
argument_list|<
name|RelOptLattice
argument_list|>
name|lattices
parameter_list|)
block|{
specifier|final
name|CalciteConnectionConfig
name|config
init|=
name|planner
operator|.
name|getContext
argument_list|()
operator|.
name|unwrap
argument_list|(
name|CalciteConnectionConfig
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|forceDecorrelate
argument_list|()
condition|)
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|rel
argument_list|,
name|relBuilder
argument_list|)
return|;
block|}
return|return
name|rel
return|;
block|}
block|}
comment|/** Program that trims fields. */
specifier|private
specifier|static
class|class
name|TrimFieldsProgram
implements|implements
name|Program
block|{
specifier|public
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
parameter_list|,
name|List
argument_list|<
name|RelOptLattice
argument_list|>
name|lattices
parameter_list|)
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelFieldTrimmer
argument_list|(
literal|null
argument_list|,
name|relBuilder
argument_list|)
operator|.
name|trim
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Programs.java
end_comment

end_unit

