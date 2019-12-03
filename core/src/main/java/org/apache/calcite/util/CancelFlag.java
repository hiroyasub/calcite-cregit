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
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptPlanner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * CancelFlag is used to post and check cancellation requests.  *  *<p>Pass it to {@link RelOptPlanner} by putting it into a {@link Context}.  */
end_comment

begin_class
specifier|public
class|class
name|CancelFlag
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/** The flag that holds the cancel state.    * Feel free to use the flag directly. */
specifier|public
specifier|final
name|AtomicBoolean
name|atomicBoolean
decl_stmt|;
specifier|public
name|CancelFlag
parameter_list|(
name|AtomicBoolean
name|atomicBoolean
parameter_list|)
block|{
name|this
operator|.
name|atomicBoolean
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|atomicBoolean
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return whether a cancellation has been requested    */
specifier|public
name|boolean
name|isCancelRequested
parameter_list|()
block|{
return|return
name|atomicBoolean
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Requests a cancellation.    */
specifier|public
name|void
name|requestCancel
parameter_list|()
block|{
name|atomicBoolean
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clears any pending cancellation request.    */
specifier|public
name|void
name|clearCancel
parameter_list|()
block|{
name|atomicBoolean
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

