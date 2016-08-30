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
name|util
operator|.
name|Bug
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|CalciteAssert
operator|.
name|that
import|;
end_import

begin_comment
comment|/**  * Tests for a JDBC front-end and JDBC back-end where the processing is not  * pushed down to JDBC (as in {@link JdbcFrontJdbcBackTest}) but is executed  * in a pipeline of linq4j operators.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontJdbcBackLinqMiddleTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testTable
parameter_list|()
block|{
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
literal|"select * from \"foodmart\".\"days\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1; week_day=Sunday\n"
operator|+
literal|"day=2; week_day=Monday\n"
operator|+
literal|"day=5; week_day=Thursday\n"
operator|+
literal|"day=4; week_day=Wednesday\n"
operator|+
literal|"day=3; week_day=Tuesday\n"
operator|+
literal|"day=6; week_day=Friday\n"
operator|+
literal|"day=7; week_day=Saturday\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhere
parameter_list|()
block|{
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
literal|"select * from \"foodmart\".\"days\" where \"day\"< 3"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1; week_day=Sunday\n"
operator|+
literal|"day=2; week_day=Monday\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhere2
parameter_list|()
block|{
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
literal|"select * from \"foodmart\".\"days\"\n"
operator|+
literal|"where not (lower(\"week_day\") = 'wednesday')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1; week_day=Sunday\n"
operator|+
literal|"day=2; week_day=Monday\n"
operator|+
literal|"day=5; week_day=Thursday\n"
operator|+
literal|"day=3; week_day=Tuesday\n"
operator|+
literal|"day=6; week_day=Friday\n"
operator|+
literal|"day=7; week_day=Saturday\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCase
parameter_list|()
block|{
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"day\",\n"
operator|+
literal|" \"week_day\",\n"
operator|+
literal|" case when \"day\"< 3 then upper(\"week_day\")\n"
operator|+
literal|"      when \"day\"< 5 then lower(\"week_day\")\n"
operator|+
literal|"      else \"week_day\" end as d\n"
operator|+
literal|"from \"foodmart\".\"days\"\n"
operator|+
literal|"where \"day\"<> 1\n"
operator|+
literal|"order by \"day\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=2; week_day=Monday; D=MONDAY\n"
operator|+
literal|"day=3; week_day=Tuesday; D=tuesday\n"
operator|+
literal|"day=4; week_day=Wednesday; D=wednesday\n"
operator|+
literal|"day=5; week_day=Thursday; D=Thursday\n"
operator|+
literal|"day=6; week_day=Friday; D=Friday\n"
operator|+
literal|"day=7; week_day=Saturday; D=Saturday\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
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
literal|"select s, count(*) as c, min(\"week_day\") as mw from (\n"
operator|+
literal|"select \"week_day\",\n"
operator|+
literal|"  substring(\"week_day\" from 1 for 1) as s\n"
operator|+
literal|"from \"foodmart\".\"days\")\n"
operator|+
literal|"group by s"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"S=T; C=2; MW=Thursday"
argument_list|,
literal|"S=F; C=1; MW=Friday"
argument_list|,
literal|"S=W; C=1; MW=Wednesday"
argument_list|,
literal|"S=S; C=2; MW=Saturday"
argument_list|,
literal|"S=M; C=1; MW=Monday"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupEmpty
parameter_list|()
block|{
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
literal|"select count(*) as c\n"
operator|+
literal|"from \"foodmart\".\"days\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=7\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a theta join: a join whose condition cannot be decomposed    * into input0.x = input1.x and ... input0.z = input1.z.    *    *<p>Currently, the query can be planned, but the plan is not efficient (uses    * cartesian product).</p>    */
annotation|@
name|Ignore
argument_list|(
literal|"non-deterministic on JDK 1.7 vs 1.8"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testJoinTheta
parameter_list|()
block|{
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"  join \"foodmart\".\"customer\" as c\n"
operator|+
literal|"  on s.\"customer_id\" - c.\"customer_id\" = 0)"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableAggregate(group=[{}], EXPR$0=[COUNT()])\n"
operator|+
literal|"  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[0], expr#3=[-($t0, $t1)], expr#4=[=($t3, $t2)], DUMMY=[$t2], $condition=[$t4])\n"
operator|+
literal|"    EnumerableJoin(condition=[true], joinType=[inner])\n"
operator|+
literal|"      JdbcToEnumerableConverter\n"
operator|+
literal|"        JdbcProject(customer_id=[$2])\n"
operator|+
literal|"          JdbcTableScan(table=[[foodmart, sales_fact_1997]])\n"
operator|+
literal|"      JdbcToEnumerableConverter\n"
operator|+
literal|"        JdbcProject(customer_id=[$0])\n"
operator|+
literal|"          JdbcTableScan(table=[[foodmart, customer]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinGroupByEmpty
parameter_list|()
block|{
if|if
condition|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
operator|&&
operator|!
name|Bug
operator|.
name|CALCITE_673_FIXED
condition|)
block|{
return|return;
block|}
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
literal|"select count(*) from (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"  join \"foodmart\".\"customer\" as c\n"
operator|+
literal|"  on s.\"customer_id\" = c.\"customer_id\")"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=86837\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinGroupByOrderBy
parameter_list|()
block|{
if|if
condition|(
name|CalciteAssert
operator|.
name|DB
operator|==
name|CalciteAssert
operator|.
name|DatabaseInstance
operator|.
name|MYSQL
operator|&&
operator|!
name|Bug
operator|.
name|CALCITE_673_FIXED
condition|)
block|{
return|return;
block|}
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
literal|"select count(*), c.\"state_province\",\n"
operator|+
literal|"  sum(s.\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"  join \"foodmart\".\"customer\" as c\n"
operator|+
literal|"  on s.\"customer_id\" = c.\"customer_id\"\n"
operator|+
literal|"group by c.\"state_province\"\n"
operator|+
literal|"order by c.\"state_province\""
argument_list|)
operator|.
name|returns2
argument_list|(
literal|"EXPR$0=24442; state_province=CA; S=74748\n"
operator|+
literal|"EXPR$0=21611; state_province=OR; S=67659\n"
operator|+
literal|"EXPR$0=40784; state_province=WA; S=124366\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeGroupBy
parameter_list|()
block|{
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
literal|"select count(*) as c, c.\"state_province\"\n"
operator|+
literal|"from \"foodmart\".\"customer\" as c\n"
operator|+
literal|"group by c.\"state_province\", c.\"country\"\n"
operator|+
literal|"order by c, 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=78; state_province=Sinaloa\n"
operator|+
literal|"C=90; state_province=Oaxaca\n"
operator|+
literal|"C=93; state_province=Veracruz\n"
operator|+
literal|"C=97; state_province=Mexico\n"
operator|+
literal|"C=99; state_province=Yucatan\n"
operator|+
literal|"C=104; state_province=Jalisco\n"
operator|+
literal|"C=106; state_province=Guerrero\n"
operator|+
literal|"C=191; state_province=Zacatecas\n"
operator|+
literal|"C=347; state_province=DF\n"
operator|+
literal|"C=1051; state_province=OR\n"
operator|+
literal|"C=1717; state_province=BC\n"
operator|+
literal|"C=2086; state_province=WA\n"
operator|+
literal|"C=4222; state_province=CA\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
comment|// Complicating factors:
comment|// Composite GROUP BY key
comment|// Order by select item, referenced by ordinal
comment|// Distinct count
comment|// Not all GROUP columns are projected
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
literal|"select c.\"state_province\",\n"
operator|+
literal|"  sum(s.\"unit_sales\") as s,\n"
operator|+
literal|"  count(distinct c.\"customer_id\") as dc\n"
operator|+
literal|"from \"foodmart\".\"sales_fact_1997\" as s\n"
operator|+
literal|"  join \"foodmart\".\"customer\" as c\n"
operator|+
literal|"  on s.\"customer_id\" = c.\"customer_id\"\n"
operator|+
literal|"group by c.\"state_province\", c.\"country\"\n"
operator|+
literal|"order by c.\"state_province\", 2"
argument_list|)
operator|.
name|planHasSql
argument_list|(
literal|"SELECT `state_province`, `S`, `DC`\n"
operator|+
literal|"FROM (SELECT `customer`.`state_province`, `customer`.`country`, SUM(`sales_fact_1997`.`unit_sales`) AS `S`, COUNT(DISTINCT `customer`.`customer_id`) AS `DC`\n"
operator|+
literal|"FROM `foodmart`.`sales_fact_1997`\n"
operator|+
literal|"INNER JOIN `foodmart`.`customer` ON `sales_fact_1997`.`customer_id` = `customer`.`customer_id`\n"
operator|+
literal|"GROUP BY `customer`.`state_province`, `customer`.`country`) AS `t0`\n"
operator|+
literal|"ORDER BY `state_province`, `S`"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"state_province=CA; S=74748.0000; DC=2716\n"
operator|+
literal|"state_province=OR; S=67659.0000; DC=1037\n"
operator|+
literal|"state_province=WA; S=124366.0000; DC=1828\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPlan
parameter_list|()
block|{
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
literal|"select c.\"state_province\"\n"
operator|+
literal|"from \"foodmart\".\"customer\" as c\n"
operator|+
literal|"where c.\"state_province\" = 'USA'"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"            public boolean moveNext() {\n"
operator|+
literal|"              while (inputEnumerator.moveNext()) {\n"
operator|+
literal|"                final String v = (String) ((Object[]) inputEnumerator.current())[10];\n"
operator|+
literal|"                if (v != null&& org.apache.calcite.runtime.SqlFunctions.eq(v, \"USA\")) {\n"
operator|+
literal|"                  return true;\n"
operator|+
literal|"                }\n"
operator|+
literal|"              }\n"
operator|+
literal|"              return false;\n"
operator|+
literal|"            }\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testPlan2
parameter_list|()
block|{
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
name|withDefaultSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"customer\".\"state_province\" as \"c0\",\n"
operator|+
literal|" \"customer\".\"country\" as \"c1\"\n"
operator|+
literal|"from \"customer\" as \"customer\"\n"
operator|+
literal|"where (\"customer\".\"country\" = 'USA')\n"
operator|+
literal|"and UPPER(\"customer\".\"state_province\") = UPPER('CA')\n"
operator|+
literal|"group by \"customer\".\"state_province\", \"customer\".\"country\"\n"
operator|+
literal|"order by \"customer\".\"state_province\" ASC"
argument_list|)
operator|.
name|planContains
argument_list|(
literal|"          public boolean moveNext() {\n"
operator|+
literal|"            while (inputEnumerator.moveNext()) {\n"
operator|+
literal|"              final Object[] current12 = (Object[]) inputEnumerator.current();\n"
operator|+
literal|"              final String v1 = (String) current12[10];\n"
operator|+
literal|"              if (org.apache.calcite.runtime.SqlFunctions.eq((String) current12[12], \"USA\")&& (v1 != null&& org.apache.calcite.runtime.SqlFunctions.eq(org.apache.calcite.runtime.SqlFunctions.upper(v1), org.apache.calcite.runtime.SqlFunctions.trim(org.apache.calcite.runtime.SqlFunctions.upper(\"CA\"))))) {\n"
operator|+
literal|"                return true;\n"
operator|+
literal|"              }\n"
operator|+
literal|"            }\n"
operator|+
literal|"            return false;\n"
operator|+
literal|"          }\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPlan3
parameter_list|()
block|{
comment|// Plan should contain 'join'. If it doesn't, maybe int-vs-Integer
comment|// data type incompatibility has caused it to use a cartesian product
comment|// instead, and that would be wrong.
comment|//
comment|// inventory_fact_1997 is on the LHS because it is larger than store.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store\".\"store_country\" as \"c0\", sum(\"inventory_fact_1997\".\"supply_time\") as \"m0\" from \"store\" as \"store\", \"inventory_fact_1997\" as \"inventory_fact_1997\" where \"inventory_fact_1997\".\"store_id\" = \"store\".\"store_id\" group by \"store\".\"store_country\""
argument_list|)
operator|.
name|planContains
argument_list|(
literal|" left.join(right, new org.apache.calcite.linq4j.function.Function1() {\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontJdbcBackLinqMiddleTest.java
end_comment

end_unit

