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
name|stream
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Sub-class of {@link org.apache.calcite.rel.stream.Delta}  * not targeted at any particular engine or calling convention.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalDelta
extends|extends
name|Delta
block|{
comment|/**    * Creates a LogicalDelta.    *    *<p>Use {@link #create} unless you know what you're doing.    *    * @param cluster   Cluster that this relational expression belongs to    * @param input     Input relational expression    */
specifier|public
name|LogicalDelta
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a LogicalDelta by parsing serialized output. */
specifier|public
name|LogicalDelta
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a LogicalDelta. */
specifier|public
specifier|static
name|LogicalDelta
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|)
block|{
specifier|final
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
decl_stmt|;
return|return
operator|new
name|LogicalDelta
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
return|;
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
return|return
operator|new
name|LogicalDelta
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalDelta.java
end_comment

end_unit

