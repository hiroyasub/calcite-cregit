begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

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
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test for {@link Functions}.  */
end_comment

begin_class
specifier|public
class|class
name|FunctionTest
block|{
comment|/** Unit test for {@link Functions#filter}. */
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|abc
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|,
literal|"C"
argument_list|,
literal|"D"
argument_list|)
decl_stmt|;
comment|// a miss, then a hit
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[A, C, D]"
argument_list|,
name|Functions
operator|.
name|filter
argument_list|(
name|abc
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|v1
parameter_list|)
block|{
return|return
operator|!
name|v1
operator|.
name|equals
argument_list|(
literal|"B"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// a hit, then all misses
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[A]"
argument_list|,
name|Functions
operator|.
name|filter
argument_list|(
name|abc
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|.
name|equals
argument_list|(
literal|"A"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// two hits, then a miss
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[A, B, D]"
argument_list|,
name|Functions
operator|.
name|filter
argument_list|(
name|abc
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|v1
parameter_list|)
block|{
return|return
operator|!
name|v1
operator|.
name|equals
argument_list|(
literal|"C"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|Functions
operator|.
name|filter
argument_list|(
name|abc
argument_list|,
name|Functions
operator|.
expr|<
name|String
operator|>
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertSame
argument_list|(
name|abc
argument_list|,
name|Functions
operator|.
name|filter
argument_list|(
name|abc
argument_list|,
name|Functions
operator|.
expr|<
name|String
operator|>
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#exists}. */
annotation|@
name|Test
specifier|public
name|void
name|testExists
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|ints
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|Functions
operator|.
name|exists
argument_list|(
name|ints
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|>
literal|20
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|Functions
operator|.
name|exists
argument_list|(
name|empty
argument_list|,
name|Functions
operator|.
expr|<
name|Integer
operator|>
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|Functions
operator|.
name|exists
argument_list|(
name|empty
argument_list|,
name|Functions
operator|.
expr|<
name|Integer
operator|>
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#all}. */
annotation|@
name|Test
specifier|public
name|void
name|testAll
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|ints
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|>
literal|20
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|<
literal|20
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
operator|new
name|Predicate1
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|<
literal|10
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|empty
argument_list|,
name|Functions
operator|.
expr|<
name|Integer
operator|>
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|empty
argument_list|,
name|Functions
operator|.
expr|<
name|Integer
operator|>
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#generate}. */
annotation|@
name|Test
specifier|public
name|void
name|testGenerate
parameter_list|()
block|{
specifier|final
name|Function1
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|xx
init|=
operator|new
name|Function1
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Integer
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|==
literal|0
condition|?
literal|"0"
else|:
literal|"x"
operator|+
name|apply
argument_list|(
name|a0
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|Functions
operator|.
name|generate
argument_list|(
literal|0
argument_list|,
name|xx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[0]"
argument_list|,
name|Functions
operator|.
name|generate
argument_list|(
literal|1
argument_list|,
name|xx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[0, x0, xx0]"
argument_list|,
name|Functions
operator|.
name|generate
argument_list|(
literal|3
argument_list|,
name|xx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|generate
init|=
name|Functions
operator|.
name|generate
argument_list|(
operator|-
literal|2
argument_list|,
name|xx
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|generate
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
comment|/** Unit test for {@link Functions#adapt(Object[], Function1)}. */
annotation|@
name|Test
specifier|public
name|void
name|testAdaptArray
parameter_list|()
block|{
name|String
index|[]
name|abc
init|=
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
decl_stmt|;
specifier|final
name|Function1
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toUpper
init|=
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|toUpperCase
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[A, B, C]"
argument_list|,
name|Functions
operator|.
name|adapt
argument_list|(
name|abc
argument_list|,
name|toUpper
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|Functions
operator|.
name|adapt
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|toUpper
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FunctionTest.java
end_comment

end_unit

