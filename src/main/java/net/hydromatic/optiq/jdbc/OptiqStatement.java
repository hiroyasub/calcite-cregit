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
name|Enumerator
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
name|Queryable
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
name|runtime
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
name|server
operator|.
name|OptiqServerStatement
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

begin_comment
comment|/**  * Implementation of {@link java.sql.Statement}  * for the Optiq engine.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OptiqStatement
implements|implements
name|Statement
implements|,
name|OptiqServerStatement
block|{
specifier|final
name|OptiqConnectionImpl
name|connection
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|/**    * Support for {@link #closeOnCompletion()} method.    */
specifier|protected
name|boolean
name|closeOnCompletion
decl_stmt|;
comment|/**    * Current result set, or null if the statement is not executing anything.    * Any method which modifies this member must synchronize    * on the OptiqStatement.    */
name|OptiqResultSet
name|openResultSet
decl_stmt|;
specifier|private
name|int
name|queryTimeoutMillis
decl_stmt|;
specifier|final
name|int
name|resultSetType
decl_stmt|;
specifier|final
name|int
name|resultSetConcurrency
decl_stmt|;
specifier|final
name|int
name|resultSetHoldability
decl_stmt|;
specifier|private
name|int
name|fetchSize
decl_stmt|;
specifier|private
name|int
name|fetchDirection
decl_stmt|;
specifier|private
name|int
name|maxRowCount
decl_stmt|;
name|OptiqStatement
parameter_list|(
name|OptiqConnectionImpl
name|connection
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
block|{
assert|assert
name|connection
operator|!=
literal|null
assert|;
name|this
operator|.
name|resultSetType
operator|=
name|resultSetType
expr_stmt|;
name|this
operator|.
name|resultSetConcurrency
operator|=
name|resultSetConcurrency
expr_stmt|;
name|this
operator|.
name|resultSetHoldability
operator|=
name|resultSetHoldability
expr_stmt|;
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
block|}
comment|// implement Statement
specifier|public
name|ResultSet
name|executeQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|OptiqPrepare
operator|.
name|PrepareResult
name|x
init|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|executeQueryInternal
argument_list|(
name|x
argument_list|)
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|ResultSet
name|resultSet
init|=
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"expected one result column"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"expected one row, got zero"
argument_list|)
throw|;
block|}
name|int
name|result
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"expected one row, got two or more"
argument_list|)
throw|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
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
name|connection
operator|.
name|server
operator|.
name|removeStatement
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|openResultSet
operator|!=
literal|null
condition|)
block|{
name|OptiqResultSet
name|c
init|=
name|openResultSet
decl_stmt|;
name|openResultSet
operator|=
literal|null
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|int
name|getMaxFieldSize
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
name|setMaxFieldSize
parameter_list|(
name|int
name|max
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
name|int
name|getMaxRows
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|maxRowCount
return|;
block|}
specifier|public
name|void
name|setMaxRows
parameter_list|(
name|int
name|maxRowCount
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|maxRowCount
operator|<
literal|0
condition|)
block|{
throw|throw
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"illegal maxRows value: "
operator|+
name|maxRowCount
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxRowCount
operator|=
name|maxRowCount
expr_stmt|;
block|}
specifier|public
name|void
name|setEscapeProcessing
parameter_list|(
name|boolean
name|enable
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
name|int
name|getQueryTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
name|long
name|timeoutSeconds
init|=
name|getQueryTimeoutMillis
argument_list|()
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|timeoutSeconds
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
if|if
condition|(
name|timeoutSeconds
operator|==
literal|0
operator|&&
name|getQueryTimeoutMillis
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Don't return timeout=0 if e.g. timeoutMillis=500. 0 is special.
return|return
literal|1
return|;
block|}
return|return
operator|(
name|int
operator|)
name|timeoutSeconds
return|;
block|}
name|int
name|getQueryTimeoutMillis
parameter_list|()
block|{
return|return
name|queryTimeoutMillis
return|;
block|}
specifier|public
name|void
name|setQueryTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|seconds
operator|<
literal|0
condition|)
block|{
throw|throw
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"illegal timeout value "
operator|+
name|seconds
argument_list|)
throw|;
block|}
name|setQueryTimeoutMillis
argument_list|(
name|seconds
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
name|void
name|setQueryTimeoutMillis
parameter_list|(
name|int
name|millis
parameter_list|)
block|{
name|this
operator|.
name|queryTimeoutMillis
operator|=
name|millis
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|cancel
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|openResultSet
operator|!=
literal|null
condition|)
block|{
name|openResultSet
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|setCursorName
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
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|OptiqPrepare
operator|.
name|PrepareResult
name|x
init|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|executeInternal
argument_list|(
name|x
argument_list|)
return|;
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// NOTE: result set becomes visible in this member while
comment|// executeQueryInternal is still in progress, and before it has
comment|// finished executing. Its internal state may not be ready for API
comment|// calls. JDBC never claims to be thread-safe! (Except for calls to the
comment|// cancel method.) It is not possible to synchronize, because it would
comment|// block 'cancel'.
return|return
name|openResultSet
return|;
block|}
specifier|public
name|int
name|getUpdateCount
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
name|getMoreResults
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
name|setFetchDirection
parameter_list|(
name|int
name|direction
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|fetchDirection
operator|=
name|direction
expr_stmt|;
block|}
specifier|public
name|int
name|getFetchDirection
parameter_list|()
block|{
return|return
name|fetchDirection
return|;
block|}
specifier|public
name|void
name|setFetchSize
parameter_list|(
name|int
name|rows
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|fetchSize
operator|=
name|rows
expr_stmt|;
block|}
specifier|public
name|int
name|getFetchSize
parameter_list|()
block|{
return|return
name|fetchSize
return|;
block|}
specifier|public
name|int
name|getResultSetConcurrency
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
name|int
name|getResultSetType
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
name|addBatch
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
name|clearBatch
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
name|int
index|[]
name|executeBatch
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
name|OptiqConnectionImpl
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|boolean
name|getMoreResults
parameter_list|(
name|int
name|current
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
name|ResultSet
name|getGeneratedKeys
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
name|int
name|executeUpdate
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
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|columnIndexes
index|[]
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
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|columnNames
index|[]
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
name|boolean
name|execute
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
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|columnIndexes
index|[]
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
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|columnNames
index|[]
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
name|int
name|getResultSetHoldability
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
name|void
name|setPoolable
parameter_list|(
name|boolean
name|poolable
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
name|boolean
name|isPoolable
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
comment|// implement Wrapper
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
name|connection
operator|.
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
comment|/**    * Executes a parsed statement.    *    * @param query Parsed statement    * @return as specified by {@link Statement#execute(String)}    * @throws SQLException if a database error occurs    */
specifier|protected
name|boolean
name|executeInternal
parameter_list|(
name|OptiqPrepare
operator|.
name|PrepareResult
name|query
parameter_list|)
throws|throws
name|SQLException
block|{
name|ResultSet
name|resultSet
init|=
name|executeQueryInternal
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Executes a parsed query, closing any previously open result set.    *    * @param query Parsed query    * @return Result set    * @throws SQLException if a database error occurs    */
specifier|protected
name|ResultSet
name|executeQueryInternal
parameter_list|(
name|OptiqPrepare
operator|.
name|PrepareResult
name|query
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// Close the previous open CellSet, if there is one.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|openResultSet
operator|!=
literal|null
condition|)
block|{
specifier|final
name|OptiqResultSet
name|cs
init|=
name|openResultSet
decl_stmt|;
name|openResultSet
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|cs
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
name|connection
operator|.
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
name|openResultSet
operator|=
name|connection
operator|.
name|factory
operator|.
name|newResultSet
argument_list|(
name|this
argument_list|,
name|query
operator|.
name|columnList
argument_list|,
name|getCursorFactory
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Release the monitor before executing, to give another thread the
comment|// opportunity to call cancel.
try|try
block|{
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
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"exception while executing query"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|openResultSet
return|;
block|}
specifier|private
specifier|static
name|Function0
argument_list|<
name|Cursor
argument_list|>
name|getCursorFactory
parameter_list|(
specifier|final
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepareResult
parameter_list|)
block|{
return|return
operator|new
name|Function0
argument_list|<
name|Cursor
argument_list|>
argument_list|()
block|{
specifier|public
name|Cursor
name|apply
parameter_list|()
block|{
name|Enumerator
argument_list|<
name|?
argument_list|>
name|enumerator
init|=
name|prepareResult
operator|.
name|execute
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
return|return
name|prepareResult
operator|.
name|columnList
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|ObjectEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|)
else|:
name|prepareResult
operator|.
name|resultClazz
operator|!=
literal|null
operator|&&
operator|!
name|prepareResult
operator|.
name|resultClazz
operator|.
name|isArray
argument_list|()
condition|?
operator|new
name|RecordEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|,
name|prepareResult
operator|.
name|resultClazz
argument_list|)
else|:
operator|new
name|ArrayEnumeratorCursor
argument_list|(
operator|(
name|Enumerator
operator|)
name|enumerator
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Called by each child result set when it is closed.    *    * @param resultSet Result set or cell set    */
name|void
name|onResultSetClose
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
if|if
condition|(
name|closeOnCompletion
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|OptiqPrepare
operator|.
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|OptiqPrepare
name|prepare
init|=
name|connection
operator|.
name|prepareFactory
operator|.
name|apply
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareSql
argument_list|(
operator|new
name|ContextImpl
argument_list|(
name|connection
argument_list|)
argument_list|,
name|sql
argument_list|,
literal|null
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|maxRowCount
operator|<=
literal|0
condition|?
operator|-
literal|1
else|:
name|maxRowCount
argument_list|)
return|;
block|}
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepare
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
specifier|final
name|OptiqPrepare
name|prepare
init|=
name|connection
operator|.
name|prepareFactory
operator|.
name|apply
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareQueryable
argument_list|(
operator|new
name|ContextImpl
argument_list|(
name|connection
argument_list|)
argument_list|,
name|queryable
argument_list|)
return|;
block|}
specifier|private
class|class
name|ContextImpl
implements|implements
name|OptiqPrepare
operator|.
name|Context
block|{
specifier|private
specifier|final
name|OptiqConnectionImpl
name|connection
decl_stmt|;
specifier|public
name|ContextImpl
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|connection
operator|.
name|typeFactory
return|;
block|}
specifier|public
name|Schema
name|getRootSchema
parameter_list|()
block|{
return|return
name|connection
operator|.
name|getRootSchema
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultSchemaPath
parameter_list|()
block|{
specifier|final
name|String
name|schemaName
init|=
name|connection
operator|.
name|getSchema
argument_list|()
decl_stmt|;
return|return
name|schemaName
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|singletonList
argument_list|(
name|schemaName
argument_list|)
return|;
block|}
specifier|public
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
name|connection
operator|.
name|getProperties
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End OptiqStatement.java
end_comment

end_unit

