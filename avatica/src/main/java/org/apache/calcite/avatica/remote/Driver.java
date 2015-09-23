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
name|BuiltInConnectionProperty
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
name|ConnectionConfig
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
name|ConnectionProperty
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
name|DriverVersion
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
name|UnregisteredDriver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|Connection
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Avatica Remote JDBC driver.  */
end_comment

begin_class
specifier|public
class|class
name|Driver
extends|extends
name|UnregisteredDriver
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONNECT_STRING_PREFIX
init|=
literal|"jdbc:avatica:remote:"
decl_stmt|;
static|static
block|{
operator|new
name|Driver
argument_list|()
operator|.
name|register
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Driver
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Defines the method of message serialization used by the Driver    */
specifier|public
specifier|static
enum|enum
name|Serialization
block|{
name|JSON
block|,
name|PROTOBUF
block|;   }
annotation|@
name|Override
specifier|protected
name|String
name|getConnectStringPrefix
parameter_list|()
block|{
return|return
name|CONNECT_STRING_PREFIX
return|;
block|}
specifier|protected
name|DriverVersion
name|createDriverVersion
parameter_list|()
block|{
return|return
name|DriverVersion
operator|.
name|load
argument_list|(
name|Driver
operator|.
name|class
argument_list|,
literal|"org-apache-calcite-jdbc.properties"
argument_list|,
literal|"Avatica Remote JDBC Driver"
argument_list|,
literal|"unknown version"
argument_list|,
literal|"Avatica"
argument_list|,
literal|"unknown version"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Collection
argument_list|<
name|ConnectionProperty
argument_list|>
name|getConnectionProperties
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|ConnectionProperty
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ConnectionProperty
argument_list|>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|list
argument_list|,
name|BuiltInConnectionProperty
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|list
argument_list|,
name|AvaticaRemoteConnectionProperty
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|Meta
name|createMeta
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
specifier|final
name|ConnectionConfig
name|config
init|=
name|connection
operator|.
name|config
argument_list|()
decl_stmt|;
specifier|final
name|Service
name|service
init|=
name|createService
argument_list|(
name|connection
argument_list|,
name|config
argument_list|)
decl_stmt|;
return|return
operator|new
name|RemoteMeta
argument_list|(
name|connection
argument_list|,
name|service
argument_list|)
return|;
block|}
comment|/**    * Creates a {@link Service} with the given {@link AvaticaConnection} and configuration.    *    * @param connection The {@link AvaticaConnection} to use.    * @param config Configuration properties    * @return A Service implementation.    */
name|Service
name|createService
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|ConnectionConfig
name|config
parameter_list|)
block|{
specifier|final
name|Service
operator|.
name|Factory
name|metaFactory
init|=
name|config
operator|.
name|factory
argument_list|()
decl_stmt|;
specifier|final
name|Service
name|service
decl_stmt|;
if|if
condition|(
name|metaFactory
operator|!=
literal|null
condition|)
block|{
name|service
operator|=
name|metaFactory
operator|.
name|create
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|config
operator|.
name|url
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|AvaticaHttpClient
name|httpClient
init|=
name|getHttpClient
argument_list|(
name|connection
argument_list|,
name|config
argument_list|)
decl_stmt|;
specifier|final
name|Serialization
name|serializationType
init|=
name|getSerialization
argument_list|(
name|config
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|serializationType
condition|)
block|{
case|case
name|JSON
case|:
name|service
operator|=
operator|new
name|RemoteService
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
break|break;
case|case
name|PROTOBUF
case|:
name|service
operator|=
operator|new
name|RemoteProtobufService
argument_list|(
name|httpClient
argument_list|,
operator|new
name|ProtobufTranslationImpl
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unhandled serialization type: "
operator|+
name|serializationType
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|service
operator|=
operator|new
name|MockJsonService
argument_list|(
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|service
return|;
block|}
comment|/**    * Creates the HTTP client that communicates with the Avatica server.    *    * @param connection The {@link AvaticaConnection}.    * @param config The configuration.    * @return An {@link AvaticaHttpClient} implementation.    */
name|AvaticaHttpClient
name|getHttpClient
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|ConnectionConfig
name|config
parameter_list|)
block|{
name|URL
name|url
decl_stmt|;
try|try
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|config
operator|.
name|url
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
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
return|return
operator|new
name|AvaticaHttpClientImpl
argument_list|(
name|url
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
throws|throws
name|SQLException
block|{
name|AvaticaConnection
name|conn
init|=
operator|(
name|AvaticaConnection
operator|)
name|super
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|conn
operator|==
literal|null
condition|)
block|{
comment|// It's not an url for our driver
return|return
literal|null
return|;
block|}
comment|// Create the corresponding remote connection
name|ConnectionConfig
name|config
init|=
name|conn
operator|.
name|config
argument_list|()
decl_stmt|;
name|Service
name|service
init|=
name|createService
argument_list|(
name|conn
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|service
operator|.
name|apply
argument_list|(
operator|new
name|Service
operator|.
name|OpenConnectionRequest
argument_list|(
name|conn
operator|.
name|id
argument_list|,
name|Service
operator|.
name|OpenConnectionRequest
operator|.
name|serializeProperties
argument_list|(
name|info
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|conn
return|;
block|}
name|Serialization
name|getSerialization
parameter_list|(
name|ConnectionConfig
name|config
parameter_list|)
block|{
specifier|final
name|String
name|serializationStr
init|=
name|config
operator|.
name|serialization
argument_list|()
decl_stmt|;
name|Serialization
name|serializationType
init|=
name|Serialization
operator|.
name|JSON
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|serializationStr
condition|)
block|{
try|try
block|{
name|serializationType
operator|=
name|Serialization
operator|.
name|valueOf
argument_list|(
name|serializationStr
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Log a warning instead of failing harshly? Intentionally no loggers available?
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|serializationType
return|;
block|}
block|}
end_class

begin_comment
comment|// End Driver.java
end_comment

end_unit

