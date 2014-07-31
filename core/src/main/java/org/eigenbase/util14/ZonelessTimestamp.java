begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ZonelessTimestamp is a timestamp value without a time zone.  */
end_comment

begin_class
specifier|public
class|class
name|ZonelessTimestamp
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
operator|-
literal|6829714640541648394L
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|int
name|precision
decl_stmt|;
specifier|protected
specifier|transient
name|Timestamp
name|tempTimestamp
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a ZonelessTimestamp.    */
specifier|public
name|ZonelessTimestamp
parameter_list|()
block|{
name|this
operator|.
name|precision
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Constructs a ZonelessTimestamp with precision.    *    *<p>The precision is the number of digits to the right of the decimal    * point in the seconds value. For example, a<code>TIMESTAMP(3)</code> has    * a precision to milliseconds.    *    * @param precision Number of digits of precision    */
specifier|public
name|ZonelessTimestamp
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
comment|// implement ZonelessDatetime
specifier|public
name|Object
name|toJdbcObject
parameter_list|()
block|{
return|return
operator|new
name|Timestamp
argument_list|(
name|getJdbcTimestamp
argument_list|(
name|DateTimeUtil
operator|.
name|DEFAULT_ZONE
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts this ZonelessTimestamp to a java.sql.Timestamp and formats it    * via the {@link java.sql.Timestamp#toString() toString()} method of that    * class.    *    *<p>Note: Jdbc formatting always includes a decimal point and at least one    * digit of milliseconds precision. Trailing zeros, except for the first one    * after the decimal point, do not appear in the output.    *    * @return the formatted time string    */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Timestamp
name|ts
init|=
name|getTempTimestamp
argument_list|(
name|getJdbcTimestamp
argument_list|(
name|DateTimeUtil
operator|.
name|DEFAULT_ZONE
argument_list|)
argument_list|)
decl_stmt|;
comment|// Remove trailing '.0' so that format is consistent with SQL spec for
comment|// CAST(TIMESTAMP(0) TO VARCHAR). E.g. "1969-12-31 16:00:00.0"
comment|// becomes "1969-12-31 16:00:00"
return|return
name|ts
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|19
argument_list|)
return|;
block|}
comment|/**    * Formats this ZonelessTimestamp via a SimpleDateFormat. This method does    * not display milliseconds precision.    *    * @param format format string, as required by SimpleDateFormat    * @return the formatted timestamp string    */
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
name|Timestamp
name|ts
init|=
name|getTempTimestamp
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
name|ts
argument_list|)
return|;
block|}
comment|/**    * Parses a string as a ZonelessTimestamp.    *    *<p>This method's parsing is strict and may parse fractional seconds (as    * opposed to just milliseconds.)    *    * @param s a string representing a time in ISO format, i.e. according to    *          the SimpleDateFormat string "yyyy-MM-dd HH:mm:ss"    * @return the parsed time, or null if parsing failed    */
specifier|public
specifier|static
name|ZonelessTimestamp
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
name|TIMESTAMP_FORMAT_STRING
argument_list|)
return|;
block|}
comment|/**    * Parses a string as a ZonelessTimestamp using a given format string.    *    *<p>This method's parsing is strict and may parse fractional seconds (as    * opposed to just milliseconds.)    *    * @param s      a string representing a time in ISO format, i.e. according to    *               the SimpleDateFormat string "yyyy-MM-dd HH:mm:ss"    * @param format format string as per {@link SimpleDateFormat}    * @return the parsed timestamp, or null if parsing failed    */
specifier|public
specifier|static
name|ZonelessTimestamp
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
name|ZonelessTimestamp
name|zt
init|=
operator|new
name|ZonelessTimestamp
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
comment|/**    * Gets a temporary Timestamp object. The same object is returned every    * time.    */
specifier|protected
name|Timestamp
name|getTempTimestamp
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|tempTimestamp
operator|==
literal|null
condition|)
block|{
name|tempTimestamp
operator|=
operator|new
name|Timestamp
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tempTimestamp
operator|.
name|setTime
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|tempTimestamp
return|;
block|}
block|}
end_class

begin_comment
comment|// End ZonelessTimestamp.java
end_comment

end_unit

