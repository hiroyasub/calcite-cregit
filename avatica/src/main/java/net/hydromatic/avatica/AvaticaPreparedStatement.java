begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
comment|/**  * Implementation of {@link java.sql.PreparedStatement}  * for the Avatica engine.  *  *<p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;  * it is instantiated using {@link AvaticaFactory#newPreparedStatement}.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AvaticaPreparedStatement
extends|extends
name|AvaticaStatement
implements|implements
name|PreparedStatement
implements|,
name|ParameterMetaData
block|{
specifier|private
specifier|final
name|AvaticaPrepareResult
name|prepareResult
decl_stmt|;
specifier|private
specifier|final
name|ResultSetMetaData
name|resultSetMetaData
decl_stmt|;
comment|/**    * Creates an AvaticaPreparedStatement.    *    * @param connection Connection    * @param prepareResult Result of preparing statement    * @param resultSetType Result set type    * @param resultSetConcurrency Result set concurrency    * @param resultSetHoldability Result set holdability    * @throws SQLException If fails due to underlying implementation reasons.    */
specifier|protected
name|AvaticaPreparedStatement
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
name|this
operator|.
name|prepareResult
operator|=
name|prepareResult
expr_stmt|;
name|this
operator|.
name|resultSetMetaData
operator|=
name|connection
operator|.
name|factory
operator|.
name|newResultSetMetaData
argument_list|(
name|this
argument_list|,
name|prepareResult
operator|.
name|getColumnList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|Object
argument_list|>
name|getParameterValues
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AvaticaParameter
name|parameter
range|:
name|prepareResult
operator|.
name|getParameterList
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|parameter
operator|.
name|value
operator|==
name|AvaticaParameter
operator|.
name|DUMMY_VALUE
condition|?
literal|null
else|:
name|parameter
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|// implement PreparedStatement
specifier|public
name|ResultSet
name|executeQuery
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|executeQueryInternal
argument_list|(
name|this
argument_list|,
name|prepareResult
argument_list|)
return|;
block|}
specifier|public
name|ParameterMetaData
name|getParameterMetaData
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|this
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|()
throws|throws
name|SQLException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO:
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
name|sqlType
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNull
argument_list|(
name|sqlType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBoolean
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|boolean
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
name|setBoolean
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setByte
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|byte
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
name|setByte
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setShort
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|short
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
name|setShort
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
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
name|setInt
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLong
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|long
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
name|setValue
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFloat
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|float
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
name|setFloat
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDouble
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|double
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
name|setDouble
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBigDecimal
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|BigDecimal
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
name|setBigDecimal
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setString
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|String
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
name|setString
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBytes
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|byte
index|[]
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
name|setBytes
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Date
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
name|setDate
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Time
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
name|setTime
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Timestamp
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
name|setTimestamp
argument_list|(
name|x
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
name|int
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
name|setUnicodeStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|int
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
name|setUnicodeStream
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
name|int
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
name|clearParameters
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
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setObject
argument_list|(
name|x
argument_list|,
name|targetSqlType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
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
name|setObject
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|execute
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
name|setCharacterStream
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|int
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
name|setRef
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Ref
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
name|setRef
argument_list|(
name|x
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
name|Blob
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
name|setBlob
argument_list|(
name|x
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
name|Clob
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
name|setClob
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setArray
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Array
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
name|setArray
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ResultSetMetaData
name|getMetaData
parameter_list|()
block|{
return|return
name|resultSetMetaData
return|;
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Date
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setDate
argument_list|(
name|x
argument_list|,
name|cal
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Time
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setTime
argument_list|(
name|x
argument_list|,
name|cal
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Timestamp
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setTimestamp
argument_list|(
name|x
argument_list|,
name|cal
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|int
name|sqlType
parameter_list|,
name|String
name|typeName
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setNull
argument_list|(
name|sqlType
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setURL
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|URL
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
name|setURL
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|int
name|parameterIndex
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|,
name|int
name|scaleOrLength
parameter_list|)
throws|throws
name|SQLException
block|{
name|getParameter
argument_list|(
name|parameterIndex
argument_list|)
operator|.
name|setObject
argument_list|(
name|x
argument_list|,
name|targetSqlType
argument_list|,
name|scaleOrLength
argument_list|)
expr_stmt|;
block|}
comment|// implement ParameterMetaData
specifier|protected
name|AvaticaParameter
name|getParameter
parameter_list|(
name|int
name|param
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
block|{
return|return
name|prepareResult
operator|.
name|getParameterList
argument_list|()
operator|.
name|get
argument_list|(
name|param
operator|-
literal|1
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|//noinspection ThrowableResultOfMethodCallIgnored
throw|throw
name|connection
operator|.
name|helper
operator|.
name|toSQLException
argument_list|(
name|connection
operator|.
name|helper
operator|.
name|createException
argument_list|(
literal|"parameter ordinal "
operator|+
name|param
operator|+
literal|" out of range"
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|getParameterCount
parameter_list|()
block|{
return|return
name|prepareResult
operator|.
name|getParameterList
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|int
name|isNullable
parameter_list|(
name|int
name|param
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|ParameterMetaData
operator|.
name|parameterNullableUnknown
return|;
block|}
specifier|public
name|boolean
name|isSigned
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|signed
return|;
block|}
specifier|public
name|int
name|getPrecision
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|precision
return|;
block|}
specifier|public
name|int
name|getScale
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|scale
return|;
block|}
specifier|public
name|int
name|getParameterType
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|parameterType
return|;
block|}
specifier|public
name|String
name|getParameterTypeName
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|typeName
return|;
block|}
specifier|public
name|String
name|getParameterClassName
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|getParameter
argument_list|(
name|index
argument_list|)
operator|.
name|className
return|;
block|}
specifier|public
name|int
name|getParameterMode
parameter_list|(
name|int
name|param
parameter_list|)
throws|throws
name|SQLException
block|{
comment|//noinspection UnusedDeclaration
name|AvaticaParameter
name|paramDef
init|=
name|getParameter
argument_list|(
name|param
argument_list|)
decl_stmt|;
comment|// forces param range check
return|return
name|ParameterMetaData
operator|.
name|parameterModeIn
return|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaPreparedStatement.java
end_comment

end_unit

