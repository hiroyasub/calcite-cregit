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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|avatica
operator|.
name|util
operator|.
name|TimeUnitRange
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
name|RexCall
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
name|RexLiteral
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
name|runtime
operator|.
name|PredicateImpl
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
name|SqlBinaryOperator
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
name|SqlKind
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
name|Bug
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
name|base
operator|.
name|Predicate
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
name|BoundType
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
name|ImmutableRangeSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Range
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
name|RangeSet
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
name|TreeRangeSet
import|;
end_import

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
name|ArrayDeque
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * Collection of planner rules that convert  * {@code EXTRACT(timeUnit FROM dateTime) = constant} to  * {@code dateTime BETWEEN lower AND upper}.  *  *<p>The rules allow conversion of queries on time dimension tables, such as  *  *<blockquote>SELECT ... FROM sales JOIN time_by_day USING (time_id)  * WHERE time_by_day.the_year = 1997  * AND time_by_day.the_month IN (4, 5, 6)</blockquote>  *  *<p>into  *  *<blockquote>SELECT ... FROM sales JOIN time_by_day USING (time_id)  * WHERE the_date BETWEEN DATE '2016-04-01' AND DATE '2016-06-30'</blockquote>  *  *<p>and is especially useful for Druid, which has a single timestamp column.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DateRangeRules
block|{
specifier|private
name|DateRangeRules
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|Filter
argument_list|>
name|FILTER_PREDICATE
init|=
operator|new
name|PredicateImpl
argument_list|<
name|Filter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
specifier|final
name|ExtractFinder
name|finder
init|=
name|ExtractFinder
operator|.
name|THREAD_INSTANCES
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|finder
operator|.
name|timeUnits
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"previous user did not clean up"
assert|;
try|try
block|{
name|filter
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
name|finder
argument_list|)
expr_stmt|;
return|return
operator|!
name|finder
operator|.
name|timeUnits
operator|.
name|isEmpty
argument_list|()
return|;
block|}
finally|finally
block|{
name|finder
operator|.
name|timeUnits
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|FILTER_INSTANCE
init|=
operator|new
name|FilterDateRangeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|TimeUnitRange
argument_list|,
name|Integer
argument_list|>
name|TIME_UNIT_CODES
init|=
name|ImmutableMap
operator|.
expr|<
name|TimeUnitRange
decl_stmt|,
name|Integer
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|YEAR
argument_list|,
name|Calendar
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MONTH
argument_list|,
name|Calendar
operator|.
name|MONTH
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|DAY
argument_list|,
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|HOUR
argument_list|,
name|Calendar
operator|.
name|HOUR
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|,
name|Calendar
operator|.
name|MINUTE
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|SECOND
argument_list|,
name|Calendar
operator|.
name|SECOND
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MILLISECOND
argument_list|,
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|TimeUnitRange
argument_list|,
name|TimeUnitRange
argument_list|>
name|TIME_UNIT_PARENTS
init|=
name|ImmutableMap
operator|.
expr|<
name|TimeUnitRange
decl_stmt|,
name|TimeUnitRange
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MONTH
argument_list|,
name|TimeUnitRange
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|DAY
argument_list|,
name|TimeUnitRange
operator|.
name|MONTH
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|HOUR
argument_list|,
name|TimeUnitRange
operator|.
name|DAY
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|,
name|TimeUnitRange
operator|.
name|HOUR
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|SECOND
argument_list|,
name|TimeUnitRange
operator|.
name|MINUTE
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MILLISECOND
argument_list|,
name|TimeUnitRange
operator|.
name|SECOND
argument_list|)
decl|.
name|put
argument_list|(
name|TimeUnitRange
operator|.
name|MICROSECOND
argument_list|,
name|TimeUnitRange
operator|.
name|SECOND
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|/** Returns whether an expression contains one or more calls to the    * {@code EXTRACT} function. */
specifier|public
specifier|static
name|Set
argument_list|<
name|TimeUnitRange
argument_list|>
name|extractTimeUnits
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
specifier|final
name|ExtractFinder
name|finder
init|=
name|ExtractFinder
operator|.
name|THREAD_INSTANCES
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
assert|assert
name|finder
operator|.
name|timeUnits
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"previous user did not clean up"
assert|;
name|e
operator|.
name|accept
argument_list|(
name|finder
argument_list|)
expr_stmt|;
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|finder
operator|.
name|timeUnits
argument_list|)
return|;
block|}
finally|finally
block|{
name|finder
operator|.
name|timeUnits
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Rule that converts EXTRACT in a Filter condition into a date range. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"WeakerAccess"
argument_list|)
specifier|public
specifier|static
class|class
name|FilterDateRangeRule
extends|extends
name|RelOptRule
block|{
specifier|public
name|FilterDateRangeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|FILTER_PREDICATE
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"FilterDateRangeRule"
argument_list|)
expr_stmt|;
block|}
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
name|RexBuilder
name|rexBuilder
init|=
name|filter
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexNode
name|condition
init|=
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
argument_list|>
name|operandRanges
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TimeUnitRange
name|timeUnit
range|:
name|extractTimeUnits
argument_list|(
name|condition
argument_list|)
control|)
block|{
name|condition
operator|=
name|condition
operator|.
name|accept
argument_list|(
operator|new
name|ExtractShuttle
argument_list|(
name|rexBuilder
argument_list|,
name|timeUnit
argument_list|,
name|operandRanges
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|RexUtil
operator|.
name|eq
argument_list|(
name|condition
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|relBuilderFactory
operator|.
name|create
argument_list|(
name|filter
operator|.
name|getCluster
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|RexUtil
operator|.
name|simplify
argument_list|(
name|rexBuilder
argument_list|,
name|condition
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Visitor that searches for calls to the {@code EXTRACT} function, building    * a list of distinct time units. */
specifier|private
specifier|static
class|class
name|ExtractFinder
extends|extends
name|RexVisitorImpl
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|TimeUnitRange
argument_list|>
name|timeUnits
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|TimeUnitRange
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|ExtractFinder
argument_list|>
name|THREAD_INSTANCES
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ExtractFinder
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ExtractFinder
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ExtractFinder
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|ExtractFinder
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EXTRACT
case|:
specifier|final
name|RexLiteral
name|operand
init|=
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|timeUnits
operator|.
name|add
argument_list|(
operator|(
name|TimeUnitRange
operator|)
name|operand
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
comment|/** Walks over an expression, replacing {@code EXTRACT} with date ranges. */
specifier|public
specifier|static
class|class
name|ExtractShuttle
extends|extends
name|RexShuttle
block|{
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
specifier|final
name|TimeUnitRange
name|timeUnit
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
argument_list|>
name|operandRanges
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|RexCall
argument_list|>
name|calls
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|ExtractShuttle
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|TimeUnitRange
name|timeUnit
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
argument_list|>
name|operandRanges
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|this
operator|.
name|timeUnit
operator|=
name|timeUnit
expr_stmt|;
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"Change type to Map<RexNode, RangeSet<Calendar>> when"
operator|+
literal|" [CALCITE-1367] is fixed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operandRanges
operator|=
name|operandRanges
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EQUALS
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
case|case
name|GREATER_THAN
case|:
case|case
name|LESS_THAN
case|:
specifier|final
name|RexNode
name|op0
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|op0
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
if|if
condition|(
name|isExtractCall
argument_list|(
name|op1
argument_list|)
condition|)
block|{
return|return
name|foo
argument_list|(
name|call
operator|.
name|getKind
argument_list|()
operator|.
name|reverse
argument_list|()
argument_list|,
operator|(
operator|(
name|RexCall
operator|)
name|op1
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|(
name|RexLiteral
operator|)
name|op0
argument_list|)
return|;
block|}
block|}
switch|switch
condition|(
name|op1
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
if|if
condition|(
name|isExtractCall
argument_list|(
name|op0
argument_list|)
condition|)
block|{
return|return
name|foo
argument_list|(
name|call
operator|.
name|getKind
argument_list|()
argument_list|,
operator|(
operator|(
name|RexCall
operator|)
name|op0
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|(
name|RexLiteral
operator|)
name|op1
argument_list|)
return|;
block|}
block|}
default|default:
name|calls
operator|.
name|push
argument_list|(
name|call
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
finally|finally
block|{
name|calls
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|RexNode
argument_list|>
name|visitList
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exprs
parameter_list|,
name|boolean
index|[]
name|update
parameter_list|)
block|{
if|if
condition|(
name|exprs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
comment|// a bit more efficient
block|}
switch|switch
condition|(
name|calls
operator|.
name|peek
argument_list|()
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|AND
case|:
return|return
name|super
operator|.
name|visitList
argument_list|(
name|exprs
argument_list|,
name|update
argument_list|)
return|;
default|default:
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
argument_list|>
name|save
init|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|operandRanges
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|>
name|clonedOperands
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|exprs
control|)
block|{
name|RexNode
name|clonedOperand
init|=
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|clonedOperand
operator|!=
name|operand
operator|)
operator|&&
operator|(
name|update
operator|!=
literal|null
operator|)
condition|)
block|{
name|update
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
name|clonedOperands
operator|.
name|add
argument_list|(
name|clonedOperand
argument_list|)
expr_stmt|;
comment|// Restore the state. For an operator such as "OR", an argument
comment|// cannot inherit the previous argument's state.
name|operandRanges
operator|.
name|clear
argument_list|()
expr_stmt|;
name|operandRanges
operator|.
name|putAll
argument_list|(
name|save
argument_list|)
expr_stmt|;
block|}
return|return
name|clonedOperands
operator|.
name|build
argument_list|()
return|;
block|}
block|}
name|boolean
name|isExtractCall
parameter_list|(
name|RexNode
name|e
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
name|EXTRACT
case|:
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|e
decl_stmt|;
specifier|final
name|RexLiteral
name|flag
init|=
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TimeUnitRange
name|timeUnit
init|=
operator|(
name|TimeUnitRange
operator|)
name|flag
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|timeUnit
operator|==
name|this
operator|.
name|timeUnit
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
name|RexNode
name|foo
parameter_list|(
name|SqlKind
name|comparison
parameter_list|,
name|RexNode
name|operand
parameter_list|,
name|RexLiteral
name|literal
parameter_list|)
block|{
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
name|rangeSet
init|=
name|operandRanges
operator|.
name|get
argument_list|(
name|operand
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rangeSet
operator|==
literal|null
condition|)
block|{
name|rangeSet
operator|=
name|ImmutableRangeSet
operator|.
expr|<
name|Calendar
operator|>
name|of
argument_list|()
operator|.
name|complement
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RangeSet
argument_list|<
name|Calendar
argument_list|>
name|s2
init|=
name|TreeRangeSet
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Calendar.MONTH is 0-based
specifier|final
name|int
name|v
init|=
operator|(
operator|(
name|BigDecimal
operator|)
name|literal
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
operator|-
operator|(
name|timeUnit
operator|==
name|TimeUnitRange
operator|.
name|MONTH
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
for|for
control|(
name|Range
argument_list|<
name|Calendar
argument_list|>
name|r
range|:
name|rangeSet
operator|.
name|asRanges
argument_list|()
control|)
block|{
specifier|final
name|Calendar
name|c
decl_stmt|;
switch|switch
condition|(
name|timeUnit
condition|)
block|{
case|case
name|YEAR
case|:
name|c
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|DateTimeUtils
operator|.
name|GMT_ZONE
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|()
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|v
argument_list|,
name|Calendar
operator|.
name|JANUARY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|s2
operator|.
name|add
argument_list|(
name|baz
argument_list|(
name|timeUnit
argument_list|,
name|comparison
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|MONTH
case|:
case|case
name|DAY
case|:
case|case
name|HOUR
case|:
case|case
name|MINUTE
case|:
case|case
name|SECOND
case|:
if|if
condition|(
name|r
operator|.
name|hasLowerBound
argument_list|()
condition|)
block|{
name|c
operator|=
operator|(
name|Calendar
operator|)
name|r
operator|.
name|lowerEndpoint
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|next
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
name|v
argument_list|,
name|r
argument_list|,
name|i
operator|++
operator|>
literal|0
argument_list|)
condition|)
block|{
name|s2
operator|.
name|add
argument_list|(
name|baz
argument_list|(
name|timeUnit
argument_list|,
name|comparison
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Intersect old range set with new.
name|s2
operator|.
name|removeAll
argument_list|(
name|rangeSet
operator|.
name|complement
argument_list|()
argument_list|)
expr_stmt|;
name|operandRanges
operator|.
name|put
argument_list|(
name|operand
operator|.
name|toString
argument_list|()
argument_list|,
name|ImmutableRangeSet
operator|.
name|copyOf
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Range
argument_list|<
name|Calendar
argument_list|>
name|r
range|:
name|s2
operator|.
name|asRanges
argument_list|()
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|toRex
argument_list|(
name|operand
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|RexUtil
operator|.
name|composeDisjunction
argument_list|(
name|rexBuilder
argument_list|,
name|nodes
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|next
parameter_list|(
name|Calendar
name|c
parameter_list|,
name|TimeUnitRange
name|timeUnit
parameter_list|,
name|int
name|v
parameter_list|,
name|Range
argument_list|<
name|Calendar
argument_list|>
name|r
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
specifier|final
name|Calendar
name|original
init|=
operator|(
name|Calendar
operator|)
name|c
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|int
name|code
init|=
name|TIME_UNIT_CODES
operator|.
name|get
argument_list|(
name|timeUnit
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|c
operator|.
name|set
argument_list|(
name|code
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|int
name|v2
init|=
name|c
operator|.
name|get
argument_list|(
name|code
argument_list|)
decl_stmt|;
if|if
condition|(
name|v2
operator|<
name|v
condition|)
block|{
comment|// E.g. when we set DAY=30 on 2014-02-01, we get 2014-02-30 because
comment|// February has 28 days.
continue|continue;
block|}
if|if
condition|(
name|strict
operator|&&
name|original
operator|.
name|compareTo
argument_list|(
name|c
argument_list|)
operator|==
literal|0
condition|)
block|{
name|c
operator|.
name|add
argument_list|(
name|TIME_UNIT_CODES
operator|.
name|get
argument_list|(
name|TIME_UNIT_PARENTS
operator|.
name|get
argument_list|(
name|timeUnit
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|r
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|private
name|RexNode
name|toRex
parameter_list|(
name|RexNode
name|operand
parameter_list|,
name|Range
argument_list|<
name|Calendar
argument_list|>
name|r
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|hasLowerBound
argument_list|()
condition|)
block|{
specifier|final
name|SqlBinaryOperator
name|op
init|=
name|r
operator|.
name|lowerBoundType
argument_list|()
operator|==
name|BoundType
operator|.
name|CLOSED
condition|?
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
else|:
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op
argument_list|,
name|operand
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|r
operator|.
name|lowerEndpoint
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|hasUpperBound
argument_list|()
condition|)
block|{
specifier|final
name|SqlBinaryOperator
name|op
init|=
name|r
operator|.
name|upperBoundType
argument_list|()
operator|==
name|BoundType
operator|.
name|CLOSED
condition|?
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
else|:
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op
argument_list|,
name|operand
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|r
operator|.
name|upperEndpoint
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|nodes
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|Range
argument_list|<
name|Calendar
argument_list|>
name|baz
parameter_list|(
name|TimeUnitRange
name|timeUnit
parameter_list|,
name|SqlKind
name|comparison
parameter_list|,
name|Calendar
name|c
parameter_list|)
block|{
switch|switch
condition|(
name|comparison
condition|)
block|{
case|case
name|EQUALS
case|:
return|return
name|Range
operator|.
name|closedOpen
argument_list|(
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|true
argument_list|)
argument_list|,
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
case|case
name|LESS_THAN
case|:
return|return
name|Range
operator|.
name|lessThan
argument_list|(
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|Range
operator|.
name|lessThan
argument_list|(
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|Range
operator|.
name|atLeast
argument_list|(
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|Range
operator|.
name|atLeast
argument_list|(
name|round
argument_list|(
name|c
argument_list|,
name|timeUnit
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|comparison
argument_list|)
throw|;
block|}
block|}
comment|/** Returns a copy of a calendar, optionally rounded up to the next time      * unit. */
specifier|private
name|Calendar
name|round
parameter_list|(
name|Calendar
name|c
parameter_list|,
name|TimeUnitRange
name|timeUnit
parameter_list|,
name|boolean
name|down
parameter_list|)
block|{
name|c
operator|=
operator|(
name|Calendar
operator|)
name|c
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|down
condition|)
block|{
specifier|final
name|Integer
name|code
init|=
name|TIME_UNIT_CODES
operator|.
name|get
argument_list|(
name|timeUnit
argument_list|)
decl_stmt|;
specifier|final
name|int
name|v
init|=
name|c
operator|.
name|get
argument_list|(
name|code
argument_list|)
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
name|code
argument_list|,
name|v
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DateRangeRules.java
end_comment

end_unit

