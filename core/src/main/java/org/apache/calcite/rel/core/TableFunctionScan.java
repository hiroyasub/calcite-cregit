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
name|metadata
operator|.
name|RelColumnMapping
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexShuttle
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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

begin_comment
comment|/**  * Relational expression that calls a table-valued function.  *  *<p>The function returns a result set.  * It can appear as a leaf in a query tree,  * or can be applied to relational inputs.  *  * @see org.apache.calcite.rel.logical.LogicalTableFunctionScan  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TableFunctionScan
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexNode
name|rexCall
decl_stmt|;
specifier|private
specifier|final
name|Type
name|elementType
decl_stmt|;
specifier|private
name|ImmutableList
argument_list|<
name|RelNode
argument_list|>
name|inputs
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableSet
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a<code>TableFunctionScan</code>.    *    * @param cluster        Cluster that this relational expression belongs to    * @param inputs         0 or more relational inputs    * @param rexCall        Function invocation expression    * @param elementType    Element type of the collection that will implement    *                       this table    * @param rowType        Row type produced by function    * @param columnMappings Column mappings associated with this function    */
specifier|protected
name|TableFunctionScan
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
name|RexNode
name|rexCall
parameter_list|,
name|Type
name|elementType
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
name|traits
argument_list|)
expr_stmt|;
name|this
operator|.
name|rexCall
operator|=
name|rexCall
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
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
name|columnMappings
operator|=
name|columnMappings
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|columnMappings
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a TableFunctionScan by parsing serialized output.    */
specifier|protected
name|TableFunctionScan
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
name|input
operator|.
name|getExpression
argument_list|(
literal|"invocation"
argument_list|)
argument_list|,
operator|(
name|Type
operator|)
name|input
operator|.
name|get
argument_list|(
literal|"elementType"
argument_list|)
argument_list|,
name|input
operator|.
name|getRowType
argument_list|(
literal|"rowType"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|RelColumnMapping
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|TableFunctionScan
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
name|rexCall
argument_list|,
name|elementType
argument_list|,
name|rowType
argument_list|,
name|columnMappings
argument_list|)
return|;
block|}
comment|/**    * Copies this relational expression, substituting traits and    * inputs.    *    * @param traitSet       Traits    * @param inputs         0 or more relational inputs    * @param rexCall        Function invocation expression    * @param elementType    Element type of the collection that will implement    *                       this table    * @param rowType        Row type produced by function    * @param columnMappings Column mappings associated with this function    * @return Copy of this relational expression, substituting traits and    * inputs    */
specifier|public
specifier|abstract
name|TableFunctionScan
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
name|RexNode
name|rexCall
parameter_list|,
name|Type
name|elementType
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
function_decl|;
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
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|rexCall
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|RexNode
name|rexCall
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|rexCall
argument_list|)
decl_stmt|;
if|if
condition|(
name|rexCall
operator|==
name|this
operator|.
name|rexCall
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|inputs
argument_list|,
name|rexCall
argument_list|,
name|elementType
argument_list|,
name|rowType
argument_list|,
name|columnMappings
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
name|double
name|getRows
parameter_list|()
block|{
comment|// Calculate result as the sum of the input rowcount estimates,
comment|// assuming there are any, otherwise use the superclass default.  So
comment|// for a no-input UDX, behave like an AbstractRelNode; for a one-input
comment|// UDX, behave like a SingleRel; for a multi-input UDX, behave like
comment|// UNION ALL.  TODO jvs 10-Sep-2007: UDX-supplied costing metadata.
if|if
condition|(
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|super
operator|.
name|getRows
argument_list|()
return|;
block|}
name|double
name|nRows
init|=
literal|0.0
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|Double
name|d
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|nRows
operator|+=
name|d
expr_stmt|;
block|}
block|}
return|return
name|nRows
return|;
block|}
comment|/**    * Returns function invocation expression.    *    *<p>Within this rexCall, instances of    * {@link org.apache.calcite.rex.RexInputRef} refer to entire input    * {@link org.apache.calcite.rel.RelNode}s rather than their fields.    *    * @return function invocation expression    */
specifier|public
name|RexNode
name|getCall
parameter_list|()
block|{
return|return
name|rexCall
return|;
block|}
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
name|pw
operator|.
name|item
argument_list|(
literal|"invocation"
argument_list|,
name|rexCall
argument_list|)
operator|.
name|item
argument_list|(
literal|"rowType"
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
if|if
condition|(
name|elementType
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"elementType"
argument_list|,
name|elementType
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
return|;
block|}
comment|/**    * Returns set of mappings known for this table function, or null if unknown    * (not the same as empty!).    *    * @return set of mappings known for this table function, or null if unknown    * (not the same as empty!)    */
specifier|public
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|getColumnMappings
parameter_list|()
block|{
return|return
name|columnMappings
return|;
block|}
comment|/**    * Returns element type of the collection that will implement this table.    *    * @return element type of the collection that will implement this table    */
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|elementType
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionScan.java
end_comment

end_unit

