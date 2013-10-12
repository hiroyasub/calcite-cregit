begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expressions
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|ParameterExpression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|MapSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|server
operator|.
name|OptiqServer
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|server
operator|.
name|OptiqServerStatement
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
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
name|util
operator|.
name|*
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
comment|/**  * Implementation of JDBC connection  * in the Optiq engine.  *  *<p>Abstract to allow newer versions of JDBC to add methods.</p>  */
end_comment

begin_class
specifier|abstract
class|class
name|OptiqConnectionImpl
implements|implements
name|OptiqConnection
implements|,
name|QueryProvider
block|{
specifier|public
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|autoCommit
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|boolean
name|readOnly
decl_stmt|;
specifier|private
name|int
name|transactionIsolation
decl_stmt|;
specifier|private
name|int
name|holdability
decl_stmt|;
specifier|private
name|int
name|networkTimeout
decl_stmt|;
specifier|private
name|String
name|catalog
decl_stmt|;
specifier|final
name|ParameterExpression
name|rootExpression
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|DataContext
operator|.
name|class
argument_list|,
literal|"root"
argument_list|)
decl_stmt|;
specifier|final
name|MutableSchema
name|rootSchema
decl_stmt|;
specifier|final
name|UnregisteredDriver
name|driver
decl_stmt|;
specifier|final
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|Factory
name|factory
decl_stmt|;
specifier|final
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
name|prepareFactory
decl_stmt|;
specifier|private
specifier|final
name|String
name|url
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|info
decl_stmt|;
specifier|private
name|String
name|schema
decl_stmt|;
specifier|private
specifier|final
name|OptiqDatabaseMetaData
name|metaData
decl_stmt|;
specifier|final
name|Helper
name|helper
init|=
name|Helper
operator|.
name|INSTANCE
decl_stmt|;
specifier|final
name|OptiqServer
name|server
init|=
operator|new
name|OptiqServerImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Schema
name|informationSchema
decl_stmt|;
comment|/**    * Creates an OptiqConnectionImpl.    *    *<p>Not public; method is called only from the driver.</p>    *    * @param driver Driver    * @param factory Factory for JDBC objects    * @param prepareFactory Factory for {@link OptiqPrepare}    * @param url Server URL    * @param info Other connection properties    */
name|OptiqConnectionImpl
parameter_list|(
name|UnregisteredDriver
name|driver
parameter_list|,
name|Factory
name|factory
parameter_list|,
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
name|prepareFactory
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
name|prepareFactory
operator|=
name|prepareFactory
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
name|this
operator|.
name|rootSchema
operator|=
operator|new
name|RootSchema
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|informationSchema
operator|=
name|metaData
operator|.
name|meta
operator|.
name|createInformationSchema
argument_list|()
expr_stmt|;
block|}
comment|/** Returns a view onto this connection's configuration properties. Code    * within Optiq should use this view rather than calling    * {@link Properties#getProperty(String)}. */
name|ConnectionProperty
operator|.
name|ConnectionConfig
name|config
parameter_list|()
block|{
return|return
name|ConnectionProperty
operator|.
name|connectionConfig
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|static
name|ConnectionProperty
operator|.
name|ConnectionConfig
name|configOf
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
block|{
return|return
name|ConnectionProperty
operator|.
name|connectionConfig
argument_list|(
name|connection
operator|.
name|getProperties
argument_list|()
argument_list|)
return|;
block|}
comment|// OptiqConnection methods
specifier|public
name|MutableSchema
name|getRootSchema
parameter_list|()
block|{
return|return
name|rootSchema
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|// QueryProvider methods
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|OptiqQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|rowType
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|rowType
parameter_list|)
block|{
return|return
operator|new
name|OptiqQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|this
argument_list|,
name|rowType
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO:
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// TODO:
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|executeQuery
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
try|try
block|{
name|OptiqStatement
name|statement
init|=
name|createStatement
argument_list|()
decl_stmt|;
name|OptiqPrepare
operator|.
name|PrepareResult
name|enumerable
init|=
name|statement
operator|.
name|prepare
argument_list|(
name|queryable
argument_list|)
decl_stmt|;
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
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
comment|// Connection methods
specifier|public
name|OptiqStatement
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
name|this
operator|.
name|autoCommit
operator|=
name|autoCommit
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
name|autoCommit
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
name|this
operator|.
name|readOnly
operator|=
name|readOnly
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
name|readOnly
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
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
block|}
specifier|public
name|String
name|getCatalog
parameter_list|()
block|{
return|return
name|catalog
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
name|this
operator|.
name|transactionIsolation
operator|=
name|level
expr_stmt|;
block|}
specifier|public
name|int
name|getTransactionIsolation
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|transactionIsolation
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|OptiqStatement
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
name|OptiqStatement
name|statement
init|=
name|factory
operator|.
name|newStatement
argument_list|(
name|this
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
decl_stmt|;
name|server
operator|.
name|addStatement
argument_list|(
name|statement
argument_list|)
expr_stmt|;
return|return
name|statement
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
name|OptiqPreparedStatement
name|statement
init|=
name|factory
operator|.
name|newPreparedStatement
argument_list|(
name|this
argument_list|,
name|sql
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
decl_stmt|;
name|server
operator|.
name|addStatement
argument_list|(
name|statement
argument_list|)
expr_stmt|;
return|return
name|statement
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|createException
argument_list|(
literal|"Error while preparing statement ["
operator|+
name|sql
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|createException
argument_list|(
literal|"Error while preparing statement ["
operator|+
name|sql
operator|+
literal|"]"
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
operator|new
name|UnsupportedOperationException
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
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
specifier|public
name|String
name|getSchema
parameter_list|()
block|{
return|return
name|schema
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
operator|new
name|UnsupportedOperationException
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
specifier|static
class|class
name|OptiqQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|BaseQueryable
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|OptiqQueryable
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|elementType
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OptiqConnection
name|getConnection
parameter_list|()
block|{
return|return
operator|(
name|OptiqConnection
operator|)
name|provider
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OptiqServerImpl
implements|implements
name|OptiqServer
block|{
specifier|final
name|List
argument_list|<
name|OptiqServerStatement
argument_list|>
name|statementList
init|=
operator|new
name|ArrayList
argument_list|<
name|OptiqServerStatement
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|removeStatement
parameter_list|(
name|OptiqServerStatement
name|optiqServerStatement
parameter_list|)
block|{
name|statementList
operator|.
name|add
argument_list|(
name|optiqServerStatement
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addStatement
parameter_list|(
name|OptiqServerStatement
name|statement
parameter_list|)
block|{
name|statementList
operator|.
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|RootSchema
extends|extends
name|MapSchema
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
name|RootSchema
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|connection
argument_list|,
name|connection
operator|.
name|typeFactory
argument_list|,
literal|""
argument_list|,
name|connection
operator|.
name|rootExpression
argument_list|)
expr_stmt|;
comment|// Store the time at which the query started executing. The SQL
comment|// standard says that functions such as CURRENTTIMESTAMP return the
comment|// same value throughout the query.
specifier|final
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|timeZoneName
init|=
name|ConnectionProperty
operator|.
name|TIMEZONE
operator|.
name|getString
argument_list|(
name|connection
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TimeZone
name|timeZone
init|=
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
decl_stmt|;
specifier|final
name|long
name|localOffset
init|=
name|timeZone
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
specifier|final
name|long
name|currentOffset
init|=
name|localOffset
decl_stmt|;
name|map
operator|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"utcTimestamp"
argument_list|,
name|time
argument_list|)
operator|.
name|put
argument_list|(
literal|"currentTimestamp"
argument_list|,
name|time
operator|+
name|currentOffset
argument_list|)
operator|.
name|put
argument_list|(
literal|"localTimestamp"
argument_list|,
name|time
operator|+
name|localOffset
argument_list|)
operator|.
name|put
argument_list|(
literal|"timeZone"
argument_list|,
name|timeZone
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqConnectionImpl.java
end_comment

end_unit

