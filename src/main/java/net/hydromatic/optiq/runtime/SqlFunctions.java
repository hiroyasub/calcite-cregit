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

begin_comment
comment|/**  * Helper methods to implement SQL functions in generated code.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|SqlFunctions
block|{
comment|/** SQL SUBSTRING(string FROM ... TO ...) function. */
specifier|public
specifier|static
name|String
name|substring
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
return|return
name|s
operator|.
name|substring
argument_list|(
name|from
operator|-
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|to
operator|-
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlFunctions.java
end_comment

end_unit

