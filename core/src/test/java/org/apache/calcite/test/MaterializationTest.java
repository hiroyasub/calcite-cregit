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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|junit
operator|.
name|Assert
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
name|Assert
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
name|Assert
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
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Unit test for the materialized view rewrite mechanism. Each test has a  * query and one or more materializations (what Oracle calls materialized views)  * and checks that the materialization is used.  */
end_comment

begin_class
specifier|public
class|class
name|MaterializationTest
block|{
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
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
name|JdbcTest
operator|.
name|HR_MODEL
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
name|testFilterQueryOnProjectView
parameter_list|()
block|{
try|try
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|JdbcTest
operator|.
name|HR_MODEL
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
finally|finally
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|false
argument_list|)
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
name|CONTAINS_M0
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
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
name|explainChecker
parameter_list|)
block|{
try|try
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
operator|.
name|explainMatches
argument_list|(
literal|""
argument_list|,
name|explainChecker
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
try|try
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, emps]])"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Prepare
operator|.
name|THREAD_TRIM
operator|.
name|set
argument_list|(
literal|false
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
name|JdbcTest
operator|.
name|HR_MODEL
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
name|JdbcTest
operator|.
name|HR_MODEL
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
name|JdbcTest
operator|.
name|HR_MODEL
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
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
name|Ignore
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
name|Ignore
argument_list|(
literal|"not implemented"
argument_list|)
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCalcRel(expr#0..2=[{inputs}], expr#3=[1], "
operator|+
literal|"expr#4=[+($t1, $t3)], X=[$t4], name=[$t2], condition=?)\n"
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
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view */
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
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view */
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
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query and columns selected are subset of columns in materialized view    * Condition here is complex*/
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
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is stronger in    * query. However, columns selected are not present in columns of materialized view,    * hence should not use materialized view*/
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query.*/
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition currently    * has unsupported type being checked on query.    */
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view    * Condition here is complex*/
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
name|JdbcTest
operator|.
name|HR_MODEL
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView()} but condition is weaker in    * query and columns selected are subset of columns in materialized view    * Condition here is complex*/
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterQueryOnFilterView13()} but using alias    * and condition of query is stronger*/
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
name|testAggregate
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
comment|/** Aggregation query at coarser level of aggregation than aggregation    * materialization. Requires an additional aggregate to roll up. Note that    * COUNT is rolled up using SUM. */
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
name|JdbcTest
operator|.
name|HR_MODEL
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
comment|/** Aggregation materialization with a project. */
annotation|@
name|Ignore
argument_list|(
literal|"work in progress"
argument_list|)
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
name|JdbcTest
operator|.
name|HR_MODEL
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testSwapJoin
parameter_list|()
block|{
name|String
name|q1
init|=
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" as s join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
decl_stmt|;
name|String
name|q2
init|=
literal|"select count(*) as c from \"foodmart\".\"time_by_day\" as t join \"foodmart\".\"sales_fact_1997\" as s on t.\"time_id\" = s.\"time_id\""
decl_stmt|;
block|}
annotation|@
name|Ignore
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
name|Ignore
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
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testDifferentColumnNames
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testDifferentType
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPartialUnion
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testNonDisjointUnion
parameter_list|()
block|{
block|}
annotation|@
name|Ignore
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
literal|"NOT(=($0, 0))"
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
literal|"AND(=($0, 0), NOT(=($1, 1)))"
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
literal|"AND(=($0, 0), NOT(AND(=($0, 0), =($1, 1))))"
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
literal|"AND(=($0, 0), $2, $3, NOT(AND($2, $3, $4)), NOT($4))"
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
name|RexUtil
operator|.
name|simplify
argument_list|(
name|rexBuilder
argument_list|,
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
name|RexUtil
operator|.
name|simplify
argument_list|(
name|rexBuilder
argument_list|,
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
name|x
argument_list|,
name|i1
argument_list|)
decl_stmt|;
comment|// $0 = 1 again
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
comment|// TODO:
comment|// Example 2.
comment|//   condition: x = 1,
comment|//   target:    x = 1 or z = 3
comment|// yields
comment|//   residue:   not (z = 3)
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|rexBuilder
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
literal|"NOT(=($2, 3))"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2b.
comment|//   condition: x = 1 or y = 2
comment|//   target:    x = 1 or y = 2 or z = 3
comment|// yields
comment|//   residue:   not (z = 3)
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|rexBuilder
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
literal|"NOT(=($2, 3))"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2c.
comment|//   condition: x = 1
comment|//   target:    x = 1 or y = 2 or z = 3
comment|// yields
comment|//   residue:   not (y = 2) and not (z = 3)
name|newFilter
operator|=
name|SubstitutionVisitor
operator|.
name|splitFilter
argument_list|(
name|rexBuilder
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
literal|"AND(NOT(=($1, 2)), NOT(=($2, 3)))"
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
name|rexBuilder
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
name|rexBuilder
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
comment|// TODO:
comment|// Example 3.
comment|// Condition [x = 1 and y = 2],
comment|// target [y = 2 and x = 1] yields
comment|// residue [true].
comment|// TODO:
comment|// Example 4.
comment|// TODO:
block|}
comment|/** Tests a complicated star-join query on a complicated materialized    * star-join query. Some of the features:    *    * 1. query joins in different order;    * 2. query's join conditions are in where clause;    * 3. query does not use all join tables (safe to omit them because they are    *    many-to-mandatory-one joins);    * 4. query is at higher granularity, therefore needs to roll up;    * 5. query has a condition on one of the materialization's grouping columns.    */
annotation|@
name|Ignore
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
name|Ignore
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
name|Ignore
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
name|JdbcTest
operator|.
name|HR_MODEL
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
block|}
end_class

begin_comment
comment|// End MaterializationTest.java
end_comment

end_unit

