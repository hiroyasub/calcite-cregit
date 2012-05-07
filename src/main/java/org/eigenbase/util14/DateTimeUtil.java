begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util14
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility functions for datetime types: date, time, timestamp. Refactored from  * SqlParserUtil because they are required by the Jdbc driver. TODO: review  * methods for performance. Due to allocations required, it may be preferable to  * introduce a "formatter" with the required state.  *  * @version $Id$  * @since Sep 28, 2006  */
end_comment

begin_class
specifier|public
class|class
name|DateTimeUtil
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * the SimpleDateFormat string for ISO dates, "yyyy-MM-dd"      */
specifier|public
specifier|static
specifier|final
name|String
name|DateFormatStr
init|=
literal|"yyyy-MM-dd"
decl_stmt|;
comment|/**      * the SimpleDateFormat string for ISO times, "HH:mm:ss"      */
specifier|public
specifier|static
specifier|final
name|String
name|TimeFormatStr
init|=
literal|"HH:mm:ss"
decl_stmt|;
comment|/**      * the SimpleDateFormat string for ISO timestamps, "yyyy-MM-dd HH:mm:ss"      */
specifier|public
specifier|static
specifier|final
name|String
name|TimestampFormatStr
init|=
name|DateFormatStr
operator|+
literal|" "
operator|+
name|TimeFormatStr
decl_stmt|;
comment|/**      * the GMT time zone      */
specifier|public
specifier|static
specifier|final
name|TimeZone
name|gmtZone
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
comment|/**      * the Java default time zone      */
specifier|public
specifier|static
specifier|final
name|TimeZone
name|defaultZone
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/**      * The number of milliseconds in a day.      *      *<p>In the fennel calculator, this is the modulo 'mask' when converting      * TIMESTAMP values to DATE and TIME values.      */
specifier|public
specifier|static
name|long
name|MILLIS_PER_DAY
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Parses a string using {@link SimpleDateFormat} and a given pattern. This      * method parses a string at the specified parse position and if successful,      * updates the parse position to the index after the last character used.      * The parsing is strict and requires months to be less than 12, days to be      * less than 31, etc.      *      * @param s string to be parsed      * @param pattern {@link SimpleDateFormat}  pattern      * @param tz time zone in which to interpret string. Defaults to the Java      * default time zone      * @param pp position to start parsing from      *      * @return a Calendar initialized with the parsed value, or null if parsing      * failed. If returned, the Calendar is configured to the GMT time zone.      *      * @pre pattern != null      */
specifier|private
specifier|static
name|Calendar
name|parseDateFormat
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|pattern
parameter_list|,
name|TimeZone
name|tz
parameter_list|,
name|ParsePosition
name|pp
parameter_list|)
block|{
assert|assert
operator|(
name|pattern
operator|!=
literal|null
operator|)
assert|;
name|SimpleDateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
if|if
condition|(
name|tz
operator|==
literal|null
condition|)
block|{
name|tz
operator|=
name|defaultZone
expr_stmt|;
block|}
name|Calendar
name|ret
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tz
argument_list|)
decl_stmt|;
name|df
operator|.
name|setCalendar
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|df
operator|.
name|setLenient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|Date
name|d
init|=
name|df
operator|.
name|parse
argument_list|(
name|s
argument_list|,
name|pp
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|d
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ret
operator|.
name|setTime
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setTimeZone
argument_list|(
name|gmtZone
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**      * Parses a string using {@link SimpleDateFormat} and a given pattern. The      * entire string must match the pattern specified.      *      * @param s string to be parsed      * @param pattern {@link SimpleDateFormat}  pattern      * @param tz time zone in which to interpret string. Defaults to the Java      * default time zone      *      * @return a Calendar initialized with the parsed value, or null if parsing      * failed. If returned, the Calendar is configured to the GMT time zone.      *      * @pre pattern != null      */
specifier|public
specifier|static
name|Calendar
name|parseDateFormat
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|pattern
parameter_list|,
name|TimeZone
name|tz
parameter_list|)
block|{
assert|assert
operator|(
name|pattern
operator|!=
literal|null
operator|)
assert|;
name|ParsePosition
name|pp
init|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Calendar
name|ret
init|=
name|parseDateFormat
argument_list|(
name|s
argument_list|,
name|pattern
argument_list|,
name|tz
argument_list|,
name|pp
argument_list|)
decl_stmt|;
if|if
condition|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|!=
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// Didn't consume entire string - not good
return|return
literal|null
return|;
block|}
return|return
name|ret
return|;
block|}
comment|/**      * Parses a string using {@link SimpleDateFormat} and a given pattern, and      * if present, parses a fractional seconds component. The fractional seconds      * component must begin with a decimal point ('.') followed by numeric      * digits. The precision is rounded to a maximum of 3 digits of fractional      * seconds precision (to obtain milliseconds).      *      * @param s string to be parsed      * @param pattern {@link SimpleDateFormat}  pattern      * @param tz time zone in which to interpret string. Defaults to the local      * time zone      *      * @return a {@link DateTimeUtil.PrecisionTime PrecisionTime} initialized      * with the parsed value, or null if parsing failed. The PrecisionTime      * contains a GMT Calendar and a precision.      *      * @pre pattern != null      */
specifier|public
specifier|static
name|PrecisionTime
name|parsePrecisionDateTimeLiteral
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|pattern
parameter_list|,
name|TimeZone
name|tz
parameter_list|)
block|{
name|ParsePosition
name|pp
init|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Calendar
name|cal
init|=
name|parseDateFormat
argument_list|(
name|s
argument_list|,
name|pattern
argument_list|,
name|tz
argument_list|,
name|pp
argument_list|)
decl_stmt|;
if|if
condition|(
name|cal
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// Invalid date/time format
block|}
comment|// Note: the Java SimpleDateFormat 'S' treats any number after
comment|// the decimal as milliseconds. That means 12:00:00.9 has 9
comment|// milliseconds and 12:00:00.9999 has 9999 milliseconds.
name|int
name|p
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// Check to see if rest is decimal portion
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|pp
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Skip decimal sign
name|pp
operator|.
name|setIndex
argument_list|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Parse decimal portion
if|if
condition|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|secFraction
init|=
name|s
operator|.
name|substring
argument_list|(
name|pp
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|secFraction
operator|.
name|matches
argument_list|(
literal|"\\d+"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|()
decl_stmt|;
name|Number
name|num
init|=
name|nf
operator|.
name|parse
argument_list|(
name|s
argument_list|,
name|pp
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|num
operator|==
literal|null
operator|)
operator|||
operator|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|!=
name|s
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
comment|// Invalid decimal portion
return|return
literal|null
return|;
block|}
comment|// Determine precision - only support prec 3 or lower
comment|// (milliseconds) Higher precisions are quietly rounded away
name|p
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|3
argument_list|,
name|secFraction
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Calculate milliseconds
name|int
name|ms
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|num
operator|.
name|longValue
argument_list|()
operator|*
name|Math
operator|.
name|pow
argument_list|(
literal|10
argument_list|,
literal|3
operator|-
name|secFraction
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cal
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
name|ms
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
operator|(
name|pp
operator|.
name|getIndex
argument_list|()
operator|==
name|s
operator|.
name|length
argument_list|()
operator|)
assert|;
name|PrecisionTime
name|ret
init|=
operator|new
name|PrecisionTime
argument_list|(
name|cal
argument_list|,
name|p
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
comment|/**      * Gets the active time zone based on a Calendar argument      */
specifier|public
specifier|static
name|TimeZone
name|getTimeZone
parameter_list|(
name|Calendar
name|cal
parameter_list|)
block|{
if|if
condition|(
name|cal
operator|==
literal|null
condition|)
block|{
return|return
name|defaultZone
return|;
block|}
return|return
name|cal
operator|.
name|getTimeZone
argument_list|()
return|;
block|}
comment|/**      * Checks if the date/time format is valid      *      * @param pattern {@link SimpleDateFormat}  pattern      *      * @throws IllegalArgumentException if the given pattern is invalid      */
specifier|public
specifier|static
name|void
name|checkDateFormat
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
operator|new
name|SimpleDateFormat
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new date formatter with Farrago specific options. Farrago      * parsing is strict and does not allow values such as day 0, month 13, etc.      *      * @param format {@link SimpleDateFormat}  pattern      */
specifier|public
specifier|static
name|SimpleDateFormat
name|newDateFormat
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|format
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setLenient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|sdf
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Helper class for {@link DateTimeUtil#parsePrecisionDateTimeLiteral}      */
specifier|public
specifier|static
class|class
name|PrecisionTime
block|{
specifier|private
specifier|final
name|Calendar
name|cal
decl_stmt|;
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
specifier|public
name|PrecisionTime
parameter_list|(
name|Calendar
name|cal
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|this
operator|.
name|cal
operator|=
name|cal
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
block|}
specifier|public
name|Calendar
name|getCalendar
parameter_list|()
block|{
return|return
name|cal
return|;
block|}
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|precision
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End DateTimeUtil.java
end_comment

end_unit

