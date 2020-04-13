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
name|sql2rel
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
name|RelTraitDef
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
name|RelCollations
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
name|RelDistributions
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
name|Test
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

begin_class
class|class
name|RelFieldTrimmerTest
block|{
specifier|public
specifier|static
name|Frameworks
operator|.
name|ConfigBuilder
name|config
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
name|SCOTT_WITH_TEMPORAL
argument_list|)
argument_list|)
operator|.
name|traitDefs
argument_list|(
operator|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
operator|)
literal|null
argument_list|)
operator|.
name|programs
argument_list|(
name|Programs
operator|.
name|heuristicJoinOrder
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortExchangeFieldTrimmer
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
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
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|sortExchange
argument_list|(
name|RelDistributions
operator|.
name|hash
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|RelCollations
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelFieldTrimmer
name|fieldTrimmer
init|=
operator|new
name|RelFieldTrimmer
argument_list|(
literal|null
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|RelNode
name|trimmed
init|=
name|fieldTrimmer
operator|.
name|trim
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalSortExchange(distribution=[hash[1]], collation=[[0]])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|trimmed
argument_list|,
name|hasTree
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortExchangeFieldTrimmerWhenProjectCannotBeMerged
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
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
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|sortExchange
argument_list|(
name|RelDistributions
operator|.
name|hash
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|RelCollations
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelFieldTrimmer
name|fieldTrimmer
init|=
operator|new
name|RelFieldTrimmer
argument_list|(
literal|null
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|RelNode
name|trimmed
init|=
name|fieldTrimmer
operator|.
name|trim
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalProject(EMPNO=[$0])\n"
operator|+
literal|"  LogicalSortExchange(distribution=[hash[1]], collation=[[0]])\n"
operator|+
literal|"    LogicalProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"      LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|trimmed
argument_list|,
name|hasTree
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortExchangeFieldTrimmerWithEmptyCollation
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
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
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|sortExchange
argument_list|(
name|RelDistributions
operator|.
name|hash
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|RelCollations
operator|.
name|EMPTY
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelFieldTrimmer
name|fieldTrimmer
init|=
operator|new
name|RelFieldTrimmer
argument_list|(
literal|null
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|RelNode
name|trimmed
init|=
name|fieldTrimmer
operator|.
name|trim
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalSortExchange(distribution=[hash[1]], collation=[[]])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|trimmed
argument_list|,
name|hasTree
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortExchangeFieldTrimmerWithSingletonDistribution
parameter_list|()
block|{
specifier|final
name|RelBuilder
name|builder
init|=
name|RelBuilder
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
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"DEPTNO"
argument_list|)
argument_list|)
operator|.
name|sortExchange
argument_list|(
name|RelDistributions
operator|.
name|SINGLETON
argument_list|,
name|RelCollations
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"EMPNO"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ENAME"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelFieldTrimmer
name|fieldTrimmer
init|=
operator|new
name|RelFieldTrimmer
argument_list|(
literal|null
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|RelNode
name|trimmed
init|=
name|fieldTrimmer
operator|.
name|trim
argument_list|(
name|root
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"LogicalSortExchange(distribution=[single], collation=[[0]])\n"
operator|+
literal|"  LogicalProject(EMPNO=[$0], ENAME=[$1])\n"
operator|+
literal|"    LogicalTableScan(table=[[scott, EMP]])\n"
decl_stmt|;
name|assertThat
argument_list|(
name|trimmed
argument_list|,
name|hasTree
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
