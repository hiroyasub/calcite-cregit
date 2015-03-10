begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Callback to be called when a test for validity succeeds or fails.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Litmus
block|{
comment|/** Implementation of {@link org.apache.calcite.util.Litmus} that throws    * an {@link java.lang.AssertionError} on failure. */
name|Litmus
name|THROW
init|=
operator|new
name|Litmus
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|fail
parameter_list|(
name|String
name|message
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|succeed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|/** Called when test fails. Returns false or throws. */
name|boolean
name|fail
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
comment|/** Called when test succeeds. Returns true. */
name|boolean
name|succeed
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Litmus.java
end_comment

end_unit

