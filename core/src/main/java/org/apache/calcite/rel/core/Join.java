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
name|core
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
name|BiRel
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
name|RelWriter
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
name|hint
operator|.
name|Hintable
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
name|hint
operator|.
name|RelHint
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
name|RelDataTypeFactory
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
name|RexChecker
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
name|RexShuttle
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
name|type
operator|.
name|SqlTypeName
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
name|validate
operator|.
name|SqlValidatorUtil
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
name|Litmus
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
name|org
operator|.
name|apiguardian
operator|.
name|api
operator|.
name|API
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|EnsuresNonNullIf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Objects
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
comment|/**  * Relational expression that combines two relational expressions according to  * some condition.  *  *<p>Each output row has columns from the left and right inputs.  * The set of output rows is a subset of the cartesian product of the two  * inputs; precisely which subset depends on the join condition.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Join
extends|extends
name|BiRel
implements|implements
name|Hintable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RexNode
name|condition
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableSet
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|hints
decl_stmt|;
comment|/**    * Values must be of enumeration {@link JoinRelType}, except that    * {@link JoinRelType#RIGHT} is disallowed.    */
specifier|protected
specifier|final
name|JoinRelType
name|joinType
decl_stmt|;
specifier|protected
specifier|final
name|JoinInfo
name|joinInfo
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Join.    *    * @param cluster          Cluster    * @param traitSet         Trait set    * @param hints            Hints    * @param left             Left input    * @param right            Right input    * @param condition        Join condition    * @param joinType         Join type    * @param variablesSet     variables that are set by the    *                         LHS and used by the RHS and are not available to    *                         nodes above this Join in the tree    */
specifier|protected
name|Join
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
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
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|condition
argument_list|,
literal|"condition"
argument_list|)
expr_stmt|;
name|this
operator|.
name|variablesSet
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|variablesSet
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|joinType
argument_list|,
literal|"joinType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinInfo
operator|=
name|JoinInfo
operator|.
name|of
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|)
expr_stmt|;
name|this
operator|.
name|hints
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|hints
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|Join
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
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
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
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
name|Join
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
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
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
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
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|RexNode
name|condition
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|condition
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|condition
operator|==
name|condition
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|condition
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|joinType
argument_list|,
name|isSemiJoinDone
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
specifier|public
name|JoinRelType
name|getJoinType
parameter_list|()
block|{
return|return
name|joinType
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|,
annotation|@
name|Nullable
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|isValid
argument_list|(
name|litmus
argument_list|,
name|context
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|!=
name|getSystemFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|+
operator|(
name|joinType
operator|.
name|projectsRight
argument_list|()
condition|?
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
else|:
literal|0
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"field count mismatch"
argument_list|)
return|;
block|}
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|condition
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"condition must be boolean: {}"
argument_list|,
name|condition
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|// The input to the condition is a row type consisting of system
comment|// fields, left fields, and right fields. Very similar to the
comment|// output row type, except that fields have not yet been made due
comment|// due to outer joins.
name|RexChecker
name|checker
init|=
operator|new
name|RexChecker
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|getSystemFieldList
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|getLeft
argument_list|()
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
name|getRight
argument_list|()
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
name|context
argument_list|,
name|litmus
argument_list|)
decl_stmt|;
name|condition
operator|.
name|accept
argument_list|(
name|checker
argument_list|)
expr_stmt|;
if|if
condition|(
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|+
literal|" failures in condition "
operator|+
name|condition
argument_list|)
return|;
block|}
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
comment|// Maybe we should remove this for semi-join?
if|if
condition|(
name|isSemiJoin
argument_list|()
condition|)
block|{
comment|// REVIEW jvs 9-Apr-2006:  Just for now...
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeTinyCost
argument_list|()
return|;
block|}
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
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link RelMdUtil#getJoinRowCount(RelMetadataQuery, Join, RexNode)}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|double
name|estimateJoinedRows
parameter_list|(
name|Join
name|joinRel
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
return|return
name|Util
operator|.
name|first
argument_list|(
name|RelMdUtil
operator|.
name|getJoinRowCount
argument_list|(
name|mq
argument_list|,
name|joinRel
argument_list|,
name|condition
argument_list|)
argument_list|,
literal|1D
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|Util
operator|.
name|first
argument_list|(
name|RelMdUtil
operator|.
name|getJoinRowCount
argument_list|(
name|mq
argument_list|,
name|this
argument_list|,
name|condition
argument_list|)
argument_list|,
literal|1D
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|getVariablesSet
parameter_list|()
block|{
return|return
name|variablesSet
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"condition"
argument_list|,
name|condition
argument_list|)
operator|.
name|item
argument_list|(
literal|"joinType"
argument_list|,
name|joinType
operator|.
name|lowerName
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"variablesSet"
argument_list|,
name|variablesSet
argument_list|,
operator|!
name|variablesSet
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"systemFields"
argument_list|,
name|getSystemFieldList
argument_list|()
argument_list|,
operator|!
name|getSystemFieldList
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|API
argument_list|(
name|since
operator|=
literal|"1.24"
argument_list|,
name|status
operator|=
name|API
operator|.
name|Status
operator|.
name|INTERNAL
argument_list|)
annotation|@
name|EnsuresNonNullIf
argument_list|(
name|expression
operator|=
literal|"#1"
argument_list|,
name|result
operator|=
literal|true
argument_list|)
specifier|protected
name|boolean
name|deepEquals0
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Join
name|o
init|=
operator|(
name|Join
operator|)
name|obj
decl_stmt|;
return|return
name|traitSet
operator|.
name|equals
argument_list|(
name|o
operator|.
name|traitSet
argument_list|)
operator|&&
name|left
operator|.
name|deepEquals
argument_list|(
name|o
operator|.
name|left
argument_list|)
operator|&&
name|right
operator|.
name|deepEquals
argument_list|(
name|o
operator|.
name|right
argument_list|)
operator|&&
name|condition
operator|.
name|equals
argument_list|(
name|o
operator|.
name|condition
argument_list|)
operator|&&
name|joinType
operator|==
name|o
operator|.
name|joinType
operator|&&
name|hints
operator|.
name|equals
argument_list|(
name|o
operator|.
name|hints
argument_list|)
operator|&&
name|getRowType
argument_list|()
operator|.
name|equalsSansFieldNames
argument_list|(
name|o
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|API
argument_list|(
name|since
operator|=
literal|"1.24"
argument_list|,
name|status
operator|=
name|API
operator|.
name|Status
operator|.
name|INTERNAL
argument_list|)
specifier|protected
name|int
name|deepHashCode0
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|traitSet
argument_list|,
name|left
operator|.
name|deepHashCode
argument_list|()
argument_list|,
name|right
operator|.
name|deepHashCode
argument_list|()
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|hints
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|SqlValidatorUtil
operator|.
name|deriveJoinRowType
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
argument_list|,
name|joinType
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
name|getSystemFieldList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether this LogicalJoin has already spawned a    * {@code SemiJoin} via    * {@link org.apache.calcite.rel.rules.JoinAddRedundantSemiJoinRule}.    *    *<p>The base implementation returns false.</p>    *    * @return whether this join has already spawned a semi join    */
specifier|public
name|boolean
name|isSemiJoinDone
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns whether this Join is a semijoin.    *    * @return true if this Join's join type is semi.    */
specifier|public
name|boolean
name|isSemiJoin
parameter_list|()
block|{
return|return
name|joinType
operator|==
name|JoinRelType
operator|.
name|SEMI
return|;
block|}
comment|/**    * Returns a list of system fields that will be prefixed to    * output row type.    *    * @return list of system fields    */
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getSystemFieldList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelDataType
name|deriveJoinRowType
parameter_list|(
name|RelDataType
name|leftType
parameter_list|,
name|RelDataType
name|rightType
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|deriveJoinRowType
argument_list|(
name|leftType
argument_list|,
name|rightType
argument_list|,
name|joinType
argument_list|,
name|typeFactory
argument_list|,
name|fieldNameList
argument_list|,
name|systemFieldList
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|RelDataType
name|createJoinType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|leftType
parameter_list|,
name|RelDataType
name|rightType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|createJoinType
argument_list|(
name|typeFactory
argument_list|,
name|leftType
argument_list|,
name|rightType
argument_list|,
name|fieldNameList
argument_list|,
name|systemFieldList
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|Join
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|2
assert|;
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|getCondition
argument_list|()
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|joinType
argument_list|,
name|isSemiJoinDone
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a copy of this join, overriding condition, system fields and    * inputs.    *    *<p>General contract as {@link RelNode#copy}.    *    * @param traitSet      Traits    * @param conditionExpr Condition    * @param left          Left input    * @param right         Right input    * @param joinType      Join type    * @param semiJoinDone  Whether this join has been translated to a    *                      semi-join    * @return Copy of this join    */
specifier|public
specifier|abstract
name|Join
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|conditionExpr
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
function_decl|;
comment|/**    * Analyzes the join condition.    *    * @return Analyzed join condition    */
specifier|public
name|JoinInfo
name|analyzeCondition
parameter_list|()
block|{
return|return
name|joinInfo
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|getHints
parameter_list|()
block|{
return|return
name|hints
return|;
block|}
block|}
end_class

end_unit

