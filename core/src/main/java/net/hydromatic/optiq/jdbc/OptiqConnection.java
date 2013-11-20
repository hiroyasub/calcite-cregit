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
name|linq4j
operator|.
name|QueryProvider
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
comment|/**  * Extension to Optiq's implementation of  * {@link java.sql.Connection JDBC connection} allows schemas to be defined  * dynamically.  *  *<p>You can start off with an empty connection (no schemas), define one  * or two schemas, and start querying them.</p>  *  *<p>Since an {@code OptiqConnection} implements the linq4j  * {@link QueryProvider} interface, you can use a connection to execute  * expression trees as queries.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqConnection
extends|extends
name|Connection
extends|,
name|QueryProvider
block|{
comment|/**    * Returns the root schema.    *    *<p>You can define objects (such as relations) in this schema, and    * also nested schemas.</p>    *    * @return Root schema    */
name|MutableSchema
name|getRootSchema
parameter_list|()
function_decl|;
comment|/**    * Returns the type factory.    *    * @return Type factory    */
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Returns an instance of the connection properties.    *    *<p>NOTE: The resulting collection of properties is same collection used    * by the connection, and is writable, but behavior if you modify the    * collection is undefined. Some implementations might, for example, see    * a modified property, but only if you set it before you create a    * statement. We will remove this method when there are better    * implementations of stateful connections and configuration.</p>    *    * @return properties    */
name|Properties
name|getProperties
parameter_list|()
function_decl|;
comment|// in java.sql.Connection from JDK 1.7, but declare here to allow other JDKs
name|void
name|setSchema
parameter_list|(
name|String
name|schema
parameter_list|)
throws|throws
name|SQLException
function_decl|;
comment|// in java.sql.Connection from JDK 1.7, but declare here to allow other JDKs
name|String
name|getSchema
parameter_list|()
throws|throws
name|SQLException
function_decl|;
comment|/** Returns a view onto this connection's configuration properties. Code    * within Optiq should use this view rather than calling    * {@link java.util.Properties#getProperty(String)}. */
name|ConnectionConfig
name|config
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End OptiqConnection.java
end_comment

end_unit

