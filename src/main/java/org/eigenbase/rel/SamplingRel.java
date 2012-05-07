begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
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
comment|/**  * SamplingRel represents the TABLESAMPLE BERNOULLI or SYSTEM keyword applied to  * a table, view or subquery.  *  * @author Stephan Zuercher  */
end_comment

begin_class
specifier|public
class|class
name|SamplingRel
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelOptSamplingParameters
name|params
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SamplingRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RelOptSamplingParameters
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|comprises
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|SamplingRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|params
argument_list|)
return|;
block|}
comment|/**      * Retrieve the sampling parameters for this SamplingRel.      */
specifier|public
name|RelOptSamplingParameters
name|getSamplingParameters
parameter_list|()
block|{
return|return
name|params
return|;
block|}
comment|// implement RelNode
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|explain
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"child"
block|,
literal|"mode"
block|,
literal|"rate"
block|,
literal|"repeatableSeed"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|params
operator|.
name|isBernoulli
argument_list|()
condition|?
literal|"bernoulli"
else|:
literal|"system"
block|,
name|params
operator|.
name|getSamplingPercentage
argument_list|()
block|,
name|params
operator|.
name|isRepeatable
argument_list|()
condition|?
name|params
operator|.
name|getRepeatableSeed
argument_list|()
else|:
literal|"-"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SamplingRel.java
end_comment

end_unit

