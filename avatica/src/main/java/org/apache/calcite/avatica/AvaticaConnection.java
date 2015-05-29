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
name|remote
operator|.
name|TypedValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|CallableStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
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
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|NClob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
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
name|SQLClientInfoException
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
name|SQLWarning
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLXML
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Savepoint
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
name|sql
operator|.
name|Struct
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|ConcurrentHashMap
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
name|Executor
import|;
end_import

begin_comment
comment|/**  * Implementation of JDBC connection  * for the Avatica framework.  *  *<p>Abstract to allow newer versions of JDBC to add methods.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AvaticaConnection
implements|implements
name|Connection
block|{
specifier|protected
name|int
name|statementCount
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|int
name|holdability
decl_stmt|;
specifier|private
name|int
name|networkTimeout
decl_stmt|;
specifier|public
specifier|final
name|String
name|id
decl_stmt|;
specifier|public
specifier|final
name|Meta
operator|.
name|ConnectionHandle
name|handle
decl_stmt|;
specifier|protected
specifier|final
name|UnregisteredDriver
name|driver
decl_stmt|;
specifier|protected
specifier|final
name|AvaticaFactory
name|factory
decl_stmt|;
specifier|final
name|String
name|url
decl_stmt|;
specifier|protected
specifier|final
name|Properties
name|info
decl_stmt|;
specifier|protected
specifier|final
name|Meta
name|meta
decl_stmt|;
specifier|protected
specifier|final
name|AvaticaDatabaseMetaData
name|metaData
decl_stmt|;
specifier|public
specifier|final
name|Helper
name|helper
init|=
name|Helper
operator|.
name|INSTANCE
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|InternalProperty
argument_list|,
name|Object
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|AvaticaStatement
argument_list|>
name|statementMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Creates an AvaticaConnection.    *    *<p>Not public; method is called only from the driver or a derived    * class.</p>    *    * @param driver Driver    * @param factory Factory for JDBC objects    * @param url Server URL    * @param info Other connection properties    */
specifier|protected
name|AvaticaConnection
parameter_list|(
name|UnregisteredDriver
name|driver
parameter_list|,
name|AvaticaFactory
name|factory
parameter_list|,
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|handle
operator|=
operator|new
name|Meta
operator|.
name|ConnectionHandle
argument_list|(
name|this
operator|.
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|driver
operator|=
name|driver
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|meta
operator|=
name|driver
operator|.
name|createMeta
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|factory
operator|.
name|newDatabaseMetaData
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|holdability
operator|=
name|metaData
operator|.
name|getResultSetHoldability
argument_list|()
expr_stmt|;
block|}
comment|/** Returns a view onto this connection's configuration properties. Code    * in Avatica and derived projects should use this view rather than calling    * {@link java.util.Properties#getProperty(String)}. Derived projects will    * almost certainly subclass {@link ConnectionConfig} with their own    * properties. */
specifier|public
name|ConnectionConfig
name|config
parameter_list|()
block|{
return|return
operator|new
name|ConnectionConfigImpl
argument_list|(
name|info
argument_list|)
return|;
block|}
comment|// Connection methods
specifier|public
name|AvaticaStatement
name|createStatement
parameter_list|()
throws|throws
name|SQLException
block|{
comment|//noinspection MagicConstant
return|return
name|createStatement
argument_list|(
name|ResultSet
operator|.
name|TYPE_FORWARD_ONLY
argument_list|,
name|ResultSet
operator|.
name|CONCUR_READ_ONLY
argument_list|,
name|holdability
argument_list|)
return|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
comment|//noinspection MagicConstant
return|return
name|prepareStatement
argument_list|(
name|sql
argument_list|,
name|ResultSet
operator|.
name|TYPE_FORWARD_ONLY
argument_list|,
name|ResultSet
operator|.
name|CONCUR_READ_ONLY
argument_list|,
name|holdability
argument_list|)
return|;
block|}
specifier|public
name|CallableStatement
name|prepareCall
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|String
name|nativeSQL
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setAutoCommit
parameter_list|(
name|boolean
name|autoCommit
parameter_list|)
throws|throws
name|SQLException
block|{
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
operator|.
name|setAutoCommit
argument_list|(
name|autoCommit
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|getAutoCommit
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
argument_list|)
operator|.
name|isAutoCommit
argument_list|()
return|;
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
comment|// Per specification, if onConnectionClose throws, this method will throw
comment|// a SQLException, but statement will still be closed.
try|try
block|{
name|meta
operator|.
name|closeConnection
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|driver
operator|.
name|handler
operator|.
name|onConnectionClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"While closing connection"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|closed
return|;
block|}
specifier|public
name|DatabaseMetaData
name|getMetaData
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|metaData
return|;
block|}
specifier|public
name|void
name|setReadOnly
parameter_list|(
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|SQLException
block|{
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
operator|.
name|setReadOnly
argument_list|(
name|readOnly
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
argument_list|)
operator|.
name|isReadOnly
argument_list|()
return|;
block|}
specifier|public
name|void
name|setCatalog
parameter_list|(
name|String
name|catalog
parameter_list|)
throws|throws
name|SQLException
block|{
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
operator|.
name|setCatalog
argument_list|(
name|catalog
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getCatalog
parameter_list|()
block|{
return|return
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
argument_list|)
operator|.
name|getCatalog
argument_list|()
return|;
block|}
specifier|public
name|void
name|setTransactionIsolation
parameter_list|(
name|int
name|level
parameter_list|)
throws|throws
name|SQLException
block|{
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
operator|.
name|setTransactionIsolation
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getTransactionIsolation
parameter_list|()
throws|throws
name|SQLException
block|{
comment|//noinspection MagicConstant
return|return
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
argument_list|)
operator|.
name|getTransactionIsolation
argument_list|()
return|;
block|}
specifier|public
name|SQLWarning
name|getWarnings
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// no-op since connection pooling often calls this.
block|}
specifier|public
name|Statement
name|createStatement
parameter_list|(
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|)
throws|throws
name|SQLException
block|{
comment|//noinspection MagicConstant
return|return
name|createStatement
argument_list|(
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|holdability
argument_list|)
return|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|)
throws|throws
name|SQLException
block|{
comment|//noinspection MagicConstant
return|return
name|prepareStatement
argument_list|(
name|sql
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|holdability
argument_list|)
return|;
block|}
specifier|public
name|CallableStatement
name|prepareCall
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTypeMap
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setTypeMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setHoldability
parameter_list|(
name|int
name|holdability
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
operator|!
operator|(
name|holdability
operator|==
name|ResultSet
operator|.
name|CLOSE_CURSORS_AT_COMMIT
operator|||
name|holdability
operator|==
name|ResultSet
operator|.
name|HOLD_CURSORS_OVER_COMMIT
operator|)
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"invalid value"
argument_list|)
throw|;
block|}
name|this
operator|.
name|holdability
operator|=
name|holdability
expr_stmt|;
block|}
specifier|public
name|int
name|getHoldability
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|holdability
return|;
block|}
specifier|public
name|Savepoint
name|setSavepoint
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Savepoint
name|setSavepoint
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|rollback
parameter_list|(
name|Savepoint
name|savepoint
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|releaseSavepoint
parameter_list|(
name|Savepoint
name|savepoint
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|AvaticaStatement
name|createStatement
parameter_list|(
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|factory
operator|.
name|newStatement
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
return|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
block|{
specifier|final
name|Meta
operator|.
name|ConnectionHandle
name|ch
init|=
operator|new
name|Meta
operator|.
name|ConnectionHandle
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|Meta
operator|.
name|StatementHandle
name|h
init|=
name|meta
operator|.
name|prepare
argument_list|(
name|ch
argument_list|,
name|sql
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|newPreparedStatement
argument_list|(
name|this
argument_list|,
name|h
argument_list|,
name|h
operator|.
name|signature
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"while preparing SQL: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CallableStatement
name|prepareCall
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
index|[]
name|columnIndexes
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
index|[]
name|columnNames
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Clob
name|createClob
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Blob
name|createBlob
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|NClob
name|createNClob
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|SQLXML
name|createSQLXML
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|(
name|int
name|timeout
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setClientInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SQLClientInfoException
block|{
throw|throw
name|helper
operator|.
name|clientInfo
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setClientInfo
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|SQLClientInfoException
block|{
throw|throw
name|helper
operator|.
name|clientInfo
argument_list|()
throw|;
block|}
specifier|public
name|String
name|getClientInfo
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Properties
name|getClientInfo
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Array
name|createArrayOf
parameter_list|(
name|String
name|typeName
parameter_list|,
name|Object
index|[]
name|elements
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|Struct
name|createStruct
parameter_list|(
name|String
name|typeName
parameter_list|,
name|Object
index|[]
name|attributes
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setSchema
parameter_list|(
name|String
name|schema
parameter_list|)
throws|throws
name|SQLException
block|{
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
operator|.
name|setSchema
argument_list|(
name|schema
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getSchema
parameter_list|()
block|{
return|return
name|meta
operator|.
name|connectionSync
argument_list|(
name|handle
argument_list|,
operator|new
name|ConnectionPropertiesImpl
argument_list|()
argument_list|)
operator|.
name|getSchema
argument_list|()
return|;
block|}
specifier|public
name|void
name|abort
parameter_list|(
name|Executor
name|executor
parameter_list|)
throws|throws
name|SQLException
block|{
throw|throw
name|helper
operator|.
name|unsupported
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setNetworkTimeout
parameter_list|(
name|Executor
name|executor
parameter_list|,
name|int
name|milliseconds
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|networkTimeout
operator|=
name|milliseconds
expr_stmt|;
block|}
specifier|public
name|int
name|getNetworkTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|networkTimeout
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|iface
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|iface
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"does not implement '"
operator|+
name|iface
operator|+
literal|"'"
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|iface
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Returns the time zone of this connection. Determines the offset applied    * when converting datetime values from the database into    * {@link java.sql.Timestamp} values. */
specifier|public
name|TimeZone
name|getTimeZone
parameter_list|()
block|{
specifier|final
name|String
name|timeZoneName
init|=
name|config
argument_list|()
operator|.
name|timeZone
argument_list|()
decl_stmt|;
return|return
name|timeZoneName
operator|==
literal|null
condition|?
name|TimeZone
operator|.
name|getDefault
argument_list|()
else|:
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|timeZoneName
argument_list|)
return|;
block|}
comment|/**    * Executes a prepared query, closing any previously open result set.    *    * @param statement     Statement    * @param signature     Prepared query    * @param firstFrame    First frame of rows, or null if we need to execute    * @return Result set    * @throws java.sql.SQLException if a database error occurs    */
specifier|protected
name|ResultSet
name|executeQueryInternal
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|Meta
operator|.
name|Signature
name|signature
parameter_list|,
name|Meta
operator|.
name|Frame
name|firstFrame
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// Close the previous open result set, if there is one.
synchronized|synchronized
init|(
name|statement
init|)
block|{
if|if
condition|(
name|statement
operator|.
name|openResultSet
operator|!=
literal|null
condition|)
block|{
specifier|final
name|AvaticaResultSet
name|rs
init|=
name|statement
operator|.
name|openResultSet
decl_stmt|;
name|statement
operator|.
name|openResultSet
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"Error while closing previous result set"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|final
name|TimeZone
name|timeZone
init|=
name|getTimeZone
argument_list|()
decl_stmt|;
name|statement
operator|.
name|openResultSet
operator|=
name|factory
operator|.
name|newResultSet
argument_list|(
name|statement
argument_list|,
name|signature
argument_list|,
name|timeZone
argument_list|,
name|firstFrame
argument_list|)
expr_stmt|;
block|}
comment|// Release the monitor before executing, to give another thread the
comment|// opportunity to call cancel.
try|try
block|{
name|statement
operator|.
name|openResultSet
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"exception while executing query: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|statement
operator|.
name|openResultSet
return|;
block|}
specifier|protected
name|Meta
operator|.
name|ExecuteResult
name|prepareAndExecuteInternal
parameter_list|(
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
name|SQLException
block|{
specifier|final
name|Meta
operator|.
name|PrepareCallback
name|callback
init|=
operator|new
name|Meta
operator|.
name|PrepareCallback
argument_list|()
block|{
specifier|public
name|Object
name|getMonitor
parameter_list|()
block|{
return|return
name|statement
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|statement
operator|.
name|openResultSet
operator|!=
literal|null
condition|)
block|{
specifier|final
name|AvaticaResultSet
name|rs
init|=
name|statement
operator|.
name|openResultSet
decl_stmt|;
name|statement
operator|.
name|openResultSet
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|helper
operator|.
name|createException
argument_list|(
literal|"Error while closing previous result set"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|assign
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|,
name|Meta
operator|.
name|Frame
name|firstFrame
parameter_list|,
name|int
name|updateCount
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|updateCount
operator|!=
operator|-
literal|1
condition|)
block|{
name|statement
operator|.
name|updateCount
operator|=
name|updateCount
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TimeZone
name|timeZone
init|=
name|getTimeZone
argument_list|()
decl_stmt|;
name|statement
operator|.
name|openResultSet
operator|=
name|factory
operator|.
name|newResultSet
argument_list|(
name|statement
argument_list|,
name|signature
argument_list|,
name|timeZone
argument_list|,
name|firstFrame
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|statement
operator|.
name|openResultSet
operator|!=
literal|null
condition|)
block|{
name|statement
operator|.
name|openResultSet
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|meta
operator|.
name|prepareAndExecute
argument_list|(
name|statement
operator|.
name|handle
argument_list|,
name|sql
argument_list|,
name|maxRowCount
argument_list|,
name|callback
argument_list|)
return|;
block|}
specifier|protected
name|ResultSet
name|createResultSet
parameter_list|(
name|Meta
operator|.
name|MetaResultSet
name|metaResultSet
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|Meta
operator|.
name|StatementHandle
name|h
init|=
operator|new
name|Meta
operator|.
name|StatementHandle
argument_list|(
name|metaResultSet
operator|.
name|connectionId
argument_list|,
name|metaResultSet
operator|.
name|statementId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|AvaticaStatement
name|statement
init|=
name|lookupStatement
argument_list|(
name|h
argument_list|)
decl_stmt|;
return|return
name|executeQueryInternal
argument_list|(
name|statement
argument_list|,
name|metaResultSet
operator|.
name|signature
operator|.
name|sanitize
argument_list|()
argument_list|,
name|metaResultSet
operator|.
name|firstFrame
argument_list|)
return|;
block|}
comment|/** Creates a statement wrapper around an existing handle. */
specifier|protected
name|AvaticaStatement
name|lookupStatement
parameter_list|(
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|AvaticaStatement
name|statement
init|=
name|statementMap
operator|.
name|get
argument_list|(
name|h
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
return|return
name|statement
return|;
block|}
comment|//noinspection MagicConstant
return|return
name|factory
operator|.
name|newStatement
argument_list|(
name|this
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|h
argument_list|)
argument_list|,
name|ResultSet
operator|.
name|TYPE_FORWARD_ONLY
argument_list|,
name|ResultSet
operator|.
name|CONCUR_READ_ONLY
argument_list|,
name|holdability
argument_list|)
return|;
block|}
comment|// do not make public
specifier|protected
specifier|static
name|Trojan
name|createTrojan
parameter_list|()
block|{
return|return
operator|new
name|Trojan
argument_list|()
return|;
block|}
comment|/** A way to call package-protected methods. But only a sub-class of    * connection can create one. */
specifier|public
specifier|static
class|class
name|Trojan
block|{
comment|// must be private
specifier|private
name|Trojan
parameter_list|()
block|{
block|}
comment|/** A means for anyone who has a trojan to call the protected method      * {@link org.apache.calcite.avatica.AvaticaResultSet#execute()}.      * @throws SQLException if execute fails for some reason. */
specifier|public
name|ResultSet
name|execute
parameter_list|(
name|AvaticaResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|resultSet
operator|.
name|execute
argument_list|()
return|;
block|}
comment|/** A means for anyone who has a trojan to call the protected method      * {@link org.apache.calcite.avatica.AvaticaStatement#getParameterValues()}.      */
specifier|public
name|List
argument_list|<
name|TypedValue
argument_list|>
name|getParameterValues
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|)
block|{
return|return
name|statement
operator|.
name|getParameterValues
argument_list|()
return|;
block|}
comment|/** A means for anyone who has a trojan to get the protected field      * {@link org.apache.calcite.avatica.AvaticaConnection#meta}. */
specifier|public
name|Meta
name|getMeta
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
return|return
name|connection
operator|.
name|meta
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AvaticaConnection.java
end_comment

end_unit

