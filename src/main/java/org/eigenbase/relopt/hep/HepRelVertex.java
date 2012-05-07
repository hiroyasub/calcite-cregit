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
name|relopt
operator|.
name|hep
package|;
end_package

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
name|*
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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * HepRelVertex wraps a real {@link RelNode} as a vertex in a DAG representing  * the entire query expression.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|HepRelVertex
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Wrapped rel currently chosen for implementation of expression.      */
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
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// HepRelMetadataProvider is supposed to intercept this
comment|// and redirect to the real rels.
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"should never get here"
argument_list|)
throw|;
block|}
comment|// implement RelNode
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|currentRel
argument_list|)
return|;
block|}
comment|// implement RelNode
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
comment|// implement RelNode
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|currentRel
operator|.
name|isDistinct
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|protected
name|String
name|computeDigest
parameter_list|()
block|{
return|return
literal|"HepRelVertex("
operator|+
name|currentRel
operator|+
literal|")"
return|;
block|}
comment|/**      * Replaces the implementation for this expression with a new one.      *      * @param newRel new expression      */
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
comment|/**      * @return current implementation chosen for this vertex      */
specifier|public
name|RelNode
name|getCurrentRel
parameter_list|()
block|{
return|return
name|currentRel
return|;
block|}
block|}
end_class

begin_comment
comment|// End HepRelVertex.java
end_comment

end_unit

