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
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|rex
operator|.
name|RexUtil
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
name|mapping
operator|.
name|Mappings
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
name|ImmutableMap
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes a {@link SortRel} past a {@link ProjectRel}.  */
end_comment

begin_class
specifier|public
class|class
name|PushSortPastProjectRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushSortPastProjectRule
name|INSTANCE
init|=
operator|new
name|PushSortPastProjectRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a PushSortPastProjectRule.      */
specifier|private
name|PushSortPastProjectRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|SortRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|SortRel
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ProjectRel
name|project
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
name|project
operator|.
name|getCluster
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|.
name|getConvention
argument_list|()
operator|!=
name|project
operator|.
name|getConvention
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Determine mapping between project input and output fields. If sort
comment|// relies on non-trivial expressions, we can't push.
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|map
init|=
name|RelOptUtil
operator|.
name|permutation
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|fc
range|:
name|sort
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
if|if
condition|(
name|map
operator|.
name|getTargetOpt
argument_list|(
name|fc
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|RelCollation
name|newCollation
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|()
operator|.
name|canonize
argument_list|(
name|RexUtil
operator|.
name|apply
argument_list|(
name|map
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SortRel
name|newSort
init|=
name|sort
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|newCollation
argument_list|)
argument_list|,
name|project
operator|.
name|getChild
argument_list|()
argument_list|,
name|newCollation
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
decl_stmt|;
name|RelNode
name|newProject
init|=
name|project
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelNode
operator|>
name|of
argument_list|(
name|newSort
argument_list|)
argument_list|)
decl_stmt|;
comment|// Not only is newProject equivalent to sort;
comment|// newSort is equivalent to project's input
comment|// (but only if the sort is not also applying an offset/limit).
name|Map
argument_list|<
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|equiv
init|=
name|sort
operator|.
name|offset
operator|==
literal|null
operator|&&
name|sort
operator|.
name|fetch
operator|==
literal|null
condition|?
name|ImmutableMap
operator|.
expr|<
name|RelNode
decl_stmt|,
name|RelNode
decl|>
name|of
argument_list|(
name|newSort
argument_list|,
name|project
operator|.
name|getChild
argument_list|()
argument_list|)
range|:
name|ImmutableMap
operator|.
expr|<
name|RelNode
decl_stmt|,
name|RelNode
decl|>
name|of
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newProject
argument_list|,
name|equiv
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PushSortPastProjectRule.java
end_comment

end_unit

