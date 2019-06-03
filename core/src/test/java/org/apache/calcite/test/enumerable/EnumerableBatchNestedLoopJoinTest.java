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
name|core
operator|.
name|JoinRelType
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
comment|/**  * Unit test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableBatchNestedLoopJoin}  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableBatchNestedLoopJoinTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simpleInnerBatchJoinTestBuilder
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|withRel
argument_list|(
name|builder
lambda|->
name|builder
operator|.
name|scan
argument_list|(
literal|"s"
argument_list|,
literal|"depts"
argument_list|)
operator|.
name|as
argument_list|(
literal|"d"
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
literal|"e"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
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
literal|"d"
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
literal|"e"
argument_list|,
literal|"deptno"
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
literal|"deptno"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=10"
argument_list|,
literal|"deptno=10"
argument_list|,
literal|"deptno=10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleInnerBatchJoinTestSQL
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
literal|"select e.name from emps e join depts d on d.deptno = e.deptno"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill"
argument_list|,
literal|"name=Sebastian"
argument_list|,
literal|"name=Theodore"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleLeftBatchJoinTestSQL
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
literal|"select e.name, d.deptno from emps e left join depts d on d.deptno = e.deptno"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Bill; deptno=10"
argument_list|,
literal|"name=Eric; deptno=null"
argument_list|,
literal|"name=Sebastian; deptno=10"
argument_list|,
literal|"name=Theodore; deptno=10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|innerBatchJoinTestSQL
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(e.name) from emps e join depts d on d.deptno = e.deptno"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=46"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|innerBatchJoinTestSQL2
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(e.name) from emps e join depts d on d.deptno = e.empid"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|leftBatchJoinTestSQL
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(d.deptno) from depts d left join emps e on d.deptno = e.deptno"
operator|+
literal|" where d.deptno<30 and d.deptno>10"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=8"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinSubQuery
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT count(name) FROM emps e WHERE e.deptno NOT IN "
operator|+
literal|"(SELECT d.deptno FROM depts d WHERE d.name = 'Sales')"
decl_stmt|;
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|sql
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_MERGE_JOIN_RULE
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
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=23"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoinOnString
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT d.name, e.salary FROM depts d join emps e on d.name = e.name"
decl_stmt|;
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|sql
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_MERGE_JOIN_RULE
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
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSemiJoin
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"?"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_MERGE_JOIN_RULE
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
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|withRel
argument_list|(
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
literal|"depts"
argument_list|)
operator|.
name|as
argument_list|(
literal|"d"
argument_list|)
operator|.
name|semiJoin
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
literal|"empid"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|"d"
argument_list|,
literal|"deptno"
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
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"name=Emmanuel"
argument_list|,
literal|"name=Gabriel"
argument_list|,
literal|"name=Michelle"
argument_list|,
literal|"name=Ursula"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAntiJoin
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_MERGE_JOIN_RULE
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
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|withRel
argument_list|(
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
name|antiJoin
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
literal|"name=Theodore; salary=11500.0"
argument_list|,
literal|"name=Eric; salary=8000.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|innerBatchJoinAndTestSQL
parameter_list|()
block|{
name|tester
argument_list|(
literal|false
argument_list|,
operator|new
name|JdbcTest
operator|.
name|HrSchemaBig
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(e.name) from emps e join depts d on d.deptno = e.empid and d.deptno = e.deptno"
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
name|ENUMERABLE_CORRELATE_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_BATCH_NESTED_LOOP_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=1"
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
comment|// End EnumerableBatchNestedLoopJoinTest.java
end_comment

end_unit

