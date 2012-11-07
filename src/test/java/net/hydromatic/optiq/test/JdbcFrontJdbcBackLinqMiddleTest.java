begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for a JDBC front-end and JDBC back-end where the processing is not  * pushed down to JDBC (as in {@link JdbcFrontJdbcBackTest}) but is executed  * in a pipeline of linq4j operators.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontJdbcBackLinqMiddleTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testTable
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testWhere
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testWhere2
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testCase
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testGroup
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
name|returns
argument_list|(
literal|"S=T; C=2; MW=Thursday\n"
operator|+
literal|"S=F; C=1; MW=Friday\n"
operator|+
literal|"S=W; C=1; MW=Wednesday\n"
operator|+
literal|"S=S; C=2; MW=Saturday\n"
operator|+
literal|"S=M; C=1; MW=Monday\n"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGroupEmpty
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testJoinGroupByEmpty
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
specifier|public
name|void
name|testJoinGroupByOrderBy
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*), c.\"state_province\", sum(s.\"unit_sales\") as s\n"
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
name|returns
argument_list|(
literal|"EXPR$0=24442; state_province=CA; S=74748\n"
operator|+
literal|"EXPR$0=21611; state_province=OR; S=67659\n"
operator|+
literal|"EXPR$0=40784; state_province=WA; S=124366\n"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompositeGroupBy
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
operator|.
name|query
argument_list|(
literal|"select count(*) as c, c.\"state_province\"\n"
operator|+
literal|"from \"foodmart\".\"customer\" as c\n"
operator|+
literal|"group by c.\"state_province\", c.\"country\"\n"
operator|+
literal|"order by c.\"state_province\", 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=1717; state_province=BC\n"
operator|+
literal|"C=4222; state_province=CA\n"
operator|+
literal|"C=347; state_province=DF\n"
operator|+
literal|"C=106; state_province=Guerrero\n"
operator|+
literal|"C=104; state_province=Jalisco\n"
operator|+
literal|"C=97; state_province=Mexico\n"
operator|+
literal|"C=1051; state_province=OR\n"
operator|+
literal|"C=90; state_province=Oaxaca\n"
operator|+
literal|"C=78; state_province=Sinaloa\n"
operator|+
literal|"C=93; state_province=Veracruz\n"
operator|+
literal|"C=2086; state_province=WA\n"
operator|+
literal|"C=99; state_province=Yucatan\n"
operator|+
literal|"C=191; state_province=Zacatecas\n"
argument_list|)
expr_stmt|;
block|}
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
name|assertThat
argument_list|()
operator|.
name|inJdbcFoodmart
argument_list|()
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
name|returns
argument_list|(
literal|"state_province=CA; S=74748; DC=24442\n"
operator|+
literal|"state_province=OR; S=67659; DC=21611\n"
operator|+
literal|"state_province=WA; S=124366; DC=40784\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontJdbcBackLinqMiddleTest.java
end_comment

end_unit

