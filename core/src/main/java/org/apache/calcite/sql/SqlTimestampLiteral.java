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
name|sql
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
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
name|type
operator|.
name|SqlTypeName
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

begin_comment
comment|/**  * A SQL literal representing a TIMESTAMP value, for example<code>TIMESTAMP  * '1969-07-21 03:15 GMT'</code>.  *  *<p>Create values using {@link SqlLiteral#createTimestamp}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTimestampLiteral
extends|extends
name|SqlAbstractDateTimeLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlTimestampLiteral
parameter_list|(
name|Calendar
name|cal
parameter_list|,
name|int
name|precision
parameter_list|,
name|boolean
name|hasTimeZone
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|cal
argument_list|,
name|hasTimeZone
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|precision
argument_list|,
name|DateTimeUtils
operator|.
name|TIMESTAMP_FORMAT_STRING
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlTimestampLiteral
parameter_list|(
name|Calendar
name|cal
parameter_list|,
name|int
name|precision
parameter_list|,
name|boolean
name|hasTimeZone
parameter_list|,
name|String
name|format
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|cal
argument_list|,
name|hasTimeZone
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|precision
argument_list|,
name|format
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/*   /**    * Converts this literal to a {@link java.sql.Timestamp} object.    o/   public Timestamp getTimestamp() {     return new Timestamp(getCal().getTimeInMillis());   } */
comment|/*   /**    * Converts this literal to a {@link java.sql.Time} object.    o/   public Time getTime() {     long millis = getCal().getTimeInMillis();     int tzOffset = Calendar.getInstance().getTimeZone().getOffset(millis);     return new Time(millis - tzOffset);   } */
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlTimestampLiteral
argument_list|(
operator|(
name|Calendar
operator|)
name|value
argument_list|,
name|precision
argument_list|,
name|hasTimeZone
argument_list|,
name|formatString
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TIMESTAMP '"
operator|+
name|toFormattedString
argument_list|()
operator|+
literal|"'"
return|;
block|}
comment|/**    * Returns e.g. '03:05:67.456'.    */
specifier|public
name|String
name|toFormattedString
parameter_list|()
block|{
name|String
name|result
init|=
name|getTimestamp
argument_list|()
operator|.
name|toString
argument_list|(
name|formatString
argument_list|)
decl_stmt|;
specifier|final
name|Calendar
name|cal
init|=
name|getCal
argument_list|()
decl_stmt|;
if|if
condition|(
name|precision
operator|>
literal|0
condition|)
block|{
assert|assert
name|precision
operator|<=
literal|3
assert|;
comment|// get the millisecond count.  millisecond => at most 3 digits.
name|String
name|digits
init|=
name|Long
operator|.
name|toString
argument_list|(
name|cal
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|+
literal|"."
operator|+
name|digits
operator|.
name|substring
argument_list|(
name|digits
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|,
name|digits
operator|.
name|length
argument_list|()
operator|-
literal|3
operator|+
name|precision
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|0
operator|==
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
assert|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
switch|switch
condition|(
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|getDatabaseProduct
argument_list|()
condition|)
block|{
case|case
name|MSSQL
case|:
name|writer
operator|.
name|literal
argument_list|(
literal|"'"
operator|+
name|this
operator|.
name|toFormattedString
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|writer
operator|.
name|literal
argument_list|(
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlTimestampLiteral.java
end_comment

end_unit

