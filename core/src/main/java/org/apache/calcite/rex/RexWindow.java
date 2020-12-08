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
name|base
operator|.
name|Preconditions
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
name|List
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

begin_comment
comment|/**  * Specification of the window of rows over which a {@link RexOver} windowed  * aggregate is evaluated.  *  *<p>Treat it as immutable!  */
end_comment

begin_class
specifier|public
class|class
name|RexWindow
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexFieldCollation
argument_list|>
name|orderKeys
decl_stmt|;
specifier|private
specifier|final
name|RexWindowBound
name|lowerBound
decl_stmt|;
specifier|private
specifier|final
name|RexWindowBound
name|upperBound
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isRows
decl_stmt|;
specifier|private
specifier|final
name|String
name|digest
decl_stmt|;
specifier|public
specifier|final
name|int
name|nodeCount
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a window.    *    *<p>If you need to create a window from outside this package, use    * {@link RexBuilder#makeOver}.    *    *<p>If {@code orderKeys} is empty the bracket will usually be    * "BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING".    *    *<p>The digest assumes 'default' brackets, and does not print brackets or    * bounds that are the default.    *    *<p>If {@code orderKeys} is empty, assumes the bracket is "RANGE BETWEEN    * UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING" and does not print the    * bracket.    *    *<li>If {@code orderKeys} is not empty, the default top is "CURRENT ROW".    * The default bracket is "RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW",    * which will be printed as blank.    * "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW" is different, and is    * printed as "ROWS UNBOUNDED PRECEDING".    * "ROWS BETWEEN 5 PRECEDING AND CURRENT ROW" is printed as    * "ROWS 5 PRECEDING".    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"method.invocation.invalid"
argument_list|)
name|RexWindow
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|partitionKeys
parameter_list|,
name|List
argument_list|<
name|RexFieldCollation
argument_list|>
name|orderKeys
parameter_list|,
name|RexWindowBound
name|lowerBound
parameter_list|,
name|RexWindowBound
name|upperBound
parameter_list|,
name|boolean
name|isRows
parameter_list|)
block|{
name|this
operator|.
name|partitionKeys
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|partitionKeys
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|orderKeys
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowerBound
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|lowerBound
argument_list|,
literal|"lowerBound"
argument_list|)
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|upperBound
argument_list|,
literal|"upperBound"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isRows
operator|=
name|isRows
expr_stmt|;
name|this
operator|.
name|nodeCount
operator|=
name|computeCodeCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|computeDigest
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
operator|(
name|lowerBound
operator|.
name|isUnbounded
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isPreceding
argument_list|()
operator|&&
name|upperBound
operator|.
name|isUnbounded
argument_list|()
operator|&&
name|upperBound
operator|.
name|isFollowing
argument_list|()
operator|&&
name|isRows
operator|)
argument_list|,
literal|"use RANGE for unbounded, not ROWS"
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|digest
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
name|digest
operator|.
name|hashCode
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
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|instanceof
name|RexWindow
condition|)
block|{
name|RexWindow
name|window
init|=
operator|(
name|RexWindow
operator|)
name|that
decl_stmt|;
return|return
name|digest
operator|.
name|equals
argument_list|(
name|window
operator|.
name|digest
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|computeDigest
parameter_list|()
block|{
return|return
name|appendDigest_
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
name|StringBuilder
name|appendDigest
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|allowFraming
parameter_list|)
block|{
if|if
condition|(
name|allowFraming
condition|)
block|{
comment|// digest was calculated with allowFraming=true; reuse it
return|return
name|sb
operator|.
name|append
argument_list|(
name|digest
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|appendDigest_
argument_list|(
name|sb
argument_list|,
name|allowFraming
argument_list|)
return|;
block|}
block|}
specifier|private
name|StringBuilder
name|appendDigest_
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|allowFraming
parameter_list|)
block|{
specifier|final
name|int
name|initialLength
init|=
name|sb
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|partitionKeys
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"PARTITION BY "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|partitionKeys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
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
name|sb
operator|.
name|append
argument_list|(
name|partitionKeys
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|orderKeys
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|>
name|initialLength
condition|?
literal|" ORDER BY "
else|:
literal|"ORDER BY "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderKeys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
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
name|sb
operator|.
name|append
argument_list|(
name|orderKeys
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// There are 3 reasons to skip the ROWS/RANGE clause.
comment|// 1. If this window is being used with a RANK-style function that does not
comment|//    allow framing, or
comment|// 2. If there is no ORDER BY (in which case a frame is invalid), or
comment|// 3. If the ROWS/RANGE clause is the default, "RANGE BETWEEN UNBOUNDED
comment|//    PRECEDING AND CURRENT ROW"
if|if
condition|(
operator|!
name|allowFraming
comment|// 1
operator|||
name|orderKeys
operator|.
name|isEmpty
argument_list|()
comment|// 2
operator|||
operator|(
name|lowerBound
operator|.
name|isUnbounded
argument_list|()
comment|// 3
operator|&&
name|lowerBound
operator|.
name|isPreceding
argument_list|()
operator|&&
name|upperBound
operator|.
name|isCurrentRow
argument_list|()
operator|&&
operator|!
name|isRows
operator|)
condition|)
block|{
comment|// No ROWS or RANGE clause
block|}
if|else if
condition|(
name|upperBound
operator|.
name|isCurrentRow
argument_list|()
condition|)
block|{
comment|// Per MSSQL: If ROWS/RANGE is specified and<window frame preceding>
comment|// is used for<window frame extent> (short syntax) then this
comment|// specification is used for the window frame boundary starting point and
comment|// CURRENT ROW is used for the boundary ending point. For example
comment|// "ROWS 5 PRECEDING" is equal to "ROWS BETWEEN 5 PRECEDING AND CURRENT
comment|// ROW".
comment|//
comment|// By similar reasoning to (3) above, we print the shorter option if it is
comment|// the default. If the RexWindow is, say, "ROWS BETWEEN 5 PRECEDING AND
comment|// CURRENT ROW", we output "ROWS 5 PRECEDING" because it is equivalent and
comment|// is shorter.
name|sb
operator|.
name|append
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|>
name|initialLength
condition|?
operator|(
name|isRows
condition|?
literal|" ROWS "
else|:
literal|" RANGE "
operator|)
else|:
operator|(
name|isRows
condition|?
literal|"ROWS "
else|:
literal|"RANGE "
operator|)
argument_list|)
operator|.
name|append
argument_list|(
name|lowerBound
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|>
name|initialLength
condition|?
operator|(
name|isRows
condition|?
literal|" ROWS BETWEEN "
else|:
literal|" RANGE BETWEEN "
operator|)
else|:
operator|(
name|isRows
condition|?
literal|"ROWS BETWEEN "
else|:
literal|"RANGE BETWEEN "
operator|)
argument_list|)
operator|.
name|append
argument_list|(
name|lowerBound
argument_list|)
operator|.
name|append
argument_list|(
literal|" AND "
argument_list|)
operator|.
name|append
argument_list|(
name|upperBound
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
specifier|public
name|RexWindowBound
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBound
return|;
block|}
specifier|public
name|RexWindowBound
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
specifier|public
name|boolean
name|isRows
parameter_list|()
block|{
return|return
name|isRows
return|;
block|}
specifier|private
name|int
name|computeCodeCount
parameter_list|()
block|{
return|return
name|RexUtil
operator|.
name|nodeCount
argument_list|(
name|partitionKeys
argument_list|)
operator|+
name|RexUtil
operator|.
name|nodeCount
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|orderKeys
argument_list|)
argument_list|)
operator|+
operator|(
name|lowerBound
operator|==
literal|null
condition|?
literal|0
else|:
name|lowerBound
operator|.
name|nodeCount
argument_list|()
operator|)
operator|+
operator|(
name|upperBound
operator|==
literal|null
condition|?
literal|0
else|:
name|upperBound
operator|.
name|nodeCount
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

