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
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Unit test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableHashJoin}.  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableHashJoinTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|innerJoin
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
literal|"select e.empid, e.name, d.name as dept from emps e join depts "
operator|+
literal|"d on e.deptno=d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], "
operator|+
literal|"name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill; dept=Sales"
argument_list|,
literal|"empid=110; name=Theodore; dept=Sales"
argument_list|,
literal|"empid=150; name=Sebastian; dept=Sales"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|innerJoinWithPredicate
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
literal|"select e.empid, e.name, d.name as dept from emps e join depts d"
operator|+
literal|" on e.deptno=d.deptno and e.empid<150 and e.empid>d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], expr#5=[>($t0,"
operator|+
literal|" $t3)], empid=[$t0], name=[$t2], dept=[$t4], $condition=[$t5])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[150], "
operator|+
literal|"expr#6=[<($t0, $t5)], proj#0..2=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill; dept=Sales"
argument_list|,
literal|"empid=110; name=Theodore; dept=Sales"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leftOuterJoin
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
literal|"select e.empid, e.name, d.name as dept from emps e  left outer "
operator|+
literal|"join depts d on e.deptno=d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], "
operator|+
literal|"name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[left])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])\n"
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
name|rightOuterJoin
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
literal|"select e.empid, e.name, d.name as dept from emps e  right outer "
operator|+
literal|"join depts d on e.deptno=d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], "
operator|+
literal|"name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[right])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
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
literal|"empid=null; name=null; dept=Marketing"
argument_list|,
literal|"empid=null; name=null; dept=HR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leftOuterJoinWithPredicate
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
literal|"select e.empid, e.name, d.name as dept from emps e left outer "
operator|+
literal|"join depts d on e.deptno=d.deptno and e.empid<150 and e"
operator|+
literal|".empid>d.deptno"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], "
operator|+
literal|"name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[AND(=($1, $3),<($0, 150),>"
operator|+
literal|"($0, $3))], joinType=[left])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill; dept=Sales"
argument_list|,
literal|"empid=110; name=Theodore; dept=Sales"
argument_list|,
literal|"empid=150; name=Sebastian; dept=null"
argument_list|,
literal|"empid=200; name=Eric; dept=null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rightOuterJoinWithPredicate
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
literal|"select e.empid, e.name, d.name as dept from emps e right outer "
operator|+
literal|"join depts d on e.deptno=d.deptno and e.empid<150"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], "
operator|+
literal|"name=[$t2], dept=[$t4])\n"
operator|+
literal|"  EnumerableHashJoin(condition=[=($1, $3)], joinType=[right])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[150], "
operator|+
literal|"expr#6=[<($t0, $t5)], proj#0..2=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, depts]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"empid=100; name=Bill; dept=Sales"
argument_list|,
literal|"empid=110; name=Theodore; dept=Sales"
argument_list|,
literal|"empid=null; name=null; dept=Marketing"
argument_list|,
literal|"empid=null; name=null; dept=HR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|semiJoin
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
literal|"SELECT d.deptno, d.name FROM depts d WHERE d.deptno in (SELECT e.deptno FROM emps e)"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableHashJoin(condition=[=($0, $3)], "
operator|+
literal|"joinType=[semi])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, depts]])\n"
operator|+
literal|"  EnumerableTableScan(table=[[s, emps]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10; name=Sales"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|semiJoinWithPredicate
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
literal|"?"
argument_list|)
operator|.
name|withRel
argument_list|(
comment|// Retrieve employees with the top salary in their department. Equivalent SQL:
comment|//   SELECT e.name, e.salary FROM emps e
comment|//   WHERE  EXISTS (
comment|//     SELECT 1 FROM emps e2
comment|//     WHERE e.deptno = e2.deptno AND e2.salary> e.salary)
name|builder
lambda|->
name|builder
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"emps"
argument_list|)
operator|.
name|as
argument_list|(
literal|"e"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"emps"
argument_list|)
operator|.
name|as
argument_list|(
literal|"e2"
argument_list|)
operator|.
name|semiJoin
argument_list|(
name|builder
operator|.
name|and
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"e"
argument_list|,
literal|"deptno"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"e2"
argument_list|,
literal|"deptno"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"e2"
argument_list|,
literal|"salary"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"e"
argument_list|,
literal|"salary"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"salary"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; salary=10000.0"
argument_list|,
literal|"name=Sebastian; salary=7000.0"
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
comment|// End EnumerableHashJoinTest.java
end_comment

end_unit

