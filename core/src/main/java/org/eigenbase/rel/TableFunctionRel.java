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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|RelColumnMapping
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
name|rex
operator|.
name|*
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
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  *<code>TableFunctionRel</code> represents a call to a function which returns a  * result set. Currently, it can only appear as a leaf in a query tree, but  * eventually we will extend it to take relational inputs.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionRel
extends|extends
name|TableFunctionRelBase
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>TableFunctionRel</code>.      *      * @param cluster Cluster that this relational expression belongs to      * @param inputs 0 or more relational inputs      * @param rexCall function invocation expression      * @param rowType row type produced by function      * @param columnMappings column mappings associated with this function      */
specifier|public
name|TableFunctionRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|RexNode
name|rexCall
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|inputs
argument_list|,
name|rexCall
argument_list|,
name|rowType
argument_list|,
name|columnMappings
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a TableFunctionRel by parsing serialized output. */
specifier|public
name|TableFunctionRel
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
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|TableFunctionRel
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
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|TableFunctionRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|inputs
argument_list|,
name|getCall
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|columnMappings
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// REVIEW jvs 8-Jan-2006:  what is supposed to be here
comment|// for an abstract rel?
return|return
name|planner
operator|.
name|makeHugeCost
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionRel.java
end_comment

end_unit

