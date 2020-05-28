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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelOptSchema
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
name|RelOptTable
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
name|RelShuttle
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
name|hint
operator|.
name|Hintable
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
name|hint
operator|.
name|RelHint
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|RexBuilder
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
name|tools
operator|.
name|RelBuilder
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
name|util
operator|.
name|ImmutableBitSet
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
name|util
operator|.
name|ImmutableIntList
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
comment|/**  * Relational operator that returns the contents of a table.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TableScan
extends|extends
name|AbstractRelNode
implements|implements
name|Hintable
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * The table definition.    */
specifier|protected
specifier|final
name|RelOptTable
name|table
decl_stmt|;
comment|/**    * The table hints.    */
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|hints
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|TableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|RelOptSchema
name|relOptSchema
init|=
name|table
operator|.
name|getRelOptSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|relOptSchema
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|getPlanner
argument_list|()
operator|.
name|registerSchema
argument_list|(
name|relOptSchema
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|hints
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|hints
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|TableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a TableScan by parsing serialized output.    */
specifier|protected
name|TableScan
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
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|input
operator|.
name|getTable
argument_list|(
literal|"table"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|table
operator|.
name|getRowCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|double
name|dRows
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|double
name|dCpu
init|=
name|dRows
operator|+
literal|1
decl_stmt|;
comment|// ensure non-zero cost
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|table
operator|.
name|getRowType
argument_list|()
return|;
block|}
comment|/** Returns an identity projection for the given table. */
specifier|public
specifier|static
name|ImmutableIntList
name|identity
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|ImmutableIntList
operator|.
name|identity
argument_list|(
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns an identity projection. */
specifier|public
name|ImmutableIntList
name|identity
parameter_list|()
block|{
return|return
name|identity
argument_list|(
name|table
argument_list|)
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
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"table"
argument_list|,
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Projects a subset of the fields of the table, and also asks for "extra"    * fields that were not included in the table's official type.    *    *<p>The default implementation assumes that tables cannot do either of    * these operations, therefore it adds a {@link Project} that projects    * {@code NULL} values for the extra fields, using the    * {@link RelBuilder#project(Iterable)} method.    *    *<p>Sub-classes, representing table types that have these capabilities,    * should override.</p>    *    * @param fieldsUsed  Bitmap of the fields desired by the consumer    * @param extraFields Extra fields, not advertised in the table's row-type,    *                    wanted by the consumer    * @param relBuilder Builder used to create a Project    * @return Relational expression that projects the desired fields    */
specifier|public
name|RelNode
name|project
parameter_list|(
name|ImmutableBitSet
name|fieldsUsed
parameter_list|,
name|Set
argument_list|<
name|RelDataTypeField
argument_list|>
name|extraFields
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|)
block|{
specifier|final
name|int
name|fieldCount
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldsUsed
operator|.
name|equals
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|fieldCount
argument_list|)
argument_list|)
operator|&&
name|extraFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
name|int
name|fieldSize
init|=
name|fieldsUsed
operator|.
name|size
argument_list|()
operator|+
name|extraFields
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fieldSize
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fieldSize
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
comment|// Project the subset of fields.
for|for
control|(
name|int
name|i
range|:
name|fieldsUsed
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|exprList
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|this
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|nameList
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Project nulls for the extra fields. (Maybe a sub-class table has
comment|// extra fields, but we don't.)
for|for
control|(
name|RelDataTypeField
name|extraField
range|:
name|extraFields
control|)
block|{
name|exprList
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|extraField
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nameList
operator|.
name|add
argument_list|(
name|extraField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|this
argument_list|)
operator|.
name|project
argument_list|(
name|exprList
argument_list|,
name|nameList
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|getHints
parameter_list|()
block|{
return|return
name|hints
return|;
block|}
block|}
end_class

end_unit

