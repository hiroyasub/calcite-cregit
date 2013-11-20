begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|splunk
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
name|DriverVersion
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|UnregisteredDriver
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
name|MutableSchema
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
name|Schema
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
name|jdbc
operator|.
name|JdbcSchema
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
name|splunk
operator|.
name|search
operator|.
name|SplunkConnection
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
name|jdbc
operator|.
name|*
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * JDBC driver for Splunk.  *  *<p>It accepts connect strings that start with "jdbc:splunk:".</p>  */
end_comment

begin_class
specifier|public
class|class
name|SplunkDriver
extends|extends
name|UnregisteredDriver
block|{
specifier|protected
name|SplunkDriver
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
static|static
block|{
operator|new
name|SplunkDriver
argument_list|()
operator|.
name|register
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getConnectStringPrefix
parameter_list|()
block|{
return|return
literal|"jdbc:splunk:"
return|;
block|}
specifier|protected
name|DriverVersion
name|createDriverVersion
parameter_list|()
block|{
return|return
operator|new
name|SplunkDriverVersion
argument_list|()
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
name|Connection
name|connection
init|=
name|super
operator|.
name|connect
argument_list|(
name|url
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
operator|(
name|OptiqConnection
operator|)
name|connection
decl_stmt|;
name|SplunkConnection
name|splunkConnection
decl_stmt|;
try|try
block|{
name|String
name|url1
init|=
name|info
operator|.
name|getProperty
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url1
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify 'url' property"
argument_list|)
throw|;
block|}
name|URL
name|url2
init|=
operator|new
name|URL
argument_list|(
name|url1
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|info
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify 'user' property"
argument_list|)
throw|;
block|}
name|String
name|password
init|=
name|info
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify 'password' property"
argument_list|)
throw|;
block|}
name|splunkConnection
operator|=
operator|new
name|SplunkConnection
argument_list|(
name|url2
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Cannot connect"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|MutableSchema
name|rootSchema
init|=
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
specifier|final
name|String
name|schemaName
init|=
literal|"splunk"
decl_stmt|;
specifier|final
name|SplunkSchema
name|schema
init|=
operator|new
name|SplunkSchema
argument_list|(
name|optiqConnection
argument_list|,
name|rootSchema
argument_list|,
name|schemaName
argument_list|,
name|splunkConnection
argument_list|,
name|optiqConnection
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|rootSchema
operator|.
name|getSubSchemaExpression
argument_list|(
name|schemaName
argument_list|,
name|Schema
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|rootSchema
operator|.
name|addSchema
argument_list|(
name|schemaName
argument_list|,
name|schema
argument_list|)
expr_stmt|;
comment|// Include a schema called "mysql" in every splunk connection.
comment|// This is a hack for demo purposes. TODO: Add a config file mechanism.
if|if
condition|(
literal|true
condition|)
block|{
specifier|final
name|String
name|mysqlSchemaName
init|=
literal|"mysql"
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"com.mysql.jdbc.Driver"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
name|JdbcSchema
operator|.
name|create
argument_list|(
name|optiqConnection
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|JdbcSchema
operator|.
name|dataSource
argument_list|(
literal|"jdbc:mysql://localhost"
argument_list|,
literal|null
argument_list|,
literal|"foodmart"
argument_list|,
literal|"foodmart"
argument_list|)
argument_list|,
literal|"foodmart"
argument_list|,
literal|""
argument_list|,
name|mysqlSchemaName
argument_list|)
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
block|}
end_class

begin_comment
comment|// End SplunkDriver.java
end_comment

end_unit

