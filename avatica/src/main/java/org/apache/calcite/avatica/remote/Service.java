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
name|annotation
operator|.
name|JsonCreator
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
name|annotation
operator|.
name|JsonProperty
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
name|annotation
operator|.
name|JsonSubTypes
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
name|annotation
operator|.
name|JsonTypeInfo
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
comment|/**  * API for request-response calls to an Avatica server.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Service
block|{
name|ResultSetResponse
name|apply
parameter_list|(
name|CatalogsRequest
name|request
parameter_list|)
function_decl|;
name|ResultSetResponse
name|apply
parameter_list|(
name|SchemasRequest
name|request
parameter_list|)
function_decl|;
name|ResultSetResponse
name|apply
parameter_list|(
name|TablesRequest
name|request
parameter_list|)
function_decl|;
name|ResultSetResponse
name|apply
parameter_list|(
name|TableTypesRequest
name|request
parameter_list|)
function_decl|;
name|ResultSetResponse
name|apply
parameter_list|(
name|ColumnsRequest
name|request
parameter_list|)
function_decl|;
name|PrepareResponse
name|apply
parameter_list|(
name|PrepareRequest
name|request
parameter_list|)
function_decl|;
name|ExecuteResponse
name|apply
parameter_list|(
name|PrepareAndExecuteRequest
name|request
parameter_list|)
function_decl|;
name|FetchResponse
name|apply
parameter_list|(
name|FetchRequest
name|request
parameter_list|)
function_decl|;
name|CreateStatementResponse
name|apply
parameter_list|(
name|CreateStatementRequest
name|request
parameter_list|)
function_decl|;
name|CloseStatementResponse
name|apply
parameter_list|(
name|CloseStatementRequest
name|request
parameter_list|)
function_decl|;
name|CloseConnectionResponse
name|apply
parameter_list|(
name|CloseConnectionRequest
name|request
parameter_list|)
function_decl|;
name|ConnectionSyncResponse
name|apply
parameter_list|(
name|ConnectionSyncRequest
name|request
parameter_list|)
function_decl|;
comment|/** Factory that creates a {@code Service}. */
interface|interface
name|Factory
block|{
name|Service
name|create
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
function_decl|;
block|}
comment|/** Base class for all service request messages. */
annotation|@
name|JsonTypeInfo
argument_list|(
name|use
operator|=
name|JsonTypeInfo
operator|.
name|Id
operator|.
name|NAME
argument_list|,
name|property
operator|=
literal|"request"
argument_list|,
name|defaultImpl
operator|=
name|SchemasRequest
operator|.
name|class
argument_list|)
annotation|@
name|JsonSubTypes
argument_list|(
block|{
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CatalogsRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"getCatalogs"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|SchemasRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"getSchemas"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|TablesRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"getTables"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|TableTypesRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"getTableTypes"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|ColumnsRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"getColumns"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|PrepareRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"prepare"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|PrepareAndExecuteRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"prepareAndExecute"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|FetchRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"fetch"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CreateStatementRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"createStatement"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CloseStatementRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"closeStatement"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CloseConnectionRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"closeConnection"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|ConnectionSyncRequest
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"connectionSync"
argument_list|)
block|}
argument_list|)
specifier|abstract
class|class
name|Request
block|{
specifier|abstract
name|Response
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
function_decl|;
block|}
comment|/** Base class for all service response messages. */
annotation|@
name|JsonTypeInfo
argument_list|(
name|use
operator|=
name|JsonTypeInfo
operator|.
name|Id
operator|.
name|NAME
argument_list|,
name|property
operator|=
literal|"response"
argument_list|,
name|defaultImpl
operator|=
name|ResultSetResponse
operator|.
name|class
argument_list|)
annotation|@
name|JsonSubTypes
argument_list|(
block|{
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|ResultSetResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"resultSet"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|PrepareResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"prepare"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|FetchResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"fetch"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CreateStatementResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"createStatement"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CloseStatementResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"closeStatement"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|CloseConnectionResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"closeConnection"
argument_list|)
block|,
annotation|@
name|JsonSubTypes
operator|.
name|Type
argument_list|(
name|value
operator|=
name|ConnectionSyncResponse
operator|.
name|class
argument_list|,
name|name
operator|=
literal|"connectionSync"
argument_list|)
block|}
argument_list|)
specifier|abstract
class|class
name|Response
block|{   }
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#getCatalogs()}. */
class|class
name|CatalogsRequest
extends|extends
name|Request
block|{
name|ResultSetResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Request for    * {@link Meta#getSchemas(String, org.apache.calcite.avatica.Meta.Pat)}. */
class|class
name|SchemasRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|catalog
decl_stmt|;
specifier|public
specifier|final
name|String
name|schemaPattern
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|SchemasRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"catalog"
argument_list|)
name|String
name|catalog
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"schemaPattern"
argument_list|)
name|String
name|schemaPattern
parameter_list|)
block|{
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
name|this
operator|.
name|schemaPattern
operator|=
name|schemaPattern
expr_stmt|;
block|}
name|ResultSetResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Request for    * {@link Meta#getTables(String, org.apache.calcite.avatica.Meta.Pat, org.apache.calcite.avatica.Meta.Pat, java.util.List)}    */
class|class
name|TablesRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|catalog
decl_stmt|;
specifier|public
specifier|final
name|String
name|schemaPattern
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableNamePattern
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|typeList
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|TablesRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"catalog"
argument_list|)
name|String
name|catalog
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"schemaPattern"
argument_list|)
name|String
name|schemaPattern
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"tableNamePattern"
argument_list|)
name|String
name|tableNamePattern
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"typeList"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|typeList
parameter_list|)
block|{
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
name|this
operator|.
name|schemaPattern
operator|=
name|schemaPattern
expr_stmt|;
name|this
operator|.
name|tableNamePattern
operator|=
name|tableNamePattern
expr_stmt|;
name|this
operator|.
name|typeList
operator|=
name|typeList
expr_stmt|;
block|}
annotation|@
name|Override
name|Response
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Request for    * {@link Meta#getTableTypes()}. */
class|class
name|TableTypesRequest
extends|extends
name|Request
block|{
annotation|@
name|Override
name|ResultSetResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Request for    * {@link Meta#getColumns(String, org.apache.calcite.avatica.Meta.Pat, org.apache.calcite.avatica.Meta.Pat, org.apache.calcite.avatica.Meta.Pat)}.    */
class|class
name|ColumnsRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|catalog
decl_stmt|;
specifier|public
specifier|final
name|String
name|schemaPattern
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableNamePattern
decl_stmt|;
specifier|public
specifier|final
name|String
name|columnNamePattern
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|ColumnsRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"catalog"
argument_list|)
name|String
name|catalog
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"schemaPattern"
argument_list|)
name|String
name|schemaPattern
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"tableNamePattern"
argument_list|)
name|String
name|tableNamePattern
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"columnNamePattern"
argument_list|)
name|String
name|columnNamePattern
parameter_list|)
block|{
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
name|this
operator|.
name|schemaPattern
operator|=
name|schemaPattern
expr_stmt|;
name|this
operator|.
name|tableNamePattern
operator|=
name|tableNamePattern
expr_stmt|;
name|this
operator|.
name|columnNamePattern
operator|=
name|columnNamePattern
expr_stmt|;
block|}
name|ResultSetResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response that contains a result set.    *    *<p>Regular result sets have {@code updateCount} -1;    * any other value means a dummy result set that is just a count, and has    * no signature and no other data.    *    *<p>Several types of request, including    * {@link org.apache.calcite.avatica.Meta#getCatalogs()} and    * {@link org.apache.calcite.avatica.Meta#getSchemas(String, org.apache.calcite.avatica.Meta.Pat)}    * {@link Meta#getTables(String, Meta.Pat, Meta.Pat, List)}    * {@link Meta#getTableTypes()}    * return this response. */
class|class
name|ResultSetResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|statementId
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|ownStatement
decl_stmt|;
specifier|public
specifier|final
name|Meta
operator|.
name|Signature
name|signature
decl_stmt|;
specifier|public
specifier|final
name|Meta
operator|.
name|Frame
name|firstFrame
decl_stmt|;
specifier|public
specifier|final
name|int
name|updateCount
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|ResultSetResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"statementId"
argument_list|)
name|int
name|statementId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"ownStatement"
argument_list|)
name|boolean
name|ownStatement
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"signature"
argument_list|)
name|Meta
operator|.
name|Signature
name|signature
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"firstFrame"
argument_list|)
name|Meta
operator|.
name|Frame
name|firstFrame
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"updateCount"
argument_list|)
name|int
name|updateCount
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|statementId
operator|=
name|statementId
expr_stmt|;
name|this
operator|.
name|ownStatement
operator|=
name|ownStatement
expr_stmt|;
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
name|this
operator|.
name|firstFrame
operator|=
name|firstFrame
expr_stmt|;
name|this
operator|.
name|updateCount
operator|=
name|updateCount
expr_stmt|;
block|}
block|}
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#prepareAndExecute(org.apache.calcite.avatica.Meta.ConnectionHandle, String, int, org.apache.calcite.avatica.Meta.PrepareCallback)}. */
class|class
name|PrepareAndExecuteRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
specifier|public
specifier|final
name|int
name|maxRowCount
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|PrepareAndExecuteRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"sql"
argument_list|)
name|String
name|sql
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"maxRowCount"
argument_list|)
name|int
name|maxRowCount
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|maxRowCount
operator|=
name|maxRowCount
expr_stmt|;
block|}
annotation|@
name|Override
name|ExecuteResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response to a    * {@link org.apache.calcite.avatica.remote.Service.PrepareAndExecuteRequest}. */
class|class
name|ExecuteResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|ResultSetResponse
argument_list|>
name|results
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|ExecuteResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"resultSets"
argument_list|)
name|List
argument_list|<
name|ResultSetResponse
argument_list|>
name|results
parameter_list|)
block|{
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
block|}
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#prepare(org.apache.calcite.avatica.Meta.ConnectionHandle, String, int)}. */
class|class
name|PrepareRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
specifier|public
specifier|final
name|int
name|maxRowCount
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|PrepareRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"sql"
argument_list|)
name|String
name|sql
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"maxRowCount"
argument_list|)
name|int
name|maxRowCount
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|maxRowCount
operator|=
name|maxRowCount
expr_stmt|;
block|}
annotation|@
name|Override
name|PrepareResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response from    * {@link org.apache.calcite.avatica.remote.Service.PrepareRequest}. */
class|class
name|PrepareResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|Meta
operator|.
name|StatementHandle
name|statement
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|PrepareResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"statement"
argument_list|)
name|Meta
operator|.
name|StatementHandle
name|statement
parameter_list|)
block|{
name|this
operator|.
name|statement
operator|=
name|statement
expr_stmt|;
block|}
block|}
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#fetch(Meta.StatementHandle, List, int, int)}. */
class|class
name|FetchRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|statementId
decl_stmt|;
specifier|public
specifier|final
name|int
name|offset
decl_stmt|;
comment|/** Maximum number of rows to be returned in the frame. Negative means no      * limit. */
specifier|public
specifier|final
name|int
name|fetchMaxRowCount
decl_stmt|;
comment|/** A list of parameter values, if statement is to be executed; otherwise      * null. */
specifier|public
specifier|final
name|List
argument_list|<
name|TypedValue
argument_list|>
name|parameterValues
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|FetchRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"statementId"
argument_list|)
name|int
name|statementId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"parameterValues"
argument_list|)
name|List
argument_list|<
name|TypedValue
argument_list|>
name|parameterValues
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"offset"
argument_list|)
name|int
name|offset
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"fetchMaxRowCount"
argument_list|)
name|int
name|fetchMaxRowCount
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|statementId
operator|=
name|statementId
expr_stmt|;
name|this
operator|.
name|parameterValues
operator|=
name|parameterValues
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|fetchMaxRowCount
operator|=
name|fetchMaxRowCount
expr_stmt|;
block|}
annotation|@
name|Override
name|FetchResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response from    * {@link org.apache.calcite.avatica.remote.Service.FetchRequest}. */
class|class
name|FetchResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|Meta
operator|.
name|Frame
name|frame
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|FetchResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"frame"
argument_list|)
name|Meta
operator|.
name|Frame
name|frame
parameter_list|)
block|{
name|this
operator|.
name|frame
operator|=
name|frame
expr_stmt|;
block|}
block|}
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#createStatement(org.apache.calcite.avatica.Meta.ConnectionHandle)}. */
class|class
name|CreateStatementRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|CreateStatementRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"signature"
argument_list|)
name|String
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
annotation|@
name|Override
name|CreateStatementResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response from    * {@link org.apache.calcite.avatica.remote.Service.CreateStatementRequest}. */
class|class
name|CreateStatementResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|statementId
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|CreateStatementResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"statementId"
argument_list|)
name|int
name|statementId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|statementId
operator|=
name|statementId
expr_stmt|;
block|}
block|}
comment|/** Request for    * {@link org.apache.calcite.avatica.Meta#closeStatement(org.apache.calcite.avatica.Meta.StatementHandle)}. */
class|class
name|CloseStatementRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|statementId
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|CloseStatementRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"statementId"
argument_list|)
name|int
name|statementId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|statementId
operator|=
name|statementId
expr_stmt|;
block|}
annotation|@
name|Override
name|CloseStatementResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response from    * {@link org.apache.calcite.avatica.remote.Service.CloseStatementRequest}. */
class|class
name|CloseStatementResponse
extends|extends
name|Response
block|{
annotation|@
name|JsonCreator
specifier|public
name|CloseStatementResponse
parameter_list|()
block|{
block|}
block|}
comment|/** Request for    * {@link Meta#closeConnection(org.apache.calcite.avatica.Meta.ConnectionHandle)}. */
class|class
name|CloseConnectionRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|CloseConnectionRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
annotation|@
name|Override
name|CloseConnectionResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response from    * {@link org.apache.calcite.avatica.remote.Service.CloseConnectionRequest}. */
class|class
name|CloseConnectionResponse
extends|extends
name|Response
block|{
annotation|@
name|JsonCreator
specifier|public
name|CloseConnectionResponse
parameter_list|()
block|{
block|}
block|}
comment|/** Request for {@link Meta#connectionSync(Meta.ConnectionHandle, Meta.ConnectionProperties)}. */
class|class
name|ConnectionSyncRequest
extends|extends
name|Request
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|Meta
operator|.
name|ConnectionProperties
name|connProps
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|ConnectionSyncRequest
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"connProps"
argument_list|)
name|Meta
operator|.
name|ConnectionProperties
name|connProps
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|connProps
operator|=
name|connProps
expr_stmt|;
block|}
annotation|@
name|Override
name|ConnectionSyncResponse
name|accept
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
return|return
name|service
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
comment|/** Response for    * {@link Meta#connectionSync(Meta.ConnectionHandle, Meta.ConnectionProperties)}. */
class|class
name|ConnectionSyncResponse
extends|extends
name|Response
block|{
specifier|public
specifier|final
name|Meta
operator|.
name|ConnectionProperties
name|connProps
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|ConnectionSyncResponse
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connProps"
argument_list|)
name|Meta
operator|.
name|ConnectionProperties
name|connProps
parameter_list|)
block|{
name|this
operator|.
name|connProps
operator|=
name|connProps
expr_stmt|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End Service.java
end_comment

end_unit

