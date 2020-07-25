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
name|Locale
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
comment|/**  * Utility methods to manipulate String representation of DateTime values.  */
end_comment

begin_class
specifier|public
class|class
name|DateTimeStringUtils
block|{
specifier|private
name|DateTimeStringUtils
parameter_list|()
block|{
block|}
comment|/** The SimpleDateFormat string for ISO timestamps,    * "yyyy-MM-dd'T'HH:mm:ss'Z'". */
specifier|public
specifier|static
specifier|final
name|String
name|ISO_DATETIME_FORMAT
init|=
literal|"yyyy-MM-dd'T'HH:mm:ss'Z'"
decl_stmt|;
comment|/** The SimpleDateFormat string for ISO timestamps with precisions, "yyyy-MM-dd'T'HH:mm:ss    * .SSS'Z'"*/
specifier|public
specifier|static
specifier|final
name|String
name|ISO_DATETIME_FRACTIONAL_SECOND_FORMAT
init|=
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
decl_stmt|;
specifier|static
name|String
name|pad
parameter_list|(
name|int
name|length
parameter_list|,
name|long
name|v
parameter_list|)
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|v
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|length
condition|)
block|{
name|s
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Appends hour:minute:second to a buffer; assumes they are valid. */
specifier|static
name|StringBuilder
name|hms
parameter_list|(
name|StringBuilder
name|b
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
name|int2
argument_list|(
name|b
argument_list|,
name|h
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|int2
argument_list|(
name|b
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|int2
argument_list|(
name|b
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/** Appends year-month-day and hour:minute:second to a buffer; assumes they    * are valid. */
specifier|static
name|StringBuilder
name|ymdhms
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
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
name|ymd
argument_list|(
name|b
argument_list|,
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|hms
argument_list|(
name|b
argument_list|,
name|h
argument_list|,
name|m
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
comment|/** Appends year-month-day to a buffer; assumes they are valid. */
specifier|static
name|StringBuilder
name|ymd
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|)
block|{
name|int4
argument_list|(
name|b
argument_list|,
name|year
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|int2
argument_list|(
name|b
argument_list|,
name|month
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|int2
argument_list|(
name|b
argument_list|,
name|day
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
specifier|private
specifier|static
name|void
name|int4
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
operator|(
name|i
operator|/
literal|1000
operator|)
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
operator|(
name|i
operator|/
literal|100
operator|)
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
operator|(
name|i
operator|/
literal|10
operator|)
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
name|i
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|int2
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
operator|(
name|i
operator|/
literal|10
operator|)
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'0'
operator|+
name|i
operator|%
literal|10
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|boolean
name|isValidTimeZone
parameter_list|(
specifier|final
name|String
name|timeZone
parameter_list|)
block|{
if|if
condition|(
name|timeZone
operator|.
name|equals
argument_list|(
literal|"GMT"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|String
name|id
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|timeZone
argument_list|)
operator|.
name|getID
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
literal|"GMT"
argument_list|)
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
comment|/**    * Create a SimpleDateFormat with format string with default time zone UTC.    */
specifier|public
specifier|static
name|SimpleDateFormat
name|getDateFormatter
parameter_list|(
name|String
name|format
parameter_list|)
block|{
return|return
name|getDateFormatter
argument_list|(
name|format
argument_list|,
name|DateTimeUtils
operator|.
name|UTC_ZONE
argument_list|)
return|;
block|}
comment|/**    * Create a SimpleDateFormat with format string and time zone.    */
specifier|public
specifier|static
name|SimpleDateFormat
name|getDateFormatter
parameter_list|(
name|String
name|format
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
block|{
specifier|final
name|SimpleDateFormat
name|dateFormatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|format
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|dateFormatter
operator|.
name|setTimeZone
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
return|return
name|dateFormatter
return|;
block|}
block|}
end_class

end_unit

