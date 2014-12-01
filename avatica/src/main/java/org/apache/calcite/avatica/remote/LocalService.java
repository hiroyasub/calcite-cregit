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
name|List
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Service} that talks to a local {@link Meta}.  */
end_comment

begin_class
specifier|public
class|class
name|LocalService
implements|implements
name|Service
block|{
specifier|final
name|Meta
name|meta
decl_stmt|;
specifier|public
name|LocalService
parameter_list|(
name|Meta
name|meta
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|iterable
parameter_list|)
block|{
if|if
condition|(
name|iterable
operator|instanceof
name|List
condition|)
block|{
return|return
operator|(
name|List
argument_list|<
name|E
argument_list|>
operator|)
name|iterable
return|;
block|}
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|rowList
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|E
name|row
range|:
name|iterable
control|)
block|{
name|rowList
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
return|return
name|rowList
return|;
block|}
comment|/** Converts a result set (not serializable) into a serializable response. */
specifier|public
name|ResultSetResponse
name|toResponse
parameter_list|(
name|Meta
operator|.
name|MetaResultSet
name|resultSet
parameter_list|)
block|{
name|Meta
operator|.
name|CursorFactory
name|cursorFactory
init|=
name|resultSet
operator|.
name|signature
operator|.
name|cursorFactory
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|iterable
operator|!=
literal|null
condition|)
block|{
name|list
operator|=
name|list
argument_list|(
name|resultSet
operator|.
name|iterable
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|cursorFactory
operator|.
name|style
condition|)
block|{
case|case
name|ARRAY
case|:
name|cursorFactory
operator|=
name|Meta
operator|.
name|CursorFactory
operator|.
name|LIST
expr_stmt|;
break|break;
case|case
name|MAP
case|:
break|break;
default|default:
name|cursorFactory
operator|=
name|Meta
operator|.
name|CursorFactory
operator|.
name|map
argument_list|(
name|cursorFactory
operator|.
name|fieldNames
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//noinspection unchecked
name|list
operator|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
operator|(
name|List
operator|)
name|list2
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
name|cursorFactory
operator|=
name|Meta
operator|.
name|CursorFactory
operator|.
name|LIST
expr_stmt|;
block|}
name|Meta
operator|.
name|Signature
name|signature
init|=
name|resultSet
operator|.
name|signature
decl_stmt|;
if|if
condition|(
name|cursorFactory
operator|!=
name|resultSet
operator|.
name|signature
operator|.
name|cursorFactory
condition|)
block|{
name|signature
operator|=
name|signature
operator|.
name|setCursorFactory
argument_list|(
name|cursorFactory
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ResultSetResponse
argument_list|(
name|resultSet
operator|.
name|statementId
argument_list|,
name|resultSet
operator|.
name|ownStatement
argument_list|,
name|signature
argument_list|,
name|list
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|list2
parameter_list|(
name|Meta
operator|.
name|MetaResultSet
name|resultSet
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
return|return
name|MetaImpl
operator|.
name|collect
argument_list|(
name|resultSet
operator|.
name|signature
operator|.
name|cursorFactory
argument_list|,
name|meta
operator|.
name|createIterable
argument_list|(
operator|new
name|Meta
operator|.
name|StatementHandle
argument_list|(
name|resultSet
operator|.
name|statementId
argument_list|)
argument_list|,
name|resultSet
operator|.
name|signature
argument_list|,
name|resultSet
operator|.
name|iterable
argument_list|)
argument_list|,
name|list
argument_list|)
return|;
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|CatalogsRequest
name|request
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|MetaResultSet
name|resultSet
init|=
name|meta
operator|.
name|getCatalogs
argument_list|()
decl_stmt|;
return|return
name|toResponse
argument_list|(
name|resultSet
argument_list|)
return|;
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|SchemasRequest
name|request
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|MetaResultSet
name|resultSet
init|=
name|meta
operator|.
name|getSchemas
argument_list|(
name|request
operator|.
name|catalog
argument_list|,
name|Meta
operator|.
name|Pat
operator|.
name|of
argument_list|(
name|request
operator|.
name|schemaPattern
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toResponse
argument_list|(
name|resultSet
argument_list|)
return|;
block|}
specifier|public
name|PrepareResponse
name|apply
parameter_list|(
name|PrepareRequest
name|request
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|StatementHandle
name|h
init|=
operator|new
name|Meta
operator|.
name|StatementHandle
argument_list|(
name|request
operator|.
name|statementId
argument_list|)
decl_stmt|;
specifier|final
name|Meta
operator|.
name|Signature
name|signature
init|=
name|meta
operator|.
name|prepare
argument_list|(
name|h
argument_list|,
name|request
operator|.
name|sql
argument_list|,
name|request
operator|.
name|maxRowCount
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrepareResponse
argument_list|(
name|signature
argument_list|)
return|;
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|PrepareAndExecuteRequest
name|request
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|StatementHandle
name|h
init|=
operator|new
name|Meta
operator|.
name|StatementHandle
argument_list|(
name|request
operator|.
name|statementId
argument_list|)
decl_stmt|;
specifier|final
name|Meta
operator|.
name|MetaResultSet
name|resultSet
init|=
name|meta
operator|.
name|prepareAndExecute
argument_list|(
name|h
argument_list|,
name|request
operator|.
name|sql
argument_list|,
name|request
operator|.
name|maxRowCount
argument_list|,
operator|new
name|Meta
operator|.
name|PrepareCallback
argument_list|()
block|{
specifier|public
name|Object
name|getMonitor
parameter_list|()
block|{
return|return
name|LocalService
operator|.
name|class
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
block|}
specifier|public
name|void
name|assign
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|,
name|Iterable
argument_list|<
name|Object
argument_list|>
name|iterable
parameter_list|)
block|{
block|}
specifier|public
name|void
name|execute
parameter_list|()
block|{
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|toResponse
argument_list|(
name|resultSet
argument_list|)
return|;
block|}
specifier|public
name|CreateStatementResponse
name|apply
parameter_list|(
name|CreateStatementRequest
name|request
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|StatementHandle
name|h
init|=
name|meta
operator|.
name|createStatement
argument_list|(
operator|new
name|Meta
operator|.
name|ConnectionHandle
argument_list|(
name|request
operator|.
name|connectionId
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|CreateStatementResponse
argument_list|(
name|h
operator|.
name|id
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LocalService.java
end_comment

end_unit

