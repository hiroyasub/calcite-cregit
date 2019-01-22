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
name|sql
operator|.
name|validate
operator|.
name|SqlMonotonicity
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
comment|/**  * Definition of the ordering of one field of a {@link RelNode} whose  * output is to be sorted.  *  * @see RelCollation  */
end_comment

begin_class
specifier|public
class|class
name|RelFieldCollation
block|{
comment|/** Utility method that compares values taking into account null    * direction. */
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|Comparable
name|c1
parameter_list|,
name|Comparable
name|c2
parameter_list|,
name|int
name|nullComparison
parameter_list|)
block|{
if|if
condition|(
name|c1
operator|==
name|c2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|else if
condition|(
name|c1
operator|==
literal|null
condition|)
block|{
return|return
name|nullComparison
return|;
block|}
if|else if
condition|(
name|c2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
name|nullComparison
return|;
block|}
else|else
block|{
comment|//noinspection unchecked
return|return
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
return|;
block|}
block|}
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Direction that a field is ordered in.    */
specifier|public
enum|enum
name|Direction
block|{
comment|/**      * Ascending direction: A value is always followed by a greater or equal      * value.      */
name|ASCENDING
argument_list|(
literal|"ASC"
argument_list|)
block|,
comment|/**      * Strictly ascending direction: A value is always followed by a greater      * value.      */
name|STRICTLY_ASCENDING
argument_list|(
literal|"SASC"
argument_list|)
block|,
comment|/**      * Descending direction: A value is always followed by a lesser or equal      * value.      */
name|DESCENDING
argument_list|(
literal|"DESC"
argument_list|)
block|,
comment|/**      * Strictly descending direction: A value is always followed by a lesser      * value.      */
name|STRICTLY_DESCENDING
argument_list|(
literal|"SDESC"
argument_list|)
block|,
comment|/**      * Clustered direction: Values occur in no particular order, and the      * same value may occur in contiguous groups, but never occurs after      * that. This sort order tends to occur when values are ordered      * according to a hash-key.      */
name|CLUSTERED
argument_list|(
literal|"CLU"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|shortString
decl_stmt|;
name|Direction
parameter_list|(
name|String
name|shortString
parameter_list|)
block|{
name|this
operator|.
name|shortString
operator|=
name|shortString
expr_stmt|;
block|}
comment|/** Converts the direction to a      * {@link org.apache.calcite.sql.validate.SqlMonotonicity}. */
specifier|public
name|SqlMonotonicity
name|monotonicity
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|ASCENDING
case|:
return|return
name|SqlMonotonicity
operator|.
name|INCREASING
return|;
case|case
name|STRICTLY_ASCENDING
case|:
return|return
name|SqlMonotonicity
operator|.
name|STRICTLY_INCREASING
return|;
case|case
name|DESCENDING
case|:
return|return
name|SqlMonotonicity
operator|.
name|DECREASING
return|;
case|case
name|STRICTLY_DESCENDING
case|:
return|return
name|SqlMonotonicity
operator|.
name|STRICTLY_DECREASING
return|;
case|case
name|CLUSTERED
case|:
return|return
name|SqlMonotonicity
operator|.
name|MONOTONIC
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
comment|/** Converts a {@link SqlMonotonicity} to a direction. */
specifier|public
specifier|static
name|Direction
name|of
parameter_list|(
name|SqlMonotonicity
name|monotonicity
parameter_list|)
block|{
switch|switch
condition|(
name|monotonicity
condition|)
block|{
case|case
name|INCREASING
case|:
return|return
name|ASCENDING
return|;
case|case
name|DECREASING
case|:
return|return
name|DESCENDING
return|;
case|case
name|STRICTLY_INCREASING
case|:
return|return
name|STRICTLY_ASCENDING
return|;
case|case
name|STRICTLY_DECREASING
case|:
return|return
name|STRICTLY_DESCENDING
return|;
case|case
name|MONOTONIC
case|:
return|return
name|CLUSTERED
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown: "
operator|+
name|monotonicity
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the null direction if not specified. Consistent with Oracle,      * NULLS are sorted as if they were positive infinity. */
specifier|public
name|NullDirection
name|defaultNullDirection
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|ASCENDING
case|:
case|case
name|STRICTLY_ASCENDING
case|:
return|return
name|NullDirection
operator|.
name|LAST
return|;
case|case
name|DESCENDING
case|:
case|case
name|STRICTLY_DESCENDING
case|:
return|return
name|NullDirection
operator|.
name|FIRST
return|;
default|default:
return|return
name|NullDirection
operator|.
name|UNSPECIFIED
return|;
block|}
block|}
comment|/** Returns whether this is {@link #DESCENDING} or      * {@link #STRICTLY_DESCENDING}. */
specifier|public
name|boolean
name|isDescending
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|DESCENDING
case|:
case|case
name|STRICTLY_DESCENDING
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Ordering of nulls.    */
specifier|public
enum|enum
name|NullDirection
block|{
name|FIRST
argument_list|(
operator|-
literal|1
argument_list|)
block|,
name|LAST
argument_list|(
literal|1
argument_list|)
block|,
name|UNSPECIFIED
argument_list|(
literal|1
argument_list|)
block|;
specifier|public
specifier|final
name|int
name|nullComparison
decl_stmt|;
name|NullDirection
parameter_list|(
name|int
name|nullComparison
parameter_list|)
block|{
name|this
operator|.
name|nullComparison
operator|=
name|nullComparison
expr_stmt|;
block|}
block|}
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * 0-based index of field being sorted.    */
specifier|private
specifier|final
name|int
name|fieldIndex
decl_stmt|;
comment|/**    * Direction of sorting.    */
specifier|public
specifier|final
name|Direction
name|direction
decl_stmt|;
comment|/**    * Direction of sorting of nulls.    */
specifier|public
specifier|final
name|NullDirection
name|nullDirection
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an ascending field collation.    */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
name|this
argument_list|(
name|fieldIndex
argument_list|,
name|Direction
operator|.
name|ASCENDING
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a field collation with unspecified null direction.    */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|,
name|Direction
name|direction
parameter_list|)
block|{
name|this
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|direction
operator|.
name|defaultNullDirection
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a field collation.    */
specifier|public
name|RelFieldCollation
parameter_list|(
name|int
name|fieldIndex
parameter_list|,
name|Direction
name|direction
parameter_list|,
name|NullDirection
name|nullDirection
parameter_list|)
block|{
name|this
operator|.
name|fieldIndex
operator|=
name|fieldIndex
expr_stmt|;
name|this
operator|.
name|direction
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|direction
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullDirection
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|nullDirection
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a copy of this RelFieldCollation against a different field.    */
specifier|public
name|RelFieldCollation
name|withFieldIndex
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
return|return
name|this
operator|.
name|fieldIndex
operator|==
name|fieldIndex
condition|?
name|this
else|:
operator|new
name|RelFieldCollation
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|nullDirection
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RelFieldCollation
name|copy
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|withFieldIndex
argument_list|(
name|target
argument_list|)
return|;
block|}
comment|/** Creates a copy of this RelFieldCollation with a different direction. */
specifier|public
name|RelFieldCollation
name|withDirection
parameter_list|(
name|Direction
name|direction
parameter_list|)
block|{
return|return
name|this
operator|.
name|direction
operator|==
name|direction
condition|?
name|this
else|:
operator|new
name|RelFieldCollation
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|nullDirection
argument_list|)
return|;
block|}
comment|/** Creates a copy of this RelFieldCollation with a different null    * direction. */
specifier|public
name|RelFieldCollation
name|withNullDirection
parameter_list|(
name|NullDirection
name|nullDirection
parameter_list|)
block|{
return|return
name|this
operator|.
name|nullDirection
operator|==
name|nullDirection
condition|?
name|this
else|:
operator|new
name|RelFieldCollation
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|nullDirection
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of this RelFieldCollation with the field index shifted    * {@code offset} to the right.    */
specifier|public
name|RelFieldCollation
name|shift
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|withFieldIndex
argument_list|(
name|fieldIndex
operator|+
name|offset
argument_list|)
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
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|RelFieldCollation
operator|&&
name|fieldIndex
operator|==
operator|(
operator|(
name|RelFieldCollation
operator|)
name|o
operator|)
operator|.
name|fieldIndex
operator|&&
name|direction
operator|==
operator|(
operator|(
name|RelFieldCollation
operator|)
name|o
operator|)
operator|.
name|direction
operator|&&
name|nullDirection
operator|==
operator|(
operator|(
name|RelFieldCollation
operator|)
name|o
operator|)
operator|.
name|nullDirection
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
name|Objects
operator|.
name|hash
argument_list|(
name|fieldIndex
argument_list|,
name|direction
argument_list|,
name|nullDirection
argument_list|)
return|;
block|}
specifier|public
name|int
name|getFieldIndex
parameter_list|()
block|{
return|return
name|fieldIndex
return|;
block|}
specifier|public
name|RelFieldCollation
operator|.
name|Direction
name|getDirection
parameter_list|()
block|{
return|return
name|direction
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|direction
operator|==
name|Direction
operator|.
name|ASCENDING
operator|&&
name|nullDirection
operator|==
name|direction
operator|.
name|defaultNullDirection
argument_list|()
condition|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|fieldIndex
argument_list|)
return|;
block|}
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fieldIndex
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|direction
operator|.
name|shortString
argument_list|)
expr_stmt|;
if|if
condition|(
name|nullDirection
operator|!=
name|direction
operator|.
name|defaultNullDirection
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|nullDirection
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|shortString
parameter_list|()
block|{
if|if
condition|(
name|nullDirection
operator|==
name|direction
operator|.
name|defaultNullDirection
argument_list|()
condition|)
block|{
return|return
name|direction
operator|.
name|shortString
return|;
block|}
switch|switch
condition|(
name|nullDirection
condition|)
block|{
case|case
name|FIRST
case|:
return|return
name|direction
operator|.
name|shortString
operator|+
literal|"-nulls-first"
return|;
case|case
name|LAST
case|:
return|return
name|direction
operator|.
name|shortString
operator|+
literal|"-nulls-last"
return|;
default|default:
return|return
name|direction
operator|.
name|shortString
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelFieldCollation.java
end_comment

end_unit

