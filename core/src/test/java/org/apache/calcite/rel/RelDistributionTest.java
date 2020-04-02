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
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelTraitSet
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
comment|/**  * Tests for {@link RelDistribution}.  */
end_comment

begin_class
class|class
name|RelDistributionTest
block|{
annotation|@
name|Test
name|void
name|testRelDistributionSatisfy
parameter_list|()
block|{
name|RelDistribution
name|distribution1
init|=
name|RelDistributions
operator|.
name|hash
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|RelDistribution
name|distribution2
init|=
name|RelDistributions
operator|.
name|hash
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|RelTraitSet
operator|.
name|createEmpty
argument_list|()
decl_stmt|;
name|RelTraitSet
name|simpleTrait1
init|=
name|traitSet
operator|.
name|plus
argument_list|(
name|distribution1
argument_list|)
decl_stmt|;
name|RelTraitSet
name|simpleTrait2
init|=
name|traitSet
operator|.
name|plus
argument_list|(
name|distribution2
argument_list|)
decl_stmt|;
name|RelTraitSet
name|compositeTrait
init|=
name|traitSet
operator|.
name|replace
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|distribution1
argument_list|,
name|distribution2
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|compositeTrait
operator|.
name|satisfies
argument_list|(
name|simpleTrait1
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
name|compositeTrait
operator|.
name|satisfies
argument_list|(
name|simpleTrait2
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
name|distribution1
operator|.
name|compareTo
argument_list|(
name|distribution2
argument_list|)
argument_list|,
name|is
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|distribution2
operator|.
name|compareTo
argument_list|(
name|distribution1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//noinspection EqualsWithItself
name|assertThat
argument_list|(
name|distribution2
operator|.
name|compareTo
argument_list|(
name|distribution2
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

