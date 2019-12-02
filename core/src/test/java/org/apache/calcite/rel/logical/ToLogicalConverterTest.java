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
name|rel
operator|.
name|logical
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
name|EnumerableConvention
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
name|enumerable
operator|.
name|EnumerableInterpreterRule
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
name|plan
operator|.
name|RelOptRule
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
name|rel
operator|.
name|rules
operator|.
name|ProjectToWindowRule
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
name|rex
operator|.
name|RexCorrelVariable
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|parser
operator|.
name|SqlParser
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
name|test
operator|.
name|RelBuilderTest
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
name|FrameworkConfig
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
name|Planner
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
name|Program
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
name|Programs
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
name|RelBuilder
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
name|RuleSets
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
name|TestUtil
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
name|ImmutableSet
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|hasTree
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
comment|/**  * Tests for {@link ToLogicalConverter}.  */
end_comment

begin_class
specifier|public
class|class
name|ToLogicalConverterTest
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|RelOptRule
argument_list|>
name|RULE_SET
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ProjectToWindowRule
operator|.
name|PROJECT
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_CORRELATE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_PROJECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_FILTER_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_AGGREGATE_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_SORT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_LIMIT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_COLLECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_UNCOLLECT_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_UNION_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_WINDOW_RULE
argument_list|,
name|EnumerableRules
operator|.
name|ENUMERABLE_TABLE_SCAN_RULE
argument_list|,
name|EnumerableInterpreterRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlToRelConverter
operator|.
name|Config
name|DEFAULT_REL_CONFIG
init|=
name|SqlToRelConverter
operator|.
name|configBuilder
argument_list|()
operator|.
name|withTrimUnusedFields
argument_list|(
literal|false
argument_list|)
operator|.
name|withConvertTableAccess
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|FrameworkConfig
name|frameworkConfig
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema
init|=
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|)
decl_stmt|;
return|return
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|schema
argument_list|)
operator|.
name|sqlToRelConverterConfig
argument_list|(
name|DEFAULT_REL_CONFIG
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|RelBuilder
name|builder
parameter_list|()
block|{
return|return
name|RelBuilder
operator|.
name|create
argument_list|(
name|RelBuilderTest
operator|.
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelNode
name|rel
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|frameworkConfig
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
operator|.
name|rel
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|RelNode
name|toPhysical
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelOptPlanner
name|planner
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|planner
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|RULE_SET
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Program
name|program
init|=
name|Programs
operator|.
name|of
argument_list|(
name|RuleSets
operator|.
name|ofList
argument_list|(
name|planner
operator|.
name|getRules
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|program
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelNode
name|toLogical
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|rel
operator|.
name|accept
argument_list|(
operator|new
name|ToLogicalConverter
argument_list|(
name|builder
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|verify
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
name|expectedPhysical
parameter_list|,
name|String
name|expectedLogical
parameter_list|)
block|{
name|RelNode
name|physical
init|=
name|toPhysical
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|RelNode
name|logical
init|=
name|toLogical
argument_list|(
name|physical
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|physical
argument_list|,
name|hasTree
argument_list|(
name|expectedPhysical
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|logical
argument_list|,
name|hasTree
argument_list|(
name|expectedLogical
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   VALUES (true, 1), (false, -50) AS t(a, b)
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|values
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
operator|-
literal|50
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
literal|"EnumerableValues(tuples=[[{ true, 1 }, { false, -50 }]])\n"
argument_list|,
literal|"LogicalValues(tuples=[[{ true, 1 }, { false, -50 }]])\n"
argument_list|)
expr_stmt|;
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
name|RelNode
name|rel
init|=
name|builder
argument_list|()
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
literal|"EnumerableTableScan(table=[[scott, EMP]])\n"
argument_list|,
literal|"LogicalTableScan(table=[[scott, EMP]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProject
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT deptno
comment|//   FROM emp
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableProject(DEPTNO=[$7])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalProject(DEPTNO=[$7])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
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
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
comment|//   WHERE deptno = 10
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableFilter(condition=[=($7, 10)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalFilter(condition=[=($7, 10)])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
comment|//   ORDER BY 3
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|sort
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
comment|//   FETCH 10
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|limit
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableLimit(fetch=[10])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalSort(fetch=[10])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortLimit
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
comment|//   ORDER BY deptno DESC FETCH 10
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|sortLimit
argument_list|(
operator|-
literal|1
argument_list|,
literal|10
argument_list|,
name|builder
operator|.
name|desc
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableLimit(fetch=[10])\n"
operator|+
literal|"  EnumerableSort(sort0=[$7], dir0=[DESC])\n"
operator|+
literal|"    EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalSort(sort0=[$7], dir0=[DESC], fetch=[10])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregate
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT COUNT(empno) AS c
comment|//   FROM emp
comment|//   GROUP BY deptno
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"C"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableAggregate(group=[{7}], C=[COUNT($0)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalAggregate(group=[{7}], C=[COUNT($0)])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT *
comment|//   FROM emp
comment|//   JOIN dept ON emp.deptno = dept.deptno
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rel
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
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|"DEPTNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|"DEPTNO"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableHashJoin(condition=[=($7, $8)], joinType=[inner])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalJoin(condition=[=($7, $8)], joinType=[inner])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrelation
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|Holder
argument_list|<
name|RexCorrelVariable
argument_list|>
name|v
init|=
name|Holder
operator|.
name|of
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|variable
argument_list|(
name|v
argument_list|)
operator|.
name|scan
argument_list|(
literal|"DEPT"
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|LEFT
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
literal|0
argument_list|,
literal|"SAL"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|v
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableCorrelate(correlation=[$cor0], joinType=[left], requiredColumns=[{}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  EnumerableFilter(condition=[=($cor0.SAL, 1000)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalCorrelate(correlation=[$cor0], joinType=[left], requiredColumns=[{5}])\n"
operator|+
literal|"  LogicalTableScan(table=[[scott, EMP]])\n"
operator|+
literal|"  LogicalFilter(condition=[=($cor0.SAL, 1000)])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
comment|// Equivalent SQL:
comment|//   SELECT deptno FROM emp
comment|//   UNION ALL
comment|//   SELECT deptno FROM dept
specifier|final
name|RelBuilder
name|builder
init|=
name|builder
argument_list|()
decl_stmt|;
name|RelNode
name|rel
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"DEPT"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|union
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableUnion(all=[true])\n"
operator|+
literal|"  EnumerableProject(DEPTNO=[$0])\n"
operator|+
literal|"    EnumerableTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  EnumerableProject(DEPTNO=[$7])\n"
operator|+
literal|"    EnumerableTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalUnion(all=[true])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$0])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, DEPT]])\n"
operator|+
literal|"  LogicalProject(DEPTNO=[$7])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUncollect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select did \n"
operator|+
literal|"from unnest(select collect(\"department_id\") as deptid"
operator|+
literal|"            from \"department\") as t(did)"
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableUncollect\n"
operator|+
literal|"  EnumerableAggregate(group=[{}], DEPTID=[COLLECT($0)])\n"
operator|+
literal|"    JdbcToEnumerableConverter\n"
operator|+
literal|"      JdbcProject(department_id=[$0])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, department]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"Uncollect\n"
operator|+
literal|"  LogicalAggregate(group=[{}], DEPTID=[COLLECT($0)])\n"
operator|+
literal|"    LogicalProject(department_id=[$0])\n"
operator|+
literal|"      LogicalTableScan(table=[[foodmart, department]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWindow
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT rank() over (order by \"hire_date\") FROM \"employee\""
decl_stmt|;
name|String
name|expectedPhysial
init|=
literal|""
operator|+
literal|"EnumerableProject($0=[$17])\n"
operator|+
literal|"  EnumerableWindow(window#0=[window(partition {} order by [9] range between "
operator|+
literal|"UNBOUNDED PRECEDING and CURRENT ROW aggs [RANK()])])\n"
operator|+
literal|"    JdbcToEnumerableConverter\n"
operator|+
literal|"      JdbcTableScan(table=[[foodmart, employee]])\n"
decl_stmt|;
name|String
name|expectedLogical
init|=
literal|""
operator|+
literal|"LogicalProject($0=[$17])\n"
operator|+
literal|"  LogicalWindow(window#0=[window(partition {} order by [9] range between UNBOUNDED"
operator|+
literal|" PRECEDING and CURRENT ROW aggs [RANK()])])\n"
operator|+
literal|"    LogicalTableScan(table=[[foodmart, employee]])\n"
decl_stmt|;
name|verify
argument_list|(
name|rel
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expectedPhysial
argument_list|,
name|expectedLogical
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ToLogicalConverterTest.java
end_comment

end_unit

