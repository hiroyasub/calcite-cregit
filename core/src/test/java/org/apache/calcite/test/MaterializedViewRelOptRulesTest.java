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
name|tools
operator|.
name|Programs
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Unit test for  * {@link org.apache.calcite.rel.rules.materialize.MaterializedViewRule} and its  * sub-classes, in which materialized views are matched to the structure of a  * plan.  */
end_comment

begin_class
class|class
name|MaterializedViewRelOptRulesTest
extends|extends
name|AbstractMaterializedViewTest
block|{
annotation|@
name|Test
name|void
name|testSwapJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
argument_list|,
literal|"select count(*) as c from \"foodmart\".\"time_by_day\" as t"
operator|+
literal|" join \"foodmart\".\"sales_fact_1997\" as s on t.\"time_id\" = s.\"time_id\""
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
comment|/** Aggregation materialization with a project. */
annotation|@
name|Test
name|void
name|testAggregateProject
parameter_list|()
block|{
comment|// Note that materialization does not start with the GROUP BY columns.
comment|// Not a smart way to design a materialization, but people may do it.
name|sql
argument_list|(
literal|"select \"deptno\", count(*) as c, \"empid\" + 2, sum(\"empid\") as s "
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
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], C=[$t3], deptno=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], agg#0=[$SUM0($1)])\n"
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
name|testAggregateMaterializationNoAggregateFuncs1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationNoAggregateFuncs2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}])\n"
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
name|testAggregateMaterializationNoAggregateFuncs3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|,
literal|"select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationNoAggregateFuncs4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\"\n"
operator|+
literal|"from \"emps\" where \"deptno\" = 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}])\n"
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
name|testAggregateMaterializationNoAggregateFuncs5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\"\n"
operator|+
literal|"from \"emps\" where \"deptno\" = 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationNoAggregateFuncs6
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\"\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[10], expr#3=[<($t2, $t1)], proj#0..1=[{exprs}], $condition=[$t3])\n"
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
name|testAggregateMaterializationNoAggregateFuncs7
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\"\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 5 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" where \"deptno\"< 10 group by \"deptno\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationNoAggregateFuncs8
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\" from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationNoAggregateFuncs9
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\" from \"emps\"\n"
operator|+
literal|"where \"salary\"> 1000 group by \"name\", \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"where \"salary\"> 2000 group by \"name\", \"empid\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs1
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}])\n"
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
name|testAggregateMaterializationAggregateFuncs2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n"
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
name|testAggregateMaterializationAggregateFuncs3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", \"empid\", sum(\"empid\") as s, count(*) as c\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], S=[$t3], C=[$t2])\n"
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
name|testAggregateMaterializationAggregateFuncs4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
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
name|testAggregateMaterializationAggregateFuncs5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
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
name|testAggregateMaterializationAggregateFuncs6
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") + 2 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\", sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs7
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" + 1, sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
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
literal|"      EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
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
name|testAggregateMaterializationAggregateFuncs8
parameter_list|()
block|{
comment|// TODO: It should work, but top project in the query is not matched by the planner.
comment|// It needs further checking.
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\" + 1, count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" where \"deptno\">= 10 group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"deptno\" + 1, sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" where \"deptno\"> 10 group by \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs9
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs10
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") + 1 as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs11
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to minute), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to minute)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs12
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to second)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to month), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs13
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", cast('1997-01-20 12:34:56' as timestamp), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", cast('1997-01-20 12:34:56' as timestamp)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to year), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs14
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month), "
operator|+
literal|"count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"empid\", floor(cast('1997-01-20 12:34:56' as timestamp) to month)"
argument_list|,
literal|"select floor(cast('1997-01-20 12:34:56' as timestamp) to hour), sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by floor(cast('1997-01-20 12:34:56' as timestamp) to hour)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs15
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"eventid\", floor(cast(\"ts\" as timestamp) to second), "
operator|+
literal|"count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to second)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to minute), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to minute)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs16
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"eventid\", cast(\"ts\" as timestamp), count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", cast(\"ts\" as timestamp)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to year), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs17
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"eventid\", floor(cast(\"ts\" as timestamp) to month), "
operator|+
literal|"count(*) + 1 as c, sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to month)"
argument_list|,
literal|"select floor(cast(\"ts\" as timestamp) to hour), sum(\"eventid\") as s\n"
operator|+
literal|"from \"events\" group by floor(cast(\"ts\" as timestamp) to hour)"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableTableScan(table=[[hr, events]])"
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
name|testAggregateMaterializationAggregateFuncs18
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\"*\"deptno\", sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\"*\"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs19
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" group by \"empid\", \"deptno\""
argument_list|,
literal|"select \"empid\" + 10, count(*) + 1 as c\n"
operator|+
literal|"from \"emps\" group by \"empid\" + 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMaterializationAggregateFuncs20
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 11 as \"empno\", 22 as \"sal\", count(*) from \"emps\" group by 11, 22"
argument_list|,
literal|"select * from\n"
operator|+
literal|"(select 11 as \"empno\", 22 as \"sal\", count(*)\n"
operator|+
literal|"from \"emps\" group by 11, 22) tmp\n"
operator|+
literal|"where \"sal\" = 33"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableValues(tuples=[[]])"
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
name|testJoinAggregateMaterializationNoAggregateFuncs1
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t1)], "
operator|+
literal|"empid=[$t0], $condition=[$t3])\n"
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
name|testJoinAggregateMaterializationNoAggregateFuncs2
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t0)], "
operator|+
literal|"empid=[$t1], $condition=[$t3])\n"
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
name|testJoinAggregateMaterializationNoAggregateFuncs3
parameter_list|()
block|{
comment|// It does not match, Project on top of query
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs4
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t1)], "
operator|+
literal|"empid=[$t0], $condition=[$t3])\n"
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
name|testJoinAggregateMaterializationNoAggregateFuncs5
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[<($t2, $t1)], "
operator|+
literal|"deptno=[$t0], $condition=[$t3])\n"
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
name|testJoinAggregateMaterializationNoAggregateFuncs6
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[<($t2, $t1)], "
operator|+
literal|"proj#0..1=[{exprs}], $condition=[$t3])\n"
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
name|testJoinAggregateMaterializationNoAggregateFuncs7
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, MV0]])"
argument_list|,
literal|"expr#5=[Sarg[(10..11]]], expr#6=[SEARCH($t0, $t5)]"
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
name|testJoinAggregateMaterializationNoAggregateFuncs8
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationNoAggregateFuncs9
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableAggregate(group=[{0}])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, MV0]])"
argument_list|,
literal|"expr#5=[Sarg[(10..11], [19..20)]], expr#6=[SEARCH($t0, $t5)]"
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
name|testJoinAggregateMaterializationNoAggregateFuncs10
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
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
name|testJoinAggregateMaterializationAggregateFuncs1
parameter_list|()
block|{
comment|// This test relies on FK-UK relationship
name|sql
argument_list|(
literal|"select \"empid\", \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n"
operator|+
literal|"from \"emps\" join \"depts\" using (\"deptno\")\n"
operator|+
literal|"group by \"empid\", \"depts\".\"deptno\""
argument_list|,
literal|"select \"deptno\" from \"emps\" group by \"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}])\n"
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
name|testJoinAggregateMaterializationAggregateFuncs2
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n"
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
name|testJoinAggregateMaterializationAggregateFuncs3
parameter_list|()
block|{
comment|// This test relies on FK-UK relationship
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], S=[$t3], C=[$t2])\n"
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
name|testJoinAggregateMaterializationAggregateFuncs4
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], "
operator|+
literal|"proj#0..3=[{exprs}], $condition=[$t5])\n"
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
name|testJoinAggregateMaterializationAggregateFuncs5
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
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
literal|"      EnumerableTableScan(table=[[hr, MV0]])"
argument_list|)
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
name|testJoinAggregateMaterializationAggregateFuncs7
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], S=[$SUM0($2)])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, depts]])"
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
name|testJoinAggregateMaterializationAggregateFuncs8
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{4}], S=[$SUM0($2)])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, depts]])"
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
name|testJoinAggregateMaterializationAggregateFuncs9
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..2=[{inputs}], deptno=[$t1], S=[$t2])\n"
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
name|testJoinAggregateMaterializationAggregateFuncs10
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationAggregateFuncs11
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], "
operator|+
literal|"expr#3=[+($t1, $t2)], empid=[$t0], EXPR$1=[$t3])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], agg#0=[$SUM0($1)])"
argument_list|,
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableAggregate(group=[{2}], agg#0=[COUNT()])"
argument_list|,
literal|"EnumerableAggregate(group=[{1}], agg#0=[$SUM0($2)])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, MV0]])"
argument_list|,
literal|"expr#5=[Sarg[(10..11], [19..20)]], expr#6=[SEARCH($t0, $t5)]"
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
name|testJoinAggregateMaterializationAggregateFuncs12
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"depts\".\"deptno\", \"dependents\".\"empid\", "
operator|+
literal|"count(distinct \"emps\".\"salary\") as s\n"
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationAggregateFuncs13
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationAggregateFuncs14
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4276">[CALCITE-4276]    * If query contains join and rollup function (FLOOR), rewrite to materialized    * view contains bad field offset</a>. */
annotation|@
name|Test
name|void
name|testJoinAggregateMaterializationAggregateFuncs15
parameter_list|()
block|{
specifier|final
name|String
name|m
init|=
literal|""
operator|+
literal|"SELECT \"deptno\",\n"
operator|+
literal|"  COUNT(*) AS \"dept_size\",\n"
operator|+
literal|"  SUM(\"salary\") AS \"dept_budget\"\n"
operator|+
literal|"FROM \"emps\"\n"
operator|+
literal|"GROUP BY \"deptno\""
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|""
operator|+
literal|"SELECT FLOOR(\"CREATED_AT\" TO YEAR) AS by_year,\n"
operator|+
literal|"  COUNT(*) AS \"num_emps\"\n"
operator|+
literal|"FROM (SELECT\"deptno\"\n"
operator|+
literal|"    FROM \"emps\") AS \"t\"\n"
operator|+
literal|"JOIN (SELECT \"deptno\",\n"
operator|+
literal|"        \"inceptionDate\" as \"CREATED_AT\"\n"
operator|+
literal|"    FROM \"depts2\") using (\"deptno\")\n"
operator|+
literal|"GROUP BY FLOOR(\"CREATED_AT\" TO YEAR)"
decl_stmt|;
name|String
name|plan
init|=
literal|""
operator|+
literal|"EnumerableAggregate(group=[{8}], num_emps=[$SUM0($1)])\n"
operator|+
literal|"  EnumerableCalc(expr#0..7=[{inputs}], expr#8=[FLAG(YEAR)], "
operator|+
literal|"expr#9=[FLOOR($t3, $t8)], proj#0..7=[{exprs}], $f8=[$t9])\n"
operator|+
literal|"    EnumerableHashJoin(condition=[=($0, $4)], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts2]])\n"
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
name|plan
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
name|testJoinMaterialization1
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
annotation|@
name|Disabled
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
name|testJoinMaterialization4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\" = 1"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):INTEGER NOT NULL], expr#2=[1], "
operator|+
literal|"expr#3=[=($t1, $t2)], deptno=[$t0], $condition=[$t3])\n"
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
name|testJoinMaterialization5
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(\"empid\" as BIGINT) from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\"> 1"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], "
operator|+
literal|"expr#2=[1], expr#3=[<($t2, $t1)], EXPR$0=[$t1], $condition=[$t3])\n"
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
name|testJoinMaterialization6
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(\"empid\" as BIGINT) from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" \"deptno\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\") where \"empid\" = 1"
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], "
operator|+
literal|"expr#2=[1], expr#3=[CAST($t1):INTEGER NOT NULL], expr#4=[=($t2, $t3)], "
operator|+
literal|"EXPR$0=[$t1], $condition=[$t4])\n"
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
name|testJoinMaterialization7
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..2=[{inputs}], empid=[$t1])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):VARCHAR], name=[$t1])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t1):VARCHAR], empid=[$t0], name0=[$t2])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, dependents]])"
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
name|testJoinMaterialization8
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t2])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $4)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):VARCHAR], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t1):VARCHAR], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, dependents]])"
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
name|testJoinMaterialization9
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterialization10
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|"EnumerableUnion(all=[true])"
argument_list|,
literal|"EnumerableTableScan(table=[[hr, MV0]])"
argument_list|,
literal|"expr#5=[Sarg[(10..30]]], expr#6=[SEARCH($t0, $t5)]"
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
name|testJoinMaterialization11
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" from \"emps\"\n"
operator|+
literal|"where \"deptno\" in (select \"deptno\" from \"depts\")"
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterialization12
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK1
parameter_list|()
block|{
name|sql
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
literal|"join \"dependents\" using (\"empid\")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK2
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid=[$t0])\n"
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
name|testJoinMaterializationUKFK3
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"empid\" \"deptno\" from\n"
operator|+
literal|"(select * from \"emps\" where \"empid\" = 1)\n"
operator|+
literal|"join \"depts\" using (\"deptno\")"
argument_list|,
literal|"select \"empid\" from \"emps\" where \"empid\" = 1\n"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK5
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid=[$t0])\n"
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
name|testJoinMaterializationUKFK6
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], empid=[$t0])\n"
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
name|testJoinMaterializationUKFK7
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK8
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|noMat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinMaterializationUKFK9
parameter_list|()
block|{
name|sql
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
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryProjectWithBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
argument_list|,
literal|"select s.\"time_id\" between 1 and 3"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
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
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..7=[{inputs}], expr#8=[1], expr#9=[>=($t1, $t8)],"
operator|+
literal|" expr#10=[3], expr#11=[<=($t1, $t10)], expr#12=[AND($t9, $t11)], $f0=[$t12])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart, MV0]])"
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
name|testJoinQueryProjectWithBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
operator|+
literal|" where s.\"store_id\" = 1"
argument_list|,
literal|"select s.\"time_id\" between 1 and 3"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" join \"foodmart\".\"time_by_day\" as t on s.\"time_id\" = t.\"time_id\""
operator|+
literal|" where s.\"store_id\" = 1"
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
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..17=[{inputs}], expr#18=[1], expr#19=[>=($t8, $t18)], "
operator|+
literal|"expr#20=[3], expr#21=[<=($t8, $t20)], expr#22=[AND($t19, $t21)], $f0=[$t22])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart, MV0]])"
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
name|testViewProjectWithBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select s.\"time_id\", s.\"time_id\" between 1 and 3"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
argument_list|,
literal|"select s.\"time_id\""
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
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
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], time_id=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart, MV0]])"
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
name|testQueryAndViewProjectWithBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select s.\"time_id\", s.\"time_id\" between 1 and 3"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
argument_list|,
literal|"select s.\"time_id\" between 1 and 3"
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
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
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], EXPR$1=[$t1])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart, MV0]])"
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
name|testViewProjectWithMultifieldExpressions
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select s.\"time_id\", s.\"time_id\">= 1 and s.\"time_id\"< 3,"
operator|+
literal|" s.\"time_id\">= 1 or s.\"time_id\"< 3, "
operator|+
literal|" s.\"time_id\" + s.\"time_id\", "
operator|+
literal|" s.\"time_id\" * s.\"time_id\""
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
argument_list|,
literal|"select s.\"time_id\""
operator|+
literal|" from \"foodmart\".\"sales_fact_1997\" as s"
operator|+
literal|" where s.\"store_id\" = 1"
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
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], time_id=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[foodmart, MV0]])"
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
name|testAggregateOnJoinKeys
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"salary\" "
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\", \"salary\""
argument_list|,
literal|"select \"empid\", \"depts\".\"deptno\" "
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on \"depts\".\"deptno\" = \"empid\" group by \"empid\", \"depts\".\"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0=[{inputs}], empid=[$t0], empid0=[$t0])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}])\n"
operator|+
literal|"    EnumerableHashJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts]])"
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
name|testAggregateOnJoinKeys2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select \"deptno\", \"empid\", \"salary\", sum(1) "
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"group by \"deptno\", \"empid\", \"salary\""
argument_list|,
literal|"select sum(1) "
operator|+
literal|"from \"emps\"\n"
operator|+
literal|"join \"depts\" on \"depts\".\"deptno\" = \"empid\" group by \"empid\", \"depts\".\"deptno\""
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], EXPR$0=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{1}], EXPR$0=[$SUM0($3)])\n"
operator|+
literal|"    EnumerableHashJoin(condition=[=($1, $4)], joinType=[inner])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, MV0]])\n"
operator|+
literal|"      EnumerableTableScan(table=[[hr, depts]])"
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
name|testAggregateMaterializationOnCountDistinctQuery1
parameter_list|()
block|{
comment|// The column empid is already unique, thus DISTINCT is not
comment|// in the COUNT of the resulting rewriting
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], C=[COUNT($1)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]]"
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
name|testAggregateMaterializationOnCountDistinctQuery2
parameter_list|()
block|{
comment|// The column empid is already unique, thus DISTINCT is not
comment|// in the COUNT of the resulting rewriting
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], C=[COUNT($2)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[hr, MV0]]"
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
name|testAggregateMaterializationOnCountDistinctQuery3
parameter_list|()
block|{
comment|// The column salary is not unique, thus we end up with
comment|// a different rewriting
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], EXPR$1=[COUNT($1)])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 2}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]]"
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
name|testAggregateMaterializationOnCountDistinctQuery4
parameter_list|()
block|{
comment|// Although there is no DISTINCT in the COUNT, this is
comment|// equivalent to previous query
name|sql
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
argument_list|)
operator|.
name|withChecker
argument_list|(
name|resultContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{0}], EXPR$1=[COUNT()])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[hr, MV0]]"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
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
name|RelOptPlanner
name|planner
init|=
name|queryRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|queryRel
operator|.
name|getCluster
argument_list|()
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|RelOptUtil
operator|.
name|registerDefaultRules
argument_list|(
name|planner
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Programs
operator|.
name|standard
argument_list|()
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|queryRel
argument_list|,
name|traitSet
argument_list|,
name|testConfig
operator|.
name|materializations
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

