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
name|equalTo
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
comment|/**  * Unit test for SubstutionVisitor.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializedViewSubstitutionVisitorTest
extends|extends
name|AbstractMaterializedViewTest
block|{
annotation|@
name|Test
name|void
name|testFilter
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterToProject0
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *, \"empid\" * 2 from \"emps\""
argument_list|,
literal|"select * from \"emps\" where (\"empid\" * 2)> 3"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterToProject1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"salary\" from \"emps\""
argument_list|,
literal|"select \"empid\", \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\" where (\"salary\" * 0.8)> 10000"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Runs the same test as {@link #testFilterQueryOnProjectView()} but more    * concisely. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView0
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in    * materialized view. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView()} but with extra column in both    * materialized view and query. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\""
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" - 10 = 0"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but materialized view cannot    * be used because it does not contain required expression. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" + 10 = 20"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but also contains an    * expression column. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1 as ee, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\", \"empid\" + 1 as e from \"emps\" where \"deptno\" - 10 = 2"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[2], "
operator|+
literal|"expr#4=[=($t0, $t3)], name=[$t2], E=[$t1], $condition=[$t4])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]]"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Cannot materialize because "name" is not projected in the MV. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView6
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\"  from \"emps\""
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\" - 10 = 0"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnProjectView3()} but also contains an    * expression column. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView7
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\""
argument_list|,
literal|"select \"name\", \"empid\" + 2 from \"emps\" where \"deptno\" - 10 = 0"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-988">[CALCITE-988]    * FilterToProjectUnifyRule.invert(MutableRel, MutableRel, MutableProject)    * works incorrectly</a>. */
annotation|@
name|Test
name|void
name|testFilterQueryOnProjectView8
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"salary\", \"commission\",\n"
operator|+
literal|"\"deptno\", \"empid\", \"name\" from \"emps\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select *\n"
operator|+
literal|"from (select * from \"emps\" where \"name\" is null)\n"
operator|+
literal|"where \"commission\" is null"
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" "
operator|+
literal|"where \"deptno\" = 10 and \"empid\"< 150"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * view. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"name\" from \"emps\"\n"
operator|+
literal|"where \"deptno\" = 10 or \"deptno\" = 20 or \"empid\"< 160"
argument_list|,
literal|"select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[1], expr#4=[+($t1, $t3)], expr#5=[10], "
operator|+
literal|"expr#6=[CAST($t0):INTEGER NOT NULL], expr#7=[=($t5, $t6)], X=[$t4], "
operator|+
literal|"name=[$t2], $condition=[$t7])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView6
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\", \"salary\" from \"emps\" "
operator|+
literal|"where \"salary\"> 2000.5"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView7
parameter_list|()
block|{
name|sql
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
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. However, columns selected are not present in columns of materialized    * view, Hence should not use materialized view. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView8
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" where \"deptno\"> 30"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView9
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" "
operator|+
literal|"where \"deptno\"> 30 or \"empid\"> 10"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition currently    * has unsupported type being checked on query. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView10
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"deptno\"> 10 "
operator|+
literal|"and \"name\" = \'calcite\'"
argument_list|,
literal|"select \"name\", \"empid\" from \"emps\" where \"deptno\"> 30 "
operator|+
literal|"or \"empid\"> 10"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView11
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where "
operator|+
literal|"(\"salary\"< 1111.9 and \"deptno\"> 10)"
operator|+
literal|"or (\"empid\"> 400 and \"salary\"> 5000)"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition of    * query is stronger but is on the column not present in MV (salary).    */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView12
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"name\", \"deptno\" from \"emps\" where \"salary\"> 2000.5"
argument_list|,
literal|"select \"name\" from \"emps\" where \"deptno\"> 30 and \"salary\"> 3000"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view.    * Condition here is complex. */
annotation|@
name|Test
name|void
name|testFilterQueryOnFilterView13
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView7()} but columns in materialized    * view are a permutation of columns in the query. */
annotation|@
name|Test
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
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView13()} but using alias    * and condition of query is stronger. */
annotation|@
name|Test
name|void
name|testAlias
parameter_list|()
block|{
name|sql
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
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Aggregation query at same level of aggregation as aggregation    * materialization. */
annotation|@
name|Test
name|void
name|testAggregate0
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) as c from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*) + 1 as c from \"emps\" group by \"empid\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Aggregation query at same level of aggregation as aggregation    * materialization but with different row types. */
annotation|@
name|Test
name|void
name|testAggregate1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) as c0 from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*) as c1 from \"emps\" group by \"empid\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregate3
parameter_list|()
block|{
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\"), sum(\"commission\"), sum(\"k\")\n"
operator|+
literal|"from\n"
operator|+
literal|"  (select \"deptno\", \"salary\", \"commission\", 100 as \"k\"\n"
operator|+
literal|"  from \"emps\")\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", sum(\"salary\"), sum(\"k\")\n"
operator|+
literal|"from\n"
operator|+
literal|"  (select \"deptno\", \"salary\", 100 as \"k\"\n"
operator|+
literal|"  from \"emps\")\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * Matching failed because the filtering condition under Aggregate    * references columns for aggregation.    */
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/**    * There will be a compensating Project added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * There will be a compensating Project + Filter added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * There will be a compensating Project + Filter added after matching of the Aggregate.    * This rule targets to test if the Calc can be handled.    */
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Aggregation query at same level of aggregation as aggregation    * materialization with grouping sets. */
annotation|@
name|Test
name|void
name|testAggregateGroupSets1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\"\n"
operator|+
literal|"from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Aggregation query with different grouping sets, should not    * do materialization. */
annotation|@
name|Test
name|void
name|testAggregateGroupSets2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\"\n"
operator|+
literal|"from \"emps\" group by rollup(\"empid\",\"deptno\")"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** Aggregation query at coarser level of aggregation than aggregation    * materialization. Requires an additional aggregate to roll up. Note that    * COUNT is rolled up using SUM0. */
annotation|@
name|Test
name|void
name|testAggregateRollUp1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..1=[{inputs}], expr#2=[1], "
operator|+
literal|"expr#3=[+($t1, $t2)], C=[$t3], deptno=[$t0])\n"
operator|+
literal|"  LogicalAggregate(group=[{1}], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/**    * stddev_pop aggregate function does not support roll up.    */
annotation|@
name|Test
name|void
name|testAggregateRollUp2
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"empid\", stddev_pop(\"deptno\") "
operator|+
literal|"from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"empid\", stddev_pop(\"deptno\") "
operator|+
literal|"from \"emps\" "
operator|+
literal|"group by \"empid\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
comment|/** Aggregation query with groupSets at coarser level of aggregation than    * aggregation materialization. Requires an additional aggregate to roll up.    * Note that COUNT is rolled up using SUM0. */
annotation|@
name|Test
name|void
name|testAggregateGroupSetsRollUp
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c, \"deptno\"\n"
operator|+
literal|"from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateGroupSetsRollUp2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by cube(\"empid\",\"deptno\")"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3087">[CALCITE-3087]    * AggregateOnProjectToAggregateUnifyRule ignores Project incorrectly when its    * Mapping breaks ordering</a>. */
annotation|@
name|Test
name|void
name|testAggregateOnProject1
parameter_list|()
block|{
name|sql
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
name|void
name|testAggregateOnProject2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s from \"emps\" "
operator|+
literal|"group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\" from \"emps\" group by cube(\"deptno\", \"empid\")"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateOnProject3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"salary\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\"\n"
operator|+
literal|"from \"emps\" group by rollup(\"deptno\", \"empid\")"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t2, $t3)], C=[$t4], deptno=[$t1])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1}], groups=[[{0, 1}, {1}, {}]], agg#0=[$SUM0($2)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateOnProject4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"salary\", \"empid\", \"deptno\", count(*) as c, sum(\"commission\") as s\n"
operator|+
literal|"from \"emps\" group by \"salary\", \"empid\", \"deptno\""
argument_list|,
literal|"select count(*) + 1 as c,  \"deptno\"\n"
operator|+
literal|"from \"emps\" group by rollup(\"empid\", \"deptno\", \"salary\")"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..3=[{inputs}], expr#4=[1], "
operator|+
literal|"expr#5=[+($t3, $t4)], C=[$t5], deptno=[$t2])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 1, 2}], groups=[[{0, 1, 2}, {1, 2}, {1}, {}]], agg#0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3448">[CALCITE-3448]    * AggregateOnCalcToAggregateUnifyRule ignores Project incorrectly when    * there's missing grouping or mapping breaks ordering</a>. */
annotation|@
name|Test
name|void
name|testAggregateOnProject5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", \"name\", count(*) from \"emps\"\n"
operator|+
literal|"group by \"empid\", \"deptno\", \"name\""
argument_list|,
literal|"select \"name\", \"empid\", count(*) from \"emps\" group by \"name\", \"empid\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], name=[$t1], empid=[$t0], EXPR$2=[$t2])\n"
operator|+
literal|"  LogicalAggregate(group=[{0, 2}], EXPR$2=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPermutationError
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select min(\"salary\"), count(*), max(\"salary\"), sum(\"salary\"), \"empid\" "
operator|+
literal|"from \"emps\" group by \"empid\""
argument_list|,
literal|"select count(*), \"empid\" from \"emps\" group by \"empid\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-891">[CALCITE-891]    * TableScan without Project cannot be substituted by any projected    * materialization</a>. */
annotation|@
name|Test
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
name|String
name|m
init|=
literal|"select \"deptno\", \"empid\", \"name\",\n"
operator|+
literal|"\"salary\", \"commission\" from \"emps\""
decl_stmt|;
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|String
name|m
init|=
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
decl_stmt|;
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalCalc(expr#0..4=[{inputs}], expr#5=[300], expr#6=[>($t0, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"    LogicalTableScan(table=[[hr, emps]])\n"
operator|+
literal|"  LogicalCalc(expr#0..4=[{inputs}], expr#5=[200], expr#6=[<($t0, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableModify
parameter_list|()
block|{
name|String
name|m
init|=
literal|"select \"deptno\", \"empid\", \"name\""
operator|+
literal|"from \"emps\" where \"deptno\" = 10"
decl_stmt|;
name|String
name|q
init|=
literal|"upsert into \"dependents\""
operator|+
literal|"select \"empid\" + 1 as x, \"name\""
operator|+
literal|"from \"emps\" where \"deptno\" = 10"
decl_stmt|;
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..9=[{inputs}], proj#0..4=[{exprs}], deptno0=[$t6], name0=[$t7], salary0=[$t8], commission0=[$t9])\n"
operator|+
literal|"  LogicalJoin(condition=[=($0, $5)], joinType=[inner])\n"
operator|+
literal|"    LogicalCalc(expr#0..4=[{inputs}], expr#5=[300], expr#6=[<($t0, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"    LogicalCalc(expr#0..4=[{inputs}], expr#5=[200], expr#6=[<($t0, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaterializationOnJoinQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from \"emps\" where \"empid\"< 500"
argument_list|,
literal|"select *\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\"< 300 "
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|sql
argument_list|,
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
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
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
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
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectOnCalcsToIntersect
parameter_list|()
block|{
specifier|final
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
literal|"intersect all\n"
operator|+
literal|"select \"deptno\", \"salary\"\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"< 100"
decl_stmt|;
specifier|final
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
literal|"intersect all\n"
operator|+
literal|"select \"deptno\", \"salary\" * 2\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"where \"empid\"< 100 and \"salary\"> 100"
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
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
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectToCalcOnIntersect
parameter_list|()
block|{
specifier|final
name|String
name|intersect
init|=
literal|""
operator|+
literal|"select \"deptno\",\"name\" from \"emps\"\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"select \"deptno\",\"name\" from \"depts\""
decl_stmt|;
specifier|final
name|String
name|mv
init|=
literal|"select \"name\", \"deptno\" from ("
operator|+
name|intersect
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\",\"deptno\" from \"depts\"\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"select \"name\",\"deptno\" from \"emps\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConstantFilterInAgg
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\") as cnt\n"
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select count(distinct \"deptno\") as cnt\n"
operator|+
literal|"from \"emps\" where \"name\" = 'hello'"
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..1=[{inputs}], expr#2=['hello':VARCHAR], expr#3=[CAST($t0)"
operator|+
literal|":VARCHAR], expr#4=[=($t2, $t3)], CNT=[$t1], $condition=[$t4])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConstantFilterInAgg2
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|" group by \"name\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\" where \"name\" = 'hello'\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=['hello':VARCHAR], expr#4=[CAST($t0)"
operator|+
literal|":VARCHAR], expr#5=[=($t3, $t4)], deptno=[$t1], CNT=[$t2], $condition=[$t5])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConstantFilterInAgg3
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|" group by \"name\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\" where \"name\" = 'hello' and \"deptno\" = 1\n"
operator|+
literal|"group by \"deptno\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"LogicalCalc(expr#0..2=[{inputs}], expr#3=['hello':VARCHAR], expr#4=[CAST($t0)"
operator|+
literal|":VARCHAR], expr#5=[=($t3, $t4)], expr#6=[1], expr#7=[CAST($t1):INTEGER NOT NULL], "
operator|+
literal|"expr#8=[=($t6, $t7)], expr#9=[AND($t5, $t8)], deptno=[$t1], CNT=[$t2], "
operator|+
literal|"$condition=[$t9])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConstantFilterInAgg4
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|" group by \"name\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"deptno\", \"commission\", count(distinct \"commission\") as cnt\n"
operator|+
literal|"from \"emps\" where \"name\" = 'hello' and \"deptno\" = 1\n"
operator|+
literal|"group by \"deptno\", \"commission\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConstantFilterInAggUsingSubquery
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\") as cnt "
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select cnt from(\n"
operator|+
literal|" select \"name\", count(distinct \"deptno\") as cnt "
operator|+
literal|" from \"emps\" group by \"name\") t\n"
operator|+
literal|"where \"name\" = 'hello'"
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Unit test for logic functions    * {@link org.apache.calcite.plan.SubstitutionVisitor#mayBeSatisfiable} and    * {@link RexUtil#simplify}. */
annotation|@
name|Test
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
literal|"AND(=($0, 0),<>($1, 1))"
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
specifier|final
name|RexNode
name|x_plus_y_gt
init|=
comment|// x + y> 2
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
argument_list|,
name|i2
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|y_plus_x_gt
init|=
comment|// y + x> 2
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|y
argument_list|,
name|x
argument_list|)
argument_list|,
name|i2
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|x_times_y_gt
init|=
comment|// x*y> 2
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
argument_list|,
name|i2
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|y_times_x_gt
init|=
comment|// 2< y*x
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|i2
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|,
name|y
argument_list|,
name|x
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|x_plus_x_gt
init|=
comment|// x + x> 2
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
argument_list|,
name|i2
argument_list|)
decl_stmt|;
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
name|toString
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
name|toString
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
name|toString
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
name|toString
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
comment|// Example 8.
comment|//   condition: x + y> 2
comment|//   target:    y + x> 2
comment|// yields
comment|//   residue:  true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_plus_y_gt
argument_list|,
name|y_plus_x_gt
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
comment|// Example 9.
comment|//   condition: x + x> 2
comment|//   target:    x + x> 2
comment|// yields
comment|//   residue:  true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_plus_x_gt
argument_list|,
name|x_plus_x_gt
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
comment|// Example 10.
comment|//   condition: x * y> 2
comment|//   target:    2< y * x
comment|// yields
comment|//   residue:  true
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|simplify
argument_list|,
name|x_times_y_gt
argument_list|,
name|y_times_x_gt
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
block|}
annotation|@
name|Test
name|void
name|testSubQuery
parameter_list|()
block|{
specifier|final
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
name|sql
argument_list|(
name|m
argument_list|,
name|q
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a complicated star-join query on a complicated materialized    * star-join query. Some of the features:    *    *<ol>    *<li>query joins in different order;    *<li>query's join conditions are in where clause;    *<li>query does not use all join tables (safe to omit them because they are    *    many-to-mandatory-one joins);    *<li>query is at higher granularity, therefore needs to roll up;    *<li>query has a condition on one of the materialization's grouping columns.    *</ol>    */
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testFilterGroupQueryOnStar
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withDefaultSchemaSpec
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Simpler than {@link #testFilterGroupQueryOnStar()}, tests a query on a    * materialization that is just a join. */
annotation|@
name|Disabled
annotation|@
name|Test
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
name|sql
argument_list|(
name|q
argument_list|,
name|q
operator|+
literal|"where t.\"month_of_year\" = 10"
argument_list|)
operator|.
name|withDefaultSchemaSpec
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** A materialization that is a join of a union cannot at present be converted    * to a star table and therefore cannot be recognized. This test checks that    * nothing unpleasant happens. */
annotation|@
name|Disabled
annotation|@
name|Test
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
name|sql
argument_list|(
name|q
argument_list|,
name|q
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testDifferentColumnNames
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testDifferentType
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testPartialUnion
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testNonDisjointUnion
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testMaterializationReferencesTableInOtherSchema
parameter_list|()
block|{
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testOrderByQueryOnProjectView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\""
argument_list|,
literal|"select \"empid\" from \"emps\" order by \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testOrderByQueryOnOrderByView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\" from \"emps\" order by \"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\" order by \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryDistinctColumnInTargetGroupByList0
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"commission\", \"deptno\"\n"
operator|+
literal|"from \"emps\" group by \"name\", \"commission\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\", \"commission\", count(distinct \"deptno\") as cnt\n"
operator|+
literal|"from \"emps\" group by \"name\", \"commission\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryDistinctColumnInTargetGroupByList1
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\" "
operator|+
literal|"from \"emps\" group by \"name\", \"deptno\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\")\n"
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryDistinctColumnInTargetGroupByList2
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", \"empid\"\n"
operator|+
literal|"from \"emps\" group by \"name\", \"deptno\", \"empid\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\"), count(distinct \"empid\")\n"
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryDistinctColumnInTargetGroupByList3
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", \"empid\", count(\"commission\")\n"
operator|+
literal|"from \"emps\" group by \"name\", \"deptno\", \"empid\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\"), count(distinct \"empid\"), count"
operator|+
literal|"(\"commission\")\n"
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryDistinctColumnInTargetGroupByList4
parameter_list|()
block|{
specifier|final
name|String
name|mv
init|=
literal|""
operator|+
literal|"select \"name\", \"deptno\", \"empid\"\n"
operator|+
literal|"from \"emps\" group by \"name\", \"deptno\", \"empid\""
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|""
operator|+
literal|"select \"name\", count(distinct \"deptno\")\n"
operator|+
literal|"from \"emps\" group by \"name\""
decl_stmt|;
name|sql
argument_list|(
name|mv
argument_list|,
name|query
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
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
specifier|protected
name|List
argument_list|<
name|RelNode
argument_list|>
name|optimize
parameter_list|(
name|TestConfig
name|testConfig
parameter_list|)
block|{
name|RelNode
name|queryRel
init|=
name|testConfig
operator|.
name|queryRel
decl_stmt|;
name|RelOptMaterialization
name|materialization
init|=
name|testConfig
operator|.
name|materializations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|substitutes
init|=
operator|new
name|SubstitutionVisitor
argument_list|(
name|canonicalize
argument_list|(
name|materialization
operator|.
name|queryRel
argument_list|)
argument_list|,
name|canonicalize
argument_list|(
name|queryRel
argument_list|)
argument_list|)
operator|.
name|go
argument_list|(
name|materialization
operator|.
name|tableRel
argument_list|)
decl_stmt|;
return|return
name|substitutes
return|;
block|}
specifier|private
name|RelNode
name|canonicalize
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|HepProgram
name|program
init|=
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_PROJECT_TRANSPOSE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_MERGE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_INTO_JOIN
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|JOIN_CONDITION_PUSH
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_AGGREGATE_TRANSPOSE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_MERGE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_REMOVE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_JOIN_TRANSPOSE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_SET_OP_TRANSPOSE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_TO_CALC
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_CALC
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|FILTER_CALC_MERGE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_CALC_MERGE
argument_list|)
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|CALC_MERGE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
decl_stmt|;
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
block|}
end_class

end_unit

