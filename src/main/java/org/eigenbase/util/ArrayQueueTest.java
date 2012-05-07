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
name|util
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
name|junit
operator|.
name|framework
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link ArrayQueue}.  *  * @author Stephan Zuercher  * @version $Id$  * @since Sep 16, 2004  */
end_comment

begin_class
specifier|public
class|class
name|ArrayQueueTest
extends|extends
name|TestCase
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ArrayQueueTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|testOfferPoll
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRepeatedOfferPoll
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
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
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
operator|+
literal|"_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|i
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
operator|+
literal|"_"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|j
argument_list|)
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testEmptyAndClear
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddAddAllRemove
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExceptions
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|queue
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
block|}
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
block|}
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|queue
operator|.
name|addAll
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
block|}
try|try
block|{
name|queue
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
block|}
try|try
block|{
name|queue
operator|.
name|element
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|public
name|void
name|testPeek
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIterator
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|queue
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConstructors
parameter_list|()
block|{
try|try
block|{
operator|new
name|ArrayQueue
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ArrayQueue
argument_list|<
name|Integer
argument_list|>
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue1
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue1
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|queue1
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue1
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue2
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|queue1
argument_list|,
name|queue2
argument_list|)
expr_stmt|;
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue3
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|queue1
operator|.
name|equals
argument_list|(
name|queue3
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1, 2, 3]"
argument_list|,
name|queue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToArray
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Object
operator|)
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|queue
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue2
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|queue2
operator|.
name|add
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|queue2
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|3
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOfferNull
parameter_list|()
block|{
name|ArrayQueue
argument_list|<
name|String
argument_list|>
name|queue
init|=
operator|new
name|ArrayQueue
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|queue
operator|.
name|offer
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|queue
operator|.
name|offer
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|queue
operator|.
name|offer
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|offer
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|queue
operator|.
name|offer
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ArrayQueueTest.java
end_comment

end_unit

