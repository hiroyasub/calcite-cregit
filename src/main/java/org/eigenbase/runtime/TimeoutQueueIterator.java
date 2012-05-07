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
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|// REVIEW: SWZ: 7/13/2006: In principal this class also exhibits the same bug
end_comment

begin_comment
comment|// that was fixed in //open/dt/dev/.../TimeoutQueueTupleIter#2.  It doesn't
end_comment

begin_comment
comment|// occur because this class is no longer used with row objects.  It is still
end_comment

begin_comment
comment|// used for "explain plan" but since the output there is immutable Strings,
end_comment

begin_comment
comment|// and because the query timeout isn't propagated to the result set used,
end_comment

begin_comment
comment|// the bug doesn't occur.  Leaving it unfixed here, since the pattern used to
end_comment

begin_comment
comment|// fix TimeoutQueueTupleIter is hard to apply to this class's Iterator
end_comment

begin_comment
comment|// calling convention.  Prehaps we should migrate explain plan to TupleIter
end_comment

begin_comment
comment|// convention and eliminate this class altogether.
end_comment

begin_comment
comment|/**  * Adapter which allows you to iterate over an {@link Iterator} with a timeout.  *  *<p>The interface is similar to an {@link Iterator}: the {@link #hasNext}  * method tests whether there are more rows, and the {@link #next} method gets  * the next row. Each has a timeout parameter, and throws a {@link  * QueueIterator.TimeoutException} if the timeout is exceeded. There is also a  * {@link #close} method, which you must call.  *  *<p>The class is implemented using a thread which reads from the underlying  * iterator and places the results into a {@link QueueIterator}. If a method  * call times out, the underlying thread will wait for the result of the call  * until it completes.  *  *<p>There is no facility to cancel the fetch from the underlying iterator.  *  * @author tleung  * @version $Id$  * @since Jun 20, 2004  * @testcase  */
end_comment

begin_class
specifier|public
class|class
name|TimeoutQueueIterator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|QueueIterator
name|queueIterator
decl_stmt|;
comment|// only protected to suit
comment|// QueueIteratorTest
specifier|private
specifier|final
name|Iterator
name|producer
decl_stmt|;
specifier|private
name|Thread
name|thread
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|TimeoutQueueIterator
parameter_list|(
name|Iterator
name|producer
parameter_list|)
block|{
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
name|this
operator|.
name|queueIterator
operator|=
operator|new
name|QueueIterator
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns whether the producer has another row, if that can be determined      * within the timeout interval.      *      * @param timeoutMillis Millisonds to wait; less than or equal to zero means      * don't wait      *      * @throws QueueIterator.TimeoutException if producer does not answer within      * the timeout interval      */
specifier|public
name|boolean
name|hasNext
parameter_list|(
name|long
name|timeoutMillis
parameter_list|)
throws|throws
name|QueueIterator
operator|.
name|TimeoutException
block|{
return|return
name|queueIterator
operator|.
name|hasNext
argument_list|(
name|timeoutMillis
argument_list|)
return|;
block|}
comment|/**      * Returns the next row from the producer, if it can be fetched within the      * timeout interval.      *      * @throws QueueIterator.TimeoutException if producer does not answer within      * the timeout interval      */
specifier|public
name|Object
name|next
parameter_list|(
name|long
name|timeoutMillis
parameter_list|)
throws|throws
name|QueueIterator
operator|.
name|TimeoutException
block|{
return|return
name|queueIterator
operator|.
name|next
argument_list|(
name|timeoutMillis
argument_list|)
return|;
block|}
comment|/**      * Starts the thread which reads from the consumer.      *      * @pre thread == null // not previously started      */
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|Util
operator|.
name|pre
argument_list|(
name|thread
operator|==
literal|null
argument_list|,
literal|"thread == null"
argument_list|)
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|doWork
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"TimeoutQueueIterator"
operator|+
name|thread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Releases the resources used by this iterator, including killing the      * underlying thread.      *      * @param timeoutMillis Timeout while waiting for the underlying thread to      * die. Zero means wait forever.      */
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|long
name|timeoutMillis
parameter_list|)
block|{
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Empty the queue -- the thread will wait for us to consume
comment|// all items in the queue, hanging the join call.
while|while
condition|(
name|queueIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|queueIterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|thread
operator|.
name|join
argument_list|(
name|timeoutMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
name|thread
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Reads objects from the producer and writes them into the QueueIterator.      * This is the method called by the thread when you call {@link #start}.      * Never throws an exception.      */
specifier|private
name|void
name|doWork
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|producer
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Object
name|o
init|=
name|producer
operator|.
name|next
argument_list|()
decl_stmt|;
name|queueIterator
operator|.
name|put
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
comment|// Signal that the stream ended without error.
name|queueIterator
operator|.
name|done
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Signal that the stream ended with an error.
name|queueIterator
operator|.
name|done
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TimeoutQueueIterator.java
end_comment

end_unit

