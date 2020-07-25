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
name|test
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
name|plan
operator|.
name|Context
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
name|plan
operator|.
name|volcano
operator|.
name|VolcanoPlanner
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
name|RelRoot
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
name|runtime
operator|.
name|FlatLists
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
name|runtime
operator|.
name|Hook
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
name|tools
operator|.
name|RelBuilder
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
name|Closer
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
name|ImmutableMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|UnaryOperator
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * RelOptTestBase is an abstract base for tests which exercise a planner and/or  * rules via {@link DiffRepository}.  */
end_comment

begin_class
specifier|abstract
class|class
name|RelOptTestBase
extends|extends
name|SqlToRelTestBase
block|{
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|protected
name|Tester
name|createTester
parameter_list|()
block|{
return|return
name|super
operator|.
name|createTester
argument_list|()
operator|.
name|withDecorrelation
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|Tester
name|createDynamicTester
parameter_list|()
block|{
return|return
name|getTesterWithDynamicTable
argument_list|()
return|;
block|}
comment|/**    * Checks the plan for a SQL statement before/after executing a given rule,    * with a pre-program to prepare the tree.    *    * @param tester     Tester    * @param preProgram Program to execute before comparing before state    * @param planner    Planner    * @param sql        SQL query    * @param unchanged  Whether the rule is to have no effect    */
specifier|private
name|void
name|checkPlanning
parameter_list|(
name|Tester
name|tester
parameter_list|,
name|HepProgram
name|preProgram
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|,
name|String
name|sql
parameter_list|,
name|boolean
name|unchanged
parameter_list|)
block|{
specifier|final
name|DiffRepository
name|diffRepos
init|=
name|getDiffRepos
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
name|diffRepos
operator|.
name|expand
argument_list|(
literal|"sql"
argument_list|,
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|RelRoot
name|root
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sql2
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|relInitial
init|=
name|root
operator|.
name|rel
decl_stmt|;
name|assertNotNull
argument_list|(
name|relInitial
argument_list|)
expr_stmt|;
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
name|list
operator|.
name|add
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|registerMetadataProviders
argument_list|(
name|list
argument_list|)
expr_stmt|;
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
specifier|final
name|RelOptCluster
name|cluster
init|=
name|relInitial
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|setMetadataProvider
argument_list|(
name|plannerChain
argument_list|)
expr_stmt|;
name|RelNode
name|relBefore
decl_stmt|;
if|if
condition|(
name|preProgram
operator|==
literal|null
condition|)
block|{
name|relBefore
operator|=
name|relInitial
expr_stmt|;
block|}
else|else
block|{
name|HepPlanner
name|prePlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|preProgram
argument_list|)
decl_stmt|;
name|prePlanner
operator|.
name|setRoot
argument_list|(
name|relInitial
argument_list|)
expr_stmt|;
name|relBefore
operator|=
name|prePlanner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|relBefore
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|planBefore
init|=
name|NL
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|relBefore
argument_list|)
decl_stmt|;
name|diffRepos
operator|.
name|assertEquals
argument_list|(
literal|"planBefore"
argument_list|,
literal|"${planBefore}"
argument_list|,
name|planBefore
argument_list|)
expr_stmt|;
name|SqlToRelTestBase
operator|.
name|assertValid
argument_list|(
name|relBefore
argument_list|)
expr_stmt|;
if|if
condition|(
name|planner
operator|instanceof
name|VolcanoPlanner
condition|)
block|{
name|relBefore
operator|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|relBefore
argument_list|,
name|relBefore
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
argument_list|)
expr_stmt|;
block|}
name|planner
operator|.
name|setRoot
argument_list|(
name|relBefore
argument_list|)
expr_stmt|;
name|RelNode
name|r
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
if|if
condition|(
name|tester
operator|.
name|isLateDecorrelate
argument_list|()
condition|)
block|{
specifier|final
name|String
name|planMid
init|=
name|NL
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|diffRepos
operator|.
name|assertEquals
argument_list|(
literal|"planMid"
argument_list|,
literal|"${planMid}"
argument_list|,
name|planMid
argument_list|)
expr_stmt|;
name|SqlToRelTestBase
operator|.
name|assertValid
argument_list|(
name|r
argument_list|)
expr_stmt|;
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
name|cluster
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|r
operator|=
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|r
argument_list|,
name|relBuilder
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|planAfter
init|=
name|NL
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|unchanged
condition|)
block|{
name|assertThat
argument_list|(
name|planAfter
argument_list|,
name|is
argument_list|(
name|planBefore
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diffRepos
operator|.
name|assertEquals
argument_list|(
literal|"planAfter"
argument_list|,
literal|"${planAfter}"
argument_list|,
name|planAfter
argument_list|)
expr_stmt|;
if|if
condition|(
name|planBefore
operator|.
name|equals
argument_list|(
name|planAfter
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Expected plan before and after is the same.\n"
operator|+
literal|"You must use unchanged=true or call checkUnchanged"
argument_list|)
throw|;
block|}
block|}
name|SqlToRelTestBase
operator|.
name|assertValid
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the SQL statement for a test. */
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Allows fluent testing. */
class|class
name|Sql
block|{
specifier|private
specifier|final
name|Tester
name|tester
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
name|HepProgram
name|preProgram
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Hook
argument_list|,
name|Consumer
argument_list|>
name|hooks
decl_stmt|;
specifier|private
name|ImmutableList
argument_list|<
name|Function
argument_list|<
name|Tester
argument_list|,
name|Tester
argument_list|>
argument_list|>
name|transforms
decl_stmt|;
name|Sql
parameter_list|(
name|Tester
name|tester
parameter_list|,
name|String
name|sql
parameter_list|,
name|HepProgram
name|preProgram
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|,
name|ImmutableMap
argument_list|<
name|Hook
argument_list|,
name|Consumer
argument_list|>
name|hooks
parameter_list|,
name|ImmutableList
argument_list|<
name|Function
argument_list|<
name|Tester
argument_list|,
name|Tester
argument_list|>
argument_list|>
name|transforms
parameter_list|)
block|{
name|this
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|preProgram
operator|=
name|preProgram
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|transforms
operator|=
name|transforms
expr_stmt|;
block|}
specifier|public
name|Sql
name|withTester
parameter_list|(
name|UnaryOperator
argument_list|<
name|Tester
argument_list|>
name|transform
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|transform
operator|.
name|apply
argument_list|(
name|tester
argument_list|)
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|transforms
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withPre
parameter_list|(
name|HepProgram
name|preProgram
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|transforms
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|with
parameter_list|(
name|HepPlanner
name|hepPlanner
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
name|hepPlanner
argument_list|,
name|hooks
argument_list|,
name|transforms
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|with
parameter_list|(
name|HepProgram
name|program
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
argument_list|,
name|hooks
argument_list|,
name|transforms
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withRule
parameter_list|(
name|RelOptRule
modifier|...
name|rules
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
name|with
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/** Adds a transform that will be applied to {@link #tester}      * just before running the query. */
specifier|private
name|Sql
name|withTransform
parameter_list|(
name|Function
argument_list|<
name|Tester
argument_list|,
name|Tester
argument_list|>
name|transform
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|FlatLists
operator|.
name|append
argument_list|(
name|transforms
argument_list|,
name|transform
argument_list|)
argument_list|)
return|;
block|}
comment|/** Adds a hook and a handler for that hook. Calcite will create a thread      * hook (by calling {@link Hook#addThread(Consumer)})      * just before running the query, and remove the hook afterwards. */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Sql
name|withHook
parameter_list|(
name|Hook
name|hook
parameter_list|,
name|Consumer
argument_list|<
name|T
argument_list|>
name|handler
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|FlatLists
operator|.
name|append
argument_list|(
name|hooks
argument_list|,
name|hook
argument_list|,
name|handler
argument_list|)
argument_list|,
name|transforms
argument_list|)
return|;
block|}
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link #withHook(Hook, Consumer)}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Sql
name|withHook
parameter_list|(
name|Hook
name|hook
parameter_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|T
argument_list|,
name|Void
argument_list|>
name|handler
parameter_list|)
block|{
return|return
name|withHook
argument_list|(
name|hook
argument_list|,
operator|(
name|Consumer
argument_list|<
name|T
argument_list|>
operator|)
name|handler
operator|::
name|apply
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|V
parameter_list|>
name|Sql
name|withProperty
parameter_list|(
name|Hook
name|hook
parameter_list|,
name|V
name|value
parameter_list|)
block|{
return|return
name|withHook
argument_list|(
name|hook
argument_list|,
name|Hook
operator|.
name|propertyJ
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|expand
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
return|return
name|withTransform
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withExpand
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withLateDecorrelation
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
return|return
name|withTransform
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withLateDecorrelation
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withDecorrelation
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
return|return
name|withTransform
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withDecorrelation
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withTrim
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
return|return
name|withTransform
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withTrim
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withContext
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|)
block|{
return|return
name|withTransform
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withContext
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Checks the plan for a SQL statement before/after executing a given rule,      * with a optional pre-program specified by {@link #withPre(HepProgram)}      * to prepare the tree.      */
specifier|public
name|void
name|check
parameter_list|()
block|{
name|check
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks that the plan is the same before and after executing a given      * planner. Useful for checking circumstances where rules should not fire.      */
specifier|public
name|void
name|checkUnchanged
parameter_list|()
block|{
name|check
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|void
name|check
parameter_list|(
name|boolean
name|unchanged
parameter_list|)
block|{
try|try
init|(
name|Closer
name|closer
init|=
operator|new
name|Closer
argument_list|()
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Hook
argument_list|,
name|Consumer
argument_list|>
name|entry
range|:
name|hooks
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|closer
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|addThread
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Tester
name|t
init|=
name|tester
decl_stmt|;
for|for
control|(
name|Function
argument_list|<
name|Tester
argument_list|,
name|Tester
argument_list|>
name|transform
range|:
name|transforms
control|)
block|{
name|t
operator|=
name|transform
operator|.
name|apply
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|checkPlanning
argument_list|(
name|t
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|sql
argument_list|,
name|unchanged
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

