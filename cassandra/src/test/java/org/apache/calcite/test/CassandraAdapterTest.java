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
name|com
operator|.
name|datastax
operator|.
name|driver
operator|.
name|core
operator|.
name|Session
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

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.cassandra} package.  *  *<p>Will start embedded cassandra cluster and populate it from local {@code twissandra.cql} file.  * All configuration files are located in test classpath.  *  *<p>Note that tests will be skipped if running on JDK11+  * (which is not yet supported by cassandra) see  *<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>.  *  */
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
specifier|public
class|class
name|CassandraAdapterTest
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
name|TWISSANDRA
init|=
name|CassandraExtension
operator|.
name|getDataset
argument_list|(
literal|"/model.json"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeAll
specifier|static
name|void
name|load
parameter_list|(
name|Session
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
literal|"twissandra.cql"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"users\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"userline\" where \"username\"='!PUBLIC!'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"username=!PUBLIC!; time=e8754000-80b8-1fe9-8e73-e3698c967ddd; "
operator|+
literal|"tweet_id=f3c329de-d05b-11e5-b58b-90e2ba530b12\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=CassandraToEnumerableConverter\n"
operator|+
literal|"  CassandraFilter(condition=[=($0, '!PUBLIC!')])\n"
operator|+
literal|"    CassandraTableScan(table=[[twissandra, userline]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterUUID
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"tweets\" where \"tweet_id\"='f3cd759c-d05b-11e5-b58b-90e2ba530b12'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
operator|.
name|returns
argument_list|(
literal|"tweet_id=f3cd759c-d05b-11e5-b58b-90e2ba530b12; "
operator|+
literal|"body=Lacus augue pede posuere.; username=JmuhsAaMdw\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=CassandraToEnumerableConverter\n"
operator|+
literal|"  CassandraFilter(condition=[=(CAST($0):CHAR(36), 'f3cd759c-d05b-11e5-b58b-90e2ba530b12')])\n"
operator|+
literal|"    CassandraTableScan(table=[[twissandra, tweets]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"userline\" where \"username\" = '!PUBLIC!' order by \"time\" desc"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|146
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=CassandraToEnumerableConverter\n"
operator|+
literal|"  CassandraSort(sort0=[$1], dir0=[DESC])\n"
operator|+
literal|"    CassandraFilter(condition=[=($0, '!PUBLIC!')])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProject
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"tweet_id\" from \"userline\" where \"username\" = '!PUBLIC!' limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"tweet_id=f3c329de-d05b-11e5-b58b-90e2ba530b12\n"
operator|+
literal|"tweet_id=f3dbb03a-d05b-11e5-b58b-90e2ba530b12\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=CassandraToEnumerableConverter\n"
operator|+
literal|"  CassandraLimit(fetch=[2])\n"
operator|+
literal|"    CassandraProject(tweet_id=[$2])\n"
operator|+
literal|"      CassandraFilter(condition=[=($0, '!PUBLIC!')])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectAlias
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"tweet_id\" as \"foo\" from \"userline\" "
operator|+
literal|"where \"username\" = '!PUBLIC!' limit 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"foo=f3c329de-d05b-11e5-b58b-90e2ba530b12\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectConstant
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select 'foo' as \"bar\" from \"userline\" limit 1"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"bar=foo\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"tweet_id\" from \"userline\" where \"username\" = '!PUBLIC!' limit 8"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"CassandraLimit(fetch=[8])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortLimit
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"userline\" where \"username\"='!PUBLIC!' "
operator|+
literal|"order by \"time\" desc limit 10"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"  CassandraLimit(fetch=[10])\n"
operator|+
literal|"    CassandraSort(sort0=[$1], dir0=[DESC])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortOffset
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"tweet_id\" from \"userline\" where "
operator|+
literal|"\"username\"='!PUBLIC!' limit 2 offset 1"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"CassandraLimit(offset=[1], fetch=[2])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"tweet_id=f3dbb03a-d05b-11e5-b58b-90e2ba530b12\n"
operator|+
literal|"tweet_id=f3e4182e-d05b-11e5-b58b-90e2ba530b12\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaterializedView
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|TWISSANDRA
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"tweet_id\" from \"tweets\" where \"username\"='JmuhsAaMdw'"
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"CassandraTableScan(table=[[twissandra, Tweets_By_User]])"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

