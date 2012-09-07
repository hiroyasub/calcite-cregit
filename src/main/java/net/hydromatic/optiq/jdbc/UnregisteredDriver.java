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
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Implementation of Optiq JDBC driver that does not register itself.  *  *<p>You can easily create a "vanity driver" that recognizes its own  * URL prefix as a sub-class of this class. Per the JDBC specification it  * must register itself.</p>  */
end_comment

begin_class
specifier|public
class|class
name|UnregisteredDriver
implements|implements
name|java
operator|.
name|sql
operator|.
name|Driver
block|{
specifier|final
name|DriverVersion
name|version
init|=
operator|new
name|DriverVersion
argument_list|()
decl_stmt|;
specifier|final
name|Factory
name|factory
decl_stmt|;
specifier|protected
name|UnregisteredDriver
parameter_list|()
block|{
name|this
operator|.
name|factory
operator|=
name|createFactory
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|Factory
name|createFactory
parameter_list|()
block|{
specifier|final
name|String
name|factoryClassName
init|=
name|getFactoryClassName
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|factoryClassName
argument_list|)
decl_stmt|;
return|return
operator|(
name|Factory
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
catch|catch
parameter_list|(
name|IllegalAccessException
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
catch|catch
parameter_list|(
name|InstantiationException
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
specifier|private
specifier|static
name|String
name|getFactoryClassName
parameter_list|()
block|{
try|try
block|{
comment|// If java.sql.PseudoColumnUsage is present, we are running JDBC 4.1
comment|// or later.
name|Class
operator|.
name|forName
argument_list|(
literal|"java.sql.PseudoColumnUsage"
argument_list|)
expr_stmt|;
return|return
literal|"net.hydromatic.optiq.jdbc.FactoryJdbc41"
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// java.sql.PseudoColumnUsage is not present. This means we are
comment|// running JDBC 4.0 or earlier.
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"java.sql.Wrapper"
argument_list|)
expr_stmt|;
return|return
literal|"net.hydromatic.optiq.jdbc.FactoryJdbc4Impl"
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e2
parameter_list|)
block|{
comment|// java.sql.Wrapper is not present. This means we are running
comment|// JDBC 3.0 or earlier (probably JDK 1.5). Load the JDBC 3.0
comment|// factory.
return|return
literal|"net.hydromatic.optiq.jdbc.FactoryJdbc3Impl"
return|;
block|}
block|}
block|}
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
if|if
condition|(
operator|!
name|acceptsURL
argument_list|(
name|url
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|factory
operator|.
name|newConnection
argument_list|(
name|this
argument_list|,
name|factory
argument_list|,
name|url
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|acceptsURL
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|OptiqConnectionImpl
operator|.
name|acceptsURL
argument_list|(
name|url
argument_list|)
return|;
block|}
specifier|public
name|DriverPropertyInfo
index|[]
name|getPropertyInfo
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
name|List
argument_list|<
name|DriverPropertyInfo
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|DriverPropertyInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|// First, add the contents of info
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|info
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|DriverPropertyInfo
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Next, add property definitions not mentioned in info
for|for
control|(
name|ConnectionProperty
name|p
range|:
name|ConnectionProperty
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|containsKey
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|DriverPropertyInfo
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|DriverPropertyInfo
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|// JDBC 4.1 support (JDK 1.7 and higher)
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/**      * Returns the driver name. Not in the JDBC API.      *      * @return Driver name      */
name|String
name|getName
parameter_list|()
block|{
return|return
name|version
operator|.
name|name
return|;
block|}
comment|/**      * Returns the driver version. Not in the JDBC API.      *      * @return Driver version      */
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
operator|.
name|versionString
return|;
block|}
specifier|public
name|int
name|getMajorVersion
parameter_list|()
block|{
return|return
name|version
operator|.
name|majorVersion
return|;
block|}
specifier|public
name|int
name|getMinorVersion
parameter_list|()
block|{
return|return
name|version
operator|.
name|minorVersion
return|;
block|}
specifier|public
name|boolean
name|jdbcCompliant
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Registers this driver with the driver manager.      */
specifier|protected
name|void
name|register
parameter_list|()
block|{
try|try
block|{
name|DriverManager
operator|.
name|registerDriver
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error occurred while registering JDBC driver "
operator|+
name|this
operator|+
literal|": "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End UnregisteredDriver.java
end_comment

end_unit

