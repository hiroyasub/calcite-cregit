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
name|adapter
operator|.
name|innodb
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
name|config
operator|.
name|CalciteSystemProperty
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
name|hint
operator|.
name|HintPredicates
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
name|hint
operator|.
name|HintStrategyTable
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|util
operator|.
name|Holder
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
name|Pair
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
name|Sources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|util
operator|.
name|Utils
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
name|ImmutableMap
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
name|Lists
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
name|AfterEach
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
name|BeforeEach
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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|OffsetDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|zone
operator|.
name|ZoneRules
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
name|Comparator
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.innodb} package.  *  *<p>Will read InnoDB data file {@code emp.ibd} and {@code dept.ibd}.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbAdapterTest
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|INNODB_MODEL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|Sources
operator|.
name|of
argument_list|(
name|InnodbAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model.json"
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
name|void
name|testSelectCount
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAll
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|all
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAll2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"DEPT\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbTableScan(table=[[test, DEPT]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=10; DNAME=ACCOUNTING; LOC=NEW YORK\n"
operator|+
literal|"DEPTNO=20; DNAME=RESEARCH; LOC=DALLAS\n"
operator|+
literal|"DEPTNO=30; DNAME=SALES; LOC=CHICAGO\n"
operator|+
literal|"DEPTNO=40; DNAME=OPERATIONS; LOC=BOSTON\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAllProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; ENAME=SMITH\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD\n"
operator|+
literal|"EMPNO=7566; ENAME=JONES\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAllOrderByAsc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" ORDER BY EMPNO ASC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|all
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAllOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" ORDER BY EMPNO DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbSort(sort0=[$0], dir0=[DESC])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|allReversed
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAllProjectSomeFieldsOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" ORDER BY EMPNO DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbSort(sort0=[$0], dir0=[DESC])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7934; ENAME=MILLER\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7566; ENAME=JONES\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN\n"
operator|+
literal|"EMPNO=7369; ENAME=SMITH\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKey
parameter_list|()
block|{
for|for
control|(
name|Integer
name|empno
range|:
name|empnoMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO = "
operator|+
name|empno
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, EMPNO="
operator|+
name|empno
operator|+
literal|")])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
name|empno
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO = 0"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, EMPNO=0)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyProjectAllFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT ENAME,EMPNO,JOB,AGE,MGR,HIREDATE,SAL,COMM,DEPTNO,EMAIL,"
operator|+
literal|"CREATE_DATETIME,CREATE_TIME,UPSERT_TIME FROM \"EMP\" WHERE EMPNO = 7499"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(ENAME=[$1], EMPNO=[$0], JOB=[$2], AGE=[$3], MGR=[$4], "
operator|+
literal|"HIREDATE=[$5], SAL=[$6], COMM=[$7], DEPTNO=[$8], EMAIL=[$9], "
operator|+
literal|"CREATE_DATETIME=[$10], CREATE_TIME=[$11], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, EMPNO=7499)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"ENAME=ALLEN; EMPNO=7499; JOB=SALESMAN; AGE=24; MGR=7698; HIREDATE=1981-02-20; "
operator|+
literal|"SAL=1600.00; COMM=300.00; DEPTNO=30; EMAIL=allen@calcite; "
operator|+
literal|"CREATE_DATETIME=2018-04-09 09:00:00; CREATE_TIME=09:00:00; "
operator|+
literal|"UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-04-09 09:00:00"
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,AGE,HIREDATE FROM \"EMP\" WHERE EMPNO = 7902"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], AGE=[$3], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, EMPNO=7902)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7902; AGE=28; HIREDATE=1981-12-03\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGt
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO> 7600"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>7600)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoGt
argument_list|(
literal|7600
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGt2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO> 7654"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>7654)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoGt
argument_list|(
literal|7654
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO> 10000"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>10000)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGte
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO>= 7600"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>=7600)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoGte
argument_list|(
literal|7600
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGte2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO>= 7654"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>=7654)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoGte
argument_list|(
literal|7654
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGte3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO>= 10000"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO>=10000)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLt
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO< 7800"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<7800)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoLt
argument_list|(
literal|7800
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLt2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO< 7839"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<7839)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoLt
argument_list|(
literal|7839
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLtNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO< 5000"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<5000)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLte
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO<= 7800"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<=7800)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoLte
argument_list|(
literal|7800
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLte2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO<= 7839"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<=7839)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|someEmpnoLte
argument_list|(
literal|7839
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryLteNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMPNO<= 5000"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, EMPNO<=5000)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtLtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO> 7600 AND EMPNO< 7900"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>7600, EMPNO<7900)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO> 7600 AND EMPNO<= 7900"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>7600, EMPNO<=7900)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGteLtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO>= 7369 AND EMPNO< 7900"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>=7369, EMPNO<7900)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; ENAME=SMITH\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD\n"
operator|+
literal|"EMPNO=7566; ENAME=JONES\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGteLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO>= 7788 AND EMPNO<= 7900"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>=7788, EMPNO<=7900)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtLtNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO> 7370 AND EMPNO< 7400"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>7370, EMPNO<7400)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGteLteEqualsProjectSomeFields
parameter_list|()
block|{
for|for
control|(
name|Integer
name|empno
range|:
name|empnoMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO FROM \"EMP\" WHERE EMPNO>= "
operator|+
name|empno
operator|+
literal|" AND EMPNO<= "
operator|+
name|empno
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0])\n"
operator|+
literal|"    InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, EMPNO="
operator|+
name|empno
operator|+
literal|")])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO="
operator|+
name|empno
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtProjectSomeFieldsOrderByAsc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO> 7600 ORDER BY EMPNO ASC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"      InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>7600)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; ENAME=MARTIN\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByPrimaryKeyRangeQueryGtProjectSomeFieldsOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE EMPNO> 7600 ORDER BY EMPNO DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbSort(sort0=[$0], dir0=[DESC])\n"
operator|+
literal|"      InnodbFilter(condition=[(PK_RANGE_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO>7600)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7934; ENAME=MILLER\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER\n"
operator|+
literal|"EMPNO=7839; ENAME=KING\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarchar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE ENAME = 'JONES'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=ENAME_KEY, ENAME=JONES)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7566
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharNotExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE ENAME = 'NOT_EXISTS'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=ENAME_KEY, "
operator|+
literal|"ENAME=NOT_EXISTS)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharInTransformToPointQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE ENAME IN ('FORD')"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=ENAME_KEY, ENAME=FORD)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7902
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharCaseInsensitive
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE ENAME = 'miller'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=ENAME_KEY, ENAME=miller)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7934
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT ENAME,UPSERT_TIME FROM \"EMP\" WHERE ENAME = 'BLAKE'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_POINT_QUERY, index=ENAME_KEY, ENAME=BLAKE)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"ENAME=BLAKE; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-06-01 14:45:00"
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharRangeQueryCoveringIndexOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT ENAME FROM \"EMP\" WHERE ENAME>= 'CLARK' AND ENAME< 'SMITHY' ORDER BY ENAME DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(ENAME=[$1])\n"
operator|+
literal|"    InnodbSort(sort0=[$1], dir0=[DESC])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=ENAME_KEY, "
operator|+
literal|"ENAME>=CLARK, ENAME<SMITHY)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"ENAME=SMITH\n"
operator|+
literal|"ENAME=SCOTT\n"
operator|+
literal|"ENAME=MILLER\n"
operator|+
literal|"ENAME=MARTIN\n"
operator|+
literal|"ENAME=KING\n"
operator|+
literal|"ENAME=JONES\n"
operator|+
literal|"ENAME=JAMES\n"
operator|+
literal|"ENAME=FORD\n"
operator|+
literal|"ENAME=CLARK\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharRangeQueryGtProjectSomeFieldsOrderByNonSkAsc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,AGE FROM \"EMP\" WHERE ENAME> 'MILLER' ORDER BY AGE ASC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbProject(EMPNO=[$0], ENAME=[$1], AGE=[$3])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=ENAME_KEY, "
operator|+
literal|"ENAME>MILLER)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; ENAME=SMITH; AGE=30\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD; AGE=41\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT; AGE=45\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; AGE=54\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkVarcharRangeQueryGtProjectSomeFieldsOrderByNonSkDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,SAL FROM \"EMP\" WHERE ENAME> 'MILLER' ORDER BY SAL DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableSort(sort0=[$2], dir0=[DESC])\n"
operator|+
literal|"  InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbProject(EMPNO=[$0], ENAME=[$1], SAL=[$6])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=ENAME_KEY, "
operator|+
literal|"ENAME>MILLER)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; ENAME=SCOTT; SAL=3000.00\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; SAL=1500.00\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD; SAL=1250.00\n"
operator|+
literal|"EMPNO=7369; ENAME=SMITH; SAL=800.00\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkDateProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,HIREDATE FROM \"EMP\" WHERE HIREDATE = '1980-12-17'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_POINT_QUERY, index=HIREDATE_KEY, "
operator|+
literal|"HIREDATE=1980-12-17)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; ENAME=SMITH; HIREDATE=1980-12-17\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkDateRangeQueryGtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DEPTNO,ENAME,HIREDATE FROM \"EMP\" WHERE HIREDATE> '1970-01-01'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(DEPTNO=[$8], ENAME=[$1], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=HIREDATE_KEY, "
operator|+
literal|"HIREDATE>1970-01-01)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=20; ENAME=SMITH; HIREDATE=1980-12-17\n"
operator|+
literal|"DEPTNO=30; ENAME=BLAKE; HIREDATE=1981-01-05\n"
operator|+
literal|"DEPTNO=20; ENAME=JONES; HIREDATE=1981-02-04\n"
operator|+
literal|"DEPTNO=30; ENAME=ALLEN; HIREDATE=1981-02-20\n"
operator|+
literal|"DEPTNO=30; ENAME=WARD; HIREDATE=1981-02-22\n"
operator|+
literal|"DEPTNO=10; ENAME=CLARK; HIREDATE=1981-06-09\n"
operator|+
literal|"DEPTNO=30; ENAME=TURNER; HIREDATE=1981-09-08\n"
operator|+
literal|"DEPTNO=30; ENAME=MARTIN; HIREDATE=1981-09-28\n"
operator|+
literal|"DEPTNO=10; ENAME=KING; HIREDATE=1981-11-17\n"
operator|+
literal|"DEPTNO=30; ENAME=JAMES; HIREDATE=1981-12-03\n"
operator|+
literal|"DEPTNO=20; ENAME=FORD; HIREDATE=1981-12-03\n"
operator|+
literal|"DEPTNO=10; ENAME=MILLER; HIREDATE=1982-01-23\n"
operator|+
literal|"DEPTNO=20; ENAME=SCOTT; HIREDATE=1987-04-19\n"
operator|+
literal|"DEPTNO=20; ENAME=ADAMS; HIREDATE=1987-05-23\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkDateRangeQueryLtProjectSomeFieldsOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DEPTNO,ENAME,HIREDATE FROM \"EMP\" WHERE HIREDATE< '2020-01-01' "
operator|+
literal|"ORDER BY HIREDATE DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(DEPTNO=[$8], ENAME=[$1], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbSort(sort0=[$5], dir0=[DESC])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=HIREDATE_KEY, "
operator|+
literal|"HIREDATE<2020-01-01)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=20; ENAME=ADAMS; HIREDATE=1987-05-23\n"
operator|+
literal|"DEPTNO=20; ENAME=SCOTT; HIREDATE=1987-04-19\n"
operator|+
literal|"DEPTNO=10; ENAME=MILLER; HIREDATE=1982-01-23\n"
operator|+
literal|"DEPTNO=20; ENAME=FORD; HIREDATE=1981-12-03\n"
operator|+
literal|"DEPTNO=30; ENAME=JAMES; HIREDATE=1981-12-03\n"
operator|+
literal|"DEPTNO=10; ENAME=KING; HIREDATE=1981-11-17\n"
operator|+
literal|"DEPTNO=30; ENAME=MARTIN; HIREDATE=1981-09-28\n"
operator|+
literal|"DEPTNO=30; ENAME=TURNER; HIREDATE=1981-09-08\n"
operator|+
literal|"DEPTNO=10; ENAME=CLARK; HIREDATE=1981-06-09\n"
operator|+
literal|"DEPTNO=30; ENAME=WARD; HIREDATE=1981-02-22\n"
operator|+
literal|"DEPTNO=30; ENAME=ALLEN; HIREDATE=1981-02-20\n"
operator|+
literal|"DEPTNO=20; ENAME=JONES; HIREDATE=1981-02-04\n"
operator|+
literal|"DEPTNO=30; ENAME=BLAKE; HIREDATE=1981-01-05\n"
operator|+
literal|"DEPTNO=20; ENAME=SMITH; HIREDATE=1980-12-17\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkDateRangeQueryGtLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DEPTNO,ENAME,HIREDATE FROM \"EMP\" WHERE HIREDATE< '1981-12-03' "
operator|+
literal|"AND HIREDATE>= '1981-06-09'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(DEPTNO=[$8], ENAME=[$1], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=HIREDATE_KEY, "
operator|+
literal|"HIREDATE>=1981-06-09, HIREDATE<1981-12-03)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=10; ENAME=CLARK; HIREDATE=1981-06-09\n"
operator|+
literal|"DEPTNO=30; ENAME=TURNER; HIREDATE=1981-09-08\n"
operator|+
literal|"DEPTNO=30; ENAME=MARTIN; HIREDATE=1981-09-28\n"
operator|+
literal|"DEPTNO=10; ENAME=KING; HIREDATE=1981-11-17\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkDateRangeQueryNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DEPTNO,ENAME,HIREDATE FROM \"EMP\" WHERE HIREDATE< '1981-12-03' "
operator|+
literal|"AND HIREDATE> '1981-12-01'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(DEPTNO=[$8], ENAME=[$1], HIREDATE=[$5])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=HIREDATE_KEY, "
operator|+
literal|"HIREDATE>1981-12-01, "
operator|+
literal|"HIREDATE<1981-12-03)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTime
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE CREATE_TIME = '12:12:56'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=CREATE_TIME_KEY, "
operator|+
literal|"CREATE_TIME=12:12:56)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7654
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimeRangeQueryGtLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,CREATE_TIME FROM \"EMP\" WHERE CREATE_TIME> '12:00:00' "
operator|+
literal|"AND CREATE_TIME<= '18:00:00'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], CREATE_TIME=[$11])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=CREATE_TIME_KEY, "
operator|+
literal|"CREATE_TIME>12:00:00, CREATE_TIME<=18:00:00)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; ENAME=SCOTT; CREATE_TIME=12:12:12\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; CREATE_TIME=12:12:56\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES; CREATE_TIME=12:19:00\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE; CREATE_TIME=14:45:00\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimeRangeQueryNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,UPSERT_TIME FROM \"EMP\" WHERE CREATE_TIME> '23:50:00' "
operator|+
literal|"AND CREATE_TIME< '23:59:00'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=CREATE_TIME_KEY, "
operator|+
literal|"CREATE_TIME>23:50:00, "
operator|+
literal|"CREATE_TIME<23:59:00)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimestamp
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE UPSERT_TIME = '"
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|"'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=UPSERT_TIME_KEY, UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|")])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7654
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimestampRangeQueryGteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,UPSERT_TIME FROM \"EMP\" WHERE UPSERT_TIME>= '2000-01-01 00:00:00'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=UPSERT_TIME_KEY, "
operator|+
literal|"UPSERT_TIME>=2000-01-01 00:00:00)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7566; ENAME=JONES; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2015-03-09 22:16:30"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2016-09-02 23:15:01"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-17 22:01:37"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-18 23:11:06"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-04-09 09:00:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-06-01 14:45:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-05-29 00:00:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7839; ENAME=KING; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-06-08 15:15:15"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-07-28 12:12:12"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7782; ENAME=CLARK; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-09-30 02:14:56"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-11-16 10:26:40"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7369; ENAME=SMITH; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2020-01-01 18:35:40"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2020-01-02 12:19:00"
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimestampRangeQueryLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,UPSERT_TIME FROM \"EMP\" WHERE UPSERT_TIME<= '2018-09-04 12:12:56'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=UPSERT_TIME_KEY, "
operator|+
literal|"UPSERT_TIME<=2018-09-04 12:12:56)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7566; ENAME=JONES; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2015-03-09 22:16:30"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2016-09-02 23:15:01"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-17 22:01:37"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-18 23:11:06"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-04-09 09:00:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-06-01 14:45:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimestampRangeQueryGtLteProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,UPSERT_TIME FROM \"EMP\" WHERE UPSERT_TIME> '"
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-18 23:11:06"
argument_list|)
operator|+
literal|"' AND UPSERT_TIME<= '"
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|"'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=UPSERT_TIME_KEY, UPSERT_TIME>"
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-18 23:11:06"
argument_list|)
operator|+
literal|", UPSERT_TIME<="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|")])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7499; ENAME=ALLEN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-04-09 09:00:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-06-01 14:45:00"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkTimestampRangeQueryNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,UPSERT_TIME FROM \"EMP\" WHERE UPSERT_TIME> '2020-08-18 13:11:06' "
operator|+
literal|"AND UPSERT_TIME<= '2020-08-18 23:11:06'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], UPSERT_TIME=[$12])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=UPSERT_TIME_KEY, "
operator|+
literal|"UPSERT_TIME>2020-08-18 13:11:06, UPSERT_TIME<=2020-08-18 23:11:06)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallint
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE AGE = 30"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=AGE_KEY, AGE=30)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7369
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallint2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE AGE = 32"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=AGE_KEY, AGE=32)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7782
argument_list|,
literal|7934
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySmallintRangeQueryLtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,AGE FROM \"EMP\" WHERE AGE< 30"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], AGE=[$3])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE<30)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7839; ENAME=KING; AGE=22\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN; AGE=24\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; AGE=27\n"
operator|+
literal|"EMPNO=7566; ENAME=JONES; AGE=28\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD; AGE=28\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallintRangeQueryGtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,AGE FROM \"EMP\" WHERE AGE> 30"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], AGE=[$3])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE>30)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7782; ENAME=CLARK; AGE=32\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER; AGE=32\n"
operator|+
literal|"EMPNO=7876; ENAME=ADAMS; AGE=35\n"
operator|+
literal|"EMPNO=7698; ENAME=BLAKE; AGE=38\n"
operator|+
literal|"EMPNO=7900; ENAME=JAMES; AGE=40\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD; AGE=41\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT; AGE=45\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; AGE=54\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallintRangeQueryGtLtProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,AGE FROM \"EMP\" WHERE AGE> 30 AND AGE< 35"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], AGE=[$3])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE>30, AGE<35)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7782; ENAME=CLARK; AGE=32\n"
operator|+
literal|"EMPNO=7934; ENAME=MILLER; AGE=32\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallintNothing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE AGE = 100"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=AGE_KEY, AGE=100)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallintRangeQueryNoting
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE AGE> 80"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE>80)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkSmallintRangeQueryLtProjectSomeFieldsOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,AGE FROM \"EMP\" WHERE AGE< 32 ORDER BY AGE DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], AGE=[$3])\n"
operator|+
literal|"    InnodbSort(sort0=[$3], dir0=[DESC])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE<32)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; ENAME=SMITH; AGE=30\n"
operator|+
literal|"EMPNO=7902; ENAME=FORD; AGE=28\n"
operator|+
literal|"EMPNO=7566; ENAME=JONES; AGE=28\n"
operator|+
literal|"EMPNO=7654; ENAME=MARTIN; AGE=27\n"
operator|+
literal|"EMPNO=7499; ENAME=ALLEN; AGE=24\n"
operator|+
literal|"EMPNO=7839; ENAME=KING; AGE=22\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkLimitedLengthVarcharConditionNotPushDown
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE EMAIL = 'king@calcite'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectBySkLimitedLengthVarcharConditionPushDown
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME,EMAIL FROM \"EMP\" WHERE EMAIL> 'kkk'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1], EMAIL=[$9])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=EMAIL_KEY, EMAIL>kkk)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; ENAME=MARTIN; EMAIL=martin@calcite\n"
operator|+
literal|"EMPNO=7788; ENAME=SCOTT; EMAIL=scott@calcite\n"
operator|+
literal|"EMPNO=7369; ENAME=SMITH; EMAIL=smith@calcite\n"
operator|+
literal|"EMPNO=7844; ENAME=TURNER; EMAIL=turner@calcite\n"
operator|+
literal|"EMPNO=7521; ENAME=WARD; EMAIL=ward@calcite\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkDateTimeRangeQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,CREATE_DATETIME,JOB FROM \"EMP\" WHERE "
operator|+
literal|"CREATE_DATETIME>= '2018-09-02 12:12:56'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], CREATE_DATETIME=[$10], JOB=[$2])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=CREATE_DATETIME_JOB_KEY, "
operator|+
literal|"CREATE_DATETIME>=2018-09-02 12:12:56)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; CREATE_DATETIME=2018-09-02 12:12:56; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7902; CREATE_DATETIME=2019-05-29 00:00:00; JOB=ANALYST\n"
operator|+
literal|"EMPNO=7839; CREATE_DATETIME=2019-06-08 15:15:15; JOB=PRESIDENT\n"
operator|+
literal|"EMPNO=7788; CREATE_DATETIME=2019-07-28 12:12:12; JOB=ANALYST\n"
operator|+
literal|"EMPNO=7782; CREATE_DATETIME=2019-09-30 02:14:56; JOB=MANAGER\n"
operator|+
literal|"EMPNO=7521; CREATE_DATETIME=2019-11-16 10:26:40; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7369; CREATE_DATETIME=2020-01-01 18:35:40; JOB=CLERK\n"
operator|+
literal|"EMPNO=7900; CREATE_DATETIME=2020-01-02 12:19:00; JOB=CLERK\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkDateTimeVarcharPointQueryCoveringIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,CREATE_DATETIME,JOB FROM \"EMP\" WHERE "
operator|+
literal|"CREATE_DATETIME = '2018-09-02 12:12:56' AND JOB = 'SALESMAN'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], CREATE_DATETIME=[$10], JOB=[$2])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_POINT_QUERY, index=CREATE_DATETIME_JOB_KEY, "
operator|+
literal|"CREATE_DATETIME=2018-09-02 12:12:56,JOB=SALESMAN)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7654; CREATE_DATETIME=2018-09-02 12:12:56; JOB=SALESMAN\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkTinyIntVarcharPointQueryCoveringIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,JOB FROM \"EMP\" WHERE DEPTNO = 20 AND JOB = 'ANALYST'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], DEPTNO=[$8], JOB=[$2])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_POINT_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO=20,JOB=ANALYST)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; DEPTNO=20; JOB=ANALYST\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; JOB=ANALYST\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkRangeQueryPushDownPartialCondition
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,ENAME FROM \"EMP\" WHERE DEPTNO = 20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>=20, DEPTNO<=20)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; DEPTNO=20; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; ENAME=FORD\n"
operator|+
literal|"EMPNO=7369; DEPTNO=20; ENAME=SMITH\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; ENAME=JONES\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkRangeQueryPushDownPartialCondition2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,JOB FROM \"EMP\" WHERE JOB = 'SALESMAN' AND DEPTNO> 20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>20)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7499; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7521; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7654; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7844; DEPTNO=30; JOB=SALESMAN\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkRangeQueryPushDownPartialCondition3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,JOB FROM \"EMP\" WHERE JOB>= 'SALE' AND DEPTNO>= 20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableCalc(expr#0..12=[{inputs}], expr#13=['SALE'], "
operator|+
literal|"expr#14=[>=($t2, $t13)], "
operator|+
literal|"EMPNO=[$t0], DEPTNO=[$t8], JOB=[$t2], $condition=[$t14])\n"
operator|+
literal|"  InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>=20)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7499; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7521; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7654; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7844; DEPTNO=30; JOB=SALESMAN\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkBreakLeftPrefixRuleConditionNotPushDown
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE JOB = 'CLERK'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkTinyIntDecimalDecimalPointQueryProjectAllFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE DEPTNO = 30 AND SAL = 1250 AND COMM = 500.00"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbFilter(condition=[(SK_POINT_QUERY, index=DEPTNO_SAL_COMM_KEY, "
operator|+
literal|"DEPTNO=30,SAL=1250,COMM=500.00)])\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
name|some
argument_list|(
literal|7521
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkTinyIntDecimalDecimalPointQueryProjectSomeFields
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,ENAME FROM \"EMP\" WHERE DEPTNO = 30 AND SAL = 1250 AND COMM = 500.00"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_POINT_QUERY, index=DEPTNO_SAL_COMM_KEY, "
operator|+
literal|"DEPTNO=30,SAL=1250,COMM=500.00)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7521; ENAME=WARD\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkWithSameLeftPrefixChooseOneIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,ENAME FROM \"EMP\" WHERE DEPTNO = 20 AND SAL> 30"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>=20, DEPTNO<=20)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; DEPTNO=20; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; ENAME=FORD\n"
operator|+
literal|"EMPNO=7369; DEPTNO=20; ENAME=SMITH\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; ENAME=JONES\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkForceIndexAsPrimaryKey
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,ENAME FROM \"EMP\"/*+ index(PRIMARY_KEY) */ WHERE "
operator|+
literal|"DEPTNO = 10 AND SAL> 500"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]], forceIndex=[PRIMARY_KEY])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7782; DEPTNO=10; ENAME=CLARK\n"
operator|+
literal|"EMPNO=7839; DEPTNO=10; ENAME=KING\n"
operator|+
literal|"EMPNO=7934; DEPTNO=10; ENAME=MILLER\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkWithSameLeftPrefixForceIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,ENAME FROM \"EMP\"/*+ index(DEPTNO_SAL_COMM_KEY) */ WHERE "
operator|+
literal|"DEPTNO = 20 AND SAL> 30"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_SAL_COMM_KEY, "
operator|+
literal|"DEPTNO>=20, DEPTNO<=20)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]], forceIndex=[DEPTNO_SAL_COMM_KEY])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7369; DEPTNO=20; ENAME=SMITH\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; ENAME=ADAMS\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; ENAME=JONES\n"
operator|+
literal|"EMPNO=7788; DEPTNO=20; ENAME=SCOTT\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; ENAME=FORD\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkWithSameLeftPrefixForceIndexCoveringIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,MGR FROM \"EMP\"/*+ index(DEPTNO_MGR_KEY) */ WHERE DEPTNO> 0"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], DEPTNO=[$8], MGR=[$4])\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_MGR_KEY, DEPTNO>0)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]], forceIndex=[DEPTNO_MGR_KEY])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7839; DEPTNO=10; MGR=null\n"
operator|+
literal|"EMPNO=7934; DEPTNO=10; MGR=7782\n"
operator|+
literal|"EMPNO=7782; DEPTNO=10; MGR=7839\n"
operator|+
literal|"EMPNO=7788; DEPTNO=20; MGR=7566\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; MGR=7566\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; MGR=7788\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; MGR=7839\n"
operator|+
literal|"EMPNO=7369; DEPTNO=20; MGR=7902\n"
operator|+
literal|"EMPNO=7499; DEPTNO=30; MGR=7698\n"
operator|+
literal|"EMPNO=7521; DEPTNO=30; MGR=7698\n"
operator|+
literal|"EMPNO=7654; DEPTNO=30; MGR=7698\n"
operator|+
literal|"EMPNO=7844; DEPTNO=30; MGR=7698\n"
operator|+
literal|"EMPNO=7900; DEPTNO=30; MGR=7698\n"
operator|+
literal|"EMPNO=7698; DEPTNO=30; MGR=7839\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupByFilterPushDown
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DEPTNO,SUM(SAL) AS TOTAL_SAL FROM EMP WHERE AGE> 30 GROUP BY DEPTNO"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableAggregate(group=[{8}], TOTAL_SAL=[$SUM0($6)])\n"
operator|+
literal|"  InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbFilter(condition=[(SK_RANGE_QUERY, index=AGE_KEY, AGE>30)])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"DEPTNO=20; TOTAL_SAL=4100.00\n"
operator|+
literal|"DEPTNO=10; TOTAL_SAL=3750.00\n"
operator|+
literal|"DEPTNO=30; TOTAL_SAL=6550.00\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinProjectAndFilterPushDown
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,EMP.DEPTNO,JOB,DNAME FROM \"EMP\" JOIN \"DEPT\" "
operator|+
literal|"ON EMP.DEPTNO = DEPT.DEPTNO AND EMP.DEPTNO = 20"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableHashJoin(condition=[=($2, $3)], joinType=[inner])\n"
operator|+
literal|"    InnodbToEnumerableConverter\n"
operator|+
literal|"      InnodbProject(EMPNO=[$0], JOB=[$2], DEPTNO=[$8])\n"
operator|+
literal|"        InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>=20, DEPTNO<=20)])\n"
operator|+
literal|"          InnodbTableScan(table=[[test, EMP]])\n"
operator|+
literal|"    InnodbToEnumerableConverter\n"
operator|+
literal|"      InnodbProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, DEPT]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7788; DEPTNO=20; JOB=ANALYST; DNAME=RESEARCH\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; JOB=ANALYST; DNAME=RESEARCH\n"
operator|+
literal|"EMPNO=7369; DEPTNO=20; JOB=CLERK; DNAME=RESEARCH\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; JOB=CLERK; DNAME=RESEARCH\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; JOB=MANAGER; DNAME=RESEARCH\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinProjectAndFilterPushDown2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,EMP.DEPTNO,JOB,DNAME FROM \"EMP\" JOIN \"DEPT\" "
operator|+
literal|"ON EMP.DEPTNO = DEPT.DEPTNO AND EMP.EMPNO = 7900"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableHashJoin(condition=[=($2, $3)], joinType=[inner])\n"
operator|+
literal|"    InnodbToEnumerableConverter\n"
operator|+
literal|"      InnodbProject(EMPNO=[$0], JOB=[$2], DEPTNO=[$8])\n"
operator|+
literal|"        InnodbFilter(condition=[(PK_POINT_QUERY, index=PRIMARY_KEY, "
operator|+
literal|"EMPNO=7900)])\n"
operator|+
literal|"          InnodbTableScan(table=[[test, EMP]])\n"
operator|+
literal|"    InnodbToEnumerableConverter\n"
operator|+
literal|"      InnodbProject(DEPTNO=[$0], DNAME=[$1])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, DEPT]])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7900; DEPTNO=30; JOB=CLERK; DNAME=SALES\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectFilterNoIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\" WHERE MGR = 7839"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectForceIndexCoveringIndex
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,MGR FROM \"EMP\"/*+ index(DEPTNO_MGR_KEY) */ WHERE MGR = 7839"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"  InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbProject(EMPNO=[$0], MGR=[$4])\n"
operator|+
literal|"      InnodbTableScan(table=[[test, EMP]], forceIndex=[DEPTNO_MGR_KEY])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectForceIndexIncorrectIndexName
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"EMP\"/*+ index(NOT_EXISTS) */ WHERE MGR = 7839"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"InnodbToEnumerableConverter\n"
operator|+
literal|"    InnodbTableScan(table=[[test, EMP]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectByMultipleSkForceIndexOrderByDesc
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT EMPNO,DEPTNO,JOB FROM \"EMP\"/*+ index(DEPTNO_JOB_KEY) */ WHERE DEPTNO> 10 "
operator|+
literal|"ORDER BY DEPTNO DESC,JOB DESC"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=InnodbToEnumerableConverter\n"
operator|+
literal|"  InnodbProject(EMPNO=[$0], DEPTNO=[$8], JOB=[$2])\n"
operator|+
literal|"    InnodbSort(sort0=[$8], sort1=[$2], dir0=[DESC], dir1=[DESC])\n"
operator|+
literal|"      InnodbFilter(condition=[(SK_RANGE_QUERY, index=DEPTNO_JOB_KEY, "
operator|+
literal|"DEPTNO>10)])\n"
operator|+
literal|"        InnodbTableScan(table=[[test, EMP]], forceIndex=[DEPTNO_JOB_KEY])\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EMPNO=7844; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7654; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7521; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7499; DEPTNO=30; JOB=SALESMAN\n"
operator|+
literal|"EMPNO=7698; DEPTNO=30; JOB=MANAGER\n"
operator|+
literal|"EMPNO=7900; DEPTNO=30; JOB=CLERK\n"
operator|+
literal|"EMPNO=7566; DEPTNO=20; JOB=MANAGER\n"
operator|+
literal|"EMPNO=7876; DEPTNO=20; JOB=CLERK\n"
operator|+
literal|"EMPNO=7369; DEPTNO=20; JOB=CLERK\n"
operator|+
literal|"EMPNO=7902; DEPTNO=20; JOB=ANALYST\n"
operator|+
literal|"EMPNO=7788; DEPTNO=20; JOB=ANALYST\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectNotExistTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM \"NOT_EXIST\""
argument_list|)
operator|.
name|failsAtValidation
argument_list|(
literal|"Object 'NOT_EXIST' not found"
argument_list|)
expr_stmt|;
block|}
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|>
name|rows
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
literal|7369
argument_list|,
literal|"EMPNO=7369; ENAME=SMITH; JOB=CLERK; AGE=30; MGR=7902; "
operator|+
literal|"HIREDATE=1980-12-17; SAL=800.00; COMM=null; DEPTNO=20; EMAIL=smith@calcite; "
operator|+
literal|"CREATE_DATETIME=2020-01-01 18:35:40; CREATE_TIME=18:35:40; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2020-01-01 18:35:40"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7499
argument_list|,
literal|"EMPNO=7499; ENAME=ALLEN; JOB=SALESMAN; AGE=24; MGR=7698; "
operator|+
literal|"HIREDATE=1981-02-20; SAL=1600.00; COMM=300.00; DEPTNO=30; EMAIL=allen@calcite; "
operator|+
literal|"CREATE_DATETIME=2018-04-09 09:00:00; CREATE_TIME=09:00:00; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-04-09 09:00:00"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7521
argument_list|,
literal|"EMPNO=7521; ENAME=WARD; JOB=SALESMAN; AGE=41; MGR=7698; "
operator|+
literal|"HIREDATE=1981-02-22; SAL=1250.00; COMM=500.00; DEPTNO=30; EMAIL=ward@calcite; "
operator|+
literal|"CREATE_DATETIME=2019-11-16 10:26:40; CREATE_TIME=10:26:40; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-11-16 10:26:40"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7566
argument_list|,
literal|"EMPNO=7566; ENAME=JONES; JOB=MANAGER; AGE=28; MGR=7839; "
operator|+
literal|"HIREDATE=1981-02-04; SAL=2975.00; COMM=null; DEPTNO=20; EMAIL=jones@calcite; "
operator|+
literal|"CREATE_DATETIME=2015-03-09 22:16:30; CREATE_TIME=22:16:30; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2015-03-09 22:16:30"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7654
argument_list|,
literal|"EMPNO=7654; ENAME=MARTIN; JOB=SALESMAN; AGE=27; MGR=7698; "
operator|+
literal|"HIREDATE=1981-09-28; SAL=1250.00; COMM=1400.00; DEPTNO=30; EMAIL=martin@calcite; "
operator|+
literal|"CREATE_DATETIME=2018-09-02 12:12:56; CREATE_TIME=12:12:56; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-09-02 12:12:56"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7698
argument_list|,
literal|"EMPNO=7698; ENAME=BLAKE; JOB=MANAGER; AGE=38; MGR=7839; "
operator|+
literal|"HIREDATE=1981-01-05; SAL=2850.00; COMM=null; DEPTNO=30; EMAIL=blake@calcite; "
operator|+
literal|"CREATE_DATETIME=2018-06-01 14:45:00; CREATE_TIME=14:45:00; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2018-06-01 14:45:00"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7782
argument_list|,
literal|"EMPNO=7782; ENAME=CLARK; JOB=MANAGER; AGE=32; MGR=7839; "
operator|+
literal|"HIREDATE=1981-06-09; SAL=2450.00; COMM=null; DEPTNO=10; EMAIL=null; "
operator|+
literal|"CREATE_DATETIME=2019-09-30 02:14:56; CREATE_TIME=02:14:56; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-09-30 02:14:56"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7788
argument_list|,
literal|"EMPNO=7788; ENAME=SCOTT; JOB=ANALYST; AGE=45; MGR=7566; "
operator|+
literal|"HIREDATE=1987-04-19; SAL=3000.00; COMM=null; DEPTNO=20; EMAIL=scott@calcite; "
operator|+
literal|"CREATE_DATETIME=2019-07-28 12:12:12; CREATE_TIME=12:12:12; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-07-28 12:12:12"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7839
argument_list|,
literal|"EMPNO=7839; ENAME=KING; JOB=PRESIDENT; AGE=22; MGR=null; "
operator|+
literal|"HIREDATE=1981-11-17; SAL=5000.00; COMM=null; DEPTNO=10; EMAIL=king@calcite; "
operator|+
literal|"CREATE_DATETIME=2019-06-08 15:15:15; CREATE_TIME=null; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-06-08 15:15:15"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7844
argument_list|,
literal|"EMPNO=7844; ENAME=TURNER; JOB=SALESMAN; AGE=54; MGR=7698; "
operator|+
literal|"HIREDATE=1981-09-08; SAL=1500.00; COMM=0.00; DEPTNO=30; EMAIL=turner@calcite; "
operator|+
literal|"CREATE_DATETIME=2017-08-17 22:01:37; CREATE_TIME=22:01:37; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-17 22:01:37"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7876
argument_list|,
literal|"EMPNO=7876; ENAME=ADAMS; JOB=CLERK; AGE=35; MGR=7788; "
operator|+
literal|"HIREDATE=1987-05-23; SAL=1100.00; COMM=null; DEPTNO=20; EMAIL=adams@calcite; "
operator|+
literal|"CREATE_DATETIME=null; CREATE_TIME=23:11:06; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2017-08-18 23:11:06"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7900
argument_list|,
literal|"EMPNO=7900; ENAME=JAMES; JOB=CLERK; AGE=40; MGR=7698; "
operator|+
literal|"HIREDATE=1981-12-03; SAL=950.00; COMM=null; DEPTNO=30; EMAIL=james@calcite; "
operator|+
literal|"CREATE_DATETIME=2020-01-02 12:19:00; CREATE_TIME=12:19:00; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2020-01-02 12:19:00"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7902
argument_list|,
literal|"EMPNO=7902; ENAME=FORD; JOB=ANALYST; AGE=28; MGR=7566; "
operator|+
literal|"HIREDATE=1981-12-03; SAL=3000.00; COMM=null; DEPTNO=20; EMAIL=ford@calcite; "
operator|+
literal|"CREATE_DATETIME=2019-05-29 00:00:00; CREATE_TIME=null; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2019-05-29 00:00:00"
argument_list|)
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
literal|7934
argument_list|,
literal|"EMPNO=7934; ENAME=MILLER; JOB=CLERK; AGE=32; MGR=7782; "
operator|+
literal|"HIREDATE=1982-01-23; SAL=1300.00; COMM=null; DEPTNO=10; EMAIL=null; "
operator|+
literal|"CREATE_DATETIME=2016-09-02 23:15:01; CREATE_TIME=23:15:01; UPSERT_TIME="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2016-09-02 23:15:01"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|>
name|reversedRows
init|=
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
name|Comparator
operator|.
name|reverseOrder
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
specifier|static
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|empnoMap
init|=
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|,
name|Pair
operator|::
name|getValue
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Whether to run this test.    */
specifier|private
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|CalciteSystemProperty
operator|.
name|TEST_INNODB
operator|.
name|value
argument_list|()
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|sql
parameter_list|(
name|String
name|sql
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
name|INNODB_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
return|;
block|}
name|Hook
operator|.
name|Closeable
name|closeable
decl_stmt|;
annotation|@
name|BeforeEach
specifier|public
name|void
name|before
parameter_list|()
block|{
name|this
operator|.
name|closeable
operator|=
name|Hook
operator|.
name|SQL2REL_CONVERTER_CONFIG_BUILDER
operator|.
name|addThread
argument_list|(
name|InnodbAdapterTest
operator|::
name|assignHints
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterEach
specifier|public
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|closeable
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|closeable
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|assignHints
parameter_list|(
name|Holder
argument_list|<
name|SqlToRelConverter
operator|.
name|Config
argument_list|>
name|configHolder
parameter_list|)
block|{
name|HintStrategyTable
name|strategies
init|=
name|HintStrategyTable
operator|.
name|builder
argument_list|()
operator|.
name|hintStrategy
argument_list|(
literal|"index"
argument_list|,
name|HintPredicates
operator|.
name|TABLE_SCAN
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|configHolder
operator|.
name|accept
argument_list|(
name|config
lambda|->
name|config
operator|.
name|withHintStrategyTable
argument_list|(
name|strategies
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|expectedLocalTime
parameter_list|(
name|String
name|dateTime
parameter_list|)
block|{
name|ZoneRules
name|rules
init|=
name|ZoneId
operator|.
name|systemDefault
argument_list|()
operator|.
name|getRules
argument_list|()
decl_stmt|;
name|LocalDateTime
name|ldt
init|=
name|Utils
operator|.
name|parseDateTimeText
argument_list|(
name|dateTime
argument_list|)
decl_stmt|;
name|Instant
name|instant
init|=
name|ldt
operator|.
name|toInstant
argument_list|(
name|ZoneOffset
operator|.
name|of
argument_list|(
literal|"+00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|ZoneOffset
name|standardOffset
init|=
name|rules
operator|.
name|getOffset
argument_list|(
name|instant
argument_list|)
decl_stmt|;
name|OffsetDateTime
name|odt
init|=
name|instant
operator|.
name|atOffset
argument_list|(
name|standardOffset
argument_list|)
decl_stmt|;
return|return
name|odt
operator|.
name|toLocalDateTime
argument_list|()
operator|.
name|format
argument_list|(
name|Utils
operator|.
name|TIME_FORMAT_TIMESTAMP
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|all
parameter_list|()
block|{
return|return
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|rows
argument_list|)
argument_list|)
operator|+
literal|"\n"
return|;
block|}
specifier|private
specifier|static
name|String
name|allReversed
parameter_list|()
block|{
return|return
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|reversedRows
argument_list|)
argument_list|)
operator|+
literal|"\n"
return|;
block|}
specifier|private
specifier|static
name|String
name|someEmpnoGt
parameter_list|(
name|int
name|empno
parameter_list|)
block|{
return|return
name|some
argument_list|(
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|>
name|empno
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|someEmpnoGte
parameter_list|(
name|int
name|empno
parameter_list|)
block|{
return|return
name|some
argument_list|(
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|>=
name|empno
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|someEmpnoLt
parameter_list|(
name|int
name|empno
parameter_list|)
block|{
return|return
name|some
argument_list|(
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|<
name|empno
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|someEmpnoLte
parameter_list|(
name|int
name|empno
parameter_list|)
block|{
return|return
name|some
argument_list|(
name|rows
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Pair
operator|::
name|getKey
argument_list|)
operator|.
name|filter
argument_list|(
name|i
lambda|->
name|i
operator|<=
name|empno
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|some
parameter_list|(
name|int
modifier|...
name|empnos
parameter_list|)
block|{
return|return
name|some
argument_list|(
name|Arrays
operator|.
name|stream
argument_list|(
name|empnos
argument_list|)
operator|.
name|boxed
argument_list|()
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|some
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|empnos
parameter_list|)
block|{
if|if
condition|(
name|empnos
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
name|empnos
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|empno
lambda|->
name|empnoMap
operator|.
name|get
argument_list|(
name|empno
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|join
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|join
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|empList
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|empList
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|String
operator|.
name|join
argument_list|(
literal|"\n"
argument_list|,
name|empList
argument_list|)
operator|+
literal|"\n"
return|;
block|}
block|}
end_class

end_unit

