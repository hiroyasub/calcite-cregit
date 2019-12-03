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
name|rel
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
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
comment|/**  * Tests for {@link RelCollation} and {@link RelFieldCollation}.  */
end_comment

begin_class
specifier|public
class|class
name|RelCollationTest
block|{
comment|/** Unit test for {@link RelCollations#contains}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ArraysAsListWithZeroOrOneArgument"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCollationContains
parameter_list|()
block|{
specifier|final
name|RelCollation
name|collation21
init|=
name|RelCollations
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
literal|2
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|)
argument_list|,
operator|new
name|RelFieldCollation
argument_list|(
literal|1
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// if there are duplicates in keys, later occurrences are ignored
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation21
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelCollation
name|collation1
init|=
name|RelCollations
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
literal|1
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|RelCollations
operator|.
name|contains
argument_list|(
name|collation1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for    *  {@link org.apache.calcite.rel.RelCollationImpl#compareTo}. */
annotation|@
name|Test
specifier|public
name|void
name|testCollationCompare
parameter_list|()
block|{
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|()
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|collation
argument_list|(
literal|1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|collation
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|RelCollation
name|collation
parameter_list|(
name|int
modifier|...
name|ordinals
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ordinal
range|:
name|ordinals
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|ordinal
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|RelCollations
operator|.
name|of
argument_list|(
name|list
argument_list|)
return|;
block|}
block|}
end_class

end_unit

