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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Bug
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
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Tests {@link org.apache.calcite.adapter.enumerable.EnumerableCorrelate}  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableCorrelateTest
block|{
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
literal|"EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"  EnumerableSemiJoin(condition=[=($1, $5)], joinType=[inner])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=[true], $f01=[$t0], $f0=[$t5])\n"
operator|+
literal|"      EnumerableJoin(condition=[=($0, $1)], joinType=[inner])\n"
operator|+
literal|"        EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"          EnumerableCalc(expr#0..4=[{inputs}], $f0=[$t1])\n"
operator|+
literal|"            EnumerableTableScan(table=[[s, emps]])\n"
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
literal|"EnumerableCalc(expr#0..5=[{inputs}], expr#6=[IS NOT NULL($t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"  EnumerableCorrelate(correlation=[$cor0], joinType=[LEFT], requiredColumns=[{1}])\n"
operator|+
literal|"    EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableAggregate(group=[{}], agg#0=[MIN($0)])\n"
operator|+
literal|"      EnumerableCalc(expr#0..3=[{inputs}], expr#4=[true], expr#5=[$cor0], expr#6=[$t5.deptno], expr#7=[=($t0, $t6)], $f0=[$t4], $condition=[$t7])\n"
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
specifier|final
name|boolean
name|forceDecorrelate
parameter_list|,
specifier|final
name|Object
name|schema
parameter_list|)
block|{
name|Bug
operator|.
name|remark
argument_list|(
literal|"CALCITE-489 - Teach CalciteAssert to respect multiple settings"
argument_list|)
expr_stmt|;
specifier|final
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"lex"
argument_list|,
literal|"JAVA"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"forceDecorrelate"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|forceDecorrelate
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
specifier|public
name|CalciteConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|p
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
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
name|calciteConnection
operator|.
name|setSchema
argument_list|(
literal|"s"
argument_list|)
expr_stmt|;
return|return
name|calciteConnection
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableCorrelateTest.java
end_comment

end_unit

