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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
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
comment|/**  * Implementation of {@link org.apache.calcite.avatica.remote.Service}  * that encodes requests and responses as JSON.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JsonService
implements|implements
name|Service
block|{
specifier|protected
specifier|static
specifier|final
name|ObjectMapper
name|MAPPER
decl_stmt|;
static|static
block|{
name|MAPPER
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|MAPPER
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MAPPER
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JsonService
parameter_list|()
block|{
block|}
comment|/** Derived class should implement this method to transport requests and    * responses to and from the peer service. */
specifier|public
specifier|abstract
name|String
name|apply
parameter_list|(
name|String
name|request
parameter_list|)
function_decl|;
comment|/** Modifies a signature, changing the representation of numeric columns    * within it. This deals with the fact that JSON transmits a small long value,    * or a float which is a whole number, as an integer. Thus the accessors need    * be prepared to accept any numeric type. */
specifier|private
specifier|static
name|Meta
operator|.
name|Signature
name|finagle
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnMetaData
name|column
range|:
name|signature
operator|.
name|columns
control|)
block|{
name|columns
operator|.
name|add
argument_list|(
name|finagle
argument_list|(
name|column
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|columns
operator|.
name|equals
argument_list|(
name|signature
operator|.
name|columns
argument_list|)
condition|)
block|{
return|return
name|signature
return|;
block|}
return|return
operator|new
name|Meta
operator|.
name|Signature
argument_list|(
name|columns
argument_list|,
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
name|cursorFactory
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ColumnMetaData
name|finagle
parameter_list|(
name|ColumnMetaData
name|column
parameter_list|)
block|{
switch|switch
condition|(
name|column
operator|.
name|type
operator|.
name|rep
condition|)
block|{
case|case
name|BYTE
case|:
case|case
name|PRIMITIVE_BYTE
case|:
case|case
name|DOUBLE
case|:
case|case
name|PRIMITIVE_DOUBLE
case|:
case|case
name|FLOAT
case|:
case|case
name|PRIMITIVE_FLOAT
case|:
case|case
name|INTEGER
case|:
case|case
name|PRIMITIVE_INT
case|:
case|case
name|SHORT
case|:
case|case
name|PRIMITIVE_SHORT
case|:
case|case
name|LONG
case|:
case|case
name|PRIMITIVE_LONG
case|:
return|return
name|column
operator|.
name|setRep
argument_list|(
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|NUMBER
argument_list|)
return|;
block|}
switch|switch
condition|(
name|column
operator|.
name|type
operator|.
name|id
condition|)
block|{
case|case
name|Types
operator|.
name|VARBINARY
case|:
case|case
name|Types
operator|.
name|BINARY
case|:
return|return
name|column
operator|.
name|setRep
argument_list|(
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|STRING
argument_list|)
return|;
case|case
name|Types
operator|.
name|DECIMAL
case|:
case|case
name|Types
operator|.
name|NUMERIC
case|:
return|return
name|column
operator|.
name|setRep
argument_list|(
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|NUMBER
argument_list|)
return|;
default|default:
return|return
name|column
return|;
block|}
block|}
specifier|private
specifier|static
name|PrepareResponse
name|finagle
parameter_list|(
name|PrepareResponse
name|response
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|StatementHandle
name|statement
init|=
name|finagle
argument_list|(
name|response
operator|.
name|statement
argument_list|)
decl_stmt|;
if|if
condition|(
name|statement
operator|==
name|response
operator|.
name|statement
condition|)
block|{
return|return
name|response
return|;
block|}
return|return
operator|new
name|PrepareResponse
argument_list|(
name|statement
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Meta
operator|.
name|StatementHandle
name|finagle
parameter_list|(
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|)
block|{
specifier|final
name|Meta
operator|.
name|Signature
name|signature
init|=
name|finagle
argument_list|(
name|h
operator|.
name|signature
argument_list|)
decl_stmt|;
if|if
condition|(
name|signature
operator|==
name|h
operator|.
name|signature
condition|)
block|{
return|return
name|h
return|;
block|}
return|return
operator|new
name|Meta
operator|.
name|StatementHandle
argument_list|(
name|h
operator|.
name|connectionId
argument_list|,
name|h
operator|.
name|id
argument_list|,
name|signature
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ResultSetResponse
name|finagle
parameter_list|(
name|ResultSetResponse
name|r
parameter_list|)
block|{
if|if
condition|(
name|r
operator|.
name|updateCount
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|r
operator|.
name|signature
operator|==
literal|null
assert|;
return|return
name|r
return|;
block|}
specifier|final
name|Meta
operator|.
name|Signature
name|signature
init|=
name|finagle
argument_list|(
name|r
operator|.
name|signature
argument_list|)
decl_stmt|;
if|if
condition|(
name|signature
operator|==
name|r
operator|.
name|signature
condition|)
block|{
return|return
name|r
return|;
block|}
return|return
operator|new
name|ResultSetResponse
argument_list|(
name|r
operator|.
name|connectionId
argument_list|,
name|r
operator|.
name|statementId
argument_list|,
name|r
operator|.
name|ownStatement
argument_list|,
name|signature
argument_list|,
name|r
operator|.
name|firstFrame
argument_list|,
name|r
operator|.
name|updateCount
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ExecuteResponse
name|finagle
parameter_list|(
name|ExecuteResponse
name|r
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|ResultSetResponse
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ResultSetResponse
name|result
range|:
name|r
operator|.
name|results
control|)
block|{
name|ResultSetResponse
name|result2
init|=
name|finagle
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|result2
operator|!=
name|result
condition|)
block|{
operator|++
name|changeCount
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
name|result2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changeCount
operator|==
literal|0
condition|)
block|{
return|return
name|r
return|;
block|}
return|return
operator|new
name|ExecuteResponse
argument_list|(
name|results
argument_list|)
return|;
block|}
comment|//@VisibleForTesting
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|decode
parameter_list|(
name|String
name|response
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|valueType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MAPPER
operator|.
name|readValue
argument_list|(
name|response
argument_list|,
name|valueType
argument_list|)
return|;
block|}
comment|//@VisibleForTesting
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|String
name|encode
parameter_list|(
name|T
name|request
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|MAPPER
operator|.
name|writeValue
argument_list|(
name|w
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return
name|w
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|RuntimeException
name|handle
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
name|e
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
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ResultSetResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|SchemasRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ResultSetResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|TablesRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ResultSetResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|TableTypesRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ResultSetResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ResultSetResponse
name|apply
parameter_list|(
name|ColumnsRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ResultSetResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|PrepareResponse
name|apply
parameter_list|(
name|PrepareRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|finagle
argument_list|(
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|PrepareResponse
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ExecuteResponse
name|apply
parameter_list|(
name|PrepareAndExecuteRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|finagle
argument_list|(
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ExecuteResponse
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|FetchResponse
name|apply
parameter_list|(
name|FetchRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|FetchResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CreateStatementResponse
name|apply
parameter_list|(
name|CreateStatementRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|CreateStatementResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CloseStatementResponse
name|apply
parameter_list|(
name|CloseStatementRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|CloseStatementResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CloseConnectionResponse
name|apply
parameter_list|(
name|CloseConnectionRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|CloseConnectionResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ConnectionSyncResponse
name|apply
parameter_list|(
name|ConnectionSyncRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|ConnectionSyncResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|DatabasePropertyResponse
name|apply
parameter_list|(
name|DatabasePropertyRequest
name|request
parameter_list|)
block|{
try|try
block|{
return|return
name|decode
argument_list|(
name|apply
argument_list|(
name|encode
argument_list|(
name|request
argument_list|)
argument_list|)
argument_list|,
name|DatabasePropertyResponse
operator|.
name|class
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JsonService.java
end_comment

end_unit

