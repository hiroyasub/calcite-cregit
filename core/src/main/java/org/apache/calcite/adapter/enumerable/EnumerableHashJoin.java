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
name|RelNodes
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
name|RelMdCollation
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
name|RelMdUtil
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
name|ImmutableIntList
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
name|Util
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.Join} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableHashJoin
extends|extends
name|Join
implements|implements
name|EnumerableRel
block|{
comment|/** Creates an EnumerableHashJoin.    *    *<p>Use {@link #create} unless you know what you're doing. */
specifier|protected
name|EnumerableHashJoin
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
name|EnumerableHashJoin
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
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
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
comment|/** Creates an EnumerableHashJoin. */
specifier|public
specifier|static
name|EnumerableHashJoin
name|create
parameter_list|(
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
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|left
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdCollation
operator|.
name|enumerableHashJoin
argument_list|(
name|mq
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|joinType
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableHashJoin
argument_list|(
name|cluster
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
annotation|@
name|Override
specifier|public
name|EnumerableHashJoin
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
return|return
operator|new
name|EnumerableHashJoin
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
name|SEMI
case|:
case|case
name|ANTI
case|:
comment|// SEMI and ANTI join cannot be flipped
break|break;
case|case
name|RIGHT
case|:
name|rowCount
operator|=
name|RelMdUtil
operator|.
name|addEpsilon
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|RelNodes
operator|.
name|COMPARATOR
operator|.
name|compare
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
operator|>
literal|0
condition|)
block|{
name|rowCount
operator|=
name|RelMdUtil
operator|.
name|addEpsilon
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Cheaper if the smaller number of rows is coming from the LHS.
comment|// Model this by adding L log L to the cost.
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
else|else
block|{
name|rowCount
operator|+=
name|Util
operator|.
name|nLogN
argument_list|(
name|leftRowCount
argument_list|)
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
else|else
block|{
name|rowCount
operator|+=
name|rightRowCount
expr_stmt|;
block|}
if|if
condition|(
name|isSemiJoin
argument_list|()
condition|)
block|{
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
operator|.
name|multiplyBy
argument_list|(
literal|.01d
argument_list|)
return|;
block|}
else|else
block|{
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
block|}
annotation|@
name|Override
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
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|SEMI
case|:
case|case
name|ANTI
case|:
assert|assert
name|joinInfo
operator|.
name|isEqui
argument_list|()
assert|;
return|return
name|implementHashSemiJoin
argument_list|(
name|implementor
argument_list|,
name|pref
argument_list|)
return|;
default|default:
return|return
name|implementHashJoin
argument_list|(
name|implementor
argument_list|,
name|pref
argument_list|)
return|;
block|}
block|}
specifier|private
name|Result
name|implementHashSemiJoin
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
assert|assert
name|joinType
operator|==
name|JoinRelType
operator|.
name|SEMI
operator|||
name|joinType
operator|==
name|JoinRelType
operator|.
name|ANTI
assert|;
specifier|final
name|Method
name|method
init|=
name|joinType
operator|==
name|JoinRelType
operator|.
name|SEMI
condition|?
name|BuiltInMethod
operator|.
name|SEMI_JOIN
operator|.
name|method
else|:
name|BuiltInMethod
operator|.
name|ANTI_JOIN
operator|.
name|method
decl_stmt|;
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
name|leftResult
operator|.
name|physType
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
name|method
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|leftExpression
argument_list|,
name|rightExpression
argument_list|,
name|leftResult
operator|.
name|physType
operator|.
name|generateAccessor
argument_list|(
name|joinInfo
operator|.
name|leftKeys
argument_list|)
argument_list|,
name|rightResult
operator|.
name|physType
operator|.
name|generateAccessor
argument_list|(
name|joinInfo
operator|.
name|rightKeys
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Result
name|implementHashJoin
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
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
name|PhysType
name|keyPhysType
init|=
name|leftResult
operator|.
name|physType
operator|.
name|project
argument_list|(
name|joinInfo
operator|.
name|leftKeys
argument_list|,
name|JavaRowFormat
operator|.
name|LIST
argument_list|)
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
name|leftExpression
argument_list|,
name|BuiltInMethod
operator|.
name|HASH_JOIN
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|rightExpression
argument_list|,
name|leftResult
operator|.
name|physType
operator|.
name|generateAccessor
argument_list|(
name|joinInfo
operator|.
name|leftKeys
argument_list|)
argument_list|,
name|rightResult
operator|.
name|physType
operator|.
name|generateAccessor
argument_list|(
name|joinInfo
operator|.
name|rightKeys
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
argument_list|)
operator|.
name|append
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|keyPhysType
operator|.
name|comparer
argument_list|()
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableHashJoin.java
end_comment

end_unit

