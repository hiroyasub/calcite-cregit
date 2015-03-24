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
operator|.
name|remote
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
name|AvaticaParameter
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
name|MetaImpl
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
comment|/**  * Implementation of {@link Meta} for the remote driver.  */
end_comment

begin_class
class|class
name|RemoteMeta
extends|extends
name|MetaImpl
block|{
specifier|final
name|Service
name|service
decl_stmt|;
specifier|public
name|RemoteMeta
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|Service
name|service
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
specifier|private
name|MetaResultSet
name|toResultSet
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|Service
operator|.
name|ResultSetResponse
name|response
parameter_list|)
block|{
name|Signature
name|signature0
init|=
name|response
operator|.
name|signature
decl_stmt|;
if|if
condition|(
name|signature0
operator|==
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
init|=
name|clazz
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|ColumnMetaData
operator|>
name|emptyList
argument_list|()
else|:
name|fieldMetaData
argument_list|(
name|clazz
argument_list|)
operator|.
name|columns
decl_stmt|;
name|signature0
operator|=
name|Signature
operator|.
name|create
argument_list|(
name|columns
argument_list|,
literal|"?"
argument_list|,
name|Collections
operator|.
expr|<
name|AvaticaParameter
operator|>
name|emptyList
argument_list|()
argument_list|,
name|CursorFactory
operator|.
name|ARRAY
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MetaResultSet
argument_list|(
name|response
operator|.
name|statementId
argument_list|,
name|response
operator|.
name|ownStatement
argument_list|,
name|signature0
argument_list|,
name|response
operator|.
name|firstFrame
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StatementHandle
name|createStatement
parameter_list|(
name|ConnectionHandle
name|ch
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|CreateStatementResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|CreateStatementRequest
argument_list|(
name|ch
operator|.
name|id
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|StatementHandle
argument_list|(
name|response
operator|.
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|closeStatement
parameter_list|(
name|StatementHandle
name|h
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|CloseStatementResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|CloseStatementRequest
argument_list|(
name|h
operator|.
name|id
argument_list|)
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|getCatalogs
parameter_list|()
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|CatalogsRequest
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|toResultSet
argument_list|(
name|MetaCatalog
operator|.
name|class
argument_list|,
name|response
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|getSchemas
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|SchemasRequest
argument_list|(
name|catalog
argument_list|,
name|schemaPattern
operator|.
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toResultSet
argument_list|(
name|MetaSchema
operator|.
name|class
argument_list|,
name|response
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|getTables
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|typeList
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|TablesRequest
argument_list|(
name|catalog
argument_list|,
name|schemaPattern
operator|.
name|s
argument_list|,
name|tableNamePattern
operator|.
name|s
argument_list|,
name|typeList
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toResultSet
argument_list|(
name|MetaTable
operator|.
name|class
argument_list|,
name|response
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|getTableTypes
parameter_list|()
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|TableTypesRequest
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|toResultSet
argument_list|(
name|MetaTableType
operator|.
name|class
argument_list|,
name|response
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|getColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|ColumnsRequest
argument_list|(
name|catalog
argument_list|,
name|schemaPattern
operator|.
name|s
argument_list|,
name|tableNamePattern
operator|.
name|s
argument_list|,
name|columnNamePattern
operator|.
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toResultSet
argument_list|(
name|MetaColumn
operator|.
name|class
argument_list|,
name|response
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Signature
name|prepare
parameter_list|(
name|StatementHandle
name|h
parameter_list|,
name|String
name|sql
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|PrepareResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|PrepareRequest
argument_list|(
name|h
operator|.
name|id
argument_list|,
name|sql
argument_list|,
name|maxRowCount
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|signature
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetaResultSet
name|prepareAndExecute
parameter_list|(
name|StatementHandle
name|h
parameter_list|,
name|String
name|sql
parameter_list|,
name|int
name|maxRowCount
parameter_list|,
name|PrepareCallback
name|callback
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|ResultSetResponse
name|response
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|callback
operator|.
name|getMonitor
argument_list|()
init|)
block|{
name|callback
operator|.
name|clear
argument_list|()
expr_stmt|;
name|response
operator|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|PrepareAndExecuteRequest
argument_list|(
name|h
operator|.
name|id
argument_list|,
name|sql
argument_list|,
name|maxRowCount
argument_list|)
argument_list|)
expr_stmt|;
name|callback
operator|.
name|assign
argument_list|(
name|response
operator|.
name|signature
argument_list|,
name|response
operator|.
name|firstFrame
argument_list|)
expr_stmt|;
block|}
name|callback
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|toResultSet
argument_list|(
literal|null
argument_list|,
name|response
argument_list|)
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
annotation|@
name|Override
specifier|public
name|Frame
name|fetch
parameter_list|(
name|StatementHandle
name|h
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|parameterValues
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|fetchMaxRowCount
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|FetchResponse
name|response
init|=
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|FetchRequest
argument_list|(
name|h
operator|.
name|id
argument_list|,
name|parameterValues
argument_list|,
name|offset
argument_list|,
name|fetchMaxRowCount
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|frame
return|;
block|}
block|}
end_class

begin_comment
comment|// End RemoteMeta.java
end_comment

end_unit

