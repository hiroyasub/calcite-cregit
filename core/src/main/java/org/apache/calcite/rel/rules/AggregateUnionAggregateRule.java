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
name|LogicalUnion
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

begin_comment
comment|/**  * Planner rule that matches  * {@link org.apache.calcite.rel.core.Aggregate}s beneath a  * {@link org.apache.calcite.rel.core.Union} and pulls them up, so  * that a single  * {@link org.apache.calcite.rel.core.Aggregate} removes duplicates.  *  *<p>This rule only handles cases where the  * {@link org.apache.calcite.rel.core.Union}s  * still have only two inputs.  *  * @see CoreRules#AGGREGATE_UNION_AGGREGATE  * @see CoreRules#AGGREGATE_UNION_AGGREGATE_FIRST  * @see CoreRules#AGGREGATE_UNION_AGGREGATE_SECOND  */
end_comment

begin_class
specifier|public
class|class
name|AggregateUnionAggregateRule
extends|extends
name|RelRule
argument_list|<
name|AggregateUnionAggregateRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates an AggregateUnionAggregateRule. */
specifier|protected
name|AggregateUnionAggregateRule
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
name|AggregateUnionAggregateRule
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
name|Union
argument_list|>
name|unionClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|firstUnionInputClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|secondUnionInputClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|desc
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
name|withDescription
argument_list|(
name|desc
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
argument_list|,
name|unionClass
argument_list|,
name|firstUnionInputClass
argument_list|,
name|secondUnionInputClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateUnionAggregateRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Aggregate
argument_list|>
name|aggregateClass
parameter_list|,
name|RelFactories
operator|.
name|AggregateFactory
name|aggregateFactory
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClass
parameter_list|,
name|RelFactories
operator|.
name|SetOpFactory
name|setOpFactory
parameter_list|)
block|{
name|this
argument_list|(
name|aggregateClass
argument_list|,
name|unionClass
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|aggregateFactory
argument_list|,
name|setOpFactory
argument_list|)
argument_list|,
literal|"AggregateUnionAggregateRule"
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns an input with the same row type with the input Aggregate,    * create a Project node if needed.    */
specifier|private
specifier|static
name|RelNode
name|getInputWithSameRowType
parameter_list|(
name|Aggregate
name|bottomAggRel
parameter_list|)
block|{
if|if
condition|(
name|RelOptUtil
operator|.
name|areRowTypesEqual
argument_list|(
name|bottomAggRel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|bottomAggRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|bottomAggRel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|bottomAggRel
operator|.
name|getInput
argument_list|()
argument_list|,
name|bottomAggRel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asList
argument_list|()
argument_list|)
return|;
block|}
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
name|Aggregate
name|topAggRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Union
name|union
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// If distincts haven't been removed yet, defer invoking this rule
if|if
condition|(
operator|!
name|union
operator|.
name|all
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
name|Aggregate
name|bottomAggRel
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
operator|instanceof
name|Aggregate
condition|)
block|{
comment|// Aggregate is the second input
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|push
argument_list|(
name|getInputWithSameRowType
argument_list|(
name|bottomAggRel
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
operator|instanceof
name|Aggregate
condition|)
block|{
comment|// Aggregate is the first input
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|getInputWithSameRowType
argument_list|(
name|bottomAggRel
argument_list|)
argument_list|)
operator|.
name|push
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
comment|// Only pull up aggregates if they are there just to remove distincts
if|if
condition|(
operator|!
name|topAggRel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|bottomAggRel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|relBuilder
operator|.
name|union
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|rename
argument_list|(
name|union
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|topAggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|)
argument_list|,
name|topAggRel
operator|.
name|getAggCallList
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
name|withDescription
argument_list|(
literal|"AggregateUnionAggregateRule"
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
argument_list|,
name|LogicalUnion
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|Config
name|AGG_FIRST
init|=
name|DEFAULT
operator|.
name|withDescription
argument_list|(
literal|"AggregateUnionAggregateRule:first-input-agg"
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
argument_list|,
name|LogicalUnion
operator|.
name|class
argument_list|,
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|Config
name|AGG_SECOND
init|=
name|DEFAULT
operator|.
name|withDescription
argument_list|(
literal|"AggregateUnionAggregateRule:second-input-agg"
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
argument_list|,
name|LogicalUnion
operator|.
name|class
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|LogicalAggregate
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|AggregateUnionAggregateRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|AggregateUnionAggregateRule
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
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Union
argument_list|>
name|unionClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|firstUnionInputClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|secondUnionInputClass
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
name|aggregateClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|Aggregate
operator|::
name|isSimple
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
name|unionClass
argument_list|)
operator|.
name|inputs
argument_list|(
name|b2
lambda|->
name|b2
operator|.
name|operand
argument_list|(
name|firstUnionInputClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|,
name|b3
lambda|->
name|b3
operator|.
name|operand
argument_list|(
name|secondUnionInputClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
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

