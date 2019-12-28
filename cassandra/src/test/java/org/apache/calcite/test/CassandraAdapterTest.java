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
name|config
operator|.
name|CalciteSystemProperty
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
name|Bug
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
name|Sources
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cassandra
operator|.
name|config
operator|.
name|DatabaseDescriptor
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
name|CassandraCQLUnit
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.cassandra} package.  *  *<p>Will start embedded cassandra cluster and populate it from local {@code twissandra.cql} file.  * All configuration files are located in test classpath.  *  *<p>Note that tests will be skipped if running on JDK11+  * (which is not yet supported by cassandra) see  *<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>.  *  */
end_comment

begin_comment
comment|// force tests to run sequentially (maven surefire and failsafe are running them in parallel)
end_comment

begin_comment
comment|// seems like some of our code is sharing static variables (like Hooks) which causes tests
end_comment

begin_comment
comment|// to fail non-deterministically (flaky tests).
end_comment

begin_class
annotation|@
name|Execution
argument_list|(
name|ExecutionMode
operator|.
name|SAME_THREAD
argument_list|)
specifier|public
class|class
name|CassandraAdapterTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExternalResource
name|RULE
init|=
name|initCassandraIfEnabled
argument_list|()
decl_stmt|;
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
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|Sources
operator|.
name|of
argument_list|(
name|CassandraAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model.json"
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Whether to run this test.    *<p>Enabled by default, unless explicitly disabled    * from command line ({@code -Dcalcite.test.cassandra=false}) or running on incompatible JDK    * version (see below).    *    *<p>As of this wiring Cassandra 4.x is not yet released and we're using 3.x    * (which fails on JDK11+). All cassandra tests will be skipped if    * running on JDK11+.    *    * @see<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>    * @return {@code true} if test is compatible with current environment,    *         {@code false} otherwise    */
specifier|private
specifier|static
name|boolean
name|enabled
parameter_list|()
block|{
specifier|final
name|boolean
name|enabled
init|=
name|CalciteSystemProperty
operator|.
name|TEST_CASSANDRA
operator|.
name|value
argument_list|()
decl_stmt|;
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"remove JDK version check once current adapter supports Cassandra 4.x"
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|compatibleJdk
init|=
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
operator|<
literal|11
decl_stmt|;
return|return
name|enabled
operator|&&
name|compatibleJdk
return|;
block|}
specifier|private
specifier|static
name|ExternalResource
name|initCassandraIfEnabled
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
argument_list|()
condition|)
block|{
comment|// Return NOP resource (to avoid nulls)
return|return
operator|new
name|ExternalResource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|base
parameter_list|,
specifier|final
name|Description
name|description
parameter_list|)
block|{
return|return
name|super
operator|.
name|apply
argument_list|(
name|base
argument_list|,
name|description
argument_list|)
return|;
block|}
block|}
return|;
block|}
name|String
name|configurationFileName
init|=
literal|null
decl_stmt|;
comment|// use default one
comment|// Apache Jenkins often fails with
comment|// CassandraAdapterTest Cassandra daemon did not start within timeout (20 sec by default)
name|long
name|startUpTimeoutMillis
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
decl_stmt|;
name|CassandraCQLUnit
name|rule
init|=
operator|new
name|CassandraCQLUnit
argument_list|(
operator|new
name|ClassPathCQLDataSet
argument_list|(
literal|"twissandra.cql"
argument_list|)
argument_list|,
name|configurationFileName
argument_list|,
name|startUpTimeoutMillis
argument_list|)
decl_stmt|;
comment|// This static init is necessary otherwise tests fail with CassandraUnit in IntelliJ (jdk10)
comment|// should be called right after constructor
comment|// NullPointerException for DatabaseDescriptor.getDiskFailurePolicy
comment|// for more info see
comment|// https://github.com/jsevellec/cassandra-unit/issues/249
comment|// https://github.com/jsevellec/cassandra-unit/issues/221
name|DatabaseDescriptor
operator|.
name|daemonInitialization
argument_list|()
expr_stmt|;
return|return
name|rule
return|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
comment|// run tests only if explicitly enabled
name|assumeTrue
argument_list|(
literal|"test explicitly disabled"
argument_list|,
name|enabled
argument_list|()
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

