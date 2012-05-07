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
name|sql
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

begin_class
specifier|public
specifier|abstract
class|class
name|TupleIterResultSet
extends|extends
name|AbstractIterResultSet
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|TupleIter
name|tupleIter
decl_stmt|;
specifier|private
name|TimeoutQueueTupleIter
name|timeoutTupleIter
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a result set based upon an iterator. The column-getter accesses      * columns based upon their ordinal.      *      * @pre tupleIter != null      */
specifier|public
name|TupleIterResultSet
parameter_list|(
name|TupleIter
name|tupleIter
parameter_list|,
name|ColumnGetter
name|columnGetter
parameter_list|)
block|{
name|super
argument_list|(
name|columnGetter
argument_list|)
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|tupleIter
operator|!=
literal|null
argument_list|,
literal|"tupleIter != null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|tupleIter
operator|=
name|tupleIter
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Sets the timeout that this TupleIterResultSet will wait for a row from      * the underlying iterator.      *      * @param timeoutMillis Timeout in milliseconds. Must be greater than zero.      */
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeoutMillis
parameter_list|)
block|{
name|super
operator|.
name|setTimeout
argument_list|(
name|timeoutMillis
argument_list|)
expr_stmt|;
assert|assert
name|timeoutTupleIter
operator|==
literal|null
assert|;
comment|// we create a new semaphore for each executeQuery call
comment|// and then pass ownership to the result set returned
comment|// the query timeout used is the last set via JDBC.
name|timeoutTupleIter
operator|=
operator|new
name|TimeoutQueueTupleIter
argument_list|(
name|tupleIter
argument_list|)
expr_stmt|;
name|timeoutTupleIter
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|timeoutTupleIter
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|noTimeout
init|=
literal|0
decl_stmt|;
name|timeoutTupleIter
operator|.
name|closeAllocation
argument_list|(
name|noTimeout
argument_list|)
expr_stmt|;
name|timeoutTupleIter
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------------------
comment|// the remaining methods implement ResultSet
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|maxRows
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|row
operator|>=
name|maxRows
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
try|try
block|{
name|Object
name|next
init|=
operator|(
name|timeoutTupleIter
operator|!=
literal|null
operator|)
condition|?
name|timeoutTupleIter
operator|.
name|fetchNext
argument_list|(
name|timeoutMillis
argument_list|)
else|:
name|tupleIter
operator|.
name|fetchNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
name|TupleIter
operator|.
name|NoDataReason
operator|.
name|END_OF_DATA
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|next
operator|instanceof
name|TupleIter
operator|.
name|NoDataReason
condition|)
block|{
comment|// TODO: SWZ: 2/23/2006: better exception
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
name|current
operator|=
name|next
expr_stmt|;
name|row
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|QueueIterator
operator|.
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SqlTimeoutException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|newFetchError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TupleIterResultSet.java
end_comment

end_unit

