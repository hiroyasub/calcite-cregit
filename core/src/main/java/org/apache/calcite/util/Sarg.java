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
name|boolean
name|containsNull
decl_stmt|;
specifier|public
specifier|final
name|int
name|pointCount
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
name|boolean
name|containsNull
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|containsNull
operator|=
name|containsNull
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
name|containsNull
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>Produces a similar result to {@link RangeSet}, but adds ", null"    * if nulls are matched, and simplifies point ranges. For example,    * the Sarg that allows the range set    *    *<blockquote>{@code [[7&#x2025;7], [9&#x2025;9],    * (10&#x2025;+&infin;)]}</blockquote>    *    * and also null is printed as    *    *<blockquote>{@code Sarg[7, 9, (10&#x2025;+&infin;), null]}</blockquote>    */
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
if|if
condition|(
name|containsNull
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", null"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
return|;
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
operator|(
name|containsNull
condition|?
literal|2
else|:
literal|3
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
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
operator|&&
name|containsNull
operator|==
operator|(
operator|(
name|Sarg
operator|)
name|o
operator|)
operator|.
name|containsNull
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
block|}
end_class

end_unit

