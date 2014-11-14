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
name|test
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * ConcurrentTestTimedCommandGenerator extends  * {@link ConcurrentTestCommandGenerator} and repeats the configured command  * sequence until a certain amount of time has elapsed.  *  *<p>The command sequence is always completed in full, even if the time limit  * has been exceeded. Therefore, the time limit can only be considered the  * minimum length of time that the test will run and not a guarantee of how long  * the test will take.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentTestTimedCommandGenerator
extends|extends
name|ConcurrentTestCommandGenerator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|int
name|runTimeSeconds
decl_stmt|;
specifier|private
name|long
name|endTimeMillis
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a new ConcurrentTestTimedCommandGenerator that will run    * for at least the given amount of time. See    * {@link ConcurrentTestTimedCommandGenerator} for more information on the    * semantics of run-time length.    *    * @param runTimeSeconds minimum run-time length, in seconds    */
specifier|public
name|ConcurrentTestTimedCommandGenerator
parameter_list|(
name|int
name|runTimeSeconds
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|runTimeSeconds
operator|=
name|runTimeSeconds
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Retrieves an Iterator based on the configured commands. This Iterator,    * when it reaches the end of the command list will compare the current time    * with the test's end time. If there is time left, the Iterator will repeat    * the command sequence.    *    *<p>The test's end time is computed by taking the value of<code>    * System.currentTimeMillis()</code> the first time this method is called    * (across all thread IDs) and adding the configured run time.    *    * @param threadId the thread ID to get an Iterator on    */
name|Iterable
argument_list|<
name|ConcurrentTestCommand
argument_list|>
name|getCommandIterable
parameter_list|(
specifier|final
name|int
name|threadId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|endTimeMillis
operator|==
literal|0L
condition|)
block|{
name|endTimeMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
name|runTimeSeconds
operator|*
literal|1000
operator|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Iterable
argument_list|<
name|ConcurrentTestCommand
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|ConcurrentTestCommand
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|TimedIterator
argument_list|<
name|ConcurrentTestCommand
argument_list|>
argument_list|(
name|getCommands
argument_list|(
name|threadId
argument_list|)
argument_list|,
name|endTimeMillis
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Outputs command sequence and notes how long the sequence will be    * repeated.    */
name|void
name|printCommands
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|Integer
name|threadId
parameter_list|)
block|{
name|super
operator|.
name|printCommands
argument_list|(
name|out
argument_list|,
name|threadId
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Repeat sequence for "
operator|+
name|runTimeSeconds
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * TimedIterator is an Iterator that repeats a given collection's elements    * until<code>System.currentTimeMillis()>= endTimeMillis</code>.    */
specifier|private
class|class
name|TimedIterator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|commands
decl_stmt|;
specifier|private
name|long
name|endTimeMillis
decl_stmt|;
specifier|private
name|int
name|commandIndex
decl_stmt|;
specifier|private
name|TimedIterator
parameter_list|(
name|Collection
argument_list|<
name|E
argument_list|>
name|commands
parameter_list|,
name|long
name|endTimeMillis
parameter_list|)
block|{
name|this
operator|.
name|commands
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|this
operator|.
name|endTimeMillis
operator|=
name|endTimeMillis
expr_stmt|;
name|this
operator|.
name|commandIndex
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|commandIndex
operator|<
name|commands
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTimeMillis
condition|)
block|{
name|commandIndex
operator|=
literal|0
expr_stmt|;
return|return
name|commands
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
comment|// handle empty array
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|E
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|commands
operator|.
name|get
argument_list|(
name|commandIndex
operator|++
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConcurrentTestTimedCommandGenerator.java
end_comment

end_unit

