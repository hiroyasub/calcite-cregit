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

begin_comment
comment|// REVIEW mberkowitz 1-Nov-2008. This adapter is used only to present a
end_comment

begin_comment
comment|// FarragoJavaUdxIterator as a TupleIter. Redundant since a
end_comment

begin_comment
comment|// FarragoJavaUdxIterator can be a TupleIter itself, and provide a correct,
end_comment

begin_comment
comment|// non-blocking fetchNext(). However some farrago queries depend on fetchNext()
end_comment

begin_comment
comment|// to block: eg in unitsql/expressions/udfInvocation.sql, SELECT * FROM
end_comment

begin_comment
comment|// TABLE(RAMP(5)) ORDER BY 1;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Consequently, I've made FarragoJavaUdxIterator implement TupleIter as well as
end_comment

begin_comment
comment|// RestartableIterator, but as a kludge I've retained this adapter for farrago
end_comment

begin_comment
comment|// queries.
end_comment

begin_comment
comment|/**  *<code>RestartableIteratorTupleIter</code> adapts an underlying {@link  * RestartableIterator} as a {@link TupleIter}. It is an imperfect adaptor;  * {@link #fetchNext} blocks when a real TupleIter would return {@link  * TupleIter.NoDataReason#UNDERFLOW}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RestartableIteratorTupleIter
extends|extends
name|AbstractTupleIter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RestartableIterator
name|iterator
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RestartableIteratorTupleIter
parameter_list|(
name|RestartableIterator
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement TupleIter
specifier|public
name|Object
name|fetchNext
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// blocks
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
return|return
name|NoDataReason
operator|.
name|END_OF_DATA
return|;
block|}
comment|// implement TupleIter
specifier|public
name|void
name|restart
parameter_list|()
block|{
name|iterator
operator|.
name|restart
argument_list|()
expr_stmt|;
block|}
comment|// implement TupleIter
specifier|public
name|void
name|closeAllocation
parameter_list|()
block|{
block|}
block|}
end_class

begin_comment
comment|// End RestartableIteratorTupleIter.java
end_comment

end_unit

