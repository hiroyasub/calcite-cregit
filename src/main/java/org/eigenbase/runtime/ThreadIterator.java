begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>ThreadIterator</code> converts 'push' code to 'pull'. You implement  * {@link #doWork} to call {@link #put} with each row, and this class invokes it  * in a separate thread. Then the results come out via the familiar {@link  * Iterator} interface. For example,  *  *<blockquote>  *<pre>class ArrayIterator extends ThreadIterator {  *   Object[] a;  *   ArrayIterator(Object[] a) {  *     this.a = a;  *     start();  *   }  *   protected void doWork() {  *     for (int i = 0; i< a.length; i++) {  *       put(a[i]);  *     }  *   }  * }</pre>  *</blockquote>  *  * Or, more typically, using an anonymous class:  *  *<blockquote>  *<pre>Iterator i = new ThreadIterator() {  *   int limit;  *   public ThreadIterator start(int limit) {  *     this.limit = limit;  *     return super.start();  *   }  *   protected void doWork() {  *     for (int i = 0; i< limit; i++) {  *       put(new Integer(i));  *     }  *   }  * }.start(100);  * while (i.hasNext()) {  *<em>etc.</em>  * }</pre>  *</blockquote>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ThreadIterator
extends|extends
name|QueueIterator
implements|implements
name|Iterator
implements|,
name|Runnable
implements|,
name|Iterable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|Thread
name|thread
decl_stmt|;
specifier|private
name|String
name|threadName
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ThreadIterator
parameter_list|()
block|{
block|}
specifier|public
name|ThreadIterator
parameter_list|(
name|BlockingQueue
name|queue
parameter_list|)
block|{
name|super
argument_list|(
literal|1
argument_list|,
literal|null
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|setThreadName
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|threadName
operator|=
name|s
expr_stmt|;
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
name|thread
operator|.
name|setName
argument_list|(
name|threadName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement Iterable
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|start
argument_list|()
return|;
block|}
comment|// implement Runnable
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|calledDone
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doWork
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|done
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|calledDone
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|calledDone
condition|)
block|{
name|done
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * The implementation should call {@link #put} with each row.      */
specifier|protected
specifier|abstract
name|void
name|doWork
parameter_list|()
function_decl|;
specifier|protected
name|ThreadIterator
name|start
parameter_list|()
block|{
assert|assert
operator|(
name|thread
operator|==
literal|null
operator|)
assert|;
name|thread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadName
operator|!=
literal|null
condition|)
block|{
name|thread
operator|.
name|setName
argument_list|(
name|threadName
argument_list|)
expr_stmt|;
block|}
comment|// Make the thread a daemon so that we don't have to worry
comment|// about cleaning it up.  This is important since we can't
comment|// be guaranteed that onClose will get called (someone
comment|// may create an iterator and then forget about it), so
comment|// requiring a join() call would be a bad idea.  Of course,
comment|// if someone does forget about it, and the producer
comment|// thread gets stuck on a full queue, it will never exit,
comment|// and will become a resource leak.
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// implement QueueIterator
specifier|protected
name|void
name|onEndOfQueue
parameter_list|()
block|{
name|thread
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ThreadIterator.java
end_comment

end_unit

