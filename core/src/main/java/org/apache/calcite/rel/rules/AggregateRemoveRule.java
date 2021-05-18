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
name|SqlFunctions
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
name|sql
operator|.
name|SqlKind
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
name|SqlSplittableAggFunction
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
comment|/**  * Planner rule that removes  * a {@link org.apache.calcite.rel.core.Aggregate}  * if it computes no aggregate functions  * (that is, it is implementing {@code SELECT DISTINCT}),  * or all the aggregate functions are splittable,  * and the underlying relational expression is already distinct.  *  * @see CoreRules#AGGREGATE_REMOVE  */
end_comment

begin_class
specifier|public
class|class
name|AggregateRemoveRule
extends|extends
name|RelRule
argument_list|<
name|AggregateRemoveRule
operator|.
name|Config
argument_list|>
implements|implements
name|SubstitutionRule
block|{
comment|/** Creates an AggregateRemoveRule. */
specifier|protected
name|AggregateRemoveRule
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
name|AggregateRemoveRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateRemoveRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
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
operator|.
name|withOperandFor
argument_list|(
name|aggregateClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isAggregateSupported
parameter_list|(
name|Aggregate
name|aggregate
parameter_list|)
block|{
if|if
condition|(
name|aggregate
operator|.
name|getGroupType
argument_list|()
operator|!=
name|Aggregate
operator|.
name|Group
operator|.
name|SIMPLE
operator|||
name|aggregate
operator|.
name|getGroupCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// If any aggregate functions do not support splitting, bail out.
for|for
control|(
name|AggregateCall
name|aggregateCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
if|if
condition|(
name|aggregateCall
operator|.
name|filterArg
operator|>=
literal|0
operator|||
operator|!
name|aggregateCall
operator|.
name|getAggregation
argument_list|()
operator|.
name|maybeUnwrap
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|aggregate
operator|.
name|getInput
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
if|if
condition|(
operator|!
name|SqlFunctions
operator|.
name|isTrue
argument_list|(
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|input
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|relBuilder
operator|.
name|getRexBuilder
argument_list|()
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
name|SqlAggFunction
name|aggregation
init|=
name|aggCall
operator|.
name|getAggregation
argument_list|()
decl_stmt|;
if|if
condition|(
name|aggregation
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|SUM0
condition|)
block|{
comment|// Bail out for SUM0 to avoid potential infinite rule matching,
comment|// because it may be generated by transforming SUM aggregate
comment|// function to SUM0 and COUNT.
return|return;
block|}
specifier|final
name|SqlSplittableAggFunction
name|splitter
init|=
name|aggregation
operator|.
name|unwrapOrThrow
argument_list|(
name|SqlSplittableAggFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|singleton
init|=
name|splitter
operator|.
name|singleton
argument_list|(
name|rexBuilder
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|aggCall
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|cast
init|=
name|rexBuilder
operator|.
name|ensureType
argument_list|(
name|aggCall
operator|.
name|type
argument_list|,
name|singleton
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|projects
operator|.
name|add
argument_list|(
name|cast
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|newInput
init|=
name|convert
argument_list|(
name|input
argument_list|,
name|aggregate
operator|.
name|getTraitSet
argument_list|()
operator|.
name|simplify
argument_list|()
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|newInput
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|projects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|projects
operator|.
name|addAll
argument_list|(
literal|0
argument_list|,
name|relBuilder
operator|.
name|fields
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|project
argument_list|(
name|projects
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|newInput
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|>
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
comment|// If aggregate was projecting a subset of columns, and there were no
comment|// aggregate functions, add a project for the same effect.
name|relBuilder
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|prune
argument_list|(
name|aggregate
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
name|EMPTY
operator|.
name|withRelBuilderFactory
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateRemoveRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateRemoveRule
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
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|aggregateClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|AggregateRemoveRule
operator|::
name|isAggregateSupported
argument_list|)
operator|.
name|anyInputs
argument_list|()
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

