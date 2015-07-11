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
name|adapter
operator|.
name|enumerable
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
name|linq4j
operator|.
name|function
operator|.
name|Predicate2
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|linq4j
operator|.
name|tree
operator|.
name|ParameterExpression
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
name|RelOptCluster
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelTraitSet
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
name|InvalidRelException
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
name|CorrelationId
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
name|RexProgramBuilder
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
name|BuiltInMethod
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
name|Set
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Join} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}  * that allows conditions that are not just {@code =} (equals). */
end_comment

begin_class
specifier|public
class|class
name|EnumerableThetaJoin
extends|extends
name|Join
implements|implements
name|EnumerableRel
block|{
comment|/** Creates an EnumerableThetaJoin. */
specifier|protected
name|EnumerableThetaJoin
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
throws|throws
name|InvalidRelException
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|variablesSet
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|EnumerableThetaJoin
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|)
throws|throws
name|InvalidRelException
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|CorrelationId
operator|.
name|setOf
argument_list|(
name|variablesStopped
argument_list|)
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableThetaJoin
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|EnumerableThetaJoin
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|variablesSet
argument_list|,
name|joinType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
comment|// Semantic error not possible. Must be a bug. Convert to
comment|// internal error.
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// Joins can be flipped, and for many algorithms, both versions are viable
comment|// and have the same cost. To make the results stable between versions of
comment|// the planner, make one of the versions slightly more expensive.
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|RIGHT
case|:
name|rowCount
operator|=
name|addEpsilon
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|left
operator|.
name|getId
argument_list|()
operator|>
name|right
operator|.
name|getId
argument_list|()
condition|)
block|{
name|rowCount
operator|=
name|addEpsilon
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|rightRowCount
init|=
name|right
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
decl_stmt|;
specifier|final
name|double
name|leftRowCount
init|=
name|left
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|leftRowCount
argument_list|)
condition|)
block|{
name|rowCount
operator|=
name|leftRowCount
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|rightRowCount
argument_list|)
condition|)
block|{
name|rowCount
operator|=
name|rightRowCount
expr_stmt|;
block|}
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|rowCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|double
name|addEpsilon
parameter_list|(
name|double
name|d
parameter_list|)
block|{
assert|assert
name|d
operator|>=
literal|0d
assert|;
specifier|final
name|double
name|d0
init|=
name|d
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|10
condition|)
block|{
comment|// For small d, adding 1 would change the value significantly.
name|d
operator|*=
literal|1.001d
expr_stmt|;
if|if
condition|(
name|d
operator|!=
name|d0
condition|)
block|{
return|return
name|d
return|;
block|}
block|}
comment|// For medium d, add 1. Keeps integral values integral.
operator|++
name|d
expr_stmt|;
if|if
condition|(
name|d
operator|!=
name|d0
condition|)
block|{
return|return
name|d
return|;
block|}
comment|// For large d, adding 1 might not change the value. Add .1%.
comment|// If d is NaN, this still will probably not change the value. That's OK.
name|d
operator|*=
literal|1.001d
expr_stmt|;
return|return
name|d
return|;
block|}
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Result
name|leftResult
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|left
argument_list|,
name|pref
argument_list|)
decl_stmt|;
name|Expression
name|leftExpression
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"left"
argument_list|,
name|leftResult
operator|.
name|block
argument_list|)
decl_stmt|;
specifier|final
name|Result
name|rightResult
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|right
argument_list|,
name|pref
argument_list|)
decl_stmt|;
name|Expression
name|rightExpression
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"right"
argument_list|,
name|rightResult
operator|.
name|block
argument_list|)
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|pref
operator|.
name|preferArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|builder
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|THETA_JOIN
operator|.
name|method
argument_list|,
name|leftExpression
argument_list|,
name|rightExpression
argument_list|,
name|predicate
argument_list|(
name|implementor
argument_list|,
name|builder2
argument_list|,
name|leftResult
operator|.
name|physType
argument_list|,
name|rightResult
operator|.
name|physType
argument_list|,
name|condition
argument_list|)
argument_list|,
name|EnumUtils
operator|.
name|joinSelector
argument_list|(
name|joinType
argument_list|,
name|physType
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|leftResult
operator|.
name|physType
argument_list|,
name|rightResult
operator|.
name|physType
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
name|Expression
name|predicate
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|BlockBuilder
name|builder
parameter_list|,
name|PhysType
name|leftPhysType
parameter_list|,
name|PhysType
name|rightPhysType
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
specifier|final
name|ParameterExpression
name|left_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|leftPhysType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"left"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|right_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|rightPhysType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"right"
argument_list|)
decl_stmt|;
specifier|final
name|RexProgramBuilder
name|program
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
name|program
operator|.
name|addCondition
argument_list|(
name|condition
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|RexToLixTranslator
operator|.
name|translateCondition
argument_list|(
name|program
operator|.
name|getProgram
argument_list|()
argument_list|,
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|builder
argument_list|,
operator|new
name|RexToLixTranslator
operator|.
name|InputGetterImpl
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|Expression
operator|)
name|left_
argument_list|,
name|leftPhysType
argument_list|)
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|Expression
operator|)
name|right_
argument_list|,
name|rightPhysType
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|implementor
operator|.
name|allCorrelateVariables
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Predicate2
operator|.
name|class
argument_list|,
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|,
name|left_
argument_list|,
name|right_
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableThetaJoin.java
end_comment

end_unit

