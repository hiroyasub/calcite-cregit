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
name|test
package|;
end_package

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
name|metadata
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
name|relopt
operator|.
name|hep
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
comment|/**      * Checks the plan for a SQL statement before/after executing a given rule.      *      * @param rule Planner rule      * @param sql SQL query      */
specifier|protected
name|void
name|checkPlanning
parameter_list|(
name|RelOptRule
name|rule
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|checkPlanning
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the plan for a SQL statement before/after executing a given      * program.      *      * @param program Planner program      * @param sql SQL query      */
specifier|protected
name|void
name|checkPlanning
parameter_list|(
name|HepProgram
name|program
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|checkPlanning
argument_list|(
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the plan for a SQL statement before/after executing a given      * planner.      *      * @param planner Planner      * @param sql SQL query      */
specifier|protected
name|void
name|checkPlanning
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|checkPlanning
argument_list|(
literal|null
argument_list|,
name|planner
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the plan for a SQL statement before/after executing a given rule,      * with a pre-program to prepare the tree.      *      * @param preProgram Program to execute before comparing before state      * @param rule Planner rule      * @param sql SQL query      */
specifier|protected
name|void
name|checkPlanning
parameter_list|(
name|HepProgram
name|preProgram
parameter_list|,
name|RelOptRule
name|rule
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|HepProgramBuilder
name|programBuilder
init|=
name|HepProgram
operator|.
name|builder
argument_list|()
decl_stmt|;
name|programBuilder
operator|.
name|addRuleInstance
argument_list|(
name|rule
argument_list|)
expr_stmt|;
specifier|final
name|HepPlanner
name|planner
init|=
operator|new
name|HepPlanner
argument_list|(
name|programBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|checkPlanning
argument_list|(
name|preProgram
argument_list|,
name|planner
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks the plan for a SQL statement before/after executing a given rule,      * with a pre-program to prepare the tree.      *      * @param preProgram Program to execute before comparing before state      * @param planner Planner      * @param sql SQL query      */
specifier|protected
name|void
name|checkPlanning
parameter_list|(
name|HepProgram
name|preProgram
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|,
name|String
name|sql
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
name|RelNode
name|relInitial
init|=
name|tester
operator|.
name|convertSqlToRel
argument_list|(
name|sql2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|relInitial
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ChainedRelMetadataProvider
name|plannerChain
init|=
operator|new
name|ChainedRelMetadataProvider
argument_list|()
decl_stmt|;
name|DefaultRelMetadataProvider
name|defaultProvider
init|=
operator|new
name|DefaultRelMetadataProvider
argument_list|()
decl_stmt|;
name|plannerChain
operator|.
name|addProvider
argument_list|(
name|defaultProvider
argument_list|)
expr_stmt|;
name|planner
operator|.
name|registerMetadataProviders
argument_list|(
name|plannerChain
argument_list|)
expr_stmt|;
name|relInitial
operator|.
name|getCluster
argument_list|()
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
name|assertTrue
argument_list|(
name|relBefore
operator|!=
literal|null
argument_list|)
expr_stmt|;
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
name|planner
operator|.
name|setRoot
argument_list|(
name|relBefore
argument_list|)
expr_stmt|;
name|RelNode
name|relAfter
init|=
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
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
block|}
comment|/**      * Creates a program which is a sequence of rules.      *      * @param rules Sequence of rules      *      * @return Program      */
specifier|protected
specifier|static
name|HepProgram
name|createProgram
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
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptTestBase.java
end_comment

end_unit

