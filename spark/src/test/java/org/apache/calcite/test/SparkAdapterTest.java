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
name|spark
operator|.
name|SparkRel
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

begin_comment
comment|/**  * Tests for using Calcite with Spark as an internal engine, as implemented by  * the {@link org.apache.calcite.adapter.spark} package.  */
end_comment

begin_class
specifier|public
class|class
name|SparkAdapterTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VALUES0
init|=
literal|"(values (1, 'a'), (2, 'b'))"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALUES1
init|=
literal|"(values (1, 'a'), (2, 'b')) as t(x, y)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALUES2
init|=
literal|"(values (1, 'a'), (2, 'b'), (1, 'b'), (2, 'c'), (2, 'c')) as t(x, y)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALUES3
init|=
literal|"(values (1, 'a'), (2, 'b')) as v(w, z)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VALUES4
init|=
literal|"(values (1, 'a'), (2, 'b'), (3, 'b'), (4, 'c'), (2, 'c')) as t(x, y)"
decl_stmt|;
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SPARK
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/**    * Tests a VALUES query evaluated using Spark.    * There are no data sources.    */
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
block|{
comment|// Insert a spurious reference to a class in Calcite's Spark adapter.
comment|// Otherwise this test doesn't depend on the Spark module at all, and
comment|// Javadoc gets confused.
name|Util
operator|.
name|discard
argument_list|(
name|SparkRel
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES0
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"EXPR$0=1; EXPR$1=a\n"
operator|+
literal|"EXPR$0=2; EXPR$1=b\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|/** Tests values followed by filter, evaluated by Spark. */
annotation|@
name|Test
specifier|public
name|void
name|testValuesFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x< 2"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[2], expr#3=[<($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableAggregate(group=[{0, 1}])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests about grouping and aggregate functions
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(x) as SUM_X, min(y) as MIN_Y, max(y) as MAX_Y, "
operator|+
literal|"count(*) as CNT_Y, count(distinct y) as CNT_DIST_Y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"group by x"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..5=[{inputs}], expr#6=[CAST($t1):INTEGER NOT NULL], expr#7=[CAST($t2):CHAR(1) NOT NULL], expr#8=[CAST($t3):CHAR(1) NOT NULL], expr#9=[CAST($t4):BIGINT NOT NULL], SUM_X=[$t6], MIN_Y=[$t7], MAX_Y=[$t8], CNT_Y=[$t9], CNT_DIST_Y=[$t5])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], SUM_X=[MIN($2) FILTER $7], MIN_Y=[MIN($3) FILTER $7], MAX_Y=[MIN($4) FILTER $7], CNT_Y=[MIN($5) FILTER $7], CNT_DIST_Y=[COUNT($1) FILTER $6])\n"
operator|+
literal|"    EnumerableCalc(expr#0..6=[{inputs}], expr#7=[0], expr#8=[=($t6, $t7)], expr#9=[1], expr#10=[=($t6, $t9)], proj#0..5=[{exprs}], $g_0=[$t8], $g_1=[$t10])\n"
operator|+
literal|"      EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}]], SUM_X=[$SUM0($0)], MIN_Y=[MIN($1)], MAX_Y=[MAX($1)], CNT_Y=[COUNT()], $g=[GROUPING($0, $1)])\n"
operator|+
literal|"        EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"SUM_X=2; MIN_Y=a; MAX_Y=b; CNT_Y=2; CNT_DIST_Y=2\n"
operator|+
literal|"SUM_X=6; MIN_Y=b; MAX_Y=c; CNT_Y=3; CNT_DIST_Y=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggFuncNoGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(x) as SUM_X, min(y) as MIN_Y, max(y) as MAX_Y, "
operator|+
literal|"count(*) as CNT_Y, count(distinct y) as CNT_DIST_Y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], expr#5=[CAST($t3):BIGINT NOT NULL], proj#0..2=[{exprs}], CNT_Y=[$t5], CNT_DIST_Y=[$t4])\n"
operator|+
literal|"  EnumerableAggregate(group=[{}], SUM_X=[MIN($1) FILTER $6], MIN_Y=[MIN($2) FILTER $6], MAX_Y=[MIN($3) FILTER $6], CNT_Y=[MIN($4) FILTER $6], CNT_DIST_Y=[COUNT($0) FILTER $5])\n"
operator|+
literal|"    EnumerableCalc(expr#0..5=[{inputs}], expr#6=[0], expr#7=[=($t5, $t6)], expr#8=[1], expr#9=[=($t5, $t8)], proj#0..4=[{exprs}], $g_0=[$t7], $g_1=[$t9])\n"
operator|+
literal|"      EnumerableAggregate(group=[{1}], groups=[[{1}, {}]], SUM_X=[$SUM0($0)], MIN_Y=[MIN($1)], MAX_Y=[MAX($1)], CNT_Y=[COUNT()], $g=[GROUPING($1)])\n"
operator|+
literal|"        EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"SUM_X=8; MIN_Y=a; MAX_Y=c; CNT_Y=5; CNT_DIST_Y=3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOrderByAsc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, count(*) as CNT_Y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"group by x\n"
operator|+
literal|"order by x asc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; CNT_Y=2\n"
operator|+
literal|"X=2; CNT_Y=3\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMinMaxCountCountDistinctOrderByAsc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, min(y) as MIN_Y, max(y) as MAX_Y, count(*) as CNT_Y, "
operator|+
literal|"count(distinct y) as CNT_DIST_Y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"group by x\n"
operator|+
literal|"order by x asc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], expr#5=[CAST($t1):CHAR(1) NOT NULL], expr#6=[CAST($t2):CHAR(1) NOT NULL], expr#7=[CAST($t3):BIGINT NOT NULL], X=[$t0], MIN_Y=[$t5], MAX_Y=[$t6], CNT_Y=[$t7], CNT_DIST_Y=[$t4])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0}], MIN_Y=[MIN($2) FILTER $6], MAX_Y=[MIN($3) FILTER $6], CNT_Y=[MIN($4) FILTER $6], CNT_DIST_Y=[COUNT($1) FILTER $5])\n"
operator|+
literal|"      EnumerableCalc(expr#0..5=[{inputs}], expr#6=[0], expr#7=[=($t5, $t6)], expr#8=[1], expr#9=[=($t5, $t8)], proj#0..4=[{exprs}], $g_0=[$t7], $g_1=[$t9])\n"
operator|+
literal|"        EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}]], MIN_Y=[MIN($1)], MAX_Y=[MAX($1)], CNT_Y=[COUNT()], $g=[GROUPING($0, $1)])\n"
operator|+
literal|"          EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; MIN_Y=a; MAX_Y=b; CNT_Y=2; CNT_DIST_Y=2\n"
operator|+
literal|"X=2; MIN_Y=b; MAX_Y=c; CNT_Y=3; CNT_DIST_Y=2\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMiMaxCountCountDistinctOrderByDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, min(y) as MIN_Y, max(y) as MAX_Y, count(*) as CNT_Y, "
operator|+
literal|"count(distinct y) as CNT_DIST_Y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"group by x\n"
operator|+
literal|"order by x desc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], expr#5=[CAST($t1):CHAR(1) NOT NULL], expr#6=[CAST($t2):CHAR(1) NOT NULL], expr#7=[CAST($t3):BIGINT NOT NULL], X=[$t0], MIN_Y=[$t5], MAX_Y=[$t6], CNT_Y=[$t7], CNT_DIST_Y=[$t4])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[DESC])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0}], MIN_Y=[MIN($2) FILTER $6], MAX_Y=[MIN($3) FILTER $6], CNT_Y=[MIN($4) FILTER $6], CNT_DIST_Y=[COUNT($1) FILTER $5])\n"
operator|+
literal|"      EnumerableCalc(expr#0..5=[{inputs}], expr#6=[0], expr#7=[=($t5, $t6)], expr#8=[1], expr#9=[=($t5, $t8)], proj#0..4=[{exprs}], $g_0=[$t7], $g_1=[$t9])\n"
operator|+
literal|"        EnumerableAggregate(group=[{0, 1}], groups=[[{0, 1}, {0}]], MIN_Y=[MIN($1)], MAX_Y=[MAX($1)], CNT_Y=[COUNT()], $g=[GROUPING($0, $1)])\n"
operator|+
literal|"          EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2; MIN_Y=b; MAX_Y=c; CNT_Y=3; CNT_DIST_Y=2\n"
operator|+
literal|"X=1; MIN_Y=a; MAX_Y=b; CNT_Y=2; CNT_DIST_Y=2\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByHaving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"group by x\n"
operator|+
literal|"having count(*)> 2"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[2], expr#3=[>($t1, $t2)], X=[$t0], $condition=[$t3])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], agg#0=[COUNT()])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests about set operators (UNION, UNION ALL, INTERSECT)
annotation|@
name|Test
specifier|public
name|void
name|testUnionAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|" union all\n"
operator|+
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableUnion(all=[true])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|" union\n"
operator|+
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableUnion(all=[false])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntersect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|" intersect\n"
operator|+
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableIntersect(all=[false])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=2; Y=b"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests about sorting
annotation|@
name|Test
specifier|public
name|void
name|testSortXAscProjectY
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by x asc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], Y=[$t1], X=[$t0])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Y=a\n"
operator|+
literal|"Y=b\n"
operator|+
literal|"Y=b\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=c\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortXDescYDescProjectY
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by x desc, y desc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableSort(sort0=[$1], sort1=[$0], dir0=[DESC], dir1=[DESC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], Y=[$t1], X=[$t0])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Y=c\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=b\n"
operator|+
literal|"Y=b\n"
operator|+
literal|"Y=a\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortXDescYAscProjectY
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by x desc, y"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableSort(sort0=[$1], sort1=[$0], dir0=[DESC], dir1=[ASC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], Y=[$t1], X=[$t0])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Y=b\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=a\n"
operator|+
literal|"Y=b\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortXAscYDescProjectY
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by x, y desc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableSort(sort0=[$1], sort1=[$0], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], Y=[$t1], X=[$t0])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Y=b\n"
operator|+
literal|"Y=a\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=c\n"
operator|+
literal|"Y=b\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests involving joins
annotation|@
name|Test
specifier|public
name|void
name|testJoinProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select t.y, v.z\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"  join "
operator|+
name|VALUES3
operator|+
literal|" on t.x = v.w"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], Y=[$t3], Z=[$t1])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Y=a; Z=a\n"
operator|+
literal|"Y=b; Z=a\n"
operator|+
literal|"Y=b; Z=b\n"
operator|+
literal|"Y=c; Z=b\n"
operator|+
literal|"Y=c; Z=b"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinProjectAliasProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select r.z\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"    join "
operator|+
name|VALUES3
operator|+
literal|" on t.x = v.w) as r"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], Z=[$t1])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"Z=a\n"
operator|+
literal|"Z=a\n"
operator|+
literal|"Z=b\n"
operator|+
literal|"Z=b\n"
operator|+
literal|"Z=b"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests involving LIMIT/OFFSET
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x = 1\n"
operator|+
literal|"limit 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableLimit(fetch=[1])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[=($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by y\n"
operator|+
literal|"limit 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableLimit(fetch=[1])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOffset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"order by y\n"
operator|+
literal|"offset 2"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableLimit(offset=[2])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests involving "complex" filters in WHERE clause
annotation|@
name|Test
specifier|public
name|void
name|testFilterBetween
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES4
operator|+
literal|"\n"
operator|+
literal|"where x between 3 and 4"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[3], expr#3=[>=($t0, $t2)], expr#4=[4], expr#5=[<=($t0, $t4)], expr#6=[AND($t3, $t5)], proj#0..1=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 3, 'b' }, { 4, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=3; Y=b\n"
operator|+
literal|"X=4; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterIsIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES4
operator|+
literal|"\n"
operator|+
literal|"where x in (3, 4)"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[3], expr#3=[=($t0, $t2)], expr#4=[4], expr#5=[=($t0, $t4)], expr#6=[OR($t3, $t5)], proj#0..1=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 3, 'b' }, { 4, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=3; Y=b\n"
operator|+
literal|"X=4; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterTrue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where true"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterFalse
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where false"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[false], proj#0..1=[{exprs}], $condition=[$t2])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterOr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x = 1 or x = 2"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[=($t0, $t2)], expr#4=[2], expr#5=[=($t0, $t4)], expr#6=[OR($t3, $t5)], proj#0..1=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterIsNotNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x is not null"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a\n"
operator|+
literal|"X=1; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterIsNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x is null"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[IS NULL($t0)], proj#0..1=[{exprs}], $condition=[$t2])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests on more complex queries as UNION operands
annotation|@
name|Test
specifier|public
name|void
name|testUnionWithFilters
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x> 1\n"
operator|+
literal|" union all\n"
operator|+
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x> 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableUnion(all=[true])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[>($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[>($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=2; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionWithFiltersProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x> 1\n"
operator|+
literal|" union\n"
operator|+
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|"where x> 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableUnion(all=[false])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[>($t0, $t2)], X=[$t0], $condition=[$t3])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[>($t0, $t2)], X=[$t0], $condition=[$t3])\n"
operator|+
literal|"    EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }, { 1, 'b' }, { 2, 'c' }, { 2, 'c' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests involving arithmetic operators
annotation|@
name|Test
specifier|public
name|void
name|testArithmeticPlus
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x + 1> 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t0, $t2)], expr#4=[>($t3, $t2)], X=[$t0], $condition=[$t4])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1\n"
operator|+
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArithmeticMinus
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x - 1> 0"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[-($t0, $t2)], expr#4=[0], expr#5=[>($t3, $t4)], X=[$t0], $condition=[$t5])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArithmeticMul
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x * x> 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[*($t0, $t0)], expr#3=[1], expr#4=[>($t2, $t3)], X=[$t0], $condition=[$t4])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArithmeticDiv
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x / x = 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[/($t0, $t0)], expr#3=[1], expr#4=[=($t2, $t3)], X=[$t0], $condition=[$t4])\n"
operator|+
literal|"  EnumerableValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1\n"
operator|+
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
comment|// Tests involving sub-queries (both correlated and non correlated)
annotation|@
name|Disabled
argument_list|(
literal|"[CALCITE-2184] ClassCastException: RexSubQuery cannot be cast to RexLocalRef"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFilterExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES4
operator|+
literal|"\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from "
operator|+
name|VALUES3
operator|+
literal|"\n"
operator|+
literal|"  where w< x\n"
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=todo\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2; Y=b\n"
operator|+
literal|"X=2; Y=c\n"
operator|+
literal|"X=3; Y=b\n"
operator|+
literal|"X=4; Y=c"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"[CALCITE-2184] ClassCastException: RexSubQuery cannot be cast to RexLocalRef"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFilterNotExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from "
operator|+
name|VALUES4
operator|+
literal|"\n"
operator|+
literal|"where not exists (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from "
operator|+
name|VALUES3
operator|+
literal|"\n"
operator|+
literal|"  where w> x\n"
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=todo\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1; Y=a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"[CALCITE-2184] ClassCastException: RexSubQuery cannot be cast to RexLocalRef"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryAny
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x<= any (\n"
operator|+
literal|"  select x\n"
operator|+
literal|"  from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=todo\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=1\n"
operator|+
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"[CALCITE-2184] ClassCastException: RexSubQuery cannot be cast to RexLocalRef"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testSubQueryAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x\n"
operator|+
literal|"from "
operator|+
name|VALUES1
operator|+
literal|"\n"
operator|+
literal|"where x<= all (\n"
operator|+
literal|"  select x\n"
operator|+
literal|"  from "
operator|+
name|VALUES2
operator|+
literal|"\n"
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=todo\n\n"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
literal|"X=2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
name|expectedResult
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

