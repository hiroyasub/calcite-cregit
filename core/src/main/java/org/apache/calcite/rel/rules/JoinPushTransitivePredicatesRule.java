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
name|Contexts
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
name|Join
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
comment|/**  * Planner rule that infers predicates from on a  * {@link org.apache.calcite.rel.core.Join} and creates  * {@link org.apache.calcite.rel.core.Filter}s if those predicates can be pushed  * to its inputs.  *  *<p>Uses {@link org.apache.calcite.rel.metadata.RelMdPredicates} to infer  * the predicates,  * returns them in a {@link org.apache.calcite.plan.RelOptPredicateList}  * and applies them appropriately.  */
end_comment

begin_class
specifier|public
class|class
name|JoinPushTransitivePredicatesRule
extends|extends
name|RelOptRule
block|{
comment|/** The singleton. */
specifier|public
specifier|static
specifier|final
name|JoinPushTransitivePredicatesRule
name|INSTANCE
init|=
operator|new
name|JoinPushTransitivePredicatesRule
argument_list|(
name|Join
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|/** Creates a JoinPushTransitivePredicatesRule. */
specifier|public
name|JoinPushTransitivePredicatesRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
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
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinPushTransitivePredicatesRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|filterFactory
argument_list|)
argument_list|)
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
name|Join
name|join
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
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
name|RelOptPredicateList
name|preds
init|=
name|mq
operator|.
name|getPulledUpPredicates
argument_list|(
name|join
argument_list|)
decl_stmt|;
if|if
condition|(
name|preds
operator|.
name|leftInferredPredicates
operator|.
name|isEmpty
argument_list|()
operator|&&
name|preds
operator|.
name|rightInferredPredicates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
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
name|RelNode
name|lChild
init|=
name|join
operator|.
name|getLeft
argument_list|()
decl_stmt|;
if|if
condition|(
name|preds
operator|.
name|leftInferredPredicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|RelNode
name|curr
init|=
name|lChild
decl_stmt|;
name|lChild
operator|=
name|relBuilder
operator|.
name|push
argument_list|(
name|lChild
argument_list|)
operator|.
name|filter
argument_list|(
name|preds
operator|.
name|leftInferredPredicates
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|curr
argument_list|,
name|lChild
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|rChild
init|=
name|join
operator|.
name|getRight
argument_list|()
decl_stmt|;
if|if
condition|(
name|preds
operator|.
name|rightInferredPredicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|RelNode
name|curr
init|=
name|rChild
decl_stmt|;
name|rChild
operator|=
name|relBuilder
operator|.
name|push
argument_list|(
name|rChild
argument_list|)
operator|.
name|filter
argument_list|(
name|preds
operator|.
name|rightInferredPredicates
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|curr
argument_list|,
name|rChild
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|newRel
init|=
name|join
operator|.
name|copy
argument_list|(
name|join
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|lChild
argument_list|,
name|rChild
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|onCopy
argument_list|(
name|join
argument_list|,
name|newRel
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JoinPushTransitivePredicatesRule.java
end_comment

end_unit

