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
name|core
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
name|linq4j
operator|.
name|Ord
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
name|RelOptUtil
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
name|type
operator|.
name|RelDataType
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
name|sql
operator|.
name|SqlKind
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
name|base
operator|.
name|Preconditions
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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

begin_comment
comment|/**  *<code>SetOp</code> is an abstract base for relational set operators such  * as UNION, MINUS (aka EXCEPT), and INTERSECT.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SetOp
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|ImmutableList
argument_list|<
name|RelNode
argument_list|>
name|inputs
decl_stmt|;
specifier|public
specifier|final
name|SqlKind
name|kind
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|all
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SetOp.    */
specifier|protected
name|SetOp
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
name|SqlKind
name|kind
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|kind
operator|==
name|SqlKind
operator|.
name|UNION
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|INTERSECT
operator|||
name|kind
operator|==
name|SqlKind
operator|.
name|EXCEPT
argument_list|)
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|kind
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
comment|/**    * Creates a SetOp by parsing serialized output.    */
specifier|protected
name|SetOp
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInputs
argument_list|()
argument_list|,
name|SqlKind
operator|.
name|UNION
argument_list|,
name|input
operator|.
name|getBoolean
argument_list|(
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|abstract
name|SetOp
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
annotation|@
name|Override
specifier|public
name|SetOp
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
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|newInputs
operator|.
name|set
argument_list|(
name|ordinalInParent
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|inputs
operator|=
name|ImmutableList
operator|.
name|copyOf
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|/**    * Returns whether all the inputs of this set operator have the same row    * type as its output row.    *    * @param compareNames Whether column names are important in the    *                     homogeneity comparison    */
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
block|}
end_class

begin_comment
comment|// End SetOp.java
end_comment

end_unit

