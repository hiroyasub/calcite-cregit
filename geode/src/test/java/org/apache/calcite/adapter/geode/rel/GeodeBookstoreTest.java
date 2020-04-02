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
name|jupiter
operator|.
name|api
operator|.
name|BeforeAll
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

begin_comment
comment|/**  * Tests using {@code Bookshop} schema.  */
end_comment

begin_class
class|class
name|GeodeBookstoreTest
extends|extends
name|AbstractGeodeTest
block|{
annotation|@
name|BeforeAll
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
name|bookMaster
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
literal|"BookMaster"
argument_list|)
decl_stmt|;
operator|new
name|JsonLoader
argument_list|(
name|bookMaster
argument_list|)
operator|.
name|loadClasspathResource
argument_list|(
literal|"/book_master.json"
argument_list|)
expr_stmt|;
name|Region
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|bookCustomer
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
literal|"BookCustomer"
argument_list|)
decl_stmt|;
operator|new
name|JsonLoader
argument_list|(
name|bookCustomer
argument_list|)
operator|.
name|loadClasspathResource
argument_list|(
literal|"/book_customer.json"
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"BookMaster"
argument_list|,
literal|"BookCustomer"
argument_list|)
argument_list|)
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
name|void
name|testSelect
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from geode.BookMaster"
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
name|void
name|testWhereEqual
parameter_list|()
block|{
name|String
name|expectedQuery
init|=
literal|"SELECT * FROM /BookMaster WHERE itemNumber = 123"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from geode.BookMaster WHERE itemNumber = 123"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWhereWithAnd
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from geode.BookMaster WHERE itemNumber> 122 "
operator|+
literal|"AND itemNumber<= 123"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT * FROM /BookMaster "
operator|+
literal|"WHERE itemNumber> 122 AND itemNumber<= 123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWhereWithOr
parameter_list|()
block|{
name|String
name|expectedQuery
init|=
literal|"SELECT author AS author FROM /BookMaster "
operator|+
literal|"WHERE itemNumber IN SET(123, 789)"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select author from geode.BookMaster "
operator|+
literal|"WHERE itemNumber = 123 OR itemNumber = 789"
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWhereWithAndOr
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT author from geode.BookMaster "
operator|+
literal|"WHERE (itemNumber> 123 AND itemNumber = 789) "
operator|+
literal|"OR author='Daisy Mae West'"
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
literal|"=(CAST($4):VARCHAR, 'Daisy Mae West'))])\n"
operator|+
literal|"      GeodeTableScan(table=[[geode, BookMaster]])\n"
operator|+
literal|"\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT author AS author FROM /BookMaster "
operator|+
literal|"WHERE (itemNumber> 123 AND itemNumber = 789) OR author = 'Daisy Mae West'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Not supported YET
annotation|@
name|Test
name|void
name|testWhereWithOrAnd
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT author from geode.BookMaster "
operator|+
literal|"WHERE (itemNumber> 100 OR itemNumber = 789) "
operator|+
literal|"AND author='Daisy Mae West'"
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
name|void
name|testProjectionsAndWhereGreatThan
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select author from geode.BookMaster WHERE itemNumber> 123"
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT author AS author "
operator|+
literal|"FROM /BookMaster WHERE itemNumber> 123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLimit
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from geode.BookMaster LIMIT 1"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortWithProjection
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished from geode.BookMaster ORDER BY yearPublished ASC"
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortWithProjectionAndLimit
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished from geode.BookMaster ORDER BY yearPublished "
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSortBy2Columns
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished, itemNumber from geode.BookMaster ORDER BY "
operator|+
literal|"yearPublished ASC, itemNumber DESC"
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
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT yearPublished AS yearPublished, "
operator|+
literal|"itemNumber AS itemNumber "
operator|+
literal|"FROM /BookMaster ORDER BY yearPublished ASC, itemNumber DESC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// geode Group By and Aggregation Function Support
comment|//
comment|/**    * OQL Error: Query contains group by columns not present in projected fields    * Solution: Automatically expand the projections to include all missing GROUP By columns.    */
annotation|@
name|Test
name|void
name|testAddMissingGroupByColumnToProjectedFields
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished from geode.BookMaster GROUP BY  yearPublished, "
operator|+
literal|"author"
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
comment|/**    * When the group by columns match the projected fields, the optimizers removes the projected    * relation.    */
annotation|@
name|Test
name|void
name|testMissingProjectRelationOnGroupByColumnMatchingProjectedFields
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished from geode.BookMaster GROUP BY yearPublished"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
comment|/**    * When the group by columns match the projected fields, the optimizers removes the projected    * relation.    */
annotation|@
name|Test
name|void
name|testMissingProjectRelationOnGroupByColumnMatchingProjectedFields2
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished, MAX(retailCost) from geode.BookMaster GROUP BY "
operator|+
literal|"yearPublished"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCount
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select COUNT(retailCost) from geode.BookMaster"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCountStar
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select COUNT(*) from geode.BookMaster"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCountInGroupBy
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished, COUNT(retailCost) from geode.BookMaster GROUP BY "
operator|+
literal|"yearPublished"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaxMinSumAvg
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select MAX(retailCost), MIN(retailCost), SUM(retailCost), AVG"
operator|+
literal|"(retailCost) from geode.BookMaster"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaxMinSumAvgInGroupBy
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished, MAX(retailCost), MIN(retailCost), SUM"
operator|+
literal|"(retailCost), AVG(retailCost) from geode.BookMaster "
operator|+
literal|"GROUP BY  yearPublished"
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
literal|"    GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupBy
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select yearPublished, MAX(retailCost) AS MAXCOST, author from "
operator|+
literal|"geode.BookMaster GROUP BY yearPublished, author"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"yearPublished=2011; MAXCOST=59.99; author=Jim Heavisides"
argument_list|,
literal|"yearPublished=1971; MAXCOST=11.99; author=Clarence Meeks"
argument_list|,
literal|"yearPublished=2011; MAXCOST=34.99; author=Daisy Mae West"
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
literal|"      GeodeTableScan(table=[[geode, BookMaster]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithNestedPdx
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from geode.BookCustomer limit 2"
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
literal|"    GeodeTableScan(table=[[geode, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithNestedPdx2
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select primaryAddress from geode.BookCustomer limit 2"
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
literal|"      GeodeTableScan(table=[[geode, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithNestedPdxFieldAccess
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select primaryAddress['city'] as city from geode.BookCustomer limit 2"
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
literal|"      GeodeTableScan(table=[[geode, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithNullFieldValue
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select primaryAddress['addressLine2'] from geode.BookCustomer limit"
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
literal|"      GeodeTableScan(table=[[geode, BookCustomer]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterWithNestedField
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT primaryAddress['postalCode'] AS postalCode\n"
operator|+
literal|"FROM geode.BookCustomer\n"
operator|+
literal|"WHERE primaryAddress['postalCode']> '0'\n"
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
literal|"      GeodeTableScan(table=[[geode, BookCustomer]])\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT primaryAddress.postalCode AS postalCode "
operator|+
literal|"FROM /BookCustomer WHERE primaryAddress.postalCode> '0'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlSimple
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT itemNumber FROM geode.BookMaster WHERE itemNumber> 123"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT itemNumber AS itemNumber "
operator|+
literal|"FROM /BookMaster WHERE itemNumber> 123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlSingleNumberWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT * FROM geode.BookMaster "
operator|+
literal|"WHERE itemNumber = 123"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT * FROM /BookMaster "
operator|+
literal|"WHERE itemNumber = 123"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlDistinctSort
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT DISTINCT itemNumber, author "
operator|+
literal|"FROM geode.BookMaster ORDER BY itemNumber, author"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlDistinctSort2
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT itemNumber, author "
operator|+
literal|"FROM geode.BookMaster GROUP BY itemNumber, author ORDER BY itemNumber, "
operator|+
literal|"author"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlDistinctSort3
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT DISTINCT * FROM geode.BookMaster"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlLimit2
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT DISTINCT * FROM geode.BookMaster LIMIT 2"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlDisjunction
parameter_list|()
block|{
name|String
name|expectedQuery
init|=
literal|"SELECT author AS author FROM /BookMaster "
operator|+
literal|"WHERE itemNumber IN SET(789, 123)"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT author FROM geode.BookMaster "
operator|+
literal|"WHERE itemNumber = 789 OR itemNumber = 123"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlConjunction
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT author FROM geode.BookMaster "
operator|+
literal|"WHERE itemNumber = 789 AND author = 'Jim Heavisides'"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT author AS author FROM /BookMaster "
operator|+
literal|"WHERE itemNumber = 789 AND author = 'Jim Heavisides'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlBookMasterWhere
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select author, title from geode.BookMaster "
operator|+
literal|"WHERE author = 'Jim Heavisides' LIMIT 2"
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT author AS author, title AS title FROM /BookMaster "
operator|+
literal|"WHERE author = 'Jim Heavisides' LIMIT 2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlBookMasterCount
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) from geode.BookMaster"
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInSetFilterWithNestedStringField
parameter_list|()
block|{
name|String
name|expectedQuery
init|=
literal|"SELECT primaryAddress.city AS city FROM /BookCustomer "
operator|+
literal|"WHERE primaryAddress.city IN SET('Topeka', 'San Francisco')"
decl_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT primaryAddress['city'] AS city\n"
operator|+
literal|"FROM geode.BookCustomer\n"
operator|+
literal|"WHERE primaryAddress['city'] = 'Topeka' OR primaryAddress['city'] = 'San Francisco'\n"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

