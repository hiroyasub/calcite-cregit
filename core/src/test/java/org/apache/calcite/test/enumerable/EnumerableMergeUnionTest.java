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
comment|/**  * Unit test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableMergeUnion}.  */
end_comment

begin_class
class|class
name|EnumerableMergeUnionTest
block|{
annotation|@
name|Test
name|void
name|mergeUnionAllOrderByEmpid
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union all select empid, name from emps where name like '%l') order by empid"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[true])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=1; name=Bill"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=12; name=Paul"
argument_list|,
literal|"empid=29; name=Anibal"
argument_list|,
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=45; name=Pascal"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByEmpid
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union select empid, name from emps where name like '%l') order by empid"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=1; name=Bill"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=12; name=Paul"
argument_list|,
literal|"empid=29; name=Anibal"
argument_list|,
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=45; name=Pascal"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionAllOrderByName
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union all select empid, name from emps where name like '%l') order by name"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[true])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=29; name=Anibal"
argument_list|,
literal|"empid=1; name=Bill"
argument_list|,
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|,
literal|"empid=45; name=Pascal"
argument_list|,
literal|"empid=12; name=Paul"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByName
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union select empid, name from emps where name like '%l') order by name"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=29; name=Anibal"
argument_list|,
literal|"empid=1; name=Bill"
argument_list|,
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|,
literal|"empid=45; name=Pascal"
argument_list|,
literal|"empid=12; name=Paul"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionSingleColumnOrderByName
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
argument_list|,
literal|"select * from (select name from emps where name like 'G%' union select name from emps where name like '%l') order by name"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"name=Anibal"
argument_list|,
literal|"name=Bill"
argument_list|,
literal|"name=Emmanuel"
argument_list|,
literal|"name=Gabriel"
argument_list|,
literal|"name=Guy"
argument_list|,
literal|"name=Pascal"
argument_list|,
literal|"name=Paul"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByNameWithLimit
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union select empid, name from emps where name like '%l') order by name limit 3"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableLimit(fetch=[3])\n"
operator|+
literal|"  EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"      EnumerableLimitSort(sort0=[$2], dir0=[ASC], fetch=[3])\n"
operator|+
literal|"        EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"          EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"      EnumerableLimitSort(sort0=[$2], dir0=[ASC], fetch=[3])\n"
operator|+
literal|"        EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"          EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=29; name=Anibal"
argument_list|,
literal|"empid=1; name=Bill"
argument_list|,
literal|"empid=40; name=Emmanuel"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByNameWithOffset
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union select empid, name from emps where name like '%l') order by name offset 2"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableLimit(offset=[2])\n"
operator|+
literal|"  EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"    EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"      EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"        EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableSort(sort0=[$1], dir0=[ASC])\n"
operator|+
literal|"      EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], empid=[$t0], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"        EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|,
literal|"empid=45; name=Pascal"
argument_list|,
literal|"empid=12; name=Paul"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByNameWithLimitAndOffset
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
argument_list|,
literal|"select * from (select empid, name from emps where name like 'G%' union select empid, name from emps where name like '%l') order by name limit 3 offset 2"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableLimit(offset=[2], fetch=[3])\n"
operator|+
literal|"  EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"      EnumerableLimitSort(sort0=[$2], dir0=[ASC], fetch=[5])\n"
operator|+
literal|"        EnumerableCalc(expr#0..4=[{inputs}], expr#5=['G%'], expr#6=[LIKE($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"          EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], empid=[$t0], name=[$t2])\n"
operator|+
literal|"      EnumerableLimitSort(sort0=[$2], dir0=[ASC], fetch=[5])\n"
operator|+
literal|"        EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%l'], expr#6=[LIKE($t2, $t5)], proj#0..4=[{exprs}], $condition=[$t6])\n"
operator|+
literal|"          EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"empid=40; name=Emmanuel"
argument_list|,
literal|"empid=10; name=Gabriel"
argument_list|,
literal|"empid=6; name=Guy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionAllOrderByCommissionAscNullsFirstAndNameDesc
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
argument_list|,
literal|"select * from (select commission, name from emps where name like 'R%' union all select commission, name from emps where name like '%y%') order by commission asc nulls first, name desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[true])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC-nulls-first], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['R%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC-nulls-first], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%y%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"commission=null; name=Taylor"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Ralf"
argument_list|,
literal|"commission=250; name=Seohyun"
argument_list|,
literal|"commission=250; name=Hyuna"
argument_list|,
literal|"commission=250; name=Andy"
argument_list|,
literal|"commission=500; name=Kylie"
argument_list|,
literal|"commission=500; name=Guy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByCommissionAscNullsFirstAndNameDesc
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
argument_list|,
literal|"select * from (select commission, name from emps where name like 'R%' union select commission, name from emps where name like '%y%') order by commission asc nulls first, name desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC-nulls-first], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['R%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC-nulls-first], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%y%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"commission=null; name=Taylor"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Ralf"
argument_list|,
literal|"commission=250; name=Seohyun"
argument_list|,
literal|"commission=250; name=Hyuna"
argument_list|,
literal|"commission=250; name=Andy"
argument_list|,
literal|"commission=500; name=Kylie"
argument_list|,
literal|"commission=500; name=Guy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionAllOrderByCommissionAscNullsLastAndNameDesc
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
argument_list|,
literal|"select * from (select commission, name from emps where name like 'R%' union all select commission, name from emps where name like '%y%') order by commission asc nulls last, name desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[true])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['R%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%y%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"commission=250; name=Seohyun"
argument_list|,
literal|"commission=250; name=Hyuna"
argument_list|,
literal|"commission=250; name=Andy"
argument_list|,
literal|"commission=500; name=Kylie"
argument_list|,
literal|"commission=500; name=Guy"
argument_list|,
literal|"commission=null; name=Taylor"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Ralf"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|mergeUnionOrderByCommissionAscNullsLastAndNameDesc
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
argument_list|,
literal|"select * from (select commission, name from emps where name like 'R%' union select commission, name from emps where name like '%y%') order by commission asc nulls last, name desc"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableMergeUnion(all=[false])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['R%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
operator|+
literal|"  EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"    EnumerableCalc(expr#0..4=[{inputs}], expr#5=['%y%'], expr#6=[LIKE($t2, $t5)], commission=[$t4], name=[$t2], $condition=[$t6])\n"
operator|+
literal|"      EnumerableTableScan(table=[[s, emps]])\n"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"commission=250; name=Seohyun"
argument_list|,
literal|"commission=250; name=Hyuna"
argument_list|,
literal|"commission=250; name=Andy"
argument_list|,
literal|"commission=500; name=Kylie"
argument_list|,
literal|"commission=500; name=Guy"
argument_list|,
literal|"commission=null; name=Taylor"
argument_list|,
literal|"commission=null; name=Riyad"
argument_list|,
literal|"commission=null; name=Ralf"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|tester
parameter_list|(
name|boolean
name|forceDecorrelate
parameter_list|,
name|Object
name|schema
parameter_list|,
name|String
name|sqlQuery
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
operator|.
name|query
argument_list|(
name|sqlQuery
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
comment|// Force UNION to be implemented via EnumerableMergeUnion
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_UNION_RULE
argument_list|)
expr_stmt|;
comment|// Allow EnumerableLimitSort optimization
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_LIMIT_SORT_RULE
argument_list|)
expr_stmt|;
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

