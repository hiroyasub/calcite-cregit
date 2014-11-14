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
name|List
import|;
end_import

begin_comment
comment|/**  * Planner rule that matches  * {@link org.apache.calcite.rel.logical.LogicalAggregate}s beneath a  * {@link org.apache.calcite.rel.logical.LogicalUnion} and pulls them up, so  * that a single  * {@link org.apache.calcite.rel.logical.LogicalAggregate} removes duplicates.  *  *<p>This rule only handles cases where the  * {@link org.apache.calcite.rel.logical.LogicalUnion}s  * still have only two inputs.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateUnionAggregateRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|AggregateUnionAggregateRule
name|INSTANCE
init|=
operator|new
name|AggregateUnionAggregateRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a AggregateUnionAggregateRule.    */
specifier|private
name|AggregateUnionAggregateRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Aggregate
operator|.
name|IS_SIMPLE
argument_list|,
name|operand
argument_list|(
name|LogicalUnion
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|operand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
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
name|LogicalUnion
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
name|LogicalAggregate
name|topAggRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LogicalAggregate
name|bottomAggRel
decl_stmt|;
comment|// We want to apply this rule on the pattern where the LogicalAggregate
comment|// is the second input into the Union first.  Hence, that's why the
comment|// rule pattern matches on generic RelNodes rather than explicit
comment|// UnionRels.  By doing so, and firing this rule in a bottom-up order,
comment|// it allows us to only specify a single pattern for this rule.
name|List
argument_list|<
name|RelNode
argument_list|>
name|unionInputs
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
name|LogicalAggregate
condition|)
block|{
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|unionInputs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
argument_list|,
name|call
operator|.
name|rel
argument_list|(
literal|3
argument_list|)
operator|.
name|getInput
argument_list|(
literal|0
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
name|LogicalAggregate
condition|)
block|{
name|bottomAggRel
operator|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|unionInputs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
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
name|LogicalUnion
name|newUnion
init|=
operator|new
name|LogicalUnion
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|unionInputs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LogicalAggregate
name|newAggRel
init|=
operator|new
name|LogicalAggregate
argument_list|(
name|topAggRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newUnion
argument_list|,
literal|false
argument_list|,
name|topAggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
literal|null
argument_list|,
name|topAggRel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newAggRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateUnionAggregateRule.java
end_comment

end_unit

