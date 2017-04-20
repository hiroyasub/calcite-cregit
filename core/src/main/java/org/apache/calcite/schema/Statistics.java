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
name|schema
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
name|rel
operator|.
name|RelCollation
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
name|rel
operator|.
name|RelDistribution
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
name|rel
operator|.
name|RelDistributionTraitDef
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
name|rel
operator|.
name|RelReferentialConstraint
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
name|ImmutableBitSet
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Utility functions regarding {@link Statistic}.  */
end_comment

begin_class
specifier|public
class|class
name|Statistics
block|{
specifier|private
name|Statistics
parameter_list|()
block|{
block|}
comment|/** Returns a {@link Statistic} that knows nothing about a table. */
specifier|public
specifier|static
specifier|final
name|Statistic
name|UNKNOWN
init|=
operator|new
name|Statistic
argument_list|()
block|{
specifier|public
name|Double
name|getRowCount
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|getReferentialConstraints
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|RelReferentialConstraint
operator|>
name|of
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollations
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Returns a statistic with a given set of referential constraints. */
specifier|public
specifier|static
name|Statistic
name|of
parameter_list|(
specifier|final
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|referentialConstraints
parameter_list|)
block|{
return|return
name|of
argument_list|(
literal|null
argument_list|,
name|ImmutableList
operator|.
expr|<
name|ImmutableBitSet
operator|>
name|of
argument_list|()
argument_list|,
name|referentialConstraints
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns a statistic with a given row count and set of unique keys. */
specifier|public
specifier|static
name|Statistic
name|of
parameter_list|(
specifier|final
name|double
name|rowCount
parameter_list|,
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|rowCount
argument_list|,
name|keys
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelReferentialConstraint
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns a statistic with a given row count, set of unique keys,    * and collations. */
specifier|public
specifier|static
name|Statistic
name|of
parameter_list|(
specifier|final
name|double
name|rowCount
parameter_list|,
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keys
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|rowCount
argument_list|,
name|keys
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelReferentialConstraint
operator|>
name|of
argument_list|()
argument_list|,
name|collations
argument_list|)
return|;
block|}
comment|/** Returns a statistic with a given row count, set of unique keys,    * referential constraints, and collations. */
specifier|public
specifier|static
name|Statistic
name|of
parameter_list|(
specifier|final
name|Double
name|rowCount
parameter_list|,
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keys
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|referentialConstraints
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|)
block|{
return|return
operator|new
name|Statistic
argument_list|()
block|{
specifier|public
name|Double
name|getRowCount
parameter_list|()
block|{
return|return
name|rowCount
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
for|for
control|(
name|ImmutableBitSet
name|key
range|:
name|keys
control|)
block|{
if|if
condition|(
name|columns
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|getReferentialConstraints
parameter_list|()
block|{
return|return
name|referentialConstraints
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollations
parameter_list|()
block|{
return|return
name|collations
return|;
block|}
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Statistics.java
end_comment

end_unit

