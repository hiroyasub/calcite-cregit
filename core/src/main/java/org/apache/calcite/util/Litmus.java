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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|helpers
operator|.
name|MessageFormatter
import|;
end_import

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
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|String
name|s
init|=
name|message
operator|==
literal|null
condition|?
literal|null
else|:
name|MessageFormatter
operator|.
name|arrayFormat
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
operator|.
name|getMessage
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|s
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
annotation|@
name|Override
specifier|public
name|boolean
name|check
parameter_list|(
name|boolean
name|condition
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|condition
condition|)
block|{
return|return
name|succeed
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|fail
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
comment|/** Implementation of {@link org.apache.calcite.util.Litmus} that returns    * a status code but does not throw. */
name|Litmus
name|IGNORE
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
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
literal|false
return|;
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
annotation|@
name|Override
specifier|public
name|boolean
name|check
parameter_list|(
name|boolean
name|condition
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|condition
return|;
block|}
block|}
decl_stmt|;
comment|/** Called when test fails. Returns false or throws.    *    * @param message Message    * @param args Arguments    */
name|boolean
name|fail
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
comment|/** Called when test succeeds. Returns true. */
name|boolean
name|succeed
parameter_list|()
function_decl|;
comment|/** Checks a condition.    *    *<p>If the condition is true, calls {@link #succeed};    * if the condition is false, calls {@link #fail},    * converting {@code info} into a string message.    */
name|boolean
name|check
parameter_list|(
name|boolean
name|condition
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

