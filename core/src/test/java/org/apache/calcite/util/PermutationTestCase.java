begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link Permutation}.  */
end_comment

begin_class
specifier|public
class|class
name|PermutationTestCase
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|PermutationTestCase
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Test
specifier|public
name|void
name|testOne
parameter_list|()
block|{
specifier|final
name|Permutation
name|perm
init|=
operator|new
name|Permutation
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[0, 1, 2, 3]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|perm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|perm
operator|.
name|set
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[2, 1, 0, 3]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|perm
operator|.
name|set
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[2, 0, 1, 3]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Permutation
name|invPerm
init|=
name|perm
operator|.
name|inverse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[1, 2, 0, 3]"
argument_list|,
name|invPerm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// changing perm doesn't change inverse
name|perm
operator|.
name|set
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[0, 2, 1, 3]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1, 2, 0, 3]"
argument_list|,
name|invPerm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTwo
parameter_list|()
block|{
specifier|final
name|Permutation
name|perm
init|=
operator|new
name|Permutation
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|2
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|perm
operator|.
name|isIdentity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[3, 2, 0, 1]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Permutation
name|perm2
init|=
operator|(
name|Permutation
operator|)
name|perm
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[3, 2, 0, 1]"
argument_list|,
name|perm2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|perm
operator|.
name|equals
argument_list|(
name|perm2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|perm2
operator|.
name|equals
argument_list|(
name|perm
argument_list|)
argument_list|)
expr_stmt|;
name|perm
operator|.
name|set
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[3, 2, 1, 0]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|perm
operator|.
name|equals
argument_list|(
name|perm2
argument_list|)
argument_list|)
expr_stmt|;
comment|// clone not affected
name|assertEquals
argument_list|(
literal|"[3, 2, 0, 1]"
argument_list|,
name|perm2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|perm2
operator|.
name|set
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[0, 2, 3, 1]"
argument_list|,
name|perm2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsert
parameter_list|()
block|{
name|Permutation
name|perm
init|=
operator|new
name|Permutation
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|0
block|,
literal|4
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|perm
operator|.
name|insertTarget
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[4, 0, 5, 3, 1, 2]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// insert at start
name|perm
operator|=
operator|new
name|Permutation
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|0
block|,
literal|4
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|perm
operator|.
name|insertTarget
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[4, 1, 5, 3, 2, 0]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// insert at end
name|perm
operator|=
operator|new
name|Permutation
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|0
block|,
literal|4
block|,
literal|2
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|perm
operator|.
name|insertTarget
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[3, 0, 4, 2, 1, 5]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// insert into empty
name|perm
operator|=
operator|new
name|Permutation
argument_list|(
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
name|perm
operator|.
name|insertTarget
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[0]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
specifier|final
name|Permutation
name|perm
init|=
operator|new
name|Permutation
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|perm
operator|.
name|isIdentity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|perm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|perm
operator|.
name|equals
argument_list|(
name|perm
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|perm
operator|.
name|equals
argument_list|(
name|perm
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|perm
operator|.
name|set
argument_list|(
literal|1
argument_list|,
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
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|perm
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|,
literal|2
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
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

begin_comment
comment|// End PermutationTestCase.java
end_comment

end_unit

