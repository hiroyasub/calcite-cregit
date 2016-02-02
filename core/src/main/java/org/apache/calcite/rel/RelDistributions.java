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
name|RelOptPlanner
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
name|plan
operator|.
name|RelTrait
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
name|util
operator|.
name|ImmutableIntList
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
name|util
operator|.
name|Util
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
name|util
operator|.
name|mapping
operator|.
name|Mapping
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
name|util
operator|.
name|mapping
operator|.
name|Mappings
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
name|base
operator|.
name|Preconditions
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
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Objects
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Utilities concerning {@link org.apache.calcite.rel.RelDistribution}.  */
end_comment

begin_class
specifier|public
class|class
name|RelDistributions
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableIntList
name|EMPTY
init|=
name|ImmutableIntList
operator|.
name|of
argument_list|()
decl_stmt|;
comment|/** The singleton singleton distribution. */
specifier|public
specifier|static
specifier|final
name|RelDistribution
name|SINGLETON
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|SINGLETON
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
comment|/** The singleton random distribution. */
specifier|public
specifier|static
specifier|final
name|RelDistribution
name|RANDOM_DISTRIBUTED
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|RANDOM_DISTRIBUTED
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
comment|/** The singleton round-robin distribution. */
specifier|public
specifier|static
specifier|final
name|RelDistribution
name|ROUND_ROBIN_DISTRIBUTED
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|ROUND_ROBIN_DISTRIBUTED
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
comment|/** The singleton broadcast distribution. */
specifier|public
specifier|static
specifier|final
name|RelDistribution
name|BROADCAST_DISTRIBUTED
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|BROADCAST_DISTRIBUTED
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelDistribution
name|ANY
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|ANY
argument_list|,
name|EMPTY
argument_list|)
decl_stmt|;
specifier|private
name|RelDistributions
parameter_list|()
block|{
block|}
comment|/** Creates a hash distribution. */
specifier|public
specifier|static
name|RelDistribution
name|hash
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|numbers
parameter_list|)
block|{
name|ImmutableIntList
name|list
init|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|numbers
argument_list|)
decl_stmt|;
if|if
condition|(
name|numbers
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
operator|!
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|isOrdered
argument_list|(
name|list
argument_list|)
condition|)
block|{
name|list
operator|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|sortedCopy
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RelDistributionImpl
name|trait
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|HASH_DISTRIBUTED
argument_list|,
name|list
argument_list|)
decl_stmt|;
return|return
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|trait
argument_list|)
return|;
block|}
comment|/** Creates a range distribution. */
specifier|public
specifier|static
name|RelDistribution
name|range
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|numbers
parameter_list|)
block|{
name|ImmutableIntList
name|list
init|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|numbers
argument_list|)
decl_stmt|;
name|RelDistributionImpl
name|trait
init|=
operator|new
name|RelDistributionImpl
argument_list|(
name|RelDistribution
operator|.
name|Type
operator|.
name|RANGE_DISTRIBUTED
argument_list|,
name|list
argument_list|)
decl_stmt|;
return|return
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|trait
argument_list|)
return|;
block|}
comment|/** Implementation of {@link org.apache.calcite.rel.RelDistribution}. */
specifier|private
specifier|static
class|class
name|RelDistributionImpl
implements|implements
name|RelDistribution
block|{
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|ImmutableIntList
name|keys
decl_stmt|;
specifier|private
name|RelDistributionImpl
parameter_list|(
name|Type
name|type
parameter_list|,
name|ImmutableIntList
name|keys
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|keys
argument_list|)
expr_stmt|;
assert|assert
name|type
operator|!=
name|Type
operator|.
name|HASH_DISTRIBUTED
operator|||
name|keys
operator|.
name|size
argument_list|()
operator|<
literal|2
operator|||
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|isOrdered
argument_list|(
name|keys
argument_list|)
operator|:
literal|"key columns of hash distribution must be in order"
assert|;
assert|assert
name|type
operator|==
name|Type
operator|.
name|HASH_DISTRIBUTED
operator|||
name|type
operator|==
name|Type
operator|.
name|RANDOM_DISTRIBUTED
operator|||
name|keys
operator|.
name|isEmpty
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|type
argument_list|,
name|keys
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|RelDistributionImpl
operator|&&
name|type
operator|==
operator|(
operator|(
name|RelDistributionImpl
operator|)
name|obj
operator|)
operator|.
name|type
operator|&&
name|keys
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RelDistributionImpl
operator|)
name|obj
operator|)
operator|.
name|keys
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|type
operator|.
name|shortName
return|;
block|}
else|else
block|{
return|return
name|type
operator|.
name|shortName
operator|+
name|keys
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
name|keys
return|;
block|}
specifier|public
name|RelDistributionTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|RelDistributionTraitDef
operator|.
name|INSTANCE
return|;
block|}
specifier|public
name|RelDistribution
name|apply
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
if|if
condition|(
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|getTraitDef
argument_list|()
operator|.
name|canonize
argument_list|(
operator|new
name|RelDistributionImpl
argument_list|(
name|type
argument_list|,
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|Mappings
operator|.
name|apply
argument_list|(
operator|(
name|Mapping
operator|)
name|mapping
argument_list|,
name|keys
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
if|if
condition|(
name|trait
operator|==
name|this
operator|||
name|trait
operator|==
name|ANY
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|trait
operator|instanceof
name|RelDistributionImpl
condition|)
block|{
name|RelDistributionImpl
name|distribution
init|=
operator|(
name|RelDistributionImpl
operator|)
name|trait
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|distribution
operator|.
name|type
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|HASH_DISTRIBUTED
case|:
comment|// The "leading edge" property of Range does not apply to Hash.
comment|// Only Hash[x, y] satisfies Hash[x, y].
return|return
name|keys
operator|.
name|equals
argument_list|(
name|distribution
operator|.
name|keys
argument_list|)
return|;
case|case
name|RANGE_DISTRIBUTED
case|:
comment|// Range[x, y] satisfies Range[x, y, z] but not Range[x]
return|return
name|Util
operator|.
name|startsWith
argument_list|(
name|distribution
operator|.
name|keys
argument_list|,
name|keys
argument_list|)
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
block|}
if|if
condition|(
name|trait
operator|==
name|RANDOM_DISTRIBUTED
condition|)
block|{
comment|// RANDOM is satisfied by HASH, ROUND-ROBIN, RANDOM, RANGE;
comment|// we've already checked RANDOM
return|return
name|type
operator|==
name|Type
operator|.
name|HASH_DISTRIBUTED
operator|||
name|type
operator|==
name|Type
operator|.
name|ROUND_ROBIN_DISTRIBUTED
operator|||
name|type
operator|==
name|Type
operator|.
name|RANGE_DISTRIBUTED
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelDistributions.java
end_comment

end_unit

