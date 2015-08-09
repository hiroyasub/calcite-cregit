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
name|linq4j
operator|.
name|function
operator|.
name|Function0
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
name|type
operator|.
name|SqlTypeName
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
name|ImmutableBitSet
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
name|Iterables
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
literal|"     }\n"
return|;
block|}
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
literal|"  BindableTableScan(table=[[]])"
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
literal|"    BindableTableScan(table=[[]])"
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
literal|"        BindableTableScan(table=[[]])"
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
comment|/**    * Regression test for CALCITE-809    */
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
literal|"  BindableTableScan(table=[[]])"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
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
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|input
parameter_list|)
block|{
try|try
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ResultSetMetaData
name|metaData
init|=
name|input
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
name|input
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
name|CalciteAssert
operator|.
name|rowToString
argument_list|(
name|input
argument_list|,
name|buf
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|String
name|actualRow
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
implements|,
name|StreamableTable
block|{
specifier|protected
specifier|final
name|RelProtoDataType
name|protoRowType
init|=
operator|new
name|RelProtoDataType
argument_list|()
block|{
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataTypeFactory
name|a0
parameter_list|)
block|{
return|return
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
return|;
block|}
block|}
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
expr|<
name|ImmutableBitSet
operator|>
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
specifier|final
name|ImmutableList
argument_list|<
name|Object
index|[]
argument_list|>
name|rows
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|Object
index|[]
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
argument_list|,
operator|new
name|Object
index|[]
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
argument_list|,
operator|new
name|Object
index|[]
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
argument_list|,
operator|new
name|Object
index|[]
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
argument_list|,
operator|new
name|Object
index|[]
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
argument_list|)
decl_stmt|;
return|return
operator|new
name|OrdersTable
argument_list|(
name|rows
argument_list|)
return|;
block|}
specifier|private
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
specifier|public
specifier|static
specifier|final
name|Function0
argument_list|<
name|Object
index|[]
argument_list|>
name|ROW_GENERATOR
init|=
operator|new
name|Function0
argument_list|()
block|{
specifier|private
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|items
init|=
name|Iterables
operator|.
name|cycle
argument_list|(
literal|"paint"
argument_list|,
literal|"paper"
argument_list|,
literal|"brush"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|apply
parameter_list|()
block|{
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
name|counter
operator|++
block|,
name|items
operator|.
name|next
argument_list|()
block|,
literal|10
block|}
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Table representing an infinitely larger ORDERS stream.    */
specifier|public
specifier|static
class|class
name|InfiniteOrdersTable
extends|extends
name|BaseOrderStreamTable
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
operator|new
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Object
index|[]
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|next
parameter_list|()
block|{
return|return
name|ROW_GENERATOR
operator|.
name|apply
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
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
name|this
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End StreamTest.java
end_comment

end_unit

