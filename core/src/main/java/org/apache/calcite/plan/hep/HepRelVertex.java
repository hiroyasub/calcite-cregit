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
name|plan
operator|.
name|hep
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
name|AbstractRelNode
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
comment|/**  * HepRelVertex wraps a real {@link RelNode} as a vertex in a DAG representing  * the entire query expression.  */
end_comment

begin_class
specifier|public
class|class
name|HepRelVertex
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Wrapped rel currently chosen for implementation of expression.    */
specifier|private
name|RelNode
name|currentRel
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|HepRelVertex
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
argument_list|)
expr_stmt|;
name|currentRel
operator|=
name|rel
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|explain
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|currentRel
operator|.
name|explain
argument_list|(
name|pw
argument_list|)
expr_stmt|;
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
name|traitSet
operator|.
name|equals
argument_list|(
name|this
operator|.
name|traitSet
argument_list|)
assert|;
assert|assert
name|inputs
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getInputs
argument_list|()
argument_list|)
assert|;
return|return
name|this
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
comment|// HepRelMetadataProvider is supposed to intercept this
comment|// and redirect to the real rels. But sometimes it doesn't.
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeTinyCost
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getRowCount
argument_list|(
name|currentRel
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|currentRel
operator|.
name|getRowType
argument_list|()
return|;
block|}
comment|/**    * Replaces the implementation for this expression with a new one.    *    * @param newRel new expression    */
name|void
name|replaceRel
parameter_list|(
name|RelNode
name|newRel
parameter_list|)
block|{
name|currentRel
operator|=
name|newRel
expr_stmt|;
block|}
comment|/**    * @return current implementation chosen for this vertex    */
specifier|public
name|RelNode
name|getCurrentRel
parameter_list|()
block|{
return|return
name|currentRel
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
specifier|final
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
literal|"currentRel"
argument_list|,
name|currentRel
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDigest
parameter_list|()
block|{
return|return
literal|"HepRelVertex("
operator|+
name|currentRel
operator|+
literal|')'
return|;
block|}
block|}
end_class

end_unit

