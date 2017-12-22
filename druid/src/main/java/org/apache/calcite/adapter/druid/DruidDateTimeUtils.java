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
name|adapter
operator|.
name|druid
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
name|type
operator|.
name|SqlTypeName
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
name|DateString
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
name|TimestampString
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
name|TimestampWithTimeZoneString
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
name|Util
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
name|trace
operator|.
name|CalciteTrace
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
name|Function
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
name|TreeRangeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|Interval
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|chrono
operator|.
name|ISOChronology
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Utilities for generating intervals from RexNode.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
class|class
name|DruidDateTimeUtils
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
specifier|private
name|DruidDateTimeUtils
parameter_list|()
block|{
block|}
comment|/**    * Generates a list of {@link Interval}s equivalent to a given    * expression. Assumes that all the predicates in the input    * reference a single column: the timestamp column.    */
specifier|public
specifier|static
name|List
argument_list|<
name|Interval
argument_list|>
name|createInterval
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|String
name|timeZone
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|ranges
init|=
name|extractRanges
argument_list|(
name|e
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|timeZone
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|ranges
operator|==
literal|null
condition|)
block|{
comment|// We did not succeed, bail out
return|return
literal|null
return|;
block|}
specifier|final
name|TreeRangeSet
name|condensedRanges
init|=
name|TreeRangeSet
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|Range
name|r
range|:
name|ranges
control|)
block|{
name|condensedRanges
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Inferred ranges on interval : "
operator|+
name|condensedRanges
argument_list|)
expr_stmt|;
block|}
return|return
name|toInterval
argument_list|(
name|ImmutableList
operator|.
expr|<
name|Range
operator|>
name|copyOf
argument_list|(
name|condensedRanges
operator|.
name|asRanges
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|List
argument_list|<
name|Interval
argument_list|>
name|toInterval
parameter_list|(
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|ranges
parameter_list|)
block|{
name|List
argument_list|<
name|Interval
argument_list|>
name|intervals
init|=
name|Lists
operator|.
name|transform
argument_list|(
name|ranges
argument_list|,
operator|new
name|Function
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|,
name|Interval
argument_list|>
argument_list|()
block|{
specifier|public
name|Interval
name|apply
parameter_list|(
name|Range
argument_list|<
name|TimestampString
argument_list|>
name|range
parameter_list|)
block|{
if|if
condition|(
operator|!
name|range
operator|.
name|hasLowerBound
argument_list|()
operator|&&
operator|!
name|range
operator|.
name|hasUpperBound
argument_list|()
condition|)
block|{
return|return
name|DruidTable
operator|.
name|DEFAULT_INTERVAL
return|;
block|}
name|long
name|start
init|=
name|range
operator|.
name|hasLowerBound
argument_list|()
condition|?
name|range
operator|.
name|lowerEndpoint
argument_list|()
operator|.
name|getMillisSinceEpoch
argument_list|()
else|:
name|DruidTable
operator|.
name|DEFAULT_INTERVAL
operator|.
name|getStartMillis
argument_list|()
decl_stmt|;
name|long
name|end
init|=
name|range
operator|.
name|hasUpperBound
argument_list|()
condition|?
name|range
operator|.
name|upperEndpoint
argument_list|()
operator|.
name|getMillisSinceEpoch
argument_list|()
else|:
name|DruidTable
operator|.
name|DEFAULT_INTERVAL
operator|.
name|getEndMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|range
operator|.
name|hasLowerBound
argument_list|()
operator|&&
name|range
operator|.
name|lowerBoundType
argument_list|()
operator|==
name|BoundType
operator|.
name|OPEN
condition|)
block|{
name|start
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|range
operator|.
name|hasUpperBound
argument_list|()
operator|&&
name|range
operator|.
name|upperBoundType
argument_list|()
operator|==
name|BoundType
operator|.
name|CLOSED
condition|)
block|{
name|end
operator|++
expr_stmt|;
block|}
return|return
operator|new
name|Interval
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Converted time ranges "
operator|+
name|ranges
operator|+
literal|" to interval "
operator|+
name|intervals
argument_list|)
expr_stmt|;
block|}
return|return
name|intervals
return|;
block|}
specifier|protected
specifier|static
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|extractRanges
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|boolean
name|withNot
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EQUALS
case|:
case|case
name|LESS_THAN
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
case|case
name|GREATER_THAN
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
case|case
name|BETWEEN
case|:
case|case
name|IN
case|:
return|return
name|leafToRanges
argument_list|(
operator|(
name|RexCall
operator|)
name|node
argument_list|,
name|timeZone
argument_list|,
name|withNot
argument_list|)
return|;
case|case
name|NOT
case|:
return|return
name|extractRanges
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|timeZone
argument_list|,
operator|!
name|withNot
argument_list|)
return|;
case|case
name|OR
case|:
block|{
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|intervals
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|child
range|:
name|call
operator|.
name|getOperands
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|extracted
init|=
name|extractRanges
argument_list|(
name|child
argument_list|,
name|timeZone
argument_list|,
name|withNot
argument_list|)
decl_stmt|;
if|if
condition|(
name|extracted
operator|!=
literal|null
condition|)
block|{
name|intervals
operator|.
name|addAll
argument_list|(
name|extracted
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|intervals
return|;
block|}
case|case
name|AND
case|:
block|{
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|child
range|:
name|call
operator|.
name|getOperands
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|extractedRanges
init|=
name|extractRanges
argument_list|(
name|child
argument_list|,
name|timeZone
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|extractedRanges
operator|==
literal|null
operator|||
name|extractedRanges
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// We could not extract, we bail out
return|return
literal|null
return|;
block|}
if|if
condition|(
name|ranges
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ranges
operator|.
name|addAll
argument_list|(
name|extractedRanges
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|overlapped
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Range
name|current
range|:
name|ranges
control|)
block|{
for|for
control|(
name|Range
name|interval
range|:
name|extractedRanges
control|)
block|{
if|if
condition|(
name|current
operator|.
name|isConnected
argument_list|(
name|interval
argument_list|)
condition|)
block|{
name|overlapped
operator|.
name|add
argument_list|(
name|current
operator|.
name|intersection
argument_list|(
name|interval
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ranges
operator|=
name|overlapped
expr_stmt|;
block|}
return|return
name|ranges
return|;
block|}
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|protected
specifier|static
name|List
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|leafToRanges
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|boolean
name|withNot
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
name|LESS_THAN
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
case|case
name|GREATER_THAN
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
block|{
specifier|final
name|TimestampString
name|value
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|RexInputRef
operator|&&
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|timeZone
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|RexInputRef
operator|&&
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|timeZone
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LESS_THAN
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|withNot
condition|?
name|Range
operator|.
name|atLeast
argument_list|(
name|value
argument_list|)
else|:
name|Range
operator|.
name|lessThan
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|withNot
condition|?
name|Range
operator|.
name|greaterThan
argument_list|(
name|value
argument_list|)
else|:
name|Range
operator|.
name|atMost
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|withNot
condition|?
name|Range
operator|.
name|atMost
argument_list|(
name|value
argument_list|)
else|:
name|Range
operator|.
name|greaterThan
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|withNot
condition|?
name|Range
operator|.
name|lessThan
argument_list|(
name|value
argument_list|)
else|:
name|Range
operator|.
name|atLeast
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
default|default:
if|if
condition|(
operator|!
name|withNot
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Range
operator|.
name|closed
argument_list|(
name|value
argument_list|,
name|value
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Range
operator|.
name|lessThan
argument_list|(
name|value
argument_list|)
argument_list|,
name|Range
operator|.
name|greaterThan
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
block|}
case|case
name|BETWEEN
case|:
block|{
specifier|final
name|TimestampString
name|value1
decl_stmt|;
specifier|final
name|TimestampString
name|value2
decl_stmt|;
if|if
condition|(
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|timeZone
argument_list|)
operator|!=
literal|null
operator|&&
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|timeZone
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|value1
operator|=
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
name|value2
operator|=
name|literalValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|timeZone
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|inverted
init|=
name|value1
operator|.
name|compareTo
argument_list|(
name|value2
argument_list|)
operator|>
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|withNot
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|inverted
condition|?
name|Range
operator|.
name|closed
argument_list|(
name|value2
argument_list|,
name|value1
argument_list|)
else|:
name|Range
operator|.
name|closed
argument_list|(
name|value1
argument_list|,
name|value2
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Range
operator|.
name|lessThan
argument_list|(
name|inverted
condition|?
name|value2
else|:
name|value1
argument_list|)
argument_list|,
name|Range
operator|.
name|greaterThan
argument_list|(
name|inverted
condition|?
name|value1
else|:
name|value2
argument_list|)
argument_list|)
return|;
block|}
case|case
name|IN
case|:
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Range
argument_list|<
name|TimestampString
argument_list|>
argument_list|>
name|ranges
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
name|Util
operator|.
name|skip
argument_list|(
name|call
operator|.
name|operands
argument_list|)
control|)
block|{
specifier|final
name|TimestampString
name|element
init|=
name|literalValue
argument_list|(
name|operand
argument_list|,
name|timeZone
argument_list|)
decl_stmt|;
if|if
condition|(
name|element
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|withNot
condition|)
block|{
name|ranges
operator|.
name|add
argument_list|(
name|Range
operator|.
name|lessThan
argument_list|(
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|ranges
operator|.
name|add
argument_list|(
name|Range
operator|.
name|greaterThan
argument_list|(
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ranges
operator|.
name|add
argument_list|(
name|Range
operator|.
name|closed
argument_list|(
name|element
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ranges
operator|.
name|build
argument_list|()
return|;
block|}
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|protected
specifier|static
name|TimestampString
name|literalValue
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
switch|switch
condition|(
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getTypeName
argument_list|()
condition|)
block|{
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
return|return
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValueAs
argument_list|(
name|TimestampString
operator|.
name|class
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
comment|// Cast timestamp to timestamp with local time zone
specifier|final
name|TimestampString
name|t
init|=
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValueAs
argument_list|(
name|TimestampString
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|TimestampWithTimeZoneString
argument_list|(
name|t
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|timeZone
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|withTimeZone
argument_list|(
name|DateTimeUtils
operator|.
name|UTC_ZONE
argument_list|)
operator|.
name|getLocalTimestampString
argument_list|()
return|;
case|case
name|DATE
case|:
comment|// Cast date to timestamp with local time zone
specifier|final
name|DateString
name|d
init|=
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValueAs
argument_list|(
name|DateString
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|TimestampWithTimeZoneString
argument_list|(
name|TimestampString
operator|.
name|fromMillisSinceEpoch
argument_list|(
name|d
operator|.
name|getMillisSinceEpoch
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|timeZone
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|withTimeZone
argument_list|(
name|DateTimeUtils
operator|.
name|UTC_ZONE
argument_list|)
operator|.
name|getLocalTimestampString
argument_list|()
return|;
block|}
break|break;
case|case
name|CAST
case|:
comment|// Normally all CASTs are eliminated by now by constant reduction.
comment|// But when HiveExecutor is used there may be a cast that changes only
comment|// nullability, from TIMESTAMP NOT NULL literal to TIMESTAMP literal.
comment|// We can handle that case by traversing the dummy CAST.
assert|assert
name|node
operator|instanceof
name|RexCall
assert|;
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
specifier|final
name|RexNode
name|operand
init|=
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
specifier|final
name|RelDataType
name|callType
init|=
name|call
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|operandType
init|=
name|operand
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|LITERAL
operator|&&
name|callType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|operandType
operator|.
name|getSqlTypeName
argument_list|()
operator|&&
operator|(
name|callType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|DATE
operator|||
name|callType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|TIMESTAMP
operator|||
name|callType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
operator|)
operator|&&
name|callType
operator|.
name|isNullable
argument_list|()
operator|&&
operator|!
name|operandType
operator|.
name|isNullable
argument_list|()
condition|)
block|{
return|return
name|literalValue
argument_list|(
name|operand
argument_list|,
name|timeZone
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Infers granularity from a timeunit.    * It support {@code FLOOR(<time> TO<timeunit>)} and {@code EXTRACT(<timeunit> FROM<time>)}.    * It returns null if it cannot be inferred.    *    * @param node the Rex node    * @return the granularity, or null if it cannot be inferred    */
specifier|public
specifier|static
name|Granularity
name|extractGranularity
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|int
name|flagIndex
decl_stmt|;
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EXTRACT
case|:
name|flagIndex
operator|=
literal|0
expr_stmt|;
break|break;
case|case
name|FLOOR
case|:
name|flagIndex
operator|=
literal|1
expr_stmt|;
break|break;
default|default:
return|return
literal|null
return|;
block|}
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|operands
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|flagIndex
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
if|if
condition|(
name|timeUnit
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|timeUnit
condition|)
block|{
case|case
name|YEAR
case|:
return|return
name|Granularity
operator|.
name|YEAR
return|;
case|case
name|QUARTER
case|:
return|return
name|Granularity
operator|.
name|QUARTER
return|;
case|case
name|MONTH
case|:
return|return
name|Granularity
operator|.
name|MONTH
return|;
case|case
name|WEEK
case|:
return|return
name|Granularity
operator|.
name|WEEK
return|;
case|case
name|DAY
case|:
return|return
name|Granularity
operator|.
name|DAY
return|;
case|case
name|HOUR
case|:
return|return
name|Granularity
operator|.
name|HOUR
return|;
case|case
name|MINUTE
case|:
return|return
name|Granularity
operator|.
name|MINUTE
return|;
case|case
name|SECOND
case|:
return|return
name|Granularity
operator|.
name|SECOND
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DruidDateTimeUtils.java
end_comment

end_unit

