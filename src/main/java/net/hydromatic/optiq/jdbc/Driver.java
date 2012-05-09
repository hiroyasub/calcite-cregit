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
block|}
end_class

begin_comment
comment|// End Driver.java
end_comment

end_unit

