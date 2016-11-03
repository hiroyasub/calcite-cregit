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
name|RelOptRuleOperand
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
name|Aggregate
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
name|AggregateCall
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
name|runtime
operator|.
name|PredicateImpl
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
name|mapping
operator|.
name|Mapping
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
name|MappingType
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
name|base
operator|.
name|Function
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
name|Predicate
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
name|Iterables
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Rule to extract a {@link org.apache.calcite.rel.core.Project}  * from an {@link org.apache.calcite.rel.core.Aggregate}  * and push it down towards the input.  *  *<p>What projections can be safely pushed down depends upon which fields the  * Aggregate uses.  *  *<p>To prevent cycles, this rule will not extract a {@code Project} if the  * {@code Aggregate}s input is already a {@code Project}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateExtractProjectRule
extends|extends
name|RelOptRule
block|{
comment|/** Predicate that prevents matching against an {@code Aggregate} whose input    * is already a {@code Project}. This will prevent this rule firing    * repeatedly. */
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|RelNode
argument_list|>
name|PREDICATE
init|=
operator|new
name|PredicateImpl
argument_list|<
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|test
parameter_list|(
annotation|@
name|Nullable
name|RelNode
name|relNode
parameter_list|)
block|{
return|return
operator|!
operator|(
name|relNode
operator|instanceof
name|Project
operator|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Creates an AggregateExtractProjectRule.    *    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|AggregateExtractProjectRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|inputClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|operand
argument_list|(
name|aggregateClass
argument_list|,
name|operand
argument_list|(
name|inputClass
argument_list|,
literal|null
argument_list|,
name|PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AggregateExtractProjectRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|builderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|builderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Aggregate
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Compute which input fields are used.
comment|// 1. group fields are always used
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|inputFieldsUsed
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|rebuild
argument_list|()
decl_stmt|;
comment|// 2. agg functions
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
range|:
name|aggCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|inputFieldsUsed
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggCall
operator|.
name|filterArg
operator|>=
literal|0
condition|)
block|{
name|inputFieldsUsed
operator|.
name|set
argument_list|(
name|aggCall
operator|.
name|filterArg
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
operator|.
name|push
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Mapping
name|mapping
init|=
name|Mappings
operator|.
name|create
argument_list|(
name|MappingType
operator|.
name|INVERSE_SURJECTION
argument_list|,
name|aggregate
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|inputFieldsUsed
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|inputFieldsUsed
operator|.
name|build
argument_list|()
control|)
block|{
name|projects
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|j
operator|++
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|project
argument_list|(
name|projects
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableBitSet
name|newGroupSet
init|=
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|newGroupSets
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ImmutableBitSet
argument_list|,
name|ImmutableBitSet
argument_list|>
argument_list|()
block|{
specifier|public
name|ImmutableBitSet
name|apply
parameter_list|(
name|ImmutableBitSet
name|input
parameter_list|)
block|{
return|return
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelBuilder
operator|.
name|AggCall
argument_list|>
name|newAggCallList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|args
init|=
name|relBuilder
operator|.
name|fields
argument_list|(
name|Mappings
operator|.
name|apply2
argument_list|(
name|mapping
argument_list|,
name|aggCall
operator|.
name|getArgList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|filterArg
init|=
name|aggCall
operator|.
name|filterArg
operator|<
literal|0
condition|?
literal|null
else|:
name|relBuilder
operator|.
name|field
argument_list|(
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|aggCall
operator|.
name|filterArg
argument_list|)
argument_list|)
decl_stmt|;
name|newAggCallList
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|aggregateCall
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|aggCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|aggCall
operator|.
name|isApproximate
argument_list|()
argument_list|,
name|filterArg
argument_list|,
name|aggCall
operator|.
name|name
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelBuilder
operator|.
name|GroupKey
name|groupKey
init|=
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|newGroupSet
argument_list|,
name|newGroupSets
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|groupKey
argument_list|,
name|newAggCallList
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
comment|// End AggregateExtractProjectRule.java
end_comment

end_unit
