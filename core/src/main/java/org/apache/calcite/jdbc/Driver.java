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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcSchema
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
name|HandlerImpl
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|config
operator|.
name|CalciteConnectionProperty
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
name|function
operator|.
name|Function0
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
name|model
operator|.
name|JsonSchema
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
name|model
operator|.
name|ModelHandler
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
name|schema
operator|.
name|SchemaFactory
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
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
name|util
operator|.
name|JsonBuilder
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
name|util
operator|.
name|Util
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
name|Map
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
comment|/**  * Calcite JDBC driver.  */
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
literal|"jdbc:calcite:"
decl_stmt|;
specifier|final
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
name|prepareFactory
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
name|this
operator|.
name|prepareFactory
operator|=
name|createPrepareFactory
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Function0
argument_list|<
name|CalcitePrepare
argument_list|>
name|createPrepareFactory
parameter_list|()
block|{
return|return
name|CalcitePrepare
operator|.
name|DEFAULT_FACTORY
return|;
block|}
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
annotation|@
name|Override
specifier|protected
name|String
name|getFactoryClassName
parameter_list|(
name|JdbcVersion
name|jdbcVersion
parameter_list|)
block|{
switch|switch
condition|(
name|jdbcVersion
condition|)
block|{
case|case
name|JDBC_30
case|:
case|case
name|JDBC_40
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"JDBC version not supported: "
operator|+
name|jdbcVersion
argument_list|)
throw|;
case|case
name|JDBC_41
case|:
default|default:
return|return
literal|"org.apache.calcite.jdbc.CalciteJdbc41Factory"
return|;
block|}
block|}
annotation|@
name|Override
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
literal|"Calcite JDBC Driver"
argument_list|,
literal|"unknown version"
argument_list|,
literal|"Calcite"
argument_list|,
literal|"unknown version"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Handler
name|createHandler
parameter_list|()
block|{
return|return
operator|new
name|HandlerImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onConnectionInit
parameter_list|(
name|AvaticaConnection
name|connection_
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|CalciteConnectionImpl
name|connection
init|=
operator|(
name|CalciteConnectionImpl
operator|)
name|connection_
decl_stmt|;
name|super
operator|.
name|onConnectionInit
argument_list|(
name|connection
argument_list|)
expr_stmt|;
specifier|final
name|String
name|model
init|=
name|model
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|model
operator|!=
literal|null
condition|)
block|{
try|try
block|{
operator|new
name|ModelHandler
argument_list|(
name|connection
argument_list|,
name|model
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|connection
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
name|String
name|model
parameter_list|(
name|CalciteConnectionImpl
name|connection
parameter_list|)
block|{
name|String
name|model
init|=
name|connection
operator|.
name|config
argument_list|()
operator|.
name|model
argument_list|()
decl_stmt|;
if|if
condition|(
name|model
operator|!=
literal|null
condition|)
block|{
return|return
name|model
return|;
block|}
name|SchemaFactory
name|schemaFactory
init|=
name|connection
operator|.
name|config
argument_list|()
operator|.
name|schemaFactory
argument_list|(
name|SchemaFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|info
init|=
name|connection
operator|.
name|getProperties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|schemaName
init|=
name|Util
operator|.
name|first
argument_list|(
name|connection
operator|.
name|config
argument_list|()
operator|.
name|schema
argument_list|()
argument_list|,
literal|"adhoc"
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaFactory
operator|==
literal|null
condition|)
block|{
specifier|final
name|JsonSchema
operator|.
name|Type
name|schemaType
init|=
name|connection
operator|.
name|config
argument_list|()
operator|.
name|schemaType
argument_list|()
decl_stmt|;
if|if
condition|(
name|schemaType
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|schemaType
condition|)
block|{
case|case
name|JDBC
case|:
name|schemaFactory
operator|=
name|JdbcSchema
operator|.
name|Factory
operator|.
name|INSTANCE
expr_stmt|;
break|break;
case|case
name|MAP
case|:
name|schemaFactory
operator|=
name|AbstractSchema
operator|.
name|Factory
operator|.
name|INSTANCE
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|schemaFactory
operator|!=
literal|null
condition|)
block|{
specifier|final
name|JsonBuilder
name|json
init|=
operator|new
name|JsonBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|root
init|=
name|json
operator|.
name|map
argument_list|()
decl_stmt|;
name|root
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|root
operator|.
name|put
argument_list|(
literal|"defaultSchema"
argument_list|,
name|schemaName
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|schemaList
init|=
name|json
operator|.
name|list
argument_list|()
decl_stmt|;
name|root
operator|.
name|put
argument_list|(
literal|"schemas"
argument_list|,
name|schemaList
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|schema
init|=
name|json
operator|.
name|map
argument_list|()
decl_stmt|;
name|schemaList
operator|.
name|add
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|schema
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"custom"
argument_list|)
expr_stmt|;
name|schema
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|schemaName
argument_list|)
expr_stmt|;
name|schema
operator|.
name|put
argument_list|(
literal|"factory"
argument_list|,
name|schemaFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operandMap
init|=
name|json
operator|.
name|map
argument_list|()
decl_stmt|;
name|schema
operator|.
name|put
argument_list|(
literal|"operand"
argument_list|,
name|operandMap
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|Util
operator|.
name|toMap
argument_list|(
name|info
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"schema."
argument_list|)
condition|)
block|{
name|operandMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
literal|"schema."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|"inline:"
operator|+
name|json
operator|.
name|toJsonString
argument_list|(
name|root
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
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
argument_list|<>
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
name|CalciteConnectionProperty
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
return|return
operator|new
name|CalciteMetaImpl
argument_list|(
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
argument_list|)
return|;
block|}
comment|/** Creates an internal connection. */
name|CalciteConnection
name|connect
parameter_list|(
name|CalciteSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
name|CalciteConnection
operator|)
operator|(
operator|(
name|CalciteFactory
operator|)
name|factory
operator|)
operator|.
name|newConnection
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|CONNECT_STRING_PREFIX
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|rootSchema
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
comment|/** Creates an internal connection. */
name|CalciteConnection
name|connect
parameter_list|(
name|CalciteSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
return|return
operator|(
name|CalciteConnection
operator|)
operator|(
operator|(
name|CalciteFactory
operator|)
name|factory
operator|)
operator|.
name|newConnection
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|CONNECT_STRING_PREFIX
argument_list|,
name|properties
argument_list|,
name|rootSchema
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Driver.java
end_comment

end_unit

