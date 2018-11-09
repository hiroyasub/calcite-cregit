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
name|logical
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
name|Convention
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
name|RelCollationTraitDef
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
name|core
operator|.
name|SortExchange
import|;
end_import

begin_comment
comment|/**  * Sub-class of {@link org.apache.calcite.rel.core.SortExchange} not  * targeted at any particular engine or calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|LogicalSortExchange
extends|extends
name|SortExchange
block|{
specifier|private
name|LogicalSortExchange
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
parameter_list|,
name|RelCollation
name|collation
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|distribution
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a LogicalSortExchange.    *    * @param input     Input relational expression    * @param distribution Distribution specification    * @param collation array of sort specifications    */
specifier|public
specifier|static
name|LogicalSortExchange
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RelDistribution
name|distribution
parameter_list|,
name|RelCollation
name|collation
parameter_list|)
block|{
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|collation
operator|=
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|collation
argument_list|)
expr_stmt|;
name|distribution
operator|=
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|distribution
argument_list|)
expr_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
operator|.
name|replace
argument_list|(
name|distribution
argument_list|)
operator|.
name|replace
argument_list|(
name|collation
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalSortExchange
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|distribution
argument_list|,
name|collation
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SortExchange
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
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|)
block|{
return|return
operator|new
name|LogicalSortExchange
argument_list|(
name|this
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newDistribution
argument_list|,
name|newCollation
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalSortExchange.java
end_comment

end_unit

