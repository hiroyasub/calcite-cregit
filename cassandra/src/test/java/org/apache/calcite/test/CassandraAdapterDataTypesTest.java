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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|oss
operator|.
name|driver
operator|.
name|api
operator|.
name|core
operator|.
name|CqlSession
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|oss
operator|.
name|driver
operator|.
name|api
operator|.
name|core
operator|.
name|type
operator|.
name|codec
operator|.
name|TypeCodecs
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
name|org
operator|.
name|cassandraunit
operator|.
name|CQLDataLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|cassandraunit
operator|.
name|dataset
operator|.
name|cql
operator|.
name|ClassPathCQLDataSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|BeforeAll
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|ExtendWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|Execution
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|ExecutionMode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.cassandra} package related to data types.  *  *<p>Will start embedded cassandra cluster and populate it from local {@code datatypes.cql} file.  * All configuration files are located in test classpath.  *  *<p>Note that tests will be skipped if running on JDK11+  * (which is not yet supported by cassandra) see  *<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>.  *  */
end_comment

begin_class
annotation|@
name|Execution
argument_list|(
name|ExecutionMode
operator|.
name|SAME_THREAD
argument_list|)
annotation|@
name|ExtendWith
argument_list|(
name|CassandraExtension
operator|.
name|class
argument_list|)
class|class
name|CassandraAdapterDataTypesTest
block|{
comment|/** Connection factory based on the "mongo-zips" model. */
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DTCASSANDRA
init|=
name|CassandraExtension
operator|.
name|getDataset
argument_list|(
literal|"/model-datatypes.json"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeAll
specifier|static
name|void
name|load
parameter_list|(
name|CqlSession
name|session
parameter_list|)
block|{
operator|new
name|CQLDataLoader
argument_list|(
name|session
argument_list|)
operator|.
name|load
argument_list|(
operator|new
name|ClassPathCQLDataSet
argument_list|(
literal|"datatypes.cql"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimpleTypesRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_simple\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[f_int INTEGER"
operator|+
literal|", f_ascii VARCHAR"
operator|+
literal|", f_bigint BIGINT"
operator|+
literal|", f_blob VARBINARY"
operator|+
literal|", f_boolean BOOLEAN"
operator|+
literal|", f_date DATE"
operator|+
literal|", f_decimal DOUBLE"
operator|+
literal|", f_double DOUBLE"
operator|+
literal|", f_duration ANY"
operator|+
literal|", f_float REAL"
operator|+
literal|", f_inet ANY"
operator|+
literal|", f_int_null INTEGER"
operator|+
literal|", f_smallint SMALLINT"
operator|+
literal|", f_text VARCHAR"
operator|+
literal|", f_time BIGINT"
operator|+
literal|", f_timestamp TIMESTAMP"
operator|+
literal|", f_timeuuid CHAR"
operator|+
literal|", f_tinyint TINYINT"
operator|+
literal|", f_uuid CHAR"
operator|+
literal|", f_varchar VARCHAR"
operator|+
literal|", f_varint INTEGER]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFilterWithNonStringLiteral
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_type\" where \"f_id\" = 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_type\" where \"f_id\"> 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_id=3000000000; f_user=ANNA\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_date_type\" where \"f_date\" = '2015-05-03'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_date=2015-05-03; f_user=ANNA\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_timestamp_type\" where cast(\"f_timestamp\" as timestamp "
operator|+
literal|"with local time zone) = '2011-02-03 04:05:00 UTC'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_timestamp=2011-02-03 04:05:00; f_user=ANNA\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_timestamp_type\" where \"f_timestamp\""
operator|+
literal|" = '2011-02-03 04:05:00'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_timestamp=2011-02-03 04:05:00; f_user=ANNA\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimpleTypesValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_simple\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_int=0"
operator|+
literal|"; f_ascii=abcdefg"
operator|+
literal|"; f_bigint=3000000000"
operator|+
literal|"; f_blob=20"
operator|+
literal|"; f_boolean=true"
operator|+
literal|"; f_date=2015-05-03"
operator|+
literal|"; f_decimal=2.1"
operator|+
literal|"; f_double=2.0"
operator|+
literal|"; f_duration=89h9m9s"
operator|+
literal|"; f_float=5.1"
operator|+
literal|"; f_inet=/192.168.0.1"
operator|+
literal|"; f_int_null=null"
operator|+
literal|"; f_smallint=5"
operator|+
literal|"; f_text=abcdefg"
operator|+
literal|"; f_time=48654234000000"
operator|+
literal|"; f_timestamp=2011-02-03 04:05:00"
operator|+
literal|"; f_timeuuid=8ac6d1dc-fbeb-11e9-8f0b-362b9e155667"
operator|+
literal|"; f_tinyint=0"
operator|+
literal|"; f_uuid=123e4567-e89b-12d3-a456-426655440000"
operator|+
literal|"; f_varchar=abcdefg"
operator|+
literal|"; f_varint=10\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCounterRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_counter\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[f_int INTEGER, f_counter BIGINT]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCounterValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_counter\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_int=1; f_counter=1\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionsRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_collections\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[f_int INTEGER"
operator|+
literal|", f_list INTEGER ARRAY"
operator|+
literal|", f_map (VARCHAR, VARCHAR) MAP"
operator|+
literal|", f_set DOUBLE MULTISET"
operator|+
literal|", f_tuple STRUCT]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionsValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_collections\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_int=0"
operator|+
literal|"; f_list=[1, 2, 3]"
operator|+
literal|"; f_map={k1=v1, k2=v2}"
operator|+
literal|"; f_set=[2.0, 3.1]"
operator|+
literal|"; f_tuple={3000000000, 30ff87, 2015-05-03 13:30:54.234}"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionsInnerRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"f_list\"[1], "
operator|+
literal|"\"f_map\"['k1'], "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"1\", "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"2\", "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"3\""
operator|+
literal|" from \"test_collections\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[EXPR$0 INTEGER"
operator|+
literal|", EXPR$1 VARCHAR"
operator|+
literal|", 1 BIGINT"
operator|+
literal|", 2 VARBINARY"
operator|+
literal|", 3 TIMESTAMP]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionsInnerValues
parameter_list|()
block|{
comment|// timestamp retrieval depends on the user timezone, we must compute the expected result
name|long
name|v
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|TypeCodecs
operator|.
name|TIMESTAMP
operator|.
name|parse
argument_list|(
literal|"'2015-05-03 13:30:54.234'"
argument_list|)
argument_list|)
operator|.
name|toEpochMilli
argument_list|()
decl_stmt|;
name|String
name|expectedTimestamp
init|=
name|DateTimeUtils
operator|.
name|unixTimestampToString
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"f_list\"[1], "
operator|+
literal|"\"f_map\"['k1'], "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"1\", "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"2\", "
operator|+
literal|"\"test_collections\".\"f_tuple\".\"3\""
operator|+
literal|" from \"test_collections\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1"
operator|+
literal|"; EXPR$1=v1"
operator|+
literal|"; 1=3000000000"
operator|+
literal|"; 2=30ff87"
operator|+
literal|"; 3="
operator|+
name|expectedTimestamp
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
comment|// frozen collections should not affect the row type
annotation|@
name|Test
name|void
name|testFrozenCollectionsRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_frozen_collections\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[f_int INTEGER"
operator|+
literal|", f_list INTEGER ARRAY"
operator|+
literal|", f_map (VARCHAR, VARCHAR) MAP"
operator|+
literal|", f_set DOUBLE MULTISET"
operator|+
literal|", f_tuple STRUCT]"
argument_list|)
expr_stmt|;
comment|// we should test (BIGINT, VARBINARY, TIMESTAMP) STRUCT but inner types are not exposed
block|}
comment|// frozen collections should not affect the result set
annotation|@
name|Test
name|void
name|testFrozenCollectionsValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|DTCASSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_frozen_collections\""
argument_list|)
operator|.
name|returns
argument_list|(
literal|"f_int=0"
operator|+
literal|"; f_list=[1, 2, 3]"
operator|+
literal|"; f_map={k1=v1, k2=v2}"
operator|+
literal|"; f_set=[2.0, 3.1]"
operator|+
literal|"; f_tuple={3000000000, 30ff87, 2015-05-03 13:30:54.234}"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

