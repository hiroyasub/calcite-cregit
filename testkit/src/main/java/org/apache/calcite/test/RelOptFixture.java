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
name|ConventionTraitDef
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
name|sql
operator|.
name|test
operator|.
name|SqlTestFactory
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
name|test
operator|.
name|SqlTester
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
name|util
operator|.
name|SqlOperatorTables
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
name|validate
operator|.
name|SqlConformance
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
name|SqlToRelConverter
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
name|test
operator|.
name|catalog
operator|.
name|MockCatalogReaderDynamic
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|BiFunction
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
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|relIsValid
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|SqlToRelTestBase
operator|.
name|NL
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * A fixture for testing planner rules.  *  *<p>It provides a fluent API so that you can write tests by chaining method  * calls.  *  *<p>A fixture is immutable. If you have two test cases that require a similar  * set up (for example, the same SQL expression and set of planner rules), it is  * safe to use the same fixture object as a starting point for both tests.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptFixture
block|{
specifier|static
specifier|final
name|RelOptFixture
name|DEFAULT
init|=
operator|new
name|RelOptFixture
argument_list|(
name|SqlToRelFixture
operator|.
name|TESTER
argument_list|,
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|,
name|RelSupplier
operator|.
name|NONE
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
parameter_list|(
name|f
parameter_list|,
name|r
parameter_list|)
lambda|->
name|r
argument_list|,
parameter_list|(
name|f
parameter_list|,
name|r
parameter_list|)
lambda|->
name|r
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withIdentifierExpansion
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|withSqlToRelConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withRelBuilderConfig
argument_list|(
name|b
lambda|->
name|b
operator|.
name|withPruneInputOfAggregate
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * The tester for this test. The field is vestigial; there is no    * {@code withTester} method, and the same tester is always used.    */
specifier|final
name|SqlTester
name|tester
decl_stmt|;
specifier|final
name|RelSupplier
name|relSupplier
decl_stmt|;
specifier|final
name|SqlTestFactory
name|factory
decl_stmt|;
specifier|final
annotation|@
name|Nullable
name|DiffRepository
name|diffRepos
decl_stmt|;
specifier|final
annotation|@
name|Nullable
name|HepProgram
name|preProgram
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|final
name|ImmutableMap
argument_list|<
name|Hook
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|hooks
decl_stmt|;
specifier|final
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|before
decl_stmt|;
specifier|final
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|after
decl_stmt|;
specifier|final
name|boolean
name|decorrelate
decl_stmt|;
specifier|final
name|boolean
name|lateDecorrelate
decl_stmt|;
name|RelOptFixture
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|SqlTestFactory
name|factory
parameter_list|,
annotation|@
name|Nullable
name|DiffRepository
name|diffRepos
parameter_list|,
name|RelSupplier
name|relSupplier
parameter_list|,
annotation|@
name|Nullable
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
argument_list|<
name|Object
argument_list|>
argument_list|>
name|hooks
parameter_list|,
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|before
parameter_list|,
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|after
parameter_list|,
name|boolean
name|decorrelate
parameter_list|,
name|boolean
name|lateDecorrelate
parameter_list|)
block|{
name|this
operator|.
name|tester
operator|=
name|requireNonNull
argument_list|(
name|tester
argument_list|,
literal|"tester"
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|diffRepos
operator|=
name|diffRepos
expr_stmt|;
name|this
operator|.
name|relSupplier
operator|=
name|requireNonNull
argument_list|(
name|relSupplier
argument_list|,
literal|"relSupplier"
argument_list|)
expr_stmt|;
name|this
operator|.
name|before
operator|=
name|requireNonNull
argument_list|(
name|before
argument_list|,
literal|"before"
argument_list|)
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|requireNonNull
argument_list|(
name|after
argument_list|,
literal|"after"
argument_list|)
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
name|requireNonNull
argument_list|(
name|hooks
argument_list|,
literal|"hooks"
argument_list|)
expr_stmt|;
name|this
operator|.
name|decorrelate
operator|=
name|decorrelate
expr_stmt|;
name|this
operator|.
name|lateDecorrelate
operator|=
name|lateDecorrelate
expr_stmt|;
block|}
specifier|public
name|RelOptFixture
name|withDiffRepos
parameter_list|(
name|DiffRepository
name|diffRepos
parameter_list|)
block|{
if|if
condition|(
name|diffRepos
operator|.
name|equals
argument_list|(
name|this
operator|.
name|diffRepos
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withRelSupplier
parameter_list|(
name|RelSupplier
name|relSupplier
parameter_list|)
block|{
if|if
condition|(
name|relSupplier
operator|.
name|equals
argument_list|(
name|this
operator|.
name|relSupplier
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|withRelSupplier
argument_list|(
name|RelSupplier
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|)
return|;
block|}
name|RelOptFixture
name|relFn
parameter_list|(
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
name|relFn
parameter_list|)
block|{
return|return
name|withRelSupplier
argument_list|(
name|RelSupplier
operator|.
name|of
argument_list|(
name|relFn
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withBefore
parameter_list|(
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|transform
parameter_list|)
block|{
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|before0
init|=
name|this
operator|.
name|before
decl_stmt|;
specifier|final
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|before
init|=
parameter_list|(
name|sql
parameter_list|,
name|r
parameter_list|)
lambda|->
name|transform
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|before0
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|r
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withAfter
parameter_list|(
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|after0
init|=
name|this
operator|.
name|after
decl_stmt|;
specifier|final
name|BiFunction
argument_list|<
name|RelOptFixture
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|after
init|=
parameter_list|(
name|sql
parameter_list|,
name|r
parameter_list|)
lambda|->
name|transform
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|after0
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|r
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withDynamicTable
parameter_list|()
block|{
return|return
name|withCatalogReaderFactory
argument_list|(
name|MockCatalogReaderDynamic
operator|::
name|create
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withFactory
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTestFactory
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlTestFactory
name|factory
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|factory
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|factory
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withPre
parameter_list|(
name|HepProgram
name|preProgram
parameter_list|)
block|{
if|if
condition|(
name|preProgram
operator|.
name|equals
argument_list|(
name|this
operator|.
name|preProgram
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withPreRule
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
name|withPre
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withPlanner
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
if|if
condition|(
name|planner
operator|.
name|equals
argument_list|(
name|this
operator|.
name|planner
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withProgram
parameter_list|(
name|HepProgram
name|program
parameter_list|)
block|{
return|return
name|withPlanner
argument_list|(
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
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
name|withProgram
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Adds a hook and a handler for that hook. Calcite will create a thread    * hook (by calling {@link Hook#addThread(Consumer)})    * just before running the query, and remove the hook afterwards.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|RelOptFixture
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
specifier|final
name|ImmutableMap
argument_list|<
name|Hook
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|hooks
init|=
name|FlatLists
operator|.
name|append
argument_list|(
operator|(
name|Map
operator|)
name|this
operator|.
name|hooks
argument_list|,
name|hook
argument_list|,
operator|(
name|Consumer
operator|)
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
name|hooks
operator|.
name|equals
argument_list|(
name|this
operator|.
name|hooks
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|V
parameter_list|>
name|RelOptFixture
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
name|RelOptFixture
name|withRelBuilderSimplify
parameter_list|(
name|boolean
name|simplify
parameter_list|)
block|{
return|return
name|withProperty
argument_list|(
name|Hook
operator|.
name|REL_BUILDER_SIMPLIFY
argument_list|,
name|simplify
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withExpand
parameter_list|(
specifier|final
name|boolean
name|expand
parameter_list|)
block|{
return|return
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
name|expand
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withSqlToRelConfig
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withRelBuilderConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|RelBuilder
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|addRelBuilderConfigTransform
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withLateDecorrelate
parameter_list|(
specifier|final
name|boolean
name|lateDecorrelate
parameter_list|)
block|{
if|if
condition|(
name|lateDecorrelate
operator|==
name|this
operator|.
name|lateDecorrelate
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withDecorrelate
parameter_list|(
specifier|final
name|boolean
name|decorrelate
parameter_list|)
block|{
if|if
condition|(
name|decorrelate
operator|==
name|this
operator|.
name|decorrelate
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|RelOptFixture
argument_list|(
name|tester
argument_list|,
name|factory
argument_list|,
name|diffRepos
argument_list|,
name|relSupplier
argument_list|,
name|preProgram
argument_list|,
name|planner
argument_list|,
name|hooks
argument_list|,
name|before
argument_list|,
name|after
argument_list|,
name|decorrelate
argument_list|,
name|lateDecorrelate
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withTrim
parameter_list|(
specifier|final
name|boolean
name|trim
parameter_list|)
block|{
return|return
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withTrimUnusedFields
argument_list|(
name|trim
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withCatalogReaderFactory
parameter_list|(
name|SqlTestFactory
operator|.
name|CatalogReaderFactory
name|factory
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withCatalogReader
argument_list|(
name|factory
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withConformance
parameter_list|(
specifier|final
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|t
lambda|->
name|conformance
operator|.
name|allowGeometry
argument_list|()
condition|?
name|SqlOperatorTables
operator|.
name|chain
argument_list|(
name|t
argument_list|,
name|SqlOperatorTables
operator|.
name|spatialInstance
argument_list|()
argument_list|)
else|:
name|t
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withContext
parameter_list|(
specifier|final
name|UnaryOperator
argument_list|<
name|Context
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withPlannerContext
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|()
block|{
return|return
name|relSupplier
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Checks the plan for a SQL statement before/after executing a given rule,    * with an optional pre-program specified by {@link #withPre(HepProgram)}    * to prepare the tree.    */
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
comment|/**    * Checks that the plan is the same before and after executing a given    * planner. Useful for checking circumstances where rules should not fire.    */
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
argument_list|<
name|Object
argument_list|>
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
name|checkPlanning
argument_list|(
name|unchanged
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Checks the plan for a given {@link RelNode} supplier before/after executing    * a given rule, with a pre-program to prepare the tree.    *    * @param unchanged Whether the rule is to have no effect    */
specifier|private
name|void
name|checkPlanning
parameter_list|(
name|boolean
name|unchanged
parameter_list|)
block|{
specifier|final
name|RelNode
name|relInitial
init|=
name|toRel
argument_list|()
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
comment|// Rather than a single mutable 'RelNode r', this method uses lots of
comment|// final variables (relInitial, r1, relBefore, and so forth) so that the
comment|// intermediate states of planning are visible in the debugger.
specifier|final
name|RelNode
name|r1
decl_stmt|;
if|if
condition|(
name|preProgram
operator|==
literal|null
condition|)
block|{
name|r1
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
name|r1
operator|=
name|prePlanner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RelNode
name|relBefore
init|=
name|before
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|r1
argument_list|)
decl_stmt|;
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
specifier|final
name|DiffRepository
name|diffRepos
init|=
name|diffRepos
argument_list|()
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
name|assertThat
argument_list|(
name|relBefore
argument_list|,
name|relIsValid
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|r2
decl_stmt|;
if|if
condition|(
name|planner
operator|instanceof
name|VolcanoPlanner
condition|)
block|{
name|r2
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
else|else
block|{
name|r2
operator|=
name|relBefore
expr_stmt|;
block|}
name|planner
operator|.
name|setRoot
argument_list|(
name|r2
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|r3
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|r4
decl_stmt|;
if|if
condition|(
name|lateDecorrelate
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
name|r3
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
name|assertThat
argument_list|(
name|r3
argument_list|,
name|relIsValid
argument_list|()
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
name|r4
operator|=
name|RelDecorrelator
operator|.
name|decorrelateQuery
argument_list|(
name|r3
argument_list|,
name|relBuilder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r4
operator|=
name|r3
expr_stmt|;
block|}
specifier|final
name|RelNode
name|relAfter
init|=
name|after
operator|.
name|apply
argument_list|(
name|this
argument_list|,
name|r4
argument_list|)
decl_stmt|;
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
name|relAfter
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
name|assertThat
argument_list|(
name|relAfter
argument_list|,
name|relIsValid
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelOptFixture
name|withVolcanoPlanner
parameter_list|(
name|boolean
name|topDown
parameter_list|)
block|{
return|return
name|withVolcanoPlanner
argument_list|(
name|topDown
argument_list|,
name|p
lambda|->
name|RelOptUtil
operator|.
name|registerDefaultRules
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withVolcanoPlanner
parameter_list|(
name|boolean
name|topDown
parameter_list|,
name|Consumer
argument_list|<
name|VolcanoPlanner
argument_list|>
name|init
parameter_list|)
block|{
specifier|final
name|VolcanoPlanner
name|planner
init|=
operator|new
name|VolcanoPlanner
argument_list|()
decl_stmt|;
name|planner
operator|.
name|setTopDownOpt
argument_list|(
name|topDown
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|init
operator|.
name|accept
argument_list|(
name|planner
argument_list|)
expr_stmt|;
return|return
name|withPlanner
argument_list|(
name|planner
argument_list|)
operator|.
name|withDecorrelate
argument_list|(
literal|true
argument_list|)
operator|.
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withCluster
argument_list|(
name|cluster
lambda|->
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelOptFixture
name|withSubQueryRules
parameter_list|()
block|{
return|return
name|withExpand
argument_list|(
literal|false
argument_list|)
operator|.
name|withRule
argument_list|(
name|CoreRules
operator|.
name|PROJECT_SUB_QUERY_TO_CORRELATE
argument_list|,
name|CoreRules
operator|.
name|FILTER_SUB_QUERY_TO_CORRELATE
argument_list|,
name|CoreRules
operator|.
name|JOIN_SUB_QUERY_TO_CORRELATE
argument_list|)
return|;
block|}
comment|/**    * Returns the diff repository, checking that it is not null.    * (It is allowed to be null because some tests that don't use a diff    * repository.)    */
specifier|public
name|DiffRepository
name|diffRepos
parameter_list|()
block|{
return|return
name|DiffRepository
operator|.
name|castNonNull
argument_list|(
name|diffRepos
argument_list|)
return|;
block|}
block|}
end_class

end_unit

