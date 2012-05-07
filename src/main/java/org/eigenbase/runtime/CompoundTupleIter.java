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
name|test
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

begin_comment
comment|/**  *<code>CompoundTupleIter</code> creates an iterator out of several iterators.  *  *<p>CompoundTupleIter is serial: it yields all the elements of its first input  * Iterator, then all those of its second input, etc. When all inputs are  * exhausted, it is done. (Cf {@link CompoundParallelTupleIter}.)  *  * @author Stephan Zuercher  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|CompoundTupleIter
implements|implements
name|TupleIter
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
name|TupleIter
name|iterator
decl_stmt|;
specifier|private
name|TupleIter
index|[]
name|iterators
decl_stmt|;
specifier|private
name|int
name|i
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|CompoundTupleIter
parameter_list|(
name|TupleIter
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
name|initIterator
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
name|void
name|initIterator
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|iterators
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|iterator
operator|=
name|this
operator|.
name|iterators
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|iterator
operator|=
name|TupleIter
operator|.
name|EMPTY_ITERATOR
expr_stmt|;
block|}
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
name|iterator
operator|.
name|addListener
argument_list|(
name|c
argument_list|)
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
comment|// try to set a timeout on all underlings, but return false if any
comment|// refused.
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|&=
name|iterators
index|[
name|i
index|]
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|,
name|asUnderflow
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Object
name|fetchNext
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
name|Object
name|next
init|=
name|iterator
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
name|i
operator|++
expr_stmt|;
if|if
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
return|return
name|fetchNext
argument_list|()
return|;
block|}
return|return
name|NoDataReason
operator|.
name|END_OF_DATA
return|;
block|}
return|return
name|next
return|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
comment|// fetchNext() can be called repeatedly after it returns END_OF_DATA.
comment|// Even if it isn't, it uses recursion which implies an extra call
comment|// when END_OF_DATA on the last iterator is reached.
comment|// Each extra call increments i.  We want to restart all iterators
comment|// that we've touched (e.g. 0 to i, inclusive) but need to compensate
comment|// i that's grown too large.
specifier|final
name|int
name|maxIndex
init|=
name|Math
operator|.
name|min
argument_list|(
name|i
argument_list|,
name|iterators
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<=
name|maxIndex
condition|;
name|index
operator|++
control|)
block|{
name|iterators
index|[
name|index
index|]
operator|.
name|restart
argument_list|()
expr_stmt|;
block|}
name|i
operator|=
literal|0
expr_stmt|;
name|initIterator
argument_list|()
expr_stmt|;
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
name|iterator
operator|.
name|printStatus
argument_list|(
name|b
argument_list|)
return|;
block|}
specifier|public
name|void
name|closeAllocation
parameter_list|()
block|{
for|for
control|(
name|TupleIter
name|iter
range|:
name|iterators
control|)
block|{
name|iter
operator|.
name|closeAllocation
argument_list|()
expr_stmt|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|public
specifier|static
class|class
name|Test
extends|extends
name|EigenbaseTestCase
block|{
specifier|public
name|Test
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundIter
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundIterEmpty
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundIterFirstEmpty
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|null
block|}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
block|,
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|null
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// makes a trivial CalcTupleIter on top of a base TupleIter
specifier|private
specifier|static
name|CalcTupleIter
name|makeCalcTupleIter
parameter_list|(
specifier|final
name|TupleIter
name|base
parameter_list|)
block|{
return|return
operator|new
name|CalcTupleIter
argument_list|(
name|base
argument_list|)
block|{
specifier|public
name|Object
name|fetchNext
parameter_list|()
block|{
return|return
name|base
operator|.
name|fetchNext
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|void
name|testCompoundCalcIter
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"c"
block|}
argument_list|)
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundCalcIterFirstEmpty
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
argument_list|)
block|,
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**          * Checks that a BoxTupleIter returns the same values as the contents of          * an array.          */
specifier|protected
name|void
name|assertUnboxedEquals
parameter_list|(
name|TupleIter
name|p
parameter_list|,
name|Object
index|[]
name|a
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
name|p
operator|.
name|fetchNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Box
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|(
operator|(
name|Box
operator|)
name|o
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|==
name|NoDataReason
operator|.
name|END_OF_DATA
condition|)
block|{
break|break;
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|list
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundBoxIter
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
operator|new
name|BoxTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"400"
block|,
literal|"401"
block|,
literal|"402"
block|,
literal|"403"
block|}
argument_list|)
argument_list|)
block|,
operator|new
name|BoxTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"500"
block|,
literal|"501"
block|,
literal|"502"
block|,
literal|"503"
block|}
argument_list|)
argument_list|)
block|,
operator|new
name|BoxTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"600"
block|,
literal|"601"
block|,
literal|"602"
block|,
literal|"603"
block|}
argument_list|)
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertUnboxedEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"400"
block|,
literal|"401"
block|,
literal|"402"
block|,
literal|"403"
block|,
literal|"500"
block|,
literal|"501"
block|,
literal|"502"
block|,
literal|"503"
block|,
literal|"600"
block|,
literal|"601"
block|,
literal|"602"
block|,
literal|"603"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompoundBoxedCalcIter
parameter_list|()
block|{
name|TupleIter
name|iterator
init|=
operator|new
name|CompoundTupleIter
argument_list|(
operator|new
name|TupleIter
index|[]
block|{
operator|new
name|BoxTupleIter
argument_list|(
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"400"
block|,
literal|"401"
block|,
literal|"402"
block|,
literal|"403"
block|}
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|BoxTupleIter
argument_list|(
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"500"
block|,
literal|"501"
block|,
literal|"502"
block|,
literal|"503"
block|}
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|BoxTupleIter
argument_list|(
name|makeCalcTupleIter
argument_list|(
name|makeTupleIter
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"600"
block|,
literal|"601"
block|,
literal|"602"
block|,
literal|"603"
block|}
argument_list|)
argument_list|)
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|assertUnboxedEquals
argument_list|(
name|iterator
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"400"
block|,
literal|"401"
block|,
literal|"402"
block|,
literal|"403"
block|,
literal|"500"
block|,
literal|"501"
block|,
literal|"502"
block|,
literal|"503"
block|,
literal|"600"
block|,
literal|"601"
block|,
literal|"602"
block|,
literal|"603"
block|}
argument_list|)
expr_stmt|;
block|}
comment|// a boxed value (see BoxTupleIter below)
specifier|static
class|class
name|Box
block|{
name|Object
name|val
decl_stmt|;
specifier|public
name|Box
parameter_list|()
block|{
name|val
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|val
return|;
block|}
specifier|public
name|Box
name|setValue
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|// An TupleIter that always returns the same object, a Box, but with
comment|// different contents. Mimics the TupleIter from a farrago dynamic
comment|// statement.
specifier|static
class|class
name|BoxTupleIter
extends|extends
name|AbstractTupleIter
block|{
name|TupleIter
name|base
decl_stmt|;
name|Box
name|box
decl_stmt|;
specifier|public
name|BoxTupleIter
parameter_list|(
name|TupleIter
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|box
operator|=
operator|new
name|Box
argument_list|()
expr_stmt|;
block|}
comment|// implement TupleIter
specifier|public
name|Object
name|fetchNext
parameter_list|()
block|{
name|Object
name|result
init|=
name|base
operator|.
name|fetchNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|instanceof
name|NoDataReason
condition|)
block|{
return|return
name|result
return|;
block|}
name|box
operator|.
name|setValue
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|box
return|;
block|}
comment|// implement TupleIter
specifier|public
name|void
name|restart
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// implement TupleIter
specifier|public
name|void
name|closeAllocation
parameter_list|()
block|{
name|box
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End CompoundTupleIter.java
end_comment

end_unit

