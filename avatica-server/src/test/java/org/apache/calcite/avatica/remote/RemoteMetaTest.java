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
name|avatica
operator|.
name|remote
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
name|AvaticaConnection
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
name|AvaticaStatement
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
name|ConnectionPropertiesImpl
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
name|Meta
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
name|RemoteDriverTest
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
name|jdbc
operator|.
name|JdbcMeta
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
name|server
operator|.
name|HttpServer
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
name|server
operator|.
name|Main
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
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|Test
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
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
import|import
name|java
operator|.
name|sql
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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

begin_comment
comment|/** Tests covering {@link RemoteMeta}. */
end_comment

begin_class
specifier|public
class|class
name|RemoteMetaTest
block|{
specifier|private
specifier|static
specifier|final
name|RemoteDriverTest
operator|.
name|ConnectionSpec
name|CONNECTION_SPEC
init|=
name|RemoteDriverTest
operator|.
name|ConnectionSpec
operator|.
name|HSQLDB
decl_stmt|;
specifier|private
specifier|static
name|HttpServer
name|start
decl_stmt|;
specifier|private
specifier|static
name|String
name|url
decl_stmt|;
comment|/** Factory that provides a JMeta */
specifier|public
specifier|static
class|class
name|FullyRemoteJdbcMetaFactory
implements|implements
name|Meta
operator|.
name|Factory
block|{
specifier|private
specifier|static
name|JdbcMeta
name|instance
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|JdbcMeta
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|instance
operator|=
operator|new
name|JdbcMeta
argument_list|(
name|CONNECTION_SPEC
operator|.
name|url
argument_list|,
name|CONNECTION_SPEC
operator|.
name|username
argument_list|,
name|CONNECTION_SPEC
operator|.
name|password
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
block|}
return|return
name|instance
return|;
block|}
annotation|@
name|Override
specifier|public
name|Meta
name|create
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
return|return
name|getInstance
argument_list|()
return|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|start
operator|=
name|Main
operator|.
name|start
argument_list|(
operator|new
name|String
index|[]
block|{
name|FullyRemoteJdbcMetaFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|port
init|=
name|start
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|url
operator|=
literal|"jdbc:avatica:remote:url=http://localhost:"
operator|+
name|port
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|start
operator|!=
literal|null
condition|)
block|{
name|start
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Meta
name|getMeta
parameter_list|(
name|AvaticaConnection
name|conn
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|AvaticaConnection
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"meta"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Meta
operator|)
name|f
operator|.
name|get
argument_list|(
name|conn
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Meta
operator|.
name|ExecuteResult
name|prepareAndExecuteInternal
parameter_list|(
name|AvaticaConnection
name|conn
parameter_list|,
specifier|final
name|AvaticaStatement
name|statement
parameter_list|,
name|String
name|sql
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Method
name|m
init|=
name|AvaticaConnection
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"prepareAndExecuteInternal"
argument_list|,
name|AvaticaStatement
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Meta
operator|.
name|ExecuteResult
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|conn
argument_list|,
name|statement
argument_list|,
name|sql
argument_list|,
operator|new
name|Integer
argument_list|(
name|maxRowCount
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Connection
name|getConnection
parameter_list|(
name|JdbcMeta
name|m
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|JdbcMeta
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"connectionCache"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
name|Cache
argument_list|<
name|String
argument_list|,
name|Connection
argument_list|>
name|connectionCache
init|=
operator|(
name|Cache
argument_list|<
name|String
argument_list|,
name|Connection
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
name|m
argument_list|)
decl_stmt|;
return|return
name|connectionCache
operator|.
name|getIfPresent
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoteExecuteMaxRowCount
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|AvaticaConnection
name|conn
init|=
operator|(
name|AvaticaConnection
operator|)
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
init|)
block|{
specifier|final
name|AvaticaStatement
name|statement
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|prepareAndExecuteInternal
argument_list|(
name|conn
argument_list|,
name|statement
argument_list|,
literal|"select * from (values ('a', 1), ('b', 2))"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|statement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Check maxRowCount=0 and ResultSets is 0 row"
argument_list|,
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Check result set meta is still there"
argument_list|,
name|rs
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoteConnectionProperties
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|AvaticaConnection
name|conn
init|=
operator|(
name|AvaticaConnection
operator|)
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
init|)
block|{
name|String
name|id
init|=
name|conn
operator|.
name|id
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ConnectionPropertiesImpl
argument_list|>
name|m
init|=
operator|(
operator|(
name|RemoteMeta
operator|)
name|getMeta
argument_list|(
name|conn
argument_list|)
operator|)
operator|.
name|propsMap
decl_stmt|;
name|assertFalse
argument_list|(
literal|"remote connection map should start ignorant"
argument_list|,
name|m
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
comment|// force creating a connection object on the remote side.
try|try
init|(
specifier|final
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|assertTrue
argument_list|(
literal|"creating a statement starts a local object."
argument_list|,
name|m
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stmt
operator|.
name|execute
argument_list|(
literal|"select count(1) from EMP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Connection
name|remoteConn
init|=
name|getConnection
argument_list|(
name|FullyRemoteJdbcMetaFactory
operator|.
name|getInstance
argument_list|()
argument_list|,
name|id
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|defaultRO
init|=
name|remoteConn
operator|.
name|isReadOnly
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|defaultAutoCommit
init|=
name|remoteConn
operator|.
name|getAutoCommit
argument_list|()
decl_stmt|;
specifier|final
name|String
name|defaultCatalog
init|=
name|remoteConn
operator|.
name|getCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|defaultSchema
init|=
name|remoteConn
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setReadOnly
argument_list|(
operator|!
name|defaultRO
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"local changes dirty local state"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"remote connection has not been touched"
argument_list|,
name|defaultRO
argument_list|,
name|remoteConn
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setAutoCommit
argument_list|(
operator|!
name|defaultAutoCommit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"remote connection has not been touched"
argument_list|,
name|defaultAutoCommit
argument_list|,
name|remoteConn
operator|.
name|getAutoCommit
argument_list|()
argument_list|)
expr_stmt|;
comment|// further interaction with the connection will force a sync
try|try
init|(
specifier|final
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
operator|!
name|defaultAutoCommit
argument_list|,
name|remoteConn
operator|.
name|getAutoCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"local values should be clean"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End RemoteMetaTest.java
end_comment

end_unit

