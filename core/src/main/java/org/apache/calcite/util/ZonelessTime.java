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
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * ZonelessTime is a time value without a time zone.  */
end_comment

begin_class
specifier|public
class|class
name|ZonelessTime
extends|extends
name|ZonelessDatetime
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * SerialVersionUID created with JDK 1.5 serialver tool.    */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|906156904798141861L
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|int
name|precision
decl_stmt|;
specifier|protected
specifier|transient
name|Time
name|tempTime
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a ZonelessTime    */
specifier|public
name|ZonelessTime
parameter_list|()
block|{
name|precision
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Constructs a ZonelessTime with precision.    *    *<p>The precision is the number of digits to the right of the decimal    * point in the seconds value. For example, a<code>TIME(6)</code> has a    * precision to microseconds.    *    * @param precision Number of digits of precision    */
specifier|public
name|ZonelessTime
parameter_list|(
name|int
name|precision
parameter_list|)
block|{
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// override ZonelessDatetime
specifier|public
name|void
name|setZonelessTime
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|super
operator|.
name|setZonelessTime
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|clearDate
argument_list|()
expr_stmt|;
block|}
comment|// override ZonelessDatetime
specifier|public
name|void
name|setZonedTime
parameter_list|(
name|long
name|value
parameter_list|,
name|TimeZone
name|zone
parameter_list|)
block|{
name|super
operator|.
name|setZonedTime
argument_list|(
name|value
argument_list|,
name|zone
argument_list|)
expr_stmt|;
name|clearDate
argument_list|()
expr_stmt|;
block|}
comment|// implement ZonelessDatetime
specifier|public
name|Object
name|toJdbcObject
parameter_list|()
block|{
return|return
operator|new
name|Time
argument_list|(
name|getJdbcTime
argument_list|(
name|DateTimeUtil
operator|.
name|DEFAULT_ZONE
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Override ZonelessDatetime.    *    *<p>NOTE: the returned timestamp is based on the current date of the    * specified time zone, rather than the context variable for current_date,    * as specified by the SQL standard.    */
specifier|public
name|long
name|getJdbcTimestamp
parameter_list|(
name|TimeZone
name|zone
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|getCalendar
argument_list|(
name|DateTimeUtil
operator|.
name|GMT_ZONE
argument_list|)
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|hour
init|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
decl_stmt|;
name|int
name|minute
init|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
name|int
name|second
init|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|int
name|millis
init|=
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
name|cal
operator|.
name|setTimeZone
argument_list|(
name|zone
argument_list|)
expr_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
name|hour
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
name|minute
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
name|second
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
name|millis
argument_list|)
expr_stmt|;
return|return
name|cal
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
comment|/**    * Converts this ZonelessTime to a java.sql.Time and formats it via the    * {@link java.sql.Time#toString() toString()} method of that class.    *    * @return the formatted time string    */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Time
name|jdbcTime
init|=
name|getTempTime
argument_list|(
name|getJdbcTime
argument_list|(
name|DateTimeUtil
operator|.
name|DEFAULT_ZONE
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|jdbcTime
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Formats this ZonelessTime via a SimpleDateFormat    *    * @param format format string, as required by SimpleDateFormat    * @return the formatted time string    */
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|DateFormat
name|formatter
init|=
name|getFormatter
argument_list|(
name|format
argument_list|)
decl_stmt|;
name|Time
name|jdbcTime
init|=
name|getTempTime
argument_list|(
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|formatter
operator|.
name|format
argument_list|(
name|jdbcTime
argument_list|)
return|;
block|}
comment|/**    * Parses a string as a ZonelessTime.    *    * @param s a string representing a time in ISO format, i.e. according to    *          the {@link SimpleDateFormat} string "HH:mm:ss"    * @return the parsed time, or null if parsing failed    */
specifier|public
specifier|static
name|ZonelessTime
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|parse
argument_list|(
name|s
argument_list|,
name|DateTimeUtil
operator|.
name|TIME_FORMAT_STRING
argument_list|)
return|;
block|}
comment|/**    * Parses a string as a ZonelessTime using a given format string.    *    * @param s      a string representing a time the given format    * @param format format string as per {@link SimpleDateFormat}    * @return the parsed time, or null if parsing failed    */
specifier|public
specifier|static
name|ZonelessTime
name|parse
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|format
parameter_list|)
block|{
name|DateTimeUtil
operator|.
name|PrecisionTime
name|pt
init|=
name|DateTimeUtil
operator|.
name|parsePrecisionDateTimeLiteral
argument_list|(
name|s
argument_list|,
name|format
argument_list|,
name|DateTimeUtil
operator|.
name|GMT_ZONE
argument_list|)
decl_stmt|;
if|if
condition|(
name|pt
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ZonelessTime
name|zt
init|=
operator|new
name|ZonelessTime
argument_list|(
name|pt
operator|.
name|getPrecision
argument_list|()
argument_list|)
decl_stmt|;
name|zt
operator|.
name|setZonelessTime
argument_list|(
name|pt
operator|.
name|getCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|zt
return|;
block|}
comment|/**    * Gets a temporary Time object. The same object is returned every time.    */
specifier|protected
name|Time
name|getTempTime
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|tempTime
operator|==
literal|null
condition|)
block|{
name|tempTime
operator|=
operator|new
name|Time
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tempTime
operator|.
name|setTime
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|tempTime
return|;
block|}
block|}
end_class

begin_comment
comment|// End ZonelessTime.java
end_comment

end_unit

