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
name|math
operator|.
name|BigDecimal
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Rule to reduce aggregates to simpler forms. Currently only AVG(x) to  * SUM(x)/COUNT(x), but eventually will handle others such as STDDEV.  */
end_comment

begin_class
specifier|public
class|class
name|ReduceAggregatesRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * The singleton.      */
specifier|public
specifier|static
specifier|final
name|ReduceAggregatesRule
name|instance
init|=
operator|new
name|ReduceAggregatesRule
argument_list|(
name|any
argument_list|(
name|AggregateRel
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|ReduceAggregatesRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|ruleCall
parameter_list|)
block|{
name|AggregateRelBase
name|oldAggRel
init|=
operator|(
name|AggregateRelBase
operator|)
name|ruleCall
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|containsAvgStddevVarCall
argument_list|(
name|oldAggRel
operator|.
name|getAggCallList
argument_list|()
argument_list|)
condition|)
block|{
name|reduceAggs
argument_list|(
name|ruleCall
argument_list|,
name|oldAggRel
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns whether any of the aggregates are calls to AVG, STDDEV_*, VAR_*.      *      * @param aggCallList List of aggregate calls      */
specifier|private
name|boolean
name|containsAvgStddevVarCall
parameter_list|(
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCallList
parameter_list|)
block|{
for|for
control|(
name|AggregateCall
name|call
range|:
name|aggCallList
control|)
block|{
if|if
condition|(
name|call
operator|.
name|getAggregation
argument_list|()
operator|instanceof
name|SqlAvgAggFunction
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Reduces all calls to AVG, STDDEV_POP, STDDEV_SAMP, VAR_POP, VAR_SAMP in      * the aggregates list to.      *      *<p>It handles newly generated common subexpressions since this was done      * at the sql2rel stage.      */
specifier|private
name|void
name|reduceAggs
parameter_list|(
name|RelOptRuleCall
name|ruleCall
parameter_list|,
name|AggregateRelBase
name|oldAggRel
parameter_list|)
block|{
name|RexBuilder
name|rexBuilder
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|oldCalls
init|=
name|oldAggRel
operator|.
name|getAggCallList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nGroups
init|=
name|oldAggRel
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|AggregateCall
argument_list|,
name|RexNode
argument_list|>
name|aggCallMapping
init|=
operator|new
name|HashMap
argument_list|<
name|AggregateCall
argument_list|,
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|projList
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
comment|// pass through group key
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nGroups
condition|;
operator|++
name|i
control|)
block|{
name|projList
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|getFieldType
argument_list|(
name|oldAggRel
argument_list|,
name|i
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// List of input expressions. If a particular aggregate needs more, it
comment|// will add an expression to the end, and we will create an extra
comment|// project.
name|RelNode
name|input
init|=
name|oldAggRel
operator|.
name|getChild
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|inputExprs
init|=
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|inputExprs
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|inputExprs
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create new agg function calls and rest of project list together
for|for
control|(
name|AggregateCall
name|oldCall
range|:
name|oldCalls
control|)
block|{
name|projList
operator|.
name|add
argument_list|(
name|reduceAgg
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|,
name|inputExprs
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|extraArgCount
init|=
name|inputExprs
operator|.
name|size
argument_list|()
operator|-
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|extraArgCount
operator|>
literal|0
condition|)
block|{
name|input
operator|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|input
argument_list|,
name|inputExprs
argument_list|,
name|CompositeList
operator|.
name|of
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|nCopies
argument_list|(
name|extraArgCount
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|AggregateRelBase
name|newAggRel
init|=
name|newAggregateRel
argument_list|(
name|oldAggRel
argument_list|,
name|input
argument_list|,
name|newCalls
argument_list|)
decl_stmt|;
name|RelNode
name|projectRel
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newAggRel
argument_list|,
name|projList
argument_list|,
name|oldAggRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
decl_stmt|;
name|ruleCall
operator|.
name|transformTo
argument_list|(
name|projectRel
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RexNode
name|reduceAgg
parameter_list|(
name|AggregateRelBase
name|oldAggRel
parameter_list|,
name|AggregateCall
name|oldCall
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
parameter_list|,
name|Map
argument_list|<
name|AggregateCall
argument_list|,
name|RexNode
argument_list|>
name|aggCallMapping
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|inputExprs
parameter_list|)
block|{
if|if
condition|(
name|oldCall
operator|.
name|getAggregation
argument_list|()
operator|instanceof
name|SqlAvgAggFunction
condition|)
block|{
specifier|final
name|SqlAvgAggFunction
operator|.
name|Subtype
name|subtype
init|=
operator|(
operator|(
name|SqlAvgAggFunction
operator|)
name|oldCall
operator|.
name|getAggregation
argument_list|()
operator|)
operator|.
name|getSubtype
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|subtype
condition|)
block|{
case|case
name|AVG
case|:
comment|// replace original AVG(x) with SUM(x) / COUNT(x)
return|return
name|reduceAvg
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
return|;
case|case
name|STDDEV_POP
case|:
comment|// replace original STDDEV_POP(x) with
comment|//   SQRT(
comment|//     (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x))
comment|//     / COUNT(x))
return|return
name|reduceStddev
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|,
name|inputExprs
argument_list|)
return|;
case|case
name|STDDEV_SAMP
case|:
comment|// replace original STDDEV_POP(x) with
comment|//   SQRT(
comment|//     (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x))
comment|//     / CASE COUNT(x) WHEN 1 THEN NULL ELSE COUNT(x) - 1 END)
return|return
name|reduceStddev
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|,
name|inputExprs
argument_list|)
return|;
case|case
name|VAR_POP
case|:
comment|// replace original VAR_POP(x) with
comment|//     (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x))
comment|//     / COUNT(x)
return|return
name|reduceStddev
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|,
name|inputExprs
argument_list|)
return|;
case|case
name|VAR_SAMP
case|:
comment|// replace original VAR_POP(x) with
comment|//     (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x))
comment|//     / CASE COUNT(x) WHEN 1 THEN NULL ELSE COUNT(x) - 1 END
return|return
name|reduceStddev
argument_list|(
name|oldAggRel
argument_list|,
name|oldCall
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|,
name|inputExprs
argument_list|)
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|subtype
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// anything else:  preserve original call
name|RexBuilder
name|rexBuilder
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nGroups
init|=
name|oldAggRel
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|oldCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
return|;
block|}
block|}
specifier|private
name|RexNode
name|reduceAvg
parameter_list|(
name|AggregateRelBase
name|oldAggRel
parameter_list|,
name|AggregateCall
name|oldCall
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
parameter_list|,
name|Map
argument_list|<
name|AggregateCall
argument_list|,
name|RexNode
argument_list|>
name|aggCallMapping
parameter_list|)
block|{
specifier|final
name|int
name|nGroups
init|=
name|oldAggRel
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|int
name|iAvgInput
init|=
name|oldCall
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelDataType
name|avgInputType
init|=
name|getFieldType
argument_list|(
name|oldAggRel
operator|.
name|getChild
argument_list|()
argument_list|,
name|iAvgInput
argument_list|)
decl_stmt|;
name|RelDataType
name|sumType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|avgInputType
argument_list|,
name|avgInputType
operator|.
name|isNullable
argument_list|()
argument_list|)
decl_stmt|;
name|SqlAggFunction
name|sumAgg
init|=
operator|new
name|SqlSumAggFunction
argument_list|(
name|sumType
argument_list|)
decl_stmt|;
name|AggregateCall
name|sumCall
init|=
operator|new
name|AggregateCall
argument_list|(
name|sumAgg
argument_list|,
name|oldCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|oldCall
operator|.
name|getArgList
argument_list|()
argument_list|,
name|sumType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SqlAggFunction
name|countAgg
init|=
name|SqlStdOperatorTable
operator|.
name|countOperator
decl_stmt|;
name|RelDataType
name|countType
init|=
name|countAgg
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|AggregateCall
name|countCall
init|=
operator|new
name|AggregateCall
argument_list|(
name|countAgg
argument_list|,
name|oldCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|oldCall
operator|.
name|getArgList
argument_list|()
argument_list|,
name|countType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// NOTE:  these references are with respect to the output
comment|// of newAggRel
name|RexNode
name|numeratorRef
init|=
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|sumCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
decl_stmt|;
name|RexNode
name|denominatorRef
init|=
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|countCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|divideRef
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|divideOperator
argument_list|,
name|numeratorRef
argument_list|,
name|denominatorRef
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|oldCall
operator|.
name|getType
argument_list|()
argument_list|,
name|divideRef
argument_list|)
return|;
block|}
specifier|private
name|RexNode
name|reduceStddev
parameter_list|(
name|AggregateRelBase
name|oldAggRel
parameter_list|,
name|AggregateCall
name|oldCall
parameter_list|,
name|boolean
name|biased
parameter_list|,
name|boolean
name|sqrt
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
parameter_list|,
name|Map
argument_list|<
name|AggregateCall
argument_list|,
name|RexNode
argument_list|>
name|aggCallMapping
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|inputExprs
parameter_list|)
block|{
comment|// stddev_pop(x) ==>
comment|//   power(
comment|//     (sum(x * x) - sum(x) * sum(x) / count(x))
comment|//     / count(x),
comment|//     .5)
comment|//
comment|// stddev_samp(x) ==>
comment|//   power(
comment|//     (sum(x * x) - sum(x) * sum(x) / count(x))
comment|//     / nullif(count(x) - 1, 0),
comment|//     .5)
specifier|final
name|int
name|nGroups
init|=
name|oldAggRel
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|oldAggRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
assert|assert
name|oldCall
operator|.
name|getArgList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|:
name|oldCall
operator|.
name|getArgList
argument_list|()
assert|;
specifier|final
name|int
name|argOrdinal
init|=
name|oldCall
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|argType
init|=
name|getFieldType
argument_list|(
name|oldAggRel
operator|.
name|getChild
argument_list|()
argument_list|,
name|argOrdinal
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|argRef
init|=
name|inputExprs
operator|.
name|get
argument_list|(
name|argOrdinal
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|argSquared
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|multiplyOperator
argument_list|,
name|argRef
argument_list|,
name|argRef
argument_list|)
decl_stmt|;
specifier|final
name|int
name|argSquaredOrdinal
init|=
name|lookupOrAdd
argument_list|(
name|inputExprs
argument_list|,
name|argSquared
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sumType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|argType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AggregateCall
name|sumArgSquaredAggCall
init|=
operator|new
name|AggregateCall
argument_list|(
operator|new
name|SqlSumAggFunction
argument_list|(
name|sumType
argument_list|)
argument_list|,
name|oldCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|argSquaredOrdinal
argument_list|)
argument_list|,
name|sumType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|sumArgSquared
init|=
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|sumArgSquaredAggCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
decl_stmt|;
specifier|final
name|AggregateCall
name|sumArgAggCall
init|=
operator|new
name|AggregateCall
argument_list|(
operator|new
name|SqlSumAggFunction
argument_list|(
name|sumType
argument_list|)
argument_list|,
name|oldCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|argOrdinal
argument_list|)
argument_list|,
name|sumType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|sumArg
init|=
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|sumArgAggCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|sumSquaredArg
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|multiplyOperator
argument_list|,
name|sumArg
argument_list|,
name|sumArg
argument_list|)
decl_stmt|;
specifier|final
name|SqlAggFunction
name|countAgg
init|=
name|SqlStdOperatorTable
operator|.
name|countOperator
decl_stmt|;
specifier|final
name|RelDataType
name|countType
init|=
name|countAgg
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|AggregateCall
name|countArgAggCall
init|=
operator|new
name|AggregateCall
argument_list|(
name|countAgg
argument_list|,
name|oldCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|oldCall
operator|.
name|getArgList
argument_list|()
argument_list|,
name|countType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|countArg
init|=
name|rexBuilder
operator|.
name|addAggCall
argument_list|(
name|countArgAggCall
argument_list|,
name|nGroups
argument_list|,
name|newCalls
argument_list|,
name|aggCallMapping
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|avgSumSquaredArg
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|divideOperator
argument_list|,
name|sumSquaredArg
argument_list|,
name|countArg
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|diff
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|minusOperator
argument_list|,
name|sumArgSquared
argument_list|,
name|avgSumSquaredArg
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|denominator
decl_stmt|;
if|if
condition|(
name|biased
condition|)
block|{
name|denominator
operator|=
name|countArg
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|RexLiteral
name|one
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|nul
init|=
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|countArg
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|countMinusOne
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|minusOperator
argument_list|,
name|countArg
argument_list|,
name|one
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|countEqOne
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|equalsOperator
argument_list|,
name|countArg
argument_list|,
name|one
argument_list|)
decl_stmt|;
name|denominator
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|caseOperator
argument_list|,
name|countEqOne
argument_list|,
name|nul
argument_list|,
name|countMinusOne
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RexNode
name|div
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|divideOperator
argument_list|,
name|diff
argument_list|,
name|denominator
argument_list|)
decl_stmt|;
name|RexNode
name|result
init|=
name|div
decl_stmt|;
if|if
condition|(
name|sqrt
condition|)
block|{
specifier|final
name|RexNode
name|half
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|"0.5"
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|powerFunc
argument_list|,
name|div
argument_list|,
name|half
argument_list|)
expr_stmt|;
block|}
return|return
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|oldCall
operator|.
name|getType
argument_list|()
argument_list|,
name|result
argument_list|)
return|;
block|}
comment|/**      * Finds the ordinal of an element in a list, or adds it.      *      * @param list List      * @param element Element to lookup or add      * @param<T> Element type      * @return Ordinal of element in list      */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
name|lookupOrAdd
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|T
name|element
parameter_list|)
block|{
name|int
name|ordinal
init|=
name|list
operator|.
name|indexOf
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinal
operator|==
operator|-
literal|1
condition|)
block|{
name|ordinal
operator|=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
return|return
name|ordinal
return|;
block|}
comment|/**      * Do a shallow clone of oldAggRel and update aggCalls. Could be refactored      * into AggregateRelBase and subclasses - but it's only needed for some      * subclasses.      *      * @param oldAggRel AggregateRel to clone.      * @param inputRel Input relational expression      * @param newCalls New list of AggregateCalls      *  @return shallow clone with new list of AggregateCalls.      */
specifier|protected
name|AggregateRelBase
name|newAggregateRel
parameter_list|(
name|AggregateRelBase
name|oldAggRel
parameter_list|,
name|RelNode
name|inputRel
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newCalls
parameter_list|)
block|{
return|return
operator|new
name|AggregateRel
argument_list|(
name|oldAggRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|inputRel
argument_list|,
name|oldAggRel
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|newCalls
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|getFieldType
parameter_list|(
name|RelNode
name|relNode
parameter_list|,
name|int
name|i
parameter_list|)
block|{
specifier|final
name|RelDataTypeField
name|inputField
init|=
name|relNode
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
name|inputField
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End ReduceAggregatesRule.java
end_comment

end_unit

