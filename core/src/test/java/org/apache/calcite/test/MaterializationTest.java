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
name|java
operator|.
name|ReflectiveSchema
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|materialize
operator|.
name|MaterializationService
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
name|RelOptPredicateList
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
name|RelOptRules
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
name|RelOptTable
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
name|SubstitutionVisitor
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
name|prepare
operator|.
name|Prepare
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
name|RelReferentialConstraint
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
name|RelReferentialConstraintImpl
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
name|RelVisitor
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
name|TableScan
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeSystem
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexInputRef
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexSimplify
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
name|rex
operator|.
name|RexUtil
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
name|schema
operator|.
name|QueryableTable
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
name|TranslatableTable
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|JdbcTest
operator|.
name|Department
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
name|JdbcTest
operator|.
name|Dependent
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
name|JdbcTest
operator|.
name|Employee
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
name|JdbcTest
operator|.
name|Event
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
name|JdbcTest
operator|.
name|Location
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
name|JsonBuilder
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
name|Smalls
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
name|TryThreadLocal
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
name|mapping
operator|.
name|IntPair
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
name|Ordering
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
name|Disabled
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
name|Tag
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|Collections
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
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
name|assertEquals
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
name|assertFalse
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
name|assertNull
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Unit test for the materialized view rewrite mechanism. Each test has a  * query and one or more materializations (what Oracle calls materialized views)  * and checks that the materialization is used.  */
end_comment

