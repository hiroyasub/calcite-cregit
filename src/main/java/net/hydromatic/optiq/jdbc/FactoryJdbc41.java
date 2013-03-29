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
name|function
operator|.
name|Function0
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Factory} for JDBC 4.1 (corresponds to JDK 1.7).  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
class|class
name|FactoryJdbc41
implements|implements
name|Factory
block|{
specifier|private
specifier|final
name|int
name|major
decl_stmt|;
specifier|private
specifier|final
name|int
name|minor
decl_stmt|;
comment|/** Creates a JDBC factory. */
specifier|public
name|FactoryJdbc41
parameter_list|()
block|{
name|this
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a JDBC factory with given major/minor version number. */
specifier|protected
name|FactoryJdbc41
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|)
block|{
name|this
operator|.
name|major
operator|=
name|major
expr_stmt|;
name|this
operator|.
name|minor
operator|=
name|minor
expr_stmt|;
block|}
specifier|public
name|int
name|getJdbcMajorVersion
parameter_list|()
block|{
return|return
name|major
return|;
block|}
specifier|public
name|int
name|getJdbcMinorVersion
parameter_list|()
block|{
return|return
name|minor
return|;
block|}
specifier|public
name|OptiqConnectionImpl
name|newConnection
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
return|return
operator|new
name|OptiqConnectionJdbc41
argument_list|(
name|driver
argument_list|,
name|factory
argument_list|,
name|prepareFactory
argument_list|,
name|url
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|public
name|OptiqDatabaseMetaData
name|newDatabaseMetaData
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
return|return
operator|new
name|OptiqDatabaseMetaDataJdbc41
argument_list|(
name|connection
argument_list|)
return|;
block|}
specifier|public
name|OptiqStatement
name|newStatement
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
return|return
operator|new
name|OptiqStatementJdbc41
argument_list|(
name|connection
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
name|OptiqPreparedStatement
name|newPreparedStatement
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|,
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
return|return
operator|new
name|OptiqPreparedStatementJdbc41
argument_list|(
name|connection
argument_list|,
name|sql
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
name|OptiqResultSet
name|newResultSet
parameter_list|(
name|OptiqStatement
name|statement
parameter_list|,
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepareResult
parameter_list|)
block|{
return|return
operator|new
name|OptiqResultSet
argument_list|(
name|statement
argument_list|,
name|prepareResult
argument_list|)
return|;
block|}
specifier|public
name|ResultSetMetaData
name|newResultSetMetaData
parameter_list|(
name|OptiqStatement
name|statement
parameter_list|,
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepareResult
parameter_list|)
block|{
return|return
operator|new
name|OptiqResultSetMetaData
argument_list|(
name|statement
argument_list|,
literal|null
argument_list|,
name|prepareResult
operator|.
name|columnList
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|OptiqConnectionJdbc41
extends|extends
name|OptiqConnectionImpl
block|{
name|OptiqConnectionJdbc41
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
name|super
argument_list|(
name|driver
argument_list|,
name|factory
argument_list|,
name|prepareFactory
argument_list|,
name|url
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OptiqStatementJdbc41
extends|extends
name|OptiqStatement
block|{
specifier|public
name|OptiqStatementJdbc41
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
name|super
argument_list|(
name|connection
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|closeOnCompletion
parameter_list|()
throws|throws
name|SQLException
block|{
name|this
operator|.
name|closeOnCompletion
operator|=
literal|true
expr_stmt|;
block|}
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
block|}
specifier|private
specifier|static
class|class
name|OptiqPreparedStatementJdbc41
extends|extends
name|OptiqPreparedStatement
block|{
name|OptiqPreparedStatementJdbc41
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|,
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
name|super
argument_list|(
name|connection
argument_list|,
name|sql
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRowId
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|RowId
name|x
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setRowId
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNString
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|value
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNCharacterStream
argument_list|(
name|value
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|NClob
name|value
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNClob
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setClob
argument_list|(
name|reader
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setBlob
argument_list|(
name|inputStream
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNClob
argument_list|(
name|reader
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSQLXML
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|SQLXML
name|xmlObject
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setSQLXML
argument_list|(
name|xmlObject
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setAsciiStream
argument_list|(
name|x
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setBinaryStream
argument_list|(
name|x
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setCharacterStream
argument_list|(
name|reader
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setAsciiStream
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setBinaryStream
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setCharacterStream
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|value
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNCharacterStream
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setClob
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setBlob
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNClob
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
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
block|}
specifier|private
specifier|static
class|class
name|OptiqDatabaseMetaDataJdbc41
extends|extends
name|OptiqDatabaseMetaData
block|{
name|OptiqDatabaseMetaDataJdbc41
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FactoryJdbc41.java
end_comment

end_unit

