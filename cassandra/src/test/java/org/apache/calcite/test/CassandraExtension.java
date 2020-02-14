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
name|concurrent
operator|.
name|StageManager
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
name|org
operator|.
name|apache
operator|.
name|cassandra
operator|.
name|db
operator|.
name|WindowsFailedSnapshotTracker
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
name|service
operator|.
name|CassandraDaemon
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
name|service
operator|.
name|StorageService
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
name|utils
operator|.
name|FBUtilities
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|thrift
operator|.
name|transport
operator|.
name|TTransportException
import|;
end_import

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
name|Cluster
import|;
end_import

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
name|utils
operator|.
name|EmbeddedCassandraServerHelper
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
name|ConditionEvaluationResult
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
name|ExecutionCondition
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
name|ExtensionConfigurationException
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
name|ExtensionContext
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
name|ParameterContext
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
name|ParameterResolutionException
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
name|ParameterResolver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|ExecutionException
import|;
end_import

begin_comment
comment|/**  * JUnit5 extension to start and stop embedded cassandra server.  *  *<p>Note that tests will be skipped if running on JDK11+  * (which is not yet supported by cassandra) see  *<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>.  */
end_comment

begin_class
class|class
name|CassandraExtension
implements|implements
name|ParameterResolver
implements|,
name|ExecutionCondition
block|{
specifier|private
specifier|static
specifier|final
name|ExtensionContext
operator|.
name|Namespace
name|NAMESPACE
init|=
name|ExtensionContext
operator|.
name|Namespace
operator|.
name|create
argument_list|(
name|CassandraExtension
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"cassandra"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|supportsParameter
parameter_list|(
specifier|final
name|ParameterContext
name|parameterContext
parameter_list|,
specifier|final
name|ExtensionContext
name|extensionContext
parameter_list|)
throws|throws
name|ParameterResolutionException
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|parameterContext
operator|.
name|getParameter
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|Session
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
operator|||
name|Cluster
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|resolveParameter
parameter_list|(
specifier|final
name|ParameterContext
name|parameterContext
parameter_list|,
specifier|final
name|ExtensionContext
name|extensionContext
parameter_list|)
throws|throws
name|ParameterResolutionException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|parameterContext
operator|.
name|getParameter
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Session
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|getOrCreate
argument_list|(
name|extensionContext
argument_list|)
operator|.
name|session
return|;
block|}
if|else if
condition|(
name|Cluster
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|getOrCreate
argument_list|(
name|extensionContext
argument_list|)
operator|.
name|cluster
return|;
block|}
throw|throw
operator|new
name|ExtensionConfigurationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s supports only %s or %s but yours was %s"
argument_list|,
name|CassandraExtension
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|Session
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Cluster
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|type
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDataset
parameter_list|(
name|String
name|resourcePath
parameter_list|)
block|{
return|return
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
name|CassandraExtension
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|resourcePath
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Register cassandra resource in root context so it can be shared with other tests    */
specifier|private
specifier|static
name|CassandraResource
name|getOrCreate
parameter_list|(
name|ExtensionContext
name|context
parameter_list|)
block|{
comment|// same cassandra instance should be shared across all extension instances
return|return
name|context
operator|.
name|getRoot
argument_list|()
operator|.
name|getStore
argument_list|(
name|NAMESPACE
argument_list|)
operator|.
name|getOrComputeIfAbsent
argument_list|(
name|KEY
argument_list|,
name|key
lambda|->
operator|new
name|CassandraResource
argument_list|()
argument_list|,
name|CassandraResource
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Whether to run this test.    *<p>Enabled by default, unless explicitly disabled    * from command line ({@code -Dcalcite.test.cassandra=false}) or running on incompatible JDK    * version (see below).    *    *<p>As of this wiring Cassandra 4.x is not yet released and we're using 3.x    * (which fails on JDK11+). All cassandra tests will be skipped if    * running on JDK11+.    *    * @see<a href="https://issues.apache.org/jira/browse/CASSANDRA-9608">CASSANDRA-9608</a>    * @return {@code true} if test is compatible with current environment,    *         {@code false} otherwise    */
annotation|@
name|Override
specifier|public
name|ConditionEvaluationResult
name|evaluateExecutionCondition
parameter_list|(
specifier|final
name|ExtensionContext
name|context
parameter_list|)
block|{
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
if|if
condition|(
name|enabled
operator|&&
name|compatibleJdk
condition|)
block|{
return|return
name|ConditionEvaluationResult
operator|.
name|enabled
argument_list|(
literal|"Cassandra enabled"
argument_list|)
return|;
block|}
return|return
name|ConditionEvaluationResult
operator|.
name|disabled
argument_list|(
literal|"Cassandra tests disabled"
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|CassandraResource
implements|implements
name|ExtensionContext
operator|.
name|Store
operator|.
name|CloseableResource
block|{
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|Cluster
name|cluster
decl_stmt|;
specifier|private
name|CassandraResource
parameter_list|()
block|{
name|startCassandra
argument_list|()
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
name|EmbeddedCassandraServerHelper
operator|.
name|getCluster
argument_list|()
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|EmbeddedCassandraServerHelper
operator|.
name|getSession
argument_list|()
expr_stmt|;
block|}
comment|/**      * Best effort to gracefully shutdown<strong>embedded</strong> cassandra cluster.      *      * Since it uses many static variables as well as {@link System#exit(int)} during close,      * clean shutdown (as part of unit test) is not straightforward.      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|close
argument_list|()
expr_stmt|;
name|CassandraDaemon
name|daemon
init|=
name|extractDaemon
argument_list|()
decl_stmt|;
if|if
condition|(
name|daemon
operator|.
name|thriftServer
operator|!=
literal|null
condition|)
block|{
name|daemon
operator|.
name|thriftServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|daemon
operator|.
name|stopNativeTransport
argument_list|()
expr_stmt|;
name|StorageService
name|storage
init|=
name|StorageService
operator|.
name|instance
decl_stmt|;
name|storage
operator|.
name|setRpcReady
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|storage
operator|.
name|stopClient
argument_list|()
expr_stmt|;
name|storage
operator|.
name|stopTransports
argument_list|()
expr_stmt|;
try|try
block|{
name|storage
operator|.
name|drain
argument_list|()
expr_stmt|;
comment|// try to close all resources
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ExecutionException
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
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|StageManager
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
name|FBUtilities
operator|.
name|isWindows
condition|)
block|{
comment|// for some reason .toDelete stale folder is not deleted on cassandra shutdown
comment|// doing it manually here
name|WindowsFailedSnapshotTracker
operator|.
name|resetForTests
argument_list|()
expr_stmt|;
comment|// manually delete stale file(s)
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|WindowsFailedSnapshotTracker
operator|.
name|TODELETEFILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|startCassandra
parameter_list|()
block|{
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
comment|// Apache Jenkins often fails with
comment|// Cassandra daemon did not start within timeout (20 sec by default)
try|try
block|{
name|EmbeddedCassandraServerHelper
operator|.
name|startEmbeddedCassandra
argument_list|(
name|Duration
operator|.
name|ofMinutes
argument_list|(
literal|1
argument_list|)
operator|.
name|toMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TTransportException
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Extract {@link CassandraDaemon} instance using reflection. It will be used      * to shutdown the cluster      */
specifier|private
specifier|static
name|CassandraDaemon
name|extractDaemon
parameter_list|()
block|{
try|try
block|{
name|Field
name|field
init|=
name|EmbeddedCassandraServerHelper
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"cassandraDaemon"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CassandraDaemon
name|daemon
init|=
operator|(
name|CassandraDaemon
operator|)
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|daemon
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cassandra daemon was not initialized by "
operator|+
name|EmbeddedCassandraServerHelper
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|daemon
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
decl||
name|IllegalAccessException
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
block|}
block|}
end_class

end_unit
