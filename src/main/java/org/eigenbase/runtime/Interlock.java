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

begin_comment
comment|/**  * A synchronization primitive which allows a producer and a consumer to use the  * same resources without treading on each other's feet.  *  *<p>At most one of the producer and consumer has access at a time. The  * synchronization ensures that the call sequence is as follows:  *  *<ul><li{@link #beginWriting()} (called by producer)<li{@link #endWriting()}  * (called by producer)<li{@link #beginReading()} (called by consumer)<li  * {@link #endReading()} (called by consumer)  *</ul>  *  *<p>{@link ExclusivePipe} is a simple extension to this class containing a  * {@link java.nio.ByteBuffer} as the shared resource.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|Interlock
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * The producer notifies<code>empty</code> every time it finishes writing.      * The consumer waits for it.      */
specifier|private
specifier|final
name|Semaphore
name|empty
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * The consumer notifies<code>full</code> every time it finishes reading.      * The producer waits for it, then starts work.      */
specifier|private
specifier|final
name|Semaphore
name|full
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|Interlock
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Acquires the buffer, in preparation for writing.      *      *<p>The producer should call this method. After this call completes, the      * consumer's call to {@link #beginReading()} will block until the producer      * has called {@link #endWriting()}.      */
specifier|public
name|void
name|beginWriting
parameter_list|()
block|{
name|full
operator|.
name|acquire
argument_list|()
expr_stmt|;
comment|// wait for consumer thread to use previous
block|}
comment|/**      * Releases the buffer after writing.      *      *<p>The producer should call this method. After this call completes, the      * producers's call to {@link #beginWriting()} will block until the consumer      * has called {@link #beginReading()} followed by {@link #endReading()}.      */
specifier|public
name|void
name|endWriting
parameter_list|()
block|{
name|empty
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// wake up consumer
block|}
comment|/**      * Acquires the buffer, in preparation for reading.      *      *<p>After this call completes, the producer's call to {@link      * #beginWriting()} will block until the consumer has called {@link      * #endReading()}.      */
specifier|public
name|void
name|beginReading
parameter_list|()
block|{
name|empty
operator|.
name|acquire
argument_list|()
expr_stmt|;
comment|// wait for producer to produce one
block|}
comment|/**      * Releases the buffer after reading its contents.      *      *<p>The consumer should call this method. After this call completes, the      * consumer's call to {@link #beginReading()} will block until the producer      * has called {@link #beginWriting()} followed by {@link #endWriting()}.      */
specifier|public
name|void
name|endReading
parameter_list|()
block|{
name|full
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// wake up producer
block|}
block|}
end_class

begin_comment
comment|// End Interlock.java
end_comment

end_unit

