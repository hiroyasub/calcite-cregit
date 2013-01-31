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
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Called at various points in the JDBC lifecycle.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Handler
block|{
comment|/** Called when a connection is being created. If it throws, the connection      * will not be created.      *      * @param connection Connection      * @throws SQLException on error      */
name|void
name|onConnectionInit
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
end_interface

begin_comment
comment|// End Handler.java
end_comment

end_unit

