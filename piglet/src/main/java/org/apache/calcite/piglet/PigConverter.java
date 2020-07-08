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
name|piglet
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
name|EnumerableConvention
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
name|rel
operator|.
name|RelCollation
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
name|RelCollations
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
name|logical
operator|.
name|ToLogicalConverter
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
name|rel2sql
operator|.
name|RelToSqlConverter
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
name|sql
operator|.
name|SqlDialect
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
name|SqlNode
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
name|SqlWriter
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
name|SqlWriterConfig
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|tools
operator|.
name|FrameworkConfig
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
name|tools
operator|.
name|Program
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
name|tools
operator|.
name|Programs
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
name|tools
operator|.
name|RuleSets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|ExecType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|PigServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|impl
operator|.
name|logicalLayer
operator|.
name|FrontendException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|newplan
operator|.
name|logical
operator|.
name|relational
operator|.
name|LogicalPlan
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
comment|/**  * Extension from PigServer to convert Pig scripts into logical relational  * algebra plans and SQL statements.  */
end_comment

begin_class
specifier|public
class|class
name|PigConverter
extends|extends
name|PigServer
block|{
comment|// Basic transformation and implementation rules to optimize for Pig-translated logical plans
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|PIG_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_LOGICAL_PROJECT_AND_WINDOW
argument_list|,
name|PigToSqlAggregateRule
operator|.
name|INSTANCE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
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
name|ENUMERABLE_WINDOW_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|TO_INTERPRETER
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|TRANSFORM_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CoreRules
operator|.
name|PROJECT_WINDOW_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|FILTER_MERGE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_MERGE
argument_list|,
name|CoreRules
operator|.
name|FILTER_PROJECT_TRANSPOSE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
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
name|ENUMERABLE_WINDOW_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|TO_INTERPRETER
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|PigRelBuilder
name|builder
decl_stmt|;
specifier|private
name|PigConverter
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|,
name|ExecType
name|execType
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|execType
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|PigConverter
name|create
parameter_list|(
name|FrameworkConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|PigConverter
argument_list|(
name|config
argument_list|,
name|ExecType
operator|.
name|LOCAL
argument_list|)
return|;
block|}
specifier|public
name|PigRelBuilder
name|getBuilder
parameter_list|()
block|{
return|return
name|builder
return|;
block|}
comment|/**    * Parses a Pig script and converts it into relational algebra plans,    * optimizing the result.    *    *<p>Equivalent to {@code pigQuery2Rel(pigQuery, true, true, true)}.    *    * @param pigQuery Pig script    *    * @return A list of root nodes of the translated relational plans. Each of    * these root corresponds to a sink operator (normally a STORE command) in the    * Pig plan    *    * @throws IOException Exception during parsing or translating Pig    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|pigQuery2Rel
parameter_list|(
name|String
name|pigQuery
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pigQuery2Rel
argument_list|(
name|pigQuery
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Parses a Pig script and converts it into relational algebra plans.    *    * @param pigQuery Pig script    * @param planRewrite Whether to rewrite the translated plan    * @param validate Whether to validate the Pig logical plan before doing    *   translation    * @param usePigRules Whether to use Pig Rules (see PigRelPlanner} to rewrite    *   translated rel plan    *    * @return A list of root nodes of the translated relational plans. Each of    * these root corresponds to a sink operator (normally a STORE command) in the    * Pig plan    *    * @throws IOException Exception during parsing or translating Pig    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|pigQuery2Rel
parameter_list|(
name|String
name|pigQuery
parameter_list|,
name|boolean
name|planRewrite
parameter_list|,
name|boolean
name|validate
parameter_list|,
name|boolean
name|usePigRules
parameter_list|)
throws|throws
name|IOException
block|{
name|setBatchOn
argument_list|()
expr_stmt|;
name|registerQuery
argument_list|(
name|pigQuery
argument_list|)
expr_stmt|;
specifier|final
name|LogicalPlan
name|pigPlan
init|=
name|getCurrentDAG
argument_list|()
operator|.
name|getLogicalPlan
argument_list|()
decl_stmt|;
if|if
condition|(
name|validate
condition|)
block|{
name|pigPlan
operator|.
name|validate
argument_list|(
name|getPigContext
argument_list|()
argument_list|,
name|scope
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|pigPlan2Rel
argument_list|(
name|pigPlan
argument_list|,
name|planRewrite
argument_list|,
name|usePigRules
argument_list|)
return|;
block|}
comment|/**    * Gets a Pig script string from a file after doing param substitution.    *    * @param in Pig script file    * @param params Param sub map    */
specifier|public
name|String
name|getPigScript
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getPigContext
argument_list|()
operator|.
name|doParamSubstitution
argument_list|(
name|in
argument_list|,
name|paramMapToList
argument_list|(
name|params
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Parses a Pig script and converts it into relational algebra plans.    *    * @param fileName File name    * @param params Param substitution map    * @param planRewrite Whether to rewrite the translated plan    *    * @return A list of root nodes of the translated relational plans. Each of    * these root corresponds to a sink operator (normally a STORE command) in the    * Pig plan    *    * @throws IOException Exception during parsing or translating Pig    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|pigScript2Rel
parameter_list|(
name|String
name|fileName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|boolean
name|planRewrite
parameter_list|)
throws|throws
name|IOException
block|{
name|setBatchOn
argument_list|()
expr_stmt|;
name|registerScript
argument_list|(
name|fileName
argument_list|,
name|params
argument_list|)
expr_stmt|;
specifier|final
name|LogicalPlan
name|pigPlan
init|=
name|getCurrentDAG
argument_list|()
operator|.
name|getLogicalPlan
argument_list|()
decl_stmt|;
name|pigPlan
operator|.
name|validate
argument_list|(
name|getPigContext
argument_list|()
argument_list|,
name|scope
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|pigPlan2Rel
argument_list|(
name|pigPlan
argument_list|,
name|planRewrite
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|pigPlan2Rel
parameter_list|(
name|LogicalPlan
name|pigPlan
parameter_list|,
name|boolean
name|planRewrite
parameter_list|,
name|boolean
name|usePigRules
parameter_list|)
throws|throws
name|FrontendException
block|{
specifier|final
name|PigRelOpWalker
name|walker
init|=
operator|new
name|PigRelOpWalker
argument_list|(
name|pigPlan
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|relNodes
init|=
operator|new
name|PigRelOpVisitor
argument_list|(
name|pigPlan
argument_list|,
name|walker
argument_list|,
name|builder
argument_list|)
operator|.
name|translate
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|storeRels
init|=
name|builder
operator|.
name|getRelsForStores
argument_list|()
decl_stmt|;
name|relNodes
operator|=
name|storeRels
operator|!=
literal|null
condition|?
name|storeRels
else|:
name|relNodes
expr_stmt|;
if|if
condition|(
name|usePigRules
condition|)
block|{
name|relNodes
operator|=
name|optimizePlans
argument_list|(
name|relNodes
argument_list|,
name|PIG_RULES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|planRewrite
condition|)
block|{
name|relNodes
operator|=
name|optimizePlans
argument_list|(
name|relNodes
argument_list|,
name|TRANSFORM_RULES
argument_list|)
expr_stmt|;
block|}
return|return
name|relNodes
return|;
block|}
comment|/**    * Converts a Pig script to a list of SQL statements.    *    * @param pigQuery Pig script    * @param sqlDialect Dialect of SQL language    * @throws IOException Exception during parsing or translating Pig    */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|pigToSql
parameter_list|(
name|String
name|pigQuery
parameter_list|,
name|SqlDialect
name|sqlDialect
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SqlWriterConfig
name|config
init|=
name|SqlPrettyWriter
operator|.
name|config
argument_list|()
operator|.
name|withQuoteAllIdentifiers
argument_list|(
literal|false
argument_list|)
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|false
argument_list|)
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|false
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|2
argument_list|)
operator|.
name|withDialect
argument_list|(
name|sqlDialect
argument_list|)
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|config
argument_list|)
decl_stmt|;
return|return
name|pigToSql
argument_list|(
name|pigQuery
argument_list|,
name|writer
argument_list|)
return|;
block|}
comment|/**    * Converts a Pig script to a list of SQL statements.    *    * @param pigQuery Pig script    * @param writer The SQL writer to decide dialect and format of SQL statements    * @throws IOException Exception during parsing or translating Pig    */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|pigToSql
parameter_list|(
name|String
name|pigQuery
parameter_list|,
name|SqlWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RelToSqlConverter
name|sqlConverter
init|=
operator|new
name|PigRelToSqlConverter
argument_list|(
name|writer
operator|.
name|getDialect
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|finalRels
init|=
name|pigQuery2Rel
argument_list|(
name|pigQuery
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|sqlStatements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|finalRels
control|)
block|{
specifier|final
name|SqlNode
name|sqlNode
init|=
name|sqlConverter
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|rel
argument_list|)
operator|.
name|asStatement
argument_list|()
decl_stmt|;
name|sqlNode
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|sqlStatements
operator|.
name|add
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sqlStatements
return|;
block|}
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|optimizePlans
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|originalRels
parameter_list|,
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rules
parameter_list|)
block|{
specifier|final
name|RelOptPlanner
name|planner
init|=
name|originalRels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
comment|// Remember old rule set of the planner before resetting it with new rules
specifier|final
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|oldRules
init|=
name|planner
operator|.
name|getRules
argument_list|()
decl_stmt|;
name|resetPlannerRules
argument_list|(
name|planner
argument_list|,
name|rules
argument_list|)
expr_stmt|;
specifier|final
name|Program
name|program
init|=
name|Programs
operator|.
name|of
argument_list|(
name|RuleSets
operator|.
name|ofList
argument_list|(
name|planner
operator|.
name|getRules
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|optimizedPlans
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|originalRels
control|)
block|{
specifier|final
name|RelCollation
name|collation
init|=
name|rel
operator|instanceof
name|Sort
condition|?
operator|(
operator|(
name|Sort
operator|)
name|rel
operator|)
operator|.
name|collation
else|:
name|RelCollations
operator|.
name|EMPTY
decl_stmt|;
comment|// Apply the planner to obtain the physical plan
specifier|final
name|RelNode
name|physicalPlan
init|=
name|program
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
operator|.
name|replace
argument_list|(
name|collation
argument_list|)
operator|.
name|simplify
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|// Then convert the physical plan back to logical plan
specifier|final
name|RelNode
name|logicalPlan
init|=
operator|new
name|ToLogicalConverter
argument_list|(
name|builder
argument_list|)
operator|.
name|visit
argument_list|(
name|physicalPlan
argument_list|)
decl_stmt|;
name|optimizedPlans
operator|.
name|add
argument_list|(
name|logicalPlan
argument_list|)
expr_stmt|;
block|}
name|resetPlannerRules
argument_list|(
name|planner
argument_list|,
name|oldRules
argument_list|)
expr_stmt|;
return|return
name|optimizedPlans
return|;
block|}
specifier|private
name|void
name|resetPlannerRules
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|List
argument_list|<
name|RelOptRule
argument_list|>
name|rulesToSet
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
name|rulesToSet
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
block|}
block|}
end_class

end_unit

