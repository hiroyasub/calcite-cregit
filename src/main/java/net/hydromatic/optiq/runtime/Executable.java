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
name|runtime
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerable
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
name|DataContext
import|;
end_import

begin_comment
comment|/**  * Executable statement.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Executable
block|{
comment|/**    * Executes this statement and returns an enumerable which will yield rows.    * The {@code environment} parameter provides the values in the root of the    * environment (usually schemas).    *    * @param dataContext Environment that provides tables    * @return Enumerable over rows    */
name|Enumerable
name|execute
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Executable.java
end_comment

end_unit

