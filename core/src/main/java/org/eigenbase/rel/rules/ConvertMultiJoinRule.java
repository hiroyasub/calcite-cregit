begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|Pair
import|;
end_import

begin_comment
comment|/**  * Rule to flatten a tree of {@link JoinRel}s into a single {@link MultiJoinRel}  * with N inputs. An input is not flattened if the input is a null generating  * input in an outer join, i.e., either input in a full outer join, the right  * hand side of a left outer join, or the left hand side of a right outer join.  *  *<p>Join conditions are also pulled up from the inputs into the topmost {@link  * MultiJoinRel}, unless the input corresponds to a null generating input in an  * outer join,  *  *<p>Outer join information is also stored in the {@link MultiJoinRel}. A  * boolean flag indicates if the join is a full outer join, and in the case of  * left and right outer joins, the join type and outer join conditions are  * stored in arrays in the {@link MultiJoinRel}. This outer join information is  * associated with the null generating input in the outer join. So, in the case  * of a a left outer join between A and B, the information is associated with B,  * not A.  *  *<p>Here are examples of the {@link MultiJoinRel}s constructed after this rule  * has been applied on following join trees.  *  *<pre>  * A JOIN B -> MJ(A, B)  * A JOIN B JOIN C -> MJ(A, B, C)  * A LEFTOUTER B -> MJ(A, B), left outer join on input#1  * A RIGHTOUTER B -> MJ(A, B), right outer join on input#0  * A FULLOUTER B -> MJ[full](A, B)  * A LEFTOUTER (B JOIN C) -> MJ(A, MJ(B, C))), left outer join on input#1 in  * the outermost MultiJoinRel  * (A JOIN B) LEFTOUTER C -> MJ(A, B, C), left outer join on input#2  * A LEFTOUTER (B FULLOUTER C) -> MJ(A, MJ[full](B, C)), left outer join on  *      input#1 in the outermost MultiJoinRel  * (A LEFTOUTER B) FULLOUTER (C RIGHTOUTER D) ->  *      MJ[full](MJ(A, B), MJ(C, D)), left outer join on input #1 in the first  *      inner MultiJoinRel and right outer join on input#0 in the second inner  *      MultiJoinRel  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|ConvertMultiJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|ConvertMultiJoinRule
name|instance
init|=
operator|new
name|ConvertMultiJoinRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ConvertMultiJoinRule.    */
specifier|private
name|ConvertMultiJoinRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|JoinRel
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
name|JoinRel
name|origJoinRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelNode
name|left
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelNode
name|right
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// combine the children MultiJoinRel inputs into an array of inputs
comment|// for the new MultiJoinRel
name|List
argument_list|<
name|BitSet
argument_list|>
name|projFieldsList
init|=
operator|new
name|ArrayList
argument_list|<
name|BitSet
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|joinFieldRefCountsList
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
name|combineInputs
argument_list|(
name|origJoinRel
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|projFieldsList
argument_list|,
name|joinFieldRefCountsList
argument_list|)
decl_stmt|;
comment|// combine the outer join information from the left and right
comment|// inputs, and include the outer join information from the current
comment|// join, if it's a left/right outer join
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|joinSpecs
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|combineOuterJoins
argument_list|(
name|origJoinRel
argument_list|,
name|newInputs
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|joinSpecs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|newOuterJoinConds
init|=
name|Pair
operator|.
name|right
argument_list|(
name|joinSpecs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|JoinRelType
argument_list|>
name|joinTypes
init|=
name|Pair
operator|.
name|left
argument_list|(
name|joinSpecs
argument_list|)
decl_stmt|;
comment|// pull up the join filters from the children MultiJoinRels and
comment|// combine them with the join filter associated with this JoinRel to
comment|// form the join filter for the new MultiJoinRel
name|RexNode
name|newJoinFilter
init|=
name|combineJoinFilters
argument_list|(
name|origJoinRel
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
comment|// add on the join field reference counts for the join condition
comment|// associated with this JoinRel
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|newJoinFieldRefCountsMap
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|addOnJoinFieldRefCounts
argument_list|(
name|newInputs
argument_list|,
name|origJoinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|origJoinRel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|joinFieldRefCountsList
argument_list|,
name|newJoinFieldRefCountsMap
argument_list|)
expr_stmt|;
name|RexNode
name|newPostJoinFilter
init|=
name|combinePostJoinFilters
argument_list|(
name|origJoinRel
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
name|RelNode
name|multiJoin
init|=
operator|new
name|MultiJoinRel
argument_list|(
name|origJoinRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newInputs
argument_list|,
name|newJoinFilter
argument_list|,
name|origJoinRel
operator|.
name|getRowType
argument_list|()
argument_list|,
operator|(
name|origJoinRel
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|FULL
operator|)
argument_list|,
name|newOuterJoinConds
argument_list|,
name|joinTypes
argument_list|,
name|projFieldsList
argument_list|,
name|newJoinFieldRefCountsMap
argument_list|,
name|newPostJoinFilter
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|multiJoin
argument_list|)
expr_stmt|;
block|}
comment|/**    * Combines the inputs into a JoinRel into an array of inputs.    *    * @param join                   original join    * @param left                   left input into join    * @param right                  right input into join    * @param projFieldsList         returns a list of the new combined projection    *                               fields    * @param joinFieldRefCountsList returns a list of the new combined join    *                               field reference counts    * @return combined left and right inputs in an array    */
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|combineInputs
parameter_list|(
name|JoinRel
name|join
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|List
argument_list|<
name|BitSet
argument_list|>
name|projFieldsList
parameter_list|,
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|joinFieldRefCountsList
parameter_list|)
block|{
comment|// leave the null generating sides of an outer join intact; don't
comment|// pull up those children inputs into the array we're constructing
name|int
name|nInputs
decl_stmt|;
name|int
name|nInputsOnLeft
decl_stmt|;
specifier|final
name|MultiJoinRel
name|leftMultiJoin
decl_stmt|;
name|JoinRelType
name|joinType
init|=
name|join
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
name|boolean
name|combineLeft
init|=
name|canCombine
argument_list|(
name|left
argument_list|,
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineLeft
condition|)
block|{
name|leftMultiJoin
operator|=
operator|(
name|MultiJoinRel
operator|)
name|left
expr_stmt|;
name|nInputs
operator|=
name|left
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|nInputsOnLeft
operator|=
name|nInputs
expr_stmt|;
block|}
else|else
block|{
name|leftMultiJoin
operator|=
literal|null
expr_stmt|;
name|nInputs
operator|=
literal|1
expr_stmt|;
name|nInputsOnLeft
operator|=
literal|1
expr_stmt|;
block|}
specifier|final
name|MultiJoinRel
name|rightMultiJoin
decl_stmt|;
name|boolean
name|combineRight
init|=
name|canCombine
argument_list|(
name|right
argument_list|,
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineRight
condition|)
block|{
name|rightMultiJoin
operator|=
operator|(
name|MultiJoinRel
operator|)
name|right
expr_stmt|;
name|nInputs
operator|+=
name|right
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rightMultiJoin
operator|=
literal|null
expr_stmt|;
name|nInputs
operator|+=
literal|1
expr_stmt|;
block|}
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|combineLeft
condition|)
block|{
for|for
control|(
init|;
name|i
operator|<
name|left
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|newInputs
operator|.
name|add
argument_list|(
name|leftMultiJoin
operator|.
name|getInput
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|projFieldsList
operator|.
name|add
argument_list|(
name|leftMultiJoin
operator|.
name|getProjFields
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|joinFieldRefCountsList
operator|.
name|add
argument_list|(
name|leftMultiJoin
operator|.
name|getJoinFieldRefCountsMap
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|newInputs
operator|.
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|i
operator|=
literal|1
expr_stmt|;
name|projFieldsList
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|joinFieldRefCountsList
operator|.
name|add
argument_list|(
operator|new
name|int
index|[
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|combineRight
condition|)
block|{
for|for
control|(
init|;
name|i
operator|<
name|nInputs
condition|;
name|i
operator|++
control|)
block|{
name|newInputs
operator|.
name|add
argument_list|(
name|rightMultiJoin
operator|.
name|getInput
argument_list|(
name|i
operator|-
name|nInputsOnLeft
argument_list|)
argument_list|)
expr_stmt|;
name|projFieldsList
operator|.
name|add
argument_list|(
name|rightMultiJoin
operator|.
name|getProjFields
argument_list|()
operator|.
name|get
argument_list|(
name|i
operator|-
name|nInputsOnLeft
argument_list|)
argument_list|)
expr_stmt|;
name|joinFieldRefCountsList
operator|.
name|add
argument_list|(
name|rightMultiJoin
operator|.
name|getJoinFieldRefCountsMap
argument_list|()
operator|.
name|get
argument_list|(
name|i
operator|-
name|nInputsOnLeft
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|newInputs
operator|.
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|projFieldsList
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|joinFieldRefCountsList
operator|.
name|add
argument_list|(
operator|new
name|int
index|[
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|newInputs
return|;
block|}
comment|/**    * Combines the outer join conditions and join types from the left and right    * join inputs. If the join itself is either a left or right outer join,    * then the join condition corresponding to the join is also set in the    * position corresponding to the null-generating input into the join. The    * join type is also set.    *    * @param joinRel        join rel    * @param combinedInputs the combined inputs to the join    * @param left           left child of the joinrel    * @param right          right child of the joinrel    * @param joinSpecs      the list where the join types and conditions will be    *                       copied    */
specifier|private
name|void
name|combineOuterJoins
parameter_list|(
name|JoinRel
name|joinRel
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|combinedInputs
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|joinSpecs
parameter_list|)
block|{
name|JoinRelType
name|joinType
init|=
name|joinRel
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
name|boolean
name|leftCombined
init|=
name|canCombine
argument_list|(
name|left
argument_list|,
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|rightCombined
init|=
name|canCombine
argument_list|(
name|right
argument_list|,
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|LEFT
case|:
if|if
condition|(
name|leftCombined
condition|)
block|{
name|copyOuterJoinInfo
argument_list|(
operator|(
name|MultiJoinRel
operator|)
name|left
argument_list|,
name|joinSpecs
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
operator|(
name|RexNode
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|joinType
argument_list|,
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|RIGHT
case|:
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|joinType
argument_list|,
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rightCombined
condition|)
block|{
name|copyOuterJoinInfo
argument_list|(
operator|(
name|MultiJoinRel
operator|)
name|right
argument_list|,
name|joinSpecs
argument_list|,
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
operator|(
name|RexNode
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
if|if
condition|(
name|leftCombined
condition|)
block|{
name|copyOuterJoinInfo
argument_list|(
operator|(
name|MultiJoinRel
operator|)
name|left
argument_list|,
name|joinSpecs
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
operator|(
name|RexNode
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rightCombined
condition|)
block|{
name|copyOuterJoinInfo
argument_list|(
operator|(
name|MultiJoinRel
operator|)
name|right
argument_list|,
name|joinSpecs
argument_list|,
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
operator|(
name|RexNode
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Copies outer join data from a source MultiJoinRel to a new set of arrays.    * Also adjusts the conditions to reflect the new position of an input if    * that input ends up being shifted to the right.    *    * @param multiJoinRel     the source MultiJoinRel    * @param destJoinSpecs    the list where the join types and conditions will    *                         be copied    * @param adjustmentAmount if&gt; 0, the amount the RexInputRefs in the join    *                         conditions need to be adjusted by    * @param srcFields        the source fields that the original join conditions    *                         are referencing    * @param destFields       the destination fields that the new join conditions    */
specifier|private
name|void
name|copyOuterJoinInfo
parameter_list|(
name|MultiJoinRel
name|multiJoinRel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|destJoinSpecs
parameter_list|,
name|int
name|adjustmentAmount
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|srcFields
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|destFields
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|srcJoinSpecs
init|=
name|Pair
operator|.
name|zip
argument_list|(
name|multiJoinRel
operator|.
name|getJoinTypes
argument_list|()
argument_list|,
name|multiJoinRel
operator|.
name|getOuterJoinConditions
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|adjustmentAmount
operator|==
literal|0
condition|)
block|{
name|destJoinSpecs
operator|.
name|addAll
argument_list|(
name|srcJoinSpecs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|nFields
init|=
name|srcFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|nFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|nFields
condition|;
name|idx
operator|++
control|)
block|{
name|adjustments
index|[
name|idx
index|]
operator|=
name|adjustmentAmount
expr_stmt|;
block|}
for|for
control|(
name|Pair
argument_list|<
name|JoinRelType
argument_list|,
name|RexNode
argument_list|>
name|src
range|:
name|srcJoinSpecs
control|)
block|{
name|destJoinSpecs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|src
operator|.
name|left
argument_list|,
name|src
operator|.
name|right
operator|==
literal|null
condition|?
literal|null
else|:
name|src
operator|.
name|right
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|multiJoinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|srcFields
argument_list|,
name|destFields
argument_list|,
name|adjustments
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Combines the join filters from the left and right inputs (if they are    * MultiJoinRels) with the join filter in the joinrel into a single AND'd    * join filter, unless the inputs correspond to null generating inputs in an    * outer join    *    * @param joinRel join rel    * @param left    left child of the joinrel    * @param right   right child of the joinrel    * @return combined join filters AND'd together    */
specifier|private
name|RexNode
name|combineJoinFilters
parameter_list|(
name|JoinRel
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|)
block|{
name|RexBuilder
name|rexBuilder
init|=
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|JoinRelType
name|joinType
init|=
name|joinRel
operator|.
name|getJoinType
argument_list|()
decl_stmt|;
comment|// first need to adjust the RexInputs of the right child, since
comment|// those need to shift over to the right
name|RexNode
name|rightFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|canCombine
argument_list|(
name|right
argument_list|,
name|joinType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
condition|)
block|{
name|MultiJoinRel
name|multiJoin
init|=
operator|(
name|MultiJoinRel
operator|)
name|right
decl_stmt|;
name|rightFilter
operator|=
name|shiftRightFilter
argument_list|(
name|joinRel
argument_list|,
name|left
argument_list|,
name|multiJoin
argument_list|,
name|multiJoin
operator|.
name|getJoinFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// AND the join condition if this isn't a left or right outer join;
comment|// in those cases, the outer join condition is already tracked
comment|// separately
name|RexNode
name|newFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|joinType
operator|!=
name|JoinRelType
operator|.
name|LEFT
operator|)
operator|&&
operator|(
name|joinType
operator|!=
name|JoinRelType
operator|.
name|RIGHT
operator|)
condition|)
block|{
name|newFilter
operator|=
name|joinRel
operator|.
name|getCondition
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|canCombine
argument_list|(
name|left
argument_list|,
name|joinType
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
condition|)
block|{
name|RexNode
name|leftFilter
init|=
operator|(
operator|(
name|MultiJoinRel
operator|)
name|left
operator|)
operator|.
name|getJoinFilter
argument_list|()
decl_stmt|;
name|newFilter
operator|=
name|RelOptUtil
operator|.
name|andJoinFilters
argument_list|(
name|rexBuilder
argument_list|,
name|newFilter
argument_list|,
name|leftFilter
argument_list|)
expr_stmt|;
block|}
name|newFilter
operator|=
name|RelOptUtil
operator|.
name|andJoinFilters
argument_list|(
name|rexBuilder
argument_list|,
name|newFilter
argument_list|,
name|rightFilter
argument_list|)
expr_stmt|;
return|return
name|newFilter
return|;
block|}
comment|/**    * @param input          input into a join    * @param nullGenerating true if the input is null generating    * @return true if the input can be combined into a parent MultiJoinRel    */
specifier|private
name|boolean
name|canCombine
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|boolean
name|nullGenerating
parameter_list|)
block|{
return|return
operator|(
operator|(
name|input
operator|instanceof
name|MultiJoinRel
operator|)
operator|&&
operator|!
operator|(
operator|(
name|MultiJoinRel
operator|)
name|input
operator|)
operator|.
name|isFullOuterJoin
argument_list|()
operator|&&
operator|!
name|nullGenerating
operator|)
return|;
block|}
comment|/**    * Shifts a filter originating from the right child of the JoinRel to the    * right, to reflect the filter now being applied on the resulting    * MultiJoinRel.    *    * @param joinRel     the original JoinRel    * @param left        the left child of the JoinRel    * @param right       the right child of the JoinRel    * @param rightFilter the filter originating from the right child    * @return the adjusted right filter    */
specifier|private
name|RexNode
name|shiftRightFilter
parameter_list|(
name|JoinRel
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|MultiJoinRel
name|right
parameter_list|,
name|RexNode
name|rightFilter
parameter_list|)
block|{
if|if
condition|(
name|rightFilter
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|nFieldsOnLeft
init|=
name|left
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
name|nFieldsOnRight
init|=
name|right
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
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|nFieldsOnRight
index|]
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
name|nFieldsOnRight
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
name|nFieldsOnLeft
expr_stmt|;
block|}
name|rightFilter
operator|=
name|rightFilter
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|adjustments
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rightFilter
return|;
block|}
comment|/**    * Adds on to the existing join condition reference counts the references    * from the new join condition.    *    * @param multiJoinInputs          inputs into the new MultiJoinRel    * @param nTotalFields             total number of fields in the MultiJoinRel    * @param joinCondition            the new join condition    * @param origJoinFieldRefCounts   existing join condition reference counts    * @param newJoinFieldRefCountsMap map containing the new join condition    */
specifier|private
name|void
name|addOnJoinFieldRefCounts
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|multiJoinInputs
parameter_list|,
name|int
name|nTotalFields
parameter_list|,
name|RexNode
name|joinCondition
parameter_list|,
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|origJoinFieldRefCounts
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|newJoinFieldRefCountsMap
parameter_list|)
block|{
comment|// count the input references in the join condition
name|int
index|[]
name|joinCondRefCounts
init|=
operator|new
name|int
index|[
name|nTotalFields
index|]
decl_stmt|;
name|joinCondition
operator|.
name|accept
argument_list|(
operator|new
name|InputReferenceCounter
argument_list|(
name|joinCondRefCounts
argument_list|)
argument_list|)
expr_stmt|;
comment|// first, make a copy of the ref counters
name|int
name|nInputs
init|=
name|multiJoinInputs
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|currInput
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
index|[]
name|origRefCounts
range|:
name|origJoinFieldRefCounts
control|)
block|{
name|newJoinFieldRefCountsMap
operator|.
name|put
argument_list|(
name|currInput
argument_list|,
name|origRefCounts
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
name|currInput
operator|++
expr_stmt|;
block|}
comment|// add on to the counts for each input into the MultiJoinRel the
comment|// reference counts computed for the current join condition
name|currInput
operator|=
operator|-
literal|1
expr_stmt|;
name|int
name|startField
init|=
literal|0
decl_stmt|;
name|int
name|nFields
init|=
literal|0
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
name|nTotalFields
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|joinCondRefCounts
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
while|while
condition|(
name|i
operator|>=
operator|(
name|startField
operator|+
name|nFields
operator|)
condition|)
block|{
name|startField
operator|+=
name|nFields
expr_stmt|;
name|currInput
operator|++
expr_stmt|;
assert|assert
operator|(
name|currInput
operator|<
name|nInputs
operator|)
assert|;
name|nFields
operator|=
name|multiJoinInputs
operator|.
name|get
argument_list|(
name|currInput
argument_list|)
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
block|}
name|int
index|[]
name|refCounts
init|=
name|newJoinFieldRefCountsMap
operator|.
name|get
argument_list|(
name|currInput
argument_list|)
decl_stmt|;
name|refCounts
index|[
name|i
operator|-
name|startField
index|]
operator|+=
name|joinCondRefCounts
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|/**    * Combines the post-join filters from the left and right inputs (if they    * are MultiJoinRels) into a single AND'd filter.    *    * @param joinRel the original JoinRel    * @param left    left child of the JoinRel    * @param right   right child of the JoinRel    * @return combined post-join filters AND'd together    */
specifier|private
name|RexNode
name|combinePostJoinFilters
parameter_list|(
name|JoinRel
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|)
block|{
name|RexNode
name|rightPostJoinFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|right
operator|instanceof
name|MultiJoinRel
condition|)
block|{
name|rightPostJoinFilter
operator|=
name|shiftRightFilter
argument_list|(
name|joinRel
argument_list|,
name|left
argument_list|,
operator|(
name|MultiJoinRel
operator|)
name|right
argument_list|,
operator|(
operator|(
name|MultiJoinRel
operator|)
name|right
operator|)
operator|.
name|getPostJoinFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|leftPostJoinFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|left
operator|instanceof
name|MultiJoinRel
condition|)
block|{
name|leftPostJoinFilter
operator|=
operator|(
operator|(
name|MultiJoinRel
operator|)
name|left
operator|)
operator|.
name|getPostJoinFilter
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|leftPostJoinFilter
operator|==
literal|null
operator|)
operator|&&
operator|(
name|rightPostJoinFilter
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|RelOptUtil
operator|.
name|andJoinFilters
argument_list|(
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|leftPostJoinFilter
argument_list|,
name|rightPostJoinFilter
argument_list|)
return|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Visitor that keeps a reference count of the inputs used by an expression.    */
specifier|private
class|class
name|InputReferenceCounter
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
specifier|private
specifier|final
name|int
index|[]
name|refCounts
decl_stmt|;
specifier|public
name|InputReferenceCounter
parameter_list|(
name|int
index|[]
name|refCounts
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|refCounts
operator|=
name|refCounts
expr_stmt|;
block|}
specifier|public
name|Void
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
name|refCounts
index|[
name|inputRef
operator|.
name|getIndex
argument_list|()
index|]
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConvertMultiJoinRule.java
end_comment

end_unit

