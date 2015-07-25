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
name|materialize
operator|.
name|Lattices
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
name|materialize
operator|.
name|MaterializationService
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
name|base
operator|.
name|Throwables
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
name|Lists
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
name|IOException
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
name|ResultSet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|anyOf
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
name|containsString
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
name|equalTo
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
name|assertFalse
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
comment|/**  * Unit test for lattices.  */
end_comment

begin_class
specifier|public
class|class
name|LatticeTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SALES_LATTICE
init|=
literal|"{\n"
operator|+
literal|"  name: 'star',\n"
operator|+
literal|"  sql: [\n"
operator|+
literal|"    'select 1 from \"foodmart\".\"sales_fact_1997\" as \"s\"',\n"
operator|+
literal|"    'join \"foodmart\".\"product\" as \"p\" using (\"product_id\")',\n"
operator|+
literal|"    'join \"foodmart\".\"time_by_day\" as \"t\" using (\"time_id\")',\n"
operator|+
literal|"    'join \"foodmart\".\"product_class\" as \"pc\" on \"p\".\"product_class_id\" = \"pc\".\"product_class_id\"'\n"
operator|+
literal|"  ],\n"
operator|+
literal|"  auto: false,\n"
operator|+
literal|"  algorithm: true,\n"
operator|+
literal|"  algorithmMaxMillis: 10000,\n"
operator|+
literal|"  rowCountEstimate: 86837,\n"
operator|+
literal|"  defaultMeasures: [ {\n"
operator|+
literal|"    agg: 'count'\n"
operator|+
literal|"  } ],\n"
operator|+
literal|"  tiles: [ {\n"
operator|+
literal|"    dimensions: [ 'the_year', ['t', 'quarter'] ],\n"
operator|+
literal|"   measures: [ {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'unit_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'store_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'count'\n"
operator|+
literal|"    } ]\n"
operator|+
literal|"  } ]\n"
operator|+
literal|"}\n"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INVENTORY_LATTICE
init|=
literal|"{\n"
operator|+
literal|"  name: 'warehouse',\n"
operator|+
literal|"  sql: [\n"
operator|+
literal|"  'select 1 from \"foodmart\".\"inventory_fact_1997\" as \"s\"',\n"
operator|+
literal|"  'join \"foodmart\".\"product\" as \"p\" using (\"product_id\")',\n"
operator|+
literal|"  'join \"foodmart\".\"time_by_day\" as \"t\" using (\"time_id\")',\n"
operator|+
literal|"  'join \"foodmart\".\"warehouse\" as \"w\" using (\"warehouse_id\")'\n"
operator|+
literal|"  ],\n"
operator|+
literal|"  auto: false,\n"
operator|+
literal|"  algorithm: true,\n"
operator|+
literal|"  algorithmMaxMillis: 10000,\n"
operator|+
literal|"  rowCountEstimate: 4070,\n"
operator|+
literal|"  defaultMeasures: [ {\n"
operator|+
literal|"    agg: 'count'\n"
operator|+
literal|"  } ],\n"
operator|+
literal|"  tiles: [ {\n"
operator|+
literal|"    dimensions: [ 'the_year', 'warehouse_name'],\n"
operator|+
literal|"    measures: [ {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'store_invoice'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'supply_time'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'warehouse_cost'\n"
operator|+
literal|"    } ]\n"
operator|+
literal|"  } ]\n"
operator|+
literal|"}\n"
decl_stmt|;
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|modelWithLattice
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|sql
parameter_list|,
name|String
modifier|...
name|extras
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{ name: '"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"', sql: "
argument_list|)
operator|.
name|append
argument_list|(
name|TestUtil
operator|.
name|escapeString
argument_list|(
name|sql
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|extra
range|:
name|extras
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|extra
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|modelWithLattices
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|modelWithLattices
parameter_list|(
name|String
modifier|...
name|lattices
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|JdbcTest
operator|.
name|EmpDeptTableFactory
argument_list|>
name|clazz
init|=
name|JdbcTest
operator|.
name|EmpDeptTableFactory
operator|.
name|class
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
literal|""
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|JdbcTest
operator|.
name|FOODMART_SCHEMA
operator|+
literal|",\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       lattices: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|lattices
argument_list|)
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"adhoc"
argument_list|)
return|;
block|}
comment|/** Tests that it's OK for a lattice to have the same name as a table in the    * schema. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithSameNameAsTable
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"EMPLOYEES"
argument_list|,
literal|"select * from \"foodmart\".\"days\""
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from EMPLOYEES"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that it's an error to have two lattices with the same name in a    * schema. */
annotation|@
name|Test
specifier|public
name|void
name|testTwoLatticesWithSameNameFails
parameter_list|()
block|{
name|modelWithLattices
argument_list|(
literal|"{name: 'Lattice1', sql: 'select * from \"foodmart\".\"days\"'}"
argument_list|,
literal|"{name: 'Lattice1', sql: 'select * from \"foodmart\".\"time_by_day\"'}"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Duplicate lattice 'Lattice1'"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice whose SQL is invalid. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeInvalidSqlFails
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select foo from nonexistent"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Error instantiating JsonLattice(name=star, "
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Table 'NONEXISTENT' not found"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice whose SQL is invalid because it contains a GROUP BY. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeSqlWithGroupByFails
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s group by \"product_id\""
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Invalid node type LogicalAggregate in lattice query"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice whose SQL is invalid because it contains a ORDER BY. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeSqlWithOrderByFails
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s order by \"product_id\""
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Invalid node type LogicalSort in lattice query"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice whose SQL is invalid because it contains a UNION ALL. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeSqlWithUnionFails
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"union all\n"
operator|+
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"Invalid node type LogicalUnion in lattice query"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice with valid join SQL. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeSqlWithJoin
parameter_list|()
block|{
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"values 1"
argument_list|)
operator|.
name|returnsValue
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a lattice with invalid SQL (for a lattice). */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeInvalidSql
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t on s.\"product_id\" = 100"
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"only equi-join of columns allowed: 100"
argument_list|)
expr_stmt|;
block|}
comment|/** Left join is invalid in a lattice. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeInvalidSql2
parameter_list|()
block|{
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
operator|+
literal|"left join \"foodmart\".\"time_by_day\" as t on s.\"product_id\" = p.\"product_id\""
argument_list|)
operator|.
name|connectThrows
argument_list|(
literal|"only inner join allowed, but got LEFT"
argument_list|)
expr_stmt|;
block|}
comment|/** When a lattice is registered, there is a table with the same name.    * It can be used for explain, but not for queries. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeStarTable
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
try|try
block|{
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) from \"adhoc\".\"star\""
argument_list|)
operator|.
name|convertMatches
argument_list|(
name|CalciteAssert
operator|.
name|checkRel
argument_list|(
literal|""
operator|+
literal|"LogicalAggregate(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"  LogicalProject($f0=[0])\n"
operator|+
literal|"    StarTableScan(table=[[adhoc, star]])\n"
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|e
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"CannotPlanException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|counter
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that a 2-way join query can be mapped 4-way join lattice. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeRecognizeJoin
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"select s.\"unit_sales\", p.\"brand_name\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|substitutionMatches
argument_list|(
name|CalciteAssert
operator|.
name|checkRel
argument_list|(
literal|"LogicalProject(unit_sales=[$7], brand_name=[$10])\n"
operator|+
literal|"  LogicalProject(product_id=[$0], time_id=[$1], customer_id=[$2], promotion_id=[$3], store_id=[$4], store_sales=[$5], store_cost=[$6], unit_sales=[$7], product_class_id=[$8], product_id0=[$9], brand_name=[$10], product_name=[$11], SKU=[$12], SRP=[$13], gross_weight=[$14], net_weight=[$15], recyclable_package=[$16], low_fat=[$17], units_per_case=[$18], cases_per_pallet=[$19], shelf_width=[$20], shelf_height=[$21], shelf_depth=[$22])\n"
operator|+
literal|"    LogicalTableScan(table=[[adhoc, star]])\n"
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests an aggregate on a 2-way join query can use an aggregate table. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeRecognizeGroupJoin
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|AssertQuery
name|that
init|=
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"select distinct p.\"brand_name\", s.\"customer_id\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|substitutionMatches
argument_list|(
operator|new
name|Function
argument_list|<
name|RelNode
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|RelNode
name|relNode
parameter_list|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|String
name|s
init|=
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|relNode
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|anyOf
argument_list|(
name|containsString
argument_list|(
literal|"LogicalProject(brand_name=[$1], customer_id=[$0])\n"
operator|+
literal|"  LogicalAggregate(group=[{2, 10}])\n"
operator|+
literal|"    LogicalTableScan(table=[[adhoc, star]])\n"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"LogicalAggregate(group=[{2, 10}])\n"
operator|+
literal|"  LogicalTableScan(table=[[adhoc, star]])\n"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|that
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..1=[{inputs}], brand_name=[$t1], customer_id=[$t0])\n"
operator|+
literal|"  EnumerableTableScan(table=[[adhoc, m{2, 10}]])"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|69203
argument_list|)
expr_stmt|;
comment|// Run the same query again and see whether it uses the same
comment|// materialization.
name|that
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|CREATE_MATERIALIZATION
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|materializationName
parameter_list|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|69203
argument_list|)
expr_stmt|;
comment|// Ideally the counter would stay at 2. It increments to 3 because
comment|// CalciteAssert.AssertQuery creates a new schema for every request,
comment|// and therefore cannot re-use lattices or materializations from the
comment|// previous request.
name|assertThat
argument_list|(
name|counter
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a model with pre-defined tiles. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithPreDefinedTiles
parameter_list|()
block|{
name|foodmartModel
argument_list|(
literal|" auto: false,\n"
operator|+
literal|"  defaultMeasures: [ {\n"
operator|+
literal|"    agg: 'count'\n"
operator|+
literal|"  } ],\n"
operator|+
literal|"  tiles: [ {\n"
operator|+
literal|"    dimensions: [ 'the_year', ['t', 'quarter'] ],\n"
operator|+
literal|"    measures: [ ]\n"
operator|+
literal|"  } ]\n"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select distinct t.\"the_year\", t.\"quarter\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t using (\"time_id\")\n"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[adhoc, m{27, 31}"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/** A query that uses a pre-defined aggregate table, at the same    *  granularity but fewer calls to aggregate functions. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithPreDefinedTilesFewerMeasures
parameter_list|()
block|{
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.\"the_year\", t.\"quarter\", count(*) as c\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t using (\"time_id\")\n"
operator|+
literal|"group by t.\"the_year\", t.\"quarter\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[adhoc, m{27, 31}"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"the_year=1997; quarter=Q1; C=21588"
argument_list|,
literal|"the_year=1997; quarter=Q2; C=20368"
argument_list|,
literal|"the_year=1997; quarter=Q3; C=21453"
argument_list|,
literal|"the_year=1997; quarter=Q4; C=23428"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a query that uses a pre-defined aggregate table at a lower    * granularity. Includes a measure computed from a grouping column, a measure    * based on COUNT rolled up using SUM, and an expression on a measure. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithPreDefinedTilesRollUp
parameter_list|()
block|{
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|query
argument_list|(
literal|"select t.\"the_year\",\n"
operator|+
literal|"  count(*) as c,\n"
operator|+
literal|"  min(\"quarter\") as q,\n"
operator|+
literal|"  sum(\"unit_sales\") * 10 as us\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t using (\"time_id\")\n"
operator|+
literal|"group by t.\"the_year\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[*($t3, $t4)], proj#0..2=[{exprs}], US=[$t5])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], C=[$SUM0($2)], Q=[MIN($1)], agg#2=[$SUM0($4)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[adhoc, m{27, 31}"
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"the_year=1997; C=86837; Q=Q1; US=2667730.0000"
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a model that uses an algorithm to generate an initial set of    * tiles.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-428">[CALCITE-428]    * Use optimization algorithm to suggest which tiles of a lattice to    * materialize</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTileAlgorithm
parameter_list|()
block|{
name|checkTileAlgorithm
argument_list|(
name|FoodMartLatticeStatisticProvider
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"EnumerableAggregate(group=[{2, 3}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[adhoc, m{16, 17, 27, 31}]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTileAlgorithm2
parameter_list|()
block|{
comment|// Different explain than above, but note that it still selects columns
comment|// (27, 31).
name|checkTileAlgorithm
argument_list|(
name|Lattices
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|"#CACHED_SQL"
argument_list|,
literal|"EnumerableAggregate(group=[{0, 1}])\n"
operator|+
literal|"  EnumerableTableScan(table=[[adhoc, m{27, 31, 32, 36, 37}]"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkTileAlgorithm
parameter_list|(
name|String
name|statisticProvider
parameter_list|,
name|String
name|expectedExplain
parameter_list|)
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|MaterializationService
operator|.
name|instance
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|foodmartModel
argument_list|(
literal|" auto: false,\n"
operator|+
literal|"  algorithm: true,\n"
operator|+
literal|"  algorithmMaxMillis: -1,\n"
operator|+
literal|"  rowCountEstimate: 86000,\n"
operator|+
literal|"  defaultMeasures: [ {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'unit_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'store_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'count'\n"
operator|+
literal|"  } ],\n"
operator|+
literal|"  statisticProvider: '"
operator|+
name|statisticProvider
operator|+
literal|"',\n"
operator|+
literal|"  tiles: [ {\n"
operator|+
literal|"    dimensions: [ 'the_year', ['t', 'quarter'] ],\n"
operator|+
literal|"    measures: [ ]\n"
operator|+
literal|"  } ]\n"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select distinct t.\"the_year\", t.\"quarter\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t using (\"time_id\")\n"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedExplain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"the_year=1997; quarter=Q1"
argument_list|,
literal|"the_year=1997; quarter=Q2"
argument_list|,
literal|"the_year=1997; quarter=Q3"
argument_list|,
literal|"the_year=1997; quarter=Q4"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a query that is created within {@link #testTileAlgorithm()}. */
annotation|@
name|Test
specifier|public
name|void
name|testJG
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"SELECT \"s\".\"unit_sales\", \"p\".\"recyclable_package\", \"t\".\"the_day\", \"t\".\"the_year\", \"t\".\"quarter\", \"pc\".\"product_family\", COUNT(*) AS \"m0\", SUM(\"s\".\"store_sales\") AS \"m1\", SUM(\"s\".\"unit_sales\") AS \"m2\"\n"
operator|+
literal|"FROM \"foodmart\".\"sales_fact_1997\" AS \"s\"\n"
operator|+
literal|"JOIN \"foodmart\".\"product\" AS \"p\" ON \"s\".\"product_id\" = \"p\".\"product_id\"\n"
operator|+
literal|"JOIN \"foodmart\".\"time_by_day\" AS \"t\" ON \"s\".\"time_id\" = \"t\".\"time_id\"\n"
operator|+
literal|"JOIN \"foodmart\".\"product_class\" AS \"pc\" ON \"p\".\"product_class_id\" = \"pc\".\"product_class_id\"\n"
operator|+
literal|"GROUP BY \"s\".\"unit_sales\", \"p\".\"recyclable_package\", \"t\".\"the_day\", \"t\".\"the_year\", \"t\".\"quarter\", \"pc\".\"product_family\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"JdbcToEnumerableConverter\n"
operator|+
literal|"  JdbcAggregate(group=[{3, 6, 8, 9, 10, 12}], m0=[COUNT()], m1=[$SUM0($2)], m2=[$SUM0($3)])\n"
operator|+
literal|"    JdbcJoin(condition=[=($4, $11)], joinType=[inner])\n"
operator|+
literal|"      JdbcJoin(condition=[=($1, $7)], joinType=[inner])\n"
operator|+
literal|"        JdbcJoin(condition=[=($0, $5)], joinType=[inner])\n"
operator|+
literal|"          JdbcProject(product_id=[$0], time_id=[$1], store_sales=[$5], unit_sales=[$7])\n"
operator|+
literal|"            JdbcTableScan(table=[[foodmart, sales_fact_1997]])\n"
operator|+
literal|"          JdbcProject(product_class_id=[$0], product_id=[$1], recyclable_package=[$8])\n"
operator|+
literal|"            JdbcTableScan(table=[[foodmart, product]])\n"
operator|+
literal|"        JdbcProject(time_id=[$0], the_day=[$2], the_year=[$4], quarter=[$8])\n"
operator|+
literal|"          JdbcTableScan(table=[[foodmart, time_by_day]])\n"
operator|+
literal|"      JdbcProject(product_class_id=[$0], product_family=[$4])\n"
operator|+
literal|"        JdbcTableScan(table=[[foodmart, product_class]])"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a query that uses no columns from the fact table. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByEmpty
parameter_list|()
block|{
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) as c from \"foodmart\".\"sales_fact_1997\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=86837"
argument_list|)
expr_stmt|;
block|}
comment|/** Calls {@link #testDistinctCount()} followed by    * {@link #testGroupByEmpty()}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByEmptyWithPrelude
parameter_list|()
block|{
name|testDistinctCount
argument_list|()
expr_stmt|;
name|testGroupByEmpty
argument_list|()
expr_stmt|;
block|}
comment|/** Tests a query that uses no dimension columns and one measure column. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByEmpty2
parameter_list|()
block|{
name|foodmartModel
argument_list|()
operator|.
name|query
argument_list|(
literal|"select sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"S=266773.0000"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that two queries of the same dimensionality that use different    * measures can use the same materialization. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByEmpty3
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|mats
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|handler
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|materializationName
parameter_list|)
block|{
name|mats
operator|.
name|add
argument_list|(
name|materializationName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|CalciteAssert
operator|.
name|AssertThat
name|that
init|=
name|foodmartModel
argument_list|()
operator|.
name|pooled
argument_list|()
decl_stmt|;
name|that
operator|.
name|query
argument_list|(
literal|"select sum(\"unit_sales\") as s, count(*) as c\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\""
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|CREATE_MATERIALIZATION
argument_list|,
name|handler
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableTableScan(table=[[adhoc, m{}]])"
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"S=266773.0000; C=86837"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mats
operator|.
name|toString
argument_list|()
argument_list|,
name|mats
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// A similar query can use the same materialization.
name|that
operator|.
name|query
argument_list|(
literal|"select sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\""
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|CREATE_MATERIALIZATION
argument_list|,
name|handler
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"S=266773.0000"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mats
operator|.
name|toString
argument_list|()
argument_list|,
name|mats
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Rolling up SUM. */
annotation|@
name|Test
specifier|public
name|void
name|testSum
parameter_list|()
block|{
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|query
argument_list|(
literal|"select sum(\"unit_sales\") as c\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"group by \"product_id\"\n"
operator|+
literal|"order by 1 desc limit 1"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|enable
argument_list|(
name|CalciteAssert
operator|.
name|DB
operator|!=
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|ORACLE
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=267.0000"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a distinct-count query.    *    *<p>We can't just roll up count(distinct ...) as we do count(...), but we    * can still use the aggregate table if we're smart. */
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(distinct \"quarter\") as c\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" using (\"time_id\")\n"
operator|+
literal|"group by \"the_year\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], C=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], C=[COUNT($1)])\n"
operator|+
literal|"    EnumerableTableScan(table=[[adhoc, m{27, 31}]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount2
parameter_list|()
block|{
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(distinct \"the_year\") as c\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\"\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" using (\"time_id\")\n"
operator|+
literal|"group by \"the_year\""
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..1=[{inputs}], C=[$t1])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0}], C=[COUNT($0)])\n"
operator|+
literal|"    EnumerableAggregate(group=[{0}])\n"
operator|+
literal|"      EnumerableTableScan(table=[[adhoc, m{27, 31}]])"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=1"
argument_list|)
expr_stmt|;
block|}
comment|/** Runs all queries against the Foodmart schema, using a lattice.    *    *<p>Disabled for normal runs, because it is slow. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testAllFoodmartQueries
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test ids that had bugs in them until recently. Useful for a sanity check.
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|fixed
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|13
argument_list|,
literal|24
argument_list|,
literal|28
argument_list|,
literal|30
argument_list|,
literal|61
argument_list|,
literal|76
argument_list|,
literal|79
argument_list|,
literal|81
argument_list|,
literal|85
argument_list|,
literal|98
argument_list|,
literal|101
argument_list|,
literal|107
argument_list|,
literal|128
argument_list|,
literal|129
argument_list|,
literal|130
argument_list|,
literal|131
argument_list|)
decl_stmt|;
comment|// Test ids that still have bugs
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|bad
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|382
argument_list|,
literal|423
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"i="
operator|+
name|i
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|bad
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|check
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"error in "
operator|+
name|i
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|check
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FoodmartTest
operator|.
name|FoodmartQuery
name|query
init|=
name|FoodmartTest
operator|.
name|FoodMartQuerySet
operator|.
name|instance
argument_list|()
operator|.
name|queries
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|foodmartModelWithOneTile
argument_list|()
operator|.
name|withDefaultSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|query
argument_list|(
name|query
operator|.
name|sql
argument_list|)
operator|.
name|sameResultWithMaterializationsDisabled
argument_list|()
expr_stmt|;
block|}
comment|/** A tile with no measures should inherit default measure list from the    * lattice. */
annotation|@
name|Test
specifier|public
name|void
name|testTileWithNoMeasures
parameter_list|()
block|{
comment|// TODO
block|}
comment|/** A lattice with no default measure list should get "count(*)" is its    * default measure. */
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithNoMeasures
parameter_list|()
block|{
comment|// TODO
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDimensionIsInvalidColumn
parameter_list|()
block|{
comment|// TODO
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMeasureArgIsInvalidColumn
parameter_list|()
block|{
comment|// TODO
block|}
comment|/** It is an error for "customer_id" to be a measure arg, because is not a    * unique alias. Both "c" and "t" have "customer_id". */
annotation|@
name|Test
specifier|public
name|void
name|testMeasureArgIsNotUniqueAlias
parameter_list|()
block|{
comment|// TODO
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMeasureAggIsInvalid
parameter_list|()
block|{
comment|// TODO
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwoLattices
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|modelWithLattices
argument_list|(
name|SALES_LATTICE
argument_list|,
name|INVENTORY_LATTICE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select s.\"unit_sales\", p.\"brand_name\"\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|substitutionMatches
argument_list|(
name|CalciteAssert
operator|.
name|checkRel
argument_list|(
literal|"LogicalProject(unit_sales=[$7], brand_name=[$10])\n"
operator|+
literal|"  LogicalProject(product_id=[$0], time_id=[$1], customer_id=[$2], promotion_id=[$3], store_id=[$4], store_sales=[$5], store_cost=[$6], unit_sales=[$7], product_class_id=[$8], product_id0=[$9], brand_name=[$10], product_name=[$11], SKU=[$12], SRP=[$13], gross_weight=[$14], net_weight=[$15], recyclable_package=[$16], low_fat=[$17], units_per_case=[$18], cases_per_pallet=[$19], shelf_width=[$20], shelf_height=[$21], shelf_depth=[$22])\n"
operator|+
literal|"    LogicalTableScan(table=[[adhoc, star]])\n"
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-787">[CALCITE-787]    * Star table wrongly assigned to materialized view</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testOneLatticeOneMV
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|JdbcTest
operator|.
name|EmpDeptTableFactory
argument_list|>
name|clazz
init|=
name|JdbcTest
operator|.
name|EmpDeptTableFactory
operator|.
name|class
decl_stmt|;
specifier|final
name|String
name|mv
init|=
literal|"       materializations: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           table: \"m0\",\n"
operator|+
literal|"           view: \"m0v\",\n"
operator|+
literal|"           sql: \"select * from \\\"foodmart\\\".\\\"sales_fact_1997\\\" "
operator|+
literal|"where \\\"product_id\\\" = 10\" "
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|""
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|JdbcTest
operator|.
name|FOODMART_SCHEMA
operator|+
literal|",\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       tables: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'EMPLOYEES',\n"
operator|+
literal|"           type: 'custom',\n"
operator|+
literal|"           factory: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"',\n"
operator|+
literal|"           operand: {'foo': true, 'bar': 345}\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ],\n"
operator|+
literal|"       lattices: "
operator|+
literal|"["
operator|+
name|INVENTORY_LATTICE
operator|+
literal|"       ]\n"
operator|+
literal|"     },\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'mat',\n"
operator|+
name|mv
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"foodmart\".\"sales_fact_1997\" where \"product_id\" = 10"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|substitutionMatches
argument_list|(
name|CalciteAssert
operator|.
name|checkRel
argument_list|(
literal|"EnumerableTableScan(table=[[mat, m0]])\n"
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|intValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-760">[CALCITE-760]    * Aggregate recommender blows up if row count estimate is too high</a>. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testLatticeWithBadRowCountEstimate
parameter_list|()
block|{
specifier|final
name|String
name|lattice
init|=
name|INVENTORY_LATTICE
operator|.
name|replace
argument_list|(
literal|"rowCountEstimate: 4070,"
argument_list|,
literal|"rowCountEstimate: 4074070,"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|lattice
operator|.
name|equals
argument_list|(
name|INVENTORY_LATTICE
argument_list|)
argument_list|)
expr_stmt|;
name|modelWithLattices
argument_list|(
name|lattice
argument_list|)
operator|.
name|query
argument_list|(
literal|"values 1\n"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|foodmartModel
parameter_list|(
name|String
modifier|...
name|extras
parameter_list|)
block|{
return|return
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as \"s\"\n"
operator|+
literal|"join \"foodmart\".\"product\" as \"p\" using (\"product_id\")\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as \"t\" using (\"time_id\")\n"
operator|+
literal|"join \"foodmart\".\"product_class\" as \"pc\" on \"p\".\"product_class_id\" = \"pc\".\"product_class_id\""
argument_list|,
name|extras
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|foodmartModelWithOneTile
parameter_list|()
block|{
return|return
name|foodmartModel
argument_list|(
literal|" auto: false,\n"
operator|+
literal|"  defaultMeasures: [ {\n"
operator|+
literal|"    agg: 'count'\n"
operator|+
literal|"  } ],\n"
operator|+
literal|"  tiles: [ {\n"
operator|+
literal|"    dimensions: [ 'the_year', ['t', 'quarter'] ],\n"
operator|+
literal|"    measures: [ {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'unit_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'sum',\n"
operator|+
literal|"      args: 'store_sales'\n"
operator|+
literal|"    }, {\n"
operator|+
literal|"      agg: 'count'\n"
operator|+
literal|"    } ]\n"
operator|+
literal|"  } ]\n"
argument_list|)
return|;
block|}
comment|// Just for debugging.
specifier|private
specifier|static
name|void
name|runJdbc
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
literal|"jdbc:calcite:model=core/src/test/resources/mysql-foodmart-lattice-model.json"
argument_list|)
decl_stmt|;
specifier|final
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"select * from \"adhoc\".\"m{27, 31}\""
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|CalciteAssert
operator|.
name|toString
argument_list|(
name|resultSet
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End LatticeTest.java
end_comment

end_unit

