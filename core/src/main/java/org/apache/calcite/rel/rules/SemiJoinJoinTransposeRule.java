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
name|tools
operator|.
name|RelBuilderFactory
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
name|immutables
operator|.
name|value
operator|.
name|Value
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
comment|/**  * Planner rule that pushes a {@link Join#isSemiJoin semi-join}  * down in a tree past a {@link org.apache.calcite.rel.core.Join}  * in order to trigger other rules that will convert {@code SemiJoin}s.  *  *<ul>  *<li>SemiJoin(LogicalJoin(X, Y), Z)&rarr; LogicalJoin(SemiJoin(X, Z), Y)  *<li>SemiJoin(LogicalJoin(X, Y), Z)&rarr; LogicalJoin(X, SemiJoin(Y, Z))  *</ul>  *  *<p>Whether this  * first or second conversion is applied depends on which operands actually  * participate in the semi-join.  *  * @see CoreRules#SEMI_JOIN_JOIN_TRANSPOSE  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|SemiJoinJoinTransposeRule
extends|extends
name|RelRule
argument_list|<
name|SemiJoinJoinTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a SemiJoinJoinTransposeRule. */
specifier|protected
name|SemiJoinJoinTransposeRule
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
name|SemiJoinJoinTransposeRule
parameter_list|(
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
argument_list|)
expr_stmt|;
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
name|Join
name|semiJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|join
operator|.
name|isSemiJoin
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|ImmutableIntList
name|leftKeys
init|=
name|semiJoin
operator|.
name|analyzeCondition
argument_list|()
operator|.
name|leftKeys
decl_stmt|;
comment|// X is the left child of the join below the semi-join
comment|// Y is the right child of the join below the semi-join
comment|// Z is the right child of the semi-join
specifier|final
name|int
name|nFieldsX
init|=
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nFieldsY
init|=
name|join
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nFieldsZ
init|=
name|semiJoin
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nTotalFields
init|=
name|nFieldsX
operator|+
name|nFieldsY
operator|+
name|nFieldsZ
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// create a list of fields for the full join result; note that
comment|// we can't simply use the fields from the semi-join because the
comment|// row-type of a semi-join only includes the left hand side fields
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|joinFields
init|=
name|semiJoin
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|nFieldsX
operator|+
name|nFieldsY
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|joinFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|joinFields
operator|=
name|semiJoin
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nFieldsZ
condition|;
name|i
operator|++
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|joinFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// determine which operands below the semi-join are the actual
comment|// Rels that participate in the semi-join
name|int
name|nKeysFromX
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|leftKey
range|:
name|leftKeys
control|)
block|{
if|if
condition|(
name|leftKey
operator|<
name|nFieldsX
condition|)
block|{
name|nKeysFromX
operator|++
expr_stmt|;
block|}
block|}
comment|// the keys must all originate from either the left or right;
comment|// otherwise, a semi-join wouldn't have been created
assert|assert
operator|(
name|nKeysFromX
operator|==
literal|0
operator|)
operator|||
operator|(
name|nKeysFromX
operator|==
name|leftKeys
operator|.
name|size
argument_list|()
operator|)
assert|;
comment|// need to convert the semi-join condition and possibly the keys
specifier|final
name|RexNode
name|newSemiJoinFilter
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|nTotalFields
index|]
decl_stmt|;
if|if
condition|(
name|nKeysFromX
operator|>
literal|0
condition|)
block|{
comment|// (X, Y, Z) --> (X, Z, Y)
comment|// semiJoin(X, Z)
comment|// pass 0 as Y's adjustment because there shouldn't be any
comment|// references to Y in the semi-join filter
name|setJoinAdjustments
argument_list|(
name|adjustments
argument_list|,
name|nFieldsX
argument_list|,
name|nFieldsY
argument_list|,
name|nFieldsZ
argument_list|,
literal|0
argument_list|,
operator|-
name|nFieldsY
argument_list|)
expr_stmt|;
name|newSemiJoinFilter
operator|=
name|semiJoin
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|semiJoin
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|fields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// (X, Y, Z) --> (X, Y, Z)
comment|// semiJoin(Y, Z)
name|setJoinAdjustments
argument_list|(
name|adjustments
argument_list|,
name|nFieldsX
argument_list|,
name|nFieldsY
argument_list|,
name|nFieldsZ
argument_list|,
operator|-
name|nFieldsX
argument_list|,
operator|-
name|nFieldsX
argument_list|)
expr_stmt|;
name|newSemiJoinFilter
operator|=
name|semiJoin
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|semiJoin
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|fields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create the new join
specifier|final
name|RelNode
name|leftSemiJoinOp
decl_stmt|;
if|if
condition|(
name|nKeysFromX
operator|>
literal|0
condition|)
block|{
name|leftSemiJoinOp
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leftSemiJoinOp
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
specifier|final
name|LogicalJoin
name|newSemiJoin
init|=
name|LogicalJoin
operator|.
name|create
argument_list|(
name|leftSemiJoinOp
argument_list|,
name|semiJoin
operator|.
name|getRight
argument_list|()
argument_list|,
comment|// No need to copy the hints, the framework would try to do that.
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|newSemiJoinFilter
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|JoinRelType
operator|.
name|SEMI
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|left
decl_stmt|;
specifier|final
name|RelNode
name|right
decl_stmt|;
if|if
condition|(
name|nKeysFromX
operator|>
literal|0
condition|)
block|{
name|left
operator|=
name|newSemiJoin
expr_stmt|;
name|right
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|left
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
name|right
operator|=
name|newSemiJoin
expr_stmt|;
block|}
specifier|final
name|RelNode
name|newJoin
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
name|left
argument_list|,
name|right
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
name|transformTo
argument_list|(
name|newJoin
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets an array to reflect how much each index corresponding to a field    * needs to be adjusted. The array corresponds to fields in a 3-way join    * between (X, Y, and Z). X remains unchanged, but Y and Z need to be    * adjusted by some fixed amount as determined by the input.    *    * @param adjustments array to be filled out    * @param nFieldsX    number of fields in X    * @param nFieldsY    number of fields in Y    * @param nFieldsZ    number of fields in Z    * @param adjustY     the amount to adjust Y by    * @param adjustZ     the amount to adjust Z by    */
specifier|private
specifier|static
name|void
name|setJoinAdjustments
parameter_list|(
name|int
index|[]
name|adjustments
parameter_list|,
name|int
name|nFieldsX
parameter_list|,
name|int
name|nFieldsY
parameter_list|,
name|int
name|nFieldsZ
parameter_list|,
name|int
name|adjustY
parameter_list|,
name|int
name|adjustZ
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nFieldsX
condition|;
name|i
operator|++
control|)
block|{
name|adjustments
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|nFieldsX
init|;
name|i
operator|<
operator|(
name|nFieldsX
operator|+
name|nFieldsY
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|adjustments
index|[
name|i
index|]
operator|=
name|adjustY
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|nFieldsX
operator|+
name|nFieldsY
init|;
name|i
operator|<
operator|(
name|nFieldsX
operator|+
name|nFieldsY
operator|+
name|nFieldsZ
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|adjustments
index|[
name|i
index|]
operator|=
name|adjustZ
expr_stmt|;
block|}
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
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
name|ImmutableSemiJoinJoinTransposeRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandFor
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|Join
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|SemiJoinJoinTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|SemiJoinJoinTransposeRule
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
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|join2Class
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
name|joinClass
argument_list|)
operator|.
name|predicate
argument_list|(
name|Join
operator|::
name|isSemiJoin
argument_list|)
operator|.
name|inputs
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|join2Class
argument_list|)
operator|.
name|anyInputs
argument_list|()
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

