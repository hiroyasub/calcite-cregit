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
name|RelOptPredicateList
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
name|RelOptRule
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
name|RelFactories
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
name|Union
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
name|Pair
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
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Planner rule that pulls up constants through a Union operator.  */
end_comment

begin_class
specifier|public
class|class
name|UnionPullUpConstantsRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|UnionPullUpConstantsRule
name|INSTANCE
init|=
operator|new
name|UnionPullUpConstantsRule
argument_list|(
name|Union
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|/** Creates a UnionPullUpConstantsRule. */
specifier|protected
name|UnionPullUpConstantsRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|unionClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
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
name|Union
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|union
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
comment|// No room for optimization since we cannot create an empty Project
comment|// operator. If we created a Project with one column, this rule would
comment|// cycle.
return|return;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|union
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelOptPredicateList
name|predicates
init|=
name|mq
operator|.
name|getPulledUpPredicates
argument_list|(
name|union
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicates
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
name|constants
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RexNode
argument_list|,
name|RexNode
argument_list|>
name|e
range|:
name|predicates
operator|.
name|constantMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|instanceof
name|RexInputRef
condition|)
block|{
name|constants
operator|.
name|put
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|e
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// None of the expressions are constant. Nothing to do.
if|if
condition|(
name|constants
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Create expressions for Project operators before and after the Union
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|union
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|topChildExprs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|topChildExprsFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ImmutableBitSet
operator|.
name|Builder
name|refsIndexBuilder
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fields
control|)
block|{
specifier|final
name|RexNode
name|constant
init|=
name|constants
operator|.
name|get
argument_list|(
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|constant
operator|!=
literal|null
condition|)
block|{
name|topChildExprs
operator|.
name|add
argument_list|(
name|constant
argument_list|)
expr_stmt|;
name|topChildExprsFields
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
else|else
block|{
specifier|final
name|RexNode
name|expr
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|union
argument_list|,
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|topChildExprs
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|topChildExprsFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|refs
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|refsIndexBuilder
operator|.
name|set
argument_list|(
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableBitSet
name|refsIndex
init|=
name|refsIndexBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Update top Project positions
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|RelOptUtil
operator|.
name|permutation
argument_list|(
name|refs
argument_list|,
name|union
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
operator|.
name|getRowType
argument_list|()
argument_list|)
operator|.
name|inverse
argument_list|()
decl_stmt|;
name|topChildExprs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|topChildExprs
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create new Project-Union-Project sequences
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|union
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|newChildExprs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
range|:
name|refsIndex
control|)
block|{
name|newChildExprs
operator|.
name|add
argument_list|(
name|Pair
operator|.
expr|<
name|RexNode
argument_list|,
name|String
operator|>
name|of
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|input
argument_list|,
name|j
argument_list|)
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newChildExprs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// At least a single item in project is required.
name|newChildExprs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|topChildExprs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|topChildExprsFields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add the input with project on top
name|relBuilder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|newChildExprs
argument_list|)
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|newChildExprs
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|union
argument_list|(
name|union
operator|.
name|all
argument_list|,
name|union
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create top Project fixing nullability of fields
name|relBuilder
operator|.
name|project
argument_list|(
name|topChildExprs
argument_list|,
name|topChildExprsFields
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|convert
argument_list|(
name|union
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
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
block|}
end_class

begin_comment
comment|// End UnionPullUpConstantsRule.java
end_comment

end_unit

