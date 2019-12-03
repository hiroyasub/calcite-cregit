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
name|avatica
operator|.
name|AvaticaResultSet
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
name|ColumnMetaData
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
name|Handler
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
name|util
operator|.
name|Cursor
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|Linq4j
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
name|runtime
operator|.
name|ArrayEnumeratorCursor
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
name|runtime
operator|.
name|ObjectEnumeratorCursor
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
name|ResultSet
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
name|SQLException
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link ResultSet}  * for the Calcite engine.  */
end_comment

begin_class
specifier|public
class|class
name|CalciteResultSet
extends|extends
name|AvaticaResultSet
block|{
comment|/** Creates a CalciteResultSet. */
name|CalciteResultSet
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|CalcitePrepare
operator|.
name|CalciteSignature
name|calciteSignature
parameter_list|,
name|ResultSetMetaData
name|resultSetMetaData
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|Meta
operator|.
name|Frame
name|firstFrame
parameter_list|)
throws|throws
name|SQLException
block|{
name|super
argument_list|(
name|statement
argument_list|,
literal|null
argument_list|,
name|calciteSignature
argument_list|,
name|resultSetMetaData
argument_list|,
name|timeZone
argument_list|,
name|firstFrame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|CalciteResultSet
name|execute
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Call driver's callback. It is permitted to throw a RuntimeException.
name|CalciteConnectionImpl
name|connection
init|=
name|getCalciteConnection
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
parameter_list|()
lambda|->
block|{
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
argument_list|<
name|Object
argument_list|>
name|iterable
parameter_list|)
throws|throws
name|SQLException
block|{
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
specifier|final
name|CalcitePrepare
operator|.
name|CalciteSignature
name|signature
init|=
operator|(
name|CalcitePrepare
operator|.
name|CalciteSignature
operator|)
name|this
operator|.
name|signature
decl_stmt|;
specifier|final
name|CalcitePrepare
operator|.
name|CalciteSignature
argument_list|<
name|Object
argument_list|>
name|newSignature
init|=
operator|new
name|CalcitePrepare
operator|.
name|CalciteSignature
argument_list|<>
argument_list|(
name|signature
operator|.
name|sql
argument_list|,
name|signature
operator|.
name|parameters
argument_list|,
name|signature
operator|.
name|internalParameters
argument_list|,
name|signature
operator|.
name|rowType
argument_list|,
name|columnMetaDataList
argument_list|,
name|Meta
operator|.
name|CursorFactory
operator|.
name|ARRAY
argument_list|,
name|signature
operator|.
name|rootSchema
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
name|statement
operator|.
name|getStatementType
argument_list|()
argument_list|)
decl_stmt|;
name|ResultSetMetaData
name|subResultSetMetaData
init|=
operator|new
name|AvaticaResultSetMetaData
argument_list|(
name|statement
argument_list|,
literal|null
argument_list|,
name|newSignature
argument_list|)
decl_stmt|;
specifier|final
name|CalciteResultSet
name|resultSet
init|=
operator|new
name|CalciteResultSet
argument_list|(
name|statement
argument_list|,
name|signature
argument_list|,
name|subResultSetMetaData
argument_list|,
name|localCalendar
operator|.
name|getTimeZone
argument_list|()
argument_list|,
operator|new
name|Meta
operator|.
name|Frame
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|iterable
argument_list|)
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
parameter_list|<
name|T
parameter_list|>
name|CalcitePrepare
operator|.
name|CalciteSignature
argument_list|<
name|T
argument_list|>
name|getSignature
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|CalcitePrepare
operator|.
name|CalciteSignature
operator|)
name|signature
return|;
block|}
comment|// do not make public
name|CalciteConnectionImpl
name|getCalciteConnection
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
operator|(
name|CalciteConnectionImpl
operator|)
name|statement
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
end_class

end_unit

