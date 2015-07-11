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
operator|.
name|core
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
name|RelOptCluster
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
name|RelOptCost
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
name|RelTraitSet
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
name|RelDistributions
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
name|RelInput
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
name|RelNode
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
name|RelWriter
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
name|SingleRel
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression that imposes a particular distribution on its input  * without otherwise changing its content.  *  * @see org.apache.calcite.rel.core.SortExchange  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Exchange
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|RelDistribution
name|distribution
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an Exchange.    *    * @param cluster   Cluster this relational expression belongs to    * @param traitSet  Trait set    * @param input     Input relational expression    * @param distribution Distribution specification    */
specifier|protected
name|Exchange
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelDistribution
name|distribution
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|distribution
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|distribution
argument_list|)
expr_stmt|;
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|distribution
argument_list|)
operator|:
literal|"traits="
operator|+
name|traitSet
operator|+
literal|", distribution"
operator|+
name|distribution
assert|;
assert|assert
name|distribution
operator|!=
name|RelDistributions
operator|.
name|ANY
assert|;
block|}
comment|/**    * Creates a Exchange by parsing serialized output.    */
specifier|public
name|Exchange
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|plus
argument_list|(
name|input
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|input
operator|.
name|getDistribution
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|Exchange
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|distribution
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|Exchange
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelDistribution
name|newDistribution
parameter_list|)
function_decl|;
comment|/** Returns the distribution of the rows returned by this Exchange. */
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
name|distribution
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// Higher cost if rows are wider discourages pushing a project through an
comment|// exchange.
name|double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|double
name|bytesPerRow
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|*
literal|4
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|Util
operator|.
name|nLogN
argument_list|(
name|rowCount
argument_list|)
operator|*
name|bytesPerRow
argument_list|,
name|rowCount
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"distribution"
argument_list|,
name|distribution
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Exchange.java
end_comment

end_unit

