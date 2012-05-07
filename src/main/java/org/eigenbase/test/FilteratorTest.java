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
name|test
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
comment|/**  * Unit test for {@link Filterator}.  *  * @author jhyde  * @version $Id$  * @since September 6, 2006  */
end_comment

begin_class
specifier|public
class|class
name|FilteratorTest
extends|extends
name|TestCase
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|testOne
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tomDickHarry
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"tom"
argument_list|,
literal|"dick"
argument_list|,
literal|"harry"
argument_list|)
decl_stmt|;
specifier|final
name|Filterator
argument_list|<
name|String
argument_list|>
name|filterator
init|=
operator|new
name|Filterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|tomDickHarry
operator|.
name|iterator
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// call hasNext twice
name|assertTrue
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tom"
argument_list|,
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// call next without calling hasNext
name|assertEquals
argument_list|(
literal|"dick"
argument_list|,
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"harry"
argument_list|,
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNulls
parameter_list|()
block|{
comment|// Nulls don't cause an error - but are not emitted, because they
comment|// fail the instanceof test.
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tomDickHarry
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"paul"
argument_list|,
literal|null
argument_list|,
literal|"ringo"
argument_list|)
decl_stmt|;
specifier|final
name|Filterator
argument_list|<
name|String
argument_list|>
name|filterator
init|=
operator|new
name|Filterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|tomDickHarry
operator|.
name|iterator
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"paul"
argument_list|,
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ringo"
argument_list|,
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSubtypes
parameter_list|()
block|{
specifier|final
name|ArrayList
name|arrayList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|final
name|HashSet
name|hashSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
specifier|final
name|LinkedList
name|linkedList
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
name|Collection
index|[]
name|collections
init|=
block|{
literal|null
block|,
name|arrayList
block|,
name|hashSet
block|,
name|linkedList
block|,
literal|null
block|,         }
decl_stmt|;
specifier|final
name|Filterator
argument_list|<
name|List
argument_list|>
name|filterator
init|=
operator|new
name|Filterator
argument_list|<
name|List
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|collections
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|List
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// skips null
name|assertTrue
argument_list|(
name|arrayList
operator|==
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// skips the HashSet
name|assertTrue
argument_list|(
name|linkedList
operator|==
name|filterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBox
parameter_list|()
block|{
specifier|final
name|Number
index|[]
name|numbers
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3.14
block|,
literal|4
block|,
literal|null
block|,
literal|6E23
block|}
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|Util
operator|.
name|filter
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|numbers
argument_list|)
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"[1, 2, 4]"
argument_list|,
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FilteratorTest.java
end_comment

end_unit

