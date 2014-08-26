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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
operator|.
name|BitSets
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

begin_comment
comment|/**  * Rule to remove distinct aggregates from a {@link AggregateRel}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RemoveDistinctAggregateRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** The default instance of the rule; operates only on logical expressions. */
specifier|public
specifier|static
specifier|final
name|RemoveDistinctAggregateRule
name|INSTANCE
init|=
operator|new
name|RemoveDistinctAggregateRule
argument_list|(
name|AggregateRel
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|DEFAULT_JOIN_FACTORY
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RemoveDistinctAggregateRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|AggregateRel
argument_list|>
name|clazz
parameter_list|,
name|RelFactories
operator|.
name|JoinFactory
name|joinFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinFactory
operator|=
name|joinFactory
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
name|AggregateRelBase
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|aggregate
operator|.
name|containsDistinctCall
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Find all of the agg expressions. We use a LinkedHashSet to ensure
comment|// determinism.
name|int
name|nonDistinctCount
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|argListSets
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
operator|++
name|nonDistinctCount
expr_stmt|;
continue|continue;
block|}
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|argList
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|arg
range|:
name|aggCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|argList
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|argListSets
operator|.
name|add
argument_list|(
name|argList
argument_list|)
expr_stmt|;
block|}
name|Util
operator|.
name|permAssert
argument_list|(
name|argListSets
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|,
literal|"containsDistinctCall lied"
argument_list|)
expr_stmt|;
comment|// If all of the agg expressions are distinct and have the same
comment|// arguments then we can use a more efficient form.
if|if
condition|(
operator|(
name|nonDistinctCount
operator|==
literal|0
operator|)
operator|&&
operator|(
name|argListSets
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|)
block|{
name|RelNode
name|converted
init|=
name|convertMonopole
argument_list|(
name|aggregate
argument_list|,
name|argListSets
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Create a list of the expressions which will yield the final result.
comment|// Initially, the expressions point to the input field.
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|aggFields
init|=
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexInputRef
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|RexInputRef
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
specifier|final
name|BitSet
name|groupSet
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|groupSet
argument_list|)
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|RexInputRef
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|aggFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Aggregate the original relation, including any non-distinct aggs.
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCallList
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|groupCount
init|=
name|groupSet
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
operator|++
name|i
expr_stmt|;
if|if
condition|(
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
name|refs
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|refs
operator|.
name|add
argument_list|(
operator|new
name|RexInputRef
argument_list|(
name|groupCount
operator|+
name|newAggCallList
operator|.
name|size
argument_list|()
argument_list|,
name|aggFields
operator|.
name|get
argument_list|(
name|groupCount
operator|+
name|i
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newAggCallList
operator|.
name|add
argument_list|(
name|aggCall
argument_list|)
expr_stmt|;
block|}
comment|// In the case where there are no non-distinct aggs (regardless of
comment|// whether there are group bys), there's no need to generate the
comment|// extra aggregate and join.
name|RelNode
name|rel
decl_stmt|;
if|if
condition|(
name|newAggCallList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|rel
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|rel
operator|=
operator|new
name|AggregateRel
argument_list|(
name|aggregate
operator|.
name|getCluster
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getChild
argument_list|()
argument_list|,
name|groupSet
argument_list|,
name|newAggCallList
argument_list|)
expr_stmt|;
block|}
comment|// For each set of operands, find and rewrite all calls which have that
comment|// set of operands.
for|for
control|(
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
range|:
name|argListSets
control|)
block|{
name|rel
operator|=
name|doRewrite
argument_list|(
name|aggregate
argument_list|,
name|rel
argument_list|,
name|argList
argument_list|,
name|refs
argument_list|)
expr_stmt|;
block|}
comment|//noinspection unchecked
name|rel
operator|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|rel
argument_list|,
operator|(
name|List
operator|)
name|refs
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts an aggregate relational expression that contains just one    * distinct aggregate function (or perhaps several over the same arguments)    * and no non-distinct aggregate functions.    */
specifier|private
name|RelNode
name|convertMonopole
parameter_list|(
name|AggregateRelBase
name|aggregate
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|)
block|{
comment|// For example,
comment|//    SELECT deptno, COUNT(DISTINCT sal), SUM(DISTINCT sal)
comment|//    FROM emp
comment|//    GROUP BY deptno
comment|//
comment|// becomes
comment|//
comment|//    SELECT deptno, COUNT(distinct_sal), SUM(distinct_sal)
comment|//    FROM (
comment|//      SELECT DISTINCT deptno, sal AS distinct_sal
comment|//      FROM EMP GROUP BY deptno)
comment|//    GROUP BY deptno
comment|// Project the columns of the GROUP BY plus the arguments
comment|// to the agg function.
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|sourceOf
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AggregateRelBase
name|distinct
init|=
name|createSelectDistinct
argument_list|(
name|aggregate
argument_list|,
name|argList
argument_list|,
name|sourceOf
argument_list|)
decl_stmt|;
comment|// Create an aggregate on top, with the new aggregate list.
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|(
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
decl_stmt|;
name|rewriteAggCalls
argument_list|(
name|newAggCalls
argument_list|,
name|argList
argument_list|,
name|sourceOf
argument_list|)
expr_stmt|;
return|return
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|distinct
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|newAggCalls
argument_list|)
return|;
block|}
comment|/**    * Converts all distinct aggregate calls to a given set of arguments.    *    *<p>This method is called several times, one for each set of arguments.    * Each time it is called, it generates a JOIN to a new SELECT DISTINCT    * relational expression, and modifies the set of top-level calls.    *    * @param aggregate Original aggregate    * @param left      Child relational expression (either the original    *                  aggregate, the output from the previous call to this    *                  method, or null in the case where we're converting the    *                  first distinct aggregate in a query with no non-distinct    *                  aggregates)    * @param argList   Arguments to the distinct aggregate function    * @param refs      Array of expressions which will be the projected by the    *                  result of this rule. Those relating to this arg list will    *                  be modified    * @return Relational expression    */
specifier|private
name|RelNode
name|doRewrite
parameter_list|(
name|AggregateRelBase
name|aggregate
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|List
argument_list|<
name|RexInputRef
argument_list|>
name|refs
parameter_list|)
block|{
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|aggregate
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|leftFields
decl_stmt|;
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
name|leftFields
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|leftFields
operator|=
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
block|}
comment|// AggregateRel(
comment|//     child,
comment|//     {COUNT(DISTINCT 1), SUM(DISTINCT 1), SUM(2)})
comment|//
comment|// becomes
comment|//
comment|// AggregateRel(
comment|//     JoinRel(
comment|//         child,
comment|//         AggregateRel(child,< all columns> {}),
comment|//         INNER,
comment|//<f2 = f5>))
comment|//
comment|// E.g.
comment|//   SELECT deptno, SUM(DISTINCT sal), COUNT(DISTINCT gender), MAX(age)
comment|//   FROM Emps
comment|//   GROUP BY deptno
comment|//
comment|// becomes
comment|//
comment|//   SELECT e.deptno, adsal.sum_sal, adgender.count_gender, e.max_age
comment|//   FROM (
comment|//     SELECT deptno, MAX(age) as max_age
comment|//     FROM Emps GROUP BY deptno) AS e
comment|//   JOIN (
comment|//     SELECT deptno, COUNT(gender) AS count_gender FROM (
comment|//       SELECT DISTINCT deptno, gender FROM Emps) AS dgender
comment|//     GROUP BY deptno) AS adgender
comment|//     ON e.deptno = adgender.deptno
comment|//   JOIN (
comment|//     SELECT deptno, SUM(sal) AS sum_sal FROM (
comment|//       SELECT DISTINCT deptno, sal FROM Emps) AS dsal
comment|//     GROUP BY deptno) AS adsal
comment|//   ON e.deptno = adsal.deptno
comment|//   GROUP BY e.deptno
comment|//
comment|// Note that if a query contains no non-distinct aggregates, then the
comment|// very first join/group by is omitted.  In the example above, if
comment|// MAX(age) is removed, then the sub-select of "e" is not needed, and
comment|// instead the two other group by's are joined to one another.
comment|// Project the columns of the GROUP BY plus the arguments
comment|// to the agg function.
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|sourceOf
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AggregateRelBase
name|distinct
init|=
name|createSelectDistinct
argument_list|(
name|aggregate
argument_list|,
name|argList
argument_list|,
name|sourceOf
argument_list|)
decl_stmt|;
comment|// Now compute the aggregate functions on top of the distinct dataset.
comment|// Each distinct agg becomes a non-distinct call to the corresponding
comment|// field from the right; for example,
comment|//   "COUNT(DISTINCT e.sal)"
comment|// becomes
comment|//   "COUNT(distinct_e.sal)".
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCallList
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
init|=
name|aggregate
operator|.
name|getAggCallList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|groupCount
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|groupCount
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
operator|++
name|i
expr_stmt|;
comment|// Ignore agg calls which are not distinct or have the wrong set
comment|// arguments. If we're rewriting aggs whose args are {sal}, we will
comment|// rewrite COUNT(DISTINCT sal) and SUM(DISTINCT sal) but ignore
comment|// COUNT(DISTINCT gender) or SUM(sal).
if|if
condition|(
operator|!
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|equals
argument_list|(
name|argList
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Re-map arguments.
specifier|final
name|int
name|argCount
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|argCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|argCount
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Integer
name|arg
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|newArgs
operator|.
name|add
argument_list|(
name|sourceOf
operator|.
name|get
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AggregateCall
name|newAggCall
init|=
operator|new
name|AggregateCall
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
literal|false
argument_list|,
name|newArgs
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|refs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
assert|;
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
name|refs
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|RexInputRef
argument_list|(
name|groupCount
operator|+
name|aggCallList
operator|.
name|size
argument_list|()
argument_list|,
name|newAggCall
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refs
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|RexInputRef
argument_list|(
name|leftFields
operator|.
name|size
argument_list|()
operator|+
name|groupCount
operator|+
name|aggCallList
operator|.
name|size
argument_list|()
argument_list|,
name|newAggCall
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aggCallList
operator|.
name|add
argument_list|(
name|newAggCall
argument_list|)
expr_stmt|;
block|}
name|AggregateRelBase
name|distinctAgg
init|=
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|distinct
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggCallList
argument_list|)
decl_stmt|;
comment|// If there's no left child yet, no need to create the join
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
return|return
name|distinctAgg
return|;
block|}
comment|// Create the join condition. It is of the form
comment|//  'left.f0 = right.f0 and left.f1 = right.f1 and ...'
comment|// where {f0, f1, ...} are the GROUP BY fields.
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|distinctFields
init|=
name|distinctAgg
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|RexNode
name|condition
init|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|groupCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|leftOrdinal
init|=
name|i
decl_stmt|;
specifier|final
name|int
name|rightOrdinal
init|=
name|sourceOf
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// null values form its own group
comment|// use "is not distinct from" so that the join condition
comment|// allows null values to match.
name|RexNode
name|equi
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|RexInputRef
operator|.
name|of
argument_list|(
name|leftOrdinal
argument_list|,
name|leftFields
argument_list|)
argument_list|,
operator|new
name|RexInputRef
argument_list|(
name|leftFields
operator|.
name|size
argument_list|()
operator|+
name|rightOrdinal
argument_list|,
name|distinctFields
operator|.
name|get
argument_list|(
name|rightOrdinal
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|condition
operator|=
name|equi
expr_stmt|;
block|}
else|else
block|{
name|condition
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|condition
argument_list|,
name|equi
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Join in the new 'select distinct' relation.
return|return
name|joinFactory
operator|.
name|createJoin
argument_list|(
name|left
argument_list|,
name|distinctAgg
argument_list|,
name|condition
argument_list|,
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|rewriteAggCalls
parameter_list|(
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|sourceOf
parameter_list|)
block|{
comment|// Rewrite the agg calls. Each distinct agg becomes a non-distinct call
comment|// to the corresponding field from the right; for example,
comment|// "COUNT(DISTINCT e.sal)" becomes   "COUNT(distinct_e.sal)".
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|newAggCalls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|AggregateCall
name|aggCall
init|=
name|newAggCalls
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Ignore agg calls which are not distinct or have the wrong set
comment|// arguments. If we're rewriting aggs whose args are {sal}, we will
comment|// rewrite COUNT(DISTINCT sal) and SUM(DISTINCT sal) but ignore
comment|// COUNT(DISTINCT gender) or SUM(sal).
if|if
condition|(
operator|!
name|aggCall
operator|.
name|isDistinct
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|equals
argument_list|(
name|argList
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Re-map arguments.
specifier|final
name|int
name|argCount
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|newArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|argCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|argCount
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Integer
name|arg
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|newArgs
operator|.
name|add
argument_list|(
name|sourceOf
operator|.
name|get
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AggregateCall
name|newAggCall
init|=
operator|new
name|AggregateCall
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
literal|false
argument_list|,
name|newArgs
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newAggCalls
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|newAggCall
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Given an {@link AggregateRel} and the ordinals of the arguments to a    * particular call to an aggregate function, creates a 'select distinct'    * relational expression which projects the group columns and those    * arguments but nothing else.    *    *<p>For example, given    *    *<blockquote>    *<pre>select f0, count(distinct f1), count(distinct f2)    * from t group by f0</pre>    *</blockquote>    *    * and the arglist    *    *<blockquote>{2}</blockquote>    *    * returns    *    *<blockquote>    *<pre>select distinct f0, f2 from t</pre>    *</blockquote>    *    * '    *    *<p>The<code>sourceOf</code> map is populated with the source of each    * column; in this case sourceOf.get(0) = 0, and sourceOf.get(1) = 2.</p>    *    * @param aggregate Aggregate relational expression    * @param argList   Ordinals of columns to make distinct    * @param sourceOf  Out parameter, is populated with a map of where each    *                  output field came from    * @return Aggregate relational expression which projects the required    * columns    */
specifier|private
specifier|static
name|AggregateRelBase
name|createSelectDistinct
parameter_list|(
name|AggregateRelBase
name|aggregate
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|sourceOf
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|child
init|=
name|aggregate
operator|.
name|getChild
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|childFields
init|=
name|child
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
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
control|)
block|{
name|sourceOf
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|projects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|projects
operator|.
name|add
argument_list|(
name|RexInputRef
operator|.
name|of2
argument_list|(
name|i
argument_list|,
name|childFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Integer
name|arg
range|:
name|argList
control|)
block|{
if|if
condition|(
name|sourceOf
operator|.
name|get
argument_list|(
name|arg
argument_list|)
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
name|sourceOf
operator|.
name|put
argument_list|(
name|arg
argument_list|,
name|projects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|projects
operator|.
name|add
argument_list|(
name|RexInputRef
operator|.
name|of2
argument_list|(
name|arg
argument_list|,
name|childFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|project
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|child
argument_list|,
name|projects
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Get the distinct values of the GROUP BY fields and the arguments
comment|// to the agg functions.
return|return
name|aggregate
operator|.
name|copy
argument_list|(
name|aggregate
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|project
argument_list|,
name|BitSets
operator|.
name|range
argument_list|(
name|projects
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|AggregateCall
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RemoveDistinctAggregateRule.java
end_comment

end_unit

