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
name|optiq
operator|.
name|DataContext
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
name|MutableSchema
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
name|Schema
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
name|runtime
operator|.
name|ByteString
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|BasicSqlType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeFactoryImpl
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
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
specifier|static
specifier|final
name|String
name|CONNECT_STRING_PREFIX
init|=
literal|"jdbc:optiq:"
decl_stmt|;
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
init|=
operator|new
name|MapSchema
argument_list|(
name|this
argument_list|,
name|typeFactory
argument_list|,
name|rootExpression
argument_list|)
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
name|OptiqServer
argument_list|()
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
decl_stmt|;
comment|/**      * Creates an OptiqConnectionImpl.      *      *<p>Not public; method is called only from the driver.</p>      *      * @param driver Driver      * @param factory Factory for JDBC objects      * @param url Server URL      * @param info Other connection properties      */
name|OptiqConnectionImpl
parameter_list|(
name|UnregisteredDriver
name|driver
parameter_list|,
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
name|ObjectAbstractQueryable2
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
name|ObjectAbstractQueryable2
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
operator|)
name|enumerable
operator|.
name|execute
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
name|closed
operator|=
literal|true
expr_stmt|;
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
throws|throws
name|SQLException
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|clearWarnings
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
throws|throws
name|SQLException
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
name|boolean
name|acceptsURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
return|return
name|url
operator|.
name|startsWith
argument_list|(
name|CONNECT_STRING_PREFIX
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|JavaTypeFactoryImpl
extends|extends
name|SqlTypeFactoryImpl
implements|implements
name|JavaTypeFactory
block|{
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|type
operator|.
name|getFields
argument_list|()
control|)
block|{
comment|// FIXME: watch out for recursion
name|list
operator|.
name|add
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|createType
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|canonize
argument_list|(
operator|new
name|JavaRecordType
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|RelDataTypeField
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|RelDataType
condition|)
block|{
return|return
operator|(
name|RelDataType
operator|)
name|type
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|Class
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO: implement "
operator|+
name|type
operator|+
literal|": "
operator|+
name|type
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|type
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
return|return
name|createJavaType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
if|else if
condition|(
name|clazz
operator|==
name|String
operator|.
name|class
condition|)
block|{
comment|// TODO: similar special treatment for BigDecimal, BigInteger,
comment|//  Date, Time, Timestamp, Double etc.
return|return
name|createJavaType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
if|else if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|createMultisetType
argument_list|(
name|createType
argument_list|(
name|clazz
operator|.
name|getComponentType
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createStructType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
specifier|public
name|Type
name|getJavaClass
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|RelRecordType
condition|)
block|{
name|JavaRecordType
name|javaRecordType
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|JavaRecordType
condition|)
block|{
name|javaRecordType
operator|=
operator|(
name|JavaRecordType
operator|)
name|type
expr_stmt|;
return|return
name|javaRecordType
operator|.
name|clazz
return|;
block|}
else|else
block|{
return|return
operator|(
name|RelRecordType
operator|)
name|type
return|;
block|}
block|}
if|if
condition|(
name|type
operator|instanceof
name|JavaType
condition|)
block|{
name|JavaType
name|javaType
init|=
operator|(
name|JavaType
operator|)
name|type
decl_stmt|;
return|return
name|javaType
operator|.
name|getJavaClass
argument_list|()
return|;
block|}
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
operator|&&
name|type
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|getJavaClass
argument_list|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
condition|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|VARCHAR
case|:
case|case
name|CHAR
case|:
return|return
name|String
operator|.
name|class
return|;
case|case
name|INTEGER
case|:
return|return
name|Integer
operator|.
name|class
return|;
case|case
name|BIGINT
case|:
return|return
name|Long
operator|.
name|class
return|;
case|case
name|SMALLINT
case|:
return|return
name|Short
operator|.
name|class
return|;
case|case
name|TINYINT
case|:
return|return
name|Byte
operator|.
name|class
return|;
case|case
name|DECIMAL
case|:
return|return
name|BigDecimal
operator|.
name|class
return|;
case|case
name|BOOLEAN
case|:
return|return
name|Boolean
operator|.
name|class
return|;
case|case
name|BINARY
case|:
case|case
name|VARBINARY
case|:
return|return
name|ByteString
operator|.
name|class
return|;
case|case
name|DATE
case|:
return|return
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
return|;
case|case
name|TIME
case|:
return|return
name|Time
operator|.
name|class
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|Timestamp
operator|.
name|class
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|JavaRecordType
extends|extends
name|RelRecordType
block|{
specifier|private
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|public
name|JavaRecordType
parameter_list|(
name|RelDataTypeField
index|[]
name|fields
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
name|super
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
assert|assert
name|clazz
operator|!=
literal|null
assert|;
block|}
block|}
specifier|static
class|class
name|ObjectAbstractQueryable2
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
name|ObjectAbstractQueryable2
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
block|}
block|}
end_class

begin_comment
comment|// End OptiqConnectionImpl.java
end_comment

end_unit

