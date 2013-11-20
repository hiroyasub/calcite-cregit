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
name|avatica
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
comment|/**  * Implementation of {@link Handler} that does nothing for each callback.  * It is recommended implementations of {@code Handler} use this as a base  * class, to ensure forward compatibility.  */
end_comment

begin_class
specifier|public
class|class
name|HandlerImpl
implements|implements
name|Handler
block|{
specifier|public
name|void
name|onConnectionInit
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// nothing
block|}
specifier|public
name|void
name|onConnectionClose
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
throws|throws
name|RuntimeException
block|{
comment|// nothing
block|}
specifier|public
name|void
name|onStatementExecute
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|ResultSink
name|resultSink
parameter_list|)
throws|throws
name|RuntimeException
block|{
comment|// nothing
block|}
specifier|public
name|void
name|onStatementClose
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|)
throws|throws
name|RuntimeException
block|{
comment|// nothing
block|}
block|}
end_class

begin_comment
comment|// End HandlerImpl.java
end_comment

end_unit

