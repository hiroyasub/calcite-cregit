begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|avatica
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
name|Linq4j
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
comment|/**  * Implementation of {@link ResultSet}  * for the Optiq engine.  */
end_comment

begin_class
specifier|public
class|class
name|OptiqResultSet
extends|extends
name|AvaticaResultSet
block|{
name|OptiqResultSet
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|OptiqPrepare
operator|.
name|PrepareResult
name|prepareResult
parameter_list|,
name|ResultSetMetaData
name|resultSetMetaData
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
block|{
name|super
argument_list|(
name|statement
argument_list|,
name|prepareResult
argument_list|,
name|resultSetMetaData
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|OptiqResultSet
name|execute
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Call driver's callback. It is permitted to throw a RuntimeException.
name|OptiqConnectionImpl
name|connection
init|=
name|getOptiqConnection
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|autoTemp
init|=
name|connection
operator|.
name|config
argument_list|()
operator|.
name|autoTemp
argument_list|()
decl_stmt|;
name|Handler
operator|.
name|ResultSink
name|resultSink
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|autoTemp
condition|)
block|{
name|resultSink
operator|=
operator|new
name|Handler
operator|.
name|ResultSink
argument_list|()
block|{
specifier|public
name|void
name|toBeCompleted
parameter_list|()
block|{
block|}
block|}
expr_stmt|;
block|}
name|connection
operator|.
name|getDriver
argument_list|()
operator|.
name|handler
operator|.
name|onStatementExecute
argument_list|(
name|statement
argument_list|,
name|resultSink
argument_list|)
expr_stmt|;
name|super
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultSet
name|create
parameter_list|(
name|ColumnMetaData
operator|.
name|AvaticaType
name|elementType
parameter_list|,
name|Iterable
name|iterable
parameter_list|)
block|{
specifier|final
name|OptiqResultSet
name|resultSet
init|=
operator|new
name|OptiqResultSet
argument_list|(
name|statement
argument_list|,
operator|(
name|OptiqPrepare
operator|.
name|PrepareResult
operator|)
name|prepareResult
argument_list|,
name|resultSetMetaData
argument_list|,
name|localCalendar
operator|.
name|getTimeZone
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Cursor
name|cursor
init|=
name|resultSet
operator|.
name|createCursor
argument_list|(
name|elementType
argument_list|,
name|iterable
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnMetaDataList
decl_stmt|;
if|if
condition|(
name|elementType
operator|instanceof
name|ColumnMetaData
operator|.
name|StructType
condition|)
block|{
name|columnMetaDataList
operator|=
operator|(
operator|(
name|ColumnMetaData
operator|.
name|StructType
operator|)
name|elementType
operator|)
operator|.
name|columns
expr_stmt|;
block|}
else|else
block|{
name|columnMetaDataList
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|ColumnMetaData
operator|.
name|dummy
argument_list|(
name|elementType
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resultSet
operator|.
name|execute2
argument_list|(
name|cursor
argument_list|,
name|columnMetaDataList
argument_list|)
return|;
block|}
specifier|private
name|Cursor
name|createCursor
parameter_list|(
name|ColumnMetaData
operator|.
name|AvaticaType
name|elementType
parameter_list|,
name|Iterable
name|iterable
parameter_list|)
block|{
specifier|final
name|Enumerator
name|enumerator
init|=
name|Linq4j
operator|.
name|iterableEnumerator
argument_list|(
name|iterable
argument_list|)
decl_stmt|;
comment|//noinspection unchecked
return|return
operator|!
operator|(
name|elementType
operator|instanceof
name|ColumnMetaData
operator|.
name|StructType
operator|)
operator|||
operator|(
operator|(
name|ColumnMetaData
operator|.
name|StructType
operator|)
name|elementType
operator|)
operator|.
name|columns
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|ObjectEnumeratorCursor
argument_list|(
name|enumerator
argument_list|)
else|:
operator|new
name|ArrayEnumeratorCursor
argument_list|(
name|enumerator
argument_list|)
return|;
block|}
comment|// do not make public
name|OptiqPrepare
operator|.
name|PrepareResult
name|getPrepareResult
parameter_list|()
block|{
return|return
operator|(
name|OptiqPrepare
operator|.
name|PrepareResult
operator|)
name|prepareResult
return|;
block|}
comment|// do not make public
name|OptiqConnectionImpl
name|getOptiqConnection
parameter_list|()
block|{
return|return
operator|(
name|OptiqConnectionImpl
operator|)
name|statement
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End OptiqResultSet.java
end_comment

end_unit

