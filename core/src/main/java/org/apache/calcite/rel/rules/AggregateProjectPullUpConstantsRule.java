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
name|logical
operator|.
name|LogicalAggregate
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
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * Planner rule that removes constant keys from an  * {@link org.apache.calcite.rel.core.Aggregate}.  *  *<p>Constant fields are deduced using  * {@link RelMetadataQuery#getPulledUpPredicates(RelNode)}; the input does not  * need to be a {@link org.apache.calcite.rel.core.Project}.  *  *<p>This rules never removes the last column, because {@code Aggregate([])}  * returns 1 row even if its input is empty.  *  *<p>Since the transformed relational expression has to match the original  * relational expression, the constants are placed in a projection above the  * reduced aggregate. If those constants are not used, another rule will remove  * them from the project.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateProjectPullUpConstantsRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** The singleton. */
specifier|public
specifier|static
specifier|final
name|AggregateProjectPullUpConstantsRule
name|INSTANCE
init|=
operator|new
name|AggregateProjectPullUpConstantsRule
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|LogicalProject
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"AggregateProjectPullUpConstantsRule"
argument_list|)
decl_stmt|;
comment|/** More general instance that matches any relational expression. */
specifier|public
specifier|static
specifier|final
name|AggregateProjectPullUpConstantsRule
name|INSTANCE2
init|=
operator|new
name|AggregateProjectPullUpConstantsRule
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"AggregatePullUpConstantsRule"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AggregateProjectPullUpConstantsRule.    *    * @param aggregateClass Aggregate class    * @param inputClass Input class, such as {@link LogicalProject}    * @param relBuilderFactory Builder for relational expressions    * @param description Description, or null to guess description    */
specifier|public
name|AggregateProjectPullUpConstantsRule
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
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|aggregateClass
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|.
name|IS_SIMPLE
argument_list|,
name|operand
argument_list|(
name|inputClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
assert|assert
operator|!
name|aggregate
operator|.
name|indicator
operator|:
literal|"predicate ensured no grouping sets"
assert|;
specifier|final
name|int
name|groupCount
init|=
name|aggregate
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupCount
operator|==
literal|1
condition|)
block|{
comment|// No room for optimization since we cannot convert from non-empty
comment|// GROUP BY list to the empty one.
return|return;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|aggregate
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
name|aggregate
operator|.
name|getInput
argument_list|()
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
name|NavigableMap
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|aggregate
operator|.
name|getGroupSet
argument_list|()
control|)
block|{
specifier|final
name|RexInputRef
name|ref
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|aggregate
operator|.
name|getInput
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicates
operator|.
name|constantMap
operator|.
name|containsKey
argument_list|(
name|ref
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|predicates
operator|.
name|constantMap
operator|.
name|get
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// None of the group expressions are constant. Nothing to do.
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|groupCount
operator|==
name|map
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// At least a single item in group by is required.
comment|// Otherwise "GROUP BY 1, 2" might be altered to "GROUP BY ()".
comment|// Removing of the first element is not optimal here,
comment|// however it will allow us to use fast path below (just trim
comment|// groupCount).
name|map
operator|.
name|remove
argument_list|(
name|map
operator|.
name|navigableKeySet
argument_list|()
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ImmutableBitSet
name|newGroupSet
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|newGroupSet
operator|=
name|newGroupSet
operator|.
name|clear
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|newGroupCount
init|=
name|newGroupSet
operator|.
name|cardinality
argument_list|()
decl_stmt|;
comment|// If the constants are on the trailing edge of the group list, we just
comment|// reduce the group count.
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|// Clone aggregate calls.
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
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
name|newAggCalls
operator|.
name|add
argument_list|(
name|aggCall
operator|.
name|adaptTo
argument_list|(
name|input
argument_list|,
name|aggCall
operator|.
name|getArgList
argument_list|()
argument_list|,
name|aggCall
operator|.
name|filterArg
argument_list|,
name|groupCount
argument_list|,
name|newGroupCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|newGroupSet
argument_list|,
literal|null
argument_list|)
argument_list|,
name|newAggCalls
argument_list|)
expr_stmt|;
comment|// Create a projection back again.
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|source
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|RexNode
name|expr
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|field
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|groupCount
condition|)
block|{
comment|// Aggregate expressions' names and positions are unchanged.
name|expr
operator|=
name|relBuilder
operator|.
name|field
argument_list|(
name|i
operator|-
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|pos
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|nth
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|pos
argument_list|)
condition|)
block|{
comment|// Re-generate the constant expression in the project.
name|RelDataType
name|originalType
init|=
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|projects
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|originalType
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|pos
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|expr
operator|=
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|originalType
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|pos
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expr
operator|=
name|map
operator|.
name|get
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Project the aggregation expression, in its original
comment|// position.
name|expr
operator|=
name|relBuilder
operator|.
name|field
argument_list|(
name|source
argument_list|)
expr_stmt|;
operator|++
name|source
expr_stmt|;
block|}
block|}
name|projects
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|expr
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|project
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|projects
argument_list|)
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|projects
argument_list|)
argument_list|)
expr_stmt|;
comment|// inverse
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
comment|// End AggregateProjectPullUpConstantsRule.java
end_comment

end_unit

