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
name|DataContext
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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Linq4j
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
name|RelCollations
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|RelProtoDataType
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
name|ScannableTable
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
name|Schema
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
name|Statistic
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
name|Statistics
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
name|StreamableTable
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
name|Table
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
name|TableFactory
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
name|sql
operator|.
name|SqlCall
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
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
name|ResultSetMetaData
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
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests for streaming queries.  */
end_comment

begin_class
specifier|public
class|class
name|StreamTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_SCHEMA_NAME
init|=
literal|"STREAMS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INFINITE_STREAM_SCHEMA_NAME
init|=
literal|"INFINITE_STREAMS"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_JOINS_SCHEMA_NAME
init|=
literal|"STREAM_JOINS"
decl_stmt|;
specifier|private
specifier|static
name|String
name|schemaFor
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|TableFactory
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
literal|"     {\n"
operator|+
literal|"       name: '"
operator|+
name|name
operator|+
literal|"',\n"
operator|+
literal|"       tables: [ {\n"
operator|+
literal|"         type: 'custom',\n"
operator|+
literal|"         name: 'ORDERS',\n"
operator|+
literal|"         stream: {\n"
operator|+
literal|"           stream: true\n"
operator|+
literal|"         },\n"
operator|+
literal|"         factory: '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       } ]\n"
operator|+
literal|"     }"
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|STREAM_JOINS_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'STREAM_JOINS',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'STREAM_JOINS',\n"
operator|+
literal|"       tables: [ {\n"
operator|+
literal|"         type: 'custom',\n"
operator|+
literal|"         name: 'ORDERS',\n"
operator|+
literal|"         stream: {\n"
operator|+
literal|"           stream: true\n"
operator|+
literal|"         },\n"
operator|+
literal|"         factory: '"
operator|+
name|OrdersStreamTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       }, \n"
operator|+
literal|"       {\n"
operator|+
literal|"         type: 'custom',\n"
operator|+
literal|"         name: 'PRODUCTS',\n"
operator|+
literal|"         factory: '"
operator|+
name|ProductsTableFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"       }]\n"
operator|+
literal|"     }]}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'foodmart',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|schemaFor
argument_list|(
name|STREAM_SCHEMA_NAME
argument_list|,
name|OrdersStreamTableFactory
operator|.
name|class
argument_list|)
operator|+
literal|",\n"
operator|+
name|schemaFor
argument_list|(
name|INFINITE_STREAM_SCHEMA_NAME
argument_list|,
name|InfiniteOrdersStreamTableFactory
operator|.
name|class
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testStream
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"STREAMS"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream * from orders"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalProject(ROWTIME=[$0], ID=[$1], PRODUCT=[$2], UNITS=[$3])\n"
operator|+
literal|"    LogicalTableScan(table=[[STREAMS, ORDERS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"ROWTIME=2015-02-15 10:15:00; ID=1; PRODUCT=paint; UNITS=10"
argument_list|,
literal|"ROWTIME=2015-02-15 10:24:15; ID=2; PRODUCT=paper; UNITS=5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamFilterProject
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"STREAMS"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream product from orders where units> 6"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalProject(PRODUCT=[$2])\n"
operator|+
literal|"    LogicalFilter(condition=[>($3, 6)])\n"
operator|+
literal|"      LogicalTableScan(table=[[STREAMS, ORDERS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..3=[{inputs}], expr#4=[6], expr#5=[>($t3, $t4)], PRODUCT=[$t2], $condition=[$t5])\n"
operator|+
literal|"  EnumerableInterpreter\n"
operator|+
literal|"    BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"PRODUCT=paint"
argument_list|,
literal|"PRODUCT=brush"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamGroupByHaving
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"STREAMS"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream floor(rowtime to hour) as rowtime,\n"
operator|+
literal|"  product, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by floor(rowtime to hour), product\n"
operator|+
literal|"having count(*)> 1"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalFilter(condition=[>($2, 1)])\n"
operator|+
literal|"    LogicalAggregate(group=[{0, 1}], C=[COUNT()])\n"
operator|+
literal|"      LogicalProject(ROWTIME=[FLOOR($0, FLAG(HOUR))], PRODUCT=[$2])\n"
operator|+
literal|"        LogicalTableScan(table=[[STREAMS, ORDERS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableCalc(expr#0..2=[{inputs}], expr#3=[1], expr#4=[>($t2, $t3)], proj#0..2=[{exprs}], $condition=[$t4])\n"
operator|+
literal|"  EnumerableAggregate(group=[{0, 1}], C=[COUNT()])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[FLAG(HOUR)], expr#5=[FLOOR($t0, $t4)], ROWTIME=[$t5], PRODUCT=[$t2])\n"
operator|+
literal|"      EnumerableInterpreter\n"
operator|+
literal|"        BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; C=2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamOrderBy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"STREAMS"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream floor(rowtime to hour) as rowtime,\n"
operator|+
literal|"  product, units\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by floor(orders.rowtime to hour), product desc"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"    LogicalProject(ROWTIME=[FLOOR($0, FLAG(HOUR))], PRODUCT=[$2], UNITS=[$3])\n"
operator|+
literal|"      LogicalTableScan(table=[[STREAMS, ORDERS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[FLAG(HOUR)], expr#5=[FLOOR($t0, $t4)], ROWTIME=[$t5], PRODUCT=[$t2], UNITS=[$t3])\n"
operator|+
literal|"    EnumerableInterpreter\n"
operator|+
literal|"      BindableTableScan(table=[[STREAMS, ORDERS, (STREAM)]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paper; UNITS=5"
argument_list|,
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=10"
argument_list|,
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testStreamUnionAllOrderBy
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
literal|"STREAMS"
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from (\n"
operator|+
literal|"  select rowtime, product\n"
operator|+
literal|"  from orders\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select rowtime, product\n"
operator|+
literal|"  from orders)\n"
operator|+
literal|"order by rowtime\n"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    LogicalProject(ROWTIME=[$0], PRODUCT=[$1])\n"
operator|+
literal|"      LogicalUnion(all=[true])\n"
operator|+
literal|"        LogicalProject(ROWTIME=[$0], PRODUCT=[$2])\n"
operator|+
literal|"          EnumerableTableScan(table=[[STREAMS, ORDERS]])\n"
operator|+
literal|"        LogicalProject(ROWTIME=[$0], PRODUCT=[$2])\n"
operator|+
literal|"          EnumerableTableScan(table=[[STREAMS, ORDERS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableSort(sort0=[$0], sort1=[$1], dir0=[ASC], dir1=[DESC])\n"
operator|+
literal|"  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[FLAG(HOUR)], expr#5=[FLOOR($t0, $t4)], ROWTIME=[$t5], PRODUCT=[$t2], UNITS=[$t3])\n"
operator|+
literal|"    EnumerableInterpreter\n"
operator|+
literal|"      BindableTableScan(table=[[]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paper; UNITS=5"
argument_list|,
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=10"
argument_list|,
literal|"ROWTIME=2015-02-15 10:00:00; PRODUCT=paint; UNITS=3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-809">[CALCITE-809]    * TableScan does not support large/infinite scans</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testInfiniteStreamsDoNotBufferInMemory
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
name|INFINITE_STREAM_SCHEMA_NAME
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream * from orders"
argument_list|)
operator|.
name|limit
argument_list|(
literal|100
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[INFINITE_STREAMS, ORDERS, (STREAM)]])"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testStreamCancel
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[INFINITE_STREAMS, ORDERS, (STREAM)]])"
decl_stmt|;
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
name|INFINITE_STREAM_SCHEMA_NAME
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream * from orders"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returns
argument_list|(
name|resultSet
lambda|->
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
operator|++
name|n
operator|==
literal|5
condition|)
block|{
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|getStatement
argument_list|()
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
literal|"expected cancel, got end-of-data"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"Statement canceled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// With a 3 millisecond delay, typically n is between 200 - 400
comment|// before cancel takes effect.
name|assertTrue
argument_list|(
literal|"n is "
operator|+
name|n
argument_list|,
name|n
operator|>
literal|5
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamToRelationJoin
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_JOINS_MODEL
argument_list|)
operator|.
name|withDefaultSchema
argument_list|(
name|STREAM_JOINS_SCHEMA_NAME
argument_list|)
operator|.
name|query
argument_list|(
literal|"select stream "
operator|+
literal|"orders.rowtime as rowtime, orders.id as orderId, products.supplier as supplierId "
operator|+
literal|"from orders join products on orders.product = products.id"
argument_list|)
operator|.
name|convertContains
argument_list|(
literal|"LogicalDelta\n"
operator|+
literal|"  LogicalProject(ROWTIME=[$0], ORDERID=[$1], SUPPLIERID=[$6])\n"
operator|+
literal|"    LogicalJoin(condition=[=($4, $5)], joinType=[inner])\n"
operator|+
literal|"      LogicalProject(ROWTIME=[$0], ID=[$1], PRODUCT=[$2], UNITS=[$3], PRODUCT0=[CAST($2):VARCHAR(32) NOT NULL])\n"
operator|+
literal|"        LogicalTableScan(table=[[STREAM_JOINS, ORDERS]])\n"
operator|+
literal|"      LogicalTableScan(table=[[STREAM_JOINS, PRODUCTS]])\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableCalc(expr#0..6=[{inputs}], proj#0..1=[{exprs}], SUPPLIERID=[$t6])\n"
operator|+
literal|"  EnumerableJoin(condition=[=($4, $5)], joinType=[inner])\n"
operator|+
literal|"    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[CAST($t2):VARCHAR(32) NOT NULL], proj#0..4=[{exprs}])\n"
operator|+
literal|"      EnumerableInterpreter\n"
operator|+
literal|"        BindableTableScan(table=[[STREAM_JOINS, ORDERS, (STREAM)]])\n"
operator|+
literal|"    EnumerableInterpreter\n"
operator|+
literal|"      BindableTableScan(table=[[STREAM_JOINS, PRODUCTS]])"
argument_list|)
operator|.
name|returns
argument_list|(
name|startsWith
argument_list|(
literal|"ROWTIME=2015-02-15 10:15:00; ORDERID=1; SUPPLIERID=1"
argument_list|,
literal|"ROWTIME=2015-02-15 10:24:15; ORDERID=2; SUPPLIERID=0"
argument_list|,
literal|"ROWTIME=2015-02-15 10:24:45; ORDERID=3; SUPPLIERID=1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testTumbleViaOver
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"WITH HourlyOrderTotals (rowtime, productId, c, su) AS (\n"
operator|+
literal|"  SELECT FLOOR(rowtime TO HOUR),\n"
operator|+
literal|"    productId,\n"
operator|+
literal|"    COUNT(*),\n"
operator|+
literal|"    SUM(units)\n"
operator|+
literal|"  FROM Orders\n"
operator|+
literal|"  GROUP BY FLOOR(rowtime TO HOUR), productId)\n"
operator|+
literal|"SELECT STREAM rowtime,\n"
operator|+
literal|"  productId,\n"
operator|+
literal|"  SUM(su) OVER w AS su,\n"
operator|+
literal|"  SUM(c) OVER w AS c\n"
operator|+
literal|"FROM HourlyTotals\n"
operator|+
literal|"WINDOW w AS (\n"
operator|+
literal|"  ORDER BY rowtime\n"
operator|+
literal|"  PARTITION BY productId\n"
operator|+
literal|"  RANGE INTERVAL '2' HOUR PRECEDING)\n"
decl_stmt|;
name|String
name|sql2
init|=
literal|""
operator|+
literal|"SELECT STREAM rowtime, productId, SUM(units) AS su, COUNT(*) AS c\n"
operator|+
literal|"FROM Orders\n"
operator|+
literal|"GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR)"
decl_stmt|;
comment|// sql and sql2 should give same result
name|CalciteAssert
operator|.
name|model
argument_list|(
name|STREAM_JOINS_MODEL
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Consumer
argument_list|<
name|ResultSet
argument_list|>
name|startsWith
parameter_list|(
name|String
modifier|...
name|rows
parameter_list|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|rowList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rows
argument_list|)
decl_stmt|;
return|return
name|resultSet
lambda|->
block|{
try|try
block|{
specifier|final
name|CalciteAssert
operator|.
name|ResultSetFormatter
name|formatter
init|=
operator|new
name|CalciteAssert
operator|.
name|ResultSetFormatter
argument_list|()
decl_stmt|;
specifier|final
name|ResultSetMetaData
name|metaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedRow
range|:
name|rowList
control|)
block|{
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"input ended too soon"
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|rowToString
argument_list|(
name|resultSet
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|String
name|actualRow
init|=
name|formatter
operator|.
name|string
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualRow
argument_list|,
name|equalTo
argument_list|(
name|expectedRow
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
block|}
return|;
block|}
comment|/**    * Base table for the Orders table. Manages the base schema used for the test tables and common    * functions.    */
specifier|private
specifier|abstract
specifier|static
class|class
name|BaseOrderStreamTable
implements|implements
name|ScannableTable
block|{
specifier|protected
specifier|final
name|RelProtoDataType
name|protoRowType
init|=
name|a0
lambda|->
name|a0
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"ROWTIME"
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
literal|"ID"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|add
argument_list|(
literal|"PRODUCT"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|10
argument_list|)
operator|.
name|add
argument_list|(
literal|"UNITS"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
literal|100d
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|RelCollations
operator|.
name|createSingleton
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Mock table that returns a stream of orders from a fixed array. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
specifier|static
class|class
name|OrdersStreamTableFactory
implements|implements
name|TableFactory
argument_list|<
name|Table
argument_list|>
block|{
comment|// public constructor, per factory contract
specifier|public
name|OrdersStreamTableFactory
parameter_list|()
block|{
block|}
specifier|public
name|Table
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|OrdersTable
argument_list|(
name|getRowList
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|getRowList
parameter_list|()
block|{
specifier|final
name|Object
index|[]
index|[]
name|rows
init|=
block|{
block|{
name|ts
argument_list|(
literal|10
argument_list|,
literal|15
argument_list|,
literal|0
argument_list|)
block|,
literal|1
block|,
literal|"paint"
block|,
literal|10
block|}
block|,
block|{
name|ts
argument_list|(
literal|10
argument_list|,
literal|24
argument_list|,
literal|15
argument_list|)
block|,
literal|2
block|,
literal|"paper"
block|,
literal|5
block|}
block|,
block|{
name|ts
argument_list|(
literal|10
argument_list|,
literal|24
argument_list|,
literal|45
argument_list|)
block|,
literal|3
block|,
literal|"brush"
block|,
literal|12
block|}
block|,
block|{
name|ts
argument_list|(
literal|10
argument_list|,
literal|58
argument_list|,
literal|0
argument_list|)
block|,
literal|4
block|,
literal|"paint"
block|,
literal|3
block|}
block|,
block|{
name|ts
argument_list|(
literal|11
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|)
block|,
literal|5
block|,
literal|"paint"
block|,
literal|3
block|}
block|}
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rows
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Object
name|ts
parameter_list|(
name|int
name|h
parameter_list|,
name|int
name|m
parameter_list|,
name|int
name|s
parameter_list|)
block|{
return|return
name|DateTimeUtils
operator|.
name|unixTimestamp
argument_list|(
literal|2015
argument_list|,
literal|2
argument_list|,
literal|15
argument_list|,
name|h
argument_list|,
name|m
argument_list|,
name|s
argument_list|)
return|;
block|}
block|}
comment|/** Table representing the ORDERS stream. */
specifier|public
specifier|static
class|class
name|OrdersTable
extends|extends
name|BaseOrderStreamTable
implements|implements
name|StreamableTable
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
decl_stmt|;
specifier|public
name|OrdersTable
parameter_list|(
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
parameter_list|)
block|{
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|rows
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Table
name|stream
parameter_list|()
block|{
return|return
operator|new
name|OrdersTable
argument_list|(
name|rows
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Mock table that returns a stream of orders from a fixed array.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
specifier|static
class|class
name|InfiniteOrdersStreamTableFactory
implements|implements
name|TableFactory
argument_list|<
name|Table
argument_list|>
block|{
comment|// public constructor, per factory contract
specifier|public
name|InfiniteOrdersStreamTableFactory
parameter_list|()
block|{
block|}
specifier|public
name|Table
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|InfiniteOrdersTable
argument_list|()
return|;
block|}
block|}
comment|/**    * Table representing an infinitely larger ORDERS stream.    */
specifier|public
specifier|static
class|class
name|InfiniteOrdersTable
extends|extends
name|BaseOrderStreamTable
implements|implements
name|StreamableTable
block|{
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
parameter_list|()
lambda|->
operator|new
name|Iterator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|final
name|String
index|[]
name|items
operator|=
block|{
literal|"paint"
block|,
literal|"paper"
block|,
literal|"brush"
block|}
argument_list|;
specifier|private
name|int
name|counter
operator|=
literal|0
argument_list|;
specifier|public
name|boolean
name|hasNext
argument_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Object
index|[]
name|next
parameter_list|()
block|{
specifier|final
name|int
name|index
init|=
name|counter
operator|++
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|System
operator|.
name|currentTimeMillis
argument_list|()
block|,
name|index
block|,
name|items
index|[
name|index
operator|%
name|items
operator|.
name|length
index|]
block|,
literal|10
block|}
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|)
class|;
block|}
end_class

begin_function
specifier|public
name|Table
name|stream
parameter_list|()
block|{
return|return
name|this
return|;
block|}
end_function

begin_comment
unit|}
comment|/** Table representing the history of the ORDERS stream. */
end_comment

begin_class
unit|public
specifier|static
class|class
name|OrdersHistoryTable
extends|extends
name|BaseOrderStreamTable
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
decl_stmt|;
specifier|public
name|OrdersHistoryTable
parameter_list|(
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
parameter_list|)
block|{
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|rows
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**    * Mocks a simple relation to use for stream joining test.    */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|ProductsTableFactory
implements|implements
name|TableFactory
argument_list|<
name|Table
argument_list|>
block|{
specifier|public
name|Table
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|Object
index|[]
index|[]
name|rows
init|=
block|{
block|{
literal|"paint"
block|,
literal|1
block|}
block|,
block|{
literal|"paper"
block|,
literal|0
block|}
block|,
block|{
literal|"brush"
block|,
literal|1
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ProductsTable
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rows
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**    * Table representing the PRODUCTS relation.    */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|ProductsTable
implements|implements
name|ScannableTable
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
decl_stmt|;
specifier|public
name|ProductsTable
parameter_list|(
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
parameter_list|)
block|{
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
block|}
specifier|private
specifier|final
name|RelProtoDataType
name|protoRowType
init|=
name|a0
lambda|->
name|a0
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"ID"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|32
argument_list|)
operator|.
name|add
argument_list|(
literal|"SUPPLIER"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|rows
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
literal|200d
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
unit|}
comment|// End StreamTest.java
end_comment

end_unit

