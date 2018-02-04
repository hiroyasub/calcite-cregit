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
name|rex
operator|.
name|RexInputRef
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
name|RexUtil
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
name|RexVisitorImpl
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
name|ImmutableMap
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
name|Lists
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
name|Maps
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Planner rule to flatten a tree of  * {@link org.apache.calcite.rel.logical.LogicalJoin}s  * into a single {@link MultiJoin} with N inputs.  *  *<p>An input is not flattened if  * the input is a null generating input in an outer join, i.e., either input in  * a full outer join, the right hand side of a left outer join, or the left hand  * side of a right outer join.  *  *<p>Join conditions are also pulled up from the inputs into the topmost  * {@link MultiJoin},  * unless the input corresponds to a null generating input in an outer join,  *  *<p>Outer join information is also stored in the {@link MultiJoin}. A  * boolean flag indicates if the join is a full outer join, and in the case of  * left and right outer joins, the join type and outer join conditions are  * stored in arrays in the {@link MultiJoin}. This outer join information is  * associated with the null generating input in the outer join. So, in the case  * of a a left outer join between A and B, the information is associated with B,  * not A.  *  *<p>Here are examples of the {@link MultiJoin}s constructed after this rule  * has been applied on following join trees.  *  *<ul>  *<li>A JOIN B&rarr; MJ(A, B)  *  *<li>A JOIN B JOIN C&rarr; MJ(A, B, C)  *  *<li>A LEFT JOIN B&rarr; MJ(A, B), left outer join on input#1  *  *<li>A RIGHT JOIN B&rarr; MJ(A, B), right outer join on input#0  *  *<li>A FULL JOIN B&rarr; MJ[full](A, B)  *  *<li>A LEFT JOIN (B JOIN C)&rarr; MJ(A, MJ(B, C))), left outer join on  * input#1 in the outermost MultiJoin  *  *<li>(A JOIN B) LEFT JOIN C&rarr; MJ(A, B, C), left outer join on input#2  *  *<li>(A LEFT JOIN B) JOIN C&rarr; MJ(MJ(A, B), C), left outer join on input#1  * of the inner MultiJoin        TODO  *  *<li>A LEFT JOIN (B FULL JOIN C)&rarr; MJ(A, MJ[full](B, C)), left outer join  * on input#1 in the outermost MultiJoin  *  *<li>(A LEFT JOIN B) FULL JOIN (C RIGHT JOIN D)&rarr;  *      MJ[full](MJ(A, B), MJ(C, D)), left outer join on input #1 in the first  *      inner MultiJoin and right outer join on input#0 in the second inner  *      MultiJoin  *</ul>  *  *<p>The constructor is parameterized to allow any sub-class of  * {@link org.apache.calcite.rel.core.Join}, not just  * {@link org.apache.calcite.rel.logical.LogicalJoin}.</p>  *  * @see org.apache.calcite.rel.rules.FilterMultiJoinMergeRule  * @see org.apache.calcite.rel.rules.ProjectMultiJoinMergeRule  */
end_comment

