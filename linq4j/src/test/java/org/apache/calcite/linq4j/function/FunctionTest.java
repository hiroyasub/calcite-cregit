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
name|jupiter
operator|.
name|api
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Test for {@link Functions}.  */
end_comment

begin_class
class|class
name|FunctionTest
block|{
comment|/** Unit test for {@link Functions#filter}. */
annotation|@
name|Test
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
name|v1
lambda|->
operator|!
name|v1
operator|.
name|equals
argument_list|(
literal|"B"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// a hit, then all misses
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
name|v1
lambda|->
name|v1
operator|.
name|equals
argument_list|(
literal|"A"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// two hits, then a miss
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
name|v1
lambda|->
operator|!
name|v1
operator|.
name|equals
argument_list|(
literal|"C"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#exists}. */
annotation|@
name|Test
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
name|assertFalse
argument_list|(
name|Functions
operator|.
name|exists
argument_list|(
name|ints
argument_list|,
name|v1
lambda|->
name|v1
operator|>
literal|20
argument_list|)
argument_list|)
expr_stmt|;
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
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#all}. */
annotation|@
name|Test
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
name|assertFalse
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
name|v1
lambda|->
name|v1
operator|>
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
name|v1
lambda|->
name|v1
operator|<
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Functions
operator|.
name|all
argument_list|(
name|ints
argument_list|,
name|v1
lambda|->
name|v1
operator|<
literal|10
argument_list|)
argument_list|)
expr_stmt|;
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
name|falsePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|truePredicate1
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link Functions#generate}. */
annotation|@
name|Test
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
block|}
end_class

end_unit

