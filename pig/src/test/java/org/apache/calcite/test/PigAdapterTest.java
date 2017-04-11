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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|util
operator|.
name|List
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
comment|/**  * Tests for the {@code org.apache.calcite.adapter.pig} package.  */
end_comment

begin_class
specifier|public
class|class
name|PigAdapterTest
extends|extends
name|AbstractPigTest
block|{
comment|// Undo the %20 replacement of a space by URL
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|MODEL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|PigAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model.json"
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|replace
argument_list|(
literal|"%20"
argument_list|,
literal|" "
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testScanAndFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"t\" where \"tc0\"> 'abc'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"  PigFilter(condition=[>($0, 'abc')])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'abc');"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithMultipleFilters
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"t\" where \"tc0\"> 'abc' and \"tc1\" = '3'"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"  PigFilter(condition=[AND(>($0, 'abc'), =($1, '3'))])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = FILTER t BY (tc0> 'abc') AND (tc1 == '3');"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithGroupByAndCount
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(\"tc1\") c from \"t\" group by \"tc0\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"    PigAggregate(group=[{0}], C=[COUNT($1)])\n"
operator|+
literal|"      PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc0);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE group AS tc0, COUNT(t.tc1) AS C;\n"
operator|+
literal|"};"
argument_list|)
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
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(\"tc0\") c from \"t\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"  PigAggregate(group=[{}], C=[COUNT($0)])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t ALL;\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE COUNT(t.tc0) AS C;\n"
operator|+
literal|"};"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImplWithGroupByMultipleFields
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"t\" group by \"tc1\", \"tc0\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"  PigAggregate(group=[{0, 1}])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc0, tc1);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  GENERATE group.tc0 AS tc0, group.tc1 AS tc1;\n"
operator|+
literal|"};"
argument_list|)
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
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(distinct \"tc0\") c from \"t\" group by \"tc1\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"    PigAggregate(group=[{1}], C=[COUNT(DISTINCT $0)])\n"
operator|+
literal|"      PigTableScan(table=[[PIG, t]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"t = GROUP t BY (tc1);\n"
operator|+
literal|"t = FOREACH t {\n"
operator|+
literal|"  tc0_DISTINCT = DISTINCT t.tc0;\n"
operator|+
literal|"  GENERATE group AS tc1, COUNT(tc0_DISTINCT) AS C;\n"
operator|+
literal|"};"
argument_list|)
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
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"t\" join \"s\" on \"tc1\"=\"sc0\""
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PigToEnumerableConverter\n"
operator|+
literal|"  PigJoin(condition=[=($1, $2)], joinType=[inner])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, t]])\n"
operator|+
literal|"    PigTableScan(table=[[PIG, s]])"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|pigScriptChecker
argument_list|(
literal|"t = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (tc0:chararray, tc1:chararray);\n"
operator|+
literal|"s = LOAD '"
operator|+
name|getFullPathForTestDataFile
argument_list|(
literal|"data2.txt"
argument_list|)
operator|+
literal|"' USING PigStorage() AS (sc0:chararray, sc1:chararray);\n"
operator|+
literal|"t = JOIN t BY tc1 , s BY sc0;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a function that checks that a particular Pig Latin scriptis    * generated to implement a query. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|static
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
name|pigScriptChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|strings
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|List
name|actual
parameter_list|)
block|{
name|String
name|actualArray
init|=
name|actual
operator|==
literal|null
operator|||
name|actual
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
name|String
operator|)
name|actual
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expected Pig script not found"
argument_list|,
name|strings
index|[
literal|0
index|]
argument_list|,
name|actualArray
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End PigAdapterTest.java
end_comment

end_unit

