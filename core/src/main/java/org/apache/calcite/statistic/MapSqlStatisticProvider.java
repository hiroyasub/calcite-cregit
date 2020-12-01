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
name|statistic
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcTable
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
name|SqlStatisticProvider
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
name|RelOptTable
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
name|ImmutableMap
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
name|ImmutableMultimap
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
name|stream
operator|.
name|Collectors
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
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|cardinalityMap
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMultimap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
argument_list|>
name|keyMap
decl_stmt|;
name|MapSqlStatisticProvider
parameter_list|()
block|{
specifier|final
name|Initializer
name|initializer
init|=
operator|new
name|Initializer
argument_list|()
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"agg_c_14_sales_fact_1997"
argument_list|,
literal|86_805
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"account"
argument_list|,
literal|11
argument_list|,
literal|"account_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"category"
argument_list|,
literal|4
argument_list|,
literal|"category_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"currency"
argument_list|,
literal|10_281
argument_list|,
literal|"currency_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"customer"
argument_list|,
literal|10_281
argument_list|,
literal|"customer_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"days"
argument_list|,
literal|7
argument_list|,
literal|"day"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"employee"
argument_list|,
literal|1_155
argument_list|,
literal|"employee_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"employee_closure"
argument_list|,
literal|7_179
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"department"
argument_list|,
literal|10_281
argument_list|,
literal|"department_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"inventory_fact_1997"
argument_list|,
literal|4_070
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"position"
argument_list|,
literal|18
argument_list|,
literal|"position_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"product"
argument_list|,
literal|1560
argument_list|,
literal|"product_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"product_class"
argument_list|,
literal|110
argument_list|,
literal|"product_class_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"promotion"
argument_list|,
literal|1_864
argument_list|,
literal|"promotion_id"
argument_list|)
comment|// region really has 110 rows; made it smaller than store to trick FK
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"region"
argument_list|,
literal|24
argument_list|,
literal|"region_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"salary"
argument_list|,
literal|21_252
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"sales_fact_1997"
argument_list|,
literal|86_837
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"store"
argument_list|,
literal|25
argument_list|,
literal|"store_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"store_ragged"
argument_list|,
literal|25
argument_list|,
literal|"store_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"time_by_day"
argument_list|,
literal|730
argument_list|,
literal|"time_id"
argument_list|,
literal|"the_date"
argument_list|)
comment|// 2 keys
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"warehouse"
argument_list|,
literal|24
argument_list|,
literal|"warehouse_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"foodmart"
argument_list|,
literal|"warehouse_class"
argument_list|,
literal|6
argument_list|,
literal|"warehouse_class_id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"scott"
argument_list|,
literal|"EMP"
argument_list|,
literal|10
argument_list|,
literal|"EMPNO"
argument_list|)
operator|.
name|put
argument_list|(
literal|"scott"
argument_list|,
literal|"DEPT"
argument_list|,
literal|4
argument_list|,
literal|"DEPTNO"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CALL_CENTER"
argument_list|,
literal|8
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CATALOG_PAGE"
argument_list|,
literal|11_718
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CATALOG_RETURNS"
argument_list|,
literal|144_067
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CATALOG_SALES"
argument_list|,
literal|1_441_548
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CUSTOMER"
argument_list|,
literal|100_000
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CUSTOMER_ADDRESS"
argument_list|,
literal|50_000
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"CUSTOMER_DEMOGRAPHICS"
argument_list|,
literal|1_920_800
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"DATE_DIM"
argument_list|,
literal|73049
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"DBGEN_VERSION"
argument_list|,
literal|1
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"HOUSEHOLD_DEMOGRAPHICS"
argument_list|,
literal|7200
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"INCOME_BAND"
argument_list|,
literal|20
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"INVENTORY"
argument_list|,
literal|11_745_000
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"ITEM"
argument_list|,
literal|18_000
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"PROMOTION"
argument_list|,
literal|300
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"REASON"
argument_list|,
literal|35
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"SHIP_MODE"
argument_list|,
literal|20
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"STORE"
argument_list|,
literal|12
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"STORE_RETURNS"
argument_list|,
literal|287_514
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"STORE_SALES"
argument_list|,
literal|2_880_404
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"TIME_DIM"
argument_list|,
literal|86_400
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"WAREHOUSE"
argument_list|,
literal|5
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"WEB_PAGE"
argument_list|,
literal|60
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"WEB_RETURNS"
argument_list|,
literal|71_763
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"WEB_SALES"
argument_list|,
literal|719_384
argument_list|,
literal|"id"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tpcds"
argument_list|,
literal|"WEB_SITE"
argument_list|,
literal|1
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|cardinalityMap
operator|=
name|initializer
operator|.
name|cardinalityMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|keyMap
operator|=
name|initializer
operator|.
name|keyMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|tableCardinality
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedName
init|=
name|table
operator|.
name|maybeUnwrap
argument_list|(
name|JdbcTable
operator|.
name|class
argument_list|)
operator|.
name|map
argument_list|(
name|value
lambda|->
name|Arrays
operator|.
name|asList
argument_list|(
name|value
operator|.
name|jdbcSchemaName
argument_list|,
name|value
operator|.
name|jdbcTableName
argument_list|)
argument_list|)
operator|.
name|orElseGet
argument_list|(
name|table
operator|::
name|getQualifiedName
argument_list|)
decl_stmt|;
return|return
name|cardinalityMap
operator|.
name|get
argument_list|(
name|qualifiedName
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isForeignKey
parameter_list|(
name|RelOptTable
name|fromTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fromColumns
parameter_list|,
name|RelOptTable
name|toTable
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|toColumns
parameter_list|)
block|{
comment|// Assume that anything that references a primary key is a foreign key.
comment|// It's wrong but it's enough for our current test cases.
return|return
name|isKey
argument_list|(
name|toTable
argument_list|,
name|toColumns
argument_list|)
comment|// supervisor_id contains one 0 value, which does not match any
comment|// employee_id, therefore it is not a foreign key
operator|&&
operator|!
literal|"[foodmart, employee].[supervisor_id]"
operator|.
name|equals
argument_list|(
name|fromTable
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|"."
operator|+
name|columnNames
argument_list|(
name|fromTable
argument_list|,
name|fromColumns
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|columns
parameter_list|)
block|{
comment|// In order to match, all column ordinals must be in range 0 .. columnCount
return|return
name|columns
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|columnOrdinal
lambda|->
operator|(
name|columnOrdinal
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|columnOrdinal
operator|<
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|)
argument_list|)
comment|// ... and the column names match the name of the primary key
operator|&&
name|keyMap
operator|.
name|get
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|columnNames
argument_list|(
name|table
argument_list|,
name|columns
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|columnNames
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|columns
parameter_list|)
block|{
return|return
name|columns
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|columnOrdinal
lambda|->
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|get
argument_list|(
name|columnOrdinal
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/** Helper during construction. */
specifier|private
specifier|static
class|class
name|Initializer
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|cardinalityMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
argument_list|>
name|keyMapBuilder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Initializer
name|put
parameter_list|(
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|int
name|count
parameter_list|,
name|Object
modifier|...
name|keys
parameter_list|)
block|{
name|String
name|qualifiedName
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|schema
argument_list|,
name|table
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|cardinalityMapBuilder
operator|.
name|put
argument_list|(
name|qualifiedName
argument_list|,
operator|(
name|double
operator|)
name|count
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|key
range|:
name|keys
control|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|keyList
decl_stmt|;
if|if
condition|(
name|key
operator|instanceof
name|String
condition|)
block|{
name|keyList
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|key
operator|instanceof
name|String
index|[]
condition|)
block|{
name|keyList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
operator|(
name|String
index|[]
operator|)
name|key
argument_list|)
expr_stmt|;
comment|// composite key
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown key "
operator|+
name|key
argument_list|)
throw|;
block|}
name|keyMapBuilder
operator|.
name|put
argument_list|(
name|qualifiedName
argument_list|,
name|keyList
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_enum

end_unit

