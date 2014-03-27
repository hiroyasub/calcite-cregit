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
name|rex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util14
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ByteString
import|;
end_import

begin_comment
comment|/**  * Constant value in a row-expression.  *  *<p>There are several methods for creating literals in {@link RexBuilder}:  * {@link RexBuilder#makeLiteral(boolean)} and so forth.</p>  *  *<p>How is the value stored? In that respect, the class is somewhat of a black  * box. There is a {@link #getValue} method which returns the value as an  * object, but the type of that value is implementation detail, and it is best  * that your code does not depend upon that knowledge. It is better to use  * task-oriented methods such as {@link #getValue2} and  * {@link #toJavaString}.</p>  *  *<p>The allowable types and combinations are:</p>  *  *<table>  *<caption>Allowable types for RexLiteral instances</caption>  *<tr>  *<th>TypeName</th>  *<th>Meaing</th>  *<th>Value type</th>  *</tr>  *<tr>  *<td>{@link SqlTypeName#NULL}</td>  *<td>The null value. It has its own special type.</td>  *<td>null</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#BOOLEAN}</td>  *<td>Boolean, namely<code>TRUE</code>,<code>FALSE</code> or<code>  * UNKNOWN</code>.</td>  *<td>{@link Boolean}, or null represents the UNKNOWN value</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#DECIMAL}</td>  *<td>Exact number, for example<code>0</code>,<code>-.5</code>,<code>  * 12345</code>.</td>  *<td>{@link BigDecimal}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#DOUBLE}</td>  *<td>Approximate number, for example<code>6.023E-23</code>.</td>  *<td>{@link BigDecimal}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#DATE}</td>  *<td>Date, for example<code>DATE '1969-04'29'</code></td>  *<td>{@link Calendar}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#TIME}</td>  *<td>Time, for example<code>TIME '18:37:42.567'</code></td>  *<td>{@link Calendar}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#TIMESTAMP}</td>  *<td>Timestamp, for example<code>TIMESTAMP '1969-04-29  * 18:37:42.567'</code></td>  *<td>{@link Calendar}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#CHAR}</td>  *<td>Character constant, for example<code>'Hello, world!'</code>,<code>  * ''</code>,<code>_N'Bonjour'</code>,<code>_ISO-8859-1'It''s superman!'  * COLLATE SHIFT_JIS$ja_JP$2</code>. These are always CHAR, never VARCHAR.</td>  *<td>{@link NlsString}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#BINARY}</td>  *<td>Binary constant, for example<code>X'7F34'</code>. (The number of hexits  * must be even; see above.) These constants are always BINARY, never  * VARBINARY.</td>  *<td>{@link ByteBuffer}</td>  *</tr>  *<tr>  *<td>{@link SqlTypeName#SYMBOL}</td>  *<td>A symbol is a special type used to make parsing easier; it is not part of  * the SQL standard, and is not exposed to end-users. It is used to hold a flag,  * such as the LEADING flag in a call to the function<code>  * TRIM([LEADING|TRAILING|BOTH] chars FROM string)</code>.</td>  *<td>An enum class</td>  *</tr>  *</table>  */
end_comment

