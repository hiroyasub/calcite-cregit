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
name|plan
operator|.
name|RelOptUtil
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|PigRelBuilder
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
name|Util
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
comment|/**  * Unit test for {@link PigRelBuilder}.  */
end_comment

begin_class
specifier|public
class|class
name|PigRelBuilderTest
block|{
comment|/** Creates a config based on the "scott" schema. */
specifier|public
specifier|static
name|Frameworks
operator|.
name|ConfigBuilder
name|config
parameter_list|()
block|{
return|return
name|RelBuilderTest
operator|.
name|config
argument_list|()
return|;
block|}
comment|/** Converts a relational expression to a sting with linux line-endings. */
specifier|private
name|String
name|str
parameter_list|(
name|RelNode
name|r
parameter_list|)
block|{
return|return
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScan
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"LogicalTableScan(table=[[scott, EMP]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCogroup
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCross
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCube
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefine
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinct
parameter_list|()
block|{
comment|// Syntax:
comment|//   alias = DISTINCT alias [PARTITION BY partitioner] [PARALLEL n];
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|distinct
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"LogicalAggregate(group=[{0, 1, 2, 3, 4, 5, 6, 7}])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
comment|// Syntax:
comment|//  FILTER name BY expr
comment|// Example:
comment|//  output_var = FILTER input_var BY (field1 is not null);
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|load
argument_list|(
literal|"EMP.csv"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|isNotNull
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"MGR"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"LogicalFilter(condition=[IS NOT NULL($3)])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForeach
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
comment|// Syntax:
comment|//   alias = GROUP alias { ALL | BY expression}
comment|//     [, alias ALL | BY expression ...] [USING 'collected' | 'merge']
comment|//     [PARTITION BY partitioner] [PARALLEL n];
comment|// Equivalent to Pig Latin:
comment|//   r = GROUP e BY (deptno, job);
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|group
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|builder
operator|.
name|groupKey
argument_list|(
literal|"DEPTNO"
argument_list|,
literal|"JOB"
argument_list|)
operator|.
name|alias
argument_list|(
literal|"e"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|""
operator|+
literal|"LogicalAggregate(group=[{2, 7}], EMP=[COLLECT($8)])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1], JOB=[$2], MGR=[$3], HIREDATE=[$4], SAL=[$5], COMM=[$6], DEPTNO=[$7], $f8=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup2
parameter_list|()
block|{
comment|// Equivalent to Pig Latin:
comment|//   r = GROUP e BY deptno, d BY deptno;
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"DEPT"
argument_list|)
operator|.
name|group
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|builder
operator|.
name|groupKey
argument_list|(
literal|"DEPTNO"
argument_list|)
operator|.
name|alias
argument_list|(
literal|"e"
argument_list|)
argument_list|,
name|builder
operator|.
name|groupKey
argument_list|(
literal|"DEPTNO"
argument_list|)
operator|.
name|alias
argument_list|(
literal|"d"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"LogicalJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"  LogicalAggregate(group=[{0}], EMP=[COLLECT($8)])\n"
operator|+
literal|"    LogicalProject(EMPNO=[$0], ENAME=[$1], JOB=[$2], MGR=[$3], HIREDATE=[$4], SAL=[$5], COMM=[$6], DEPTNO=[$7], $f8=[ROW($0, $1, $2, $3, $4, $5, $6, $7)])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n  LogicalAggregate(group=[{0}], DEPT=[COLLECT($3)])\n"
operator|+
literal|"    LogicalProject(DEPTNO=[$0], DNAME=[$1], LOC=[$2], $f3=[ROW($0, $1, $2)])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImport
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinInner
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinOuter
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoad
parameter_list|()
block|{
comment|// Syntax:
comment|//   LOAD 'data' [USING function] [AS schema];
comment|// Equivalent to Pig Latin:
comment|//   LOAD 'EMPS.csv'
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|root
init|=
name|builder
operator|.
name|load
argument_list|(
literal|"EMP.csv"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|str
argument_list|(
name|root
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"LogicalTableScan(table=[[scott, EMP]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapReduce
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderBy
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRank
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSample
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSplit
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStore
parameter_list|()
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
block|}
block|}
end_class

begin_comment
comment|// End PigRelBuilderTest.java
end_comment

end_unit

