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
name|java
operator|.
name|util
operator|.
name|logging
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
name|trace
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
comment|/**  *<code>CompoundIterator</code> creates an iterator out of several.  * CompoundIterator is serial: it yields all the elements of its first input  * Iterator, then all those of its second input, etc. When all inputs are  * exhausted, it is done.  *  *<p>NOTE jvs 21-Mar-2006: This class is no longer used except by Saffron, but  * is generally useful. Should probably be moved to a utility package.  */
end_comment

begin_class
specifier|public
class|class
name|CompoundIterator
implements|implements
name|RestartableIterator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Logger
name|tracer
init|=
name|EigenbaseTrace
operator|.
name|getCompoundIteratorTracer
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|Iterator
name|iterator
decl_stmt|;
specifier|private
name|Iterator
index|[]
name|iterators
decl_stmt|;
specifier|private
name|int
name|i
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|CompoundIterator
parameter_list|(
name|Iterator
index|[]
name|iterators
parameter_list|)
block|{
name|this
operator|.
name|iterators
operator|=
name|iterators
expr_stmt|;
name|this
operator|.
name|i
operator|=
literal|0
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
name|tracer
operator|.
name|finer
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
return|return
name|nextIterator
argument_list|()
return|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|nextIterator
argument_list|()
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
name|tracer
operator|.
name|finer
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
name|nextIterator
argument_list|()
expr_stmt|;
block|}
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|tracer
operator|.
name|finer
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
name|nextIterator
argument_list|()
expr_stmt|;
block|}
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// moves to the next child iterator, skipping any empty ones, and returns
comment|// true. when all the child iteratators are used up, return false;
specifier|private
name|boolean
name|nextIterator
parameter_list|()
block|{
while|while
condition|(
name|i
operator|<
name|iterators
operator|.
name|length
condition|)
block|{
name|iterator
operator|=
name|iterators
index|[
name|i
operator|++
index|]
expr_stmt|;
name|tracer
operator|.
name|fine
argument_list|(
literal|"try "
operator|+
name|iterator
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
name|tracer
operator|.
name|fine
argument_list|(
literal|"exhausted iterators"
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// implement RestartableIterator
specifier|public
name|void
name|restart
parameter_list|()
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
operator|++
name|j
control|)
block|{
name|Util
operator|.
name|restartIterator
argument_list|(
name|iterators
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
literal|0
expr_stmt|;
name|iterator
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CompoundIterator.java
end_comment

end_unit

