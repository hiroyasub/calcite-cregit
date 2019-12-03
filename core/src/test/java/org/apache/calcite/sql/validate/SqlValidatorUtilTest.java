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
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|CalciteContextException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|test
operator|.
name|SqlTestFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|test
operator|.
name|SqlTester
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|test
operator|.
name|SqlValidatorTester
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|anyOf
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
name|CoreMatchers
operator|.
name|not
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
name|sameInstance
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
comment|/**  * Tests for {@link SqlValidatorUtil}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorUtilTest
block|{
specifier|private
specifier|static
name|void
name|checkChangedFieldList
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|nameList
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|resultList
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
comment|// Check that the new names are appended with "0" in order they appear in
comment|// original nameList. This is assuming that we only have one "collision".
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|nameList
control|)
block|{
name|String
name|newName
init|=
name|resultList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newName
argument_list|,
name|anyOf
argument_list|(
name|is
argument_list|(
name|name
argument_list|)
argument_list|,
name|is
argument_list|(
name|name
operator|+
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|// Make sure each name is unique
name|List
argument_list|<
name|String
argument_list|>
name|copyResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|resultList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|result
range|:
name|resultList
control|)
block|{
name|copyResultList
operator|.
name|add
argument_list|(
name|result
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|result
range|:
name|resultList
control|)
block|{
specifier|final
name|String
name|lowerResult
init|=
name|result
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|copyResultList
operator|.
name|contains
argument_list|(
name|lowerResult
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|copyResultList
operator|.
name|remove
argument_list|(
name|lowerResult
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|caseSensitive
condition|)
block|{
name|assertThat
argument_list|(
name|copyResultList
operator|.
name|contains
argument_list|(
name|lowerResult
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|copyResultList
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"col_ABC"
argument_list|,
literal|"col_abC"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|nameList
argument_list|,
name|sameInstance
argument_list|(
name|resultList
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyNotCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"col_ABC"
argument_list|,
literal|"col_abC"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultList
argument_list|,
name|not
argument_list|(
name|nameList
argument_list|)
argument_list|)
expr_stmt|;
name|checkChangedFieldList
argument_list|(
name|nameList
argument_list|,
name|resultList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyOrderingCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"k68s"
argument_list|,
literal|"def"
argument_list|,
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"abc"
argument_list|,
literal|"123"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|nameList
argument_list|,
name|sameInstance
argument_list|(
name|resultList
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyOrderingRepeatedCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"k68s"
argument_list|,
literal|"def"
argument_list|,
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"def"
argument_list|,
literal|"123"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|nameList
argument_list|,
name|not
argument_list|(
name|resultList
argument_list|)
argument_list|)
expr_stmt|;
name|checkChangedFieldList
argument_list|(
name|nameList
argument_list|,
name|resultList
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyOrderingNotCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"k68s"
argument_list|,
literal|"def"
argument_list|,
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"abc"
argument_list|,
literal|"123"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultList
argument_list|,
name|not
argument_list|(
name|nameList
argument_list|)
argument_list|)
expr_stmt|;
name|checkChangedFieldList
argument_list|(
name|nameList
argument_list|,
name|resultList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniquifyOrderingRepeatedNotCaseSensitive
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"k68s"
argument_list|,
literal|"def"
argument_list|,
literal|"col1"
argument_list|,
literal|"COL1"
argument_list|,
literal|"def"
argument_list|,
literal|"123"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|resultList
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultList
argument_list|,
name|not
argument_list|(
name|nameList
argument_list|)
argument_list|)
expr_stmt|;
name|checkChangedFieldList
argument_list|(
name|nameList
argument_list|,
name|resultList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCheckingDuplicatesWithCompoundIdentifiers
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|newList
operator|.
name|add
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"c0"
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
expr_stmt|;
name|newList
operator|.
name|add
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"c0"
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester
init|=
operator|new
name|SqlValidatorTester
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorImpl
name|validator
init|=
operator|(
name|SqlValidatorImpl
operator|)
name|tester
operator|.
name|getValidator
argument_list|()
decl_stmt|;
try|try
block|{
name|SqlValidatorUtil
operator|.
name|checkIdentifierListForDuplicates
argument_list|(
name|newList
argument_list|,
name|validator
operator|.
name|getValidationErrorFunction
argument_list|()
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
name|CalciteContextException
name|e
parameter_list|)
block|{
comment|// ok
block|}
comment|// should not throw
name|newList
operator|.
name|set
argument_list|(
literal|1
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"c1"
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
expr_stmt|;
name|SqlValidatorUtil
operator|.
name|checkIdentifierListForDuplicates
argument_list|(
name|newList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameMatcher
parameter_list|()
block|{
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|beatles
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"john"
argument_list|,
literal|"paul"
argument_list|,
literal|"ringo"
argument_list|,
literal|"rinGo"
argument_list|)
decl_stmt|;
specifier|final
name|SqlNameMatcher
name|insensitiveMatcher
init|=
name|SqlNameMatchers
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|insensitiveMatcher
operator|.
name|frequency
argument_list|(
name|beatles
argument_list|,
literal|"ringo"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|insensitiveMatcher
operator|.
name|frequency
argument_list|(
name|beatles
argument_list|,
literal|"rinGo"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SqlNameMatcher
name|sensitiveMatcher
init|=
name|SqlNameMatchers
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sensitiveMatcher
operator|.
name|frequency
argument_list|(
name|beatles
argument_list|,
literal|"ringo"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sensitiveMatcher
operator|.
name|frequency
argument_list|(
name|beatles
argument_list|,
literal|"rinGo"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sensitiveMatcher
operator|.
name|frequency
argument_list|(
name|beatles
argument_list|,
literal|"Ringo"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

