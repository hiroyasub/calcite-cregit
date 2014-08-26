begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|ImmutableIntList
import|;
end_import

begin_comment
comment|/**  * PushSemiJoinPastJoinRule implements the rule for pushing semi-joins down in a  * tree past a join in order to trigger other rules that will convert  * semi-joins.  *  *<ul>  *<li>SemiJoinRel(JoinRel(X, Y), Z)&rarr; JoinRel(SemiJoinRel(X, Z), Y)  *<li>SemiJoinRel(JoinRel(X, Y), Z)&rarr; JoinRel(X, SemiJoinRel(Y, Z))  *</ul>  *  *<p>Whether this  * first or second conversion is applied depends on which operands actually  * participate in the semi-join.</p>  */
end_comment

begin_class
specifier|public
class|class
name|PushSemiJoinPastJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|PushSemiJoinPastJoinRule
name|INSTANCE
init|=
operator|new
name|PushSemiJoinPastJoinRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a PushSemiJoinPastJoinRule.    */
specifier|private
name|PushSemiJoinPastJoinRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|SemiJoinRel
operator|.
name|class
argument_list|,
name|some
argument_list|(
name|operand
argument_list|(
name|JoinRelBase
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
name|SemiJoinRel
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
name|JoinRelBase
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
operator|instanceof
name|SemiJoinRel
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
name|getLeftKeys
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableIntList
name|rightKeys
init|=
name|semiJoin
operator|.
name|getRightKeys
argument_list|()
decl_stmt|;
comment|// X is the left child of the join below the semi-join
comment|// Y is the right child of the join below the semi-join
comment|// Z is the right child of the semi-join
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
name|int
name|nTotalFields
init|=
name|nFieldsX
operator|+
name|nFieldsY
operator|+
name|nFieldsZ
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
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
name|RexNode
name|newSemiJoinFilter
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|newLeftKeys
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
name|newLeftKeys
operator|=
name|leftKeys
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
name|newLeftKeys
operator|=
name|RelOptUtil
operator|.
name|adjustKeys
argument_list|(
name|leftKeys
argument_list|,
operator|-
name|nFieldsX
argument_list|)
expr_stmt|;
block|}
comment|// create the new join
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
name|SemiJoinRel
name|newSemiJoin
init|=
operator|new
name|SemiJoinRel
argument_list|(
name|semiJoin
operator|.
name|getCluster
argument_list|()
argument_list|,
name|semiJoin
operator|.
name|getCluster
argument_list|()
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|leftSemiJoinOp
argument_list|,
name|semiJoin
operator|.
name|getRight
argument_list|()
argument_list|,
name|newSemiJoinFilter
argument_list|,
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|newLeftKeys
argument_list|)
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
name|RelNode
name|leftJoinRel
decl_stmt|;
name|RelNode
name|rightJoinRel
decl_stmt|;
if|if
condition|(
name|nKeysFromX
operator|>
literal|0
condition|)
block|{
name|leftJoinRel
operator|=
name|newSemiJoin
expr_stmt|;
name|rightJoinRel
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leftJoinRel
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
name|rightJoinRel
operator|=
name|newSemiJoin
expr_stmt|;
block|}
name|RelNode
name|newJoinRel
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
name|leftJoinRel
argument_list|,
name|rightJoinRel
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
name|newJoinRel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets an array to reflect how much each index corresponding to a field    * needs to be adjusted. The array corresponds to fields in a 3-way join    * between (X, Y, and Z). X remains unchanged, but Y and Z need to be    * adjusted by some fixed amount as determined by the input.    *    * @param adjustments array to be filled out    * @param nFieldsX    number of fields in X    * @param nFieldsY    number of fields in Y    * @param nFieldsZ    number of fields in Z    * @param adjustY     the amount to adjust Y by    * @param adjustZ     the amount to adjust Z by    */
specifier|private
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
block|}
end_class

begin_comment
comment|// End PushSemiJoinPastJoinRule.java
end_comment

end_unit

