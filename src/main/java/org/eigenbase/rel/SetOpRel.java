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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|FlatLists
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
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
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  *<code>SetOpRel</code> is an abstract base for relational set operators such  * as UNION, MINUS (aka EXCEPT), and INTERSECT.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SetOpRel
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|all
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SetOpRel
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|inputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|abstract
name|SetOpRel
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
parameter_list|,
name|boolean
name|all
parameter_list|)
function_decl|;
specifier|public
name|SetOpRel
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
name|inputs
argument_list|,
name|all
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
block|{
specifier|final
name|RelNode
index|[]
name|newInputs
init|=
name|inputs
operator|.
name|toArray
argument_list|(
operator|new
name|RelNode
index|[
name|inputs
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|newInputs
index|[
name|ordinalInParent
index|]
operator|=
name|p
expr_stmt|;
name|inputs
operator|=
name|FlatLists
operator|.
name|of
argument_list|(
name|newInputs
argument_list|)
expr_stmt|;
name|recomputeDigest
argument_list|()
expr_stmt|;
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
comment|// If not ALL then the rows are distinct.
comment|// Therefore the set of all columns is a key.
return|return
operator|!
name|all
operator|&&
name|columns
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
operator|>=
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|inputs
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelNode
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|inputs
argument_list|)
control|)
block|{
name|pw
operator|.
name|input
argument_list|(
literal|"input#"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
operator|.
name|item
argument_list|(
literal|"all"
argument_list|,
name|all
argument_list|)
return|;
block|}
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|leastRestrictive
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataType
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|inputs
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|inputs
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Returns whether all the inputs of this set operator have the same row      * type as its output row.      *      * @param compareNames whether or not column names are important in the      * homogeneity comparison      */
specifier|public
name|boolean
name|isHomogeneous
parameter_list|(
name|boolean
name|compareNames
parameter_list|)
block|{
name|RelDataType
name|unionType
init|=
name|getRowType
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|getInputs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|areRowTypesEqual
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|unionType
argument_list|,
name|compareNames
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns whether all the inputs of this set operator have the same row      * type as its output row. Equivalent to {@link #isHomogeneous(boolean)      * isHomogeneous(true)}.      */
specifier|public
name|boolean
name|isHomogeneous
parameter_list|()
block|{
return|return
name|isHomogeneous
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SetOpRel.java
end_comment

end_unit

