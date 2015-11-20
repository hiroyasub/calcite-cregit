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
name|CalciteConnection
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
name|SchemaPlus
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
name|impl
operator|.
name|AbstractSchema
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
name|impl
operator|.
name|ScalarFunctionImpl
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
name|impl
operator|.
name|ViewTable
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
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
name|Statement
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
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for user-defined functions (including user-defined table functions  * and user-defined aggregate functions).  *  * @see Smalls  */
end_comment

begin_class
specifier|public
class|class
name|UdfTest
block|{
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|withUdf
parameter_list|()
block|{
specifier|final
name|String
name|model
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|JdbcTest
operator|.
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_PLUS',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyPlusFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_LEFT',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyLeftFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'ABCDE',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyAbcdeFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_STR',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyToStringFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_DOUBLE',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MyDoubleFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'COUNT_ARGS',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|CountArgs0Function
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'COUNT_ARGS',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|CountArgs1Function
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'COUNT_ARGS',\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|CountArgs2Function
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_ABS',\n"
operator|+
literal|"           className: '"
operator|+
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           methodName: 'abs'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|MultipleFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           methodName: '*'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           className: '"
operator|+
name|Smalls
operator|.
name|AllTypesFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           methodName: '*'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
return|;
block|}
comment|/** Tests user-defined function. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedFunction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"adhoc\".my_plus(\"deptno\", 100) as p\n"
operator|+
literal|"from \"adhoc\".EMPLOYEES"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"P=110\n"
operator|+
literal|"P=120\n"
operator|+
literal|"P=110\n"
operator|+
literal|"P=110\n"
decl_stmt|;
name|withUdf
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedFunctionB
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"adhoc\".my_double(\"deptno\") as p\n"
operator|+
literal|"from \"adhoc\".EMPLOYEES"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"P=20\n"
operator|+
literal|"P=40\n"
operator|+
literal|"P=20\n"
operator|+
literal|"P=20\n"
decl_stmt|;
name|withUdf
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-937">[CALCITE-937]    * User-defined function within view</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedFunctionInView
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.calcite.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|)
decl_stmt|;
name|CalciteConnection
name|calciteConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|SchemaPlus
name|rootSchema
init|=
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|rootSchema
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SchemaPlus
name|post
init|=
name|rootSchema
operator|.
name|add
argument_list|(
literal|"POST"
argument_list|,
operator|new
name|AbstractSchema
argument_list|()
argument_list|)
decl_stmt|;
name|post
operator|.
name|add
argument_list|(
literal|"MY_INCREMENT"
argument_list|,
name|ScalarFunctionImpl
operator|.
name|create
argument_list|(
name|Smalls
operator|.
name|MyIncrement
operator|.
name|class
argument_list|,
literal|"eval"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|viewSql
init|=
literal|"select \"empid\" as EMPLOYEE_ID,\n"
operator|+
literal|"  \"name\" || ' ' || \"name\" as EMPLOYEE_NAME,\n"
operator|+
literal|"  \"salary\" as EMPLOYEE_SALARY,\n"
operator|+
literal|"  POST.MY_INCREMENT(\"empid\", 10) as INCREMENTED_SALARY\n"
operator|+
literal|"from \"hr\".\"emps\""
decl_stmt|;
name|post
operator|.
name|add
argument_list|(
literal|"V_EMP"
argument_list|,
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|post
argument_list|,
name|viewSql
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
literal|""
operator|+
literal|"EMPLOYEE_ID=100; EMPLOYEE_NAME=Bill Bill; EMPLOYEE_SALARY=10000.0; INCREMENTED_SALARY=110.0\n"
operator|+
literal|"EMPLOYEE_ID=200; EMPLOYEE_NAME=Eric Eric; EMPLOYEE_SALARY=8000.0; INCREMENTED_SALARY=220.0\n"
operator|+
literal|"EMPLOYEE_ID=150; EMPLOYEE_NAME=Sebastian Sebastian; EMPLOYEE_SALARY=7000.0; INCREMENTED_SALARY=165.0\n"
operator|+
literal|"EMPLOYEE_ID=110; EMPLOYEE_NAME=Theodore Theodore; EMPLOYEE_SALARY=11500.0; INCREMENTED_SALARY=121.0\n"
decl_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|viewSql
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|ResultSet
name|viewResultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"POST\".\"V_EMP\""
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|viewResultSet
argument_list|)
argument_list|,
name|is
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests that IS NULL/IS NOT NULL is properly implemented for non-strict    * functions.    */
annotation|@
name|Test
specifier|public
name|void
name|testNotNullImplementor
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select upper(\"adhoc\".my_str(\"name\")) as p from \"adhoc\".EMPLOYEES"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=<BILL>\n"
operator|+
literal|"P=<ERIC>\n"
operator|+
literal|"P=<SEBASTIAN>\n"
operator|+
literal|"P=<THEODORE>\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" as p from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where \"adhoc\".my_str(\"name\") is not null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=Bill\n"
operator|+
literal|"P=Eric\n"
operator|+
literal|"P=Sebastian\n"
operator|+
literal|"P=Theodore\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" as p from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where \"adhoc\".my_str(upper(\"name\")) is not null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=Bill\n"
operator|+
literal|"P=Eric\n"
operator|+
literal|"P=Sebastian\n"
operator|+
literal|"P=Theodore\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" as p from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where upper(\"adhoc\".my_str(\"name\")) is not null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=Bill\n"
operator|+
literal|"P=Eric\n"
operator|+
literal|"P=Sebastian\n"
operator|+
literal|"P=Theodore\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" as p from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where \"adhoc\".my_str(\"name\") is null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"name\" as p from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where \"adhoc\".my_str(upper(\"adhoc\".my_str(\"name\")"
operator|+
literal|")) ='8'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/** Tests derived return type of user-defined function. */
annotation|@
name|Test
specifier|public
name|void
name|testUdfDerivedReturnType
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select max(\"adhoc\".my_double(\"deptno\")) as p from \"adhoc\".EMPLOYEES"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=40\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select max(\"adhoc\".my_str(\"name\")) as p\n"
operator|+
literal|"from \"adhoc\".EMPLOYEES\n"
operator|+
literal|"where \"adhoc\".my_str(\"name\") is null"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=null\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a user-defined function that has multiple overloads. */
annotation|@
name|Test
specifier|public
name|void
name|testUdfOverloaded
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".count_args(),\n"
operator|+
literal|" \"adhoc\".count_args(0),\n"
operator|+
literal|" \"adhoc\".count_args(0, 0))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=0; EXPR$1=1; EXPR$2=2\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select max(\"adhoc\".count_args()) as p0,\n"
operator|+
literal|" min(\"adhoc\".count_args(0)) as p1,\n"
operator|+
literal|" max(\"adhoc\".count_args(0, 0)) as p2\n"
operator|+
literal|"from \"adhoc\".EMPLOYEES limit 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P0=0; P1=1; P2=2\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests passing parameters to user-defined function by name. */
annotation|@
name|Test
specifier|public
name|void
name|testUdfArgumentName
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
comment|// arguments in physical order
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"s\" => 'hello', \"n\" => 3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=hel\n"
argument_list|)
expr_stmt|;
comment|// arguments in reverse order
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 3, \"s\" => 'hello'))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=hel\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 1 + 2, \"s\" => 'hello'))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=hel\n"
argument_list|)
expr_stmt|;
comment|// duplicate argument names
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 3, \"n\" => 2, \"s\" => 'hello'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Duplicate argument name 'n'"
argument_list|)
expr_stmt|;
comment|// invalid argument names
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 3, \"m\" => 2, \"s\" => 'h'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"MY_LEFT(n =><NUMERIC>, m =><NUMERIC>, s =><CHARACTER>)"
argument_list|)
expr_stmt|;
comment|// missing arguments
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 3))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature MY_LEFT(n =><NUMERIC>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"s\" => 'hello'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature MY_LEFT(s =><CHARACTER>)"
argument_list|)
expr_stmt|;
comment|// arguments of wrong type
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 'hello', \"s\" => 'x'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"MY_LEFT(n =><CHARACTER>, s =><CHARACTER>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".my_left(\"n\" => 1, \"s\" => 0))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"MY_LEFT(n =><NUMERIC>, s =><NUMERIC>)"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests calling a user-defined function some of whose parameters are    * optional. */
annotation|@
name|Test
specifier|public
name|void
name|testUdfArgumentOptional
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,b=>2,c=>3,d=>4,e=>5))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: 4, e: 5}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2,3,4,CAST(NULL AS INTEGER)))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: 4, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,b=>2,c=>3,d=>4))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: 4, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,b=>2,c=>3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: null, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,e=>5,c=>3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: null, c: 3, d: null, e: 5}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2,3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: null, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2,3,4))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: 4, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2,3,4,5))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: 2, c: 3, d: 4, e: 5}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature ABCDE(<NUMERIC>,<NUMERIC>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,DEFAULT,3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: null, c: 3, d: null, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,DEFAULT,'abcde'))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature ABCDE(<NUMERIC>,<ANY>,<CHARACTER>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(true))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature ABCDE(<BOOLEAN>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(true,DEFAULT))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature ABCDE(<BOOLEAN>,<ANY>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,DEFAULT,3,DEFAULT))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: null, c: 3, d: null, e: null}\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(1,2,DEFAULT))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"DEFAULT is only allowed for optional parameters"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,b=>2,c=>DEFAULT))"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"DEFAULT is only allowed for optional parameters"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values (\"adhoc\".abcde(a=>1,b=>DEFAULT,c=>3))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0={a: 1, b: null, c: 3, d: null, e: null}\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test for    * {@link org.apache.calcite.runtime.CalciteResource#requireDefaultConstructor(String)}. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedFunction2
parameter_list|()
throws|throws
name|Exception
block|{
name|withBadUdf
argument_list|(
name|Smalls
operator|.
name|AwkwardFunction
operator|.
name|class
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Declaring class 'org.apache.calcite.util.Smalls$AwkwardFunction' of non-static user-defined function must have a public constructor with zero parameters"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests user-defined function, with multiple methods per class. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedFunctionWithMethodName
parameter_list|()
throws|throws
name|Exception
block|{
comment|// java.lang.Math has abs(int) and abs(double).
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values abs(-4)"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values abs(-4.5)"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"4.5"
argument_list|)
expr_stmt|;
comment|// 3 overloads of "fun1", another method "fun2", but method "nonStatic"
comment|// cannot be used as a function
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"fun1\"(2)"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"fun1\"(2, 3)"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"5"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"fun1\"('Foo Bar')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"foo bar"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"fun2\"(10)"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"30"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"nonStatic\"(2)"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature nonStatic(<NUMERIC>)"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests user-defined aggregate function. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedAggregateFunction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|empDept
init|=
name|JdbcTest
operator|.
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sum
init|=
name|Smalls
operator|.
name|MyStaticSumFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sum2
init|=
name|Smalls
operator|.
name|MySumFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|empDept
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_SUM',\n"
operator|+
literal|"           className: '"
operator|+
name|sum
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_SUM2',\n"
operator|+
literal|"           className: '"
operator|+
name|sum2
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
decl_stmt|;
name|with
operator|.
name|withDefaultSchema
argument_list|(
literal|null
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"adhoc\".my_sum(\"deptno\") as p from \"adhoc\".EMPLOYEES\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=50\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select my_sum(\"empid\"), \"deptno\" as p from EMPLOYEES\n"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Expression 'deptno' is not being grouped"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select my_sum(\"deptno\") as p from EMPLOYEES\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"P=50\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select my_sum(\"name\") as p from EMPLOYEES\n"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Cannot apply 'MY_SUM' to arguments of type 'MY_SUM(<JAVATYPE(CLASS JAVA.LANG.STRING)>)'. Supported form(s): 'MY_SUM(<NUMERIC>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select my_sum(\"deptno\", 1) as p from EMPLOYEES\n"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature MY_SUM(<NUMERIC>,<NUMERIC>)"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select my_sum() as p from EMPLOYEES\n"
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"No match found for function signature MY_SUM()"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"deptno\", my_sum(\"deptno\") as p from EMPLOYEES\n"
operator|+
literal|"group by \"deptno\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=20; P=20"
argument_list|,
literal|"deptno=10; P=30"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select \"deptno\", my_sum2(\"deptno\") as p from EMPLOYEES\n"
operator|+
literal|"group by \"deptno\""
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"deptno=20; P=20"
argument_list|,
literal|"deptno=10; P=30"
argument_list|)
expr_stmt|;
block|}
comment|/** Test for    * {@link org.apache.calcite.runtime.CalciteResource#firstParameterOfAdd(String)}. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedAggregateFunction3
parameter_list|()
throws|throws
name|Exception
block|{
name|withBadUdf
argument_list|(
name|Smalls
operator|.
name|SumFunctionBadIAdd
operator|.
name|class
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Caused by: java.lang.RuntimeException: In user-defined aggregate class 'org.apache.calcite.util.Smalls$SumFunctionBadIAdd', first parameter to 'add' method must be the accumulator (the return type of the 'init' method)"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|CalciteAssert
operator|.
name|AssertThat
name|withBadUdf
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
specifier|final
name|String
name|empDept
init|=
name|JdbcTest
operator|.
name|EmpDeptTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|className
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|empDept
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'AWKWARD',\n"
operator|+
literal|"           className: '"
operator|+
name|className
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
return|;
block|}
comment|/** Tests user-defined aggregate function with FILTER.    *    *<p>Also tests that we do not try to push ADAF to JDBC source. */
annotation|@
name|Test
specifier|public
name|void
name|testUserDefinedAggregateFunctionWithFilter
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sum
init|=
name|Smalls
operator|.
name|MyStaticSumFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sum2
init|=
name|Smalls
operator|.
name|MySumFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|JdbcTest
operator|.
name|SCOTT_SCHEMA
operator|+
literal|",\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_SUM',\n"
operator|+
literal|"           className: '"
operator|+
name|sum
operator|+
literal|"'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_SUM2',\n"
operator|+
literal|"           className: '"
operator|+
name|sum2
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select deptno, \"adhoc\".my_sum(deptno) as p\n"
operator|+
literal|"from scott.emp\n"
operator|+
literal|"group by deptno\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=20; P=100\n"
operator|+
literal|"DEPTNO=10; P=30\n"
operator|+
literal|"DEPTNO=30; P=180\n"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"select deptno,\n"
operator|+
literal|"  \"adhoc\".my_sum(deptno) filter (where job = 'CLERK') as c,\n"
operator|+
literal|"  \"adhoc\".my_sum(deptno) filter (where job = 'XXX') as x\n"
operator|+
literal|"from scott.emp\n"
operator|+
literal|"group by deptno\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=20; C=40; X=0\n"
operator|+
literal|"DEPTNO=10; C=10; X=0\n"
operator|+
literal|"DEPTNO=30; C=30; X=0\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests resolution of functions using schema paths. */
annotation|@
name|Test
specifier|public
name|void
name|testPath
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|name
init|=
name|Smalls
operator|.
name|MyPlusFunction
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_PLUS',\n"
operator|+
literal|"           className: '"
operator|+
name|name
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     },\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc2',\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_PLUS2',\n"
operator|+
literal|"           className: '"
operator|+
name|name
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     },\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc3',\n"
operator|+
literal|"       path: ['adhoc2','adhoc3'],\n"
operator|+
literal|"       functions: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'MY_PLUS3',\n"
operator|+
literal|"           className: '"
operator|+
name|name
operator|+
literal|"'\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|err
init|=
literal|"No match found for function signature"
decl_stmt|;
specifier|final
name|String
name|res
init|=
literal|"EXPR$0=2\n"
decl_stmt|;
comment|// adhoc can see own function MY_PLUS but not adhoc2.MY_PLUS2 unless
comment|// qualified
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|adhoc
init|=
name|with
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
decl_stmt|;
name|adhoc
operator|.
name|query
argument_list|(
literal|"values MY_PLUS(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|adhoc
operator|.
name|query
argument_list|(
literal|"values MY_PLUS2(1, 1)"
argument_list|)
operator|.
name|throws_
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|adhoc
operator|.
name|query
argument_list|(
literal|"values \"adhoc2\".MY_PLUS(1, 1)"
argument_list|)
operator|.
name|throws_
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|adhoc
operator|.
name|query
argument_list|(
literal|"values \"adhoc2\".MY_PLUS2(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// adhoc2 can see own function MY_PLUS2 but not adhoc2.MY_PLUS unless
comment|// qualified
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|adhoc2
init|=
name|with
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc2"
argument_list|)
decl_stmt|;
name|adhoc2
operator|.
name|query
argument_list|(
literal|"values MY_PLUS2(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|adhoc2
operator|.
name|query
argument_list|(
literal|"values MY_PLUS(1, 1)"
argument_list|)
operator|.
name|throws_
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|adhoc2
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".MY_PLUS(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// adhoc3 can see own adhoc2.MY_PLUS2 because in path, with or without
comment|// qualification, but can only see adhoc.MY_PLUS with qualification
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|adhoc3
init|=
name|with
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc3"
argument_list|)
decl_stmt|;
name|adhoc3
operator|.
name|query
argument_list|(
literal|"values MY_PLUS2(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|adhoc3
operator|.
name|query
argument_list|(
literal|"values MY_PLUS(1, 1)"
argument_list|)
operator|.
name|throws_
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|adhoc3
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".MY_PLUS(1, 1)"
argument_list|)
operator|.
name|returns
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-986">[CALCITE-986]    * User-defined function with Date or Timestamp parameters</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testDate
parameter_list|()
block|{
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|with
init|=
name|withUdf
argument_list|()
decl_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"dateFun\"(DATE '1970-01-01')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"dateFun\"(DATE '1970-01-02')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"86400000"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"dateFun\"(cast(null as date))"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"-1"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timeFun\"(TIME '00:00:00')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timeFun\"(TIME '00:01:30')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"90000"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timeFun\"(cast(null as time))"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"-1"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timestampFun\"(TIMESTAMP '1970-01-01 00:00:00')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timestampFun\"(TIMESTAMP '1970-01-02 00:01:30')"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"86490000"
argument_list|)
expr_stmt|;
name|with
operator|.
name|query
argument_list|(
literal|"values \"adhoc\".\"timestampFun\"(cast(null as timestamp))"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"-1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End UdfTest.java
end_comment

end_unit

