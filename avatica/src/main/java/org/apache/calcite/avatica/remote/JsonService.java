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
name|DeserializationFeature
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

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.remote.Service}  * that encodes requests and responses as JSON.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JsonService
extends|extends
name|AbstractService
block|{
specifier|public
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
name|MAPPER
operator|.
name|configure
argument_list|(
name|DeserializationFeature
operator|.
name|USE_BIG_DECIMAL_FOR_FLOATS
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
name|ResultSetResponse
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
name|ResultSetResponse
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
name|ResultSetResponse
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
name|ResultSetResponse
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
name|ResultSetResponse
name|apply
parameter_list|(
name|TypeInfoRequest
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
name|ResultSetResponse
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
name|ResultSetResponse
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
name|ExecuteResponse
name|apply
parameter_list|(
name|ExecuteRequest
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

