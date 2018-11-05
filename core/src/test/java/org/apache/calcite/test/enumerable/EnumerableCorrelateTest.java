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
name|rel
operator|.
name|rules
operator|.
name|JoinToCorrelateRule
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
name|JdbcTest
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
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Unit test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableCorrelate}.  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableCorrelateTest
block|{
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2605">[CALCITE-2605]    * NullPointerException when left outer join implemented with EnumerableCorrelate</a> */
annotation|@
name|Test
specifier|public
name|void
name|leftOuterJoinCorrelate
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select e.empid, e.name, d.name as dept from emps e left outer join depts d on e.deptno=d.deptno"
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
comment|// force the left outer join to run via EnumerableCorrelate instead of EnumerableJoin
name|planner
operator|.
name|addRule
argument_list|(
name|JoinToCorrelateRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableCorrelate(correlation=[$cor0], joinType=[left], requiredColumns=[{1}])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[$cor0], expr#5=[$t4.deptno], expr#6=[=($t5, $t0)], proj#0..1=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill; dept=Sales"
argument_list|,
literal|"empid=110; name=Theodore; dept=Sales"
argument_list|,
literal|"empid=150; name=Sebastian; dept=Sales"
argument_list|,
literal|"empid=200; name=Eric; dept=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleCorrelateDecorrelated
parameter_list|()
block|{
name|tester
argument_list|(
literal|true
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empid, name from emps e where exists (select 1 from depts d where d.deptno=e.deptno)"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..2=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"  EnumerableSemiJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, depts]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill"
argument_list|,
literal|"empid=110; name=Theodore"
argument_list|,
literal|"empid=150; name=Sebastian"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleCorrelate
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select empid, name from emps e where exists (select 1 from depts d where d.deptno=e.deptno)"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"  EnumerableCorrelate(correlation=[$cor0], joinType=[inner], requiredColumns=[{1}])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"      EnumerableCalc(expr#0..3=[{inputs}], expr#4=[true], expr#5=[$cor0], expr#6=[$t5.deptno], expr#7=[=($t0, $t6)], i=[$t4], $condition=[$t7])\n"
operator|+
literal|"        EnumerableTableScan(table=[[s, depts]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill"
argument_list|,
literal|"empid=110; name=Theodore"
argument_list|,
literal|"empid=150; name=Sebastian"
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

begin_comment
comment|// End EnumerableCorrelateTest.java
end_comment

end_unit

