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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|linq4j
operator|.
name|tree
operator|.
name|Types
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
name|RelCollation
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
name|RelCollations
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
name|EquiJoin
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
name|rex
operator|.
name|RexLiteral
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|Type
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
name|Set
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Join} in  * {@link EnumerableConvention enumerable calling convention} using  * a merge algorithm. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableMergeJoin
extends|extends
name|EquiJoin
implements|implements
name|EnumerableRel
block|{
name|EnumerableMergeJoin
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
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
init|=
name|traits
operator|.
name|getTraits
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
assert|assert
name|collations
operator|==
literal|null
operator|||
name|RelCollations
operator|.
name|contains
argument_list|(
name|collations
argument_list|,
name|joinInfo
operator|.
name|leftKeys
argument_list|)
assert|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|EnumerableMergeJoin
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
name|variablesSet
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|EnumerableMergeJoin
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
name|leftKeys
argument_list|,
name|rightKeys
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
specifier|public
specifier|static
name|EnumerableMergeJoin
name|create
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexLiteral
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
parameter_list|)
throws|throws
name|InvalidRelException
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|right
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|traitSet
operator|.
name|isEnabled
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
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
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
init|=
name|RelMdCollation
operator|.
name|mergeJoin
argument_list|(
name|mq
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
name|traitSet
operator|=
name|traitSet
operator|.
name|replace
argument_list|(
name|collations
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EnumerableMergeJoin
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
name|leftKeys
argument_list|,
name|rightKeys
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableMergeJoin
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
name|EnumerableMergeJoin
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
comment|// We assume that the inputs are sorted. The price of sorting them has
comment|// already been paid. The cost of the join is therefore proportional to the
comment|// input and output size.
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
specifier|final
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
specifier|final
name|double
name|d
init|=
name|leftRowCount
operator|+
name|rightRowCount
operator|+
name|rowCount
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|d
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
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
specifier|final
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
name|ParameterExpression
name|left_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|leftResult
operator|.
name|physType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"left"
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
specifier|final
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
name|ParameterExpression
name|right_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|rightResult
operator|.
name|physType
operator|.
name|getJavaRowType
argument_list|()
argument_list|,
literal|"right"
argument_list|)
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
name|implementor
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|typeFactory
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
name|List
argument_list|<
name|Expression
argument_list|>
name|leftExpressions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|rightExpressions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|joinInfo
operator|.
name|leftKeys
argument_list|,
name|joinInfo
operator|.
name|rightKeys
argument_list|)
control|)
block|{
specifier|final
name|RelDataType
name|keyType
init|=
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|pair
operator|.
name|left
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|pair
operator|.
name|right
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Type
name|keyClass
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|keyType
argument_list|)
decl_stmt|;
name|leftExpressions
operator|.
name|add
argument_list|(
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|keyClass
argument_list|,
name|leftResult
operator|.
name|physType
operator|.
name|fieldReference
argument_list|(
name|left_
argument_list|,
name|pair
operator|.
name|left
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rightExpressions
operator|.
name|add
argument_list|(
name|Types
operator|.
name|castIfNecessary
argument_list|(
name|keyClass
argument_list|,
name|rightResult
operator|.
name|physType
operator|.
name|fieldReference
argument_list|(
name|right_
argument_list|,
name|pair
operator|.
name|right
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PhysType
name|leftKeyPhysType
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
specifier|final
name|PhysType
name|rightKeyPhysType
init|=
name|rightResult
operator|.
name|physType
operator|.
name|project
argument_list|(
name|joinInfo
operator|.
name|rightKeys
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
name|BuiltInMethod
operator|.
name|MERGE_JOIN
operator|.
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
name|Expressions
operator|.
name|lambda
argument_list|(
name|leftKeyPhysType
operator|.
name|record
argument_list|(
name|leftExpressions
argument_list|)
argument_list|,
name|left_
argument_list|)
argument_list|,
name|Expressions
operator|.
name|lambda
argument_list|(
name|rightKeyPhysType
operator|.
name|record
argument_list|(
name|rightExpressions
argument_list|)
argument_list|,
name|right_
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
comment|// End EnumerableMergeJoin.java
end_comment

end_unit

