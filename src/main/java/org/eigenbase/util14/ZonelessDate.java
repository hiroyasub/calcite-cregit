begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|Date
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
comment|/**  * ZonelessDate is a date value without a time zone.  *  * @author John Pham  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|ZonelessDate
extends|extends
name|ZonelessDatetime
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * SerialVersionUID created with JDK 1.5 serialver tool.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6385775986251759394L
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|transient
name|Date
name|tempDate
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs a ZonelessDate.      */
specifier|public
name|ZonelessDate
parameter_list|()
block|{
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
name|clearTime
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
name|clearTime
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
name|Date
argument_list|(
name|getJdbcDate
argument_list|(
name|DateTimeUtil
operator|.
name|defaultZone
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Converts this ZonelessDate to a java.sql.Date and formats it via the      * {@link java.sql.Date#toString() toString()} method of that class.      *      * @return the formatted date string      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Date
name|jdbcDate
init|=
name|getTempDate
argument_list|(
name|getJdbcDate
argument_list|(
name|DateTimeUtil
operator|.
name|defaultZone
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|jdbcDate
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Formats this ZonelessDate via a SimpleDateFormat      *      * @param format format string, as required by {@link SimpleDateFormat}      *      * @return the formatted date string      */
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
name|Date
name|jdbcDate
init|=
name|getTempDate
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
name|jdbcDate
argument_list|)
return|;
block|}
comment|/**      * Parses a string as a ZonelessDate.      *      * @param s a string representing a date in ISO format, i.e. according to      * the SimpleDateFormat string "yyyy-MM-dd"      *      * @return the parsed date, or null if parsing failed      */
specifier|public
specifier|static
name|ZonelessDate
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
name|DateFormatStr
argument_list|)
return|;
block|}
comment|/**      * Parses a string as a ZonelessDate with a given format string.      *      * @param s a string representing a date in ISO format, i.e. according to      * the SimpleDateFormat string "yyyy-MM-dd"      * @param format format string as per {@link SimpleDateFormat}      *      * @return the parsed date, or null if parsing failed      */
specifier|public
specifier|static
name|ZonelessDate
name|parse
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|format
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|DateTimeUtil
operator|.
name|parseDateFormat
argument_list|(
name|s
argument_list|,
name|format
argument_list|,
name|DateTimeUtil
operator|.
name|gmtZone
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
block|}
name|ZonelessDate
name|zd
init|=
operator|new
name|ZonelessDate
argument_list|()
decl_stmt|;
name|zd
operator|.
name|setZonelessTime
argument_list|(
name|cal
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|zd
return|;
block|}
comment|/**      * Gets a temporary Date object. The same object is returned every time.      */
specifier|protected
name|Date
name|getTempDate
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|tempDate
operator|==
literal|null
condition|)
block|{
name|tempDate
operator|=
operator|new
name|Date
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tempDate
operator|.
name|setTime
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|tempDate
return|;
block|}
block|}
end_class

begin_comment
comment|// End ZonelessDate.java
end_comment

end_unit