begin_class
specifier|public
class|class
name|RexLiteral
extends|extends
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * The value of this literal. Must be consistent with its type, as per    * {@link #valueMatchesType}. For example, you can't store an {@link    * Integer} value here just because you feel like it -- all numbers are    * represented by a {@link BigDecimal}. But since this field is private, it    * doesn't really matter how the values are stored.    */
specifier|private
specifier|final
name|Comparable
name|value
decl_stmt|;
comment|/**    * The real type of this literal, as reported by {@link #getType}.    */
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
comment|// TODO jvs 26-May-2006:  Use SqlTypeFamily instead; it exists
comment|// for exactly this purpose (to avoid the confusion which results
comment|// from overloading SqlTypeName).
comment|/**    * An indication of the broad type of this literal -- even if its type isn't    * a SQL type. Sometimes this will be different than the SQL type; for    * example, all exact numbers, including integers have typeName {@link    * SqlTypeName#DECIMAL}. See {@link #valueMatchesType} for the definitive    * story.    */
specifier|private
specifier|final
name|SqlTypeName
name|typeName
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a<code>RexLiteral</code>.    */
name|RexLiteral
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|)
block|{
assert|assert
name|type
operator|!=
literal|null
assert|;
assert|assert
name|value
operator|==
literal|null
operator|||
name|valueMatchesType
argument_list|(
name|value
argument_list|,
name|typeName
argument_list|,
literal|true
argument_list|)
assert|;
assert|assert
operator|(
name|value
operator|==
literal|null
operator|)
operator|==
name|type
operator|.
name|isNullable
argument_list|()
assert|;
assert|assert
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|ANY
assert|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|toJavaString
argument_list|(
name|value
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return whether value is appropriate for its type (we have rules about    * these things)    */
specifier|public
specifier|static
name|boolean
name|valueMatchesType
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|BOOLEAN
case|:
comment|// Unlike SqlLiteral, we do not allow boolean null.
return|return
name|value
operator|instanceof
name|Boolean
return|;
case|case
name|NULL
case|:
return|return
literal|false
return|;
comment|// value should have been null
case|case
name|INTEGER
case|:
comment|// not allowed -- use Decimal
case|case
name|TINYINT
case|:
case|case
name|SMALLINT
case|:
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
comment|// fall through
case|case
name|DECIMAL
case|:
case|case
name|DOUBLE
case|:
case|case
name|FLOAT
case|:
case|case
name|REAL
case|:
case|case
name|BIGINT
case|:
return|return
name|value
operator|instanceof
name|BigDecimal
return|;
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|TIMESTAMP
case|:
return|return
name|value
operator|instanceof
name|Calendar
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|value
operator|instanceof
name|BigDecimal
return|;
case|case
name|VARBINARY
case|:
comment|// not allowed -- use Binary
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
comment|// fall through
case|case
name|BINARY
case|:
return|return
name|value
operator|instanceof
name|ByteString
return|;
case|case
name|VARCHAR
case|:
comment|// not allowed -- use Char
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
comment|// fall through
case|case
name|CHAR
case|:
comment|// A SqlLiteral's charset and collation are optional; not so a
comment|// RexLiteral.
return|return
operator|(
name|value
operator|instanceof
name|NlsString
operator|)
operator|&&
operator|(
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getCharset
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getCollation
argument_list|()
operator|!=
literal|null
operator|)
return|;
case|case
name|SYMBOL
case|:
return|return
name|value
operator|instanceof
name|Enum
return|;
case|case
name|ANY
case|:
comment|// Literal of type ANY is not legal. "CAST(2 AS ANY)" remains
comment|// an integer literal surrounded by a cast function.
return|return
literal|false
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|String
name|toJavaString
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|printAsJava
argument_list|(
name|value
argument_list|,
name|pw
argument_list|,
name|typeName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Prints the value this literal as a Java string constant.    */
specifier|public
name|void
name|printAsJava
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|printAsJava
argument_list|(
name|value
argument_list|,
name|pw
argument_list|,
name|typeName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Prints a value as a Java string. The value must be consistent with the    * type, as per {@link #valueMatchesType}.    *    *<p>Typical return values:    *    *<ul>    *<li>true</li>    *<li>null</li>    *<li>"Hello, world!"</li>    *<li>1.25</li>    *<li>1234ABCD</li>    *</ul>    *    * @param value    Value    * @param pw       Writer to write to    * @param typeName Type family    */
specifier|private
specifier|static
name|void
name|printAsJava
parameter_list|(
name|Comparable
name|value
parameter_list|,
name|PrintWriter
name|pw
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|java
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|CHAR
case|:
name|NlsString
name|nlsString
init|=
operator|(
name|NlsString
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|java
condition|)
block|{
name|Util
operator|.
name|printJavaString
argument_list|(
name|pw
argument_list|,
name|nlsString
operator|.
name|getValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|includeCharset
init|=
operator|(
name|nlsString
operator|.
name|getCharsetName
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|nlsString
operator|.
name|getCharsetName
argument_list|()
operator|.
name|equals
argument_list|(
name|SaffronProperties
operator|.
name|instance
argument_list|()
operator|.
name|defaultCharset
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|nlsString
operator|.
name|asSql
argument_list|(
name|includeCharset
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|BOOLEAN
case|:
assert|assert
name|value
operator|instanceof
name|Boolean
assert|;
name|pw
operator|.
name|print
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DECIMAL
case|:
assert|assert
name|value
operator|instanceof
name|BigDecimal
assert|;
name|pw
operator|.
name|print
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
assert|assert
name|value
operator|instanceof
name|BigDecimal
assert|;
name|pw
operator|.
name|print
argument_list|(
name|Util
operator|.
name|toScientificNotation
argument_list|(
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BIGINT
case|:
assert|assert
name|value
operator|instanceof
name|BigDecimal
assert|;
name|pw
operator|.
name|print
argument_list|(
operator|(
operator|(
name|BigDecimal
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|'L'
argument_list|)
expr_stmt|;
break|break;
case|case
name|BINARY
case|:
assert|assert
name|value
operator|instanceof
name|ByteString
assert|;
name|pw
operator|.
name|print
argument_list|(
literal|"X'"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
operator|(
operator|(
name|ByteString
operator|)
name|value
operator|)
operator|.
name|toString
argument_list|(
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
break|break;
case|case
name|NULL
case|:
assert|assert
name|value
operator|==
literal|null
assert|;
name|pw
operator|.
name|print
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
break|break;
case|case
name|SYMBOL
case|:
assert|assert
name|value
operator|instanceof
name|Enum
assert|;
name|pw
operator|.
name|print
argument_list|(
literal|"FLAG("
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|printDatetime
argument_list|(
name|pw
argument_list|,
operator|new
name|ZonelessDate
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|TIME
case|:
name|printDatetime
argument_list|(
name|pw
argument_list|,
operator|new
name|ZonelessTime
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|TIMESTAMP
case|:
name|printDatetime
argument_list|(
name|pw
argument_list|,
operator|new
name|ZonelessTimestamp
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTERVAL_DAY_TIME
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|value
operator|==
literal|null
assert|;
name|pw
operator|.
name|print
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
assert|assert
name|valueMatchesType
argument_list|(
name|value
argument_list|,
name|typeName
argument_list|,
literal|true
argument_list|)
assert|;
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|typeName
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|printDatetime
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|ZonelessDatetime
name|datetime
parameter_list|,
name|Comparable
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|instanceof
name|Calendar
assert|;
name|datetime
operator|.
name|setZonelessTime
argument_list|(
operator|(
operator|(
name|Calendar
operator|)
name|value
operator|)
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|datetime
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts a Jdbc string into a RexLiteral. This method accepts a string,    * as returned by the Jdbc method ResultSet.getString(), and restores the    * string into an equivalent RexLiteral. It allows one to use Jdbc strings    * as a common format for data.    *    *<p>If a null literal is provided, then a null pointer will be returned.    *    * @param type     data type of literal to be read    * @param typeName type family of literal    * @param literal  the (non-SQL encoded) string representation, as returned    *                 by the Jdbc call to return a column as a string    * @return a typed RexLiteral, or null    */
specifier|public
specifier|static
name|RexLiteral
name|fromJdbcString
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|String
name|literal
parameter_list|)
block|{
if|if
condition|(
name|literal
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|CHAR
case|:
name|Charset
name|charset
init|=
name|type
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|SqlCollation
name|collation
init|=
name|type
operator|.
name|getCollation
argument_list|()
decl_stmt|;
name|NlsString
name|str
init|=
operator|new
name|NlsString
argument_list|(
name|literal
argument_list|,
name|charset
operator|.
name|name
argument_list|()
argument_list|,
name|collation
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
name|str
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
name|boolean
name|b
init|=
name|ConversionUtil
operator|.
name|toBoolean
argument_list|(
name|literal
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
name|b
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|DECIMAL
case|:
case|case
name|DOUBLE
case|:
name|BigDecimal
name|d
init|=
operator|new
name|BigDecimal
argument_list|(
name|literal
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
name|d
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|BINARY
case|:
name|byte
index|[]
name|bytes
init|=
name|ConversionUtil
operator|.
name|toByteArrayFromString
argument_list|(
name|literal
argument_list|,
literal|16
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
operator|new
name|ByteString
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|NULL
case|:
return|return
operator|new
name|RexLiteral
argument_list|(
literal|null
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|INTERVAL_DAY_TIME
case|:
name|long
name|millis
init|=
name|SqlParserUtil
operator|.
name|intervalToMillis
argument_list|(
name|literal
argument_list|,
name|type
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|millis
argument_list|)
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|INTERVAL_YEAR_MONTH
case|:
name|long
name|months
init|=
name|SqlParserUtil
operator|.
name|intervalToMonths
argument_list|(
name|literal
argument_list|,
name|type
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|months
argument_list|)
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|TIMESTAMP
case|:
name|String
name|format
init|=
name|getCalendarFormat
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
name|TimeZone
name|tz
init|=
name|DateTimeUtil
operator|.
name|GMT_ZONE
decl_stmt|;
name|Calendar
name|cal
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
name|SqlTypeName
operator|.
name|DATE
condition|)
block|{
name|cal
operator|=
name|DateTimeUtil
operator|.
name|parseDateFormat
argument_list|(
name|literal
argument_list|,
name|format
argument_list|,
name|tz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Allow fractional seconds for times and timestamps
name|DateTimeUtil
operator|.
name|PrecisionTime
name|ts
init|=
name|DateTimeUtil
operator|.
name|parsePrecisionDateTimeLiteral
argument_list|(
name|literal
argument_list|,
name|format
argument_list|,
name|tz
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|!=
literal|null
condition|)
block|{
name|cal
operator|=
name|ts
operator|.
name|getCalendar
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cal
operator|==
literal|null
condition|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"fromJdbcString: invalid date/time value '"
operator|+
name|literal
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
operator|new
name|RexLiteral
argument_list|(
name|cal
argument_list|,
name|type
argument_list|,
name|typeName
argument_list|)
return|;
case|case
name|SYMBOL
case|:
comment|// Symbols are for internal use
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"fromJdbcString: unsupported type"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getCalendarFormat
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|DATE
case|:
return|return
name|DateTimeUtil
operator|.
name|DATE_FORMAT_STRING
return|;
case|case
name|TIME
case|:
return|return
name|DateTimeUtil
operator|.
name|TIME_FORMAT_STRING
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|DateTimeUtil
operator|.
name|TIMESTAMP_FORMAT_STRING
return|;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"getCalendarFormat: unknown type"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|SqlTypeName
name|getTypeName
parameter_list|()
block|{
return|return
name|typeName
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|LITERAL
return|;
block|}
comment|/**    * Returns the value of this literal.    */
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
assert|assert
name|valueMatchesType
argument_list|(
name|value
argument_list|,
name|typeName
argument_list|,
literal|true
argument_list|)
operator|:
name|value
assert|;
return|return
name|value
return|;
block|}
comment|/**    * Returns the value of this literal, in the form that the calculator    * program builder wants it.    */
specifier|public
name|Object
name|getValue2
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|BINARY
case|:
return|return
operator|(
operator|(
name|ByteBuffer
operator|)
name|value
operator|)
operator|.
name|array
argument_list|()
return|;
case|case
name|CHAR
case|:
return|return
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
return|;
case|case
name|DECIMAL
case|:
return|return
operator|(
operator|(
name|BigDecimal
operator|)
name|value
operator|)
operator|.
name|unscaledValue
argument_list|()
operator|.
name|longValue
argument_list|()
return|;
case|case
name|DATE
case|:
return|return
operator|(
name|int
operator|)
operator|(
operator|(
operator|(
name|Calendar
operator|)
name|value
operator|)
operator|.
name|getTimeInMillis
argument_list|()
operator|/
name|DateTimeUtil
operator|.
name|MILLIS_PER_DAY
operator|)
return|;
case|case
name|TIME
case|:
return|return
operator|(
name|int
operator|)
operator|(
operator|(
operator|(
name|Calendar
operator|)
name|value
operator|)
operator|.
name|getTimeInMillis
argument_list|()
operator|%
name|DateTimeUtil
operator|.
name|MILLIS_PER_DAY
operator|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
operator|(
operator|(
name|Calendar
operator|)
name|value
operator|)
operator|.
name|getTimeInMillis
argument_list|()
return|;
default|default:
return|return
name|value
return|;
block|}
block|}
comment|/**    * Returns the value of this literal, in the form that the rex-to-lix    * translator wants it.    */
specifier|public
name|Object
name|getValue3
parameter_list|()
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|DECIMAL
case|:
assert|assert
name|value
operator|instanceof
name|BigDecimal
assert|;
return|return
name|value
return|;
default|default:
return|return
name|getValue2
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|booleanValue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
operator|(
name|Boolean
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|value
return|;
block|}
specifier|public
name|boolean
name|isAlwaysTrue
parameter_list|()
block|{
if|if
condition|(
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|booleanValue
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isAlwaysFalse
parameter_list|()
block|{
if|if
condition|(
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
name|booleanValue
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|RexLiteral
operator|)
operator|&&
name|equals
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|obj
operator|)
operator|.
name|value
argument_list|,
name|value
argument_list|)
operator|&&
name|equals
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|obj
operator|)
operator|.
name|type
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Util
operator|.
name|hashV
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|intValue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|Comparable
name|value
init|=
name|findValue
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|stringValue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|Comparable
name|value
init|=
name|findValue
argument_list|(
name|node
argument_list|)
decl_stmt|;
return|return
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Comparable
name|findValue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|RexLiteral
condition|)
block|{
return|return
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|value
return|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|RexCall
condition|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
specifier|final
name|SqlOperator
name|operator
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|operator
operator|==
name|SqlStdOperatorTable
operator|.
name|CAST
condition|)
block|{
return|return
name|findValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|operator
operator|==
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
condition|)
block|{
specifier|final
name|BigDecimal
name|value
init|=
operator|(
name|BigDecimal
operator|)
name|findValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|value
operator|.
name|negate
argument_list|()
return|;
block|}
block|}
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"not a literal: "
operator|+
name|node
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|boolean
name|isNullLiteral
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
operator|(
name|node
operator|instanceof
name|RexLiteral
operator|)
operator|&&
operator|(
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|value
operator|==
literal|null
operator|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|equals
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|==
literal|null
operator|)
condition|?
operator|(
name|o2
operator|==
literal|null
operator|)
else|:
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitLiteral
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexLiteral.java
end_comment

end_unit

