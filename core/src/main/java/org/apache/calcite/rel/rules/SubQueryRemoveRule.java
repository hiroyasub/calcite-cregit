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
name|RelOptRuleOperand
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
name|Correlate
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
name|Filter
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
name|Project
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
name|rex
operator|.
name|LogicVisitor
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
name|RexCorrelVariable
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
name|rex
operator|.
name|RexSubQuery
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|sql2rel
operator|.
name|RelDecorrelator
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
name|RelBuilder
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
comment|/**  * Transform that converts IN, EXISTS and scalar sub-queries into joins.  *  *<p>Sub-queries are represented by {@link RexSubQuery} expressions.  *  *<p>A sub-query may or may not be correlated. If a sub-query is correlated,  * the wrapped {@link RelNode} will contain a {@link RexCorrelVariable} before  * the rewrite, and the product of the rewrite will be a {@link Correlate}.  * The Correlate can be removed using {@link RelDecorrelator}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SubQueryRemoveRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|SubQueryRemoveRule
name|PROJECT
init|=
operator|new
name|SubQueryRemoveRule
argument_list|(
name|operand
argument_list|(
name|Project
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|PROJECT_PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"SubQueryRemoveRule:Project"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Project
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexSubQuery
name|e
init|=
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|find
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|e
operator|!=
literal|null
assert|;
specifier|final
name|RelOptUtil
operator|.
name|Logic
name|logic
init|=
name|LogicVisitor
operator|.
name|find
argument_list|(
name|RelOptUtil
operator|.
name|Logic
operator|.
name|TRUE_FALSE_UNKNOWN
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|builder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|target
init|=
name|apply
argument_list|(
name|e
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|CorrelationId
operator|>
name|of
argument_list|()
argument_list|,
name|logic
argument_list|,
name|builder
argument_list|,
literal|1
argument_list|,
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|RexShuttle
name|shuttle
init|=
operator|new
name|ReplaceSubQueryShuttle
argument_list|(
name|e
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|shuttle
operator|.
name|apply
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SubQueryRemoveRule
name|FILTER
init|=
operator|new
name|SubQueryRemoveRule
argument_list|(
name|operand
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|FILTER_PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"SubQueryRemoveRule:Filter"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Filter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexSubQuery
name|e
init|=
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|find
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|e
operator|!=
literal|null
assert|;
specifier|final
name|RelOptUtil
operator|.
name|Logic
name|logic
init|=
name|LogicVisitor
operator|.
name|find
argument_list|(
name|RelOptUtil
operator|.
name|Logic
operator|.
name|TRUE
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|builder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|target
init|=
name|apply
argument_list|(
name|e
argument_list|,
name|filter
operator|.
name|getVariablesSet
argument_list|()
argument_list|,
name|logic
argument_list|,
name|builder
argument_list|,
literal|1
argument_list|,
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|RexShuttle
name|shuttle
init|=
operator|new
name|ReplaceSubQueryShuttle
argument_list|(
name|e
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|builder
operator|.
name|filter
argument_list|(
name|shuttle
operator|.
name|apply
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|fields
argument_list|(
name|builder
argument_list|,
name|filter
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SubQueryRemoveRule
name|JOIN
init|=
operator|new
name|SubQueryRemoveRule
argument_list|(
name|operand
argument_list|(
name|Join
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|JOIN_PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"SubQueryRemoveRule:Join"
argument_list|)
block|{
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
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelBuilder
name|builder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RexSubQuery
name|e
init|=
name|RexUtil
operator|.
name|SubQueryFinder
operator|.
name|find
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|e
operator|!=
literal|null
assert|;
specifier|final
name|RelOptUtil
operator|.
name|Logic
name|logic
init|=
name|LogicVisitor
operator|.
name|find
argument_list|(
name|RelOptUtil
operator|.
name|Logic
operator|.
name|TRUE
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|target
init|=
name|apply
argument_list|(
name|e
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|CorrelationId
operator|>
name|of
argument_list|()
argument_list|,
name|logic
argument_list|,
name|builder
argument_list|,
literal|2
argument_list|,
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|RexShuttle
name|shuttle
init|=
operator|new
name|ReplaceSubQueryShuttle
argument_list|(
name|e
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|builder
operator|.
name|join
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|shuttle
operator|.
name|apply
argument_list|(
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|fields
argument_list|(
name|builder
argument_list|,
name|join
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
name|SubQueryRemoveRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|RexNode
name|apply
parameter_list|(
name|RexSubQuery
name|e
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
parameter_list|,
name|RelOptUtil
operator|.
name|Logic
name|logic
parameter_list|,
name|RelBuilder
name|builder
parameter_list|,
name|int
name|inputCount
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
switch|switch
condition|(
name|e
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|SCALAR_QUERY
case|:
name|builder
operator|.
name|push
argument_list|(
name|e
operator|.
name|rel
argument_list|)
expr_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|final
name|Boolean
name|unique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|builder
operator|.
name|peek
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
operator|==
literal|null
operator|||
operator|!
name|unique
condition|)
block|{
name|builder
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SINGLE_VALUE
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|LEFT
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|,
name|variablesSet
argument_list|)
expr_stmt|;
return|return
name|field
argument_list|(
name|builder
argument_list|,
name|inputCount
argument_list|,
name|offset
argument_list|)
return|;
case|case
name|IN
case|:
case|case
name|EXISTS
case|:
comment|// Most general case, where the left and right keys might have nulls, and
comment|// caller requires 3-valued logic return.
comment|//
comment|// select e.deptno, e.deptno in (select deptno from emp)
comment|//
comment|// becomes
comment|//
comment|// select e.deptno,
comment|//   case
comment|//   when ct.c = 0 then false
comment|//   when dt.i is not null then true
comment|//   when e.deptno is null then null
comment|//   when ct.ck< ct.c then null
comment|//   else false
comment|//   end
comment|// from e
comment|// left join (
comment|//   (select count(*) as c, count(deptno) as ck from emp) as ct
comment|//   cross join (select distinct deptno, true as i from emp)) as dt
comment|//   on e.deptno = dt.deptno
comment|//
comment|// If keys are not null we can remove "ct" and simplify to
comment|//
comment|// select e.deptno,
comment|//   case
comment|//   when dt.i is not null then true
comment|//   else false
comment|//   end
comment|// from e
comment|// left join (select distinct deptno, true as i from emp) as dt
comment|//   on e.deptno = dt.deptno
comment|//
comment|// We could further simplify to
comment|//
comment|// select e.deptno,
comment|//   dt.i is not null
comment|// from e
comment|// left join (select distinct deptno, true as i from emp) as dt
comment|//   on e.deptno = dt.deptno
comment|//
comment|// but have not yet.
comment|//
comment|// If the logic is TRUE we can just kill the record if the condition
comment|// evaluates to FALSE or UNKNOWN. Thus the query simplifies to an inner
comment|// join:
comment|//
comment|// select e.deptno,
comment|//   true
comment|// from e
comment|// inner join (select distinct deptno from emp) as dt
comment|//   on e.deptno = dt.deptno
comment|//
name|builder
operator|.
name|push
argument_list|(
name|e
operator|.
name|rel
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|e
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IN
case|:
name|fields
operator|.
name|addAll
argument_list|(
name|builder
operator|.
name|fields
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// First, the cross join
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE_FALSE_UNKNOWN
case|:
case|case
name|UNKNOWN_AS_TRUE
case|:
if|if
condition|(
operator|!
name|variablesSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// We have not yet figured out how to include "ct" in a query if
comment|// the source relation "e.rel" is correlated. So, dodge the issue:
comment|// we pretend that the join key is NOT NULL.
comment|//
comment|// We will get wrong results in correlated IN where the join
comment|// key has nulls. E.g.
comment|//
comment|//   SELECT *
comment|//   FROM emp
comment|//   WHERE mgr NOT IN (
comment|//     SELECT mgr
comment|//     FROM emp AS e2
comment|//     WHERE
name|logic
operator|=
name|RelOptUtil
operator|.
name|Logic
operator|.
name|TRUE_FALSE
expr_stmt|;
break|break;
block|}
name|builder
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|()
argument_list|,
name|builder
operator|.
name|count
argument_list|(
literal|false
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|builder
operator|.
name|aggregateCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|"ck"
argument_list|,
name|builder
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|as
argument_list|(
literal|"ct"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|,
name|variablesSet
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|2
expr_stmt|;
name|builder
operator|.
name|push
argument_list|(
name|e
operator|.
name|rel
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Now the left join
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE
case|:
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|project
argument_list|(
name|builder
operator|.
name|alias
argument_list|(
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|aggregate
argument_list|(
name|builder
operator|.
name|groupKey
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|fields
operator|.
name|add
argument_list|(
name|builder
operator|.
name|alias
argument_list|(
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|project
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|builder
operator|.
name|distinct
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|as
argument_list|(
literal|"dt"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|conditions
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
name|RexNode
argument_list|,
name|RexNode
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|e
operator|.
name|getOperands
argument_list|()
argument_list|,
name|builder
operator|.
name|fields
argument_list|()
argument_list|)
control|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|pair
operator|.
name|left
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|offset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE
case|:
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|builder
operator|.
name|and
argument_list|(
name|conditions
argument_list|)
argument_list|,
name|variablesSet
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
return|;
block|}
name|builder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|LEFT
argument_list|,
name|builder
operator|.
name|and
argument_list|(
name|conditions
argument_list|)
argument_list|,
name|variablesSet
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|keyIsNulls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|e
operator|.
name|getOperands
argument_list|()
control|)
block|{
if|if
condition|(
name|operand
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|keyIsNulls
operator|.
name|add
argument_list|(
name|builder
operator|.
name|isNull
argument_list|(
name|operand
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE_FALSE_UNKNOWN
case|:
case|case
name|UNKNOWN_AS_TRUE
case|:
name|operands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"ct"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|operands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|isNotNull
argument_list|(
name|builder
operator|.
name|field
argument_list|(
literal|"dt"
argument_list|,
literal|"i"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keyIsNulls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|operands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|or
argument_list|(
name|keyIsNulls
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Boolean
name|b
init|=
literal|true
decl_stmt|;
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|TRUE_FALSE_UNKNOWN
case|:
name|b
operator|=
literal|null
expr_stmt|;
comment|// fall through
case|case
name|UNKNOWN_AS_TRUE
case|:
name|operands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ct"
argument_list|,
literal|"ck"
argument_list|)
argument_list|,
name|builder
operator|.
name|field
argument_list|(
literal|"ct"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|literal
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|operands
operator|.
name|add
argument_list|(
name|builder
operator|.
name|literal
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|operands
operator|.
name|build
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
operator|.
name|getKind
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Returns a reference to a particular field, by offset, across several    * inputs on a {@link RelBuilder}'s stack. */
specifier|private
name|RexInputRef
name|field
parameter_list|(
name|RelBuilder
name|builder
parameter_list|,
name|int
name|inputCount
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
for|for
control|(
name|int
name|inputOrdinal
init|=
literal|0
init|;
condition|;
control|)
block|{
specifier|final
name|RelNode
name|r
init|=
name|builder
operator|.
name|peek
argument_list|(
name|inputCount
argument_list|,
name|inputOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|r
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
return|return
name|builder
operator|.
name|field
argument_list|(
name|inputCount
argument_list|,
name|inputOrdinal
argument_list|,
name|offset
argument_list|)
return|;
block|}
operator|++
name|inputOrdinal
expr_stmt|;
name|offset
operator|-=
name|r
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns a list of expressions that project the first {@code fieldCount}    * fields of the top input on a {@link RelBuilder}'s stack. */
specifier|private
specifier|static
name|List
argument_list|<
name|RexNode
argument_list|>
name|fields
parameter_list|(
name|RelBuilder
name|builder
parameter_list|,
name|int
name|fieldCount
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|projects
operator|.
name|add
argument_list|(
name|builder
operator|.
name|field
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|projects
return|;
block|}
comment|/** Shuttle that replaces occurrences of a given    * {@link org.apache.calcite.rex.RexSubQuery} with a replacement    * expression. */
specifier|private
specifier|static
class|class
name|ReplaceSubQueryShuttle
extends|extends
name|RexShuttle
block|{
specifier|private
specifier|final
name|RexSubQuery
name|subQuery
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|replacement
decl_stmt|;
specifier|public
name|ReplaceSubQueryShuttle
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|,
name|RexNode
name|replacement
parameter_list|)
block|{
name|this
operator|.
name|subQuery
operator|=
name|subQuery
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitSubQuery
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|)
block|{
return|return
name|RexUtil
operator|.
name|eq
argument_list|(
name|subQuery
argument_list|,
name|this
operator|.
name|subQuery
argument_list|)
condition|?
name|replacement
else|:
name|subQuery
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SubQueryRemoveRule.java
end_comment

end_unit

