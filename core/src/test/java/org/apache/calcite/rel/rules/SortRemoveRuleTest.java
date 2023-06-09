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
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
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
name|hamcrest
operator|.
name|MatcherAssert
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    *<p>Since join inputs are sorted, and this join preserves the order of the    * left input, there shouldn't be any sort operator above the join.    */
annotation|@
name|Test
name|void
name|removeSortOverEnumerableHashJoin
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
name|CoreRules
operator|.
name|SORT_PROJECT_TRANSPOSE
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
operator|new
name|Fixture
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
operator|.
name|assertThatPlan
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableHashJoin"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    *<p>Since join inputs are sorted, and this join preserves the order of the    * left input, there shouldn't be any sort operator above the join.    */
annotation|@
name|Test
name|void
name|removeSortOverEnumerableNestedLoopJoin
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
name|CoreRules
operator|.
name|SORT_PROJECT_TRANSPOSE
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
comment|// Inner join is not considered since the ENUMERABLE_JOIN_RULE does not generate a nestedLoop
comment|// join in the case of inner joins.
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
operator|new
name|Fixture
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
operator|.
name|assertThatPlan
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"EnumerableNestedLoopJoin"
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    *<p>Since join inputs are sorted, and this join preserves the order of the    * left input, there shouldn't be any sort operator above the join.    *    *<p>Until CALCITE-2018 is fixed we can add back EnumerableRules.ENUMERABLE_SORT_RULE    */
annotation|@
name|Test
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
name|CoreRules
operator|.
name|SORT_PROJECT_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|JOIN_TO_CORRELATE
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
operator|new
name|Fixture
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
operator|.
name|plan
argument_list|()
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
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2554">[CALCITE-2554]    * Enrich enumerable join operators with order preserving information</a>.    *    *<p>Since join inputs are sorted, and this join preserves the order of the    * left input, there shouldn't be any sort operator above the join.    */
annotation|@
name|Test
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
name|CoreRules
operator|.
name|SORT_PROJECT_TRANSPOSE
argument_list|,
name|CoreRules
operator|.
name|PROJECT_TO_SEMI_JOIN
argument_list|,
name|CoreRules
operator|.
name|JOIN_TO_SEMI_JOIN
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
name|ENUMERABLE_JOIN_RULE
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
operator|new
name|Fixture
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|)
operator|.
name|withSqlToRel
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withExpand
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|plan
argument_list|()
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
literal|"EnumerableHashJoin"
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
specifier|static
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
comment|/** Test fixture. */
specifier|private
specifier|static
class|class
name|Fixture
block|{
specifier|final
name|RuleSet
name|prepareRules
decl_stmt|;
specifier|final
name|String
name|sql
decl_stmt|;
specifier|final
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|sqlToRelConfigTransform
decl_stmt|;
name|Fixture
parameter_list|(
name|String
name|sql
parameter_list|,
name|RuleSet
name|prepareRules
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|sqlToRelConfigTransform
parameter_list|)
block|{
name|this
operator|.
name|prepareRules
operator|=
name|prepareRules
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|sqlToRelConfigTransform
operator|=
name|sqlToRelConfigTransform
expr_stmt|;
block|}
name|Fixture
parameter_list|(
name|String
name|sql
parameter_list|,
name|RuleSet
name|prepareRules
parameter_list|)
block|{
name|this
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|,
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Fixture
name|withSqlToRel
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
specifier|final
name|UnaryOperator
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|newTransform
init|=
name|c
lambda|->
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|sqlToRelConfigTransform
operator|.
name|apply
argument_list|(
name|c
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Fixture
argument_list|(
name|sql
argument_list|,
name|prepareRules
argument_list|,
name|newTransform
argument_list|)
return|;
block|}
comment|/**      * The default schema that is used in these tests provides tables sorted on the primary key. Due      * to this scan operators always come with a {@link org.apache.calcite.rel.RelCollation} trait.      */
name|RelNode
name|plan
parameter_list|()
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
name|sqlToRelConverterConfig
argument_list|(
name|sqlToRelConfigTransform
operator|.
name|apply
argument_list|(
name|SqlToRelConverter
operator|.
name|config
argument_list|()
argument_list|)
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
name|CoreRules
operator|.
name|SORT_REMOVE
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
name|Fixture
name|assertThatPlan
parameter_list|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
try|try
block|{
name|RelNode
name|actualPlan
init|=
name|plan
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|SortRemoveRuleTest
operator|.
name|toString
argument_list|(
name|actualPlan
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

