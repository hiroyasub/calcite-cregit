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
name|linq4j
package|;
end_package

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Enumerator that keeps some recent and some "future" values.  *  * @param<E> Row value  */
end_comment

begin_class
specifier|public
class|class
name|MemoryEnumerator
parameter_list|<
annotation|@
name|Nullable
name|E
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|E
argument_list|>
name|enumerator
decl_stmt|;
specifier|private
specifier|final
name|MemoryFactory
argument_list|<
name|E
argument_list|>
name|memoryFactory
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|prevCounter
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|postCounter
decl_stmt|;
comment|/**    * Creates a MemoryEnumerator.    *    *<p>Use factory method {@link MemoryEnumerable#enumerator()}.    *    * @param enumerator The Enumerator that memory should be "wrapped" around    * @param history Number of present steps to remember    * @param future Number of future steps to "remember"    */
name|MemoryEnumerator
parameter_list|(
name|Enumerator
argument_list|<
name|E
argument_list|>
name|enumerator
parameter_list|,
name|int
name|history
parameter_list|,
name|int
name|future
parameter_list|)
block|{
name|this
operator|.
name|enumerator
operator|=
name|enumerator
expr_stmt|;
name|this
operator|.
name|memoryFactory
operator|=
operator|new
name|MemoryFactory
argument_list|<>
argument_list|(
name|history
argument_list|,
name|future
argument_list|)
expr_stmt|;
name|this
operator|.
name|prevCounter
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|this
operator|.
name|postCounter
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
name|current
parameter_list|()
block|{
return|return
name|memoryFactory
operator|.
name|create
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|prevCounter
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|boolean
name|lastMove
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|prevCounter
operator|.
name|getAndDecrement
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|lastMove
operator|=
name|moveNextInternal
argument_list|()
expr_stmt|;
block|}
return|return
name|lastMove
return|;
block|}
else|else
block|{
return|return
name|moveNextInternal
argument_list|()
return|;
block|}
block|}
specifier|private
name|boolean
name|moveNextInternal
parameter_list|()
block|{
specifier|final
name|boolean
name|moveNext
init|=
name|enumerator
operator|.
name|moveNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|moveNext
condition|)
block|{
name|memoryFactory
operator|.
name|add
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Check if we have to add "history" additional values
if|if
condition|(
name|postCounter
operator|.
name|getAndDecrement
argument_list|()
operator|>
literal|0
condition|)
block|{
name|memoryFactory
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