begin_class
specifier|public
class|class
name|JoinToMultiJoinRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|JoinToMultiJoinRule
name|INSTANCE
init|=
operator|new
name|JoinToMultiJoinRule
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|JoinToMultiJoinRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a JoinToMultiJoinRule.    */
specifier|public
name|JoinToMultiJoinRule
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
specifier|final
name|Join
name|origJoin
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
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
comment|// combine the children MultiJoin inputs into an array of inputs
comment|// for the new MultiJoin
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|projFieldsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|joinFieldRefCountsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
name|combineInputs
argument_list|(
name|origJoin
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
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|combineOuterJoins
argument_list|(
name|origJoin
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
comment|// pull up the join filters from the children MultiJoinRels and
comment|// combine them with the join filter associated with this LogicalJoin to
comment|// form the join filter for the new MultiJoin
name|List
argument_list|<
name|RexNode
argument_list|>
name|newJoinFilters
init|=
name|combineJoinFilters
argument_list|(
name|origJoin
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
comment|// add on the join field reference counts for the join condition
comment|// associated with this LogicalJoin
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
name|newJoinFieldRefCountsMap
init|=
name|addOnJoinFieldRefCounts
argument_list|(
name|newInputs
argument_list|,
name|origJoin
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|origJoin
operator|.
name|getCondition
argument_list|()
argument_list|,
name|joinFieldRefCountsList
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|newPostJoinFilters
init|=
name|combinePostJoinFilters
argument_list|(
name|origJoin
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|origJoin
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RelNode
name|multiJoin
init|=
operator|new
name|MultiJoin
argument_list|(
name|origJoin
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newInputs
argument_list|,
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newJoinFilters
argument_list|,
literal|false
argument_list|)
argument_list|,
name|origJoin
operator|.
name|getRowType
argument_list|()
argument_list|,
name|origJoin
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|FULL
argument_list|,
name|Pair
operator|.
name|right
argument_list|(
name|joinSpecs
argument_list|)
argument_list|,
name|Pair
operator|.
name|left
argument_list|(
name|joinSpecs
argument_list|)
argument_list|,
name|projFieldsList
argument_list|,
name|newJoinFieldRefCountsMap
argument_list|,
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|newPostJoinFilters
argument_list|,
literal|true
argument_list|)
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
comment|/**    * Combines the inputs into a LogicalJoin into an array of inputs.    *    * @param join                   original join    * @param left                   left input into join    * @param right                  right input into join    * @param projFieldsList         returns a list of the new combined projection    *                               fields    * @param joinFieldRefCountsList returns a list of the new combined join    *                               field reference counts    * @return combined left and right inputs in an array    */
specifier|private
name|List
argument_list|<
name|RelNode
argument_list|>
name|combineInputs
parameter_list|(
name|Join
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
name|ImmutableBitSet
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
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|// leave the null generating sides of an outer join intact; don't
comment|// pull up those children inputs into the array we're constructing
if|if
condition|(
name|canCombine
argument_list|(
name|left
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|MultiJoin
name|leftMultiJoin
init|=
operator|(
name|MultiJoin
operator|)
name|left
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
operator|.
name|toIntArray
argument_list|()
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
name|canCombine
argument_list|(
name|right
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|MultiJoin
name|rightMultiJoin
init|=
operator|(
name|MultiJoin
operator|)
name|right
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
name|right
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
name|rightMultiJoin
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
name|rightMultiJoin
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
name|rightMultiJoin
operator|.
name|getJoinFieldRefCountsMap
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toIntArray
argument_list|()
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
name|Join
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
name|MultiJoin
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
name|MultiJoin
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
name|MultiJoin
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
name|MultiJoin
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
comment|/**    * Copies outer join data from a source MultiJoin to a new set of arrays.    * Also adjusts the conditions to reflect the new position of an input if    * that input ends up being shifted to the right.    *    * @param multiJoin     the source MultiJoin    * @param destJoinSpecs    the list where the join types and conditions will    *                         be copied    * @param adjustmentAmount if&gt; 0, the amount the RexInputRefs in the join    *                         conditions need to be adjusted by    * @param srcFields        the source fields that the original join conditions    *                         are referencing    * @param destFields       the destination fields that the new join conditions    */
specifier|private
name|void
name|copyOuterJoinInfo
parameter_list|(
name|MultiJoin
name|multiJoin
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
name|multiJoin
operator|.
name|getJoinTypes
argument_list|()
argument_list|,
name|multiJoin
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
assert|assert
name|srcFields
operator|!=
literal|null
assert|;
assert|assert
name|destFields
operator|!=
literal|null
assert|;
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
name|multiJoin
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
comment|/**    * Combines the join filters from the left and right inputs (if they are    * MultiJoinRels) with the join filter in the joinrel into a single AND'd    * join filter, unless the inputs correspond to null generating inputs in an    * outer join    *    * @param joinRel join rel    * @param left    left child of the join    * @param right   right child of the join    * @return combined join filters AND-ed together    */
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|combineJoinFilters
parameter_list|(
name|Join
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
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
comment|// AND the join condition if this isn't a left or right outer join;
comment|// in those cases, the outer join condition is already tracked
comment|// separately
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|filters
operator|.
name|add
argument_list|(
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|)
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
name|filters
operator|.
name|add
argument_list|(
operator|(
operator|(
name|MultiJoin
operator|)
name|left
operator|)
operator|.
name|getJoinFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Need to adjust the RexInputs of the right child, since
comment|// those need to shift over to the right
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
name|MultiJoin
name|multiJoin
init|=
operator|(
name|MultiJoin
operator|)
name|right
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
return|return
name|filters
return|;
block|}
comment|/**    * Returns whether an input can be merged into a given relational expression    * without changing semantics.    *    * @param input          input into a join    * @param nullGenerating true if the input is null generating    * @return true if the input can be combined into a parent MultiJoin    */
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
name|input
operator|instanceof
name|MultiJoin
operator|&&
operator|!
operator|(
operator|(
name|MultiJoin
operator|)
name|input
operator|)
operator|.
name|isFullOuterJoin
argument_list|()
operator|&&
operator|!
operator|(
operator|(
name|MultiJoin
operator|)
name|input
operator|)
operator|.
name|containsOuter
argument_list|()
operator|&&
operator|!
name|nullGenerating
return|;
block|}
comment|/**    * Shifts a filter originating from the right child of the LogicalJoin to the    * right, to reflect the filter now being applied on the resulting    * MultiJoin.    *    * @param joinRel     the original LogicalJoin    * @param left        the left child of the LogicalJoin    * @param right       the right child of the LogicalJoin    * @param rightFilter the filter originating from the right child    * @return the adjusted right filter    */
specifier|private
name|RexNode
name|shiftRightFilter
parameter_list|(
name|Join
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|MultiJoin
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
comment|/**    * Adds on to the existing join condition reference counts the references    * from the new join condition.    *    * @param multiJoinInputs          inputs into the new MultiJoin    * @param nTotalFields             total number of fields in the MultiJoin    * @param joinCondition            the new join condition    * @param origJoinFieldRefCounts   existing join condition reference counts    *    * @return Map containing the new join condition    */
specifier|private
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
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
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|refCountsMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
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
name|refCountsMap
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
comment|// add on to the counts for each input into the MultiJoin the
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
name|currInput
operator|<
name|nInputs
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
name|refCountsMap
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
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|entry
range|:
name|refCountsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Combines the post-join filters from the left and right inputs (if they    * are MultiJoinRels) into a single AND'd filter.    *    * @param joinRel the original LogicalJoin    * @param left    left child of the LogicalJoin    * @param right   right child of the LogicalJoin    * @return combined post-join filters AND'd together    */
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|combinePostJoinFilters
parameter_list|(
name|Join
name|joinRel
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|right
operator|instanceof
name|MultiJoin
condition|)
block|{
specifier|final
name|MultiJoin
name|multiRight
init|=
operator|(
name|MultiJoin
operator|)
name|right
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|shiftRightFilter
argument_list|(
name|joinRel
argument_list|,
name|left
argument_list|,
name|multiRight
argument_list|,
name|multiRight
operator|.
name|getPostJoinFilter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|instanceof
name|MultiJoin
condition|)
block|{
name|filters
operator|.
name|add
argument_list|(
operator|(
operator|(
name|MultiJoin
operator|)
name|left
operator|)
operator|.
name|getPostJoinFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|filters
return|;
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
comment|// End JoinToMultiJoinRule.java
end_comment

end_unit

