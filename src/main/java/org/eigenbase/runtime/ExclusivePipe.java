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
name|nio
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A synchronization primitive which allows producer and a consumer to use the  * same {@link java.nio.Buffer} without treading on each other's feet.  *  *<p>This class is<em>synchronous</em>: only one of the producer and consumer  * has access at a time. There is only one buffer, and data is not copied.  *  *<p>The byte buffer is fixed in size. The producer writes up to the maximum  * number of bytes into the buffer, then yields. The consumer must read all of  * the data in the buffer before yielding back to the producer.  *  * @author jhyde  * @version $Id$  * @testcase  */
end_comment

begin_class
specifier|public
class|class
name|ExclusivePipe
extends|extends
name|Interlock
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ByteBuffer
name|buf
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ExclusivePipe
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|buf
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the buffer.      */
specifier|public
name|ByteBuffer
name|getBuffer
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
specifier|public
name|void
name|beginWriting
parameter_list|()
block|{
name|super
operator|.
name|beginWriting
argument_list|()
expr_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// don't need to synchronize -- we hold the semaphore
block|}
specifier|public
name|void
name|beginReading
parameter_list|()
block|{
name|super
operator|.
name|beginReading
argument_list|()
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// don't need to synchronize -- we hold the semaphore
block|}
block|}
end_class

begin_comment
comment|// End ExclusivePipe.java
end_comment

end_unit

