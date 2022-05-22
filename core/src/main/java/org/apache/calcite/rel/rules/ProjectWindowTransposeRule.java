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
name|rules
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
name|RelOptRuleCall
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
name|RelRule
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
name|RelCollations
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
name|RelFieldCollation
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
name|core
operator|.
name|Project
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
name|core
operator|.
name|Window
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
name|logical
operator|.
name|LogicalProject
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
name|logical
operator|.
name|LogicalWindow
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
name|RelDataTypeFactory
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
name|RexCall
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
name|RexInputRef
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlAggFunction
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
name|RelBuilderFactory
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
name|BitSets
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
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
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

begin_comment
comment|/**  * Planner rule that pushes  * a {@link org.apache.calcite.rel.logical.LogicalProject}  * past a {@link org.apache.calcite.rel.logical.LogicalWindow}.  *  * @see CoreRules#PROJECT_WINDOW_TRANSPOSE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|ProjectWindowTransposeRule
extends|extends
name|RelRule
argument_list|<
name|ProjectWindowTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a ProjectWindowTransposeRule. */
specifier|protected
name|ProjectWindowTransposeRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ProjectWindowTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Window
name|window
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|window
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|rowTypeWindowInput
init|=
name|window
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|windowInputColumn
init|=
name|rowTypeWindowInput
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Record the window input columns which are actually referred
comment|// either in the LogicalProject above LogicalWindow or LogicalWindow itself
comment|// (Note that the constants used in LogicalWindow are not considered here)
specifier|final
name|ImmutableBitSet
name|beReferred
init|=
name|findReference
argument_list|(
name|project
argument_list|,
name|window
argument_list|)
decl_stmt|;
comment|// If all the window input columns are referred,
comment|// it is impossible to trim anyone of them out
if|if
condition|(
name|beReferred
operator|.
name|cardinality
argument_list|()
operator|==
name|windowInputColumn
condition|)
block|{
return|return;
block|}
comment|// Put a Project below LogicalWindow
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// Keep only the fields which are referred
for|for
control|(
name|int
name|index
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|beReferred
argument_list|)
control|)
block|{
specifier|final
name|RelDataTypeField
name|relDataTypeField
init|=
name|rowTypeWindowInput
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|exps
operator|.
name|add
argument_list|(
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|relDataTypeField
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|relDataTypeField
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LogicalProject
name|projectBelowWindow
init|=
operator|new
name|LogicalProject
argument_list|(
name|cluster
argument_list|,
name|window
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|window
operator|.
name|getInput
argument_list|()
argument_list|,
name|exps
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create a new LogicalWindow with necessary inputs only
specifier|final
name|List
argument_list|<
name|Window
operator|.
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// As the un-referred columns are trimmed by the LogicalProject,
comment|// the indices specified in LogicalWindow would need to be adjusted
specifier|final
name|RexShuttle
name|indexAdjustment
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
specifier|final
name|int
name|newIndex
init|=
name|getAdjustedIndex
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|,
name|beReferred
argument_list|,
name|windowInputColumn
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexInputRef
argument_list|(
name|newIndex
argument_list|,
name|inputRef
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitCall
parameter_list|(
specifier|final
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|instanceof
name|Window
operator|.
name|RexWinAggCall
condition|)
block|{
specifier|final
name|Window
operator|.
name|RexWinAggCall
name|aggCall
init|=
operator|(
name|Window
operator|.
name|RexWinAggCall
operator|)
name|call
decl_stmt|;
name|boolean
index|[]
name|update
init|=
block|{
literal|false
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|clonedOperands
init|=
name|visitList
argument_list|(
name|call
operator|.
name|operands
argument_list|,
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
name|update
index|[
literal|0
index|]
condition|)
block|{
return|return
operator|new
name|Window
operator|.
name|RexWinAggCall
argument_list|(
operator|(
name|SqlAggFunction
operator|)
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|call
operator|.
name|getType
argument_list|()
argument_list|,
name|clonedOperands
argument_list|,
name|aggCall
operator|.
name|ordinal
argument_list|,
name|aggCall
operator|.
name|distinct
argument_list|,
name|aggCall
operator|.
name|ignoreNulls
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|call
return|;
block|}
block|}
else|else
block|{
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|int
name|aggCallIndex
init|=
name|windowInputColumn
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|outputBuilder
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|outputBuilder
operator|.
name|addAll
argument_list|(
name|projectBelowWindow
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Window
operator|.
name|Group
name|group
range|:
name|window
operator|.
name|groups
control|)
block|{
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|keys
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|orderKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Window
operator|.
name|RexWinAggCall
argument_list|>
name|aggCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Adjust keys
for|for
control|(
name|int
name|index
range|:
name|group
operator|.
name|keys
control|)
block|{
name|keys
operator|.
name|set
argument_list|(
name|getAdjustedIndex
argument_list|(
name|index
argument_list|,
name|beReferred
argument_list|,
name|windowInputColumn
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Adjust orderKeys
for|for
control|(
name|RelFieldCollation
name|relFieldCollation
range|:
name|group
operator|.
name|orderKeys
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
specifier|final
name|int
name|index
init|=
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
name|orderKeys
operator|.
name|add
argument_list|(
name|relFieldCollation
operator|.
name|withFieldIndex
argument_list|(
name|getAdjustedIndex
argument_list|(
name|index
argument_list|,
name|beReferred
argument_list|,
name|windowInputColumn
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Adjust Window Functions
for|for
control|(
name|Window
operator|.
name|RexWinAggCall
name|rexWinAggCall
range|:
name|group
operator|.
name|aggCalls
control|)
block|{
name|aggCalls
operator|.
name|add
argument_list|(
operator|(
name|Window
operator|.
name|RexWinAggCall
operator|)
name|rexWinAggCall
operator|.
name|accept
argument_list|(
name|indexAdjustment
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataTypeField
name|relDataTypeField
init|=
name|window
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|aggCallIndex
argument_list|)
decl_stmt|;
name|outputBuilder
operator|.
name|add
argument_list|(
name|relDataTypeField
argument_list|)
expr_stmt|;
operator|++
name|aggCallIndex
expr_stmt|;
block|}
name|groups
operator|.
name|add
argument_list|(
operator|new
name|Window
operator|.
name|Group
argument_list|(
name|keys
operator|.
name|build
argument_list|()
argument_list|,
name|group
operator|.
name|isRows
argument_list|,
name|group
operator|.
name|lowerBound
argument_list|,
name|group
operator|.
name|upperBound
argument_list|,
name|RelCollations
operator|.
name|of
argument_list|(
name|orderKeys
argument_list|)
argument_list|,
name|aggCalls
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LogicalWindow
name|newLogicalWindow
init|=
name|LogicalWindow
operator|.
name|create
argument_list|(
name|window
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|projectBelowWindow
argument_list|,
name|window
operator|.
name|constants
argument_list|,
name|outputBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|groups
argument_list|)
decl_stmt|;
comment|// Modify the top LogicalProject
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|topProjExps
init|=
name|indexAdjustment
operator|.
name|visitList
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Project
name|newTopProj
init|=
name|project
operator|.
name|copy
argument_list|(
name|newLogicalWindow
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newLogicalWindow
argument_list|,
name|topProjExps
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ProjectRemoveRule
operator|.
name|isTrivial
argument_list|(
name|newTopProj
argument_list|)
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|newLogicalWindow
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|newTopProj
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|ImmutableBitSet
name|findReference
parameter_list|(
specifier|final
name|Project
name|project
parameter_list|,
specifier|final
name|Window
name|window
parameter_list|)
block|{
specifier|final
name|int
name|windowInputColumn
init|=
name|window
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|beReferred
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexShuttle
name|referenceFinder
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|inputRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|windowInputColumn
condition|)
block|{
name|beReferred
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|inputRef
return|;
block|}
block|}
decl_stmt|;
comment|// Reference in LogicalProject
name|referenceFinder
operator|.
name|visitEach
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reference in LogicalWindow
for|for
control|(
name|Window
operator|.
name|Group
name|group
range|:
name|window
operator|.
name|groups
control|)
block|{
comment|// Reference in Partition-By
for|for
control|(
name|int
name|index
range|:
name|group
operator|.
name|keys
control|)
block|{
if|if
condition|(
name|index
operator|<
name|windowInputColumn
condition|)
block|{
name|beReferred
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Reference in Order-By
for|for
control|(
name|RelFieldCollation
name|relFieldCollation
range|:
name|group
operator|.
name|orderKeys
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
if|if
condition|(
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
operator|<
name|windowInputColumn
condition|)
block|{
name|beReferred
operator|.
name|set
argument_list|(
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Reference in Window Functions
name|referenceFinder
operator|.
name|visitEach
argument_list|(
name|group
operator|.
name|aggCalls
argument_list|)
expr_stmt|;
block|}
return|return
name|beReferred
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|int
name|getAdjustedIndex
parameter_list|(
specifier|final
name|int
name|initIndex
parameter_list|,
specifier|final
name|ImmutableBitSet
name|beReferred
parameter_list|,
specifier|final
name|int
name|windowInputColumn
parameter_list|)
block|{
if|if
condition|(
name|initIndex
operator|>=
name|windowInputColumn
condition|)
block|{
return|return
name|beReferred
operator|.
name|cardinality
argument_list|()
operator|+
operator|(
name|initIndex
operator|-
name|windowInputColumn
operator|)
return|;
block|}
else|else
block|{
return|return
name|beReferred
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|initIndex
argument_list|)
operator|.
name|cardinality
argument_list|()
return|;
block|}
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableProjectWindowTransposeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|LogicalWindow
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectWindowTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectWindowTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Project
argument_list|>
name|projectClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Window
argument_list|>
name|windowClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|projectClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|windowClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

