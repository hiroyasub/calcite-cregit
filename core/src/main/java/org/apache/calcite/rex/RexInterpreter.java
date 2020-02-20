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
name|rex
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
name|TimeUnit
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
name|metadata
operator|.
name|NullSentinel
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
name|util
operator|.
name|NlsString
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
name|math
operator|.
name|BigInteger
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
name|Comparator
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
name|function
operator|.
name|IntPredicate
import|;
end_import

begin_comment
comment|/**  * Evaluates {@link RexNode} expressions.  *  *<p>Caveats:  *<ul>  *<li>It uses interpretation, so it is not very efficient.  *<li>It is intended for testing, so does not cover very many functions and  *   operators. (Feel free to contribute more!)  *<li>It is not well tested.  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|RexInterpreter
implements|implements
name|RexVisitor
argument_list|<
name|Comparable
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|NullSentinel
name|N
init|=
name|NullSentinel
operator|.
name|INSTANCE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|SUPPORTED_SQL_KIND
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|SqlKind
operator|.
name|EQUALS
argument_list|,
name|SqlKind
operator|.
name|IS_DISTINCT_FROM
argument_list|,
name|SqlKind
operator|.
name|NOT_EQUALS
argument_list|,
name|SqlKind
operator|.
name|GREATER_THAN
argument_list|,
name|SqlKind
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|SqlKind
operator|.
name|LESS_THAN
argument_list|,
name|SqlKind
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|SqlKind
operator|.
name|AND
argument_list|,
name|SqlKind
operator|.
name|OR
argument_list|,
name|SqlKind
operator|.
name|NOT
argument_list|,
name|SqlKind
operator|.
name|CASE
argument_list|,
name|SqlKind
operator|.
name|IS_TRUE
argument_list|,
name|SqlKind
operator|.
name|IS_NOT_TRUE
argument_list|,
name|SqlKind
operator|.
name|IS_FALSE
argument_list|,
name|SqlKind
operator|.
name|IS_NOT_FALSE
argument_list|,
name|SqlKind
operator|.
name|PLUS_PREFIX
argument_list|,
name|SqlKind
operator|.
name|MINUS_PREFIX
argument_list|,
name|SqlKind
operator|.
name|PLUS
argument_list|,
name|SqlKind
operator|.
name|MINUS
argument_list|,
name|SqlKind
operator|.
name|TIMES
argument_list|,
name|SqlKind
operator|.
name|DIVIDE
argument_list|,
name|SqlKind
operator|.
name|COALESCE
argument_list|,
name|SqlKind
operator|.
name|CEIL
argument_list|,
name|SqlKind
operator|.
name|FLOOR
argument_list|,
name|SqlKind
operator|.
name|EXTRACT
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Comparable
argument_list|>
name|environment
decl_stmt|;
comment|/** Creates an interpreter.    *    * @param environment Values of certain expressions (usually    *       {@link RexInputRef}s)    */
specifier|private
name|RexInterpreter
parameter_list|(
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Comparable
argument_list|>
name|environment
parameter_list|)
block|{
name|this
operator|.
name|environment
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
comment|/** Evaluates an expression in an environment. */
specifier|public
specifier|static
name|Comparable
name|evaluate
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|Map
argument_list|<
name|RexNode
argument_list|,
name|Comparable
argument_list|>
name|map
parameter_list|)
block|{
specifier|final
name|Comparable
name|v
init|=
name|e
operator|.
name|accept
argument_list|(
operator|new
name|RexInterpreter
argument_list|(
name|map
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"evaluate "
operator|+
name|e
operator|+
literal|" on "
operator|+
name|map
operator|+
literal|" returns "
operator|+
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
specifier|private
name|IllegalArgumentException
name|unbound
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
return|return
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unbound: "
operator|+
name|e
argument_list|)
return|;
block|}
specifier|private
name|Comparable
name|getOrUnbound
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
specifier|final
name|Comparable
name|comparable
init|=
name|environment
operator|.
name|get
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparable
operator|!=
literal|null
condition|)
block|{
return|return
name|comparable
return|;
block|}
throw|throw
name|unbound
argument_list|(
name|e
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|getOrUnbound
argument_list|(
name|inputRef
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|localRef
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
return|return
name|Util
operator|.
name|first
argument_list|(
name|literal
operator|.
name|getValue4
argument_list|()
argument_list|,
name|N
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|over
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|correlVariable
parameter_list|)
block|{
return|return
name|getOrUnbound
argument_list|(
name|correlVariable
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|)
block|{
return|return
name|getOrUnbound
argument_list|(
name|dynamicParam
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|rangeRef
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
return|return
name|getOrUnbound
argument_list|(
name|fieldAccess
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|visitSubQuery
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|subQuery
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitTableInputRef
parameter_list|(
name|RexTableInputRef
name|fieldRef
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|fieldRef
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitPatternFieldRef
parameter_list|(
name|RexPatternFieldRef
name|fieldRef
parameter_list|)
block|{
throw|throw
name|unbound
argument_list|(
name|fieldRef
argument_list|)
throw|;
block|}
specifier|public
name|Comparable
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|call
operator|.
name|operands
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
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
name|IS_NOT_DISTINCT_FROM
case|:
if|if
condition|(
name|containsNull
argument_list|(
name|values
argument_list|)
condition|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|// falls through EQUALS
case|case
name|EQUALS
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|==
literal|0
argument_list|)
return|;
case|case
name|IS_DISTINCT_FROM
case|:
if|if
condition|(
name|containsNull
argument_list|(
name|values
argument_list|)
condition|)
block|{
return|return
operator|!
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|// falls through NOT_EQUALS
case|case
name|NOT_EQUALS
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|!=
literal|0
argument_list|)
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|>
literal|0
argument_list|)
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|>=
literal|0
argument_list|)
return|;
case|case
name|LESS_THAN
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|<
literal|0
argument_list|)
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|compare
argument_list|(
name|values
argument_list|,
name|c
lambda|->
name|c
operator|<=
literal|0
argument_list|)
return|;
case|case
name|AND
case|:
return|return
name|values
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Truthy
operator|::
name|of
argument_list|)
operator|.
name|min
argument_list|(
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toComparable
argument_list|()
return|;
case|case
name|OR
case|:
return|return
name|values
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Truthy
operator|::
name|of
argument_list|)
operator|.
name|max
argument_list|(
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toComparable
argument_list|()
return|;
case|case
name|NOT
case|:
return|return
name|not
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
case|case
name|CASE
case|:
return|return
name|case_
argument_list|(
name|values
argument_list|)
return|;
case|case
name|IS_TRUE
case|:
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|true
argument_list|)
return|;
case|case
name|IS_NOT_TRUE
case|:
return|return
operator|!
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|true
argument_list|)
return|;
case|case
name|IS_NULL
case|:
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|N
argument_list|)
return|;
case|case
name|IS_NOT_NULL
case|:
return|return
operator|!
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|N
argument_list|)
return|;
case|case
name|IS_FALSE
case|:
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|false
argument_list|)
return|;
case|case
name|IS_NOT_FALSE
case|:
return|return
operator|!
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|false
argument_list|)
return|;
case|case
name|PLUS_PREFIX
case|:
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
case|case
name|MINUS_PREFIX
case|:
return|return
name|containsNull
argument_list|(
name|values
argument_list|)
condition|?
name|N
else|:
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|negate
argument_list|()
return|;
case|case
name|PLUS
case|:
return|return
name|containsNull
argument_list|(
name|values
argument_list|)
condition|?
name|N
else|:
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|MINUS
case|:
return|return
name|containsNull
argument_list|(
name|values
argument_list|)
condition|?
name|N
else|:
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|subtract
argument_list|(
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|TIMES
case|:
return|return
name|containsNull
argument_list|(
name|values
argument_list|)
condition|?
name|N
else|:
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|multiply
argument_list|(
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|DIVIDE
case|:
return|return
name|containsNull
argument_list|(
name|values
argument_list|)
condition|?
name|N
else|:
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|divide
argument_list|(
name|number
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|CAST
case|:
return|return
name|cast
argument_list|(
name|call
argument_list|,
name|values
argument_list|)
return|;
case|case
name|COALESCE
case|:
return|return
name|coalesce
argument_list|(
name|call
argument_list|,
name|values
argument_list|)
return|;
case|case
name|CEIL
case|:
case|case
name|FLOOR
case|:
return|return
name|ceil
argument_list|(
name|call
argument_list|,
name|values
argument_list|)
return|;
case|case
name|EXTRACT
case|:
return|return
name|extract
argument_list|(
name|call
argument_list|,
name|values
argument_list|)
return|;
default|default:
throw|throw
name|unbound
argument_list|(
name|call
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Comparable
name|extract
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
specifier|final
name|Comparable
name|v
init|=
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
name|N
condition|)
block|{
return|return
name|N
return|;
block|}
specifier|final
name|TimeUnitRange
name|timeUnitRange
init|=
operator|(
name|TimeUnitRange
operator|)
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|v2
decl_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|Long
condition|)
block|{
comment|// TIMESTAMP
name|v2
operator|=
operator|(
name|int
operator|)
operator|(
operator|(
operator|(
name|Long
operator|)
name|v
operator|)
operator|/
name|TimeUnit
operator|.
name|DAY
operator|.
name|multiplier
operator|.
name|longValue
argument_list|()
operator|)
expr_stmt|;
block|}
else|else
block|{
comment|// DATE
name|v2
operator|=
operator|(
name|Integer
operator|)
name|v
expr_stmt|;
block|}
return|return
name|DateTimeUtils
operator|.
name|unixDateExtract
argument_list|(
name|timeUnitRange
argument_list|,
name|v2
argument_list|)
return|;
block|}
specifier|private
name|Comparable
name|coalesce
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
for|for
control|(
name|Comparable
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
name|N
condition|)
block|{
return|return
name|value
return|;
block|}
block|}
return|return
name|N
return|;
block|}
specifier|private
name|Comparable
name|ceil
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|N
condition|)
block|{
return|return
name|N
return|;
block|}
specifier|final
name|Long
name|v
init|=
operator|(
name|Long
operator|)
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TimeUnitRange
name|unit
init|=
operator|(
name|TimeUnitRange
operator|)
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
name|YEAR
case|:
case|case
name|MONTH
case|:
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|FLOOR
case|:
return|return
name|DateTimeUtils
operator|.
name|unixTimestampFloor
argument_list|(
name|unit
argument_list|,
name|v
argument_list|)
return|;
default|default:
return|return
name|DateTimeUtils
operator|.
name|unixTimestampCeil
argument_list|(
name|unit
argument_list|,
name|v
argument_list|)
return|;
block|}
block|}
specifier|final
name|TimeUnitRange
name|subUnit
init|=
name|subUnit
argument_list|(
name|unit
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|v2
init|=
name|v
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|e
init|=
name|DateTimeUtils
operator|.
name|unixTimestampExtract
argument_list|(
name|subUnit
argument_list|,
name|v2
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|0
condition|)
block|{
return|return
name|v2
return|;
block|}
name|v2
operator|-=
name|unit
operator|.
name|startUnit
operator|.
name|multiplier
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|TimeUnitRange
name|subUnit
parameter_list|(
name|TimeUnitRange
name|unit
parameter_list|)
block|{
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
name|QUARTER
case|:
return|return
name|TimeUnitRange
operator|.
name|MONTH
return|;
default|default:
return|return
name|TimeUnitRange
operator|.
name|DAY
return|;
block|}
block|}
specifier|private
name|Comparable
name|cast
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|N
condition|)
block|{
return|return
name|N
return|;
block|}
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|Comparable
name|not
parameter_list|(
name|Comparable
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|true
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|false
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|N
return|;
block|}
block|}
specifier|private
name|Comparable
name|case_
parameter_list|(
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
specifier|final
name|int
name|size
decl_stmt|;
specifier|final
name|Comparable
name|elseValue
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|size
operator|=
name|values
operator|.
name|size
argument_list|()
expr_stmt|;
name|elseValue
operator|=
name|N
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
name|values
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
name|elseValue
operator|=
name|Util
operator|.
name|last
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
literal|true
argument_list|)
condition|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
return|return
name|elseValue
return|;
block|}
specifier|private
name|BigDecimal
name|number
parameter_list|(
name|Comparable
name|comparable
parameter_list|)
block|{
return|return
name|comparable
operator|instanceof
name|BigDecimal
condition|?
operator|(
name|BigDecimal
operator|)
name|comparable
else|:
name|comparable
operator|instanceof
name|BigInteger
condition|?
operator|new
name|BigDecimal
argument_list|(
operator|(
name|BigInteger
operator|)
name|comparable
argument_list|)
else|:
name|comparable
operator|instanceof
name|Long
operator|||
name|comparable
operator|instanceof
name|Integer
operator|||
name|comparable
operator|instanceof
name|Short
condition|?
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|comparable
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
else|:
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|comparable
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Comparable
name|compare
parameter_list|(
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|,
name|IntPredicate
name|p
parameter_list|)
block|{
if|if
condition|(
name|containsNull
argument_list|(
name|values
argument_list|)
condition|)
block|{
return|return
name|N
return|;
block|}
name|Comparable
name|v0
init|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Comparable
name|v1
init|=
name|values
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|v0
operator|instanceof
name|Number
operator|&&
name|v1
operator|instanceof
name|NlsString
condition|)
block|{
try|try
block|{
name|v1
operator|=
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|NlsString
operator|)
name|v1
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|v1
operator|instanceof
name|Number
operator|&&
name|v0
operator|instanceof
name|NlsString
condition|)
block|{
try|try
block|{
name|v0
operator|=
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|NlsString
operator|)
name|v0
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|v0
operator|instanceof
name|Number
condition|)
block|{
name|v0
operator|=
name|number
argument_list|(
name|v0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v1
operator|instanceof
name|Number
condition|)
block|{
name|v1
operator|=
name|number
argument_list|(
name|v1
argument_list|)
expr_stmt|;
block|}
comment|//noinspection unchecked
specifier|final
name|int
name|c
init|=
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
decl_stmt|;
return|return
name|p
operator|.
name|test
argument_list|(
name|c
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|containsNull
parameter_list|(
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|)
block|{
for|for
control|(
name|Comparable
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|==
name|N
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
comment|/** An enum that wraps boolean and unknown values and makes them    * comparable. */
enum|enum
name|Truthy
block|{
comment|// Order is important; AND returns the min, OR returns the max
name|FALSE
block|,
name|UNKNOWN
block|,
name|TRUE
block|;
specifier|static
name|Truthy
name|of
parameter_list|(
name|Comparable
name|c
parameter_list|)
block|{
return|return
name|c
operator|.
name|equals
argument_list|(
literal|true
argument_list|)
condition|?
name|TRUE
else|:
name|c
operator|.
name|equals
argument_list|(
literal|false
argument_list|)
condition|?
name|FALSE
else|:
name|UNKNOWN
return|;
block|}
name|Comparable
name|toComparable
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|TRUE
case|:
return|return
literal|true
return|;
case|case
name|FALSE
case|:
return|return
literal|false
return|;
case|case
name|UNKNOWN
case|:
return|return
name|N
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

