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
name|util
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
name|linq4j
operator|.
name|Ord
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
name|RexUnknownAs
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
name|Iterables
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|BiConsumer
import|;
end_import

begin_comment
comment|/** Set of values (or ranges) that are the target of a search.  *  *<p>The name is derived from<b>S</b>earch<b>arg</b>ument, an ancient  * concept in database implementation; see Access Path Selection in a Relational  * Database Management System&mdash; Selinger et al. 1979 or the  * "<a href="https://blog.acolyer.org/2016/01/04/access-path-selection/">morning  * paper summary</a>.  *  *<p>In RexNode, a Sarg only occur as the right-hand operand in a call to  * {@link SqlStdOperatorTable#SEARCH}, wrapped in a  * {@link org.apache.calcite.rex.RexLiteral}. Lifecycle methods:  *  *<ul>  *<li>{@link org.apache.calcite.rex.RexUtil#expandSearch} removes  *     calls to SEARCH and the included Sarg, converting them to comparisons;  *<li>{@link org.apache.calcite.rex.RexSimplify} converts complex comparisons  *     on the same argument into SEARCH calls with an included Sarg;  *<li>Various {@link org.apache.calcite.tools.RelBuilder} methods,  *     including {@link org.apache.calcite.tools.RelBuilder#in}  *     and {@link org.apache.calcite.tools.RelBuilder#between}  *     call {@link org.apache.calcite.rex.RexBuilder}  *     methods {@link org.apache.calcite.rex.RexBuilder#makeIn}  *     and {@link org.apache.calcite.rex.RexBuilder#makeBetween}  *     that create Sarg instances directly;  *<li>{@link org.apache.calcite.rel.rel2sql.SqlImplementor} converts  *     {@link org.apache.calcite.rex.RexCall}s  *     to SEARCH into {@link org.apache.calcite.sql.SqlNode} AST expressions  *     such as comparisons, {@code BETWEEN} and {@code IN}.  *</ul>  *  * @param<C> Value type  *  * @see SqlStdOperatorTable#SEARCH  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"BetaApi"
block|,
literal|"type.argument.type.incompatible"
block|,
literal|"UnstableApiUsage"
block|}
argument_list|)
specifier|public
class|class
name|Sarg
parameter_list|<
name|C
extends|extends
name|Comparable
parameter_list|<
name|C
parameter_list|>
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|Sarg
argument_list|<
name|C
argument_list|>
argument_list|>
block|{
specifier|public
specifier|final
name|RangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
decl_stmt|;
specifier|public
specifier|final
name|RexUnknownAs
name|nullAs
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 1.28
specifier|public
specifier|final
name|boolean
name|containsNull
decl_stmt|;
specifier|public
specifier|final
name|int
name|pointCount
decl_stmt|;
comment|/** Returns FALSE for all null and not-null values.    *    *<p>{@code SEARCH(x, FALSE)} is equivalent to {@code FALSE}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|FALSE
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|,
literal|"Sarg[FALSE]"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|/** Returns TRUE for all not-null values, FALSE for null.    *    *<p>{@code SEARCH(x, IS_NOT_NULL)} is equivalent to    * {@code x IS NOT NULL}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|IS_NOT_NULL
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
operator|.
name|complement
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|,
literal|"Sarg[IS NOT NULL]"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|/** Returns FALSE for all not-null values, TRUE for null.    *    *<p>{@code SEARCH(x, IS_NULL)} is equivalent to {@code x IS NULL}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|IS_NULL
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|TRUE
argument_list|,
literal|"Sarg[IS NULL]"
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|/** Returns TRUE for all null and not-null values.    *    *<p>{@code SEARCH(x, TRUE)} is equivalent to {@code TRUE}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|TRUE
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
operator|.
name|complement
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|TRUE
argument_list|,
literal|"Sarg[TRUE]"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|/** Returns FALSE for all not-null values, UNKNOWN for null.    *    *<p>{@code SEARCH(x, NOT_EQUAL)} is equivalent to {@code x<> x}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|NOT_EQUAL
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|,
literal|"Sarg[<>]"
argument_list|,
literal|6
argument_list|)
decl_stmt|;
comment|/** Returns TRUE for all not-null values, UNKNOWN for null.    *    *<p>{@code SEARCH(x, EQUAL)} is equivalent to {@code x = x}. */
specifier|private
specifier|static
specifier|final
name|SpecialSarg
name|EQUAL
init|=
operator|new
name|SpecialSarg
argument_list|(
name|ImmutableRangeSet
operator|.
name|of
argument_list|()
operator|.
name|complement
argument_list|()
argument_list|,
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|,
literal|"Sarg[=]"
argument_list|,
literal|7
argument_list|)
decl_stmt|;
specifier|private
name|Sarg
parameter_list|(
name|ImmutableRangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
parameter_list|,
name|RexUnknownAs
name|nullAs
parameter_list|)
block|{
name|this
operator|.
name|rangeSet
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rangeSet
argument_list|,
literal|"rangeSet"
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullAs
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|nullAs
argument_list|,
literal|"nullAs"
argument_list|)
expr_stmt|;
name|this
operator|.
name|containsNull
operator|=
name|nullAs
operator|==
name|RexUnknownAs
operator|.
name|TRUE
expr_stmt|;
name|this
operator|.
name|pointCount
operator|=
name|RangeSets
operator|.
name|countPoints
argument_list|(
name|rangeSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|C
argument_list|>
parameter_list|>
name|Sarg
argument_list|<
name|C
argument_list|>
name|of
parameter_list|(
name|boolean
name|containsNull
parameter_list|,
name|RangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|containsNull
condition|?
name|RexUnknownAs
operator|.
name|TRUE
else|:
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|,
name|rangeSet
argument_list|)
return|;
block|}
comment|/** Creates a search argument. */
specifier|public
specifier|static
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|C
argument_list|>
parameter_list|>
name|Sarg
argument_list|<
name|C
argument_list|>
name|of
parameter_list|(
name|RexUnknownAs
name|nullAs
parameter_list|,
name|RangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
parameter_list|)
block|{
if|if
condition|(
name|rangeSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|nullAs
condition|)
block|{
case|case
name|FALSE
case|:
return|return
name|FALSE
return|;
case|case
name|TRUE
case|:
return|return
name|IS_NULL
return|;
default|default:
return|return
name|NOT_EQUAL
return|;
block|}
block|}
if|if
condition|(
name|rangeSet
operator|.
name|equals
argument_list|(
name|RangeSets
operator|.
name|rangeSetAll
argument_list|()
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|nullAs
condition|)
block|{
case|case
name|FALSE
case|:
return|return
name|IS_NOT_NULL
return|;
case|case
name|TRUE
case|:
return|return
name|TRUE
return|;
default|default:
return|return
name|EQUAL
return|;
block|}
block|}
return|return
operator|new
name|Sarg
argument_list|<>
argument_list|(
name|ImmutableRangeSet
operator|.
name|copyOf
argument_list|(
name|rangeSet
argument_list|)
argument_list|,
name|nullAs
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>Produces a similar result to {@link RangeSet},    * but adds "; NULL AS FALSE" or "; NULL AS TRUE" to indicate {@link #nullAs},    * and simplifies point ranges.    *    *<p>For example, the Sarg that allows the range set    *    *<blockquote>{@code [[7..7], [9..9], (10..+â)]}</blockquote>    *    *<p>and also null is printed as    *    *<blockquote>{@code Sarg[7, 9, (10..+â); NULL AS TRUE]}</blockquote>    */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|printTo
argument_list|(
name|sb
argument_list|,
name|StringBuilder
operator|::
name|append
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Prints this Sarg to a StringBuilder, using the given printer to deal    * with each embedded value. */
specifier|public
name|StringBuilder
name|printTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|BiConsumer
argument_list|<
name|StringBuilder
argument_list|,
name|C
argument_list|>
name|valuePrinter
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"Sarg["
argument_list|)
expr_stmt|;
specifier|final
name|RangeSets
operator|.
name|Consumer
argument_list|<
name|C
argument_list|>
name|printer
init|=
name|RangeSets
operator|.
name|printer
argument_list|(
name|sb
argument_list|,
name|valuePrinter
argument_list|)
decl_stmt|;
name|Ord
operator|.
name|forEach
argument_list|(
name|rangeSet
operator|.
name|asRanges
argument_list|()
argument_list|,
parameter_list|(
name|r
parameter_list|,
name|i
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|RangeSets
operator|.
name|forEach
argument_list|(
name|r
argument_list|,
name|printer
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|nullAs
condition|)
block|{
case|case
name|FALSE
case|:
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"; NULL AS FALSE]"
argument_list|)
return|;
case|case
name|TRUE
case|:
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"; NULL AS TRUE]"
argument_list|)
return|;
case|case
name|UNKNOWN
case|:
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Sarg
argument_list|<
name|C
argument_list|>
name|o
parameter_list|)
block|{
return|return
name|RangeSets
operator|.
name|compare
argument_list|(
name|rangeSet
argument_list|,
name|o
operator|.
name|rangeSet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|RangeSets
operator|.
name|hashCode
argument_list|(
name|rangeSet
argument_list|)
operator|*
literal|31
operator|+
name|nullAs
operator|.
name|ordinal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|Sarg
operator|&&
name|nullAs
operator|==
operator|(
operator|(
name|Sarg
operator|)
name|o
operator|)
operator|.
name|nullAs
operator|&&
name|rangeSet
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Sarg
operator|)
name|o
operator|)
operator|.
name|rangeSet
argument_list|)
return|;
block|}
comment|/** Returns whether this Sarg includes all values (including or not including    * null). */
specifier|public
name|boolean
name|isAll
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns whether this Sarg includes no values (including or not including    * null). */
specifier|public
name|boolean
name|isNone
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns whether this Sarg is a collection of 1 or more points (and perhaps    * an {@code IS NULL} if {@link #containsNull}).    *    *<p>Such sargs could be translated as {@code ref = value}    * or {@code ref IN (value1, ...)}. */
specifier|public
name|boolean
name|isPoints
parameter_list|()
block|{
return|return
name|pointCount
operator|==
name|rangeSet
operator|.
name|asRanges
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Returns whether this Sarg, when negated, is a collection of 1 or more    * points (and perhaps an {@code IS NULL} if {@link #containsNull}).    *    *<p>Such sargs could be translated as {@code ref<> value}    * or {@code ref NOT IN (value1, ...)}. */
specifier|public
name|boolean
name|isComplementedPoints
parameter_list|()
block|{
return|return
name|rangeSet
operator|.
name|span
argument_list|()
operator|.
name|encloses
argument_list|(
name|Range
operator|.
name|all
argument_list|()
argument_list|)
operator|&&
operator|!
name|rangeSet
operator|.
name|equals
argument_list|(
name|RangeSets
operator|.
name|rangeSetAll
argument_list|()
argument_list|)
operator|&&
name|rangeSet
operator|.
name|complement
argument_list|()
operator|.
name|asRanges
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|RangeSets
operator|::
name|isPoint
argument_list|)
return|;
block|}
comment|/** Returns a measure of the complexity of this expression.    *    *<p>It is basically the number of values that need to be checked against    * (including NULL).    *    *<p>Examples:    *<ul>    *<li>{@code x = 1}, {@code x<> 1}, {@code x> 1} have complexity 1    *<li>{@code x> 1 or x is null} has complexity 2    *<li>{@code x in (2, 4, 6) or x> 20} has complexity 4    *<li>{@code x between 3 and 8 or x between 10 and 20} has complexity 2    *</ul>    */
specifier|public
name|int
name|complexity
parameter_list|()
block|{
name|int
name|complexity
decl_stmt|;
if|if
condition|(
name|rangeSet
operator|.
name|asRanges
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|rangeSet
operator|.
name|complement
argument_list|()
operator|.
name|asRanges
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|RangeSets
operator|.
name|isPoint
argument_list|(
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|rangeSet
operator|.
name|complement
argument_list|()
operator|.
name|asRanges
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
comment|// The complement of a point is a range set with two elements.
comment|// For example, "x<> 1" is "[(-inf, 1), (1, inf)]".
comment|// We want this to have complexity 1.
name|complexity
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|complexity
operator|=
name|rangeSet
operator|.
name|asRanges
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nullAs
operator|==
name|RexUnknownAs
operator|.
name|TRUE
condition|)
block|{
operator|++
name|complexity
expr_stmt|;
block|}
return|return
name|complexity
return|;
block|}
comment|/** Returns a Sarg that matches a value if and only this Sarg does not. */
specifier|public
name|Sarg
name|negate
parameter_list|()
block|{
return|return
name|Sarg
operator|.
name|of
argument_list|(
name|nullAs
operator|.
name|negate
argument_list|()
argument_list|,
name|rangeSet
operator|.
name|complement
argument_list|()
argument_list|)
return|;
block|}
comment|/** Sarg whose range is all or none.    *    *<p>There are only 6 instances: {all, none} * {true, false, unknown}.    *    * @param<C> Value type */
specifier|private
specifier|static
class|class
name|SpecialSarg
parameter_list|<
name|C
extends|extends
name|Comparable
parameter_list|<
name|C
parameter_list|>
parameter_list|>
extends|extends
name|Sarg
argument_list|<
name|C
argument_list|>
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|int
name|ordinal
decl_stmt|;
name|SpecialSarg
parameter_list|(
name|ImmutableRangeSet
argument_list|<
name|C
argument_list|>
name|rangeSet
parameter_list|,
name|RexUnknownAs
name|nullAs
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|super
argument_list|(
name|rangeSet
argument_list|,
name|nullAs
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
assert|assert
name|rangeSet
operator|.
name|isEmpty
argument_list|()
operator|==
operator|(
operator|(
name|ordinal
operator|&
literal|1
operator|)
operator|==
literal|0
operator|)
assert|;
assert|assert
name|rangeSet
operator|.
name|equals
argument_list|(
name|RangeSets
operator|.
name|rangeSetAll
argument_list|()
argument_list|)
operator|==
operator|(
operator|(
name|ordinal
operator|&
literal|1
operator|)
operator|==
literal|1
operator|)
assert|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|==
name|o
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ordinal
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAll
parameter_list|()
block|{
return|return
operator|(
name|ordinal
operator|&
literal|1
operator|)
operator|==
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNone
parameter_list|()
block|{
return|return
operator|(
name|ordinal
operator|&
literal|1
operator|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|complexity
parameter_list|()
block|{
switch|switch
condition|(
name|ordinal
condition|)
block|{
case|case
literal|2
case|:
comment|// Sarg[FALSE]
return|return
literal|0
return|;
comment|// for backwards compatibility
case|case
literal|5
case|:
comment|// Sarg[TRUE]
return|return
literal|2
return|;
comment|// for backwards compatibility
default|default:
return|return
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|printTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|BiConsumer
argument_list|<
name|StringBuilder
argument_list|,
name|C
argument_list|>
name|valuePrinter
parameter_list|)
block|{
return|return
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

