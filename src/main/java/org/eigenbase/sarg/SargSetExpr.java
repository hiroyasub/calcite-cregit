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
name|sarg
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
name|reltype
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
name|rex
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
comment|/**  * SargSetExpr represents the application of a {@link SargSetOperator set  * operator} to zero or more child {@link SargExpr sarg expressions}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SargSetExpr
implements|implements
name|SargExpr
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SargFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|dataType
decl_stmt|;
specifier|private
specifier|final
name|SargSetOperator
name|setOp
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SargExpr
argument_list|>
name|children
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * @see SargFactory#newSetExpr      */
name|SargSetExpr
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|RelDataType
name|dataType
parameter_list|,
name|SargSetOperator
name|setOp
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|dataType
operator|=
name|dataType
expr_stmt|;
name|this
operator|.
name|setOp
operator|=
name|setOp
expr_stmt|;
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|SargExpr
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * @return a read-only list of this expression's children (the returned      * children themselves are modifiable)      */
specifier|public
name|List
argument_list|<
name|SargExpr
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|children
argument_list|)
return|;
block|}
comment|/**      * Adds a child to this expression.      *      * @param child child to add      */
specifier|public
name|void
name|addChild
parameter_list|(
name|SargExpr
name|child
parameter_list|)
block|{
assert|assert
operator|(
name|child
operator|.
name|getDataType
argument_list|()
operator|==
name|dataType
operator|)
assert|;
if|if
condition|(
name|setOp
operator|==
name|SargSetOperator
operator|.
name|COMPLEMENT
condition|)
block|{
assert|assert
operator|(
name|children
operator|.
name|isEmpty
argument_list|()
operator|)
assert|;
block|}
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
comment|// implement SargExpr
specifier|public
name|SargFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
comment|// implement SargExpr
specifier|public
name|RelDataType
name|getDataType
parameter_list|()
block|{
return|return
name|dataType
return|;
block|}
comment|// implement SargExpr
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|setOp
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|SargExpr
name|child
range|:
name|children
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" )"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// implement SargExpr
specifier|public
name|SargIntervalSequence
name|evaluate
parameter_list|()
block|{
if|if
condition|(
name|setOp
operator|==
name|SargSetOperator
operator|.
name|COMPLEMENT
condition|)
block|{
assert|assert
operator|(
name|children
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
assert|;
name|SargExpr
name|child
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|child
operator|.
name|evaluateComplemented
argument_list|()
return|;
block|}
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|list
init|=
name|evaluateChildren
argument_list|(
name|this
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|setOp
condition|)
block|{
case|case
name|UNION
case|:
return|return
name|evaluateUnion
argument_list|(
name|list
argument_list|)
return|;
case|case
name|INTERSECTION
case|:
return|return
name|evaluateIntersection
argument_list|(
name|list
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|setOp
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|evaluateChildren
parameter_list|(
name|SargSetExpr
name|setExpr
parameter_list|)
block|{
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SargIntervalSequence
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SargExpr
name|child
range|:
name|setExpr
operator|.
name|children
control|)
block|{
name|SargIntervalSequence
name|newSeq
init|=
name|child
operator|.
name|evaluate
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|newSeq
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|// implement SargExpr
specifier|public
name|void
name|collectDynamicParams
parameter_list|(
name|Set
argument_list|<
name|RexDynamicParam
argument_list|>
name|dynamicParams
parameter_list|)
block|{
for|for
control|(
name|SargExpr
name|child
range|:
name|children
control|)
block|{
name|child
operator|.
name|collectDynamicParams
argument_list|(
name|dynamicParams
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|SargIntervalSequence
name|evaluateUnion
parameter_list|(
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|list
parameter_list|)
block|{
name|SargIntervalSequence
name|seq
init|=
operator|new
name|SargIntervalSequence
argument_list|()
decl_stmt|;
comment|// Toss all entries from each sequence in the list into one big sorted
comment|// set.
name|SortedSet
argument_list|<
name|SargInterval
argument_list|>
name|intervals
init|=
operator|new
name|TreeSet
argument_list|<
name|SargInterval
argument_list|>
argument_list|(
operator|new
name|IntervalComparator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SargIntervalSequence
name|childSeq
range|:
name|list
control|)
block|{
name|intervals
operator|.
name|addAll
argument_list|(
name|childSeq
operator|.
name|getList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Now, overlapping ranges are consecutive in the set.  Merge them by
comment|// increasing the upper bound of the first; discard the others.  In the
comment|// example, [4, 6] and [5, 7) are combined to form [4, 7).  (7, 8] is
comment|// not merged with the new range because neither range contains the
comment|// value 7.
comment|//
comment|// Input:
comment|//          1  2  3  4  5  6  7  8  9
comment|// 1 [1, 3] [-----]
comment|// 2 [4, 6]          [-----]
comment|// 3 [5, 7)             [-----)
comment|// 4 (7, 8]                   (--]
comment|//
comment|// Output:
comment|// 1 [1, 3] [-----]
comment|// 2 [4, 7)          [--------)
comment|// 3 (7, 8]                   (--]
name|SargInterval
name|accumulator
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SargInterval
name|interval
range|:
name|intervals
control|)
block|{
comment|// Empty intervals should have been previously filtered out.
assert|assert
operator|(
operator|!
name|interval
operator|.
name|isEmpty
argument_list|()
operator|)
assert|;
if|if
condition|(
name|accumulator
operator|==
literal|null
condition|)
block|{
comment|// The very first interval:  start accumulating.
name|accumulator
operator|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
expr_stmt|;
name|accumulator
operator|.
name|copyFrom
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|seq
operator|.
name|addInterval
argument_list|(
name|accumulator
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|accumulator
operator|.
name|contains
argument_list|(
name|interval
argument_list|)
condition|)
block|{
comment|// Just drop new interval because it's already covered
comment|// by accumulator.
continue|continue;
block|}
comment|// Test for overlap.
name|int
name|c
init|=
name|interval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|accumulator
operator|.
name|getUpperBound
argument_list|()
argument_list|)
decl_stmt|;
comment|// If no overlap, test for touching instead.
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|interval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|isTouching
argument_list|(
name|accumulator
operator|.
name|getUpperBound
argument_list|()
argument_list|)
condition|)
block|{
comment|// Force test below to pass.
name|c
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|c
operator|<=
literal|0
condition|)
block|{
comment|// Either touching or overlap:  grow the accumulator.
name|accumulator
operator|.
name|upperBound
operator|.
name|copyFrom
argument_list|(
name|interval
operator|.
name|getUpperBound
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Disjoint:  start accumulating a new interval
name|accumulator
operator|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
expr_stmt|;
name|accumulator
operator|.
name|copyFrom
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|seq
operator|.
name|addInterval
argument_list|(
name|accumulator
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|seq
return|;
block|}
specifier|private
name|SargIntervalSequence
name|evaluateIntersection
parameter_list|(
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|list
parameter_list|)
block|{
name|SargIntervalSequence
name|seq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Counterintuitive but true: intersection of no sets is the
comment|// universal set (kinda like 2^0=1).  One way to prove this to
comment|// yourself is to apply DeMorgan's law.  The union of no sets is
comment|// certainly the empty set.  So the complement of that union is the
comment|// universal set.  That's equivalent to the intersection of the
comment|// complements of no sets, which is the intersection of no sets.
comment|// QED.
name|seq
operator|=
operator|new
name|SargIntervalSequence
argument_list|()
expr_stmt|;
name|seq
operator|.
name|addInterval
argument_list|(
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|seq
return|;
block|}
comment|// The way we evaluate the intersection is to start with the first
comment|// entry as a baseline, and then keep deleting stuff from it by
comment|// intersecting the other entrie in turn.  Whatever makes it through
comment|// this filtering remains as the final result.
for|for
control|(
name|SargIntervalSequence
name|newSeq
range|:
name|list
control|)
block|{
if|if
condition|(
name|seq
operator|==
literal|null
condition|)
block|{
comment|// first child
name|seq
operator|=
name|newSeq
expr_stmt|;
continue|continue;
block|}
name|intersectSequences
argument_list|(
name|seq
argument_list|,
name|newSeq
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
specifier|private
name|void
name|intersectSequences
parameter_list|(
name|SargIntervalSequence
name|targetSeq
parameter_list|,
name|SargIntervalSequence
name|sourceSeq
parameter_list|)
block|{
name|ListIterator
argument_list|<
name|SargInterval
argument_list|>
name|targetIter
init|=
name|targetSeq
operator|.
name|list
operator|.
name|listIterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|targetIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// No target intervals at all, so quit.
return|return;
block|}
name|ListIterator
argument_list|<
name|SargInterval
argument_list|>
name|sourceIter
init|=
name|sourceSeq
operator|.
name|list
operator|.
name|listIterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|sourceIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// No source intervals at all, so result is empty
name|targetSeq
operator|.
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// Start working on first source and target intervals
name|SargInterval
name|target
init|=
name|targetIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|SargInterval
name|source
init|=
name|sourceIter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// loop invariant:  both source and target are non-null on entry
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|target
operator|.
name|getLowerBound
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Source is completely below target; discard it and
comment|// move on to next one.
if|if
condition|(
operator|!
name|sourceIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// No more sources.
break|break;
block|}
name|source
operator|=
name|sourceIter
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|target
operator|.
name|getUpperBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|source
operator|.
name|getLowerBound
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Target is completely below source; discard it and
comment|// move on to next one.
name|targetIter
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|targetIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// All done.
return|return;
block|}
name|target
operator|=
name|targetIter
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|// Overlap case:  perform intersection of the two intervals.
if|if
condition|(
name|source
operator|.
name|getLowerBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|target
operator|.
name|getLowerBound
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// Source starts after target starts, so trim the target.
name|target
operator|.
name|setLower
argument_list|(
name|source
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|source
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getStrictness
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|c
init|=
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|target
operator|.
name|getUpperBound
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// The source ends before the target ends, so split the target
comment|// into two parts.  The first part will be kept for sure; the
comment|// second part will be compared against further source ranges.
name|SargInterval
name|newTarget
init|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|dataType
argument_list|)
decl_stmt|;
name|newTarget
operator|.
name|setLower
argument_list|(
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getStrictnessComplement
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|.
name|getUpperBound
argument_list|()
operator|.
name|isFinite
argument_list|()
condition|)
block|{
name|newTarget
operator|.
name|setUpper
argument_list|(
name|target
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|target
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getStrictness
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Trim current target to exclude the part of the range
comment|// which will move to newTarget.
name|target
operator|.
name|setUpper
argument_list|(
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|source
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getStrictness
argument_list|()
argument_list|)
expr_stmt|;
comment|// Insert newTarget after target.  This makes newTarget
comment|// into previous().
name|targetIter
operator|.
name|add
argument_list|(
name|newTarget
argument_list|)
expr_stmt|;
comment|// Next time through, work on newTarget.
comment|// targetIter.previous() is pointing at the newTarget.
name|target
operator|=
name|targetIter
operator|.
name|previous
argument_list|()
expr_stmt|;
comment|// Now targetIter.next() is also pointing at the newTarget;
comment|// need to do this redundant step to get targetIter in sync
comment|// with target.
name|target
operator|=
name|targetIter
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Advance source.
if|if
condition|(
operator|!
name|sourceIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|source
operator|=
name|sourceIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
comment|// Source and target ends coincide, so advance both source and
comment|// target.
if|if
condition|(
operator|!
name|targetIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
name|target
operator|=
name|targetIter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|sourceIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|source
operator|=
name|sourceIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Source ends after target ends, so advance target.
assert|assert
operator|(
name|c
operator|>
literal|0
operator|)
assert|;
if|if
condition|(
operator|!
name|targetIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
name|target
operator|=
name|targetIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Discard any remaining targets since they didn't have corresponding
comment|// sources.
for|for
control|(
init|;
condition|;
control|)
block|{
name|targetIter
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|targetIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|targetIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
comment|// implement SargExpr
specifier|public
name|SargIntervalSequence
name|evaluateComplemented
parameter_list|()
block|{
if|if
condition|(
name|setOp
operator|==
name|SargSetOperator
operator|.
name|COMPLEMENT
condition|)
block|{
comment|// Double negation is a nop
return|return
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|evaluate
argument_list|()
return|;
block|}
comment|// Use DeMorgan's Law:  complement of union is intersection of
comment|// complements, and vice versa
name|List
argument_list|<
name|SargIntervalSequence
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SargIntervalSequence
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SargExpr
name|child
range|:
name|children
control|)
block|{
name|SargIntervalSequence
name|newSeq
init|=
name|child
operator|.
name|evaluateComplemented
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|newSeq
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|setOp
condition|)
block|{
case|case
name|INTERSECTION
case|:
return|return
name|evaluateUnion
argument_list|(
name|list
argument_list|)
return|;
case|case
name|UNION
case|:
return|return
name|evaluateIntersection
argument_list|(
name|list
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|setOp
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Comparator used in evaluateUnionOp. Intervals collate based on      * {lowerBound, upperBound}.      */
specifier|private
specifier|static
class|class
name|IntervalComparator
implements|implements
name|Comparator
argument_list|<
name|SargInterval
argument_list|>
block|{
name|IntervalComparator
parameter_list|()
block|{
block|}
comment|// implement Comparator
specifier|public
name|int
name|compare
parameter_list|(
name|SargInterval
name|i1
parameter_list|,
name|SargInterval
name|i2
parameter_list|)
block|{
name|int
name|c
init|=
name|i1
operator|.
name|getLowerBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|i2
operator|.
name|getLowerBound
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|i1
operator|.
name|getUpperBound
argument_list|()
operator|.
name|compareTo
argument_list|(
name|i2
operator|.
name|getUpperBound
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|IntervalComparator
operator|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SargSetExpr.java
end_comment

end_unit

