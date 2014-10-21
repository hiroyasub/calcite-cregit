begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
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
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|collect
operator|.
name|ImmutableList
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
name|OptiqAssert
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
name|OptiqAssert
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
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
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
name|withSchema
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
literal|"Invalid node type AggregateRel in lattice query"
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
literal|"Invalid node type SortRel in lattice query"
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
literal|"Invalid node type UnionRel in lattice query"
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
name|OptiqAssert
operator|.
name|checkRel
argument_list|(
literal|"AggregateRel(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"  ProjectRel(DUMMY=[0])\n"
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
name|Util
operator|.
name|getStackTrace
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
name|OptiqAssert
operator|.
name|checkRel
argument_list|(
literal|"ProjectRel(unit_sales=[$7], brand_name=[$10])\n"
operator|+
literal|"  ProjectRel($f0=[$0], $f1=[$1], $f2=[$2], $f3=[$3], $f4=[$4], $f5=[$5], $f6=[$6], $f7=[$7], $f8=[$8], $f9=[$9], $f10=[$10], $f11=[$11], $f12=[$12], $f13=[$13], $f14=[$14], $f15=[$15], $f16=[$16], $f17=[$17], $f18=[$18], $f19=[$19], $f20=[$20], $f21=[$21], $f22=[$22])\n"
operator|+
literal|"    TableAccessRel(table=[[adhoc, star]])\n"
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
name|OptiqAssert
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
literal|"ProjectRel($f0=[$1], $f1=[$0])\n"
operator|+
literal|"  AggregateRel(group=[{2, 10}])\n"
operator|+
literal|"    TableAccessRel(table=[[adhoc, star]])\n"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"AggregateRel(group=[{2, 10}])\n"
operator|+
literal|"  TableAccessRel(table=[[adhoc, star]])\n"
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
literal|"EnumerableCalcRel(expr#0..1=[{inputs}], $f0=[$t1], $f1=[$t0])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[adhoc, m{2, 10}]])"
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
comment|// OptiqAssert.AssertQuery creates a new schema for every request,
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
literal|"EnumerableTableAccessRel(table=[[adhoc, m{27, 31}"
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
literal|"EnumerableCalcRel(expr#0..4=[{inputs}], proj#0..2=[{exprs}])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[adhoc, m{27, 31}"
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
literal|"EnumerableCalcRel(expr#0..3=[{inputs}], expr#4=[10], expr#5=[*($t3, $t4)], proj#0..2=[{exprs}], US=[$t5])\n"
operator|+
literal|"  EnumerableAggregateRel(group=[{0}], agg#0=[$SUM0($2)], Q=[MIN($1)], agg#2=[$SUM0($4)])\n"
operator|+
literal|"    EnumerableTableAccessRel(table=[[adhoc, m{27, 31}"
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
comment|/** Tests a model that uses an algorithm to generate an initial set of    * tiles.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-428">CALCITE-428,    * "Use optimization algorithm to suggest which tiles of a lattice to    * materialize"</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTileAlgorithm
parameter_list|()
block|{
name|foodmartModel
argument_list|(
literal|" auto: false,\n"
operator|+
literal|"  algorithm: true,\n"
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
literal|"EnumerableAggregateRel(group=[{3, 4}])\n"
operator|+
literal|"  EnumerableTableAccessRel(table=[[adhoc, m{7, 16, 25, 27, 31, 37}]])"
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
operator|.
name|withSchema
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
specifier|private
name|OptiqAssert
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
block|}
end_class

begin_comment
comment|// End LatticeTest.java
end_comment

end_unit

