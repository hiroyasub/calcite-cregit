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
name|pig
operator|.
name|PigAggregate
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
name|pig
operator|.
name|PigFilter
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
name|pig
operator|.
name|PigRel
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
name|pig
operator|.
name|PigRelFactories
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
name|pig
operator|.
name|PigRules
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
name|pig
operator|.
name|PigTable
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
name|FilterAggregateTransposeRule
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
name|FilterJoinRule
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
name|FilterJoinRule
operator|.
name|FilterIntoJoinRule
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
name|Schema
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
name|RelBuilderFactory
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|pigunit
operator|.
name|Cluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|pigunit
operator|.
name|PigTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|pigunit
operator|.
name|pig
operator|.
name|PigServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|test
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|io
operator|.
name|File
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
name|rel
operator|.
name|rules
operator|.
name|FilterJoinRule
operator|.
name|TRUE_PREDICATE
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
operator|.
name|EQUALS
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.pig} package that tests the  * building of {@link PigRel} relational expressions using {@link RelBuilder} and  * associated factories in {@link PigRelFactories}.  */
end_comment

begin_class
specifier|public
class|class
name|PigRelBuilderStyleTest
extends|extends
name|AbstractPigTest
block|{
specifier|public
name|PigRelBuilderStyleTest
parameter_list|()
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
literal|"Pigs don't like Windows"
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|,
name|is
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testScanAndFilter
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|GREATER_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'abc');"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(b,2)"
block|,
literal|"(c,3)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1751"
argument_list|)
specifier|public
name|void
name|testImplWithMultipleFilters
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|and
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|GREATER_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|call
argument_list|(
name|EQUALS
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc1"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"3"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'abc') AND (tc1 == '3');"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(c,3)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1751"
argument_list|)
specifier|public
name|void
name|testImplWithGroupByAndCount
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc0);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE group AS tc0, COUNT(t.tc1) AS c;\n"
operator|+
literal|"};"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(a,1)"
block|,
literal|"(b,1)"
block|,
literal|"(c,1)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithCountWithoutGroupBy
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc0"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t ALL;\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE COUNT(t.tc0) AS c;\n"
operator|+
literal|"};"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(3)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1751"
argument_list|)
specifier|public
name|void
name|testImplWithGroupByMultipleFields
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|"tc1"
argument_list|,
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc0, tc1);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE group.tc0 AS tc0, group.tc1 AS tc1, COUNT(t.tc1) AS c;\n"
operator|+
literal|"};"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(a,1,1)"
block|,
literal|"(b,2,1)"
block|,
literal|"(c,3,1)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithGroupByCountDistinct
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|"tc1"
argument_list|,
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|true
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc0, tc1);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  tc1_DISTINCT = DISTINCT t.tc1;\n"
operator|+
literal|"  GENERATE group.tc0 AS tc0, group.tc1 AS tc1, COUNT(tc1_DISTINCT) AS c;\n"
operator|+
literal|"};"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(a,1,1)"
block|,
literal|"(b,2,1)"
block|,
literal|"(c,3,1)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithJoin
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"s"
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
literal|0
argument_list|,
literal|"tc1"
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
literal|"sc0"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|GREATER_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'a');\n"
operator|+
literal|"s = LOAD 'target/data2.txt"
operator|+
literal|"' USING PigStorage() AS (sc0:chararray, sc1:chararray);\n"
operator|+
literal|"t = JOIN t BY tc1 , s BY sc0;"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(b,2,2,label2)"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"CALCITE-1751"
argument_list|)
specifier|public
name|void
name|testImplWithJoinAndGroupBy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|schema
init|=
name|createTestSchema
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|createRelBuilder
argument_list|(
name|schema
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|node
init|=
name|builder
operator|.
name|scan
argument_list|(
literal|"t"
argument_list|)
operator|.
name|scan
argument_list|(
literal|"s"
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
literal|"tc1"
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
literal|"sc0"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|GREATER_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"tc0"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|"tc1"
argument_list|)
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"sc1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|optimized
init|=
name|optimizeWithVolcano
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertScriptAndResults
argument_list|(
literal|"t"
argument_list|,
name|getPigScript
argument_list|(
name|optimized
argument_list|,
name|schema
argument_list|)
argument_list|,
literal|"t = LOAD 'target/data.txt"
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'abc');\n"
operator|+
literal|"s = LOAD 'target/data2.txt"
operator|+
literal|"' USING PigStorage() AS (sc0:chararray, sc1:chararray);\n"
operator|+
literal|"t = JOIN t BY tc1 LEFT, s BY sc0;\n"
operator|+
literal|"t = GROUP t BY (tc1);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE group AS tc1, COUNT(t.sc1) AS c;\n"
operator|+
literal|"};"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"(2,1)"
block|,
literal|"(3,0)"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SchemaPlus
name|createTestSchema
parameter_list|()
block|{
name|SchemaPlus
name|result
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"t"
argument_list|,
operator|new
name|PigTable
argument_list|(
literal|"target/data.txt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tc0"
block|,
literal|"tc1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
operator|new
name|PigTable
argument_list|(
literal|"target/data2.txt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sc0"
block|,
literal|"sc1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|RelBuilder
name|createRelBuilder
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|)
block|{
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|schema
argument_list|)
operator|.
name|context
argument_list|(
name|PigRelFactories
operator|.
name|ALL_PIG_REL_FACTORIES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|)
return|;
block|}
specifier|private
name|RelNode
name|optimizeWithVolcano
parameter_list|(
name|RelNode
name|root
parameter_list|)
block|{
name|RelOptPlanner
name|planner
init|=
name|getVolcanoPlanner
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|findBestExp
argument_list|()
return|;
block|}
specifier|private
name|RelOptPlanner
name|getVolcanoPlanner
parameter_list|(
name|RelNode
name|root
parameter_list|)
block|{
specifier|final
name|RelBuilderFactory
name|builderFactory
init|=
name|RelBuilder
operator|.
name|proto
argument_list|(
name|PigRelFactories
operator|.
name|ALL_PIG_REL_FACTORIES
argument_list|)
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
init|=
name|root
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
comment|// VolcanoPlanner
for|for
control|(
name|RelOptRule
name|r
range|:
name|PigRules
operator|.
name|ALL_PIG_OPT_RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|planner
operator|.
name|removeRule
argument_list|(
name|FilterAggregateTransposeRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|removeRule
argument_list|(
name|FilterJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|FilterAggregateTransposeRule
argument_list|(
name|PigFilter
operator|.
name|class
argument_list|,
name|builderFactory
argument_list|,
name|PigAggregate
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
operator|new
name|FilterIntoJoinRule
argument_list|(
literal|true
argument_list|,
name|builderFactory
argument_list|,
name|TRUE_PREDICATE
argument_list|)
argument_list|)
expr_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|root
argument_list|)
expr_stmt|;
return|return
name|planner
return|;
block|}
specifier|private
name|void
name|assertScriptAndResults
parameter_list|(
name|String
name|relAliasForStore
parameter_list|,
name|String
name|script
parameter_list|,
name|String
name|expectedScript
parameter_list|,
name|String
index|[]
name|expectedResults
parameter_list|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
name|expectedScript
argument_list|,
name|script
argument_list|)
expr_stmt|;
name|script
operator|=
name|script
operator|+
literal|"\nSTORE "
operator|+
name|relAliasForStore
operator|+
literal|" INTO 'myoutput';"
expr_stmt|;
name|PigTest
name|pigTest
init|=
operator|new
name|PigTest
argument_list|(
name|script
operator|.
name|split
argument_list|(
literal|"[\\r\\n]+"
argument_list|)
argument_list|)
decl_stmt|;
name|pigTest
operator|.
name|assertOutputAnyOrder
argument_list|(
name|expectedResults
argument_list|)
expr_stmt|;
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
name|String
name|getPigScript
parameter_list|(
name|RelNode
name|root
parameter_list|,
name|Schema
name|schema
parameter_list|)
block|{
name|PigRel
operator|.
name|Implementor
name|impl
init|=
operator|new
name|PigRel
operator|.
name|Implementor
argument_list|()
decl_stmt|;
name|impl
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|root
argument_list|)
expr_stmt|;
return|return
name|impl
operator|.
name|getScript
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdownPigServer
parameter_list|()
block|{
name|PigServer
name|pigServer
init|=
name|PigTest
operator|.
name|getPigServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|pigServer
operator|!=
literal|null
condition|)
block|{
name|pigServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setupDataFilesForPigServer
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"pigunit.exectype"
argument_list|,
name|Util
operator|.
name|getLocalTestMode
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Cluster
name|cluster
init|=
name|PigTest
operator|.
name|getCluster
argument_list|()
decl_stmt|;
comment|// Put the data files in target/ so they don't dirty the local git checkout
name|cluster
operator|.
name|update
argument_list|(
operator|new
name|Path
argument_list|(
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"target/data.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|update
argument_list|(
operator|new
name|Path
argument_list|(
name|getFullPathForTestDataFile
argument_list|(
literal|"data2.txt"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"target/data2.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PigRelBuilderStyleTest.java
end_comment

end_unit

