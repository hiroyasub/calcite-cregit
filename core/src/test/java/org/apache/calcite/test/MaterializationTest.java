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
name|DepartmentPlus
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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

begin_comment
comment|/**  * Integration tests for the materialized view rewrite mechanism. Each test has a  * query and one or more materializations (what Oracle calls materialized views)  * and checks that the materialization is used.  */
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
annotation|@
name|Test
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
name|void
name|testViewMaterialization
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
name|String
name|materialize
init|=
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
decl_stmt|;
name|String
name|query
init|=
literal|"select \"depts\".\"name\"\n"
operator|+
literal|"from \"depts\"\n"
operator|+
literal|"join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")"
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
literal|true
argument_list|,
literal|"matview"
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
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableValues(tuples=[[{ 'noname' }]])"
argument_list|)
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"noname"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
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
name|replace
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
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
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
name|Disabled
argument_list|(
literal|"Creating mv for depts considering all its column throws exception"
argument_list|)
annotation|@
name|Test
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
annotation|@
name|Nullable
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
name|DepartmentPlus
index|[]
name|depts2
init|=
block|{
operator|new
name|DepartmentPlus
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
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|DepartmentPlus
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
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|DepartmentPlus
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
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
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

