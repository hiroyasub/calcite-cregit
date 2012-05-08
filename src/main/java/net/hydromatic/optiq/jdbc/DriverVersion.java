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
comment|/**  * Driver version information.  *  *<p>Kept in a separate file so that that version information can be  * generated.</p>  */
end_comment

begin_class
class|class
name|DriverVersion
block|{
specifier|final
name|int
name|majorVersion
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|minorVersion
init|=
literal|1
decl_stmt|;
specifier|final
name|String
name|name
init|=
literal|"Optiq JDBC Driver"
decl_stmt|;
specifier|final
name|String
name|versionString
init|=
literal|"0.1"
decl_stmt|;
specifier|final
name|String
name|productName
init|=
literal|"Optiq"
decl_stmt|;
specifier|final
name|String
name|productVersion
init|=
literal|"0.1"
decl_stmt|;
specifier|public
name|int
name|databaseMajorVersion
init|=
literal|0
decl_stmt|;
specifier|public
name|int
name|databaseMinorVersion
init|=
literal|1
decl_stmt|;
block|}
end_class

begin_comment
comment|// End DriverVersion.java
end_comment

end_unit

