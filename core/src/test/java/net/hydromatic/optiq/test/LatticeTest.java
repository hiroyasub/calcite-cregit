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
name|Arrays
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
parameter_list|)
block|{
return|return
name|modelWithLattices
argument_list|(
literal|"{ name: '"
operator|+
name|name
operator|+
literal|"', sql: "
operator|+
name|TestUtil
operator|.
name|escapeString
argument_list|(
name|sql
argument_list|)
operator|+
literal|"}"
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
name|convertContains
argument_list|(
literal|"AggregateRel(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"  ProjectRel(DUMMY=[0])\n"
operator|+
literal|"    StarTableScan(table=[[adhoc, star]])\n"
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
name|substitutionMatches
argument_list|(
name|OptiqAssert
operator|.
name|checkRel
argument_list|(
literal|"ProjectRel(unit_sales=[$1], brand_name=[$3])\n"
operator|+
literal|"  JoinRel(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"    ProjectRel(product_id=[$0], unit_sales=[$7])\n"
operator|+
literal|"      ProjectRel($f0=[$0], $f1=[$1], $f2=[$2], $f3=[$3], $f4=[$4], $f5=[$5], $f6=[$6], $f7=[$7])\n"
operator|+
literal|"        TableAccessRel(table=[[adhoc, star]])\n"
operator|+
literal|"    ProjectRel(product_id=[$1], brand_name=[$2])\n"
operator|+
literal|"      JdbcTableScan(table=[[foodmart, product]])\n"
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
specifier|private
name|OptiqAssert
operator|.
name|AssertThat
name|foodmartModel
parameter_list|()
block|{
return|return
name|modelWithLattice
argument_list|(
literal|"star"
argument_list|,
literal|"select 1 from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"join \"foodmart\".\"product\" as p using (\"product_id\")\n"
operator|+
literal|"join \"foodmart\".\"time_by_day\" as t using (\"time_id\")\n"
operator|+
literal|"join \"foodmart\".\"product_class\" as pc on p.\"product_class_id\" = pc.\"product_class_id\""
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LatticeTest.java
end_comment

end_unit

