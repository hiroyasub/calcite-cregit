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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|Strings
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|DateTimeStringUtils
operator|.
name|ymdhms
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|floorMod
import|;
end_import

begin_comment
comment|/**  * Timestamp literal.  *  *<p>Immutable, internally represented as a string (in ISO format),  * and can support unlimited precision (milliseconds, nanoseconds).  */
end_comment

begin_class
specifier|public
class|class
name|TimestampString
implements|implements
name|Comparable
argument_list|<
name|TimestampString
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]"
operator|+
literal|" "
operator|+
literal|"[0-9][0-9]:[0-9][0-9]:[0-9][0-9](\\.[0-9]*[1-9])?"
argument_list|)
decl_stmt|;
comment|/** The allowed format of input strings is slightly more flexible than    * normalized strings. Input strings can have trailing zeros in the fractional    * seconds. */
specifier|private
specifier|static
specifier|final
name|Pattern
name|INPUT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]"
operator|+
literal|" "
operator|+
literal|"[0-9][0-9]:[0-9][0-9]:[0-9][0-9](\\.[0-9]+)?"
argument_list|)
decl_stmt|;
comment|/** The Unix epoch. */
specifier|public
specifier|static
specifier|final
name|TimestampString
name|EPOCH
init|=
operator|new
name|TimestampString
argument_list|(
literal|1970
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|v
decl_stmt|;
comment|/** Creates a TimeString. */
specifier|public
name|TimestampString
parameter_list|(
name|String
name|v
parameter_list|)
block|{
name|this
operator|.
name|v
operator|=
name|normalize
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a TimestampString for year, month, day, hour, minute, second,    *  millisecond values. */
specifier|public
name|TimestampString
parameter_list|(
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|,
name|int
name|h
parameter_list|,
name|int
name|m
parameter_list|,
name|int
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|ymdhms
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|,
name|h
argument_list|,
name|m
argument_list|,
name|s
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the fraction field of a {@code TimestampString} to a given number    * of milliseconds. Nukes the value set via {@link #withNanos}.    *    *<p>For example,    * {@code new TimestampString(1970, 1, 1, 2, 3, 4).withMillis(56)}    * yields {@code TIMESTAMP '1970-01-01 02:03:04.056'}. */
specifier|public
name|TimestampString
name|withMillis
parameter_list|(
name|int
name|millis
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|millis
operator|>=
literal|0
operator|&&
name|millis
operator|<
literal|1000
argument_list|)
expr_stmt|;
return|return
name|withFraction
argument_list|(
name|DateTimeStringUtils
operator|.
name|pad
argument_list|(
literal|3
argument_list|,
name|millis
argument_list|)
argument_list|)
return|;
block|}
comment|/** Sets the fraction field of a {@code TimestampString} to a given number    * of nanoseconds. Nukes the value set via {@link #withMillis(int)}.    *    *<p>For example,    * {@code new TimestampString(1970, 1, 1, 2, 3, 4).withNanos(56789)}    * yields {@code TIMESTAMP '1970-01-01 02:03:04.000056789'}. */
specifier|public
name|TimestampString
name|withNanos
parameter_list|(
name|int
name|nanos
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|nanos
operator|>=
literal|0
operator|&&
name|nanos
operator|<
literal|1000000000
argument_list|)
expr_stmt|;
return|return
name|withFraction
argument_list|(
name|DateTimeStringUtils
operator|.
name|pad
argument_list|(
literal|9
argument_list|,
name|nanos
argument_list|)
argument_list|)
return|;
block|}
comment|/** Sets the fraction field of a {@code TimestampString}.    * The precision is determined by the number of leading zeros.    * Trailing zeros are stripped.    *    *<p>For example,    * {@code new TimestampString(1970, 1, 1, 2, 3, 4).withFraction("00506000")}    * yields {@code TIMESTAMP '1970-01-01 02:03:04.00506'}. */
specifier|public
name|TimestampString
name|withFraction
parameter_list|(
name|String
name|fraction
parameter_list|)
block|{
name|String
name|v
init|=
name|this
operator|.
name|v
decl_stmt|;
name|int
name|i
init|=
name|v
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|fraction
operator|.
name|endsWith
argument_list|(
literal|"0"
argument_list|)
condition|)
block|{
name|fraction
operator|=
name|fraction
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fraction
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fraction
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|v
operator|=
name|v
operator|+
literal|"."
operator|+
name|fraction
expr_stmt|;
block|}
return|return
operator|new
name|TimestampString
argument_list|(
name|v
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|v
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|INPUT_PATTERN
operator|.
name|matcher
argument_list|(
name|v
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
comment|// Remove trailing zeros in the fractional seconds
if|if
condition|(
name|v
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|>=
literal|0
condition|)
block|{
while|while
condition|(
name|v
operator|.
name|endsWith
argument_list|(
literal|"0"
argument_list|)
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|checkArgument
argument_list|(
name|PATTERN
operator|.
name|matcher
argument_list|(
name|v
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
return|return
name|v
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
name|v
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
comment|// The value is in canonical form (no trailing zeros).
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|TimestampString
operator|&&
operator|(
operator|(
name|TimestampString
operator|)
name|o
operator|)
operator|.
name|v
operator|.
name|equals
argument_list|(
name|v
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
name|v
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|TimestampString
name|o
parameter_list|)
block|{
return|return
name|v
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|v
argument_list|)
return|;
block|}
comment|/** Creates a TimestampString from a Calendar. */
specifier|public
specifier|static
name|TimestampString
name|fromCalendarFields
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
block|{
return|return
operator|new
name|TimestampString
argument_list|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
argument_list|)
operator|.
name|withMillis
argument_list|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns this value rounded to {@code precision} decimal digits after the    * point.    *    *<p>Uses rounding mode {@link java.math.RoundingMode#DOWN}. */
specifier|public
name|TimestampString
name|round
parameter_list|(
name|int
name|precision
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|precision
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|int
name|targetLength
init|=
literal|20
operator|+
name|precision
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|<=
name|targetLength
condition|)
block|{
return|return
name|this
return|;
block|}
name|String
name|v
init|=
name|this
operator|.
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|targetLength
argument_list|)
decl_stmt|;
while|while
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>=
literal|20
operator|&&
operator|(
name|v
operator|.
name|endsWith
argument_list|(
literal|"0"
argument_list|)
operator|||
name|v
operator|.
name|endsWith
argument_list|(
literal|"."
argument_list|)
operator|)
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TimestampString
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** Returns the number of milliseconds since the epoch. */
specifier|public
name|long
name|getMillisSinceEpoch
parameter_list|()
block|{
specifier|final
name|int
name|year
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|month
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|day
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|8
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|11
argument_list|,
literal|13
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|m
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|14
argument_list|,
literal|16
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|s
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|17
argument_list|,
literal|19
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ms
init|=
name|getMillisInSecond
argument_list|()
decl_stmt|;
specifier|final
name|int
name|d
init|=
name|DateTimeUtils
operator|.
name|ymdToUnixDate
argument_list|(
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|)
decl_stmt|;
return|return
name|d
operator|*
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
operator|+
name|h
operator|*
name|DateTimeUtils
operator|.
name|MILLIS_PER_HOUR
operator|+
name|m
operator|*
name|DateTimeUtils
operator|.
name|MILLIS_PER_MINUTE
operator|+
name|s
operator|*
name|DateTimeUtils
operator|.
name|MILLIS_PER_SECOND
operator|+
name|ms
return|;
block|}
specifier|private
name|int
name|getMillisInSecond
parameter_list|()
block|{
switch|switch
condition|(
name|v
operator|.
name|length
argument_list|()
condition|)
block|{
case|case
literal|19
case|:
comment|// "1999-12-31 12:34:56"
return|return
literal|0
return|;
case|case
literal|21
case|:
comment|// "1999-12-31 12:34:56.7"
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|20
argument_list|)
argument_list|)
operator|*
literal|100
return|;
case|case
literal|22
case|:
comment|// "1999-12-31 12:34:56.78"
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|20
argument_list|)
argument_list|)
operator|*
literal|10
return|;
case|case
literal|23
case|:
comment|// "1999-12-31 12:34:56.789"
default|default:
comment|// "1999-12-31 12:34:56.789123456"
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|20
argument_list|,
literal|23
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Creates a TimestampString that is a given number of milliseconds since    * the epoch. */
specifier|public
specifier|static
name|TimestampString
name|fromMillisSinceEpoch
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
return|return
operator|new
name|TimestampString
argument_list|(
name|DateTimeUtils
operator|.
name|unixTimestampToString
argument_list|(
name|millis
argument_list|)
argument_list|)
operator|.
name|withMillis
argument_list|(
operator|(
name|int
operator|)
name|floorMod
argument_list|(
name|millis
argument_list|,
literal|1000L
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
return|return
name|Util
operator|.
name|calendar
argument_list|(
name|getMillisSinceEpoch
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts this TimestampString to a string, truncated or padded with    * zeros to a given precision. */
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|precision
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|precision
operator|>=
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|p
init|=
name|precision
argument_list|()
decl_stmt|;
if|if
condition|(
name|precision
operator|<
name|p
condition|)
block|{
return|return
name|round
argument_list|(
name|precision
argument_list|)
operator|.
name|toString
argument_list|(
name|precision
argument_list|)
return|;
block|}
if|if
condition|(
name|precision
operator|>
name|p
condition|)
block|{
name|String
name|s
init|=
name|v
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|0
condition|)
block|{
name|s
operator|+=
literal|"."
expr_stmt|;
block|}
return|return
name|s
operator|+
name|Strings
operator|.
name|repeat
argument_list|(
literal|"0"
argument_list|,
name|precision
operator|-
name|p
argument_list|)
return|;
block|}
return|return
name|v
return|;
block|}
specifier|private
name|int
name|precision
parameter_list|()
block|{
return|return
name|v
operator|.
name|length
argument_list|()
operator|<
literal|20
condition|?
literal|0
else|:
operator|(
name|v
operator|.
name|length
argument_list|()
operator|-
literal|20
operator|)
return|;
block|}
block|}
end_class

end_unit

