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
name|geode
operator|.
name|rel
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
name|Util
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

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.geode} package.  *  *<p>Before calling this rel, you need to populate Geode, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-rel-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Geode and the "bookshop" and "zips" rel dataset.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeZipsIT
block|{
comment|/**    * Connection factory based on the "geode relational " model.    */
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|GEODE_ZIPS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"CONFORMANCE"
argument_list|,
literal|"LENIENT"
argument_list|,
literal|"model"
argument_list|,
name|GeodeZipsIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model-zips.json"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Whether to run Geode tests. Enabled by default, however rel is only    * included if "it" profile is activated ({@code -Pit}). To disable,    * specify {@code -Dcalcite.rel.geode=false} on the Java command line.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Util
operator|.
name|getBooleanProperty
argument_list|(
literal|"calcite.rel.geode"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to run this rel.    */
specifier|protected
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|ENABLED
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByView
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"state\", SUM(\"pop\") FROM \"geode\".\"ZIPS\" GROUP BY \"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeAggregateRel(group=[{1}], EXPR$1=[SUM($0)])\n"
operator|+
literal|"    GeodeProjectRel(pop=[CAST($3):INTEGER], state=[CAST($4):VARCHAR(2) CHARACTER SET"
operator|+
literal|" \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByViewWithAliases
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"state\" as \"st\", SUM(\"pop\") \"po\" "
operator|+
literal|"FROM \"geode\".\"ZIPS\" GROUP BY "
operator|+
literal|"\"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeAggregateRel(group=[{1}], po=[SUM($0)])\n"
operator|+
literal|"    GeodeProjectRel(pop=[CAST($3):INTEGER], state=[CAST($4):VARCHAR(2) CHARACTER SET"
operator|+
literal|" \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByRaw
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"state\" as \"st\", SUM(\"pop\") \"po\" "
operator|+
literal|"FROM \"geode_raw\".\"Zips\" GROUP"
operator|+
literal|" BY \"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeAggregateRel(group=[{4}], po=[SUM($3)])\n"
operator|+
literal|"    GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByRawWithAliases
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"state\" AS \"st\", SUM(\"pop\") AS \"po\" "
operator|+
literal|"FROM \"geode_raw\".\"Zips\" "
operator|+
literal|"GROUP BY \"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeAggregateRel(group=[{4}], po=[SUM($3)])\n"
operator|+
literal|"    GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaxRaw
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT MAX(\"pop\") FROM \"geode_raw\".\"Zips\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeAggregateRel(group=[{}], EXPR$0=[MAX($3)])\n"
operator|+
literal|"    GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
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
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"r\".\"_id\" FROM \"geode_raw\".\"Zips\" AS \"v\" JOIN \"geode_raw\""
operator|+
literal|".\"Zips\" AS \"r\" ON \"v\".\"_id\" = \"r\".\"_id\" LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableCalc(expr#0..2=[{inputs}], _id1=[$t0])\n"
operator|+
literal|"  EnumerableLimit(fetch=[1])\n"
operator|+
literal|"    EnumerableJoin(condition=[=($1, $2)], joinType=[inner])\n"
operator|+
literal|"      GeodeToEnumerableConverterRel\n"
operator|+
literal|"        GeodeProjectRel(_id=[$0], _id0=[CAST($0):VARCHAR CHARACTER SET "
operator|+
literal|"\"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"          GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
operator|+
literal|"      GeodeToEnumerableConverterRel\n"
operator|+
literal|"        GeodeProjectRel(_id0=[CAST($0):VARCHAR CHARACTER SET \"ISO-8859-1\" COLLATE "
operator|+
literal|"\"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"          GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectLocItem
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"loc\"[0] as \"lat\", \"loc\"[1] as \"lon\" "
operator|+
literal|"FROM \"geode_raw\".\"Zips\" LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lat=-74.700748; lon=41.65158\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeProjectRel(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSortRel(fetch=[1])\n"
operator|+
literal|"      GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testItemPredicate
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"loc\"[0] as \"lat\", \"loc\"[1] as \"lon\" "
operator|+
literal|"FROM \"geode_raw\".\"Zips\" WHERE \"loc\"[0]< 0 LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lat=-74.700748; lon=41.65158\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeProjectRel(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSortRel(fetch=[1])\n"
operator|+
literal|"      GeodeFilterRel(condition=[<(ITEM($2, 0), 0)])\n"
operator|+
literal|"        GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|GEODE_ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"loc\"[0] as \"lat\", \"loc\"[1] as \"lon\" "
operator|+
literal|"FROM \"geode_raw\".\"Zips\" WHERE \"loc\"[0]> 0 LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverterRel\n"
operator|+
literal|"  GeodeProjectRel(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSortRel(fetch=[1])\n"
operator|+
literal|"      GeodeFilterRel(condition=[>(ITEM($2, 0), 0)])\n"
operator|+
literal|"        GeodeTableScanRel(table=[[geode_raw, Zips]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeZipsIT.java
end_comment

end_unit

