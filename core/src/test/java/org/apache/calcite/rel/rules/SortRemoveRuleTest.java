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
name|rel
operator|.
name|RelCollationTraitDef
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
name|schema
operator|.
name|SchemaPlus
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
name|schemas
operator|.
name|HrClusteredSchema
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
name|SqlExplainFormat
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
name|SqlExplainLevel
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
name|parser
operator|.
name|SqlParser
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
name|Frameworks
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
name|Planner
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
name|RuleSet
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
name|calcite
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|allOf
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
name|containsString
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
name|not
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests the application of the {@link SortRemoveRule}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SortRemoveRuleTest
block|{
comment|/**    * The default schema that is used in these tests provides tables sorted on the primary key. Due    * to this scan operators always come with a {@link org.apache.calcite.rel.RelCollation} trait.    */
specifier|private
name|RelNode
name|transform
parameter_list|(
name|String
name|sql
parameter_list|,
name|RuleSet
name|prepareRules
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|defSchema
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|HrClusteredSchema
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|defSchema
argument_list|)
operator|.
name|traitDefs
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|,
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
operator|.
name|programs
argument_list|(
name|Programs
operator|.
name|of
argument_list|(
name|prepareRules
argument_list|)
argument_list|,
name|Programs
operator|.
name|ofRules
argument_list|(
name|SortRemoveRule
operator|.
name|INSTANCE
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
name|RelRoot
name|planRoot
init|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
decl_stmt|;
name|RelNode
name|planBefore
init|=
name|planRoot
operator|.
name|rel
decl_stmt|;
name|RelTraitSet
name|desiredTraits
init|=
name|planBefore
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
name|planRoot
operator|.
name|collation
argument_list|)
operator|.
name|simplify
argument_list|()
decl_stmt|;
name|RelNode
name|planAfter
init|=
name|planner
operator|.
name|transform
argument_list|(
literal|0
argument_list|,
name|desiredTraits
argument_list|,
name|planBefore
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|transform
argument_list|(
literal|1
argument_list|,
name|desiredTraits
argument_list|,
name|planAfter
argument_list|)
return|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    * Since join inputs are sorted, and this join preserves the order of the left input, there    * shouldn't be any sort operator above the join.    */
annotation|@
name|Test
specifier|public
name|void
name|removeSortOverEnumerableJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|prepareRules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|joinType
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"left"
argument_list|,
literal|"right"
argument_list|,
literal|"full"
argument_list|,
literal|"inner"
argument_list|)
control|)
block|{
name|String
name|sql
init|=
literal|"select e.\"deptno\" from \"hr\".\"emps\" e "
operator|+
name|joinType
operator|+
literal|" join \"hr\".\"depts\" d "
operator|+
literal|" on e.\"deptno\" = d.\"deptno\" "
operator|+
literal|"order by e.\"empid\" "
decl_stmt|;
name|RelNode
name|actualPlan
init|=
name|transform
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|actualPlan
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableJoin"
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableSort"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    * Since join inputs are sorted, and this join preserves the order of the left input, there    * shouldn't be any sort operator above the join.    */
annotation|@
name|Test
specifier|public
name|void
name|removeSortOverEnumerableThetaJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|prepareRules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|)
decl_stmt|;
comment|// Inner join is not considered since the ENUMERABLE_JOIN_RULE does not generate a theta join
comment|// in the case of inner joins.
for|for
control|(
name|String
name|joinType
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"left"
argument_list|,
literal|"right"
argument_list|,
literal|"full"
argument_list|)
control|)
block|{
name|String
name|sql
init|=
literal|"select e.\"deptno\" from \"hr\".\"emps\" e "
operator|+
name|joinType
operator|+
literal|" join \"hr\".\"depts\" d "
operator|+
literal|" on e.\"deptno\"> d.\"deptno\" "
operator|+
literal|"order by e.\"empid\" "
decl_stmt|;
name|RelNode
name|actualPlan
init|=
name|transform
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|actualPlan
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableThetaJoin"
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableSort"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    * Since join inputs are sorted, and this join preserves the order of the left input, there    * shouldn't be any sort operator above the join.    */
annotation|@
name|Test
specifier|public
name|void
name|removeSortOverEnumerableCorrelate
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|prepareRules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|JoinToCorrelateRule
operator|.
name|INSTANCE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_CORRELATE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|joinType
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"left"
argument_list|,
literal|"inner"
argument_list|)
control|)
block|{
name|String
name|sql
init|=
literal|"select e.\"deptno\" from \"hr\".\"emps\" e "
operator|+
name|joinType
operator|+
literal|" join \"hr\".\"depts\" d "
operator|+
literal|" on e.\"deptno\" = d.\"deptno\" "
operator|+
literal|"order by e.\"empid\" "
decl_stmt|;
name|RelNode
name|actualPlan
init|=
name|transform
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|actualPlan
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableCorrelate"
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableSort"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    * Since join inputs are sorted, and this join preserves the order of the left input, there    * shouldn't be any sort operator above the join.    */
annotation|@
name|Test
specifier|public
name|void
name|removeSortOverEnumerableSemiJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|RuleSet
name|prepareRules
init|=
name|RuleSets
operator|.
name|ofList
argument_list|(
name|SortProjectTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|SemiJoinRule
operator|.
name|PROJECT
argument_list|,
name|SemiJoinRule
operator|.
name|JOIN
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SEMI_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
literal|"select e.\"deptno\" from \"hr\".\"emps\" e\n"
operator|+
literal|" where e.\"deptno\" in (select d.\"deptno\" from \"hr\".\"depts\" d)\n"
operator|+
literal|" order by e.\"empid\""
decl_stmt|;
name|RelNode
name|actualPlan
init|=
name|transform
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|toString
argument_list|(
name|actualPlan
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableSemiJoin"
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableSort"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|""
argument_list|,
name|rel
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SortRemoveRuleTest.java
end_comment

end_unit

