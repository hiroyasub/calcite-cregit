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
name|materialize
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link SqlStatisticProvider} that looks up values in a  * table.  *  *<p>Only for testing.  */
end_comment

begin_enum
specifier|public
enum|enum
name|MapSqlStatisticProvider
implements|implements
name|SqlStatisticProvider
block|{
name|INSTANCE
block|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|CARDINALITY_MAP
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Double
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"[foodmart, agg_c_14_sales_fact_1997]"
argument_list|,
literal|86_805d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, customer]"
argument_list|,
literal|10_281d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, employee]"
argument_list|,
literal|1_155d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, employee_closure]"
argument_list|,
literal|7_179d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, department]"
argument_list|,
literal|10_281d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, inventory_fact_1997]"
argument_list|,
literal|4_070d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, position]"
argument_list|,
literal|18d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, product]"
argument_list|,
literal|1560d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, product_class]"
argument_list|,
literal|110d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, promotion]"
argument_list|,
literal|1_864d
argument_list|)
comment|// region really has 110 rows; made it smaller than store to trick FK
decl|.
name|put
argument_list|(
literal|"[foodmart, region]"
argument_list|,
literal|24d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, salary]"
argument_list|,
literal|21_252d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, sales_fact_1997]"
argument_list|,
literal|86_837d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, store]"
argument_list|,
literal|25d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, store_ragged]"
argument_list|,
literal|25d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, time_by_day]"
argument_list|,
literal|730d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, warehouse]"
argument_list|,
literal|24d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[foodmart, warehouse_class]"
argument_list|,
literal|6d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[scott, EMP]"
argument_list|,
literal|10d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[scott, DEPT]"
argument_list|,
literal|4d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CALL_CENTER]"
argument_list|,
literal|8d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CATALOG_PAGE]"
argument_list|,
literal|11_718d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CATALOG_RETURNS]"
argument_list|,
literal|144_067d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CATALOG_SALES]"
argument_list|,
literal|1_441_548d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CUSTOMER]"
argument_list|,
literal|100_000d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CUSTOMER_ADDRESS]"
argument_list|,
literal|50_000d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, CUSTOMER_DEMOGRAPHICS]"
argument_list|,
literal|1_920_800d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, DATE_DIM]"
argument_list|,
literal|73049d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, DBGEN_VERSION]"
argument_list|,
literal|1d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, HOUSEHOLD_DEMOGRAPHICS]"
argument_list|,
literal|7200d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, INCOME_BAND]"
argument_list|,
literal|20d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, INVENTORY]"
argument_list|,
literal|11_745_000d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, ITEM]"
argument_list|,
literal|18_000d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, PROMOTION]"
argument_list|,
literal|300d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, REASON]"
argument_list|,
literal|35d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, SHIP_MODE]"
argument_list|,
literal|20d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, STORE]"
argument_list|,
literal|12d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, STORE_RETURNS]"
argument_list|,
literal|287_514d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, STORE_SALES]"
argument_list|,
literal|2_880_404d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, TIME_DIM]"
argument_list|,
literal|86_400d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, WAREHOUSE]"
argument_list|,
literal|5d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, WEB_PAGE]"
argument_list|,
literal|60d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, WEB_RETURNS]"
argument_list|,
literal|71_763d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, WEB_SALES]"
argument_list|,
literal|719_384d
argument_list|)
decl|.
name|put
argument_list|(
literal|"[tpcds, WEB_SITE]"
argument_list|,
literal|1d
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|public
name|double
name|tableCardinality
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedTableName
parameter_list|)
block|{
return|return
name|CARDINALITY_MAP
operator|.
name|get
argument_list|(
name|qualifiedTableName
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End MapSqlStatisticProvider.java
end_comment

end_unit

