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
name|ImmutableRangeSet
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
name|Range
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
name|RangeSet
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
name|TreeRangeSet
import|;
end_import

begin_comment
comment|/** Utilities for Guava {@link com.google.common.collect.RangeSet}. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"UnstableApiUsage"
block|}
argument_list|)
specifier|public
class|class
name|RangeSets
block|{
specifier|private
name|RangeSets
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|ImmutableRangeSet
name|ALL
init|=
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
operator|.
name|complement
argument_list|()
decl_stmt|;
comment|/** Subtracts a range from a range set. */
specifier|public
specifier|static
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|C
argument_list|>
parameter_list|>
name|RangeSet
argument_list|<
name|C
argument_list|>
name|minus
parameter_list|(
name|RangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
parameter_list|,
name|Range
argument_list|<
name|C
argument_list|>
name|range
parameter_list|)
block|{
specifier|final
name|TreeRangeSet
argument_list|<
name|C
argument_list|>
name|mutableRangeSet
init|=
name|TreeRangeSet
operator|.
name|create
argument_list|(
name|rangeSet
argument_list|)
decl_stmt|;
name|mutableRangeSet
operator|.
name|remove
argument_list|(
name|range
argument_list|)
expr_stmt|;
return|return
name|mutableRangeSet
operator|.
name|equals
argument_list|(
name|rangeSet
argument_list|)
condition|?
name|rangeSet
else|:
name|ImmutableRangeSet
operator|.
name|copyOf
argument_list|(
name|mutableRangeSet
argument_list|)
return|;
block|}
comment|/** Returns the unrestricted range set. */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|C
argument_list|>
parameter_list|>
name|RangeSet
argument_list|<
name|C
argument_list|>
name|rangeSetAll
parameter_list|()
block|{
return|return
operator|(
name|RangeSet
operator|)
name|ALL
return|;
block|}
block|}
end_class

end_unit

