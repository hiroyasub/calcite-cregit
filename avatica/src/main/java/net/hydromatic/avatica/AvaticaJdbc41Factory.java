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
name|avatica
package|;
end_package

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
name|List
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

begin_comment
comment|/**  * Implementation of {@link AvaticaFactory} for JDBC 4.1 (corresponds to JDK 1.7).  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
class|class
name|AvaticaJdbc41Factory
implements|implements
name|AvaticaFactory
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
name|AvaticaJdbc41Factory
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
name|AvaticaJdbc41Factory
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
name|AvaticaConnection
name|newConnection
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
return|return
operator|new
name|AvaticaJdbc41Connection
argument_list|(
name|driver
argument_list|,
name|factory
argument_list|,
name|url
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|public
name|AvaticaDatabaseMetaData
name|newDatabaseMetaData
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|AvaticaJdbc41DatabaseMetaData
argument_list|(
name|connection
argument_list|)
return|;
block|}
specifier|public
name|AvaticaStatement
name|newStatement
parameter_list|(
name|AvaticaConnection
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
name|AvaticaJdbc41Statement
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
name|AvaticaPreparedStatement
name|newPreparedStatement
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|AvaticaPrepareResult
name|prepareResult
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
name|AvaticaJdbc41PreparedStatement
argument_list|(
name|connection
argument_list|,
name|prepareResult
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
name|AvaticaResultSet
name|newResultSet
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|AvaticaPrepareResult
name|prepareResult
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
block|{
specifier|final
name|ResultSetMetaData
name|metaData
init|=
name|newResultSetMetaData
argument_list|(
name|statement
argument_list|,
name|prepareResult
operator|.
name|getColumnList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|AvaticaResultSet
argument_list|(
name|statement
argument_list|,
name|prepareResult
argument_list|,
name|metaData
argument_list|,
name|timeZone
argument_list|)
return|;
block|}
specifier|public
name|AvaticaResultSetMetaData
name|newResultSetMetaData
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnMetaDataList
parameter_list|)
block|{
return|return
operator|new
name|AvaticaResultSetMetaData
argument_list|(
name|statement
argument_list|,
literal|null
argument_list|,
name|columnMetaDataList
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|AvaticaJdbc41Connection
extends|extends
name|AvaticaConnection
block|{
name|AvaticaJdbc41Connection
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
name|super
argument_list|(
name|driver
argument_list|,
name|factory
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
name|AvaticaJdbc41Statement
extends|extends
name|AvaticaStatement
block|{
specifier|public
name|AvaticaJdbc41Statement
parameter_list|(
name|AvaticaConnection
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
name|AvaticaJdbc41PreparedStatement
extends|extends
name|AvaticaPreparedStatement
block|{
name|AvaticaJdbc41PreparedStatement
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|AvaticaPrepareResult
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
name|AvaticaJdbc41DatabaseMetaData
extends|extends
name|AvaticaDatabaseMetaData
block|{
name|AvaticaJdbc41DatabaseMetaData
parameter_list|(
name|AvaticaConnection
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
comment|// End AvaticaJdbc41Factory.java
end_comment

end_unit

