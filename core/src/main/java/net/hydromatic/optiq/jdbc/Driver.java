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
name|function
operator|.
name|Function0
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
name|config
operator|.
name|OptiqConnectionProperty
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
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|model
operator|.
name|ModelHandler
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
name|*
import|;
end_import

begin_comment
comment|/**  * Optiq JDBC driver.  */
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
literal|"jdbc:optiq:"
decl_stmt|;
specifier|final
name|Function0
argument_list|<
name|OptiqPrepare
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
name|OptiqPrepare
argument_list|>
name|createPrepareFactory
parameter_list|()
block|{
return|return
name|OptiqPrepare
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
return|return
literal|"net.hydromatic.optiq.jdbc.OptiqJdbc3Factory"
return|;
case|case
name|JDBC_40
case|:
return|return
literal|"net.hydromatic.optiq.jdbc.OptiqJdbc40Factory"
return|;
case|case
name|JDBC_41
case|:
default|default:
return|return
literal|"net.hydromatic.optiq.jdbc.OptiqJdbc41Factory"
return|;
block|}
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
literal|"net-hydromatic-optiq-jdbc.properties"
argument_list|,
literal|"Optiq JDBC Driver"
argument_list|,
literal|"unknown version"
argument_list|,
literal|"Optiq"
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
name|OptiqConnectionImpl
name|connection
init|=
operator|(
name|OptiqConnectionImpl
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
name|OptiqConnectionProperty
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/** Creates an internal connection. */
name|OptiqConnection
name|connect
parameter_list|(
name|OptiqRootSchema
name|rootSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|(
name|OptiqConnection
operator|)
operator|(
operator|(
name|OptiqFactory
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
block|}
end_class

begin_comment
comment|// End Driver.java
end_comment

end_unit

