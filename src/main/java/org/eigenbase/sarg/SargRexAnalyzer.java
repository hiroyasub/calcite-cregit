begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sarg
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
name|sql
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
name|sql
operator|.
name|fun
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SargRexAnalyzer attempts to translate a rex predicate into a {@link  * SargBinding}. It assumes that the predicate expression is already  * well-formed.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SargRexAnalyzer
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SargFactory
name|factory
decl_stmt|;
comment|/**      * If true, conjuntions on the same input reference are disallowed, as well      * as all disjunctions. Also, only a single range predicate is allowed.      */
specifier|private
specifier|final
name|boolean
name|simpleMode
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|SqlOperator
argument_list|,
name|CallConvertlet
argument_list|>
name|convertletMap
decl_stmt|;
specifier|private
name|boolean
name|failed
decl_stmt|;
specifier|private
name|RexInputRef
name|boundInputRef
decl_stmt|;
specifier|private
name|RexNode
name|coordinate
decl_stmt|;
specifier|private
name|boolean
name|variableSeen
decl_stmt|;
specifier|private
name|boolean
name|reverse
decl_stmt|;
specifier|private
name|List
argument_list|<
name|SargExpr
argument_list|>
name|exprStack
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexCFList
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RexNode
argument_list|>
name|nonSargFilterList
decl_stmt|;
specifier|private
name|List
argument_list|<
name|SargBinding
argument_list|>
name|sargBindingList
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|SargExpr
argument_list|,
name|RexNode
argument_list|>
name|sarg2RexMap
decl_stmt|;
comment|/**      * If>= 0, treat RexInputRefs whose index is within the range      * [lowerRexInputIdx, upperRexInputIdx) as coordinates in expressions      */
specifier|private
name|int
name|lowerRexInputIdx
decl_stmt|;
comment|/**      * If>= 0, treat RexInputRefs whose index is within the range      * [lowerRexInputIdx, upperRexInputIdx) as coordinates in expressions      */
specifier|private
name|int
name|upperRexInputIdx
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|SargRexAnalyzer
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|boolean
name|simpleMode
parameter_list|)
block|{
name|this
argument_list|(
name|factory
argument_list|,
name|simpleMode
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|SargRexAnalyzer
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|boolean
name|simpleMode
parameter_list|,
name|int
name|lowerRexInputRef
parameter_list|,
name|int
name|upperRexInputRef
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|simpleMode
operator|=
name|simpleMode
expr_stmt|;
name|this
operator|.
name|lowerRexInputIdx
operator|=
name|lowerRexInputRef
expr_stmt|;
name|this
operator|.
name|upperRexInputIdx
operator|=
name|upperRexInputRef
expr_stmt|;
assert|assert
operator|(
operator|(
operator|(
name|lowerRexInputIdx
operator|<
literal|0
operator|)
operator|&&
operator|(
name|upperRexInputIdx
operator|<
literal|0
operator|)
operator|)
operator|||
operator|(
operator|(
name|lowerRexInputIdx
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|upperRexInputIdx
operator|>=
literal|0
operator|)
operator|)
operator|)
assert|;
name|convertletMap
operator|=
operator|new
name|HashMap
argument_list|<
name|SqlOperator
argument_list|,
name|CallConvertlet
argument_list|>
argument_list|()
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|equalsOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
literal|null
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|isNullOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
literal|null
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|isTrueOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
literal|null
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|isFalseOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
literal|null
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|isUnknownOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
literal|null
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|lessThanOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
name|SargBoundType
operator|.
name|UPPER
argument_list|,
name|SargStrictness
operator|.
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|lessThanOrEqualOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
name|SargBoundType
operator|.
name|UPPER
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|greaterThanOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
name|SargBoundType
operator|.
name|LOWER
argument_list|,
name|SargStrictness
operator|.
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|greaterThanOrEqualOperator
argument_list|,
operator|new
name|ComparisonConvertlet
argument_list|(
name|SargBoundType
operator|.
name|LOWER
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
argument_list|)
expr_stmt|;
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
operator|new
name|BooleanConvertlet
argument_list|(
name|SargSetOperator
operator|.
name|INTERSECTION
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|simpleMode
condition|)
block|{
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|orOperator
argument_list|,
operator|new
name|BooleanConvertlet
argument_list|(
name|SargSetOperator
operator|.
name|UNION
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|registerConvertlet
argument_list|(
name|SqlStdOperatorTable
operator|.
name|notOperator
argument_list|,
operator|new
name|BooleanConvertlet
argument_list|(
name|SargSetOperator
operator|.
name|COMPLEMENT
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: likeOperator (via complement notLikeOperator)
comment|// TODO:  non-literal constants (e.g. CURRENT_USER)
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
name|void
name|registerConvertlet
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|CallConvertlet
name|convertlet
parameter_list|)
block|{
name|convertletMap
operator|.
name|put
argument_list|(
name|op
argument_list|,
name|convertlet
argument_list|)
expr_stmt|;
block|}
comment|// REVIEW jvs 18-Mar-2006:  rename these two to decomposeConjunction
comment|// and recomposeConjunction so "one not skilled in the art" will
comment|// still have a chance of figuring out what they're for just
comment|// from the names.  Also, some explanation of the state maintained
comment|// by SargRexAnalyzer across the various calls is in order.
comment|// (It used to be one-shot, but no longer.)
comment|/**      * Reconstructs a rex predicate from a list of SargExprs which will be      * AND'ed together.      */
specifier|private
name|void
name|recomposeConjunction
parameter_list|()
block|{
name|SargBinding
name|currBinding
decl_stmt|,
name|nextBinding
decl_stmt|;
name|RexInputRef
name|currRef
decl_stmt|,
name|nextRef
decl_stmt|;
name|SargExpr
name|currSargExpr
decl_stmt|,
name|nextSargExpr
decl_stmt|;
name|RexNode
name|currAndNode
decl_stmt|;
name|boolean
name|recomp
decl_stmt|;
name|ListIterator
name|iter
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
name|sargBindingList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|currBinding
operator|=
name|sargBindingList
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|currRef
operator|=
name|currBinding
operator|.
name|getInputRef
argument_list|()
expr_stmt|;
name|currSargExpr
operator|=
name|currBinding
operator|.
name|getExpr
argument_list|()
expr_stmt|;
name|currAndNode
operator|=
name|sarg2RexMap
operator|.
name|get
argument_list|(
name|currSargExpr
argument_list|)
expr_stmt|;
comment|// don't need this anymore
comment|// will be have new mapping put back if currSargExpr remain
comment|// unchanged.
name|sarg2RexMap
operator|.
name|remove
argument_list|(
name|currSargExpr
argument_list|)
expr_stmt|;
name|recomp
operator|=
literal|false
expr_stmt|;
comment|// search the rest of the list to find SargExpr on the same col.
name|iter
operator|=
name|sargBindingList
operator|.
name|listIterator
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|nextBinding
operator|=
operator|(
name|SargBinding
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|nextRef
operator|=
name|nextBinding
operator|.
name|getInputRef
argument_list|()
expr_stmt|;
name|nextSargExpr
operator|=
name|nextBinding
operator|.
name|getExpr
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextRef
operator|.
name|getIndex
argument_list|()
operator|==
name|currRef
operator|.
name|getIndex
argument_list|()
condition|)
block|{
comment|// build new SargExpr
name|SargSetExpr
name|expr
init|=
name|factory
operator|.
name|newSetExpr
argument_list|(
name|currSargExpr
operator|.
name|getDataType
argument_list|()
argument_list|,
name|SargSetOperator
operator|.
name|INTERSECTION
argument_list|)
decl_stmt|;
name|expr
operator|.
name|addChild
argument_list|(
name|currSargExpr
argument_list|)
expr_stmt|;
name|expr
operator|.
name|addChild
argument_list|(
name|nextSargExpr
argument_list|)
expr_stmt|;
comment|// build new RexNode
name|currAndNode
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|currAndNode
argument_list|,
name|sarg2RexMap
operator|.
name|get
argument_list|(
name|nextSargExpr
argument_list|)
argument_list|)
expr_stmt|;
name|currSargExpr
operator|=
name|expr
expr_stmt|;
name|sarg2RexMap
operator|.
name|remove
argument_list|(
name|nextSargExpr
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|recomp
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|recomp
condition|)
block|{
assert|assert
operator|(
operator|!
name|simpleMode
operator|)
assert|;
if|if
condition|(
operator|!
name|testDynamicParamSupport
argument_list|(
name|currSargExpr
argument_list|)
condition|)
block|{
comment|// Oops, we can't actually support the conjunction we
comment|// recomposed.  Toss it.  (We could do a better job by at
comment|// least using part of it, but the effort might be better
comment|// spent on implementing deferred expression evaluation.)
name|nonSargFilterList
operator|.
name|add
argument_list|(
name|currAndNode
argument_list|)
expr_stmt|;
name|sargBindingList
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|recomp
condition|)
block|{
name|SargBinding
name|newBinding
init|=
operator|new
name|SargBinding
argument_list|(
name|currSargExpr
argument_list|,
name|currRef
argument_list|)
decl_stmt|;
name|sargBindingList
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|sargBindingList
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|newBinding
argument_list|)
expr_stmt|;
block|}
name|sarg2RexMap
operator|.
name|put
argument_list|(
name|currSargExpr
argument_list|,
name|currAndNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Analyzes a rex predicate.      *      * @param rexPredicate predicate to be analyzed      *      * @return a list of SargBindings contained in the input rex predicate      */
specifier|public
name|List
argument_list|<
name|SargBinding
argument_list|>
name|analyzeAll
parameter_list|(
name|RexNode
name|rexPredicate
parameter_list|)
block|{
name|rexCFList
operator|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
expr_stmt|;
name|sargBindingList
operator|=
operator|new
name|ArrayList
argument_list|<
name|SargBinding
argument_list|>
argument_list|()
expr_stmt|;
name|sarg2RexMap
operator|=
operator|new
name|HashMap
argument_list|<
name|SargExpr
argument_list|,
name|RexNode
argument_list|>
argument_list|()
expr_stmt|;
name|nonSargFilterList
operator|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
expr_stmt|;
name|SargBinding
name|sargBinding
decl_stmt|;
comment|// Flatten out the RexNode tree into a list of terms that
comment|// are AND'ed together
name|RelOptUtil
operator|.
name|decomposeConjunction
argument_list|(
name|rexPredicate
argument_list|,
name|rexCFList
argument_list|)
expr_stmt|;
comment|// In simple mode, each input ref can only be referenced once, so
comment|// keep a list of them.  We also only allow one non-point expression.
name|List
argument_list|<
name|Integer
argument_list|>
name|boundRefList
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|rangeFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RexNode
name|rexPred
range|:
name|rexCFList
control|)
block|{
name|sargBinding
operator|=
name|analyze
argument_list|(
name|rexPred
argument_list|)
expr_stmt|;
if|if
condition|(
name|sargBinding
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|simpleMode
condition|)
block|{
name|RexInputRef
name|inputRef
init|=
name|sargBinding
operator|.
name|getInputRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|boundRefList
operator|.
name|contains
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
condition|)
block|{
name|nonSargFilterList
operator|.
name|add
argument_list|(
name|rexPred
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|boundRefList
operator|.
name|add
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SargIntervalSequence
name|sargSeq
init|=
name|sargBinding
operator|.
name|getExpr
argument_list|()
operator|.
name|evaluate
argument_list|()
decl_stmt|;
if|if
condition|(
name|sargSeq
operator|.
name|isRange
argument_list|()
condition|)
block|{
if|if
condition|(
name|rangeFound
condition|)
block|{
name|nonSargFilterList
operator|.
name|add
argument_list|(
name|rexPred
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|rangeFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|sargBindingList
operator|.
name|add
argument_list|(
name|sargBinding
argument_list|)
expr_stmt|;
name|sarg2RexMap
operator|.
name|put
argument_list|(
name|sargBinding
operator|.
name|getExpr
argument_list|()
argument_list|,
name|rexPred
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nonSargFilterList
operator|.
name|add
argument_list|(
name|rexPred
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Reset the state variables used during analyze, just for sanity sake.
name|failed
operator|=
literal|false
expr_stmt|;
name|boundInputRef
operator|=
literal|null
expr_stmt|;
name|clearLeaf
argument_list|()
expr_stmt|;
comment|// Combine the AND terms back together.
name|recomposeConjunction
argument_list|()
expr_stmt|;
return|return
name|sargBindingList
return|;
block|}
comment|/**      * Tests whether we can support the usage of dynamic parameters in a given      * SargExpr.      *      * @param sargExpr expression to test      *      * @return true if supported      */
specifier|private
name|boolean
name|testDynamicParamSupport
parameter_list|(
name|SargExpr
name|sargExpr
parameter_list|)
block|{
comment|// NOTE jvs 18-Mar-2006: we don't currently support boolean operators
comment|// over predicates on dynamic parameters.  The reason is that we
comment|// evaluate sarg expressions at prepare time rather than at execution
comment|// time.  To support them, we would need a way to defer evaluation
comment|// until execution time (Broadbase used to do that).
name|Set
argument_list|<
name|RexDynamicParam
argument_list|>
name|dynamicParams
init|=
operator|new
name|HashSet
argument_list|<
name|RexDynamicParam
argument_list|>
argument_list|()
decl_stmt|;
name|sargExpr
operator|.
name|collectDynamicParams
argument_list|(
name|dynamicParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|dynamicParams
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no dynamic params, no problem
return|return
literal|true
return|;
block|}
if|if
condition|(
name|sargExpr
operator|instanceof
name|SargIntervalExpr
condition|)
block|{
comment|// We can support a single point or ray, which is the
comment|// most that would have been generated by the time this is
comment|// called.  Should probably assert that.
return|return
literal|true
return|;
block|}
comment|// Anything else is unsupported.
return|return
literal|false
return|;
block|}
comment|/**      * Reconstructs a rex predicate from the non-sargable filter predicates      * which are AND'ed together.      *      * @return the rex predicate reconstructed from the non-sargable predicates.      */
specifier|public
name|RexNode
name|getNonSargFilterRexNode
parameter_list|()
block|{
if|if
condition|(
name|nonSargFilterList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|RexNode
name|newAndNode
init|=
name|nonSargFilterList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nonSargFilterList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|newAndNode
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|newAndNode
argument_list|,
name|nonSargFilterList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newAndNode
return|;
block|}
comment|/**      * @deprecated use {@link #getNonSargFilterRexNode()}      */
specifier|public
name|RexNode
name|getPostFilterRexNode
parameter_list|()
block|{
return|return
name|getNonSargFilterRexNode
argument_list|()
return|;
block|}
comment|/**      * Reconstructs a rex predicate from a list of SargBindings which are AND'ed      * together.      *      * @param sargBindingList list of SargBindings to be converted.      *      * @return the rex predicate reconstructed from the list of SargBindings.      */
specifier|public
name|RexNode
name|getSargBindingListToRexNode
parameter_list|(
name|List
argument_list|<
name|SargBinding
argument_list|>
name|sargBindingList
parameter_list|)
block|{
if|if
condition|(
name|sargBindingList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|RexNode
name|newAndNode
init|=
name|sarg2RexMap
operator|.
name|get
argument_list|(
name|sargBindingList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getExpr
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|sargBindingList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|nextNode
init|=
name|sarg2RexMap
operator|.
name|get
argument_list|(
name|sargBindingList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getExpr
argument_list|()
argument_list|)
decl_stmt|;
name|newAndNode
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|newAndNode
argument_list|,
name|nextNode
argument_list|)
expr_stmt|;
block|}
return|return
name|newAndNode
return|;
block|}
comment|/**      * @deprecated use {@link #getSargBindingListToRexNode(List)}      */
specifier|public
name|RexNode
name|getResidualSargRexNode
parameter_list|(
name|List
argument_list|<
name|SargBinding
argument_list|>
name|residualSargList
parameter_list|)
block|{
return|return
name|getSargBindingListToRexNode
argument_list|(
name|residualSargList
argument_list|)
return|;
block|}
comment|/**      * Analyzes a rex predicate.      *      * @param rexPredicate predicate to be analyzed      *      * @return corresponding bound sarg expression, or null if analysis failed      */
specifier|public
name|SargBinding
name|analyze
parameter_list|(
name|RexNode
name|rexPredicate
parameter_list|)
block|{
name|NodeVisitor
name|visitor
init|=
operator|new
name|NodeVisitor
argument_list|()
decl_stmt|;
comment|// Initialize analysis state.
name|exprStack
operator|=
operator|new
name|ArrayList
argument_list|<
name|SargExpr
argument_list|>
argument_list|()
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
name|boundInputRef
operator|=
literal|null
expr_stmt|;
name|clearLeaf
argument_list|()
expr_stmt|;
comment|// Walk the predicate.
name|rexPredicate
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|boundInputRef
operator|==
literal|null
condition|)
block|{
comment|// No variable references at all, so not sargable.
name|failed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|exprStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|failed
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// well-formedness assumption
assert|assert
operator|(
name|exprStack
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
assert|;
name|SargExpr
name|expr
init|=
name|exprStack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|testDynamicParamSupport
argument_list|(
name|expr
argument_list|)
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SargBinding
argument_list|(
name|expr
argument_list|,
name|boundInputRef
argument_list|)
return|;
block|}
specifier|private
name|void
name|clearLeaf
parameter_list|()
block|{
name|coordinate
operator|=
literal|null
expr_stmt|;
name|variableSeen
operator|=
literal|false
expr_stmt|;
name|reverse
operator|=
literal|false
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|abstract
class|class
name|CallConvertlet
block|{
specifier|public
specifier|abstract
name|void
name|convert
parameter_list|(
name|RexCall
name|call
parameter_list|)
function_decl|;
block|}
specifier|private
class|class
name|ComparisonConvertlet
extends|extends
name|CallConvertlet
block|{
specifier|private
specifier|final
name|SargBoundType
name|boundType
decl_stmt|;
specifier|private
specifier|final
name|SargStrictness
name|strictness
decl_stmt|;
name|ComparisonConvertlet
parameter_list|(
name|SargBoundType
name|boundType
parameter_list|,
name|SargStrictness
name|strictness
parameter_list|)
block|{
name|this
operator|.
name|boundType
operator|=
name|boundType
expr_stmt|;
name|this
operator|.
name|strictness
operator|=
name|strictness
expr_stmt|;
block|}
comment|// implement CallConvertlet
specifier|public
name|void
name|convert
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
operator|!
name|variableSeen
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|SqlOperator
name|op
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|isNullOperator
operator|)
operator|||
operator|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|isUnknownOperator
operator|)
condition|)
block|{
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|constantNull
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|isTrueOperator
condition|)
block|{
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|isFalseOperator
condition|)
block|{
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|coordinate
operator|==
literal|null
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|failed
condition|)
block|{
return|return;
block|}
name|SargIntervalExpr
name|expr
init|=
name|factory
operator|.
name|newIntervalExpr
argument_list|(
name|boundInputRef
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|boundType
operator|==
literal|null
condition|)
block|{
name|expr
operator|.
name|setPoint
argument_list|(
name|coordinate
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SargBoundType
name|actualBound
init|=
name|boundType
decl_stmt|;
if|if
condition|(
name|reverse
condition|)
block|{
if|if
condition|(
name|actualBound
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
name|actualBound
operator|=
name|SargBoundType
operator|.
name|UPPER
expr_stmt|;
block|}
else|else
block|{
name|actualBound
operator|=
name|SargBoundType
operator|.
name|LOWER
expr_stmt|;
block|}
block|}
if|if
condition|(
name|actualBound
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
name|expr
operator|.
name|setLower
argument_list|(
name|coordinate
argument_list|,
name|strictness
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expr
operator|.
name|setUpper
argument_list|(
name|coordinate
argument_list|,
name|strictness
argument_list|)
expr_stmt|;
block|}
block|}
name|exprStack
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|clearLeaf
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|BooleanConvertlet
extends|extends
name|CallConvertlet
block|{
specifier|private
specifier|final
name|SargSetOperator
name|setOp
decl_stmt|;
name|BooleanConvertlet
parameter_list|(
name|SargSetOperator
name|setOp
parameter_list|)
block|{
name|this
operator|.
name|setOp
operator|=
name|setOp
expr_stmt|;
block|}
comment|// implement CallConvertlet
specifier|public
name|void
name|convert
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
comment|// REVIEW jvs 18-Mar-2006:  The test for variableSeen
comment|// here and elsewhere precludes predicates like where
comment|// (boolean_col AND (int_col> 10)).  Should probably
comment|// support bare boolean columns as predicates with
comment|// coordinate TRUE.
if|if
condition|(
name|variableSeen
operator|||
operator|(
name|coordinate
operator|!=
literal|null
operator|)
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|failed
condition|)
block|{
return|return;
block|}
name|int
name|nOperands
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|length
decl_stmt|;
assert|assert
operator|(
name|exprStack
operator|.
name|size
argument_list|()
operator|>=
name|nOperands
operator|)
assert|;
name|SargSetExpr
name|expr
init|=
name|factory
operator|.
name|newSetExpr
argument_list|(
name|boundInputRef
operator|.
name|getType
argument_list|()
argument_list|,
name|setOp
argument_list|)
decl_stmt|;
comment|// Pop the correct number of operands off the stack
comment|// and transfer them to the new set expression.
name|ListIterator
argument_list|<
name|SargExpr
argument_list|>
name|iter
init|=
name|exprStack
operator|.
name|listIterator
argument_list|(
name|exprStack
operator|.
name|size
argument_list|()
operator|-
name|nOperands
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|expr
operator|.
name|addChild
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|exprStack
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|NodeVisitor
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
name|NodeVisitor
parameter_list|()
block|{
comment|// go deep
name|super
argument_list|(
literal|true
argument_list|)
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
name|boolean
name|coordinate
init|=
operator|!
name|isRealRexInputRef
argument_list|(
name|inputRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|coordinate
condition|)
block|{
name|visitCoordinate
argument_list|(
name|inputRef
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|variableSeen
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|boundInputRef
operator|==
literal|null
condition|)
block|{
name|boundInputRef
operator|=
name|inputRef
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|inputRef
operator|.
name|getIndex
argument_list|()
operator|!=
name|boundInputRef
operator|.
name|getIndex
argument_list|()
condition|)
block|{
comment|// sargs can only be over a single variable
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|isRealRexInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
if|if
condition|(
operator|(
name|lowerRexInputIdx
operator|<
literal|0
operator|)
operator|&&
operator|(
name|upperRexInputIdx
operator|<
literal|0
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|idx
init|=
name|inputRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|idx
operator|>=
name|lowerRexInputIdx
operator|)
operator|&&
operator|(
name|idx
operator|<
name|upperRexInputIdx
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|Void
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
name|visitCoordinate
argument_list|(
name|literal
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Void
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Void
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|correlVariable
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Void
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
name|CallConvertlet
name|convertlet
init|=
name|convertletMap
operator|.
name|get
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|convertlet
operator|==
literal|null
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// visit operands first
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|convertlet
operator|.
name|convert
argument_list|(
name|call
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Void
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|)
block|{
if|if
condition|(
name|simpleMode
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|visitCoordinate
argument_list|(
name|dynamicParam
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|visitCoordinate
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|variableSeen
condition|)
block|{
comment|// We may be looking at an expression like (1< x).
name|reverse
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|coordinate
operator|!=
literal|null
condition|)
block|{
comment|// e.g. constants on both sides of comparison
name|failed
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|coordinate
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|Void
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
name|Void
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SargRexAnalyzer.java
end_comment

end_unit

