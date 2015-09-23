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
name|jdbc
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|AvaticaDatabaseMetaData
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
name|AvaticaFactory
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
name|AvaticaPreparedStatement
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
name|AvaticaResultSetMetaData
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
name|QueryState
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
name|UnregisteredDriver
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
name|NClob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|RowId
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
name|SQLXML
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
comment|/**  * Implementation of {@link org.apache.calcite.avatica.AvaticaFactory}  * for Calcite and JDBC 4.1 (corresponds to JDK 1.7).  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|CalciteJdbc41Factory
extends|extends
name|CalciteFactory
block|{
comment|/** Creates a factory for JDBC version 4.1. */
specifier|public
name|CalciteJdbc41Factory
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
name|CalciteJdbc41Factory
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|)
block|{
name|super
argument_list|(
name|major
argument_list|,
name|minor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CalciteJdbc41Connection
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
parameter_list|,
name|CalciteRootSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|CalciteJdbc41Connection
argument_list|(
operator|(
name|Driver
operator|)
name|driver
argument_list|,
name|factory
argument_list|,
name|url
argument_list|,
name|info
argument_list|,
name|rootSchema
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|CalciteJdbc41DatabaseMetaData
name|newDatabaseMetaData
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|CalciteJdbc41DatabaseMetaData
argument_list|(
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
argument_list|)
return|;
block|}
specifier|public
name|CalciteJdbc41Statement
name|newStatement
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
return|return
operator|new
name|CalciteJdbc41Statement
argument_list|(
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
argument_list|,
name|h
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
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|,
name|Meta
operator|.
name|Signature
name|signature
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
name|CalciteJdbc41PreparedStatement
argument_list|(
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
argument_list|,
name|h
argument_list|,
operator|(
name|CalcitePrepare
operator|.
name|CalciteSignature
operator|)
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
specifier|public
name|CalciteResultSet
name|newResultSet
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|QueryState
name|state
parameter_list|,
name|Meta
operator|.
name|Signature
name|signature
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|Meta
operator|.
name|Frame
name|firstFrame
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
name|signature
argument_list|)
decl_stmt|;
specifier|final
name|CalcitePrepare
operator|.
name|CalciteSignature
name|calciteSignature
init|=
operator|(
name|CalcitePrepare
operator|.
name|CalciteSignature
operator|)
name|signature
decl_stmt|;
return|return
operator|new
name|CalciteResultSet
argument_list|(
name|statement
argument_list|,
name|calciteSignature
argument_list|,
name|metaData
argument_list|,
name|timeZone
argument_list|,
name|firstFrame
argument_list|)
return|;
block|}
specifier|public
name|ResultSetMetaData
name|newResultSetMetaData
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|Meta
operator|.
name|Signature
name|signature
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
name|signature
argument_list|)
return|;
block|}
comment|/** Implementation of connection for JDBC 4.1. */
specifier|private
specifier|static
class|class
name|CalciteJdbc41Connection
extends|extends
name|CalciteConnectionImpl
block|{
name|CalciteJdbc41Connection
parameter_list|(
name|Driver
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
parameter_list|,
name|CalciteRootSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
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
argument_list|,
name|rootSchema
argument_list|,
name|typeFactory
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Implementation of statement for JDBC 4.1. */
specifier|private
specifier|static
class|class
name|CalciteJdbc41Statement
extends|extends
name|CalciteStatement
block|{
specifier|public
name|CalciteJdbc41Statement
parameter_list|(
name|CalciteConnectionImpl
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
name|super
argument_list|(
name|connection
argument_list|,
name|h
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Implementation of prepared statement for JDBC 4.1. */
specifier|private
specifier|static
class|class
name|CalciteJdbc41PreparedStatement
extends|extends
name|CalcitePreparedStatement
block|{
name|CalciteJdbc41PreparedStatement
parameter_list|(
name|CalciteConnectionImpl
name|connection
parameter_list|,
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|,
name|CalcitePrepare
operator|.
name|CalciteSignature
name|signature
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
name|h
argument_list|,
name|signature
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
name|getSite
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
block|}
comment|/** Implementation of database metadata for JDBC 4.1. */
specifier|private
specifier|static
class|class
name|CalciteJdbc41DatabaseMetaData
extends|extends
name|AvaticaDatabaseMetaData
block|{
name|CalciteJdbc41DatabaseMetaData
parameter_list|(
name|CalciteConnectionImpl
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
comment|// End CalciteJdbc41Factory.java
end_comment

end_unit

