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
comment|/**  * TupleIter provides an Iterator-like interface for reading tuple data.  *  *<p>TupleIter replaces the combination of {@link java.util.Iterator} and  * {@link org.eigenbase.runtime.RestartableIterator}.  *  *<p>Note that calling {@link ClosableAllocation#closeAllocation()  * closeAllocation()} closes this iterator, allowing it to release its  * resources. No further calls to {@link TupleIter#fetchNext} or {@link  * TupleIter#restart} may be made once the iterator is closed.  *  * @author Stephan Zuercher  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|TupleIter
extends|extends
name|ClosableAllocation
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|TupleIter
name|EMPTY_ITERATOR
init|=
operator|new
name|TupleIter
argument_list|()
block|{
specifier|public
name|Object
name|fetchNext
parameter_list|()
block|{
return|return
name|NoDataReason
operator|.
name|END_OF_DATA
return|;
block|}
specifier|public
name|boolean
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|,
name|boolean
name|asUnderflow
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|addListener
parameter_list|(
name|MoreDataListener
name|c
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
block|}
specifier|public
name|StringBuilder
name|printStatus
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
return|return
name|b
return|;
block|}
specifier|public
name|void
name|closeAllocation
parameter_list|()
block|{
block|}
block|}
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**      * NoDataReason provides a reason why no data was returned by a call to      * {@link TupleIter#fetchNext}.      */
specifier|public
enum|enum
name|NoDataReason
block|{
comment|/**          * End of data. No more data will be returned unless the iterator is          * reset by a call to {@link TupleIter#restart()}.          */
name|END_OF_DATA
block|,
comment|/**          * Data underflow. No more data will be returned until the underlying          * data source provides more input rows.          */
name|UNDERFLOW
block|,     }
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the next element in the iteration. If there is no next value, it      * returns a value from the {@link NoDataReason} enumeration indicating why      * no data was returned.      *      *<p>If this method returns {@link NoDataReason#END_OF_DATA}, no further      * data will be returned by this iterator unless {@link TupleIter#restart()}      * is called.      *      *<p>If this method returns {@link NoDataReason#UNDERFLOW}, no data is      * currently available, but may be come available in the future. It is      * possible for consecutive calls to return UNDERFLOW and then END_OF_DATA.      *      *<p>The object returned by this method may be re-used for each subsequent      * call to<code>fetchNext()</code>. In other words, callers must either      * make certain that the returned value is no longer needed or is copied      * before any subsequent calls to<code>fetchNext()</code>.      *      * @return the next element in the iteration, or an instance of {@link      * NoDataReason}.      */
specifier|public
name|Object
name|fetchNext
parameter_list|()
function_decl|;
comment|// REVIEW mberkowitz 27-Nov-2008 Is this too contrived? Intended to support
comment|// FarragoTransform.execute() in data-push mode.
comment|/**      * Sets a timeout for {@link TupleIter#fetchNext}; (optional operation). Not      * all implementing classes support a timeout. For those that do, this      * method provides a common interface, For those that do not, the adapter      * {@link TimeoutQueueTupleIter} puts a timeout queue on top.      *      * @param timeout in milliseconds. 0 means poll, infinity means block.      * @param asUnderflow true means indicate timeout by returning {@link      * NoDataReason#UNDERFLOW}; false means throw {@link      * TupleIter.TimeoutException} on a timeout.      *      * @return true if the timeout was set, false if the implementing class does      * not support a timeout.      */
specifier|public
name|boolean
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|,
name|boolean
name|asUnderflow
parameter_list|)
function_decl|;
comment|/**      * Registers a request to be notified when data next available.      * Useful after UNDERFLOW for a push-mode reader.      * @return true if the request was accepted,      *   false if notice is not avaiable.      */
specifier|public
name|boolean
name|addListener
parameter_list|(
name|MoreDataListener
name|c
parameter_list|)
function_decl|;
comment|/**      * Restarts this iterator, so that a subsequent call to {@link      * TupleIter#fetchNext()} returns the first element in the collection being      * iterated.      */
specifier|public
name|void
name|restart
parameter_list|()
function_decl|;
comment|/**      * Prints the state of the iterator.      * (Avoids constructing a String; uses no locks.)      * @param b target      * @return b      */
specifier|public
name|StringBuilder
name|printStatus
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
function_decl|;
comment|/**      * One way to indicate that {@link TupleIter#fetchNext} timed-out. The other      * way is to return {@link NoDataReason#UNDERFLOW}. See {@link      * TupleIter#setTimeout}. Since throwing this exception is optional for      * fetchNext, it is a RuntimeException.      */
specifier|public
specifier|static
class|class
name|TimeoutException
extends|extends
name|RuntimeException
block|{     }
comment|/**      * A callback, called when data is available after UNDERFLOW.      * See {@link TupleIter#addListener}.      */
specifier|public
interface|interface
name|MoreDataListener
block|{
specifier|public
name|void
name|onMoreData
parameter_list|()
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End TupleIter.java
end_comment

end_unit

