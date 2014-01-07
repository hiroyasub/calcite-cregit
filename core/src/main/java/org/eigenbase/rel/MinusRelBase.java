begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelTraitSet
import|;
end_import

begin_comment
comment|/**  * Abstract base class for implementations of  * {@link MinusRel}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MinusRelBase
extends|extends
name|SetOpRel
block|{
specifier|public
name|MinusRelBase
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|inputs
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a MinusRelBase by parsing serialized output.    */
specifier|protected
name|MinusRelBase
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
annotation|@
name|Override
specifier|public
name|double
name|getRows
parameter_list|()
block|{
comment|// REVIEW jvs 30-May-2005:  I just pulled this out of a hat.
name|double
name|dRows
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|inputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|dRows
operator|-=
literal|0.5
operator|*
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|inputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dRows
operator|<
literal|0
condition|)
block|{
name|dRows
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|dRows
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
return|return
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
operator|||
name|super
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MinusRelBase.java
end_comment

end_unit

