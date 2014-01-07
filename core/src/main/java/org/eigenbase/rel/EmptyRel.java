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
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>EmptyRel</code> represents a relational expression with zero rows.  *  *<p>EmptyRel can not be implemented, but serves as a token for rules to match  * so that empty sections of queries can be eliminated.  *  *<p>Rules:  *  *<ul>  *<li>Created by {@code net.sf.farrago.query.FarragoReduceValuesRule}</li>  *<li>Triggers {@link org.eigenbase.rel.rules.RemoveEmptyRule}</li>  *</ul>  *  * @see org.eigenbase.rel.ValuesRel  */
end_comment

begin_class
specifier|public
class|class
name|EmptyRel
extends|extends
name|AbstractRelNode
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new EmptyRel.    *    * @param cluster Cluster    * @param rowType row type for tuples which would be produced by this rel if    *                it actually produced any, but it doesn't (see, philosophy is    *                good for something after all!)    */
specifier|public
name|EmptyRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
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
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
comment|// immutable with no children
return|return
name|this
return|;
block|}
comment|// implement RelNode
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|rowType
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
return|return
name|planner
operator|.
name|makeZeroCost
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
literal|0.0
return|;
block|}
comment|// implement RelNode
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
comment|// For rel digest, include the row type to discriminate
comment|// this from other empties with different row types.
comment|// For normal EXPLAIN PLAN, omit the type.
operator|.
name|itemIf
argument_list|(
literal|"type"
argument_list|,
name|rowType
argument_list|,
name|pw
operator|.
name|getDetailLevel
argument_list|()
operator|==
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EmptyRel.java
end_comment

end_unit

