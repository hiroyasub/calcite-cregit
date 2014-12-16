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
name|Filter
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
name|Lists
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
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Filter}  * past a {@link org.apache.calcite.rel.core.Aggregate}.  *  * @see org.apache.calcite.rel.rules.AggregateFilterTransposeRule  */
end_comment

begin_class
specifier|public
class|class
name|FilterAggregateTransposeRule
extends|extends
name|RelOptRule
block|{
comment|/** The default instance of    * {@link FilterAggregateTransposeRule}.    *    *<p>It matches any kind of agg. or filter */
specifier|public
specifier|static
specifier|final
name|FilterAggregateTransposeRule
name|INSTANCE
init|=
operator|new
name|FilterAggregateTransposeRule
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|DEFAULT_FILTER_FACTORY
argument_list|,
name|Aggregate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushFilterPastAggRule.    *    *<p>If {@code filterFactory} is null, creates the same kind of filter as    * matched in the rule. Similarly {@code aggregateFactory}.</p>    */
specifier|public
name|FilterAggregateTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|filterClass
argument_list|,
name|operand
argument_list|(
name|aggregateClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|filterFactory
operator|=
name|filterFactory
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
name|Filter
name|filterRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
name|aggRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|conditions
init|=
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|filterRel
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|groupKeys
init|=
name|aggRel
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|filterRel
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
name|RelDataTypeField
argument_list|>
name|origFields
init|=
name|aggRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|origFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|pushedConditions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|remainingConditions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|condition
range|:
name|conditions
control|)
block|{
name|ImmutableBitSet
name|rCols
init|=
name|RelOptUtil
operator|.
name|InputFinder
operator|.
name|bits
argument_list|(
name|condition
argument_list|)
decl_stmt|;
name|boolean
name|push
init|=
name|groupKeys
operator|.
name|contains
argument_list|(
name|rCols
argument_list|)
decl_stmt|;
if|if
condition|(
name|push
operator|&&
name|aggRel
operator|.
name|indicator
condition|)
block|{
comment|// If grouping sets are used, the filter can be pushed if
comment|// the columns referenced in the predicate are present in
comment|// all the grouping sets.
for|for
control|(
name|ImmutableBitSet
name|groupingSet
range|:
name|aggRel
operator|.
name|getGroupSets
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|groupingSet
operator|.
name|contains
argument_list|(
name|rCols
argument_list|)
condition|)
block|{
name|push
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|push
condition|)
block|{
name|pushedConditions
operator|.
name|add
argument_list|(
name|condition
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
name|origFields
argument_list|,
name|aggRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|adjustments
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remainingConditions
operator|.
name|add
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
block|}
name|RelNode
name|rel
init|=
name|RelOptUtil
operator|.
name|createFilter
argument_list|(
name|aggRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
name|pushedConditions
argument_list|,
name|filterFactory
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|==
name|aggRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
condition|)
block|{
return|return;
block|}
name|rel
operator|=
name|aggRel
operator|.
name|copy
argument_list|(
name|aggRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
name|rel
operator|=
name|RelOptUtil
operator|.
name|createFilter
argument_list|(
name|rel
argument_list|,
name|remainingConditions
argument_list|,
name|filterFactory
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FilterAggregateTransposeRule.java
end_comment

end_unit

