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
name|rel
operator|.
name|RelCollationTraitDef
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
name|Sort
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
name|rex
operator|.
name|RexBuilder
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Planner rule that removes keys from a  * a {@link org.apache.calcite.rel.core.Sort} if those keys are known to be  * constant, or removes the entire Sort if all keys are constant.  *  *<p>Requires {@link RelCollationTraitDef}.  */
end_comment

begin_class
specifier|public
class|class
name|SortRemoveConstantKeysRule
extends|extends
name|RelOptRule
implements|implements
name|SubstitutionRule
block|{
comment|/** @deprecated Use {@link CoreRules#SORT_REMOVE_CONSTANT_KEYS}. */
annotation|@
name|Deprecated
comment|// to be removed before 1.25
specifier|public
specifier|static
specifier|final
name|SortRemoveConstantKeysRule
name|INSTANCE
init|=
name|CoreRules
operator|.
name|SORT_REMOVE_CONSTANT_KEYS
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"DeprecatedIsStillUsed"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 1.25
name|SortRemoveConstantKeysRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Sort
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"SortRemoveConstantKeysRule"
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
name|Sort
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
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|sort
operator|.
name|getInput
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
name|input
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
name|RexBuilder
name|rexBuilder
init|=
name|sort
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|collationsList
init|=
name|sort
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|fc
lambda|->
operator|!
name|predicates
operator|.
name|constantMap
operator|.
name|containsKey
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|input
argument_list|,
name|fc
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|collationsList
operator|.
name|size
argument_list|()
operator|==
name|sort
operator|.
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// No active collations. Remove the sort completely
if|if
condition|(
name|collationsList
operator|.
name|isEmpty
argument_list|()
operator|&&
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
condition|)
block|{
name|call
operator|.
name|transformTo
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|prune
argument_list|(
name|sort
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Sort
name|result
init|=
name|sort
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
argument_list|,
name|RelCollations
operator|.
name|of
argument_list|(
name|collationsList
argument_list|)
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|prune
argument_list|(
name|sort
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

