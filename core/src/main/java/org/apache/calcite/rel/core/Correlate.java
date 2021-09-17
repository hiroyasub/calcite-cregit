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
name|RelInput
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
name|Litmus
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * A relational operator that performs nested-loop joins.  *  *<p>It behaves like a kind of {@link org.apache.calcite.rel.core.Join},  * but works by setting variables in its environment and restarting its  * right-hand input.  *  *<p>Correlate is not a join since: typical rules should not match Correlate.  *  *<p>A Correlate is used to represent a correlated query. One  * implementation strategy is to de-correlate the expression.  *  *<table>  *<caption>Mapping of physical operations to logical ones</caption>  *<tr><th>Physical operation</th><th>Logical operation</th></tr>  *<tr><td>NestedLoops</td><td>Correlate(A, B, regular)</td></tr>  *<tr><td>NestedLoopsOuter</td><td>Correlate(A, B, outer)</td></tr>  *<tr><td>NestedLoopsSemi</td><td>Correlate(A, B, semi)</td></tr>  *<tr><td>NestedLoopsAnti</td><td>Correlate(A, B, anti)</td></tr>  *<tr><td>HashJoin</td><td>EquiJoin(A, B)</td></tr>  *<tr><td>HashJoinOuter</td><td>EquiJoin(A, B, outer)</td></tr>  *<tr><td>HashJoinSemi</td><td>SemiJoin(A, B, semi)</td></tr>  *<tr><td>HashJoinAnti</td><td>SemiJoin(A, B, anti)</td></tr>  *</table>  *  * @see CorrelationId  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Correlate
extends|extends
name|BiRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|CorrelationId
name|correlationId
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableBitSet
name|requiredColumns
decl_stmt|;
specifier|protected
specifier|final
name|JoinRelType
name|joinType
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Correlate.    *    * @param cluster      Cluster this relational expression belongs to    * @param left         Left input relational expression    * @param right        Right input relational expression    * @param correlationId Variable name for the row of left input    * @param requiredColumns Set of columns that are used by correlation    * @param joinType Join type    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"method.invocation.invalid"
argument_list|)
specifier|protected
name|Correlate
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
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
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
assert|assert
operator|!
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
operator|:
literal|"Correlate has invalid join type "
operator|+
name|joinType
assert|;
name|this
operator|.
name|joinType
operator|=
name|requireNonNull
argument_list|(
name|joinType
argument_list|,
literal|"joinType"
argument_list|)
expr_stmt|;
name|this
operator|.
name|correlationId
operator|=
name|requireNonNull
argument_list|(
name|correlationId
argument_list|,
literal|"correlationId"
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredColumns
operator|=
name|requireNonNull
argument_list|(
name|requiredColumns
argument_list|,
literal|"requiredColumns"
argument_list|)
expr_stmt|;
assert|assert
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
literal|null
argument_list|)
assert|;
block|}
comment|/**    * Creates a Correlate by parsing serialized output.    *    * @param input Input representation    */
specifier|protected
name|Correlate
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|input
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|CorrelationId
argument_list|(
name|requireNonNull
argument_list|(
operator|(
name|Integer
operator|)
name|input
operator|.
name|get
argument_list|(
literal|"correlation"
argument_list|)
argument_list|,
literal|"correlation"
argument_list|)
argument_list|)
argument_list|,
name|input
operator|.
name|getBitSet
argument_list|(
literal|"requiredColumns"
argument_list|)
argument_list|,
name|requireNonNull
argument_list|(
name|input
operator|.
name|getEnum
argument_list|(
literal|"joinType"
argument_list|,
name|JoinRelType
operator|.
name|class
argument_list|)
argument_list|,
literal|"joinType"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|ImmutableBitSet
name|leftColumns
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|isValid
argument_list|(
name|litmus
argument_list|,
name|context
argument_list|)
operator|&&
name|litmus
operator|.
name|check
argument_list|(
name|leftColumns
operator|.
name|contains
argument_list|(
name|requiredColumns
argument_list|)
argument_list|,
literal|"Required columns {} not subset of left columns {}"
argument_list|,
name|requiredColumns
argument_list|,
name|leftColumns
argument_list|)
operator|&&
name|RelOptUtil
operator|.
name|notContainsCorrelation
argument_list|(
name|left
argument_list|,
name|correlationId
argument_list|,
name|litmus
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Correlate
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
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|Correlate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
function_decl|;
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
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|LEFT
case|:
case|case
name|INNER
case|:
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
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
case|case
name|ANTI
case|:
case|case
name|SEMI
case|:
return|return
name|left
operator|.
name|getRowType
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown join type "
operator|+
name|joinType
argument_list|)
throw|;
block|}
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
literal|"correlation"
argument_list|,
name|correlationId
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
name|item
argument_list|(
literal|"requiredColumns"
argument_list|,
name|requiredColumns
argument_list|)
return|;
block|}
comment|/**    * Returns the correlating expressions.    *    * @return correlating expressions    */
specifier|public
name|CorrelationId
name|getCorrelationId
parameter_list|()
block|{
return|return
name|correlationId
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCorrelVariable
parameter_list|()
block|{
return|return
name|correlationId
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Returns the required columns in left relation required for the correlation    * in the right.    *    * @return columns in left relation required for the correlation in the right    */
specifier|public
name|ImmutableBitSet
name|getRequiredColumns
parameter_list|()
block|{
return|return
name|requiredColumns
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
name|ImmutableSet
operator|.
name|of
argument_list|(
name|correlationId
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
name|double
name|leftRowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|left
argument_list|)
decl_stmt|;
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
return|return
name|leftRowCount
return|;
default|default:
return|return
name|leftRowCount
operator|*
name|mq
operator|.
name|getRowCount
argument_list|(
name|right
argument_list|)
return|;
block|}
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
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|rightRowCount
argument_list|)
condition|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeInfiniteCost
argument_list|()
return|;
block|}
name|Double
name|restartCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|getLeft
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|restartCount
operator|==
literal|null
condition|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeInfiniteCost
argument_list|()
return|;
block|}
comment|// RelMetadataQuery.getCumulativeCost(getRight()); does not work for
comment|// RelSubset, so we ask planner to cost-estimate right relation
name|RelOptCost
name|rightCost
init|=
name|planner
operator|.
name|getCost
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|mq
argument_list|)
decl_stmt|;
if|if
condition|(
name|rightCost
operator|==
literal|null
condition|)
block|{
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeInfiniteCost
argument_list|()
return|;
block|}
name|RelOptCost
name|rescanCost
init|=
name|rightCost
operator|.
name|multiplyBy
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|1.0
argument_list|,
name|restartCount
operator|-
literal|1
argument_list|)
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
comment|/* generate results */
operator|+
name|leftRowCount
comment|/* scan left results */
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|plus
argument_list|(
name|rescanCost
argument_list|)
return|;
block|}
block|}
end_class

end_unit

