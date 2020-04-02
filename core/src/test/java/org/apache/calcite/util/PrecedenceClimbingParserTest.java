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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link PrecedenceClimbingParser}.  */
end_comment

begin_class
class|class
name|PrecedenceClimbingParserTest
block|{
annotation|@
name|Test
name|void
name|testBasic
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|atom
argument_list|(
literal|"b"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"*"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"c"
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|4
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"(a + ((- b) * (c !)))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRepeatedPrefixPostfix
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|prefix
argument_list|(
literal|"+"
argument_list|,
literal|3
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"+"
argument_list|,
literal|3
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"+"
argument_list|,
literal|3
argument_list|)
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|4
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|atom
argument_list|(
literal|"b"
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|4
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|4
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"((+ (- (+ (+ (a !))))) + (- (- ((b !) !))))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAtom
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOnlyPrefix
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|prefix
argument_list|(
literal|"-"
argument_list|,
literal|3
argument_list|)
operator|.
name|atom
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"(- (- 1))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOnlyPostfix
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|1
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|33333
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"!"
argument_list|,
literal|33333
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"((1 !) !)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeftAssociative
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"*"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"b"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"c"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"d"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"e"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"*"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"f"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"((((a * b) + c) + d) + (e * f))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRightAssociative
parameter_list|()
block|{
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"^"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
operator|.
name|atom
argument_list|(
literal|"b"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"^"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
operator|.
name|atom
argument_list|(
literal|"c"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"^"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
operator|.
name|atom
argument_list|(
literal|"d"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"e"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"*"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"f"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"((a ^ (b ^ (c ^ d))) + (e * f))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSpecial
parameter_list|()
block|{
comment|// price> 5 and price between 1 + 2 and 3 * 4 and price is null
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"price"
argument_list|)
operator|.
name|infix
argument_list|(
literal|">"
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"5"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"and"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"price"
argument_list|)
operator|.
name|special
argument_list|(
literal|"between"
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
parameter_list|(
name|parser
parameter_list|,
name|op
parameter_list|)
lambda|->
operator|new
name|PrecedenceClimbingParser
operator|.
name|Result
argument_list|(
name|op
operator|.
name|previous
argument_list|,
name|op
operator|.
name|next
operator|.
name|next
operator|.
name|next
argument_list|,
name|parser
operator|.
name|call
argument_list|(
name|op
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|op
operator|.
name|previous
argument_list|,
name|op
operator|.
name|next
argument_list|,
name|op
operator|.
name|next
operator|.
name|next
operator|.
name|next
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|atom
argument_list|(
literal|"1"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"+"
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"2"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"and"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"3"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"*"
argument_list|,
literal|6
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"4"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"and"
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"price"
argument_list|)
operator|.
name|postfix
argument_list|(
literal|"is null"
argument_list|,
literal|4
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"(((price> 5) and between(price, (1 + 2), (3 * 4)))"
operator|+
literal|" and (price is null))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualPrecedence
parameter_list|()
block|{
comment|// LIKE has same precedence as '='; LIKE is right-assoc, '=' is left
specifier|final
name|PrecedenceClimbingParser
name|p
init|=
operator|new
name|PrecedenceClimbingParser
operator|.
name|Builder
argument_list|()
operator|.
name|atom
argument_list|(
literal|"a"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"="
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"b"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"like"
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
operator|.
name|atom
argument_list|(
literal|"c"
argument_list|)
operator|.
name|infix
argument_list|(
literal|"="
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|)
operator|.
name|atom
argument_list|(
literal|"d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|PrecedenceClimbingParser
operator|.
name|Token
name|token
init|=
name|p
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|p
operator|.
name|print
argument_list|(
name|token
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"(((a = b) like c) = d)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

