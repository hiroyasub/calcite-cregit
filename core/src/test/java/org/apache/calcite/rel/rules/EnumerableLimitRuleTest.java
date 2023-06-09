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
name|RelFieldCollation
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
name|List
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

begin_comment
comment|/**  * Tests the application of the {@code EnumerableLimitRule}.  */
end_comment

begin_class
class|class
name|EnumerableLimitRuleTest
block|{
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2941">[CALCITE-2941]    * EnumerableLimitRule on Sort with no collation creates EnumerableLimit with    * wrong traitSet and cluster</a>.    */
annotation|@
name|Test
name|void
name|enumerableLimitOnEmptySort
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
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_RULE
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
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|)
decl_stmt|;
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
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelBuilder
name|builder
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|RelNode
name|planBefore
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"hr"
argument_list|,
literal|"emps"
argument_list|)
operator|.
name|sort
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|0
argument_list|)
argument_list|)
comment|// will produce collation [0] in the plan
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|notEquals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|0
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
comment|// force a limit inside an "empty" Sort (with no collation)
operator|.
name|build
argument_list|()
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
name|Program
name|program
init|=
name|Programs
operator|.
name|of
argument_list|(
name|prepareRules
argument_list|)
decl_stmt|;
name|RelNode
name|planAfter
init|=
name|program
operator|.
name|run
argument_list|(
name|planBefore
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
argument_list|,
name|planBefore
argument_list|,
name|desiredTraits
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
comment|// verify that the collation [0] is not lost in the final plan
specifier|final
name|RelCollation
name|collation
init|=
name|planAfter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollationList
init|=
name|collation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|fieldCollationList
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldCollationList
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldCollationList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldIndex
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

