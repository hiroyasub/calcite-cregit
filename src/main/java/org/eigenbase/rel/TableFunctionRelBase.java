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
name|rex
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
comment|/**  *<code>TableFunctionRelBase</code> is an abstract base class for  * implementations of {@link TableFunctionRel}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TableFunctionRelBase
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
name|RelDataType
name|rowType
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>TableFunctionRelBase</code>.      *      * @param cluster Cluster that this relational expression belongs to      * @param inputs 0 or more relational inputs      * @param rexCall function invocation expression      * @param rowType row type produced by function      * @param columnMappings column mappings associated with this function      */
specifier|protected
name|TableFunctionRelBase
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
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|columnMappings
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|RexNode
index|[]
name|getChildExps
parameter_list|()
block|{
return|return
operator|new
name|RexNode
index|[]
block|{
name|rexCall
block|}
return|;
block|}
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
name|int
name|i
init|=
literal|0
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
name|Double
name|d
init|=
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
specifier|public
name|RexNode
name|getCall
parameter_list|()
block|{
comment|// NOTE jvs 7-May-2006:  Within this rexCall, instances
comment|// of RexInputRef refer to entire input RelNodes rather
comment|// than their fields.
return|return
name|rexCall
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
literal|"invocation"
argument_list|,
name|rexCall
argument_list|)
return|;
block|}
comment|/**      * @return set of mappings known for this table function, or null if unknown      * (not the same as empty!)      */
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
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionRelBase.java
end_comment

end_unit

