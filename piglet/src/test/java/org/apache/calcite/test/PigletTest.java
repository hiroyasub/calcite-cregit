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
name|piglet
operator|.
name|parser
operator|.
name|ParseException
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
comment|/** Unit tests for Piglet. */
end_comment

begin_class
specifier|public
class|class
name|PigletTest
block|{
specifier|private
specifier|static
name|Fluent
name|pig
parameter_list|(
name|String
name|pig
parameter_list|)
block|{
return|return
operator|new
name|Fluent
argument_list|(
name|pig
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseLoad
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'Emp';"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"{op: PROGRAM, stmts: [\n"
operator|+
literal|"  {op: LOAD, target: A, name: Emp}]}"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|parseContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Tests parsing and un-parsing all kinds of operators. */
annotation|@
name|Test
specifier|public
name|void
name|testParse2
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'Emp';\n"
operator|+
literal|"DESCRIBE A;\n"
operator|+
literal|"DUMP A;\n"
operator|+
literal|"B = FOREACH A GENERATE 1, name;\n"
operator|+
literal|"B1 = FOREACH A {\n"
operator|+
literal|"  X = DISTINCT A;\n"
operator|+
literal|"  Y = FILTER X BY foo;\n"
operator|+
literal|"  Z = LIMIT Z 3;\n"
operator|+
literal|"  GENERATE 1, name;\n"
operator|+
literal|"}\n"
operator|+
literal|"C = FILTER B BY name;\n"
operator|+
literal|"D = DISTINCT C;\n"
operator|+
literal|"E = ORDER D BY $1 DESC, $2 ASC, $3;\n"
operator|+
literal|"F = ORDER E BY * DESC;\n"
operator|+
literal|"G = LIMIT F -10;\n"
operator|+
literal|"H = GROUP G ALL;\n"
operator|+
literal|"I = GROUP H BY e;\n"
operator|+
literal|"J = GROUP I BY (e1, e2);\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"{op: PROGRAM, stmts: [\n"
operator|+
literal|"  {op: LOAD, target: A, name: Emp},\n"
operator|+
literal|"  {op: DESCRIBE, relation: A},\n"
operator|+
literal|"  {op: DUMP, relation: A},\n"
operator|+
literal|"  {op: FOREACH, target: B, source: A, expList: [\n"
operator|+
literal|"    1,\n"
operator|+
literal|"    name]},\n"
operator|+
literal|"  {op: FOREACH, target: B1, source: A, nestedOps: [\n"
operator|+
literal|"    {op: DISTINCT, target: X, source: A},\n"
operator|+
literal|"    {op: FILTER, target: Y, source: X, condition: foo},\n"
operator|+
literal|"    {op: LIMIT, target: Z, source: Z, count: 3}], expList: [\n"
operator|+
literal|"    1,\n"
operator|+
literal|"    name]},\n"
operator|+
literal|"  {op: FILTER, target: C, source: B, condition: name},\n"
operator|+
literal|"  {op: DISTINCT, target: D, source: C},\n"
operator|+
literal|"  {op: ORDER, target: E, source: D},\n"
operator|+
literal|"  {op: ORDER, target: F, source: E},\n"
operator|+
literal|"  {op: LIMIT, target: G, source: F, count: -10},\n"
operator|+
literal|"  {op: GROUP, target: H, source: G},\n"
operator|+
literal|"  {op: GROUP, target: I, source: H, keys: [\n"
operator|+
literal|"    e]},\n"
operator|+
literal|"  {op: GROUP, target: J, source: I, keys: [\n"
operator|+
literal|"    e1,\n"
operator|+
literal|"    e2]}]}"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|parseContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScan
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'EMP';"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDump
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"DUMP A;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
specifier|final
name|String
name|out
init|=
literal|"(10,ACCOUNTING,NEW YORK)\n"
operator|+
literal|"(20,RESEARCH,DALLAS)\n"
operator|+
literal|"(30,SALES,CHICAGO)\n"
operator|+
literal|"(40,OPERATIONS,BOSTON)\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
operator|.
name|returns
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** VALUES is an extension to Pig. You can achieve the same effect in standard    * Pig by creating a text file. */
annotation|@
name|Test
specifier|public
name|void
name|testDumpValues
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = VALUES (1, 'a'), (2, 'b') AS (x: int, y: string);\n"
operator|+
literal|"DUMP A;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
decl_stmt|;
specifier|final
name|String
name|out
init|=
literal|"(1,a)\n(2,b)\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
operator|.
name|returns
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForeach
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"B = FOREACH A GENERATE DNAME, $2;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalProject(DNAME=[$1], LOC=[$2])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
comment|// foreach nested not implemented yet
annotation|@
name|Test
specifier|public
name|void
name|testForeachNested
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'EMP';\n"
operator|+
literal|"B = GROUP A BY DEPTNO;\n"
operator|+
literal|"C = FOREACH B {\n"
operator|+
literal|"  D = ORDER A BY SAL DESC;\n"
operator|+
literal|"  E = LIMIT D 3;\n"
operator|+
literal|"  GENERATE E.DEPTNO, E.EMPNO;\n"
operator|+
literal|"}"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalProject(DNAME=[$1], LOC=[$2])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'EMP';\n"
operator|+
literal|"B = GROUP A BY DEPTNO;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalAggregate(group=[{7}], A=[COLLECT($8)])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1], JOB=[$2], MGR=[$3], HIREDATE=[$4], SAL=[$5], COMM=[$6], DEPTNO=[$7], $f8=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupExample
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|pre
init|=
literal|"A = VALUES ('John',18,4.0F),\n"
operator|+
literal|"('Mary',19,3.8F),\n"
operator|+
literal|"('Bill',20,3.9F),\n"
operator|+
literal|"('Joe',18,3.8F) AS (name:chararray,age:int,gpa:float);\n"
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|pre
operator|+
literal|"B = GROUP A BY age;\n"
operator|+
literal|"DUMP B;\n"
decl_stmt|;
name|pig
argument_list|(
name|b
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"(18,{(John,18,4.0F),(Joe,18,3.8F)})"
argument_list|,
literal|"(19,{(Mary,19,3.8F)})"
argument_list|,
literal|"(20,{(Bill,20,3.9F)})"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctExample
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|pre
init|=
literal|"A = VALUES (8,3,4),\n"
operator|+
literal|"(1,2,3),\n"
operator|+
literal|"(4,3,3),\n"
operator|+
literal|"(4,3,3),\n"
operator|+
literal|"(1,2,3) AS (a1:int,a2:int,a3:int);\n"
decl_stmt|;
specifier|final
name|String
name|x
init|=
name|pre
operator|+
literal|"X = DISTINCT A;\n"
operator|+
literal|"DUMP X;\n"
decl_stmt|;
name|pig
argument_list|(
name|x
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"(1,2,3)"
argument_list|,
literal|"(4,3,3)"
argument_list|,
literal|"(8,3,4)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"B = FILTER A BY DEPTNO;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalFilter(condition=[$0])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterExample
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|pre
init|=
literal|"A = VALUES (1,2,3),\n"
operator|+
literal|"(4,2,1),\n"
operator|+
literal|"(8,3,4),\n"
operator|+
literal|"(4,3,3),\n"
operator|+
literal|"(7,2,5),\n"
operator|+
literal|"(8,4,3) AS (f1:int,f2:int,f3:int);\n"
decl_stmt|;
specifier|final
name|String
name|x
init|=
name|pre
operator|+
literal|"X = FILTER A BY f3 == 3;\n"
operator|+
literal|"DUMP X;\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(1,2,3)\n"
operator|+
literal|"(4,3,3)\n"
operator|+
literal|"(8,4,3)\n"
decl_stmt|;
name|pig
argument_list|(
name|x
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|x2
init|=
name|pre
operator|+
literal|"X2 = FILTER A BY (f1 == 8) OR (NOT (f2+f3> f1));\n"
operator|+
literal|"DUMP X2;\n"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"(4,2,1)\n"
operator|+
literal|"(8,3,4)\n"
operator|+
literal|"(7,2,5)\n"
operator|+
literal|"(8,4,3)\n"
decl_stmt|;
name|pig
argument_list|(
name|x2
argument_list|)
operator|.
name|returns
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"B = LIMIT A 3;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalSort(fetch=[3])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimitExample
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|pre
init|=
literal|"A = VALUES (1,2,3),\n"
operator|+
literal|"(4,2,1),\n"
operator|+
literal|"(8,3,4),\n"
operator|+
literal|"(4,3,3),\n"
operator|+
literal|"(7,2,5),\n"
operator|+
literal|"(8,4,3) AS (f1:int,f2:int,f3:int);\n"
decl_stmt|;
specifier|final
name|String
name|x
init|=
name|pre
operator|+
literal|"X = LIMIT A 3;\n"
operator|+
literal|"DUMP X;\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(1,2,3)\n"
operator|+
literal|"(4,2,1)\n"
operator|+
literal|"(8,3,4)\n"
decl_stmt|;
name|pig
argument_list|(
name|x
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|x2
init|=
name|pre
operator|+
literal|"B = ORDER A BY f1 DESC, f2 ASC;\n"
operator|+
literal|"X2 = LIMIT B 3;\n"
operator|+
literal|"DUMP X2;\n"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"(8,3,4)\n"
operator|+
literal|"(8,4,3)\n"
operator|+
literal|"(7,2,5)\n"
decl_stmt|;
name|pig
argument_list|(
name|x2
argument_list|)
operator|.
name|returns
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrder
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"B = ORDER A BY DEPTNO DESC, DNAME;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], sort1=[$1], dir0=[DESC], dir1=[ASC])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderStar
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = LOAD 'DEPT';\n"
operator|+
literal|"B = ORDER A BY * DESC;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$0], sort1=[$1], sort2=[$2], dir0=[DESC], dir1=[DESC], dir2=[DESC])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderExample
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|pre
init|=
literal|"A = VALUES (1,2,3),\n"
operator|+
literal|"(4,2,1),\n"
operator|+
literal|"(8,3,4),\n"
operator|+
literal|"(4,3,3),\n"
operator|+
literal|"(7,2,5),\n"
operator|+
literal|"(8,4,3) AS (a1:int,a2:int,a3:int);\n"
decl_stmt|;
specifier|final
name|String
name|x
init|=
name|pre
operator|+
literal|"X = ORDER A BY a3 DESC;\n"
operator|+
literal|"DUMP X;\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(7,2,5)\n"
operator|+
literal|"(8,3,4)\n"
operator|+
literal|"(1,2,3)\n"
operator|+
literal|"(4,3,3)\n"
operator|+
literal|"(8,4,3)\n"
operator|+
literal|"(4,2,1)\n"
decl_stmt|;
name|pig
argument_list|(
name|x
argument_list|)
operator|.
name|returns
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** VALUES is an extension to Pig. You can achieve the same effect in standard    * Pig by creating a text file. */
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = VALUES (1, 'a'), (2, 'b') AS (x: int, y: string);\n"
operator|+
literal|"DUMP A;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesNested
parameter_list|()
throws|throws
name|ParseException
block|{
specifier|final
name|String
name|s
init|=
literal|"A = VALUES (1, {('a', true), ('b', false)}),\n"
operator|+
literal|" (2, {})\n"
operator|+
literal|"AS (x: int, y: bag {tuple(a: string, b: boolean)});\n"
operator|+
literal|"DUMP A;"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"LogicalValues(tuples=[[{ 1, [['a', true], ['b', false]] }, { 2, [] }]])\n"
decl_stmt|;
name|pig
argument_list|(
name|s
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PigletTest.java
end_comment

end_unit

