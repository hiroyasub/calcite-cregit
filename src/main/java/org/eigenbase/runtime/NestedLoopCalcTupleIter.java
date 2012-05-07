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
comment|/**  *<code>NestedLoopCalcTupleIter</code> is a specialization of {@link  * CalcTupleIter} for use in implementing nested loop inner joins over  * iterators.  *  *<p>REVIEW jvs 20-Mar-2004: I have parameterized this to handle inner and left  * outer joins, as well as one-to-many and many-to-one variants. This comes at  * the price of some efficiency. It would probably be better to write  * specialized bases for each purpose.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NestedLoopCalcTupleIter
extends|extends
name|CalcTupleIter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|Object
name|rightIterator
decl_stmt|;
specifier|protected
name|Object
name|leftObj
decl_stmt|;
specifier|protected
name|Object
name|rightObj
decl_stmt|;
specifier|private
name|boolean
name|isOpen
decl_stmt|;
specifier|private
name|boolean
name|isLeftOuter
decl_stmt|;
specifier|private
name|boolean
name|needNullRow
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|NestedLoopCalcTupleIter
parameter_list|(
name|TupleIter
name|leftIterator
parameter_list|,
name|boolean
name|isLeftOuter
parameter_list|)
block|{
name|super
argument_list|(
name|leftIterator
argument_list|)
expr_stmt|;
name|this
operator|.
name|isLeftOuter
operator|=
name|isLeftOuter
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
operator|!
name|isOpen
condition|)
block|{
name|isOpen
operator|=
literal|true
expr_stmt|;
name|open
argument_list|()
expr_stmt|;
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|leftObj
operator|==
literal|null
condition|)
block|{
name|Object
name|next
init|=
name|inputIterator
operator|.
name|fetchNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|NoDataReason
condition|)
block|{
return|return
name|next
return|;
block|}
name|leftObj
operator|=
name|next
expr_stmt|;
block|}
if|if
condition|(
name|rightIterator
operator|==
literal|null
condition|)
block|{
name|rightIterator
operator|=
name|getNextRightIterator
argument_list|()
expr_stmt|;
name|needNullRow
operator|=
name|isLeftOuter
expr_stmt|;
block|}
if|if
condition|(
name|rightIterator
operator|instanceof
name|TupleIter
condition|)
block|{
name|TupleIter
name|ri
init|=
operator|(
name|TupleIter
operator|)
name|rightIterator
decl_stmt|;
name|Object
name|next
init|=
name|ri
operator|.
name|fetchNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
name|NoDataReason
operator|.
name|END_OF_DATA
condition|)
block|{
if|if
condition|(
name|needNullRow
condition|)
block|{
name|needNullRow
operator|=
literal|false
expr_stmt|;
return|return
name|calcRightNullRow
argument_list|()
return|;
block|}
name|leftObj
operator|=
literal|null
expr_stmt|;
name|rightObj
operator|=
literal|null
expr_stmt|;
name|rightIterator
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
name|rightObj
operator|=
name|next
expr_stmt|;
block|}
else|else
block|{
name|rightObj
operator|=
name|rightIterator
expr_stmt|;
name|rightIterator
operator|=
name|TupleIter
operator|.
name|EMPTY_ITERATOR
expr_stmt|;
block|}
name|Object
name|row
init|=
name|calcJoinRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|row
operator|!=
literal|null
condition|)
block|{
name|needNullRow
operator|=
literal|false
expr_stmt|;
return|return
name|row
return|;
block|}
block|}
block|}
comment|/**      * Method which can be overridden by subclasses to carry out      * post-constructor initialization.      */
specifier|protected
name|void
name|open
parameter_list|()
block|{
block|}
comment|/**      * Method to be implemented by subclasses to determine next right-hand      * iterator based on current value of leftObj. For a many-to-one join, this      * can return the right-hand object directly instead of a TupleIter, but      * should return {@link TupleIter#EMPTY_ITERATOR} for a mismatch.      *      * @return iterator or object      */
specifier|protected
specifier|abstract
name|Object
name|getNextRightIterator
parameter_list|()
function_decl|;
comment|/**      * Method to be implemented by subclasses to either calculate the next      * joined row based on current values of leftObj and rightObj, or else to      * filter out this combination.      *      * @return row or null for filtered oute      */
specifier|protected
specifier|abstract
name|Object
name|calcJoinRow
parameter_list|()
function_decl|;
comment|/**      * Method to be implemented by subclasses to calculate a mismatch row in a      * left outer join. Inner joins can use the default (return null) because it      * will never be called.      *      * @return row with all right fields set to null      */
specifier|protected
name|Object
name|calcRightNullRow
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|//  override CalcTupleIter
specifier|public
name|StringBuilder
name|printStatus
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
name|super
operator|.
name|printStatus
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"left: "
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rightIterator
operator|instanceof
name|TupleIter
condition|)
block|{
operator|(
operator|(
name|TupleIter
operator|)
name|rightIterator
operator|)
operator|.
name|printStatus
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"right: "
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
block|}
end_class

begin_comment
comment|// End NestedLoopCalcTupleIter.java
end_comment

end_unit

