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
name|jdbc
operator|.
name|CalciteConnection
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
name|schema
operator|.
name|impl
operator|.
name|ViewTable
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
name|impl
operator|.
name|ViewTableMacro
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
name|geode
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|Region
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Collections
import|;
end_import

begin_comment
comment|/**  * Tests based on {@code zips-min.json} dataset. Runs automatically as part of CI.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeZipsTest
extends|extends
name|AbstractGeodeTest
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Cache
name|cache
init|=
name|POLICY
operator|.
name|cache
argument_list|()
decl_stmt|;
name|Region
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|region
init|=
name|cache
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|createRegionFactory
argument_list|()
decl|.
name|create
argument_list|(
literal|"zips"
argument_list|)
decl_stmt|;
operator|new
name|JsonLoader
argument_list|(
name|region
argument_list|)
operator|.
name|loadClasspathResource
argument_list|(
literal|"/zips-mini.json"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|ConnectionFactory
name|newConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:lex=JAVA"
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|root
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"geode"
argument_list|,
operator|new
name|GeodeSchema
argument_list|(
name|POLICY
operator|.
name|cache
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"zips"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// add calcite view programmatically
specifier|final
name|String
name|viewSql
init|=
literal|"select \"_id\" AS \"id\", \"city\", \"loc\", "
operator|+
literal|"cast(\"pop\" AS integer) AS \"pop\", cast(\"state\" AS varchar(2)) AS \"state\" "
operator|+
literal|"from \"geode\".\"zips\""
decl_stmt|;
name|ViewTableMacro
name|macro
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|root
argument_list|,
name|viewSql
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"geode"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"geode"
argument_list|,
literal|"view"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"view"
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|calciteAssert
parameter_list|()
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByView
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT state, SUM(pop) FROM view GROUP BY state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT state AS state, "
operator|+
literal|"SUM(pop) AS EXPR$1 FROM /zips GROUP BY state"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Currently fails"
argument_list|)
specifier|public
name|void
name|testGroupByViewWithAliases
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT state as st, SUM(pop) po "
operator|+
literal|"FROM view GROUP BY state"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT state, SUM(pop) AS po FROM /zips GROUP BY state"
argument_list|)
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{1}], po=[SUM($0)])\n"
operator|+
literal|"    GeodeProject(pop=[CAST($3):INTEGER], state=[CAST($4):VARCHAR(2) CHARACTER SET"
operator|+
literal|" \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      GeodeTableScan(table=[[geode, zips]])\n"
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
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT state as st, SUM(pop) po "
operator|+
literal|"FROM geode.zips GROUP BY state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{4}], po=[SUM($3)])\n"
operator|+
literal|"    GeodeTableScan(table=[[geode, zips]])\n"
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
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT state AS st, SUM(pop) AS po "
operator|+
literal|"FROM geode.zips GROUP BY state"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|51
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeAggregate(group=[{4}], po=[SUM($3)])\n"
operator|+
literal|"    GeodeTableScan(table=[[geode, zips]])\n"
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
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT MAX(pop) FROM view"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=112047\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT MAX(pop) AS EXPR$0 FROM /zips"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Currently fails"
argument_list|)
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT r._id FROM geode.zips AS v "
operator|+
literal|"JOIN geode.zips AS r ON v._id = r._id LIMIT 1"
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
literal|"      GeodeToEnumerableConverter\n"
operator|+
literal|"        GeodeProject(_id=[$0], _id0=[CAST($0):VARCHAR CHARACTER SET "
operator|+
literal|"\"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"          GeodeTableScan(table=[[geode, zips]])\n"
operator|+
literal|"      GeodeToEnumerableConverter\n"
operator|+
literal|"        GeodeProject(_id0=[CAST($0):VARCHAR CHARACTER SET \"ISO-8859-1\" COLLATE "
operator|+
literal|"\"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"          GeodeTableScan(table=[[geode, zips]])\n"
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
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT loc[0] as lat, loc[1] as lon "
operator|+
literal|"FROM view LIMIT 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lat=-105.007985; lon=39.840562\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSort(fetch=[1])\n"
operator|+
literal|"      GeodeTableScan(table=[[geode, zips]])\n"
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
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT loc[0] as lat, loc[1] as lon "
operator|+
literal|"FROM view WHERE loc[0]< 0 LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"lat=-105.007985; lon=39.840562\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSort(fetch=[1])\n"
operator|+
literal|"      GeodeFilter(condition=[<(ITEM($2, 0), 0)])\n"
operator|+
literal|"        GeodeTableScan(table=[[geode, zips]])\n"
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT loc[0] as lat, loc[1] as lon "
operator|+
literal|"FROM view WHERE loc[0]> 0 LIMIT 1"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=GeodeToEnumerableConverter\n"
operator|+
literal|"  GeodeProject(lat=[ITEM($2, 0)], lon=[ITEM($2, 1)])\n"
operator|+
literal|"    GeodeSort(fetch=[1])\n"
operator|+
literal|"      GeodeFilter(condition=[>(ITEM($2, 0), 0)])\n"
operator|+
literal|"        GeodeTableScan(table=[[geode, zips]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeZipsTest.java
end_comment

end_unit

