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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|SQLWarning
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
name|Collections
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

begin_comment
comment|/**  * Implementation of {@link java.sql.Statement}  * for the Avatica engine.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AvaticaStatement
implements|implements
name|Statement
block|{
specifier|public
specifier|final
name|AvaticaConnection
name|connection
decl_stmt|;
comment|/** Statement id; unique within connection. */
specifier|public
specifier|final
name|Meta
operator|.
name|StatementHandle
name|handle
decl_stmt|;
specifier|protected
name|boolean
name|closed
decl_stmt|;
comment|/**    * Support for {@link #closeOnCompletion()} method.    */
specifier|protected
name|boolean
name|closeOnCompletion
decl_stmt|;
comment|/**    * Current result set, or null if the statement is not executing anything.    * Any method which modifies this member must synchronize    * on the AvaticaStatement.    */
specifier|protected
name|AvaticaResultSet
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
specifier|protected
name|int
name|maxRowCount
decl_stmt|;
comment|/**    * Creates an AvaticaStatement.    *    * @param connection Connection    * @param h Statement handle    * @param resultSetType Result set type    * @param resultSetConcurrency Result set concurrency    * @param resultSetHoldability Result set holdability    */
specifier|protected
name|AvaticaStatement
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|Meta
operator|.
name|StatementHandle
name|h
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
name|this
operator|.
name|connection
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|connection
argument_list|)
expr_stmt|;
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
name|closed
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|h
operator|==
literal|null
condition|)
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
name|connection
operator|.
name|id
argument_list|)
decl_stmt|;
name|h
operator|=
name|connection
operator|.
name|meta
operator|.
name|createStatement
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|statementMap
operator|.
name|put
argument_list|(
name|h
operator|.
name|id
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|handle
operator|=
name|h
expr_stmt|;
block|}
comment|/** Returns the identifier of the statement, unique within its connection. */
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|handle
operator|.
name|id
return|;
block|}
comment|// implement Statement
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
try|try
block|{
comment|// In JDBC, maxRowCount = 0 means no limit; in prepare it means LIMIT 0
specifier|final
name|int
name|maxRowCount1
init|=
name|maxRowCount
operator|<=
literal|0
condition|?
operator|-
literal|1
else|:
name|maxRowCount
decl_stmt|;
name|Meta
operator|.
name|Signature
name|x
init|=
name|connection
operator|.
name|meta
operator|.
name|prepare
argument_list|(
name|handle
argument_list|,
name|sql
argument_list|,
name|maxRowCount1
argument_list|)
decl_stmt|;
return|return
name|executeInternal
argument_list|(
name|x
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
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"while executing SQL: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResultSet
name|executeQueryOld
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
block|{
specifier|final
name|int
name|maxRowCount1
init|=
name|maxRowCount
operator|<=
literal|0
condition|?
operator|-
literal|1
else|:
name|maxRowCount
decl_stmt|;
name|Meta
operator|.
name|Signature
name|x
init|=
name|connection
operator|.
name|meta
operator|.
name|prepare
argument_list|(
name|handle
argument_list|,
name|sql
argument_list|,
name|maxRowCount1
argument_list|)
decl_stmt|;
return|return
name|executeQueryInternal
argument_list|(
name|x
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
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"error while executing SQL \""
operator|+
name|sql
operator|+
literal|"\": "
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
block|}
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
try|try
block|{
comment|// In JDBC, maxRowCount = 0 means no limit; in prepare it means LIMIT 0
specifier|final
name|int
name|maxRowCount1
init|=
name|maxRowCount
operator|<=
literal|0
condition|?
operator|-
literal|1
else|:
name|maxRowCount
decl_stmt|;
return|return
name|connection
operator|.
name|prepareAndExecuteInternal
argument_list|(
name|this
argument_list|,
name|sql
argument_list|,
name|maxRowCount1
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
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"error while executing SQL \""
operator|+
name|sql
operator|+
literal|"\": "
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
throws|throws
name|SQLException
block|{
try|try
block|{
name|close_
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
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
literal|"While closing statement"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|close_
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
if|if
condition|(
name|openResultSet
operator|!=
literal|null
condition|)
block|{
name|AvaticaResultSet
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
comment|// If onStatementClose throws, this method will throw an exception (later
comment|// converted to SQLException), but this statement still gets closed.
name|connection
operator|.
name|driver
operator|.
name|handler
operator|.
name|onStatementClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
comment|// no warnings, since warnings are not supported
block|}
specifier|public
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// no-op since warnings are not supported
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
return|return
operator|-
literal|1
return|;
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
name|AvaticaConnection
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
name|int
name|executeUpdate
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
name|boolean
name|execute
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
comment|// implements java.sql.Statement.closeOnCompletion (added in JDK 1.7)
specifier|public
name|void
name|closeOnCompletion
parameter_list|()
throws|throws
name|SQLException
block|{
name|closeOnCompletion
operator|=
literal|true
expr_stmt|;
block|}
comment|// implements java.sql.Statement.isCloseOnCompletion (added in JDK 1.7)
specifier|public
name|boolean
name|isCloseOnCompletion
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|closeOnCompletion
return|;
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
comment|/**    * Executes a prepared statement.    *    * @param signature Parsed statement    *    * @return as specified by {@link java.sql.Statement#execute(String)}    * @throws java.sql.SQLException if a database error occurs    */
specifier|protected
name|boolean
name|executeInternal
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|)
throws|throws
name|SQLException
block|{
name|ResultSet
name|resultSet
init|=
name|executeQueryInternal
argument_list|(
name|signature
argument_list|)
decl_stmt|;
comment|// user may have cancelled the query
if|if
condition|(
name|resultSet
operator|.
name|isClosed
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Executes a prepared query, closing any previously open result set.    *    * @param signature Parsed query    * @return Result set    * @throws java.sql.SQLException if a database error occurs    */
specifier|protected
name|ResultSet
name|executeQueryInternal
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|connection
operator|.
name|executeQueryInternal
argument_list|(
name|this
argument_list|,
name|signature
argument_list|,
literal|null
argument_list|)
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
name|close_
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the list of values of this statement's parameters.    *    *<p>Called at execute time. Not a public API.</p>    *    *<p>The default implementation returns the empty list, because non-prepared    * statements have no parameters.</p>    *    * @see org.apache.calcite.avatica.AvaticaConnection.Trojan#getParameterValues(AvaticaStatement)    */
specifier|protected
name|List
argument_list|<
name|Object
argument_list|>
name|getParameterValues
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaStatement.java
end_comment

end_unit

