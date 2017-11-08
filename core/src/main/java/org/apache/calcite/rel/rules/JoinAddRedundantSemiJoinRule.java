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
name|JoinInfo
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
name|JoinRelType
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
name|SemiJoin
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
name|LogicalJoin
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
comment|/**  * Rule to add a semi-join into a join. Transformation is as follows:  *  *<p>LogicalJoin(X, Y)&rarr; LogicalJoin(SemiJoin(X, Y), Y)  *  *<p>The constructor is parameterized to allow any sub-class of  * {@link org.apache.calcite.rel.core.Join}, not just  * {@link org.apache.calcite.rel.logical.LogicalJoin}.  */
end_comment

begin_class
specifier|public
class|class
name|JoinAddRedundantSemiJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|JoinAddRedundantSemiJoinRule
name|INSTANCE
init|=
operator|new
name|JoinAddRedundantSemiJoinRule
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an JoinAddRedundantSemiJoinRule.    */
specifier|public
name|JoinAddRedundantSemiJoinRule
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
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Join
name|origJoinRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|origJoinRel
operator|.
name|isSemiJoinDone
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// can't process outer joins using semijoins
if|if
condition|(
name|origJoinRel
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
return|return;
block|}
comment|// determine if we have a valid join condition
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|origJoinRel
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|joinInfo
operator|.
name|leftKeys
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|RelNode
name|semiJoin
init|=
name|SemiJoin
operator|.
name|create
argument_list|(
name|origJoinRel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|origJoinRel
operator|.
name|getRight
argument_list|()
argument_list|,
name|origJoinRel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|joinInfo
operator|.
name|leftKeys
argument_list|,
name|joinInfo
operator|.
name|rightKeys
argument_list|)
decl_stmt|;
name|RelNode
name|newJoinRel
init|=
name|origJoinRel
operator|.
name|copy
argument_list|(
name|origJoinRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|origJoinRel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|semiJoin
argument_list|,
name|origJoinRel
operator|.
name|getRight
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newJoinRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JoinAddRedundantSemiJoinRule.java
end_comment

end_unit

