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
comment|/**  * Thread-local variable that returns a handle that can be closed.  *  * @param<T> Value type  */
end_comment

begin_class
specifier|public
class|class
name|TryThreadLocal
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ThreadLocal
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|T
name|initialValue
decl_stmt|;
comment|/** Creates a TryThreadLocal.    *    * @param initialValue Initial value    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|TryThreadLocal
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|T
name|initialValue
parameter_list|)
block|{
return|return
operator|new
name|TryThreadLocal
argument_list|<>
argument_list|(
name|initialValue
argument_list|)
return|;
block|}
specifier|private
name|TryThreadLocal
parameter_list|(
name|T
name|initialValue
parameter_list|)
block|{
name|this
operator|.
name|initialValue
operator|=
name|initialValue
expr_stmt|;
block|}
comment|// It is important that this method is final.
comment|// This ensures that the sub-class does not choose a different initial
comment|// value. Then the close logic can detect whether the previous value was
comment|// equal to the initial value.
annotation|@
name|Override
specifier|protected
specifier|final
name|T
name|initialValue
parameter_list|()
block|{
return|return
name|initialValue
return|;
block|}
comment|/** Assigns the value as {@code value} for the current thread.    * Returns a {@link Memo} which, when closed, will assign the value    * back to the previous value. */
specifier|public
name|Memo
name|push
parameter_list|(
name|T
name|value
parameter_list|)
block|{
specifier|final
name|T
name|previous
init|=
name|get
argument_list|()
decl_stmt|;
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
parameter_list|()
lambda|->
block|{
if|if
condition|(
name|previous
operator|==
name|initialValue
condition|)
block|{
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|set
argument_list|(
name|previous
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Remembers to set the value back. */
specifier|public
interface|interface
name|Memo
extends|extends
name|AutoCloseable
block|{
comment|/** Sets the value back; never throws. */
annotation|@
name|Override
name|void
name|close
parameter_list|()
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End TryThreadLocal.java
end_comment

end_unit

