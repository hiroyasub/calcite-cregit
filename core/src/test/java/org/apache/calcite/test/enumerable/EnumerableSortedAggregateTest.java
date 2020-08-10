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
operator|.
name|enumerable
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|config
operator|.
name|Lex
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
name|test
operator|.
name|CalciteAssert
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
name|schemata
operator|.
name|hr
operator|.
name|HrSchema
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
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/** Test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableSortedAggregate}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableSortedAggregateTest
block|{
annotation|@
name|Test
name|void
name|sortedAgg
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select deptno, "
operator|+
literal|"max(salary) as max_salary, count(name) as num_employee "
operator|+
literal|"from emps group by deptno"
argument_list|)
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
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSortedAggregate(group=[{1}], max_salary=[MAX($3)], num_employee=[COUNT($2)])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"deptno=10; max_salary=11500.0; num_employee=3"
argument_list|,
literal|"deptno=20; max_salary=8000.0; num_employee=1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|sortedAggTwoGroupKeys
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select deptno, commission, "
operator|+
literal|"max(salary) as max_salary, count(name) as num_employee "
operator|+
literal|"from emps group by deptno, commission"
argument_list|)
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
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSortedAggregate(group=[{1, 4}], max_salary=[MAX($3)], num_employee=[COUNT($2)])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], sort1=[$4], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"deptno=10; commission=250; max_salary=11500.0; num_employee=1"
argument_list|,
literal|"deptno=10; commission=1000; max_salary=10000.0; num_employee=1"
argument_list|,
literal|"deptno=10; commission=null; max_salary=7000.0; num_employee=1"
argument_list|,
literal|"deptno=20; commission=500; max_salary=8000.0; num_employee=1"
argument_list|)
expr_stmt|;
block|}
comment|// Outer sort is expected to be pushed through aggregation.
annotation|@
name|Test
name|void
name|sortedAggGroupbyXOrderbyX
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select deptno, "
operator|+
literal|"max(salary) as max_salary, count(name) as num_employee "
operator|+
literal|"from emps group by deptno order by deptno"
argument_list|)
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
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSortedAggregate(group=[{1}], max_salary=[MAX($3)], num_employee=[COUNT($2)])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"deptno=10; max_salary=11500.0; num_employee=3"
argument_list|,
literal|"deptno=20; max_salary=8000.0; num_employee=1"
argument_list|)
expr_stmt|;
block|}
comment|// Outer sort is not expected to be pushed through aggregation.
annotation|@
name|Test
name|void
name|sortedAggGroupbyXOrderbyY
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select deptno, "
operator|+
literal|"max(salary) as max_salary, count(name) as num_employee "
operator|+
literal|"from emps group by deptno order by num_employee desc"
argument_list|)
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
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSort(sort0=[$2], dir0=[DESC])\n"
operator|+
literal|"  EnumerableSortedAggregate(group=[{1}], max_salary=[MAX($3)], num_employee=[COUNT($2)])\n"
operator|+
literal|"    EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"deptno=10; max_salary=11500.0; num_employee=3"
argument_list|,
literal|"deptno=20; max_salary=8000.0; num_employee=1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|sortedAggNullValueInSortedGroupByKeys
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select commission, "
operator|+
literal|"count(deptno) as num_dept "
operator|+
literal|"from emps group by commission"
argument_list|)
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
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_SORTED_AGGREGATE_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSortedAggregate(group=[{4}], num_dept=[COUNT()])\n"
operator|+
literal|"  EnumerableSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"commission=250; num_dept=1"
argument_list|,
literal|"commission=500; num_dept=1"
argument_list|,
literal|"commission=1000; num_dept=1"
argument_list|,
literal|"commission=null; num_dept=1"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|tester
parameter_list|(
name|boolean
name|forceDecorrelate
parameter_list|,
name|Object
name|schema
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
name|CalciteConnectionProperty
operator|.
name|LEX
argument_list|,
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FORCE_DECORRELATE
argument_list|,
name|forceDecorrelate
argument_list|)
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
name|schema
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

