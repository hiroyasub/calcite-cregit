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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Used to extend functionality of mtsql.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConcurrentTestPlugin
block|{
comment|/**      * Should containing test be disabled?      *      * @return true if containing test should be disabled      */
specifier|public
name|boolean
name|isTestDisabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * What commands are supported by this plugin within      * a thread or repeat section. Commands should start with '@'.      *      * @return List of supported commands      */
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getSupportedThreadCommands
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
comment|/**      * What commands are supported by this plugin before      * the setup section. Commands should start with '@'.      *      * @return List of supported commands      */
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getSupportedPreSetupCommands
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
comment|/**      *  Create and return plugin command for given name.      * @param name Name of command plugin      * @param params parameters for command.      * @return Initialized plugin command.      */
specifier|public
specifier|abstract
name|ConcurrentTestPluginCommand
name|getCommandFor
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|params
parameter_list|)
function_decl|;
comment|/**      *  Do pre-setup action for given command and parameters.      * @param name Name of command plugin      * @param params parameters for command.      */
specifier|public
name|void
name|preSetupFor
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|params
parameter_list|)
block|{
block|}
block|}
end_class

begin_comment
comment|// End ConcurrentTestPlugin.java
end_comment

end_unit

