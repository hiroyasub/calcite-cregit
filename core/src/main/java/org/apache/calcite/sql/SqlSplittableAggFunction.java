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
name|sql
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
name|rel
operator|.
name|core
operator|.
name|AggregateCall
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|mapping
operator|.
name|Mappings
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

begin_comment
comment|/**  * Aggregate function that can be split into partial aggregates.  *  *<p>For example, {@code COUNT(x)} can be split into {@code COUNT(x)} on  * subsets followed by {@code SUM} to combine those counts.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlSplittableAggFunction
block|{
name|AggregateCall
name|split
parameter_list|(
name|AggregateCall
name|aggregateCall
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
function_decl|;
comment|/** Called to generate an aggregate for the other side of the join    * than the side aggregate call's arguments come from. Returns null if    * no aggregate is required. */
name|AggregateCall
name|other
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|AggregateCall
name|e
parameter_list|)
function_decl|;
comment|/** Generates an aggregate call to merge sub-totals.    *    *<p>Most implementations will add a single aggregate call to    * {@code aggCalls}, and return a {@link RexInputRef} that points to it.    *    * @param rexBuilder Rex builder    * @param extra Place to define extra input expressions    * @param offset Offset due to grouping columns (and indicator columns if    *     applicable)    * @param inputRowType Input row type    * @param aggregateCall Source aggregate call    * @param leftSubTotal Ordinal of the sub-total coming from the left side of    *     the join, or -1 if there is no such sub-total    * @param rightSubTotal Ordinal of the sub-total coming from the right side    *     of the join, or -1 if there is no such sub-total    *    * @return Aggregate call    */
name|AggregateCall
name|topSplit
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Registry
argument_list|<
name|RexNode
argument_list|>
name|extra
parameter_list|,
name|int
name|offset
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|,
name|int
name|leftSubTotal
parameter_list|,
name|int
name|rightSubTotal
parameter_list|)
function_decl|;
comment|/** Generates an expression for the value of the aggregate function when    * applied to a single row.    *    *<p>For example, if there is one row:    *<ul>    *<li>{@code SUM(x)} is {@code x}    *<li>{@code MIN(x)} is {@code x}    *<li>{@code MAX(x)} is {@code x}    *<li>{@code COUNT(x)} is {@code CASE WHEN x IS NOT NULL THEN 1 ELSE 0 END 1}    *   which can be simplified to {@code 1} if {@code x} is never null    *<li>{@code COUNT(*)} is 1    *</ul>    *    * @param rexBuilder Rex builder    * @param inputRowType Input row type    * @param aggregateCall Aggregate call    *    * @return Expression for single row    */
name|RexNode
name|singleton
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|)
function_decl|;
comment|/** Collection in which one can register an element. Registering may return    * a reference to an existing element.    *    * @param<E> element type */
interface|interface
name|Registry
parameter_list|<
name|E
parameter_list|>
block|{
name|int
name|register
parameter_list|(
name|E
name|e
parameter_list|)
function_decl|;
block|}
comment|/** Splitting strategy for {@code COUNT}.    *    *<p>COUNT splits into itself followed by SUM. (Actually    * SUM0, because the total needs to be 0, not null, if there are 0 rows.)    * This rule works for any number of arguments to COUNT, including COUNT(*).    */
class|class
name|CountSplitter
implements|implements
name|SqlSplittableAggFunction
block|{
specifier|public
specifier|static
specifier|final
name|CountSplitter
name|INSTANCE
init|=
operator|new
name|CountSplitter
argument_list|()
decl_stmt|;
specifier|public
name|AggregateCall
name|split
parameter_list|(
name|AggregateCall
name|aggregateCall
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
name|aggregateCall
operator|.
name|transform
argument_list|(
name|mapping
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|other
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|AggregateCall
name|e
parameter_list|)
block|{
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|topSplit
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Registry
argument_list|<
name|RexNode
argument_list|>
name|extra
parameter_list|,
name|int
name|offset
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|,
name|int
name|leftSubTotal
parameter_list|,
name|int
name|rightSubTotal
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|merges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftSubTotal
operator|>=
literal|0
condition|)
block|{
name|merges
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|aggregateCall
operator|.
name|type
argument_list|,
name|leftSubTotal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rightSubTotal
operator|>=
literal|0
condition|)
block|{
name|merges
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|aggregateCall
operator|.
name|type
argument_list|,
name|rightSubTotal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|node
decl_stmt|;
switch|switch
condition|(
name|merges
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
name|node
operator|=
name|merges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|node
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|,
name|merges
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected count "
operator|+
name|merges
argument_list|)
throw|;
block|}
name|int
name|ordinal
init|=
name|extra
operator|.
name|register
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUM0
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ordinal
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|aggregateCall
operator|.
name|type
argument_list|,
name|aggregateCall
operator|.
name|name
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      *      *<p>{@code COUNT(*)}, and {@code COUNT} applied to all NOT NULL arguments,      * become {@code 1}; otherwise      * {@code CASE WHEN arg0 IS NOT NULL THEN 1 ELSE 0 END}.      */
specifier|public
name|RexNode
name|singleton
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|predicates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|arg
range|:
name|aggregateCall
operator|.
name|getArgList
argument_list|()
control|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|arg
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|type
argument_list|,
name|arg
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RexNode
name|predicate
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|predicates
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicate
operator|==
literal|null
condition|)
block|{
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|predicate
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ZERO
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Aggregate function that splits into two applications of itself.    *    *<p>Examples are MIN and MAX. */
class|class
name|SelfSplitter
implements|implements
name|SqlSplittableAggFunction
block|{
specifier|public
specifier|static
specifier|final
name|SelfSplitter
name|INSTANCE
init|=
operator|new
name|SelfSplitter
argument_list|()
decl_stmt|;
specifier|public
name|RexNode
name|singleton
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|)
block|{
specifier|final
name|int
name|arg
init|=
name|aggregateCall
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
name|RelDataTypeField
name|field
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|arg
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|split
parameter_list|(
name|AggregateCall
name|aggregateCall
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
name|aggregateCall
operator|.
name|transform
argument_list|(
name|mapping
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|other
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|AggregateCall
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// no aggregate function required on other side
block|}
specifier|public
name|AggregateCall
name|topSplit
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Registry
argument_list|<
name|RexNode
argument_list|>
name|extra
parameter_list|,
name|int
name|offset
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|,
name|int
name|leftSubTotal
parameter_list|,
name|int
name|rightSubTotal
parameter_list|)
block|{
assert|assert
operator|(
name|leftSubTotal
operator|>=
literal|0
operator|)
operator|!=
operator|(
name|rightSubTotal
operator|>=
literal|0
operator|)
assert|;
specifier|final
name|int
name|arg
init|=
name|leftSubTotal
operator|>=
literal|0
condition|?
name|leftSubTotal
else|:
name|rightSubTotal
decl_stmt|;
return|return
name|aggregateCall
operator|.
name|copy
argument_list|(
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|arg
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
comment|/** Splitting strategy for {@code SUM}. */
class|class
name|SumSplitter
implements|implements
name|SqlSplittableAggFunction
block|{
specifier|public
specifier|static
specifier|final
name|SumSplitter
name|INSTANCE
init|=
operator|new
name|SumSplitter
argument_list|()
decl_stmt|;
specifier|public
name|RexNode
name|singleton
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|)
block|{
specifier|final
name|int
name|arg
init|=
name|aggregateCall
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
name|RelDataTypeField
name|field
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|arg
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|split
parameter_list|(
name|AggregateCall
name|aggregateCall
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
name|aggregateCall
operator|.
name|transform
argument_list|(
name|mapping
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|other
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|AggregateCall
name|e
parameter_list|)
block|{
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
literal|false
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|AggregateCall
name|topSplit
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Registry
argument_list|<
name|RexNode
argument_list|>
name|extra
parameter_list|,
name|int
name|offset
parameter_list|,
name|RelDataType
name|inputRowType
parameter_list|,
name|AggregateCall
name|aggregateCall
parameter_list|,
name|int
name|leftSubTotal
parameter_list|,
name|int
name|rightSubTotal
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|merges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftSubTotal
operator|>=
literal|0
condition|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|fieldList
operator|.
name|get
argument_list|(
name|leftSubTotal
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|merges
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|type
argument_list|,
name|leftSubTotal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rightSubTotal
operator|>=
literal|0
condition|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|fieldList
operator|.
name|get
argument_list|(
name|rightSubTotal
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|merges
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|type
argument_list|,
name|rightSubTotal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|node
decl_stmt|;
switch|switch
condition|(
name|merges
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
name|node
operator|=
name|merges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|node
operator|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|,
name|merges
argument_list|)
expr_stmt|;
name|node
operator|=
name|rexBuilder
operator|.
name|makeAbstractCast
argument_list|(
name|aggregateCall
operator|.
name|type
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected count "
operator|+
name|merges
argument_list|)
throw|;
block|}
name|int
name|ordinal
init|=
name|extra
operator|.
name|register
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
name|AggregateCall
operator|.
name|create
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ordinal
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|aggregateCall
operator|.
name|type
argument_list|,
name|aggregateCall
operator|.
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End SqlSplittableAggFunction.java
end_comment

end_unit

