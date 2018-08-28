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
name|Sources
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
comment|/**  * Tests for the {@code org.apache.calcite.adapter.geode} package.  *  *<p>Before calling this rel, you need to populate Geode, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-rel-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Geode and the "bookshop"  * and "zips" rel dataset.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeAdapterBookshopIT
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
name|GEODE
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
name|GeodeAdapterBookshopIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model-bookshop.json"
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
name|testSelect
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"BookMaster\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereEqual
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"BookMaster\" WHERE \"itemNumber\" = 123"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"itemNumber=123; description=Run on sentences and drivel on all things mundane;"
operator|+
literal|" retailCost=34.99; yearPublished=2011; author=Daisy Mae West; title=A Treatise of "
operator|+
literal|"Treatises\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeFilter(condition=[=(CAST($0):INTEGER, 123)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereWithAnd
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"BookMaster\" WHERE \"itemNumber\"> 122 "
operator|+
literal|"AND \"itemNumber\"<= 123"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"itemNumber=123; description=Run on sentences and drivel on all things mundane; "
operator|+
literal|"retailCost=34.99; yearPublished=2011; author=Daisy Mae West; title=A Treatise of "
operator|+
literal|"Treatises\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeFilter(condition=[AND(>($0, 122),<=($0, 123))])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereWithOr
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"author\" from \"BookMaster\" "
operator|+
literal|"WHERE \"itemNumber\" = 123 OR \"itemNumber\" = 789"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"author=Jim Heavisides"
argument_list|,
literal|"author=Daisy Mae West"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(author=[$4])\n"
operator|+
literal|"    GeodeFilter(condition=[OR(=(CAST($0):INTEGER, 123), "
operator|+
literal|"=(CAST($0):INTEGER, 789))])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereWithAndOr
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"author\" from \"BookMaster\" "
operator|+
literal|"WHERE (\"itemNumber\"> 123 AND \"itemNumber\" = 789) "
operator|+
literal|"OR \"author\"='Daisy Mae West'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"author=Jim Heavisides"
argument_list|,
literal|"author=Daisy Mae West"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(author=[$4])\n"
operator|+
literal|"    GeodeFilter(condition=[OR(AND(>($0, 123), =(CAST($0):INTEGER, 789)), "
operator|+
literal|"=(CAST($4):VARCHAR CHARACTER SET \"ISO-8859-1\" "
operator|+
literal|"COLLATE \"ISO-8859-1$en_US$primary\", 'Daisy Mae West'))])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Not supported YET
annotation|@
name|Test
specifier|public
name|void
name|testWhereWithOrAnd
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"author\" from \"BookMaster\" "
operator|+
literal|"WHERE (\"itemNumber\"> 100 OR \"itemNumber\" = 789) "
operator|+
literal|"AND \"author\"='Daisy Mae West'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"author=Daisy Mae West"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectionsAndWhereGreatThan
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"author\" from \"BookMaster\" WHERE \"itemNumber\"> 123"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"author=Clarence Meeks\n"
operator|+
literal|"author=Jim Heavisides\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(author=[$4])\n"
operator|+
literal|"    GeodeFilter(condition=[>($0, 123)])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])"
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"BookMaster\" LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"itemNumber=123; description=Run on sentences and drivel on all things mundane; "
operator|+
literal|"retailCost=34.99; yearPublished=2011; author=Daisy Mae West; title=A Treatise of "
operator|+
literal|"Treatises\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeSort(fetch=[1])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortWithProjection
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\" from \"BookMaster\" ORDER BY \"yearPublished\" ASC"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971\n"
operator|+
literal|"yearPublished=2011\n"
operator|+
literal|"yearPublished=2011\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    GeodeProject(yearPublished=[$3])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortWithProjectionAndLimit
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\" from \"BookMaster\" ORDER BY \"yearPublished\" "
operator|+
literal|"LIMIT 2"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971\n"
operator|+
literal|"yearPublished=2011\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(yearPublished=[$3])\n"
operator|+
literal|"    GeodeSort(sort0=[$3], dir0=[ASC], fetch=[2])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortBy2Columns
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\", \"itemNumber\" from \"BookMaster\" ORDER BY "
operator|+
literal|"\"yearPublished\" ASC, \"itemNumber\" DESC"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971; itemNumber=456\n"
operator|+
literal|"yearPublished=2011; itemNumber=789\n"
operator|+
literal|"yearPublished=2011; itemNumber=123\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(yearPublished=[$3], itemNumber=[$0])\n"
operator|+
literal|"    GeodeSort(sort0=[$3], sort1=[$0], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// TEST Group By and Aggregation Function Support
comment|//
comment|/**    * OQL Error: Query contains group by columns not present in projected fields    * Solution: Automatically expand the projections to include all missing GROUP By columns.    */
annotation|@
name|Test
specifier|public
name|void
name|testAddMissingGroupByColumnToProjectedFields
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\" from \"BookMaster\" GROUP BY  \"yearPublished\", "
operator|+
literal|"\"author\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971\n"
operator|+
literal|"yearPublished=2011\n"
operator|+
literal|"yearPublished=2011\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(yearPublished=[$0])\n"
operator|+
literal|"    GeodeAggregate(group=[{3, 4}])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
comment|/**    * When the group by columns match the projected fields, the optimizers removes the projected    * relation.    */
annotation|@
name|Test
specifier|public
name|void
name|testMissingProjectRelationOnGroupByColumnMatchingProjectedFields
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\" from \"BookMaster\" GROUP BY \"yearPublished\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971\n"
operator|+
literal|"yearPublished=2011\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{3}])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
comment|/**    * When the group by columns match the projected fields, the optimizers removes the projected    * relation.    */
annotation|@
name|Test
specifier|public
name|void
name|testMissingProjectRelationOnGroupByColumnMatchingProjectedFields2
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\", MAX(\"retailCost\") from \"BookMaster\" GROUP BY "
operator|+
literal|"\"yearPublished\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971; EXPR$1=11.99\n"
operator|+
literal|"yearPublished=2011; EXPR$1=59.99\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{3}], EXPR$1=[MAX($2)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCount
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select COUNT(\"retailCost\") from \"BookMaster\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3\n"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"3"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{}], EXPR$0=[COUNT($2)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountStar
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select COUNT(*) from \"BookMaster\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountInGroupBy
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\", COUNT(\"retailCost\") from \"BookMaster\" GROUP BY "
operator|+
literal|"\"yearPublished\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=1971; EXPR$1=1\n"
operator|+
literal|"yearPublished=2011; EXPR$1=2\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{3}], EXPR$1=[COUNT($2)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaxMinSumAvg
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select MAX(\"retailCost\"), MIN(\"retailCost\"), SUM(\"retailCost\"), AVG"
operator|+
literal|"(\"retailCost\") from \"BookMaster\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=59.99; EXPR$1=11.99; EXPR$2=106.97000122070312; "
operator|+
literal|"EXPR$3=35.65666580200195\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{}], EXPR$0=[MAX($2)], EXPR$1=[MIN($2)], EXPR$2=[SUM($2)"
operator|+
literal|"], EXPR$3=[AVG($2)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaxMinSumAvgInGroupBy
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\", MAX(\"retailCost\"), MIN(\"retailCost\"), SUM"
operator|+
literal|"(\"retailCost\"), AVG(\"retailCost\") from \"BookMaster\" "
operator|+
literal|"GROUP BY  \"yearPublished\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=2011; EXPR$1=59.99; EXPR$2=34.99; EXPR$3=94.9800033569336; "
operator|+
literal|"EXPR$4=47.4900016784668\n"
operator|+
literal|"yearPublished=1971; EXPR$1=11.99; EXPR$2=11.99; EXPR$3=11.989999771118164; "
operator|+
literal|"EXPR$4=11.989999771118164\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{3}], EXPR$1=[MAX($2)], EXPR$2=[MIN($2)], EXPR$3=[SUM($2)"
operator|+
literal|"], EXPR$4=[AVG($2)])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"yearPublished\", MAX(\"retailCost\") AS MAXCOST, \"author\" from "
operator|+
literal|"\"BookMaster\" GROUP BY \"yearPublished\", \"author\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"yearPublished=2011; MAXCOST=59.99; author=Jim Heavisides\n"
operator|+
literal|"yearPublished=1971; MAXCOST=11.99; author=Clarence Meeks\n"
operator|+
literal|"yearPublished=2011; MAXCOST=34.99; author=Daisy Mae West\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(yearPublished=[$0], MAXCOST=[$2], author=[$1])\n"
operator|+
literal|"    GeodeAggregate(group=[{3, 4}], MAXCOST=[MAX($2)])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithNestedPdx
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"BookCustomer\" limit 2"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeSort(fetch=[2])\n"
operator|+
literal|"    GeodeTableScan(table=[[TEST, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithNestedPdx2
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"primaryAddress\" from \"BookCustomer\" limit 2"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"primaryAddress=PDX[addressLine1,addressLine2,addressLine3,city,state,"
operator|+
literal|"postalCode,country,phoneNumber,addressTag]\n"
operator|+
literal|"primaryAddress=PDX[addressLine1,addressLine2,addressLine3,city,state,postalCode,"
operator|+
literal|"country,phoneNumber,addressTag]\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(primaryAddress=[$3])\n"
operator|+
literal|"    GeodeSort(fetch=[2])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithNestedPdxFieldAccess
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"primaryAddress\"['city'] as \"city\" from \"BookCustomer\" limit 2"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"city=Topeka\n"
operator|+
literal|"city=San Francisco\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(city=[ITEM($3, 'city')])\n"
operator|+
literal|"    GeodeSort(fetch=[2])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectWithNullFieldValue
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"primaryAddress\"['addressLine2'] from \"BookCustomer\" limit"
operator|+
literal|" 2"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=null\n"
operator|+
literal|"EXPR$0=null\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(EXPR$0=[ITEM($3, 'addressLine2')])\n"
operator|+
literal|"    GeodeSort(fetch=[2])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterWithNestedField
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
name|GEODE
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT \"primaryAddress\"['postalCode'] AS \"postalCode\"\n"
operator|+
literal|"FROM \"TEST\".\"BookCustomer\"\n"
operator|+
literal|"WHERE \"primaryAddress\"['postalCode']> '0'\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returns
argument_list|(
literal|"postalCode=50505\n"
operator|+
literal|"postalCode=50505\n"
operator|+
literal|"postalCode=50505\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(postalCode=[ITEM($3, 'postalCode')])\n"
operator|+
literal|"    GeodeFilter(condition=[>(ITEM($3, 'postalCode'), '0')])\n"
operator|+
literal|"      GeodeTableScan(table=[[TEST, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeAdapterBookshopIT.java
end_comment

end_unit

