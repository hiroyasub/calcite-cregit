begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|concurrent
package|;
end_package

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
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/**  * Used to extend functionality of mtsql.  *  * @author jhahn  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConcurrentTestPluginCommand
block|{
specifier|public
specifier|static
interface|interface
name|TestContext
block|{
comment|/**          * Store a message as output for mtsql script.          *          * @param message Message to be output          */
specifier|public
name|void
name|storeMessage
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
comment|/**          * Get connection for thread.          *          * @return connection for thread          */
specifier|public
name|Connection
name|getConnection
parameter_list|()
function_decl|;
comment|/**          * Get current statement for thread, or null if none.          *          * @return current statement for thread          */
specifier|public
name|Statement
name|getCurrentStatement
parameter_list|()
function_decl|;
block|}
comment|/**      * Implement this method to extend functionality of mtsql.      *      * @param testContext Exposed context for plugin to run in.      * @throws IOException      */
name|void
name|execute
parameter_list|(
name|TestContext
name|testContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

begin_comment
comment|// End ConcurrentTestPluginCommand.java
end_comment

end_unit

