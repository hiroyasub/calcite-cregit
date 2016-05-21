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
name|adapter
operator|.
name|elasticsearch
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
name|RelOptRule
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
name|RelOptTable
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
name|TableScan
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
comment|/**  * Relational expression representing a scan of an Elasticsearch type.  *  *<p> Additional operations might be applied,  * using the "find" method.</p>  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchTableScan
extends|extends
name|TableScan
implements|implements
name|ElasticsearchRel
block|{
specifier|private
specifier|final
name|ElasticsearchTable
name|elasticsearchTable
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|projectRowType
decl_stmt|;
comment|/**    * Creates an ElasticsearchTableScan.    *    * @param cluster Cluster    * @param traitSet Trait set    * @param table Table    * @param elasticsearchTable Elasticsearch table    * @param projectRowType Fields and types to project; null to project raw row    */
specifier|protected
name|ElasticsearchTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|ElasticsearchTable
name|elasticsearchTable
parameter_list|,
name|RelDataType
name|projectRowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|elasticsearchTable
operator|=
name|elasticsearchTable
expr_stmt|;
name|this
operator|.
name|projectRowType
operator|=
name|projectRowType
expr_stmt|;
assert|assert
name|elasticsearchTable
operator|!=
literal|null
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|ElasticsearchRel
operator|.
name|CONVENTION
assert|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
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
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|projectRowType
operator|!=
literal|null
condition|?
name|projectRowType
else|:
name|super
operator|.
name|deriveRowType
argument_list|()
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
specifier|final
name|float
name|f
init|=
name|projectRowType
operator|==
literal|null
condition|?
literal|1f
else|:
operator|(
name|float
operator|)
name|projectRowType
operator|.
name|getFieldCount
argument_list|()
operator|/
literal|100f
decl_stmt|;
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|.1
operator|*
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|ElasticsearchToEnumerableConverterRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|ElasticsearchRules
operator|.
name|RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|elasticsearchTable
operator|=
name|elasticsearchTable
expr_stmt|;
name|implementor
operator|.
name|table
operator|=
name|table
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ElasticsearchTableScan.java
end_comment

end_unit

