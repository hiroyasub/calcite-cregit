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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function1
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
comment|/**  * Tests for a JDBC front-end and JDBC back-end.  *  *<p>The idea is that as much as possible of the query is pushed down  * to the JDBC data source, in the form of a large (and hopefully efficient)  * SQL statement.</p>  *  * @see JdbcFrontJdbcBackLinqMiddleTest  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontJdbcBackTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testWhere2
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
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
name|testTables
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
name|ResultSet
name|rset
init|=
name|a0
operator|.
name|getMetaData
argument_list|()
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|rset
operator|.
name|next
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|rset
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"account;agg_c_10_sales_fact_1997;agg_c_14_sales_fact_1997;agg_c_special_sales_fact_1997;agg_g_ms_pcat_sales_fact_1997;agg_l_03_sales_fact_1997;agg_l_04_sales_fact_1997;agg_l_05_sales_fact_1997;agg_lc_06_sales_fact_1997;agg_lc_100_sales_fact_1997;agg_ll_01_sales_fact_1997;agg_pl_01_sales_fact_1997;category;currency;customer;days;department;employee;employee_closure;expense_fact;inventory_fact_1997;inventory_fact_1998;position;product;product_class;products;promotion;region;reserve_employee;salary;sales_fact_1997;sales_fact_1998;sales_fact_dec_1998;store;store_ragged;time_by_day;warehouse;warehouse_class;COLUMNS;TABLES;"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTablesByType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|REGULAR_PLUS_METADATA
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
name|ResultSet
name|rset
init|=
name|a0
operator|.
name|getMetaData
argument_list|()
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"SYSTEM_TABLE"
block|}
argument_list|)
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|rset
operator|.
name|next
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|rset
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"COLUMNS;TABLES;"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumns
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
name|ResultSet
name|rset
init|=
name|a0
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"sales_fact_1997"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|rset
operator|.
name|next
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|rset
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"product_id;time_id;customer_id;promotion_id;store_id;store_sales;store_cost;unit_sales;"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a JDBC method known to be not implemented (as it happens,    * {@link java.sql.DatabaseMetaData#getPrimaryKeys}) that therefore uses    * empty result set. */
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
argument_list|)
operator|.
name|doWithConnection
argument_list|(
operator|new
name|Function1
argument_list|<
name|OptiqConnection
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|OptiqConnection
name|a0
parameter_list|)
block|{
try|try
block|{
name|ResultSet
name|rset
init|=
name|a0
operator|.
name|getMetaData
argument_list|()
operator|.
name|getPrimaryKeys
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"sales_fact_1997"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|rset
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
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
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
argument_list|)
operator|.
name|withSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select case when \"sales_fact_1997\".\"promotion_id\" = 1 then 0\n"
operator|+
literal|"                        else \"sales_fact_1997\".\"store_sales\" end as \"c0\"\n"
operator|+
literal|"from \"sales_fact_1997\" as \"sales_fact_1997\""
operator|+
literal|"where \"product_id\" = 1\n"
operator|+
literal|"and \"time_id\"< 400"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=11.4000\n"
operator|+
literal|"c0=8.5500\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontJdbcBackTest.java
end_comment

end_unit