begin_class
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
specifier|public
class|class
name|MaterializationTest
block|{
specifier|private
specifier|static
specifier|final
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|CONTAINS_M0
init|=
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|CONTAINS_LOCATIONS
init|=
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, locations]])"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Ordering
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|CASE_INSENSITIVE_LIST_COMPARATOR
init|=
name|Ordering
operator|.
name|from
argument_list|(
name|String
operator|.
name|CASE_INSENSITIVE_ORDER
argument_list|)
operator|.
name|lexicographical
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Ordering
argument_list|<
name|Iterable
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|CASE_INSENSITIVE_LIST_LIST_COMPARATOR
init|=
name|CASE_INSENSITIVE_LIST_COMPARATOR
operator|.
name|lexicographical
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HR_FKUK_SCHEMA
init|=
literal|"{\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'hr',\n"
operator|+
literal|"       factory: '"
operator|+
name|ReflectiveSchema
operator|.
name|Factory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         class: '"
operator|+
name|HrFKUKSchema
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HR_FKUK_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'hr',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|HR_FKUK_SCHEMA
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|final
name|JavaTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
init|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RexSimplify
name|simplify
init|=
operator|new
name|RexSimplify
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|EMPTY
argument_list|,
name|RexUtil
operator|.
name|EXECUTOR
argument_list|)
operator|.
name|withParanoid
argument_list|(
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testScan
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'SCOTT_CLONE',\n"
operator|+
literal|"  schemas: [ {\n"
operator|+
literal|"    name: 'SCOTT_CLONE',\n"
operator|+
literal|"    type: 'custom',\n"
operator|+
literal|"    factory: 'org.apache.calcite.adapter.clone.CloneSchema$Factory',\n"
operator|+
literal|"    operand: {\n"
operator|+
literal|"      jdbcDriver: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|driver
operator|+
literal|"',\n"
operator|+
literal|"      jdbcUser: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|username
operator|+
literal|"',\n"
operator|+
literal|"      jdbcPassword: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|password
operator|+
literal|"',\n"
operator|+
literal|"      jdbcUrl: '"
operator|+
name|JdbcTest
operator|.
name|SCOTT
operator|.
name|url
operator|+
literal|"',\n"
operator|+
literal|"      jdbcSchema: 'SCOTT'\n"
operator|+
literal|"   } } ]\n"
operator|+
literal|"}"
argument_list|,
literal|"m0"
argument_list|,
literal|"select empno, deptno from emp order by deptno"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empno, deptno from emp"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[SCOTT_CLONE, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\" + 1 from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterToProject0
parameter_list|()
block|{
name|String
name|union
init|=
literal|"select * from \"emps\" where \"empid\"> 300\n"
operator|+
literal|"union all select * from \"emps\" where \"empid\"< 200"
decl_stmt|;
name|String
name|mv
init|=
literal|"select *, \"empid\" * 2 from ("
operator|+
name|union
operator|+
literal|")"
decl_stmt|;
name|String
name|query
init|=
literal|"select * from ("
operator|+
name|union
operator|+
literal|") where (\"empid\" * 2)> 3"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterToProject1
parameter_list|()
block|{
name|String
name|agg
init|=
literal|"select \"deptno\", count(*) as \"c\", sum(\"salary\") as \"s\"\n"
operator|+
literal|"from \"emps\" group by \"deptno\""
decl_stmt|;
name|String
name|mv
init|=
literal|"select \"c\", \"s\", \"s\" from ("
operator|+
name|agg
operator|+
literal|")"
decl_stmt|;
name|String
name|query
init|=
literal|"select * from ("
operator|+
name|agg
operator|+
literal|") where (\"s\" * 0.8)> 10000"
decl_stmt|;
name|checkNoMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|)
block|{
name|checkMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|,
name|RuleSets
operator|.
name|ofList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|boolean
name|onlyBySubstitution
parameter_list|)
block|{
name|checkMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|,
name|RuleSets
operator|.
name|ofList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|,
name|onlyBySubstitution
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|model
parameter_list|,
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|explainChecker
parameter_list|)
block|{
name|checkMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|model
argument_list|,
name|explainChecker
argument_list|,
name|RuleSets
operator|.
name|ofList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|model
parameter_list|,
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|explainChecker
parameter_list|,
specifier|final
name|RuleSet
name|rules
parameter_list|)
block|{
name|checkMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|model
argument_list|,
name|explainChecker
argument_list|,
name|rules
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|model
parameter_list|,
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|explainChecker
parameter_list|,
specifier|final
name|RuleSet
name|rules
parameter_list|,
name|boolean
name|onlyBySubstitution
parameter_list|)
block|{
name|checkThatMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
literal|"m0"
argument_list|,
literal|false
argument_list|,
name|model
argument_list|,
name|explainChecker
argument_list|,
name|rules
argument_list|,
name|onlyBySubstitution
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|checkThatMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|existing
parameter_list|,
name|String
name|model
parameter_list|,
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|explainChecker
parameter_list|,
specifier|final
name|RuleSet
name|rules
parameter_list|)
block|{
return|return
name|checkThatMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|name
argument_list|,
name|existing
argument_list|,
name|model
argument_list|,
name|explainChecker
argument_list|,
name|rules
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Checks that a given query can use a materialized view with a given    * definition. */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|checkThatMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|existing
parameter_list|,
name|String
name|model
parameter_list|,
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|explainChecker
parameter_list|,
specifier|final
name|RuleSet
name|rules
parameter_list|,
name|boolean
name|onlyBySubstitution
parameter_list|)
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|AssertQuery
name|that
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|model
argument_list|,
name|existing
argument_list|,
name|name
argument_list|,
name|materialize
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Add any additional rules required for the test
if|if
condition|(
name|rules
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
operator|||
name|onlyBySubstitution
condition|)
block|{
name|that
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PLANNER
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelOptPlanner
argument_list|>
operator|)
name|planner
lambda|->
block|{
for|for
control|(
name|RelOptRule
name|rule
range|:
name|rules
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
if|if
condition|(
name|onlyBySubstitution
condition|)
block|{
name|RelOptRules
operator|.
name|MATERIALIZATION_RULES
operator|.
name|forEach
argument_list|(
name|rule
lambda|->
block|{
name|planner
operator|.
name|removeRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|that
operator|.
name|explainMatches
argument_list|(
literal|""
argument_list|,
name|explainChecker
argument_list|)
return|;
block|}
block|}
comment|/** Checks that a given query CAN NOT use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkNoMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|model
parameter_list|)
block|{
name|checkNoMaterialize
argument_list|(
name|materialize
argument_list|,
name|query
argument_list|,
name|model
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that a given query CAN NOT use a materialized view with a given    * definition. */
specifier|private
name|void
name|checkNoMaterialize
parameter_list|(
name|String
name|materialize
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|model
parameter_list|,
name|boolean
name|onlyBySubstitution
parameter_list|)
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|AssertQuery
name|that
init|=
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|model
argument_list|,
literal|"m0"
argument_list|,
name|materialize
argument_list|)
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|onlyBySubstitution
condition|)
block|{
name|that
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|PLANNER
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelOptPlanner
argument_list|>
operator|)
name|planner
lambda|->
block|{
name|RelOptRules
operator|.
name|MATERIALIZATION_RULES
operator|.
name|forEach
argument_list|(
name|rule
lambda|->
block|{
name|planner
operator|.
name|removeRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
name|that
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Runs the same test as {@link #testFilterQueryOnProjectView()} but more    * concisely. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView0
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in    * materialized view. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in both    * materialized view and query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" - 10 = 0"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but materialized view cannot    * be used because it does not contain required expression. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView4
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" + 10 = 20"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but also contains an    * expression column. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1 as ee, \"name\"\n"
operator|+
literal|"from \"emps\""
argument_list|,
literal|"select \"name\", \"empid\" + 1 as e\n"
operator|+
literal|"from \"emps\" where \"deptno\" - 10 = 2"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[2], "
operator|+
literal|"expr#4=[=($t0, $t3)], name=[$t2], E=[$t1], $condition=[$t4])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Cannot materialize because "name" is not projected in the MV. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView6
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\"  from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" - 10 = 0"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but also contains an    * expression column. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView7
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\", \"empid\" + 2 from \"emps\" where \"deptno\" - 10 = 0"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-988">[CALCITE-988]    * FilterToProjectUnifyRule.invert(MutableRel, MutableRel, MutableProject)    * works incorrectly</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnProjectView8
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
specifier|final
name|String
name|m
init|=
literal|"select \"salary\", \"commission\",\n"
operator|+
literal|"\"deptno\", \"empid\", \"name\" from \"emps\""
decl_stmt|;
specifier|final
name|String
name|v
init|=
literal|"select * from \"emps\" where \"name\" is null"
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select * from V where \"commission\" is null"
decl_stmt|;
specifier|final
name|JsonBuilder
name|builder
init|=
operator|new
name|JsonBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'hr',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      materializations: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          table: 'm0',\n"
operator|+
literal|"          view: 'm0v',\n"
operator|+
literal|"          sql: "
operator|+
name|builder
operator|.
name|toJsonString
argument_list|(
name|m
argument_list|)
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      tables: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          name: 'V',\n"
operator|+
literal|"          type: 'view',\n"
operator|+
literal|"          sql: "
operator|+
name|builder
operator|.
name|toJsonString
argument_list|(
name|v
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      name: 'hr',\n"
operator|+
literal|"      factory: 'org.apache.calcite.adapter.java.ReflectiveSchema$Factory',\n"
operator|+
literal|"      operand: {\n"
operator|+
literal|"        class: 'org.apache.calcite.test.JdbcTest$HrSchema'\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|model
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|""
argument_list|,
name|CONTAINS_M0
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" "
operator|+
literal|"where \"deptno\" = 10 and \"empid\"< 150"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * view. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" "
operator|+
literal|"where \"deptno\" = 10 or \"deptno\" = 20 or \"empid\"< 160"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], expr#4=[+($t1, $t3)], expr#5=[10], "
operator|+
literal|"expr#6=[CAST($t0):INTEGER NOT NULL], expr#7=[=($t5, $t6)], X=[$t4], "
operator|+
literal|"name=[$t2], $condition=[$t7])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select * from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView6
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"name\", \"deptno\", \"salary\" from \"emps\" "
operator|+
literal|"where \"salary\"> 2000.5"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView7
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select * from \"emps\" where "
operator|+
literal|"((\"salary\"< 1111.9 and \"deptno\"> 10)"
operator|+
literal|"or (\"empid\"> 400 and \"salary\"> 5000) "
operator|+
literal|"or \"salary\"> 500)"
argument_list|,
literal|"select \"name\" from \"emps\" where (\"salary\"> 1000 "
operator|+
literal|"or (\"deptno\">= 30 and \"salary\"<= 500))"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. However, columns selected are not present in columns of materialized    * view, Hence should not use materialized view. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView8
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" where \"deptno\"> 30"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView9
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" "
operator|+
literal|"where \"deptno\"> 30 or \"empid\"> 10"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition currently    * has unsupported type being checked on query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView10
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10 "
operator|+
literal|"and \"name\" = \'calcite\'"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" where \"deptno\"> 30 "
operator|+
literal|"or \"empid\"> 10"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView11
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where "
operator|+
literal|"(\"salary\"< 1111.9 and \"deptno\"> 10)"
operator|+
literal|"or (\"empid\"> 400 and \"salary\"> 5000)"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition of    * query is stronger but is on the column not present in MV (salary).    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView12
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"salary\"> 2000.5"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView13
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select * from \"emps\" where "
operator|+
literal|"(\"salary\"< 1111.9 and \"deptno\"> 10)"
operator|+
literal|"or (\"empid\"> 400 and \"salary\"> 5000)"
argument_list|,
literal|"select \"name\" from \"emps\" where \"salary\"> 1000 "
operator|+
literal|"or (\"deptno\"> 30 and \"salary\"> 3000)"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView7()} but columns in materialized    * view are a permutation of columns in the query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterQueryOnFilterView14
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select * from \"emps\" where (\"salary\"> 1000 "
operator|+
literal|"or (\"deptno\">= 30 and \"salary\"<= 500))"
decl_stmt|;
name|String
name|m
init|=
literal|"select \"deptno\", \"empid\", \"name\", \"salary\", \"commission\" "
operator|+
literal|"from \"emps\" as em where "
operator|+
literal|"((\"salary\"< 1111.9 and \"deptno\"> 10)"
operator|+
literal|"or (\"empid\"> 400 and \"salary\"> 5000) "
operator|+
literal|"or \"salary\"> 500)"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView13()} but using alias    * and condition of query is stronger. */
annotation|@
name|Test
specifier|public
name|void
name|testAlias
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select * from \"emps\" as em where "
operator|+
literal|"(em.\"salary\"< 1111.9 and em.\"deptno\"> 10)"
operator|+
literal|"or (em.\"empid\"> 400 and em.\"salary\"> 5000)"
argument_list|,
literal|"select \"name\" as n from \"emps\" as e where "
operator|+
literal|"(e.\"empid\"> 500 and e.\"salary\"> 6000)"
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation query at same level of aggregation as aggregation    * materialization. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregate0
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select count(*) as c from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*) + 1 as c from \"emps\" group by \"empid\""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Aggregation query at same level of aggregation as aggregation    * materialization but with different row types. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregate1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select count(*) as c0 from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*) as c1 from \"emps\" group by \"empid\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" group by \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate3
parameter_list|()
block|{
name|String
name|deduplicated
init|=
literal|"(select \"empid\", \"deptno\", \"name\", \"salary\", \"commission\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", \"deptno\", \"name\", \"salary\", \"commission\")"
decl_stmt|;
name|String
name|mv
init|=
literal|"select \"deptno\", sum(\"salary\"), sum(\"commission\"), sum(\"k\")\n"
operator|+
literal|"from\n"
operator|+
literal|"  (select \"deptno\", \"salary\", \"commission\", 100 as \"k\"\n"
operator|+
literal|"  from "
operator|+
name|deduplicated
operator|+
literal|")\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|"select \"deptno\", sum(\"salary\"), sum(\"k\")\n"
operator|+
literal|"from\n"
operator|+
literal|"  (select \"deptno\", \"salary\", 100 as \"k\"\n"
operator|+
literal|"  from "
operator|+
name|deduplicated
operator|+
literal|")\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate4
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\", \"commission\", sum(\"salary\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"commission\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"commission\" = 100\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate5
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\" + \"commission\", \"commission\", sum(\"salary\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\" + \"commission\", \"commission\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"commission\", sum(\"salary\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"commission\" * (\"deptno\" + \"commission\") = 100\n"
operator|+
literal|"group by \"commission\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Matching failed because the filtering condition under Aggregate    * references columns for aggregation.    */
annotation|@
name|Test
specifier|public
name|void
name|testAggregate6
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\", sum(\"commission\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"salary\"> 1000\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|checkNoMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate7
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select 11 as \"empno\", 22 as \"sal\", count(*) from \"emps\" group by 11, 22"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from\n"
operator|+
literal|"(select 11 as \"empno\", 22 as \"sal\", count(*)\n"
operator|+
literal|"from \"emps\" group by 11, 22) tmp\n"
operator|+
literal|"where \"sal\" = 33"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableValues(tuples=[[]])"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * There will be a compensating Project added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
specifier|public
name|void
name|testCompensatingCalcWithAggregate0
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\", sum(\"commission\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * There will be a compensating Project + Filter added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
specifier|public
name|void
name|testCompensatingCalcWithAggregate1
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\", sum(\"commission\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"deptno\">=20\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * There will be a compensating Project + Filter added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
specifier|public
name|void
name|testCompensatingCalcWithAggregate2
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\", sum(\"commission\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"deptno\">= 10\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 10"
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"deptno\", sum(\"salary\") as \"sum_salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"deptno\">= 20\n"
operator|+
literal|"group by \"deptno\")\n"
operator|+
literal|"where \"sum_salary\"> 20"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation query at same level of aggregation as aggregation    * materialization with grouping sets. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateGroupSets1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation query with different grouping sets, should not    * do materialization. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateGroupSets2
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by rollup(\"empid\",\"deptno\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation query at coarser level of aggregation than aggregation    * materialization. Requires an additional aggregate to roll up. Note that    * COUNT is rolled up using SUM0. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateRollUp
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], "
operator|+
literal|"expr#3=[+($t1, $t2)], C=[$t3], deptno=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation query with groupSets at coarser level of aggregation than    * aggregation materialization. Requires an additional aggregate to roll up.    * Note that COUNT is rolled up using SUM0. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateGroupSetsRollUp
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateGroupSetsRollUp2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Aggregation materialization with a project. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateProject
parameter_list|()
block|{
comment|// Note that materialization does not start with the GROUP BY columns.
comment|// Not a smart way to design a materialization, but people may do it.
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", count(*) as c, \"empid\" + 2, sum(\"empid\") as s from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], $f0=[$t3], deptno=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], agg#0=[$SUM0($1)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3087">[CALCITE-3087]    * AggregateOnProjectToAggregateUnifyRule ignores Project incorrectly when its    * Mapping breaks ordering</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProject1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\", \"empid\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProject2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by cube(\"deptno\", \"empid\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProject3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by rollup(\"deptno\", \"empid\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProject4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"salary\", \"empid\", \"deptno\", count(*) as c, sum(\"commission\") as s from \"emps\" "
operator|+
literal|"group by \"salary\", \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by rollup(\"empid\", \"deptno\", \"salary\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..3=[{inputs}], expr#4=[1], "
operator|+
literal|"expr#5=[+($t3, $t4)], C=[$t5], deptno=[$t2])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1, 2}], groups=[[{0, 1, 2}, {1, 2}, {1}, {}]], agg#0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3448">[CALCITE-3448]    * AggregateOnCalcToAggregateUnifyRule ignores Project incorrectly when    * there's missing grouping or mapping breaks ordering</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProject5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", \"name\", count(*) from \"emps\"\n"
operator|+
literal|"group by \"empid\", \"deptno\", \"name\""
argument_list|,
literal|"select \"name\", \"empid\", count(*) from \"emps\" group by \"name\", \"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..2=[{inputs}], name=[$t1], empid=[$t0], EXPR$2=[$t2])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 2}], EXPR$2=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateOnProjectAndFilter
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\"), count(1)\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", count(1)\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"deptno\" = 10\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectOnProject
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\") + 2, sum(\"commission\")\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\") + 2\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPermutationError
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select min(\"salary\"), count(*), max(\"salary\"), sum(\"salary\"), \"empid\" "
operator|+
literal|"from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*), \"empid\" from \"emps\" group by \"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnLeftProjectToJoin
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\"), sum(\"commission\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\", count(\"name\")\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\", count(\"name\")\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnRightProjectToJoin
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\"), sum(\"commission\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\", count(\"name\")\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\"), sum(\"commission\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\"\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnProjectsToJoin
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\"), sum(\"commission\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\", count(\"name\")\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\")\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"A\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  (select \"deptno\"\n"
operator|+
literal|"  from \"depts\"\n"
operator|+
literal|"  group by \"deptno\") \"B\"\n"
operator|+
literal|"  on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnCalcToJoin0
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join \"depts\"\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"A\".\"empid\", \"A\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|" (select \"empid\", \"deptno\" from \"emps\" where \"deptno\"> 10) A"
operator|+
literal|" join \"depts\"\n"
operator|+
literal|"on \"A\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnCalcToJoin1
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join \"depts\"\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"B\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join\n"
operator|+
literal|"(select \"deptno\" from \"depts\" where \"deptno\"> 10) B\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnCalcToJoin2
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join \"depts\"\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"empid\", \"deptno\" from \"emps\" where \"empid\"> 10) A\n"
operator|+
literal|"join\n"
operator|+
literal|"(select \"deptno\" from \"depts\" where \"deptno\"> 10) B\n"
operator|+
literal|"on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnCalcToJoin3
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join \"depts\"\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"empid\", \"deptno\" + 1 as \"deptno\" from \"emps\" where \"empid\"> 10) A\n"
operator|+
literal|"join\n"
operator|+
literal|"(select \"deptno\" from \"depts\" where \"deptno\"> 10) B\n"
operator|+
literal|"on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
comment|// Match failure because join condition references non-mapping projects.
name|checkNoMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnCalcToJoin4
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\", \"depts\".\"deptno\" from\n"
operator|+
literal|"\"emps\" join \"depts\"\n"
operator|+
literal|"on \"emps\".\"deptno\" = \"depts\".\"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select * from\n"
operator|+
literal|"(select \"empid\", \"deptno\" from \"emps\" where \"empid\" is not null) A\n"
operator|+
literal|"full join\n"
operator|+
literal|"(select \"deptno\" from \"depts\" where \"deptno\" is not null) B\n"
operator|+
literal|"on \"A\".\"deptno\" = \"B\".\"deptno\""
decl_stmt|;
comment|// Match failure because of outer join type but filtering condition in Calc is not empty.
name|checkNoMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSwapJoin
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" as s join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
argument_list|,
literal|"select count(*) as c from \"foodmart\".\"time_by_day\" as t join \"foodmart\".\"sales_fact_1997\" as s on t.\"time_id\" = s.\"time_id\""
argument_list|,
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[mat, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testOrderByQueryOnProjectView
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" from \"emps\" order by \"deptno\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testOrderByQueryOnOrderByView
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\" order by \"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\" order by \"deptno\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testDifferentColumnNames
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testDifferentType
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testPartialUnion
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testNonDisjointUnion
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationReferencesTableInOtherSchema
parameter_list|()
block|{
block|}
comment|/** Unit test for logic functions    * {@link org.apache.calcite.plan.SubstitutionVisitor#mayBeSatisfiable} and    * {@link RexUtil#simplify}. */
annotation|@
name|Test
specifier|public
name|void
name|testSatisfiable
parameter_list|()
block|{
comment|// TRUE may be satisfiable
name|checkSatisfiable
argument_list|(
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// FALSE is not satisfiable
name|checkNotSatisfiable
argument_list|(
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// The expression "$0 = 1".
specifier|final
name|RexNode
name|i0_eq_0
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
comment|// "$0 = 1" may be satisfiable
name|checkSatisfiable
argument_list|(
name|i0_eq_0
argument_list|,
literal|"=($0, 0)"
argument_list|)
expr_stmt|;
comment|// "$0 = 1 AND TRUE" may be satisfiable
specifier|final
name|RexNode
name|e0
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e0
argument_list|,
literal|"=($0, 0)"
argument_list|)
expr_stmt|;
comment|// "$0 = 1 AND FALSE" is not satisfiable
specifier|final
name|RexNode
name|e1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e1
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT $0 = 0" is not satisfiable
specifier|final
name|RexNode
name|e2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e2
argument_list|)
expr_stmt|;
comment|// "TRUE AND NOT $0 = 0" may be satisfiable. Can simplify.
specifier|final
name|RexNode
name|e3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e3
argument_list|,
literal|"<>($0, 0)"
argument_list|)
expr_stmt|;
comment|// The expression "$1 = 1".
specifier|final
name|RexNode
name|i1_eq_1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
comment|// "$0 = 0 AND $1 = 1 AND NOT $0 = 0" is not satisfiable
specifier|final
name|RexNode
name|e4
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i1_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e4
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT $1 = 1" may be satisfiable. Can't simplify.
specifier|final
name|RexNode
name|e5
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i1_eq_1
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e5
argument_list|,
literal|"AND(=($0, 0),<>($1, 1))"
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND NOT ($0 = 0 AND $1 = 1)" may be satisfiable. Can simplify.
specifier|final
name|RexNode
name|e6
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|i1_eq_1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e6
argument_list|,
literal|"AND(=($0, 0), OR(<>($0, 0),<>($1, 1)))"
argument_list|)
expr_stmt|;
comment|// "$0 = 0 AND ($1 = 1 AND NOT ($0 = 0))" is not satisfiable.
specifier|final
name|RexNode
name|e7
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i1_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i0_eq_0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkNotSatisfiable
argument_list|(
name|e7
argument_list|)
expr_stmt|;
comment|// The expression "$2".
specifier|final
name|RexInputRef
name|i2
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// The expression "$3".
specifier|final
name|RexInputRef
name|i3
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// The expression "$4".
specifier|final
name|RexInputRef
name|i4
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|typeFactory
operator|.
name|createType
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// "$0 = 0 AND $2 AND $3 AND NOT ($2 AND $3 AND $4) AND NOT ($2 AND $4)" may
comment|// be satisfiable. Can't simplify.
specifier|final
name|RexNode
name|e8
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i0_eq_0
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i2
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i3
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|i2
argument_list|,
name|i3
argument_list|,
name|i4
argument_list|)
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|i4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSatisfiable
argument_list|(
name|e8
argument_list|,
literal|"AND(=($0, 0), $2, $3, OR(NOT($2), NOT($3), NOT($4)), NOT($4))"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkNotSatisfiable
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|SubstitutionVisitor
operator|.
name|mayBeSatisfiable
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|simple
init|=
name|simplify
operator|.
name|simplifyUnknownAsFalse
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|RexLiteral
operator|.
name|booleanValue
argument_list|(
name|simple
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkSatisfiable
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|SubstitutionVisitor
operator|.
name|mayBeSatisfiable
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|simple
init|=
name|simplify
operator|.
name|simplifyUnknownAsFalse
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|simple
operator|.
name|toStringRaw
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSplitFilter
parameter_list|()
block|{
specifier|final
name|RexLiteral
name|i1
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
decl_stmt|;
specifier|final
name|RexLiteral
name|i2
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexLiteral
name|i3
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intType
init|=
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RexInputRef
name|x
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|intType
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// $0
specifier|final
name|RexInputRef
name|y
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|intType
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// $1
specifier|final
name|RexInputRef
name|z
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|intType
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// $2
specifier|final
name|RexNode
name|x_eq_1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|x
argument_list|,
name|i1
argument_list|)
decl_stmt|;
comment|// $0 = 1
specifier|final
name|RexNode
name|x_eq_1_b
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|i1
argument_list|,
name|x
argument_list|)
decl_stmt|;
comment|// 1 = $0
specifier|final
name|RexNode
name|x_eq_2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|x
argument_list|,
name|i2
argument_list|)
decl_stmt|;
comment|// $0 = 2
specifier|final
name|RexNode
name|y_eq_2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|y
argument_list|,
name|i2
argument_list|)
decl_stmt|;
comment|// $1 = 2
specifier|final
name|RexNode
name|z_eq_3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|z
argument_list|,
name|i3
argument_list|)
decl_stmt|;
comment|// $2 = 3
name|RexNode
name|newFilter
decl_stmt|;
comment|// Example 1.
comment|//   condition: x = 1 or y = 2
comment|//   target:    y = 2 or 1 = x
comment|// yields
comment|//   residue:   true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|y_eq_2
argument_list|,
name|x_eq_1_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|isAlwaysTrue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Example 2.
comment|//   condition: x = 1,
comment|//   target:    x = 1 or z = 3
comment|// yields
comment|//   residue:   x = 1
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|z_eq_3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"=($0, 1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2b.
comment|//   condition: x = 1 or y = 2
comment|//   target:    x = 1 or y = 2 or z = 3
comment|// yields
comment|//   residue:   x = 1 or y = 2
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|,
name|z_eq_3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"OR(=($0, 1), =($1, 2))"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2c.
comment|//   condition: x = 1
comment|//   target:    x = 1 or y = 2 or z = 3
comment|// yields
comment|//   residue:   x = 1
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|,
name|z_eq_3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"=($0, 1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2d.
comment|//   condition: x = 1 or y = 2
comment|//   target:    y = 2 or x = 1
comment|// yields
comment|//   residue:   true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|y_eq_2
argument_list|,
name|x_eq_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|isAlwaysTrue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2e.
comment|//   condition: x = 1
comment|//   target:    x = 1 (different object)
comment|// yields
comment|//   residue:   true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|x_eq_1_b
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|isAlwaysTrue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2f.
comment|//   condition: x = 1 or y = 2
comment|//   target:    x = 1
comment|// yields
comment|//   residue:   null
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|x_eq_1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
comment|// Example 3.
comment|// Condition [x = 1 and y = 2],
comment|// target [y = 2 and x = 1] yields
comment|// residue [true].
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|y_eq_2
argument_list|,
name|x_eq_1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|isAlwaysTrue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Example 4.
comment|//   condition: x = 1 and y = 2
comment|//   target:    y = 2
comment|// yields
comment|//   residue:   x = 1
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|,
name|y_eq_2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newFilter
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"=($0, 1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Example 5.
comment|//   condition: x = 1
comment|//   target:    x = 1 and y = 2
comment|// yields
comment|//   residue:   null
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
comment|// Example 6.
comment|//   condition: x = 1
comment|//   target:    y = 2
comment|// yields
comment|//   residue:   null
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|y_eq_2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
comment|// Example 7.
comment|//   condition: x = 1
comment|//   target:    x = 2
comment|// yields
comment|//   residue:   null
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_eq_1
argument_list|,
name|x_eq_2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|newFilter
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a complicated star-join query on a complicated materialized    * star-join query. Some of the features:    *    *<ol>    *<li>query joins in different order;    *<li>query's join conditions are in where clause;    *<li>query does not use all join tables (safe to omit them because they are    *    many-to-mandatory-one joins);    *<li>query is at higher granularity, therefore needs to roll up;    *<li>query has a condition on one of the materialization's grouping columns.    *</ol>    */
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testFilterGroupQueryOnStar
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select p.\"product_name\", t.\"the_year\",\n"
operator|+
literal|"  sum(f.\"unit_sales\") as \"sum_unit_sales\", count(*) as \"c\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as f\n"
operator|+
literal|"join (\n"
operator|+
literal|"    select \"time_id\", \"the_year\", \"the_month\"\n"
operator|+
literal|"    from \"foodmart\".\"time_by_day\") as t\n"
operator|+
literal|"  on f.\"time_id\" = t.\"time_id\"\n"
operator|+
literal|"join \"foodmart\".\"product\" as p\n"
operator|+
literal|"  on f.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"join \"foodmart\".\"product_class\" as pc"
operator|+
literal|"  on p.\"product_class_id\" = pc.\"product_class_id\"\n"
operator|+
literal|"group by t.\"the_year\",\n"
operator|+
literal|" t.\"the_month\",\n"
operator|+
literal|" pc.\"product_department\",\n"
operator|+
literal|" pc.\"product_category\",\n"
operator|+
literal|" p.\"product_name\""
argument_list|,
literal|"select t.\"the_month\", count(*) as x\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select \"time_id\", \"the_year\", \"the_month\"\n"
operator|+
literal|"  from \"foodmart\".\"time_by_day\") as t,\n"
operator|+
literal|" \"foodmart\".\"sales_fact_1997\" as f\n"
operator|+
literal|"where t.\"the_year\" = 1997\n"
operator|+
literal|"and t.\"time_id\" = f.\"time_id\"\n"
operator|+
literal|"group by t.\"the_year\",\n"
operator|+
literal|" t.\"the_month\"\n"
argument_list|,
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|)
expr_stmt|;
block|}
comment|/** Simpler than {@link #testFilterGroupQueryOnStar()}, tests a query on a    * materialization that is just a join. */
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testQueryOnStar
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as f\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t on f.\"time_id\" = t.\"time_id\"\n"
operator|+
literal|"join \"foodmart\".\"product\" as p on f.\"product_id\" = p.\"product_id\"\n"
operator|+
literal|"join \"foodmart\".\"product_class\" as pc on p.\"product_class_id\" = pc.\"product_class_id\"\n"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|q
argument_list|,
name|q
operator|+
literal|"where t.\"month_of_year\" = 10"
argument_list|,
name|JdbcTest
operator|.
name|FOODMART_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|)
expr_stmt|;
block|}
comment|/** A materialization that is a join of a union cannot at present be converted    * to a star table and therefore cannot be recognized. This test checks that    * nothing unpleasant happens. */
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testJoinOnUnionMaterialization
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" union all select * from \"emps\")\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
decl_stmt|;
name|checkNoMaterialize
argument_list|(
name|q
argument_list|,
name|q
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"empid\"< 300)\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
decl_stmt|;
name|checkMaterialize
argument_list|(
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-891">[CALCITE-891]    * TableScan without Project cannot be substituted by any projected    * materialization</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization2
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
decl_stmt|;
specifier|final
name|String
name|m
init|=
literal|"select \"deptno\", \"empid\", \"name\",\n"
operator|+
literal|"\"salary\", \"commission\" from \"emps\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization3
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\" = 1"
decl_stmt|;
specifier|final
name|String
name|m
init|=
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionAll
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select * from \"emps\" where \"empid\"> 300\n"
operator|+
literal|"union all select * from \"emps\" where \"empid\"< 200"
decl_stmt|;
name|String
name|m
init|=
literal|"select * from \"emps\" where \"empid\"< 500"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs3
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs5
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" where \"deptno\" = 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs6
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" where \"deptno\"> 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[10], expr#3=[<($t2, $t1)], "
operator|+
literal|"proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs7
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" where \"deptno\"> 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\"< 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs8
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationNoAggregateFuncs9
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\"\n"
operator|+
literal|"where \"salary\"> 1000 group by \"name\", \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"where \"salary\"> 2000 group by \"name\", \"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs3
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", \"empid\", sum(\"empid\") as s, count(*) as c\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], "
operator|+
literal|"S=[$t3], C=[$t2])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)],"
operator|+
literal|" deptno=[$t0], S=[$t3])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs6
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") + 2 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs7
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" + 1, sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t0, $t2)], "
operator|+
literal|"expr#4=[+($t1, $t2)], EXPR$0=[$t3], S=[$t4])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs8
parameter_list|()
block|{
comment|// TODO: It should work, but top project in the query is not matched by the planner.
comment|// It needs further checking.
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\" + 1, count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" + 1, sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs9
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs10
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs11
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to minute), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to minute)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs12
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to month), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs13
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", cast('1997-01-20 12:34:56' as timestamp), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", cast('1997-01-20 12:34:56' as timestamp)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs14
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to hour), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to hour)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs15
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"eventid\", floor(cast(\"ts\" as timestamp) to second), count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to second)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to minute), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to minute)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs16
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"eventid\", cast(\"ts\" as timestamp), count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", cast(\"ts\" as timestamp)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to year), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to year)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs17
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"eventid\", floor(cast(\"ts\" as timestamp) to month), count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to month)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to hour), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to hour)"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, events]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs18
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\"*\"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\"*\"deptno\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationAggregateFuncs19
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\" + 10, count(*) + 1 as c\n"
operator|+
literal|"from \"emps\" group by \"empid\" + 10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 20\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[>($t1, $t2)], "
operator|+
literal|"empid=[$t0], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"empid\" from \"depts\"\n"
operator|+
literal|"join \"emps\" using (\"deptno\") where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 20\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t0)], "
operator|+
literal|"empid=[$t1], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs3
parameter_list|()
block|{
comment|// It does not match, Project on top of query
name|checkNoMaterialize
argument_list|(
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 20\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"emps\".\"deptno\"> 10\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"depts\".\"deptno\"> 20\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t1)], "
operator|+
literal|"empid=[$t0], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"emps\".\"empid\" from \"depts\"\n"
operator|+
literal|"join \"emps\" using (\"deptno\") where \"emps\".\"empid\"> 10\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"emps\".\"empid\""
argument_list|,
literal|"select \"depts\".\"deptno\" from \"depts\"\n"
operator|+
literal|"join \"emps\" using (\"deptno\") where \"emps\".\"empid\"> 15\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"emps\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[>($t1, $t2)], "
operator|+
literal|"deptno=[$t0], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs6
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"emps\".\"empid\" from \"depts\"\n"
operator|+
literal|"join \"emps\" using (\"deptno\") where \"emps\".\"empid\"> 10\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"emps\".\"empid\""
argument_list|,
literal|"select \"depts\".\"deptno\" from \"depts\"\n"
operator|+
literal|"join \"emps\" using (\"deptno\") where \"emps\".\"empid\"> 15\n"
operator|+
literal|"group by \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[<($t2, $t1)], "
operator|+
literal|"proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs7
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 11\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|"expr#5=[10], expr#6=[>($t0, $t5)], expr#7=[11], expr#8=[>=($t7, $t0)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs8
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 20\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10 and \"depts\".\"deptno\"< 20\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs9
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 11 and \"depts\".\"deptno\"< 19\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10 and \"depts\".\"deptno\"< 20\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|"expr#13=[OR($t10, $t12)], expr#14=[AND($t6, $t8, $t13)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs10
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"name\", \"dependents\".\"name\" as \"name2\", "
operator|+
literal|"\"emps\".\"deptno\", \"depts\".\"deptno\" as \"deptno2\", "
operator|+
literal|"\"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\", \"dependents\", \"emps\"\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"depts\".\"name\", \"dependents\".\"name\", "
operator|+
literal|"\"emps\".\"deptno\", \"depts\".\"deptno\", "
operator|+
literal|"\"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{4}])\n"
operator|+
literal|"  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[=($t2, $t3)], "
operator|+
literal|"expr#6=[CAST($t1):VARCHAR], "
operator|+
literal|"expr#7=[CAST($t0):VARCHAR], "
operator|+
literal|"expr#8=[=($t6, $t7)], expr#9=[AND($t5, $t8)], proj#0..4=[{exprs}], $condition=[$t9])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs1
parameter_list|()
block|{
comment|// This test relies on FK-UK relationship
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"emps\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"group by \"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"group by \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs3
parameter_list|()
block|{
comment|// This test relies on FK-UK relationship
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"deptno\", \"empid\", sum(\"empid\") as s, count(*) as c\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], "
operator|+
literal|"S=[$t3], C=[$t2])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"emps\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where \"emps\".\"deptno\">= 10 group by \"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"depts\".\"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where \"emps\".\"deptno\"> 10 group by \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\">= 10 group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"depts\".\"deptno\", sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10 group by \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], "
operator|+
literal|"deptno=[$t0], S=[$t3])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs6
parameter_list|()
block|{
comment|// This rewriting would be possible if planner generates a pre-aggregation,
comment|// since the materialized view would match the sub-query.
comment|// Initial investigation after enabling AggregateJoinTransposeRule.EXTENDED
comment|// shows that the rewriting with pre-aggregations is generated and the
comment|// materialized view rewriting happens.
comment|// However, we end up discarding the plan with the materialized view and still
comment|// using the plan with the pre-aggregations.
comment|// TODO: Explore and extend to choose best rewriting.
specifier|final
name|String
name|m
init|=
literal|"select \"depts\".\"name\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"group by \"depts\".\"name\""
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select \"dependents\".\"empid\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"group by \"dependents\".\"empid\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs7
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"dependents\".\"empid\", \"emps\".\"deptno\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"dependents\".\"empid\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{4}], S=[$SUM0($6)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..6=[{inputs}], expr#7=[=($t5, $t0)], proj#0..6=[{exprs}], $condition=[$t7])\n"
operator|+
literal|"    EnumerableNestedLoopJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs8
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"dependents\".\"empid\", \"emps\".\"deptno\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"depts\".\"name\", sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"depts\".\"name\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{1}], S=[$SUM0($6)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..6=[{inputs}], expr#7=[=($t5, $t0)], proj#0..6=[{exprs}], $condition=[$t7])\n"
operator|+
literal|"    EnumerableNestedLoopJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts]])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs9
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"emps\".\"deptno\", count(distinct \"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], deptno=[$t1], S=[$t2])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs10
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"emps\".\"deptno\", count(distinct \"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"emps\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs11
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\", count(\"emps\".\"salary\") as s\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 11 and \"depts\".\"deptno\"< 19\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\", count(\"emps\".\"salary\") + 1\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10 and \"depts\".\"deptno\"< 20\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"PLAN=EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], "
operator|+
literal|"empid=[$t0], EXPR$1=[$t3])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], agg#0=[$SUM0($1)])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}], agg#0=[COUNT()])"
argument_list|,
literal|"EnumerableAggregate(group=[{1}], agg#0=[$SUM0($2)])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|"expr#13=[OR($t10, $t12)], expr#14=[AND($t6, $t8, $t13)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs12
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\", count(distinct \"emps\".\"salary\") as s\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 11 and \"depts\".\"deptno\"< 19\n"
operator|+
literal|"group by \"depts\".\"deptno\", \"dependents\".\"empid\""
argument_list|,
literal|"select \"dependents\".\"empid\", count(distinct \"emps\".\"salary\") + 1\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10 and \"depts\".\"deptno\"< 20\n"
operator|+
literal|"group by \"dependents\".\"empid\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs13
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"emps\".\"deptno\", count(\"salary\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n"
operator|+
literal|"group by \"dependents\".\"empid\", \"emps\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinAggregateMaterializationAggregateFuncs14
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"emps\".\"name\", \"emps\".\"deptno\", \"depts\".\"name\", "
operator|+
literal|"count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where (\"depts\".\"name\" is not null and \"emps\".\"name\" = 'a') or "
operator|+
literal|"(\"depts\".\"name\" is not null and \"emps\".\"name\" = 'b')\n"
operator|+
literal|"group by \"empid\", \"emps\".\"name\", \"depts\".\"name\", \"emps\".\"deptno\""
argument_list|,
literal|"select \"depts\".\"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where \"depts\".\"name\" is not null and \"emps\".\"name\" = 'a'\n"
operator|+
literal|"group by \"depts\".\"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):INTEGER NOT NULL], expr#2=[1], "
operator|+
literal|"expr#3=[=($t1, $t2)], deptno=[$t0], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select cast(\"empid\" as BIGINT) from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\"> 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], "
operator|+
literal|"expr#2=[1], expr#3=[>($t1, $t2)], EXPR$0=[$t1], $condition=[$t3])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization6
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select cast(\"empid\" as BIGINT) from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], "
operator|+
literal|"expr#2=[CAST($t1):INTEGER NOT NULL], expr#3=[1], expr#4=[=($t2, $t3)], "
operator|+
literal|"EXPR$0=[$t1], $condition=[$t4])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization7
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], empid=[$t1])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):VARCHAR], name00=[$t1])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, m0]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t1):VARCHAR], empid=[$t0], name0=[$t2])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, dependents]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization8
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], empid=[$t0])\n"
operator|+
literal|"  EnumerableNestedLoopJoin(condition=[=(CAST($1):VARCHAR, CAST($2):VARCHAR)], joinType=[inner])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, dependents]])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization9
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization10
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 30"
argument_list|,
literal|"select \"dependents\".\"empid\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n"
operator|+
literal|"where \"depts\".\"deptno\"> 10"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|"expr#5=[10], expr#6=[>($t0, $t5)], expr#7=[30], expr#8=[>=($t7, $t0)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization11
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"where \"deptno\" in (select \"deptno\" from \"depts\")"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"PLAN=EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterialization12
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\", \"emps\".\"name\", \"emps\".\"deptno\", \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where (\"depts\".\"name\" is not null and \"emps\".\"name\" = 'a') or "
operator|+
literal|"(\"depts\".\"name\" is not null and \"emps\".\"name\" = 'b') or "
operator|+
literal|"(\"depts\".\"name\" is not null and \"emps\".\"name\" = 'c')"
argument_list|,
literal|"select \"depts\".\"deptno\", \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"where (\"depts\".\"name\" is not null and \"emps\".\"name\" = 'a') or "
operator|+
literal|"(\"depts\".\"name\" is not null and \"emps\".\"name\" = 'b')"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CONTAINS_M0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK1
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"a\".\"empid\" \"deptno\" from\n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
argument_list|,
literal|"select \"a\".\"empid\" from \n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"PLAN=EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK2
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"a\".\"empid\", \"a\".\"deptno\" from\n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
argument_list|,
literal|"select \"a\".\"empid\" from \n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK3
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"a\".\"empid\", \"a\".\"deptno\" from\n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
argument_list|,
literal|"select \"a\".\"name\" from \n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1) \"a\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK4
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"empid\" \"deptno\" from\n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1)\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" from \"emps\" where \"empid\" = 1\n"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"PLAN=EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK5
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
literal|"select \"emps\".\"empid\" from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid0=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK6
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n"
operator|+
literal|"join \"depts\" \"b\" on (\"emps\".\"deptno\"=\"b\".\"deptno\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
literal|"select \"emps\".\"empid\" from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid0=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK7
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" \"a\" on (\"emps\".\"name\"=\"a\".\"name\")\n"
operator|+
literal|"join \"depts\" \"b\" on (\"emps\".\"name\"=\"b\".\"name\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
literal|"select \"emps\".\"empid\" from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK8
parameter_list|()
block|{
name|checkNoMaterialize
argument_list|(
literal|"select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n"
operator|+
literal|"join \"depts\" \"b\" on (\"emps\".\"name\"=\"b\".\"name\")\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
literal|"select \"emps\".\"empid\" from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")\n"
operator|+
literal|"where \"emps\".\"empid\" = 1"
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinMaterializationUKFK9
parameter_list|()
block|{
name|checkMaterialize
argument_list|(
literal|"select * from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
argument_list|,
literal|"select \"emps\".\"empid\", \"dependents\".\"empid\", \"emps\".\"deptno\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"dependents\" using (\"empid\")"
operator|+
literal|"join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n"
operator|+
literal|"where \"emps\".\"name\" = 'Bill'"
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testViewMaterialization
parameter_list|()
block|{
name|checkThatMaterialize
argument_list|(
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
argument_list|,
literal|"matview"
argument_list|,
literal|true
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableValues(tuples=[[{ 'noname' }]])"
argument_list|)
argument_list|,
name|RuleSets
operator|.
name|ofList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"noname"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubQuery
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select \"empid\", \"deptno\", \"salary\" from \"emps\" e1\n"
operator|+
literal|"where \"empid\" = (\n"
operator|+
literal|"  select max(\"empid\") from \"emps\"\n"
operator|+
literal|"  where \"deptno\" = e1.\"deptno\")"
decl_stmt|;
specifier|final
name|String
name|m
init|=
literal|"select \"empid\", \"deptno\" from \"emps\"\n"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableModify
parameter_list|()
block|{
specifier|final
name|String
name|m
init|=
literal|"select \"deptno\", \"empid\", \"name\""
operator|+
literal|"from \"emps\" where \"deptno\" = 10"
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|"upsert into \"dependents\""
operator|+
literal|"select \"empid\" + 1 as x, \"name\""
operator|+
literal|"from \"emps\" where \"deptno\" = 10"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|substitutedNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
name|m
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|SUB
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelNode
argument_list|>
operator|)
name|r
lambda|->
name|substitutedNames
operator|.
name|add
argument_list|(
operator|new
name|TableNameVisitor
argument_list|()
operator|.
name|run
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"hr, m0"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Table "dependents" not modifiable.
block|}
name|assertThat
argument_list|(
name|substitutedNames
argument_list|,
name|is
argument_list|(
name|list3
argument_list|(
operator|new
name|String
index|[]
index|[]
index|[]
block|{
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-761">[CALCITE-761]    * Pre-populated materializations</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testPrePopulated
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select distinct \"deptno\" from \"emps\""
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
name|builder
lambda|->
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|builder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"table"
argument_list|,
literal|"locations"
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
literal|"select distinct `deptno` as `empid`, '' as `name`\n"
operator|+
literal|"from `emps`"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|"`"
argument_list|,
literal|"\""
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"sql"
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|map
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|""
argument_list|,
name|CONTAINS_LOCATIONS
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testViewSchemaPath
parameter_list|()
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
specifier|final
name|String
name|m
init|=
literal|"select empno, deptno from emp"
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select deptno from scott.emp"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"SCOTT"
argument_list|)
decl_stmt|;
specifier|final
name|JsonBuilder
name|builder
init|=
operator|new
name|JsonBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'hr',\n"
operator|+
literal|"  schemas: [\n"
operator|+
name|JdbcTest
operator|.
name|SCOTT_SCHEMA
operator|+
literal|"  ,\n"
operator|+
literal|"    {\n"
operator|+
literal|"      materializations: [\n"
operator|+
literal|"        {\n"
operator|+
literal|"          table: 'm0',\n"
operator|+
literal|"          view: 'm0v',\n"
operator|+
literal|"          sql: "
operator|+
name|builder
operator|.
name|toJsonString
argument_list|(
name|m
argument_list|)
operator|+
literal|",\n"
operator|+
literal|"          viewSchemaPath: "
operator|+
name|builder
operator|.
name|toJsonString
argument_list|(
name|path
argument_list|)
operator|+
literal|"        }\n"
operator|+
literal|"      ],\n"
operator|+
literal|"      type: 'custom',\n"
operator|+
literal|"      name: 'hr',\n"
operator|+
literal|"      factory: 'org.apache.calcite.adapter.java.ReflectiveSchema$Factory',\n"
operator|+
literal|"      operand: {\n"
operator|+
literal|"        class: 'org.apache.calcite.test.JdbcTest$HrSchema'\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}\n"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|model
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|""
argument_list|,
name|CONTAINS_M0
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleMaterializationMultiUsage
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"empid\"< 300)\n"
operator|+
literal|"join (select * from \"emps\" where \"empid\"< 200) using (\"empid\")"
decl_stmt|;
name|String
name|m
init|=
literal|"select * from \"emps\" where \"empid\"< 500"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|m
argument_list|,
name|q
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiMaterializationMultiUsage
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"empid\"< 300)\n"
operator|+
literal|"join (select \"deptno\", count(*) as c from \"emps\" group by \"deptno\") using (\"deptno\")"
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" group by \"deptno\""
argument_list|,
literal|"m1"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m1]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationOnJoinQuery
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\"< 300 "
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Disabled
argument_list|(
literal|"Creating mv for depts considering all its column throws exception"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testMultiMaterializationOnJoinQuery
parameter_list|()
block|{
specifier|final
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\"< 300 "
operator|+
literal|"and \"depts\".\"deptno\"> 200"
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|,
literal|"m1"
argument_list|,
literal|"select * from \"depts\" where \"deptno\"> 100"
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m0]])"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, m1]])"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationOnCountDistinctQuery1
parameter_list|()
block|{
comment|// The column empid is already unique, thus DISTINCT is not
comment|// in the COUNT of the resulting rewriting
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\", \"salary\""
argument_list|,
literal|"select \"deptno\", count(distinct \"empid\") as c from (\n"
operator|+
literal|"select \"deptno\", \"empid\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\")\n"
operator|+
literal|"group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}], C=[COUNT($1)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationOnCountDistinctQuery2
parameter_list|()
block|{
comment|// The column empid is already unique, thus DISTINCT is not
comment|// in the COUNT of the resulting rewriting
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"salary\", \"empid\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"salary\", \"empid\""
argument_list|,
literal|"select \"deptno\", count(distinct \"empid\") as c from (\n"
operator|+
literal|"select \"deptno\", \"empid\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\")\n"
operator|+
literal|"group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}], C=[COUNT($2)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, m0]]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationOnCountDistinctQuery3
parameter_list|()
block|{
comment|// The column salary is not unique, thus we end up with
comment|// a different rewriting
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"empid\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\", \"salary\""
argument_list|,
literal|"select \"deptno\", count(distinct \"salary\") from (\n"
operator|+
literal|"select \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"salary\")\n"
operator|+
literal|"group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}], EXPR$1=[COUNT($1)])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 2}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateMaterializationOnCountDistinctQuery4
parameter_list|()
block|{
comment|// Although there is no DISTINCT in the COUNT, this is
comment|// equivalent to previous query
name|checkMaterialize
argument_list|(
literal|"select \"deptno\", \"salary\", \"empid\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"salary\", \"empid\""
argument_list|,
literal|"select \"deptno\", count(\"salary\") from (\n"
operator|+
literal|"select \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"salary\")\n"
operator|+
literal|"group by \"deptno\""
argument_list|,
name|HR_FKUK_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}], EXPR$1=[COUNT()])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, m0]]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationSubstitution
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"empid\"< 300)\n"
operator|+
literal|"join (select * from \"emps\" where \"empid\"< 200) using (\"empid\")"
decl_stmt|;
specifier|final
name|String
index|[]
index|[]
index|[]
name|expectedNames
init|=
block|{
block|{
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|}
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|substitutedNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 300"
argument_list|,
literal|"m1"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 600"
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|SUB
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelNode
argument_list|>
operator|)
name|r
lambda|->
name|substitutedNames
operator|.
name|add
argument_list|(
operator|new
name|TableNameVisitor
argument_list|()
operator|.
name|run
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
name|substitutedNames
operator|.
name|sort
argument_list|(
name|CASE_INSENSITIVE_LIST_LIST_COMPARATOR
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|substitutedNames
argument_list|,
name|is
argument_list|(
name|list3
argument_list|(
name|expectedNames
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationSubstitution2
parameter_list|()
block|{
name|String
name|q
init|=
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"empid\"< 300)\n"
operator|+
literal|"join (select * from \"emps\" where \"empid\"< 200) using (\"empid\")"
decl_stmt|;
specifier|final
name|String
index|[]
index|[]
index|[]
name|expectedNames
init|=
block|{
block|{
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"emps"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m0"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m1"
block|}
block|}
block|,
block|{
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|,
block|{
literal|"hr"
block|,
literal|"m2"
block|}
block|}
block|}
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|substitutedNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withMaterializations
argument_list|(
name|HR_FKUK_MODEL
argument_list|,
literal|"m0"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 300"
argument_list|,
literal|"m1"
argument_list|,
literal|"select * from \"emps\" where \"empid\"< 600"
argument_list|,
literal|"m2"
argument_list|,
literal|"select * from \"m1\""
argument_list|)
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|SUB
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelNode
argument_list|>
operator|)
name|r
lambda|->
name|substitutedNames
operator|.
name|add
argument_list|(
operator|new
name|TableNameVisitor
argument_list|()
operator|.
name|run
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
name|substitutedNames
operator|.
name|sort
argument_list|(
name|CASE_INSENSITIVE_LIST_LIST_COMPARATOR
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|substitutedNames
argument_list|,
name|is
argument_list|(
name|list3
argument_list|(
name|expectedNames
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaterializationAfterTrimingOfUnusedFields
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"y\".\"deptno\", \"y\".\"name\", \"x\".\"sum_salary\"\n"
operator|+
literal|"from\n"
operator|+
literal|"  (select \"deptno\", sum(\"salary\") \"sum_salary\"\n"
operator|+
literal|"  from \"emps\"\n"
operator|+
literal|"  group by \"deptno\") \"x\"\n"
operator|+
literal|"  join\n"
operator|+
literal|"  \"depts\" \"y\"\n"
operator|+
literal|"  on \"x\".\"deptno\"=\"y\".\"deptno\"\n"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|sql
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionAllToUnionAll
parameter_list|()
block|{
name|String
name|sql0
init|=
literal|"select * from \"emps\" where \"empid\"< 300"
decl_stmt|;
name|String
name|sql1
init|=
literal|"select * from \"emps\" where \"empid\"> 200"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|sql0
operator|+
literal|" union all "
operator|+
name|sql1
argument_list|,
name|sql1
operator|+
literal|" union all "
operator|+
name|sql0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionDistinctToUnionDistinct
parameter_list|()
block|{
name|String
name|sql0
init|=
literal|"select * from \"emps\" where \"empid\"< 300"
decl_stmt|;
name|String
name|sql1
init|=
literal|"select * from \"emps\" where \"empid\"> 200"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|sql0
operator|+
literal|" union "
operator|+
name|sql1
argument_list|,
name|sql1
operator|+
literal|" union "
operator|+
name|sql0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionDistinctToUnionAll
parameter_list|()
block|{
name|String
name|sql0
init|=
literal|"select * from \"emps\" where \"empid\"< 300"
decl_stmt|;
name|String
name|sql1
init|=
literal|"select * from \"emps\" where \"empid\"> 200"
decl_stmt|;
name|checkNoMaterialize
argument_list|(
name|sql0
operator|+
literal|" union "
operator|+
name|sql1
argument_list|,
name|sql0
operator|+
literal|" union all "
operator|+
name|sql1
argument_list|,
name|HR_FKUK_MODEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionOnCalcsToUnion
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"> 300\n"
operator|+
literal|"union all\n"
operator|+
literal|"select \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"< 100"
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", \"salary\" * 2\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"> 300 and \"salary\"> 100\n"
operator|+
literal|"union all\n"
operator|+
literal|"select \"deptno\", \"salary\" * 2\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"< 100 and \"salary\"> 100"
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntersectToIntersect0
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\" from \"emps\"\n"
operator|+
literal|"intersect\n"
operator|+
literal|"select \"deptno\" from \"depts\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\" from \"depts\"\n"
operator|+
literal|"intersect\n"
operator|+
literal|"select \"deptno\" from \"emps\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntersectToIntersect1
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\" from \"emps\"\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"select \"deptno\" from \"depts\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\" from \"depts\"\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"select \"deptno\" from \"emps\""
decl_stmt|;
name|checkMaterialize
argument_list|(
name|mv
argument_list|,
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|list3
parameter_list|(
name|E
index|[]
index|[]
index|[]
name|as
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|List
argument_list|<
name|List
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|E
index|[]
index|[]
name|a
range|:
name|as
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|list2
argument_list|(
name|a
argument_list|)
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
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|List
argument_list|<
name|E
argument_list|>
argument_list|>
name|list2
parameter_list|(
name|E
index|[]
index|[]
name|as
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|List
argument_list|<
name|E
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|E
index|[]
name|a
range|:
name|as
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|a
argument_list|)
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
comment|/**    * Implementation of RelVisitor to extract substituted table names.    */
specifier|private
specifier|static
class|class
name|TableNameVisitor
extends|extends
name|RelVisitor
block|{
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|run
parameter_list|(
name|RelNode
name|input
parameter_list|)
block|{
name|go
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|node
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|TableScan
condition|)
block|{
name|RelOptTable
name|table
init|=
name|node
operator|.
name|getTable
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|qName
init|=
name|table
operator|.
name|getQualifiedName
argument_list|()
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
name|qName
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|,
name|ordinal
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Hr schema with FK-UK relationship.    */
specifier|public
specifier|static
class|class
name|HrFKUKSchema
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HrFKUKSchema"
return|;
block|}
specifier|public
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|10
argument_list|,
literal|"Bill"
argument_list|,
literal|10000
argument_list|,
literal|1000
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|200
argument_list|,
literal|20
argument_list|,
literal|"Eric"
argument_list|,
literal|8000
argument_list|,
literal|500
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|150
argument_list|,
literal|10
argument_list|,
literal|"Sebastian"
argument_list|,
literal|7000
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|10
argument_list|,
literal|"Theodore"
argument_list|,
literal|10000
argument_list|,
literal|250
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Department
index|[]
name|depts
init|=
block|{
operator|new
name|Department
argument_list|(
literal|10
argument_list|,
literal|"Sales"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|0
index|]
argument_list|,
name|emps
index|[
literal|2
index|]
argument_list|,
name|emps
index|[
literal|3
index|]
argument_list|)
argument_list|,
operator|new
name|Location
argument_list|(
operator|-
literal|122
argument_list|,
literal|38
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|30
argument_list|,
literal|"Marketing"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|Location
argument_list|(
literal|0
argument_list|,
literal|52
argument_list|)
argument_list|)
block|,
operator|new
name|Department
argument_list|(
literal|20
argument_list|,
literal|"HR"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|emps
index|[
literal|1
index|]
argument_list|)
argument_list|,
literal|null
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Dependent
index|[]
name|dependents
init|=
block|{
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"Michael"
argument_list|)
block|,
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"Jane"
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Dependent
index|[]
name|locations
init|=
block|{
operator|new
name|Dependent
argument_list|(
literal|10
argument_list|,
literal|"San Francisco"
argument_list|)
block|,
operator|new
name|Dependent
argument_list|(
literal|20
argument_list|,
literal|"San Diego"
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|Event
index|[]
name|events
init|=
block|{
operator|new
name|Event
argument_list|(
literal|100
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|Event
argument_list|(
literal|200
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|Event
argument_list|(
literal|150
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|Event
argument_list|(
literal|110
argument_list|,
literal|null
argument_list|)
block|,     }
decl_stmt|;
specifier|public
specifier|final
name|RelReferentialConstraint
name|rcs0
init|=
name|RelReferentialConstraintImpl
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"hr"
argument_list|,
literal|"emps"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"hr"
argument_list|,
literal|"depts"
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|IntPair
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|QueryableTable
name|foo
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|Smalls
operator|.
name|generateStrings
argument_list|(
name|count
argument_list|)
return|;
block|}
specifier|public
name|TranslatableTable
name|view
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|Smalls
operator|.
name|view
argument_list|(
name|s
argument_list|)
return|;
block|}
specifier|public
name|TranslatableTable
name|matview
parameter_list|()
block|{
return|return
name|Smalls
operator|.
name|strView
argument_list|(
literal|"noname"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

