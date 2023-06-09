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
comment|/**  * Tests for the {@code org.apache.calcite.adapter.cassandra} package.  *  *  * Instantiates a CQL session without keyspace, but passes it to  * {@code org.apache.calcite.adapter.cassandra.CassandraTable}.  * All generated CQL queries should still succeed and explicitly  * reference the keyspace.  */
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
name|CassandraAdapterWithoutKeyspaceTest
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|TWISSANDRA_WITHOUT_KEYSPACE
init|=
name|CassandraExtension
operator|.
name|getDataset
argument_list|(
literal|"/model-without-keyspace.json"
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
literal|"twissandra-small.cql"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|TWISSANDRA_WITHOUT_KEYSPACE
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
block|}
end_class

end_unit

