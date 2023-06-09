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
name|RelFactories
operator|.
name|ProjectFactory
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
name|RexUtil
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
name|Permutation
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
name|List
import|;
end_import

begin_comment
comment|/**  * ProjectMergeRule merges a {@link org.apache.calcite.rel.core.Project} into  * another {@link org.apache.calcite.rel.core.Project},  * provided the projects aren't projecting identical sets of input references.  *  * @see CoreRules#PROJECT_MERGE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|ProjectMergeRule
extends|extends
name|RelRule
argument_list|<
name|ProjectMergeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Default amount by which complexity is allowed to increase.    *    * @see Config#bloat() */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BLOAT
init|=
literal|100
decl_stmt|;
comment|/** Creates a ProjectMergeRule. */
specifier|protected
name|ProjectMergeRule
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
name|ProjectMergeRule
parameter_list|(
name|boolean
name|force
parameter_list|,
name|int
name|bloat
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|CoreRules
operator|.
name|PROJECT_MERGE
operator|.
name|config
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
operator|.
name|withForce
argument_list|(
name|force
argument_list|)
operator|.
name|withBloat
argument_list|(
name|bloat
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ProjectMergeRule
parameter_list|(
name|boolean
name|force
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|CoreRules
operator|.
name|PROJECT_MERGE
operator|.
name|config
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
operator|.
name|withForce
argument_list|(
name|force
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ProjectMergeRule
parameter_list|(
name|boolean
name|force
parameter_list|,
name|ProjectFactory
name|projectFactory
parameter_list|)
block|{
name|this
argument_list|(
name|CoreRules
operator|.
name|PROJECT_MERGE
operator|.
name|config
operator|.
name|withRelBuilderFactory
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|projectFactory
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withForce
argument_list|(
name|force
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Project
name|topProject
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Project
name|bottomProject
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|topProject
operator|.
name|getConvention
argument_list|()
operator|==
name|bottomProject
operator|.
name|getConvention
argument_list|()
return|;
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
name|topProject
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Project
name|bottomProject
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// If one or both projects are permutations, short-circuit the complex logic
comment|// of building a RexProgram.
specifier|final
name|Permutation
name|topPermutation
init|=
name|topProject
operator|.
name|getPermutation
argument_list|()
decl_stmt|;
if|if
condition|(
name|topPermutation
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|topPermutation
operator|.
name|isIdentity
argument_list|()
condition|)
block|{
comment|// Let ProjectRemoveRule handle this.
return|return;
block|}
specifier|final
name|Permutation
name|bottomPermutation
init|=
name|bottomProject
operator|.
name|getPermutation
argument_list|()
decl_stmt|;
if|if
condition|(
name|bottomPermutation
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bottomPermutation
operator|.
name|isIdentity
argument_list|()
condition|)
block|{
comment|// Let ProjectRemoveRule handle this.
return|return;
block|}
specifier|final
name|Permutation
name|product
init|=
name|topPermutation
operator|.
name|product
argument_list|(
name|bottomPermutation
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|bottomProject
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|product
argument_list|)
argument_list|,
name|topProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// If we're not in force mode and the two projects reference identical
comment|// inputs, then return and let ProjectRemoveRule replace the projects.
if|if
condition|(
operator|!
name|config
operator|.
name|force
argument_list|()
condition|)
block|{
if|if
condition|(
name|RexUtil
operator|.
name|isIdentity
argument_list|(
name|topProject
operator|.
name|getProjects
argument_list|()
argument_list|,
name|topProject
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|newProjects
init|=
name|RelOptUtil
operator|.
name|pushPastProjectUnlessBloat
argument_list|(
name|topProject
operator|.
name|getProjects
argument_list|()
argument_list|,
name|bottomProject
argument_list|,
name|config
operator|.
name|bloat
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newProjects
operator|==
literal|null
condition|)
block|{
comment|// Merged projects are significantly more complex. Do not merge.
return|return;
block|}
specifier|final
name|RelNode
name|input
init|=
name|bottomProject
operator|.
name|getInput
argument_list|()
decl_stmt|;
if|if
condition|(
name|RexUtil
operator|.
name|isIdentity
argument_list|(
name|newProjects
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|config
operator|.
name|force
argument_list|()
operator|||
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|equals
argument_list|(
name|topProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// replace the two projects with a combined projection
name|relBuilder
operator|.
name|push
argument_list|(
name|bottomProject
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|newProjects
argument_list|,
name|topProject
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
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
name|ImmutableProjectMergeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|Project
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectMergeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectMergeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Limit how much complexity can increase during merging.      * Default is {@link #DEFAULT_BLOAT} (100). */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|int
name|bloat
parameter_list|()
block|{
return|return
name|ProjectMergeRule
operator|.
name|DEFAULT_BLOAT
return|;
block|}
comment|/** Sets {@link #bloat()}. */
name|Config
name|withBloat
parameter_list|(
name|int
name|bloat
parameter_list|)
function_decl|;
comment|/** Whether to always merge projects, default true. */
annotation|@
name|Value
operator|.
name|Default
specifier|default
name|boolean
name|force
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Sets {@link #force()}. */
name|Config
name|withForce
parameter_list|(
name|boolean
name|force
parameter_list|)
function_decl|;
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
name|projectClass
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

